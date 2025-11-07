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
 *   10 Nov 2025 (Robin Gerling): created
 */
package org.knime.js.base.node.parameters.slider;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.webui.node.dialog.defaultdialog.internal.widget.PersistWithin;
import org.knime.js.base.node.parameters.slider.SliderNodeParametersUtil.LabelAndTicksSection;
import org.knime.js.core.settings.slider.SliderPipsSettings;
import org.knime.node.parameters.NodeParameters;
import org.knime.node.parameters.Widget;
import org.knime.node.parameters.layout.After;
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
import org.knime.node.parameters.updates.ValueReference;
import org.knime.node.parameters.widget.choices.Label;
import org.knime.node.parameters.widget.text.TextInputWidget;
import org.knime.node.parameters.widget.text.TextInputWidgetValidation.PatternValidation;

/**
 * Utility class to create the WebUI dialog for slider ticks settings.
 *
 * @author Robin Gerling, KNIME GmbH, Konstanz
 */
@SuppressWarnings("restriction")
@LoadDefaultsForAbsentFields
@Layout(LabelAndTicksSection.class)
public final class SliderTicksSettingsNodeParameters implements NodeParameters {

    private static final String CFG_MODE = "mode";

    private static final String CFG_DENSITY = "density";

    private static final String CFG_VALUES = "values";

    private static final String CFG_STEPPED = "stepped";

    private static final String CFG_FILTER = "filter";

    private static final String CFG_FORMAT = "format";

    private static final String CFG_FORMAT_DEFINED = "formatDefined";

    private static final String CFG_PIPS_DEFINED = "pipsDefined";

    /** Layout section for label format settings. */
    @Effect(predicate = ShowLabelSettings.class, type = EffectType.SHOW)
    @After(LabelAndTicksSection.class)
    @Section(title = "Label Format")
    public interface LabelFormatSection {
    }

    @Widget(title = "Enable labels & ticks",
        description = "Check, to enable the display of ticks and labels for the slider.")
    @Persist(configKey = CFG_PIPS_DEFINED)
    @ValueReference(EnableLabelsReference.class)
    boolean m_enableLabels;

    @Persistor(TicksSettingsPersistor.class)
    TicksSettings m_ticksSettings = new TicksSettings();

    @Effect(predicate = ShowLabelSettings.class, type = EffectType.SHOW)
    static final class TicksSettings implements NodeParameters {

        TicksSettings() {
        }

        TicksSettings(final TicksMode mode, final boolean stepped) {
            m_mode = mode;
            m_stepped = stepped;
        }

        TicksSettings(final TicksMode mode, final String values, final boolean stepped) {
            this(mode, stepped);
            if (mode == TicksMode.positions) {
                m_positionValues = values;
            } else if (mode == TicksMode.values) {
                m_values = values;
            }
        }

        TicksSettings(final TicksMode mode, final int count, final boolean stepped) {
            this(mode, stepped);
            m_countValue = count;
        }

        @Widget(title = "Ticks mode", description = "The mode after which the labels and ticks are displayed.")
        @ValueReference(TicksModeReference.class)
        TicksMode m_mode = TicksMode.range;

        @Widget(title = "Position values",
            description = "Provide the values as a comma separated list of values between 0 and 100.")
        @Effect(predicate = IsPositionsMode.class, type = EffectType.SHOW)
        @TextInputWidget(patternValidation = CommaSeparatedNumbersValidation.class)
        String m_positionValues = "0,25,50,75,100";

        @Widget(title = "Count", description = "Provide the specific number of labels to draw.")
        @Effect(predicate = IsCountMode.class, type = EffectType.SHOW)
        int m_countValue = 6;

        @Widget(title = "Values", description = "Draw labels and major ticks at positions of the actual values.")
        @Effect(predicate = IsValuesMode.class, type = EffectType.SHOW)
        @TextInputWidget(patternValidation = CommaSeparatedNumbersValidation.class)
        String m_values = "";

        @Widget(title = "Stepped",
            description = "Check to override calculated or given label positions,"
                + " to match the values to the provided slider steps.")
        @Effect(predicate = IsRangeOrStepsMode.class, type = EffectType.HIDE)
        boolean m_stepped;
    }

    @Widget(title = "Density",
        description = "A measure to pre-scale the number of ticks. Higher number means less ticks.")
    @Persistor(DensityPersistor.class)
    @Effect(predicate = ShowLabelSettings.class, type = EffectType.SHOW)
    int m_density = 3;

    @Persist(configKey = CFG_FILTER)
    String m_filter;

    @Layout(LabelFormatSection.class)
    @Widget(title = "Label format options", description = "Set formatting options for all labels drawn.")
    @Persist(configKey = CFG_FORMAT)
    NumberFormatParameters m_format = new NumberFormatParameters();

    @Persist(configKey = CFG_FORMAT_DEFINED)
    @PersistWithin(CFG_FORMAT)
    boolean m_formatDefined = true;

    private static final class EnableLabelsReference implements ParameterReference<Boolean> {
    }

    private static final class TicksModeReference implements ParameterReference<TicksMode> {
    }

    private static final class CommaSeparatedNumbersValidation extends PatternValidation {

        private static final String NUMBER_PATTERN = "[+-]?(\\d+(\\.\\d*)?|\\.\\d+)([eE][+-]?\\d+)?";

