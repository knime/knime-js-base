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
 *   3 May 2019 (albrecht): created
 */
package org.knime.js.base.node.configuration;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.knime.core.node.dialog.DialogNodeValue;
import org.knime.core.quickform.QuickFormRepresentation;

/**
 *
 * @author Christian Albrecht, KNIME GmbH, Konstanz, Germany
 */
public abstract class AbstractDialogNodeRepresentation<VAL extends DialogNodeValue,
    CONF extends LabeledDialogNodeConfig<VAL>> implements QuickFormRepresentation<VAL> {

    private final String m_label;
    private final String m_description;
    private final VAL m_defaultValue;
    private final VAL m_currentValue;

    /**
     * @param currentValue The value currently used by the node
     * @param config The config of the node
     */
    public AbstractDialogNodeRepresentation(final VAL currentValue, final CONF config) {
        m_label = config.getLabel();
        m_description = config.getDescription();
        m_defaultValue = config.getDefaultValue();
        m_currentValue = currentValue;
    }

    /**
     * @return the label
     */
    @Override
    public String getLabel() {
        return m_label;
    }

    /**
     * @return the description
     */
    @Override
    public String getDescription() {
        return m_description;
    }

    /**
     * @return the defaultValue
     */
    public VAL getDefaultValue() {
        return m_defaultValue;
    }

    /**
     * @return the currentValue
     */
    public VAL getCurrentValue() {
        return m_currentValue;
    }

    /**
     * @param panel The panel to fill with the information contained in this representation
     */
    protected void fillDialogPanel(final AbstractDialogNodeConfigurationPanel<VAL> panel) {
        panel.setLabel(m_label);
        panel.setDescription(m_description);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("label=");
        sb.append(m_label);
        sb.append(", ");
        sb.append("description=");
        sb.append(m_description);
        return sb.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(m_label)
                .append(m_description)
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
        AbstractDialogNodeRepresentation<VAL, CONF> other = (AbstractDialogNodeRepresentation<VAL, CONF>) obj;
        return new EqualsBuilder()
                .append(m_label, other.m_label)
                .append(m_description, other.m_description)
                .isEquals();
    }

}
