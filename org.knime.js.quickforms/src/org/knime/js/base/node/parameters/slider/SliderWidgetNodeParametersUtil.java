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
 *   18 Nov 2025 (Robin Gerling): created
 */
package org.knime.js.base.node.parameters.slider;

import java.util.function.Supplier;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.webui.node.dialog.defaultdialog.util.updates.StateComputationFailureException;
import org.knime.core.webui.node.dialog.defaultdialog.widget.Modification;
import org.knime.core.webui.node.dialog.defaultdialog.widget.Modification.WidgetGroupModifier;
import org.knime.js.base.node.parameters.slider.SliderNodeParametersUtil.AbstractValueMaxValidation;
import org.knime.js.base.node.parameters.slider.SliderNodeParametersUtil.AbstractValueMinValidation;
import org.knime.js.base.node.parameters.slider.SliderNodeParametersUtil.RangeSection;
import org.knime.js.base.node.parameters.slider.SliderNodeParametersUtil.UseCustomMax;
import org.knime.js.base.node.parameters.slider.SliderNodeParametersUtil.UseCustomMin;
import org.knime.node.parameters.NodeParameters;
import org.knime.node.parameters.NodeParametersInput;
import org.knime.node.parameters.Widget;
import org.knime.node.parameters.layout.Layout;
import org.knime.node.parameters.persistence.NodeParametersPersistor;
import org.knime.node.parameters.persistence.Persist;
import org.knime.node.parameters.persistence.Persistor;
import org.knime.node.parameters.updates.Effect;
import org.knime.node.parameters.updates.Effect.EffectType;
import org.knime.node.parameters.updates.ParameterReference;
import org.knime.node.parameters.updates.StateProvider;
import org.knime.node.parameters.updates.ValueProvider;
import org.knime.node.parameters.updates.ValueReference;

/**
 * Utility class containing shared components for Slider Widget node parameters. Provides reusable providers,
 * persistors, and helper classes for slider-based widgets.
 *
 * @author Robin Gerling, KNIME GmbH, Konstanz, Germany
 */
@SuppressWarnings({"restriction"})
public final class SliderWidgetNodeParametersUtil {

    private SliderWidgetNodeParametersUtil() {
        // Utility class
    }

    /**
     * Some fields like the sliderExists field can be false when the node was not configured yet (i.e. dragged in newly,
     * or dragged in non-configured from another AP). When it is opened in the modern dialog, it needs to be set to
     * true.
     */
    public static final class TrueValueProvider implements StateProvider<Boolean> {

        @Override
        public void init(final StateProviderInitializer initializer) {
            initializer.computeBeforeOpenDialog();
        }

        @Override
        public Boolean computeState(final NodeParametersInput parametersInput) throws StateComputationFailureException {
            return true;
        }

    }

    /**
     * Abstract base class for value providers that round double values to 6 decimal places. We round to 6 decimal
     * places to avoid rounding issues when choosing an extreme value in the resulting slider (which rounds to 7
     * digits).
     */
    public abstract static class RoundedDoubleValueProvider implements StateProvider<Double> {
        private final Class<? extends StateProvider<Double>> m_baseProvider;

        private Supplier<Double> m_unroundedValueProvider;

        /**
         * Creates a new rounded double value provider that wraps the given base provider.
         *
         * @param baseProvider the base provider class to wrap and whose value is to be rounded
         */
        protected RoundedDoubleValueProvider(final Class<? extends StateProvider<Double>> baseProvider) {
            m_baseProvider = baseProvider;
        }

        @Override
        public void init(final StateProviderInitializer initializer) {
            m_unroundedValueProvider = initializer.computeFromProvidedState(m_baseProvider);
        }

        @Override
        public final Double computeState(final NodeParametersInput parametersInput)
            throws StateComputationFailureException {
            return roundTo6DecimalPlaces(m_unroundedValueProvider.get());
        }
    }

    /**
     * Rounds the given double value to 6 decimal places. We round to 6 decimal places to avoid rounding issues when
     * choosing an extreme value in the resulting slider (which rounds to 7 digits).
     *
     * @param value the value to round
     * @return the rounded value
     */
    public static Double roundTo6DecimalPlaces(final Double value) {
        return Math.round(value * 1000000.0) / 1000000.0;
    }

