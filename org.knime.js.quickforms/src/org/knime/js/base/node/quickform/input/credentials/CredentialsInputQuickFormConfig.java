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
 *   Jun 12, 2014 (winter): created
 */
package org.knime.js.base.node.quickform.input.credentials;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.js.base.node.quickform.QuickFormFlowVariableConfig;

/**
 * The config for the credentials input quick form node.
 *
 * @author Bernd Wiswedel, KNIME.com AG, Zurich, Switzerland
 */
public class CredentialsInputQuickFormConfig extends QuickFormFlowVariableConfig<CredentialsInputQuickFormValue> {

    private static final String CFG_ERROR_MESSAGE = "error_message";
    private static final String CFG_PROMPT_USER = "prompt_username";
    private static final String CFG_USE_SERVER_LOGIN = "use_server_login";
    private static final String CFG_NO_DISPLAY = "no_display";

    private static final String DEFAULT_ERROR_MESSAGE = "";
    private static final boolean DEFAULT_PROMPT_USER = true;
    private static final boolean DEFAULT_USE_SERVER_LOGIN_CREDENTIALS = false;
    private static final boolean DEFAULT_NO_DISPLAY = false;

    private String m_errorMessage = DEFAULT_ERROR_MESSAGE;
    private boolean m_promptUsername = DEFAULT_PROMPT_USER;
    private boolean m_useServerLoginCredentials = DEFAULT_USE_SERVER_LOGIN_CREDENTIALS;
    private boolean m_noDisplay = DEFAULT_NO_DISPLAY;

    /** @return the errorMessage */
    String getErrorMessage() {
        return m_errorMessage;
    }

    /** @param errorMessage the errorMessage to set */
    void setErrorMessage(final String errorMessage) {
        m_errorMessage = errorMessage;
    }

    /** @return the useServerLoginCredentials */
    boolean isUseServerLoginCredentials() {
        return m_useServerLoginCredentials;
    }

    /** @param useServerLoginCredentials the useServerLoginCredentials to set */
    void setUseServerLoginCredentials(final boolean useServerLoginCredentials) {
        m_useServerLoginCredentials = useServerLoginCredentials;
    }

    /** @return the promptUsername */
    boolean isPromptUsername() {
        return m_promptUsername;
    }

    /** @param promptUsername the promptUsername to set */
    void setPromptUsername(final boolean promptUsername) {
        m_promptUsername = promptUsername;
    }

    /**
     * @return the noDisplay
     */
    public boolean getNoDisplay() {
        return m_noDisplay;
    }

    /**
     * @param noDisplay the noDisplay to set
     */
    public void setNoDisplay(final boolean noDisplay) {
        m_noDisplay = noDisplay;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveSettings(final NodeSettingsWO settings) {
        settings.addString(CFG_ERROR_MESSAGE, m_errorMessage);
        settings.addBoolean(CFG_PROMPT_USER, m_promptUsername);
        settings.addBoolean(CFG_USE_SERVER_LOGIN, m_useServerLoginCredentials);

        //added with 3.4
        settings.addBoolean(CFG_NO_DISPLAY, m_noDisplay);

        super.saveSettings(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
        m_errorMessage = settings.getString(CFG_ERROR_MESSAGE);
        m_promptUsername = settings.getBoolean(CFG_PROMPT_USER);
        m_useServerLoginCredentials = settings.getBoolean(CFG_USE_SERVER_LOGIN);

        //added with 3.4
        m_noDisplay = settings.getBoolean(CFG_NO_DISPLAY, DEFAULT_NO_DISPLAY);

        super.loadSettings(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadSettingsInDialog(final NodeSettingsRO settings) {
        m_errorMessage = settings.getString(CFG_ERROR_MESSAGE, DEFAULT_ERROR_MESSAGE);
        m_promptUsername = settings.getBoolean(CFG_PROMPT_USER, DEFAULT_PROMPT_USER);
        m_useServerLoginCredentials = settings.getBoolean(CFG_USE_SERVER_LOGIN, DEFAULT_USE_SERVER_LOGIN_CREDENTIALS);

        //added with 3.4
        m_noDisplay = settings.getBoolean(CFG_NO_DISPLAY, DEFAULT_NO_DISPLAY);

        super.loadSettingsInDialog(settings);
    }

    /** {@inheritDoc} */
    @Override
    protected CredentialsInputQuickFormValue createEmptyValue() {
        return new CredentialsInputQuickFormValue();
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return new ToStringBuilder(this).appendSuper(super.toString())
                .append(CFG_PROMPT_USER, m_promptUsername)
                .append(CFG_USE_SERVER_LOGIN, m_useServerLoginCredentials)
                .append(CFG_NO_DISPLAY, m_noDisplay)
                .append(CFG_ERROR_MESSAGE, m_errorMessage).toString();
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        return new HashCodeBuilder().appendSuper(super.hashCode())
                .append(m_promptUsername)
                .append(m_useServerLoginCredentials)
                .append(m_errorMessage)
                .append(m_noDisplay)
                .toHashCode();
    }

    /** {@inheritDoc} */
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
        CredentialsInputQuickFormConfig other = (CredentialsInputQuickFormConfig)obj;
        return new EqualsBuilder().appendSuper(super.equals(obj))
                .append(m_promptUsername, other.m_promptUsername)
                .append(m_useServerLoginCredentials, other.m_useServerLoginCredentials)
                .append(m_errorMessage, other.m_errorMessage)
                .append(m_noDisplay, other.m_noDisplay)
                .isEquals();
    }

}
