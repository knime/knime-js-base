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
 *   May 28, 2019 (Daniel Bogenrieder): created
 */
package org.knime.js.base.node.widget.input.slider;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.knime.js.base.node.base.LabeledConfig;
import org.knime.js.base.node.base.input.slider.SliderNodeConfig;
import org.knime.js.base.node.base.input.slider.SliderNodeRepresentation;
import org.knime.js.base.node.base.input.slider.SliderNodeValue;
import org.knime.js.core.settings.slider.SliderSettings;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * The base representation for the slider configuration and widget node
 *
 * @author Daniel Bogenrieder, KNIME GmbH, Konstanz, Germany
 *
 */
@JsonAutoDetect
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public class SliderWidgetNodeRepresentation extends SliderNodeRepresentation<SliderNodeValue> {

    private final SliderSettings m_sliderSettings;
    private double m_customMinValue;
    private double m_customMaxValue;


    /**
     * @param currentValue
     * @param defaultValue
     * @param config
     * @param labelConfig
     * @param sliderSettings the settings of the noUI-slider
     * @param customMinValue a custom minimum of the slider
     * @param customMaxValue a custom maximum of the slider
     */
    @JsonCreator
    public SliderWidgetNodeRepresentation(final SliderNodeValue currentValue, final SliderNodeValue defaultValue,
        final SliderNodeConfig config, final LabeledConfig labelConfig,
        @JsonProperty("sliderSettings") final SliderSettings sliderSettings,
        @JsonProperty("customMinValue") final double customMinValue,
        @JsonProperty("customMaxValue") final double customMaxValue) {
        super(currentValue, defaultValue, config, labelConfig);
        m_sliderSettings = sliderSettings;
        m_customMinValue = customMinValue;
        m_customMaxValue = customMaxValue;
    }

    /**
     * @param currentValue
     * @param config
     */
    public SliderWidgetNodeRepresentation(final SliderNodeValue currentValue, final SliderInputWidgetConfig config) {
        super(currentValue, config.getDefaultValue(),config.getSliderConfig(), config.getLabelConfig());
        m_sliderSettings = config.getSliderSettings();
        m_customMinValue = config.getCustomMinValue();
        m_customMaxValue = config.getCustomMaxValue();
    }

    /**
     * @return the sliderSettings
     */
    @JsonProperty("sliderSettings")
    public SliderSettings getSliderSettings() {
        return m_sliderSettings;
    }

    /**
     * @return the customMin
     */
    @JsonProperty("customMinValue")
    public double getCustomMinValue() {
        return m_customMinValue;
    }

    /**
     * @return the customMax
     */
    @JsonProperty("customMaxValue")
    public double getCustomMaxValue() {
        return m_customMaxValue;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @JsonIgnore
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString());
        sb.append(", ");
        sb.append("customMinValue=");
        sb.append(m_customMinValue);
        sb.append(", ");
        sb.append("customMaxValue=");
        sb.append(m_customMaxValue);
        return sb.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @JsonIgnore
    public int hashCode() {
        return new HashCodeBuilder()
                .appendSuper(super.hashCode())
                .append(m_customMinValue)
                .append(m_customMaxValue)
                .toHashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @JsonIgnore
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
        @SuppressWarnings("unchecked")
        SliderWidgetNodeRepresentation other = (SliderWidgetNodeRepresentation)obj;
        return new EqualsBuilder().appendSuper(super.equals(obj))
                .appendSuper(super.equals(obj))
                .append(m_customMinValue, other.m_customMinValue)
                .append(m_customMaxValue, other.m_customMaxValue)
                .isEquals();
    }
}
