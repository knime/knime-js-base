/*
 * ------------------------------------------------------------------------
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
 * ------------------------------------------------------------------------
 */
package org.knime.js.base.node.quickform.input.credentials;

import java.util.Collections;
import java.util.List;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.web.ValidationError;
import org.knime.core.node.workflow.Credentials;
import org.knime.core.node.workflow.CredentialsStore.CredentialsNode;
import org.knime.core.node.workflow.WorkflowLoadHelper;
import org.knime.js.base.node.quickform.QuickFormFlowVariableNodeModel;

/**
 * The model for the credentials input quick form node.
 *
 * @author Bernd Wiswedel, KNIME.com AG, Zurich, Switzerland
 */
public final class CredentialsInputQuickFormNodeModel extends
    QuickFormFlowVariableNodeModel<CredentialsInputQuickFormRepresentation,
    CredentialsInputQuickFormValue, CredentialsInputQuickFormConfig> implements CredentialsNode {

    CredentialsInputQuickFormNodeModel() {
        super("Credentials Input");
    }

    /** {@inheritDoc} */
    @Override
    public String getJavascriptObjectID() {
        return "org_knime_js_base_node_quickform_input_credentials";
    }

    /** {@inheritDoc} */
    @Override
    protected void createAndPushFlowVariable() throws InvalidSettingsException {
        final CredentialsInputQuickFormValue value = getRelevantValue();
        ValidationError error = validateViewValue(value);
        if (error != null) {
            throw new InvalidSettingsException(error.getError());
        }
        String credentialsIdentifier = getConfig().getFlowVariableName();
        String username = value.getUsername();
        String password = value.getPassword();
        pushCredentialsFlowVariable(credentialsIdentifier, username, password);
    }

    /** {@inheritDoc} */
    @Override
    public CredentialsInputQuickFormValue createEmptyViewValue() {
        return new CredentialsInputQuickFormValue();
    }

    @Override
    protected void copyValueToConfig() {
        getConfig().getDefaultValue().setUsername(getViewValue().getUsername());
    }

    /** {@inheritDoc} */
    @Override
    public CredentialsInputQuickFormConfig createEmptyConfig() {
        return new CredentialsInputQuickFormConfig();
    }

    /** {@inheritDoc} */
    @Override
    protected CredentialsInputQuickFormRepresentation getRepresentation() {
        return new CredentialsInputQuickFormRepresentation(getRelevantValue(), getConfig());
    }

    /** {@inheritDoc} */
    @Override
    public ValidationError validateViewValue(final CredentialsInputQuickFormValue viewContent) {
        String username = viewContent.getUsername();
        if (username == null) {
            return new ValidationError("no user name set");
        }
        return super.validateViewValue(viewContent);
    }

    /** {@inheritDoc} */
    @Override
    public void doAfterLoadFromDisc(final WorkflowLoadHelper loadHelper, final boolean isExecuted, final boolean isInactive) {
        String credentialsIdentifier = getConfig().getFlowVariableName();
        final CredentialsInputQuickFormValue value = getRelevantValue();
        final String username = value != null ? value.getUsername() : null;
        if (credentialsIdentifier == null || username == null) {
            // no configuration, nothing to fix
            return;
        }
        String password = value.getPassword();
        if (!value.isSavePassword() && !isExecuted && !isInactive) {
            Credentials tempCredentials = new Credentials(credentialsIdentifier, username, password);
            List<Credentials> loadCredentials = loadHelper.loadCredentials(Collections.singletonList(tempCredentials));
            password = loadCredentials.iterator().next().getPassword();
            value.setPassword(password);
            if (password == null) {
                setWarningMessage("No password set after loading workflow - reconfigure the node to fix it");
            }
        }
        pushCredentialsFlowVariable(credentialsIdentifier, username, password);
    }

}
