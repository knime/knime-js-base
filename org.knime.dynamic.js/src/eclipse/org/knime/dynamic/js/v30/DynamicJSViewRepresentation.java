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
 * History
 *   24.04.2015 (Christian Albrecht, KNIME AG, Zurich, Switzerland): created
 */
package org.knime.dynamic.js.v30;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.dynamic.js.DynamicJSDependency;
import org.knime.dynamic.js.SettingsModelSVGOptions.JSONSVGOptions;
import org.knime.js.core.JSONDataTable;
import org.knime.js.core.JSONViewContent;
import org.knime.js.core.components.datetime.SettingsModelDateTimeOptions.JSONDateTimeOptions;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;

/**
 *
 * @author Christian Albrecht, KNIME AG, Zurich, Switzerland
 * @since 3.0
 */
@JsonAutoDetect
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public class DynamicJSViewRepresentation extends JSONViewContent {

    private static final NodeLogger LOGGER = NodeLogger.getLogger(DynamicJSViewRepresentation.class);

	static final String NUM_SETTINGS = "numSettings";
	private static final String JS_NAMESPACE = "jsNamespace";
	private static final String JS_CODE = "jsCode";
	private static final String CSS_CODE = "cssCode";
	private static final String JS_DEPENDENCIES = "jsDependencies";
    private static final String CSS_DEPENDENCIES = "cssDependencies";
    private static final String BINARY_FILES = "binaryFiles";
    private static final String IN_OBJECTS = "inObjects";
    private static final String TABLE_IDS = "tableIds";
    static final String FLOW_VARIABLES = "variables";
    static final String OPTIONS = "options";
    static final String CLASS_NAME = "className";
    static final String JSON_VALUE = "jsonValue";
    private static final String NEW = "new";
    private static final String IN_VIEW = "inView";
    private static final String WARN_MESSAGE = "warnMessage";
    private static final String ERROR_MESSAGE = "errorMessage";

    private String m_jsNamespace = new String();
    private String[] m_jsCode = new String[0];
    private String[] m_cssCode = new String[0];
    private DynamicJSDependency[] m_jsDependencies = new DynamicJSDependency[0];
    private List<String> m_cssDependencies = new ArrayList<String>();
    private Object[] m_inObjects = new Object[0];
    private String[] m_tableIds = new String[0];
    private Map<String, String> m_flowVariables = new HashMap<String, String>();
    private Map<String, Object> m_options = new HashMap<String, Object>();
    private Map<String, String> m_binaryFiles = new HashMap<String, String>();
    private String m_warnMessage = new String();
    private String m_errorMessage = new String();

    private boolean m_new = true;
    private boolean m_runningInView = true;

    @JsonProperty("jsCode")
    public String[] getJsCode() {
		return m_jsCode;
	}

    @JsonProperty("jsCode")
    public void setJsCode(final String[] jsCode) {
		m_jsCode = jsCode;
	}

    @JsonProperty("cssCode")
    public String[] getCssCode() {
		return m_cssCode;
	}

    @JsonProperty("cssCode")
    public void setCssCode(final String[] cssCode) {
		m_cssCode = cssCode;
	}

    @JsonProperty("jsDependencies")
    public DynamicJSDependency[] getJsDependencies() {
		return m_jsDependencies;
	}

    @JsonProperty("jsDependencies")
    public void setJsDependencies(final DynamicJSDependency[] jsDependencies) {
		m_jsDependencies = jsDependencies;
	}

    @JsonProperty("jsNamespace")
    public String getJsNamespace() {
		return m_jsNamespace;
	}

    @JsonProperty("jsNamespace")
    public void setJsNamespace(final String jsNamespace) {
		m_jsNamespace = jsNamespace;
	}

    @JsonProperty("cssDependencies")
    public String[] getCssDependencies() {
		return m_cssDependencies.toArray(new String[0]);
	}

    @JsonProperty("cssDependencies")
    public void setCssDependencies(final String[] cssDependencies) {
		m_cssDependencies = Arrays.asList(cssDependencies);
	}

    @JsonIgnore
    public void addCssDependencies(final String... cssDependencies) {
        m_cssDependencies.addAll(Arrays.asList(cssDependencies));
    }

    @JsonProperty("inObjects")
    public Object[] getInObjects() {
		return m_inObjects;
	}

    @JsonProperty("inObjects")
    public void setInObjects(final Object[] inObjects) {
		m_inObjects = inObjects;
	}

    /**
     * @since 3.3
     */
    @JsonProperty("tableIds")
    public String[] getTableIds() {
        return m_tableIds;
    }

    /**
     * @since 3.3
     */
    @JsonProperty("tableIds")
    public void setTableIds(final String[] tableIds) {
        m_tableIds = tableIds;
    }

    @JsonProperty("flowVariables")
    public Map<String, String> getFlowVariables() {
		return m_flowVariables;
	}

    @JsonProperty("flowVariables")
    public void setFlowVariables(final Map<String, String> flowVariables) {
		m_flowVariables = flowVariables;
	}

    @JsonProperty("options")
    public Map<String, Object> getOptions() {
		return m_options;
	}

    @JsonProperty("options")
    public void setOptions(final Map<String, Object> options) {
		m_options = options;
	}

    @JsonProperty("binaryFiles")
    public Map<String, String> getBinaryFiles() {
		return m_binaryFiles;
	}

    @JsonProperty("binaryFiles")
    public void setBinaryFiles(final Map<String, String> binaryFiles) {
		m_binaryFiles = binaryFiles;
	}

    /**
     * @since 3.4
     */
    @JsonProperty("warnMessage")
    public String getWarnMessage() {
        return m_warnMessage;
    }

    /**
     * @since 3.4
     */
    @JsonProperty("warnMessage")
    public void setWarnMessage(final String warnMessage) {
        m_warnMessage = warnMessage;
    }

    /**
     * @since 3.4
     */
    @JsonProperty("errorMessage")
    public String getErrorMessage() {
        return m_errorMessage;
    }

    /**
     * @since 3.4
     */
    @JsonProperty("errorMessage")
    public void setErrorMessage(final String errorMessage) {
        m_errorMessage = errorMessage;
    }

    @JsonIgnore
    public boolean isNew() {
        return m_new;
    }

    @JsonIgnore
    public void setInitialized() {
        m_new = false;
    }

    /**
     * @return if running in view
     */
    public boolean getRunningInView() {
        return m_runningInView;
    }

    /**
     * @param runningInView the runningInView to set
     */
    public void setRunningInView(final boolean runningInView) {
        m_runningInView = runningInView;
    }

	@Override
	public void saveToNodeSettings(final NodeSettingsWO settings) {
		settings.addString(JS_NAMESPACE, m_jsNamespace);
		settings.addStringArray(JS_CODE, m_jsCode);
		settings.addStringArray(CSS_CODE, m_cssCode);
		NodeSettingsWO dependencySettings = settings.addNodeSettings(JS_DEPENDENCIES);
		dependencySettings.addInt(NUM_SETTINGS, m_jsDependencies.length);
		for (int i = 0; i < m_jsDependencies.length; i++) {
		    m_jsDependencies[i].saveToNodeSettings(dependencySettings.addNodeSettings("dependency_" + i));
		}
        settings.addStringArray(CSS_DEPENDENCIES, m_cssDependencies.toArray(new String[0]));
        settings.addBoolean(NEW, m_new);
        settings.addBoolean(IN_VIEW, m_runningInView);
        settings.addStringArray(TABLE_IDS, m_tableIds);
        saveMap(settings.addNodeSettings(FLOW_VARIABLES), m_flowVariables, false);
        saveMap(settings.addNodeSettings(BINARY_FILES), m_binaryFiles, false);
        saveMap(settings.addNodeSettings(OPTIONS), m_options, true);
        NodeSettingsWO inObjects = settings.addNodeSettings(IN_OBJECTS);
        inObjects.addInt(NUM_SETTINGS, m_inObjects.length);
        for (int i = 0; i < m_inObjects.length; i++) {
            NodeSettingsWO objectSettings = inObjects.addNodeSettings("inObject_" + i);
            if (m_inObjects[i] instanceof JSONDataTable) {
                ((JSONDataTable)m_inObjects[i]).saveJSONToNodeSettings(objectSettings);
            } else {
                String jsonString = null;
                if (m_inObjects[i] != null) {
                    try {
                        // assume object is serializable into JSON
                        ObjectMapper mapper = new ObjectMapper();
                        jsonString = mapper.writeValueAsString(m_inObjects[i]);
                    } catch (JsonProcessingException e) {
                        LOGGER.error("Failed to write inObject from port index " + i
                            + ". Possible implementation or processing error: " + e.getMessage(), e);
                    }
                    objectSettings.addString(CLASS_NAME, m_inObjects[i].getClass().getName());
                }
                objectSettings.addString(JSON_VALUE, jsonString);
            }
        }

        //added with 3.4
        settings.addString(WARN_MESSAGE, m_warnMessage);
        settings.addString(ERROR_MESSAGE, m_errorMessage);
	}

    static void saveMap(final NodeSettingsWO settings, final Map<String, ?> map, final boolean objectMap) {
		settings.addInt(NUM_SETTINGS, map.size());
		String mapClass = objectMap ? "object" : "string";
		settings.addString("mapClass", mapClass);
		int i = 0;
		for (Entry<String, ?> entry : map.entrySet()) {
			settings.addString("key_" + i, entry.getKey());
			saveCollectionValue(settings, entry.getValue(), i);
			i++;
		}
	}

    static void saveList(final NodeSettingsWO settings, final List<?> list) {
        settings.addInt(NUM_SETTINGS, list.size());
        for (int i = 0; i < list.size(); i++) {
            saveCollectionValue(settings, list.get(i), i);
        }
    }

    @SuppressWarnings("unchecked")
    static void saveCollectionValue(final NodeSettingsWO settings, final Object value, final Integer index) {
        String valueKey = "value_" + index;
        if (value == null) {
            settings.addString("class_" + index, String.class.getName());
            settings.addString(valueKey, null);
            return;
        }
        settings.addString("class_" + index, value.getClass().getName());
        //TODO: add possible other types
        if (value instanceof Boolean) {
            settings.addBoolean(valueKey, (Boolean)value);
        } else if (value instanceof Integer) {
            settings.addInt(valueKey, (Integer)value);
        } else if (value instanceof Double) {
            settings.addDouble(valueKey, (Double)value);
        } else if (value instanceof String){
            settings.addString(valueKey, (String)value);
        } else if (value instanceof String[]) {
            settings.addStringArray(valueKey, (String[])value);
        } else if (value instanceof Date) {
            settings.addLong(valueKey, ((Date)value).getTime());
        } else if (value instanceof JSONDataTable) {
            ((JSONDataTable)value).saveJSONToNodeSettings(settings.addNodeSettings(valueKey));
        } else if (value instanceof JSONSVGOptions) {
            ((JSONSVGOptions)value).saveToNodeSettings(settings.addNodeSettings(valueKey));
        } else if (value instanceof JSONDateTimeOptions) {
            ((JSONDateTimeOptions)value).saveToNodeSettings(settings.addNodeSettings(valueKey));
        } else if (value instanceof List<?>) {
            saveList(settings.addNodeSettings(valueKey), (List<?>)value);
        } else if (value instanceof Map<?,?>) {
            saveMap(settings.addNodeSettings(valueKey), (Map<String, ?>)value, true);
        } else {
            settings.addString(valueKey, value.toString());
        }
    }

    @SuppressWarnings("unchecked")
	@Override
	public void loadFromNodeSettings(final NodeSettingsRO settings)
			throws InvalidSettingsException {
		NodeSettingsRO dependencySettings = settings.getNodeSettings(JS_DEPENDENCIES);
		m_jsDependencies = new DynamicJSDependency[dependencySettings.getInt(NUM_SETTINGS)];
		for (int i = 0; i < m_jsDependencies.length; i++) {
		    DynamicJSDependency dep = new DynamicJSDependency();
		    dep.loadFromNodeSettings(dependencySettings.getNodeSettings("dependency_" + i));
		    m_jsDependencies[i] = dep;
		}
		m_jsCode = settings.getStringArray(JS_CODE);
		m_cssCode = settings.getStringArray(CSS_CODE);
		m_jsNamespace = settings.getString(JS_NAMESPACE);
        m_cssDependencies = Arrays.asList(settings.getStringArray(CSS_DEPENDENCIES));
        m_new = settings.getBoolean(NEW);
        m_runningInView = settings.getBoolean(IN_VIEW);

        m_flowVariables = (Map<String, String>) loadMap(settings.getNodeSettings(FLOW_VARIABLES));
        m_binaryFiles = (Map<String, String>) loadMap(settings.getNodeSettings(BINARY_FILES));
        m_options = (Map<String, Object>) loadMap(settings.getNodeSettings(OPTIONS));
        NodeSettingsRO inObjects = settings.getNodeSettings(IN_OBJECTS);
        int numSettings = inObjects.getInt(NUM_SETTINGS);
        m_inObjects = new Object[numSettings];
        for (int i = 0; i < numSettings; i++) {
            NodeSettingsRO objectSettings = inObjects.getNodeSettings("inObject_" + i);
            if (objectSettings.containsKey(JSONDataTable.KNIME_DATA_TABLE_CONF)) {
                m_inObjects[i] = JSONDataTable.loadFromNodeSettings(objectSettings);
            } else {
                String jsonString = objectSettings.getString(JSON_VALUE);
                Object jsonObject = null;
                if (jsonString != null && !jsonString.isEmpty()) {
                    try {
                        // TODO: Possibly use URLClassLoader for inner classes of DynamicJSProcessors
                        Class<?> c = Class.forName(objectSettings.getString(CLASS_NAME));
                        jsonObject = c.newInstance();
                        ObjectMapper mapper = new ObjectMapper();
                        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                        ObjectReader reader = mapper.readerForUpdating(jsonObject);
                        reader.readValue(jsonString);
                    } catch (Exception e) {
                        LOGGER.error("Unable to deserialize inObject from JSON: " + e.getMessage(), e);
                    }
                }
                m_inObjects[i] = jsonObject;
            }
        }

        // ids added with 3.3
        m_tableIds = settings.getStringArray(TABLE_IDS, new String[0]);

        // added with 3.4
        m_warnMessage = settings.getString(WARN_MESSAGE, new String());
        m_errorMessage = settings.getString(ERROR_MESSAGE, new String());
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	static Map<String, ?> loadMap(final NodeSettingsRO settings) throws InvalidSettingsException {
		int numSettings = settings.getInt(NUM_SETTINGS);
		String mapClass = settings.getString("mapClass");
		Map map;
		if (mapClass.equals("string")) {
			map = new HashMap<String, String>();
		} else {
			map = new HashMap<String, Object>();
		}
		for (int i = 0; i < numSettings; i++) {
			String key = settings.getString("key_" + i);
			Object value = loadCollectionValue(settings, i);
			map.put(key, value);
		}
		return map;
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
    static ArrayList<?> loadList(final NodeSettingsRO settings) throws InvalidSettingsException {
	    int numSettings = settings.getInt(NUM_SETTINGS);
        ArrayList list = new ArrayList();
        for (int i = 0; i < numSettings; i++) {
            Object value = loadCollectionValue(settings, i);
            list.add(value);
        }
        return list;
	}

	@SuppressWarnings("rawtypes")
    static Object loadCollectionValue(final NodeSettingsRO settings, final int index) throws InvalidSettingsException {
	    Object value = null;
	    String classString = settings.getString("class_" + index);
	    Class clazz;
        try {
            clazz = Class.forName(classString);
        } catch (ClassNotFoundException e) {
            throw new InvalidSettingsException("Could not find class for name: " + classString);
        }
        String valueKey = "value_" + index;
        if (Boolean.class.equals(clazz)) {
            value = settings.getBoolean(valueKey);
        } else if (Integer.class.equals(clazz)) {
            value = settings.getInt(valueKey);
        } else if (Double.class.equals(clazz)) {
            value = settings.getDouble(valueKey);
        } else if (String.class.equals(clazz)) {
            value = settings.getString(valueKey);
        } else if (String[].class.equals(clazz)) {
            value = settings.getStringArray(valueKey);
        } else if (Date.class.equals(clazz)) {
            Date d = new Date();
            d.setTime(settings.getLong(valueKey));
            value = d;
        } else if (JSONDataTable.class.equals(clazz)) {
            value = JSONDataTable.loadFromNodeSettings(settings.getNodeSettings(valueKey));
        } else if (JSONSVGOptions.class.equals(clazz)) {
            value = JSONSVGOptions.loadFromNodeSettings(settings.getNodeSettings(valueKey));
        } else if (JSONDateTimeOptions.class.equals(clazz)) {
            value = JSONDateTimeOptions.loadFromNodeSettings(settings.getNodeSettings(valueKey));
        } else if (List.class.isAssignableFrom(clazz)) {
            value = loadList(settings.getNodeSettings(valueKey));
	    } else if (Map.class.isAssignableFrom(clazz)) {
            value = loadMap(settings.getNodeSettings(valueKey));
        } else {
            throw new InvalidSettingsException("Unsupported map type: " + clazz);
        }
        return value;
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
        DynamicJSViewRepresentation other = (DynamicJSViewRepresentation)obj;
        return new EqualsBuilder()
                .append(m_jsNamespace, other.m_jsNamespace)
                .append(m_jsCode, other.m_jsCode)
                .append(m_cssCode, other.m_cssCode)
                .append(m_jsDependencies, other.m_jsDependencies)
                .append(m_cssDependencies, other.m_cssDependencies)
                .append(m_tableIds, other.m_tableIds)
                .append(m_inObjects, other.m_inObjects)
                .append(m_flowVariables, other.m_flowVariables)
                //TODO: deal with string arrays, all other types are fine
                .append(m_options, other.m_options)
                .append(m_binaryFiles, other.m_binaryFiles)
                .append(m_warnMessage, other.m_warnMessage)
                .append(m_errorMessage, other.m_errorMessage)
                .append(m_new, other.m_new)
                .append(m_runningInView, other.m_runningInView)
                .isEquals();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(m_jsNamespace)
                .append(m_jsCode)
                .append(m_cssCode)
                .append(m_jsDependencies)
                .append(m_cssDependencies)
                .append(m_tableIds)
                .append(m_inObjects)
                .append(m_flowVariables)
                //TODO: deal with string arrays, all other types are fine
                .append(m_options)
                .append(m_binaryFiles)
                .append(m_warnMessage)
                .append(m_errorMessage)
                .append(m_new)
                .append(m_runningInView)
                .toHashCode();
    }

}
