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
package org.knime.js.base.node.quickform.input.string;

import java.awt.GridBagConstraints;

import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.NotConfigurableException;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.js.base.node.quickform.QuickFormNodeDialog;

/**
 * The dialog for the string input quick form node.
 *
 * @author Patrick Winter, KNIME.com AG, Zurich, Switzerland
 */
public class StringInputQuickFormNodeDialog extends QuickFormNodeDialog {

    private final RegexPanel m_regexField;

    private final JTextField m_defaultField;

    // added with 3.5

    private final ButtonGroup m_editorTypeGroup;
    private final JRadioButton m_singleLineEditorButton;
    private final JRadioButton m_multilineEditorButton;
    private final JSpinner m_multilineEditorWidthSpinner;
    private final JSpinner m_multilineEditorHeightSpinner;

    private StringInputQuickFormConfig m_config;

    /** Constructors, inits fields calls layout routines. */
    StringInputQuickFormNodeDialog() {
        m_config = new StringInputQuickFormConfig();
        m_regexField = new RegexPanel();
        m_defaultField = new JTextField(DEF_TEXTFIELD_WIDTH);

        // added with 3.5
        m_singleLineEditorButton = new JRadioButton(StringInputQuickFormConfig.EDITOR_TYPE_SINGLE_LINE_STRING);
        m_multilineEditorButton = new JRadioButton(StringInputQuickFormConfig.EDITOR_TYPE_MULTI_LINE_STRING);
        m_editorTypeGroup = new ButtonGroup();
        m_editorTypeGroup.add(m_singleLineEditorButton);
        m_editorTypeGroup.add(m_multilineEditorButton);
        m_multilineEditorWidthSpinner = new JSpinner(new SpinnerNumberModel(60, 10, Integer.MAX_VALUE, 10));
        m_multilineEditorHeightSpinner = new JSpinner(new SpinnerNumberModel(5, 1, Integer.MAX_VALUE, 1));

        m_multilineEditorButton.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(final ChangeEvent e) {
                updateComponents();
            }
        });

        createAndAddTab();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected final void fillPanel(final JPanel panelWithGBLayout, final GridBagConstraints gbc) {
        // added with 3.5
        addPairToPanel("Editor type: ", m_singleLineEditorButton, panelWithGBLayout, gbc);
        addPairToPanel("", m_multilineEditorButton, panelWithGBLayout, gbc);
        addPairToPanel("Multi-line editor width: ", m_multilineEditorWidthSpinner, panelWithGBLayout, gbc);
        addPairToPanel("Multi-line editor height: ", m_multilineEditorHeightSpinner, panelWithGBLayout, gbc);

        // original ones
        addPairToPanel("Regular Expression: ", m_regexField.getRegexPanel(), panelWithGBLayout, gbc);
        addPairToPanel("Validation Error Message: ", m_regexField.getErrorMessagePanel(), panelWithGBLayout, gbc);
        addPairToPanel("Common Regular Expressions: ",
                m_regexField.getCommonRegexesPanel(), panelWithGBLayout, gbc);
        addPairToPanel("Default Value: ", m_defaultField, panelWithGBLayout, gbc);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadSettingsFrom(final NodeSettingsRO settings, final PortObjectSpec[] specs)
            throws NotConfigurableException {
        m_config.loadSettingsInDialog(settings);
        loadSettingsFrom(m_config);
        m_regexField.setRegex(m_config.getRegex());
        m_regexField.setErrorMessage(m_config.getErrorMessage());
        m_defaultField.setText(m_config.getDefaultValue().getString());
        // added with 3.5
        boolean isSingleEditor = m_config.getEditorType().equals(StringInputQuickFormConfig.EDITOR_TYPE_SINGLE_LINE_STRING);
        m_singleLineEditorButton.setSelected(isSingleEditor);
        m_multilineEditorButton.setSelected(!isSingleEditor);
        m_multilineEditorWidthSpinner.setValue(m_config.getMultilineEditorWidth());
        m_multilineEditorHeightSpinner.setValue(m_config.getMultilineEditorHeight());
        updateComponents();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) throws InvalidSettingsException {
        m_regexField.commitRegexHistory();
        saveSettingsTo(m_config);
        m_config.setRegex(m_regexField.getRegex());
        m_config.setErrorMessage(m_regexField.getErrorMessage());
        m_config.getDefaultValue().setString(m_defaultField.getText());
        // added with 3.5
        m_config.setEditorType(m_singleLineEditorButton.isSelected() ? StringInputQuickFormConfig.EDITOR_TYPE_SINGLE_LINE_STRING : StringInputQuickFormConfig.EDITOR_TYPE_MULTI_LINE_STRING);
        m_config.setMultilineEditorWidth((int)m_multilineEditorWidthSpinner.getValue());
        m_config.setMultilineEditorHeight((int) m_multilineEditorHeightSpinner.getValue());

        m_config.saveSettings(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getValueString(final NodeSettingsRO settings) throws InvalidSettingsException {
        StringInputQuickFormValue value = new StringInputQuickFormValue();
        value.loadFromNodeSettings(settings);
        return value.getString();
    }

    /**
     * Update the components state
     */
    protected void updateComponents() {
        boolean isMultiEditor = m_multilineEditorButton.isSelected();

        m_multilineEditorWidthSpinner.setEnabled(isMultiEditor);
        m_multilineEditorHeightSpinner.setEnabled(isMultiEditor);
        String multiLineTooltip = isMultiEditor ? "" : "To enable the control choose the " + StringInputQuickFormConfig.EDITOR_TYPE_MULTI_LINE_STRING + " editor type";
        m_multilineEditorWidthSpinner.setToolTipText(multiLineTooltip);
        m_multilineEditorHeightSpinner.setToolTipText(multiLineTooltip);

        m_regexField.setEnabled(!isMultiEditor);
        String regexTooltip = isMultiEditor ? "To enable the control choose the " + StringInputQuickFormConfig.EDITOR_TYPE_SINGLE_LINE_STRING + " editor type" : "";
        m_regexField.setToolTipText(regexTooltip);
    }
}
