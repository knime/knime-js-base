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
 *   4 Nov 2025 (Robin Gerling): created
 */
package org.knime.js.base.node.parameters.fileupload;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.webui.node.dialog.defaultdialog.widget.Modification;
import org.knime.core.webui.node.dialog.defaultdialog.widget.Modification.WidgetGroupModifier;
import org.knime.js.base.node.base.input.fileupload.FileUploadNodeConfig;
import org.knime.js.base.node.base.input.fileupload.FileUploadNodeUtil;
import org.knime.js.base.node.configuration.input.fileupload.FileDialogNodeNodeDialog;
import org.knime.js.base.node.parameters.ConfigurationAndWidgetNodeParametersUtil.FormFieldSection;
import org.knime.js.base.node.parameters.ConfigurationAndWidgetNodeParametersUtil.OutputSection;
import org.knime.js.base.node.widget.input.fileupload.FileUploadWidgetNodeDialog;
import org.knime.node.parameters.NodeParameters;
import org.knime.node.parameters.Widget;
import org.knime.node.parameters.layout.Layout;
import org.knime.node.parameters.migration.LoadDefaultsForAbsentFields;
import org.knime.node.parameters.persistence.NodeParametersPersistor;
import org.knime.node.parameters.persistence.Persist;
import org.knime.node.parameters.persistence.Persistor;
import org.knime.node.parameters.widget.number.NumberInputWidget;
import org.knime.node.parameters.widget.number.NumberInputWidgetValidation.MinValidation.IsNonNegativeValidation;

/**
 * Shared parameters for Single/Multiple File Upload Configuration and Widget nodes.
 *
 * @author Robin Gerling, KNIME GmbH, Konstanz, Germany
 */
@SuppressWarnings("restriction")
@LoadDefaultsForAbsentFields
public final class SingleMultipleFileUploadNodeParameters implements NodeParameters {

    /**
     * The config key for the disable output setting.
     */
    public static final String CFG_DISABLE_OUTPUT = "disable_output";

    /**
     * The default value for the disable output setting.
     */
    public static final boolean DEFAULT_DISABLE_OUTPUT = true;

    @Widget(title = "Valid file extensions", description = """
            A comma-separated list of file extensions that is used as filter in the
            file browser (not only the one in the "Default file" option but also
            in a remote file browser), e.g. ".csv,.csv.gz" (or "csv,csv.gz") will filter for
            files ending with ".csv" or ".csv.gz". Leave empty to accept any file.
            """)
    @Layout(FormFieldSection.class)
    @Persistor(FileExtensionsPersistor.class)
    String m_fileExtensions = "";

    @Widget(title = "Timeout (seconds)", description = """
            The time in seconds after which the connection times out. The timeout is used when testing the existence of
            default files. The default is set to 1 second and should be sufficient in most cases.
            """)
    @NumberInputWidget(minValidation = IsNonNegativeValidation.class)
    @Layout(OutputSection.Top.class)
    @Persistor(TimeoutPersistor.class)
    double m_timeout = 1;

    /**
     * Only used in the frontend of the Widget node, but it as not possible to set in the classic dialog, so we also do
     * not show it.
     */
    @Persist(configKey = FileUploadNodeConfig.CFG_ERROR_MESSAGE)
    String m_errorMessage = "";

    /**
     * Not used in the configuration node. ({@link FileUploadNodeConfig#getDisableOutput()}) is only used in the Widget
     * node model.
     */
    @Widget(title = "Disable output, if file does not exist", description = """
            If enabled, the output port will be disabled, if the provided file does not exist or cannot be accessed.
            This option is helpful in the wizard (KNIME Hub) execution to keep this and subsequent nodes from failing
            during execution.
            """)
    @Layout(OutputSection.Bottom.class)
    @Persist(configKey = CFG_DISABLE_OUTPUT)
    @Modification.WidgetReference(DisableOutputModificationReference.class)
    boolean m_disableOutputIfFileDoesNotExist = DEFAULT_DISABLE_OUTPUT;

    private static final class DisableOutputModificationReference implements Modification.Reference {
    }

    /**
     * Removes the disable output setting from the dialog.
     */
    public static final class HideDisableOutputCheckbox implements Modification.Modifier {
        @Override
        public void modify(final WidgetGroupModifier group) {
            group.find(DisableOutputModificationReference.class).removeAnnotation(Widget.class);
        }
    }

    private static final class FileExtensionsPersistor implements NodeParametersPersistor<String> {

        /**
         * {@link FileUploadNodeUtil#extractExtensions}, but the dialog should show the dots of the extensions
         */
        @Override
        public String load(final NodeSettingsRO settings) throws InvalidSettingsException {
            final var extensions = settings.getStringArray(FileUploadNodeConfig.CFG_FILE_TYPES, new String[0]);
            return Arrays.stream(extensions).filter(value -> !value.contains("|")).collect(Collectors.joining(","));
        }

        @Override
        public void save(final String extensions, final NodeSettingsWO settings) {
            final var fileExtensions = extensions == null ? new String[0] : FileUploadNodeUtil.getFileTypes(extensions);
            settings.addStringArray(FileUploadNodeConfig.CFG_FILE_TYPES, fileExtensions);
        }

        @Override
        public String[][] getConfigPaths() {
            return new String[][]{{FileUploadNodeConfig.CFG_FILE_TYPES}};
        }

    }

    private static final class TimeoutPersistor implements NodeParametersPersistor<Double> {

        /**
         * We need to divide by 1000 as the time is stored in milliseconds. Issue: the flow variable override will show
         * 0, but as we do not know whether we load a flow variable or a non flow variable, we cannot just drop the
         * division in certain cases.</br>
         * Division as in {@link FileDialogNodeNodeDialog#saveSettingsTo(NodeSettingsWO)},
         * {@link FileUploadWidgetNodeDialog#saveSettingsTo(NodeSettingsWO)}.
         */
        @Override
        public Double load(final NodeSettingsRO settings) throws InvalidSettingsException {
            final var timeoutInMilliseconds = settings.getInt(FileUploadNodeConfig.CFG_TIMEOUT);
            return (double)timeoutInMilliseconds / 1000;
        }

        /**
         * Multiplication as in
         * {@link FileDialogNodeNodeDialog#loadSettingsFrom(NodeSettingsRO, org.knime.core.node.port.PortObjectSpec[])},
         * {@link FileUploadWidgetNodeDialog#loadSettingsFrom(NodeSettingsRO, org.knime.core.node.port.PortObjectSpec[])}.
         */
        @Override
        public void save(final Double param, final NodeSettingsWO settings) {
            settings.addInt(FileUploadNodeConfig.CFG_TIMEOUT, (int)(param * 1000));
        }

        @Override
        public String[][] getConfigPaths() {
            return new String[][]{{FileUploadNodeConfig.CFG_TIMEOUT}};
        }

    }
}