        @Override
        protected String getPattern() {
            return String.format("%s(\s*,\s*%s)*(\s*,)?", NUMBER_PATTERN, NUMBER_PATTERN);
        }

        @Override
        public String getErrorMessage() {
            return "The string must consist of comma-separated numbers.";
        }
    }

    private static final class ShowLabelSettings implements EffectPredicateProvider {
        @Override
        public EffectPredicate init(final PredicateInitializer i) {
            return i.getBoolean(EnableLabelsReference.class).isTrue();
        }
    }

    private static final class IsPositionsMode implements EffectPredicateProvider {
        @Override
        public EffectPredicate init(final PredicateInitializer i) {
            return i.getEnum(TicksModeReference.class).isOneOf(TicksMode.positions);
        }
    }

    private static final class IsCountMode implements EffectPredicateProvider {
        @Override
        public EffectPredicate init(final PredicateInitializer i) {
            return i.getEnum(TicksModeReference.class).isOneOf(TicksMode.count);
        }
    }

    private static final class IsValuesMode implements EffectPredicateProvider {
        @Override
        public EffectPredicate init(final PredicateInitializer i) {
            return i.getEnum(TicksModeReference.class).isOneOf(TicksMode.values);
        }
    }

    private static final class IsRangeOrStepsMode implements EffectPredicateProvider {
        @Override
        public EffectPredicate init(final PredicateInitializer i) {
            return i.getEnum(TicksModeReference.class).isOneOf(TicksMode.range, TicksMode.steps);

        }
    }

    /**
     * We cannot use the {@link SliderPipsSettings.PipMode} because we need lowercase enum constants for backward
     * compatibility.
     */
    @SuppressWarnings("java:S115")
    enum TicksMode {
            @Label(value = "Range", description = "Draw labels at the specified range points."
                + " For linear sliders this is only minimum and maximum.")
            range, //
            @Label(value = "Steps",
                description = "Draws labels at specified steps (See \"Step size\" option). Major ticks and labels are"
                    + " generated at the minimum and maximum and smaller labels at each step size.")
            steps, //
            @Label(value = "Positions",
                description = "Draw labels and major ticks at set positions (percentages). Use the \"Position values\""
                    + " field to specify the positions as a comma separated list of values between 0 and 100.")
            positions, //
            @Label(value = "Count", description = "Draws the number of labels specified."
                + " Use the \"Count\" field to provide the count as an integer number.")
            count, //
            @Label(value = "Values", description = "Draw labels and major ticks at positions of the actual values."
                + " Use the \"Values\" field to specify the values as a comma separated list of numbers.")
            values;
    }

    private static final class TicksSettingsPersistor implements NodeParametersPersistor<TicksSettings> {

        @Override
        public TicksSettings load(final NodeSettingsRO settings) throws InvalidSettingsException {
            final var ticksMode = TicksMode.valueOf(settings.getString(CFG_MODE));
            if (ticksMode == TicksMode.range || ticksMode == TicksMode.steps) {
                return new TicksSettings(ticksMode, false);
            }
            final var values = settings.getDoubleArray(CFG_VALUES);
            final var stepped = settings.getBooleanArray(CFG_STEPPED)[0];
            if (ticksMode == TicksMode.count) {
                return new TicksSettings(ticksMode, (int)values[0], stepped);
            }
            final var valuesString = Arrays.stream(values).mapToObj(String::valueOf).collect(Collectors.joining(","));
            return new TicksSettings(ticksMode, valuesString, stepped);
        }

        @Override
        public void save(final TicksSettings param, final NodeSettingsWO settings) {
            settings.addString(CFG_MODE, param.m_mode.name());
            double[] values = null;
            boolean[] stepped = new boolean[]{param.m_stepped};
            if (param.m_mode == TicksMode.count) {
                values = new double[]{param.m_countValue};
            } else if (param.m_mode == TicksMode.positions || param.m_mode == TicksMode.values) {
                final var valuesString = param.m_mode == TicksMode.positions ? param.m_positionValues : param.m_values;
                /**
                 * This might lead to a number format exception and the user won't be able to save the settings. But it
                 * is not visible by a toast in the frontend, only in the KNIME log.
                 */
                values = Arrays.stream(valuesString.split(",")) //
                    .map(String::trim) //
                    .mapToDouble(Double::parseDouble).toArray();
            } else {
                stepped = null;
            }
            settings.addDoubleArray(CFG_VALUES, values);
            settings.addBooleanArray(CFG_STEPPED, stepped);
        }

        @Override
        public String[][] getConfigPaths() {
            return new String[][]{{CFG_MODE}, {CFG_VALUES}, {CFG_STEPPED}};
        }

    }

    private static final class DensityPersistor implements NodeParametersPersistor<Integer> {

        @Override
        public Integer load(final NodeSettingsRO settings) throws InvalidSettingsException {
            return settings.getIntArray(CFG_DENSITY)[0];
        }

        @Override
        public void save(final Integer param, final NodeSettingsWO settings) {
            settings.addIntArray(CFG_DENSITY, param);
        }

        @Override
        public String[][] getConfigPaths() {
            return new String[][]{{CFG_DENSITY}};
        }
    }

}
