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
 *
 */
package org.knime.quickform.nodes.in.listbox;

import java.util.ArrayList;
import java.util.Arrays;

import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.RowKey;
import org.knime.core.data.def.DefaultRow;
import org.knime.core.data.def.StringCell;
import org.knime.core.node.BufferedDataContainer;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.port.PortObject;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.util.node.quickform.AbstractQuickFormElement;
import org.knime.core.util.node.quickform.in.AbstractQuickFormInElement;
import org.knime.core.util.node.quickform.in.StringListPasteboxInputQuickFormInElement;
import org.knime.quickform.nodes.in.QuickFormInNodeModel;

/**
 * Node for list box input.
 *
 * @author Thomas Gabriel, KNIME.com AG, Zurich
 * @since 2.6
 */
public class ListBoxInputQuickFormInNodeModel
        extends QuickFormInNodeModel<ListBoxInputQuickFormInConfiguration> {

    private static final NodeLogger LOGGER = NodeLogger.getLogger(ListBoxInputQuickFormInNodeModel.class);

    /** Create a new list box node model. */
    public ListBoxInputQuickFormInNodeModel() {
        super(0, 1);
    }

    /** {@inheritDoc} */
    @Override
    protected void createAndPushFlowVariable() throws InvalidSettingsException {
        ListBoxInputQuickFormInConfiguration cfg = getConfiguration();
        if (cfg == null) {
            throw new InvalidSettingsException("No settings available.");
        }
        final String variableName = cfg.getVariableName();
        final String value = cfg.getValueConfiguration().getValue();
        pushFlowVariableString(variableName, value);
    }

    /** {@inheritDoc} */
    @Override
    protected PortObjectSpec[] configure(final PortObjectSpec[] inSpecs)
            throws InvalidSettingsException {
        ListBoxInputQuickFormInConfiguration cfg = getConfiguration();
        if (cfg == null) {
            throw new InvalidSettingsException("No settings available.");
        }
        final String variableName = cfg.getVariableName();
        createAndPushFlowVariable();
        return new PortObjectSpec[]{createSpec(variableName)};
    }

    /** {@inheritDoc} */
    @Override
    protected PortObject[] execute(final PortObject[] inObjects,
            final ExecutionContext exec) throws Exception {
        ListBoxInputQuickFormInConfiguration cfg = getConfiguration();
        final String variableName = cfg.getVariableName();
        final String value = cfg.getValueConfiguration().getValue();
        final String separatorRegexp = getSeparatorRegex();
        final ArrayList<String> values = new ArrayList<String>();

        if (cfg.getSeparateEachCharacter()) {
                values.addAll(Arrays.asList(value.split("")));
        } else if (separatorRegexp.isEmpty()) {
                values.add(value);
        } else {
            values.addAll(Arrays.asList(value.split(separatorRegexp, -1)));
        }

        DataTableSpec outSpec = createSpec(variableName);
        BufferedDataContainer cont = exec.createDataContainer(outSpec, true);
        for (int i = 0; i < values.size(); i++) {
            cont.addRowToTable(new DefaultRow(RowKey.createRowKey(i), new StringCell(values.get(i))));
       }
        cont.close();
        createAndPushFlowVariable();
        return new PortObject[]{cont.getTable()};
    }

    /**
     * @return separator regex
     */
    private String getSeparatorRegex() {
        String separator = getConfiguration().getSeparator();
        if (getConfiguration().getSeparateEachCharacter() || separator == null || separator.isEmpty()) {
            return "";
        } else {
            StringBuilder sepString = new StringBuilder();
            for (int i = 0; i < separator.length(); i++) {
                if (i > 0) {
                    sepString.append('|');
                }
                char c = separator.charAt(i);
                if (c == '\\') {
                    if (i + 1 < separator.length()) {
                        if (separator.charAt(i + 1) == 'n') {
                            sepString.append("\\n");
                            i++;
                        } else if (separator.charAt(i + 1) == 't') {
                            sepString.append("\\t");
                            i++;
                        } else {
                            // not supported
                            setWarningMessage("A back slash must not be followed by a char other than n or t; ignore the separator.");
                            return "";
                        }
                    } else {
                        sepString.append("\\\\");
                    }
                }
                else if (c == '[' || c == '^') {
                    // these symbols are not allowed in [] (see the else-block below)
                    sepString.append("\\" + c);
                }
                else {
                    // a real, non-specific char
                    sepString.append("[" + c + "]");
                }
            }
            return sepString.toString();
        }
    }

    private DataTableSpec createSpec(final String variableName) {
        final DataColumnSpec cspec = new DataColumnSpecCreator(variableName, StringCell.TYPE).createSpec();
        return new DataTableSpec(cspec);
    }

    /** {@inheritDoc} */
    @Override
    protected ListBoxInputQuickFormInConfiguration createConfiguration() {
        return new ListBoxInputQuickFormInConfiguration();
    }

    /** {@inheritDoc} */
    @Override
    public AbstractQuickFormInElement getQuickFormElement() {
        ListBoxInputQuickFormInConfiguration cfg = getConfiguration();
        StringListPasteboxInputQuickFormInElement e = new StringListPasteboxInputQuickFormInElement(
            cfg.getLabel(), cfg.getDescription(), cfg.getWeight());
        e.setValue(cfg.getValueConfiguration().getValue());
        e.setSeparator(cfg.getSeparator());
        return e;
    }

    /** {@inheritDoc} */
    @Override
    public void loadFromQuickFormElement(final AbstractQuickFormInElement e)
            throws InvalidSettingsException {
        ListBoxInputQuickFormInConfiguration cfg = getConfiguration();
        StringListPasteboxInputQuickFormInElement si = AbstractQuickFormElement.cast(
            StringListPasteboxInputQuickFormInElement.class, e);
        cfg.getValueConfiguration().setValue(si.getValue());
    }

}
