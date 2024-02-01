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
package org.knime.js.base.node.widget.output.text;

import java.awt.GridBagConstraints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.DefaultListModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;

import org.knime.base.util.flowvariable.FlowVariableResolver;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.NotConfigurableException;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.util.FlowVariableListCellRenderer;
import org.knime.core.node.workflow.FlowVariable;
import org.knime.js.base.node.widget.output.text.TextOutputWidgetConfig.OutputTextFormat;
import org.knime.js.base.util.LabeledViewNodeDialog;

/**
 *
 * @author Christian Albrecht, KNIME GmbH, Konstanz, Germany
 */
public class TextOutputWidgetDialog extends LabeledViewNodeDialog {

    private final JList<FlowVariable> m_flowVarList;
    private final JComboBox<?> m_textFormatBox;
    private final JCheckBox m_sanitizeCheckbox;
    private final JTextArea m_textArea;
    private final TextOutputWidgetConfig m_config;

    /**
     * Create new dialog.
     */
    @SuppressWarnings({"unchecked", "rawtypes" })
    public TextOutputWidgetDialog() {
        m_config = new TextOutputWidgetConfig();
        m_textFormatBox = new JComboBox(OutputTextFormat.values());
        m_sanitizeCheckbox = new JCheckBox("Sanitize input data");
        m_textArea = new JTextArea(10, DEF_TEXTFIELD_WIDTH);
        m_flowVarList = new JList(new DefaultListModel());
        m_flowVarList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        m_flowVarList.setCellRenderer(new FlowVariableListCellRenderer());
        m_flowVarList.addMouseListener(new MouseAdapter() {
            /** {@inheritDoc} */
            @Override
            public final void mouseClicked(final MouseEvent e) {
                if (e.getClickCount() == 2) {
                    FlowVariable o = m_flowVarList.getSelectedValue();
                    if (o != null) {
                        m_textArea.replaceSelection(FlowVariableResolver.getPlaceHolderForVariable(o));
                        m_flowVarList.clearSelection();
                        m_textArea.requestFocus();
                    }
                }
            }
        });
        createAndAddTab();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void fillPanel(final JPanel panelWithGBLayout, final GridBagConstraints gbc) {
        addPairToPanel("Text format: ", m_textFormatBox, panelWithGBLayout, gbc);
        addPairToPanel("", m_sanitizeCheckbox, panelWithGBLayout, gbc);

        JSplitPane splitter = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitter.setRightComponent(new JScrollPane(m_textArea));
        JScrollPane flowScroller = new JScrollPane(m_flowVarList, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                                                   ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        splitter.setLeftComponent(flowScroller);
        splitter.setResizeWeight(0.4);
        splitter.setDividerLocation(0.4);
        gbc.weighty = 1.0;
        gbc.gridheight = 3;
        gbc.fill = GridBagConstraints.BOTH;
        addPairToPanel("Text: ", splitter, panelWithGBLayout, gbc);
        gbc.weighty = 0.0;
        gbc.gridheight = 1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadSettingsFrom(final NodeSettingsRO settings, final PortObjectSpec[] specs)
        throws NotConfigurableException {
        m_config.loadSettingsInDialog(settings);
        String s = m_config.getText();
        if (s == null) {
            s = "";
        }
        DefaultListModel<FlowVariable> listModel = (DefaultListModel<FlowVariable>)m_flowVarList.getModel();
        listModel.removeAllElements();
        for (FlowVariable e : getAvailableFlowVariables().values()) {
            listModel.addElement(e);
        }
        m_textArea.setText(s);
        m_textFormatBox.setSelectedItem(m_config.getTextFormat());
        m_sanitizeCheckbox.setSelected(m_config.isSanitizeInput());
        loadSettingsFrom(m_config);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) throws InvalidSettingsException {
        saveSettingsTo(m_config);
        String s = m_textArea.getText();
        m_config.setText(s);
        m_config.setTextFormat((OutputTextFormat)m_textFormatBox.getSelectedItem());
        m_config.setSanitizeInput(m_sanitizeCheckbox.isSelected());
        m_config.saveSettings(settings);
    }
}