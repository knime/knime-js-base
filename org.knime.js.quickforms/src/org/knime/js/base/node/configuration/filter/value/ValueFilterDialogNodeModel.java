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
package org.knime.js.base.node.configuration.filter.value;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.node.BufferedDataContainer;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.BufferedDataTableHolder;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.port.PortObject;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.port.PortType;
import org.knime.core.node.util.CheckUtils;
import org.knime.js.base.dialog.selection.multiple.MultipleSelectionsComponentFactory;
import org.knime.js.base.node.base.filter.value.ValueFilterNodeConfig;
import org.knime.js.base.node.configuration.DialogNodeModel;

/**
 * Node model for the value filter configuration node
 *
 * @author Christian Albrecht, KNIME GmbH, Konstanz, Germany
 */
public class ValueFilterDialogNodeModel extends
    DialogNodeModel<ValueFilterDialogNodeRepresentation, ValueFilterDialogNodeValue, ValueFilterDialogNodeConfig>
    implements BufferedDataTableHolder {

    private static final DataTableSpec EMPTY_TABLE_SPEC = new DataTableSpec();

    private BufferedDataTable m_table;

    /**
     * Creates a new value filter configuration node model
     */
    public ValueFilterDialogNodeModel() {
        super(new PortType[]{BufferedDataTable.TYPE}, new PortType[]{BufferedDataTable.TYPE});
    }

    /** Check that the selected filter column exists in the input table and that the selected values are valid. */
    @Override
    protected PortObjectSpec[] configure(final PortObjectSpec[] inSpecs) throws InvalidSettingsException {
        // extract all domains from the input table's column specs
        updateValues((DataTableSpec)inSpecs[0]);
        // get filter configuration
        Map<String, List<String>> value = createAndPushFlowVariable();
        // fail if the filter column does not exist in the input table
        String column = value.entrySet().iterator().next().getKey();
        DataTableSpec inTable = (DataTableSpec)inSpecs[0];
        int colIndex;
        for (colIndex = 0; colIndex < inTable.getNumColumns(); colIndex++) {
            if (inTable.getColumnSpec(colIndex).getName().equals(column)) {
                break;
            }
        }
        CheckUtils.checkSetting(colIndex < inTable.getNumColumns(), "The column '%s' was not found", column);
        return new DataTableSpec[]{(DataTableSpec)inSpecs[0]};
    }

    /**
     * Retains all rows of the input table that match one of the permitted values in the filter column. <br/>
     * A value matches if its string representation matches the string representation of a permitted value.
     *
     * @throws InvalidSettingsException If the input table has no column with the selected name.
     */
    @Override
    protected PortObject[] execute(final PortObject[] inObjects, final ExecutionContext exec) throws Exception {
        m_table = (BufferedDataTable)inObjects[0];
        //
        updateValues(m_table.getDataTableSpec());
        // get filter configuration
        Map<String, List<String>> value = createAndPushFlowVariable();
        // maps column name to the string representations of the permitted values
        Entry<String, List<String>> entry = value.entrySet().iterator().next();
        String column = entry.getKey();
        List<String> values = entry.getValue();
        BufferedDataTable inTable = (BufferedDataTable)inObjects[0];
        BufferedDataContainer container = exec.createDataContainer(inTable.getDataTableSpec(), false);
        int colIndex;
        for (colIndex = 0; colIndex < inTable.getDataTableSpec().getNumColumns(); colIndex++) {
            if (inTable.getDataTableSpec().getColumnSpec(colIndex).getName().equals(column)) {
                break;
            }
        }
        if (colIndex >= inTable.getDataTableSpec().getNumColumns()) {
            throw new InvalidSettingsException("The column '" + "' was not found");
        }
        inTable.getDataTableSpec().getColumnSpec(column);
        for (DataRow row : inTable) {
            if (values.contains(row.getCell(colIndex).toString())) {
                container.addRowToTable(row);
            }
        }
        container.close();
        return new PortObject[]{container.getTable()};
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void reset() {
        m_table = null;
        updateValues(EMPTY_TABLE_SPEC);
        super.reset();
    }

    /**
     * Updates the possible values in the config.
     *
     * @param spec The input spec
     */
    private void updateValues(final DataTableSpec spec) {
        getConfig().getValueFilterConfig().setFromSpec(spec);
    }

    /**
     * Push the current value as flow variable.
     *
     * @throws InvalidSettingsException If the current value is not among the possible values
     */
    private Map<String, List<String>> createAndPushFlowVariable() throws InvalidSettingsException {
        Map<String, List<String>> value = checkSelectedValues();
        Entry<String, List<String>> entry = value.entrySet().iterator().next();
        String column = entry.getKey();
        List<String> values = entry.getValue();
        pushFlowVariableString(getConfig().getFlowVariableName() + " (column)", column);
        pushFlowVariableString(getConfig().getFlowVariableName(), StringUtils.join(values, ","));
        return value;
    }

    /**
     * Validates the selected column. Omits chosen values that are no longer among the possible values. Includes values
     * not mentioned in include or exclude list, based on the currently active policy.
     *
     * Auto-guessing: If the selected column is not part of the input table, a random column with a discrete domain is
     * selected. Adds all values of that column to the include list (enforce exclude is active) or to the exclude list
     * (enforce include is active).
     *
     * @throws InvalidSettingsException If there are no columns with discrete domains.
     * @throws InvalidSettingsException If the selected column is locked but does not exist in the input table.
     * @return The selected column and the list of values to include
     */
    private Map<String, List<String>> checkSelectedValues() throws InvalidSettingsException {
        ValueFilterDialogNodeValue rValue = getRelevantValue();
        String column = rValue.getColumn();
        ValueFilterNodeConfig valueFilterConfig = getConfig().getValueFilterConfig();
        Map<String, List<String>> possibleValues = valueFilterConfig.getPossibleValues();
        // if there are no columns with discrete domains
        if (possibleValues.isEmpty()) {
            throw new InvalidSettingsException("No column available for selection in input table.");
        }
        if (!possibleValues.containsKey(column)) {
            if (!StringUtils.isEmpty(column)) {
                throw new InvalidSettingsException(
                    "Column '" + column + "' is not part of the table spec anymore.");
            }
            // "Guessing" means that we take the first column from the possibleValues set
            var guessedColumn = possibleValues.entrySet().iterator().next();
            column = guessedColumn.getKey();
            // To respect the specified include/exclude option, even if the column is guessed,
            // we update the NodeValue with the domain of the guessed column. See AP-20227
            rValue.updateInclExcl(guessedColumn.getValue());
            setWarningMessage("Auto-guessing default column and value.");
        }
        var columnValues = possibleValues.get(column);
        if (columnValues == null || columnValues.isEmpty()) {
            throw new InvalidSettingsException("No possible values found for column '" + column + "'");
        }
        List<String> errorList = new ArrayList<>();
        var selectedValues = new ArrayList<>(Arrays.asList(rValue.getValues()));
        var it = selectedValues.iterator();
        while (it.hasNext()) {
            String value = it.next();
            if (!columnValues.contains(value)) {
                errorList.add(value);
                it.remove();
            }
        }
        if (!errorList.isEmpty()) {
            String plural = errorList.size() > 1 ? "s" : "";
            String verb = errorList.size() > 1 ? "' are " : "' is ";
            setWarningMessage("The selected value" + plural + " '" + String.join(", ", errorList) + verb
                + "not among the possible values in the column '" + column + "'. Omitting value" + plural + ".");
        }
        Map<String, List<String>> result = new HashMap<>();
        result.put(column, selectedValues);

        // The list type might have been changed via the configuration node
        // to a setting under which the enforce policy of the overwriting value should not be considered.
        if (!valueFilterConfig.getType().equals(MultipleSelectionsComponentFactory.TWINLIST)) {
            rValue.setEnforceOption(getConfig().getDefaultValue().getEnforceOption());
        }

        rValue.updateInclExcl(columnValues);

        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ValueFilterDialogNodeValue createEmptyDialogValue() {
        return new ValueFilterDialogNodeValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ValueFilterDialogNodeConfig createEmptyConfig() {
        return new ValueFilterDialogNodeConfig();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected ValueFilterDialogNodeRepresentation getRepresentation() {
        return new ValueFilterDialogNodeRepresentation(getRelevantValue(), getConfig());
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
            updateValues(m_table.getDataTableSpec());
        }
    }

}
