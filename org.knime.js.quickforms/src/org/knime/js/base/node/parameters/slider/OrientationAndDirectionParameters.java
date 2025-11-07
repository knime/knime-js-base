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
import java.util.function.Function;
import java.util.function.Supplier;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.webui.node.dialog.defaultdialog.util.updates.StateComputationFailureException;
import org.knime.js.core.settings.slider.SliderSettings;
import org.knime.js.core.settings.slider.SliderSettings.Direction;
import org.knime.js.core.settings.slider.SliderSettings.Orientation;
import org.knime.node.parameters.NodeParameters;
import org.knime.node.parameters.NodeParametersInput;
import org.knime.node.parameters.Widget;
import org.knime.node.parameters.migration.LoadDefaultsForAbsentFields;
import org.knime.node.parameters.persistence.NodeParametersPersistor;
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
 * The orientation and direction settings for sliders using ({@link SliderSettings}). As it is not possible to change
 * labels of another setting based on the state of another setting, the direction setting was split in two settings
 * which are hidden based on the Orientation setting and update each other's values.
 *
 * @author Robin Gerling, KNIME GmbH, Konstanz
 */
@SuppressWarnings("restriction")
@LoadDefaultsForAbsentFields
public final class OrientationAndDirectionParameters implements NodeParameters {

    /** Default constructor. */
    public OrientationAndDirectionParameters() {
    }

    OrientationAndDirectionParameters(final Orientation orientation, final Direction direction) {
        m_orientation = OrientationWebUI.fromOrientation(orientation);
        m_horizontalDirection = HorizontalDirection.fromDirection(direction);
        m_verticalDirection = VerticalDirection.fromDirection(direction);
    }

    @Widget(title = "Orientation", description = "Choose if the slider is drawn horizontally or vertically.")
    @ValueReference(OrientationReference.class)
    @ValueSwitchWidget
    OrientationWebUI m_orientation = OrientationWebUI.HORIZONTAL;

    @Widget(title = "Horizontal direction",
        description = "Choose whether the slider increases from left to right or from right to left.")
    @Effect(predicate = IsHorizontalOrientation.class, type = EffectType.SHOW)
    @ValueSwitchWidget
    @ValueReference(HorizontalDirectionReference.class)
    @ValueProvider(HorizontalDirectionValueProvider.class)
    HorizontalDirection m_horizontalDirection = HorizontalDirection.LEFT_TO_RIGHT;

    @Widget(title = "Vertical direction",
        description = "Choose whether the slider increases from top to bottom or from bottom to top.")
    @Effect(predicate = IsHorizontalOrientation.class, type = EffectType.HIDE)
    @ValueSwitchWidget
    @ValueReference(VerticalDirectionReference.class)
    @ValueProvider(VerticalDirectionValueProvider.class)
    VerticalDirection m_verticalDirection = VerticalDirection.TOP_TO_BOTTOM;

    private static <E extends Enum<E>, V> E findByValue(final E[] values, final Function<E, V> getter, final V value) {
        return Arrays.stream(values) //
            .filter(e -> getter.apply(e).equals(value)) //
            .findFirst() //
            .orElseThrow(() -> new IllegalArgumentException("Unknown value: " + value));
    }

    /**
     * We cannot use the {@link SliderSettings.Orientation} directly as enum here as the serialization for the selected
     * value vs the possible values differs.
     */
    enum OrientationWebUI {
            @Label("Horizontal")
            HORIZONTAL(Orientation.HORIZONTAL), //
            @Label("Vertical")
            VERTICAL(Orientation.VERTICAL);

        private final Orientation m_orientation;

        OrientationWebUI(final Orientation orientation) {
            m_orientation = orientation;
        }

        private static OrientationWebUI fromOrientation(final Orientation o) {
            return findByValue(values(), webUI -> webUI.m_orientation, o);
        }

        private String toValue() {
            return m_orientation.toValue();
        }
    }

    enum HorizontalDirection {
            @Label("Left to right")
            LEFT_TO_RIGHT(Direction.LTR), //
            @Label("Right to left")
            RIGHT_TO_LEFT(Direction.RTL);

        private final Direction m_direction;

        HorizontalDirection(final Direction direction) {
            m_direction = direction;
        }

        private static HorizontalDirection fromDirection(final Direction dir) {
            return findByValue(values(), d -> d.m_direction, dir);
        }

        private Direction toDirection() {
            return m_direction;
        }
    }

