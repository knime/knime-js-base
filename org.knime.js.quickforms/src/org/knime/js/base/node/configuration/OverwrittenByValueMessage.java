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
 *   Jun 10, 2025 (Paul Bärnreuther): created
 */
package org.knime.js.base.node.configuration;

import java.util.Optional;

import org.knime.core.webui.node.dialog.defaultdialog.DefaultNodeSettings.DefaultNodeSettingsContext;
import org.knime.core.webui.node.dialog.defaultdialog.util.updates.StateComputationFailureException;
import org.knime.core.webui.node.dialog.defaultdialog.widget.TextMessage;
import org.knime.core.webui.node.dialog.defaultdialog.widget.TextMessage.Message;
import org.knime.core.webui.node.dialog.defaultdialog.widget.TextMessage.MessageType;
import org.knime.core.webui.node.dialog.defaultdialog.widget.updates.StateProvider;

/**
 * A message shown in configuration dialogs whenever the current value of the configuration comes from saved settings of
 * the containing component.
 *
 * @author Paul Bärnreuther
 * @param <VAL> the type of the value of this configuration.
 */
@SuppressWarnings("restriction")
public abstract class OverwrittenByValueMessage<VAL> implements StateProvider<Optional<TextMessage.Message>> {

    @Override
    public void init(final StateProviderInitializer initializer) {
        initializer.computeAfterOpenDialog();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Optional<Message> computeState(final DefaultNodeSettingsContext context)
        throws StateComputationFailureException {
        final var currentValue = context.getDialogNode().getDialogValue();
        if (currentValue == null) {
            return Optional.empty();
        } else {
            return Optional.of(new Message("Value overwritten by dialog.",
                String.format("Current value: %s", valueToString((VAL)currentValue)), MessageType.INFO));
        }
    }

    /**
     * Converts the given value to a string representation.
     *
     * @param value the current value to convert.
     * @return the string representation of the value.
     */
    protected abstract String valueToString(VAL value);

}
