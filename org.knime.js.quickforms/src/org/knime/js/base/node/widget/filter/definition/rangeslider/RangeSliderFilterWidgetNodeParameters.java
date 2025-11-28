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
 *   17 Nov 2025 (Robin Gerling): created
 */
package org.knime.js.base.node.widget.filter.definition.rangeslider;

import java.util.Optional;
import java.util.function.Supplier;

import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DoubleValue;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.webui.node.dialog.defaultdialog.util.updates.StateComputationFailureException;
import org.knime.js.base.node.parameters.ConfigurationAndWidgetNodeParametersUtil.FormFieldSection;
import org.knime.js.base.node.parameters.ConfigurationAndWidgetNodeParametersUtil.OutputSection;
import org.knime.js.base.node.parameters.slider.SliderNodeParametersUtil;
import org.knime.js.base.node.parameters.slider.SliderNodeParametersUtil.DefaultValueSection;
import org.knime.js.base.node.parameters.slider.SliderNodeParametersUtil.RangeSection;
import org.knime.js.base.node.parameters.slider.SliderNodeParametersUtil.UseCustomMaxReference;
import org.knime.js.base.node.parameters.slider.SliderNodeParametersUtil.UseCustomMinReference;
import org.knime.js.base.node.widget.WidgetNodeParametersBase;
import org.knime.js.base.node.widget.filter.definition.rangeslider.RangeSliderFilterWidgetSliderSettingsNodeParameters.IsDefiningMaximum;
import org.knime.js.base.node.widget.filter.definition.rangeslider.RangeSliderFilterWidgetSliderSettingsNodeParameters.IsDefiningMinimum;
import org.knime.js.base.node.widget.filter.definition.rangeslider.RangeSliderFilterWidgetSliderSettingsNodeParameters.RangeColumnReference;
import org.knime.js.base.node.widget.filter.definition.rangeslider.RangeSliderFilterWidgetSliderSettingsNodeParameters.UseCustomDefaultMaxReference;
import org.knime.js.base.node.widget.filter.definition.rangeslider.RangeSliderFilterWidgetSliderSettingsNodeParameters.UseCustomDefaultMinReference;
import org.knime.js.core.settings.slider.SliderNodeDialogUI;
import org.knime.node.parameters.NodeParameters;
import org.knime.node.parameters.NodeParametersInput;
import org.knime.node.parameters.Widget;
import org.knime.node.parameters.layout.Layout;
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
import org.knime.node.parameters.updates.legacy.ColumnNameAutoGuessValueProvider;
import org.knime.node.parameters.widget.choices.ChoicesProvider;
import org.knime.node.parameters.widget.choices.Label;
import org.knime.node.parameters.widget.choices.ValueSwitchWidget;
import org.knime.node.parameters.widget.choices.util.ColumnSelectionUtil;
import org.knime.node.parameters.widget.choices.util.CompatibleColumnsProvider.DoubleColumnsProvider;

/**
 * Widget-specific parameters wrapper for the range slider filter widget node.
 *
 * @author Robin Gerling
 */
@SuppressWarnings("restriction")
@LoadDefaultsForAbsentFields
public final class RangeSliderFilterWidgetNodeParameters extends WidgetNodeParametersBase {

    @Widget(title = "Merge with existing filter definitions (Table)",
        description = "Check this setting to keep any pre-existing filter definitions on the output table. "
            + "If not set only this node's filter definition is present on the output table.")
    @Persist(configKey = RangeSliderFilterWidgetConfig.CFG_MERGE_WITH_EXISTING_FILTERS_TABLE)
    @Layout(OutputSection.class)
    boolean m_mergeWithExistingFiltersTable = true;

    @Widget(title = "Merge with existing filter definitions (Model port)",
        description = "Check this setting to keep any pre-existing filter definitions on the model output port. "
            + "If not set only this node's filter definition is present on the output model.")
    @Persist(configKey = RangeSliderFilterWidgetConfig.CFG_MERGE_WITH_EXISTING_FILTERS_MODEL)
    @Layout(OutputSection.class)
    boolean m_mergeWithExistingFiltersModel = true;

    @Widget(title = "Label", description = "Display a label below or besides the slider.")
    @Persistor(DisplayLabelPersistor.class)
    @ValueReference(DisplayLabelReference.class)
    @Layout(FormFieldSection.class)
    @ValueSwitchWidget
    DisplayLabel m_displayLabel = DisplayLabel.NONE;

