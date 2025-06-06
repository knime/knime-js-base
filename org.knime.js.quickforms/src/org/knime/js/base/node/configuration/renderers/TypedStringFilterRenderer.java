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
 *   8 May 2025 (Robin Gerling): created
 */
package org.knime.js.base.node.configuration.renderers;

import java.util.Arrays;
import java.util.Optional;

import org.knime.core.data.DataColumnSpec;
import org.knime.core.node.dialog.SubNodeDescriptionProvider;
import org.knime.core.webui.node.dialog.defaultdialog.jsonforms.renderers.TypedStringFilterRendererSpec;
import org.knime.core.webui.node.dialog.defaultdialog.widget.choices.TypedStringChoice;
import org.knime.js.base.node.configuration.filter.column.ColumnFilterDialogNodeRepresentation;

/**
 * A column filter renderer for the {@link ColumnFilterDialogNodeRepresentation}.
 *
 * @author Robin Gerling
 */
public class TypedStringFilterRenderer extends AbstractRepresentationRenderer
    implements TypedStringFilterRendererSpec {

    private final DataColumnSpec[] m_possibleSpecs;

    private final boolean m_hasSizeLimit;

    private final int m_sizeLimit;

    /**
     * Creates a new column filter renderer from the given node representation and config.
     *
     * @param nodeRep the representation of the node
     * @param possibleSpecs the possible column specs to choose columns from
     * @param hasSizeLimit whether the component should limit its size
     * @param sizeLimit the size limit of possible values to display simultaneously
     */
    public TypedStringFilterRenderer(final SubNodeDescriptionProvider<?> nodeRep, final DataColumnSpec[] possibleSpecs,
        final boolean hasSizeLimit, final int sizeLimit) {
        super(nodeRep);
        m_possibleSpecs = possibleSpecs;
        m_hasSizeLimit = hasSizeLimit;
        m_sizeLimit = sizeLimit;
    }

    @Override
    public Optional<TypedStringFilterRendererOptions> getOptions() {
        return Optional.of(new TypedStringFilterRendererOptions() {
            @Override
            public Optional<TypedStringChoice[]> getPossibleValues() {
                return Optional.of(Arrays.stream(m_possibleSpecs).map(TypedStringChoice::fromColSpec)
                    .toArray(TypedStringChoice[]::new));
            }

            @Override
            public Optional<Integer> getTwinlistSize() {
                return m_hasSizeLimit ? Optional.of(m_sizeLimit) : Optional.empty();
            }

            @Override
            public Optional<String> getUnknownValuesText() {
                return Optional.of("Any unknown column");
            }

            @Override
            public Optional<String> getEmptyStateLabel() {
                return Optional.of("No columns in this list.");
            }
        });
    }

}
