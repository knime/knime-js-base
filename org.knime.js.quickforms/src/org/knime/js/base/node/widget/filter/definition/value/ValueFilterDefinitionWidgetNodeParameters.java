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
 *   26 Nov 2025 (Robin Gerling): created
 */
package org.knime.js.base.node.widget.filter.definition.value;

import static org.knime.js.base.dialog.selection.multiple.MultipleSelectionsComponentFactory.COMBOBOX;
import static org.knime.js.base.dialog.selection.multiple.MultipleSelectionsComponentFactory.TWINLIST;
import static org.knime.js.base.dialog.selection.single.SingleSelectionComponentFactory.DROPDOWN;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.StringValue;
import org.knime.core.util.Pair;
import org.knime.core.webui.node.dialog.defaultdialog.internal.widget.PersistWithin;
import org.knime.core.webui.node.dialog.defaultdialog.util.updates.StateComputationFailureException;
import org.knime.core.webui.node.dialog.defaultdialog.widget.Modification;
import org.knime.js.base.dialog.selection.multiple.MultipleSelectionsComponentFactory;
import org.knime.js.base.dialog.selection.single.SingleSelectionComponentFactory;
import org.knime.js.base.node.parameters.ConfigurationAndWidgetNodeParametersUtil.FormFieldSection;
import org.knime.js.base.node.parameters.ConfigurationAndWidgetNodeParametersUtil.OutputSection;
import org.knime.js.base.node.parameters.filterandselection.LimitVisibleOptionsParameters;
import org.knime.js.base.node.parameters.filterandselection.LimitVisibleOptionsParameters.LimitVisibleOptionsParametersModifier;
import org.knime.js.base.node.widget.WidgetNodeParametersBase;
import org.knime.js.base.node.widget.filter.definition.RangeFilterWidgetNodeParameters;
import org.knime.js.base.node.widget.filter.definition.RangeFilterWidgetNodeParameters.FilterColumnReference;
import org.knime.node.parameters.NodeParametersInput;
import org.knime.node.parameters.Widget;
import org.knime.node.parameters.layout.After;
import org.knime.node.parameters.layout.Before;
import org.knime.node.parameters.layout.Layout;
import org.knime.node.parameters.layout.Section;
import org.knime.node.parameters.migration.LoadDefaultsForAbsentFields;
import org.knime.node.parameters.persistence.Persist;
import org.knime.node.parameters.persistence.Persistor;
import org.knime.node.parameters.persistence.legacy.EnumBooleanPersistor;
import org.knime.node.parameters.persistence.legacy.LegacyNameFilterPersistor;
import org.knime.node.parameters.updates.EffectPredicate;
import org.knime.node.parameters.updates.EffectPredicateProvider;
import org.knime.node.parameters.updates.ParameterReference;
import org.knime.node.parameters.updates.StateProvider;
import org.knime.node.parameters.updates.ValueProvider;
import org.knime.node.parameters.updates.ValueReference;
import org.knime.node.parameters.widget.choices.ChoicesProvider;
import org.knime.node.parameters.widget.choices.ColumnChoicesProvider;
import org.knime.node.parameters.widget.choices.Label;
import org.knime.node.parameters.widget.choices.StringChoice;
import org.knime.node.parameters.widget.choices.StringChoicesProvider;
import org.knime.node.parameters.widget.choices.TypedStringChoice;
import org.knime.node.parameters.widget.choices.ValueSwitchWidget;
import org.knime.node.parameters.widget.choices.filter.StringFilter;
import org.knime.node.parameters.widget.choices.util.DomainChoicesProvider;
import org.knime.node.parameters.widget.number.NumberInputWidgetValidation.MinValidation;

/**
 * WebUI Node Parameters for the Interactive Value Filter Widget.
 *
 * @author Robin Gerling, KNIME GmbH, Konstanz
 */
@SuppressWarnings("restriction")
@LoadDefaultsForAbsentFields
public final class ValueFilterDefinitionWidgetNodeParameters extends WidgetNodeParametersBase {

