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
 *   25 Sept 2025 (Robin Gerling): created
 */
package org.knime.js.base.node.configuration.input.slider;

import java.util.Optional;
import java.util.function.Supplier;

import org.knime.core.data.DataCell;
import org.knime.core.data.IntValue;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.util.Pair;
import org.knime.core.webui.node.dialog.defaultdialog.setting.singleselection.NoneChoice;
import org.knime.core.webui.node.dialog.defaultdialog.setting.singleselection.StringOrEnum;
import org.knime.core.webui.node.dialog.defaultdialog.util.updates.StateComputationFailureException;
import org.knime.js.base.node.base.input.slider.SliderNodeConfig;
import org.knime.js.base.node.base.input.slider.SliderNodeValue;
import org.knime.js.base.node.configuration.ConfigurationNodeSettings;
import org.knime.js.base.node.configuration.OverwrittenByValueMessage;
import org.knime.node.parameters.NodeParameters;
import org.knime.node.parameters.NodeParametersInput;
import org.knime.node.parameters.Widget;
import org.knime.node.parameters.layout.After;
import org.knime.node.parameters.layout.Before;
import org.knime.node.parameters.layout.Layout;
import org.knime.node.parameters.layout.Section;
import org.knime.node.parameters.migration.LoadDefaultsForAbsentFields;
import org.knime.node.parameters.persistence.NodeParametersPersistor;
import org.knime.node.parameters.persistence.Persist;
import org.knime.node.parameters.persistence.Persistor;
import org.knime.node.parameters.updates.Effect;
import org.knime.node.parameters.updates.Effect.EffectType;
import org.knime.node.parameters.updates.EffectPredicate;
import org.knime.node.parameters.updates.EffectPredicateProvider;
import org.knime.node.parameters.updates.ParameterReference;
import org.knime.node.parameters.updates.StateProvider;
import org.knime.node.parameters.updates.ValueProvider;
import org.knime.node.parameters.updates.ValueReference;
import org.knime.node.parameters.widget.choices.ChoicesProvider;
import org.knime.node.parameters.widget.choices.util.CompatibleColumnsProvider;
import org.knime.node.parameters.widget.message.TextMessage;
import org.knime.node.parameters.widget.number.NumberInputWidget;
import org.knime.node.parameters.widget.number.NumberInputWidgetValidation.MaxValidation;
import org.knime.node.parameters.widget.number.NumberInputWidgetValidation.MinValidation;

/**
 * Node parameters for the Integer Slider Configuration Node.
 *
 * @author Robin Gerling, KNIME GmbH, Konstanz, Germany
 */
@SuppressWarnings("restriction")
@LoadDefaultsForAbsentFields
public final class IntegerSliderDialogNodeParameters extends ConfigurationNodeSettings {

    protected IntegerSliderDialogNodeParameters() {
        super(IntegerSliderDialogNodeConfig.class);
    }

    @Section(title = "Validation")
    @After(FormFieldSection.class)
    @Before(OutputSection.class)
    interface Validation {
    }

    @TextMessage(IntegerSliderOverwrittenByValueMessage.class)
    @Layout(OutputSection.Top.class)
    Void m_overwrittenByValueMessage;

    private static final class DefaultValue implements NodeParameters {
        @Widget(title = "Default Value",
            description = "The initial value of the slider parameter. Must be between the minimum and maximum values.")
        @Persistor(DefaultValuePersistor.class)
        @ValueReference(DefaultValueReference.class)
        @ValueProvider(DefaultValueValueProvider.class)
        @Layout(OutputSection.Top.class)
        @NumberInputWidget(minValidationProvider = ValueMinValidation.class,
            maxValidationProvider = ValueMaxValidation.class)
        int m_integer = 50;
    }

    DefaultValue m_defaultValue = new DefaultValue();

    @Widget(title = "Range Column",
        description = "Select a column from the input table to determine the range of the slider. "
            + "The minimum and maximum values from this column will be used as the default range.")
    @ChoicesProvider(DomainColumnChoicesProvider.class)
    @ValueReference(DomainColumnReference.class)
    @Persistor(DomainColumnPersistor.class)
    @Layout(Validation.class)
    StringOrEnum<NoneChoice> m_domainColumn = new StringOrEnum<>(NoneChoice.NONE);

