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
 *   7 Jun 2025 (Robin Gerling): created
 */
package org.knime.js.base.node.configuration.input.bool;

import static org.knime.js.base.node.base.input.bool.BooleanNodeConfig.CFG_PUSH_INT_VAR;
import static org.knime.js.base.node.base.input.bool.BooleanNodeConfig.DEFAULT_PUSH_INT_VAR;

import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.node.parameters.NodeParameters;
import org.knime.js.base.node.configuration.ConfigurationNodeSettings;
import org.knime.js.base.node.configuration.OverwrittenByValueMessage;
import org.knime.js.base.node.configuration.input.bool.BooleanDialogNodeSettings.OutputType.OutputTypePersistor;
import org.knime.node.parameters.Widget;
import org.knime.node.parameters.layout.Layout;
import org.knime.node.parameters.persistence.NodeParametersPersistor;
import org.knime.node.parameters.persistence.Persistor;
import org.knime.node.parameters.widget.choices.Label;
import org.knime.node.parameters.widget.choices.ValueSwitchWidget;
import org.knime.node.parameters.widget.message.TextMessage;

/**
 * Settings for the boolean configuration node.
 *
 * @author Robin Gerling
 */
@SuppressWarnings("restriction")
public final class BooleanDialogNodeSettings extends ConfigurationNodeSettings {

    /**
     * Default constructor
     */
    public BooleanDialogNodeSettings() {
        super(BooleanInputDialogNodeConfig.class);
    }

    // the default value whose type is specific to the node

    @TextMessage(BooleanOverwrittenByValueMessage.class)
    @Layout(OutputSection.Top.class)
    Void m_overwrittenByValueMessage;

    static final class BooleanOverwrittenByValueMessage extends OverwrittenByValueMessage<BooleanDialogNodeValue> {

        @Override
        protected String valueToString(final BooleanDialogNodeValue value) {
            return String.valueOf(value.getBoolean());
        }

    }

    static final class DefaultValue implements NodeParameters {
        @Widget(title = "Default value",
            description = "Default value for the field. If empty, no default value will be set.")
        @Layout(OutputSection.Top.class)
        boolean m_boolean;
    }

    DefaultValue m_defaultValue = new DefaultValue();

    // settings specific to the BooleanDialogNode

    enum OutputType {
            /** Output as a value of type boolean */
            @Label("Boolean")
            BOOLEAN,
            /** Output as a value of type number */
            @Label("Number (Integer)")
            INTEGER;

        static final class OutputTypePersistor implements NodeParametersPersistor<OutputType> {
            @Override
            public OutputType load(final NodeSettingsRO settings) {
                final var pushIntVar = settings.getBoolean(CFG_PUSH_INT_VAR, DEFAULT_PUSH_INT_VAR);
                return pushIntVar ? INTEGER : BOOLEAN;
            }

            @Override
            public void save(final OutputType outputType, final NodeSettingsWO settings) {
                settings.addBoolean(CFG_PUSH_INT_VAR, outputType == INTEGER);
            }

            @Override
            public String[][] getConfigPaths() {
                return new String[][]{{CFG_PUSH_INT_VAR}};
            }
        }
    }

    @Widget(title = "Output type", description = "The type of the output variable.")
    @ValueSwitchWidget
    @Layout(OutputSection.Bottom.class)
    @Persistor(OutputTypePersistor.class)
    OutputType m_pushIntVar = OutputType.BOOLEAN;

}
