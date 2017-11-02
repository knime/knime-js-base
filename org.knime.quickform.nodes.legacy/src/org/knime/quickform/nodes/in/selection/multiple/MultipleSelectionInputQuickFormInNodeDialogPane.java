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
 * History:
 * 24-Febr-2011: created
 */
package org.knime.quickform.nodes.in.selection.multiple;

import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.util.node.quickform.in.MultipleSelectionInputQuickFormInElement.Layout;
import org.knime.quickform.nodes.in.QuickFormInNodeDialogPane;

/**
 * Dialog to node.
 *
 * @author Peter Ohl, KNIME AG, Zurich, Switzerland
 */
final class MultipleSelectionInputQuickFormInNodeDialogPane extends
        QuickFormInNodeDialogPane<MultipleSelectionInputQuickFormInConfiguration> {

    private final JTextField m_valueField;

    private final JTextField m_choicesField;

    private final List<JRadioButton> m_layoutButtons = new ArrayList<JRadioButton>();

    /** Constructors, inits fields calls layout routines. */
    MultipleSelectionInputQuickFormInNodeDialogPane() {
        m_valueField = new JTextField(DEF_TEXTFIELD_WIDTH);
        m_choicesField = new JTextField(DEF_TEXTFIELD_WIDTH);
        createAndAddTab();
    }

    /** {@inheritDoc} */
    @Override
    protected MultipleSelectionInputQuickFormInConfiguration createConfiguration() {
        return new MultipleSelectionInputQuickFormInConfiguration();
    }

    /** {@inheritDoc} */
    @Override
    protected void fillPanel(final JPanel panelWithGBLayout,
            final GridBagConstraints gbc) {
        addPairToPanel("Variable Values: ", m_valueField, panelWithGBLayout, gbc);
        addPairToPanel("Possible Choices: ", m_choicesField, panelWithGBLayout, gbc);
        ButtonGroup bg = new ButtonGroup();
        final JPanel layoutPanel = new JPanel(new GridLayout(Layout.values().length, 1));
        for (Layout l : Layout.values()) {
            final JRadioButton button = new JRadioButton(l.toString());
            bg.add(button);
            layoutPanel.add(button);
            m_layoutButtons.add(button);
        }
        m_layoutButtons.get(0).setSelected(true);
        addPairToPanel("Layout: ", layoutPanel, panelWithGBLayout, gbc);
    }

    /** {@inheritDoc} */
    @Override
    protected void saveAdditionalSettings(final MultipleSelectionInputQuickFormInConfiguration config)
            throws InvalidSettingsException {
        if (m_valueField.getText().trim().isEmpty()) {
            throw new InvalidSettingsException("Variable value must not be empty");
        }
        final String choices = m_choicesField.getText();
        final String[] choiseArray = choices.split(",");
        final String values = m_valueField.getText().trim();
        final String[] valueArray = values.split(",");
        for (int j = 0; j < valueArray.length; j++) {
            boolean match = false;
            // trim value
            final String value = valueArray[j].trim();
            for (int i = 0; i < choiseArray.length; i++) {
                // trim choice
                String choice = choiseArray[i].trim();
                if (choice.equals(value)) {
                    match = true;
                }
            }
            if (!match) {
                throw new InvalidSettingsException("Variable value not contained in possible choices.");
            }
        }
        String layoutText = null;
        for (JRadioButton b : m_layoutButtons) {
            if (b.isSelected()) {
                layoutText = b.getText();
                break;
            }
        }
        config.setChoices(choices);
        config.getValueConfiguration().setValues(values);
        for (Layout l : Layout.values()) {
            if (l.toString().equals(layoutText)) {
                config.getValueConfiguration().setLayout(l);
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    protected void loadAdditionalSettings(final MultipleSelectionInputQuickFormInConfiguration config) {
        final String ch = config.getChoices();
        m_choicesField.setText(ch);
        final String value = config.getValueConfiguration().getValues();
        m_valueField.setText(value);
        Layout layout = config.getValueConfiguration().getLayout();
        for (final JRadioButton button : m_layoutButtons) {
            if (button.getText().equals(layout.toString())) {
                button.setSelected(true);
                break;
            }
        }
    }
}
