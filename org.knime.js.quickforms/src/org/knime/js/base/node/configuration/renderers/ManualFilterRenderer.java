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

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.knime.core.node.dialog.SubNodeDescriptionProvider;
import org.knime.core.webui.node.dialog.defaultdialog.jsonforms.renderers.ManualFilterRendererSpec;
import org.knime.core.webui.node.dialog.defaultdialog.widget.choices.StringChoice;

/**
 * Manual filter widget from node description provider with static choices.
 *
 * @author Paul Bärnreuther
 */
public final class ManualFilterRenderer extends AbstractRepresentationRenderer implements ManualFilterRendererSpec {

    private final boolean m_isLimitNumberOfVizOptions;

    private final int m_numberOfVizOptions;

    private final List<String> m_possibleValues;

    /**
     * Creates a new string filter widget renderer with the possible values.
     *
     * @param rep providing title and description
     * @param possibleValues the possible values of the dropdown
     * @param isLimitNumberOfVizOptions whether the number of options is limited
     * @param numberOfVizOptions the number of options to visualize
     */
    public ManualFilterRenderer(final SubNodeDescriptionProvider<?> rep, final String[] possibleValues,
        final boolean isLimitNumberOfVizOptions, final int numberOfVizOptions) {
        super(rep);
        m_possibleValues = Arrays.asList(possibleValues);
        m_isLimitNumberOfVizOptions = isLimitNumberOfVizOptions;
        m_numberOfVizOptions = numberOfVizOptions;

    }

    @Override
    public Optional<ManualFilterRendererOptions> getOptions() {
        return Optional.of(new ManualFilterRendererOptions() {

            @Override
            public Optional<StringChoice[]> getPossibleValues() {
                return Optional.of(m_possibleValues.stream().map(StringChoice::fromId).toArray(StringChoice[]::new));
            }

            @Override
            public Optional<Integer> getTwinlistSize() {
                return Optional.of(m_numberOfVizOptions).filter(size -> m_isLimitNumberOfVizOptions);
            }

        });
    }

}
