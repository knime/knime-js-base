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
 *   Oct 6, 2021 (konrad-amtenbrink): created
 */
package org.knime.js.base.node.base;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.knime.js.core.JSONViewContent;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author Konrad Amtenbrink, KNIME GmbH, Berlin, Germany
 * @param <VAL>
 */
public abstract class ReExecutableRepresentation<VAL extends JSONViewContent> extends LabeledNodeRepresentation<VAL> {

    private boolean m_triggerReExecution;

    /**
     * @param label
     * @param description
     * @param required
     * @param defaultValue
     * @param currentValue
     * @param triggerReExecution
     */
    protected ReExecutableRepresentation(final String label, final String description, final boolean required,
        final VAL defaultValue, final VAL currentValue, final boolean triggerReExecution) {
        super(label, description, required, defaultValue, currentValue);
        m_triggerReExecution = triggerReExecution;
    }

    /**
     * @param currentValue The value currently used by the node
     * @param defaultValue The default value of the node
     * @param config The config of the node
     * @param triggerReExecution
     */
    public ReExecutableRepresentation(final VAL currentValue, final VAL defaultValue,
        final LabeledConfig config, final boolean triggerReExecution) {
        super(currentValue, defaultValue, config);
        m_triggerReExecution = triggerReExecution;

    }

    /**
     * @return the triggerReExecution
     */
    @JsonProperty("triggerReExecution")
    public boolean getTriggerReExecution() {
        return m_triggerReExecution;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString());
        sb.append(", ");
        sb.append(m_triggerReExecution);
        return sb.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .appendSuper(super.hashCode())
                .append(m_triggerReExecution)
                .toHashCode();
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
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
        ReExecutableRepresentation<VAL> other = (ReExecutableRepresentation<VAL>)obj;
        return new EqualsBuilder()
                .appendSuper(super.equals(other))
                .append(m_triggerReExecution, other.m_triggerReExecution)
                .isEquals();
    }
}