    @After(FormFieldSection.class)
    @Before(OutputSection.class)
    @Section(title = "Data")
    interface DataSection {
    }

    @PersistWithin.PersistEmbedded
    RangeFilterWidgetNodeParameters m_rangeFilterWidgetNodeParameters = new RangeFilterWidgetNodeParameters();

    @Widget(title = "Filter column",
        description = "Select the column to apply the filter definition to. The domain of possible values of "
            + "the column is used to configure the filter.")
    @ValueReference(FilterColumnReference.class)
    @ValueProvider(FilterColumnValueProvider.class)
    @ChoicesProvider(FilterColumnChoicesProvider.class)
    @Persist(configKey = ValueFilterDefinitionWidgetConfig.CFG_COLUMN)
    @Layout(DataSection.class)
    String m_column = "";

    @Widget(title = "Default values",
        description = "Select the default values to be used for filtering from the possible values of the chosen "
            + "filter column. If <i>Single Values</i> is selected only the first value in the includes list will "
            + "be present on the filter.")
    @ValueReference(DefaultValuesValueReference.class)
    @ValueProvider(DefaultValuesValueProvider.class)
    @ChoicesProvider(DefaultValuesChoicesProvider.class)
    @Persistor(DefaultValuesPersistor.class)
    @Layout(DataSection.class)
    StringFilter m_defaultValues = new StringFilter();

    @Widget(title = "Selection type",
        description = "Choose multiple to be able to select multiple nominal values for filtering and single if "
            + "only one value should be part of the filter at any given time. Depending on the selection here the "
            + "type field will offer different options for the display of the filter in the view.")
    @ValueReference(SelectionTypeReference.class)
    @Persistor(SelectionTypePersistor.class)
    @ValueSwitchWidget
    @Layout(FormFieldSection.class)
    SelectionType m_multipleOrSingleValues = SelectionType.MULTIPLE;

    @Widget(title = "Selection component type",
        description = "Select the display type of the filter. Depending on the selection of "
            + "multiple / single values, different options are offered.")
    @Persist(configKey = ValueFilterDefinitionWidgetConfig.CFG_TYPE)
    @ValueReference(SelectionComponentTypeReference.class)
    @ValueProvider(TypeValueProvider.class)
    @ChoicesProvider(TypeChoicesProvider.class)
    @Layout(FormFieldSection.class)
    String m_type = TWINLIST;

    @PersistWithin.PersistEmbedded
    @Layout(FormFieldSection.class)
    @Modification(LimitVisibleOptionsModification.class)
    LimitVisibleOptionsParameters m_limitVisibleOptions =
        new LimitVisibleOptionsParameters(ValueFilterDefinitionWidgetConfig.DEFAULT_NUMBER_VIS_OPTIONS);

    enum SelectionType {
            @Label(value = "Multiple", description = "Allow selection of multiple values.")
            MULTIPLE, //
            @Label(value = "Single", description = "Allow selection of a single value only.")
            SINGLE;
    }

    private static final class SelectionTypePersistor extends EnumBooleanPersistor<SelectionType> {
        SelectionTypePersistor() {
            super(ValueFilterDefinitionWidgetConfig.CFG_USE_MULTIPLE, SelectionType.class, SelectionType.MULTIPLE);
        }
    }

    private static final class DefaultValuesValueReference implements ParameterReference<StringFilter> {
    }

    private static final class SelectionTypeReference implements ParameterReference<SelectionType> {
    }

    private static final class SelectionComponentTypeReference implements ParameterReference<String> {
    }

    private static final class FilterColumnValueProvider implements StateProvider<String> {

        private Supplier<String> m_column;

        private Supplier<List<TypedStringChoice>> m_columnChoices;

        @Override
        public void init(final StateProviderInitializer initializer) {
            m_column = initializer.getValueSupplier(FilterColumnReference.class);
            m_columnChoices = initializer.computeFromProvidedState(FilterColumnChoicesProvider.class);
        }

