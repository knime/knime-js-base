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
 *
 */
package org.knime.quickform.nodes.in.dbl;

import java.awt.GridBagConstraints;

import javax.swing.JPanel;
import javax.swing.JTextField;

import org.knime.core.node.InvalidSettingsException;
import org.knime.quickform.nodes.in.QuickFormInNodeDialogPane;

/**
 * Dialog to node.
 *
 * @author Peter Ohl, KNIME.com, Zurich, Switzerland
 */
final class DoubleInputQuickFormInNodeDialogPane
    extends QuickFormInNodeDialogPane<DoubleInputQuickFormInConfiguration> {

    private final JTextField m_valueField;

    /** Constructors, inits fields calls layout routines. */
    DoubleInputQuickFormInNodeDialogPane() {
        m_valueField = new JTextField(DEF_TEXTFIELD_WIDTH);
        m_valueField.setText("0.0");
        createAndAddTab();
    }

    /** {@inheritDoc} */
    @Override
    protected DoubleInputQuickFormInConfiguration createConfiguration() {
        return new DoubleInputQuickFormInConfiguration();
    }

    /** {@inheritDoc} */
    @Override
    protected void fillPanel(final JPanel panelWithGBLayout,
            final GridBagConstraints gbc) {
        addPairToPanel("Double Value: ", m_valueField, panelWithGBLayout, gbc);
    }

    /** {@inheritDoc} */
    @Override
    protected void saveAdditionalSettings(
            final DoubleInputQuickFormInConfiguration config)
            throws InvalidSettingsException {
        Double d;
        try {
            d = Double.parseDouble(m_valueField.getText());
        } catch (NumberFormatException e) {
            throw new InvalidSettingsException(
                    "Please provide a valid double value.");
        }
        config.getValueConfiguration().setValue(d);
    }

    /** {@inheritDoc} */
    @Override
    protected void loadAdditionalSettings(
            final DoubleInputQuickFormInConfiguration config) {
        Double d = config.getValueConfiguration().getValue();
        if (d == null) {
            d = new Double(0.0);
        }
        m_valueField.setText(d.toString());
    }
}
