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
 *   20 Nov 2025 (robin): created
 */
package org.knime.js.base.node.widget.filter.definition.rangeslider;

import java.math.BigDecimal;
import java.util.function.Supplier;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.webui.node.dialog.defaultdialog.util.updates.StateComputationFailureException;
import org.knime.js.base.node.parameters.slider.SliderNodeParametersUtil;
import org.knime.js.base.node.parameters.slider.SliderNodeParametersUtil.DefaultValueSection;
import org.knime.js.base.node.parameters.slider.SliderWidgetNodeParametersUtil.CustomMaxReference;
import org.knime.js.base.node.parameters.slider.SliderWidgetNodeParametersUtil.CustomMinReference;
import org.knime.js.base.node.widget.filter.definition.rangeslider.RangeSliderFilterWidgetSliderSettingsNodeParameters.CustomDefaultMaxReference;
import org.knime.js.base.node.widget.filter.definition.rangeslider.RangeSliderFilterWidgetSliderSettingsNodeParameters.CustomDefaultMinReference;
import org.knime.js.base.node.widget.filter.definition.rangeslider.RangeSliderFilterWidgetSliderSettingsNodeParameters.IsDefiningMaximum;
import org.knime.js.base.node.widget.filter.definition.rangeslider.RangeSliderFilterWidgetSliderSettingsNodeParameters.IsDefiningMinimum;
import org.knime.js.base.node.widget.filter.definition.rangeslider.RangeSliderFilterWidgetSliderSettingsNodeParameters.RoundedCustomDefaultMaxValueProvider;
import org.knime.js.base.node.widget.filter.definition.rangeslider.RangeSliderFilterWidgetSliderSettingsNodeParameters.RoundedCustomDefaultMinValueProvider;
import org.knime.js.base.node.widget.filter.definition.rangeslider.RangeSliderFilterWidgetSliderSettingsNodeParameters.UseCustomDefaultMaxReference;
import org.knime.js.base.node.widget.filter.definition.rangeslider.RangeSliderFilterWidgetSliderSettingsNodeParameters.UseCustomDefaultMinReference;
import org.knime.node.parameters.NodeParameters;
import org.knime.node.parameters.NodeParametersInput;
import org.knime.node.parameters.Widget;
import org.knime.node.parameters.layout.Layout;
import org.knime.node.parameters.persistence.NodeParametersPersistor;
import org.knime.node.parameters.updates.Effect;
import org.knime.node.parameters.updates.Effect.EffectType;
import org.knime.node.parameters.updates.EffectPredicate;
import org.knime.node.parameters.updates.EffectPredicateProvider;
import org.knime.node.parameters.updates.StateProvider;
import org.knime.node.parameters.updates.ValueProvider;
import org.knime.node.parameters.updates.ValueReference;
import org.knime.node.parameters.widget.number.NumberInputWidget;
import org.knime.node.parameters.widget.number.NumberInputWidgetValidation.MaxValidation;
import org.knime.node.parameters.widget.number.NumberInputWidgetValidation.MinValidation;

/**
 * Parameters for the start values of the range slider widget.
 *
 * @author Robin Gerling, KNIME GmbH, Konstanz
 */
@SuppressWarnings("restriction")
final class StartParameters implements NodeParameters {

    StartParameters() {
    }

    StartParameters(final double customDefaultMinimum, final double customDefaultMaximum) {
        m_customDefaultMinimum = customDefaultMinimum;
        m_customDefaultMaximum = customDefaultMaximum;
    }

    @Widget(title = "Default minimum",
        description = "The default minimum value for the minimum slider."
            + " This value is used when 'Use custom default minimum value' is enabled.")
    @ValueReference(CustomDefaultMinReference.class)
    @ValueProvider(RoundedCustomDefaultMinValueProvider.class)
    @Effect(predicate = UseCustomDefaultMin.class, type = EffectType.SHOW)
    @Layout(DefaultValueSection.DefaultMinimum.class)
    @NumberInputWidget(minValidationProvider = CustomDefaultMinMinValidation.class,
        maxValidationProvider = DefaultValuesMaxValidation.class)
    double m_customDefaultMinimum;

