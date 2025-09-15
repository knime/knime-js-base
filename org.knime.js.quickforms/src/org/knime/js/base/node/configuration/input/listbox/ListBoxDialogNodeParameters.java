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
 *   Jan 15, 2025 (user): created
 */
package org.knime.js.base.node.configuration.input.listbox;

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
import static org.knime.js.base.node.base.input.string.StringNodeConfig.DEFAULT_ERROR_MESSAGE;
import static org.knime.js.base.node.base.input.string.StringNodeConfig.DEFAULT_REGEX;

import org.knime.js.base.node.base.input.listbox.ListBoxNodeConfig;
import org.knime.js.base.node.configuration.ConfigurationNodeSettings;
import org.knime.js.base.node.configuration.OverwrittenByValueMessage;
import org.knime.node.parameters.NodeParameters;
import org.knime.node.parameters.Widget;
import org.knime.node.parameters.layout.After;
import org.knime.node.parameters.layout.Before;
import org.knime.node.parameters.layout.Layout;
import org.knime.node.parameters.layout.Section;
import org.knime.node.parameters.persistence.Persist;
import org.knime.node.parameters.updates.Effect;
import org.knime.node.parameters.updates.Effect.EffectType;
import org.knime.node.parameters.updates.EffectPredicate;
import org.knime.node.parameters.updates.EffectPredicateProvider;
import org.knime.node.parameters.updates.ParameterReference;
import org.knime.node.parameters.updates.ValueReference;
import org.knime.node.parameters.widget.message.TextMessage;
import org.knime.node.parameters.widget.number.NumberInputWidget;
import org.knime.node.parameters.widget.number.NumberInputWidgetValidation.MinValidation.IsPositiveIntegerValidation;
import org.knime.node.parameters.widget.text.TextAreaWidget;

/**
 * Settings for the list box configuration node.
 *
 * @author Christian Albrecht, KNIME GmbH, Konstanz, Germany
 */
@SuppressWarnings("restriction")
public final class ListBoxDialogNodeParameters extends ConfigurationNodeSettings {

    /**
     * Default constructor
     */
    public ListBoxDialogNodeParameters() {
        super(ListBoxInputDialogNodeConfig.class);
    }

    @Section(title = "Validation")
    @After(FormFieldSection.class)
    @Before(OutputSection.class)
    interface ValidationSection {
    }

    @TextMessage(ListBoxOverwrittenByValueMessage.class)
    @Layout(OutputSection.Top.class)
    Void m_overwrittenByValueMessage;

    static final class ListBoxOverwrittenByValueMessage extends OverwrittenByValueMessage<ListBoxDialogNodeValue> {

        @Override
        protected String valueToString(final ListBoxDialogNodeValue value) {
            return value.getString();
        }

    }

    static final class DefaultValue implements NodeParameters {
        @Widget(title = "Default value", description = "Default value for the field.")
        @TextAreaWidget
        String m_string = "";
    }

    @Layout(OutputSection.Top.class)
    DefaultValue m_defaultValue = new DefaultValue();

    @Widget(title = "Number of visible options",
        description = "The number of options visible in the list box without scrolling. Changing this value will also"
            + " affect the component's height.")
    @NumberInputWidget(minValidation = IsPositiveIntegerValidation.class)
    @Layout(FormFieldSection.class)
    @Persist(configKey = ListBoxNodeConfig.CFG_NUMBER_VIS_OPTIONS)
    int m_numberVisibleOptions = ListBoxNodeConfig.DEFAULT_NUMBER_VIS_OPTIONS;

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
    @Persist(configKey = ListBoxNodeConfig.CFG_REGEX)
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
    @Persist(configKey = ListBoxNodeConfig.CFG_ERROR_MESSAGE)
    String m_errorMessage = DEFAULT_ERROR_MESSAGE;

    @Widget(title = "Separate each character",
        description = "If checked, each character in the input will be treated as a separate option.")
    @Layout(OutputSection.Top.class)
    @Persist(configKey = ListBoxNodeConfig.CFG_SEPARATE_EACH_CHARACTER)
    @ValueReference(SeparateEachCharValueReference.class)
    boolean m_separateEachCharacter = ListBoxNodeConfig.DEFAULT_SEPARATE_EACH_CHARACTER;

    @Widget(title = "Separator", description = "The separator string used to split the options.")
    @Layout(OutputSection.Top.class)
    @Persist(configKey = ListBoxNodeConfig.CFG_SEPARATOR)
    @Effect(predicate = IsSeparateEachChar.class, type = EffectType.HIDE)
    String m_separator = ListBoxNodeConfig.DEFAULT_SEPARATOR;

    @Persist(configKey = ListBoxNodeConfig.CFG_SEPARATOR_REGEX)
    String m_separatorRegex = ListBoxNodeConfig.DEFAULT_SEPARATOR_REGEX;

    @Widget(title = "Omit empty values",
        description = "If checked, empty values will be omitted from the list of options and do not have to pass the "
            + "validation check.")
    @Layout(OutputSection.Top.class)
    @Persist(configKey = ListBoxNodeConfig.CFG_OMIT_EMPTY)
    boolean m_omitEmpty = ListBoxNodeConfig.DEFAULT_OMIT_EMPTY;

    static final class SeparateEachCharValueReference implements ParameterReference<Boolean> {
    }

    static final class IsSeparateEachChar implements EffectPredicateProvider {
        @Override
        public EffectPredicate init(final PredicateInitializer i) {
            return i.getBoolean(SeparateEachCharValueReference.class).isTrue();
        }
    }
}