        @Override
        public String computeState(final NodeParametersInput parametersInput) throws StateComputationFailureException {
            final var choices = m_columnChoices.get();
            if (choices.isEmpty()) {
                return "";
            }
            return choices.stream() //
                .map(TypedStringChoice::id) //
                .filter(c -> c.equals(m_column.get())) //
                .findFirst() //
                .orElse(choices.get(choices.size() - 1).id());
        }
    }

    private static final class FilterColumnChoicesProvider implements ColumnChoicesProvider {
        @Override
        public List<DataColumnSpec> columnChoices(final NodeParametersInput context) {
            return context.getInTableSpec(0) //
                .map(DataTableSpec::stream) //
                .orElse(Stream.empty()) //
                .filter(spec -> spec.getType().isCompatible(StringValue.class)) //
                .filter(spec -> spec.getDomain().hasValues()) //
                .toList();
        }

    }

    private static final class DefaultValuesPersistor extends LegacyNameFilterPersistor {
        public DefaultValuesPersistor() {
            super(ValueFilterDefinitionWidgetConfig.CFG_FILTER_VALUES);
        }
    }

    private static final class DefaultValuesChoicesProvider implements DomainChoicesProvider {

        private Supplier<String> m_columnSupplier;

        @Override
        public void init(final StateProviderInitializer initializer) {
            initializer.computeBeforeOpenDialog();
            m_columnSupplier = initializer.computeFromValueSupplier(FilterColumnReference.class);
        }

        @Override
        public String getColumnName() {
            return m_columnSupplier.get();
        }
    }

    private static final class DefaultValuesValueProvider implements StateProvider<StringFilter> {

        private Supplier<List<StringChoice>> m_columnSupplier;

        private Supplier<StringFilter> m_defaultValuesSupplier;

        @Override
        public void init(final StateProviderInitializer initializer) {
            m_defaultValuesSupplier = initializer.getValueSupplier(DefaultValuesValueReference.class);
            m_columnSupplier = initializer.computeFromProvidedState(DefaultValuesChoicesProvider.class);
        }

        @Override
        public StringFilter computeState(final NodeParametersInput parametersInput)
            throws StateComputationFailureException {
            final var currentFilter = m_defaultValuesSupplier.get();
            final var possibleValues = m_columnSupplier.get().stream().map(StringChoice::id).toArray(String[]::new);
            final var currentValidSelection = currentFilter.filter(possibleValues);
            return currentValidSelection.length == 0 ? new StringFilter(possibleValues)
                : new StringFilter(currentValidSelection);
        }
    }

    private static final class TypeChoicesProvider implements StringChoicesProvider {

        private Supplier<SelectionType> m_multipleOrSingleSupplier;

        @Override
        public void init(final StateProviderInitializer initializer) {
            initializer.computeBeforeOpenDialog();
            m_multipleOrSingleSupplier = initializer.computeFromValueSupplier(SelectionTypeReference.class);
        }

        @Override
        public List<String> choices(final NodeParametersInput context) {
            return Arrays.asList( //
                m_multipleOrSingleSupplier.get() == SelectionType.MULTIPLE
                    ? MultipleSelectionsComponentFactory.listMultipleSelectionsComponents()
                    : SingleSelectionComponentFactory.listSingleSelectionComponents());
        }
    }

    private static class TypeValueProvider implements StateProvider<String> {

        private Supplier<SelectionType> m_selectionType;

        @Override
        public void init(final StateProviderInitializer initializer) {
            m_selectionType = initializer.computeFromValueSupplier(SelectionTypeReference.class);
        }

        @Override
        public String computeState(final NodeParametersInput parametersInput) throws StateComputationFailureException {
            return m_selectionType.get() == SelectionType.MULTIPLE ? TWINLIST : DROPDOWN;
        }
    }

    private static final class LimitVisibleOptionsModification extends LimitVisibleOptionsParametersModifier {

        @Override
        public String getLimitNumVisOptionsDescription() {
            return """
                    By default, some components adjust their height to display all possible choices without a scroll \
                    bar. If this option is enabled, you will be able to limit the number of visible options in case \
                    there is too many.""";
        }

