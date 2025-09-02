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
 *   AI Migration - Common persistors for widget nodes
 */
package org.knime.js.base.node.widget.util;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.node.parameters.persistence.NodeParametersPersistor;

/**
 * Collection of common persistors used across widget nodes for backward compatibility.
 *
 * @author AI Migration
 */
@SuppressWarnings("restriction")
public final class CommonWidgetPersistors {

    private CommonWidgetPersistors() {
        // Utility class
    }

    /**
     * Persistor for string default values that are stored in a nested "defaultValue" config structure.
     * Legacy structure: defaultValue/string
     */
    public static final class StringDefaultValuePersistor implements NodeParametersPersistor<String> {

        @Override
        public String load(final NodeSettingsRO settings) throws InvalidSettingsException {
            if (settings.containsKey("defaultValue")) {
                NodeSettingsRO defaultValueSettings = settings.getNodeSettings("defaultValue");
                return defaultValueSettings.getString("string", "");
            }
            return "";
        }

        @Override
        public void save(final String obj, final NodeSettingsWO settings) {
            NodeSettingsWO defaultValueSettings = settings.addNodeSettings("defaultValue");
            defaultValueSettings.addString("string", obj != null ? obj : "");
        }

        @Override
        public String[][] getConfigPaths() {
            return new String[][]{{"defaultValue"}};
        }
    }

    /**
     * Persistor for double default values that are stored in a nested "defaultValue" config structure.
     * Legacy structure: defaultValue/double
     */
    public static final class DoubleDefaultValuePersistor implements NodeParametersPersistor<Double> {

        @Override
        public Double load(final NodeSettingsRO settings) throws InvalidSettingsException {
            if (settings.containsKey("defaultValue")) {
                NodeSettingsRO defaultValueSettings = settings.getNodeSettings("defaultValue");
                return defaultValueSettings.getDouble("double", 0.0);
            }
            return 0.0;
        }

        @Override
        public void save(final Double obj, final NodeSettingsWO settings) {
            NodeSettingsWO defaultValueSettings = settings.addNodeSettings("defaultValue");
            defaultValueSettings.addDouble("double", obj != null ? obj : 0.0);
        }

        @Override
        public String[][] getConfigPaths() {
            return new String[][]{{"defaultValue"}};
        }
    }

    /**
     * Persistor for integer default values that are stored in a nested "defaultValue" config structure.
     * Legacy structure: defaultValue/integer
     */
    public static final class IntegerDefaultValuePersistor implements NodeParametersPersistor<Integer> {

        @Override
        public Integer load(final NodeSettingsRO settings) throws InvalidSettingsException {
            if (settings.containsKey("defaultValue")) {
                NodeSettingsRO defaultValueSettings = settings.getNodeSettings("defaultValue");
                return defaultValueSettings.getInt("integer", 0);
            }
            return 0;
        }

        @Override
        public void save(final Integer obj, final NodeSettingsWO settings) {
            NodeSettingsWO defaultValueSettings = settings.addNodeSettings("defaultValue");
            defaultValueSettings.addInt("integer", obj != null ? obj : 0);
        }

        @Override
        public String[][] getConfigPaths() {
            return new String[][]{{"defaultValue"}};
        }
    }

    /**
     * Persistor for boolean default values that are stored in a nested "defaultValue" config structure.
     * Legacy structure: defaultValue/boolean
     */
    public static final class BooleanDefaultValuePersistor implements NodeParametersPersistor<Boolean> {

        @Override
        public Boolean load(final NodeSettingsRO settings) throws InvalidSettingsException {
            if (settings.containsKey("defaultValue")) {
                NodeSettingsRO defaultValueSettings = settings.getNodeSettings("defaultValue");
                return defaultValueSettings.getBoolean("boolean", false);
            }
            return false;
        }

        @Override
        public void save(final Boolean obj, final NodeSettingsWO settings) {
            NodeSettingsWO defaultValueSettings = settings.addNodeSettings("defaultValue");
            defaultValueSettings.addBoolean("boolean", obj != null ? obj : false);
        }

        @Override
        public String[][] getConfigPaths() {
            return new String[][]{{"defaultValue"}};
        }
    }
}
