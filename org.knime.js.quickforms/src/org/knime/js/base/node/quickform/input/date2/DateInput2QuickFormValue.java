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
 *   14.10.2013 (Christian Albrecht, KNIME AG, Zurich, Switzerland): created
 */
package org.knime.js.base.node.quickform.input.date2;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.dialog.DialogNodeValue;
import org.knime.core.util.JsonUtil;
import org.knime.js.core.JSONViewContent;
import org.knime.time.util.DateTimeUtils;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import jakarta.json.JsonException;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonString;
import jakarta.json.JsonValue;

/**
 * The value for the date input quick form node.
 *
 * @author Patrick Winter, KNIME AG, Zurich, Switzerland
 * @author Simon Schmid, KNIME.com, Konstanz, Germany
 */
@JsonAutoDetect
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public class DateInput2QuickFormValue extends JSONViewContent implements DialogNodeValue {

    /**
     * The default date for all date settings.
     */
    static final ZonedDateTime DEFAULT_ZDT = ZonedDateTime.now().withNano(0);

    private static final String CFG_DATE = "date&time";

    private ZonedDateTime m_date = DEFAULT_ZDT;

    /**
     * {@inheritDoc}
     */
    @Override
    @JsonIgnore
    public void saveToNodeSettings(final NodeSettingsWO settings) {
        String dateString = m_date != null ? m_date.toString() : null;
        settings.addString(CFG_DATE, dateString);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @JsonIgnore
    public void loadFromNodeSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
        String value = settings.getString(CFG_DATE);
        if (value == null) {
            m_date = null;
        } else {
            try {
                setDate(ZonedDateTime.parse(value));
            } catch (Exception e) {
                throw new InvalidSettingsException("Can't parse date: " + value, e);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @JsonIgnore
    public void loadFromNodeSettingsInDialog(final NodeSettingsRO settings) {
        String value = settings.getString(CFG_DATE, DEFAULT_ZDT.toString());
        if (value == null) {
            m_date = null;
        } else {
            try {
                setDate(ZonedDateTime.parse(value));
            } catch (Exception e) {
                m_date = DEFAULT_ZDT;
            }
        }
    }

    /**
     * @return the date
     */
    @JsonIgnore
    public ZonedDateTime getDate() {
        return m_date;
    }

    /**
     * @return the string
     */
    @JsonProperty("datestring")
    public String getDateAsString() {
        return m_date.toString();
    }

    /**
     * @param date the date to set
     */
    @JsonIgnore
    public void setDate(final ZonedDateTime date) {
        m_date = date;
    }

    /**
     * @param zdtString the zoned date time to set
     */
    @JsonProperty("datestring")
    public void setDateTimeComponent(final String zdtString) {
        Optional<ZonedDateTime> opt = DateTimeUtils.asZonedDateTime(zdtString);
        m_date = opt.isPresent() ? opt.get()
            : ZonedDateTime.of(DateTimeUtils.asLocalDateTime(zdtString).get(), ZoneId.systemDefault());
    }

    /**
     * @param zone the zone to set
     */
    @JsonProperty("zonestring")
    public void setTimeZoneComponent(final String zone) {
        m_date = ZonedDateTime.of(m_date.toLocalDateTime(), ZoneId.of(zone));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("date=");
        sb.append("{");
        sb.append(m_date);
        sb.append("}");
        return sb.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(m_date).toHashCode();
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
        DateInput2QuickFormValue other = (DateInput2QuickFormValue)obj;
        return new EqualsBuilder().append(m_date, other.m_date).isEquals();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadFromString(final String fromCmdLine) throws UnsupportedOperationException {
        try {
            if (fromCmdLine == null) {
                throw new UnsupportedOperationException("Input must not be null!");
            } else {
                updateDateByStringInput(fromCmdLine);
            }
        } catch (IllegalArgumentException e) {
            throw new UnsupportedOperationException(e.getMessage());
        }
    }

    private void updateDateByStringInput(final String string) throws IllegalArgumentException {
        if (DateTimeUtils.asZonedDateTime(string).isPresent()) {
            m_date = ZonedDateTime.parse(string);
        } else if (DateTimeUtils.asLocalDateTime(string).isPresent()) {
            m_date = ZonedDateTime.of(LocalDateTime.parse(string), m_date.getZone());
        } else if (DateTimeUtils.asLocalDate(string).isPresent()) {
            m_date = ZonedDateTime.of(LocalDate.parse(string), m_date.toLocalTime(), m_date.getZone());
        } else if (DateTimeUtils.asLocalTime(string).isPresent()) {
            m_date = ZonedDateTime.of(m_date.toLocalDate(), LocalTime.parse(string), m_date.getZone());
        } else if (DateTimeUtils.asTimezone(string).isPresent()) {
            m_date = ZonedDateTime.of(m_date.toLocalDateTime(), ZoneId.of(string));
        } else {
            throw new IllegalArgumentException(string + " cannot be parsed as any date&time type or time zone!");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadFromJson(final JsonValue json) throws JsonException {
        if (json instanceof JsonString) {
            loadFromString(((JsonString)json).getString());
        } else if (json instanceof JsonObject) {
            try {
                JsonValue val = ((JsonObject)json).get(CFG_DATE);
                if (JsonValue.NULL.equals(val)) {
                    throw new IllegalArgumentException("Input must not be null!");
                }
                String dateVal = ((JsonObject)json).getString(CFG_DATE);
                if (dateVal == null) {
                    throw new IllegalArgumentException("Input must not be null!");
                } else {
                    updateDateByStringInput(dateVal);
                }
            } catch (Exception e) {
                throw new JsonException("Expected string value for key '" + CFG_DATE
                    + "' to be a date, time, date&time, zoned date&time or a time zone in ISO format.", e);
            }
        } else {
            throw new JsonException("Expected JSON object or JSON string, but got " + json.getValueType());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JsonValue toJson() {
        JsonObjectBuilder builder = JsonUtil.getProvider().createObjectBuilder();
        if (m_date == null) {
            builder.addNull(CFG_DATE);
        } else {
            builder.add(CFG_DATE, m_date.toString());
        }
        return builder.build().get(CFG_DATE);
    }

}
