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
 *   7 Nov 2025 (Robin Gerling): created
 */
package org.knime.js.base.node.parameters.slider;

import java.util.Optional;
import java.util.function.Supplier;

import org.knime.core.data.DataCell;
import org.knime.core.data.DataValue;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.util.Pair;
import org.knime.core.webui.node.dialog.defaultdialog.setting.singleselection.NoneChoice;
import org.knime.core.webui.node.dialog.defaultdialog.setting.singleselection.StringOrEnum;
import org.knime.core.webui.node.dialog.defaultdialog.util.updates.StateComputationFailureException;
import org.knime.js.base.node.base.input.slider.SliderNodeConfig;
import org.knime.js.base.node.parameters.ConfigurationAndWidgetNodeParametersUtil.FormFieldSection;
import org.knime.js.base.node.parameters.ConfigurationAndWidgetNodeParametersUtil.OutputSection;
import org.knime.node.parameters.NodeParameters;
import org.knime.node.parameters.NodeParametersInput;
import org.knime.node.parameters.Widget;
import org.knime.node.parameters.layout.After;
import org.knime.node.parameters.layout.Before;
import org.knime.node.parameters.layout.Layout;
import org.knime.node.parameters.layout.Section;
import org.knime.node.parameters.persistence.NodeParametersPersistor;
import org.knime.node.parameters.persistence.Persist;
import org.knime.node.parameters.updates.Effect;
import org.knime.node.parameters.updates.Effect.EffectType;
import org.knime.node.parameters.updates.EffectPredicate;
import org.knime.node.parameters.updates.EffectPredicateProvider;
import org.knime.node.parameters.updates.ParameterReference;
import org.knime.node.parameters.updates.StateProvider;
import org.knime.node.parameters.updates.ValueProvider;
import org.knime.node.parameters.updates.ValueReference;
import org.knime.node.parameters.widget.number.NumberInputWidgetValidation.MaxValidation;
import org.knime.node.parameters.widget.number.NumberInputWidgetValidation.MinValidation;

/**
 * Utility class containing shared WebUI Node Parameters and helper classes for Slider Widget and Configuration nodes.
 * Provides reusable parameter definitions, state providers, and validation logic for slider configuration.
 *
 * @author Robin Gerling, KNIME GmbH, Konstanz, Germany
 */
@SuppressWarnings({"restriction", "javadoc"})
public final class SliderNodeParametersUtil {

    private SliderNodeParametersUtil() {
        // Utility class
    }

    // Common slider dialog titles and descriptions
    public static final String DEFAULT_VALUE_TITLE = "Default value";

    public static final String DEFAULT_VALUE_DESCRIPTION =
        "The initial value of the slider parameter. Must be between the minimum and maximum values.";

    public static final String RANGE_COLUMN_TITLE = "Range column";

    public static final String RANGE_COLUMN_DESCRIPTION =
        "Select a column from the input table to determine the range of the slider. "
            + "The minimum and maximum values from this column will be used as the default range.";

    public static final String USE_CUSTOM_MIN_TITLE = "Use custom minimum";

    public static final String USE_CUSTOM_MIN_DESCRIPTION =
        "Check to specify a custom minimum value instead of using the minimum from the range column.";

    public static final String USE_CUSTOM_MAX_TITLE = "Use custom maximum";

    public static final String USE_CUSTOM_MAX_DESCRIPTION =
        "Check to specify a custom maximum value instead of using the maximum from the range column.";

    public static final String MINIMUM_TITLE = "Minimum";

    public static final String MINIMUM_DESCRIPTION =
        "The minimum value for the slider range. This value is used when 'Use custom minimum' is enabled.";

    public static final String MAXIMUM_TITLE = "Maximum";

    public static final String MAXIMUM_DESCRIPTION =
        "The maximum value for the slider range. This value is used when 'Use custom maximum' is enabled.";

    /**
     * Parameter for enabling custom minimum value instead of using domain column minimum.
     */
    public static final class UseCustomMinParameter implements NodeParameters {
        @Widget(title = USE_CUSTOM_MIN_TITLE, description = USE_CUSTOM_MIN_DESCRIPTION)
        @Persist(configKey = SliderNodeConfig.CFG_USE_CUSTOM_MIN)
        @ValueReference(UseCustomMinReference.class)
        @ValueProvider(SetToFalseOnNewDomainColumn.class)
        @Layout(RangeSection.UseCustomMin.class)
        @Effect(predicate = IsNoneDomainColumn.class, type = EffectType.HIDE)
        boolean m_useCustomMin = SliderNodeConfig.DEFAULT_USE_CUSTOM_MIN;
    }

