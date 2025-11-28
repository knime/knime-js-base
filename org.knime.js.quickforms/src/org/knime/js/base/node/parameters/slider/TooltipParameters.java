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
import org.knime.core.webui.node.dialog.defaultdialog.widget.Modification;
import org.knime.core.webui.node.dialog.defaultdialog.widget.Modification.WidgetGroupModifier;
import org.knime.js.base.node.parameters.slider.NumberFormatParameters.RemoveElementsFromNodeDescription;
import org.knime.js.core.settings.slider.SliderSettings;
import org.knime.node.parameters.NodeParameters;
import org.knime.node.parameters.Widget;
import org.knime.node.parameters.updates.Effect;
import org.knime.node.parameters.updates.Effect.EffectType;
import org.knime.node.parameters.updates.EffectPredicate;
import org.knime.node.parameters.updates.EffectPredicateProvider;
import org.knime.node.parameters.updates.ParameterReference;
import org.knime.node.parameters.updates.ValueReference;
import org.knime.node.parameters.updates.util.BooleanReference;

/**
 * Tooltip related parameters for slider widgets (see {@link SliderSettings}).
 *
 * @author Robin Gerling, KNIME GmbH, Konstanz
 */
@SuppressWarnings("restriction")
public final class TooltipParameters implements NodeParameters {

    private static final String TOOLTIP_TYPE_BOOLEAN = "boolean";

    private static final String TOOLTIP_TYPE_FORMAT = "format";

    private static final String CFG_TOOLTIP_TYPE_PREFIX = "type_";

    private static final String CFG_TOOLTIP_VALUE_PREFIX = "value_";

    /** Configuration key for number of settings. */
    public static final String CFG_NUM_SETTINGS = "numSettings";

    /** Default constructor. */
    public TooltipParameters() {
    }

    TooltipParameters(final boolean showTooltip, final boolean useFormatter,
        final NumberFormatParameters numberFormatParameters) {
        m_showTooltip = showTooltip;
        m_useFormatter = useFormatter;
        m_numberFormatParameters = numberFormatParameters;
    }

    /** Whether to show the tooltip for the current value. Public to determine how to persist from the outside. */
    @Widget(title = "Show tooltip",
        description = "Check, if the currently selected value on the slider is supposed to be shown in a tooltip.")
    @ValueReference(ShowTooltipReference.class)
    @Modification.WidgetReference(ShowTooltipReference.class)
    public boolean m_showTooltip;

    @Widget(title = "Use formatter for tooltip", description = "Enable or disable formatting options for the tooltip.")
    @ValueReference(UseFormatterForTooltipReference.class)
    @Modification.WidgetReference(UseFormatterForTooltipReference.class)
    @Effect(predicate = ShowTooltipReference.class, type = EffectType.SHOW)
    boolean m_useFormatter;

    @Effect(predicate = ShowTooltipAndUseFormatter.class, type = EffectType.SHOW)
    @Modification.WidgetReference(NumberFormatParametersReference.class)
    NumberFormatParameters m_numberFormatParameters = new NumberFormatParameters();

    private static final class ShowTooltipReference implements BooleanReference, Modification.Reference {
    }

    private static final class UseFormatterForTooltipReference
        implements ParameterReference<Boolean>, Modification.Reference {
    }

    private static final class NumberFormatParametersReference implements Modification.Reference {
    }

    /** Predicate to show number format parameters when both show tooltip and use formatter are enabled. */
    public static final class ShowTooltipAndUseFormatter implements EffectPredicateProvider {
        @Override
        public EffectPredicate init(final PredicateInitializer i) {
            return i.getPredicate(ShowTooltipReference.class)
                .and(i.getBoolean(UseFormatterForTooltipReference.class).isTrue());
        }
    }

    /**
     * Modification to set titles, references, and effects for tooltip related parameters.
     */
    public abstract static class TooltipParametersModification implements Modification.Modifier {
        @Override
        public void modify(final WidgetGroupModifier group) {
            group.find(ShowTooltipReference.class).modifyAnnotation(Widget.class)
                .withProperty("title", getShowTooltipTitle()).modify();
            group.find(ShowTooltipReference.class).modifyAnnotation(ValueReference.class)
                .withValue(getShowTooltipReference()).modify();

            group.find(UseFormatterForTooltipReference.class).modifyAnnotation(Widget.class)
                .withProperty("title", getUseFormatterTitle()).modify();
            group.find(UseFormatterForTooltipReference.class).modifyAnnotation(ValueReference.class)
                .withValue(getUseFormatterReference()).modify();
            group.find(UseFormatterForTooltipReference.class).modifyAnnotation(Effect.class)
                .withProperty("predicate", getShowTooltipPredicate()).modify();
            group.find(NumberFormatParametersReference.class).modifyAnnotation(Effect.class)
                .withProperty("predicate", getShowTooltipAndUseFormatterPredicate()).modify();
            if (removeNumberFormatParametersFromDescription()) {
                group.find(NumberFormatParametersReference.class).addAnnotation(Modification.class)
                    .withValue(new Class[]{RemoveElementsFromNodeDescription.class}).modify();
            }
        }

