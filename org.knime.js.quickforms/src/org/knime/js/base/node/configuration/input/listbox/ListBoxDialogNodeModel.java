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
 *   24 May 2019 (albrecht): created
 */
package org.knime.js.base.node.configuration.input.listbox;

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
import org.knime.js.base.node.base.input.listbox.ListBoxNodeConfig;
import org.knime.js.base.node.base.input.listbox.ListBoxNodeUtil;
import org.knime.js.base.node.configuration.DialogNodeModel;

/**
 * Node model for the list box configuration node
 *
 * @author Christian Albrecht, KNIME GmbH, Konstanz, Germany
 */
public class ListBoxDialogNodeModel extends
    DialogNodeModel<ListBoxDialogNodeRepresentation, ListBoxDialogNodeValue, ListBoxInputDialogNodeConfig> {

    /**
     * Creates a new list box configuration node model
     */
    public ListBoxDialogNodeModel() {
        super(new PortType[0], new PortType[]{BufferedDataTable.TYPE});
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ListBoxDialogNodeValue createEmptyDialogValue() {
        return new ListBoxDialogNodeValue();
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
    protected PortObject[] execute(final PortObject[] inObjects, final ExecutionContext exec) throws Exception {
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
    public ListBoxInputDialogNodeConfig createEmptyConfig() {
        return new ListBoxInputDialogNodeConfig();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected ListBoxDialogNodeRepresentation getRepresentation() {
        return new ListBoxDialogNodeRepresentation(getRelevantValue(), getConfig());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void validateDialogValue(final ListBoxDialogNodeValue value) throws InvalidSettingsException {
        final ArrayList<String> values =
            ListBoxNodeUtil.getSeparatedValues(getConfig().getListBoxConfig(), value.getString());
        validateDialogValue(value, values);
    }

    private void validateDialogValue(final ListBoxDialogNodeValue value, final ArrayList<String> values)
        throws InvalidSettingsException {
        ListBoxNodeConfig config = getConfig().getListBoxConfig();
        String regex = config.getRegex();
        if (regex != null && !regex.isEmpty()) {
            for (int i = 0; i < values.size(); i++) {
                if (!values.get(i).matches(regex)) {
                    throw new InvalidSettingsException("Value " + (i + 1) + " is not valid:\n"
                        + config.getErrorMessage().replaceAll("[?]", values.get(i)));
                }
            }
        }
        super.validateDialogValue(value);
    }
}