    /**
     * Parameter for enabling custom maximum value instead of using domain column maximum.
     */
    public static final class UseCustomMaxParameter implements NodeParameters {
        @Widget(title = USE_CUSTOM_MAX_TITLE, description = USE_CUSTOM_MAX_DESCRIPTION)
        @Persist(configKey = SliderNodeConfig.CFG_USE_CUSTOM_MAX)
        @ValueReference(UseCustomMaxReference.class)
        @ValueProvider(SetToFalseOnNewDomainColumn.class)
        @Layout(RangeSection.UseCustomMax.class)
        @Effect(predicate = IsNoneDomainColumn.class, type = EffectType.HIDE)
        boolean m_useCustomMax = SliderNodeConfig.DEFAULT_USE_CUSTOM_MAX;
    }

    /** Layout section for range settings (min, max of the slider). */
    @After(FormFieldSection.class)
    @Before(OutputSection.class)
    @Section(title = "Range")
    public interface RangeSection {

        interface RangeColumn {

        }

        @After(RangeColumn.class)
        interface UseCustomMin {

        }

        @After(UseCustomMin.class)
        interface Minimum {

        }

        @After(Minimum.class)
        interface UseCustomMax {

        }

        @After(UseCustomMax.class)
        interface Maximum {

        }

    }

    /** Layout section for slider behaviour settings (orientation/direction, tooltip, etc). */
    @After(OutputSection.class)
    @Before(SliderTooltipSection.class)
    @Section(title = "Slider Behaviour")
    public interface SliderBehaviourSection {
    }

    /** Layout section for tooltip format settings. */
    @After(SliderBehaviourSection.class)
    @Before(LabelAndTicksSection.class)
    @Section(title = "Slider Tooltip")
    public interface SliderTooltipSection {
    }

    /** Layout section for label and pips settings. */
    @After(SliderTooltipSection.class)
    @Section(title = "Labels & Ticks")
    public interface LabelAndTicksSection {
    }

    /** Parameter reference for the domain column selection. */
    public static final class DomainColumnReference implements ParameterReference<StringOrEnum<NoneChoice>> {
    }

    /** Parameter reference for the use custom minimum flag. */
    public static final class UseCustomMinReference implements ParameterReference<Boolean> {
    }

    /** Parameter reference for the use custom maximum flag. */
    public static final class UseCustomMaxReference implements ParameterReference<Boolean> {
    }

    /**
     * Persistor for the domain column parameter.
     */
    public static final class DomainColumnPersistor implements NodeParametersPersistor<StringOrEnum<NoneChoice>> {

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

    /** State provider that sets custom min/max flags to false when a new domain column is selected. */
    private static final class SetToFalseOnNewDomainColumn implements StateProvider<Boolean> {

        private Supplier<StringOrEnum<NoneChoice>> m_domainColumnSupplier;

        @Override
        public void init(final StateProviderInitializer initializer) {
            m_domainColumnSupplier = initializer.computeFromValueSupplier(DomainColumnReference.class);
        }

        @Override
        public Boolean computeState(final NodeParametersInput parametersInput) throws StateComputationFailureException {
            if (m_domainColumnSupplier.get().getEnumChoice().isPresent()) {
                /**
                 * If <none> column is selected, we don't show custom min/max options and it is ignored in the model. We
                 * don't want to change the value in this case, since it would make the dialog dirty initially without
                 * any user interaction and without any reason.
                 */
                throw new StateComputationFailureException();
            }
            return false;
        }

    }

    /** Predicate provider that checks if no domain column is selected (NoneChoice). */
    public static final class IsNoneDomainColumn implements EffectPredicateProvider {
        @Override
        public EffectPredicate init(final PredicateInitializer i) {
            return i.getStringOrEnum(DomainColumnReference.class).isEnumChoice(NoneChoice.NONE);
        }
    }

    /** Predicate provider that determines when custom minimum controls should be enabled. */
    public static final class UseCustomMin implements EffectPredicateProvider {
        @Override
        public EffectPredicate init(final PredicateInitializer i) {
            final var isNoneColumns = i.getStringOrEnum(DomainColumnReference.class).isEnumChoice(NoneChoice.NONE);
            return i.getBoolean(UseCustomMinReference.class).isTrue().or(isNoneColumns);
        }
    }

    /** Predicate provider that determines when custom maximum controls should be enabled. */
    public static final class UseCustomMax implements EffectPredicateProvider {
        @Override
        public EffectPredicate init(final PredicateInitializer i) {
            final var isNoneColumns = i.getStringOrEnum(DomainColumnReference.class).isEnumChoice(NoneChoice.NONE);
            return i.getBoolean(UseCustomMaxReference.class).isTrue().or(isNoneColumns);
        }
    }

