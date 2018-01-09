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
 * History:
 * 23-Febr-2011: created
 */
package org.knime.quickform.nodes.in.selection.value;

import java.awt.BorderLayout;
import java.util.Set;

import javax.swing.JLabel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.defaultnodesettings.DialogComponentStringSelection;
import org.knime.core.node.defaultnodesettings.SettingsModelString;
import org.knime.core.quickform.QuickFormConfigurationPanel;
import org.knime.core.util.node.quickform.in.AbstractQuickFormInElement;
import org.knime.core.util.node.quickform.in.ValueSelectionInputQuickFormInElement;

/**
 * Dialog to node.
 *
 * @author Thomas Gabriel, KNIME AG, Zurich, Switzerland
 * @since 2.6
 */
final class ValueSelectionInputQuickFormPanel
    extends QuickFormConfigurationPanel
        <ValueSelectionInputQuickFormValueInConfiguration> {

    private final DialogComponentStringSelection m_columnField;

    private final DialogComponentStringSelection m_valueField;

    /**
     * Constructors, inits fields calls layout routines.
     * @param cfg quickform configuration
     */
    ValueSelectionInputQuickFormPanel(
            final ValueSelectionInputQuickFormInConfiguration cfg) {
        super(new BorderLayout());
        final JLabel label = new JLabel(cfg.getDescription());
        add(label, BorderLayout.NORTH);
        String[] choices = cfg.getChoices();
        if (choices == null || choices.length == 0) {
            add(new JLabel("No domain information available."), BorderLayout.SOUTH);
            m_columnField = null;
            m_valueField = null;
        } else {
            m_columnField = new DialogComponentStringSelection(new SettingsModelString("column-selection", null),
                    cfg.getLabel(), choices);
            String[] values = cfg.getChoiceValues(choices[0]).toArray(new String[0]);
            m_valueField = new DialogComponentStringSelection(new SettingsModelString("value-selection", null),
                    cfg.getLabel(), values);
            m_columnField.getModel().addChangeListener(new ChangeListener() {
                /** {@inheritDoc} */
                @Override
                public void stateChanged(final ChangeEvent ce) {
                    String column = ((SettingsModelString) m_columnField.getModel()).getStringValue();
                    updateValues(cfg.getChoiceValues(column));
                }
            });
            add(m_columnField.getComponentPanel(), BorderLayout.CENTER);
            add(m_valueField.getComponentPanel(), BorderLayout.SOUTH);
        }
        loadValueConfig(cfg.getValueConfiguration());
    }

    private void updateValues(final Set<String> values) {
        if (values != null && values.size() > 0) {
            if (m_valueField != null) {
                m_valueField.replaceListItems(values, null);
            }
        }
    }


    /** {@inheritDoc} */
    @Override
    public void saveSettings(final ValueSelectionInputQuickFormValueInConfiguration config)
            throws InvalidSettingsException {
        if (m_columnField != null && m_valueField != null) {
            final String column = ((SettingsModelString) m_columnField.getModel()).getStringValue();
            config.setColumn(column);
            final String value = ((SettingsModelString) m_valueField.getModel()).
            getStringValue();
            config.setValue(value);
        }
    }


    /** {@inheritDoc} */
    @Override
    public void loadSettings(
            final ValueSelectionInputQuickFormValueInConfiguration config) {
        loadValueConfig(config);
    }

    private void loadValueConfig(
            final ValueSelectionInputQuickFormValueInConfiguration config) {
        if (m_columnField != null && m_valueField != null) {
            final String column = config.getColumn();
            if (column != null) {
                ((SettingsModelString) m_columnField.getModel()).setStringValue(column);
            }
            final String value = config.getValue();
            if (value != null) {
                ((SettingsModelString) m_valueField.getModel()).setStringValue(value);
            }
        }
    }

    @Override
    public void updateQuickFormInElement(final AbstractQuickFormInElement e) throws InvalidSettingsException {
        ValueSelectionInputQuickFormInElement cast = AbstractQuickFormInElement.cast(
                    ValueSelectionInputQuickFormInElement.class, e);
        if (m_columnField != null && m_valueField != null) {
            final String column = ((SettingsModelString) m_columnField.getModel()).getStringValue();
            cast.setColumn(column);
            final String value = ((SettingsModelString) m_valueField.getModel()).getStringValue();
            cast.setValue(value);
        }
    }
}
