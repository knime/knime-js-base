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
 *   24 Nov 2025 (Robin Gerling): created
 */
package org.knime.js.base.node.parameters.filechooser;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.js.base.node.base.input.filechooser.FileChooserDialogUtil;
import org.knime.js.base.node.base.input.filechooser.FileChooserNodeConfig;
import org.knime.js.base.node.base.input.filechooser.FileChooserNodeConfig.SelectionType;
import org.knime.js.base.node.base.input.filechooser.FileChooserNodeValue;
import org.knime.js.base.node.base.input.filechooser.FileChooserNodeValue.FileItem;
import org.knime.js.base.node.configuration.DialogNodeConfig;
import org.knime.js.base.node.configuration.input.filechooser.FileChooserDialogNodeNodeDialog;
import org.knime.js.base.node.configuration.input.filechooser.FileChooserDialogNodeValue;
import org.knime.js.base.node.parameters.ConfigurationAndWidgetNodeParametersUtil.FormFieldSection;
import org.knime.js.base.node.parameters.ConfigurationAndWidgetNodeParametersUtil.OutputSection;
import org.knime.node.parameters.NodeParameters;
import org.knime.node.parameters.Widget;
import org.knime.node.parameters.layout.After;
import org.knime.node.parameters.layout.Before;
import org.knime.node.parameters.layout.Layout;
import org.knime.node.parameters.layout.Section;
import org.knime.node.parameters.migration.LoadDefaultsForAbsentFields;
import org.knime.node.parameters.persistence.NodeParametersPersistor;
import org.knime.node.parameters.persistence.Persist;
import org.knime.node.parameters.persistence.Persistor;

/**
 * The node parameters for configuration and widget nodes which use the {@link FileChooserNodeValue}.
 *
 * @author Robin Gerling, KNIME GmbH, Konstanz, Germany
 */
@LoadDefaultsForAbsentFields
public final class FileChooserNodeParameters implements NodeParameters {

    /** The section for selection settings. */
    @Section(title = "Selection Settings")
    @After(FormFieldSection.class)
    @Before(OutputSection.class)
    public interface SelectionSettingsSection {
    }

    @Widget(title = "Valid file extensions",
        description = "A list of file extensions that is used as filter in the file browser "
            + "(not only the one in the \"Default File\" option but also in a remote file browser), "
            + "e.g. \".csv,.csv.gz\" will filter for files ending with \".csv\" or \".csv.gz\". "
            + "Leave empty to accept any file.")
    @Persistor(FileExtensionsPersistor.class)
    @Layout(FormFieldSection.class)
    String m_validFileExtensions = "";

    static final class DefaultValue implements NodeParameters {

        DefaultValue() {
        }

        DefaultValue(final String defaultPath, final SelectionType defaultType) {
            m_defaultPath = defaultPath;
            m_defaultType = defaultType;
        }

        @Widget(title = "Default file", description = "The default file to be output during configure and design time. "
            + "If the path to the default file is present remotely as well, the file will be preselected in the view.")
        @Layout(OutputSection.Top.class)
        String m_defaultPath = "";

        SelectionType m_defaultType = SelectionType.UNKNOWN;
    }

    @Persistor(DefaultValuePersistor.class)
    DefaultValue m_defaultValue = new DefaultValue();

    @Widget(title = "Select data files", description = "Allow selection of data files.")
    @Persist(configKey = FileChooserNodeConfig.CFG_SELECT_DATAFILES)
    @Layout(SelectionSettingsSection.class)
    boolean m_selectDataFiles = FileChooserNodeConfig.DEFAULT_SELECT_DATAFILES;

    @Widget(title = "Select workflows", description = "Allow selection of workflows.")
    @Persist(configKey = FileChooserNodeConfig.CFG_SELECT_WORKFLOWS)
    @Layout(SelectionSettingsSection.class)
    boolean m_selectWorkflows = FileChooserNodeConfig.DEFAULT_SELECT_WORKFLOWS;