    /**
     * Abstract state provider that extracts lower and upper bounds from a selected domain column. Provides the
     * foundation for determining min/max values based on column data.
     *
     * @param <T> the data type for the bounds (e.g., Double, Integer)
     */
    public abstract static class AbstractLowerUpperBoundStateProvider<T>
        implements StateProvider<Optional<Pair<T, T>>> {

        private Supplier<StringOrEnum<NoneChoice>> m_domainColumnSupplier;

        private Class<? extends DataValue> m_compatibleDataValue;

        /**
         * @param compatibleDataValue the DataValue, the bound should be compatible to
         */
        protected AbstractLowerUpperBoundStateProvider(final Class<? extends DataValue> compatibleDataValue) {
            m_compatibleDataValue = compatibleDataValue;
        }

        @Override
        public void init(final StateProviderInitializer initializer) {
            m_domainColumnSupplier = initializer.computeFromValueSupplier(DomainColumnReference.class);
        }

        @Override
        public final Optional<Pair<T, T>> computeState(final NodeParametersInput parametersInput)
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
            final var hasLower = domain.hasLowerBound() && lower.getType().isCompatible(m_compatibleDataValue);
            final var hasUpper = domain.hasUpperBound() && upper.getType().isCompatible(m_compatibleDataValue);

            if (!hasLower && !hasUpper) {
                return Optional.empty();
            }

            final var min =
                hasLower ? transformDataValueCompatibleCell(lower) : transformDataValueCompatibleCell(upper);
            final var max = hasUpper ? transformDataValueCompatibleCell(upper) : min;
            return Optional.of(new Pair<>(min, max));
        }

        /**
         * Transforms a DataCell compatible with the specified DataValue type (in the constructor) to the target type.
         *
         * @param cell the data cell to transform
         * @return the transformed value of type T
         */
        public abstract T transformDataValueCompatibleCell(final DataCell cell);
    }

    /**
     * Abstract state provider that computes default values based on domain column bounds. Calculates a reasonable
     * default value when a domain column is selected.
     *
     * @param <T> the data type for the default value
     */
    public abstract static class AbstractDefaultValueValueProvider<T> implements StateProvider<T> {

        private Supplier<T> m_defaultValueSupplier;

        private Supplier<Optional<Pair<T, T>>> m_lowerUpperBoundSupplier;

        private Class<? extends ParameterReference<T>> m_defaultValueReference;

        private Class<? extends StateProvider<Optional<Pair<T, T>>>> m_lowerUpperBoundProvider;

        /**
         * @param defaultValueReference reference to the default value
         * @param lowerUpperBoundProvider state provider providing the lower/upper bound
         */
        protected AbstractDefaultValueValueProvider(final Class<? extends ParameterReference<T>> defaultValueReference,
            final Class<? extends StateProvider<Optional<Pair<T, T>>>> lowerUpperBoundProvider) {
            m_defaultValueReference = defaultValueReference;
            m_lowerUpperBoundProvider = lowerUpperBoundProvider;
        }

        @Override
        public final void init(final StateProviderInitializer initializer) {
            m_defaultValueSupplier = initializer.getValueSupplier(m_defaultValueReference);
            m_lowerUpperBoundSupplier = initializer.computeFromProvidedState(m_lowerUpperBoundProvider);
        }

        @Override
        public final T computeState(final NodeParametersInput parametersInput) throws StateComputationFailureException {
            final var lowerUpperBound = m_lowerUpperBoundSupplier.get();
            if (lowerUpperBound.isEmpty()) {
                return m_defaultValueSupplier.get();
            }
            final var min = lowerUpperBound.get().getFirst();
            final var max = lowerUpperBound.get().getSecond();
            /**
             * see {@link IntegerSliderDialogNodeNodeDialog} line 232 (max - min) / 2 + min
             */
            return computeDefaultValue(min, max);
        }

        /**
         * Computes the default value based on the minimum and maximum bounds.
         *
         * @param min the minimum bound value
         * @param max the maximum bound value
         * @return the computed default value
         */
        public abstract T computeDefaultValue(T min, T max);
    }

    /**
     * Abstract state provider for minimum value validation of the default value. Provides dynamic minimum validation
     * based on the custom minimum parameter value.
     *
     * @param <T> the data type for validation
     */
    public abstract static class AbstractValueMinValidation<T> implements StateProvider<MinValidation> {

        private Supplier<T> m_customMinSupplier;

        private Class<? extends ParameterReference<T>> m_customMinReference;

        /**
         * @param customMinReference the parameter reference for the custom minimum value
         */
        protected AbstractValueMinValidation(final Class<? extends ParameterReference<T>> customMinReference) {
            m_customMinReference = customMinReference;
        }

        @Override
        public final void init(final StateProviderInitializer initializer) {
            initializer.computeBeforeOpenDialog();
            m_customMinSupplier = initializer.computeFromValueSupplier(m_customMinReference);
        }

