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
package org.knime.js.base.node.configuration.filter.value;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.swing.JComboBox;
import javax.swing.JPanel;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.util.filter.NameFilterConfiguration.EnforceOption;
import org.knime.core.node.util.filter.StringFilterPanel;
import org.knime.js.base.dialog.selection.multiple.MultipleSelectionsComponent;
import org.knime.js.base.dialog.selection.multiple.MultipleSelectionsComponentFactory;
import org.knime.js.base.dialog.selection.multiple.TwinlistComponent;
import org.knime.js.base.node.configuration.AbstractDialogNodeConfigurationPanel;

/**
 * The component dialog panel for the value filter configuration node
 *
 * @author Christian Albrecht, KNIME GmbH, Konstanz, Germany
 */
@SuppressWarnings("serial")
public class ValueFilterConfigurationPanel extends AbstractDialogNodeConfigurationPanel<ValueFilterDialogNodeValue> {

    private JComboBox<String> m_column;
    private MultipleSelectionsComponent m_values;


    private final boolean m_twinlistUsed;

    /**
     * A mapping from column names to a list of all possible values of the resp. column.
     */
    private Map<String, List<String>> m_possibleValues;

    /**
     * @param representation the dialog node settings
     */
    public ValueFilterConfigurationPanel(final ValueFilterDialogNodeRepresentation representation) {
        super(representation.getLabel(), representation.getDescription(), representation.getDefaultValue());
        m_possibleValues = representation.getPossibleValues();
        m_column = new JComboBox<String>(representation.getPossibleColumns());
        m_column.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                updateValuesWithPossible(representation.getPossibleValues(), representation.getDefaultValue());
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

        m_twinlistUsed = representation.getType().equals(MultipleSelectionsComponentFactory.TWINLIST);

        if (m_twinlistUsed) {
            // The twinlist already provides radio buttons for enforce inclusion/exclusion (via NameFilterPanel).
            m_values = new TwinlistComponent(false);
        } else {
            m_values = MultipleSelectionsComponentFactory.createMultipleSelectionsComponent(representation.getType());
        }

        gbc.gridy++;
        panel.add(m_values.getComponent(), gbc);

        loadNodeValue(representation.getDefaultValue());

        setComponent(panel);
        m_column.setVisible(!representation.isLockColumn());
    }


    /**
     * Considers the given possible values in the column currently selected. Any of
     * these which are in neither include or exclude list will be added to one of the lists
     * according to the currently set policy. Modifies both the given Value and the state of the
     * UI. Does nothing if no column is selected.
     * @param possibleValues A map from column identifiers to lists of possible values
     *                       of the resp. column.
     */
    private void updateValuesWithPossible(final Map<String, List<String>> possibleValues, final ValueFilterDialogNodeValue value) {
        String selectedCol = (String) m_column.getSelectedItem();
        if (selectedCol == null) { return; }
        List<String> possibleValuesForCol = possibleValues.get(selectedCol);
        // update value
        value.updateInclExcl(possibleValuesForCol);
        // update state of ui
        m_values.setChoices(possibleValuesForCol.toArray(new String[0]));
        m_values.setSelections(value.getValues());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void resetToDefault() {
        setEnforceOptionSelected(getDefaultValue().getEnforceOption());
        m_column.setSelectedItem(getDefaultValue().getColumn());
        m_values.setSelections(getDefaultValue().getValues());
    }

    /**
     * @return The selected enforce inclusion/exclusion policy of the dialog.
     *         Empty if dialog does not provide means to select a policy.
     */
    private Optional<EnforceOption> getSelectedEnforceOption() {
        if (m_twinlistUsed) {
            return ((StringFilterPanel) m_values.getComponent()).getSelectedEnforceOption();
        } else {
            return Optional.empty();
        }
    }


    /**
     * Set the selected enforce option in the dialog if such controls are provided, else do nothing.
     * @param enforceOption
     */
    private void setEnforceOptionSelected(final EnforceOption enforceOption){
        if (m_twinlistUsed) {
            ((StringFilterPanel) m_values.getComponent()).setSelectedEnforceOption(enforceOption);
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    protected ValueFilterDialogNodeValue createNodeValue() throws InvalidSettingsException {
        ValueFilterDialogNodeValue value = new ValueFilterDialogNodeValue();

        String selectedCol = (String) m_column.getSelectedItem();
        if (selectedCol == null) {
            return value; // Value object with default values for members.
        }
        List<String> possibleValuesForCol = m_possibleValues.get(selectedCol);

        String[] selection = m_values.getSelections();
        // 'choices' in the context of a MultipleSelectionComponent are all values that can be selected (i.e.
        // that are shown in the UI) and coincide with the possible values for the column.
        // Excludes are all values that could have been selected but were not.
        ArrayList<String> excludes = new ArrayList<String>();
        HashSet<String> selectionSet = new HashSet<>(Arrays.asList(selection));
        for (String choice : possibleValuesForCol) {
            if (!selectionSet.contains(choice)) excludes.add(choice);
        }
        value.setValues(selection);
        value.setExcludes(excludes.toArray(new String[0]));

        // If dialog provides no way to select policy, we use the policy of the default value
        value.setEnforceOption( getSelectedEnforceOption().orElse(getDefaultValue().getEnforceOption()) );

        value.setColumn((String)m_column.getSelectedItem());
        return value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadNodeValue(final ValueFilterDialogNodeValue value) {
        super.loadNodeValue(value);
        if (value != null) {

            // value might contain an enforce policy that was set while the dialog provided a means to set
            // it (i.e. twinlist was enabled). If, then, the list type is changed in the configuration node
            // dialog, this policy should no longer be considered and the policy of the default value be
            // used instead.
            if (!m_twinlistUsed) {
                value.setEnforceOption(getDefaultValue().getEnforceOption());
            }

            updateValuesWithPossible(m_possibleValues, value);
            m_column.setSelectedItem(value.getColumn());
            m_values.setSelections(value.getValues());
            setEnforceOptionSelected(value.getEnforceOption());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setEnabled(final boolean enabled) {
        super.setEnabled(enabled);
        m_column.setEnabled(enabled);
        m_values.setEnabled(enabled);
    }

}
