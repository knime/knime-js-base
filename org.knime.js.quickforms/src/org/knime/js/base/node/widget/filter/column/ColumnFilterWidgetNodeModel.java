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
 *   27 May 2019 (albrecht): created
 */
package org.knime.js.base.node.widget.filter.column;

import org.apache.commons.lang.StringUtils;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.container.ColumnRearranger;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.BufferedDataTableHolder;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.port.PortObject;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.port.PortType;
import org.knime.js.base.node.base.filter.column.ColumnFilterNodeRepresentation;
import org.knime.js.base.node.base.filter.column.ColumnFilterNodeUtil;
import org.knime.js.base.node.base.filter.column.ColumnFilterNodeValue;
import org.knime.js.base.node.widget.WidgetNodeModel;

/**
 * The node model for the column filter widget node
 *
 * @author Christian Albrecht, KNIME GmbH, Konstanz, Germany
 */
public class ColumnFilterWidgetNodeModel extends WidgetNodeModel<ColumnFilterNodeRepresentation<ColumnFilterNodeValue>,
        ColumnFilterNodeValue, ColumnFilterWidgetConfig> implements BufferedDataTableHolder {

    private DataTableSpec m_spec = new DataTableSpec();
    private BufferedDataTable m_inTable = null;

    /**
     * Creates a new column filter widget node model
     *
     * @param viewName the interactive view name
     */
    protected ColumnFilterWidgetNodeModel(final String viewName) {
        super(new PortType[]{BufferedDataTable.TYPE}, new PortType[]{BufferedDataTable.TYPE}, viewName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected PortObjectSpec[] configure(final PortObjectSpec[] inSpecs) throws InvalidSettingsException {
        m_spec = (DataTableSpec) inSpecs[0];
        updateValuesFromSpec((DataTableSpec) inSpecs[0]);
        updateColumns((DataTableSpec) inSpecs[0]);
        createAndPushFlowVariable();
        return new DataTableSpec[]{
            ColumnFilterNodeUtil.createSpec((DataTableSpec)inSpecs[0], getRelevantValue().getColumns())};
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected PortObject[] performExecute(final PortObject[] inObjects, final ExecutionContext exec) throws Exception {
        m_inTable = (BufferedDataTable) inObjects[0];
        DataTableSpec inSpec = (DataTableSpec) inObjects[0].getSpec();
        updateColumns(inSpec);
        createAndPushFlowVariable();
        DataTableSpec outSpec =
            ColumnFilterNodeUtil.createSpec((DataTableSpec)inObjects[0].getSpec(), getRelevantValue().getColumns());
        ColumnRearranger rearranger = new ColumnRearranger(inSpec);
        rearranger.keepOnly(outSpec.getColumnNames());
        BufferedDataTable outTable = exec.createColumnRearrangeTable((BufferedDataTable)inObjects[0],
                rearranger, exec);
        return new BufferedDataTable[]{outTable};
    }

    /**
     * Pushes the current value as flow variable.
     */
    private void createAndPushFlowVariable() {
        final String[] values = getRelevantValue().getColumns();
        pushFlowVariableString(getConfig().getFlowVariableName(), StringUtils.join(values, ","));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ColumnFilterNodeValue createEmptyViewValue() {
        return new ColumnFilterNodeValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getJavascriptObjectID() {
        return "org.knime.js.base.node.widget.filter.column";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ColumnFilterWidgetConfig createEmptyConfig() {
        return new ColumnFilterWidgetConfig();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected ColumnFilterNodeRepresentation<ColumnFilterNodeValue> getRepresentation() {
        ColumnFilterWidgetConfig config = getConfig();
        return new ColumnFilterNodeRepresentation<ColumnFilterNodeValue>(getRelevantValue(), config.getDefaultValue(),
            config.getColumnFilterConfig(), config.getLabelConfig());
    }

    private void updateValuesFromSpec(final DataTableSpec spec) {
        getConfig().getDefaultValue().updateFromSpec(spec);
        if (getViewValue() != null) {
            getViewValue().updateFromSpec(spec);
        }
    }

    /**
     * Update the possible columns in the config
     *
     * @param spec The input spec
     */
    private void updateColumns(final DataTableSpec spec) {
        getConfig().getColumnFilterConfig().setPossibleColumns(spec.getColumnNames());
    }

    /**
     * @return The spec of the input table
     */
    private DataTableSpec getSpec() {
        return m_inTable != null ? m_inTable.getDataTableSpec() : m_spec;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void performReset() {
        m_inTable = null;
        super.performReset();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void useCurrentValueAsDefault() {
        getConfig().getDefaultValue().setColumns(getViewValue().getColumns());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BufferedDataTable[] getInternalTables() {
        return m_inTable != null ? new BufferedDataTable[]{m_inTable} : new BufferedDataTable[0];
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setInternalTables(final BufferedDataTable[] tables) {
        if (tables.length > 0) {
            m_inTable = tables[0];
            DataTableSpec spec = getSpec();
            if (spec != null) {
                updateColumns(spec);
                updateValuesFromSpec(spec);
            }
        }
    }

}
