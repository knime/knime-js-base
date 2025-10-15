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
 *   1 Oct 2025 (GitHub Copilot): created
 */
package org.knime.js.base.node.configuration.input.credentials;

import java.util.function.Supplier;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.webui.node.dialog.defaultdialog.util.updates.StateComputationFailureException;
import org.knime.js.base.node.base.input.credentials.CredentialsNodeConfig;
import org.knime.js.base.node.base.input.credentials.CredentialsNodeValue;
import org.knime.js.base.node.configuration.ConfigurationNodeSettings;
import org.knime.js.base.node.configuration.OverwrittenByValueMessage;
import org.knime.node.parameters.NodeParameters;
import org.knime.node.parameters.NodeParametersInput;
import org.knime.node.parameters.Widget;
import org.knime.node.parameters.layout.Layout;
import org.knime.node.parameters.migration.LoadDefaultsForAbsentFields;
import org.knime.node.parameters.persistence.NodeParametersPersistor;
import org.knime.node.parameters.persistence.Persist;
import org.knime.node.parameters.persistence.Persistor;
import org.knime.node.parameters.persistence.legacy.EnumBooleanPersistor;
import org.knime.node.parameters.updates.Effect;
import org.knime.node.parameters.updates.Effect.EffectType;
import org.knime.node.parameters.updates.EffectPredicate;
import org.knime.node.parameters.updates.EffectPredicateProvider;
import org.knime.node.parameters.updates.ParameterReference;
import org.knime.node.parameters.updates.StateProvider;
import org.knime.node.parameters.updates.ValueProvider;
import org.knime.node.parameters.updates.ValueReference;
import org.knime.node.parameters.widget.choices.Label;
import org.knime.node.parameters.widget.choices.ValueSwitchWidget;
import org.knime.node.parameters.widget.credentials.Credentials;
import org.knime.node.parameters.widget.credentials.CredentialsWidget;
import org.knime.node.parameters.widget.message.TextMessage;

/**
 * WebUI Node Parameters for the Credentials Configuration.
 *
 * @author Robin Gerling, KNIME GmbH, Konstanz, Germany
 * @author GitHub Copilot
 */
@SuppressWarnings("restriction")
@LoadDefaultsForAbsentFields
public class CredentialsDialogNodeParameters extends ConfigurationNodeSettings {

    @SuppressWarnings("javadoc")
    public enum CredentialsSource {
            @Label(value = "User input", description = "The credentials need to be provided by the user.")
            USER_INPUT, //
            @Label(value = "KNIME Server login (legacy)",
                description = "The credentials are retrieved from the KNIME Server login when executed as part of a"
                    + " job on KNIME Server. Please note that this option is not supported if your KNIME Server is"
                    + " configured to use single sign-on (SSO) via OAuth/OIDC.")
            KNIME_SERVER_LOGIN;
    }

    /**
     * Default constructor
     */
    protected CredentialsDialogNodeParameters() {
        super(CredentialsDialogNodeConfig.class);
    }

    @TextMessage(CredentialsOverwrittenByValueMessage.class)
    @Layout(OutputSection.Top.class)
    Void m_overwrittenByValueMessage;

    @Layout(OutputSection.Top.class)
    static final class DefaultValue implements NodeParameters {

        @Persistor(SavedPrior52Persistor.class)
        @ValueReference(SavedPrior52Ref.class)
        boolean m_savedPrior52;

        @Persistor(CredentialsParametersSavedPrior52Persistor.class)
        @Effect(predicate = WasSavedPrior52.class, type = EffectType.SHOW)
        CredentialsParametersSavedPrior52 m_credentialsParametersPrior52 = new CredentialsParametersSavedPrior52();

        @Persistor(CredentialsParametersSavedSince52Persistor.class)
        @Effect(predicate = WasSavedPrior52.class, type = EffectType.HIDE)
        CredentialsParametersSavedSince52 m_credentialsParametersSince52 = new CredentialsParametersSavedSince52();

        @Persist(configKey = CredentialsDialogNodeValue.USE_SERVER_CREDENTIALS)
        @ValueProvider(UseServerLoginCredentialsValueProvider.class)
        boolean m_useServerLoginCredentials;

    }

