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

import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.node.parameters.NodeParameters;
import org.knime.js.base.node.configuration.ConfigurationNodeSettings;
import org.knime.js.base.node.configuration.OverwrittenByValueMessage;
import org.knime.js.base.node.configuration.input.string.StringDialogNodeSettings.EditorType.EditorTypePersistor;
import org.knime.node.parameters.Widget;
import org.knime.node.parameters.layout.After;
import org.knime.node.parameters.layout.Before;
import org.knime.node.parameters.layout.Layout;
import org.knime.node.parameters.layout.Section;
import org.knime.node.parameters.persistence.NodeParametersPersistor;
import org.knime.node.parameters.persistence.Persist;
import org.knime.node.parameters.persistence.Persistor;
import org.knime.node.parameters.updates.Effect;
import org.knime.node.parameters.updates.EffectPredicate;
import org.knime.node.parameters.updates.EffectPredicateProvider;
import org.knime.node.parameters.updates.ParameterReference;
import org.knime.node.parameters.updates.ValueReference;
import org.knime.node.parameters.updates.Effect.EffectType;
import org.knime.node.parameters.widget.choices.Label;
import org.knime.node.parameters.widget.choices.ValueSwitchWidget;
import org.knime.node.parameters.widget.message.TextMessage;
import org.knime.node.parameters.widget.number.NumberInputWidget;
import org.knime.node.parameters.widget.number.NumberInputWidgetValidation.MinValidation.IsPositiveIntegerValidation;

/**
 * Settings for the string configuration node.
 *
 * @author Marc Bux, KNIME GmbH, Berlin, Germany
 */
@SuppressWarnings("restriction")
public final class StringDialogNodeSettings extends ConfigurationNodeSettings {

    /**
     * Default constructor
     */
    public StringDialogNodeSettings() {
        super(StringInputDialogNodeConfig.class);
    }

    @Section(title = "Validation")
    @After(FormFieldSection.class)
    @Before(OutputSection.class)
    @Effect(predicate = EditorType.IsSingleLine.class, type = EffectType.SHOW)
    interface ValidationSection {
    }

    // the default value whose type is specific to the node

    @TextMessage(StringOverwrittenByValueMessage.class)
    @Layout(OutputSection.Top.class)
    Void m_overwrittenByValueMessage;

    static final class StringOverwrittenByValueMessage extends OverwrittenByValueMessage<StringDialogNodeValue> {

        @Override
        protected String valueToString(final StringDialogNodeValue value) {
            return value.getString();
        }

    }

    static final class DefaultValue implements NodeParameters {
        @Widget(title = "Default value",
            description = "Default value for the field. If empty, no default value will be set.")
        String m_string = "";
    }

    @Layout(OutputSection.Top.class)
    DefaultValue m_defaultValue = new DefaultValue();

    // settings specific to the StringDialogNode

    enum EditorType {
            @Label("Single-line")
            SINGLE_LINE, //
            @Label("Multi-line")
            MULTI_LINE;

        static final class Ref implements ParameterReference<EditorType> {
        }

        static final class IsSingleLine implements EffectPredicateProvider {
            @Override
            public EffectPredicate init(final PredicateInitializer i) {
                return i.getEnum(Ref.class).isOneOf(SINGLE_LINE);
            }
        }

        static final class IsMultiLine implements EffectPredicateProvider {
            @Override
            public EffectPredicate init(final PredicateInitializer i) {
                return i.getEnum(Ref.class).isOneOf(MULTI_LINE);
            }
        }

        static final class EditorTypePersistor implements NodeParametersPersistor<EditorType> {
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
