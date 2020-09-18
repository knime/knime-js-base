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
 *   Sep 18, 2020 (Christian Albrecht, KNIME GmbH, Konstanz, Germany): created
 */
package org.knime.js.base.node.widget.filter.definition.value;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Arrays;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.StringValue;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.NotConfigurableException;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.util.ColumnFilter;
import org.knime.core.node.util.ColumnSelectionPanel;
import org.knime.core.node.util.DataValueColumnFilter;
import org.knime.core.node.util.filter.NameFilterConfiguration;
import org.knime.core.node.util.filter.StringFilterPanel;
import org.knime.js.base.dialog.selection.multiple.MultipleSelectionsComponentFactory;
import org.knime.js.base.dialog.selection.single.SingleSelectionComponentFactory;
import org.knime.js.core.settings.DialogUtil;

/**
 * Node dialog for the value filter definition node
 *
 * @author Christian Albrecht, KNIME GmbH, Konstanz, Germany
 */
public class ValueFilterDefinitionWidgetDialog extends NodeDialogPane {

    private final ValueFilterDefinitionWidgetConfig m_config;
    private DataTableSpec m_spec;
    private String m_previousColumn;
    private boolean m_previousMultiple = true;

    private final JCheckBox m_mergeWithExistingFiltersTable;
    private final JCheckBox m_mergeWithExistingFiltersModel;
    private final JCheckBox m_useLabelCheckbox;
    private final JCheckBox m_customLabelCheckbox;
    private final JTextField m_labelTextfield;

    private final ColumnSelectionPanel m_columnSelection;
    private final StringFilterPanel m_defaultField;

    private final JRadioButton m_singleFilterRadioButton;
    private final JRadioButton m_multipleFilterRadioButton;
    private final JComboBox<String> m_typeComboBox;

    private final JCheckBox m_limitNumberVisOptionsBox;
    private final JSpinner m_numberVisOptionSpinner;


