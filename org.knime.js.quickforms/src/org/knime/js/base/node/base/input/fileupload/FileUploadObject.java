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
 *   7 Nov 2024 (knime): created
 */
package org.knime.js.base.node.base.input.fileupload;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * @author Daniel Bogenrieder, KNIME GmbH, Konstanz, Germany
 */
@JsonAutoDetect
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public class FileUploadObject {
    protected static final String CFG_PATH = "path";
    protected static final String DEFAULT_PATH = "";
    protected String m_path = DEFAULT_PATH;

    protected static final String CFG_PATH_VALID = "pathValid";
    protected static final boolean DEFAULT_PATH_VALID = true;
    protected boolean m_pathValid = DEFAULT_PATH_VALID;

    protected static final String CFG_FILE_NAME = "fileName";
    protected static final String DEFAULT_FILE_NAME = "";
    protected String m_fileName = DEFAULT_FILE_NAME;

    protected static final String CFG_FILE_SIZE = "fileSize";
    protected static final Long DEFAULT_FILE_SIZE= null;
    protected Long m_fileSize = DEFAULT_FILE_SIZE;

    protected static final String CFG_ID = "id";
    protected String m_id = "";

    public FileUploadObject() {};

    @JsonCreator
    public FileUploadObject(
        @JsonProperty("path") final String path,
        @JsonProperty("pathValid") final boolean pathValid,
        @JsonProperty("fileName") final String fileName,
        @JsonProperty("id") final String id,
        @JsonProperty("fileSize") final Long fileSize) {
        m_path = path;
        m_pathValid = pathValid;
        m_fileName = fileName;
        m_id = id;
        m_fileSize = fileSize;
    }

    /**
     * @return the path
     */
    @JsonProperty("path")
    public String getPath() {
        return m_path;
    }

    /**
     * @param path the path to set
     */
    @JsonProperty("path")
    public void setPath(final String path) {
        m_path = path;
    }

    /**
     * @return the pathValid
     */
    @JsonProperty("pathValid")
    public boolean isPathValid() {
        return m_pathValid;
    }

    /**
     * @param pathValid the pathValid to set
     */
    @JsonProperty("pathValid")
    public void setPathValid(final boolean pathValid) {
        m_pathValid = pathValid;
    }

    /**
     * @return the fileName
     */
    @JsonProperty("fileName")
    public String getFileName() {
        return m_fileName;
    }

    /**
     * @param fileName the fileName to set
     */
    @JsonProperty("fileName")
    public void setFileName(final String fileName) {
        m_fileName = fileName;
    }

    /**
     * @return the id
     */
    @JsonProperty("id")
    public String getId() {
        return m_id;
    }

    /**
     * @param id the fileName to set
     */
    @JsonProperty("id")
    public void setId(final String id) {
        m_id = id;
    }

    /**
     * @return the fileSize
     */
    @JsonProperty("fileSize")
    public Long getFileSize() {
        return m_fileSize;
    }

    /**
     * @param fileSize the fileName to set
     */
    @JsonProperty("fileSize")
    public void setFileSize(final Long fileSize) {
        m_fileSize = fileSize;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("fileName=");
        sb.append(m_fileName);
        sb.append(", path=");
        sb.append(m_path);
        sb.append(", fileSize=");
        sb.append(m_fileSize);
        sb.append(", id=");
        sb.append(m_id);
        sb.append(", pathValid=");
        sb.append(m_pathValid);
        return sb.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(m_path)
                .append(m_pathValid)
                .append(m_fileName)
                .append(m_fileSize)
                .append(m_id)
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
        FileUploadObject other = (FileUploadObject)obj;
        // Leaving out the path as otherwise for local files we compare the base64 encoded file which explodes and crashes.
        return new EqualsBuilder()
                .append(m_fileName, other.m_fileName)
                .append(m_fileSize, other.m_fileSize)
                .append(m_id, other.m_id)
                .isEquals();
    }

}
