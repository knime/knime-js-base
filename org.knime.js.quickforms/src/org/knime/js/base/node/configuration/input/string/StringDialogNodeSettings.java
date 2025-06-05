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
 *   Jun 4, 2025 (marcbux): created
 */
package org.knime.js.base.node.configuration.input.string;

import static org.knime.js.base.node.base.LabeledConfig.DEFAULT_DESCRIPTION;
import static org.knime.js.base.node.base.LabeledConfig.DEFAULT_LABEL;
import static org.knime.js.base.node.base.LabeledConfig.DEFAULT_REQUIRED;
import static org.knime.js.base.node.base.input.string.RegexPanel.EMAIL_ERROR;
import static org.knime.js.base.node.base.input.string.RegexPanel.EMAIL_LABEL;
import static org.knime.js.base.node.base.input.string.RegexPanel.EMAIL_REGEX;
import static org.knime.js.base.node.base.input.string.RegexPanel.IPV4_ERROR;
import static org.knime.js.base.node.base.input.string.RegexPanel.IPV4_LABEL;
import static org.knime.js.base.node.base.input.string.RegexPanel.IPV4_REGEX;
import static org.knime.js.base.node.base.input.string.RegexPanel.URL_ERROR;
import static org.knime.js.base.node.base.input.string.RegexPanel.URL_LABEL;
import static org.knime.js.base.node.base.input.string.RegexPanel.URL_REGEX;
import static org.knime.js.base.node.base.input.string.RegexPanel.WIN_FILE_PATH_ERROR;
import static org.knime.js.base.node.base.input.string.RegexPanel.WIN_FILE_PATH_LABEL;
import static org.knime.js.base.node.base.input.string.StringNodeConfig.CFG_EDITOR_TYPE;
import static org.knime.js.base.node.base.input.string.StringNodeConfig.CFG_ERROR_MESSAGE;
import static org.knime.js.base.node.base.input.string.StringNodeConfig.DEFAULT_EDITOR_TYPE;
import static org.knime.js.base.node.base.input.string.StringNodeConfig.DEFAULT_ERROR_MESSAGE;
import static org.knime.js.base.node.base.input.string.StringNodeConfig.DEFAULT_MULTI_LINE_EDITOR_HEIGHT;
import static org.knime.js.base.node.base.input.string.StringNodeConfig.DEFAULT_REGEX;
import static org.knime.js.base.node.base.input.string.StringNodeConfig.EDITOR_TYPE_MULTI_LINE_STRING;
import static org.knime.js.base.node.base.input.string.StringNodeConfig.EDITOR_TYPE_SINGLE_LINE_STRING;
import static org.knime.js.base.node.configuration.input.string.StringDialogNodeSettings.EditorType.SINGLE_LINE;
import static org.knime.js.base.node.configuration.input.string.StringInputDialogNodeConfig.DEFAULT_EDITOR_WIDTH;

import java.util.function.Supplier;

import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.dialog.DialogNode;
import org.knime.core.node.workflow.SubNodeContainer;
import org.knime.core.webui.node.dialog.defaultdialog.DefaultNodeSettings;
import org.knime.core.webui.node.dialog.defaultdialog.layout.After;
import org.knime.core.webui.node.dialog.defaultdialog.layout.Layout;
import org.knime.core.webui.node.dialog.defaultdialog.layout.Section;
import org.knime.core.webui.node.dialog.defaultdialog.persistence.api.NodeSettingsPersistor;
import org.knime.core.webui.node.dialog.defaultdialog.persistence.api.Persist;
import org.knime.core.webui.node.dialog.defaultdialog.persistence.api.Persistor;
import org.knime.core.webui.node.dialog.defaultdialog.util.updates.StateComputationFailureException;
import org.knime.core.webui.node.dialog.defaultdialog.widget.Label;
import org.knime.core.webui.node.dialog.defaultdialog.widget.NumberInputWidget;
import org.knime.core.webui.node.dialog.defaultdialog.widget.ValueSwitchWidget;
import org.knime.core.webui.node.dialog.defaultdialog.widget.Widget;
import org.knime.core.webui.node.dialog.defaultdialog.widget.updates.Effect;
import org.knime.core.webui.node.dialog.defaultdialog.widget.updates.Effect.EffectType;
import org.knime.core.webui.node.dialog.defaultdialog.widget.updates.Predicate;
import org.knime.core.webui.node.dialog.defaultdialog.widget.updates.PredicateProvider;
import org.knime.core.webui.node.dialog.defaultdialog.widget.updates.Reference;
import org.knime.core.webui.node.dialog.defaultdialog.widget.updates.StateProvider;
import org.knime.core.webui.node.dialog.defaultdialog.widget.updates.ValueProvider;
import org.knime.core.webui.node.dialog.defaultdialog.widget.updates.ValueReference;
import org.knime.core.webui.node.dialog.defaultdialog.widget.validation.NumberInputWidgetValidation.MinValidation.IsPositiveIntegerValidation;
import org.knime.js.base.node.base.LabeledConfig;
import org.knime.js.base.node.configuration.DialogNodeConfig;
import org.knime.js.base.node.configuration.input.string.StringDialogNodeSettings.EditorType.EditorTypePersistor;

