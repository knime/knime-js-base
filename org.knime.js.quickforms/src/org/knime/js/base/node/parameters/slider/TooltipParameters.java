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
 *   11 Nov 2025 (robin): created
 */
package org.knime.js.base.node.parameters.slider;

import java.util.Arrays;
import java.util.stream.Stream;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.js.core.settings.slider.SliderSettings;
import org.knime.node.parameters.NodeParameters;
import org.knime.node.parameters.Widget;
import org.knime.node.parameters.persistence.NodeParametersPersistor;
import org.knime.node.parameters.updates.Effect;
import org.knime.node.parameters.updates.Effect.EffectType;
import org.knime.node.parameters.updates.EffectPredicate;
import org.knime.node.parameters.updates.EffectPredicateProvider;
import org.knime.node.parameters.updates.ParameterReference;
import org.knime.node.parameters.updates.ValueReference;

/**
 * Tooltip related parameters for slider widgets (see {@link SliderSettings}).
 *
 * @author Robin Gerling, KNIME GmbH, Konstanz
 */
public final class TooltipParameters implements NodeParameters {

    private static final String TOOLTIP_TYPE_BOOLEAN = "boolean";

    private static final String TOOLTIP_TYPE_FORMAT = "format";

    private static final String CFG_TOOLTIP_TYPE_PREFIX = "type_";

    private static final String CFG_TOOLTIP_VALUE_PREFIX = "value_";

    private static final String NUM_SETTINGS = "numSettings";

    /** Default constructor. */
    public TooltipParameters() {
    }

    TooltipParameters(final boolean showTooltip, final boolean useFormatter,
        final NumberFormatParameters numberFormatParameters) {
        m_showTooltip = showTooltip;
        m_useFormatter = useFormatter;
        m_numberFormatParameters = numberFormatParameters;
    }

    @Widget(title = "Show tooltip",
        description = "Check, if the currently selected value on the slider is supposed to be shown in a tooltip.")
    @ValueReference(ShowTooltipReference.class)
    boolean m_showTooltip;

    @Widget(title = "Use formatter for tooltip", description = "Enable or disable formatting options for the tooltip.")
    @ValueReference(UseFormatterForTooltipReference.class)
    @Effect(predicate = ShowTooltip.class, type = EffectType.SHOW)
    boolean m_useFormatter;

    @Effect(predicate = ShowTooltipAndUseFormatter.class, type = EffectType.SHOW)
    NumberFormatParameters m_numberFormatParameters = new NumberFormatParameters();

    private static final class ShowTooltipReference implements ParameterReference<Boolean> {
    }

    private static final class UseFormatterForTooltipReference implements ParameterReference<Boolean> {
    }

    private static final class ShowTooltip implements EffectPredicateProvider {
        @Override
        public EffectPredicate init(final PredicateInitializer i) {
            return i.getBoolean(ShowTooltipReference.class).isTrue();
        }
    }

    private static final class ShowTooltipAndUseFormatter implements EffectPredicateProvider {
        @Override
        public EffectPredicate init(final PredicateInitializer i) {
            return i.getPredicate(ShowTooltip.class).and(i.getBoolean(UseFormatterForTooltipReference.class).isTrue());
        }
    }

    /**
     * Abstract persistor for tooltip parameters.
     */
    public abstract static class AbstractTooltipPersistor implements NodeParametersPersistor<TooltipParameters> {

        private final String m_typeConfigKey;

        private final String m_valueConfigKey;

        private final String m_baseConfigKey;

        /**
         * @param configKeySuffix the suffix for the value_ and key_ config keys
         * @param baseConfigKey the config key under which all tooltip settings are stored
         */
        protected AbstractTooltipPersistor(final int configKeySuffix, final String baseConfigKey) {
            m_typeConfigKey = CFG_TOOLTIP_TYPE_PREFIX + configKeySuffix;
            m_valueConfigKey = CFG_TOOLTIP_VALUE_PREFIX + configKeySuffix;
            m_baseConfigKey = baseConfigKey;
        }

        @Override
        public TooltipParameters load(final NodeSettingsRO settings) throws InvalidSettingsException {
            final var tooltipSettings = settings.getNodeSettings(m_baseConfigKey);
            final var numSettings = tooltipSettings.getInt(NUM_SETTINGS);
            if (numSettings == 0) {
                return new TooltipParameters(false, false, new NumberFormatParameters());
            }
            final var type = tooltipSettings.getString(m_typeConfigKey);
            if (type.equals(TOOLTIP_TYPE_BOOLEAN)) {
                final var showTooltip = tooltipSettings.getBoolean(m_valueConfigKey);
                return new TooltipParameters(showTooltip, false, new NumberFormatParameters());
            }
            final var tooltipFormatSettings = tooltipSettings.getNodeSettings(m_valueConfigKey);
            return new TooltipParameters(true, true,
                NumberFormatParameters.loadNumberFormatParameters(tooltipFormatSettings));
        }

        @Override
        public void save(final TooltipParameters param, final NodeSettingsWO settings) {
            final var tooltipSettings = settings.addNodeSettings(m_baseConfigKey);
            if (!param.m_showTooltip) {
                tooltipSettings.addInt(NUM_SETTINGS, 0);
                return;
            }
            tooltipSettings.addInt(NUM_SETTINGS, 1);
            if (!param.m_useFormatter) {
                tooltipSettings.addString(m_typeConfigKey, TOOLTIP_TYPE_BOOLEAN);
                tooltipSettings.addBoolean(m_valueConfigKey, param.m_showTooltip);
            } else {
                tooltipSettings.addString(m_typeConfigKey, TOOLTIP_TYPE_FORMAT);
                final var tooltipFormatSettings = tooltipSettings.addNodeSettings(m_valueConfigKey);
                NumberFormatParameters.saveNumberFormatParameters(param.m_numberFormatParameters,
                    tooltipFormatSettings);
            }
        }

        @Override
        public String[][] getConfigPaths() {
            final var tooltipParamPaths =
                new String[][]{{m_baseConfigKey, NUM_SETTINGS}, {m_baseConfigKey, m_typeConfigKey}};
            final var baseValuePath = new String[]{m_baseConfigKey, m_valueConfigKey};
            final var numberFormatPaths = Arrays //
                .stream(NumberFormatParameters.getConfigPaths()) //
                .map(numberFormatPath -> Stream //
                    .of(baseValuePath, numberFormatPath) //
                    .flatMap(Arrays::stream) //
                    .toArray(String[]::new));
            return Stream.concat(Stream.of(tooltipParamPaths), numberFormatPaths).toArray(String[][]::new);
        }

    }
}
