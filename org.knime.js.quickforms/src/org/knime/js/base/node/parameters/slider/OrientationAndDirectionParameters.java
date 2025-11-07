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
import java.util.List;
import java.util.function.Supplier;

import org.knime.js.core.settings.slider.SliderSettings;
import org.knime.js.core.settings.slider.SliderSettings.Direction;
import org.knime.js.core.settings.slider.SliderSettings.Orientation;
import org.knime.node.parameters.NodeParameters;
import org.knime.node.parameters.NodeParametersInput;
import org.knime.node.parameters.Widget;
import org.knime.node.parameters.migration.LoadDefaultsForAbsentFields;
import org.knime.node.parameters.updates.ParameterReference;
import org.knime.node.parameters.updates.ValueReference;
import org.knime.node.parameters.widget.choices.ChoicesProvider;
import org.knime.node.parameters.widget.choices.EnumChoice;
import org.knime.node.parameters.widget.choices.EnumChoicesProvider;
import org.knime.node.parameters.widget.choices.ValueSwitchWidget;

/**
 * The orientation and direction settings for sliders using ({@link SliderSettings}).
 *
 * @author Robin Gerling, KNIME GmbH, Konstanz
 */
@LoadDefaultsForAbsentFields
public final class OrientationAndDirectionParameters implements NodeParameters {

    @Widget(title = "Orientation", description = "Choose if the slider is drawn horizontally or vertically.")
    @ValueReference(OrientationReference.class)
    @ValueSwitchWidget
    WebUIOrientation m_orientation = WebUIOrientation.horizontal;

    /**
     * Same as {@link Orientation} but without custom jackson annotations.
     */
    enum WebUIOrientation {
            horizontal, vertical; // NOSONAR we need lowercase enum constants for backward compatibility
    }

    private static final class OrientationReference implements ParameterReference<WebUIOrientation> {
    }

    @Widget(title = "Direction",
        description = "Choose if the slider is drawn left to right or right to left for horizontal orientation, "
            + "top to bottom or bottom to top for vertical orientation.")
    @ChoicesProvider(OrientationDependentLabelsProvider.class)
    @ValueSwitchWidget
    WebUIDirection m_direction = WebUIDirection.ltr;

    /**
     * Same as {@link Direction} but without custom jackson annotations.
     */
    enum WebUIDirection {
            ltr, rtl; // NOSONAR we need lowercase enum constants for backward compatibility
    }

    static final class OrientationDependentLabelsProvider implements EnumChoicesProvider<WebUIDirection> {

        private Supplier<WebUIOrientation> m_orientationSupplier;

        @Override
        public void init(final StateProviderInitializer initializer) {
            EnumChoicesProvider.super.init(initializer);
            m_orientationSupplier = initializer.computeFromValueSupplier(OrientationReference.class);
        }

        @Override
        public List<EnumChoice<WebUIDirection>> computeState(final NodeParametersInput context) {
            if (m_orientationSupplier.get() == WebUIOrientation.horizontal) {
                return Arrays.asList( //
                    new EnumChoice<>(WebUIDirection.ltr, "Left to right"), //
                    new EnumChoice<>(WebUIDirection.rtl, "Right to left"));
            }
            return Arrays.asList( //
                new EnumChoice<>(WebUIDirection.ltr, "Top to bottom"), //
                new EnumChoice<>(WebUIDirection.rtl, "Bottom to top"));
        }

    }

}
