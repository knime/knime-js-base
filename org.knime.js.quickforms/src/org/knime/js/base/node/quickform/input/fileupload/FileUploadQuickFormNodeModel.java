/*
 * ------------------------------------------------------------------------
 *
 *  Copyright by KNIME GmbH, Konstanz, Germany
 *  Website: http://www.knime.org; Email: contact@knime.org
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
 *  KNIME and ECLIPSE being a combined program, KNIME GMBH herewith grants
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
 *   29.09.2014 (Christian Albrecht, KNIME.com AG, Zurich, Switzerland): created
 */
package org.knime.js.base.node.quickform.input.fileupload;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Vector;

import org.apache.commons.io.FilenameUtils;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.dialog.ExternalNodeData;
import org.knime.core.node.port.PortObject;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.port.flowvariable.FlowVariablePortObject;
import org.knime.core.node.port.flowvariable.FlowVariablePortObjectSpec;
import org.knime.core.node.port.inactive.InactiveBranchPortObject;
import org.knime.core.node.port.inactive.InactiveBranchPortObjectSpec;
import org.knime.core.node.web.ValidationError;
import org.knime.js.base.node.quickform.QuickFormFlowVariableNodeModel;

/**
 *
 * @author Christian Albrecht, KNIME.com AG, Zurich, Switzerland
 */
public class FileUploadQuickFormNodeModel extends QuickFormFlowVariableNodeModel<FileUploadQuickFormRepresentation,
        FileUploadQuickFormValue, FileUploadQuickFormConfig> {

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
        if (getConfig().getDisableOutput()) {
            try {
                getFileAndURL();
            } catch (InvalidSettingsException e) {
                setWarningMessage(e.getMessage());
                return new PortObjectSpec[]{InactiveBranchPortObjectSpec.INSTANCE};
            }
        }
        createAndPushFlowVariable();
        return new PortObjectSpec[]{FlowVariablePortObjectSpec.INSTANCE};
    }

    /** {@inheritDoc} */
    @Override
    protected PortObject[] execute(final PortObject[] inObjects, final ExecutionContext exec) throws Exception {
        if (getConfig().getDisableOutput()) {
            try {
                getFileAndURL();
            } catch (InvalidSettingsException e) {
                setWarningMessage(e.getMessage());
                return new PortObject[]{InactiveBranchPortObject.INSTANCE};
            }
        }
        createAndPushFlowVariable();
        return new PortObject[]{FlowVariablePortObject.INSTANCE};
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void createAndPushFlowVariable() throws InvalidSettingsException {
        ValidationError error = validateViewValue(getRelevantValue());
        if (error != null) {
            throw new InvalidSettingsException(error.getError());
        }
        Vector<String> fileValues = getFileAndURL();
        String varIdentifier = getConfig().getFlowVariableName();
        pushFlowVariableString(varIdentifier, fileValues.get(0));
        pushFlowVariableString(varIdentifier + " (URL)", fileValues.get(1));
    }

    private Vector<String> getFileAndURL() throws InvalidSettingsException {
        String path = getRelevantValue().getPath();
        if (path == null || path.isEmpty()) {
            throw new InvalidSettingsException("No file provided");
        }

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
        Vector<String> vector = new Vector<String>();
        vector.add(path);
        vector.add(url.toString());
        return vector;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ValidationError validateViewValue(final FileUploadQuickFormValue viewContent) {
        // check for a valid file extension
        // no items in file types <=> any file type is valid
        if (getConfig().getFileTypes().length > 0) {
            String ext = "." + FilenameUtils.getExtension(viewContent.getPath());
            if (!Arrays.asList(getConfig().getFileTypes()).contains(ext)) {
                return new ValidationError("File extension " + ext + " is not valid");
            }
        }
        return super.validateViewValue(viewContent);
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
        Path p = Paths.get(getConfig().getDefaultValue().getPath());
        try {
            URL url = p.toUri().toURL();
            return ExternalNodeData.builder(getConfig().getParameterName()).resource(url).build();
        } catch (MalformedURLException ex) {
            throw new RuntimeException(ex); // should never happen
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setInputData(final ExternalNodeData inputData) {
        FileUploadQuickFormValue val = createEmptyDialogValue();
        val.setPath(inputData.getResource().getPath());
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