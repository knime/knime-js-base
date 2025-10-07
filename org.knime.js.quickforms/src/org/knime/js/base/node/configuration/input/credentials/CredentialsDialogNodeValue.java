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

import static org.knime.js.base.node.configuration.input.credentials.CredentialsDialogValueWebUICredentialsTransformerUtil.getWebUICredentials;
import static org.knime.js.base.node.configuration.input.credentials.CredentialsDialogValueWebUICredentialsTransformerUtil.setWebUICredentials;

import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.util.CoreConstants;
import org.knime.core.util.JsonUtil;
import org.knime.core.webui.node.dialog.WebDialogValue;
import org.knime.core.webui.node.dialog.defaultdialog.jsonforms.JsonFormsDataUtil;
import org.knime.js.base.node.base.input.credentials.CredentialsNodeValue;
import org.knime.node.parameters.widget.credentials.Credentials;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import jakarta.json.JsonException;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonValue;

/**
 * The config for the credentials configuration node.
 *
 * @author Daniel Bogenrieder, KNIME GmbH, Konstanz, Germany
 */
@SuppressWarnings("restriction")
public class CredentialsDialogNodeValue extends CredentialsNodeValue implements WebDialogValue {

    public static final String USE_SERVER_CREDENTIALS = "useServerLoginCredentials";

    private boolean m_useServerCredentials;

    /**
     * Sets if the server credentials used be used.
     *
     * @param serverCredentials <code>true</code> if the server credentials should be used, <code>false</code> otherwise
     */
    public void setUseServerCredentials(final boolean serverCredentials) {
        m_useServerCredentials = serverCredentials;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveToNodeSettings(final NodeSettingsWO settings) {
        super.saveToNodeSettings(settings);
        settings.addBoolean(USE_SERVER_CREDENTIALS, m_useServerCredentials);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @JsonIgnore
    public void loadFromNodeSettingsInDialog(final NodeSettingsRO settings) {
        super.loadFromNodeSettingsInDialog(settings);
        m_useServerCredentials = settings.getBoolean(USE_SERVER_CREDENTIALS, false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadFromNodeSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
        super.loadFromNodeSettings(settings);

        m_useServerCredentials = settings.getBoolean(USE_SERVER_CREDENTIALS, false);
    }

    /** {@inheritDoc} */
    @Override
    public void loadFromString(final String fromCmdLine) {
        int colonIndex = fromCmdLine.indexOf(':');
        String m_username;
        String m_password;
        if (colonIndex < 0) {
            m_username = fromCmdLine;
            m_password = null;
        } else {
            m_username = fromCmdLine.substring(0, colonIndex);
            m_password = fromCmdLine.substring(Math.min(colonIndex + 1, fromCmdLine.length()));
        }
        setPassword(m_password);
        setUsername(m_username);
    }

    /** {@inheritDoc} */
    @Override
    public void loadFromJson(final JsonValue jsonValue) throws JsonException {
        String m_username;
        String m_password;
        if (!(jsonValue instanceof JsonObject)) {
            throw new JsonException(String.format("Expected 'JSONObject' but got '%s%': %s",
                jsonValue.getClass().getSimpleName(), jsonValue));
        }
        JsonObject json = (JsonObject)jsonValue;
        try {
            JsonValue valUser = json.get(CFG_USERNAME);
            m_username = JsonValue.NULL.equals(valUser) ? null : json.getString(CFG_USERNAME);
            JsonValue valPass = json.get(CFG_PASSWORD);
            m_password = JsonValue.NULL.equals(valPass) ? null : json.getString(CFG_PASSWORD);
        } catch (Exception e) {
            throw new JsonException("Expected string value for key '" + CFG_USERNAME + "' and '" + CFG_PASSWORD + "'",
                e);
        }
        setUsername(m_username);
        setPassword(m_password);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JsonValue toJson() {
        final JsonObjectBuilder builder = JsonUtil.getProvider().createObjectBuilder();
        final JsonObjectBuilder subBuilder = JsonUtil.getProvider().createObjectBuilder();
        builder.add("type", "object");
        subBuilder.add("type", "string");
        if (m_useServerCredentials) {
            subBuilder.add("default", "<logged.in.user>");
        } else if (getUsername() == null) {
            subBuilder.addNull("default");
        } else {
            subBuilder.add("default", getUsername());
        }
        builder.add(CFG_USERNAME, subBuilder.build());
        subBuilder.add("type", "string");

        if (!m_useServerCredentials && (StringUtils.isEmpty(getPassword()) || !isSavePassword())) {
            subBuilder.addNull("default");
        } else {
            subBuilder.add("default", CoreConstants.MAGIC_DEFAULT_PASSWORD);
        }

        builder.add(CFG_PASSWORD, subBuilder.build());
        return builder.build();
    }

    static final String DIALOG_JSON_CREDENTIALS_PARENT = "credentials";

    static final String DIALOG_JSON_USERNAME_BEFORE_5_2 = "usernameBefore52";

    static final String DIALOG_JSON_PASSWORD_BEFORE_5_2 = "passwordBefore52";

    @Override
    public JsonNode toDialogJson() throws IOException {
        final var mapper = JsonFormsDataUtil.getMapper();
        final var credentials = getWebUICredentials(this);

        if (isSavedPrior52()) {
            return mapper.createObjectNode()//
                .put(DIALOG_JSON_USERNAME_BEFORE_5_2, getUsername())//
                .set(DIALOG_JSON_PASSWORD_BEFORE_5_2, mapper.valueToTree(credentials));
        }

        return mapper.createObjectNode().set(DIALOG_JSON_CREDENTIALS_PARENT, mapper.valueToTree(credentials));
    }

    @Override
    public void fromDialogJson(final JsonNode json) throws IOException {
        final var isSavedPrior52 = !json.has(DIALOG_JSON_CREDENTIALS_PARENT);
        if (isSavedPrior52) {
            final var username = json.get(DIALOG_JSON_USERNAME_BEFORE_5_2).asText(null);
            final var passwordJson = json.get(DIALOG_JSON_PASSWORD_BEFORE_5_2);
            final var mapper = JsonFormsDataUtil.getMapper();
            final var credentials = mapper.treeToValue(passwordJson, Credentials.class);
            setUsername(username);
            setPassword(credentials.getPassword());
            setSavedPrior52(true);
            return;
        }

        final var credentialsJson = ((ObjectNode)json).get(DIALOG_JSON_CREDENTIALS_PARENT);
        final var mapper = JsonFormsDataUtil.getMapper();
        final var credentials = mapper.treeToValue(credentialsJson, Credentials.class);
        setWebUICredentials(this, credentials);
    }

}
