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
 *   24.04.2015 (Christian Albrecht, KNIME.com AG, Zurich, Switzerland): created
 */
package org.knime.dynamic.js;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.js.core.JSONDataTable;
import org.knime.js.core.JSONViewContent;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 *
 * @author Christian Albrecht, KNIME.com AG, Zurich, Switzerland, University of Konstanz
 */
@JsonAutoDetect
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public class DynamicJSViewRepresentation extends JSONViewContent {

	static final String NUM_SETTINGS = "numSettings";
	private static final String JS_NAMESPACE = "jsNamespace";
	private static final String JS_CODE = "jsCode";
	private static final String CSS_CODE = "cssCode";
	private static final String JS_DEPENDENCIES = "jsDependencies";
    private static final String CSS_DEPENDENCIES = "cssDependencies";
    private static final String URL_DEPENDENCIES = "urlDependencies";
    private static final String BINARY_FILES = "binaryFiles";
    static final String TABLES = "tables";
    static final String FLOW_VARIABLES = "variables";
    static final String OPTIONS = "options";

    private String m_jsNamespace = new String();
    private String[] m_jsCode = new String[0];
    private String[] m_cssCode = new String[0];
    private String[] m_jsDependencies = new String[0];
    private String[] m_cssDependencies = new String[0];
    private String[] m_urlDependencies = new String[0];
    private JSONDataTable[] m_tables = new JSONDataTable[0];
    private Map<String, String> m_flowVariables = new HashMap<String, String>();
    private Map<String, Object> m_options = new HashMap<String, Object>();
    private Map<String, String> m_binaryFiles = new HashMap<String, String>();

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
    public String[] getJsDependencies() {
		return m_jsDependencies;
	}

    @JsonProperty("jsDependencies")
    public void setJsDependencies(final String[] jsDependencies) {
		m_jsDependencies = jsDependencies;
	}

    @JsonProperty("urlDependencies")
    public String[] getUrlDependencies() {
		return m_urlDependencies;
	}

    @JsonProperty("urlDependencies")
    public void setUrlDependencies(final String[] urlDependencies) {
		m_urlDependencies = urlDependencies;
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
		return m_cssDependencies;
	}

    @JsonProperty("cssDependencies")
    public void setCssDependencies(final String[] cssDependencies) {
		m_cssDependencies = cssDependencies;
	}

    @JsonProperty("dataTables")
    public JSONDataTable[] getTables() {
		return m_tables;
	}

    @JsonProperty("dataTables")
    public void setTables(final JSONDataTable[] tables) {
		m_tables = tables;
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

	@Override
	public void saveToNodeSettings(final NodeSettingsWO settings) {
		settings.addString(JS_NAMESPACE, m_jsNamespace);
		settings.addStringArray(JS_CODE, m_jsCode);
		settings.addStringArray(CSS_CODE, m_cssCode);
		settings.addStringArray(JS_DEPENDENCIES, m_jsDependencies);
        settings.addStringArray(CSS_DEPENDENCIES, m_cssDependencies);
        settings.addStringArray(URL_DEPENDENCIES, m_urlDependencies);
        saveMap(settings.addNodeSettings(FLOW_VARIABLES), m_flowVariables, false);
        saveMap(settings.addNodeSettings(BINARY_FILES), m_binaryFiles, false);
        saveMap(settings.addNodeSettings(OPTIONS), m_options, true);
        NodeSettingsWO tables = settings.addNodeSettings(TABLES);
        tables.addInt(NUM_SETTINGS, m_tables.length);
        for (int i = 0; i < m_tables.length; i++) {
        	m_tables[i].saveJSONToNodeSettings(tables.addNodeSettings("table_" + i));
        }
	}

	@SuppressWarnings("unchecked")
    static void saveMap(final NodeSettingsWO settings, final Map<String, ?> map, final boolean objectMap) {
		settings.addInt(NUM_SETTINGS, map.size());
		String mapClass = objectMap ? "object" : "string";
		settings.addString("mapClass", mapClass);
		int i = 0;
		for (Entry<String, ?> entry : map.entrySet()) {
			settings.addString("key_" + i, entry.getKey());
			Object value = entry.getValue();
			String valueKey = "value_" + i;
			if (value == null) {
			    settings.addString("class_" + i, String.class.getName());
			    settings.addString(valueKey, null);
			    continue;
			}
			settings.addString("class_" + i, value.getClass().getName());
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
			} else if (value instanceof Map<?,?>) {
			    saveMap(settings.addNodeSettings(valueKey), (Map<String, ?>)value, true);
			} else {
				settings.addString(valueKey, value.toString());
			}
			i++;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void loadFromNodeSettings(final NodeSettingsRO settings)
			throws InvalidSettingsException {
		m_jsDependencies = settings.getStringArray(JS_DEPENDENCIES);
		m_jsCode = settings.getStringArray(JS_CODE);
		m_cssCode = settings.getStringArray(CSS_CODE);
		m_jsNamespace = settings.getString(JS_NAMESPACE);
        m_cssDependencies = settings.getStringArray(CSS_DEPENDENCIES);
        m_urlDependencies = settings.getStringArray(URL_DEPENDENCIES);
        m_flowVariables = (Map<String, String>) loadMap(settings.getNodeSettings(FLOW_VARIABLES));
        m_binaryFiles = (Map<String, String>) loadMap(settings.getNodeSettings(BINARY_FILES));
        m_options = (Map<String, Object>) loadMap(settings.getNodeSettings(OPTIONS));
        NodeSettingsRO tables = settings.getNodeSettings(TABLES);
        int numSettings = tables.getInt(NUM_SETTINGS);
        m_tables = new JSONDataTable[numSettings];
        for (int i = 0; i < numSettings; i++) {
			m_tables[i] = JSONDataTable.loadFromNodeSettings(tables
					.getNodeSettings("table_" + i));
        }
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
			String clazz = settings.getString("class_" + i);
			Object value;
			String valueKey = "value_" + i;
			if (Boolean.class.getName().equals(clazz)) {
				value = settings.getBoolean(valueKey);
			} else if (Integer.class.getName().equals(clazz)) {
				value = settings.getInt(valueKey);
			} else if (Double.class.getName().equals(clazz)) {
				value = settings.getDouble(valueKey);
			} else if (String.class.getName().equals(clazz)) {
				value = settings.getString(valueKey);
			} else if (String[].class.getName().equals(clazz)) {
			    value = settings.getStringArray(valueKey);
			} else if (Date.class.getName().equals(clazz)) {
			    Date d = new Date();
			    d.setTime(settings.getLong(valueKey));
			    value = d;
			} else if (JSONDataTable.class.getName().equals(clazz)) {
			    value = JSONDataTable.loadFromNodeSettings(settings.getNodeSettings(valueKey));
			} else if (Map.class.getName().equals(clazz)) {
			    value = loadMap(settings.getNodeSettings(valueKey));
			} else {
				throw new InvalidSettingsException("Unsupported map type: " + clazz);
			}
			map.put(key, value);
		}
		return map;
	}

}
