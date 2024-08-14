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
package org.knime.js.base.node.widget.filter.value;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
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
import org.knime.core.node.util.filter.NameFilterConfiguration.EnforceOption;
import org.knime.js.base.dialog.selection.multiple.MultipleSelectionsComponentFactory;
import org.knime.js.base.node.base.filter.value.ValueFilterNodeConfig;
import org.knime.js.base.node.configuration.filter.value.ValueFilterDialogNodeValue;
import org.knime.js.base.node.widget.WidgetNodeModel;

/**
 * The node model for the value filter widget node
 *
 * @author Christian Albrecht, KNIME GmbH, Konstanz, Germany
 */
public class ValueFilterWidgetNodeModel extends WidgetNodeModel<ReExecutableValueFilterNodeRepresentation<ValueFilterDialogNodeValue>,
ValueFilterDialogNodeValue, ValueFilterWidgetConfig> implements BufferedDataTableHolder {

    private BufferedDataTable m_table;

    /**
     * Creates a new value filter widget node model
     *
     * @param viewName the interactive view name
     */
    public ValueFilterWidgetNodeModel(final String viewName) {
        super(new PortType[]{BufferedDataTable.TYPE}, new PortType[]{BufferedDataTable.TYPE}, viewName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected PortObjectSpec[] configure(final PortObjectSpec[] inSpecs) throws InvalidSettingsException {
        updateValues((DataTableSpec)inSpecs[0]);
        Map<String, List<String>> value = createAndPushFlowVariable();
        String column = value.entrySet().iterator().next().getKey();
        DataTableSpec inTable = (DataTableSpec)inSpecs[0];
        int colIndex;
        for (colIndex = 0; colIndex < inTable.getNumColumns(); colIndex++) {
            if (inTable.getColumnSpec(colIndex).getName().equals(column)) {
                break;
            }
        }
        if (colIndex >= inTable.getNumColumns()) {
            throw new InvalidSettingsException("The column '" + "' was not found");
        }
        return new DataTableSpec[]{(DataTableSpec)inSpecs[0]};
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected PortObject[] performExecute(final PortObject[] inObjects, final ExecutionContext exec) throws Exception {
        m_table = (BufferedDataTable)inObjects[0];
        getConfig().getValueFilterConfig().setFromSpec(m_table.getDataTableSpec());
        Map<String, List<String>> value = createAndPushFlowVariable();
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
    protected ValueFilterDialogNodeValue copyConfigToViewValue(final ValueFilterDialogNodeValue currentViewValue,
        final ValueFilterWidgetConfig config, final ValueFilterWidgetConfig previousConfig) {
        var defaultVal = config.getDefaultValue();
        var previousDefaultVal = previousConfig.getDefaultValue();
        if (!defaultVal.getColumn().equals(previousDefaultVal.getColumn())) {
            currentViewValue.setColumn(defaultVal.getColumn());
        }
        if (!Arrays.equals(defaultVal.getValues(), previousDefaultVal.getValues())) {
            List<String> defaultIncludes = Arrays.asList(config.getDefaultValue().getValues());
            List<String> defaultExcludes =
                    new ArrayList<String>(Math.max(0, config.getValueFilterConfig().getPossibleValues().get(currentViewValue.getColumn()).size()
                            - defaultIncludes.size()));

            for (String string : config.getValueFilterConfig().getPossibleValues().get(currentViewValue.getColumn())) {
                if (!defaultIncludes.contains(string)) {
                    defaultExcludes.add(string);
                }
            }

            var enforceOption = currentViewValue.getEnforceOption();
            if (enforceOption.equals(EnforceOption.EnforceInclusion)) {
                currentViewValue.setValues(defaultVal.getValues());
                config.getDefaultValue().setValues(defaultIncludes.toArray(new String[defaultIncludes.size()]));
            } else {
                config.getDefaultValue().setValues(defaultExcludes.toArray(new String[defaultExcludes.size()]));
            }

        }
        return currentViewValue;
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
     * Checks if the currently selected value is among the possible values and throws an exception if not.
     *
     * @throws InvalidSettingsException If the value is not among the possible values
     */
    private Map<String, List<String>> checkSelectedValues() throws InvalidSettingsException {
        ValueFilterDialogNodeValue rValue = getRelevantValue();
        String column = rValue.getColumn();
        List<String> values = new ArrayList<String>(Arrays.asList(rValue.getValues()));
        ValueFilterNodeConfig valueFilterConfig = getConfig().getValueFilterConfig();
        Map<String, List<String>> possibleValues = valueFilterConfig.getPossibleValues();
        if (possibleValues.size() < 1) {
            throw new InvalidSettingsException("No column available for selection in input table.");
        }
        if (!possibleValues.containsKey(column)) {
            String warning = "";
            if (!StringUtils.isEmpty(column)) {
                if (valueFilterConfig.isLockColumn()) {
                    throw new InvalidSettingsException(
                        "Locked column '" + column + "' is not part of the table spec anymore.");
                }
                warning = "Column '" + column + "' is not part of the table spec anymore.\n";
            }
            warning += "Auto-guessing default column and value.";
            column = possibleValues.keySet().toArray(new String[0])[0];
            values = new ArrayList<String>();
            setWarningMessage(warning);
        }
        List<String> columnValues = possibleValues.get(column);
        if (columnValues == null) {
            throw new InvalidSettingsException("No possible values found for column '" + column + "'");
        }
        List<String> errorList = new ArrayList<String>();
        Iterator<String> it = values.iterator();
        while (it.hasNext()) {
            String value = it.next();
            if (!columnValues.contains(value)) {
                errorList.add(value);
                it.remove();
            }
        }
        if (errorList.size() > 0) {
            String plural = errorList.size() > 1 ? "s" : "";
            String verb = errorList.size() > 1 ? "' are " : "' is ";
            setWarningMessage("The selected value" + plural + " '" + String.join(", ", errorList) + verb
                + "not among the possible values in the column '" + column + "'. Omitting value" + plural + ".");
        }
        Map<String, List<String>> result = new HashMap<String, List<String>>();
        result.put(column, values);

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
    public ValueFilterDialogNodeValue createEmptyViewValue() {
        return new ValueFilterDialogNodeValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getJavascriptObjectID() {
        return "org.knime.js.base.node.widget.filter.value";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ValueFilterWidgetConfig createEmptyConfig() {
        return new ValueFilterWidgetConfig();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected ReExecutableValueFilterNodeRepresentation<ValueFilterDialogNodeValue> getRepresentation() {
        ValueFilterWidgetConfig config = getConfig();
        return new ReExecutableValueFilterNodeRepresentation<ValueFilterDialogNodeValue>(getRelevantValue(), config.getDefaultValue(),
            config.getValueFilterConfig(), config.getLabelConfig(), config.getTriggerReExecution());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void useCurrentValueAsDefault() {
        final ValueFilterDialogNodeValue value = getConfig().getDefaultValue();
        value.setColumn(getViewValue().getColumn());
        value.setValues(getViewValue().getValues());
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