    /**
     *
     */
    public ValueFilterDefinitionWidgetDialog() {
        m_config = new ValueFilterDefinitionWidgetConfig();

        m_mergeWithExistingFiltersTable = new JCheckBox("Merge With Existing Filter Definitions (Table)");
        m_mergeWithExistingFiltersModel = new JCheckBox("Merge With Existing Filter Definitions (Model Port)");
        m_useLabelCheckbox = new JCheckBox("Show Label");
        m_useLabelCheckbox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                enableLabel();
            }
        });
        m_customLabelCheckbox = new JCheckBox("Custom");
        m_customLabelCheckbox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                enableLabel();
            }
        });
        m_labelTextfield = new JTextField(20);

        Border colBorder = BorderFactory.createTitledBorder("Filter Column");
        @SuppressWarnings("unchecked")
        ColumnFilter columnFilter = new DataValueColumnFilter(StringValue.class);
        m_columnSelection = new ColumnSelectionPanel(colBorder, columnFilter, false, false);
        m_columnSelection.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                updateLabel();
                updateDefaultField();
            }
        });
        m_defaultField = new StringFilterPanel(false);

        m_singleFilterRadioButton = new JRadioButton("Single Values");
        m_multipleFilterRadioButton = new JRadioButton("Multiple Values");
        ButtonGroup typeGroup = new ButtonGroup();
        typeGroup.add(m_singleFilterRadioButton);
        typeGroup.add(m_multipleFilterRadioButton);
        m_singleFilterRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                updateTypeSelection();
            }
        });
        m_multipleFilterRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                updateTypeSelection();
            }
        });
        m_typeComboBox = new JComboBox<String>(MultipleSelectionsComponentFactory.listMultipleSelectionsComponents());
        m_typeComboBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(final ItemEvent e) {
                enableLimitOptions();
            }
        });
        m_limitNumberVisOptionsBox = new JCheckBox("Limit number of visible options");
        m_limitNumberVisOptionsBox.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(final ChangeEvent e) {
                enableLimitOptions();
            }
        });
        m_numberVisOptionSpinner = new JSpinner(new SpinnerNumberModel(10, 2, Integer.MAX_VALUE, 1));

        addTab("Options", initOptions());
        addTab("Filter", initFilterOptions());
    }

    private JPanel initOptions() {
        JPanel labelPanel = new JPanel(new GridBagLayout());
        labelPanel.setBorder(new TitledBorder("Label Options"));
        GridBagConstraints gbcL = dgbc();
        gbcL.fill = GridBagConstraints.HORIZONTAL;
        gbcL.weightx = 0;
        labelPanel.add(m_useLabelCheckbox, gbcL);
        gbcL.gridx++;
        labelPanel.add(m_customLabelCheckbox, gbcL);
        gbcL.gridx++;
        gbcL.weightx = 1;
        labelPanel.add(m_labelTextfield, gbcL);
        gbcL.gridx = 0;
        gbcL.gridy++;

        JPanel generalPanel = new JPanel(new GridBagLayout());
        generalPanel.setBorder(new TitledBorder("General Options"));
        GridBagConstraints gbcG = dgbc();
        m_columnSelection.setPreferredSize(new Dimension(300, 50));
        generalPanel.add(m_columnSelection, gbcG);
        gbcG.gridy++;
        generalPanel.add(m_defaultField, gbcG);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = dgbc();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(labelPanel, gbc);
        gbc.gridy++;
        panel.add(generalPanel, gbc);
        gbc.gridy++;
        return panel;
    }

    private JPanel initFilterOptions() {
        JPanel filterPanel = new JPanel(new GridBagLayout());
        filterPanel.setBorder(new TitledBorder("Output Options"));
        GridBagConstraints gbcF = dgbc();
        filterPanel.add(m_mergeWithExistingFiltersTable, gbcF);
        gbcF.gridy++;
        filterPanel.add(m_mergeWithExistingFiltersModel, gbcF);

        JPanel typePanel = new JPanel(new GridBagLayout());
        typePanel.setBorder(new TitledBorder("Type"));
        GridBagConstraints gbcT = dgbc();
        typePanel.add(m_multipleFilterRadioButton, gbcT);
        gbcT.gridx++;
        typePanel.add(m_singleFilterRadioButton, gbcT);
        gbcT.gridx = 0;
        gbcT.gridy++;
        gbcT.gridwidth = 2;
        typePanel.add(m_typeComboBox, gbcT);

        JPanel limitPanel = new JPanel(new GridBagLayout());
        limitPanel.setBorder(new TitledBorder("Limit"));
        GridBagConstraints gbcL = dgbc();
        gbcL.gridwidth = 2;
        limitPanel.add(m_limitNumberVisOptionsBox, gbcL);
        gbcL.gridy++;
        gbcL.gridwidth = 1;
        limitPanel.add(new JLabel("Number of visible options"), gbcL);
        gbcL.gridx++;
        limitPanel.add(m_numberVisOptionSpinner, gbcL);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = dgbc();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(filterPanel, gbc);
        gbc.gridy++;
        panel.add(typePanel, gbc);
        gbc.gridy++;
        panel.add(limitPanel, gbc);
        return panel;
    }

    private static GridBagConstraints dgbc() {
        return DialogUtil.defaultGridBagConstraints();
    }

    private void updateDefaultField() {
        String selectedColumn = m_columnSelection.getSelectedColumn();
        if (selectedColumn == null || selectedColumn.equals(m_previousColumn)) {
            return;
        }
        String[] possibleValues =
                ValueFilterDefinitionWidgetNodeModel.getPossibleValuesForColumn(selectedColumn, m_spec);
        List<String> included = Arrays.asList(possibleValues);
        List<String> excluded = Arrays.asList(new String[0]);
        m_defaultField.update(included, excluded, possibleValues);
        m_previousColumn = selectedColumn;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadSettingsFrom(final NodeSettingsRO settings, final PortObjectSpec[] specs)
        throws NotConfigurableException {
        m_spec = (DataTableSpec)specs[0];
        m_config.loadSettingsInDialog(settings, m_spec);

        m_mergeWithExistingFiltersTable.setSelected(m_config.isMergeWithExistingFiltersTable());
        m_mergeWithExistingFiltersModel.setSelected(m_config.isMergeWithExistingFiltersModel());
        m_useLabelCheckbox.setSelected(m_config.isUseLabel());
        m_customLabelCheckbox.setSelected(m_config.isCustomLabel());
        m_labelTextfield.setText(m_config.getLabel());
        m_columnSelection.update(m_spec, m_config.getColumn(), m_config.getColumn() == null);
        DataColumnSpec columnSpec = m_spec.getColumnSpec(m_config.getColumn());
        NameFilterConfiguration filterConfiguration = m_config.getFilterValues();
        if (columnSpec != null) {
            String[] possibleValues =
                ValueFilterDefinitionWidgetNodeModel.getPossibleValuesForColumn(columnSpec.getName(), m_spec);
            m_defaultField.loadConfiguration(filterConfiguration, possibleValues);
            m_previousColumn = columnSpec.getName();
        }

        m_previousMultiple = m_config.isUseMultiple();
        m_multipleFilterRadioButton.setSelected(m_previousMultiple);
        m_singleFilterRadioButton.setSelected(!m_previousMultiple);
        fillTypeSelection();
        m_typeComboBox.setSelectedItem(m_config.getType());
        m_limitNumberVisOptionsBox.setSelected(m_config.isLimitNumberVisOptions());
        m_numberVisOptionSpinner.setValue(m_config.getNumberVisOptions());

        updateLabel();
        enableLabel();
        enableLimitOptions();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) throws InvalidSettingsException {
        m_config.setMergeWithExistingFiltersTable(m_mergeWithExistingFiltersTable.isSelected());
        m_config.setMergeWithExistingFiltersModel(m_mergeWithExistingFiltersModel.isSelected());
        m_config.setUseLabel(m_useLabelCheckbox.isSelected());
        m_config.setCustomLabel(m_customLabelCheckbox.isSelected());
        m_config.setLabel(m_labelTextfield.getText());
        m_config.setColumn(m_columnSelection.getSelectedColumn());
        m_defaultField.saveConfiguration(m_config.getFilterValues());
        m_config.setUseMultiple(m_multipleFilterRadioButton.isSelected());
        m_config.setType((String)m_typeComboBox.getSelectedItem());
        m_config.setLimitNumberVisOptions(m_limitNumberVisOptionsBox.isSelected());
        m_config.setNumberVisOptions((Integer)m_numberVisOptionSpinner.getValue());
        m_config.saveSettings(settings);
    }

    private void updateLabel() {
        if (!m_customLabelCheckbox.isSelected()) {
            m_labelTextfield.setText(m_columnSelection.getSelectedColumn());
        }
    }

    private void enableLabel() {
        boolean enable = m_useLabelCheckbox.isSelected() && m_customLabelCheckbox.isSelected();
        m_labelTextfield.setEnabled(enable);
    }

    private void fillTypeSelection() {
        boolean multiple = m_multipleFilterRadioButton.isSelected();
        m_typeComboBox.removeAllItems();
        String[] options;
        if (multiple) {
            options = MultipleSelectionsComponentFactory.listMultipleSelectionsComponents();
        } else {
            options = SingleSelectionComponentFactory.listSingleSelectionComponents();
        }
        Arrays.stream(options).forEach(option -> m_typeComboBox.addItem(option));
    }

    private void updateTypeSelection() {
        boolean multiple = m_multipleFilterRadioButton.isSelected();
        if (multiple == m_previousMultiple) {
            return;
        }
        fillTypeSelection();
        String defaultOption;
        if (multiple) {
            defaultOption = MultipleSelectionsComponentFactory.TWINLIST;
        } else {
            defaultOption = SingleSelectionComponentFactory.DROPDOWN;
        }
        m_typeComboBox.setSelectedItem(defaultOption);
        m_previousMultiple = multiple;
    }

    private void enableLimitOptions() {
        String selected = (String)m_typeComboBox.getSelectedItem();
        boolean enabled = MultipleSelectionsComponentFactory.LIST.equals(selected)
            || MultipleSelectionsComponentFactory.TWINLIST.equals(selected)
            || SingleSelectionComponentFactory.LIST.equals(selected);
        m_limitNumberVisOptionsBox.setEnabled(enabled);
        m_numberVisOptionSpinner.setEnabled(enabled && m_limitNumberVisOptionsBox.isSelected());
    }

}
