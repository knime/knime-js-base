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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import org.knime.core.node.InvalidSettingsException;
import org.knime.js.base.node.configuration.AbstractDialogNodeConfigurationPanel;
import org.knime.js.base.node.quickform.QuickFormNodeDialog;

/**
 * The component dialog panel for the credentials configuration node
 *
 * @author Daniel Bogenrieder, KNIME GmbH, Konstanz, Germany
 */
public class CredentialsConfigurationPanel extends AbstractDialogNodeConfigurationPanel<CredentialsDialogNodeValue> {

    private JTextField m_usernameField = new JTextField(QuickFormNodeDialog.DEF_TEXTFIELD_WIDTH);
    private JPasswordField m_passwordField = new JPasswordField(QuickFormNodeDialog.DEF_TEXTFIELD_WIDTH);
    private boolean m_isSavePassword;

    /**
     * @param representation The dialog representation
     *
     */
    public CredentialsConfigurationPanel(final CredentialsDialogNodeRepresentation representation) {
        super(representation.getLabel(), representation.getDescription(), representation.getDefaultValue());
        m_usernameField.setText(representation.getDefaultValue().getUsername());
        m_passwordField.setText(representation.getDefaultValue().getPassword());
        m_isSavePassword = representation.getDefaultValue().isSavePassword();
        JPanel p = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = gbc.gridy = 0;
        if (representation.isPromptUsername()) {
            p.add(new JLabel("Username "), gbc);
            gbc.gridx += 1;
            p.add(m_usernameField, gbc);
            gbc.gridx = 0;
            gbc.gridy += 1;
        }
        p.add(new JLabel("Password "), gbc);
        gbc.gridx += 1;
        p.add(m_passwordField, gbc);
        setComponent(p);
    }

    /** {@inheritDoc} */
    @Override
    public void loadNodeValue(final CredentialsDialogNodeValue value) {
        super.loadNodeValue(value);
        if (value != null) {
            m_usernameField.setText(value.getUsername());
            m_passwordField.setText(value.getPassword());
            m_isSavePassword = value.isSavePassword();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CredentialsDialogNodeValue createNodeValue() throws InvalidSettingsException {
        CredentialsDialogNodeValue value = new CredentialsDialogNodeValue();
        value.setUsername(m_usernameField.getText());
        value.setPassword(new String(m_passwordField.getPassword()));
        value.setSavePassword(m_isSavePassword);
        return value;
    }

    /** {@inheritDoc} */
    @Override
    public void setEnabled(final boolean enabled) {
        super.setEnabled(enabled);
        m_usernameField.setEnabled(enabled);
        m_passwordField.setEnabled(enabled);
    }

    /** {@inheritDoc} */
    @Override
    protected void resetToDefault() {
        final CredentialsDialogNodeValue defaultValue = getDefaultValue();
        m_usernameField.setText(defaultValue.getUsername());
        m_passwordField.setText(defaultValue.getPassword());
        m_isSavePassword = defaultValue.isSavePassword();
    }
}
