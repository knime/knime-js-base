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
 */
package org.knime.js.base.node.widget.reexecution.refresh;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.js.core.JSONViewContent;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 *
 * @author Ben Laney, KNIME GmbH, Konstanz, Germany
 * @author Shayan Heidary, KNIME GmbH, Konstanz, Germany
 */
@JsonAutoDetect
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public class RefreshButtonWidgetViewValue extends JSONViewContent {

    private static final String CFG_REFRESH_COUNTER = "refreshCounter";
    private static final int DEFAULT_REFRESH_COUNTER = 0;
    private int m_refreshCounter = DEFAULT_REFRESH_COUNTER;

    private static final String CFG_REFRESH_TIMESTAMP = "refreshTimestamp";
    private static final String DEFAULT_REFRESH_TIMESTAMP = "";
    private String m_refreshTimestamp = DEFAULT_REFRESH_TIMESTAMP;

    /**
     * @return the refreshCounter
     */
    @JsonProperty("refreshCounter")
    public int getRefreshCounter() {
        return m_refreshCounter;
    }

    /**
     * @param refreshCounter the refreshCounter to set
     */
    @JsonProperty("refreshCounter")
    public void setRefreshCounter(final int refreshCounter) {
        m_refreshCounter = refreshCounter;
    }

    /**
     * @return the refreshTimestamp
     */
    @JsonProperty("refreshTimestamp")
    public String getRefreshTimestamp() {
        return m_refreshTimestamp;
    }

    /**
     * @param refreshTimestamp the refreshTimestamp to set
     */
    @JsonProperty("refreshTimestamp")
    public void setRefreshTimestamp(final String refreshTimestamp) {
        m_refreshTimestamp = refreshTimestamp;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @JsonIgnore
    public void saveToNodeSettings(final NodeSettingsWO settings) {
        settings.addInt(CFG_REFRESH_COUNTER, getRefreshCounter());
        settings.addString(CFG_REFRESH_TIMESTAMP, getRefreshTimestamp());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @JsonIgnore
    public void loadFromNodeSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
        // added with 5.3
        setRefreshCounter(settings.getInt(CFG_REFRESH_COUNTER, DEFAULT_REFRESH_COUNTER));
        setRefreshTimestamp(settings.getString(CFG_REFRESH_TIMESTAMP, DEFAULT_REFRESH_TIMESTAMP));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("refresh-counter=");
        sb.append(getRefreshCounter());
        sb.append(", ");
        sb.append("refresh-timestamp=");
        sb.append(getRefreshTimestamp());
        return sb.toString();
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
        var other = (RefreshButtonWidgetViewValue)obj;
        return new EqualsBuilder()
                .append(m_refreshCounter, other.m_refreshCounter)
                .append(m_refreshTimestamp, other.m_refreshTimestamp)
                .isEquals();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @JsonIgnore
    public int hashCode() {
        return new HashCodeBuilder()
                .append(m_refreshCounter)
                .append(m_refreshTimestamp)
                .toHashCode();
    }
}
