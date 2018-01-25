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
 *   4 Apr 2018 (albrecht): created
 */
package org.knime.js.node.minimalRequestHandler;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.js.core.JSONViewContent;

/**
 *
 * @author Christian Albrecht, KNIME GmbH, Konstanz, Germany
 */
public class MinimalRequestHandlerViewRepresentation extends JSONViewContent {

    private boolean m_stallRequests;
    private boolean m_keepOrder;
    private boolean m_cancelPrevious;

    /**
     * @return the stallRequests
     */
    public boolean isStallRequests() {
        return m_stallRequests;
    }

    /**
     * @param stallRequests the stallRequests to set
     */
    public void setStallRequests(final boolean stallRequests) {
        m_stallRequests = stallRequests;
    }

    /**
     * @return the keepOrder
     */
    public boolean isKeepOrder() {
        return m_keepOrder;
    }

    /**
     * @param keepOrder the keepOrder to set
     */
    public void setKeepOrder(final boolean keepOrder) {
        m_keepOrder = keepOrder;
    }

    /**
     * @return the cancelPrevious
     */
    public boolean isCancelPrevious() {
        return m_cancelPrevious;
    }

    /**
     * @param cancelPrevious the cancelPrevious to set
     */
    public void setCancelPrevious(final boolean cancelPrevious) {
        m_cancelPrevious = cancelPrevious;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveToNodeSettings(final NodeSettingsWO settings) {
        settings.addBoolean(MinimalRequestHandlerConfig.CFG_STALL, m_stallRequests);
        settings.addBoolean(MinimalRequestHandlerConfig.CFG_ORDER, m_keepOrder);
        settings.addBoolean(MinimalRequestHandlerConfig.CFG_CANCEL_PREVIOUS, m_cancelPrevious);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadFromNodeSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
        m_stallRequests = settings.getBoolean(MinimalRequestHandlerConfig.CFG_STALL);
        m_keepOrder = settings.getBoolean(MinimalRequestHandlerConfig.CFG_ORDER);
        m_cancelPrevious = settings.getBoolean(MinimalRequestHandlerConfig.CFG_CANCEL_PREVIOUS);
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
        MinimalRequestHandlerViewRepresentation other = (MinimalRequestHandlerViewRepresentation)obj;
        return new EqualsBuilder()
                .append(m_stallRequests, other.m_stallRequests)
                .append(m_keepOrder, other.m_keepOrder)
                .append(m_cancelPrevious, other.m_cancelPrevious)
                .isEquals();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(m_stallRequests)
                .append(m_keepOrder)
                .append(m_cancelPrevious)
                .toHashCode();
    }

}
