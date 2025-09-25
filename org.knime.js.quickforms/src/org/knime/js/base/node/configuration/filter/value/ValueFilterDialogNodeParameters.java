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
 *   23 Sept 2025 (Robin Gerling): created
 */
package org.knime.js.base.node.configuration.filter.value;

import static org.knime.js.base.node.configuration.MultipleSelectionAndFilterNodeParametersUtil.LIMIT_VIS_OPT_DESCRIPTION;
import static org.knime.js.base.node.configuration.MultipleSelectionAndFilterNodeParametersUtil.LIMIT_VIS_OPT_TITLE;
import static org.knime.js.base.node.configuration.MultipleSelectionAndFilterNodeParametersUtil.NUM_VIS_OPT_DESCRIPTION;
import static org.knime.js.base.node.configuration.MultipleSelectionAndFilterNodeParametersUtil.NUM_VIS_OPT_TITLE;
import static org.knime.js.base.node.configuration.MultipleSelectionAndFilterNodeParametersUtil.SELECTION_TYPE_DESCRIPTION;
import static org.knime.js.base.node.configuration.MultipleSelectionAndFilterNodeParametersUtil.SELECTION_TYPE_TITLE;
import static org.knime.js.base.node.configuration.value.ValueSelectionFilterDialogNodeParametersUtil.ASK_FOR_COLUMN_DESCRIPTION;
import static org.knime.js.base.node.configuration.value.ValueSelectionFilterDialogNodeParametersUtil.ASK_FOR_COLUMN_TITLE;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.knime.core.webui.node.dialog.defaultdialog.util.updates.StateComputationFailureException;
import org.knime.js.base.node.base.filter.value.ValueFilterNodeConfig;
import org.knime.js.base.node.base.filter.value.ValueFilterNodeValue;
import org.knime.js.base.node.configuration.ConfigurationNodeParametersUtility.IsMin2Validation;
import org.knime.js.base.node.configuration.ConfigurationNodeSettings;
import org.knime.js.base.node.configuration.MultipleSelectionAndFilterNodeParametersUtil.IsListOrTwinlistSelectionType;
import org.knime.js.base.node.configuration.MultipleSelectionAndFilterNodeParametersUtil.LimitNumberOfVisibleOptionsValueReference;
import org.knime.js.base.node.configuration.MultipleSelectionAndFilterNodeParametersUtil.SelectionTypeChoicesProvider;
import org.knime.js.base.node.configuration.MultipleSelectionAndFilterNodeParametersUtil.SelectionTypeValueReference;
import org.knime.js.base.node.configuration.MultipleSelectionAndFilterNodeParametersUtil.ShowNumberOfVisibleOptions;
import org.knime.js.base.node.configuration.OverwrittenByValueMessage;
import org.knime.js.base.node.configuration.value.ValueSelectionFilterDialogNodeParametersUtil.AbstractAskForColumnPersistor;
import org.knime.js.base.node.configuration.value.ValueSelectionFilterDialogNodeParametersUtil.AbstractDefaultColumnChoicesProvider;
import org.knime.js.base.node.configuration.value.ValueSelectionFilterDialogNodeParametersUtil.AbstractDefaultColumnValueProvider;
import org.knime.js.base.node.configuration.value.ValueSelectionFilterDialogNodeParametersUtil.AbstractDefaultValueChoicesProvider;
import org.knime.js.base.node.configuration.value.ValueSelectionFilterDialogNodeParametersUtil.AbstractPossibleValuesPersistor;
import org.knime.js.base.node.configuration.value.ValueSelectionFilterDialogNodeParametersUtil.AskForColumn;
import org.knime.js.base.node.configuration.value.ValueSelectionFilterDialogNodeParametersUtil.AskForColumnValueReference;
import org.knime.js.base.node.configuration.value.ValueSelectionFilterDialogNodeParametersUtil.DefaultColumnValueReference;
import org.knime.js.base.node.configuration.value.ValueSelectionFilterDialogNodeParametersUtil.NoColumnsAvailableMessage;
import org.knime.node.parameters.NodeParameters;
import org.knime.node.parameters.NodeParametersInput;
import org.knime.node.parameters.Widget;
import org.knime.node.parameters.layout.Layout;
import org.knime.node.parameters.persistence.Persist;
import org.knime.node.parameters.persistence.Persistor;
import org.knime.node.parameters.updates.Effect;
import org.knime.node.parameters.updates.Effect.EffectType;
import org.knime.node.parameters.updates.ParameterReference;
import org.knime.node.parameters.updates.StateProvider;
import org.knime.node.parameters.updates.ValueProvider;
import org.knime.node.parameters.updates.ValueReference;
import org.knime.node.parameters.widget.choices.ChoicesProvider;
import org.knime.node.parameters.widget.choices.Label;
import org.knime.node.parameters.widget.choices.StringChoice;
import org.knime.node.parameters.widget.choices.ValueSwitchWidget;
import org.knime.node.parameters.widget.choices.filter.TwinlistWidget;
import org.knime.node.parameters.widget.message.TextMessage;
import org.knime.node.parameters.widget.number.NumberInputWidget;

