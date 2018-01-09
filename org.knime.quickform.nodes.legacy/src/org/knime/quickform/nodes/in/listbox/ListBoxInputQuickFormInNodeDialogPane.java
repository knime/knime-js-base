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
 * ------------------------------------------------------------------------
 *
 */
package org.knime.quickform.nodes.in.listbox;

import java.awt.GridBagConstraints;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.knime.core.node.InvalidSettingsException;
import org.knime.quickform.nodes.in.QuickFormInNodeDialogPane;

/**
 * Dialog to node.
 *
 * @author Thomas Gabriel, KNIME AG, Zurich, Switzerland
 * @since 2.6
 */
final class ListBoxInputQuickFormInNodeDialogPane extends
        QuickFormInNodeDialogPane<ListBoxInputQuickFormInConfiguration> {

    private final JTextArea m_valueField;

    private final JTextField m_separatorField;

    private final JCheckBox m_separateEachCharacter;


    /** Constructors, inits fields calls layout routines. */
    ListBoxInputQuickFormInNodeDialogPane() {
        m_valueField = new JTextArea(5, DEF_TEXTFIELD_WIDTH);
        m_separatorField = new JTextField(DEF_TEXTFIELD_WIDTH);
        m_separateEachCharacter = new JCheckBox();
        createAndAddTab();
        m_separateEachCharacter.addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(final ItemEvent e) {
                m_separatorField.setEnabled(!m_separateEachCharacter.isSelected());
            }
        });
    }

    /** {@inheritDoc} */
    @Override
    protected ListBoxInputQuickFormInConfiguration createConfiguration() {
        return new ListBoxInputQuickFormInConfiguration();
    }

    /** {@inheritDoc} */
    @Override
    protected void fillPanel(final JPanel panelWithGBLayout,
            final GridBagConstraints gbc) {
        gbc.gridwidth = 3;
        gbc.weighty = 3;
        int fill = gbc.fill;
        gbc.fill = GridBagConstraints.BOTH;
        addPairToPanel("String List: ", new JScrollPane(m_valueField), panelWithGBLayout, gbc);
        gbc.gridwidth = 1;
        gbc.weighty = 1;
        gbc.fill = fill;
        addPairToPanel("Separator: ", m_separatorField, panelWithGBLayout, gbc);
        addPairToPanel("Separate at each character: ", m_separateEachCharacter, panelWithGBLayout, gbc);
    }

    /** {@inheritDoc} */
    @Override
    protected void saveAdditionalSettings(
            final ListBoxInputQuickFormInConfiguration config)
            throws InvalidSettingsException {
        // trim value
        String value = m_valueField.getText().trim();
        config.getValueConfiguration().setValue(value);
        config.setSeparator(m_separatorField.getText());
        config.setSeparateEachCharacter(m_separateEachCharacter.isSelected());

    }

    /** {@inheritDoc} */
    @Override
    protected void loadAdditionalSettings(
            final ListBoxInputQuickFormInConfiguration config) {
        final String separator = config.getSeparator();
        m_separatorField.setText(separator);
        m_separateEachCharacter.setSelected(config.getSeparateEachCharacter());
        String value = config.getValueConfiguration().getValue();
        m_valueField.setText(value);
    }
}