    @Widget(title = "Custom label", description = "The custom label to display.")
    @Persist(configKey = RangeSliderFilterWidgetConfig.CFG_LABEL)
    @Layout(FormFieldSection.class)
    @ValueReference(LabelReference.class)
    @ValueProvider(LabelProvider.class)
    @Effect(predicate = IsCustomLabel.class, type = EffectType.ENABLE)
    String m_label;

    @Widget(title = SliderNodeParametersUtil.RANGE_COLUMN_TITLE,
        description = "Select the column to apply the filter definition to. "
            + "Additionally the domain of the column can be used for the range of the slider.")
    @ChoicesProvider(DoubleColumnsProvider.class)
    @ValueReference(RangeColumnReference.class)
    @ValueProvider(RangeColumnValueProvider.class)
    @Persist(configKey = SliderNodeDialogUI.CFG_DOMAIN_COLUMN)
    @Layout(RangeSection.RangeColumn.class)
    String m_rangeColumn;

    @Widget(title = SliderNodeParametersUtil.USE_CUSTOM_MIN_TITLE,
        description = SliderNodeParametersUtil.USE_CUSTOM_MIN_DESCRIPTION)
    @Persist(configKey = RangeSliderFilterWidgetConfig.CFG_CUSTOM_MIN)
    @ValueReference(UseCustomMinReference.class)
    @ValueProvider(SetToFalseOnNewDomainColumn.class)
    @Layout(RangeSection.UseCustomMin.class)
    boolean m_useCustomMin = RangeSliderFilterWidgetConfig.DEFAULT_CUSTOM_MIN;

    @Widget(title = SliderNodeParametersUtil.USE_CUSTOM_MAX_TITLE,
        description = SliderNodeParametersUtil.USE_CUSTOM_MAX_DESCRIPTION)
    @Persist(configKey = RangeSliderFilterWidgetConfig.CFG_CUSTOM_MAX)
    @ValueReference(UseCustomMaxReference.class)
    @ValueProvider(SetToFalseOnNewDomainColumn.class)
    @Layout(RangeSection.UseCustomMax.class)
    boolean m_useCustomMax = RangeSliderFilterWidgetConfig.DEFAULT_CUSTOM_MAX;

    @Persistor(DomainExtendsPersistor.class)
    @ValueProvider(UseCustomDefaultValuesProvider.class)
    UseCustomDefaultValuesParameters m_domainExtents = new UseCustomDefaultValuesParameters();

    @Persist(configKey = RangeSliderFilterWidgetConfig.CFG_SLIDER)
    RangeSliderFilterWidgetSliderSettingsNodeParameters m_sliderSettings =
        new RangeSliderFilterWidgetSliderSettingsNodeParameters();

    enum DisplayLabel {
            @Label(value = "None", description = "No label will be displayed.")
            NONE, //
            @Label(value = "Column name",
                description = "The column name of the selected range column will be used as label.")
            COLUMN_NAME, //
            @Label(value = "Custom", description = "Use a custom label.")
            CUSTOM;
    }

    private static final class DisplayLabelReference implements ParameterReference<DisplayLabel> {
    }

    private static final class LabelReference implements ParameterReference<String> {
    }

    private static final class IsCustomLabel implements EffectPredicateProvider {
        @Override
        public EffectPredicate init(final PredicateInitializer i) {
            return i.getEnum(DisplayLabelReference.class).isOneOf(DisplayLabel.CUSTOM);
        }
    }

    private static final class UseCustomDefaultValuesParameters implements NodeParameters {

        UseCustomDefaultValuesParameters() {
        }

        UseCustomDefaultValuesParameters(final boolean useCustomDefaultMin, final boolean useCustomDefaultMax) {
            m_useCustomDefaultMin = useCustomDefaultMin;
            m_useCustomDefaultMax = useCustomDefaultMax;
        }

        @Widget(title = "Use custom default minimum value",
            description = "Check to specify a custom default minimum value for the minimum slider handle instead of"
                + " using the minimum from the range column.")
        @ValueReference(UseCustomDefaultMinReference.class)
        @Effect(predicate = IsDefiningMinimum.class, type = EffectType.SHOW)
        @Layout(DefaultValueSection.UseCustomDefaultMin.class)
        boolean m_useCustomDefaultMin;

        @Widget(title = "Use custom default maximum value",
            description = "Check to specify a custom default maximum value for the maximum slider handle instead of"
                + " using the maximum from the range column.")
        @ValueReference(UseCustomDefaultMaxReference.class)
        @Effect(predicate = IsDefiningMaximum.class, type = EffectType.SHOW)
        @Layout(DefaultValueSection.UseCustomDefaultMax.class)
        boolean m_useCustomDefaultMax;

    }

