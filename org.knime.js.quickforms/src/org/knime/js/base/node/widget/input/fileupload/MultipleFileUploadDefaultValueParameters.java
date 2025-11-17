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
 *   5 Nov 2025 (Robin Gerling): created
 */
package org.knime.js.base.node.widget.input.fileupload;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.webui.node.dialog.defaultdialog.internal.file.FileReaderWidget;
import org.knime.core.webui.node.dialog.defaultdialog.internal.file.FileSystemOption;
import org.knime.core.webui.node.dialog.defaultdialog.internal.file.WithFileSystem;
import org.knime.core.webui.node.dialog.defaultdialog.util.updates.StateComputationFailureException;
import org.knime.core.webui.node.dialog.defaultdialog.widget.Modification;
import org.knime.core.webui.node.dialog.defaultdialog.widget.Modification.WidgetGroupModifier;
import org.knime.js.base.node.base.input.fileupload.FileUploadNodeUtil;
import org.knime.js.base.node.base.input.fileupload.FileUploadObject;
import org.knime.js.base.node.base.input.fileupload.MultipleFileUploadNodeValue;
import org.knime.js.base.node.parameters.ConfigurationAndWidgetNodeParametersUtil.OutputSection;
import org.knime.js.base.node.widget.WidgetConfig;
import org.knime.node.parameters.NodeParameters;
import org.knime.node.parameters.NodeParametersInput;
import org.knime.node.parameters.Widget;
import org.knime.node.parameters.layout.Layout;
import org.knime.node.parameters.migration.LoadDefaultsForAbsentFields;
import org.knime.node.parameters.persistence.NodeParametersPersistor;
import org.knime.node.parameters.updates.ParameterReference;
import org.knime.node.parameters.updates.StateProvider;
import org.knime.node.parameters.updates.ValueProvider;
import org.knime.node.parameters.updates.ValueReference;

/**
 * The parameters of the default value for the multiple file upload widget node.
 *
 * @author Robin Gerling
 */
@LoadDefaultsForAbsentFields
@Layout(OutputSection.Top.class)
@SuppressWarnings("restriction")
final class MultipleFileUploadDefaultValueParameters implements NodeParameters {

    MultipleFileUploadDefaultValueParameters() {
    }

    MultipleFileUploadDefaultValueParameters(final MultipleFileUploadFile defaultFile,
        final MultipleFileUploadFile[] wizardFiles, final boolean localUpload) {
        m_defaultFile = defaultFile;
        m_wizardFiles = wizardFiles;
        m_localUpload = localUpload;
    }

    MultipleFileUploadFile m_defaultFile = new MultipleFileUploadFile();

    /**
     * Possible additional files serving as second, third, etc. default file. These can only be set in the component
     * view by uploading multiple files and applying as default.
     */
    @Modification(MultipleFileUploadFile.RemoveUIAnnotations.class)
    @ValueProvider(RemoveWizardFilesIfDefaultIsChosen.class)
    MultipleFileUploadFile[] m_wizardFiles = new MultipleFileUploadFile[0];

    static final class RemoveWizardFilesIfDefaultIsChosen implements StateProvider<MultipleFileUploadFile[]> {

        @Override
        public void init(final StateProviderInitializer initializer) {
            initializer.computeOnValueChange(MultipleFileUploadFile.PathReference.class);
        }

        @Override
        public MultipleFileUploadFile[] computeState(final NodeParametersInput parametersInput)
            throws StateComputationFailureException {
            return new MultipleFileUploadFile[0];
        }
    }

    boolean m_localUpload = MultipleFileUploadNodeValue.DEFAULT_LOCAL_UPLOAD;

    private static class MultipleFileUploadFile implements NodeParameters {

        MultipleFileUploadFile() {
        }

        MultipleFileUploadFile(final String path, final String fileName, final boolean pathValid, final Long fileSize,
            final String id) {
            m_path = path;
            m_fileName = fileName;
            m_pathValid = pathValid;
            m_fileSize = Optional.ofNullable(fileSize);
            m_id = id;
        }

        @Widget(title = "Default file", description = """
                The file that will be used during design time, i.e. when no file is provided by a component view.
                It is possible to enter a URL here. This can be useful if a default file is to be addressed with
                the knime:// protocol (e.g. knime://knime.workflow/../data/file.csv) or if the file is present on a
                remote server.
                """)
        @FileReaderWidget
        @WithFileSystem(FileSystemOption.LOCAL)
        @ValueReference(PathReference.class)
        @Modification.WidgetReference(PathReference.class)
        String m_path = FileUploadObject.DEFAULT_PATH;

        @ValueProvider(FileNameValueProvider.class)
        @Modification.WidgetReference(FileNameReference.class)
        String m_fileName = FileUploadObject.DEFAULT_FILE_NAME;

        boolean m_pathValid = FileUploadObject.DEFAULT_PATH_VALID;

        @ValueProvider(FileSizeValueProvider.class)
        @Modification.WidgetReference(FileSizeReference.class)
        Optional<Long> m_fileSize = Optional.ofNullable(FileUploadObject.DEFAULT_FILE_SIZE);

        @SuppressWarnings("unused")
        String m_id = "";

        private static final class PathReference implements ParameterReference<String>, Modification.Reference {
        }

