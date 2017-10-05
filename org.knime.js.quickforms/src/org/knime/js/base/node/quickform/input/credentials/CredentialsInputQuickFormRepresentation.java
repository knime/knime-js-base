/*
 * ------------------------------------------------------------------------
 *  Copyright by KNIME GmbH, Konstanz, Germany
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
 * ------------------------------------------------------------------------
 */
package org.knime.js.base.node.quickform.input.credentials;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.knime.core.node.dialog.DialogNodePanel;
import org.knime.js.base.node.quickform.QuickFormRepresentationImpl;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * The representation for the credentials input quick form node.
 *
 * @author Bernd Wiswedel, KNIME.com AG, Zurich, Switzerland
 */
@JsonAutoDetect
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public class CredentialsInputQuickFormRepresentation extends
    QuickFormRepresentationImpl<CredentialsInputQuickFormValue, CredentialsInputQuickFormConfig> {

    private final boolean m_promptUsername;
    private final boolean m_useServerLoginCredentials;
    private final String m_errorMessage;
    private final boolean m_noDisplay;

    /**
     * @param currentValue The value currently used by the node
     * @param config The config of the node
     */
    public CredentialsInputQuickFormRepresentation(final CredentialsInputQuickFormValue currentValue,
        final CredentialsInputQuickFormConfig config) {
        super(currentValue, config);
        m_promptUsername = config.isPromptUsername();
        m_useServerLoginCredentials = config.isUseServerLoginCredentials();
        m_errorMessage = config.getErrorMessage();
        m_noDisplay = config.getNoDisplay();
    }


    /** @return the errorMessage */
    @JsonProperty("errorMessage")
    public String getErrorMessage() {
        return m_errorMessage;
    }

    /** @return the promptUsername */
    @JsonProperty("promptUsername")
    boolean isPromptUsername() {
        return m_promptUsername;
    }

    /** @return the useServerLoginCredentials */
    @JsonProperty("useServerLoginCredentials")
    boolean isUseServerLoginCredentials() {
        return m_useServerLoginCredentials;
    }

    /** @return the noDisplay */
    @JsonProperty("noDisplay")
    public boolean isNoDisplay() {
        return m_noDisplay;
    }

    /** {@inheritDoc} */
    @Override
    @JsonIgnore
    public DialogNodePanel<CredentialsInputQuickFormValue> createDialogPanel() {
        CredentialsInputQuickFormDialogPanel panel = new CredentialsInputQuickFormDialogPanel(this);
        fillDialogPanel(panel);
        return panel;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString());
        sb.append(", promptUsername=");
        sb.append(m_promptUsername);
        sb.append(", useServerLoginCredentials=");
        sb.append(m_useServerLoginCredentials);
        sb.append(", noDisplay=");
        sb.append(m_noDisplay);
        sb.append(", errorMessage=");
        sb.append(m_errorMessage);
        return sb.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder().appendSuper(super.hashCode())
                .append(m_promptUsername)
                .append(m_useServerLoginCredentials)
                .append(m_noDisplay)
                .append(m_errorMessage)
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
        CredentialsInputQuickFormRepresentation other = (CredentialsInputQuickFormRepresentation)obj;
        return new EqualsBuilder().appendSuper(super.equals(obj))
                .append(m_promptUsername, other.m_promptUsername)
                .append(m_useServerLoginCredentials, other.m_useServerLoginCredentials)
                .append(m_noDisplay, other.m_noDisplay)
                .append(m_errorMessage, other.m_errorMessage)
                .isEquals();
    }

}
