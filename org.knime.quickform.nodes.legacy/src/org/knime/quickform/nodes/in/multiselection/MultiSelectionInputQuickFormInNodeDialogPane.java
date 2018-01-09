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
package org.knime.quickform.nodes.in.multiselection;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.knime.core.node.InvalidSettingsException;
import org.knime.quickform.nodes.in.QuickFormInNodeDialogPane;

/**
 * Dialog to node.
 *
 * @author Thomas Gabriel, KNIME.com AG, Zurich
 * @since 2.6
 */
final class MultiSelectionInputQuickFormInNodeDialogPane extends
        QuickFormInNodeDialogPane<MultiSelectionInputQuickFormInConfiguration> {

    //
    // Members
    //

    /** Text field for column name. */
    private final JTextField m_tfColumnName;

    /** Tabbed pane holding the list choice panel and the panel to select the defaults. */
    private final JTabbedPane m_tabPanel;

    /** The GUI component for the multi-selection list to do the default selections. */
    private MultiSelectionListPanel m_listPanel;

    /** The text area for editing the list values. */
    private JTextArea m_taEditableList;

    /** The checkbox to determine, if a small GUI component shall be used. */
    private final JCheckBox m_cbUseSmallGuiComponent;

    //
    // Constructor
    //

    /** Constructors, inits fields calls layout routines. */
    MultiSelectionInputQuickFormInNodeDialogPane() {
        m_tfColumnName = new JTextField(30);
        m_tabPanel = new JTabbedPane();
        m_tabPanel.add("Edit List Items", createListEditPanel());
        m_tabPanel.add("Select Defaults", createListSelectionPanel());
        m_tabPanel.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(final ChangeEvent ce) {
                if (m_tabPanel.getSelectedIndex() == 0) {
                    updateTextArea();
                } else {
                    updateList();
                }
            }
        });
        m_cbUseSmallGuiComponent = new JCheckBox("Use small UI component for selection");
        createAndAddTab();
    }

    //
    // Protected Methods
    //

    /**
     * Creates the panel shown to edit the list of choices.
     * This gets called from the constructor.
     *
     * @return Panel with editable list components.
     */
    protected JPanel createListEditPanel() {
        m_taEditableList = new JTextArea();
        final JScrollPane scrollPane = new JScrollPane(m_taEditableList,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        final JButton btnSort1 = new JButton("Sort");
        btnSort1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                updateList();
                m_listPanel.sort();
                updateTextArea();
            }
        });

        final JPanel panel = new JPanel(new BorderLayout());
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(btnSort1, BorderLayout.SOUTH);
        return panel;
    }

    /**
     * Creates the panel shown to select the default values.
     * This gets called from the constructor.
     *
     * @return Panel with multi-selection list components.
     */
    protected JPanel createListSelectionPanel() {
        m_listPanel = new MultiSelectionListPanel(new String[0], 7, false);
        m_listPanel.getModel().addListDataListener(new ListDataListener() {
            @Override
            public void intervalRemoved(final ListDataEvent e) {
                updateTextArea();
            }

            @Override
            public void intervalAdded(final ListDataEvent e) {
                updateTextArea();
            }

            @Override
            public void contentsChanged(final ListDataEvent e) {
                updateTextArea();
            }
        });
        final JButton btnSort2 = new JButton("Sort");
        btnSort2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                m_listPanel.sort();
                updateTextArea();
            }
        });

        final JPanel panel = new JPanel(new BorderLayout());
        panel.add(m_listPanel, BorderLayout.CENTER);
        panel.add(btnSort2, BorderLayout.SOUTH);
        return panel;
    }

    /** {@inheritDoc} */
    @Override
    protected void fillPanel(final JPanel panelWithGBLayout,
            final GridBagConstraints gbc) {
        addPairToPanel("Column Name: ", m_tfColumnName,
                panelWithGBLayout, gbc);
        addPairToPanel("List of Choices: ", m_tabPanel,
                panelWithGBLayout, gbc);
        addPairToPanel("UI Option: ", m_cbUseSmallGuiComponent,
                panelWithGBLayout, gbc);
    }

    /** {@inheritDoc} */
    @Override
    protected void saveAdditionalSettings(
            final MultiSelectionInputQuickFormInConfiguration config)
            throws InvalidSettingsException {
        updateList();

        config.setChoices(createSet(m_listPanel.getValues()));
        config.setColumnName(m_tfColumnName.getText());
        config.setSmallGui(m_cbUseSmallGuiComponent.isSelected());
        MultiSelectionInputQuickFormValueInConfiguration valCfg = config
                .getValueConfiguration();
        valCfg.setValues(createArray(m_listPanel.getSelections()));
    }

    /** {@inheritDoc} */
    @Override
    protected void loadAdditionalSettings(
            final MultiSelectionInputQuickFormInConfiguration config) {
        MultiSelectionInputQuickFormValueInConfiguration valCfg = config
                .getValueConfiguration();
        String strColumnName = config.getColumnName();
        String[] arrChoices = config.getChoices();
        boolean bSmallGui = config.isSmallGui();
        String[] arrSelections = valCfg.getValues();

        m_tfColumnName.setText(strColumnName == null ? "" : strColumnName);
        m_listPanel.setValues(arrChoices);
        m_listPanel.selectValues(arrSelections);
        m_cbUseSmallGuiComponent.setSelected(bSmallGui);

        updateTextArea();
    }

    //
    // Private Methods
    //

    /**
     * Updates the list based on the current text in the text area that
     * defines the list elements.
     */
    private void updateList() {
        String strUpdate = m_taEditableList.getText();
        String strExisting = m_listPanel.getMultiLineTextValues("\n");

        if (strUpdate != null && !strUpdate.equals(strExisting)) {
            m_listPanel.setMultiLineTextValues(m_taEditableList.getText(), "\n");
        }
    }

    /**
     * Updates the text area based on the elements that are currently in the list.
     */
    private void updateTextArea() {
        m_taEditableList.setText(m_listPanel.getMultiLineTextValues("\n"));
    }

    /** {@inheritDoc} */
    @Override
    protected MultiSelectionInputQuickFormInConfiguration createConfiguration() {
        return new MultiSelectionInputQuickFormInConfiguration();
    }

    /**
     * Creates a set of strings from the passed in object array.
     *
     * @param arrItems Items to be converted to strings. Can be null.
     *
     * @return Set of strings. Never null, but possibly empty.
     */
    private Set<String> createSet(final Object[] arrItems) {
        Set<String> setItems = new LinkedHashSet<String>();
        if (arrItems != null) {
            for (Object item : arrItems) {
                if (item != null) {
                    setItems.add(item.toString());
                }
            }
        }
        return setItems;
    }

    /**
     * Creates a string array from the passed in object array.
     *
     * @param arrItems Items to be converted to strings. Can be null.
     *
     * @return Array of strings. Never null, but possibly empty.
     */
    private String[] createArray(final Object[] arrItems) {
        return createSet(arrItems).toArray(new String[0]);
    }
}
