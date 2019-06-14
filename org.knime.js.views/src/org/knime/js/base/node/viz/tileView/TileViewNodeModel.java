/*
 * ------------------------------------------------------------------------
 *
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
 * ---------------------------------------------------------------------
 *
 * History
 *   Aug 22, 2018 (awalter): created
 */
package org.knime.js.base.node.viz.tileView;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import org.knime.core.data.DataTableSpec;
import org.knime.core.data.container.ColumnRearranger;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.port.PortObject;
import org.knime.js.core.JSONDataTable;
import org.knime.js.core.node.table.AbstractTableNodeModel;

/**
 * @author Alison Walter, KNIME GmbH, Konstanz, Germany
 */
public class TileViewNodeModel extends AbstractTableNodeModel<TileViewRepresentation, TileViewValue> {

    /**
     * @param viewName The name of the interactive view
     */
    protected TileViewNodeModel(final String viewName) {
        super(viewName, new TileViewConfig());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TileViewRepresentation createEmptyViewRepresentation() {
        return new TileViewRepresentation();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TileViewValue createEmptyViewValue() {
        return new TileViewValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getJavascriptObjectID() {
        return "org.knime.js.base.node.viz.tileView";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected PortObject[] performExecute(final PortObject[] inObjects, final ExecutionContext exec) throws Exception {
        // Duplicate code from PagedTable
        BufferedDataTable out = (BufferedDataTable)inObjects[0];
        synchronized (getLock()) {
            final TileViewRepresentation viewRepresentation = getViewRepresentation();
            if (viewRepresentation.getSettings().getTable() == null) {
                m_table = (BufferedDataTable)inObjects[0];
                final JSONDataTable jsonTable = createJSONTableFromBufferedDataTable(m_table,
                    exec.createSubExecutionContext(0.5));
                viewRepresentation.getSettings().setTable(jsonTable);
                copyConfigToRepresentation();
            }

            if (m_config.getSettings().getRepresentationSettings().getEnableSelection()) {
                final TileViewValue viewValue = getViewValue();
                List<String> selectionList = null;
                if (viewValue != null) {
                    if (viewValue.getSettings().getSelection() != null) {
                        selectionList = Arrays.asList(viewValue.getSettings().getSelection());
                    }
                }
                final ColumnRearranger rearranger = createColumnAppender(m_table.getDataTableSpec(), selectionList);
                out = exec.createColumnRearrangeTable(m_table, rearranger, exec.createSubExecutionContext(0.5));
            }
            viewRepresentation.getSettings().setSubscriptionFilterIds(
                getSubscriptionFilterIds(m_table.getDataTableSpec()));
        }
        exec.setProgress(1);
        return new PortObject[]{out};
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void validateSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
        m_config.loadSettings(settings);
    }

    @Override
    protected ColumnRearranger createColumnAppender(final DataTableSpec spec, final List<String> selectionList) {
        final String newColName = m_config.getSettings().getSelectionColumnName();
        if ((newColName == null) || newColName.trim().isEmpty()) {
            m_config.getSettings().setSelectionColumnName("Selected (Tile View)");
        }
        return super.createColumnAppender(spec, selectionList);
    }

    @Override
    protected String[] determineExcludedColumns(final BufferedDataTable table) {
        String[] excluded = super.determineExcludedColumns(table);
        String labelColumn = ((TileViewConfig)m_config).getLabelCol();
        if (labelColumn == null) {
            return excluded;
        }
        Stream<String> result = Arrays.stream(excluded).filter(columnName -> !labelColumn.equals(columnName));
        return result.toArray(String[]::new);
    }

    /**
     * Copies the settings from dialog into representation and values objects.
     */
    @Override
    protected void copyConfigToRepresentation() {
        synchronized (getLock()) {
            final TileViewConfig conf = (TileViewConfig)m_config;
            final TileViewRepresentation viewRepresentation = getViewRepresentation();
            // Use setSettingsFromDialog, it ensures the table that got set on the representation settings is preserved
            viewRepresentation.setSettingsFromDialog(m_config.getSettings().getRepresentationSettings());
            viewRepresentation.setUseNumCols(conf.getUseNumCols());
            viewRepresentation.setUseColWidth(conf.getUseColWidth());
            viewRepresentation.setNumCols(conf.getNumCols());
            viewRepresentation.setColWidth(conf.getColWidth());
            viewRepresentation.setLabelCol(conf.getLabelCol());
            viewRepresentation.setUseRowID(conf.getUseRowID());
            viewRepresentation.setAlignLeft(conf.getAlignLeft());
            viewRepresentation.setAlignRight(conf.getAlignRight());
            viewRepresentation.setAlignCenter(conf.getAlignCenter());

            final TileViewValue viewValue = getViewValue();
            if (isViewValueEmpty()) {
                viewValue.setSettings(m_config.getSettings().getValueSettings());
            }
        }
    }
}