/**
 * WebUI Node Parameters for the Nominal Row Filter Configuration.
 *
 * @author Robin Gerling, KNIME GmbH, Konstanz
 */
@SuppressWarnings("restriction")
public class ValueFilterDialogNodeParameters extends ConfigurationNodeSettings {

    /**
     * Default constructor
     */
    protected ValueFilterDialogNodeParameters() {
        super(ValueFilterDialogNodeConfig.class);
    }

    enum IncludeExclude {
            @Label(value = "Enforce inclusion", description = "new values are added to the exclude list")
            EnforceInclusion, //
            @Label(value = "Enforce exclusion", description = "new values are added to the exclude list")
            EnforceExclusion;
    }

    @TextMessage(ValueFilterOverwrittenByValueMessage.class)
    @Layout(OutputSection.Top.class)
    Void m_overwrittenByValueMessage;

    private static final class DefaultValue implements NodeParameters {
        @TextMessage(NoColumnsAvailableMessage.class)
        @Layout(OutputSection.Top.class)
        Void m_noColumnsAvailableMessage;

        @Widget(title = "Default Column", description = "The column containing the values to filter.")
        @Layout(OutputSection.Top.class)
        @Persist(configKey = ValueFilterNodeValue.CFG_COLUMN)
        @ChoicesProvider(DefaultColumnChoicesProvider.class)
        @ValueProvider(DefaultColumnValueProvider.class)
        @ValueReference(DefaultColumnValueReference.class)
        String m_column = "";

        @Widget(title = "Default Values", description = "The values that are selected by default.")
        @Layout(OutputSection.Top.class)
        @Persist(configKey = ValueFilterNodeValue.CFG_VALUES)
        @ChoicesProvider(DefaultValuesChoicesProvider.class)
        @ValueProvider(DefaultValuesValueProvider.class)
        @ValueReference(DefaultValuesValueReference.class)
        @TwinlistWidget
        String[] m_values = new String[0];

        @ValueProvider(DefaultExcludesValueProvider.class)
        @ValueReference(DefaultExcludesValueReference.class)
        @Persist(configKey = ValueFilterDialogNodeValue.CFG_EXCLUDES)
        String[] m_excludes = new String[0];

        @Widget(title = "Enforce Option", description = "The enforcement policy for handling new values:")
        @Layout(OutputSection.Top.class)
        @Persist(configKey = ValueFilterDialogNodeValue.CFG_ENFORCE_OPT)
        @ValueReference(DefaultIncludeExcludeValueReference.class)
        @ValueSwitchWidget
        IncludeExclude m_enforceOption = IncludeExclude.EnforceInclusion;
    }

    DefaultValue m_defaultValue = new DefaultValue();

