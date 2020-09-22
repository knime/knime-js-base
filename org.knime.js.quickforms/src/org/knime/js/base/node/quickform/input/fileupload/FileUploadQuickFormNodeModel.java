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
 *   29.09.2014 (Christian Albrecht, KNIME AG, Zurich, Switzerland): created
 */
package org.knime.js.base.node.quickform.input.fileupload;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Vector;

import javax.net.ssl.HttpsURLConnection;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.dialog.ExternalNodeData;
import org.knime.core.node.port.PortObject;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.port.flowvariable.FlowVariablePortObject;
import org.knime.core.node.port.flowvariable.FlowVariablePortObjectSpec;
import org.knime.core.node.port.inactive.InactiveBranchPortObject;
import org.knime.core.node.port.inactive.InactiveBranchPortObjectSpec;
import org.knime.core.node.web.ValidationError;
import org.knime.core.node.workflow.NodeContext;
import org.knime.core.node.workflow.WorkflowContext;
import org.knime.core.util.FileUtil;
import org.knime.core.util.KNIMEServerHostnameVerifier;
import org.knime.js.base.node.quickform.QuickFormFlowVariableNodeModel;

/**
 *
 * @author Christian Albrecht, KNIME AG, Zurich, Switzerland
 */
public class FileUploadQuickFormNodeModel extends QuickFormFlowVariableNodeModel<FileUploadQuickFormRepresentation,
        FileUploadQuickFormValue, FileUploadQuickFormConfig> {

    private static final NodeLogger LOGGER = NodeLogger.getLogger(FileUploadQuickFormNodeModel.class);
    private static final String KNIME_PROTOCOL = "knime";

    /**
     * @param viewName
     */
    protected FileUploadQuickFormNodeModel(final String viewName) {
        super(viewName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FileUploadQuickFormValue createEmptyViewValue() {
        return new FileUploadQuickFormValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getJavascriptObjectID() {
        return "org_knime_js_base_node_quickform_input_fileupload";
    }

    /** {@inheritDoc} */
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
        return new PortObjectSpec[]{FlowVariablePortObjectSpec.INSTANCE};
    }

    /** {@inheritDoc} */
    @Override
    protected PortObject[] execute(final PortObject[] inObjects, final ExecutionContext exec) throws Exception {
        try {
            createAndPushFlowVariable();
        } catch (InvalidSettingsException e) {
            getRelevantValue().setPathValid(false);
            if (getConfig().getDisableOutput()) {
                setWarningMessage(e.getMessage());
                return new PortObject[]{InactiveBranchPortObject.INSTANCE};
            } else {
                throw e;
            }
        }
        return new PortObject[]{FlowVariablePortObject.INSTANCE};
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void createAndPushFlowVariable() throws InvalidSettingsException {
        createAndPushFlowVariable(true);
    }

    private void createAndPushFlowVariable(final boolean openStream) throws InvalidSettingsException {
        ValidationError error = validateViewValue(getRelevantValue());
        if (error != null) {
            throw new InvalidSettingsException(error.getError());
        }
        Vector<String> fileValues = getFileAndURL(openStream);
        String varIdentifier = getConfig().getFlowVariableName();
        if (fileValues.get(0) != null) {
            pushFlowVariableString(varIdentifier, fileValues.get(0));
        }
        pushFlowVariableString(varIdentifier + " (URL)", fileValues.get(1));
        if (StringUtils.isNoneEmpty(getRelevantValue().getFileName())) {
            pushFlowVariableString(varIdentifier + " (file name)", getRelevantValue().getFileName());
        }
    }

    private Vector<String> getFileAndURL(final boolean openStream) throws InvalidSettingsException {
        String path = getRelevantValue().getPath();
        if (path == null || path.isEmpty()) {
            throw new InvalidSettingsException("No file or URL provided");
        }

        Vector<String> vector = new Vector<String>();
        try {
            URL url = new URL(path);
            if ("file".equalsIgnoreCase(url.getProtocol())) {
                Path p = Paths.get(url.toURI());
                if (!Files.exists(p)) {
                    throw new InvalidSettingsException("No such file: \"" + p +"\"");
                }
                vector.add(p.toString());
                vector.add(url.toString());
            } else {
                if (openStream) {
                    // For a remote resource we always copy it locally first, because it may be accessed several times
                    // and if it's an upload from the WebPortal it requires special authentication.
                    String extension = FilenameUtils.getExtension(path);
                    String basename = FilenameUtils.getBaseName(path);
                    File tempFile = FileUtil.createTempFile((basename.length() < 3) ? "prefix" + basename : basename,
                        "." + (StringUtils.isEmpty(extension) ? "bin" : extension));

                    try (InputStream is = openStream(url); OutputStream os = Files.newOutputStream(tempFile.toPath())) {
                        IOUtils.copyLarge(is, os);
                    } catch (Exception e) {
                        StringBuilder b = new StringBuilder("Connection to given URL: \"");
                        b.append(url.toString());
                        if (e instanceof SocketTimeoutException) {
                            b.append("\" timed out. Check that the file is accessible from your network, "
                                + "and consider increasing the default timeout value.");
                        } else {
                            b.append("\" could not be achieved. ");
                            b.append(e.getMessage());
                        }
                        throw new InvalidSettingsException(b.toString(), e);
                    }
                    vector.add(tempFile.getAbsolutePath());
                    vector.add(tempFile.toURI().toString());
                } else {
                    vector.add(null);
                    vector.add(url.toString());
                }
            }
        } catch (MalformedURLException ex) {
            File f = new File(path);
            if (!f.exists()) {
                StringBuilder b = new StringBuilder("No such file: \"");
                b.append(f.getAbsolutePath()).append("\"");
                throw new InvalidSettingsException(b.toString());
            }
            URL url;
            try {
                url = f.toURI().toURL();
            } catch (MalformedURLException e) {
                StringBuilder b = new StringBuilder("Unable to derive URL from ");
                b.append("file: \"").append(f.getAbsolutePath()).append("\"");
                b.append(" (file was set as part of quick form remote control)");
                throw new InvalidSettingsException(b.toString(), e);
            }
            vector.add(path);
            vector.add(url.toString());
        } catch (URISyntaxException ex) {
            // shouldn't happen
            NodeLogger.getLogger(getClass()).debug("Invalid file URI encountered: " + ex.getMessage());
        } catch (IOException ex) {
            throw new InvalidSettingsException(
                "Could not download uploaded file to local temp directory: " + ex.getMessage(), ex);
        }
        return vector;
    }


    private InputStream openStream(final URL url) throws IOException, URISyntaxException {
        final WorkflowContext wfContext = NodeContext.getContext().getWorkflowManager().getContext();

        if ("file".equalsIgnoreCase(url.getProtocol())) {
            // a file system path should only be provided when a default file is used, otherwise access should be
            // blocked due to security reasons
            String defaultPath = getRepresentation().getDefaultValue().getPath();
            if (defaultPath != null && defaultPath.equals(getRelevantValue().getPath())) {
                LOGGER.debug("A file system path has been provided: " + url);
                return Files.newInputStream(Paths.get(url.toURI()));
            } else {
                throw new IllegalArgumentException("A file system path has been provided, but access was denied");
            }
        } else if (KNIME_PROTOCOL.equals(url.getProtocol())) {
            LOGGER.debug("A KNIME relative path has been provided: " + url);

            final URLConnection conn = url.openConnection();

            conn.setConnectTimeout(getConfig().getTimeout());
            conn.setReadTimeout(getConfig().getTimeout());

            return conn.getInputStream();
        } else if (wfContext.getRemoteRepositoryAddress().isPresent() && wfContext.getServerAuthToken().isPresent()) {
            LOGGER.debug("A server upload has been detected. An attempt will be made"
                + " to connect. The provided URL is: " + url);

            final URI repoUri = wfContext.getRemoteRepositoryAddress().get(); // NOSONAR

            final URLConnection conn =
                    new URL(repoUri.getScheme(), repoUri.getHost(), repoUri.getPort(), url.getPath()).openConnection();
            conn.setRequestProperty("Authorization", "Bearer " + wfContext.getServerAuthToken().get()); // NOSONAR

            if (conn instanceof HttpsURLConnection) {
                ((HttpsURLConnection)conn).setHostnameVerifier(KNIMEServerHostnameVerifier.getInstance());
            }
            conn.setConnectTimeout(getConfig().getTimeout());
            conn.setReadTimeout(getConfig().getTimeout());

            return conn.getInputStream();

        } else {
            final String unknownURLMsg = "The URL provided could not be recognized: " + url;
            LOGGER.debug(unknownURLMsg);
            throw new URISyntaxException(url.toString(), unknownURLMsg);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ValidationError validateViewValue(final FileUploadQuickFormValue viewValue) {
        // check for a valid file extension
        // no items in file types <=> any file type is valid
        if (getConfig().getFileTypes().length > 0) {
            String ext = "." + FilenameUtils.getExtension(viewValue.getPath());
            if (!Arrays.asList(getConfig().getFileTypes()).contains(ext)) {
                return new ValidationError("File extension " + ext + " is not valid");
            }
        }
        return super.validateViewValue(viewValue);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FileUploadQuickFormConfig createEmptyConfig() {
        return new FileUploadQuickFormConfig();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected FileUploadQuickFormRepresentation getRepresentation() {
        return new FileUploadQuickFormRepresentation(getRelevantValue(), getConfig());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void copyValueToConfig() {
        getConfig().getDefaultValue().setPath(getViewValue().getPath());
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public ExternalNodeData getInputData() {
        String path = getConfig().getDefaultValue().getPath();
        try {
            URI uri;
            if (StringUtils.isEmpty(path)) {
                uri = ExternalNodeData.NO_URI_VALUE_YET;
            } else {
                uri = FileUtil.toURL(path).toURI();
            }
            return ExternalNodeData.builder(getConfig().getParameterName())
                    .resource(uri)
                    .description(getConfig().getDescription())
                    .build();
        } catch (MalformedURLException | InvalidPathException | URISyntaxException ex) {
            throw new RuntimeException(ex); // should never happen
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setInputData(final ExternalNodeData inputData) {
        FileUploadQuickFormValue val = createEmptyDialogValue();
        val.setPath(inputData.getResource().toString());
        setDialogValue(val);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void validateInputData(final ExternalNodeData inputData) throws InvalidSettingsException {
        if (inputData.getResource() == null) {
            throw new InvalidSettingsException("No external resource URL provided for file upload");
        }
        FileUploadQuickFormValue val = createEmptyDialogValue();
        val.setPath(inputData.getResource().getPath());
        validateDialogValue(val);
    }
}
