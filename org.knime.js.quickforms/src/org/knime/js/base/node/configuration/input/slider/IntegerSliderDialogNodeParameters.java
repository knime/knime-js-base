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

import java.util.List;

import org.knime.core.data.DataCell;
import org.knime.core.data.IntValue;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.webui.node.dialog.defaultdialog.internal.widget.PersistWithin;
import org.knime.core.webui.node.dialog.defaultdialog.setting.singleselection.NoneChoice;
import org.knime.core.webui.node.dialog.defaultdialog.setting.singleselection.StringOrEnum;
import org.knime.js.base.node.base.input.slider.SliderNodeValue;
import org.knime.js.base.node.configuration.ConfigurationNodeSettings;
import org.knime.js.base.node.parameters.ConfigurationAndWidgetNodeParametersUtil.FormFieldSection;
import org.knime.js.base.node.parameters.ConfigurationAndWidgetNodeParametersUtil.OutputSection;
import org.knime.js.base.node.parameters.OverwrittenByValueMessage;
import org.knime.js.base.node.parameters.slider.SliderNodeParametersUtil;
import org.knime.js.base.node.parameters.slider.SliderNodeParametersUtil.AbstractCustomMaxValueProvider;
import org.knime.js.base.node.parameters.slider.SliderNodeParametersUtil.AbstractCustomMinValueProvider;
import org.knime.js.base.node.parameters.slider.SliderNodeParametersUtil.AbstractDefaultValueValueProvider;
import org.knime.js.base.node.parameters.slider.SliderNodeParametersUtil.AbstractLowerUpperBoundStateProvider;
import org.knime.js.base.node.parameters.slider.SliderNodeParametersUtil.AbstractValueMaxValidation;
import org.knime.js.base.node.parameters.slider.SliderNodeParametersUtil.AbstractValueMinValidation;
import org.knime.js.base.node.parameters.slider.SliderNodeParametersUtil.DomainColumnPersistor;
import org.knime.js.base.node.parameters.slider.SliderNodeParametersUtil.DomainColumnReference;
import org.knime.js.base.node.parameters.slider.SliderNodeParametersUtil.UseCustomMax;
import org.knime.js.base.node.parameters.slider.SliderNodeParametersUtil.UseCustomMaxParameter;
import org.knime.js.base.node.parameters.slider.SliderNodeParametersUtil.UseCustomMin;
import org.knime.js.base.node.parameters.slider.SliderNodeParametersUtil.UseCustomMinParameter;
import org.knime.node.parameters.NodeParameters;
import org.knime.node.parameters.Widget;
import org.knime.node.parameters.layout.After;
import org.knime.node.parameters.layout.Before;
import org.knime.node.parameters.layout.Layout;
import org.knime.node.parameters.layout.Section;
import org.knime.node.parameters.migration.LoadDefaultsForAbsentFields;
import org.knime.node.parameters.persistence.NodeParametersPersistor;
import org.knime.node.parameters.persistence.Persistor;
import org.knime.node.parameters.updates.Effect;
import org.knime.node.parameters.updates.Effect.EffectType;
import org.knime.node.parameters.updates.ParameterReference;
import org.knime.node.parameters.updates.ValueProvider;
import org.knime.node.parameters.updates.ValueReference;
import org.knime.node.parameters.widget.choices.ChoicesProvider;
import org.knime.node.parameters.widget.choices.util.CompatibleColumnsProvider;
import org.knime.node.parameters.widget.message.TextMessage;
import org.knime.node.parameters.widget.number.NumberInputWidget;

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
        @Widget(title = SliderNodeParametersUtil.DEFAULT_VALUE_TITLE,
            description = SliderNodeParametersUtil.DEFAULT_VALUE_DESCRIPTION)
        @Persistor(DefaultValuePersistor.class)
        @ValueReference(DefaultValueReference.class)
        @ValueProvider(DefaultValueValueProvider.class)
        @Layout(OutputSection.Top.class)
        @NumberInputWidget(minValidationProvider = ValueMinValidation.class,
            maxValidationProvider = ValueMaxValidation.class)
        int m_integer = 50;
    }

    DefaultValue m_defaultValue = new DefaultValue();

    @Widget(title = SliderNodeParametersUtil.RANGE_COLUMN_TITLE,
        description = SliderNodeParametersUtil.RANGE_COLUMN_DESCRIPTION)
    @ChoicesProvider(DomainColumnChoicesProvider.class)
    @ValueReference(DomainColumnReference.class)
    @Persistor(DomainColumnPersistor.class)
    @Layout(SliderNodeParametersUtil.RangeSection.RangeColumn.class)
    StringOrEnum<NoneChoice> m_domainColumn = new StringOrEnum<>(NoneChoice.NONE);

    @PersistWithin.PersistEmbedded
    UseCustomMinParameter m_useCustomMinParameter = new UseCustomMinParameter();

    @Widget(title = SliderNodeParametersUtil.MINIMUM_TITLE, description = SliderNodeParametersUtil.MINIMUM_DESCRIPTION)
    @Persistor(CustomMinPersistor.class)
    @ValueReference(CustomMinReference.class)
    @ValueProvider(CustomMinValueProvider.class)
    @Layout(SliderNodeParametersUtil.RangeSection.Minimum.class)
    @Effect(predicate = UseCustomMin.class, type = EffectType.ENABLE)
    int m_customMin = (int)IntegerSliderDialogNodeConfig.DEFAULT_MIN;

    @PersistWithin.PersistEmbedded
    UseCustomMaxParameter m_useCustomMaxParameter = new UseCustomMaxParameter();

    @Widget(title = SliderNodeParametersUtil.MAXIMUM_TITLE, description = SliderNodeParametersUtil.MAXIMUM_DESCRIPTION)
    @Persistor(CustomMaxPersistor.class)
    @ValueReference(CustomMaxReference.class)
    @ValueProvider(CustomMaxValueProvider.class)
    @Layout(SliderNodeParametersUtil.RangeSection.Maximum.class)
    @Effect(predicate = UseCustomMax.class, type = EffectType.ENABLE)
    int m_customMax = (int)IntegerSliderDialogNodeConfig.DEFAULT_MAX;

    private static final class IntegerSliderOverwrittenByValueMessage
        extends OverwrittenByValueMessage<IntegerSliderDialogNodeValue> {

        @Override
        protected String valueToString(final IntegerSliderDialogNodeValue value) {
            return String.valueOf(value.getDouble().intValue());
        }

    }

    private abstract static class IntegerToDoublePersistor implements NodeParametersPersistor<Integer> {

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

    private static final class DefaultValuePersistor extends IntegerToDoublePersistor {
        public DefaultValuePersistor() {
            super(SliderNodeValue.CFG_DOUBLE);
        }
    }

    private static final class CustomMinPersistor extends IntegerToDoublePersistor {
        public CustomMinPersistor() {
            super(IntegerSliderDialogNodeConfig.CFG_MIN);
        }
    }

    private static final class CustomMaxPersistor extends IntegerToDoublePersistor {
        public CustomMaxPersistor() {
            super(IntegerSliderDialogNodeConfig.CFG_MAX);
        }
    }

    private static final class CustomMinReference implements ParameterReference<Integer> {
    }

    private static final class DefaultValueReference implements ParameterReference<Integer> {
    }

    private static final class CustomMaxReference implements ParameterReference<Integer> {
    }

    private static final class LowerUpperBoundStateProvider extends AbstractLowerUpperBoundStateProvider<Integer> {
        LowerUpperBoundStateProvider() {
            super(IntValue.class);
        }

        @Override
        public Integer transformDataValueCompatibleCell(final DataCell cell) {
            return ((IntValue)cell).getIntValue();
        }
    }

    private static final class DefaultValueValueProvider extends AbstractDefaultValueValueProvider<Integer> {
        DefaultValueValueProvider() {
            super(DefaultValueReference.class, LowerUpperBoundStateProvider.class);
        }

        @Override
        public Integer computeDefaultValue(final Integer min, final Integer max) {
            return (max - min) / 2 + min;
        }

    }

    private static final class DomainColumnChoicesProvider extends CompatibleColumnsProvider {
        public DomainColumnChoicesProvider() {
            super(List.of(IntValue.class));
        }
    }

    private static final class ValueMinValidation extends AbstractValueMinValidation<Integer> {
        ValueMinValidation() {
            super(CustomMinReference.class);
        }
    }

    private static final class ValueMaxValidation extends AbstractValueMaxValidation<Integer> {
        ValueMaxValidation() {
            super(CustomMaxReference.class);
        }
    }

    private static final class CustomMinValueProvider extends AbstractCustomMinValueProvider<Integer> {
        CustomMinValueProvider() {
            super(LowerUpperBoundStateProvider.class);
        }
    }

    private static final class CustomMaxValueProvider extends AbstractCustomMaxValueProvider<Integer> {
        CustomMaxValueProvider() {
            super(LowerUpperBoundStateProvider.class);
        }
    }

}
