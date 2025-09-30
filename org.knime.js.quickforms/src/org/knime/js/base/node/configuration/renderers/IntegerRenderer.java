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
 *   Apr 14, 2025 (Paul Bärnreuther): created
 */
package org.knime.js.base.node.configuration.renderers;

import java.util.Optional;
import java.util.stream.Stream;

import org.knime.core.node.dialog.SubNodeDescriptionProvider;
import org.knime.core.webui.node.dialog.defaultdialog.jsonforms.renderers.IntegerRendererSpec;
import org.knime.js.base.node.configuration.input.integer.IntegerDialogNodeRepresentation;
import org.knime.js.base.node.configuration.input.slider.IntegerSliderDialogNodeRepresentation;
import org.knime.node.parameters.widget.number.NumberInputWidgetValidation;
import org.knime.node.parameters.widget.number.NumberInputWidgetValidation.MaxValidation;
import org.knime.node.parameters.widget.number.NumberInputWidgetValidation.MinValidation;

/**
 * A non-localized number renderer for {@link IntegerDialogNodeRepresentation}s and
 * {@link IntegerSliderDialogNodeRepresentation}s.
 *
 * @author Paul Bärnreuther
 */
public class IntegerRenderer extends AbstractRepresentationRenderer implements IntegerRendererSpec {

    private final boolean m_useMin;

    private final int m_min;

    private final boolean m_useMax;

    private final int m_max;

    /**
     * Creates a new {@link IntegerRenderer}.
     *
     * @param intDialogRep the representation of the node
     * @param useMin whether to use a min validation
     * @param min the value of a min validation
     * @param useMax whether to use a max validation
     * @param max the value of a max validation
     */
    public IntegerRenderer(final SubNodeDescriptionProvider<?> intDialogRep, final boolean useMin, final int min,
        final boolean useMax, final int max) {
        super(intDialogRep);
        m_useMin = useMin;
        m_min = min;
        m_useMax = useMax;
        m_max = max;
    }

    @Override
    public Optional<NumberRendererOptions> getCustomOptions() {
        final var builtinValidations =
            Stream.of(getMinValidation(), getMaxValidation()).flatMap(Optional::stream).toList();
        if (builtinValidations.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(new NumberRendererOptions() {

            @Override
            public Optional<NumberRendererValidationOptions> getValidation() {
                return Optional.of(new NumberRendererValidationOptions() {

                    @Override
                    public Optional<MaxValidation> getMax() {
                        return getMaxValidation();
                    }

                    @Override
                    public Optional<MinValidation> getMin() {
                        return getMinValidation();
                    }

                });
            }

        });

    }

    @Override
    public TypeBounds getTypeBounds() {
        return TypeBounds.INTEGER;
    }

    private Optional<MinValidation> getMinValidation() {
        if (m_useMin) {
            return Optional.of(new NumberInputWidgetValidation.MinValidation() {

                @Override
                protected double getMin() {
                    return m_min;
                }

            });
        } else {
            return Optional.empty();
        }
    }

    private Optional<MaxValidation> getMaxValidation() {
        if (m_useMax) {
            return Optional.of(new NumberInputWidgetValidation.MaxValidation() {

                @Override
                protected double getMax() {
                    return m_max;
                }

            });
        } else {
            return Optional.empty();
        }
    }

}
