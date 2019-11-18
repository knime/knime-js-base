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
package org.knime.js.base.node.configuration.selection.column;

import java.util.Arrays;
import java.util.List;

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
import org.knime.core.node.port.flowvariable.FlowVariablePortObject;
import org.knime.core.node.port.flowvariable.FlowVariablePortObjectSpec;
import org.knime.core.node.util.CheckUtils;
import org.knime.js.base.node.configuration.DialogNodeModel;

/**
 * Node model for the column selection configuration node
 *
 * @author Christian Albrecht, KNIME GmbH, Konstanz, Germany
 * @author Adrian Nembach, KNIME GmbH, Konstanz, Germany
 */
public class ColumnSelectionDialogNodeModel extends DialogNodeModel<ColumnSelectionDialogNodeRepresenation,
    ColumnSelectionDialogNodeValue, ColumnSelectionDialogNodeConfig> implements BufferedDataTableHolder {

    private BufferedDataTable m_table;

    private final boolean m_autoConfigure;

    /**
     * Creates a new column selection configuration node model
     *
     * @param autoConfigure whether the node should autoconfigure if the selected column is missing (behavior prior to
     *            4.1.0)
     */
    public ColumnSelectionDialogNodeModel(final boolean autoConfigure) {
        super(new PortType[]{BufferedDataTable.TYPE}, new PortType[]{FlowVariablePortObject.TYPE});
        m_autoConfigure = autoConfigure;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected PortObjectSpec[] configure(final PortObjectSpec[] inSpecs) throws InvalidSettingsException {
        DataTableSpec spec = (DataTableSpec)inSpecs[0];
        updateColumns(prefilter(spec));
        final String selected = createAndPushFlowVariable();
        final ColumnRearranger cr = new ColumnRearranger(spec);
        cr.keepOnly(selected);
        return new PortObjectSpec[]{FlowVariablePortObjectSpec.INSTANCE};
    }

    private DataTableSpec prefilter(final DataTableSpec spec) {
        return getConfig().getInputSpecFilterConfig().createFilter().filter(spec);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected PortObject[] execute(final PortObject[] inObjects, final ExecutionContext exec) throws Exception {
        m_table = (BufferedDataTable)inObjects[0];
        updateColumns(prefilter(m_table.getDataTableSpec()));
        final String selectedColumn = createAndPushFlowVariable();
        final ColumnRearranger cr = new ColumnRearranger(m_table.getDataTableSpec());
        cr.keepOnly(selectedColumn);
        return new PortObject[]{FlowVariablePortObject.INSTANCE};
    }

    private String createAndPushFlowVariable() throws InvalidSettingsException {
        List<String> possibleColumns = Arrays.asList(getConfig().getColumnSelectionConfig().getPossibleColumns());
        if (possibleColumns.isEmpty()) {
            throw new InvalidSettingsException("No column available for selection in input table.");
        }

        String value = getRelevantValue().getColumn();
        if (!possibleColumns.contains(value)) {
            CheckUtils.checkSetting(m_autoConfigure,
                "Column '%s' is not part of the table spec anymore.", value);
            String warning = "";
            if (!StringUtils.isEmpty(value)) {
                warning = "Column '" + value + "' is not part of the table spec anymore.\n";
            }
            warning += "Auto-guessing default column.";
            value = possibleColumns.get(0);
            setWarningMessage(warning);
        }
        pushFlowVariableString(getConfig().getFlowVariableName(), value);
        return value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ColumnSelectionDialogNodeValue createEmptyDialogValue() {
        return new ColumnSelectionDialogNodeValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ColumnSelectionDialogNodeConfig createEmptyConfig() {
        return new ColumnSelectionDialogNodeConfig();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected ColumnSelectionDialogNodeRepresenation getRepresentation() {
        return new ColumnSelectionDialogNodeRepresenation(getRelevantValue(), getConfig());
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
    protected void reset() {
        m_table = null;
        super.reset();
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
            updateColumns(prefilter(m_table.getDataTableSpec()));
        }
    }

}
