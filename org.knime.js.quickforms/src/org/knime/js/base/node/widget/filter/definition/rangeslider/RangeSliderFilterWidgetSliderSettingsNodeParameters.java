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
 *   10 Nov 2025 (robin): created
 */
package org.knime.js.base.node.widget.filter.definition.rangeslider;

import java.util.function.Supplier;

import org.knime.core.data.DataCell;
import org.knime.core.data.DoubleValue;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.webui.node.dialog.defaultdialog.internal.widget.PersistWithin.PersistEmbedded;
import org.knime.core.webui.node.dialog.defaultdialog.util.updates.StateComputationFailureException;
import org.knime.core.webui.node.dialog.defaultdialog.widget.Modification;
import org.knime.js.base.node.parameters.ConfigurationAndWidgetNodeParametersUtil.OutputSection;
import org.knime.js.base.node.parameters.slider.OrientationAndDirectionParameters;
import org.knime.js.base.node.parameters.slider.SliderNodeParametersUtil.AbstractCustomMaxValueProvider;
import org.knime.js.base.node.parameters.slider.SliderNodeParametersUtil.AbstractCustomMinValueProvider;
import org.knime.js.base.node.parameters.slider.SliderNodeParametersUtil.AbstractLowerUpperBoundStateProvider;
import org.knime.js.base.node.parameters.slider.SliderNodeParametersUtil.DomainColumnReference;
import org.knime.js.base.node.parameters.slider.SliderNodeParametersUtil.SliderBehaviourSection;
import org.knime.js.base.node.parameters.slider.SliderNodeParametersUtil.SliderTooltipsSection;
import org.knime.js.base.node.parameters.slider.SliderTicksSettingsNodeParameters;
import org.knime.js.base.node.parameters.slider.SliderWidgetNodeParametersUtil.CustomMaxReference;
import org.knime.js.base.node.parameters.slider.SliderWidgetNodeParametersUtil.CustomMinReference;
import org.knime.js.base.node.parameters.slider.SliderWidgetNodeParametersUtil.RangeParameters;
import org.knime.js.base.node.parameters.slider.SliderWidgetNodeParametersUtil.RangeParameters.RangeParametersProviderModification;
import org.knime.js.base.node.parameters.slider.SliderWidgetNodeParametersUtil.RoundedDoubleValueProvider;
import org.knime.js.base.node.parameters.slider.SliderWidgetNodeParametersUtil.TrueValueProvider;
import org.knime.js.base.node.parameters.slider.StepSizeParameter;
import org.knime.js.base.node.widget.filter.definition.RangeFilterWidgetNodeParameters.FilterColumnReference;
import org.knime.js.base.node.widget.filter.definition.rangeslider.StartParameters.StartParametersPersistor;
import org.knime.js.base.node.widget.filter.definition.rangeslider.TooltipsParameters.TooltipsPersistor;
import org.knime.node.parameters.NodeParameters;
import org.knime.node.parameters.NodeParametersInput;
import org.knime.node.parameters.Widget;
import org.knime.node.parameters.layout.Layout;
import org.knime.node.parameters.migration.LoadDefaultsForAbsentFields;
import org.knime.node.parameters.persistence.NodeParametersPersistor;
import org.knime.node.parameters.persistence.Persist;
import org.knime.node.parameters.persistence.Persistor;
import org.knime.node.parameters.updates.EffectPredicate;
import org.knime.node.parameters.updates.EffectPredicateProvider;
import org.knime.node.parameters.updates.ParameterReference;
import org.knime.node.parameters.updates.StateProvider;
import org.knime.node.parameters.updates.ValueProvider;
import org.knime.node.parameters.updates.ValueReference;
import org.knime.node.parameters.widget.choices.Label;
import org.knime.node.parameters.widget.choices.ValueSwitchWidget;

/**
 * This class represents the slider settings of the slider widget.
 *
 * @author Robin Gerling, KNIME GmbH, Konstanz
 */
@SuppressWarnings("restriction")
@LoadDefaultsForAbsentFields
public final class RangeSliderFilterWidgetSliderSettingsNodeParameters implements NodeParameters {

    @ValueProvider(TrueValueProvider.class)
    @Persist(configKey = RangeSliderFilterWidgetConfig.CFG_SLIDER_EXISTS)
    boolean m_sliderExists = true;

    @Modification(AddRangeValueProviders.class)
    RangeParameters m_range = new RangeParameters();

    @Persistor(StartParametersPersistor.class)
    StartParameters m_startParameters = new StartParameters();

    @PersistEmbedded
    @Layout(SliderBehaviourSection.class)
    StepSizeParameter m_stepSizeParameter = new StepSizeParameter();

