/*
 * ------------------------------------------------------------------------
 *  Copyright by KNIME GmbH, Konstanz, Germany
 *  Website: http://www.knime.org; Email: contact@knime.org
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
 *
 * History:
 * 23-Febr-2011: created
 *
 */
package org.knime.quickform.nodes.in.selection.value;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.quickform.AbstractQuickFormConfiguration;

/**
 * Configuration to double input node.
 * @author Thomas Gabriel, KNIME.com, Zurich, Switzerland
 * @since 2.6
 */
final class ValueSelectionInputQuickFormInConfiguration
    extends AbstractQuickFormConfiguration
            <ValueSelectionInputQuickFormValueInConfiguration> {
    
    private final Map<String, Set<String>> m_map 
        = new LinkedHashMap<String, Set<String>>();

    /** {@inheritDoc} */
    @Override
    public ValueSelectionInputQuickFormPanel createController() {
        return new ValueSelectionInputQuickFormPanel(this);
    }


    /** {@inheritDoc} */
    @Override
    public ValueSelectionInputQuickFormValueInConfiguration
            createValueConfiguration() {
        return new ValueSelectionInputQuickFormValueInConfiguration();
    }

    /**
     * @return the choices out of which the value should be selected
     */
    String[] getChoices() {
        return m_map.keySet().toArray(new String[0]);
    }
    
    /**
     * Returns a set of possible choices for the given argument.
     * @param choice to get poss. values from
     * @return a set with all poss. values
     */
    Set<String> getChoiceValues(final String choice) {
        if (m_map.containsKey(choice)) {
            return m_map.get(choice);
        } else {
            return Collections.emptySet();
        }
    }

    /**
     * @param choiceValues map containing choice to set of names
     */
    void setChoiceValues(final Map<String, Set<String>> choiceValues) {
        m_map.clear();
        if (choiceValues != null) {
            m_map.putAll(choiceValues);
        }
    }

    /** Save config to argument.
     * @param settings To save to.
     */
    @Override
    public void saveSettingsTo(final NodeSettingsWO settings) {
        super.saveSettingsTo(settings);
        settings.addStringArray("choices", getChoices());
        NodeSettingsWO subSettings = settings.addNodeSettings("choice-array");
        for (Map.Entry<String, Set<String>> e : m_map.entrySet()) {
            subSettings.addStringArray(e.getKey(), 
                    e.getValue().toArray(new String[0]));            
        }
    }

    /** Load config in model.
     * @param settings To load from.
     * @throws InvalidSettingsException If that fails for any reason.
     */
    @Override
    public void loadSettingsInModel(final NodeSettingsRO settings)
        throws InvalidSettingsException {
        super.loadSettingsInModel(settings);
        String[] choices = settings.getStringArray("choices");
        NodeSettingsRO subSettings = settings.getNodeSettings("choice-array");
        for (int i = 0; i < choices.length; i++) {
            String[] cValues = subSettings.getStringArray(choices[i]);
            LinkedHashSet<String> set = new LinkedHashSet<String>();
            for (int j = 0; j < cValues.length; j++) {
                set.add(cValues[j]);
            }
            m_map.put(choices[i], set);
        }
    }

    /** Load settings in dialog, init defaults if that fails.
     * @param settings To load from.
     */
    @Override
    public void loadSettingsInDialog(final NodeSettingsRO settings) {
        super.loadSettingsInDialog(settings);
        String[] choices = settings.getStringArray("choices", new String[0]);
        try {
            NodeSettingsRO subSettings = 
                settings.getNodeSettings("choice-array");
            for (int i = 0; i < choices.length; i++) {
                String[] cValues = subSettings.getStringArray(
                        choices[i], new String[0]);
                LinkedHashSet<String> set = new LinkedHashSet<String>();
                for (int j = 0; j < cValues.length; j++) {
                    set.add(cValues[j]);
                }
                m_map.put(choices[i], set);
            }
        } catch (InvalidSettingsException ise) {
            // ignored
            return;
        }
    }


}
