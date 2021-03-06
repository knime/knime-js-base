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
package org.knime.js.base.node.configuration.selection.value;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JPanel;

import org.knime.core.node.InvalidSettingsException;
import org.knime.js.base.dialog.selection.single.SingleSelectionComponent;
import org.knime.js.base.dialog.selection.single.SingleSelectionComponentFactory;
import org.knime.js.base.node.configuration.AbstractDialogNodeConfigurationPanel;

/**
 * The component dialog panel for the value selection configuration node
 *
 * @author Christian Albrecht, KNIME GmbH, Konstanz, Germany
 */
@SuppressWarnings("serial")
public class ValueSelectionConfigurationPanel
    extends AbstractDialogNodeConfigurationPanel<ValueSelectionDialogNodeValue> {

    private JComboBox<String> m_column;
    private SingleSelectionComponent m_value;

    /**
     * @param representation the dialog node settings
     */
    public ValueSelectionConfigurationPanel(final ValueSelectionDialogNodeRepresentation representation) {
        super(representation.getLabel(), representation.getDescription(), representation.getDefaultValue());
        m_value = SingleSelectionComponentFactory.createSingleSelectionComponent(representation.getType());
        m_column = new JComboBox<String>(representation.getPossibleColumns());
        m_column.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                List<String> possibleValues = representation.getPossibleValues().get(m_column.getSelectedItem());
                if (possibleValues != null) {
                    m_value.setChoices(possibleValues.toArray(new String[possibleValues.size()]));
                } else {
                    m_value.setChoices(new String[0]);
                }
            }
        });
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        gbc.weighty = 0;
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(m_column, gbc);
        gbc.gridy++;
        panel.add(m_value.getComponent(), gbc);
        m_column.setSelectedItem(representation.getDefaultValue().getColumn());
        List<String> possibleValues = null;
        if (representation.getPossibleValues() != null && m_column.getSelectedItem() != null) {
            possibleValues = representation.getPossibleValues().get(m_column.getSelectedItem());
        }
        if (possibleValues != null) {
            m_value.setChoices(possibleValues.toArray(new String[possibleValues.size()]));
        } else {
            m_value.setChoices(new String[0]);
        }
        m_value.setSelection(representation.getDefaultValue().getValue());
        setComponent(panel);
        m_column.setVisible(!representation.isLockColumn());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void resetToDefault() {
        m_column.setSelectedItem(getDefaultValue().getColumn());
        m_value.setSelection(getDefaultValue().getValue());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected ValueSelectionDialogNodeValue createNodeValue() throws InvalidSettingsException {
        ValueSelectionDialogNodeValue value = new ValueSelectionDialogNodeValue();
        value.setColumn((String)m_column.getSelectedItem());
        value.setValue(m_value.getSelection());
        return value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadNodeValue(final ValueSelectionDialogNodeValue value) {
        super.loadNodeValue(value);
        if (value != null) {
            m_column.setSelectedItem(value.getColumn());
            m_value.setSelection(value.getValue());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setEnabled(final boolean enabled) {
        super.setEnabled(enabled);
        m_column.setEnabled(enabled);
        m_value.setEnabled(enabled);
    }

}