    @Widget(title = "Filter boundary mode",
        description = "Select how the slider determines the boundaries applied by the filter.")
    @ValueReference(FilterBoundaryModeReference.class)
    @Persistor(FilterBoundaryModePersistor.class)
    @ValueSwitchWidget
    @Layout(OutputSection.class)
    FilterBoundaryMode m_boundaryMode = FilterBoundaryMode.BOTH;

    @Layout(SliderBehaviourSection.class)
    @PersistEmbedded
    OrientationAndDirectionParameters m_orientationAndDirection = new OrientationAndDirectionParameters();

    @Layout(SliderTooltipsSection.class)
    @Persistor(TooltipsPersistor.class)
    TooltipsParameters m_tooltips = new TooltipsParameters();

    boolean[] m_connect;

    boolean[] m_snap;

    double[] m_margin;

    double[] m_limit;

    boolean[] m_animate;

    int[] m_animationDuration;

    String m_behaviour;

    SliderTicksSettingsNodeParameters m_pips = new SliderTicksSettingsNodeParameters();

    enum FilterBoundaryMode {
            @Label(value = "Both", description = "The slider defines both boundaries."
                + " The filter applies the full closed range between the selected minimum and maximum.")
            BOTH, //
            @Label(value = "Minimum",
                description = "The slider defines only the minimum value. The upper boundary remains open"
                    + " and will be treated as infinity when the filter is applied.")
            MINIMUM, //
            @Label(value = "Maximum",
                description = "The slider defines only the maximum value. The lower boundary remains open"
                    + " and will be treated as negative infinity when the filter is applied.")
            MAXIMUM;
    }

    static final class UseCustomDefaultMinReference implements ParameterReference<Boolean> {
    }

    static final class UseCustomDefaultMaxReference implements ParameterReference<Boolean> {
    }

    static final class CustomDefaultMinReference implements ParameterReference<Double> {
    }

    static final class CustomDefaultMaxReference implements ParameterReference<Double> {
    }

    private static final class FilterBoundaryModeReference implements ParameterReference<FilterBoundaryMode> {
    }

    static final class IsDefiningMinimum implements EffectPredicateProvider {
        @Override
        public EffectPredicate init(final PredicateInitializer i) {
            return i.getEnum(FilterBoundaryModeReference.class).isOneOf(FilterBoundaryMode.BOTH,
                FilterBoundaryMode.MINIMUM);
        }
    }

    static final class IsDefiningMaximum implements EffectPredicateProvider {
        @Override
        public EffectPredicate init(final PredicateInitializer i) {
            return i.getEnum(FilterBoundaryModeReference.class).isOneOf(FilterBoundaryMode.BOTH,
                FilterBoundaryMode.MAXIMUM);
        }
    }

    /** State provider that extracts the string column name from the domain column ({@link DomainColumnReference}). */
    private static final class DomainColumnProvider implements StateProvider<String> {

        private Supplier<String> m_rangeColumnSupplier;

        @Override
        public void init(final StateProviderInitializer initializer) {
            m_rangeColumnSupplier = initializer.computeFromValueSupplier(FilterColumnReference.class);
        }

        @Override
        public String computeState(final NodeParametersInput parametersInput) throws StateComputationFailureException {
            return m_rangeColumnSupplier.get();
        }

    }

    private static final class LowerUpperBoundStateProvider extends AbstractLowerUpperBoundStateProvider<Double> {
        LowerUpperBoundStateProvider() {
            super(DoubleValue.class, DomainColumnProvider.class);
        }

        @Override
        public Double transformDataValueCompatibleCell(final DataCell cell) {
            return ((DoubleValue)cell).getDoubleValue();
        }
    }

    private static final class CustomMinValueProvider extends AbstractCustomMinValueProvider<Double> {
        CustomMinValueProvider() {
            super(LowerUpperBoundStateProvider.class);
        }
    }

    private static final class RoundedCustomMinValueProvider extends RoundedDoubleValueProvider {
        RoundedCustomMinValueProvider() {
            super(CustomMinValueProvider.class);
        }
    }

    private static final class CustomMaxValueProvider extends AbstractCustomMaxValueProvider<Double> {
        CustomMaxValueProvider() {
            super(LowerUpperBoundStateProvider.class);
        }
    }

    private static class RoundedCustomMaxValueProvider extends RoundedDoubleValueProvider {
        public RoundedCustomMaxValueProvider() {
            super(CustomMaxValueProvider.class);
        }
    }

    private static final class AddRangeValueProviders extends RangeParametersProviderModification {