    DefaultValue m_defaultValue = new DefaultValue();

    @Widget(title = "Credentials source", description = "The source of the credentials:")
    @Layout(FormFieldSection.class)
    @Persistor(CredentialsSourcePersistor.class)
    @ValueReference(CredentialsSourceRef.class)
    @ValueSwitchWidget
    CredentialsSource m_credentialsSource = CredentialsSource.USER_INPUT;

    @Widget(title = "Show input fields",
        description = "When the server login is used it might be desirable to not show any input fields. When checking"
            + " this option, the component will not display, but username and password are still retrieved from"
            + " server login credentials.")
    @Layout(FormFieldSection.class)
    @Persistor(ShowInputFieldsPersistor.class)
    @ValueReference(ShowInputFieldsRef.class)
    @Effect(predicate = IsKnimeServerLogin.class, type = EffectType.SHOW)
    boolean m_showInputFields = !CredentialsNodeConfig.DEFAULT_NO_DISPLAY;

    @Widget(title = "Enable username field",
        description = "Whether the username should be rendered as a separate field"
            + " in the configuration dialog of a component. If disabled, the <i>Default username</i> will be used.")
    @Layout(FormFieldSection.class)
    @Persist(configKey = CredentialsNodeConfig.CFG_PROMPT_USER)
    @Effect(predicate = ShowInputFields.class, type = EffectType.SHOW)
    @ValueReference(EnableUserNameFieldRef.class)
    boolean m_enableUsernameField = CredentialsNodeConfig.DEFAULT_PROMPT_USER;

    @Widget(title = "Username label", description = "A custom label which is displayed for the username input field.")
    @Layout(FormFieldSection.class)
    @Persist(configKey = CredentialsNodeConfig.CFG_USERNAME_LABEL)
    @Effect(predicate = ShowUserNameLabelInputField.class, type = EffectType.SHOW)
    String m_usernameLabel = CredentialsNodeConfig.DEFAULT_USERNAME_LABEL;

    @Widget(title = "Password label", description = "A custom label which is displayed for the password input field.")
    @Layout(FormFieldSection.class)
    @Persist(configKey = CredentialsNodeConfig.CFG_PASSWORD_LABEL)
    @Effect(predicate = ShowInputFields.class, type = EffectType.SHOW)
    String m_passwordLabel = CredentialsNodeConfig.DEFAULT_PASSWORD_LABEL;

    @Persist(configKey = CredentialsNodeConfig.CFG_ERROR_MESSAGE)
    String m_errorMessageString = CredentialsNodeConfig.DEFAULT_ERROR_MESSAGE;

    static final class CredentialsSourceRef implements ParameterReference<CredentialsSource> {
    }

    static final class ShowInputFieldsRef implements ParameterReference<Boolean> {
    }

    static final class EnableUserNameFieldRef implements ParameterReference<Boolean> {
    }

    static final class SavedPrior52Ref implements ParameterReference<Boolean> {
    }

    static final class IsKnimeServerLogin implements EffectPredicateProvider {
        @Override
        public EffectPredicate init(final PredicateInitializer initializer) {
            return initializer.getEnum(CredentialsSourceRef.class).isOneOf(CredentialsSource.KNIME_SERVER_LOGIN);
        }
    }

    static final class ShowInputFields implements EffectPredicateProvider {

        @Override
        public EffectPredicate init(final PredicateInitializer initializer) {
            final var credentialsSourcePredicate =
                initializer.getEnum(CredentialsSourceRef.class).isOneOf(CredentialsSource.USER_INPUT);
            final var serverLoginAndShowFields = initializer.getPredicate(IsKnimeServerLogin.class)
                .and(initializer.getBoolean(ShowInputFieldsRef.class).isTrue());

            return credentialsSourcePredicate.or(serverLoginAndShowFields);
        }
    }

    static final class ShowUserNameLabelInputField implements EffectPredicateProvider {

        @Override
        public EffectPredicate init(final PredicateInitializer i) {
            return i.getPredicate(ShowInputFields.class).and(i.getBoolean(EnableUserNameFieldRef.class).isTrue());
        }

    }

