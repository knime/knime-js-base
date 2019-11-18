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
 *   Nov 15, 2019 (Adrian Nembach, KNIME GmbH, Konstanz, Germany): created
 */
package org.knime.js.base.node.base.validation.min.row;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import org.knime.js.base.node.base.validation.AbstractValidatorDialog;
import org.knime.js.base.node.base.validation.DialogElement;
import org.knime.js.base.node.base.validation.ValidatorDialog;

/**
 * The {@link ValidatorDialog} for the {@link MinNumRowsValidatorFactory}.
 *
 * @author Adrian Nembach, KNIME GmbH, Konstanz, Germany
 */
final class MinNumRowsDialog extends AbstractValidatorDialog<MinNumRowsConfig> {

    private static JSpinner createLongSpinner(final Long value, final Long min, final Long max, final Long step) {
        return new JSpinner(new SpinnerNumberModel(value, min, max, step));
    }

    private final JSpinner m_minNumRows = createLongSpinner(0L, 0L, Long.MAX_VALUE, 1L);

    @Override
    public JPanel getPanel() {
        final JPanel panel = new JPanel(new GridBagLayout());
        final GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.LINE_START;
        panel.add(new JLabel("Minimum number of rows: "), gbc);
        gbc.gridx++;
        panel.add(m_minNumRows, gbc);
        gbc.gridx++;
        panel.add(new JLabel("Error message: "), gbc);
        gbc.gridx++;
        panel.add(m_errorMessage, gbc);
        return panel;
    }

    @Override
    public void load(final MinNumRowsConfig config) {
        super.load(config);
        m_minNumRows.setValue(config.getMinNumRows());
    }

    @Override
    public void save(final MinNumRowsConfig config) {
        super.save(config);
        config.setMinNumRows((long)m_minNumRows.getValue());
    }

    @Override
    public Collection<DialogElement> getDialogElements() {
        final ArrayList<DialogElement> elements = new ArrayList<>();
        elements.add(DialogElement.createLabeledElement("Minimum number of rows: ", m_minNumRows));
        elements.addAll(super.getDialogElements());
        return elements;
    }

}
