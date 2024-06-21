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
 */
package org.knime.js.base.node.widget.reexecution.refresh;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.js.base.node.widget.ReExecutableWidgetConfig;

/**
 * The node configuration for the refresh button widget node.
 *
 * @author Ben Laney, KNIME GmbH, Konstanz, Germany
 * @author Christian Albrecht, KNIME GmbH, Konstanz, Germany
 */
public class RefreshButtonWidgetNodeConfig extends ReExecutableWidgetConfig<RefreshButtonWidgetViewValue> {

    private static final boolean DEFAULT_TRIGGER_REEXECUTE = true;

    /** legacy node flow variable output name - outputs the button text */
    protected static final String FLOW_VARIABLE_NAME = "refresh_widget";

    private static final String DEFAULT_TEXT = "Refresh";
    private static final String CFG_BUTTON_TEXT = "buttonText";
    private String m_buttonText = DEFAULT_TEXT;

    /**
     *
     */
    public RefreshButtonWidgetNodeConfig() {
        super(DEFAULT_TRIGGER_REEXECUTE);
    }

    /**
     * @return the button text
     */
    public String getButtonText() {
        return m_buttonText;
    }

    /**
     * @param buttonText the button text to set
     */
    public void setButtonText(final String buttonText) {
        m_buttonText = buttonText;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveSettings(final NodeSettingsWO settings) {
        super.saveSettings(settings);
        settings.addString(CFG_BUTTON_TEXT, m_buttonText);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
        // Can not call super.loadSettings here. Some configs were added later and need to be loaded with defaults.
        m_defaultValue = createEmptyValue();
        try {
            NodeSettingsRO defaultValueSettings = settings.getNodeSettings(CFG_DEFAULT_VALUE);
            m_defaultValue.loadFromNodeSettings(defaultValueSettings);
        } catch (InvalidSettingsException e) { /* use newly created value */ }
        setHideInWizard(settings.getBoolean(CFG_HIDE_IN_WIZARD));

        // added with 5.3
        setCustomCSS(settings.getString(CFG_CUSTOM_CSS, DEFAULT_CUSTOM_CSS));
        // adds required flag
        getLabelConfig().loadSettingsInDialog(settings);

        // added with 5.3
        getFlowVariableConfig().loadSettingsInDialog(settings);
        m_buttonText = settings.getString(CFG_BUTTON_TEXT);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadSettingsInDialog(final NodeSettingsRO settings) {
        super.loadSettingsInDialog(settings);
        m_buttonText = settings.getString(CFG_BUTTON_TEXT, DEFAULT_TEXT);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString());
        sb.append(", buttonText=");
        sb.append(m_buttonText);
        return sb.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder()
            .appendSuper(super.hashCode())
            .append(m_buttonText)
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
        RefreshButtonWidgetNodeConfig other = (RefreshButtonWidgetNodeConfig)obj;
        return new EqualsBuilder()
            .appendSuper(super.equals(other))
            .append(m_buttonText, other.m_buttonText)
            .isEquals();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected RefreshButtonWidgetViewValue createEmptyValue() {
        return new RefreshButtonWidgetViewValue();
    }
}
