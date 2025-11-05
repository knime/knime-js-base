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
package org.knime.js.base.node.base.input.fileupload;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;

/**
 * Base config file for the file upload configuration and widget nodes
 *
 * @author Daniel Bogenrieder, KNIME GmbH, Konstanz, Germany
 * @author Christian Albrecht, KNIME GmbH, Konstanz, Germany
 */
public class MultipleFileUploadNodeConfig {

    private static final String CFG_FILE_TYPES = "types";

    private static final String[] DEFAULT_FILE_TYPES = new String[0];

    private String[] m_fileTypes = DEFAULT_FILE_TYPES;

    private static final String CFG_ERROR_MESSAGE = "error_message";

    private static final String DEFAULT_ERROR_MESSAGE = "";

    private String m_errorMessage = DEFAULT_ERROR_MESSAGE;

    private static final String CFG_DISABLE_OUTPUT = "disable_output";

    private static final String CFG_TIMEOUT = "timeout";

    private static final int DEFAULT_TIMEOUT = 1000;

    private int m_timeout = DEFAULT_TIMEOUT;

    private static final boolean DEFAULT_DISABLE_OUTPUT = true;

    private boolean m_disableOutput = DEFAULT_DISABLE_OUTPUT;

    public static final String CFG_ALLOW_MULTIPLE_FILES = "multiple";

    public static final boolean DEFAULT_ALLOW_MULTIPLE_FILES = true;

    private boolean m_allowMultipleFiles = DEFAULT_ALLOW_MULTIPLE_FILES;

    /**
     * @return the fileTypes
     */
    public String[] getFileTypes() {
        return m_fileTypes;
    }

    /**
     * @param fileTypes the fileTypes to set
     */
    public void setFileTypes(final String[] fileTypes) {
        m_fileTypes = fileTypes;
    }

    /**
     * @return the errorMessage
     */
    public String getErrorMessage() {
        return m_errorMessage;
    }

    /**
     * @param errorMessage the errorMessage to set
     */
    public void setErrorMessage(final String errorMessage) {
        m_errorMessage = errorMessage;
    }

    /**
     * @return the timeout
     */
    public int getTimeout() {
        return m_timeout;
    }

    /**
     * @param timeout the timeout to set
     */
    public void setTimeout(final int timeout) {
        m_timeout = timeout;
    }

    /**
     * @return if multiple file mode is enabled
     */
    public boolean isMultipleFiles() {
        return m_allowMultipleFiles;
    }

    /**
     * @param allowMultipleFiles – true if multiple file mode is enabled
     */
    public void setMultipleFileMode(final boolean allowMultipleFiles) {
        m_allowMultipleFiles = allowMultipleFiles;
    }

    /**
     * @return the disableOutput
     */
    public boolean getDisableOutput() {
        return m_disableOutput;
    }

    /**
     * @param disableOutput the disableOutput to set
     */
    public void setDisableOutput(final boolean disableOutput) {
        m_disableOutput = disableOutput;
    }

    /**
     * Saves the current settings
     *
     * @param settings the settings to save to
     */
    public void saveSettings(final NodeSettingsWO settings) {
        settings.addStringArray(CFG_FILE_TYPES, m_fileTypes);
        settings.addString(CFG_ERROR_MESSAGE, m_errorMessage);
        settings.addInt(CFG_TIMEOUT, m_timeout);
        settings.addBoolean(CFG_DISABLE_OUTPUT, m_disableOutput);
        settings.addBoolean(CFG_ALLOW_MULTIPLE_FILES, m_allowMultipleFiles);
    }

    /**
     * Loads the config from saved settings
     *
     * @param settings the settings to load from
     * @throws InvalidSettingsException
     */
    public void loadSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
        m_fileTypes = settings.getStringArray(CFG_FILE_TYPES);
        m_errorMessage = settings.getString(CFG_ERROR_MESSAGE);
        m_disableOutput = settings.getBoolean(CFG_DISABLE_OUTPUT, DEFAULT_DISABLE_OUTPUT);
        m_timeout = settings.getInt(CFG_TIMEOUT, DEFAULT_TIMEOUT);
        m_allowMultipleFiles = settings.getBoolean(CFG_ALLOW_MULTIPLE_FILES, DEFAULT_ALLOW_MULTIPLE_FILES);
    }

    /**
     * Loads the config from saved settings for dialog display
     *
     * @param settings the settings to load from
     */
    public void loadSettingsInDialog(final NodeSettingsRO settings) {
        m_fileTypes = settings.getStringArray(CFG_FILE_TYPES, DEFAULT_FILE_TYPES);
        m_errorMessage = settings.getString(CFG_ERROR_MESSAGE, DEFAULT_ERROR_MESSAGE);
        m_disableOutput = settings.getBoolean(CFG_DISABLE_OUTPUT, DEFAULT_DISABLE_OUTPUT);
        m_timeout = settings.getInt(CFG_TIMEOUT, DEFAULT_TIMEOUT);
        m_allowMultipleFiles = settings.getBoolean(CFG_ALLOW_MULTIPLE_FILES, DEFAULT_ALLOW_MULTIPLE_FILES);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString());
        sb.append(", ");
        sb.append("fileTypes=");
        sb.append(m_fileTypes);
        sb.append(", ");
        sb.append("errorMessage=");
        sb.append(m_errorMessage);
        sb.append(", ");
        sb.append("timeout=");
        sb.append(m_timeout);
        sb.append(", ");
        sb.append("disableOutput=");
        sb.append(m_disableOutput);
        sb.append(", ");
        sb.append("multiple=");
        sb.append(m_allowMultipleFiles);
        return sb.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .appendSuper(super.hashCode())
                .append(m_fileTypes)
                .append(m_errorMessage)
                .append(m_timeout)
                .append(m_disableOutput)
                .append(m_allowMultipleFiles)
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
        MultipleFileUploadNodeConfig other = (MultipleFileUploadNodeConfig)obj;
        return new EqualsBuilder()
                .appendSuper(super.equals(obj))
                .append(m_fileTypes, other.m_fileTypes)
                .append(m_errorMessage, other.m_errorMessage)
                .append(m_timeout, other.m_timeout)
                .append(m_disableOutput, other.m_disableOutput)
                .append(m_allowMultipleFiles, other.m_allowMultipleFiles)
                .isEquals();
    }
}
