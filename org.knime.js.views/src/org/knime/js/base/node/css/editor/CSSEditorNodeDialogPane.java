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
 * History
 *   05.05.2014 (Christian Albrecht, KNIME AG, Zurich, Switzerland): created
 */
package org.knime.js.base.node.css.editor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.text.BadLocationException;

import org.fife.ui.rsyntaxtextarea.folding.Fold;
import org.fife.ui.rsyntaxtextarea.folding.FoldManager;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.knime.core.data.DataTableSpec;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.NotConfigurableException;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.util.FlowVariableListCellRenderer;
import org.knime.core.node.util.ViewUtils;
import org.knime.core.node.util.rsyntaxtextarea.guarded.GuardedSection;
import org.knime.core.node.workflow.FlowVariable;
import org.knime.core.node.workflow.FlowVariable.Type;
import org.knime.js.base.node.css.editor.guarded.CssSnippetDocument;
import org.knime.js.base.node.ui.CSSSnippetTextArea;

/**
 *
 * * @author Daniel Bogenrieder, KNIME GmbH, Konstanz, Germany
 */
final class CSSEditorNodeDialogPane extends NodeDialogPane {

    private static final String GUARDED_SECTION_NAME = "prependedStylesheet";

    private final CSSEditorConfig m_config;

    private final CSSSnippetTextArea m_cssTextArea;

    private final JCheckBox m_prependStylesheetCheckbox;

    private final JComboBox<FlowVariable> m_flowVariableDropdown;

    private final JRadioButton m_newVariableRadioButton;

    private final JTextField m_newVariableEditText;

    private final JRadioButton m_replaceVariableRadioButton;

    private final JComboBox<FlowVariable> m_replaceVariableDropdown;

    private String m_guardedDocumentText;

    /**
     * Initializes new dialog pane.
     */
    CSSEditorNodeDialogPane() {
        m_config = new CSSEditorConfig();
        m_cssTextArea = new CSSSnippetTextArea();

        // Handle prepend stylesheet checkbox
        m_prependStylesheetCheckbox = new JCheckBox("Prepend existing stylesheet:");
        m_prependStylesheetCheckbox.addActionListener(e -> prependStylesheetChanged(false));

        // Handle flow variable dropdown
        m_flowVariableDropdown = new JComboBox<>(new DefaultComboBoxModel<FlowVariable>());
        m_flowVariableDropdown.setRenderer(new FlowVariableListCellRenderer());
        m_flowVariableDropdown.addItemListener(itemEvent -> {
            boolean wasCollapsed = true;
            if (m_cssTextArea.getFoldManager().getDeepestFoldContaining(1) != null) {
                wasCollapsed = m_cssTextArea.getFoldManager().getDeepestFoldContaining(1).isCollapsed();
                m_config.setWasCollapsed(wasCollapsed);
            }
            if (itemEvent.getStateChange() == ItemEvent.SELECTED && m_config.getAppendCheckbox()) {
                m_guardedDocumentText = ((FlowVariable)m_flowVariableDropdown.getSelectedItem()).getStringValue();
                String flowVariableName = ((FlowVariable)m_flowVariableDropdown.getSelectedItem()).getName();
                ((CssSnippetDocument)m_cssTextArea.getDocument()).insertNewGuardedSection(GUARDED_SECTION_NAME,
                    m_guardedDocumentText, flowVariableName);
            }
            if (wasCollapsed) {
                collapseAllFolds();
            }
        });

        // Enable and disable text of not selected Button
        m_newVariableRadioButton = new JRadioButton("Append new variable:");
        m_newVariableRadioButton.addActionListener(e -> appendVariableChanged());
        m_newVariableEditText = new JTextField();

        m_replaceVariableRadioButton = new JRadioButton("Replace existing variable:");
        m_replaceVariableRadioButton.addActionListener(e -> appendVariableChanged());
        m_replaceVariableDropdown = new JComboBox<>(new DefaultComboBoxModel<FlowVariable>());
        m_replaceVariableDropdown.setRenderer(new FlowVariableListCellRenderer());

        addTab("CSS View", initLayout());
    }