    @Widget(title = SELECTION_TYPE_TITLE, description = SELECTION_TYPE_DESCRIPTION)
    @ChoicesProvider(SelectionTypeChoicesProvider.class)
    @Persist(configKey = ValueFilterNodeConfig.CFG_TYPE)
    @ValueReference(SelectionTypeValueReference.class)
    @Layout(FormFieldSection.class)
    String m_selectionType = ValueFilterNodeConfig.DEFAULT_TYPE;

    @Widget(title = ASK_FOR_COLUMN_TITLE, description = ASK_FOR_COLUMN_DESCRIPTION)
    @Persistor(AskForColumnPersistor.class)
    @Layout(FormFieldSection.class)
    @ValueReference(AskForColumnValueReference.class)
    @ValueSwitchWidget
    AskForColumn m_askForColumn = AskForColumn.YES;

    @Widget(title = LIMIT_VIS_OPT_TITLE, description = LIMIT_VIS_OPT_DESCRIPTION)
    @Persist(configKey = ValueFilterNodeConfig.CFG_LIMIT_NUMBER_VIS_OPTIONS)
    @ValueReference(LimitNumberOfVisibleOptionsValueReference.class)
    @Effect(predicate = IsListOrTwinlistSelectionType.class, type = EffectType.SHOW)
    @Layout(FormFieldSection.class)
    boolean m_limitNumberOfVisibleOptions = ValueFilterNodeConfig.DEFAULT_LIMIT_NUMBER_VIS_OPTIONS;

    @Widget(title = NUM_VIS_OPT_TITLE, description = NUM_VIS_OPT_DESCRIPTION)
    @NumberInputWidget(minValidation = IsMin2Validation.class)
    @Persist(configKey = ValueFilterNodeConfig.CFG_NUMBER_VIS_OPTIONS)
    @Effect(predicate = ShowNumberOfVisibleOptions.class, type = EffectType.SHOW)
    @Layout(FormFieldSection.class)
    int m_numberOfVisibleOptions = ValueFilterNodeConfig.DEFAULT_NUMBER_VIS_OPTIONS;

    @Persistor(PossibleColumnValuesMapPersistor.class)
    @ValueProvider(PossibleColumnValuesMapChoicesProvider.class)
    @ValueReference(PossibleColumnValuesValueReference.class)
    Map<String, List<String>> m_possibleValues = new TreeMap<>();

    static final class PossibleColumnValuesMapChoicesProvider implements StateProvider<Map<String, List<String>>> {

        @Override
        public void init(final StateProviderInitializer initializer) {
            initializer.computeBeforeOpenDialog();
        }

        @Override
        public Map<String, List<String>> computeState(final NodeParametersInput parametersInput)
            throws StateComputationFailureException {
            final var tableSpec = parametersInput.getInTableSpec(0);
            return tableSpec.isEmpty() ? Map.of() : ValueFilterNodeConfig.getPossibleValues(tableSpec.get());
        }
    }

    private static final class DefaultColumnChoicesProvider extends AbstractDefaultColumnChoicesProvider {
        DefaultColumnChoicesProvider() {
            super(PossibleColumnValuesMapChoicesProvider.class);
        }
    }

    private static final class DefaultColumnValueProvider extends AbstractDefaultColumnValueProvider {
        DefaultColumnValueProvider() {
            super(DefaultColumnChoicesProvider.class);
        }
    }

    private static final class DefaultValuesChoicesProvider extends AbstractDefaultValueChoicesProvider {
        public DefaultValuesChoicesProvider() {
            super(PossibleColumnValuesMapChoicesProvider.class);
        }
    }

    private static final class DefaultValuesValueProvider implements StateProvider<String[]> {

        private Supplier<String[]> m_defaultValuesSupplier;

        private Supplier<String[]> m_defaultExcludesSupplier;

        private Supplier<List<StringChoice>> m_defaultValuesChoicesSupplier;

        @Override
        public void init(final StateProviderInitializer initializer) {
            initializer.computeAfterOpenDialog();
            m_defaultValuesSupplier = initializer.getValueSupplier(DefaultValuesValueReference.class);
            m_defaultExcludesSupplier = initializer.getValueSupplier(DefaultExcludesValueReference.class);
            m_defaultValuesChoicesSupplier = initializer.computeFromProvidedState(DefaultValuesChoicesProvider.class);
        }

