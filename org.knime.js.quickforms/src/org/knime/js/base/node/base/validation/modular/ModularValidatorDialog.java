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
 *   Nov 14, 2019 (Adrian Nembach, KNIME GmbH, Konstanz, Germany): created
 */
package org.knime.js.base.node.base.validation.modular;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.knime.js.base.node.base.validation.DialogElement;
import org.knime.js.base.node.base.validation.ValidatorConfig;
import org.knime.js.base.node.base.validation.ValidatorDialog;
import org.knime.js.base.node.base.validation.ValidatorFactory;

/**
 * The {@link ValidatorDialog} for a {@link ModularValidatorFactory}.
 * It combines all dialogs of the {@link ValidatorFactory factories} managed by a {@link ModularValidatorFactory}.
 * The dialogs are combined by retrieving the {@link DialogElement DialogElements} from the individual dialogs and
 * putting them into a common {@link GridBagLayout}.
 *
 * @author Adrian Nembach, KNIME GmbH, Konstanz, Germany
 */
final class ModularValidatorDialog implements ValidatorDialog<ModularValidatorConfig> {

    private final List<ValidatorDialog<?>> m_dialogs;

    ModularValidatorDialog(final List<ValidatorDialog<?>> dialogs) {
        m_dialogs = dialogs;
    }

    @Override
    public JPanel getPanel() {
        final JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder(""));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(5, 5, 5, 5);
        for (ValidatorDialog<?> dialog : m_dialogs) {
            for (DialogElement element : dialog.getDialogElements()) {
                final Optional<String> label = element.getLabel();
                int xIncrement = 1;
                if (label.isPresent()) {
                    panel.add(new JLabel(label.get()), gbc);
                    gbc.gridx++;
                } else {
                    gbc.gridwidth = 2;
                    xIncrement++;
                }
                panel.add(element.getComponent(), gbc);
                gbc.gridx += xIncrement;
                gbc.gridwidth = 1;
            }
            gbc.gridx = 0;
            gbc.gridy++;
        }
        return panel;
    }

    @Override
    public void load(final ModularValidatorConfig config) {
        mapToDialogs(config, (d, c) -> load(d, c));
    }

    @Override
    public void save(final ModularValidatorConfig config) {
        mapToDialogs(config, (d, c) -> save(d, c));
    }

    @SuppressWarnings("unchecked")
    private static void load(final ValidatorDialog<?> dialog, final ValidatorConfig config) {
        @SuppressWarnings("rawtypes") // necessary to make the next call
        final ValidatorDialog unsafe = dialog;
        // safe because dialog and config are created by the same factory
        unsafe.load(config);
    }

    @SuppressWarnings("unchecked")
    private static void save(final ValidatorDialog<?> dialog, final ValidatorConfig config) {
        @SuppressWarnings("rawtypes") // necessary to make the next call
        final ValidatorDialog unsafe = dialog;
        // safe because dialog and config are created by the same factory
        unsafe.save(config);
    }

    private void mapToDialogs(final ModularValidatorConfig config,
        final BiConsumer<ValidatorDialog<?>, ValidatorConfig> consumer) {
        final Iterator<ValidatorDialog<?>> dialogs = m_dialogs.iterator();
        for (int i = 0; dialogs.hasNext(); i++) {
            final ValidatorConfig c = config.getConfig(i);
            // the config and dialog are created by the same factory so it should be safe to use them together
            final ValidatorDialog<?> dialog = dialogs.next();
            consumer.accept(dialog, c);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<DialogElement> getDialogElements() {
        throw new UnsupportedOperationException("The modular dialog doesn't support the getDialogElements operation.");
    }

}