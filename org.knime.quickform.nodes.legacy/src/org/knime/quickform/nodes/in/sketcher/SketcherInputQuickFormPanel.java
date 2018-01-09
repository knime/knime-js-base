/*
 * ------------------------------------------------------------------------
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
 * ------------------------------------------------------------------------
  *
  * History
  *   Dec 27, 2011 (morent): created
  */

package org.knime.quickform.nodes.in.sketcher;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.quickform.QuickFormConfigurationPanel;
import org.knime.core.util.node.quickform.in.AbstractQuickFormInElement;
import org.knime.core.util.node.quickform.in.SketcherInputQuickFormInElement;
import org.knime.quickform.nodes.in.QuickFormInNodeDialogPane;

/**
 *
 * @author Dominik Morent, KNIME AG, Zurich, Switzerland
 * @since 2.6
 *
 */
public class SketcherInputQuickFormPanel extends
        QuickFormConfigurationPanel<SketcherInputQuickFormValueInConfiguration> {
    private final JTextArea m_valueField;
//    private final JTextField m_formatField;
    private final JCheckBox m_inlineCheckbox;

    /**
     * Initializes fields and calls layout routines.
     * @param cfg the quickform configuration
     */
    public SketcherInputQuickFormPanel(
            final SketcherInputQuickFormInConfiguration cfg) {
        super(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.NORTHWEST;

        String labelString = cfg.getLabel();
        JLabel label = new JLabel(labelString);
        label.setToolTipText(cfg.getDescription());
        gbc.gridwidth = 1;
        add(label, gbc);
        m_valueField = new JTextArea(10,
                QuickFormInNodeDialogPane.DEF_TEXTFIELD_WIDTH);
        m_valueField.setMinimumSize(new Dimension(100, 50));
        m_valueField.setPreferredSize(new Dimension(100, 100));
        gbc.gridwidth = GridBagConstraints.REMAINDER;

        JScrollPane sp = new JScrollPane(m_valueField);
        sp.setMinimumSize(m_valueField.getMinimumSize());
        add(sp, gbc);

//        JLabel formatLabel = new JLabel("Format");
//        gbc.gridwidth = 1;
//        add(formatLabel, gbc);
//        m_formatField = new JTextField(
//                QuickFormInNodeDialogPane.DEF_TEXTFIELD_WIDTH);
//        gbc.gridwidth = GridBagConstraints.REMAINDER;
//        add(m_formatField);
        gbc.gridx = 1;
        m_inlineCheckbox = new JCheckBox("Sketch inline");
        add(m_inlineCheckbox, gbc);
        loadValueConfig(cfg.getValueConfiguration());
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void saveSettings(
            final SketcherInputQuickFormValueInConfiguration config)
            throws InvalidSettingsException {
        config.setValue(m_valueField.getText());
//        config.setFormat(m_formatField.getText());
        config.setInline(m_inlineCheckbox.isSelected());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadSettings(
            final SketcherInputQuickFormValueInConfiguration config) {
        loadValueConfig(config);
    }


    private void loadValueConfig(
            final SketcherInputQuickFormValueInConfiguration config) {
        m_valueField.setText(config.getValue());
//        m_formatField.setText(config.getFormat());
        m_inlineCheckbox.setSelected(config.isInline());
    }


    @Override
    public void updateQuickFormInElement(final AbstractQuickFormInElement e) throws InvalidSettingsException {
        SketcherInputQuickFormInElement cast = AbstractQuickFormInElement.cast(
                SketcherInputQuickFormInElement.class, e);
        cast.setValue(m_valueField.getText());
        cast.setInline(m_inlineCheckbox.isSelected());
    }

}
