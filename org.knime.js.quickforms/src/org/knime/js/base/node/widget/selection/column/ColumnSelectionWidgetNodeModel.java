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
 *   29 May 2019 (albrecht): created
 */
package org.knime.js.base.node.widget.selection.column;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.knime.core.data.DataTableSpec;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.BufferedDataTableHolder;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.port.PortObject;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.port.PortType;
import org.knime.core.node.port.flowvariable.FlowVariablePortObject;
import org.knime.core.node.port.flowvariable.FlowVariablePortObjectSpec;
import org.knime.js.base.node.base.selection.column.ColumnSelectionNodeRepresentation;
import org.knime.js.base.node.base.selection.column.ColumnSelectionNodeValue;
import org.knime.js.base.node.widget.WidgetNodeModel;

/**
 * The node model for the column selection widget node
 *
 * @author Christian Albrecht, KNIME GmbH, Konstanz, Germany
 */
public class ColumnSelectionWidgetNodeModel extends
    WidgetNodeModel<ColumnSelectionNodeRepresentation<ColumnSelectionNodeValue>, ColumnSelectionNodeValue,
    ColumnSelectionWidgetConfig> implements BufferedDataTableHolder {

    private BufferedDataTable m_table;

    /**
     * Creates a new column selection widget node model
     *
     * @param viewName the interactive view name
     */
    public ColumnSelectionWidgetNodeModel(final String viewName) {
        super(new PortType[]{BufferedDataTable.TYPE}, new PortType[]{FlowVariablePortObject.TYPE}, viewName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected PortObjectSpec[] configure(final PortObjectSpec[] inSpecs) throws InvalidSettingsException {
        updateColumns((DataTableSpec)inSpecs[0]);
        createAndPushFlowVariable();
        return new PortObjectSpec[]{FlowVariablePortObjectSpec.INSTANCE};
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected PortObject[] performExecute(final PortObject[] inObjects, final ExecutionContext exec) throws Exception {
        m_table = (BufferedDataTable)inObjects[0];
        updateColumns(m_table.getDataTableSpec());
        createAndPushFlowVariable();
        return new PortObject[]{FlowVariablePortObject.INSTANCE};
    }

    private void createAndPushFlowVariable() throws InvalidSettingsException {
        List<String> possibleColumns = Arrays.asList(getConfig().getColumnSelectionConfig().getPossibleColumns());
        if (possibleColumns.size() < 1) {
            throw new InvalidSettingsException("No column available for selection in input table.");
        }

        String value = getRelevantValue().getColumn();
        if (!possibleColumns.contains(value)) {
            String warning = "";
            if (!StringUtils.isEmpty(value)) {
                warning = "Column '" + value + "' is not part of the table spec anymore.\n";
            }
            warning += "Auto-guessing default column.";
            value = possibleColumns.get(0);
            setWarningMessage(warning);
        }
        pushFlowVariableString(getConfig().getFlowVariableName(), value);
    }

    /**
     * Update the possible columns in the config
     *
     * @param spec The input spec
     */
    private void updateColumns(final DataTableSpec spec) {
        getConfig().getColumnSelectionConfig().setPossibleColumns(spec.getColumnNames());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void performReset() {
        m_table = null;
        super.performReset();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ColumnSelectionNodeValue createEmptyViewValue() {
        return new ColumnSelectionNodeValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getJavascriptObjectID() {
        return "org.knime.js.base.node.widget.selection.column";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ColumnSelectionWidgetConfig createEmptyConfig() {
        return new ColumnSelectionWidgetConfig();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected ColumnSelectionNodeRepresentation<ColumnSelectionNodeValue> getRepresentation() {
        ColumnSelectionWidgetConfig config = getConfig();
        return new ColumnSelectionNodeRepresentation<ColumnSelectionNodeValue>(getRelevantValue(),
            config.getDefaultValue(), config.getColumnSelectionConfig(), config.getLabelConfig(), config.getTriggerReExecution());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void useCurrentValueAsDefault() {
        getConfig().getDefaultValue().setColumn(getViewValue().getColumn());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BufferedDataTable[] getInternalTables() {
        return new BufferedDataTable[]{m_table};
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setInternalTables(final BufferedDataTable[] tables) {
        if (tables != null && tables.length > 0 && tables[0] != null) {
            m_table = tables[0];
            updateColumns(m_table.getDataTableSpec());
        }
    }

}
