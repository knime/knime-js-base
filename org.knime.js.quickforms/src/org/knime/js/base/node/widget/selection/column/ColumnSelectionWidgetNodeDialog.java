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
 *   29 May 2019 (albrecht): created
 */
package org.knime.js.base.node.widget.selection.column;

import java.awt.GridBagConstraints;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.DataValue;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.NotConfigurableException;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.util.ColumnSelectionPanel;
import org.knime.js.base.dialog.selection.single.SingleSelectionComponentFactory;
import org.knime.js.base.node.base.selection.column.ColumnSelectionNodeConfig;
import org.knime.js.base.node.base.selection.column.ColumnSelectionNodeValue;
import org.knime.js.base.node.widget.FlowVariableWidgetNodeDialog;

/**
 * Node dialog for the column selection widget node
 *
 * @author Christian Albrecht, KNIME GmbH, Konstanz, Germany
 */
public class ColumnSelectionWidgetNodeDialog extends FlowVariableWidgetNodeDialog<ColumnSelectionNodeValue> {

    private final ColumnSelectionPanel m_defaultField;
    private final JComboBox<String> m_type;
    private final JCheckBox m_limitNumberVisOptionsBox;
    private final JSpinner m_numberVisOptionSpinner;

    private final ColumnSelectionWidgetConfig m_config;

    private String[] m_possibleColumns;

    /**
     * Constructor, inits fields calls layout routines
     */
    @SuppressWarnings("unchecked")
    public ColumnSelectionWidgetNodeDialog() {
        m_config = new ColumnSelectionWidgetConfig();
        m_type = new JComboBox<String>(SingleSelectionComponentFactory.listSingleSelectionComponents());
        m_defaultField = new ColumnSelectionPanel((Border) null, new Class[]{DataValue.class});
        m_limitNumberVisOptionsBox = new JCheckBox();
        m_numberVisOptionSpinner = new JSpinner(new SpinnerNumberModel(10, 2, Integer.MAX_VALUE, 1));
        createAndAddTab();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getValueString(final NodeSettingsRO settings) throws InvalidSettingsException {
        ColumnSelectionNodeValue value = new ColumnSelectionNodeValue();
        value.loadFromNodeSettings(settings);
        return value.getColumn();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void fillPanel(final JPanel panelWithGBLayout, final GridBagConstraints gbc) {
        addPairToPanel("Selection Type: ", m_type, panelWithGBLayout, gbc);
        addPairToPanel("Default Value: ", m_defaultField, panelWithGBLayout, gbc);

        m_type.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(final ItemEvent e) {
                boolean enabled = SingleSelectionComponentFactory.LIST.equals(m_type.getSelectedItem());
                m_limitNumberVisOptionsBox.setEnabled(enabled);
                m_numberVisOptionSpinner.setEnabled(enabled && m_limitNumberVisOptionsBox.isSelected());
            }
        });
        addPairToPanel("Limit number of visible options: ", m_limitNumberVisOptionsBox, panelWithGBLayout, gbc);
        m_limitNumberVisOptionsBox.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(final ChangeEvent e) {
                m_numberVisOptionSpinner.setEnabled(m_limitNumberVisOptionsBox.isSelected());
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
        DataTableSpec spec = (DataTableSpec) specs[0];
        m_defaultField.update(spec, null);
        m_possibleColumns = spec.getColumnNames();
        String selectedDefault = m_config.getDefaultValue().getColumn();
        if (selectedDefault.isEmpty()) {
            List<DataColumnSpec> cspecs = m_defaultField.getAvailableColumns();
            if (cspecs.size() > 0) {
                selectedDefault = cspecs.get(0).getName();
            }
        }
        m_defaultField.setSelectedColumn(selectedDefault);
        ColumnSelectionNodeConfig columnSelectionConfig = m_config.getColumnSelectionConfig();
        m_type.setSelectedItem(columnSelectionConfig.getType());
        m_limitNumberVisOptionsBox.setSelected(columnSelectionConfig.isLimitNumberVisOptions());
        m_numberVisOptionSpinner.setValue(columnSelectionConfig.getNumberVisOptions());

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) throws InvalidSettingsException {
        saveSettingsTo(m_config);
        m_config.getDefaultValue().setColumn(m_defaultField.getSelectedColumn());
        ColumnSelectionNodeConfig columnSelectionConfig = m_config.getColumnSelectionConfig();
        columnSelectionConfig.setType((String)m_type.getSelectedItem());
        columnSelectionConfig.setPossibleColumns(m_possibleColumns);
        columnSelectionConfig.setLimitNumberVisOptions(m_limitNumberVisOptionsBox.isSelected());
        columnSelectionConfig.setNumberVisOptions((Integer)m_numberVisOptionSpinner.getValue());
        m_config.saveSettings(settings);
    }

}