        @Override
        public final MinValidation computeState(final NodeParametersInput parametersInput)
            throws StateComputationFailureException {
            final var customMin = castToDouble(m_customMinSupplier.get());

            return customMin.map(value -> new MinValidation() {
                @Override
                protected double getMin() {
                    return value;
                }
            }).orElse(null);
        }
    }

    /**
     * Abstract state provider for maximum value validation of the default value. Provides dynamic maximum validation
     * based on the custom maximum parameter value.
     *
     * @param <T> the data type for validation
     */
    public abstract static class AbstractValueMaxValidation<T> implements StateProvider<MaxValidation> {

        private Supplier<T> m_customMaxSupplier;

        private Class<? extends ParameterReference<T>> m_customMaxReference;

        /**
         * @param customMaxReference the parameter reference for the custom maximum value
         */
        protected AbstractValueMaxValidation(final Class<? extends ParameterReference<T>> customMaxReference) {
            m_customMaxReference = customMaxReference;
        }

        @Override
        public final void init(final StateProviderInitializer initializer) {
            initializer.computeBeforeOpenDialog();
            m_customMaxSupplier = initializer.computeFromValueSupplier(m_customMaxReference);
        }

        @Override
        public MaxValidation computeState(final NodeParametersInput parametersInput)
            throws StateComputationFailureException {
            final var customMax = castToDouble(m_customMaxSupplier.get());
            return customMax.map(value -> new MaxValidation() {
                @Override
                protected double getMax() {
                    return value;
                }
            }).orElse(null);
        }
    }

    private static <T> Optional<Double> castToDouble(final T value) {
        if (value == null) {
            return Optional.empty();
        }
        if (value instanceof Number number) {
            final var d = number.doubleValue();
            return Double.isNaN(d) ? Optional.empty() : Optional.of(d);
        }
        throw new IllegalStateException("Only numeric values are supported (Integer or Double).");
    }

    /**
     * Abstract base class for state providers that compute custom min/max values from domain bounds.
     *
     * @param <T> the data type for the custom values
     */
    private abstract static class AbstractCustomMinMaxValueProvider<T> implements StateProvider<T> {

        protected Supplier<Optional<Pair<T, T>>> m_lowerUpperBoundSupplier;

        protected Class<? extends StateProvider<Optional<Pair<T, T>>>> m_lowerUpperBoundProvider;

        /**
         * Creates a new custom min/max value provider.
         *
         * @param lowerUpperBoundProvider the state provider for domain bounds
         */
        protected AbstractCustomMinMaxValueProvider(
            final Class<? extends StateProvider<Optional<Pair<T, T>>>> lowerUpperBoundProvider) {
            m_lowerUpperBoundProvider = lowerUpperBoundProvider;
        }

        @Override
        public void init(final StateProviderInitializer initializer) {
            m_lowerUpperBoundSupplier = initializer.computeFromProvidedState(m_lowerUpperBoundProvider);
        }

    }

    /**
     * Abstract state provider that extracts the minimum value from domain column bounds.
     *
     * @param <T> the data type for the minimum value
     */
    public abstract static class AbstractCustomMinValueProvider<T> extends AbstractCustomMinMaxValueProvider<T> {

        /**
         * @param lowerUpperBoundProvider state provider providing the lower/upper bound
         */
        protected AbstractCustomMinValueProvider(
            final Class<? extends StateProvider<Optional<Pair<T, T>>>> lowerUpperBoundProvider) {
            super(lowerUpperBoundProvider);
        }

        @Override
        public void init(final StateProviderInitializer initializer) {
            super.init(initializer);
            initializer.computeOnValueChange(UseCustomMinReference.class);
        }

        @Override
        public final T computeState(final NodeParametersInput parametersInput) throws StateComputationFailureException {
            return m_lowerUpperBoundSupplier.get().map(Pair::getFirst)
                .orElseThrow(StateComputationFailureException::new);
        }

    }

    /**
     * Abstract state provider that extracts the maximum value from domain column bounds.
     *
     * @param <T> the data type for the maximum value
     */
    public abstract static class AbstractCustomMaxValueProvider<T> extends AbstractCustomMinMaxValueProvider<T> {

        /**
         * @param lowerUpperBoundProvider state provider providing the lower/upper bound
         */
        protected AbstractCustomMaxValueProvider(
            final Class<? extends StateProvider<Optional<Pair<T, T>>>> lowerUpperBoundProvider) {
            super(lowerUpperBoundProvider);
        }

        @Override
        public void init(final StateProviderInitializer initializer) {
            super.init(initializer);
            initializer.computeOnValueChange(UseCustomMaxReference.class);
        }

        @Override
        public final T computeState(final NodeParametersInput parametersInput) throws StateComputationFailureException {
            return m_lowerUpperBoundSupplier.get().map(Pair::getSecond)
                .orElseThrow(StateComputationFailureException::new);
        }

    }

}
