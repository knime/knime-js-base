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
 *   29 Nov 2019 (Marc Bux, KNIME GmbH, Berlin, Germany): created
 */
package org.knime.js.base.node.base.input.bool;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;

/**
 * Base config file for the boolean configuration and widget nodes
 *
 * @author Marc Bux, KNIME GmbH, Berlin, Germany
 */
public class BooleanNodeConfig {

    private static final String CFG_PUSH_INT_VAR = "pushIntVar";

    private static final boolean DEFAULT_PUSH_INT_VAR = false;

    private boolean m_pushIntVar = DEFAULT_PUSH_INT_VAR;

    /**
     * @return the pushIntVar
     */
    public boolean isPushIntVar() {
        return m_pushIntVar;
    }

    /**
     * @param pushIntVar the pushIntVar to set
     */
    public void setPushIntVar(final boolean pushIntVar) {
        m_pushIntVar = pushIntVar;
    }

    /**
     * Saves the current settings.
     *
     * @param settings the settings to save to
     */
    public void saveSettings(final NodeSettingsWO settings) {
        settings.addBoolean(CFG_PUSH_INT_VAR, m_pushIntVar);
    }

    /**
     * Loads the config from saved settings.
     *
     * @param settings the settings to load from
     */
    public void loadSettings(final NodeSettingsRO settings) {
        // default is true for reasons of backwards compatibility:
        // prior to KNIME 4.1, Boolean Widget and Configuration nodes would always push integer flow variables
        m_pushIntVar = settings.getBoolean(CFG_PUSH_INT_VAR, true);
    }

    /**
     * Loads the config from saved settings for dialog display.
     *
     * @param settings the settings to load from
     */
    public void loadSettingsInDialog(final NodeSettingsRO settings) {
        m_pushIntVar = settings.getBoolean(CFG_PUSH_INT_VAR, DEFAULT_PUSH_INT_VAR);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("pushIntVar=");
        sb.append(m_pushIntVar);
        return sb.toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().appendSuper(super.hashCode()).append(m_pushIntVar).toHashCode();
    }

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
        final BooleanNodeConfig other = (BooleanNodeConfig)obj;
        return new EqualsBuilder().appendSuper(super.equals(obj)).append(m_pushIntVar, other.m_pushIntVar).isEquals();
    }

}
