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

import static org.knime.core.node.workflow.VariableType.CredentialsType.CFG_IS_CREDENTIALS_FLAG;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettings;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.workflow.VariableType.CredentialsType;
import org.knime.core.util.CoreConstants;
import org.knime.js.core.JSONViewContent;
import org.knime.js.core.StringSanitizationSerializer.JsonSanitizeIgnore;

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

    private static final String WEAK_ENCRYPTION_PASSWORD = "SomeWeakEncryption#Password";

    /**
     * Key used since 5.2 as parent key for credentials settings.
     */
    public static final String CFG_CREDENTIALS_VALUE_PARENT = "credentialsValue";

    public static final String CFG_USERNAME = "username";

    public static final String CFG_PASSWORD = "password";

    public static final String CFG_SAVE_PASSWORD = "isSavePassword";

    protected static final String CFG_PASSWORD_ENCRYPTED = "passwordEncrypted";

    private String m_username;

    private String m_password;

    private boolean m_isSavePassword;

    /*
     * This flag was introduced in AP-22015. It is there to restore backwards compatibility. Prior 5.2, username and
     * password were saved in the top-level node settings and could be overridden by plain String flow variables. In
     * 5.2.0 and 5.2.1, username and password were saved in the node subsettings CFG_CREDENTIALS_VALUE_PARENT and could
     * be overridden only (!) by credentials flow variables. This broke workflows in which username and/or password were
     * overridden with plain String flow variables. In 5.2.2 / 5.3 and later, when loading from node settings, we check
     * whether the node was once saved prior 5.2 (by checking whether the username is found in the top-level node
     * settings). If so, this flag is set to true and when saving node settings again, we continue saving username /
     * password as we did prior 5.2.
     */
    private boolean m_savedPrior52;

    /** @param string the string to set */
    @JsonProperty(CFG_USERNAME)
    public void setUsername(final String string) {
        m_username = string;
    }

    /** @return the string */
    @JsonProperty(CFG_USERNAME)
    @JsonSanitizeIgnore
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
        return StringUtils.isEmpty(m_password) ? null : CoreConstants.MAGIC_DEFAULT_PASSWORD;
    }

    /** @param magicPassword the magicPassword to set */
    @JsonProperty("magicDefaultPassword")
    @JsonView(CoreConstants.ArtifactsView.class)
    private void setMagicPassword(final String magicPassword) {
        // In case the MAGIC_DEFAULT_PASSWORD is returned, the existing password is not overridden.
        // In any other case it is, so an existing password can also be reset.
        if (!StringUtils.equals(magicPassword, CoreConstants.MAGIC_DEFAULT_PASSWORD)) {
            m_password = StringUtils.isEmpty(magicPassword) ? null : magicPassword;
        }
    }

    /**
     * Save username and password in the same way as prior to KNIME AP version 5.2.
     *
     * @param settings the node settings to save to
     * @param username the user name to save
     * @param password the password to save
     * @param isSavePassword whether to save the password weakly encrypted to the node settings
     */
    @SuppressWarnings("java:S2301")
    public static void saveUsernameAndPasswordToNodeSettingsPrior52(final NodeSettingsWO settings,
        final String username, final String password, final boolean isSavePassword) {
        settings.addString(CFG_USERNAME, username);
        if (isSavePassword) {
            settings.addPassword(CFG_PASSWORD_ENCRYPTED, WEAK_ENCRYPTION_PASSWORD, password);
        } else {
            settings.addTransientString(CFG_PASSWORD, password);
        }
    }

    /**
     * Save username and password in the same way as since KNIME AP version 5.2.
     *
     * @param settings the node settings to save to
     * @param username the user name to save
     * @param password the password to save
     * @param isSavePassword whether to save the password weakly encrypted to the node settings
     */
    @SuppressWarnings("java:S2301")
    public static void saveUsernameAndPasswordToNodeSettingsSince52(final NodeSettingsWO settings,
        final String username, final String password, final boolean isSavePassword) {
        // added in 5.2 (AP-19913): This section in the settings needs to follow a certain schema in order
        // to be configurable by credentials flow variable. The schema implementation is copied (so needs/has
        // good test coverage) since we believe this piece of code will eventually be removed when nodes are
        // converted to Modern UI / org.knime.core.webui.node.dialog.defaultdialog.setting.credentials.Credentials)
        // see also org.knime.core.node.workflow.VariableType.CredentialsType.canOverwrite(Config, String)
        final var cValueSet = settings.addNodeSettings(CFG_CREDENTIALS_VALUE_PARENT);
        // only to comply to schema for variable type detection (value doesn't matter)
        cValueSet.addBoolean(CFG_IS_CREDENTIALS_FLAG, true);
        cValueSet.addString(CredentialsType.CFG_NAME, ""); // not used
        cValueSet.addString(CredentialsType.CFG_LOGIN, username);
        cValueSet.addTransientString(CredentialsType.CFG_TRANSIENT_PASSWORD, password);
        cValueSet.addTransientString(CredentialsType.CFG_TRANSIENT_SECOND_FACTOR, null); // unused
        if (isSavePassword) {
            cValueSet.addPassword(CFG_PASSWORD_ENCRYPTED, WEAK_ENCRYPTION_PASSWORD, password);
        }
    }

    /**
     * Save the isSavePassword setting to the settings
     *
     * @param settings the settings to write to
     * @param isSavePassword whether to save the password weakly encrypted or not at all
     */
    public static void saveIsSavePasswordToNodeSettings(final NodeSettingsWO settings, final boolean isSavePassword) {
        settings.addBoolean(CFG_SAVE_PASSWORD, isSavePassword);
    }

    @Override
    @JsonIgnore
    public void saveToNodeSettings(final NodeSettingsWO settings) {
        saveIsSavePasswordToNodeSettings(settings, m_isSavePassword);
        if (m_savedPrior52) {
            saveUsernameAndPasswordToNodeSettingsPrior52(settings, getUsername(), getPassword(), m_isSavePassword);
        } else {
            saveUsernameAndPasswordToNodeSettingsSince52(settings, getUsername(), getPassword(), m_isSavePassword);
        }
    }

    /**
     * Utility record to combine a username and its password.
     *
     * @param username a simple username
     * @param password a corresponding password
     */
    public record UsernamePassword(String username, String password) {
    }

    /**
     * Load the username and password in the same way as prior to KNIME AP version 5.2.
     *
     * @param settings the settings to load from
     * @param isSavePassword whether the password is contained in the settings or not
     * @return the loaded username & password
     */
    @SuppressWarnings("java:S2301")
    public static UsernamePassword loadUsernameAndPasswordFromNodeSettingsInDialogPrior52(
        final NodeSettingsRO settings, final boolean isSavePassword) {
        return new UsernamePassword( //
            settings.getString(CFG_USERNAME, System.getProperty("user.name")), //
            isSavePassword ? settings.getPassword(CFG_PASSWORD_ENCRYPTED, WEAK_ENCRYPTION_PASSWORD, "")
                : settings.getTransientString(CFG_PASSWORD));
    }

    /**
     * Load the username and password in the same way as since KNIME AP version 5.2.
     *
     * @param settings the settings to load from
     * @param isSavePassword whether the password is contained in the settings or not
     * @return the loaded username & password
     */
    @SuppressWarnings("java:S2301")
    public static UsernamePassword loadUsernameAndPasswordFromNodeSettingsInDialogSince52(
        final NodeSettingsRO settings, final boolean isSavePassword) {
        NodeSettingsRO cValueSettings;
        try {
            cValueSettings = settings.getNodeSettings(CFG_CREDENTIALS_VALUE_PARENT);
        } catch (InvalidSettingsException ise) { // NOSONAR
            cValueSettings = new NodeSettings("empty");
        }

        String password;
        if (cValueSettings.containsKey(CredentialsType.CFG_TRANSIENT_PASSWORD)) {
            password = cValueSettings.getTransientString(CredentialsType.CFG_TRANSIENT_PASSWORD);
        } else if (isSavePassword) {
            password = cValueSettings.getPassword(CFG_PASSWORD_ENCRYPTED, WEAK_ENCRYPTION_PASSWORD, "");
        } else {
            password = null;
        }

        return new UsernamePassword( //
            cValueSettings.getString(CredentialsType.CFG_LOGIN, System.getProperty("user.name")), //
            password);
    }

    /**
     * Load the isSavePassword setting
     *
     * @param settings the settings to load from
     * @return whether to save the password weakly encrypted or not at all
     */
    public static boolean loadIsSavePasswordFromNodeSettingsInDialog(final NodeSettingsRO settings) {
        return settings.getBoolean(CFG_SAVE_PASSWORD, false);
    }

    /**
     * Fail save settings loading, called from dialog code.
     *
     * @param settings To load from, not null.
     */
    @JsonIgnore
    public void loadFromNodeSettingsInDialog(final NodeSettingsRO settings) {
        final UsernamePassword usernamePassword;
        final var isSavePassword = loadIsSavePasswordFromNodeSettingsInDialog(settings);
        // saved with 5.2 or after
        if (settings.containsKey(CFG_CREDENTIALS_VALUE_PARENT)) {
            usernamePassword = loadUsernameAndPasswordFromNodeSettingsInDialogSince52(settings, isSavePassword);
        } else {
            usernamePassword = loadUsernameAndPasswordFromNodeSettingsInDialogPrior52(settings, isSavePassword);
            m_savedPrior52 = true;
        }
        setSavePassword(isSavePassword);
        setUsername(usernamePassword.username());
        setPassword(usernamePassword.password());
    }

    @Override
    @JsonIgnore
    public void loadFromNodeSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
        final String username;
        final String password;
        final var isSavePassword = settings.getBoolean(CFG_SAVE_PASSWORD);
        // saved with 5.2 or after
        if (settings.containsKey(CFG_CREDENTIALS_VALUE_PARENT)) {
            final var cValueSettings = settings.getNodeSettings(CFG_CREDENTIALS_VALUE_PARENT);
            username = cValueSettings.getString(CredentialsType.CFG_LOGIN);
            // overwritten by variable or used between model and dialog (not persisted)
            if (cValueSettings.containsKey(CredentialsType.CFG_TRANSIENT_PASSWORD)) {
                password = cValueSettings.getTransientString(CredentialsType.CFG_TRANSIENT_PASSWORD);
            } else if (isSavePassword) {
                password = cValueSettings.getPassword(CFG_PASSWORD_ENCRYPTED, WEAK_ENCRYPTION_PASSWORD);
            } else {
                password = null;
            }
        } else {
            username = settings.getString(CFG_USERNAME);
            password = isSavePassword ? settings.getPassword(CFG_PASSWORD_ENCRYPTED, WEAK_ENCRYPTION_PASSWORD)
                : settings.getTransientString(CFG_PASSWORD);
            m_savedPrior52 = true;
        }
        setSavePassword(isSavePassword);
        setUsername(username);
        setPassword(password);
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
        return new HashCodeBuilder()
            .append(m_username)
            .append(m_password)
            .append(m_isSavePassword)
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
        CredentialsNodeValue other = (CredentialsNodeValue)obj;
        return new EqualsBuilder()
            .append(m_username, other.m_username)
            .append(m_password, other.m_password)
            .append(m_isSavePassword, other.m_isSavePassword)
            .isEquals();
    }
}
