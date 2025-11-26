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
 *   Sep 18, 2020 (Christian Albrecht, KNIME GmbH, Konstanz, Germany): created
 */
package org.knime.js.base.node.widget.filter.definition.value;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.js.base.node.parameters.filterandselection.LimitVisibleOptionsParameters;
import org.knime.js.base.node.widget.filter.definition.RangeFilterWidgetNodeParameters;
import org.knime.js.core.JSONViewContent;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * View representation for the value filter definition node
 *
 * @author Christian Albrecht, KNIME GmbH, Konstanz, Germany
 */
@JsonAutoDetect
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public class ValueFilterDefinitionWidgetRepresentation extends JSONViewContent {

    private static final String CFG_TABLE_ID = "tableID";
    private String m_tableID;

    private static final String CFG_FILTER_ID = "filterID";
    private String m_filterID;

    private static final String CFG_DISABLED = "disabled";
    private boolean m_disabled = false;

    private static final String CFG_POSSIBLE_VALUES = "possibleValues";
    private String[] m_possibleValues;

    private String m_column;
    private String m_label;
    private boolean m_multipleValues;
    private String m_type;
    private boolean m_limitNumberVisOptions;
    private Integer m_numberVisOptions;


    /**
     * @return the tableID
     */
    public String getTableID() {
        return m_tableID;
    }

    /**
     * @param tableID the tableID to set
     */
    public void setTableID(final String tableID) {
        m_tableID = tableID;
    }

    /**
     * @return the filterID
     */
    public String getFilterID() {
        return m_filterID;
    }

    /**
     * @param filterID the filterID to set
     */
    public void setFilterID(final String filterID) {
        m_filterID = filterID;
    }

    /**
     * @return the disabled
     */
    public boolean isDisabled() {
        return m_disabled;
    }

    /**
     * @param disabled the disabled to set
     */
    public void setDisabled(final boolean disabled) {
        m_disabled = disabled;
    }

    /**
     * @return the possibleValues
     */
    public String[] getPossibleValues() {
        return m_possibleValues;
    }

    /**
     * @param possibleValues the possibleValues to set
     */
    public void setPossibleValues(final String[] possibleValues) {
        m_possibleValues = possibleValues;
    }

    /**
     * @param config the config to set
     */
    @JsonIgnore
    public void setConfig(final ValueFilterDefinitionWidgetConfig config) {
        m_column = config.getColumn();
        m_label = null;
        if (config.isUseLabel()) {
            m_label = config.isCustomLabel() ? config.getLabel() : m_column;
        }
        m_multipleValues = config.isUseMultiple();
        m_type = config.getType();
        m_limitNumberVisOptions = config.isLimitNumberVisOptions();
        m_numberVisOptions = config.getNumberVisOptions();
    }

    /**
     * @return the column
     */
    public String getColumn() {
        return m_column;
    }

    /**
     * @return the label
     */
    public String getLabel() {
        return m_label;
    }

    /**
     * @return the multipleValues
     */
    public boolean isMultipleValues() {
        return m_multipleValues;
    }

    /**
     * @return the type
     */
    public String getType() {
        return m_type;
    }

    /**
     * @return the limitNumberVisOptions
     */
    public boolean isLimitNumberVisOptions() {
        return m_limitNumberVisOptions;
    }

    /**
     * @return the numberVisOptions
     */
    public Integer getNumberVisOptions() {
        return m_numberVisOptions;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveToNodeSettings(final NodeSettingsWO settings) {
        settings.addString(CFG_TABLE_ID, m_tableID);
        settings.addString(CFG_FILTER_ID, m_filterID);
        settings.addBoolean(CFG_DISABLED, m_disabled);
        settings.addStringArray(CFG_POSSIBLE_VALUES, m_possibleValues);
        settings.addString(ValueFilterDefinitionWidgetConfig.CFG_COLUMN, m_column);
        settings.addString(RangeFilterWidgetNodeParameters.CFG_LABEL, m_label);
        settings.addBoolean(ValueFilterDefinitionWidgetConfig.CFG_USE_MULTIPLE, m_multipleValues);
        settings.addString(ValueFilterDefinitionWidgetConfig.CFG_TYPE, m_type);
        settings.addBoolean(LimitVisibleOptionsParameters.CFG_LIMIT_NUMBER_VIS_OPTIONS, m_limitNumberVisOptions);
        settings.addInt(LimitVisibleOptionsParameters.CFG_NUMBER_VIS_OPTIONS, m_numberVisOptions);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadFromNodeSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
        m_tableID = settings.getString(CFG_TABLE_ID);
        m_filterID = settings.getString(CFG_FILTER_ID);
        m_disabled = settings.getBoolean(CFG_DISABLED);
        m_possibleValues = settings.getStringArray(CFG_POSSIBLE_VALUES);
        m_column = settings.getString(ValueFilterDefinitionWidgetConfig.CFG_COLUMN);
        m_label = settings.getString(RangeFilterWidgetNodeParameters.CFG_LABEL);
        m_multipleValues = settings.getBoolean(ValueFilterDefinitionWidgetConfig.CFG_USE_MULTIPLE);
        m_type = settings.getString(ValueFilterDefinitionWidgetConfig.CFG_TYPE);
        m_limitNumberVisOptions = settings.getBoolean(LimitVisibleOptionsParameters.CFG_LIMIT_NUMBER_VIS_OPTIONS);
        m_numberVisOptions = settings.getInt(LimitVisibleOptionsParameters.CFG_NUMBER_VIS_OPTIONS);
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
        ValueFilterDefinitionWidgetRepresentation other = (ValueFilterDefinitionWidgetRepresentation)obj;
        return new EqualsBuilder()
                .append(m_tableID, m_tableID)
                .append(m_filterID, other.m_filterID)
                .append(m_disabled, other.m_disabled)
                .append(m_column, other.m_column)
                .append(m_label, other.m_label)
                .append(m_possibleValues, other.m_possibleValues)
                .append(m_multipleValues, other.m_multipleValues)
                .append(m_type, other.m_type)
                .append(m_limitNumberVisOptions, other.m_limitNumberVisOptions)
                .append(m_numberVisOptions, other.m_numberVisOptions)
                .isEquals();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(m_tableID)
                .append(m_filterID)
                .append(m_disabled)
                .append(m_column)
                .append(m_label)
                .append(m_possibleValues)
                .append(m_multipleValues)
                .append(m_type)
                .append(m_limitNumberVisOptions)
                .append(m_numberVisOptions)
                .toHashCode();
    }

}
