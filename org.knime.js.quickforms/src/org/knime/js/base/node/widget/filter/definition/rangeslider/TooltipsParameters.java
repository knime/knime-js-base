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

import java.util.Arrays;
import java.util.stream.Stream;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.webui.node.dialog.defaultdialog.widget.Modification;
import org.knime.js.base.node.parameters.slider.TooltipParameters;
import org.knime.js.base.node.parameters.slider.TooltipParameters.TooltipParametersModification;
import org.knime.js.base.node.widget.filter.definition.rangeslider.RangeSliderFilterWidgetSliderSettingsNodeParameters.IsDefiningMaximum;
import org.knime.js.base.node.widget.filter.definition.rangeslider.RangeSliderFilterWidgetSliderSettingsNodeParameters.IsDefiningMinimum;
import org.knime.node.parameters.NodeParameters;
import org.knime.node.parameters.persistence.NodeParametersPersistor;
import org.knime.node.parameters.updates.Effect;
import org.knime.node.parameters.updates.Effect.EffectType;
import org.knime.node.parameters.updates.EffectPredicate;
import org.knime.node.parameters.updates.EffectPredicateProvider;
import org.knime.node.parameters.updates.ParameterReference;
import org.knime.node.parameters.updates.util.BooleanReference;

/**
 * Parameters for the tooltips of the range slider filter widget.
 *
 * @author Robin Gerling, KNIME GmbH, Konstanz, Germany
 */
@SuppressWarnings("restriction")
final class TooltipsParameters implements NodeParameters {

    TooltipsParameters() {
    }

    TooltipsParameters(final TooltipParameters tooltipMinimum, final TooltipParameters tooltipMaximum) {
        m_tooltipMinimum = tooltipMinimum;
        m_tooltipMaximum = tooltipMaximum;
    }

    @Modification(MinimumTooltipModification.class)
    @Effect(predicate = IsDefiningMinimum.class, type = EffectType.SHOW)
    TooltipParameters m_tooltipMinimum = new TooltipParameters();

    @Modification(MaximumTooltipModification.class)
    @Effect(predicate = IsDefiningMaximum.class, type = EffectType.SHOW)
    TooltipParameters m_tooltipMaximum = new TooltipParameters();

    static final class TooltipsPersistor implements NodeParametersPersistor<TooltipsParameters> {

        private static final String CFG_TOOLTIPS = "tooltips";

        @Override
        public TooltipsParameters load(final NodeSettingsRO settings) throws InvalidSettingsException {
            final var tooltipSettings = settings.getNodeSettings(CFG_TOOLTIPS);
            final var minTooltipSettings = TooltipParameters.loadTooltipParameters(tooltipSettings, 0);
            final var maxTooltipSettings = TooltipParameters.loadTooltipParameters(tooltipSettings, 1);
            return new TooltipsParameters(minTooltipSettings, maxTooltipSettings);
        }

        @Override
        public void save(final TooltipsParameters param, final NodeSettingsWO settings) {
            final var tooltipSettings = settings.addNodeSettings(CFG_TOOLTIPS);
            TooltipParameters.saveTooltipParameters(param.m_tooltipMinimum, tooltipSettings, 0);
            TooltipParameters.saveTooltipParameters(param.m_tooltipMaximum, tooltipSettings, 1);
            if (param.m_tooltipMinimum.m_showTooltip || param.m_tooltipMaximum.m_showTooltip) {
                tooltipSettings.addInt(TooltipParameters.CFG_NUM_SETTINGS, 2);
            }

        }

        private static Stream<String[]> createConfigPath(final int configKeySuffix) {
            return Arrays.stream(TooltipParameters.getTooltipConfigPaths(configKeySuffix)) //
                .map(row -> Stream.concat( //
                    Stream.of(CFG_TOOLTIPS), //
                    Arrays.stream(row) //
                ).toArray(String[]::new));
        }

        @Override
        public String[][] getConfigPaths() {
            return Stream.concat(createConfigPath(0), createConfigPath(1)).toArray(String[][]::new);
        }
    }

    private static final class MinimumTooltipModification extends TooltipParametersModification {

        @Override
        public String getShowTooltipTitle() {
            return "Show tooltip for minimum slider handle";
        }

        @Override
        public String getUseFormatterTitle() {
            return "Use formatter for tooltip of minimum slider handle";
        }

        @Override
        public Class<? extends ParameterReference<Boolean>> getShowTooltipReference() {
            return ShowTooltipReference.class;
        }

        @Override
        public Class<? extends ParameterReference<Boolean>> getUseFormatterReference() {
            return UseFormatterForTooltipReference.class;
        }

        @Override
        public Class<? extends EffectPredicateProvider> getShowTooltipPredicate() {
            return ShowTooltipForMin.class;
        }

        @Override
        public Class<? extends EffectPredicateProvider> getShowTooltipAndUseFormatterPredicate() {
            return ShowTooltipAndUseFormatterForMin.class;
        }

        @Override
        public boolean removeNumberFormatParametersFromDescription() {
            return true;
        }

        private static final class ShowTooltipReference implements BooleanReference {
        }

        private static final class UseFormatterForTooltipReference implements BooleanReference {
        }

        private static final class ShowTooltipForMin implements EffectPredicateProvider {
            @Override
            public EffectPredicate init(final PredicateInitializer i) {
                return and(i.getPredicate(ShowTooltipReference.class), i.getPredicate(IsDefiningMinimum.class));
            }
        }

        private static final class ShowTooltipAndUseFormatterForMin implements EffectPredicateProvider {
            @Override
            public EffectPredicate init(final PredicateInitializer i) {
                return i.getPredicate(ShowTooltipForMin.class)
                    .and(i.getPredicate(UseFormatterForTooltipReference.class));
            }
        }

    }

    private static final class MaximumTooltipModification extends TooltipParametersModification {

        @Override
        public String getShowTooltipTitle() {
            return "Show tooltip for maximum slider handle";
        }

        @Override
        public String getUseFormatterTitle() {
            return "Use formatter for tooltip of maximum slider handle";
        }

        @Override
        public Class<? extends ParameterReference<Boolean>> getShowTooltipReference() {
            return ShowTooltipReference.class;
        }

        @Override
        public Class<? extends ParameterReference<Boolean>> getUseFormatterReference() {
            return UseFormatterForTooltipReference.class;
        }

        @Override
        public Class<? extends EffectPredicateProvider> getShowTooltipPredicate() {
            return ShowTooltipForMax.class;
        }

        @Override
        public Class<? extends EffectPredicateProvider> getShowTooltipAndUseFormatterPredicate() {
            return ShowTooltipAndUseFormatterForMax.class;
        }

        private static final class ShowTooltipReference implements BooleanReference {
        }

        private static final class UseFormatterForTooltipReference implements BooleanReference {
        }

        private static final class ShowTooltipForMax implements EffectPredicateProvider {
            @Override
            public EffectPredicate init(final PredicateInitializer i) {
                return and(i.getPredicate(ShowTooltipReference.class), i.getPredicate(IsDefiningMaximum.class));
            }
        }

        private static final class ShowTooltipAndUseFormatterForMax implements EffectPredicateProvider {
            @Override
            public EffectPredicate init(final PredicateInitializer i) {
                return i.getPredicate(ShowTooltipForMax.class)
                    .and(i.getPredicate(UseFormatterForTooltipReference.class));
            }
        }
    }

}
