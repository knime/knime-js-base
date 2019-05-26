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

import java.awt.GridBagConstraints;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.NotConfigurableException;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.js.base.node.base.listbox.ListBoxNodeConfig;
import org.knime.js.base.node.configuration.FlowVariableDialogNodeNodeDialog;
import org.knime.js.base.node.quickform.input.string.RegexPanel;
import org.knime.js.core.settings.DialogUtil;

/**
 * The dialog for the list box configuration node
 *
 * @author Christian Albrecht, KNIME GmbH, Konstanz, Germany
 */
public class ListBoxDialogNodeNodeDialog extends FlowVariableDialogNodeNodeDialog<ListBoxDialogNodeValue> {

    private static final int TEXT_AREA_HEIGHT = 5;

    private final JTextField m_separatorField;
    private final JCheckBox m_separateEachCharacterBox;
    private final JCheckBox m_omitEmptyField;
    private final RegexPanel m_regexField;
    private final JTextArea m_defaultArea;
    private final JSpinner m_numberVisOptionSpinner;

    private final ListBoxDialogNodeConfig m_config;

    /**
     * Constructor, inits fields calls layout routines
     */
    public ListBoxDialogNodeNodeDialog() {
        m_config = new ListBoxDialogNodeConfig();
        m_separatorField = new JTextField(DialogUtil.DEF_TEXTFIELD_WIDTH);
        m_separateEachCharacterBox = new JCheckBox();
        m_omitEmptyField = new JCheckBox();
        m_regexField = new RegexPanel();
        m_defaultArea = new JTextArea(TEXT_AREA_HEIGHT, DialogUtil.DEF_TEXTFIELD_WIDTH);
        m_numberVisOptionSpinner = new JSpinner(new SpinnerNumberModel(5, 2, Integer.MAX_VALUE, 1));
        createAndAddTab();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getValueString(final NodeSettingsRO settings) throws InvalidSettingsException {
        ListBoxDialogNodeValue value = new ListBoxDialogNodeValue();
        value.loadFromNodeSettings(settings);
        return value.getString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void fillPanel(final JPanel panelWithGBLayout, final GridBagConstraints gbc) {
        addPairToPanel("Separator: ", m_separatorField, panelWithGBLayout, gbc);
        addPairToPanel("Separate at each character: ", m_separateEachCharacterBox, panelWithGBLayout, gbc);
        addPairToPanel("Omit Empty Values: ", m_omitEmptyField, panelWithGBLayout, gbc);
        addPairToPanel("Regular Expression: ", m_regexField.getRegexPanel(), panelWithGBLayout, gbc);
        addPairToPanel("Validation Error Message: ", m_regexField.getErrorMessagePanel(), panelWithGBLayout, gbc);
        addPairToPanel("Common Regular Expressions: ",
                m_regexField.getCommonRegexesPanel(), panelWithGBLayout, gbc);
        gbc.weighty = 3;
        int fill = gbc.fill;
        gbc.fill = GridBagConstraints.BOTH;
        addPairToPanel("Default Value: ", new JScrollPane(m_defaultArea), panelWithGBLayout, gbc);
        gbc.fill = fill;
        m_separateEachCharacterBox.addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(final ItemEvent e) {
                m_separatorField.setEnabled(!m_separateEachCharacterBox.isSelected());
            }
        });
        addPairToPanel("Number of visible options: ", m_numberVisOptionSpinner, panelWithGBLayout, gbc);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadSettingsFrom(final NodeSettingsRO settings, final PortObjectSpec[] specs) throws NotConfigurableException {
        m_config.loadSettingsInDialog(settings);
        super.loadSettingsFrom(m_config);
        ListBoxNodeConfig config = m_config.getListBoxConfig();
        String separatorString = config.getSeparator();
        if (separatorString == null) {
            separatorString = ListBoxNodeConfig.DEFAULT_SEPARATOR;
        }
        m_separatorField.setText(separatorString);
        m_separateEachCharacterBox.setSelected(config.getSeparateEachCharacter());
        m_omitEmptyField.setSelected(config.getOmitEmpty());
        m_regexField.setRegex(config.getRegex());
        m_regexField.setErrorMessage(config.getErrorMessage());
        m_defaultArea.setText(m_config.getDefaultValue().getString());
        m_numberVisOptionSpinner.setValue(config.getNumberVisOptions());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) throws InvalidSettingsException {
        super.saveSettingsTo(m_config);
        ListBoxNodeConfig config = m_config.getListBoxConfig();
        config.setSeparator(m_separatorField.getText());
        config.setSeparateEachCharacter(m_separateEachCharacterBox.isSelected());
        config.setOmitEmpty(m_omitEmptyField.isSelected());
        config.setRegex(m_regexField.getRegex());
        config.setErrorMessage(m_regexField.getErrorMessage());
        m_config.getDefaultValue().setString(m_defaultArea.getText());
        config.setNumberVisOptions((Integer)m_numberVisOptionSpinner.getValue());
        m_config.saveSettings(settings);
    }

}
