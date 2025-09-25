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
 *   24 Sept 2025 (Robin Gerling): created
 */
package org.knime.js.base.node.configuration.value;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;

/**
 * Common load & save logic of the ValueSelection and ValueFilter node used in the node settings and node models.
 *
 * @author Robin Gerling
 */
public final class ValueSelectionFilterUtil {

    private ValueSelectionFilterUtil() {
        // utility
    }

    /**
     * Loads the config from saved settings for dialog display
     *
     * @param settings the settings to load from
     * @param cfgPossibleColumns the config key for the possible columns
     * @param cfgColValues the config key for the possible columns and their values
     * @return the map of possible columns and their respective values
     */
    public static Map<String, List<String>> loadPossibleColumnsAndValuesInDialog(final NodeSettingsRO settings,
        final String cfgPossibleColumns, final String cfgColValues) {
        final var possibleValues = new TreeMap<String, List<String>>();
        String[] columns = settings.getStringArray(cfgPossibleColumns, new String[0]); //NOSONAR
        NodeSettingsRO colSettings = settings;
        try {
            colSettings = settings.getNodeSettings(cfgColValues);
        } catch (InvalidSettingsException e) { //NOSONAR
            /* do nothing */ }
        for (String column : columns) {
            possibleValues.put(column, Arrays.asList(colSettings.getStringArray(column, new String[0]))); //NOSONAR
        }
        return possibleValues;
    }

    /**
     * Saves the current possible values to the given settings
     *
     * @param settings the settings to write the possible columns and respective values to
     * @param possibleValues the map of possible columns and values to save
     * @param cfgPossibleColumns the config key for the possible columns
     * @param cfgColValues the config key for the possible columns and their values
     */
    public static void savePossibleColumnsAndValues(final NodeSettingsWO settings,
        final Map<String, List<String>> possibleValues, final String cfgPossibleColumns, final String cfgColValues) {
        final var keySet = possibleValues.keySet();
        settings.addStringArray(cfgPossibleColumns, keySet.toArray(new String[keySet.size()]));
        NodeSettingsWO colSettings = settings.addNodeSettings(cfgColValues);
        for (String key : keySet) {
            List<String> values = possibleValues.get(key);
            colSettings.addStringArray(key, values.toArray(new String[values.size()]));
        }
    }

}
