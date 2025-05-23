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
 *   May 26, 2025 (Paul Bärnreuther): created
 */
package org.knime.js.base.node.configuration.renderers;

import java.util.List;
import java.util.Optional;

import org.knime.core.webui.node.dialog.defaultdialog.jsonforms.renderers.RadioButtonRendererSpec;
import org.knime.core.webui.node.dialog.defaultdialog.jsonforms.renderers.options.Alignment;
import org.knime.core.webui.node.dialog.defaultdialog.widget.choices.StringChoice;
import org.knime.core.webui.node.dialog.defaultdialog.widget.updates.StateProvider;

/**
 * Radio buttons with a title and provided possible values.
 *
 * @author Paul Bärnreuther
 */
public final class ProvidedChoicesRadioButtonRenderer extends AbstractProvidedChoicesRenderer
    implements RadioButtonRendererSpec {

    private final Alignment m_alignment;

    /**
     * Creates a new radio button renderer with the given title and possible values.
     *
     * @param title the title of the dropdown
     * @param possibleValues the possible values of the dropdown
     * @param alignment of the radio buttons
     */
    public ProvidedChoicesRadioButtonRenderer(final String title,
        final StateProvider<List<StringChoice>> possibleValues, final Alignment alignment) {
        super(title, possibleValues);
        m_alignment = alignment;
    }

    @Override
    public Optional<RadioButtonRendererOptions> getOptions() {
        return Optional.of(new RadioButtonRendererOptions() {

            @Override
            public Optional<Alignment> getRadioLayout() {
                return Optional.of(m_alignment);
            }
        });
    }

}
