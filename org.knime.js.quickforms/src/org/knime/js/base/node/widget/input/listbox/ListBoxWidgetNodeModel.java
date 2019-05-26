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
 *   26 May 2019 (albrecht): created
 */
package org.knime.js.base.node.widget.input.listbox;

import java.util.ArrayList;
import java.util.List;

import org.knime.core.data.DataTableSpec;
import org.knime.core.data.RowKey;
import org.knime.core.data.def.DefaultRow;
import org.knime.core.data.def.StringCell;
import org.knime.core.node.BufferedDataContainer;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.port.PortObject;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.port.PortType;
import org.knime.core.node.web.ValidationError;
import org.knime.js.base.node.base.listbox.ListBoxNodeConfig;
import org.knime.js.base.node.base.listbox.ListBoxNodeUtil;
import org.knime.js.base.node.widget.WidgetNodeModel;

/**
 * The node model for the double widget node
 *
 * @author Christian Albrecht, KNIME GmbH, Konstanz, Germany
 */
public class ListBoxWidgetNodeModel
    extends WidgetNodeModel<ListBoxWidgetRepresentation, ListBoxWidgetValue, ListBoxWidgetConfig> {

    /**
     *
     * Creates a new list box widget node model
     *
     * @param viewName the interactive view name
     */
    protected ListBoxWidgetNodeModel(final String viewName) {
        super(new PortType[0], new PortType[]{BufferedDataTable.TYPE}, viewName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected PortObjectSpec[] configure(final PortObjectSpec[] inSpecs) throws InvalidSettingsException {
        getValidatedValues();
        final String variableName = getConfig().getFlowVariableName();
        createAndPushFlowVariable();
        return new PortObjectSpec[]{ListBoxNodeUtil.createSpec(variableName)};
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected PortObject[] performExecute(final PortObject[] inObjects, final ExecutionContext exec) throws Exception {
        final String variableName = getConfig().getFlowVariableName();
        DataTableSpec outSpec = ListBoxNodeUtil.createSpec(variableName);
        BufferedDataContainer cont = exec.createDataContainer(outSpec, true);
        List<String> values = getValidatedValues();
        for (int i = 0; i < values.size(); i++) {
            cont.addRowToTable(new DefaultRow(RowKey.createRowKey(Long.valueOf(i)), new StringCell(values.get(i))));
        }
        cont.close();
        createAndPushFlowVariable();
        return new PortObject[]{cont.getTable()};
    }

    /**
     * @return List of validated values
     * @throws InvalidSettingsException If one of the values is invalid
     */
    private List<String> getValidatedValues() throws InvalidSettingsException {
        final ArrayList<String> values =
            ListBoxNodeUtil.getSeparatedValues(getConfig().getListBoxConfig(), getRelevantValue().getString());
        validateDialogValue(getRelevantValue(), values);
        return values;
    }

    private void createAndPushFlowVariable() throws InvalidSettingsException {
        if (getConfig().getListBoxConfig().getSeparator() == null) {
            setWarningMessage("Auto guessing separator.");
        }
        final String variableName = getConfig().getFlowVariableName();
        final String value = getRelevantValue().getString();
        pushFlowVariableString(variableName, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ListBoxWidgetValue createEmptyViewValue() {
        return new ListBoxWidgetValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getJavascriptObjectID() {
        return "org.knime.js.base.node.widget.input.listbox";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ListBoxWidgetConfig createEmptyConfig() {
        return new ListBoxWidgetConfig();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected ListBoxWidgetRepresentation getRepresentation() {
        return new ListBoxWidgetRepresentation(getRelevantValue(), getConfig());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ValidationError validateViewValue(final ListBoxWidgetValue value) {
        ArrayList<String> values;
        try {
            values = ListBoxNodeUtil.getSeparatedValues(getConfig().getListBoxConfig(), value.getString());
        } catch (InvalidSettingsException e) {
            return new ValidationError(e.getMessage());
        }
        return validateDialogValue(value, values);
    }

    private ValidationError validateDialogValue(final ListBoxWidgetValue value, final ArrayList<String> values) {
        ListBoxNodeConfig config = getConfig().getListBoxConfig();
        String regex = config.getRegex();
        if (regex != null && !regex.isEmpty()) {
            for (int i = 0; i < values.size(); i++) {
                if (!values.get(i).matches(regex)) {
                    return new ValidationError("Value " + (i + 1) + " is not valid:\n"
                        + config.getErrorMessage().replaceAll("[?]", values.get(i)));
                }
            }
        }
        return super.validateViewValue(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void useCurrentValueAsDefault() {
        getConfig().getDefaultValue().setString(getViewValue().getString());
    }

}
