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
package org.knime.js.base.node.parameters;

import java.util.Optional;

import org.knime.core.webui.node.dialog.defaultdialog.NodeParametersInputImpl;
import org.knime.core.webui.node.dialog.defaultdialog.util.updates.StateComputationFailureException;
import org.knime.js.base.node.widget.WidgetNodeModel;
import org.knime.node.parameters.NodeParametersInput;
import org.knime.node.parameters.updates.StateProvider;
import org.knime.node.parameters.widget.message.TextMessage;
import org.knime.node.parameters.widget.message.TextMessage.Message;
import org.knime.node.parameters.widget.message.TextMessage.MessageType;

/**
 * A message shown in configuration/widget dialogs whenever the current value of the configuration/widget comes from
 * saved settings of the containing component.
 *
 * @author Paul Bärnreuther
 * @param <V> the type of the value of this configuration/widget.
 */
@SuppressWarnings("restriction")
public abstract class OverwrittenByValueMessage<V> implements StateProvider<Optional<TextMessage.Message>> {

    @Override
    public void init(final StateProviderInitializer initializer) {
        initializer.computeAfterOpenDialog();
    }

    @Override
    public final Optional<Message> computeState(final NodeParametersInput context)
        throws StateComputationFailureException {
        final var currentValue = getCurrentValue(context);
        if (currentValue == null) {
            return Optional.empty();
        } else {
            return Optional.of(new Message("Value overwritten by dialog.",
                String.format("Current value: %s", valueToString(currentValue)), MessageType.INFO));
        }
    }

    /**
     * Either getDialogNode or getWizardNode must not throw a NullPointerException as this state provider is and should
     * only be used in configuration/widget nodes. <br>
     * Having them combined helps to deduplicate {@link #valueToString} between configuration and widget nodes.
     */
    @SuppressWarnings("unchecked")
    private final V getCurrentValue(final NodeParametersInput context) {
        try {
            return (V)((NodeParametersInputImpl)context).getDialogNode().getDialogValue();
        } catch (NullPointerException e) { //NOSONAR
            if (((NodeParametersInputImpl)context).getWizardNode() instanceof WidgetNodeModel widgetNodeModel) {
                return (V)(widgetNodeModel.hasOverwrittenDefaultValue() ? widgetNodeModel.getViewValue() : null);
            }
        }
        throw new IllegalStateException(
            "This state provider should only be used in widget nodes extending the WidgetNodeModel or in configuration"
                + " nodes extending the DialogNodeModel.");
    }

    /**
     * Converts the given value to a string representation.
     *
     * @param value the current value to convert.
     * @return the string representation of the value.
     */
    protected abstract String valueToString(V value);

}
