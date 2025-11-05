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
import org.knime.js.core.JSONViewContent;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * The base value for the file upload configuration and widget node
 *
 * @author Daniel Bogenrieder, KNIME GmbH, Konstanz, Germany
 * @author Christian Albrecht, KNIME GmbH, Konstanz, Germany
 */
@JsonAutoDetect
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public class MultipleFileUploadNodeValue extends JSONViewContent {

    private static final String CFG_LOCAL_UPLOAD = "localUpload";

    public static final boolean DEFAULT_LOCAL_UPLOAD = false;

    private boolean m_localUpload = DEFAULT_LOCAL_UPLOAD;

    private static final String CFG_AMOUNT_FILES = "amountFiles";

    @JsonProperty("files")
    private FileUploadObject[] m_files;

    public static final String DEFAULT_ID = "baseFileID";

    /**
     * @return the localUpload
     */
    @JsonIgnore
    public boolean isLocalUpload() {
        return m_localUpload;
    }

    /**
     * @param localUpload the localUpload to set
     */
    @JsonIgnore
    public void setLocalUpload(final boolean localUpload) {
        m_localUpload = localUpload;
    }

    /**
     * @return the files
     */
    @JsonProperty("files")
    public FileUploadObject[] getFiles() {
        return m_files;
    }

    /**
     * @param files the files to set
     */
    @JsonProperty("files")
    public void setFiles(final FileUploadObject[] files) {
        m_files = files;
    }

    /**
     * Saves the node value to the settings.
     *
     * @param settings to save to
     * @param files the selected files to save
     * @param localUpload value to save
     */
    public static void saveSettings(final NodeSettingsWO settings, final FileUploadObject[] files,
        final boolean localUpload) {
        if (files != null) {
            final var numFiles = files.length;
            for (var i = 0; i < numFiles; i++) {
                final var file = files[i];
                var fileSettings = settings.addNodeSettings(DEFAULT_ID + i);
                fileSettings.addString(FileUploadObject.CFG_PATH, file.getPath());
                fileSettings.addBoolean(FileUploadObject.CFG_PATH_VALID, file.isPathValid());
                fileSettings.addString(FileUploadObject.CFG_FILE_NAME, file.m_fileName);
                fileSettings.addLong(FileUploadObject.CFG_FILE_SIZE, file.m_fileSize);
            }
            settings.addInt(CFG_AMOUNT_FILES, numFiles);
            settings.addBoolean(CFG_LOCAL_UPLOAD, localUpload);
        }
    }

    @Override
    @JsonIgnore
    public void saveToNodeSettings(final NodeSettingsWO settings) {
        saveSettings(settings, m_files, m_localUpload);
    }

    /**
     * Load the files from the node settings.
     *
     * @param settings to load from
     * @return the saved files
     * @throws InvalidSettingsException when the amount of files is bigger than the saved list of files
     */
    public static FileUploadObject[] loadFiles(final NodeSettingsRO settings) throws InvalidSettingsException {
        var fileAmount = settings.getInt(CFG_AMOUNT_FILES, 0);
        FileUploadObject[] files = new FileUploadObject[fileAmount];
        for (int i = 0; i < fileAmount; i++) {
            var fileUploadSettings = settings.getNodeSettings(DEFAULT_ID + i);
            var path = fileUploadSettings.getString(FileUploadObject.CFG_PATH);
            var pathValid = fileUploadSettings.getBoolean(FileUploadObject.CFG_PATH_VALID);
            var fileName = fileUploadSettings.getString(FileUploadObject.CFG_FILE_NAME);
            var fileSize = fileUploadSettings.getLong(FileUploadObject.CFG_FILE_SIZE, 0L);

            var fileUploadObject = new FileUploadObject(path, pathValid, fileName, DEFAULT_ID + i, fileSize);
            files[i] = fileUploadObject;
        }
        return files;
    }

    /**
     * Load the local upload setting from the node settings.
     *
     * @param settings to load from
     * @return the local upload settings value
     */
    public static boolean loadLocalUpload(final NodeSettingsRO settings) {
        return settings.getBoolean(CFG_LOCAL_UPLOAD, DEFAULT_LOCAL_UPLOAD);
    }

    @Override
    @JsonIgnore
    public void loadFromNodeSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
        m_files = loadFiles(settings);
        setLocalUpload(loadLocalUpload(settings));
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (FileUploadObject file : m_files) {
            sb.append("name=");
            sb.append(file.getFileName());
            sb.append(", path=");
            sb.append(file.getPath());
            sb.append(", size=");
            sb.append(file.getFileSize());
            sb.append(", id=");
            sb.append(file.getId());
        }

        return sb.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        HashCodeBuilder hcb = new HashCodeBuilder();
        for (FileUploadObject file : m_files) {
            hcb.append(file.getPath());
            hcb.append(file.isPathValid());
            hcb.append(file.getFileName());
            hcb.append(file.getFileSize());
            hcb.append(file.getId());
        }
        hcb.append(m_localUpload);
        return hcb.toHashCode();
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
        MultipleFileUploadNodeValue other = (MultipleFileUploadNodeValue)obj;
        if (FileUploadNodeUtil.checkUploadFilesEquality(m_files, other.m_files)) {
            return true;
        }
        return new EqualsBuilder().append(m_files, other.m_files).append(m_localUpload, other.m_localUpload).isEquals();
    }
}
