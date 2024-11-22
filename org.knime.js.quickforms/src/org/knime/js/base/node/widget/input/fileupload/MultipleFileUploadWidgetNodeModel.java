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
 *   Jun 3, 2019 (Daniel Bogenrieder): created
 */
package org.knime.js.base.node.widget.input.fileupload;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Vector;

import javax.net.ssl.HttpsURLConnection;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.DataType;
import org.knime.core.data.RowKey;
import org.knime.core.data.def.DefaultRow;
import org.knime.core.data.def.LongCell;
import org.knime.core.data.def.StringCell;
import org.knime.core.node.BufferedDataContainer;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.KNIMEConstants;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.NodeSettings;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.port.PortObject;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.port.PortType;
import org.knime.core.node.port.inactive.InactiveBranchPortObjectSpec;
import org.knime.core.node.util.CheckUtils;
import org.knime.core.node.web.ValidationError;
import org.knime.core.node.workflow.NodeContext;
import org.knime.core.node.workflow.contextv2.RestLocationInfo;
import org.knime.core.node.workflow.contextv2.WorkflowContextV2;
import org.knime.core.node.workflow.contextv2.WorkflowContextV2.ExecutorType;
import org.knime.core.util.FileUtil;
import org.knime.core.util.KNIMEServerHostnameVerifier;
import org.knime.core.util.ThreadLocalHTTPAuthenticator;
import org.knime.core.util.auth.CouldNotAuthorizeException;
import org.knime.core.util.pathresolve.ResolverUtil;
import org.knime.core.util.proxy.URLConnectionFactory;
import org.knime.filehandling.core.connections.DefaultFSLocationSpec;
import org.knime.filehandling.core.connections.FSCategory;
import org.knime.filehandling.core.connections.FSLocation;
import org.knime.filehandling.core.data.location.cell.SimpleFSLocationCell;
import org.knime.filehandling.core.data.location.cell.SimpleFSLocationCellFactory;
import org.knime.filehandling.core.data.location.variable.FSLocationVariableType;
import org.knime.js.base.node.base.input.fileupload.FileUploadNodeUtil;
import org.knime.js.base.node.base.input.fileupload.FileUploadObject;
import org.knime.js.base.node.base.input.fileupload.MultipleFileUploadNodeRepresentation;
import org.knime.js.base.node.base.input.fileupload.MultipleFileUploadNodeValue;
import org.knime.js.base.node.widget.WidgetNodeModel;
import org.knime.workbench.explorer.ServerRequestModifier;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

/**
 * The node model for the file upload widget node
 *
 * @author Daniel Bogenrieder, KNIME GmbH, Konstanz, Germany
 * @author Christian Albrecht, KNIME GmbH, Konstanz, Germany
 */
