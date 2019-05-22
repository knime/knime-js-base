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
 *   22 May 2019 (albrecht): created
 */
package org.knime.js.base.node.configuration.input.integer;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.knime.core.node.dialog.DialogNodePanel;
import org.knime.js.base.node.configuration.AbstractDialogNodeRepresentation;

/**
 * The dialog representation of the integer configuration node
 *
 * @author Christian Albrecht, KNIME GmbH, Konstanz, Germany
 */
public class IntegerDialogNodeRepresentation
    extends AbstractDialogNodeRepresentation<IntegerDialogNodeValue, IntegerDialogNodeConfig> {

    private final boolean m_useMin;
    private final boolean m_useMax;
    private final int m_min;
    private final int m_max;

    /**
     * @param currentValue The value currently used by the node
     * @param config The config of the node
     */
    public IntegerDialogNodeRepresentation(final IntegerDialogNodeValue currentValue,
        final IntegerDialogNodeConfig config) {
        super(currentValue, config);
        m_useMin = config.isUseMin();
        m_useMax = config.isUseMax();
        m_min = config.getMin();
        m_max = config.getMax();
    }

    /**
     * @return the useMin
     */
    public boolean isUseMin() {
        return m_useMin;
    }

    /**
     * @return the useMax
     */
    public boolean isUseMax() {
        return m_useMax;
    }

    /**
     * @return the min
     */
    public int getMin() {
        return m_min;
    }

    /**
     * @return the max
     */
    public int getMax() {
        return m_max;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DialogNodePanel<IntegerDialogNodeValue> createDialogPanel() {
        IntegerConfigurationPanel panel = new IntegerConfigurationPanel(this);
        fillDialogPanel(panel);
        return panel;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString());
        sb.append(", ");
        sb.append("useMin=");
        sb.append(m_useMin);
        sb.append(", ");
        sb.append("useMax=");
        sb.append(m_useMax);
        sb.append(", ");
        sb.append("min=");
        sb.append(m_min);
        sb.append(", ");
        sb.append("max=");
        sb.append(m_max);
        return sb.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder()
            .appendSuper(super.hashCode())
            .append(m_useMin)
            .append(m_useMax)
            .append(m_min)
            .append(m_max)
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
        IntegerDialogNodeRepresentation other = (IntegerDialogNodeRepresentation)obj;
        return new EqualsBuilder().appendSuper(super.equals(obj))
                .append(m_useMin, other.m_useMin)
                .append(m_useMax, other.m_useMax)
                .append(m_min, other.m_min)
                .append(m_max, other.m_max)
                .isEquals();
    }

}
