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
 *   30 Apr 2025 (Robin Gerling): created
 */
package org.knime.js.base.node.configuration.renderers;

import java.util.Optional;

import org.knime.core.webui.node.dialog.defaultdialog.jsonforms.DateTimeUtil;
import org.knime.core.webui.node.dialog.defaultdialog.jsonforms.renderers.ZonedDateTimeRendererSpec;
import org.knime.js.base.node.configuration.input.date.DateDialogNodeRepresentation;
import org.knime.node.parameters.widget.choices.StringChoice;
import org.knime.time.util.DateTimeType;

/**
 * A zoned date&time renderer for {@link DateDialogNodeRepresentation}s when the type is
 * {@link DateTimeType#LOCAL_DATE_TIME}.
 *
 * @author Robin Gerling
 */
public class ZonedDateTimeRenderer extends AbstractRepresentationRenderer implements ZonedDateTimeRendererSpec {

    private final DateDialogNodeRepresentation m_dateDialogRep;

    /**
     * Creates a new zoned date&time renderer for the given {@link DateDialogNodeRepresentation}.
     *
     * @param dateDialogRep the representation of the node
     */
    public ZonedDateTimeRenderer(final DateDialogNodeRepresentation dateDialogRep) {
        super(dateDialogRep);
        m_dateDialogRep = dateDialogRep;
    }

    @Override
    public Optional<ZonedDateTimeRendererOptions> getOptions() {
        return Optional.of(new ZonedDateTimeRendererOptions() {

            @Override
            public Optional<Boolean> getShowSeconds() {
                return Optional.of(m_dateDialogRep.isShowSeconds());
            }

            @Override
            public Optional<Boolean> getShowMilliseconds() {
                return Optional.of(m_dateDialogRep.isShowMilliseconds());
            }

            @Override
            public Optional<StringChoice[]> getPossibleValues() {
                return Optional.of(DateTimeUtil.getPossibleZoneIdValues());
            }

        });
    }

}
