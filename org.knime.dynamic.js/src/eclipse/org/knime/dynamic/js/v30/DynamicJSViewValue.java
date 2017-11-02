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
 *   24.04.2015 (Christian Albrecht, KNIME AG, Zurich, Switzerland): created
 */
package org.knime.dynamic.js.v30;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
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
 * @author Christian Albrecht, KNIME AG, Zurich, Switzerland
 * @since 3.0
 */
@JsonAutoDetect
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public class DynamicJSViewValue extends JSONViewContent {

	private static final String OUT_COLUMNS = "outColumns";
	private static final String OUT_TABLES = "outTables";

    private Map<String, Object> m_options = new HashMap<String, Object>();
	private Map<String, Map<String, Object>> m_outColumns = new HashMap<String, Map<String,Object>>();
	private Map<String, JSONDataTable> m_outTables = new HashMap<String, JSONDataTable>();
	private Map<String, String> m_flowVariables = new HashMap<String, String>();

	/**
     * @return the options
     */
    @JsonProperty("options")
    public Map<String, Object> getOptions() {
		return m_options;
	}

    /**
     * @param options the options to set
     */
    @JsonProperty("options")
    public void setOptions(final Map<String, Object> options) {
		m_options = options;
	}

    /**
     * @return the outColumns
     */
    @JsonProperty("outColumns")
    public Map<String, Map<String, Object>> getOutColumns() {
        return m_outColumns;
    }

    /**
     * @param outColumns the outColumns to set
     */
    @JsonProperty("outColumns")
    public void setOutColumns(final Map<String, Map<String, Object>> outColumns) {
        m_outColumns = outColumns;
    }

    /**
     * @return the tables
     */
    @JsonProperty("tables")
    public Map<String, JSONDataTable> getTables() {
        return m_outTables;
    }

    /**
     * @param tables the tables to set
     */
    @JsonProperty("tables")
    public void setTables(final Map<String, JSONDataTable> tables) {
        m_outTables = tables;
    }

    /**
     * @return the flowVariables
     */
    @JsonProperty("flowVariables")
    public Map<String, String> getFlowVariables() {
        return m_flowVariables;
    }

    /**
     * @param flowVariables the flowVariables to set
     */
    @JsonProperty("flowVariables")
    public void setFlowVariables(final Map<String, String> flowVariables) {
        m_flowVariables = flowVariables;
    }

	@Override
	public void saveToNodeSettings(final NodeSettingsWO settings) {
	    DynamicJSViewRepresentation.saveMap(settings.addNodeSettings(DynamicJSViewRepresentation.OPTIONS), m_options, true);
	    DynamicJSViewRepresentation.saveMap(settings.addNodeSettings(OUT_COLUMNS), m_outColumns, true);
	    DynamicJSViewRepresentation.saveMap(settings.addNodeSettings(OUT_TABLES), m_outTables, true);
	    DynamicJSViewRepresentation.saveMap(settings.addNodeSettings(DynamicJSViewRepresentation.FLOW_VARIABLES), m_flowVariables, false);
	}

	@SuppressWarnings("unchecked")
    @Override
	public void loadFromNodeSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
	    m_options = (Map<String, Object>) DynamicJSViewRepresentation.loadMap(settings.getNodeSettings(DynamicJSViewRepresentation.OPTIONS));
	    m_outColumns = (Map<String, Map<String, Object>>) DynamicJSViewRepresentation.loadMap(settings.getNodeSettings(OUT_COLUMNS));
	    m_outTables = (Map<String, JSONDataTable>) DynamicJSViewRepresentation.loadMap(settings.getNodeSettings(OUT_TABLES));
	    m_flowVariables = (Map<String, String>) DynamicJSViewRepresentation.loadMap(settings.getNodeSettings(DynamicJSViewRepresentation.FLOW_VARIABLES));
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
        DynamicJSViewValue other = (DynamicJSViewValue)obj;
        return new EqualsBuilder()
                //TODO: deal with string arrays, all other types are fine
                .append(m_options, other.m_options)
                .append(m_outColumns, other.m_outColumns)
                .append(m_outTables, other.m_outTables)
                .append(m_flowVariables, other.m_flowVariables)
                .isEquals();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(m_options)
                .append(m_outColumns)
                .append(m_outTables)
                .append(m_flowVariables)
                .toHashCode();
    }

}