public class MultipleFileUploadWidgetNodeModel extends
    WidgetNodeModel<MultipleFileUploadNodeRepresentation<MultipleFileUploadNodeValue>, MultipleFileUploadNodeValue, MultipleFileUploadInputWidgetConfig> {

    private static final NodeLogger LOGGER = NodeLogger.getLogger(MultipleFileUploadWidgetNodeModel.class);

    private static final String KNIME_PROTOCOL = "knime";

    private static final String KNIME_WORKFLOW = "knime.workflow";

    private final ServerRequestModifier m_requestModifier;

    /**
     * Creates a new file upload widget node model
     *
     * @param viewName the interactive view name
     */
    protected MultipleFileUploadWidgetNodeModel(final String viewName) {
        super(new PortType[0], new PortType[]{BufferedDataTable.TYPE}, viewName);
        Bundle myself = FrameworkUtil.getBundle(getClass());
        if (myself != null) {
            BundleContext ctx = myself.getBundleContext();
            ServiceReference<ServerRequestModifier> ser = ctx.getServiceReference(ServerRequestModifier.class);
            if (ser != null) {
                try {
                    m_requestModifier = ctx.getService(ser);
                } finally {
                    ctx.ungetService(ser);
                }
            } else {
                m_requestModifier = (p, c) -> {
                };
            }
        } else {
            m_requestModifier = (p, c) -> {
            };
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MultipleFileUploadNodeValue createEmptyViewValue() {
        return new MultipleFileUploadNodeValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getJavascriptObjectID() {
        return "org.knime.js.base.node.widget.input.fileupload";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected PortObjectSpec[] configure(final PortObjectSpec[] inSpecs) throws InvalidSettingsException {

        try {
            createAndPushFlowVariable(false);
        } catch (InvalidSettingsException e) {
            if (getConfig().getDisableOutput()) {
                setWarningMessage(e.getMessage());
                return new PortObjectSpec[]{InactiveBranchPortObjectSpec.INSTANCE};
            } else {
                throw e;
            }
        }
        return new PortObjectSpec[]{createTableSpec()};
    }

    private static DataTableSpec createTableSpec() {
        final DataColumnSpec pathSpec =
            new DataColumnSpecCreator("Path", DataType.getType(SimpleFSLocationCell.class)).createSpec();
        final DataColumnSpec nameSpec = new DataColumnSpecCreator("File name", StringCell.TYPE).createSpec();
        final DataColumnSpec sizeSpec = new DataColumnSpecCreator("File size", LongCell.TYPE).createSpec();

        return new DataTableSpec(pathSpec, nameSpec, sizeSpec);
    }

    private void createAndPushFlowVariable(final boolean openStream) throws InvalidSettingsException {
        ValidationError error = validateViewValue(getRelevantValue());
        if (error != null) {
            throw new InvalidSettingsException(error.getError());
        }
        var files = getRelevantValue().getFiles();
        FileUploadObject file;
        if (files != null && files.length > 0) {
            file = files[0];
            Vector<String> fileValues = getFileAndURL(openStream, file);
            String varIdentifier = getConfig().getFlowVariableName();
            if (fileValues.get(0) != null) {
                pushFlowVariableString(varIdentifier, fileValues.get(0));
                FSLocation location =
                    new FSLocation(FSCategory.CUSTOM_URL, String.valueOf(getConfig().getTimeout()), fileValues.get(1));
                pushFlowVariable(varIdentifier + " (Path)", FSLocationVariableType.INSTANCE, location);
            }
            pushFlowVariableString(varIdentifier + " (URL)", fileValues.get(1));
            if (StringUtils.isNoneEmpty(file.getFileName())) {
                pushFlowVariableString(varIdentifier + " (file name)", file.getFileName());
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected PortObject[] performExecute(final PortObject[] inObjects, final ExecutionContext exec) throws Exception {
        DataTableSpec outSpec = createTableSpec();
        BufferedDataContainer cont = exec.createDataContainer(outSpec, true);
        var files = getRelevantValue().getFiles();
        if (files != null) {
            for (var i = 0; i < files.length; i++) {
                var paths = getFileAndURL(true, files[i]);
                File f = new File(files[i].getPath());
                Long fileSize;
                if (f.exists()) {
                    fileSize = files[i].getFileSize();
                } else {
                    fileSize = 0L;
                }
                FSLocation location =
                    new FSLocation(FSCategory.CUSTOM_URL, String.valueOf(getConfig().getTimeout()), paths.get(1));
                cont.addRowToTable(new DefaultRow(RowKey.createRowKey(Long.valueOf(i)),
                    new SimpleFSLocationCellFactory(
                        new DefaultFSLocationSpec(FSCategory.CUSTOM_URL, String.valueOf(getConfig().getTimeout())))
                            .createCell(location),
                    new StringCell(files[i].getFileName()), new LongCell(fileSize)));
            }
        }
        cont.close();

        try {
            createAndPushFlowVariable(true);
        } catch (InvalidSettingsException e) {
            if (getConfig().getDisableOutput()) {
                setWarningMessage(e.getMessage());
            } else {
                throw e;
            }
        }
        return new PortObject[]{cont.getTable()};
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected MultipleFileUploadNodeValue copyConfigToViewValue(final MultipleFileUploadNodeValue currentViewValue,
        final MultipleFileUploadInputWidgetConfig config, final MultipleFileUploadInputWidgetConfig previousConfig) {
        var defaultVal = config.getDefaultValue();
        var previousDefaultVal = previousConfig.getDefaultValue();
        if (!FileUploadNodeUtil.checkUploadFilesEquality(defaultVal.getFiles(), previousDefaultVal.getFiles())) {
            currentViewValue.setFiles(defaultVal.getFiles());
        }
        if (defaultVal.isLocalUpload() != previousDefaultVal.isLocalUpload()) {
            currentViewValue.setLocalUpload(defaultVal.isLocalUpload());
        }
        return currentViewValue;
    }

    private Vector<String> getFileAndURL(final boolean openStream, final FileUploadObject file)
        throws InvalidSettingsException {
        String path = file.getPath();

        Vector<String> vector = new Vector<>();
        try {
            URL url = new URL(path);
            if (!getConfig().isStoreInWfDir() && "file".equalsIgnoreCase(url.getProtocol())) {
                Path p = Paths.get(url.toURI());
                if (!Files.exists(p)) {
                    throw new InvalidSettingsException("No such file: \"" + p + "\"");
                }
                vector.add(p.toString());
                vector.add(url.toString());
            } else {
                if (openStream) {
                    // For a remote resource we always copy it locally first, because it may be accessed several times
                    // and if it's an upload from the WebPortal it requires special authentication.
                    final File tempFile = copyFileToTempLocation(url, file);
                    vector.add(tempFile.getAbsolutePath());
                    vector.add(getConfig().isStoreInWfDir()
                        ? new URI(KNIME_PROTOCOL, KNIME_WORKFLOW,
                            "/" + ResolverUtil.IN_WORKFLOW_TEMP_DIR + "/" + tempFile.getName(), null).toString()
                        : tempFile.toURI().toString());
                } else {
                    vector.add(null);
                    vector.add(url.toString());
                }
            }
        } catch (MalformedURLException ex) {
            File f = new File(path);
            if (!f.exists()) {
                vector.add("");
                vector.add("");
            }
            URI uri;
            try {
                if (openStream && getConfig().isStoreInWfDir()) {
                    final File tempFile = copyFileToTempLocation(f.toURI().toURL(), file);
                    uri = new URI(KNIME_PROTOCOL, KNIME_WORKFLOW,
                        "/" + ResolverUtil.IN_WORKFLOW_TEMP_DIR + "/" + tempFile.getName(), null);
                    path = tempFile.getAbsolutePath();
                } else {
                    uri = f.toURI();
                }
            } catch (URISyntaxException e) {
                StringBuilder b = new StringBuilder("Unable to derive URI from ");
                b.append("file: \"").append(f.getAbsolutePath()).append("\"");
                b.append(" (file was set as part of quick form remote control)");
                throw new InvalidSettingsException(b.toString(), e);
            } catch (IOException e) {
                throw new InvalidSettingsException(
                    "Could not transfer uploaded file to workflow temp directory: " + e.getMessage(), e);
            }
            vector.add(path);
            vector.add(uri.toString());
        } catch (URISyntaxException ex) {
            // shouldn't happen
            LOGGER.debug("Invalid file URI encountered: " + ex.getMessage());
        } catch (IOException ex) {
            throw new InvalidSettingsException(
                "Could not download uploaded file to local temp directory: " + ex.getMessage(), ex);
        }
        return vector;
    }

    private static File writeTempFileFromDataUrl(final DataURL dataUrl, final String fileName)
        throws IOException, InvalidSettingsException {
        final String basename = FilenameUtils.getBaseName(fileName);
        final String extension = FilenameUtils.getExtension(fileName);
        File tempFile = getTempFile(basename, extension);
        try {
            Files.write(tempFile.toPath(), dataUrl.getDecodedData());
        } catch (IllegalArgumentException | IOException ex) {
            throw new InvalidSettingsException("Could not write to temporary file " + tempFile, ex);
        }
        return tempFile;
    }

    private static File getTempFile(final String basename, final String extension) throws IOException {
        return FileUtil.createTempFile((basename.length() < 3) ? ("prefix" + basename) : basename,
            "." + (StringUtils.isEmpty(extension) ? "bin" : extension));
    }

    private File copyFileToTempLocation(final URL url, final FileUploadObject file)
        throws IOException {
        final String basename = FilenameUtils.getBaseName(url.getPath());
        final String extension = FilenameUtils.getExtension(url.getPath());
        File tempFile;
        if (getConfig().isStoreInWfDir()) {
            tempFile = computeFileName(file);
            tempFile.getParentFile().mkdir();
        } else {
            tempFile = getTempFile(basename, extension);
        }

        try (InputStream is = openStream(url); OutputStream os = Files.newOutputStream(tempFile.toPath())) {
            IOUtils.copyLarge(is, os);
        } catch (final Exception e) {
            final StringBuilder b = new StringBuilder("Connection to given URL: \"");
            b.append(url.toString());
            if (e instanceof SocketTimeoutException) {
                b.append("\" timed out. Check that the file is accessible from your network, "
                    + "and consider increasing the default timeout value.");
            } else {
                b.append("\" could not be achieved. ");
                b.append(e.getMessage());
            }
        }
        return tempFile;
    }

    private static File computeFileName(final FileUploadObject file) {
        File rootDir = null;
        // get the flow's tmp dir from its context
        final NodeContext nodeContext = NodeContext.getContext();
        if (nodeContext != null) {
            final WorkflowContextV2 workflowContext = nodeContext.getWorkflowManager().getContextV2();
            if (workflowContext != null) {
                rootDir = workflowContext.getExecutorInfo().getLocalWorkflowPath()
                    .resolve(ResolverUtil.IN_WORKFLOW_TEMP_DIR).toFile();
                rootDir.mkdir();
            }
        }
        if (rootDir == null) {
            // use the standard tmp dir then.
            rootDir = new File(KNIMEConstants.getKNIMETempDir());
        }
        String path = file.getPath();

        // remove query parameter from a URL because otherwise the basename and extension computation below will break
        var index = path.indexOf('?');
        if (index > 0) {
            path = path.substring(0, index);
        }

        final String extension = FilenameUtils.getExtension(path);
        final String basename = FilenameUtils.getBaseName(path);
        return new File(rootDir, basename + file.getId() + "." + extension);
    }

    /** {@inheritDoc} */
    @Override
    protected void onDispose() {
        if (getConfig().isStoreInWfDir()) {
            deleteTmpFiles();
        }
        super.onDispose();
    }

    /** {@inheritDoc} */
    @Override
    protected void performReset() {
        if (getConfig().isStoreInWfDir()) {
            deleteTmpFiles();
        }
        super.performReset();
    }

    private void deleteTmpFiles() {
        for (FileUploadObject fileUploadObject : getRelevantValue().getFiles()) {

            final File file = computeFileName(fileUploadObject);
            final StringBuilder debug = new StringBuilder();
            if (FileUtil.deleteRecursively(file)) {
                debug.append(getConfig().isStoreInWfDir() && file.getParentFile().delete()
                    ? ("Deleted temp directory " + file.getParentFile().getAbsolutePath())
                    : ("Deleted temp directory " + file.getAbsolutePath()));
            }
        }
    }

    private InputStream openStream(final URL url) throws IOException, URISyntaxException {
        if ("file".equalsIgnoreCase(url.getProtocol())) {
            return openFileStream(url);
        } else if (KNIME_PROTOCOL.equals(url.getProtocol())) {
            LOGGER.debug("A KNIME relative path has been provided: " + url);
            return openSimpleStream(url);
        } else {
            return openRemoteStream(url);
        }
    }

    private InputStream openFileStream(final URL url) throws IOException, URISyntaxException {
        // a file system path should only be provided when a default file is used or a file was uploaded from a
        // local view instance, otherwise access should be blocked due to security reasons
        String defaultPath = getRepresentation().getDefaultValue().getFiles()[0].getPath();
        MultipleFileUploadNodeValue currentValue = getRelevantValue();

        // TODO check for all files that if it is a local file check that they live in the workflow area or the temp area
        if (currentValue.isLocalUpload() || (defaultPath != null)) {
            LOGGER.debug("A file system path has been provided: " + url);
            return Files.newInputStream(Paths.get(url.toURI()));
        } else {
            throw new IllegalArgumentException("A file system path has been provided, but access was denied");
        }
    }

    private InputStream openSimpleStream(final URL url) throws IOException {
        try (final var c = ThreadLocalHTTPAuthenticator.suppressAuthenticationPopups()) {
            final var conn = URLConnectionFactory.getConnection(url);
            conn.setConnectTimeout(getConfig().getTimeout());
            conn.setReadTimeout(getConfig().getTimeout());
            return conn.getInputStream();
        }
    }

    private InputStream openRemoteStream(final URL url) throws IOException, URISyntaxException {

        // Opening a remote stream can either be an arbitrary URL or a connection to a KNIME Server to retrieve
        // an uploaded file via WebPortal

        final WorkflowContextV2 wfContext = NodeContext.getContext().getWorkflowManager().getContextV2();
        final boolean isUsingDefaultFile = getRepresentation().getDefaultValue().equals(getRelevantValue());
        final boolean isRunningOnKnimeServer = wfContext.getExecutorType() == ExecutorType.SERVER_EXECUTOR;
        final boolean isRunningOnKnimeHub = wfContext.getExecutorType() == ExecutorType.HUB_EXECUTOR;

        if (isUsingDefaultFile) {
            // For a default file we can not necessarily assume that it is pointing to a KNIME server instance.
            // If the server information matches exactly though, a connection to the KNIME Server is still established

            boolean isExactKnimeServerMatch = false;
            if (isRunningOnKnimeServer) {
                var repoUri = ((RestLocationInfo)wfContext.getLocationInfo()).getRepositoryAddress();
                isExactKnimeServerMatch = repoUri.getHost().equals(url.getHost())
                    && (repoUri.getPort() == url.getPort()) && repoUri.getScheme().equals(url.getProtocol());
            }

            if (isExactKnimeServerMatch) {
                return openStreamToKnimeServer(url);
            } else {
                // Otherwise it is assumed that the URL can be accessed with the simple connection logic.
                return openSimpleStream(url);
            }
        } else if (isRunningOnKnimeHub) {
            return openSimpleStream(url);
        } else if (isRunningOnKnimeServer) {
            // If the value has been changed, assume that a file has been uploaded to the connected KNIME server and
            // open a connection to the known server from this executor.
            return openStreamToKnimeServer(url);
        } else {
            // The value has been changed but this instance is not running as an executor to a KNIME Server.
            final String unknownURLMsg = "The URL provided could not be recognized: " + url;
            LOGGER.debug(unknownURLMsg);
            throw new URISyntaxException(url.toString(), unknownURLMsg);
        }
    }

    private InputStream openStreamToKnimeServer(final URL url) throws IOException {
        LOGGER.debug(
            "A server upload has been detected. An attempt will be made" + " to connect. The provided URL is: " + url);
        final WorkflowContextV2 wfContext = NodeContext.getContext().getWorkflowManager().getContextV2();
        assert wfContext.getLocationInfo() instanceof RestLocationInfo; // otherwise we would not have ended up here
        final var repoUri = ((RestLocationInfo)wfContext.getLocationInfo()).getRepositoryAddress();

        final var resolvedUrl = new URL(repoUri.getScheme(), repoUri.getHost(), repoUri.getPort(), url.getPath());
        try (final var c = ThreadLocalHTTPAuthenticator.suppressAuthenticationPopups()) {
            final var conn = URLConnectionFactory.getConnection(resolvedUrl);

            try {
                ((RestLocationInfo)wfContext.getLocationInfo()).getAuthenticator().authorizeClient(conn);
            } catch (CouldNotAuthorizeException e) {
                throw new IOException("Could not authorize client: " + e.getMessage(), e);
            }

            if (conn instanceof HttpsURLConnection hconn) {
                hconn.setHostnameVerifier(KNIMEServerHostnameVerifier.getInstance());
            }
            conn.setConnectTimeout(getConfig().getTimeout());
            conn.setReadTimeout(getConfig().getTimeout());
            m_requestModifier.modifyRequest(repoUri, conn);

            return conn.getInputStream();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ValidationError validateViewValue(final MultipleFileUploadNodeValue value) {
        // check for a valid file extension
        // no items in file types <=> any file type is valid
        if (getConfig().getFileTypes().length > 0) {
            for (FileUploadObject file : value.getFiles()) {

                String fileName = file.getFileName();
                String nameToTest = StringUtils.isEmpty(fileName) ? file.getPath() : fileName;
                String check_ext = FilenameUtils.getExtension(nameToTest);
                if (StringUtils.isEmpty(check_ext)) {
                    return new ValidationError("File with no extension is not valid");
                }
                final String ext = "." + check_ext.toLowerCase();
                if (!Arrays.asList(getConfig().getFileTypes()).stream()
                    .anyMatch(type -> StringUtils.equalsIgnoreCase(type, ext))) {
                    return new ValidationError("File extension " + ext + " is not valid");
                }
            }
        }
        return super.validateViewValue(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MultipleFileUploadInputWidgetConfig createEmptyConfig() {
        return new MultipleFileUploadInputWidgetConfig();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected MultipleFileUploadNodeRepresentation<MultipleFileUploadNodeValue> getRepresentation() {
        MultipleFileUploadInputWidgetConfig config = getConfig();
        return new MultipleFileUploadNodeRepresentation<>(getRelevantValue(), config.getDefaultValue(),
            config.getFileUploadConfig(), config.getLabelConfig());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadViewValue(final MultipleFileUploadNodeValue viewValue, final boolean useAsDefault) {
        synchronized (getLock()) {
            var files = viewValue.getFiles();
            for (FileUploadObject file : files) {
                var path = file.getPath();
                viewValue.setLocalUpload(false);
                // FIXME this setLocalUpload logic is not working in the case that I upload files. Apply the files and delete the default file after and apply.
                if (path.startsWith(DataURL.SCHEME)) {
                    try {
                        // local uploads utilize data protocol URLs, which need to be further processed
                        DataURL dataUrl = new DataURL(path);
                        String fileName = file.getFileName();
                        File tempFile = writeTempFileFromDataUrl(dataUrl, fileName);
                        path = tempFile.getAbsolutePath();
                        file.setPath(path);
                        viewValue.setLocalUpload(true);
                    } catch (IOException | InvalidSettingsException e) {
                        LOGGER.error("Local file upload could not be processed. " + e.getMessage(), e);
                        // avoid having invalid paths in the output
                        file.setPath(null);
                        file.setPathValid(false);
                    }
                }
            }
        }
        super.loadViewValue(viewValue, useAsDefault);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void useCurrentValueAsDefault() {
        MultipleFileUploadNodeValue defaultValue = getConfig().getDefaultValue();
        MultipleFileUploadNodeValue currentValue = getViewValue();

        defaultValue.setFiles(currentValue.getFiles());
        defaultValue.setLocalUpload(currentValue.isLocalUpload());
    }

    private static final String INTERNAL_FILE_NAME = "file-id.xml";

    /** {@inheritDoc} */
    @Override
    protected void loadInternals(final File nodeInternDir, final ExecutionMonitor exec)
        throws IOException, CanceledExecutionException {
        super.loadInternals(nodeInternDir, exec);
        final File internalFile = new File(nodeInternDir, INTERNAL_FILE_NAME);
        boolean issueWarning = false;
        if (internalFile.exists()) {
            // in most standard cases this isn't reasonable as the folder gets deleted when the flow is closed.
            // however, it's useful if the node is run in a temporary workflow that is part of the streaming executor
            try (InputStream in = new FileInputStream(internalFile)) {
                final NodeSettingsRO s = NodeSettings.loadFromXML(in);
                var amount = s.getInt("amount-files", 0);

                for (var i = 0; i < amount; i++) {
                    CheckUtils.checkSettingNotNull(s.getString("upload-file-id" + i), "id must not be null");
                    issueWarning = !computeFileName(getRelevantValue().getFiles()[i]).exists();
                    if (issueWarning) {
                        break;
                    }
                }
            } catch (final InvalidSettingsException e) {
                throw new IOException(e.getMessage(), e);
            }
        } else {
            issueWarning = getConfig().isStoreInWfDir();
        }
        if (issueWarning) {
            setWarningMessage("Did not restore content; consider to re-execute!");
        }
    }

    /** {@inheritDoc} */
    @Override
    protected void saveInternals(final File nodeInternDir, final ExecutionMonitor exec)
        throws IOException, CanceledExecutionException {
        super.saveInternals(nodeInternDir, exec);
        try (OutputStream w = new FileOutputStream(new File(nodeInternDir, INTERNAL_FILE_NAME))) {
            final NodeSettings s = new NodeSettings("file-upload-widget-node");
            var files = getRelevantValue().getFiles();
            for (var i = 0; i < files.length; i++) {
                s.addString("upload-file-id" + i, files[i].getId());
            }
            s.addInt("amount-files", getRelevantValue().getFiles().length);
            s.saveToXML(w);
        }
    }
}
