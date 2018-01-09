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
package org.knime.quickform.nodes.in.selection.value;

import java.awt.GridBagConstraints;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.border.Border;

import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.DataValue;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NotConfigurableException;
import org.knime.core.node.defaultnodesettings.DialogComponentStringSelection;
import org.knime.core.node.defaultnodesettings.SettingsModelString;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.util.ColumnSelectionPanel;
import org.knime.quickform.nodes.in.QuickFormInNodeDialogPane;

/**
 * Dialog to node.
 *
 * @author Thomas Gabriel, KNIME AG, Zurich, Switzerland
 * @since 2.6
 */
final class ValueSelectionInputQuickFormInNodeDialogPane
    extends QuickFormInNodeDialogPane
        <ValueSelectionInputQuickFormInConfiguration> {

    private final JCheckBox m_lockColumnCheckBox;

    private final ColumnSelectionPanel m_columnSelection;

    private final DialogComponentStringSelection m_valueSelection;

    /** Constructors, inits fields calls layout routines. */
    @SuppressWarnings("unchecked")
    ValueSelectionInputQuickFormInNodeDialogPane() {
        m_lockColumnCheckBox = new JCheckBox();
        m_columnSelection = new ColumnSelectionPanel((Border) null, new Class[]{DataValue.class});
        m_valueSelection = new DialogComponentStringSelection(new SettingsModelString("value-selection", null), null,
                new String[]{"<no values>"});
        m_valueSelection.getModel().setEnabled(false);
        m_columnSelection.addItemListener(new ItemListener() {
            /** {@inheritDoc} */
            @Override
            public void itemStateChanged(final ItemEvent ie) {
                Object o = ie.getItem();
                if (o != null) {
                    final String column = m_columnSelection.getSelectedColumn();
                    if (column != null) {
                        updateValues(column);
                    }
                }
            }
        });
        createAndAddTab();
    }

    private void updateValues(final String column) {
        final DataTableSpec spec = m_columnSelection.getDataTableSpec();
        DataColumnSpec dcs = spec.getColumnSpec(column);
        final Set<DataCell> vals = dcs.getDomain().getValues();
        Set<String> strValues = new LinkedHashSet<String>();
        for (final DataCell cell : vals) {
            strValues.add(cell.toString());
        }
        m_valueSelection.replaceListItems(strValues, null);
        m_valueSelection.getModel().setEnabled(true);
    }

    /** {@inheritDoc} */
    @Override
    protected void fillPanel(final JPanel panelWithGBLayout, final GridBagConstraints gbc) {
         addPairToPanel("Column Selection: ", m_columnSelection, panelWithGBLayout, gbc);
         addPairToPanel("Lock Column: ", m_lockColumnCheckBox, panelWithGBLayout, gbc);
         addPairToPanel("Value Selection: ", m_valueSelection.getComponentPanel(), panelWithGBLayout, gbc);
    }

    /** {@inheritDoc} */
    @Override
    protected ValueSelectionInputQuickFormInConfiguration createConfiguration() {
        return new ValueSelectionInputQuickFormInConfiguration();
    }

    /** {@inheritDoc} */
    @Override
    protected void loadSettingsFrom(final NodeSettingsRO settings,
        final PortObjectSpec[] specs) throws NotConfigurableException {
        final DataTableSpec spec = (DataTableSpec) specs[0];
        final List<DataColumnSpec> filteredSpecs = new ArrayList<DataColumnSpec>();
        for (DataColumnSpec cspec : spec) {
            if (cspec.getDomain().hasValues()) {
                filteredSpecs.add(cspec);
            }
        }
        if (filteredSpecs.size() == 0) {
            throw new NotConfigurableException("Data does not contain any column with domain values.");
        }
        final DataTableSpec newDTS = new DataTableSpec(filteredSpecs.toArray(new DataColumnSpec[0]));
        m_columnSelection.update(newDTS, null);
        final PortObjectSpec[] portSpecs = new PortObjectSpec[]{newDTS};
        m_valueSelection.loadSettingsFrom(settings, portSpecs);
        super.loadSettingsFrom(settings, portSpecs);
    }


    /** {@inheritDoc} */
    @Override
    protected void saveAdditionalSettings(final ValueSelectionInputQuickFormInConfiguration config)
               throws InvalidSettingsException {
        final DataTableSpec spec = m_columnSelection.getDataTableSpec();
        config.setChoiceValues(ValueSelectionInputQuickFormInNodeModel.createMap(spec));
        String column = m_columnSelection.getSelectedColumn();
        config.getValueConfiguration().setLockColumn(m_lockColumnCheckBox.isSelected());
        config.getValueConfiguration().setColumn(column);
        String value = ((SettingsModelString) m_valueSelection.getModel()).getStringValue();
        config.getValueConfiguration().setValue(value);
    }

    /** {@inheritDoc} */
    @Override
    protected void loadAdditionalSettings(final ValueSelectionInputQuickFormInConfiguration config) {
        m_lockColumnCheckBox.setSelected(config.getValueConfiguration().getLockColumn());
        final String column = config.getValueConfiguration().getColumn();
        if (column != null) {
            m_columnSelection.setSelectedColumn(column);
        }

        final String value = config.getValueConfiguration().getValue();
        if (value != null) {
            ((SettingsModelString) m_valueSelection.getModel()).setStringValue(value);
        }
    }
}