        private static final class FileNameReference implements Modification.Reference {
        }

        private static final class FileSizeReference implements Modification.Reference {
        }

        private abstract static class AbstractPathSupplierProvider<T> implements StateProvider<T> {
            protected Supplier<String> m_pathSupplier;

            @Override
            public final void init(final StateProviderInitializer initializer) {
                m_pathSupplier = initializer.computeFromValueSupplier(PathReference.class);
            }

        }

        private static final class FileNameValueProvider extends AbstractPathSupplierProvider<String> {

            @Override
            public String computeState(final NodeParametersInput parametersInput)
                throws StateComputationFailureException {
                return FileUploadNodeUtil.getFileNameFromPath(m_pathSupplier.get());
            }
        }

        private static final class FileSizeValueProvider extends AbstractPathSupplierProvider<Optional<Long>> {

            @Override
            public Optional<Long> computeState(final NodeParametersInput parametersInput)
                throws StateComputationFailureException {
                final var pathString = m_pathSupplier.get();
                final var file = new File(pathString);
                if (!file.exists()) {
                    return Optional.empty();
                }
                final var path = Paths.get(pathString);
                try {
                    return Optional.of(Files.size(path));
                } catch (Exception e) { // NOSONAR
                    return Optional.empty();
                }
            }

        }

        /**
         * Use this modification to remove the ui from these parameters
         */
        public static final class RemoveUIAnnotations implements Modification.Modifier {
            @Override
            public void modify(final WidgetGroupModifier group) {
                group.find(PathReference.class).removeAnnotation(Widget.class);
                group.find(PathReference.class).removeAnnotation(ValueReference.class);
                group.find(FileNameReference.class).removeAnnotation(ValueProvider.class);
                group.find(FileSizeReference.class).removeAnnotation(ValueProvider.class);
            }
        }
    }

    static final class MultipleFileUploadDefaultValueParametersPersistor
        implements NodeParametersPersistor<MultipleFileUploadDefaultValueParameters> {

        @Override
        public MultipleFileUploadDefaultValueParameters load(final NodeSettingsRO settings)
            throws InvalidSettingsException {
            final var defaultValueSettings = settings.getNodeSettings(WidgetConfig.CFG_DEFAULT_VALUE);
            final var files = MultipleFileUploadNodeValue.loadFiles(defaultValueSettings);
            final var transformedFiles =
                new ArrayList<>(Arrays.stream(files).map(fuo -> new MultipleFileUploadFile(fuo.getPath(),
                    fuo.getFileName(), fuo.isPathValid(), fuo.getFileSize(), fuo.getId())).toList());
            final var defaultFile =
                transformedFiles.isEmpty() ? new MultipleFileUploadFile() : transformedFiles.remove(0);
            final var localUpload = MultipleFileUploadNodeValue.loadLocalUpload(defaultValueSettings);
            return new MultipleFileUploadDefaultValueParameters(defaultFile,
                transformedFiles.toArray(MultipleFileUploadFile[]::new), localUpload);
        }

        @Override
        public void save(final MultipleFileUploadDefaultValueParameters param, final NodeSettingsWO settings) {
            AtomicInteger idIndex = new AtomicInteger(0);
            final Stream<MultipleFileUploadFile> defaultFileStream =
                defaultFileIsSet(param.m_defaultFile) ? Stream.of(param.m_defaultFile) : Stream.empty();
            final var files = Stream.concat(defaultFileStream, Arrays.stream(param.m_wizardFiles))
                .map(mfuf -> new FileUploadObject(mfuf.m_path, mfuf.m_pathValid, mfuf.m_fileName,
                    MultipleFileUploadNodeValue.DEFAULT_ID + idIndex.getAndIncrement(), mfuf.m_fileSize.orElse(null)))
                .toArray(FileUploadObject[]::new);
            final var defaultValueSettings = settings.addNodeSettings(WidgetConfig.CFG_DEFAULT_VALUE);
            MultipleFileUploadNodeValue.saveSettings(defaultValueSettings, files, param.m_localUpload);
        }

        private static boolean defaultFileIsSet(final MultipleFileUploadFile defaultFile) {
            return !defaultFile.m_fileName.equals(FileUploadObject.DEFAULT_FILE_NAME)
                && !defaultFile.m_fileSize.equals(Optional.ofNullable(FileUploadObject.DEFAULT_FILE_SIZE))
                && !defaultFile.m_path.equals(FileUploadObject.DEFAULT_PATH);
        }

        private static final String CFG_DEFAULT_FILE = MultipleFileUploadNodeValue.DEFAULT_ID + "0";

        @Override
        public String[][] getConfigPaths() {
            return new String[][]{{WidgetConfig.CFG_DEFAULT_VALUE, CFG_DEFAULT_FILE, FileUploadObject.CFG_PATH},
                {WidgetConfig.CFG_DEFAULT_VALUE, CFG_DEFAULT_FILE, FileUploadObject.CFG_PATH_VALID},
                {WidgetConfig.CFG_DEFAULT_VALUE, CFG_DEFAULT_FILE, FileUploadObject.CFG_FILE_NAME},
                {WidgetConfig.CFG_DEFAULT_VALUE, CFG_DEFAULT_FILE, FileUploadObject.CFG_FILE_SIZE}};
        }

    }

}
