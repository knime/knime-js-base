/*
 * ------------------------------------------------------------------------
 *  Copyright by KNIME AG, Zurich, Switzerland
 *  Website: http://www.knime.com; Email: contact@knime.com
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License, Version 3, as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, see <http://www.gnu.org/licenses>.
 *
 *  Additional permission under GNU GPL version 3 section 7:
 *
 *  KNIME interoperates with ECLIPSE solely via ECLIPSE's plug-in APIs.
 *  Hence, KNIME and ECLIPSE are both independent programs and are not
 *  derived from each other. Should, however, the interpretation of the
 *  GNU GPL Version 3 ("License") under any applicable laws result in
 *  KNIME and ECLIPSE being a combined program, KNIME AG herewith grants
 *  you the additional permission to use and propagate KNIME together with
 *  ECLIPSE with only the license terms in place for ECLIPSE applying to
 *  ECLIPSE and the GNU GPL Version 3 applying for KNIME, provided the
 *  license terms of ECLIPSE themselves allow for the respective use and
 *  propagation of ECLIPSE together with KNIME.
 *
 *  Additional permission relating to nodes for KNIME that extend the Node
 *  Extension (and in particular that are based on subclasses of NodeModel,
 *  NodeDialog, and NodeView) and that only interoperate with KNIME through
 *  standard APIs ("Nodes"):
 *  Nodes are deemed to be separate and independent programs and to not be
 *  covered works.  Notwithstanding anything to the contrary in the
 *  License, the License does not apply to Nodes, you are not required to
 *  license Nodes under the License, and you are granted a license to
 *  prepare and propagate Nodes, in each case even if such Nodes are
 *  propagated with or for interoperation with KNIME.  The owner of a Node
 *  may freely choose the license terms applicable to such Node, including
 *  when such Node is propagated with or for interoperation with KNIME.
 * ------------------------------------------------------------------------
 *
 * History
 *   23.04.2014 (Christian Albrecht, KNIME AG, Zurich, Switzerland): created
 */
package org.knime.js.base.node.viz.pagedTable;

import java.util.Arrays;
import java.util.List;

import org.knime.core.data.DataRow;
import org.knime.core.data.cache.DataRowCache;
import org.knime.core.data.container.ColumnRearranger;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.interactive.ViewRequestHandlingException;
import org.knime.core.node.port.PortObject;
import org.knime.js.core.JSONDataTable;
import org.knime.js.core.JSONDataTable.Builder;
import org.knime.js.core.JSONViewRequestHandler;
import org.knime.js.core.node.table.AbstractTableNodeModel;
import org.knime.js.core.settings.table.TableSettings;

/**
 *
 * @author Christian Albrecht, KNIME.com GmbH, Konstanz, Germany
 */
public class PagedTableViewNodeModel extends AbstractTableNodeModel<PagedTableViewRepresentation,
        PagedTableViewValue> implements JSONViewRequestHandler<PagedTableViewRequest, PagedTableViewResponse> {

    private static NodeLogger LOGGER = NodeLogger.getLogger(PagedTableViewNodeModel.class);

    private DataRowCache m_cache;

    /**
     * @param viewName The name of the interactive view
     */
    protected PagedTableViewNodeModel(final String viewName) {
        super(viewName, new PagedTableViewConfig());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getJavascriptObjectID() {
        return "org.knime.js.base.node.viz.pagedTable";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PagedTableViewRepresentation createEmptyViewRepresentation() {
        return new PagedTableViewRepresentation();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PagedTableViewValue createEmptyViewValue() {
        return new PagedTableViewValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PagedTableViewRepresentation getViewRepresentation() {
        PagedTableViewRepresentation rep = super.getViewRepresentation();
        if (m_cache == null) {
            m_cache = new DataRowCache();
        }
        if (m_cache.getDataTable() == null && m_table != null) {
            m_cache.setDataTable(m_table, null);
        }
        return rep;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected PortObject[] performExecute(final PortObject[] inObjects, final ExecutionContext exec) throws Exception {
        BufferedDataTable out = (BufferedDataTable)inObjects[0];
        synchronized (getLock()) {
            PagedTableViewRepresentation viewRepresentation = getViewRepresentation();
            m_table = (BufferedDataTable)inObjects[0];
            if (m_cache == null) {
                m_cache = new DataRowCache();
            }
            long start = System.currentTimeMillis();
            m_cache.setDataTable(m_table, exec.createSubExecutionContext(0.1));
            LOGGER.debug("TABLE CACHE - set data table took " + (System.currentTimeMillis() - start));
            if (viewRepresentation.getSettings().getTable() == null) {
                JSONDataTable jsonTable = createJSONTableFromBufferedDataTable(m_table, exec.createSubExecutionContext(0.5));
                viewRepresentation.getSettings().setTable(jsonTable);
                copyConfigToRepresentation();
            }

            if (m_config.getSettings().getRepresentationSettings().getEnableSelection()) {
                PagedTableViewValue viewValue = getViewValue();
                List<String> selectionList = null;
                if (viewValue != null) {
                    if (viewValue.getSettings().getSelection() != null) {
                        selectionList = Arrays.asList(viewValue.getSettings().getSelection());
                    }
                }
                ColumnRearranger rearranger = createColumnAppender(m_table.getDataTableSpec(), selectionList);
                out = exec.createColumnRearrangeTable(m_table, rearranger, exec.createSubExecutionContext(0.4));
            }
            viewRepresentation.getSettings().setSubscriptionFilterIds(getSubscriptionFilterIds(m_table.getDataTableSpec()));
        }
        exec.setProgress(1);
        return new PortObject[]{out};
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void validateSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
        (new TableSettings()).loadSettings(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void performReset() {
        m_cache = null;
        super.performReset();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PagedTableViewRequest createEmptyViewRequest() {
        return new PagedTableViewRequest();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PagedTableViewResponse handleRequest(final PagedTableViewRequest request, final ExecutionMonitor exec)
        throws ViewRequestHandlingException, InterruptedException, CanceledExecutionException {
        PagedTableViewResponse response = new PagedTableViewResponse(request);
        try {
            DataRow[] rows = new DataRow[request.getLength()];
            ExecutionMonitor cacheProgress = exec.createSubProgress(0.9);
            exec.setMessage("Caching rows...");
            for (int i = 0; i < request.getLength(); i++) {
                rows[i] = m_cache.getRow((int)request.getStart() + i, cacheProgress);
            }
            Builder tableBuilder = getJsonDataTableBuilder(m_table);
            tableBuilder.setDataRows(rows);
            tableBuilder.setFirstRow(request.getStart() + 1);
            tableBuilder.setMaxRows(request.getLength());
            exec.setMessage("Serializing response..");
            response.setTable(tableBuilder.build(exec.createSubProgress(0.1)));
        } catch (Exception e) {
            response.setError(e.getMessage());
            throw new ViewRequestHandlingException(e);
        }
        return response;
    }
}