    private void appendVariableChanged() {
        if (m_newVariableRadioButton.isSelected()) {
            m_newVariableEditText.setEnabled(true);
            m_replaceVariableDropdown.setEnabled(false);
        } else {
            m_newVariableEditText.setEnabled(false);
            if (m_replaceVariableDropdown.getItemCount() > 0) {
                m_replaceVariableDropdown.setEnabled(true);
            } else {
                m_replaceVariableRadioButton.setEnabled(false);
                m_replaceVariableDropdown.setEnabled(false);
                m_newVariableRadioButton.setSelected(true);
                m_newVariableEditText.setEnabled(true);
                m_config.setSelectedButton(0);
            }
        }
    }

    private void prependStylesheetChanged(final boolean load) {
        if (m_prependStylesheetCheckbox.isSelected()) {
            m_config.setAppendCheckbox(true);
            if (!load) {
                m_config.setCssCode(getCurrentCSSCode(GUARDED_SECTION_NAME));
            }
            if (m_flowVariableDropdown.getItemCount() > 0) {
                m_flowVariableDropdown.setEnabled(true);
                if (m_flowVariableDropdown.getSelectedIndex() >= 0) {
                    appendGuardedSection(m_config.getCssCode(), GUARDED_SECTION_NAME);
                }
            }
        } else {
            m_config.setAppendCheckbox(false);
            ((CssSnippetDocument)m_cssTextArea.getDocument()).removeGuardedSection(GUARDED_SECTION_NAME);
            m_flowVariableDropdown.setEnabled(false);
        }
        m_cssTextArea.discardAllEdits();
    }

    /**
     * Method to create a guarded section
     *
     * @param sectionName Name of the guarded section
     */
    private void appendGuardedSection(final String cssText, final String sectionName) {
        int caretPosition = m_cssTextArea.getCaretPosition();
        m_guardedDocumentText = ((FlowVariable)m_flowVariableDropdown.getSelectedItem()).getStringValue();
        String flowVariableName = ((FlowVariable)m_flowVariableDropdown.getSelectedItem()).getName();
        ((CssSnippetDocument)m_cssTextArea.getDocument()).insertNewGuardedSection(sectionName, m_guardedDocumentText,
            flowVariableName);
        CssSnippetDocument doc = (CssSnippetDocument)m_cssTextArea.getDocument();
        Fold tempFold;
        try {
            tempFold = new Fold(0, m_cssTextArea, 0);
            tempFold.setEndOffset(doc.getGuardedSection(sectionName).getText().length() - 1);

            if (m_config.getWasCollapsed()) {
                tempFold.setCollapsed(true);
            } else {
                tempFold.setCollapsed(false);
            }

            // Create a custom fold to make fold visible directly when text is displayed
            ArrayList<Fold> foldList = new ArrayList<>();
            foldList.add(tempFold);
            m_cssTextArea.getFoldManager().setFolds(foldList);

            m_cssTextArea.setCaretPosition(caretPosition);

            if (m_prependStylesheetCheckbox.isSelected()) {
                doc.remove(doc.getGuardedSection(sectionName).getText().length(),
                    m_cssTextArea.getText().length() - doc.getGuardedSection(sectionName).getText().length());
                doc.insertString(doc.getGuardedSection(sectionName).getText().length(), cssText, null);
            } else {
                doc.remove(0, m_cssTextArea.getText().length() - 1);
                doc.insertString(0, cssText, null);
            }

        } catch (BadLocationException e) {
            throw new IllegalStateException("Implementation error.", e);
        }
    }