        @Override
        public String getNumVisOptionsDescription() {
            return """
                    A number of options visible in the selection component without a vertical scroll bar. Changing \
                    this value will also affect the component's height. Notice that for Twinlist the height cannot be \
                    less than the overall height of the control buttons in the middle.""";
        }

        @Override
        public Pair<Class<? extends EffectPredicateProvider>, Class<? extends AbstractShowNumberOfVisibleOptions>>
            getEffectPredicates() {
            return new Pair<>(ShowLimitNumberOfVisibleOptions.class, ShowLimitAndShowNumberOfVisibleOptions.class);
        }

        @Override
        public Pair<Class<? extends AbstractNumVisOptionsValidationProvider>, //
                Class<? extends AbstractNumVisOptionsValueProvider>> getNumVisOptionsProviders() {
            return new Pair<>(NumVisOptionsMinValidationProvider.class, NumVisOptionsValueProvider.class);
        }

        private static final class ShowLimitNumberOfVisibleOptions implements EffectPredicateProvider {
            @Override
            public EffectPredicate init(final PredicateInitializer i) {
                return or( //
                    i.getEnum(SelectionTypeReference.class).isOneOf(SelectionType.MULTIPLE), //
                    and( //
                        i.getEnum(SelectionTypeReference.class).isOneOf(SelectionType.SINGLE),
                        i.getString(SelectionComponentTypeReference.class)
                            .isEqualTo(SingleSelectionComponentFactory.LIST)));
            }
        }

        private static final class ShowLimitAndShowNumberOfVisibleOptions extends AbstractShowNumberOfVisibleOptions {
            ShowLimitAndShowNumberOfVisibleOptions() {
                super(ShowLimitNumberOfVisibleOptions.class);
            }
        }

        private static final int MIN_NUM_VIS_OPTIONS_NON_COMBOBOX_MULTI = 5;

        private static final class NumVisOptionsMinValidationProvider extends AbstractNumVisOptionsValidationProvider {

            private Supplier<SelectionType> m_selectionType;

            private Supplier<String> m_selectionComponentType;

            @Override
            public void init(final StateProviderInitializer initializer) {
                super.init(initializer);
                m_selectionType = initializer.computeFromValueSupplier(SelectionTypeReference.class);
                m_selectionComponentType = initializer.computeFromValueSupplier(SelectionComponentTypeReference.class);
            }

            @Override
            public MinValidation computeState(final NodeParametersInput parametersInput)
                throws StateComputationFailureException {
                final var useNonDefaultValidation =
                    m_selectionType.get() == SelectionType.MULTIPLE && !m_selectionComponentType.get().equals(COMBOBOX);

                if (useNonDefaultValidation) {
                    return new MinValidation() {
                        @Override
                        protected double getMin() {
                            return MIN_NUM_VIS_OPTIONS_NON_COMBOBOX_MULTI;
                        }
                    };
                }
                return super.computeState(parametersInput);

            }
        }

        private static final class NumVisOptionsValueProvider extends AbstractNumVisOptionsValueProvider {

            private Supplier<SelectionType> m_selectionType;

            private Supplier<String> m_selectionComponentType;

            @Override
            public void init(final StateProviderInitializer initializer) {
                super.init(initializer);
                m_selectionType = initializer.computeFromValueSupplier(SelectionTypeReference.class);
                m_selectionComponentType = initializer.computeFromValueSupplier(SelectionComponentTypeReference.class);
            }

            @Override
            public Integer computeState(final NodeParametersInput parametersInput)
                throws StateComputationFailureException {
                final var numVisOptions = m_numVisOptionsSupplier.get();
                final var adaptNumVisOptions =
                    m_selectionType.get() == SelectionType.MULTIPLE && !m_selectionComponentType.get().equals(COMBOBOX);
                return adaptNumVisOptions ? Math.max(numVisOptions, MIN_NUM_VIS_OPTIONS_NON_COMBOBOX_MULTI)
                    : numVisOptions;
            }

        }

    }

}