        @Override
        public Class<? extends RoundedDoubleValueProvider> getCustomMinProvider() {
            return RoundedCustomMinValueProvider.class;
        }

        @Override
        public Class<? extends RoundedDoubleValueProvider> getCustomMaxProvider() {
            return RoundedCustomMaxValueProvider.class;
        }

    }

    static final class RoundedCustomDefaultMinValueProvider implements StateProvider<Double> {

        private Supplier<Double> m_customMinSupplier;

        private Supplier<Boolean> m_useCustomDefaultMinSupplier;

        private Supplier<FilterBoundaryMode> m_filterBoundaryMode;

        private Supplier<Double> m_customDefaultMinSupplier;

        @Override
        public void init(final StateProviderInitializer initializer) {
            m_customMinSupplier = initializer.computeFromValueSupplier(CustomMinReference.class);
            m_useCustomDefaultMinSupplier = initializer.getValueSupplier(UseCustomDefaultMinReference.class);
            m_filterBoundaryMode = initializer.computeFromValueSupplier(FilterBoundaryModeReference.class);
            m_customDefaultMinSupplier = initializer.getValueSupplier(CustomDefaultMinReference.class);
        }

        @Override
        public Double computeState(final NodeParametersInput parametersInput) throws StateComputationFailureException {
            if (m_filterBoundaryMode.get() == FilterBoundaryMode.MAXIMUM) {
                return Double.NEGATIVE_INFINITY;
            }
            final var useCustomDefaultMin = m_useCustomDefaultMinSupplier.get();
            if (useCustomDefaultMin == null) {
                throw new StateComputationFailureException();
            }
            if (useCustomDefaultMin) {
                return m_customDefaultMinSupplier.get();
            }
            return m_customMinSupplier.get();
        }

    }

    static final class RoundedCustomDefaultMaxValueProvider implements StateProvider<Double> {

        private Supplier<Double> m_customMaxSupplier;

        private Supplier<Boolean> m_useCustomDefaultMaxSupplier;

        private Supplier<FilterBoundaryMode> m_filterBoundaryMode;

        private Supplier<Double> m_customDefaultMaxSupplier;

        @Override
        public void init(final StateProviderInitializer initializer) {
            m_customMaxSupplier = initializer.computeFromValueSupplier(CustomMaxReference.class);
            m_useCustomDefaultMaxSupplier = initializer.getValueSupplier(UseCustomDefaultMaxReference.class);
            m_filterBoundaryMode = initializer.computeFromValueSupplier(FilterBoundaryModeReference.class);
            m_customDefaultMaxSupplier = initializer.getValueSupplier(CustomDefaultMaxReference.class);
        }

        @Override
        public Double computeState(final NodeParametersInput parametersInput) throws StateComputationFailureException {
            if (m_filterBoundaryMode.get() == FilterBoundaryMode.MINIMUM) {
                return Double.POSITIVE_INFINITY;
            }
            final var useCustomDefaultMax = m_useCustomDefaultMaxSupplier.get();
            if (useCustomDefaultMax == null) {
                throw new StateComputationFailureException();
            }
            if (useCustomDefaultMax) {
                return m_customDefaultMaxSupplier.get();
            }
            return m_customMaxSupplier.get();
        }

    }

    private static final class FilterBoundaryModePersistor implements NodeParametersPersistor<FilterBoundaryMode> {

        private static final String CFG_FILTER_BOUNDARY_MODE = "fix";

        private static final int INDEX_MAXIMUM = 0;

        private static final int INDEX_BOTH = 1;

        private static final int INDEX_MINIMUM = 2;

        @Override
        public FilterBoundaryMode load(final NodeSettingsRO settings) throws InvalidSettingsException {
            final var fix = settings.getBooleanArray(CFG_FILTER_BOUNDARY_MODE);
            if (fix[INDEX_MAXIMUM]) {
                return FilterBoundaryMode.MAXIMUM;
            }
            return fix[INDEX_BOTH] ? FilterBoundaryMode.BOTH : FilterBoundaryMode.MINIMUM;
        }

        @Override
        public void save(final FilterBoundaryMode param, final NodeSettingsWO settings) {
            final var fix = new boolean[]{false, false, false};
            switch (param) {
                case BOTH -> fix[INDEX_BOTH] = true;
                case MINIMUM -> fix[INDEX_MINIMUM] = true;
                case MAXIMUM -> fix[INDEX_MAXIMUM] = true;
            }
            settings.addBooleanArray(CFG_FILTER_BOUNDARY_MODE, fix);
        }

        @Override
        public String[][] getConfigPaths() {
            return new String[][]{{CFG_FILTER_BOUNDARY_MODE}};
        }

    }

}
