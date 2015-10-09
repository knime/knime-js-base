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
 *
 * History:
 * 24-Febr-2011: created
 */
package org.knime.quickform.nodes.in.stringselection;

import java.awt.GridBagConstraints;
import java.util.Arrays;

import javax.swing.JPanel;
import javax.swing.JTextField;

import org.knime.core.node.InvalidSettingsException;
import org.knime.quickform.nodes.in.QuickFormInNodeDialogPane;

/**
 * Dialog to node.
 *
 * @author Peter Ohl, KNIME.com, Zurich, Switzerland
 */
final class StringSelectionInputQuickFormInNodeDialogPane extends
        QuickFormInNodeDialogPane<StringSelectionInputQuickFormInConfiguration> {

    private final JTextField m_valueField;

    private final JTextField m_choicesField;

    /** Constructors, inits fields calls layout routines. */
    StringSelectionInputQuickFormInNodeDialogPane() {
        m_valueField = new JTextField(DEF_TEXTFIELD_WIDTH);
        m_choicesField = new JTextField(DEF_TEXTFIELD_WIDTH);
        createAndAddTab();
    }

    /** {@inheritDoc} */
    @Override
    protected StringSelectionInputQuickFormInConfiguration createConfiguration() {
        return new StringSelectionInputQuickFormInConfiguration();
    }

    /** {@inheritDoc} */
    @Override
    protected void fillPanel(final JPanel panelWithGBLayout,
            final GridBagConstraints gbc) {
        addPairToPanel("Variable Value: ", m_valueField, panelWithGBLayout, gbc);
        addPairToPanel("Possible Choices: ", m_choicesField,
                panelWithGBLayout, gbc);
    }

    /** {@inheritDoc} */
    @Override
    protected void saveAdditionalSettings(
            final StringSelectionInputQuickFormInConfiguration config)
            throws InvalidSettingsException {
        String[] choices = m_choicesField.getText().split(",");
        // trim value
        String value = m_valueField.getText().trim();

        boolean match = value.isEmpty();
        // trim choices
        for (int i = 0; i < choices.length; i++) {
            choices[i] = choices[i].trim();
            if (choices[i].equals(value)) {
                match = true;
            }
        }
        if (!match) {
            throw new InvalidSettingsException(
                    "Variable value not contained in possible choices.");
        }
        config.setChoices(choices);
        config.getValueConfiguration().setValue(value);
    }

    /** {@inheritDoc} */
    @Override
    protected void loadAdditionalSettings(
            final StringSelectionInputQuickFormInConfiguration config) {
        String ch = Arrays.toString(config.getChoices());
        // cut off brackets
        ch = ch.substring(1, ch.length() - 1);
        m_choicesField.setText(ch);
        String value = config.getValueConfiguration().getValue();
        m_valueField.setText(value);
    }
}