    @Widget(title = "Default maximum", description = SliderNodeParametersUtil.MAXIMUM_DESCRIPTION)
    @ValueReference(CustomDefaultMaxReference.class)
    @ValueProvider(RoundedCustomDefaultMaxValueProvider.class)
    @Effect(predicate = UseCustomDefaultMax.class, type = EffectType.SHOW)
    @Layout(DefaultValueSection.DefaultMaximum.class)
    @NumberInputWidget(minValidationProvider = CustomDefaultMaxMinValidation.class,
        maxValidationProvider = DefaultValuesMaxValidation.class)
    double m_customDefaultMaximum = 100;

    private static final class CustomDefaultMinMinValidation implements StateProvider<MinValidation> {

        private Supplier<Double> m_customMinSupplier;

        @Override
        public void init(final StateProviderInitializer initializer) {
            m_customMinSupplier = initializer.computeFromValueSupplier(CustomMinReference.class);
        }

        @Override
        public MinValidation computeState(final NodeParametersInput parametersInput)
            throws StateComputationFailureException {
            final var min = m_customMinSupplier.get();
            if (Double.isInfinite(min)) {
                return null;
            }
            return new MinValidation() {
                @Override
                protected double getMin() {
                    return m_customMinSupplier.get();
                }

                @Override
                public String getErrorMessage() {
                    return String.format("The value cannot be smaller than the range minimum (%s).",
                        BigDecimal.valueOf(min).stripTrailingZeros().toPlainString());
                }
            };
        }

    }

    private static final class CustomDefaultMaxMinValidation implements StateProvider<MinValidation> {

        private Supplier<Double> m_customDefaultMinSupplier;

        @Override
        public void init(final StateProviderInitializer initializer) {
            m_customDefaultMinSupplier = initializer.computeFromValueSupplier(CustomDefaultMinReference.class);
        }

        @Override
        public MinValidation computeState(final NodeParametersInput parametersInput)
            throws StateComputationFailureException {
            final var min = m_customDefaultMinSupplier.get();
            if (Double.isInfinite(min)) {
                return null;
            }
            return new MinValidation() {
                @Override
                protected double getMin() {
                    return m_customDefaultMinSupplier.get();
                }

                @Override
                public String getErrorMessage() {
                    return String.format("The value cannot be smaller than the default minimum (%s).",
                        BigDecimal.valueOf(min).stripTrailingZeros().toPlainString());
                }
            };
        }

    }

    private static final class DefaultValuesMaxValidation implements StateProvider<MaxValidation> {

        private Supplier<Double> m_customMaxSupplier;

        @Override
        public void init(final StateProviderInitializer initializer) {
            m_customMaxSupplier = initializer.computeFromValueSupplier(CustomMaxReference.class);
        }

        @Override
        public MaxValidation computeState(final NodeParametersInput parametersInput)
            throws StateComputationFailureException {
            final var max = m_customMaxSupplier.get();

            if (Double.isInfinite(max)) {
                return null;
            }
            return new MaxValidation() {
                @Override
                protected double getMax() {
                    return m_customMaxSupplier.get();
                }

                @Override
                public String getErrorMessage() {
                    return String.format("The value cannot be larger than the range maximum (%s).",
                        BigDecimal.valueOf(max).stripTrailingZeros().toPlainString());
                }
            };
        }

    }

    static final class StartParametersPersistor implements NodeParametersPersistor<StartParameters> {

        private static final String CFG_START = "start";

        @Override
        public StartParameters load(final NodeSettingsRO settings) throws InvalidSettingsException {
            final var start = settings.getDoubleArray(CFG_START);
            return new StartParameters(start[0], start[1]);
        }

        @Override
        public void save(final StartParameters param, final NodeSettingsWO settings) {
            settings.addDoubleArray(CFG_START, param.m_customDefaultMinimum, param.m_customDefaultMaximum);
        }

        @Override
        public String[][] getConfigPaths() {
            return new String[][]{{CFG_START}};
        }
    }

    private static final class UseCustomDefaultMin implements EffectPredicateProvider {
        @Override
        public EffectPredicate init(final PredicateInitializer i) {
            return and(i.getBoolean(UseCustomDefaultMinReference.class).isTrue(),
                i.getPredicate(IsDefiningMinimum.class));
        }
    }

    private static final class UseCustomDefaultMax implements EffectPredicateProvider {
        @Override
        public EffectPredicate init(final PredicateInitializer i) {
            return and(i.getBoolean(UseCustomDefaultMaxReference.class).isTrue(),
                i.getPredicate(IsDefiningMaximum.class));
        }
    }

}
