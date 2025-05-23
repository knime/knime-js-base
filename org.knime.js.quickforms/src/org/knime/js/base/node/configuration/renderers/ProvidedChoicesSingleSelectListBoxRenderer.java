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

import java.util.List;
import java.util.Optional;

import org.knime.core.webui.node.dialog.defaultdialog.jsonforms.renderers.SingleSelectListBoxRendererSpec;
import org.knime.core.webui.node.dialog.defaultdialog.widget.choices.StringChoice;
import org.knime.core.webui.node.dialog.defaultdialog.widget.updates.StateProvider;
import org.knime.js.base.node.configuration.selection.single.SingleSelectionDialogNodeRepresentation;

/**
 * A single select list box renderer for single selection configurations, e.g.,
 * {@link SingleSelectionDialogNodeRepresentation}.
 *
 * @author Robin Gerling
 */
public class ProvidedChoicesSingleSelectListBoxRenderer extends AbstractProvidedChoicesRenderer
    implements SingleSelectListBoxRendererSpec {

    private final boolean m_hasSizeLimit;

    private final int m_sizeLimit;

    /**
     * Creates a new single select list box renderer with provied choices.
     *
     * @param title of the renderer
     * @param possibleValuesProvider the provider for the possible values to choose from
     * @param hasSizeLimit whether the component should limit its size
     * @param sizeLimit the size limit of possible values to display simultaneously
     */
    public ProvidedChoicesSingleSelectListBoxRenderer(final String title,
        final StateProvider<List<StringChoice>> possibleValuesProvider, final boolean hasSizeLimit,
        final int sizeLimit) {
        super(title, possibleValuesProvider);
        m_hasSizeLimit = hasSizeLimit;
        m_sizeLimit = sizeLimit;
    }

    @Override
    public Optional<SingleSelectListBoxRendererOptions> getOptions() {
        return Optional.of(new SingleSelectListBoxRendererOptions() {
            @Override
            public Optional<Integer> getSize() {
                return m_hasSizeLimit ? Optional.of(m_sizeLimit) : Optional.empty();
            }
        });
    }

}