/**
 * Settings for the string configuration node.
 *
 * @author Marc Bux, KNIME GmbH, Berlin, Germany
 */
@SuppressWarnings("restriction")
public final class StringDialogNodeSettings implements DefaultNodeSettings {

    @Section(title = "Form Field")
    interface FormFieldSection {
    }

    @Section(title = "Validation")
    @After(FormFieldSection.class)
    @Effect(predicate = EditorType.IsSingleLine.class, type = EffectType.SHOW)
    interface ValidationSection {
    }

    @Section(title = "Output")
    @After(ValidationSection.class)
    interface OutputSection {
    }

    @Section(title = "Advanced Settings", advanced = true)
    @After(OutputSection.class)
    interface AdvancedSettingsSection {
    }

    // the default value whose type is specific to the node

    static final class DefaultValue implements DefaultNodeSettings {
        @Widget(title = "Default value",
            description = "Default value for the field. If empty, no default value will be set.")
        String m_string = "";
    }

    @Layout(OutputSection.class)
    DefaultValue m_defaultValue = new DefaultValue();

    // settings common to all configuration nodes

    @Widget(title = "Label", description = """
            Some lines of description that will be shown for instance in the node description of
            the component exposing a dialog.
            """)
    @Layout(FormFieldSection.class)
    String m_label = DEFAULT_LABEL;

    @Widget(title = "Description", description = "Description shown in the dialog and node description.")
    @Layout(FormFieldSection.class)
    String m_description = DEFAULT_DESCRIPTION;

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

    @Widget(title = "Output variable name", description = """
            Parameter identifier for external parameterization (e.g. batch execution).
            This will also be the name of the exported flow variable.
            """)
    @Layout(OutputSection.class)
    @ValueReference(FlowVariableNameRef.class)
    String m_flowVariableName =
        // see DialogNodeConfig.m_parameterName
        SubNodeContainer.getDialogNodeParameterNameDefault(StringInputDialogNodeConfig.class);

    /**
     * See {@link DialogNode#getParameterName()} .
     */
    @Widget(title = "Parameter name", description = """
            A simple name that is associated with this node for external parameterization. This is for instance \
            used in command line control or when parameters are set via a web service invocation (that is, the \
            workflow itself is the web service implementation). The returned value must not be null. An empty \
            string is discouraged and only used for backward compatibility reasons (workflows saved prior 2.12 do \
            not have this property) \
                    """, advanced = true)
    @Layout(AdvancedSettingsSection.class)
    @ValueProvider(FlowVariableNameStateProvider.class)
    String m_parameterName =
        // see DialogNodeConfig.m_parameterName
        SubNodeContainer.getDialogNodeParameterNameDefault(StringInputDialogNodeConfig.class);

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

    // settings specific to the StringDialogNode

    enum EditorType {
            @Label("Single-line")
            SINGLE_LINE, //
            @Label("Multi-line")
            MULTI_LINE;

        static final class Ref implements Reference<EditorType> {
        }

        static final class IsSingleLine implements PredicateProvider {
            @Override
            public Predicate init(final PredicateInitializer i) {
                return i.getEnum(Ref.class).isOneOf(SINGLE_LINE);
            }
        }

        static final class IsMultiLine implements PredicateProvider {
            @Override
            public Predicate init(final PredicateInitializer i) {
                return i.getEnum(Ref.class).isOneOf(MULTI_LINE);
            }
        }

