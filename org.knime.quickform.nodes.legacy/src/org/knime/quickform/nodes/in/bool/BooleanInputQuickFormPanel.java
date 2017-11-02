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
 *
 * History:
 * 23-Febr-2011: created
 */
package org.knime.quickform.nodes.in.bool;

import java.awt.FlowLayout;

import javax.swing.JCheckBox;
import javax.swing.JLabel;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.quickform.QuickFormConfigurationPanel;
import org.knime.core.util.node.quickform.in.AbstractQuickFormInElement;
import org.knime.core.util.node.quickform.in.CheckboxInputQuickFormInElement;

/**
 * Dialog to node.
 *
 * @author Thomas Gabriel, KNIME AG, Zurich, Switzerland
 * @since 2.6
 */
final class BooleanInputQuickFormPanel extends
    QuickFormConfigurationPanel<BooleanInputQuickFormValueInConfiguration> {

    private final JCheckBox m_valueField;

    /**
     * Constructors, inits fields calls layout routines.
     * @param cfg quickform configuration
     */
    BooleanInputQuickFormPanel(final BooleanInputQuickFormInConfiguration cfg) {
        super(new FlowLayout(FlowLayout.LEFT));
        String labelString = cfg.getLabel();
        JLabel label = new JLabel(labelString);
        label.setToolTipText(cfg.getDescription());
        add(label);
        m_valueField = new JCheckBox("", false);
        add(m_valueField);
        loadValueConfig(cfg.getValueConfiguration());
    }


    /** {@inheritDoc} */
    @Override
    public void saveSettings(
            final BooleanInputQuickFormValueInConfiguration config)
            throws InvalidSettingsException {
        config.setValue(m_valueField.isSelected());
    }


    /** {@inheritDoc} */
    @Override
    public void loadSettings(
            final BooleanInputQuickFormValueInConfiguration config) {
        loadValueConfig(config);
    }


    /**
     * @param config the configuration to load
     */
    private void loadValueConfig(
            final BooleanInputQuickFormValueInConfiguration config) {
        m_valueField.setSelected(config.getValue());
    }
    
    @Override
    public void updateQuickFormInElement(final AbstractQuickFormInElement e) throws InvalidSettingsException {
        CheckboxInputQuickFormInElement cast =
            AbstractQuickFormInElement.cast(
                    CheckboxInputQuickFormInElement.class, e);
        cast.setValue(m_valueField.isSelected());
    }
}
