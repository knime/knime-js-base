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
 *   AI Migration
 */
package org.knime.js.base.node.widget.input.filechooser;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.js.base.node.widget.util.WidgetNodeSettingsBase;
import org.knime.node.parameters.NodeParameters;
import org.knime.node.parameters.Widget;
import org.knime.node.parameters.layout.After;
import org.knime.node.parameters.layout.Layout;
import org.knime.node.parameters.layout.Section;
import org.knime.node.parameters.persistence.NodeParametersPersistor;
import org.knime.node.parameters.persistence.Persist;
import org.knime.node.parameters.persistence.Persistor;
import org.knime.node.parameters.widget.text.TextAreaWidget;
import org.knime.node.parameters.widget.text.TextInputWidget;

/**
 * Settings for the File Chooser Widget node.
 *
 * @author AI Migration
 */
@SuppressWarnings("restriction")
public final class FileChooserWidgetNodeSettings implements NodeParameters {

    @Section(title = "File Chooser Configuration")
    @After(WidgetNodeSettingsBase.CommonSections.ValidationSection.class)
    interface FileChooserSection {
    }

    @Widget(title = "Label", description = "A descriptive label that will be shown in the dialog")
    @Layout(WidgetNodeSettingsBase.CommonSections.LabelSection.class)
    @TextInputWidget
    @Persist(configKey = "label")
    String m_label = "File Chooser";

    @Widget(title = "Description", description = "Some lines of description that will be shown for instance in the node description of the component exposing a dialog")
    @Layout(WidgetNodeSettingsBase.CommonSections.LabelSection.class)
    @TextAreaWidget
    @Persist(configKey = "description")
    String m_description = "Enter Description";

    @Widget(title = "Parameter Name", description = "Parameter identifier for external parameterization (e.g. batch execution).")
    @Layout(WidgetNodeSettingsBase.CommonSections.ParameterSection.class)
    @TextInputWidget
    @Persist(configKey = "flowVariableName")
    String m_parameter = "file-chooser";

    @Widget(title = "Hide in Dialog", description = "If the widget is hidden, it cannot be shown in the dialog, and becomes unreachable.")
    @Layout(WidgetNodeSettingsBase.CommonSections.ParameterSection.class)
    @Persist(configKey = "hideInWizard")
    boolean m_hideInWizard = false;

    @Widget(title = "Custom CSS", description = "Enter custom CSS styling for this widget")
    @Layout(WidgetNodeSettingsBase.CommonSections.InputSection.class)
    @TextAreaWidget
    @Persist(configKey = "customCSS")
    String m_customCSS = "";

    // File chooser specific settings
    @Widget(title = "Select workflows", description = "Allow selection of workflow files")
    @Layout(FileChooserSection.class)
    @Persist(configKey = "selectWorkflows")
    boolean m_selectWorkflows = false;

    @Widget(title = "Select directories", description = "Allow selection of directories")
    @Layout(FileChooserSection.class)
    @Persist(configKey = "selectDirectories")
    boolean m_selectDirectories = false;

    @Widget(title = "Select data files", description = "Allow selection of data files")
    @Layout(FileChooserSection.class)
    @Persist(configKey = "selectDataFiles")
    boolean m_selectDataFiles = true;

    @Widget(title = "Valid File Extensions", description = "Comma-separated list of valid file extensions (e.g., csv,txt,xls)")
    @Layout(FileChooserSection.class)
    @TextInputWidget
    @Persist(configKey = "validExtensions")
    String m_validExtensions = "";

    @Widget(title = "Output selected item type", description = "Include the type of the selected item in the output")
    @Layout(FileChooserSection.class)
    @Persist(configKey = "outputType")
    boolean m_outputType = false;

    @Widget(title = "Allow multiple selection", description = "Allow selection of multiple files/folders")
    @Layout(FileChooserSection.class)
    @Persist(configKey = "multipleSelection")
    boolean m_multipleSelection = false;

    @Widget(title = "Use default mount ID", description = "Use the default mount point for file access")
    @Layout(FileChooserSection.class)
    @Persist(configKey = "useDefaultMountId")
    boolean m_useDefaultMountId = true;

    @Widget(title = "Custom Mount ID", description = "Custom mount point identifier")
    @Layout(FileChooserSection.class)
    @TextInputWidget
    @Persist(configKey = "customMountId")
    String m_customMountId = "";

    @Widget(title = "Root Directory", description = "Root directory for file browsing")
    @Layout(FileChooserSection.class)
    @TextInputWidget
    @Persist(configKey = "rootDir")
    String m_rootDir = "";

    @Widget(title = "Default File Path", description = "Default file path to display")
    @Layout(FileChooserSection.class)
    @TextInputWidget
    @Persistor(DefaultFilePersistor.class)
    String m_defaultPath = "";



    static class DefaultFilePersistor implements NodeParametersPersistor<String> {
        @Override
        public String load(final NodeSettingsRO settings) throws InvalidSettingsException {
            try {
                NodeSettingsRO defaultSettings = settings.getNodeSettings("defaultValue");
                NodeSettingsRO itemsSettings = defaultSettings.getNodeSettings("items");
                String[] keys = itemsSettings.keySet().toArray(new String[0]);
                if (keys.length > 0) {
                    NodeSettingsRO firstItem = itemsSettings.getNodeSettings(keys[0]);
                    return firstItem.getString("path", "");
                }
                return "";
            } catch (InvalidSettingsException e) {
                return "";
            }
        }

        @Override
        public void save(final String value, final NodeSettingsWO settings) {
            NodeSettingsWO defaultSettings = settings.addNodeSettings("defaultValue");
            NodeSettingsWO itemsSettings = defaultSettings.addNodeSettings("items");
            if (value != null && !value.isEmpty()) {
                NodeSettingsWO firstItem = itemsSettings.addNodeSettings("0");
                firstItem.addString("path", value);
                firstItem.addString("type", "data");
            }
        }

        @Override
        public String[][] getConfigPaths() {
            return new String[][] {{"defaultValue"}};
        }
    }
}
