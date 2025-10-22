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
 *   3 Jun 2019 (albrecht): created
 */
package org.knime.js.base.node.configuration.input.filechooser;

import java.util.Map;
import java.util.Optional;

import org.knime.core.node.dialog.DialogNodePanel;
import org.knime.core.node.dialog.SubNodeDescriptionProvider;
import org.knime.core.webui.node.dialog.PersistSchema;
import org.knime.core.webui.node.dialog.WebDialogNodeRepresentation.DefaultWebDialogNodeRepresentation;
import org.knime.core.webui.node.dialog.defaultdialog.jsonforms.renderers.DialogElementRendererSpec;
import org.knime.core.webui.node.dialog.defaultdialog.jsonforms.renderers.TextRendererSpec;
import org.knime.js.base.node.base.input.filechooser.FileChooserNodeRepresentation;
import org.knime.js.base.node.base.input.filechooser.FileChooserNodeValue;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The dialog representation of the file chooser configuration node
 *
 * @author Christian Albrecht, KNIME GmbH, Konstanz, Germany
 */
public class FileChooserDialogNodeRepresentation extends FileChooserNodeRepresentation<FileChooserDialogNodeValue>
    implements SubNodeDescriptionProvider<FileChooserDialogNodeValue>,
    DefaultWebDialogNodeRepresentation<FileChooserDialogNodeValue> {

    @JsonCreator
    private FileChooserDialogNodeRepresentation(@JsonProperty("label") final String label,
        @JsonProperty("description") final String description, @JsonProperty("required") final boolean required,
        @JsonProperty("defaultValue") final FileChooserDialogNodeValue defaultValue,
        @JsonProperty("currentValue") final FileChooserDialogNodeValue currentValue,
        @JsonProperty("selectWorkflows") final boolean selectWorkflows,
        @JsonProperty("selectDirectories") final boolean selectDirectories,
        @JsonProperty("selectDataFiles") final boolean selectDataFiles,
        @JsonProperty("useDefaultMountId") final boolean useDefaultMountId,
        @JsonProperty("customMountId") final String customMountId, @JsonProperty("rootDir") final String rootDir,
        @JsonProperty("fileTypes") final String[] fileTypes,
        @JsonProperty("multipleSelection") final boolean multipleSelection,
        @JsonProperty("errorMessage") final String errorMessage, @JsonProperty("tree") final Object tree) {
        super(label, description, required, defaultValue, currentValue, selectWorkflows, selectDirectories,
            selectDataFiles, useDefaultMountId, customMountId, rootDir, fileTypes, multipleSelection, errorMessage,
            tree);
    }

    /**
     * @param currentValue The value currently used by the node
     * @param config The config of the node
     */
    public FileChooserDialogNodeRepresentation(final FileChooserDialogNodeValue currentValue,
        final FileChooserInputDialogNodeConfig config) {
        super(currentValue, config.getDefaultValue(), config.getFileChooserConfig(), config.getLabelConfig());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DialogNodePanel<FileChooserDialogNodeValue> createDialogPanel() {
        return new FileChooserConfigurationPanel(this);
    }

    @Override
    public DialogElementRendererSpec getWebUIDialogElementRendererSpec() {
        return new TextRenderer(getLabel(), getDescription()).at(FileChooserNodeValue.CFG_ITEMS,
            FileChooserDialogNodeValue.FIRST_ITEM);
    }

    static final class TextRenderer implements TextRendererSpec {

        private final String m_title;

        private final String m_description;

        TextRenderer(final String title, final String description) {
            m_title = title;
            m_description = description;
        }

        @Override
        public String getTitle() {
            return m_title;
        }

        @Override
        public Optional<String> getDescription() {
            return Optional.of(m_description);
        }

    }

    @Override
    public Optional<PersistSchema> getPersistSchema() {
        return Optional.of(new PersistSchema.PersistTreeSchema.PersistTreeSchemaRecord(
            Map.of(FileChooserNodeValue.CFG_ITEMS, new PersistSchema.PersistTreeSchema.PersistTreeSchemaRecord(
                Map.of(FileChooserDialogNodeValue.FIRST_ITEM, new PersistSchema.PersistLeafSchema() {
                    @Override
                    public Optional<String[][]> getConfigPaths() {
                        return Optional.of(new String[][]{ //
                            {FileChooserDialogNodeValue.FIRST_ITEM, FileChooserNodeValue.FileItem.CFG_PATH}, //
                            {FileChooserDialogNodeValue.FIRST_ITEM, FileChooserNodeValue.FileItem.CFG_TYPE}});
                    }
                })))));
    }

}
