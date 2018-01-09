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
 */
package org.knime.js.base.node.quickform.input.date2;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.knime.core.node.dialog.DialogNodePanel;
import org.knime.js.base.node.quickform.QuickFormRepresentationImpl;
import org.knime.time.util.DateTimeType;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * The representation for the date input quick form node.
 *
 * @author Patrick Winter, KNIME AG, Zurich, Switzerland
 * @author Simon Schmid, KNIME.com, Konstanz, Germany
 */
@JsonAutoDetect
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public class DateInput2QuickFormRepresentation
    extends QuickFormRepresentationImpl<DateInput2QuickFormValue, DateTimeInputQuickFormConfig> {

    /**
     * @param currentValue The value currently used by the node
     * @param config The config of the node
     */
    public DateInput2QuickFormRepresentation(final DateInput2QuickFormValue currentValue,
        final DateTimeInputQuickFormConfig config) {
        super(currentValue, config);
        m_showNowButton = config.getShowNowButton();
        m_granularity = config.getGranularity();
        m_useMin = config.getUseMin();
        m_useMax = config.getUseMax();
        m_useMinExecTime = config.getUseMinExecTime();
        m_useMaxExecTime = config.getUseMaxExecTime();
        m_useDefaultExecTime = config.getUseDefaultExecTime();
        m_min = config.getMin();
        m_max = config.getMax();
        m_type = config.getType();
    }

    private final boolean m_showNowButton;

    private final GranularityTime m_granularity;

    private final boolean m_useMin;

    private final boolean m_useMax;

    private final boolean m_useMinExecTime;

    private final boolean m_useMaxExecTime;

    private final boolean m_useDefaultExecTime;

    private final ZonedDateTime m_min;

    private final ZonedDateTime m_max;

    private final DateTimeType m_type;

    private final Set<String> m_zones = new TreeSet<String>(ZoneId.getAvailableZoneIds());

    /**
     * {@inheritDoc}
     */
    @Override
    @JsonIgnore
    public DialogNodePanel<DateInput2QuickFormValue> createDialogPanel() {
        DateInput2QuickFormDialogPanel panel = new DateInput2QuickFormDialogPanel(this);
        fillDialogPanel(panel);
        return panel;
    }

    /**
     * @return the showNowButton
     */
    @JsonProperty("shownowbutton")
    public boolean getShowNowButton() {
        return m_showNowButton;
    }

    /**
     * @return the showNowButton
     */
    @JsonProperty("granularity")
    public String getGranularity() {
        if (m_granularity == GranularityTime.SHOW_MINUTES) {
            return "show_minutes";
        } else if (m_granularity == GranularityTime.SHOW_SECONDS) {
            return "show_seconds";
        } else {
            return "show_millis";
        }
    }

    /**
     * @return the useMin
     */
    @JsonProperty("usemin")
    public boolean getUseMin() {
        return m_useMin;
    }

    /**
     * @return the useMax
     */
    @JsonProperty("usemax")
    public boolean getUseMax() {
        return m_useMax;
    }

    /**
     * @return the useMinExecTime
     */
    @JsonProperty("useminexectime")
    public boolean getUseMinExecTime() {
        return m_useMinExecTime;
    }

    /**
     * @return the useMaxExecTime
     */
    @JsonProperty("usemaxexectime")
    public boolean getUseMaxExecTime() {
        return m_useMaxExecTime;
    }

    /**
     * @return the useMaxExecTime
     */
    @JsonProperty("usedefaultexectime")
    public boolean getUseDefaultExecTime() {
        return m_useDefaultExecTime;
    }

    /**
     * @return the min
     */
    @JsonIgnore
    public ZonedDateTime getMin() {
        return m_min;
    }

    /**
     * @return the min
     */
    @JsonProperty("min")
    public String getMinAsString() {
        return m_min.toString();
    }

    /**
     * @return the max
     */
    @JsonIgnore
    public ZonedDateTime getMax() {
        return m_max;
    }

    /**
     * @return the max
     */
    @JsonProperty("max")
    public String getMaxAsString() {
        return m_max.toString();
    }

    /**
     * @return the type
     */
    @JsonIgnore
    public DateTimeType getType() {
        return m_type;
    }

    /**
     * @return the type
     */
    @JsonProperty("type")
    public String getTypeDescription() {
        if (m_type == DateTimeType.LOCAL_DATE) {
            return "LD";
        } else if (m_type == DateTimeType.LOCAL_TIME) {
            return "LT";
        } else if (m_type == DateTimeType.LOCAL_DATE_TIME) {
            return "LDT";
        } else {
            return "ZDT";
        }
    }

    /**
     * @return the zones
     */
    @JsonProperty("zones")
    public Set<String> getZones() {
        return m_zones;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString());
        sb.append(", ");
        sb.append("showNowButton=");
        sb.append(m_showNowButton);
        sb.append(", ");
        sb.append("granularity=");
        sb.append(m_granularity);
        sb.append(", ");
        sb.append("useMin=");
        sb.append(m_useMin);
        sb.append(", ");
        sb.append("useMax=");
        sb.append(m_useMax);
        sb.append(", ");
        sb.append("useMinExecTime=");
        sb.append(m_useMinExecTime);
        sb.append(", ");
        sb.append("useMaxExecTime=");
        sb.append(m_useMaxExecTime);
        sb.append(", ");
        sb.append("useDefaultExecTime=");
        sb.append(m_useDefaultExecTime);
        sb.append(", ");
        sb.append("min=");
        sb.append("{");
        sb.append(m_min);
        sb.append("}");
        sb.append(", ");
        sb.append("max=");
        sb.append("{");
        sb.append(m_max);
        sb.append("}");
        sb.append(", ");
        sb.append("withTime=");
        sb.append(m_type);
        return sb.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder().appendSuper(super.hashCode()).append(m_showNowButton).append(m_granularity)
            .append(m_useMin).append(m_useMax).append(m_useMinExecTime).append(m_useMaxExecTime)
            .append(m_useDefaultExecTime).append(m_min).append(m_max).append(m_type).toHashCode();
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
        DateInput2QuickFormRepresentation other = (DateInput2QuickFormRepresentation)obj;
        return new EqualsBuilder().appendSuper(super.equals(obj)).append(m_showNowButton, other.m_showNowButton)
            .append(m_granularity, other.m_granularity).append(m_useMin, other.m_useMin)
            .append(m_useMax, other.m_useMax).append(m_useMinExecTime, other.m_useMinExecTime)
            .append(m_useMaxExecTime, other.m_useMaxExecTime).append(m_useDefaultExecTime, other.m_useDefaultExecTime)
            .append(m_min, other.m_min).append(m_max, other.m_max).append(m_type, other.m_type).isEquals();
    }

}
