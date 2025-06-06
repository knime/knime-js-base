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

import org.knime.core.webui.node.dialog.defaultdialog.jsonforms.renderers.NumberRendererSpec;
import org.knime.core.webui.node.dialog.defaultdialog.widget.validation.NumberInputWidgetValidation;
import org.knime.core.webui.node.dialog.defaultdialog.widget.validation.NumberInputWidgetValidation.MaxValidation;
import org.knime.core.webui.node.dialog.defaultdialog.widget.validation.NumberInputWidgetValidation.MinValidation;
import org.knime.js.base.node.configuration.input.dbl.DoubleDialogNodeRepresentation;

/**
 * A non-localized number renderer for {@link DoubleDialogNodeRepresentation}s.
 *
 * @author Paul Bärnreuther
 */
public class NumberRenderer extends AbstractRepresentationRenderer implements NumberRendererSpec {

    private final DoubleDialogNodeRepresentation m_doubleDialogRep;

    /**
     * Creates a new number renderer.
     *
     * @param doubleDialogRep the representation of the node
     */
    public NumberRenderer(final DoubleDialogNodeRepresentation doubleDialogRep) {
        super(doubleDialogRep);
        this.m_doubleDialogRep = doubleDialogRep;
    }

    @Override
    public Optional<NumberRendererOptions> getOptions() {
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
                    public Optional<MinValidation> getMin() {
                        return getMinValidation();
                    }

                    @Override
                    public Optional<MaxValidation> getMax() {
                        return getMaxValidation();
                    }
                });
            }

        });

    }

    Optional<MinValidation> getMinValidation() {
        if (m_doubleDialogRep.isUseMin()) {
            return Optional.of(new NumberInputWidgetValidation.MinValidation() {

                @Override
                protected double getMin() {
                    return m_doubleDialogRep.getMin();
                }

            });
        } else {
            return Optional.empty();
        }
    }

    Optional<MaxValidation> getMaxValidation() {
        if (m_doubleDialogRep.isUseMax()) {
            return Optional.of(new NumberInputWidgetValidation.MaxValidation() {

                @Override
                protected double getMax() {
                    return m_doubleDialogRep.getMax();
                }

            });
        } else {
            return Optional.empty();
        }
    }

}
