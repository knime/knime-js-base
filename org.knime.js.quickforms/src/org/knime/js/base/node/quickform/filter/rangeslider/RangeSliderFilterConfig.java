/*
 * ------------------------------------------------------------------------
 *
 *  Copyright by KNIME GmbH, Konstanz, Germany
 *  Website: http://www.knime.org; Email: contact@knime.org
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
 *  KNIME and ECLIPSE being a combined program, KNIME GMBH herewith grants
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
 *   Oct 10, 2016 (albrecht): created
 */
package org.knime.js.base.node.quickform.filter.rangeslider;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettings;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.defaultnodesettings.SettingsModelString;
import org.knime.js.core.settings.slider.SliderSettings;

/**
 * The config for the range slider filter node.
 *
 * @author Christian Albrecht, KNIME.com GmbH, Konstanz, Germany
 */
public class RangeSliderFilterConfig {

    private static final String CFG_HIDE_WIZARD = "hideInWizard";
    private static final boolean DEFAULT_HIDE_WIZARD = false;
    private boolean m_hideInWizard = DEFAULT_HIDE_WIZARD;

    private static final String CFG_SLIDER = "sliderSettings";
    private static final String CFG_SLIDER_EXISTS = "sliderExists";
    private SliderSettings m_sliderSettings = null;

    private static final String CFG_DOMAIN_COLUMN = "domainColumn";
    private SettingsModelString m_domainColumn = new SettingsModelString(CFG_DOMAIN_COLUMN, null);

    private static final String CFG_CUSTOM_MIN = "customMin";
    private static final boolean DEFAULT_CUSTOM_MIN = false;
    private boolean m_customMin = DEFAULT_CUSTOM_MIN;

    private static final String CFG_CUSTOM_MAX = "customMax";
    private static final boolean DEFAULT_CUSTOM_MAX = false;
    private boolean m_customMax = DEFAULT_CUSTOM_MAX;

    /**
     * @return the hideInWizard
     */
    public boolean getHideInWizard() {
        return m_hideInWizard;
    }

    /**
     * @param hideInWizard the hideInWizard to set
     */
    public void setHideInWizard(final boolean hideInWizard) {
        m_hideInWizard = hideInWizard;
    }

    /**
     * @return the sliderSettings
     */
    public SliderSettings getSliderSettings() {
        return m_sliderSettings;
    }

    /**
     * @param sliderSettings the sliderSettings to set
     */
    public void setSliderSettings(final SliderSettings sliderSettings) {
        m_sliderSettings = sliderSettings;
    }

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
    public boolean getCustomMin() {
        return m_customMin;
    }

    /**
     * @param customMin the customMin to set
     */
    public void setCustomMin(final boolean customMin) {
        m_customMin = customMin;
    }

    /**
     * @return the customMax
     */
    public boolean getCustomMax() {
        return m_customMax;
    }

    /**
     * @param customMax the customMax to set
     */
    public void setCustomMax(final boolean customMax) {
        m_customMax = customMax;
    }

    public void saveSettings(final NodeSettingsWO settings) {
        settings.addBoolean(CFG_HIDE_WIZARD, m_hideInWizard);
        NodeSettingsWO sliderSettings = settings.addNodeSettings(CFG_SLIDER);
        boolean sliderExists = m_sliderSettings != null;
        sliderSettings.addBoolean(CFG_SLIDER_EXISTS, sliderExists);
        if (sliderExists) {
            m_sliderSettings.saveToNodeSettings(sliderSettings);
        }
        m_domainColumn.saveSettingsTo(settings);
        settings.addBoolean(CFG_CUSTOM_MIN, m_customMin);
        settings.addBoolean(CFG_CUSTOM_MAX, m_customMax);
    }

    public void loadSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
        m_hideInWizard = settings.getBoolean(CFG_HIDE_WIZARD);
        NodeSettingsRO sliderSettings = settings.getNodeSettings(CFG_SLIDER);
        boolean sliderExists = sliderSettings.getBoolean(CFG_SLIDER_EXISTS);
        m_sliderSettings = sliderExists ? new SliderSettings() : null;
        if (sliderExists) {
            m_sliderSettings.loadFromNodeSettings(sliderSettings);
        }
        m_domainColumn.loadSettingsFrom(settings);
        m_customMin = settings.getBoolean(CFG_CUSTOM_MIN);
        m_customMax = settings.getBoolean(CFG_CUSTOM_MAX);
    }

    public void loadSettingsInDialog(final NodeSettingsRO settings) {
        m_hideInWizard = settings.getBoolean(CFG_HIDE_WIZARD, DEFAULT_HIDE_WIZARD);
        try {
            NodeSettingsRO sliderSettings = settings.getNodeSettings(CFG_SLIDER);
            m_sliderSettings = new SliderSettings();
            m_sliderSettings.loadFromNodeSettingsInDialog(sliderSettings);
        } catch (InvalidSettingsException e) {
            m_sliderSettings = new SliderSettings();
            m_sliderSettings.loadFromNodeSettingsInDialog(new NodeSettings(null));
        }
        m_customMin = settings.getBoolean(CFG_CUSTOM_MIN, DEFAULT_CUSTOM_MIN);
        m_customMax = settings.getBoolean(CFG_CUSTOM_MAX, DEFAULT_CUSTOM_MAX);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("hideInWizard=");
        sb.append(m_hideInWizard);
        sb.append(", ");
        sb.append("sliderSettings=");
        sb.append(m_sliderSettings);
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
        return new HashCodeBuilder()
                .append(m_hideInWizard)
                .append(m_sliderSettings)
                .append(m_domainColumn)
                .append(m_customMin)
                .append(m_customMax)
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
        RangeSliderFilterConfig other = (RangeSliderFilterConfig)obj;
        return new EqualsBuilder()
                .append(m_hideInWizard, other.m_hideInWizard)
                .append(m_sliderSettings, other.m_sliderSettings)
                .append(m_domainColumn, other.m_domainColumn)
                .append(m_customMin, other.m_customMin)
                .append(m_customMax, other.m_customMax)
                .isEquals();
    }

}
