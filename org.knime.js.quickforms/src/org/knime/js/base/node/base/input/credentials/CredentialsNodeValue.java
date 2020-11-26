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
package org.knime.js.base.node.base.input.credentials;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.util.CoreConstants;
import org.knime.js.core.JSONViewContent;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonView;

/**
 * The base value for the credentials configuration and widget node
 *
 * @author Daniel Bogenrieder, KNIME GmbH, Konstanz, Germany
 */
@JsonAutoDetect
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public class CredentialsNodeValue extends JSONViewContent {

    protected static final String CFG_USERNAME = "username";

    protected static final String CFG_PASSWORD = "password";

    protected static final String CFG_SAVE_PASSWORD = "isSavePassword";

    protected static final String CFG_PASSWORD_ENCRYPTED = "passwordEncrypted";

    private String m_username;

    // since 4.3
    private boolean hasArtifactsView = true;

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean getHasArtifactsView() {
        return hasArtifactsView;
    }

    private String m_password;

    private boolean m_isSavePassword;

    /** @param string the string to set */
    @JsonProperty(CFG_USERNAME)
    public void setUsername(final String string) {
        m_username = string;
    }

    /** @return the string */
    @JsonProperty(CFG_USERNAME)
    public String getUsername() {
        return m_username;
    }

    /** @param isSavePassword the property to set */
    @JsonProperty(CFG_SAVE_PASSWORD)
    public void setSavePassword(final boolean isSavePassword) {
        m_isSavePassword = isSavePassword;
    }

    /** @return the is-save-password property */
    @JsonProperty(CFG_SAVE_PASSWORD)
    public boolean isSavePassword() {
        return m_isSavePassword;
    }

    /** @param password the password to set */
    @JsonProperty(CFG_PASSWORD)
    public void setPassword(final String password) {
        m_password = password;
    }

    /** @return the password */
    @JsonProperty(CFG_PASSWORD)
    @JsonView(CoreConstants.DefaultView.class)
    public String getPassword() {
        return m_password;
    }

    /** @return the password */
    @JsonProperty("magicDefaultPassword")
    @JsonView(CoreConstants.ArtifactsView.class)
    private String getMagicPassword() {
        return StringUtils.isEmpty(m_password) || !m_isSavePassword ? null : CoreConstants.MAGIC_DEFAULT_PASSWORD;
    }

    /** @param magicPassword the magicPassword to set */
    @JsonProperty("magicDefaultPassword")
    @JsonView(CoreConstants.ArtifactsView.class)
    private void setMagicPassword(final String magicPassword) {
        if(StringUtils.isEmpty(m_password) && !StringUtils.equals(magicPassword, CoreConstants.MAGIC_DEFAULT_PASSWORD)) {
            m_password = StringUtils.isEmpty(magicPassword) ? null : magicPassword;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @JsonIgnore
    public void saveToNodeSettings(final NodeSettingsWO settings) {
        settings.addString(CFG_USERNAME, getUsername());
        settings.addBoolean(CFG_SAVE_PASSWORD, m_isSavePassword);
        if (m_isSavePassword) {
            settings.addPassword(CFG_PASSWORD_ENCRYPTED, "SomeWeakEncryption#Password", getPassword());
        } else {
            settings.addTransientString(CFG_PASSWORD, getPassword());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @JsonIgnore
    public void loadFromNodeSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
        setUsername(settings.getString(CFG_USERNAME));
        setSavePassword(settings.getBoolean(CFG_SAVE_PASSWORD));
        if (m_isSavePassword) {
            setPassword(settings.getPassword(CFG_PASSWORD_ENCRYPTED, "SomeWeakEncryption#Password"));
        } else {
            setPassword(settings.getTransientString(CFG_PASSWORD));
        }
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("username=");
        sb.append(m_username);
        sb.append(",password=");
        sb.append(StringUtils.isEmpty(m_password) ? "<not set>" : "xxxx");
        sb.append(",save-password=");
        sb.append(m_isSavePassword);
        return sb.toString();
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(m_username).append(m_password).append(m_isSavePassword).toHashCode();
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
        CredentialsNodeValue other = (CredentialsNodeValue)obj;
        return new EqualsBuilder().append(m_username, other.m_username).append(m_password, other.m_password)
            .append(m_isSavePassword, other.m_isSavePassword).isEquals();
    }
}