    @Widget(title = "Use custom minimum",
        description = "Check to specify a custom minimum value instead of using the minimum from the range column.")
    @Persist(configKey = SliderNodeConfig.CFG_USE_CUSTOM_MIN)
    @ValueReference(UseCustomMinReference.class)
    @ValueProvider(UseCustomMinValueProvider.class)
    @Layout(Validation.class)
    @Effect(predicate = IsNoneDomainColumn.class, type = EffectType.DISABLE)
    boolean m_useCustomMin = SliderNodeConfig.DEFAULT_USE_CUSTOM_MIN;

    @Widget(title = "Minimum",
        description = "The minimum value for the slider range."
            + " This value is used when 'Use custom minimum' is enabled.")
    @Persistor(CustomMinPersistor.class)
    @ValueReference(CustomMinReference.class)
    @ValueProvider(CustomMinValueProvider.class)
    @Layout(Validation.class)
    @Effect(predicate = UseCustomMin.class, type = EffectType.ENABLE)
    int m_customMin = (int)IntegerSliderDialogNodeConfig.DEFAULT_MIN;

    @Widget(title = "Use custom maximum",
        description = "Check to specify a custom maximum value instead of using the maximum from the range column.")
    @Persist(configKey = SliderNodeConfig.CFG_USE_CUSTOM_MAX)
    @ValueReference(UseCustomMaxReference.class)
    @ValueProvider(UseCustomMaxValueProvider.class)
    @Layout(Validation.class)
    @Effect(predicate = IsNoneDomainColumn.class, type = EffectType.DISABLE)
    boolean m_useCustomMax = SliderNodeConfig.DEFAULT_USE_CUSTOM_MAX;

    @Widget(title = "Maximum",
        description = "The maximum value for the slider range."
            + " This value is used when 'Use custom maximum' is enabled.")
    @Persistor(CustomMaxPersistor.class)
    @ValueReference(CustomMaxReference.class)
    @ValueProvider(CustomMaxValueProvider.class)
    @Layout(Validation.class)
    @Effect(predicate = UseCustomMax.class, type = EffectType.ENABLE)
    int m_customMax = (int)IntegerSliderDialogNodeConfig.DEFAULT_MAX;

    static final class DomainColumnReference implements ParameterReference<StringOrEnum<NoneChoice>> {
    }

    static final class UseCustomMinReference implements ParameterReference<Boolean> {
    }

    static final class CustomMinReference implements ParameterReference<Integer> {
    }

    static final class DefaultValueReference implements ParameterReference<Integer> {
    }

    static final class UseCustomMaxReference implements ParameterReference<Boolean> {
    }

    static final class CustomMaxReference implements ParameterReference<Integer> {
    }

    static final class IntegerSliderOverwrittenByValueMessage
        extends OverwrittenByValueMessage<IntegerSliderDialogNodeValue> {

        @Override
        protected String valueToString(final IntegerSliderDialogNodeValue value) {
            return String.valueOf(value.getDouble().intValue());
        }

    }

    static final class LowerUpperBoundStateProvider implements StateProvider<Optional<Pair<Integer, Integer>>> {

        private Supplier<StringOrEnum<NoneChoice>> m_domainColumnSupplier;

        @Override
        public void init(final StateProviderInitializer initializer) {
            m_domainColumnSupplier = initializer.computeFromValueSupplier(DomainColumnReference.class);
        }

        @Override
        public Optional<Pair<Integer, Integer>> computeState(final NodeParametersInput parametersInput)
            throws StateComputationFailureException {
            final var tableSpecOpt = parametersInput.getInTableSpec(0);
            final var domainColumn = m_domainColumnSupplier.get();

            if (tableSpecOpt.isEmpty() || domainColumn.getEnumChoice().isPresent()) {
                return Optional.empty();
            }

            final var spec = tableSpecOpt.get();
            final var columnName = domainColumn.getStringChoice();
            if (!spec.containsName(columnName)) {
                return Optional.empty();
            }

            final var domain = spec.getColumnSpec(columnName).getDomain();
            final var lower = domain.getLowerBound();
            final var upper = domain.getUpperBound();
            final var hasLower = domain.hasLowerBound() && lower.getType().isCompatible(IntValue.class);
            final var hasUpper = domain.hasUpperBound() && upper.getType().isCompatible(IntValue.class);

            if (!hasLower && !hasUpper) {
                return Optional.empty();
            }

            int min = hasLower ? intCompatibleDataCellToInt(lower) : intCompatibleDataCellToInt(upper);
            int max = hasUpper ? intCompatibleDataCellToInt(upper) : min;
            return Optional.of(new Pair<>(min, max));
        }

