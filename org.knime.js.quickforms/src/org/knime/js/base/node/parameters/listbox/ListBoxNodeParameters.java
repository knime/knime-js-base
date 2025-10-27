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
 *   27 Oct 2025 (Robin Gerling): created
 */
package org.knime.js.base.node.parameters.listbox;

import org.knime.core.webui.node.dialog.defaultdialog.internal.widget.PersistWithin;
import org.knime.js.base.node.base.input.listbox.ListBoxNodeConfig;
import org.knime.js.base.node.base.input.listbox.ListBoxNodeValue;
import org.knime.js.base.node.parameters.ConfigurationAndWidgetNodeParametersUtil.FormFieldSection;
import org.knime.js.base.node.parameters.ConfigurationAndWidgetNodeParametersUtil.OutputSection;
import org.knime.js.base.node.parameters.OverwrittenByValueMessage;
import org.knime.js.base.node.parameters.text.TextValidationParameters;
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
 * The node parameters for configuration and widget nodes which use the {@link ListBoxNodeValue}.
 *
 * @author Robin Gerling
 */
@SuppressWarnings("restriction")
public final class ListBoxNodeParameters implements NodeParameters {

    @Section(title = "Validation")
    @After(FormFieldSection.class)
    @Before(OutputSection.class)
    interface ValidationSection {
    }

    @TextMessage(ListBoxOverwrittenByValueMessage.class)
    @Layout(OutputSection.Top.class)
    Void m_overwrittenByValueMessage;

    static final class ListBoxOverwrittenByValueMessage extends OverwrittenByValueMessage<ListBoxNodeValue> {

        @Override
        protected String valueToString(final ListBoxNodeValue value) {
            return value.getString();
        }

    }

    static final class DefaultValue implements NodeParameters {
        @Widget(title = "Default value", description = "The value that is used by default in the text area.")
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

    @Layout(ValidationSection.class)
    @PersistWithin.PersistEmbedded
    TextValidationParameters m_textValidationParameters = new TextValidationParameters();

    @Widget(title = "Separate at each character",
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