        static final class EditorTypePersistor implements NodeSettingsPersistor<EditorType> {
            @Override
            public EditorType load(final NodeSettingsRO settings) {
                final var editorType = settings.getString(CFG_EDITOR_TYPE, DEFAULT_EDITOR_TYPE);
                if (EDITOR_TYPE_MULTI_LINE_STRING.equals(editorType)) {
                    return MULTI_LINE;
                }
                return SINGLE_LINE;
            }

            @Override
            public void save(final EditorType editorType, final NodeSettingsWO settings) {
                settings.addString(CFG_EDITOR_TYPE,
                    editorType == MULTI_LINE ? EDITOR_TYPE_MULTI_LINE_STRING : EDITOR_TYPE_SINGLE_LINE_STRING);
            }

            @Override
            public String[][] getConfigPaths() {
                return new String[][]{{CFG_EDITOR_TYPE}};
            }
        }
    }

    @Widget(title = "Field type", description = "Choose between single-line or multi-line text input.")
    @ValueSwitchWidget
    @Layout(FormFieldSection.class)
    @Persistor(EditorTypePersistor.class)
    @ValueReference(EditorType.Ref.class)
    EditorType m_editorType = SINGLE_LINE;

    @Widget(title = "Field width (legacy)", description = "The width of the editor in number of characters per line.",
        advanced = true)
    @NumberInputWidget(minValidation = IsPositiveIntegerValidation.class)
    @Layout(FormFieldSection.class)
    int m_multilineEditorWidth = DEFAULT_EDITOR_WIDTH;

    @Widget(title = "Field height",
        description = "Height of the editor in number of text lines. Multi-line editor only.")
    @NumberInputWidget(minValidation = IsPositiveIntegerValidation.class)
    @Layout(FormFieldSection.class)
    @Effect(predicate = EditorType.IsMultiLine.class, type = EffectType.SHOW)
    int m_multilineEditorHeight = DEFAULT_MULTI_LINE_EDITOR_HEIGHT;

    // HTML-escaped version of RegexPanel.WIN_FILE_PATH_REGEX
    private static final String WIN_FILE_PATH_REGEX =
        "^((\\\\\\\\[a-zA-Z0-9-]+\\\\[a-zA-Z0-9`~!@#$%^&amp;(){}'._-]+([ ]+[a-zA-Z0-9`~!@#$%^&amp;(){}'._-]+)*)"
            + "|([a-zA-Z]:))(\\\\[^ \\\\/:*?&quot;&quot;&lt;&gt;|]+([ ]+[^ \\\\/:*?&quot;&quot;&lt;&gt;|]+)*)*\\\\?$";

    @Widget(title = "Regex pattern", description = """
            Regular expression defining valid values.
            Single-line editor only.
            Common regex patterns are as follows:
            """ //
        + "<ul>" //
        + "<li><b>" + EMAIL_LABEL + "</b>: " + EMAIL_REGEX + "</li>" //
        + "<li><b>" + URL_LABEL + "</b>: " + URL_REGEX + "</li>" //
        + "<li><b>" + IPV4_LABEL + "</b>: " + IPV4_REGEX + "</li>" //
        + "<li><b>" + WIN_FILE_PATH_LABEL + "</b>: " + WIN_FILE_PATH_REGEX + "</li>" //
        + "</ul>")
    @Layout(ValidationSection.class)
    String m_regex = DEFAULT_REGEX;

    @Widget(title = "Failure message", description = """
            Message shown if the value is not valid.
            '?' will be replaced by the invalid value.
            Single-line editor only.
            Failure messages corresponding to common regex patterns are as follows:
            """ //
        + "<ul>" //
        + "<li><b>" + EMAIL_LABEL + "</b>: " + EMAIL_ERROR + "</li>" //
        + "<li><b>" + URL_LABEL + "</b>: " + URL_ERROR + "</li>" //
        + "<li><b>" + IPV4_LABEL + "</b>: " + IPV4_ERROR + "</li>" //
        + "<li><b>" + WIN_FILE_PATH_LABEL + "</b>: " + WIN_FILE_PATH_ERROR + "</li>" //
        + "</ul>")
    @Layout(ValidationSection.class)
    @Persist(configKey = CFG_ERROR_MESSAGE)
    String m_errorMessage = DEFAULT_ERROR_MESSAGE;
}
