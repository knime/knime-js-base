/*
 * ------------------------------------------------------------------------
 *  Copyright by KNIME GmbH, Konstanz, Germany
 *  Website: http://www.knime.org; Email: contact@knime.org
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
 * History:
 * 24-Febr-2011: created
 */
package org.knime.quickform.nodes.in.selection.column;

import org.knime.core.data.DataTableSpec;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.port.PortType;
import org.knime.core.node.port.flowvariable.FlowVariablePortObject;
import org.knime.core.util.node.quickform.AbstractQuickFormElement;
import org.knime.core.util.node.quickform.in.AbstractQuickFormInElement;
import org.knime.core.util.node.quickform.in.StringOptionInputQuickFormInElement;
import org.knime.quickform.nodes.in.selection.QuickFormDataInNodeModel;

/**
 * Node for column selector input quickform.
 *
 * @author Thomas Gabriel, KNIME.com AG, Zurich
 * @since 2.6
 */
public class ColumnSelectionInputQuickFormInNodeModel extends
        QuickFormDataInNodeModel<ColumnSelectionInputQuickFormInConfiguration> {

    /** Creates a new column selection node model. */
    public ColumnSelectionInputQuickFormInNodeModel() {
        super(new PortType[]{BufferedDataTable.TYPE},
                new PortType[]{FlowVariablePortObject.TYPE});
    }

    /** {@inheritDoc} */
    @Override
    protected void createAndPushFlowVariable() throws InvalidSettingsException {
        ColumnSelectionInputQuickFormInConfiguration cfg =
            (ColumnSelectionInputQuickFormInConfiguration) getConfiguration();
        if (cfg == null) {
            throw new InvalidSettingsException("No settings available");
        }
        String variableName = cfg.getVariableName();
        String value = cfg.getValueConfiguration().getValue();
        pushFlowVariableString(variableName, value == null ? "null" : value);
    }

    /** {@inheritDoc} */
    @Override
    protected PortObjectSpec[] configure(final PortObjectSpec[] inSpecs)
            throws InvalidSettingsException {
        ColumnSelectionInputQuickFormInConfiguration cfg =
            (ColumnSelectionInputQuickFormInConfiguration) getConfiguration();
        if (cfg != null) {
            final DataTableSpec inspec = (DataTableSpec) inSpecs[0];
            String value = cfg.getValueConfiguration().getValue();
            if (value == null || !inspec.containsName(value)) {
                if (inspec.getNumColumns() == 0) {
                    value = null;
                } else {
                    value = inspec.getColumnSpec(0).getName();
                }
                cfg.getValueConfiguration().setValue(value);
            }
            cfg.setChoices(inspec.getColumnNames());
        }
        return super.configure(inSpecs);
    }

    /** {@inheritDoc} */
    @Override
    public ColumnSelectionInputQuickFormInConfiguration createConfiguration() {
        return new ColumnSelectionInputQuickFormInConfiguration();
    }

    /** {@inheritDoc} */
    @Override
    public AbstractQuickFormInElement getQuickFormElement() {
        ColumnSelectionInputQuickFormInConfiguration cfg =
            (ColumnSelectionInputQuickFormInConfiguration) getConfiguration();
        StringOptionInputQuickFormInElement e =
                new StringOptionInputQuickFormInElement(cfg.getLabel(),
                        cfg.getDescription(), cfg.getWeight());
        e.setValue(cfg.getValueConfiguration().getValue());
        PortObjectSpec[] portSpecs = getInPortObjectSpecs();
        if (portSpecs != null && portSpecs[0] != null) {
            final DataTableSpec spec = (DataTableSpec) portSpecs[0];
            e.setChoices(spec.getColumnNames());
        } else {
            e.setChoices(cfg.getChoices());
        }
        return e;
    }

    /** {@inheritDoc} */
    @Override
    public void loadFromQuickFormElement(final AbstractQuickFormInElement e)
            throws InvalidSettingsException {
        ColumnSelectionInputQuickFormInConfiguration cfg =
            (ColumnSelectionInputQuickFormInConfiguration) getConfiguration();
        StringOptionInputQuickFormInElement si =
                AbstractQuickFormElement.cast(
                        StringOptionInputQuickFormInElement.class, e);
        cfg.getValueConfiguration().setValue(si.getValue());
    }

}