    private static final class SetToFalseOnNewDomainColumn implements StateProvider<Boolean> {

        @Override
        public void init(final StateProviderInitializer initializer) {
            initializer.computeOnValueChange(RangeColumnReference.class);
        }

        @Override
        public Boolean computeState(final NodeParametersInput parametersInput) throws StateComputationFailureException {
            return false;
        }

    }

    private static final class LabelProvider implements StateProvider<String> {

        private Supplier<DisplayLabel> m_displayLabelSupplier;

        private Supplier<String> m_rangeColumnSupplier;

        @Override
        public void init(final StateProviderInitializer initializer) {
            initializer.computeBeforeOpenDialog();
            m_displayLabelSupplier = initializer.computeFromValueSupplier(DisplayLabelReference.class);
            m_rangeColumnSupplier = initializer.getValueSupplier(RangeColumnReference.class);
        }

        @Override
        public String computeState(final NodeParametersInput parametersInput) throws StateComputationFailureException {
            return switch (m_displayLabelSupplier.get()) {
                case CUSTOM -> throw new StateComputationFailureException();
                case COLUMN_NAME -> m_rangeColumnSupplier.get();
                case NONE -> "";
            };
        }

    }

    private static final class UseCustomDefaultValuesProvider
        implements StateProvider<UseCustomDefaultValuesParameters> {

        @Override
        public void init(final StateProviderInitializer initializer) {
            initializer.computeOnValueChange(RangeColumnReference.class);
        }

        @Override
        public UseCustomDefaultValuesParameters computeState(final NodeParametersInput parametersInput)
            throws StateComputationFailureException {
            return new UseCustomDefaultValuesParameters();
        }

    }

    private static final class RangeColumnValueProvider extends ColumnNameAutoGuessValueProvider {

        RangeColumnValueProvider() {
            super(RangeColumnReference.class);
        }

        @Override
        protected boolean isEmpty(final String value) {
            return value == null;
        }

        @Override
        protected Optional<DataColumnSpec> autoGuessColumn(final NodeParametersInput parametersInput) {
            return ColumnSelectionUtil.getFirstCompatibleColumnOfFirstPort(parametersInput, DoubleValue.class);
        }

    }

    private static final class DisplayLabelPersistor implements NodeParametersPersistor<DisplayLabel> {

        @Override
        public DisplayLabel load(final NodeSettingsRO settings) throws InvalidSettingsException {
            final var useLabel = settings.getBoolean(RangeSliderFilterWidgetConfig.CFG_USE_LABEL);
            if (!useLabel) {
                return DisplayLabel.NONE;
            }
            final var customLabel = settings.getBoolean(RangeSliderFilterWidgetConfig.CFG_CUSTOM_LABEL);
            return customLabel ? DisplayLabel.CUSTOM : DisplayLabel.COLUMN_NAME;
        }

        @Override
        public void save(final DisplayLabel param, final NodeSettingsWO settings) {
            settings.addBoolean(RangeSliderFilterWidgetConfig.CFG_USE_LABEL, param != DisplayLabel.NONE);
            settings.addBoolean(RangeSliderFilterWidgetConfig.CFG_CUSTOM_LABEL, param == DisplayLabel.CUSTOM);
        }

        @Override
        public String[][] getConfigPaths() {
            return new String[][]{{RangeSliderFilterWidgetConfig.CFG_USE_LABEL},
                {RangeSliderFilterWidgetConfig.CFG_CUSTOM_LABEL}};
        }

    }

    private static final class DomainExtendsPersistor
        implements NodeParametersPersistor<UseCustomDefaultValuesParameters> {

        @Override
        public UseCustomDefaultValuesParameters load(final NodeSettingsRO settings) throws InvalidSettingsException {
            final var domainExtends = settings.getBooleanArray(RangeSliderFilterWidgetConfig.CFG_USE_DOMAIN_EXTENDS);
            return new UseCustomDefaultValuesParameters(!domainExtends[0], !domainExtends[1]);
        }

        @Override
        public void save(final UseCustomDefaultValuesParameters param, final NodeSettingsWO settings) {
            settings.addBooleanArray(RangeSliderFilterWidgetConfig.CFG_USE_DOMAIN_EXTENDS, !param.m_useCustomDefaultMin,
                !param.m_useCustomDefaultMax);
        }

        @Override
        public String[][] getConfigPaths() {
            return new String[][]{{RangeSliderFilterWidgetConfig.CFG_USE_DOMAIN_EXTENDS}};
        }

    }

}
