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
import org.knime.core.node.NodeLogger;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.js.base.util.LabeledViewConfig;

/**
 * The node configuration for the refresh button widget node.
 *
 * @author Ben Laney, KNIME GmbH, Konstanz, Germany
 */
public class RefreshButtonWidgetNodeConfig extends LabeledViewConfig {

    private static final NodeLogger LOGGER = NodeLogger.getLogger(RefreshButtonWidgetNodeConfig.class);

    /** node flow variable output name */
    protected static final String FLOW_VARIABLE_NAME = "refresh_widget";
    /** */
    protected static final String CFG_COUNTING_HELPER_FLAG = "helper_flag";

    private static final String DEFAULT_LABEL = "";
    private static final String CFG_LABEL = "label";

    private static final String DEFAULT_DESCRIPTION = "";
    private static final String CFG_DESCRIPTION = "description";

    private static final String DEFAULT_TEXT = "Refresh";
    private static final String CFG_BUTTON_TEXT = "buttonText";
    private String m_buttonText = DEFAULT_TEXT;

    private static final String CFG_HIDE_IN_WIZARD = "hideInWizard";
    private static final boolean DEFAULT_HIDE_IN_WIZARD = false;

    private static final String CFG_TRIGGER_REEXECUTION = "trigger_reexecution";
    private static final Boolean DEFAULT_TRIGGER_REEXECUTION = true;
    private Boolean m_triggerReExecution = DEFAULT_TRIGGER_REEXECUTION;

    private Boolean m_counting_helper_flag = false;

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
     * @return the triggerReExecution
     */
    public Boolean getTriggerReExecution() {
        return m_triggerReExecution;
    }

    /**
     *
     * @return random integer
     */
    public boolean getCountingHelperFlag() {
        return m_counting_helper_flag;
    }

    /**
     * toggle the helper flag
     */
    public void toggleCountingHelperFlag() {
        m_counting_helper_flag = !m_counting_helper_flag;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void saveSettings(final NodeSettingsWO settings) {
        settings.addString(CFG_LABEL, getLabel());
        settings.addString(CFG_DESCRIPTION, getDescription());
        settings.addString(CFG_BUTTON_TEXT, m_buttonText);
        settings.addBoolean(CFG_TRIGGER_REEXECUTION, m_triggerReExecution);
        settings.addBoolean(CFG_HIDE_IN_WIZARD, getHideInWizard());
        settings.addBoolean(CFG_COUNTING_HELPER_FLAG, m_counting_helper_flag);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
        m_buttonText = settings.getString(CFG_BUTTON_TEXT, DEFAULT_TEXT);
        setLabel(settings.getString(CFG_LABEL, DEFAULT_LABEL));
        setDescription(settings.getString(CFG_DESCRIPTION, DEFAULT_DESCRIPTION));
        m_triggerReExecution = settings.getBoolean(CFG_TRIGGER_REEXECUTION, DEFAULT_TRIGGER_REEXECUTION);
        m_counting_helper_flag = settings.getBoolean(CFG_COUNTING_HELPER_FLAG);

        // Needed as super is not called to have different defaults
        setHideInWizard(settings.getBoolean(CFG_HIDE_IN_WIZARD, DEFAULT_HIDE_IN_WIZARD));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadSettingsInDialog(final NodeSettingsRO settings) {
        super.loadSettingsInDialog(settings);
        try {
            loadSettings(settings);
        } catch (InvalidSettingsException e) {
            LOGGER.error("Refresh Button Widget node settings could not be loaded in configuration dialog.", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(RefreshButtonWidgetNodeConfig.class);
        sb.append(super.toString());
        sb.append(", buttonText=");
        sb.append(m_buttonText);
        sb.append(", triggerReExecution=");
        sb.append(m_triggerReExecution.toString());
        sb.append(", random_int");
        sb.append(m_counting_helper_flag.toString());
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
            .append(m_triggerReExecution)
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
            .append(m_triggerReExecution, other.m_triggerReExecution)
            .append(m_counting_helper_flag, other.m_counting_helper_flag)
            .isEquals();
    }
}
