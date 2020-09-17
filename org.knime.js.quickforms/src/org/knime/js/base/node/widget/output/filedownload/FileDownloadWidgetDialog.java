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
 *   Sep 17, 2020 (Christian Albrecht, KNIME GmbH, Konstanz, Germany): created
 */
package org.knime.js.base.node.widget.output.filedownload;

import java.awt.GridBagConstraints;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.commons.lang3.StringUtils;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.NotConfigurableException;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.util.FlowVariableListCellRenderer;
import org.knime.core.node.workflow.FlowVariable;
import org.knime.js.base.util.LabeledViewNodeDialog;

/**
 *
 * @author Christian Albrecht, KNIME GmbH, Konstanz, Germany
 */
public class FileDownloadWidgetDialog extends LabeledViewNodeDialog {

    private final JComboBox<FlowVariable> m_filePathVariableNameCombo;
    private final JTextField m_linkTitle;
    private final JTextField m_resourceName;
    private final FileDownloadConfig m_config;

    /**
     * Create new dialog.
     */
    public FileDownloadWidgetDialog() {
        m_config = new FileDownloadConfig();
        m_filePathVariableNameCombo = new JComboBox<FlowVariable>(new DefaultComboBoxModel<FlowVariable>());
        m_filePathVariableNameCombo.setRenderer(new FlowVariableListCellRenderer());
        m_linkTitle = new JTextField(DEF_TEXTFIELD_WIDTH);
        m_resourceName = new JTextField(DEF_TEXTFIELD_WIDTH);
        createAndAddTab();
    }

    private String getFlowVariableName() {
        FlowVariable v = (FlowVariable)m_filePathVariableNameCombo.getSelectedItem();
        if (v != null) {
            return v.getName();
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void fillPanel(final JPanel panelWithGBLayout, final GridBagConstraints gbc) {
        addPairToPanel("Link Title", m_linkTitle, panelWithGBLayout, gbc);
        addPairToPanel("Output resource name", m_resourceName, panelWithGBLayout, gbc);
        addPairToPanel("File Path Variable", m_filePathVariableNameCombo, panelWithGBLayout, gbc);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadSettingsFrom(final NodeSettingsRO settings, final PortObjectSpec[] specs)
        throws NotConfigurableException {
        m_config.loadSettingsInDialog(settings);
        loadSettingsFrom(m_config);
        m_linkTitle.setText(m_config.getLinkTitle());
        m_resourceName.setText(m_config.getResourceName());
        String flowVariableName = m_config.getFlowVariable();

        FlowVariable selectedVar = null;
        DefaultComboBoxModel<FlowVariable> m =
            (DefaultComboBoxModel<FlowVariable>)m_filePathVariableNameCombo.getModel();
        m.removeAllElements();
        for (FlowVariable v : getAvailableFlowVariables().values()) {
            if (v.getType().equals(FlowVariable.Type.STRING)) {
                m.addElement(v);
                if (v.getName().equals(flowVariableName)) {
                    selectedVar = v;
                }
            }
        }
        if (selectedVar != null) {
            m_filePathVariableNameCombo.setSelectedItem(selectedVar);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) throws InvalidSettingsException {
        if (StringUtils.isEmpty(m_linkTitle.getText())) {
            throw new InvalidSettingsException("Please provide a link title for the download link.");
        }
        saveSettingsTo(m_config);
        m_config.setLinkTitle(m_linkTitle.getText());
        m_config.setResourceName(m_resourceName.getText());
        m_config.setFlowVariable(getFlowVariableName());
        m_config.saveSettings(settings);
    }
}
