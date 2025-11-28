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
package org.knime.js.base.node.widget.input.slider;

import java.util.function.Supplier;

import org.knime.core.webui.node.dialog.defaultdialog.internal.widget.PersistWithin;
import org.knime.core.webui.node.dialog.defaultdialog.setting.singleselection.NoneChoice;
import org.knime.core.webui.node.dialog.defaultdialog.setting.singleselection.StringOrEnum;
import org.knime.core.webui.node.dialog.defaultdialog.util.updates.StateComputationFailureException;
import org.knime.js.base.node.base.input.slider.SliderNodeValue;
import org.knime.js.base.node.parameters.ConfigurationAndWidgetNodeParametersUtil.OutputSection;
import org.knime.js.base.node.parameters.OverwrittenByValueMessage;
import org.knime.js.base.node.parameters.slider.SliderNodeParametersUtil;
import org.knime.js.base.node.parameters.slider.SliderNodeParametersUtil.AbstractDefaultValueValueProvider;
import org.knime.js.base.node.parameters.slider.SliderNodeParametersUtil.DomainColumnPersistor;
import org.knime.js.base.node.parameters.slider.SliderNodeParametersUtil.DomainColumnReference;
import org.knime.js.base.node.parameters.slider.SliderNodeParametersUtil.RangeSection;
import org.knime.js.base.node.parameters.slider.SliderNodeParametersUtil.UseCustomMaxParameter;
import org.knime.js.base.node.parameters.slider.SliderNodeParametersUtil.UseCustomMinParameter;
import org.knime.js.base.node.parameters.slider.SliderWidgetNodeParametersUtil;
import org.knime.js.base.node.parameters.slider.SliderWidgetNodeParametersUtil.ValueMaxValidation;
import org.knime.js.base.node.parameters.slider.SliderWidgetNodeParametersUtil.ValueMinValidation;
import org.knime.js.base.node.widget.WidgetNodeParametersFlowVariable;
import org.knime.js.base.node.widget.input.slider.SliderWidgetSliderSettingsNodeParameters.LowerUpperBoundStateProvider;
import org.knime.node.parameters.NodeParameters;
import org.knime.node.parameters.NodeParametersInput;
import org.knime.node.parameters.Widget;
import org.knime.node.parameters.layout.Layout;
import org.knime.node.parameters.migration.LoadDefaultsForAbsentFields;
import org.knime.node.parameters.persistence.Persist;
import org.knime.node.parameters.persistence.Persistor;
import org.knime.node.parameters.updates.ParameterReference;
import org.knime.node.parameters.updates.StateProvider;
import org.knime.node.parameters.updates.ValueProvider;
import org.knime.node.parameters.updates.ValueReference;
import org.knime.node.parameters.widget.choices.ChoicesProvider;
import org.knime.node.parameters.widget.choices.util.CompatibleColumnsProvider.DoubleColumnsProvider;
import org.knime.node.parameters.widget.message.TextMessage;
import org.knime.node.parameters.widget.number.NumberInputWidget;

/**
 * Settings for the slider widget node.
 *
 * @author Robin Gerling
 */
@LoadDefaultsForAbsentFields
@SuppressWarnings("restriction")
public final class SliderWidgetNodeParameters extends WidgetNodeParametersFlowVariable {

    SliderWidgetNodeParameters() {
        super(SliderInputWidgetConfig.class);
    }

    @TextMessage(SliderOverwrittenByValueMessage.class)
    @Layout(OutputSection.Top.class)
    Void m_overwrittenByValueMessage;

    private static final class DefaultValue implements NodeParameters {
        @Widget(title = SliderNodeParametersUtil.DEFAULT_VALUE_TITLE,
            description = SliderNodeParametersUtil.DEFAULT_VALUE_DESCRIPTION)
        @ValueReference(DefaultValueReference.class)
        @Layout(OutputSection.Top.class)
        @NumberInputWidget(minValidationProvider = ValueMinValidation.class,
            maxValidationProvider = ValueMaxValidation.class)
        @ValueProvider(DefaultValueProvider.class)
        @Persist(configKey = SliderNodeValue.CFG_DOUBLE)
        double m_double;
    }

    DefaultValue m_defaultValue = new DefaultValue();

    @Widget(title = SliderNodeParametersUtil.RANGE_COLUMN_TITLE,
        description = SliderNodeParametersUtil.RANGE_COLUMN_DESCRIPTION)
    @ChoicesProvider(DoubleColumnsProvider.class)
    @ValueReference(DomainColumnReference.class)
    @Persistor(DomainColumnPersistor.class)
    @Layout(RangeSection.RangeColumn.class)
    StringOrEnum<NoneChoice> m_domainColumn = new StringOrEnum<>(NoneChoice.NONE);

    @PersistWithin.PersistEmbedded
    UseCustomMinParameter m_useCustomMinParameter = new UseCustomMinParameter();

    /**
     * Included for legacy reasons. The actual min value is stored in
     * {@link SliderWidgetSliderSettingsNodeParameters.RangeParameters.m_value0 m_sliderSettings.m_range.m_value0}
     */
    @Persist(configKey = SliderInputWidgetConfig.CFG_MIN_VALUE)
    double m_customMin = Double.NaN;

    @PersistWithin.PersistEmbedded
    UseCustomMaxParameter m_useCustomMaxParameter = new UseCustomMaxParameter();

    /**
     * Included for legacy reasons. The actual min value is stored in
     * {@link SliderWidgetSliderSettingsNodeParameters.RangeParameters.m_value1 m_sliderSettings.m_range.m_value1}
     */
    @Persist(configKey = SliderInputWidgetConfig.CFG_MAX_VALUE)
    double m_customMax = Double.NaN;

    @Persist(configKey = SliderInputWidgetConfig.CFG_SLIDER)
    SliderWidgetSliderSettingsNodeParameters m_sliderSettings = new SliderWidgetSliderSettingsNodeParameters();

    static final class DefaultValueReference implements ParameterReference<Double> {
    }

    private static final class SliderOverwrittenByValueMessage extends OverwrittenByValueMessage<SliderNodeValue> {

        @Override
        protected String valueToString(final SliderNodeValue value) {
            return String.valueOf(value.getDouble().doubleValue());
        }

    }

    private static final class DefaultValueProvider extends AbstractDefaultValueValueProvider<Double> {
        DefaultValueProvider() {
            super(DefaultValueReference.class, LowerUpperBoundStateProvider.class);
        }

        @Override
        public Double computeDefaultValue(final Double min, final Double max) {
            final var mean = (max - min) / 2 + min;
            return SliderWidgetNodeParametersUtil.roundTo6DecimalPlaces(mean);
        }

    }

    static final class DefaultValueMirrorProvider implements StateProvider<Double> {

        private Supplier<Double> m_defaultValueSupplier;

        @Override
        public void init(final StateProviderInitializer initializer) {
            initializer.computeBeforeOpenDialog();
            m_defaultValueSupplier = initializer.computeFromValueSupplier(DefaultValueReference.class);
        }

        @Override
        public Double computeState(final NodeParametersInput parametersInput) throws StateComputationFailureException {
            return m_defaultValueSupplier.get();
        }

    }

}
