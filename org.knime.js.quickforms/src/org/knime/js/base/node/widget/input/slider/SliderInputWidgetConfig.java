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
 *   May 27, 2019 (Daniel Bogenrieder): created
 */
package org.knime.js.base.node.widget.input.slider;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettings;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.defaultnodesettings.SettingsModelString;
import org.knime.js.base.node.base.input.slider.SliderNodeConfig;
import org.knime.js.base.node.base.input.slider.SliderNodeValue;
import org.knime.js.base.node.widget.LabeledFlowVariableWidgetConfig;
import org.knime.js.core.settings.slider.SliderSettings;

/**
 * The config for the slider widget node
 *
 * @author Daniel Bogenrieder, KNIME GmbH, Konstanz, Germany
 */
public class SliderInputWidgetConfig extends LabeledFlowVariableWidgetConfig<SliderNodeValue> {

    private final SliderNodeConfig m_sliderConfig;

    private static String CFG_SLIDER = "sliderSettings";
    private static String CFG_SLIDER_EXISTS = "sliderExists";
    private SliderSettings m_sliderSettings = null;

    /**
     * Instantiate a new config object
     */
    public SliderInputWidgetConfig() {
        m_sliderConfig = new SliderNodeConfig();
    }

    /**
     * @return the sliderSettings
     */
    public SliderSettings getSliderSettings() {
        return m_sliderSettings;
    }

    /**
     * @return the domainColumn
     */
    public SettingsModelString getDomainColumn() {
        return m_sliderConfig.getDomainColumn();
    }

    /**
     * @param sliderSettings the sliderSettings to set
     */
    public void setSliderSettings(final SliderSettings sliderSettings) {
        m_sliderSettings = sliderSettings;
    }

    /**
     * @return the stringConfig
     */
    public SliderNodeConfig getSliderConfig() {
        return m_sliderConfig;
    }

    /**
     * @param useCustomMin the useCustomMin to set
     */
    public void setUseCustomMin(final boolean useCustomMin) {
        m_sliderConfig.setUseCustomMin(useCustomMin);
    }

    /**
     * @return useCustomMin
     */
    public boolean isUseCustomMin () {
        return m_sliderConfig.isUseCustomMin();
    }

    /**
     * @param useCustomMax the useCustomMax to set
     */
    public void setUseCustomMax(final boolean useCustomMax) {
        m_sliderConfig.setUseCustomMax(useCustomMax);
    }

    /**
     * @return useCustomMax
     */
    public boolean isUseCustomMax () {
        return m_sliderConfig.isUseCustomMax();
    }

    /**
     * @return customMin
     */
    public double getCustomMin() {
        return m_sliderConfig.getCustomMin();
    }

    /**
     * @param min sets the customMin value
     */
    public void setCustomMin(final double min) {
        m_sliderConfig.setCustomMin(min);
    }

    /**
     * @return customMax
     */
    public double getCustomMax() {
        return m_sliderConfig.getCustomMax();
    }

    /**
     * @param max sets the customMax value
     */
    public void setCustomMax(final double max) {
        m_sliderConfig.setCustomMax(max);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected SliderNodeValue createEmptyValue() {
        return new SliderNodeValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveSettings(final NodeSettingsWO settings) {
        super.saveSettings(settings);
        NodeSettingsWO sliderSettings = settings.addNodeSettings(CFG_SLIDER);
        boolean sliderExists = m_sliderSettings != null;
        sliderSettings.addBoolean(CFG_SLIDER_EXISTS, sliderExists);
        if (sliderExists) {
            m_sliderSettings.saveToNodeSettings(sliderSettings);
        }
        m_sliderConfig.saveSettings(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
        super.loadSettings(settings);
        NodeSettingsRO sliderSettings = settings.getNodeSettings(CFG_SLIDER);
        boolean sliderExists = sliderSettings.getBoolean(CFG_SLIDER_EXISTS);
        m_sliderSettings = sliderExists ? new SliderSettings() : null;
        if (sliderExists) {
            m_sliderSettings.loadFromNodeSettings(sliderSettings);
        }
        m_sliderConfig.loadSettings(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadSettingsInDialog(final NodeSettingsRO settings) {
        super.loadSettingsInDialog(settings);
        m_sliderConfig.loadSettingsInDialog(settings);
        try {
            NodeSettingsRO sliderSettings = settings.getNodeSettings(CFG_SLIDER);
            m_sliderSettings = new SliderSettings();
            m_sliderSettings.loadFromNodeSettingsInDialog(sliderSettings);
        } catch (InvalidSettingsException e) {
            m_sliderSettings = new SliderSettings();
            m_sliderSettings.loadFromNodeSettingsInDialog(new NodeSettings(null));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString());
        sb.append(", ");
        sb.append(m_sliderConfig.toString());
        sb.append(", ");
        sb.append("sliderSettings=");
        sb.append(m_sliderSettings);
        return sb.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder().appendSuper(super.hashCode())
                .append(m_sliderSettings)
                .append(m_sliderConfig)
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
        SliderInputWidgetConfig other = (SliderInputWidgetConfig)obj;
        return new EqualsBuilder().appendSuper(super.equals(obj))
                .append(m_sliderConfig, other.m_sliderConfig)
                .append(m_sliderSettings, other.m_sliderSettings)
                .isEquals();
    }

}
