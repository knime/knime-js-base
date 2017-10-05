/*
 * ------------------------------------------------------------------------
 *  Copyright by KNIME GmbH, Konstanz, Germany
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
 *  KNIME and ECLIPSE being a combined program, KNIME GMBH herewith grants
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
 */
package org.knime.js.base.node.quickform.filter.value;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTable;
import org.knime.core.data.DataTableSpec;
import org.knime.core.node.BufferedDataContainer;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.port.PortObject;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.port.PortType;
import org.knime.js.base.node.quickform.QuickFormNodeModel;

/**
 * Model for the value filter quick form node.
 *
 * @author Patrick Winter, KNIME.com AG, Zurich, Switzerland
 */
public class ValueFilterQuickFormNodeModel
        extends
        QuickFormNodeModel
        <ValueFilterQuickFormRepresentation,
        ValueFilterQuickFormValue,
        ValueFilterQuickFormConfig> {

    /** Creates a new value selection node model.
     * @param viewName the view name*/
    public ValueFilterQuickFormNodeModel(final String viewName) {
        super(new PortType[]{BufferedDataTable.TYPE},
                new PortType[]{BufferedDataTable.TYPE}, viewName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getJavascriptObjectID() {
        return "org_knime_js_base_node_quickform_filter_value";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ValueFilterQuickFormValue createEmptyViewValue() {
        return new ValueFilterQuickFormValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected PortObjectSpec[] configure(final PortObjectSpec[] inSpecs)
            throws InvalidSettingsException {
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

    /** {@inheritDoc} */
    @Override
    protected PortObject[] execute(final PortObject[] inObjects,
            final ExecutionContext exec) throws Exception {
        getConfig().setFromSpec(((DataTable)inObjects[0]).getDataTableSpec());
        Map<String, List<String>> value = createAndPushFlowVariable();
        Entry<String, List<String>> entry = value.entrySet().iterator().next();
        String column = entry.getKey();
        List<String> values = entry.getValue();
        BufferedDataTable inTable = (BufferedDataTable)inObjects[0];
        BufferedDataContainer container =
                exec.createDataContainer(inTable.getDataTableSpec(), false);
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
     * Updates the possible values in the config.
     *
     * @param spec The input spec
     */
    private void updateValues(final DataTableSpec spec) {
        getConfig().setFromSpec(spec);
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
        ValueFilterQuickFormValue rValue = getRelevantValue();
        String column = rValue.getColumn();
        List<String> values = new ArrayList<String>(Arrays.asList(rValue.getValues()));
        Map<String, List<String>> possibleValues = getConfig().getPossibleValues();
        if (possibleValues.size() < 1) {
            throw new InvalidSettingsException("No column available for selection in input table.");
        }
        if (!possibleValues.containsKey(column)) {
            String warning = "";
            if (!StringUtils.isEmpty(column)) {
                if (getConfig().getLockColumn()) {
                    throw new InvalidSettingsException("Locked column '" + column + "' is not part of the table spec anymore.");
                }
                warning = "Column '" + column + "' is not part of the table spec anymore.\n";
            }
            warning += "Auto-guessing default column and value.";
            column = possibleValues.keySet().toArray(new String[0])[0];
            values = new ArrayList<String>();
            setWarningMessage(warning);
        }
        List<String> columnValues = possibleValues.get(column);
        if (columnValues == null || columnValues.isEmpty()) {
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
            setWarningMessage("The selected value" + plural + " '" + String.join(", ", errorList) + verb + "not among the possible values in the column '"
                + column + "'. Omitting value" + plural + ".");
        }
        Map<String, List<String>> result = new HashMap<String, List<String>>();
        result.put(column, values);
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void copyValueToConfig() {
        getConfig().getDefaultValue().setColumn(getViewValue().getColumn());
        getConfig().getDefaultValue().setValues(getViewValue().getValues());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ValueFilterQuickFormConfig createEmptyConfig() {
        return new ValueFilterQuickFormConfig();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected ValueFilterQuickFormRepresentation getRepresentation() {
        return new ValueFilterQuickFormRepresentation(getRelevantValue(), getConfig());
    }

}
