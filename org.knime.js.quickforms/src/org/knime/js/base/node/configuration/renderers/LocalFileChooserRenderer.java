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
 *   16 May 2025 (Robin Gerling): created
 */
package org.knime.js.base.node.configuration.renderers;

import java.util.Optional;

import org.knime.core.webui.node.dialog.defaultdialog.jsonforms.renderers.LocalFileChooserRendererSpec;
import org.knime.js.base.node.base.input.fileupload.FileUploadNodeUtil;
import org.knime.js.base.node.configuration.input.date.DateDialogNodeRepresentation;
import org.knime.js.base.node.configuration.input.fileupload.FileDialogNodeRepresentation;

/**
 * A local file chooser renderer for the {@link FileDialogNodeRepresentation}.
 *
 * @author Robin Gerling
 */
public final class LocalFileChooserRenderer extends AbstractRepresentationRenderer
    implements LocalFileChooserRendererSpec {

    private final FileDialogNodeRepresentation m_localFileChooserDialogRep;

    /**
     * Creates a new local file chooser renderer for the given {@link DateDialogNodeRepresentation}.
     *
     * @param localFileChooserDialogRep the representation of the node
     */
    public LocalFileChooserRenderer(final FileDialogNodeRepresentation localFileChooserDialogRep) {
        super(localFileChooserDialogRep);
        m_localFileChooserDialogRep = localFileChooserDialogRep;
    }

    @Override
    public Optional<LocalFileChooserRendererOptions> getOptions() {
        return Optional.of(new LocalFileChooserRendererOptions() {

            @Override
            public Optional<String[]> getFileExtensions() {
                final var fileTypes = m_localFileChooserDialogRep.getFileTypes();
                final var fileExtensions = FileUploadNodeUtil.extractExtensions(fileTypes);
                return fileExtensions.length == 0 ? Optional.empty() : Optional.of(fileExtensions);
            }

        });
    }

}