    static final class WasSavedPrior52 implements EffectPredicateProvider {
        @Override
        public EffectPredicate init(final PredicateInitializer initializer) {
            return initializer.getBoolean(SavedPrior52Ref.class).isTrue();
        }
    }

    static final class CredentialsOverwrittenByValueMessage
        extends OverwrittenByValueMessage<CredentialsDialogNodeValue> {

        private Supplier<Boolean> m_enableUsernameFieldSupplier;

        @Override
        public void init(final StateProviderInitializer initializer) {
            super.init(initializer);
            m_enableUsernameFieldSupplier = initializer.computeFromValueSupplier(EnableUserNameFieldRef.class);
        }

        @Override
        protected String valueToString(final CredentialsDialogNodeValue value) {
            final var passwordValue =
                value.getPassword() == null || value.getPassword().isEmpty() ? "not provided" : "******";
            return m_enableUsernameFieldSupplier.get() != null && m_enableUsernameFieldSupplier.get()
                ? String.format("Username \"%s\", Password %s", value.getUsername(), passwordValue)
                : String.format("Password %s", passwordValue);
        }
    }

    static final class UseServerLoginCredentialsValueProvider implements StateProvider<Boolean> {

        private Supplier<CredentialsSource> m_credentialsSourceSupplier;

        @Override
        public void init(final StateProviderInitializer initializer) {
            m_credentialsSourceSupplier = initializer.computeFromValueSupplier(CredentialsSourceRef.class);
        }

        @Override
        public Boolean computeState(final NodeParametersInput parametersInput) throws StateComputationFailureException {
            return m_credentialsSourceSupplier.get() == CredentialsSource.KNIME_SERVER_LOGIN;
        }

    }

    static final class ShowInputFieldsPersistor implements NodeParametersPersistor<Boolean> {
        @Override
        public Boolean load(final NodeSettingsRO settings) throws InvalidSettingsException {
            return !settings.getBoolean(CredentialsNodeConfig.CFG_NO_DISPLAY, false);
        }

        @Override
        public void save(final Boolean showInputFields, final NodeSettingsWO settings) {
            settings.addBoolean(CredentialsNodeConfig.CFG_NO_DISPLAY, !showInputFields);
        }

        @Override
        public String[][] getConfigPaths() {
            return new String[][]{{CredentialsNodeConfig.CFG_NO_DISPLAY}};
        }
    }

    static final class CredentialsSourcePersistor extends EnumBooleanPersistor<CredentialsSource> {

        public CredentialsSourcePersistor() {
            super(CredentialsNodeConfig.CFG_USE_SERVER_LOGIN, CredentialsSource.class,
                CredentialsSource.KNIME_SERVER_LOGIN);
        }
    }

    static final class SavedPrior52Persistor implements NodeParametersPersistor<Boolean> {

        @Override
        public Boolean load(final NodeSettingsRO settings) throws InvalidSettingsException {
            return !settings.containsKey(CredentialsNodeValue.CFG_CREDENTIALS_VALUE_PARENT);
        }

        @Override
        public void save(final Boolean param, final NodeSettingsWO settings) {
            // do not save as the settings already contain the necessary information we use during load
        }

        @Override
        public String[][] getConfigPaths() {
            return new String[0][];
        }

    }

    abstract static class CredentialsParameters implements NodeParameters {

        protected CredentialsParameters() {
        }

        protected CredentialsParameters(final boolean savedPrior52) {
            m_savedPrior52 = savedPrior52;
        }

        protected CredentialsParameters(final Credentials credentials, final boolean isSavePassword,
            final boolean savedPrior52) {
            m_credentials = credentials;
            m_isSavePassword = isSavePassword;
            m_savedPrior52 = savedPrior52;
        }

        @Widget(title = "Default username and password",
            description = "The default credentials. When the credentials are for a KNIME Hub, username refers to"
                + " application password id and password to application password.")
        @CredentialsWidget
        Credentials m_credentials = new Credentials();

