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
 */
package org.knime.js.base.node.widget.reexecution.refresh;

import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.NotConfigurableException;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.js.base.util.LabeledViewNodeDialog;

/**
 * Node dialog for the refresh button widget node.
 *
 * @author Ben Laney, KNIME GmbH, Konstanz, Germany
 */
public class RefreshButtonWidgetNodeDialog extends LabeledViewNodeDialog {

    private final JTextField m_textField;
    private final JButton m_resetButton;
    private final RefreshButtonWidgetNodeConfig m_config;
    private boolean m_clicked = false;

    /**
     * Create new dialog.
     */
    public RefreshButtonWidgetNodeDialog() {
        m_config = new RefreshButtonWidgetNodeConfig();
        m_textField = new JTextField(DEF_TEXTFIELD_WIDTH);
        m_resetButton = new JButton("set to zero");
        m_resetButton.addActionListener(e ->
        {
            // toggle flag if button haven't been pressed to ensure reloading of settings to reset counter
            if (!m_clicked) {
                m_config.toggleCountingHelperFlag();
                m_clicked = true;
            }
        });
        createAndAddTab();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    protected void fillPanel(final JPanel panelWithGBLayout, final GridBagConstraints gbc) {
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.NORTHWEST;

        gbc.fill = GridBagConstraints.HORIZONTAL;
        addPairToPanel("Button text: ", m_textField, panelWithGBLayout, gbc);
        gbc.fill = GridBagConstraints.NONE;
        addPairToPanel("Reset Refresh Counter: ", m_resetButton, panelWithGBLayout, gbc);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadSettingsFrom(final NodeSettingsRO settings, final PortObjectSpec[] specs)
        throws NotConfigurableException {
        m_config.loadSettingsInDialog(settings);
        String s = m_config.getButtonText();
        m_textField.setText(s);
        loadSettingsFrom(m_config);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) throws InvalidSettingsException {
        saveSettingsTo(m_config);
        String s = m_textField.getText();
        m_config.setButtonText(s);
        m_config.saveSettings(settings);
        m_clicked = false;
    }
}
