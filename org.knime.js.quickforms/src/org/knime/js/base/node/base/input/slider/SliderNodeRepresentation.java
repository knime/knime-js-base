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
 *   May 27, 2019 (daniel): created
 */
package org.knime.js.base.node.base.input.slider;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.knime.js.base.node.base.LabeledConfig;
import org.knime.js.base.node.base.LabeledNodeRepresentation;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * The base representation for the slider configuration and widget node
 *
 * @author Daniel Bogenrieder, KNIME GmbH, Konstanz, Germany
 * @param <VAL> the value implementation of the node
 */
@JsonAutoDetect
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public class SliderNodeRepresentation<VAL extends SliderNodeValue> extends LabeledNodeRepresentation<VAL> {

    private final boolean m_useCustomMin;
    private final boolean m_useCustomMax;
    private final double m_customMin;
    private final double m_customMax;

    @JsonCreator
    protected SliderNodeRepresentation(@JsonProperty("label") final String label,
        @JsonProperty("description") final String description, @JsonProperty("required") final boolean required,
        @JsonProperty("defaultValue") final VAL defaultValue,
        @JsonProperty("currentValue") final VAL currentValue,
        @JsonProperty("useCustomMin") final Boolean useCustomMin,
        @JsonProperty("useCustomMax") final Boolean useCustomMax,
        @JsonProperty("customMin") final double customMin,
        @JsonProperty("customMax") final double customMax) {
        super(label, description, required, defaultValue, currentValue);
        m_useCustomMin = useCustomMin;
        m_useCustomMax = useCustomMax;
        m_customMin = customMin;
        m_customMax = customMax;
    }

    /**
     * @param currentValue The value currently used by the node
     * @param defaultValue The default value of the node
     * @param config The config of the node
     * @param labelConfig The label config of the node
     */
    public SliderNodeRepresentation(final VAL currentValue, final VAL defaultValue,final SliderNodeConfig config,
        final LabeledConfig labelConfig) {
        super(currentValue, defaultValue, labelConfig);
        m_useCustomMin = config.isUseCustomMin();
        m_useCustomMax = config.isUseCustomMax();
        m_customMin = config.getCustomMin();
        m_customMax = config.getCustomMax();
    }

    /**
     * @return the useCustomMin
     */
    @JsonProperty("useCustomMin")
    public Boolean isUseCustomMin() {
        return m_useCustomMin;
    }

    /**
     * @return the useCustomMax
     */
    @JsonProperty("useCustomMax")
    public Boolean isUseCustomMax() {
        return m_useCustomMax;
    }

    /**
     * @return the customMin
     */
    @JsonProperty("customMin")
    public double getCustomMin() {
        return m_customMin;
    }

    /**
     * @return the customMax
     */
    @JsonProperty("customMax")
    public double getCustomMax() {
        return m_customMax;
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
        sb.append("useCustomMin=");
        sb.append(m_useCustomMin);
        sb.append(", ");
        sb.append("useCustomMax=");
        sb.append(m_useCustomMax);
        sb.append(", ");
        sb.append("customMin=");
        sb.append(m_customMin);
        sb.append(", ");
        sb.append("customMax=");
        sb.append(m_customMax);
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
                .append(m_useCustomMin)
                .append(m_useCustomMax)
                .append(m_customMin)
                .append(m_customMax)
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
        SliderNodeRepresentation<VAL> other = (SliderNodeRepresentation<VAL>)obj;
        return new EqualsBuilder().appendSuper(super.equals(obj))
                .appendSuper(super.equals(obj))
                .append(m_useCustomMin, other.m_useCustomMin)
                .append(m_useCustomMax, other.m_useCustomMax)
                .append(m_customMin, other.m_customMin)
                .append(m_customMax, other.m_customMax)
                .isEquals();
    }
}