        @Widget(title = "Save password in configuration (weakly encrypted)",
            description = "Whether the password should be saved with the workflow. Note that, if saved, the password"
                + " can be revealed with modest effort by looking at the encryption algorithm used in KNIME's (open)"
                + " source code. If the password is not saved the user will be prompted for the password when the"
                + " workflow is opened unless the node is already saved executed. In the latter case the password is"
                + " not prompted assuming downstream consumer nodes, e.g. a DB reader, are also executed and no longer"
                + " need the credentials. In such cases (resetting and) executing a downstream node will fail as the"
                + " password is not part of the credentials object passed from this node into the workflow - a"
                + " re-configuration of this node is required.")
        @Persist(configKey = CredentialsNodeValue.CFG_SAVE_PASSWORD)
        boolean m_isSavePassword;

        boolean m_savedPrior52;
    }

    static final class CredentialsParametersSavedPrior52 extends CredentialsParameters {
        CredentialsParametersSavedPrior52() {
        }

        CredentialsParametersSavedPrior52(final boolean savedPrior52) {
            super(savedPrior52);
        }

        public CredentialsParametersSavedPrior52(final Credentials credentials, final boolean isSavePassword,
            final boolean savedPrior52) {
            super(credentials, isSavePassword, savedPrior52);
        }
    }

    static final class CredentialsParametersSavedSince52 extends CredentialsParameters {
        CredentialsParametersSavedSince52() {
        }

        CredentialsParametersSavedSince52(final boolean savedPrior52) {
            super(savedPrior52);
        }

        public CredentialsParametersSavedSince52(final Credentials credentials, final boolean isSavePassword,
            final boolean savedPrior52) {
            super(credentials, isSavePassword, savedPrior52);
        }
    }

    static final class CredentialsParametersSavedPrior52Persistor
        implements NodeParametersPersistor<CredentialsParametersSavedPrior52> {

        @Override
        public CredentialsParametersSavedPrior52 load(final NodeSettingsRO settings) throws InvalidSettingsException {
            final var loadedValue = new CredentialsNodeValue();
            loadedValue.loadFromNodeSettingsInDialog(settings);
            if (!loadedValue.isSavedPrior52()) {
                return new CredentialsParametersSavedPrior52(false);
            }
            return new CredentialsParametersSavedPrior52(
                new Credentials(loadedValue.getUsername(), loadedValue.getPassword()), loadedValue.isSavePassword(),
                true);
        }

        @Override
        public void save(final CredentialsParametersSavedPrior52 param, final NodeSettingsWO settings) {
            if (!param.m_savedPrior52) {
                return;
            }
            final var value = new CredentialsNodeValue();
            value.setSavedPrior52(true);
            value.setUsername(param.m_credentials.getUsername());
            value.setPassword(param.m_credentials.getPassword());
            value.setSavePassword(param.m_isSavePassword);
            value.saveToNodeSettings(settings);
        }

        @Override
        public String[][] getConfigPaths() {
            return new String[][]{{CredentialsNodeValue.CFG_USERNAME}, {CredentialsNodeValue.CFG_SAVE_PASSWORD}};
        }
    }

    static final class CredentialsParametersSavedSince52Persistor
        implements NodeParametersPersistor<CredentialsParametersSavedSince52> {

        @Override
        public CredentialsParametersSavedSince52 load(final NodeSettingsRO settings) throws InvalidSettingsException {
            final var loadedValue = new CredentialsNodeValue();
            loadedValue.loadFromNodeSettingsInDialog(settings);
            if (loadedValue.isSavedPrior52()) {
                return new CredentialsParametersSavedSince52(true);
            }
            return new CredentialsParametersSavedSince52(
                new Credentials(loadedValue.getUsername(), loadedValue.getPassword()), loadedValue.isSavePassword(),
                false);
        }

        @Override
        public void save(final CredentialsParametersSavedSince52 param, final NodeSettingsWO settings) {
            if (param.m_savedPrior52) {
                return;
            }
            final var value = new CredentialsNodeValue();
            value.setSavedPrior52(false);
            value.setUsername(param.m_credentials.getUsername());
            value.setPassword(param.m_credentials.getPassword());
            value.setSavePassword(param.m_isSavePassword);
            value.saveToNodeSettings(settings);
        }

        @Override
        public String[][] getConfigPaths() {
            return new String[][]{{CredentialsNodeValue.CFG_CREDENTIALS_VALUE_PARENT},
                {CredentialsNodeValue.CFG_SAVE_PASSWORD}};
        }
    }
}
