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

import java.util.function.Supplier;

import org.knime.core.webui.node.dialog.defaultdialog.internal.file.FileReaderWidget;
import org.knime.core.webui.node.dialog.defaultdialog.internal.file.FileSystemOption;
import org.knime.core.webui.node.dialog.defaultdialog.internal.file.WithFileSystem;
import org.knime.core.webui.node.dialog.defaultdialog.util.updates.StateComputationFailureException;
import org.knime.js.base.node.base.input.fileupload.FileUploadNodeUtil;
import org.knime.js.base.node.base.input.fileupload.FileUploadNodeValue;
import org.knime.js.base.node.parameters.ConfigurationAndWidgetNodeParametersUtil.OutputSection;
import org.knime.js.base.node.parameters.OverwrittenByValueMessage;
import org.knime.js.base.node.widget.input.fileupload.FileUploadWidgetNodeModel;
import org.knime.node.parameters.NodeParameters;
import org.knime.node.parameters.NodeParametersInput;
import org.knime.node.parameters.Widget;
import org.knime.node.parameters.layout.Layout;
import org.knime.node.parameters.migration.LoadDefaultsForAbsentFields;
import org.knime.node.parameters.persistence.Persist;
import org.knime.node.parameters.updates.ParameterReference;
import org.knime.node.parameters.updates.StateProvider;
import org.knime.node.parameters.updates.ValueProvider;
import org.knime.node.parameters.updates.ValueReference;
import org.knime.node.parameters.widget.message.TextMessage;

/**
 * The parameters of the default value for the single file upload configuration/widget nodes.
 *
 * @author Robin Gerling
 */
@LoadDefaultsForAbsentFields
@Layout(OutputSection.Top.class)
public final class SingleFileUploadDefaultValueParameters implements NodeParameters {

    @TextMessage(FileOverwrittenByValueMessage.class)
    Void m_overwrittenByValueMessage;

    @Widget(title = "Default file", description = """
            The file that will be used during design time, i.e. when no file is provided by a component dialog.
            It is possible to enter a URL here. This can be useful if a default file is to be addressed with
            the knime:// protocol (e.g. knime://knime.workflow/../data/file.csv) or if the file is present on a
            remote server.
            """)
    @FileReaderWidget
    @WithFileSystem(FileSystemOption.LOCAL)
    @ValueReference(PathReference.class)
    @Persist(configKey = FileUploadNodeValue.CFG_PATH)
    String m_filePath = FileUploadNodeValue.DEFAULT_PATH;

    /**
     * Not used in the configuration node. ({@link FileUploadNodeValue#isPathValid}) is only used in the Widget node
     * model, but it is not meant to be changed by a user. It is only read in, changed in the model
     * {@link FileUploadWidgetNodeModel#loadViewValue}, and saved again.
     */
    @Persist(configKey = FileUploadNodeValue.CFG_PATH_VALID)
    boolean m_pathValid = FileUploadNodeValue.DEFAULT_PATH_VALID;

    /**
     * The file name is automatically determined from the file path ({@link FileNameValueProvider}) and was not a
     * setting in the classic dialog.
     */
    @ValueProvider(FileNameValueProvider.class)
    @Persist(configKey = FileUploadNodeValue.CFG_FILE_NAME)
    String m_fileName = FileUploadNodeValue.DEFAULT_FILE_NAME;

    /**
     * Not used in the configuration node. ({@link FileUploadNodeValue#isLocalUpload}) is only used in the Widget node
     * model, but it is not meant to be changed by a user, but rather by the node itself.
     */
    @Persist(configKey = FileUploadNodeValue.CFG_LOCAL_UPLOAD)
    boolean m_localUpload = FileUploadNodeValue.DEFAULT_LOCAL_UPLOAD;

    private static final class PathReference implements ParameterReference<String> {
    }

    private static final class FileNameValueProvider implements StateProvider<String> {

        private Supplier<String> m_pathSupplier;

        @Override
        public void init(final StateProviderInitializer initializer) {
            m_pathSupplier = initializer.computeFromValueSupplier(PathReference.class);
        }

        @Override
        public String computeState(final NodeParametersInput parametersInput) throws StateComputationFailureException {
            return FileUploadNodeUtil.getFileNameFromPath(m_pathSupplier.get());
        }
    }

    private static final class FileOverwrittenByValueMessage extends OverwrittenByValueMessage<FileUploadNodeValue> {

        @Override
        protected String valueToString(final FileUploadNodeValue value) {
            return value.getPath() != null ? value.getPath() : "";
        }
    }
}