    @Widget(title = "Select directories", description = "Allow selection of directories.")
    @Persist(configKey = FileChooserNodeConfig.CFG_SELECT_DIRECTORIES)
    @Layout(SelectionSettingsSection.class)
    boolean m_selectDirectories = FileChooserNodeConfig.DEFAULT_SELECT_DIRECTORIES;

    @Widget(title = "Output selected item type",
        description = "Enabling this option will append a second column to the output table "
            + "containing the type of the selected items. Also a second flow variable "
            + "containing the type of the first selected item is created. "
            + "Possible values are DATA, WORKFLOW and DIRECTORY.")
    @Persist(configKey = FileChooserNodeConfig.CFG_OUTPUT_TYPE)
    @Layout(SelectionSettingsSection.class)
    boolean m_outputType = FileChooserNodeConfig.DEFAULT_OUTPUT_TYPE;

    /** Neither the configuration nor the widget node use the following setting, why it is hidden. */
    @Persist(configKey = FileChooserNodeConfig.CFG_ERROR_MESSAGE)
    String m_errorMessageString = FileChooserNodeConfig.DEFAULT_ERROR_MESSAGE;

    private static final class DefaultValuePersistor implements NodeParametersPersistor<DefaultValue> {

        @Override
        public DefaultValue load(final NodeSettingsRO settings) throws InvalidSettingsException {
            final var fileChooserDialogNodeValue = new FileChooserDialogNodeValue();
            fileChooserDialogNodeValue
                .loadFromNodeSettingsInDialog(settings.getNodeSettings(DialogNodeConfig.CFG_DEFAULT_VALUE));
            final var items = fileChooserDialogNodeValue.getItems();
            if (items.length == 0) {
                return new DefaultValue();
            }
            final var item = items[0];
            return new DefaultValue(item.getPath(), item.getSelectionType());
        }

        @Override
        public void save(final DefaultValue defaultValue, final NodeSettingsWO settings) {
            final var defaultValueSettings = settings.addNodeSettings(DialogNodeConfig.CFG_DEFAULT_VALUE);
            final var fileChooserNodeValue = new FileChooserNodeValue();
            final var fileItem = new FileItem(defaultValue.m_defaultPath, defaultValue.m_defaultType);
            fileChooserNodeValue.setItems(new FileItem[]{fileItem});
            fileChooserNodeValue.saveToNodeSettings(defaultValueSettings);
        }

        @Override
        public String[][] getConfigPaths() {
            return new String[][]{
                {DialogNodeConfig.CFG_DEFAULT_VALUE, FileChooserNodeValue.CFG_ITEMS,
                    FileChooserNodeValue.CFG_FIRST_ITEM, FileChooserNodeValue.FileItem.CFG_PATH}, //
                {DialogNodeConfig.CFG_DEFAULT_VALUE, FileChooserNodeValue.CFG_ITEMS,
                    FileChooserNodeValue.CFG_FIRST_ITEM, FileChooserNodeValue.FileItem.CFG_TYPE}};
        }
    }

    private static final class FileExtensionsPersistor implements NodeParametersPersistor<String> {

        @Override
        public String load(final NodeSettingsRO settings) throws InvalidSettingsException {
            String[] fileTypes = settings.getStringArray(FileChooserNodeConfig.CFG_FILE_TYPES, new String[0]);
            return FileChooserDialogNodeNodeDialog.getValidFileExtensions(fileTypes);
        }

        @Override
        public void save(final String extensions, final NodeSettingsWO settings) {
            settings.addStringArray(FileChooserNodeConfig.CFG_FILE_TYPES,
                FileChooserDialogUtil.getFileTypes(extensions));
        }

        @Override
        public String[][] getConfigPaths() {
            return new String[][]{{FileChooserNodeConfig.CFG_FILE_TYPES}};
        }
    }
}