    enum VerticalDirection {
            @Label("Top to bottom")
            TOP_TO_BOTTOM(Direction.LTR), //
            @Label("Bottom to top")
            BOTTOM_TO_TOP(Direction.RTL);

        private final Direction m_direction;

        VerticalDirection(final Direction direction) {
            m_direction = direction;
        }

        private static VerticalDirection fromDirection(final Direction dir) {
            return findByValue(values(), d -> d.m_direction, dir);
        }

        private Direction toDirection() {
            return m_direction;
        }
    }

    private static final class OrientationReference implements ParameterReference<OrientationWebUI> {
    }

    private static final class HorizontalDirectionReference implements ParameterReference<HorizontalDirection> {
    }

    private static final class VerticalDirectionReference implements ParameterReference<VerticalDirection> {
    }

    private static final class IsHorizontalOrientation implements EffectPredicateProvider {
        @Override
        public EffectPredicate init(final PredicateInitializer i) {
            return i.getEnum(OrientationReference.class).isOneOf(OrientationWebUI.HORIZONTAL);
        }
    }

    private static final class HorizontalDirectionValueProvider implements StateProvider<HorizontalDirection> {

        private Supplier<VerticalDirection> m_verticalDirectionSupplier;

        private Supplier<HorizontalDirection> m_horizontalDirectionSupplier;

        @Override
        public void init(final StateProviderInitializer initializer) {
            m_verticalDirectionSupplier = initializer.computeFromValueSupplier(VerticalDirectionReference.class);
            m_horizontalDirectionSupplier = initializer.getValueSupplier(HorizontalDirectionReference.class);
        }

        @Override
        public HorizontalDirection computeState(final NodeParametersInput parametersInput)
            throws StateComputationFailureException {
            final var newDirection = m_verticalDirectionSupplier.get().toDirection();
            final var currentDirection = m_horizontalDirectionSupplier.get().toDirection();
            if (newDirection == currentDirection) {
                throw new StateComputationFailureException();
            }
            return HorizontalDirection.fromDirection(newDirection);
        }

    }

    private static final class VerticalDirectionValueProvider implements StateProvider<VerticalDirection> {

        private Supplier<HorizontalDirection> m_horizontalDirectionSupplier;

        private Supplier<VerticalDirection> m_verticalDirectionSupplier;

        @Override
        public void init(final StateProviderInitializer initializer) {
            m_horizontalDirectionSupplier = initializer.computeFromValueSupplier(HorizontalDirectionReference.class);
            m_verticalDirectionSupplier = initializer.getValueSupplier(VerticalDirectionReference.class);
        }

        @Override
        public VerticalDirection computeState(final NodeParametersInput parametersInput)
            throws StateComputationFailureException {
            final var newDirection = m_horizontalDirectionSupplier.get().toDirection();
            final var currentDirection = m_verticalDirectionSupplier.get().toDirection();
            if (newDirection == currentDirection) {
                throw new StateComputationFailureException();
            }
            return VerticalDirection.fromDirection(newDirection);
        }

    }

    /** Persistor for {@link OrientationAndDirectionParameters}. */
    public static final class OrientationAndDirectionPersistor
        implements NodeParametersPersistor<OrientationAndDirectionParameters> {

        private static final String CFG_ORIENTATION = "orientation";

        private static final String CFG_DIRECTION = "direction";

        @Override
        public OrientationAndDirectionParameters load(final NodeSettingsRO settings) throws InvalidSettingsException {
            final var orientation = Orientation.forValue(settings.getString(CFG_ORIENTATION));
            final var direction = Direction.forValue(settings.getString(CFG_DIRECTION));
            return new OrientationAndDirectionParameters(orientation, direction);
        }

        @Override
        public void save(final OrientationAndDirectionParameters param, final NodeSettingsWO settings) {
            settings.addString(CFG_ORIENTATION, param.m_orientation.toValue());
            final var verticalDirection = param.m_verticalDirection.toDirection();
            final var horizontalDirection = param.m_horizontalDirection.toDirection();
            if (horizontalDirection != verticalDirection) {
                // this should never happen as the value providers ensure that both directions are in sync
                throw new IllegalStateException(
                    String.format("Horizontal direction (%s) and vertical direction (%s) differ.", horizontalDirection,
                        verticalDirection));
            }
            settings.addString(CFG_DIRECTION, horizontalDirection.toValue());
        }

        @Override
        public String[][] getConfigPaths() {
            return new String[][]{{CFG_ORIENTATION}, {CFG_DIRECTION}};
        }

    }
}
