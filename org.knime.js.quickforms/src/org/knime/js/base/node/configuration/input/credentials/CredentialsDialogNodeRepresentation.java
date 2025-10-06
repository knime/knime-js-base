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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

import org.knime.core.node.dialog.DialogNodePanel;
import org.knime.core.node.dialog.SubNodeDescriptionProvider;
import org.knime.core.webui.node.dialog.PersistSchema;
import org.knime.core.webui.node.dialog.WebDialogNodeRepresentation.DefaultWebDialogNodeRepresentation;
import org.knime.core.webui.node.dialog.defaultdialog.jsonforms.renderers.CredentialsRendererSpec;
import org.knime.core.webui.node.dialog.defaultdialog.jsonforms.renderers.DialogElementRendererSpec;
import org.knime.core.webui.node.dialog.defaultdialog.jsonforms.renderers.TextRendererSpec;
import org.knime.js.base.node.base.input.credentials.CredentialsNodeRepresentation;
import org.knime.js.base.node.base.input.credentials.CredentialsNodeValue;
import org.knime.js.base.node.configuration.renderers.CredentialsRenderer;
import org.knime.js.base.node.configuration.renderers.LabeledGroupRenderer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The dialog representation of the credentials configuration node
 *
 * @author Daniel Bogenrieder, KNIME GmbH, Konstanz, Germany
 */
public class CredentialsDialogNodeRepresentation extends CredentialsNodeRepresentation<CredentialsDialogNodeValue>
    implements SubNodeDescriptionProvider<CredentialsDialogNodeValue>,
    DefaultWebDialogNodeRepresentation<CredentialsDialogNodeValue> {

    @JsonCreator
    private CredentialsDialogNodeRepresentation(@JsonProperty("label") final String label,
        @JsonProperty("description") final String description, @JsonProperty("required") final boolean required,
        @JsonProperty("defaultValue") final CredentialsDialogNodeValue defaultValue,
        @JsonProperty("currentValue") final CredentialsDialogNodeValue currentValue,
        @JsonProperty("promptUsername") final boolean promptUsername,
        @JsonProperty("usernameLabel") final String usernameLabel,
        @JsonProperty("passwordLabel") final String passwordLabel,
        @JsonProperty("useServerLoginCredentials") final boolean useServerLoginCredentials,
        @JsonProperty("errorMessage") final String errorMessage, @JsonProperty("noDisplay") final boolean noDisplay) {
        super(label, description, required, defaultValue, currentValue, promptUsername, useServerLoginCredentials,
            usernameLabel, passwordLabel, errorMessage, noDisplay);
    }

    /**
     * @param currentValue The value currently used by the node
     * @param config The config of the node
     * @param usernameLabel The configured label for username
     * @param passwordLabel The configured label for password
     */
    public CredentialsDialogNodeRepresentation(final CredentialsDialogNodeValue currentValue,
        final CredentialsDialogNodeConfig config, final String usernameLabel, final String passwordLabel) {
        super(currentValue, config.getDefaultValue(), config.getCredentialsConfig(), config.getLabelConfig(),
            usernameLabel, passwordLabel);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DialogNodePanel<CredentialsDialogNodeValue> createDialogPanel() {
        return new CredentialsConfigurationPanel(this);
    }

    @Override
    public DialogElementRendererSpec getWebUIDialogElementRendererSpec() {
        if (isSavedPrior52()) {
            final Collection<DialogElementRendererSpec> subRenderers = new ArrayList<>();
            if (isPromptUsername()) {
                subRenderers.add(new LegacyUsernameRenderer(getUsernameLabel())
                    .at(CredentialsDialogNodeValue.DIALOG_JSON_USERNAME_BEFORE_5_2));
            }
            subRenderers.add(new LegacyPasswordRenderer(getPasswordLabel())
                .at(CredentialsDialogNodeValue.DIALOG_JSON_PASSWORD_BEFORE_5_2));
            return new LabeledGroupRenderer(this, subRenderers);

        }
        return new CredentialsRenderer(this).at(CredentialsDialogNodeValue.DIALOG_JSON_CREDENTIALS_PARENT);
    }

    private boolean isSavedPrior52() {
        final var referenceValue = Optional.ofNullable(getCurrentValue()).orElseGet(this::getDefaultValue);
        return referenceValue.isSavedPrior52();
    }

    static class LegacyUsernameRenderer implements TextRendererSpec {

        private final String m_title;

        LegacyUsernameRenderer(final String title) {
            m_title = title;
        }

        @Override
        public String getTitle() {
            return m_title;
        }

    }

    static class LegacyPasswordRenderer implements CredentialsRendererSpec {

        private final String m_title;

        LegacyPasswordRenderer(final String title) {
            m_title = title;
        }

        @Override
        public String getTitle() {
            return m_title;
        }

        @Override
        public Optional<CredentialsRendererOptions> getOptions() {
            return Optional.of(new CredentialsRendererOptions() {

                @Override
                public Optional<Boolean> getHasUsername() {
                    return Optional.of(false);
                }

            });
        }

    }

    @Override
    public Optional<PersistSchema> getPersistSchema() {

        if (isSavedPrior52()) {
            return Optional.of(new PersistSchema.PersistTreeSchema.PersistTreeSchemaRecord(Map
                .of(CredentialsDialogNodeValue.DIALOG_JSON_USERNAME_BEFORE_5_2, new PersistSchema.PersistLeafSchema() {
                    @Override
                    public Optional<String> getConfigKey() {
                        return Optional.of(CredentialsNodeValue.CFG_USERNAME);
                    }

                }, CredentialsDialogNodeValue.DIALOG_JSON_PASSWORD_BEFORE_5_2, new PersistSchema.PersistLeafSchema() {
                    @Override
                    public Optional<String[][]> getConfigPaths() {
                        /**
                         * We disallow setting the password here since it is not always saved and it is impossible to
                         * set it since it is encrypted anyway.
                         */
                        return Optional.of(new String[0][]);
                    }

                })));
        }

        return Optional.of(new PersistSchema.PersistTreeSchema.PersistTreeSchemaRecord(
            Map.of(CredentialsDialogNodeValue.DIALOG_JSON_CREDENTIALS_PARENT, new PersistSchema.PersistLeafSchema() {
                @Override
                public Optional<String> getConfigKey() {
                    return Optional.of(CredentialsNodeValue.CFG_CREDENTIALS_VALUE_PARENT);
                }

            })));
    }

}