    /**
     * Abstract persistor that saves/loads a double value as a single-element double array. Used for persisting slider
     * range and start values in the noUiSlider format.
     */
    public abstract static class DoubleToArrayPersistor implements NodeParametersPersistor<Double> {

        private final String m_configKey;

        /**
         * Creates a new persistor for the given config key.
         *
         * @param configKey the configuration key to use for persistence
         */
        protected DoubleToArrayPersistor(final String configKey) {
            m_configKey = configKey;
        }

        @Override
        public Double load(final NodeSettingsRO settings) throws InvalidSettingsException {
            return settings.getDoubleArray(m_configKey)[0];
        }

        @Override
        public void save(final Double param, final NodeSettingsWO settings) {
            settings.addDoubleArray(m_configKey, param);
        }

        @Override
        public String[][] getConfigPaths() {
            return new String[][]{{m_configKey}};
        }
    }

    /** Reference for the minimum value of a double slider. */
    public static final class CustomMinReference implements ParameterReference<Double>, Modification.Reference {
    }

    /** Reference for the maximum value of a double slider. */
    public static final class CustomMaxReference implements ParameterReference<Double>, Modification.Reference {
    }

    /**
     * Validation provider for the minimum value of a double slider default value. Validates against the custom minimum
     * reference.
     */
    public static final class ValueMinValidation extends AbstractValueMinValidation<Double> {
        ValueMinValidation() {
            super(CustomMinReference.class);
        }
    }

    /**
     * Validation provider for the maximum value of a double slider default value. Validates against the custom maximum
     * reference.
     */
    public static final class ValueMaxValidation extends AbstractValueMaxValidation<Double> {
        ValueMaxValidation() {
            super(CustomMaxReference.class);
        }
    }

    /**
     * Generic range parameters for slider range (min/max) values.
     */
    public static final class RangeParameters implements NodeParameters {

        /** Default constructor. */
        public RangeParameters() {
        }

        private static final String CFG_MIN_KEY = "key_0";

        private static final String CFG_MAX_KEY = "key_1";

        private static final String CFG_MIN_VALUE = "value_0";

        private static final String CFG_MAX_VALUE = "value_1";

        int m_numSettings = 2;

        @Persist(configKey = CFG_MIN_KEY)
        String m_key0 = "min";

        @Widget(title = SliderNodeParametersUtil.MINIMUM_TITLE,
            description = SliderNodeParametersUtil.MINIMUM_DESCRIPTION)
        @ValueReference(CustomMinReference.class)
        @Modification.WidgetReference(CustomMinReference.class)
        @Effect(predicate = UseCustomMin.class, type = EffectType.ENABLE)
        @Persistor(RangeMinPersistor.class)
        @Layout(RangeSection.Minimum.class)
        double m_value0;

        @Persist(configKey = CFG_MAX_KEY)
        String m_key1 = "max";

        @Widget(title = SliderNodeParametersUtil.MAXIMUM_TITLE,
            description = SliderNodeParametersUtil.MAXIMUM_DESCRIPTION)
        @ValueReference(CustomMaxReference.class)
        @Modification.WidgetReference(CustomMaxReference.class)
        @Effect(predicate = UseCustomMax.class, type = EffectType.ENABLE)
        @Persistor(RangeMaxPersistor.class)
        @Layout(RangeSection.Maximum.class)
        double m_value1 = 100;

        private static final class RangeMinPersistor extends DoubleToArrayPersistor {
            RangeMinPersistor() {
                super(CFG_MIN_VALUE);
            }
        }

        private static final class RangeMaxPersistor extends DoubleToArrayPersistor {
            RangeMaxPersistor() {
                super(CFG_MAX_VALUE);
            }
        }

        /**
         * Modification that adds custom min/max value providers to the range parameters.
         */
        public abstract static class RangeParametersProviderModification implements Modification.Modifier {

            @Override
            public void modify(final WidgetGroupModifier group) {
                group.find(CustomMinReference.class).addAnnotation(ValueProvider.class)
                    .withValue(getCustomMinProvider()).modify();
                group.find(CustomMaxReference.class).addAnnotation(ValueProvider.class)
                    .withValue(getCustomMaxProvider()).modify();
            }

            /** @return the custom min value provider class */
            public abstract Class<? extends RoundedDoubleValueProvider> getCustomMinProvider();

            /** @return the custom max value provider class */
            public abstract Class<? extends RoundedDoubleValueProvider> getCustomMaxProvider();

        }

    }

}
