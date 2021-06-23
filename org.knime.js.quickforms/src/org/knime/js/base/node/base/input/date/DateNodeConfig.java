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
 *   23 May 2019 (albrecht): created
 */
package org.knime.js.base.node.base.input.date;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.time.util.DateTimeType;
import org.knime.time.util.DateTimeUtils;

/**
 * Base config file for the date configuration and widget nodes
 *
 * @author Christian Albrecht, KNIME GmbH, Konstanz, Germany
 */
public class DateNodeConfig {

    /**
     * The default date for all date settings
     */
    public static final ZonedDateTime DEFAULT_ZDT = ZonedDateTime.now().withNano(0);

    private static final String CFG_SHOW_NOW_BUTTON = "show_now";
    private static final boolean DEFAULT_SHOW_NOW_BUTTON = true;
    private boolean m_showNowButton = DEFAULT_SHOW_NOW_BUTTON;

    private static final String CFG_USE_MIN = "use_min";
    private static final boolean DEFAULT_USE_MIN = false;
    private boolean m_useMin = DEFAULT_USE_MIN;

    private static final String CFG_USE_MAX = "use_max";
    private static final boolean DEFAULT_USE_MAX = false;
    private boolean m_useMax = DEFAULT_USE_MAX;

    private static final String CFG_USE_MIN_EXEC_TIME = "use_min_exec_time";
    private static final boolean DEFAULT_USE_MIN_EXEC_TIME = false;
    private boolean m_useMinExecTime = DEFAULT_USE_MIN_EXEC_TIME;

    private static final String CFG_USE_MAX_EXEC_TIME = "use_max_exec_time";
    private static final boolean DEFAULT_USE_MAX_EXEC_TIME = false;
    private boolean m_useMaxExecTime = DEFAULT_USE_MAX_EXEC_TIME;

    private static final String CFG_USE_DEFAULT_EXEC_TIME = "use_default_exec_time";
    private static final boolean DEFAULT_USE_DEFAULT_EXEC_TIME = false;
    private boolean m_useDefaultExecTime = DEFAULT_USE_DEFAULT_EXEC_TIME;

    private static final String CFG_MIN = "min";
    private static final ZonedDateTime DEFAULT_MIN = DEFAULT_ZDT;
    private ZonedDateTime m_min = DEFAULT_MIN;

    private static final String CFG_MAX = "max";
    private static final ZonedDateTime DEFAULT_MAX = DEFAULT_ZDT;
    private ZonedDateTime m_max = DEFAULT_MAX;

    private static final String CFG_TYPE = "date_time_type";
    private static final DateTimeType DEFAULT_TYPE = DateTimeType.LOCAL_DATE_TIME;
    private DateTimeType m_type = DEFAULT_TYPE;

    private static final String CFG_GRANULARITY = "granularity";
    private static final GranularityTime DEFAULT_GRANULARITY = GranularityTime.SHOW_MINUTES;
    private GranularityTime m_granularity = DEFAULT_GRANULARITY;

    /**
     * @return the showNow
     */
    public boolean isShowNowButton() {
        return m_showNowButton;
    }

    /**
     * @param showNow the showNow to set
     */
    public void setShowNowButton(final boolean showNow) {
        m_showNowButton = showNow;
    }

    /**
     * @return the granularity
     */
    public GranularityTime getGranularity() {
        return m_granularity;
    }

    /**
     * @param granularity the granularity to set
     */
    public void setGranularity(final GranularityTime granularity) {
        m_granularity = granularity;
    }

    /**
     * @return the useMin
     */
    public boolean isUseMin() {
        return m_useMin;
    }

    /**
     * @param useMin the useMin to set
     */
    public void setUseMin(final boolean useMin) {
        m_useMin = useMin;
    }

    /**
     * @return the useMax
     */
    public boolean isUseMax() {
        return m_useMax;
    }

    /**
     * @param useMax the useMax to set
     */
    public void setUseMax(final boolean useMax) {
        m_useMax = useMax;
    }

    /**
     * @return the useMinExecTime
     */
    public boolean isUseMinExecTime() {
        return m_useMinExecTime;
    }