        /** @return the title for the show tooltip parameter */
        public abstract String getShowTooltipTitle();

        /** @return the title for the use formatter parameter */
        public abstract String getUseFormatterTitle();

        /** @return the parameter reference for the show tooltip parameter */
        public abstract Class<? extends ParameterReference<Boolean>> getShowTooltipReference();

        /** @return the parameter reference for the use formatter parameter */
        public abstract Class<? extends ParameterReference<Boolean>> getUseFormatterReference();

        /** @return the effect predicate provider for showing the use formatter parameter */
        public abstract Class<? extends EffectPredicateProvider> getShowTooltipPredicate();

        /** @return the effect predicate provider for showing the number format parameters */
        public abstract Class<? extends EffectPredicateProvider> getShowTooltipAndUseFormatterPredicate();

        /** @return whether to remove number format parameters from the description */
        public boolean removeNumberFormatParametersFromDescription() {
            return false;
        }
    }

    /**
     * Utility method to load tooltip parameters.
     *
     * @param settings the settings layer in which the base tooltip parameters are stored
     * @param configKeySuffix the suffix for the base tooltip parameter config keys (i.e. value_, key_, numSettings)
     * @return the loaded tooltip parameters
     * @throws InvalidSettingsException if loading fails
     */
    public static TooltipParameters loadTooltipParameters(final NodeSettingsRO settings, final int configKeySuffix)
        throws InvalidSettingsException {

        final var numSettings = settings.getInt(CFG_NUM_SETTINGS);
        if (numSettings == 0) {
            return new TooltipParameters(false, false, new NumberFormatParameters());
        }
        final var typeConfigKey = CFG_TOOLTIP_TYPE_PREFIX + configKeySuffix;
        final var valueConfigKey = CFG_TOOLTIP_VALUE_PREFIX + configKeySuffix;
        final var type = settings.getString(typeConfigKey);
        if (type.equals(TOOLTIP_TYPE_BOOLEAN)) {
            final var showTooltip = settings.getBoolean(valueConfigKey);
            return new TooltipParameters(showTooltip, false, new NumberFormatParameters());
        }
        final var tooltipFormatSettings = settings.getNodeSettings(valueConfigKey);
        return new TooltipParameters(true, true,
            NumberFormatParameters.loadNumberFormatParameters(tooltipFormatSettings));
    }

    /**
     * Utility method to save tooltip parameters. It handles 0 and 1 for numSettings only. If more settings are needed,
     * the setting must be overriden in the calling method (config key: {@link #CFG_NUM_SETTINGS}).
     *
     * @param param the tooltip parameters to save
     * @param settings the settings layer in which to save the base tooltip parameters
     * @param configKeySuffix the suffix for the base tooltip parameter config keys (i.e. value_, key_, numSettings)
     */
    public static void saveTooltipParameters(final TooltipParameters param, final NodeSettingsWO settings,
        final int configKeySuffix) {
        if (!param.m_showTooltip) {
            settings.addInt(CFG_NUM_SETTINGS, 0);
            return;
        }
        final var typeConfigKey = CFG_TOOLTIP_TYPE_PREFIX + configKeySuffix;
        final var valueConfigKey = CFG_TOOLTIP_VALUE_PREFIX + configKeySuffix;
        settings.addInt(CFG_NUM_SETTINGS, 1);
        if (!param.m_useFormatter) {
            settings.addString(typeConfigKey, TOOLTIP_TYPE_BOOLEAN);
            settings.addBoolean(valueConfigKey, param.m_showTooltip);
        } else {
            settings.addString(typeConfigKey, TOOLTIP_TYPE_FORMAT);
            final var tooltipFormatSettings = settings.addNodeSettings(valueConfigKey);
            NumberFormatParameters.saveNumberFormatParameters(param.m_numberFormatParameters, tooltipFormatSettings);
        }
    }

    /**
     * Utility method to get the config paths for tooltip parameters.
     *
     * @param configKeySuffix the suffix for the base tooltip parameter config keys (i.e. value_, key_, numSettings)
     * @return the config paths for the tooltip parameters
     */
    public static String[][] getTooltipConfigPaths(final int configKeySuffix) {
        final var typeConfigKey = CFG_TOOLTIP_TYPE_PREFIX + configKeySuffix;
        final var valueConfigKey = CFG_TOOLTIP_VALUE_PREFIX + configKeySuffix;

        final var tooltipParamPaths = new String[][]{{CFG_NUM_SETTINGS}, {typeConfigKey}};
        final var baseValuePath = new String[]{valueConfigKey};
        final var numberFormatPaths = Arrays //
            .stream(NumberFormatParameters.getConfigPaths()) //
            .map(numberFormatPath -> Stream //
                .of(baseValuePath, numberFormatPath) //
                .flatMap(Arrays::stream) //
                .toArray(String[]::new));
        return Stream.concat(Stream.of(tooltipParamPaths), numberFormatPaths).toArray(String[][]::new);
    }
}