        @Override
        public String[] computeState(final NodeParametersInput parametersInput)
            throws StateComputationFailureException {
            final var defaultValuesChoices = m_defaultValuesChoicesSupplier.get();
            if (defaultValuesChoices.isEmpty()) {
                return new String[0];
            }
            final var defaultValues = m_defaultValuesSupplier.get();
            final var defaultExcludes = m_defaultExcludesSupplier.get();
            final var possibleValueChoicesIds =
                defaultValuesChoices.stream().map(StringChoice::id).collect(Collectors.toSet());
            if (Arrays.stream(defaultValues).allMatch(possibleValueChoicesIds::contains)
                && Arrays.stream(defaultExcludes).allMatch(possibleValueChoicesIds::contains)) {
                return defaultValues;
            }
            return new String[0];
        }
    }

    private static final class DefaultExcludesValueProvider implements StateProvider<String[]> {

        private Supplier<String> m_defaultColumnSupplier;

        private Supplier<String[]> m_defaultValuesSupplier;

        private Supplier<Map<String, List<String>>> m_possibleColumnValuesSupplier;

        @Override
        public void init(final StateProviderInitializer initializer) {
            m_defaultColumnSupplier = initializer.getValueSupplier(DefaultColumnValueReference.class);
            m_defaultValuesSupplier = initializer.computeFromValueSupplier(DefaultValuesValueReference.class);
            m_possibleColumnValuesSupplier = initializer.getValueSupplier(PossibleColumnValuesValueReference.class);
        }

        @Override
        public String[] computeState(final NodeParametersInput parametersInput)
            throws StateComputationFailureException {
            final var defaultColumn = m_defaultColumnSupplier.get();
            final var possibleColumnValues = m_possibleColumnValuesSupplier.get();
            if (defaultColumn == null || !possibleColumnValues.containsKey(defaultColumn)
                || possibleColumnValues.get(defaultColumn).isEmpty()) {
                return new String[0];
            }
            final var defaultValues = Set.of(m_defaultValuesSupplier.get());
            final var possibleValues = possibleColumnValues.get(defaultColumn);
            return possibleValues.stream().filter(Predicate.not(defaultValues::contains)).toArray(String[]::new);
        }
    }

    static final class AskForColumnPersistor extends AbstractAskForColumnPersistor {
        public AskForColumnPersistor() {
            super(ValueFilterNodeConfig.CFG_LOCK_COLUMN);
        }
    }

    static final class PossibleColumnValuesMapPersistor extends AbstractPossibleValuesPersistor {
        PossibleColumnValuesMapPersistor() {
            super(ValueFilterNodeConfig.CFG_POSSIBLE_COLUMNS, ValueFilterNodeConfig.CFG_COL);
        }
    }

    private static final class PossibleColumnValuesValueReference
        implements ParameterReference<Map<String, List<String>>> {
    }

    private static final class DefaultValuesValueReference implements ParameterReference<String[]> {
    }

    private static final class DefaultExcludesValueReference implements ParameterReference<String[]> {
    }

    private static final class DefaultIncludeExcludeValueReference implements ParameterReference<IncludeExclude> {
    }

    private static final class ValueFilterOverwrittenByValueMessage
        extends OverwrittenByValueMessage<ValueFilterDialogNodeValue> {

        private Supplier<AskForColumn> m_askForColumnSupplier;

        @Override
        public void init(final StateProviderInitializer initializer) {
            super.init(initializer);
            m_askForColumnSupplier = initializer.computeFromValueSupplier(AskForColumnValueReference.class);
        }

        @Override
        protected String valueToString(final ValueFilterDialogNodeValue value) {
            final var values = Arrays.toString(value.getValues());
            return m_askForColumnSupplier.get() == AskForColumn.NO_USE_DEFAULT ? values
                : String.format("Column: %s; Value: %s", value.getColumn(), values);
        }

    }

}