    private String getCurrentCSSCode(final String sectionName) {
        CssSnippetDocument doc = (CssSnippetDocument)m_cssTextArea.getDocument();
        GuardedSection guardedSection = doc.getGuardedSection(sectionName);
        if (guardedSection != null) {
            try {
                final int sectionLength = guardedSection.getText().length();
                return doc.getText(sectionLength, doc.getLength() - sectionLength);
            } catch (BadLocationException e) {
                /* this should never happen */
                throw new IllegalStateException();
            }
        } else {
            return m_cssTextArea.getText();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean closeOnESC() {
        return false;
    }

    /**
     * @return
     */
    private JPanel initLayout() {
        Border noBorder = BorderFactory.createEmptyBorder();
        Border paddingBorder = BorderFactory.createEmptyBorder(3, 3, 3, 3);
        Border lineBorder = BorderFactory.createLineBorder(new Color(200, 200, 200), 1);

        JPanel wrapperPanel = new JPanel(new BorderLayout());
        wrapperPanel.setBorder(paddingBorder);
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.X_AXIS));
        topPanel.setBorder(lineBorder);
        topPanel.add(Box.createHorizontalStrut(10));
        topPanel.add(m_prependStylesheetCheckbox);
        topPanel.add(Box.createHorizontalStrut(10));

        topPanel.add(m_flowVariableDropdown);
        topPanel.add(Box.createHorizontalStrut(10));

        wrapperPanel.add(topPanel, BorderLayout.PAGE_START);

        JPanel cssPanel = new JPanel();
        cssPanel.setLayout(new BoxLayout(cssPanel, BoxLayout.X_AXIS));
        cssPanel.setBorder(noBorder);

        JScrollPane cssScrollPane = new RTextScrollPane(m_cssTextArea);
        cssScrollPane.setPreferredSize(new Dimension(510, 400));
        cssPanel.add(cssScrollPane);

        wrapperPanel.add(cssPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
        bottomPanel.setBorder(paddingBorder);
        JPanel newVariabelPanel = new JPanel();
        newVariabelPanel.setLayout(new BoxLayout(newVariabelPanel, BoxLayout.X_AXIS));
        newVariabelPanel.setBorder(noBorder);
        newVariabelPanel.add(m_newVariableRadioButton);
        m_newVariableEditText.setPreferredSize(new Dimension(100, 20));
        newVariabelPanel.add(m_newVariableEditText);

        JPanel replaceVariablePanel = new JPanel();
        replaceVariablePanel.setLayout(new BoxLayout(replaceVariablePanel, BoxLayout.X_AXIS));
        replaceVariablePanel.setBorder(noBorder);
        replaceVariablePanel.add(m_replaceVariableRadioButton);
        m_replaceVariableDropdown.setPreferredSize(new Dimension(100, 20));
        replaceVariablePanel.add(m_replaceVariableDropdown);

        ButtonGroup radioButtongroup = new ButtonGroup();
        radioButtongroup.add(m_newVariableRadioButton);
        radioButtongroup.add(m_replaceVariableRadioButton);
        m_newVariableRadioButton.setSelected(true);

        newVariabelPanel.setAlignmentX(Component.LEFT_ALIGNMENT);//0.0
        replaceVariablePanel.setAlignmentX(Component.LEFT_ALIGNMENT);//0.0
        bottomPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        bottomPanel.add(newVariabelPanel);
        bottomPanel.add(replaceVariablePanel);

        wrapperPanel.add(bottomPanel, BorderLayout.PAGE_END);
        return wrapperPanel;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadSettingsFrom(final NodeSettingsRO settings, final PortObjectSpec[] specs)
        throws NotConfigurableException {
        PortObjectSpec[] s = new PortObjectSpec[specs.length];
        for (int i = 0; i < specs.length; i++) {
            s[i] = specs[i] == null ? new DataTableSpec() : specs[i];
        }
        ViewUtils.invokeAndWaitInEDT(() -> loadSettingsFromInternal(settings));
    }

    private void loadSettingsFromInternal(final NodeSettingsRO settings) {
        resetFields();
        m_config.loadSettingsForDialog(settings);
        for (FlowVariable e : getAvailableFlowVariables().values()) {
            if (e.getType() == Type.STRING && !e.isGlobalConstant()) {
                ((DefaultComboBoxModel<FlowVariable>)m_flowVariableDropdown.getModel()).addElement(e);
                ((DefaultComboBoxModel<FlowVariable>)m_replaceVariableDropdown.getModel()).addElement(e);
                if (e.getName().equals(m_config.getPrependVariable())) {
                    m_flowVariableDropdown.setSelectedItem(e);
                }
                if (e.getName().equals(m_config.getReplaceVariable())) {
                    m_replaceVariableDropdown.setSelectedItem(e);
                }
            }
        }

        if (m_flowVariableDropdown.getItemCount() > 0) {
            m_flowVariableDropdown.setEnabled(true);
            m_replaceVariableDropdown.setEnabled(true);
            m_prependStylesheetCheckbox.setEnabled(true);
            m_replaceVariableRadioButton.setEnabled(true);
            if (m_config.getAppendCheckbox()) {
                m_prependStylesheetCheckbox.setSelected(true);
            }
            if (m_config.getSelectedButton() == 1) {
                m_replaceVariableRadioButton.setSelected(true);
            }
        }
        prependStylesheetChanged(true);
        m_newVariableEditText.setText(m_config.getFlowVariableName());

        appendVariableChanged();

        if (!m_config.getAppendCheckbox()) {
            m_flowVariableDropdown.setEnabled(false);
            m_cssTextArea.setText(m_config.getCssCode());
        }
    }

    @SuppressWarnings("rawtypes")
    private void resetFields() {
        DefaultComboBoxModel prependComboboxModel = (DefaultComboBoxModel)m_flowVariableDropdown.getModel();
        DefaultComboBoxModel replaceComboboxModel = (DefaultComboBoxModel)m_replaceVariableDropdown.getModel();
        prependComboboxModel.removeAllElements();
        replaceComboboxModel.removeAllElements();
        ((CssSnippetDocument)m_cssTextArea.getDocument()).removeGuardedSection(GUARDED_SECTION_NAME);
        m_cssTextArea.setText(null);

        m_flowVariableDropdown.setEnabled(false);
        m_replaceVariableDropdown.setEnabled(false);
        m_prependStylesheetCheckbox.setEnabled(false);
        m_prependStylesheetCheckbox.setSelected(false);
        m_replaceVariableRadioButton.setEnabled(false);
        m_newVariableRadioButton.setSelected(true);
        m_config.setSelectedButton(0);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) throws InvalidSettingsException {
        final CSSEditorConfig config = new CSSEditorConfig();
        CssSnippetDocument doc = (CssSnippetDocument)m_cssTextArea.getDocument();
        try {
            if (doc.getGuardedSection(GUARDED_SECTION_NAME) != null) {
                int guardedSectionLength = doc.getGuardedSection(GUARDED_SECTION_NAME).getText().length();
                config.setCssCode(doc.getText(guardedSectionLength, doc.getLength() - guardedSectionLength));
            } else {
                config.setCssCode(m_cssTextArea.getText());
            }
        } catch (BadLocationException e) {
            throw new IllegalStateException("Implementation error.", e);
        }
        config.setAppendCheckbox(m_prependStylesheetCheckbox.isSelected());
        if (m_prependStylesheetCheckbox.isSelected()) {
            m_guardedDocumentText = ((FlowVariable)m_flowVariableDropdown.getSelectedItem()).getStringValue();
            config.setGuardedDocument(m_guardedDocumentText);
            config.setPrependVariable(((FlowVariable)m_flowVariableDropdown.getSelectedItem()).getName());
        }
        if (m_newVariableRadioButton.isSelected()) {
            config.setSelectedButton(0);
            config.setFlowVariableName(m_newVariableEditText.getText());
        } else {
            config.setSelectedButton(1);
            config.setReplaceVariable(((FlowVariable)m_replaceVariableDropdown.getSelectedItem()).getName());
        }
        if (m_cssTextArea.getFoldManager().getDeepestFoldContaining(1) != null) {
            config.setWasCollapsed(m_cssTextArea.getFoldManager().getDeepestFoldContaining(1).isCollapsed());
        }
        config.saveSettings(settings);
    }

    /* Collapse all folds */
    private void collapseAllFolds() {
        final FoldManager foldManager = m_cssTextArea.getFoldManager();
        final int foldCount = foldManager.getFoldCount();
        for (int i = 0; i < foldCount; i++) {
            final Fold fold = foldManager.getFold(i);
            fold.setCollapsed(true);
        }
    }

}
