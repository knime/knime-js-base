/*
 * ------------------------------------------------------------------------
 *  Copyright by KNIME GmbH, Konstanz, Germany
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
 * ------------------------------------------------------------------------
 *
 */
package org.knime.quickform.nodes.in.fileupload;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.util.node.quickform.AbstractQuickFormElement;
import org.knime.core.util.node.quickform.in.AbstractQuickFormInElement;
import org.knime.core.util.node.quickform.in.FileUploadQuickFormInElement;
import org.knime.quickform.nodes.in.QuickFormInNodeModel;

/**
 * NodeModel to file upload form element.
 * @author Bernd Wiswedel, KNIME.com, Zurich, Switzerland
 */
public class FileUploadQuickFormInNodeModel
    extends QuickFormInNodeModel<FileUploadQuickFormInConfiguration> {

    private static final NodeLogger LOGGER =
        NodeLogger.getLogger(FileUploadQuickFormInNodeModel.class);

    private File m_file;

    /** {@inheritDoc} */
    @Override
    public FileUploadQuickFormInConfiguration createConfiguration() {
        return new FileUploadQuickFormInConfiguration();
    }

    /** {@inheritDoc} */
    @Override
    public AbstractQuickFormInElement getQuickFormElement() {
        FileUploadQuickFormInConfiguration cfg = getConfiguration();
        FileUploadQuickFormInElement fileUpload =
                new FileUploadQuickFormInElement(cfg.getLabel(), cfg.getDescription(), cfg.getWeight(),
                        cfg.getExtensions());
        fileUpload.setFile(m_file);
        return fileUpload;
    }

    /** {@inheritDoc} */
    @Override
    public void loadFromQuickFormElement(
            final AbstractQuickFormInElement formElement)
            throws InvalidSettingsException {
        FileUploadQuickFormInElement fe =
            AbstractQuickFormElement.cast(
                    FileUploadQuickFormInElement.class, formElement);
        m_file = fe.getFile();
        FileUploadQuickFormInConfiguration cfg = getConfiguration();
        if (m_file == null) {
            cfg.getValueConfiguration().setLocation(null);
            LOGGER.debug("Setting file to read to \"<null>\".");
        } else {
            final String filePath = m_file.getAbsolutePath();
            cfg.getValueConfiguration().setLocation(filePath);
            LOGGER.debug("Setting file to read to \"" + filePath + "\".");
        }
    }

    /** {@inheritDoc} */
    @Override
    protected void createAndPushFlowVariable() throws InvalidSettingsException {
        FileUploadQuickFormInConfiguration config = getConfiguration();
        if (config == null) {
            throw new InvalidSettingsException("No configuration available");
        }
        final String defaultFile = config.getValueConfiguration().getLocation();
        if (defaultFile == null || defaultFile.length() == 0) {
            throw new InvalidSettingsException(
                    "Invalid default file: " + defaultFile);
        }
        File f = m_file;
        if (f == null) {
            f = new File(defaultFile);
        }
        if (!f.exists()) {
            StringBuilder b = new StringBuilder("No such file: \"");
            b.append(f.getAbsolutePath()).append("\"");
            if (m_file != null) {
                b.append(" (file was set as part of quick form remote control");
                b.append(" - default is \"").append(defaultFile);
                b.append("\")");
            }
        }
        String path = f.getAbsolutePath();
        URL url;
        try {
            url = f.toURI().toURL();
        } catch (MalformedURLException e) {
            StringBuilder b = new StringBuilder("Unable to derive URL from ");
            b.append("file: \"").append(f.getAbsolutePath()).append("\"");
            if (m_file != null) {
                b.append(" (file was set as part of quick form remote control");
                b.append(" - default is \"").append(defaultFile);
                b.append("\")");
            }
            throw new InvalidSettingsException(b.toString(), e);
        }
        String varIdentifier = config.getVariableName();
        pushFlowVariableString(varIdentifier, path);
        pushFlowVariableString(varIdentifier + " (URL)", url.toString());
    }

    /** {@inheritDoc} */
    @Override
    protected void loadValidatedSettingsFrom(final NodeSettingsRO settings)
            throws InvalidSettingsException {
        super.loadValidatedSettingsFrom(settings);
        m_file = null;
    }

}
