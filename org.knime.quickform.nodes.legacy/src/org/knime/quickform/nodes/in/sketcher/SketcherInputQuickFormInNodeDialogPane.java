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

import java.awt.GridBagConstraints;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.knime.core.node.InvalidSettingsException;
import org.knime.quickform.nodes.in.QuickFormInNodeDialogPane;

/**
 *
 * @author Dominik Morent, KNIME AG, Zurich, Switzerland
 *
 */
final class SketcherInputQuickFormInNodeDialogPane extends
        QuickFormInNodeDialogPane<SketcherInputQuickFormInConfiguration> {

    private final JTextArea m_valueField;
    private final JComboBox m_formatField;
    private final JCheckBox m_inlineCheckbox;


    /** Constructors, inits fields calls layout routines. */
    SketcherInputQuickFormInNodeDialogPane() {
        m_valueField = new JTextArea(3, DEF_TEXTFIELD_WIDTH);
        m_formatField = new JComboBox(SketcherInputQuickFormValueInConfiguration.DEFAULT_FORMATS);
        m_formatField.setEditable(true);
        m_inlineCheckbox = new JCheckBox();
        m_inlineCheckbox.setToolTipText("If checked the sketcher is shown "
                + "directly in the page, otherwise it opens in a new window.");
        createAndAddTab();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void fillPanel(final JPanel panelWithGBLayout,
            final GridBagConstraints gbc) {
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 10;
        JScrollPane sp = new JScrollPane(m_valueField);
        sp.setPreferredSize(m_valueField.getPreferredSize());
        sp.setMinimumSize(m_valueField.getMinimumSize());
        addPairToPanel("Molecule String: ", sp, panelWithGBLayout, gbc);

        gbc.weighty = 0;
        // "sketch inline" not shown as the marvin sketcher has its own
        // popup. See also bug 3491.
//        addPairToPanel("Sketch inline: ", m_inlineCheckbox, panelWithGBLayout, gbc);

        addPairToPanel("Format: ", m_formatField, panelWithGBLayout, gbc);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected SketcherInputQuickFormInConfiguration createConfiguration() {
        return new SketcherInputQuickFormInConfiguration();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveAdditionalSettings(
            final SketcherInputQuickFormInConfiguration config)
            throws InvalidSettingsException {
        SketcherInputQuickFormValueInConfiguration valueConfig =
                config.getValueConfiguration();
        valueConfig.setValue(m_valueField.getText());
        final Object formatItem = m_formatField.getSelectedItem();
        valueConfig.setFormat(formatItem == null ? null : formatItem.toString());
        valueConfig.setInline(m_inlineCheckbox.isSelected());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadAdditionalSettings(
            final SketcherInputQuickFormInConfiguration config) {
        SketcherInputQuickFormValueInConfiguration valueConfig =
                        config.getValueConfiguration();
        String value = valueConfig.getValue();
        if (value == null) {
            value = "";
        }
        m_valueField.setText(value);
        String format = valueConfig.getFormat();
        if (format == null) {
            format = "";
        }
        m_formatField.setSelectedItem(format);
        m_inlineCheckbox.setSelected(valueConfig.isInline());
    }

}
