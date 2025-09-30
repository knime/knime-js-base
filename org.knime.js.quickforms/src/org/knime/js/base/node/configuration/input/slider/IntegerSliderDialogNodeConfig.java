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
 *   May 24, 2019 (Daniel Bogenrieder): created
 */
package org.knime.js.base.node.configuration.input.slider;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.defaultnodesettings.SettingsModelString;
import org.knime.js.base.node.base.input.slider.SliderNodeConfig;
import org.knime.js.base.node.configuration.LabeledFlowVariableDialogNodeConfig;

/**
 * The config for the slider configuration node.
 *
 * @author Daniel Bogenrieder, KNIME GmbH, Konstanz, Germany
 */
public class IntegerSliderDialogNodeConfig extends LabeledFlowVariableDialogNodeConfig<IntegerSliderDialogNodeValue> {

    private final SliderNodeConfig m_sliderConfig;
    public static final String CFG_MIN = "customMin";
    public static final double DEFAULT_MIN = 0;
    private double m_customMin = DEFAULT_MIN;

    public static final String CFG_MAX = "customMax";
    public static final double DEFAULT_MAX = 100;
    private double m_customMax = DEFAULT_MAX;

    /**
     * Instantiate a new config object
     */
    public IntegerSliderDialogNodeConfig() {
        m_sliderConfig = new SliderNodeConfig();
    }

    /**
     * @return the stringConfig
     */
    public SliderNodeConfig getSliderConfig() {
        return m_sliderConfig;
    }

    /**
     * @return the domainColumn
     */
    public SettingsModelString getDomainColumn() {
        return m_sliderConfig.getDomainColumn();
    }

    /**
     * @return the useCustomMin
     */
    public boolean isUseCustomMin() {
        return m_sliderConfig.isUseCustomMin();
    }

    /**
     * @param useCustomMin the useCustomMin to set
     */
    public void setUseCustomMin(final boolean useCustomMin) {
        m_sliderConfig.setUseCustomMin(useCustomMin);
    }

    /**
     * @return the useCustomMax
     */
    public boolean isUseCustomMax() {
        return m_sliderConfig.isUseCustomMax();
    }

    /**
     * @param useCustomMax the useCustomMax to set
     */
    public void setUseCustomMax(final boolean useCustomMax) {
        m_sliderConfig.setUseCustomMax(useCustomMax);
    }

    /**
     * @return the customMin value of the slider
     */
    public double getCustomMin() {
        return m_customMin;
    }

    /**
     * @param customMin the customMin value to set
     */
    public void setCustomMin(final double customMin) {
        m_customMin = customMin;
    }

    /**
     * @return the customMax value of the slider
     */
    public double getCustomMax() {
        return m_customMax;
    }

    /**
     * @param customMax the max value to set
     */
    public void setCustomMax(final double customMax) {
        m_customMax = customMax;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected IntegerSliderDialogNodeValue createEmptyValue() {
        return new IntegerSliderDialogNodeValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveSettings(final NodeSettingsWO settings) {
        super.saveSettings(settings);
        settings.addDouble(CFG_MIN, m_customMin);
        settings.addDouble(CFG_MAX, m_customMax);
        m_sliderConfig.saveSettings(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
        super.loadSettings(settings);
        m_customMin = settings.getDouble(CFG_MIN, DEFAULT_MIN);
        m_customMax = settings.getDouble(CFG_MAX, DEFAULT_MAX);
        m_sliderConfig.loadSettings(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadSettingsInDialog(final NodeSettingsRO settings) {
        super.loadSettingsInDialog(settings);
        m_customMin = settings.getDouble(CFG_MIN, DEFAULT_MIN);
        m_customMax = settings.getDouble(CFG_MAX, DEFAULT_MAX);
        m_sliderConfig.loadSettingsInDialog(settings);
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
        sb.append("customMin=");
        sb.append(m_customMin);
        sb.append(", ");
        sb.append("customMax=");
        sb.append(m_customMax);
        return sb.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder().appendSuper(super.hashCode())
                .append(m_sliderConfig)
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
        IntegerSliderDialogNodeConfig other = (IntegerSliderDialogNodeConfig)obj;
        return new EqualsBuilder().appendSuper(super.equals(obj))
                .append(m_sliderConfig, other.m_sliderConfig)
                .append(m_customMin, other.m_customMin)
                .append(m_customMax, other.m_customMax)
                .isEquals();
    }

}
