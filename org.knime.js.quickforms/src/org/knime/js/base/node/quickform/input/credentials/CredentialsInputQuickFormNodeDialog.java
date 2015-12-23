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

import java.awt.GridBagConstraints;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import org.apache.commons.lang.StringUtils;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.NotConfigurableException;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.js.base.node.quickform.QuickFormNodeDialog;

/**
 * The dialog for the credentials input quick form node.
 *
 * @author Bernd Wiswedel, KNIME.com AG, Zurich, Switzerland
 */
public class CredentialsInputQuickFormNodeDialog extends QuickFormNodeDialog {

    private final JTextField m_usernameField;
    private final JPasswordField m_passwordField;
    private final JCheckBox m_promptUsernameChecker;
    private final JCheckBox m_savePasswordChecker;
    private final JCheckBox m_useServerLoginChecker;

    /** Constructors, inits fields calls layout routines. */
    CredentialsInputQuickFormNodeDialog() {
        m_usernameField = new JTextField(DEF_TEXTFIELD_WIDTH);
        m_passwordField = new JPasswordField(DEF_TEXTFIELD_WIDTH);
        m_promptUsernameChecker = new JCheckBox("Prompt user name in wrapped metanode dialog/wizard");
        m_savePasswordChecker = new JCheckBox("Save password in configuration (weakly encrypted)");
        m_useServerLoginChecker = new JCheckBox("Use KNIME Server Login (when run on server)");
        createAndAddTab();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected final void fillPanel(final JPanel panelWithGBLayout, final GridBagConstraints gbc) {
        addPairToPanel("Username: ", m_usernameField, panelWithGBLayout, gbc);
        addPairToPanel("Password: ", m_passwordField, panelWithGBLayout, gbc);
        addPairToPanel(" ", m_promptUsernameChecker, panelWithGBLayout, gbc);
        addPairToPanel(" ", m_savePasswordChecker, panelWithGBLayout, gbc);
        addPairToPanel(" ", m_useServerLoginChecker, panelWithGBLayout, gbc);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadSettingsFrom(final NodeSettingsRO settings, final PortObjectSpec[] specs)
            throws NotConfigurableException {
        CredentialsInputQuickFormConfig config = new CredentialsInputQuickFormConfig();
        config.loadSettingsInDialog(settings);
        loadSettingsFrom(config);
        final CredentialsInputQuickFormValue defaultValue = config.getDefaultValue();
        m_usernameField.setText(defaultValue.getUsername());
        m_passwordField.setText(defaultValue.getPassword());
        m_savePasswordChecker.setSelected(defaultValue.isSavePassword());
        m_promptUsernameChecker.setSelected(config.isPromptUsername());
        m_useServerLoginChecker.setSelected(config.isUseServerLoginCredentials());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) throws InvalidSettingsException {
        CredentialsInputQuickFormConfig config = new CredentialsInputQuickFormConfig();
        config.setPromptUsername(m_promptUsernameChecker.isSelected());
        config.setUseServerLoginCredentials(m_useServerLoginChecker.isSelected());
        final CredentialsInputQuickFormValue defaultValue = config.getDefaultValue();
        defaultValue.setUsername(m_usernameField.getText());
        defaultValue.setPassword(new String(m_passwordField.getPassword()));
        defaultValue.setSavePassword(m_savePasswordChecker.isSelected());
        saveSettingsTo(config);

        config.saveSettings(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getValueString(final NodeSettingsRO settings) throws InvalidSettingsException {
        CredentialsInputQuickFormValue value = new CredentialsInputQuickFormValue();
        value.loadFromNodeSettings(settings);
        return String.format("user '%s', password %s", value.getUsername(),
            StringUtils.isEmpty(value.getPassword()) ? "&lt;not set>" : "&lt;set>");
    }

}
