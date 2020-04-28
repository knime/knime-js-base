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
 *   4 Apr 2018 (albrecht): created
 */
package org.knime.js.node.minimalRequestHandler;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JPanel;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.NotConfigurableException;
import org.knime.core.node.port.PortObjectSpec;

/**
 *
 * @author Christian Albrecht, KNIME GmbH, Konstanz, Germany
 */
public class MinimalRequestHandlerNodeDialog extends NodeDialogPane {

    private final JCheckBox m_hideInWizardCheckbox;
    private final JCheckBox m_stallRequestsCheckbox;
    private final JCheckBox m_keepOrderCheckbox;
    private final JCheckBox m_cancelPreviousCheckbox;

    /**
     *
     */
    public MinimalRequestHandlerNodeDialog() {
        m_hideInWizardCheckbox = new JCheckBox("Hide in wizard");
        m_stallRequestsCheckbox = new JCheckBox("Stall requests in node model");
        m_stallRequestsCheckbox.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent e) {
                enableOrder();
            }
        });
        m_keepOrderCheckbox = new JCheckBox("Keep responses in order");
        m_cancelPreviousCheckbox = new JCheckBox("Cancel previous requests");

        addTab("Options", getBasicPanel());
    }

    /**
     * @return
     */
    private JPanel getBasicPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(m_hideInWizardCheckbox, gbc);
        gbc.gridy++;
        panel.add(m_stallRequestsCheckbox, gbc);
        gbc.gridy++;
        panel.add(m_keepOrderCheckbox, gbc);
        gbc.gridy++;
        panel.add(m_cancelPreviousCheckbox, gbc);
        return panel;
    }

    private void enableOrder() {
        boolean enable = m_stallRequestsCheckbox.isSelected();
        m_keepOrderCheckbox.setEnabled(enable);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) throws InvalidSettingsException {
        MinimalRequestHandlerConfig config = new MinimalRequestHandlerConfig();
        config.setHideInWizard(m_hideInWizardCheckbox.isSelected());
        config.setStallRequests(m_stallRequestsCheckbox.isSelected());
        config.setKeepOrder(m_stallRequestsCheckbox.isSelected() && m_keepOrderCheckbox.isSelected());
        config.setCancelPrevious(m_cancelPreviousCheckbox.isSelected());
        config.saveToSettings(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadSettingsFrom(final NodeSettingsRO settings, final PortObjectSpec[] specs) throws NotConfigurableException {
        MinimalRequestHandlerConfig config = new MinimalRequestHandlerConfig();
        config.loadFromSettingsInDialog(settings);
        m_hideInWizardCheckbox.setSelected(config.isHideInWizard());
        m_stallRequestsCheckbox.setSelected(config.isStallRequests());
        m_keepOrderCheckbox.setSelected(config.isKeepOrder());
        m_cancelPreviousCheckbox.setSelected(config.isCancelPrevious());
        enableOrder();
    }

}
