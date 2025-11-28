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
 *   10 Nov 2025 (robin): created
 */
package org.knime.js.base.node.widget.input.slider;

import java.util.Arrays;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.knime.core.data.DataCell;
import org.knime.core.data.DoubleValue;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.webui.node.dialog.defaultdialog.internal.widget.PersistWithin.PersistEmbedded;
import org.knime.core.webui.node.dialog.defaultdialog.util.updates.StateComputationFailureException;
import org.knime.core.webui.node.dialog.defaultdialog.widget.Modification;
import org.knime.js.base.node.parameters.slider.OrientationAndDirectionParameters;
import org.knime.js.base.node.parameters.slider.SliderNodeParametersUtil.AbstractCustomMaxValueProvider;
import org.knime.js.base.node.parameters.slider.SliderNodeParametersUtil.AbstractCustomMinValueProvider;
import org.knime.js.base.node.parameters.slider.SliderNodeParametersUtil.AbstractLowerUpperBoundStateProvider;
import org.knime.js.base.node.parameters.slider.SliderNodeParametersUtil.SliderBehaviourSection;
import org.knime.js.base.node.parameters.slider.SliderNodeParametersUtil.SliderTooltipSection;
import org.knime.js.base.node.parameters.slider.SliderNodeParametersUtil.StringOrEnumDomainColumnProvider;
import org.knime.js.base.node.parameters.slider.SliderTicksSettingsNodeParameters;
import org.knime.js.base.node.parameters.slider.SliderWidgetNodeParametersUtil.DoubleToArrayPersistor;
import org.knime.js.base.node.parameters.slider.SliderWidgetNodeParametersUtil.RangeParameters;
import org.knime.js.base.node.parameters.slider.SliderWidgetNodeParametersUtil.RangeParameters.RangeParametersProviderModification;
import org.knime.js.base.node.parameters.slider.SliderWidgetNodeParametersUtil.RoundedDoubleValueProvider;
import org.knime.js.base.node.parameters.slider.SliderWidgetNodeParametersUtil.TrueValueProvider;
import org.knime.js.base.node.parameters.slider.StepSizeParameter;
import org.knime.js.base.node.parameters.slider.TooltipParameters;
import org.knime.js.base.node.widget.input.slider.SliderWidgetNodeParameters.DefaultValueMirrorProvider;
import org.knime.node.parameters.NodeParameters;
import org.knime.node.parameters.NodeParametersInput;
import org.knime.node.parameters.Widget;
import org.knime.node.parameters.layout.Layout;
import org.knime.node.parameters.migration.LoadDefaultsForAbsentFields;
import org.knime.node.parameters.persistence.NodeParametersPersistor;
import org.knime.node.parameters.persistence.Persist;
import org.knime.node.parameters.persistence.Persistor;
import org.knime.node.parameters.updates.ParameterReference;
import org.knime.node.parameters.updates.StateProvider;
import org.knime.node.parameters.updates.ValueProvider;
import org.knime.node.parameters.updates.ValueReference;

/**
 * This class represents the slider settings of the slider widget.
 *
 * @author Robin Gerling, KNIME GmbH, Konstanz
 */
@SuppressWarnings("restriction")
@LoadDefaultsForAbsentFields
public final class SliderWidgetSliderSettingsNodeParameters implements NodeParameters {

    @ValueProvider(TrueValueProvider.class)
    @Persist(configKey = SliderInputWidgetConfig.CFG_SLIDER_EXISTS)
    boolean m_sliderExists = true;

    @Layout(SliderBehaviourSection.class)
    @Persistor(ConnectParameters.ConnectPersistor.class)
    @ValueProvider(ConnectParameters.ConnectValueProvider.class)
    @ValueReference(ConnectValueReference.class)
    ConnectParameters m_connect;

    @ValueProvider(DefaultValueMirrorProvider.class)
    @Persistor(StartPersistor.class)
    double m_start;

    @Modification(AddRangeValueProviders.class)
    RangeParameters m_range = new RangeParameters();

    @PersistEmbedded
    @Layout(SliderBehaviourSection.class)
    StepSizeParameter m_stepSizeParameter = new StepSizeParameter();

    @Layout(SliderBehaviourSection.class)
    @PersistEmbedded
    OrientationAndDirectionParameters m_orientationAndDirection = new OrientationAndDirectionParameters();

    @Layout(SliderTooltipSection.class)
    @Persistor(TooltipPersistor.class)
    TooltipParameters m_tooltips = new TooltipParameters();

    boolean[] m_fix;

    boolean[] m_snap;

    double[] m_margin;

    double[] m_limit;

    boolean[] m_animate;

    int[] m_animationDuration;

    String m_behaviour;

    SliderTicksSettingsNodeParameters m_pips = new SliderTicksSettingsNodeParameters();

    private static final class ConnectValueReference implements ParameterReference<ConnectParameters> {
    }

    private static final class StartPersistor extends DoubleToArrayPersistor {
        StartPersistor() {
            super("start");
        }
    }

