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
 *   27 May 2019 (albrecht): created
 */
package org.knime.js.base.node.base.date;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.js.core.JSONViewContent;
import org.knime.time.util.DateTimeUtils;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * The base value for the date configuration and widget node
 *
 * @author Christian Albrecht, KNIME GmbH, Konstanz, Germany
 */
@JsonAutoDetect
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public class DateNodeValue extends JSONViewContent {

    /**
     * Config setting for the double value
     */
    protected static final String CFG_DATE = "date&time";
    private ZonedDateTime m_date = DateNodeConfig.DEFAULT_ZDT;

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
        return new HashCodeBuilder()
            .append(m_date)
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
        DateNodeValue other = (DateNodeValue)obj;
        return new EqualsBuilder()
            .append(m_date, other.m_date)
            .isEquals();
    }

}