    /**
     * @param useMinExecTime the useMinExecTime to set
     */
    public void setUseMinExecTime(final boolean useMinExecTime) {
        m_useMinExecTime = useMinExecTime;
    }

    /**
     * @return the useMaxExecTime
     */
    public boolean isUseMaxExecTime() {
        return m_useMaxExecTime;
    }

    /**
     * @param useMaxExecTime the useMaxExecTime to set
     */
    public void setUseMaxExecTime(final boolean useMaxExecTime) {
        m_useMaxExecTime = useMaxExecTime;
    }

    /**
     * @return the useDefaultExecTime
     */
    public boolean isUseDefaultExecTime() {
        return m_useDefaultExecTime;
    }

    /**
     * @param useDefaultExecTime the useDefaultExecTime to set
     */
    public void setUseDefaultExecTime(final boolean useDefaultExecTime) {
        m_useDefaultExecTime = useDefaultExecTime;
    }

    /**
     * @return the min
     */
    public ZonedDateTime getMin() {
        return m_min;
    }

    /**
     * @param min the min to set
     */
    public void setMin(final ZonedDateTime min) {
        m_min = min;
    }

    /**
     * @return the max
     */
    public ZonedDateTime getMax() {
        return m_max;
    }

    /**
     * @param max the max to set
     */
    public void setMax(final ZonedDateTime max) {
        m_max = max;
    }

    /**
     * @return the type
     */
    public DateTimeType getType() {
        return m_type;
    }

    /**
     * @param withTime the withTime to set
     */
    public void setType(final DateTimeType withTime) {
        m_type = withTime;
    }

    private ZonedDateTime parseZonedDateTime(final String string) throws InvalidSettingsException {
        if (DateTimeUtils.asZonedDateTime(string).isPresent()) {
            return DateTimeUtils.asZonedDateTime(string).get();
        } else if (DateTimeUtils.asLocalDateTime(string).isPresent()) {
            if (m_type == DateTimeType.ZONED_DATE_TIME) {
                throw new InvalidSettingsException("'" + string + "' could not be parsed as " + m_type + ".");
            }

            return ZonedDateTime.of(DateTimeUtils.asLocalDateTime(string).get(), ZoneId.systemDefault());
        } else if (DateTimeUtils.asLocalDate(string).isPresent()) {
            if (m_type != DateTimeType.LOCAL_DATE) {
                throw new InvalidSettingsException("'" + string + "' could not be parsed as " + m_type + ".");
            }

            return ZonedDateTime.of(DateTimeUtils.asLocalDate(string).get(), DateTimeUtils.nowLocalTimeMillis(),
                ZoneId.systemDefault());
        } else if (DateTimeUtils.asLocalTime(string).isPresent()) {
            if (m_type != DateTimeType.LOCAL_TIME) {
                throw new InvalidSettingsException("'" + string + "' could not be parsed as " + m_type + ".");
            }

            return ZonedDateTime.of(LocalDate.now(), DateTimeUtils.asLocalTime(string).get(), ZoneId.systemDefault());
        }

        throw new InvalidSettingsException(
            "'" + string + "' could not be parsed as a date, time, or date&time with zone.");
    }

    /**
     * Saves the current settings
     *
     * @param settings the settings to save to
     */
    public void saveSettings(final NodeSettingsWO settings) {
        settings.addBoolean(CFG_SHOW_NOW_BUTTON, m_showNowButton);
        settings.addString(CFG_GRANULARITY, m_granularity.name());
        settings.addBoolean(CFG_USE_MIN, m_useMin);
        settings.addBoolean(CFG_USE_MAX, m_useMax);
        settings.addBoolean(CFG_USE_MIN_EXEC_TIME, m_useMinExecTime);
        settings.addBoolean(CFG_USE_MAX_EXEC_TIME, m_useMaxExecTime);
        settings.addBoolean(CFG_USE_DEFAULT_EXEC_TIME, isUseDefaultExecTime());
        settings.addString(CFG_MIN, m_min.toString());
        settings.addString(CFG_MAX, m_max.toString());
        settings.addString(CFG_TYPE, m_type.name());
    }

