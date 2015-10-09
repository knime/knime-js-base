/*
 * ------------------------------------------------------------------------
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
 * ------------------------------------------------------------------------
 *
 */
package org.knime.quickform.nodes.out.filedownload;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.NoSuchElementException;

import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettings;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.util.node.quickform.out.FileDownloadQuickFormOutElement;
import org.knime.quickform.nodes.out.QuickFormOutNodeModel;

/**
 * Model to file download element.
 * @author Bernd Wiswedel, KNIME.com, Zurich, Switzerland
 */
class FileDownloadQuickFormOutNodeModel extends
        QuickFormOutNodeModel<FileDownloadQuickFormOutConfiguration> {

    private FileDownloadQuickFormOutElement m_element;

    /** {@inheritDoc} */
    @Override
    public FileDownloadQuickFormOutElement getQuickFormElement() {
        return m_element;
    }

    /** {@inheritDoc} */
    @Override
    protected FileDownloadQuickFormOutConfiguration createConfiguration() {
        return new FileDownloadQuickFormOutConfiguration();
    }

    /** {@inheritDoc} */
    @Override
    protected void peekFlowVariable() throws InvalidSettingsException {
        FileDownloadQuickFormOutConfiguration cfg = getConfiguration();
        if (cfg == null) {
            throw new InvalidSettingsException("No configuration available");
        }
        String varName = cfg.getFilePathVariableName();
        if (varName == null || varName.length() == 0) {
            throw new InvalidSettingsException("Invalid (empty) variable name");
        }

        String value;
        try {
            value = peekFlowVariableString(varName);
        } catch (NoSuchElementException e) {
            throw new InvalidSettingsException(e.getMessage(), e);
        }
        File f = new File(value);
        if (!f.exists()) {
            throw new InvalidSettingsException("Variable \"" + varName
                    + "\" does not denote an existing file: " + value);
        }
        m_element = createOutElement(cfg, f);
    }

    /**
     * @param cfg
     * @param f
     * @return
     */
    private static FileDownloadQuickFormOutElement createOutElement(
        final FileDownloadQuickFormOutConfiguration cfg, final File f) {
        return new FileDownloadQuickFormOutElement(cfg.getLabel(), cfg.getDescription(), cfg.getWeight(), f);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadInternals(final File nodeInternDir, final ExecutionMonitor exec) throws IOException,
        CanceledExecutionException {
        File f = new File(nodeInternDir, "file_qf.xml");
        if (!f.exists()) {
            // in 2.8.2 and before there was no file saved. This is not a problem in the desktop but in
            // the KNIME WebPortal ... we leave it to fail then.
            return;
        }
        NodeSettingsRO s = NodeSettings.loadFromXML(new BufferedInputStream(new FileInputStream(f)));
        try {
            File file = new File(s.getString("file"));
            m_element = createOutElement(getConfiguration(), file);
        } catch (InvalidSettingsException e) {
            throw new IOException("Couldn't parse file", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveInternals(final File nodeInternDir, final ExecutionMonitor exec) throws IOException,
        CanceledExecutionException {
        NodeSettings s = new NodeSettings("file_qf");
        s.addString("file", m_element.getFile().getAbsolutePath());
        s.saveToXML(new BufferedOutputStream(new FileOutputStream(new File(nodeInternDir, "file_qf.xml"))));
    }

}