    private static final class ConnectParameters implements NodeParameters {

        @SuppressWarnings("unused")
        ConnectParameters() {
        }

        ConnectParameters(final boolean lower, final boolean upper) {
            m_lower = lower;
            m_upper = upper;
        }

        @Widget(title = "Connect lower", description = "Display a colored bar on the lower end of the slider.")
        boolean m_lower;

        @Widget(title = "Connect upper", description = "Display a colored bar on the upper end of the slider.")
        boolean m_upper;

        private static final class ConnectPersistor implements NodeParametersPersistor<ConnectParameters> {

            private static final String CFG_CONNECT = "connect";

            @Override
            public ConnectParameters load(final NodeSettingsRO settings) throws InvalidSettingsException {
                final var connect = settings.getBooleanArray(CFG_CONNECT);
                return new ConnectParameters(connect[0], connect[1]);
            }

            @Override
            public void save(final ConnectParameters param, final NodeSettingsWO settings) {
                /** Due to the default being set by a value provider, param will be null here in snapshot tests. **/
                settings.addBooleanArray(CFG_CONNECT,
                    param == null ? new boolean[]{false, false} : new boolean[]{param.m_lower, param.m_upper});
            }

            @Override
            public String[][] getConfigPaths() {
                return new String[][]{{CFG_CONNECT}};
            }

        }

        /**
         * Per default, one should not initialize {@link m_connect} with {@code null}, but using the default
         * constructor. But, using the default constructor and dragging in a new slider widget would not result in a
         * dirty dialog which is needed, because the model does not specify all necessary settings for the slider to
         * work properly without an initial apply. As {@link m_connect} is only null when freshly dragged in, the dialog
         * will only be dirty in this case.
         */
        private static final class ConnectValueProvider implements StateProvider<ConnectParameters> {

            private Supplier<ConnectParameters> m_connectSupplier;

            @Override
            public void init(final StateProviderInitializer initializer) {
                initializer.computeAfterOpenDialog();
                m_connectSupplier = initializer.getValueSupplier(ConnectValueReference.class);
            }

            @Override
            public ConnectParameters computeState(final NodeParametersInput parametersInput)
                throws StateComputationFailureException {
                final var connect = m_connectSupplier.get();
                return connect == null ? new ConnectParameters(false, false) : connect;
            }

        }
    }

    private static final class TooltipPersistor implements NodeParametersPersistor<TooltipParameters> {

        private static final String CFG_TOOLTIPS = "tooltips";

        @Override
        public TooltipParameters load(final NodeSettingsRO settings) throws InvalidSettingsException {
            return TooltipParameters.loadTooltipParameters(settings.getNodeSettings(CFG_TOOLTIPS), 0);
        }

        @Override
        public void save(final TooltipParameters param, final NodeSettingsWO settings) {
            final var tooltipSettings = settings.addNodeSettings(CFG_TOOLTIPS);
            TooltipParameters.saveTooltipParameters(param, tooltipSettings, 0);
        }

        @Override
        public String[][] getConfigPaths() {
            return Arrays.stream(TooltipParameters.getTooltipConfigPaths(0)) //
                .map(row -> Stream.concat( //
                    Stream.of(CFG_TOOLTIPS), //
                    Arrays.stream(row) //
                ).toArray(String[]::new)) //
                .toArray(String[][]::new);
        }

    }

    static final class LowerUpperBoundStateProvider extends AbstractLowerUpperBoundStateProvider<Double> {
        LowerUpperBoundStateProvider() {
            super(DoubleValue.class, StringOrEnumDomainColumnProvider.class);
        }

        @Override
        public Double transformDataValueCompatibleCell(final DataCell cell) {
            return ((DoubleValue)cell).getDoubleValue();
        }
    }

    private static final class CustomMinValueProvider extends AbstractCustomMinValueProvider<Double> {
        CustomMinValueProvider() {
            super(LowerUpperBoundStateProvider.class);
        }
    }

    private static final class CustomMaxValueProvider extends AbstractCustomMaxValueProvider<Double> {
        CustomMaxValueProvider() {
            super(LowerUpperBoundStateProvider.class);
        }
    }

    private static final class DoubleCustomMinValueProvider extends RoundedDoubleValueProvider {
        DoubleCustomMinValueProvider() {
            super(CustomMinValueProvider.class);
        }
    }

    private static final class DoubleCustomMaxValueProvider extends RoundedDoubleValueProvider {
        public DoubleCustomMaxValueProvider() {
            super(CustomMaxValueProvider.class);
        }
    }

    private static final class AddRangeValueProviders extends RangeParametersProviderModification {

        @Override
        public Class<? extends RoundedDoubleValueProvider> getCustomMinProvider() {
            return DoubleCustomMinValueProvider.class;
        }

        @Override
        public Class<? extends RoundedDoubleValueProvider> getCustomMaxProvider() {
            return DoubleCustomMaxValueProvider.class;
        }

    }

}