    /**
     * Loads the config from saved settings
     *
     * @param settings the settings to load from
     * @throws InvalidSettingsException
     */
    public void loadSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
        m_showNowButton = settings.getBoolean(CFG_SHOW_NOW_BUTTON);
        m_granularity = GranularityTime.valueOf(settings.getString(CFG_GRANULARITY));
        m_useMin = settings.getBoolean(CFG_USE_MIN);
        m_useMax = settings.getBoolean(CFG_USE_MAX);
        m_useMinExecTime = settings.getBoolean(CFG_USE_MIN_EXEC_TIME);
        m_useMaxExecTime = settings.getBoolean(CFG_USE_MAX_EXEC_TIME);
        setUseDefaultExecTime(settings.getBoolean(CFG_USE_DEFAULT_EXEC_TIME));
        m_type = DateTimeType.valueOf(settings.getString(CFG_TYPE));
        try {
            m_min = parseZonedDateTime(settings.getString(CFG_MIN));
            m_max = parseZonedDateTime(settings.getString(CFG_MAX));
        } catch (DateTimeParseException e) {
            throw e;
        }
    }

    /**
     * Loads the config from saved settings for dialog display
     *
     * @param settings the settings to load from
     */
    public void loadSettingsInDialog(final NodeSettingsRO settings) {
        m_showNowButton = settings.getBoolean(CFG_SHOW_NOW_BUTTON, DEFAULT_SHOW_NOW_BUTTON);
        m_granularity = GranularityTime.valueOf(settings.getString(CFG_GRANULARITY, DEFAULT_GRANULARITY.name()));
        m_useMin = settings.getBoolean(CFG_USE_MIN, DEFAULT_USE_MIN);
        m_useMax = settings.getBoolean(CFG_USE_MAX, DEFAULT_USE_MAX);
        m_useMinExecTime = settings.getBoolean(CFG_USE_MIN_EXEC_TIME, DEFAULT_USE_MIN_EXEC_TIME);
        m_useMaxExecTime = settings.getBoolean(CFG_USE_MAX_EXEC_TIME, DEFAULT_USE_MAX_EXEC_TIME);
        setUseDefaultExecTime(settings.getBoolean(CFG_USE_DEFAULT_EXEC_TIME, DEFAULT_USE_DEFAULT_EXEC_TIME));
        m_type = DateTimeType.valueOf(settings.getString(CFG_TYPE, DEFAULT_TYPE.name()));
        try {
            m_min = ZonedDateTime.parse(settings.getString(CFG_MIN, DEFAULT_MIN.toString()));
            m_max = ZonedDateTime.parse(settings.getString(CFG_MAX, DEFAULT_MAX.toString()));
        } catch (DateTimeParseException e) {
            m_min = DEFAULT_MIN;
            m_max = DEFAULT_MAX;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
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
        sb.append(isUseDefaultExecTime());
        sb.append(", ");
        sb.append("min=");
        sb.append("{");
        sb.append(m_min.toString());
        sb.append("}");
        sb.append(", ");
        sb.append("max=");
        sb.append("{");
        sb.append(m_max.toString());
        sb.append("}");
        sb.append(", ");
        sb.append("withTime=");
        sb.append(m_type.name());
        return sb.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder()
            .append(m_showNowButton)
            .append(m_granularity)
            .append(m_useMin)
            .append(m_useMax)
            .append(m_useMinExecTime)
            .append(m_useMaxExecTime)
            .append(isUseDefaultExecTime())
            .append(m_min)
            .append(m_max)
            .append(m_type)
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
        DateNodeConfig other = (DateNodeConfig)obj;
        return new EqualsBuilder()
            .append(m_showNowButton, other.m_showNowButton)
            .append(m_granularity, other.m_granularity)
            .append(m_useMin, other.m_useMin)
            .append(m_useMax, other.m_useMax)
            .append(m_useMinExecTime, other.m_useMinExecTime)
            .append(m_useMaxExecTime, other.m_useMaxExecTime)
            .append(isUseDefaultExecTime(), other.isUseDefaultExecTime())
            .append(m_min, other.m_min)
            .append(m_max, other.m_max)
            .append(m_type, other.m_type)
            .isEquals();
    }

}
