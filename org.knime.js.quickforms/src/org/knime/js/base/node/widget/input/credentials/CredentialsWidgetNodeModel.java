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
package org.knime.js.base.node.widget.input.credentials;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang.StringUtils;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.web.ValidationError;
import org.knime.core.node.workflow.Credentials;
import org.knime.core.node.workflow.CredentialsProvider;
import org.knime.core.node.workflow.CredentialsStore.CredentialsNode;
import org.knime.core.node.workflow.ICredentials;
import org.knime.core.node.workflow.WorkflowLoadHelper;
import org.knime.core.util.CoreConstants;
import org.knime.js.base.node.base.input.credentials.CredentialsNodeRepresentation;
import org.knime.js.base.node.base.input.credentials.CredentialsNodeValue;
import org.knime.js.base.node.widget.WidgetFlowVariableNodeModel;

/**
 * The node model for the credentials widget node
 *
 * @author Daniel Bogenrieder, KNIME GmbH, Konstanz, Germany
 */
public class CredentialsWidgetNodeModel extends
    WidgetFlowVariableNodeModel<CredentialsNodeRepresentation<CredentialsNodeValue>, CredentialsNodeValue, CredentialsInputWidgetConfig>
    implements CredentialsNode {

    /**
     * @param viewName
     */
    public CredentialsWidgetNodeModel(final String viewName) {
        super(viewName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CredentialsNodeValue createEmptyViewValue() {
        return new CredentialsNodeValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getJavascriptObjectID() {
        return "org.knime.js.base.node.widget.input.credentials";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void createAndPushFlowVariable() throws InvalidSettingsException {
        final CredentialsNodeValue value = getRelevantValue();
        ValidationError error = validateViewValue(value);
        if (error != null) {
            throw new InvalidSettingsException(error.getError());
        }
        String credentialsIdentifier = getConfig().getFlowVariableName();
        if (getConfig().isUseServerLoginCredentials()
            && pushCredentialsFlowVariableWithDefaultCredentials(credentialsIdentifier)) {
            getLogger().debugWithFormat("Pushing credentials flow variable using system credentials");
        } else {
            String username = value.getUsername();
            String password = value.getPassword();
            UserNameAndPasswordPair pair = readFromCredentialsProviderIfBlank(
                getCredentialsProvider(), credentialsIdentifier, username, password);
            pushCredentialsFlowVariable(credentialsIdentifier, pair.getUsername(), pair.getPassword());
        }
    }

    /**
     * If the password is blank, read out a credentials from the credentials provider. Workaround for bug AP-5974:
     * Credentials QF node to inherit password from workflow credentials, if present
     */
    private static UserNameAndPasswordPair readFromCredentialsProviderIfBlank(final CredentialsProvider provider,
        final String credentialsIdentifier, final String defaultUser, final String defaultPassword) {
        if (StringUtils.isEmpty(defaultPassword)) {
            if (provider != null) {
                ICredentials wkfCreds;
                try {
                    wkfCreds = provider.get(credentialsIdentifier);
                    return new UserNameAndPasswordPair(wkfCreds.getLogin(), wkfCreds.getPassword());
                } catch (IllegalArgumentException e) {
                    // credentials not defined - so leave password blank.
                }
            }
        }
        return new UserNameAndPasswordPair(defaultUser, defaultPassword);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CredentialsInputWidgetConfig createEmptyConfig() {
        return new CredentialsInputWidgetConfig();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected CredentialsNodeRepresentation getRepresentation() {
        CredentialsInputWidgetConfig config = getConfig();
        return new CredentialsNodeRepresentation<CredentialsNodeValue>(getRelevantValue(), config.getDefaultValue(),
            config.getCredentialsConfig(), config.getLabelConfig());
    }

    /** {@inheritDoc} */
    @Override
    public ValidationError validateViewValue(final CredentialsNodeValue viewContent) {
        String username = viewContent.getUsername();
        if (username == null) {
            return new ValidationError("No user name set");
        }
        return super.validateViewValue(viewContent);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    protected void useCurrentValueAsDefault() {
        getConfig().getDefaultValue().setUsername(getViewValue().getUsername());
        getConfig().getDefaultValue().setSavePassword(getViewValue().isSavePassword());
        // since 4.3
        if (!StringUtils.equals(getViewValue().getPassword(), CoreConstants.MAGIC_DEFAULT_PASSWORD)) {
            getConfig().getDefaultValue().setPassword(getViewValue().getPassword());
        }
    }

    /** {@inheritDoc} */
    @Override
    public void doAfterLoadFromDisc(final WorkflowLoadHelper loadHelper, final CredentialsProvider credProvider,
        final boolean isExecuted, final boolean isInactive) {
        String credentialsIdentifier = getConfig().getFlowVariableName();
        final CredentialsNodeValue value = getRelevantValue();
        String username = value != null ? value.getUsername() : null;
        if (credentialsIdentifier == null || username == null) {
            // no configuration, nothing to fix
            return;
        }
        if (getConfig().isUseServerLoginCredentials()
                && pushCredentialsFlowVariableWithDefaultCredentials(credentialsIdentifier)) {
            return; // exit here -- flow variable is pushed
        } else if (getConfig().isUseServerLoginCredentials() && loadHelper.getSystemDefaultCredentials().isPresent()) {
            final Credentials cred = loadHelper.getSystemDefaultCredentials().get();
            value.setUsername(cred.getLogin());
            value.setPassword(cred.getPassword());
            pushCredentialsFlowVariable(credentialsIdentifier, cred.getLogin(), cred.getPassword());
        } else {
            String password = value.getPassword();
            UserNameAndPasswordPair pair = new UserNameAndPasswordPair(username, password);
            if (!value.isSavePassword() && !isExecuted && !isInactive) {
                pair = readFromCredentialsProviderIfBlank(credProvider, credentialsIdentifier, username, password);
                if (StringUtils.isEmpty(pair.getPassword())) {
                    Credentials tempCredentials = new Credentials(credentialsIdentifier, username, password);
                    List<Credentials> loadCredentials =
                        loadHelper.loadCredentialsPrefilled(Collections.singletonList(tempCredentials));
                    if (loadCredentials.isEmpty()) {
                        // components dragged from server/hub won't be prompted for password, see AP-12458
                        setWarningMessage("Credentials required - please configure the node");
                    } else {
                        final String identifierFinal = credentialsIdentifier;
                        tempCredentials = loadCredentials.stream().//
                            filter(l -> l.getName().equals(identifierFinal)).//
                            findFirst().//
                            orElseThrow(
                                () -> new IllegalStateException("Coding issue: Credentials gone after consulting '"
                                    + loadHelper.getClass().getName() + "'"));

                        // set the new password
                        password = tempCredentials.getPassword();
                        value.setPassword(password);
                        if (password == null) {
                            setWarningMessage(
                                "No password set after loading workflow - reconfigure the node to fix it");
                        }

                        // if a new credentials id and/or login name has been entered, the old value(s) should be replaced
                        // (since this is what the user would expect)
                        credentialsIdentifier = tempCredentials.getName();
                        getConfig().setFlowVariableName(credentialsIdentifier);
                        username = tempCredentials.getLogin();
                        value.setUsername(username);
                    }
                } else {
                    getLogger().debugWithFormat("Inheriting credentials \"%s\" from workflow", credentialsIdentifier);
                }
            }
            pushCredentialsFlowVariable(credentialsIdentifier, username, password);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onWorkfowCredentialsChanged(final Collection<Credentials> workflowCredentials) {
        String credentialsIdentifier = getConfig().getFlowVariableName();
        final CredentialsNodeValue value = getRelevantValue();
        final String username = value != null ? value.getUsername() : null;
        if (credentialsIdentifier == null || username == null) {
            // no configuration, nothing to fix
            return;
        }

        if (!value.isSavePassword()) {
            Optional<Credentials> wkfCredOptional =
                    workflowCredentials.stream().filter(c -> credentialsIdentifier.equals(c.getName())).findFirst();
            wkfCredOptional.ifPresent(c -> {
                value.setUsername(c.getLogin());
                value.setPassword(c.getPassword());
            });
        }
    }

    /** Username &amp; password pair ... just a pair. */
    private static final class UserNameAndPasswordPair {
        private final String m_username;
        private final String m_password;

        UserNameAndPasswordPair(final String username, final String password) {
            m_username = username;
            m_password = password;
        }
        String getPassword() {
            return m_password;
        }
        public String getUsername() {
            return m_username;
        }

    }
}