        private static int intCompatibleDataCellToInt(final DataCell cell) {
            return ((IntValue)cell).getIntValue();
        }
    }

    static final class CustomMinValueProvider implements StateProvider<Integer> {

        private Supplier<Integer> m_customMinSupplier;

        private Supplier<Optional<Pair<Integer, Integer>>> m_lowerUpperBoundSupplier;

        @Override
        public void init(final StateProviderInitializer initializer) {
            m_customMinSupplier = initializer.getValueSupplier(CustomMinReference.class);
            m_lowerUpperBoundSupplier = initializer.computeFromProvidedState(LowerUpperBoundStateProvider.class);

        }

        @Override
        public Integer computeState(final NodeParametersInput parametersInput) throws StateComputationFailureException {
            return m_lowerUpperBoundSupplier.get().map(Pair::getFirst).orElse(m_customMinSupplier.get());
        }

    }

    static final class CustomMaxValueProvider implements StateProvider<Integer> {

        private Supplier<Integer> m_customMaxSupplier;

        private Supplier<Optional<Pair<Integer, Integer>>> m_lowerUpperBoundSupplier;

        @Override
        public void init(final StateProviderInitializer initializer) {
            m_customMaxSupplier = initializer.getValueSupplier(CustomMaxReference.class);
            m_lowerUpperBoundSupplier = initializer.computeFromProvidedState(LowerUpperBoundStateProvider.class);

        }

        @Override
        public Integer computeState(final NodeParametersInput parametersInput) throws StateComputationFailureException {
            return m_lowerUpperBoundSupplier.get().map(Pair::getSecond).orElse(m_customMaxSupplier.get());
        }

    }

    static final class DefaultValueValueProvider implements StateProvider<Integer> {

        private Supplier<Integer> m_defaultValueSupplier;

        private Supplier<Optional<Pair<Integer, Integer>>> m_lowerUpperBoundSupplier;

        @Override
        public void init(final StateProviderInitializer initializer) {
            m_defaultValueSupplier = initializer.getValueSupplier(DefaultValueReference.class);
            m_lowerUpperBoundSupplier = initializer.computeFromProvidedState(LowerUpperBoundStateProvider.class);

        }

        @Override
        public Integer computeState(final NodeParametersInput parametersInput) throws StateComputationFailureException {
            final var lowerUpperBound = m_lowerUpperBoundSupplier.get();
            if (lowerUpperBound.isEmpty()) {
                return m_defaultValueSupplier.get();
            }
            final var min = lowerUpperBound.get().getFirst();
            final var max = lowerUpperBound.get().getSecond();
            /**
             * see {@link IntegerSliderDialogNodeNodeDialog} line 232
             */
            return (max - min) / 2 + min;
        }
    }

    private abstract static class UseCustomMinMaxValueProvider implements StateProvider<Boolean> {

        private Supplier<StringOrEnum<NoneChoice>> m_domainColumnSupplier;

        protected Supplier<Boolean> m_useCustomMin;

        protected Supplier<Boolean> m_useCustomMax;

        @Override
        public final void init(final StateProviderInitializer initializer) {
            initializer.computeAfterOpenDialog();
            m_useCustomMin = initializer.getValueSupplier(UseCustomMinReference.class);
            m_useCustomMax = initializer.getValueSupplier(UseCustomMaxReference.class);
            m_domainColumnSupplier = initializer.computeFromValueSupplier(DomainColumnReference.class);
        }
        @Override
        public Boolean computeState(final NodeParametersInput parametersInput) throws StateComputationFailureException {
            if (m_domainColumnSupplier.get().getEnumChoice().isPresent()) {
                return true;
            }
            return getCurrentValue();
        }

        abstract Boolean getCurrentValue();
    }

    static final class UseCustomMinValueProvider extends UseCustomMinMaxValueProvider {
        @Override
        Boolean getCurrentValue() {
            return m_useCustomMin.get();
        }
    }

    static final class UseCustomMaxValueProvider extends UseCustomMinMaxValueProvider {
        @Override
        Boolean getCurrentValue() {
            return m_useCustomMax.get();
        }
    }

