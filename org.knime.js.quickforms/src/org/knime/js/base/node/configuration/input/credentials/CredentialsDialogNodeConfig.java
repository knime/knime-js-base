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
 *   May 29, 2019 (Daniel Bogenrieder): created
 */
package org.knime.js.base.node.configuration.input.credentials;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.js.base.node.base.input.credentials.CredentialsNodeConfig;
import org.knime.js.base.node.configuration.LabeledFlowVariableDialogNodeConfig;

/**
 * The config for the credentials configuration node.
 *
 * @author Daniel Bogenrieder, KNIME GmbH, Konstanz, Germany
 */
public class CredentialsDialogNodeConfig extends LabeledFlowVariableDialogNodeConfig<CredentialsDialogNodeValue> {

    private final CredentialsNodeConfig m_credentialsConfig;

    /**
     * Instantiate a new config object
     */
    public CredentialsDialogNodeConfig() {
        m_credentialsConfig = new CredentialsNodeConfig();
    }

    /**
     * @return the stringConfig
     */
    public CredentialsNodeConfig getCredentialsConfig() {
        return m_credentialsConfig;
    }

    /** @return the errorMessage */
    public String getErrorMessage() {
        return m_credentialsConfig.getErrorMessage();
    }

    /** @param errorMessage the errorMessage to set */
    public void setErrorMessage(final String errorMessage) {
        m_credentialsConfig.setErrorMessage(errorMessage);
    }

    /** @return the useServerLoginCredentials */
    public boolean isUseServerLoginCredentials() {
        return m_credentialsConfig.isUseServerLoginCredentials();
    }

    /** @param useServerLoginCredentials the useServerLoginCredentials to set */
    public void setUseServerLoginCredentials(final boolean useServerLoginCredentials) {
        m_credentialsConfig.setUseServerLoginCredentials(useServerLoginCredentials);
    }

    /** @return the promptUsername */
    public boolean isPromptUsername() {
        return m_credentialsConfig.isPromptUsername();
    }

    /** @param promptUsername the promptUsername to set */
    public void setPromptUsername(final boolean promptUsername) {
        m_credentialsConfig.setPromptUsername(promptUsername);
    }

    /**
     * @return the noDisplay
     */
    public boolean getNoDisplay() {
        return m_credentialsConfig.getNoDisplay();
    }

    /**
     * @param noDisplay the noDisplay to set
     */
    public void setNoDisplay(final boolean noDisplay) {
        m_credentialsConfig.setNoDisplay(noDisplay);
    }

    /**
     * @return the usernameLabel
     */
    public String getUsernameLabel() {
        return m_credentialsConfig.getUsernameLabel();
    }

    /**
     * @return the passwordLabel
     */
    public String getPasswordLabel() {
        return m_credentialsConfig.getPasswordLabel();
    }

    /**
     * @param usernameLabel to set
     */
    public void setUsernameLabel(final String usernameLabel) {
        m_credentialsConfig.setUsernameLabel(usernameLabel);
    }

    /**
     * @param passwordLabel to set
     */
    public void setPasswordLabel(final String passwordLabel) {
        m_credentialsConfig.setPasswordLabel(passwordLabel);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected CredentialsDialogNodeValue createEmptyValue() {
        return new CredentialsDialogNodeValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveSettings(final NodeSettingsWO settings) {
        super.saveSettings(settings);
        m_credentialsConfig.saveSettings(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
        super.loadSettings(settings);
        m_credentialsConfig.loadSettings(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadSettingsInDialog(final NodeSettingsRO settings) {
        super.loadSettingsInDialog(settings);
        m_credentialsConfig.loadSettingsInDialog(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString());
        sb.append(", ");
        sb.append(m_credentialsConfig.toString());
        return sb.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder()
            .appendSuper(super.hashCode())
            .append(m_credentialsConfig)
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
        CredentialsDialogNodeConfig other = (CredentialsDialogNodeConfig)obj;
        return new EqualsBuilder()
            .appendSuper(super.equals(obj))
            .append(m_credentialsConfig, other.m_credentialsConfig)
            .isEquals();
    }

}
