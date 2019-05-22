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
 *   21 May 2019 (albrecht): created
 */
package org.knime.js.base.node.widget;

import java.awt.GridBagConstraints;

import javax.swing.JPanel;
import javax.swing.JTextField;

import org.knime.core.node.InvalidSettingsException;
import org.knime.js.base.node.base.LabeledValueControlledNodeDialog;
import org.knime.js.core.JSONViewContent;
import org.knime.js.core.settings.DialogUtil;

/**
 *
 * @author Christian Albrecht, KNIME GmbH, Konstanz, Germany
 * @param <VAL>
 */
public abstract class FlowVariableWidgetNodeDialog<VAL extends JSONViewContent>
    extends LabeledValueControlledNodeDialog {

    private final JTextField m_variableNameField;

    /**
     * Inits fields, sub-classes should call the {@link #createAndAddTab()}
     * method when they are done initializing their fields.
     */
    public FlowVariableWidgetNodeDialog() {
        super();
        m_variableNameField = new JTextField(DialogUtil.DEF_TEXTFIELD_WIDTH);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected JPanel createContentPanel(final GridBagConstraints gbc) {
        JPanel panel = super.createContentPanel(gbc);

        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        addPairToPanel("Variable Name: ", m_variableNameField, panel, gbc);

        return panel;
    }

    /**
     * @return The flow variable name
     */
    protected String getFlowVariableName() {
        return m_variableNameField.getText();
    }

    /**
     * @param flowVariableName The flow variable name
     */
    protected void setFlowVariableName(final String flowVariableName) {
        m_variableNameField.setText(flowVariableName);
    }

    /**
     * @param config The {@link LabeledFlowVariableWidgetConfig} to load from
     */
    protected void loadSettingsFrom(final LabeledFlowVariableWidgetConfig<VAL> config) {
        setLabel(config.getLabel());
        setDescription(config.getDescription());
        setFlowVariableName(config.getFlowVariableName());
    }

    /**
    * @param config The {@link LabeledFlowVariableWidgetConfig} to save to
    * @throws InvalidSettingsException if the parameter name is invalid
    */
   protected void saveSettingsTo(final LabeledFlowVariableWidgetConfig<VAL> config) throws InvalidSettingsException {
       config.setLabel(getLabel());
       config.setDescription(getDescription());
       config.setFlowVariableName(getFlowVariableName());
   }

}
