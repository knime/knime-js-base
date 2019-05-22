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
import java.awt.Color;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeView;
import org.knime.core.node.dialog.ValueControlledDialogPane;
import org.knime.js.base.node.quickform.ValueOverwriteMode;

/**
 *
 * @author Christian Albrecht, KNIME GmbH, Konstanz, Germany
 */
public abstract class LabeledValueControlledNodeDialog extends LabeledNodeDialog implements ValueControlledDialogPane {

    private final JLabel m_statusBarLabel;

    /**
     * Inits fields, sub-classes should call the {@link #createAndAddTab()}
     * method when they are done initializing their fields.
     */
    public LabeledValueControlledNodeDialog() {
        super();
        m_statusBarLabel = new JLabel("", NodeView.WARNING_ICON, SwingConstants.LEFT);
        Font font = m_statusBarLabel.getFont().deriveFont(Font.BOLD);
        m_statusBarLabel.setFont(font);
        m_statusBarLabel.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
        m_statusBarLabel.setBackground(Color.WHITE);
        m_statusBarLabel.setOpaque(true);
        m_statusBarLabel.setVisible(false);
    }

    /**
     * @return the statusBarLabel
     */
    public JLabel getStatusBarLabel() {
        return m_statusBarLabel;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected JPanel createBorderPanel(final JPanel content) {
        JPanel borderPanel = new JPanel(new BorderLayout());
        borderPanel.add(content, BorderLayout.CENTER);
        borderPanel.add(m_statusBarLabel, BorderLayout.SOUTH);
        return borderPanel;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadCurrentValue(final NodeSettingsRO value) throws InvalidSettingsException {
        ValueOverwriteMode mode =
            ValueOverwriteMode.valueOf(value.getString(ValueControlledNodeUtil.CFG_OVERWRITE_MODE));
        NodeSettingsRO valueSettings = value.getNodeSettings(ValueControlledNodeUtil.CFG_CURRENT_VALUE);
        if (mode == ValueOverwriteMode.NONE) {
            m_statusBarLabel.setVisible(false);
        } else {
            String overwrittenBy = "";
            switch (mode) {
                case DIALOG:
                    overwrittenBy = "dialog";
                    break;
                case WIZARD:
                    overwrittenBy = "wizard";
                    break;
                default:
                    overwrittenBy = "unknown";
            }
            String fullText =
                "Value overwritten by " + overwrittenBy + ", current value:\n" + getValueString(valueSettings);
            m_statusBarLabel.setText("<html>" + fullText.replace("\n", "<br>") + "</html>");
            m_statusBarLabel.setVisible(true);
        }
    }

    /**
     * Loads a value with the current setting and creates a string displaying the contained values.
     *
     * Is used for the overwrite label
     *
     * @param settings Object containing the settings of the value
     * @return String representing the value
     * @throws InvalidSettingsException If the settings are invalid
     */
    protected abstract String getValueString(final NodeSettingsRO settings) throws InvalidSettingsException;
}