    static final class IsNoneDomainColumn implements EffectPredicateProvider {
        @Override
        public EffectPredicate init(final PredicateInitializer i) {
            return i.getStringOrEnum(DomainColumnReference.class).isEnumChoice(NoneChoice.NONE);
        }
    }

    static final class UseCustomMin implements EffectPredicateProvider {
        @Override
        public EffectPredicate init(final PredicateInitializer i) {
            return i.getBoolean(UseCustomMinReference.class).isTrue();
        }
    }

    static final class UseCustomMax implements EffectPredicateProvider {
        @Override
        public EffectPredicate init(final PredicateInitializer i) {
            return i.getBoolean(UseCustomMaxReference.class).isTrue();
        }
    }

    static final class DomainColumnChoicesProvider extends CompatibleColumnsProvider {
        public DomainColumnChoicesProvider() {
            super(IntValue.class);
        }
    }

    static final class DomainColumnPersistor implements NodeParametersPersistor<StringOrEnum<NoneChoice>> {

        @Override
        public StringOrEnum<NoneChoice> load(final NodeSettingsRO settings) throws InvalidSettingsException {
            final var domainColumn = settings.getString(SliderNodeConfig.CFG_DOMAIN_COLUMN);
            return domainColumn == null ? new StringOrEnum<>(NoneChoice.NONE) : new StringOrEnum<>(domainColumn);
        }

        @Override
        public void save(final StringOrEnum<NoneChoice> param, final NodeSettingsWO settings) {
            settings.addString(SliderNodeConfig.CFG_DOMAIN_COLUMN,
                param.getEnumChoice().isPresent() ? null : param.getStringChoice());
        }

        @Override
        public String[][] getConfigPaths() {
            return new String[][]{{SliderNodeConfig.CFG_DOMAIN_COLUMN}};
        }

    }

    abstract static class IntegerToDoublePersistor implements NodeParametersPersistor<Integer> {

        private final String m_cfgKey;

        protected IntegerToDoublePersistor(final String cfgKey) {
            m_cfgKey = cfgKey;
        }

        @Override
        public Integer load(final NodeSettingsRO settings) throws InvalidSettingsException {
            return (int)settings.getDouble(m_cfgKey);
        }

        @Override
        public void save(final Integer param, final NodeSettingsWO settings) {
            settings.addDouble(m_cfgKey, param.doubleValue());
        }

        @Override
        public String[][] getConfigPaths() {
            return new String[][]{{m_cfgKey}};
        }
    }

    static final class DefaultValuePersistor extends IntegerToDoublePersistor {
        public DefaultValuePersistor() {
            super(SliderNodeValue.CFG_DOUBLE);
        }
    }

    static final class CustomMinPersistor extends IntegerToDoublePersistor {
        public CustomMinPersistor() {
            super(IntegerSliderDialogNodeConfig.CFG_MIN);
        }
    }

    static final class CustomMaxPersistor extends IntegerToDoublePersistor {
        public CustomMaxPersistor() {
            super(IntegerSliderDialogNodeConfig.CFG_MAX);
        }
    }

    static final class ValueMinValidation implements StateProvider<MinValidation> {

        private Supplier<Integer> m_customMinSupplier;

        @Override
        public void init(final StateProviderInitializer initializer) {
            initializer.computeBeforeOpenDialog();
            m_customMinSupplier = initializer.computeFromValueSupplier(CustomMinReference.class);
        }

        @Override
        public MinValidation computeState(final NodeParametersInput parametersInput)
            throws StateComputationFailureException {
            return new MinValidation() {
                @Override
                protected double getMin() {
                    return m_customMinSupplier.get();
                }
            };
        }
    }

    static final class ValueMaxValidation implements StateProvider<MaxValidation> {

        private Supplier<Integer> m_customMaxSupplier;

        @Override
        public void init(final StateProviderInitializer initializer) {
            initializer.computeBeforeOpenDialog();
            m_customMaxSupplier = initializer.computeFromValueSupplier(CustomMaxReference.class);
        }

        @Override
        public MaxValidation computeState(final NodeParametersInput parametersInput)
            throws StateComputationFailureException {
            return new MaxValidation() {
                @Override
                protected double getMax() {
                    return m_customMaxSupplier.get();
                }
            };
        }
    }

}
