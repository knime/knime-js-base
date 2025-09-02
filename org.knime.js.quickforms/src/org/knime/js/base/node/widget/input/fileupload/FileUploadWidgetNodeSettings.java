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
package org.knime.js.base.node.widget.input.fileupload;

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
import org.knime.node.parameters.widget.number.NumberInputWidget;
import org.knime.node.parameters.widget.text.TextAreaWidget;
import org.knime.node.parameters.widget.text.TextInputWidget;

/**
 * Settings for the File Upload Widget node.
 *
 * @author AI Migration
 */
@SuppressWarnings("restriction")
public final class FileUploadWidgetNodeSettings implements NodeParameters {

    @Section(title = "File Upload Configuration")
    @After(WidgetNodeSettingsBase.CommonSections.ValidationSection.class)
    interface UploadSection {
    }

    @Widget(title = "Label", description = "A descriptive label that will be shown in the dialog")
    @Layout(WidgetNodeSettingsBase.CommonSections.LabelSection.class)
    @TextInputWidget
    @Persist(configKey = "label")
    String m_label = "File Upload";

    @Widget(title = "Description", description = "Some lines of description that will be shown for instance in the node description of the component exposing a dialog")
    @Layout(WidgetNodeSettingsBase.CommonSections.LabelSection.class)
    @TextAreaWidget
    @Persist(configKey = "description")
    String m_description = "Enter Description";

    @Widget(title = "Parameter Name", description = "Parameter identifier for external parameterization (e.g. batch execution).")
    @Layout(WidgetNodeSettingsBase.CommonSections.ParameterSection.class)
    @TextInputWidget
    @Persist(configKey = "flowVariableName")
    String m_parameter = "file-upload";

    @Widget(title = "Hide in Dialog", description = "If the widget is hidden, it cannot be shown in the dialog, and becomes unreachable.")
    @Layout(WidgetNodeSettingsBase.CommonSections.ParameterSection.class)
    @Persist(configKey = "hideInWizard")
    boolean m_hideInWizard = false;

    @Widget(title = "Custom CSS", description = "Enter custom CSS styling for this widget")
    @Layout(WidgetNodeSettingsBase.CommonSections.InputSection.class)
    @TextAreaWidget
    @Persist(configKey = "customCSS")
    String m_customCSS = "";

    // File upload specific settings
    @Widget(title = "Valid File Extensions", description = "Comma-separated list of valid file extensions (e.g., csv,txt,xlsx)")
    @Layout(UploadSection.class)
    @TextInputWidget
    @Persistor(FileExtensionsPersistor.class)
    String m_validExtensions = "";

    @Widget(title = "Default File Path", description = "Default file path to pre-populate the upload field")
    @Layout(UploadSection.class)
    @TextInputWidget
    @Persistor(DefaultFilePersistor.class)
    String m_defaultFile = "";

    @Widget(title = "Timeout (seconds)", description = "Timeout for file upload operations in seconds")
    @Layout(UploadSection.class)
    @NumberInputWidget
    @Persist(configKey = "timeout")
    double m_timeout = 1.0;

    @Widget(title = "Disable output if file does not exist", description = "If enabled, disable the output port when the specified file does not exist")
    @Layout(UploadSection.class)
    @Persist(configKey = "disableOutput")
    boolean m_disableOutput = false;

    @Widget(title = "Store uploaded file in workflow directory", description = "If enabled, uploaded files are stored in the workflow directory")
    @Layout(UploadSection.class)
    @Persist(configKey = "storeInWfDir")
    boolean m_storeInWfDir = false;

    static class FileExtensionsPersistor implements NodeParametersPersistor<String> {
        @Override
        public String load(final NodeSettingsRO settings) throws InvalidSettingsException {
            String[] extensions = settings.getStringArray("fileTypes", new String[0]);
            return String.join(",", extensions);
        }

        @Override
        public void save(final String value, final NodeSettingsWO settings) {
            String[] extensions = value != null && !value.trim().isEmpty()
                ? value.split(",")
                : new String[0];
            // Clean up extensions
            for (int i = 0; i < extensions.length; i++) {
                extensions[i] = extensions[i].trim();
            }
            settings.addStringArray("fileTypes", extensions);
        }

        @Override
        public String[][] getConfigPaths() {
            return new String[][] {{"fileTypes"}};
        }
    }

    static class DefaultFilePersistor implements NodeParametersPersistor<String> {
        @Override
        public String load(final NodeSettingsRO settings) throws InvalidSettingsException {
            try {
                NodeSettingsRO defaultSettings = settings.getNodeSettings("defaultValue");
                return defaultSettings.getString("path", "");
            } catch (InvalidSettingsException e) {
                return "";
            }
        }

        @Override
        public void save(final String value, final NodeSettingsWO settings) {
            NodeSettingsWO defaultSettings = settings.addNodeSettings("defaultValue");
            defaultSettings.addString("path", value != null ? value : "");
        }

        @Override
        public String[][] getConfigPaths() {
            return new String[][] {{"defaultValue"}};
        }
    }
}
