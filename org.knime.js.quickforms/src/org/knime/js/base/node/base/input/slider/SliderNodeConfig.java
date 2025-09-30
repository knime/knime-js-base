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
 *   May 24, 2019 (daniel): created
 */
package org.knime.js.base.node.base.input.slider;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.defaultnodesettings.SettingsModelString;

/**
 * Base config file for the slider configuration and widget nodes
 *
 * @author Daniel Bogenrieder, KNIME GmbH, Konstanz, Germany
 */
public class SliderNodeConfig {

    public static final String CFG_DOMAIN_COLUMN = "domainColumn";
    private SettingsModelString m_domainColumn = new SettingsModelString(CFG_DOMAIN_COLUMN, null);

    public static final String CFG_USE_CUSTOM_MIN = "useCustomMin";
    public static final boolean DEFAULT_USE_CUSTOM_MIN = false;
    private boolean m_useCustomMin = DEFAULT_USE_CUSTOM_MIN;

    public static final String CFG_USE_CUSTOM_MAX = "useCustomMax";
    public static final boolean DEFAULT_USE_CUSTOM_MAX = false;
    private boolean m_useCustomMax = DEFAULT_USE_CUSTOM_MAX;

    /**
     * @return the domainColumn
     */
    public SettingsModelString getDomainColumn() {
        return m_domainColumn;
    }

    /**
     * @param domainColumn the domainColumn to set
     */
    public void setDomainColumn(final SettingsModelString domainColumn) {
        m_domainColumn = domainColumn;
    }

    /**
     * @return the customMin
     */
    public boolean isUseCustomMin() {
        return m_useCustomMin;
    }

    /**
     * @param customMin the customMin to set
     */
    public void setUseCustomMin(final boolean customMin) {
        m_useCustomMin = customMin;
    }

    /**
     * @return the customMax
     */
    public boolean isUseCustomMax() {
        return m_useCustomMax;
    }

    /**
     * @param customMax the customMax to set
     */
    public void setUseCustomMax(final boolean customMax) {
        m_useCustomMax = customMax;
    }

    /**
     * Saves the current settings
     *
     * @param settings the settings to save to
     */
    public void saveSettings(final NodeSettingsWO settings) {
        m_domainColumn.saveSettingsTo(settings);
        settings.addBoolean(CFG_USE_CUSTOM_MIN, m_useCustomMin);
        settings.addBoolean(CFG_USE_CUSTOM_MAX, m_useCustomMax);
    }

    /**
     * Loads the config from saved settings
     *
     * @param settings the settings to load from
     * @throws InvalidSettingsException
     */
    public void loadSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
        m_useCustomMin = settings.getBoolean(CFG_USE_CUSTOM_MIN);
        m_useCustomMax = settings.getBoolean(CFG_USE_CUSTOM_MAX);
        m_domainColumn.loadSettingsFrom(settings);
    }

    /**
     * Loads the config from saved settings for dialog display
     *
     * @param settings the settings to load from
     */
    public void loadSettingsInDialog(final NodeSettingsRO settings) {
        m_useCustomMin = settings.getBoolean(CFG_USE_CUSTOM_MIN, DEFAULT_USE_CUSTOM_MIN);
        m_useCustomMax = settings.getBoolean(CFG_USE_CUSTOM_MAX, DEFAULT_USE_CUSTOM_MAX);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString());
        sb.append(", ");
        sb.append("useCustomMin=");
        sb.append(m_useCustomMin);
        sb.append(", ");
        sb.append("useCustomMax=");
        sb.append(m_useCustomMax);
        if (m_domainColumn != null) {
            sb.append(", domainColumn=");
            sb.append(m_domainColumn);
        }
        return sb.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder().appendSuper(super.hashCode())
                .append(m_domainColumn)
                .append(m_useCustomMin)
                .append(m_useCustomMax)
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
        SliderNodeConfig other = (SliderNodeConfig)obj;
        return new EqualsBuilder().appendSuper(super.equals(obj))
                .append(m_useCustomMin, other.m_useCustomMin)
                .append(m_useCustomMax, other.m_useCustomMax)
                .append(m_domainColumn, other.m_domainColumn)
                .isEquals();
    }


}
