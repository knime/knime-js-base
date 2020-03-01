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

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.js.base.node.base.input.fileupload.FileUploadNodeConfig;
import org.knime.js.base.node.base.input.fileupload.FileUploadNodeValue;
import org.knime.js.base.node.configuration.input.fileupload.FileUploadDialogNodeValue;
import org.knime.js.base.node.widget.LabeledFlowVariableWidgetConfig;

/**
 * The config for the file upload widget node
 *
 * @author Daniel Bogenrieder, KNIME GmbH, Konstanz, Germany
 */
public class FileUploadInputWidgetConfig extends LabeledFlowVariableWidgetConfig<FileUploadNodeValue> {

    private final FileUploadNodeConfig m_config;

    private static final String CFG_STORE_IN_WF_DIR = "store_in_wf_dir";

    private static final boolean DEFAULT_STORE_IN_WF_DIR = false;

    private boolean m_storeInWfDir = false;

    /**
     * @return the fileTypes
     */
    public String[] getFileTypes() {
        return m_config.getFileTypes();
    }

    /**
     * @return the errorMessage
     */
    public String getErrorMessage() {
        return m_config.getErrorMessage();
    }

    /**
     * @return the timeout
     */
    public int getTimeout() {
        return m_config.getTimeout();

    }

    /**
     * @return the disableOutput
     */
    public boolean getDisableOutput() {
        return m_config.getDisableOutput();
    }

    /**
     * @return the storeRelative
     */
    public boolean isStoreInWfDir() {
        return m_storeInWfDir;
    }

    /**
     * @param storeRelative the storeRelative to set
     */
    public void setStoreInWfDir(final boolean storeRelative) {
        m_storeInWfDir = storeRelative;
    }

    /**
     * Instantiate a new config object
     */
    public FileUploadInputWidgetConfig() {
        m_config = new FileUploadNodeConfig();
    }

    /**
     * @return the config
     */
    public FileUploadNodeConfig getFileUploadConfig() {
        return m_config;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected FileUploadDialogNodeValue createEmptyValue() {
        return new FileUploadDialogNodeValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveSettings(final NodeSettingsWO settings) {
        super.saveSettings(settings);
        m_config.saveSettings(settings);
        settings.addBoolean(CFG_STORE_IN_WF_DIR, m_storeInWfDir);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
        super.loadSettings(settings);
        m_config.loadSettings(settings);
        m_storeInWfDir = settings.getBoolean(CFG_STORE_IN_WF_DIR, DEFAULT_STORE_IN_WF_DIR);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadSettingsInDialog(final NodeSettingsRO settings) {
        super.loadSettingsInDialog(settings);
        m_config.loadSettingsInDialog(settings);
        m_storeInWfDir = settings.getBoolean(CFG_STORE_IN_WF_DIR, DEFAULT_STORE_IN_WF_DIR);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString());
        sb.append(", ");
        sb.append(m_config.toString());
        sb.append(", ");
        sb.append(m_storeInWfDir);
        return sb.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder().appendSuper(super.hashCode())
                .append(m_config)
                .append(m_storeInWfDir)
                .toHashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj.getClass() != getClass()) {
            return false;
        }
        FileUploadInputWidgetConfig other = (FileUploadInputWidgetConfig)obj;
        return new EqualsBuilder().appendSuper(super.equals(obj))
                .append(m_config, other.m_config)
                .append(m_storeInWfDir, other.m_storeInWfDir)
                .isEquals();
    }


}
