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
 *   Sep 18, 2020 (Christian Albrecht, KNIME GmbH, Konstanz, Germany): created
 */
package org.knime.js.base.node.widget.filter.definition.rangeslider;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.js.core.JSONViewContent;
import org.knime.js.core.settings.slider.SliderSettings;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Representation for the range slider filter node.
 *
 * @author Christian Albrecht, KNIME GmbH, Konstanz, Germany
 */
@JsonAutoDetect
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public class RangeSliderFilterWidgetRepresentation extends JSONViewContent {

    private SliderSettings m_sliderSettings;
    private String m_columnName;
    private String m_label;

    private static final String CFG_TABLE_ID = "tableID";
    private String m_tableId;

    private static final String CFG_FILTER_ID = "filterID";
    private String m_filterId;

    private static final String CFG_DISABLED = "disabled";
    private boolean m_disabled;

    /**
     * @param config The configuration of the node
     */
    public RangeSliderFilterWidgetRepresentation(final RangeSliderFilterWidgetConfig config) {
        setConfig(config);
    }

    /**
     * Sets the config settings on this representation object
     * @param config the config to set
     */
    @JsonIgnore
    public void setConfig(final RangeSliderFilterWidgetConfig config) {
        m_sliderSettings = config.getSliderSettings();
        m_columnName = config.getDomainColumn().getStringValue();
        if (config.getUseLabel()) {
            m_label = config.getLabel();
        } else {
            m_label = null;
        }
    }

    /**
     * @return the sliderSettings
     */
    @JsonProperty("sliderSettings")
    public SliderSettings getSliderSettings() {
        return m_sliderSettings;
    }

    /**
     * @return the columnName
     */
    public String getColumnName() {
        return m_columnName;
    }

    /**
     * @return the label
     */
    public String getLabel() {
        return m_label;
    }

    /**
     * @param label the label to set
     */
    public void setLabel(final String label) {
        m_label = label;
    }

    /**
     * @return the tableId
     */
    public String getTableId() {
        return m_tableId;
    }

    /**
     * @param tableId the tableId to set
     */
    public void setTableId(final String tableId) {
        m_tableId = tableId;
    }

    /**
     * @return the filterId
     */
    public String getFilterId() {
        return m_filterId;
    }

    /**
     * @param filterId the filterId to set
     */
    public void setFilterId(final String filterId) {
        m_filterId = filterId;
    }

    /**
     * @return the disabled
     */
    public boolean getDisabled() {
        return m_disabled;
    }

    /**
     * @param disabled the disabled to set
     */
    public void setDisabled(final boolean disabled) {
        m_disabled = disabled;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (m_label != null) {
            sb.append("label=");
            sb.append(m_label);
            sb.append(", ");
        }
        sb.append("column=");
        sb.append(m_columnName);
        if (m_filterId != null) {
            sb.append(", id=");
            sb.append(m_filterId);
        }
        sb.append(", sliderSettings=");
        sb.append(m_sliderSettings);
        if (m_disabled) {
            sb.append(", disabled");
        }
        return sb.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(m_tableId)
                .append(m_filterId)
                .append(m_columnName)
                .append(m_label)
                .append(m_sliderSettings)
                .append(m_disabled)
                .toHashCode();
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
        RangeSliderFilterWidgetRepresentation other = (RangeSliderFilterWidgetRepresentation)obj;
        return new EqualsBuilder()
                .append(m_tableId, other.m_tableId)
                .append(m_filterId, other.m_filterId)
                .append(m_columnName, other.m_columnName)
                .append(m_label, other.m_label)
                .append(m_sliderSettings, other.m_sliderSettings)
                .append(m_disabled, other.m_disabled)
                .isEquals();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveToNodeSettings(final NodeSettingsWO settings) {
        settings.addString(CFG_TABLE_ID, m_tableId);
        settings.addString(CFG_FILTER_ID, m_filterId);
        settings.addBoolean(CFG_DISABLED, m_disabled);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadFromNodeSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
        m_tableId = settings.getString(CFG_TABLE_ID);
        m_filterId = settings.getString(CFG_FILTER_ID);
        m_disabled = settings.getBoolean(CFG_DISABLED);
    }

}
