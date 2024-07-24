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
 *   9 May 2019 (albrecht): created
 */
package org.knime.js.base.node.widget;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.js.core.JSONViewContent;

/**
 *
 * @author Christian Albrecht, KNIME GmbH, Konstanz, Germany
 * @param <VAL> The view value implementation of the widget node
 */
public abstract class WidgetConfig<VAL extends JSONViewContent> {

    protected static final String CFG_HIDE_IN_WIZARD = "hideInWizard";
    protected static final boolean DEFAULT_HIDE_IN_WIZARD = false;
    private boolean m_hideInWizard = DEFAULT_HIDE_IN_WIZARD;

    protected static final String CFG_CUSTOM_CSS = "customCSS";
    protected static final String DEFAULT_CUSTOM_CSS = "";
    private String m_customCSS = DEFAULT_CUSTOM_CSS;

    protected static final String CFG_DEFAULT_VALUE = "defaultValue";
    protected VAL m_defaultValue;

    /**
     * @return the hideInWizard
     */
    public boolean isHideInWizard() {
        return m_hideInWizard;
    }

    /**
     * @param hideInWizard the hideInWizard to set
     */
    public void setHideInWizard(final boolean hideInWizard) {
        m_hideInWizard = hideInWizard;
    }

    /**
     * @return the customCSS
     */
    public String getCustomCSS() {
        return m_customCSS;
    }

    /**
     * @param customCSS the customCSS to set
     */
    public void setCustomCSS(final String customCSS) {
        m_customCSS = customCSS;
    }

    /**
     * @return the default value
     */
    public synchronized VAL getDefaultValue() {
        if (m_defaultValue == null) {
            m_defaultValue = createEmptyValue();
        }
        return m_defaultValue;
    }

    /**
     * Creates an instance of a value used for the default value of this config.
     *
     * @return Create a value instance
     */
    protected abstract VAL createEmptyValue();

    /**
     * Override this method if re-execution can be triggered
     *
     * @return false by default
     */
    protected boolean canTriggerReExecution() {
        return false;
    }

    /**
     * @param settings The settings to save to
     */
    public void saveSettings(final NodeSettingsWO settings) {
        NodeSettingsWO defaultValueSettings = settings.addNodeSettings(CFG_DEFAULT_VALUE);
        getDefaultValue().saveToNodeSettings(defaultValueSettings);
        settings.addBoolean(CFG_HIDE_IN_WIZARD, m_hideInWizard);
        settings.addString(CFG_CUSTOM_CSS, m_customCSS);
    }

    /**
     * @param settings The settings to load from
     * @throws InvalidSettingsException If the settings are not valid
     */
    public void loadSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
        NodeSettingsRO defaultValueSettings = settings.getNodeSettings(CFG_DEFAULT_VALUE);
        m_defaultValue = createEmptyValue();
        m_defaultValue.loadFromNodeSettings(defaultValueSettings);
        m_hideInWizard = settings.getBoolean(CFG_HIDE_IN_WIZARD);
        m_customCSS = settings.getString(CFG_CUSTOM_CSS);
    }

    /**
     * @param settings The settings to load from
     */
    public void loadSettingsInDialog(final NodeSettingsRO settings) {
        m_defaultValue = createEmptyValue();
        NodeSettingsRO defaultValueSettings;
        try {
            defaultValueSettings = settings.getNodeSettings(CFG_DEFAULT_VALUE);
            m_defaultValue.loadFromNodeSettings(defaultValueSettings);
        } catch (InvalidSettingsException e) {
            // Stay with defaults
        }
        m_hideInWizard = settings.getBoolean(CFG_HIDE_IN_WIZARD, DEFAULT_HIDE_IN_WIZARD);
        m_customCSS = settings.getString(CFG_CUSTOM_CSS, DEFAULT_CUSTOM_CSS);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("hideInDialog=");
        sb.append(m_hideInWizard);
        sb.append(", ");
        sb.append("defaultValue=");
        sb.append("{");
        sb.append(getDefaultValue());
        sb.append("}");
        return sb.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(m_hideInWizard)
                .append(m_customCSS)
                .append(getDefaultValue())
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
        WidgetConfig<VAL> other = (WidgetConfig<VAL>)obj;
        return new EqualsBuilder()
                .append(m_hideInWizard, other.m_hideInWizard)
                .append(m_customCSS, other.m_customCSS)
                .append(getDefaultValue(), other.getDefaultValue())
                .isEquals();
    }

}
