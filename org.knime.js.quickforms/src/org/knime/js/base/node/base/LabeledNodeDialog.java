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
 *   21 May 2019 (albrecht): created
 */
package org.knime.js.base.node.base;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.NotConfigurableException;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.js.core.settings.DialogUtil;

/**
 *
 * @author Christian Albrecht, KNIME GmbH, Konstanz, Germany
 */
public abstract class LabeledNodeDialog extends NodeDialogPane {

    private final JTextField m_labelField;
    private final JTextArea m_descriptionArea;

    /**
     * Inits fields, sub-classes should call the {@link #createAndAddTab()}
     * method when they are done initializing their fields.
     */
    public LabeledNodeDialog() {
        m_labelField = new JTextField(DialogUtil.DEF_TEXTFIELD_WIDTH);
        m_descriptionArea = new JTextArea(1, DialogUtil.DEF_TEXTFIELD_WIDTH);
        m_descriptionArea.setLineWrap(true);
        m_descriptionArea.setPreferredSize(new Dimension(100, 50));
        m_descriptionArea.setMinimumSize(new Dimension(100, 30));
    }

    /**
     * Adds a panel sub-component to the dialog
     *
     * @param label The label (left hand column)
     * @param c The component (right hand column)
     * @param panelWithGBLayout Panel to add
     * @param gbc constraints.
     */
    protected final static void addPairToPanel(final String label, final JComponent c, final JPanel panelWithGBLayout,
            final GridBagConstraints gbc) {
        DialogUtil.addPairToPanel(label, c, panelWithGBLayout, gbc);
    }

    /**
     * Creates a new panel for the label components
     * @param gbc {@link GridBagConstraints} to be used for the panel
     * @return a new JPanel with label and description set
     */
    protected JPanel createContentPanel(final GridBagConstraints gbc) {
        JPanel panel = new JPanel(new GridBagLayout());
        addPairToPanel("Label: ", m_labelField, panel, gbc);

        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1;
        JScrollPane sp = new JScrollPane(m_descriptionArea);
        sp.setPreferredSize(m_descriptionArea.getPreferredSize());
        sp.setMinimumSize(m_descriptionArea.getMinimumSize());
        addPairToPanel("Description: ", sp, panel, gbc);

        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        return panel;
    }

    /**
     * Creates the border panel which is the top-level panel of the dialog
     * @param content the content panel to wrap
     * @return the newly created border panel
     */
    protected JPanel createBorderPanel(final JPanel content) {
        JPanel borderPanel = new JPanel(new BorderLayout());
        borderPanel.add(content, BorderLayout.CENTER);
        return borderPanel;
    }

    /**
     * To be called from subclasses as last line in their constructor. It
     * initializes the panel, call the
     * {@link #fillPanel(JPanel, GridBagConstraints)} method and adds the tab to
     * the dialog.
     */
    protected void createAndAddTab() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JPanel panel = createContentPanel(gbc);

        fillPanel(panel, gbc);

        JPanel borderPanel = createBorderPanel(panel);

        addTab("Control", borderPanel);
    }

    /**
     * Called from {@link #createAndAddTab()}. Subclasses should add their own
     * controls to the argument panel.
     *
     * @param panelWithGBLayout To add to.
     * @param gbc The current constraints.
     */
    protected abstract void fillPanel(final JPanel panelWithGBLayout, GridBagConstraints gbc);

    /**
     * @return The label
     */
    protected String getLabel() {
        return m_labelField.getText();
    }

    /**
     * @param label The label
     */
    protected void setLabel(final String label) {
        m_labelField.setText(label);
    }

    /**
     * @return The description
     */
    protected String getDescription() {
        return m_descriptionArea.getText();
    }

    /**
     * @param description The description
     */
    protected void setDescription(final String description) {
        m_descriptionArea.setText(description);
    }

    /**
     * @param config The {@link LabeledConfig} to load from
     */
    protected void loadSettingsFrom(final LabeledConfig config) {
        setLabel(config.getLabel());
        setDescription(config.getDescription());
    }

    /**
     * @param config The {@link LabeledConfig} to save to
     */
    protected void saveSettingsTo(final LabeledConfig config) {
        config.setLabel(getLabel());
        config.setDescription(getDescription());
    }

    /** {@inheritDoc} */
    @Override
    protected abstract void loadSettingsFrom(final NodeSettingsRO settings, final PortObjectSpec[] specs)
            throws NotConfigurableException;

    /** {@inheritDoc} */
    @Override
    protected abstract void saveSettingsTo(final NodeSettingsWO settings) throws InvalidSettingsException;

}
