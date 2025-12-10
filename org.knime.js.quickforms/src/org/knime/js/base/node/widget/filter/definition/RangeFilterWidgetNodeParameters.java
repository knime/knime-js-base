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
package org.knime.js.base.node.widget.filter.definition;

import java.util.function.Supplier;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.webui.node.dialog.defaultdialog.util.updates.StateComputationFailureException;
import org.knime.js.base.node.parameters.ConfigurationAndWidgetNodeParametersUtil.FormFieldSection;
import org.knime.js.base.node.parameters.ConfigurationAndWidgetNodeParametersUtil.OutputSection;
import org.knime.node.parameters.NodeParameters;
import org.knime.node.parameters.NodeParametersInput;
import org.knime.node.parameters.Widget;
import org.knime.node.parameters.layout.Layout;
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
import org.knime.node.parameters.widget.choices.Label;
import org.knime.node.parameters.widget.choices.ValueSwitchWidget;

/**
 * Common parameters for the range filter widget nodes.
 *
 * @author Robin Gerling, KNIME GmbH, Konstanz, Germany
 */
@SuppressWarnings("restriction")
public class RangeFilterWidgetNodeParameters implements NodeParameters {

    /** Configuration key for whether to use a label */
    public static final String CFG_USE_LABEL = "useLabel";

    /** Default value for whether to use a label */
    public static final boolean DEFAULT_USE_LABEL = false;

    /** Configuration key for the label */
    public static final String CFG_LABEL = "label";

    /** Configuration key for whether to use a custom label */
    public static final String CFG_CUSTOM_LABEL = "customLabel";

    /** Default value for whether to use a custom label */
    public static final boolean DEFAULT_CUSTOM_LABEL = false;

    /** Configuration key for merging with existing filters on the output table */
    public static final String CFG_MERGE_WITH_EXISTING_FILTERS_TABLE = "mergeWithExistingFiltersTable";

    /** Default value for merging with existing filters on the output table */
    public static final boolean DEFAULT_MERGE_WITH_EXISTING_FILTERS_TABLE = true;

    /** Configuration key for merging with existing filters on the model output port */
    public static final String CFG_MERGE_WITH_EXISTING_FILTERS_MODEL = "mergeWithExistingFiltersModel";

    /** Default value for merging with existing filters on the model output port */
    public static final boolean DEFAULT_MERGE_WITH_EXISTING_FILTERS_MODEL = true;

    @Widget(title = "Merge with existing filter definitions (Table)",
        description = "Check this setting to keep any pre-existing filter definitions on the output table. "
            + "If not set only this node's filter definition is present on the output table.")
    @Persist(configKey = CFG_MERGE_WITH_EXISTING_FILTERS_TABLE)
    @Layout(OutputSection.class)
    boolean m_mergeWithExistingFiltersTable = DEFAULT_MERGE_WITH_EXISTING_FILTERS_TABLE;

    @Widget(title = "Merge with existing filter definitions (Model port)",
        description = "Check this setting to keep any pre-existing filter definitions on the model output port. "
            + "If not set only this node's filter definition is present on the output model.")
    @Persist(configKey = CFG_MERGE_WITH_EXISTING_FILTERS_MODEL)
    @Layout(OutputSection.class)
    boolean m_mergeWithExistingFiltersModel = DEFAULT_MERGE_WITH_EXISTING_FILTERS_MODEL;

    @Widget(title = "Label", description = "Display a label below or besides the slider.")
    @Persistor(DisplayLabelPersistor.class)
    @ValueReference(DisplayLabelReference.class)
    @Layout(FormFieldSection.class)
    @ValueSwitchWidget
    DisplayLabel m_displayLabel = DisplayLabel.NONE;

    @Widget(title = "Custom label", description = "The custom label to display.")
    @Persist(configKey = CFG_LABEL)
    @Layout(FormFieldSection.class)
    @ValueReference(LabelReference.class)
    @ValueProvider(LabelProvider.class)
    @Effect(predicate = IsCustomLabel.class, type = EffectType.ENABLE)
    String m_label;

    enum DisplayLabel {
            @Label(value = "None", description = "No label will be displayed.")
            NONE, //
            @Label(value = "Column name",
                description = "The column name of the selected range column will be used as label.")
            COLUMN_NAME, //
            @Label(value = "Custom", description = "Use a custom label.")
            CUSTOM;
    }

    private static final class IsCustomLabel implements EffectPredicateProvider {
        @Override
        public EffectPredicate init(final PredicateInitializer i) {
            return i.getEnum(DisplayLabelReference.class).isOneOf(DisplayLabel.CUSTOM);
        }
    }

    private static final class DisplayLabelReference implements ParameterReference<DisplayLabel> {
    }

    private static final class LabelReference implements ParameterReference<String> {
    }

    private static final class DisplayLabelPersistor implements NodeParametersPersistor<DisplayLabel> {

        @Override
        public DisplayLabel load(final NodeSettingsRO settings) throws InvalidSettingsException {
            final var useLabel = settings.getBoolean(CFG_USE_LABEL);
            if (!useLabel) {
                return DisplayLabel.NONE;
            }
            final var customLabel = settings.getBoolean(CFG_CUSTOM_LABEL);
            return customLabel ? DisplayLabel.CUSTOM : DisplayLabel.COLUMN_NAME;
        }

        @Override
        public void save(final DisplayLabel param, final NodeSettingsWO settings) {
            settings.addBoolean(CFG_USE_LABEL, param != DisplayLabel.NONE);
            settings.addBoolean(CFG_CUSTOM_LABEL, param == DisplayLabel.CUSTOM);
        }

        @Override
        public String[][] getConfigPaths() {
            return new String[][]{{CFG_USE_LABEL}, {CFG_CUSTOM_LABEL}};
        }

    }

    /** Reference to the column to filter on */
    public static final class FilterColumnReference implements ParameterReference<String> {
    }

    private static final class LabelProvider implements StateProvider<String> {

        private Supplier<DisplayLabel> m_displayLabelSupplier;

        private Supplier<String> m_rangeColumnSupplier;

        @Override
        public void init(final StateProviderInitializer initializer) {
            initializer.computeBeforeOpenDialog();
            m_displayLabelSupplier = initializer.computeFromValueSupplier(DisplayLabelReference.class);
            m_rangeColumnSupplier = initializer.getValueSupplier(FilterColumnReference.class);
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
}
