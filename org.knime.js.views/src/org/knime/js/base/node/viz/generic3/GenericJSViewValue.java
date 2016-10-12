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
 * History
 *   30.04.2014 (Christian Albrecht, KNIME.com AG, Zurich, Switzerland): created
 */
package org.knime.js.base.node.viz.generic3;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.workflow.FlowVariable;
import org.knime.core.node.workflow.FlowVariable.Type;
import org.knime.js.core.JSONViewContent;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 *
 * @author Christian Albrecht, KNIME.com GmbH, Konstanz, Germany
 */
@JsonAutoDetect
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public class GenericJSViewValue extends JSONViewContent {

    private static final String CFG_FLOW_VARIABLES = "flowVariables";
    private static final String CFG_NUM_VALUES = "numValues";
    private static final String CFG_DEFINED = "defined";
    private Map<String, FlowVariableValue> m_flowVariables = new HashMap<String, FlowVariableValue>();
    private static final String CFG_SETTINGS = "settings";
    private String m_settings;

    /**
    * @return the flowVariables
    */
   @JsonProperty("flowVariables")
   public Map<String, FlowVariableValue> getFlowVariables() {
       return m_flowVariables;
   }

   /**
    * @param flowVariables the flowVariables to set
    */
   @JsonProperty("flowVariables")
   public void setFlowVariables(final Map<String, FlowVariableValue> flowVariables) {
       m_flowVariables = flowVariables;
   }

   /**
    * @return the settings
    */
   @JsonProperty("settings")
   public String getSettings() {
       return m_settings;
   }

   /**
    * @param settings the settings to set
    */
   @JsonProperty("settings")
   public void setSettings(final String settings) {
       m_settings = settings;
   }

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveToNodeSettings(final NodeSettingsWO settings) {
        NodeSettingsWO variableSettings = settings.addNodeSettings(CFG_FLOW_VARIABLES);
        int numVariables = m_flowVariables == null ? 0 : m_flowVariables.size();
        variableSettings.addInt(CFG_NUM_VALUES, numVariables);
        if (numVariables > 0) {
            Iterator<Entry<String, FlowVariableValue>> entries = m_flowVariables.entrySet().iterator();
            for (int v = 0; v < numVariables; v++) {
                Entry<String, FlowVariableValue> entry = entries.next();
                variableSettings.addString("key_" + v, entry.getKey());
                NodeSettingsWO entrySettings = variableSettings.addNodeSettings("value_" + v);
                FlowVariableValue value = entry.getValue();
                entrySettings.addBoolean(CFG_DEFINED, value != null);
                if (value != null) {
                    value.saveToNodeSettings(entrySettings);
                }
            }
        }
        settings.addString(CFG_SETTINGS, m_settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadFromNodeSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
        NodeSettingsRO variableSettings = settings.getNodeSettings(CFG_FLOW_VARIABLES);
        int numVariables = variableSettings.getInt(CFG_NUM_VALUES);
        m_flowVariables = new HashMap<String, FlowVariableValue>();
        for (int v = 0; v < numVariables; v++) {
            String key = variableSettings.getString("key_" + v);
            NodeSettingsRO entrySettings = variableSettings.getNodeSettings("value_" + v);
            FlowVariableValue value = entrySettings.getBoolean(CFG_DEFINED) ? new FlowVariableValue() : null;
            if (value != null) {
                value.loadFromNodeSettings(entrySettings);
            }
            m_flowVariables.put(key, value);
        }
        m_settings = settings.getString(CFG_SETTINGS);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj.getClass() != getClass()) {
            return false;
        }
        GenericJSViewValue other = (GenericJSViewValue)obj;
        return new EqualsBuilder()
                .append(m_flowVariables, other.m_flowVariables)
                .append(m_settings, other.m_settings)
                .isEquals();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(m_flowVariables)
                .append(m_settings)
                .toHashCode();
    }

    /**
     * A JSON serializable object holding the value for one flow variable definition
     * @author Christian Albrecht, KNIME.com GmbH, Konstanz, Germany
     */
    @JsonAutoDetect
    public static class FlowVariableValue {

        private static final String CFG_VALUE = "value";
        private double m_doubleValue;
        private int m_intValue;
        private String m_stringValue;
        private static final String CFG_TYPE = "type";
        private FlowVariable.Type m_type = Type.STRING;

        /**
         * @return the doubleValue
         */
        public double getDoubleValue() {
            return m_doubleValue;
        }
        /**
         * @param doubleValue the doubleValue to set
         */
        public void setDoubleValue(final double doubleValue) {
            m_doubleValue = doubleValue;
        }
        /**
         * @return the intValue
         */
        public int getIntValue() {
            return m_intValue;
        }
        /**
         * @param intValue the intValue to set
         */
        public void setIntValue(final int intValue) {
            m_intValue = intValue;
        }
        /**
         * @return the stringValue
         */
        public String getStringValue() {
            return m_stringValue;
        }
        /**
         * @param stringValue the stringValue to set
         */
        public void setStringValue(final String stringValue) {
            m_stringValue = stringValue;
        }
        /**
         * @return the type
         */
        public FlowVariable.Type getType() {
            return m_type;
        }
        /**
         * @param type the type to set
         */
        public void setType(final FlowVariable.Type type) {
            m_type = type;
        }

        /**
         * Saves the current configuration to the given settings object.
         * @param settings the settings to save to
         */
        public void saveToNodeSettings(final NodeSettingsWO settings) {
            settings.addString(CFG_TYPE, m_type.toString());
            switch (m_type) {
                case DOUBLE:
                    settings.addDouble(CFG_VALUE, m_doubleValue);
                    break;
                case INTEGER:
                    settings.addInt(CFG_VALUE, m_intValue);
                    break;
                default:
                    settings.addString(CFG_VALUE, m_stringValue);
            }
        }

        /**
         * Loads settings from the given settings object into this instance.
         * @param settings the settings to load from
         * @throws InvalidSettingsException on load error
         */
        public void loadFromNodeSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
            m_type = Type.valueOf(settings.getString(CFG_TYPE));
            switch (m_type) {
                case DOUBLE:
                    m_doubleValue = settings.getDouble(CFG_VALUE);
                    break;
                case INTEGER:
                    m_intValue = settings.getInt(CFG_VALUE);
                    break;
                default:
                    m_stringValue = settings.getString(CFG_VALUE);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean equals(final Object obj) {
            if (obj == null) {
                return false;
            }
            if (obj == this) {
                return true;
            }
            if (obj.getClass() != getClass()) {
                return false;
            }
            FlowVariableValue other = (FlowVariableValue)obj;
            return new EqualsBuilder()
                    .append(m_doubleValue, other.m_doubleValue)
                    .append(m_intValue, other.m_intValue)
                    .append(m_stringValue, other.m_stringValue)
                    .append(m_type, other.m_type)
                    .isEquals();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int hashCode() {
            return new HashCodeBuilder()
                    .append(m_doubleValue)
                    .append(m_intValue)
                    .append(m_stringValue)
                    .append(m_type)
                    .toHashCode();
        }

    }

}
