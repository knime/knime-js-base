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
package org.knime.js.base.node.configuration;

import static org.knime.js.base.node.base.LabeledConfig.DEFAULT_DESCRIPTION;
import static org.knime.js.base.node.base.LabeledConfig.DEFAULT_LABEL;
import static org.knime.js.base.node.base.LabeledConfig.DEFAULT_REQUIRED;

import java.util.function.Supplier;

import org.knime.core.node.dialog.DialogNode;
import org.knime.core.node.workflow.SubNodeContainer;
import org.knime.core.webui.node.dialog.defaultdialog.DefaultNodeSettings;
import org.knime.core.webui.node.dialog.defaultdialog.layout.After;
import org.knime.core.webui.node.dialog.defaultdialog.layout.Layout;
import org.knime.core.webui.node.dialog.defaultdialog.layout.Section;
import org.knime.core.webui.node.dialog.defaultdialog.util.updates.StateComputationFailureException;
import org.knime.core.webui.node.dialog.defaultdialog.widget.Advanced;
import org.knime.core.webui.node.dialog.defaultdialog.widget.Widget;
import org.knime.core.webui.node.dialog.defaultdialog.widget.updates.Reference;
import org.knime.core.webui.node.dialog.defaultdialog.widget.updates.StateProvider;
import org.knime.core.webui.node.dialog.defaultdialog.widget.updates.ValueProvider;
import org.knime.core.webui.node.dialog.defaultdialog.widget.updates.ValueReference;
import org.knime.js.base.node.base.LabeledConfig;

/**
 * This class specifies the common settings of configuration nodes.
 *
 * @author Robin Gerling
 */
@SuppressWarnings("restriction")
public abstract class ConfigurationNodeSettings implements DefaultNodeSettings {

    /**
     * Default constructor
     *
     * @param nodeConfigClass the nodeConfigClass to determine the default flow variable name from
     */
    protected ConfigurationNodeSettings(final Class<?> nodeConfigClass) {
        final var defaultParamName = SubNodeContainer.getDialogNodeParameterNameDefault(nodeConfigClass);
        m_flowVariableName = defaultParamName;
        m_parameterName = defaultParamName;
    }

    /**
     * The form field section of a configuration node
     */
    @Section(title = "Form Field")
    public interface FormFieldSection {
    }

    /**
     * The output section of a configuration node
     */
    @Section(title = "Output")
    @After(FormFieldSection.class)
    public interface OutputSection {
        /**
         * The elements at the top of the output section
         */
        interface Top {
        }

        /**
         * The elements at the bottom of the output section
         */
        @After(Top.class)
        interface Bottom {

        }
    }

    /**
     * The advanced settings section of a configuration node
     */
    @Section(title = "Advanced Settings")
    @Advanced
    @After(OutputSection.class)
    public interface AdvancedSettingsSection {
    }

    @Widget(title = "Label", description = """
            A descriptive label that will be shown for instance in \
            the node description of the component exposing a dialog.\
            """)
    @Layout(FormFieldSection.class)
    protected String m_label = DEFAULT_LABEL;

    @Widget(title = "Description", description = """
            Some lines of description that will be shown for instance in \
            the node description of the component exposing a dialog.\
            """)
    @Layout(FormFieldSection.class)
    protected String m_description = DEFAULT_DESCRIPTION;

    @Widget(title = "Output variable name", description = """
            Parameter identifier for external parameterization (e.g. batch execution).
            This will also be the name of the exported flow variable.
            """)
    @Layout(OutputSection.Bottom.class)
    @ValueReference(FlowVariableNameRef.class)
    protected String m_flowVariableName; // see DialogNodeConfig.m_parameterName

    /**
     * See {@link DialogNode#getParameterName()} .
     */
    @Widget(title = "Parameter name", description = """
            Parameter identifier for external parameterization (e.g. batch execution). \
            Whenever the output variable name is adjusted, the current value of the parameter \
            name is set to the same value.\
                    """, advanced = true)
    @Layout(AdvancedSettingsSection.class)
    @ValueProvider(FlowVariableNameStateProvider.class)
    protected String m_parameterName;

    /**
     * A left-over setting from the old nodes that appeared in data apps and in component dialog. See
     * {@link DialogNode#isHideInDialog()} .
     */
    @Widget(title = "Hide in dialog", description = """
            Set this to true to hide this field in a component dialog.
                     """, advanced = true)
    @Layout(AdvancedSettingsSection.class)
    boolean m_hideInDialog = DialogNodeConfig.DEFAULT_HIDE_IN_DIALOG;

    /**
     * See {@link LabeledConfig}. This setting was initially thought to be a useful feature to have, but it was never
     * implemented in any client. We probably want to remove it in the future, but if we do so now, the node model will
     * not be able to load the settings.
     */
    boolean m_required = DEFAULT_REQUIRED;

    /**
     * The reference to the string input containing the flow variable name
     */
    interface FlowVariableNameRef extends Reference<String> {
    }

    static final class FlowVariableNameStateProvider implements StateProvider<String> {
        private Supplier<String> m_flowVariableNameSupplier;

        @Override
        public void init(final StateProviderInitializer initializer) {
            m_flowVariableNameSupplier = initializer.computeFromValueSupplier(FlowVariableNameRef.class);
        }

        @Override
        public String computeState(final DefaultNodeSettingsContext context) throws StateComputationFailureException {
            return m_flowVariableNameSupplier.get();
        }
    }

}
