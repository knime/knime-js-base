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
 */
@JsonAutoDetect
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public class FileUploadNodeValue extends JSONViewContent {

    protected static final String CFG_PATH = "path";
    protected static final String DEFAULT_PATH = "";
    protected String m_path = DEFAULT_PATH;

    protected static final String CFG_PATH_VALID = "pathValid";
    protected static final boolean DEFAULT_PATH_VALID = true;
    protected boolean m_pathValid = DEFAULT_PATH_VALID;

    protected static final String CFG_FILE_NAME = "fileName";
    protected static final String DEFAULT_FILE_NAME = "";
    protected String m_fileName = DEFAULT_FILE_NAME;

    private static final String CFG_LOCAL_UPLOAD = "localUpload";
    private static final boolean DEFAULT_LOCAL_UPLOAD = false;
    private boolean m_localUpload = DEFAULT_LOCAL_UPLOAD;

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
     * {@inheritDoc}
     */
    @Override
    @JsonIgnore
    public void saveToNodeSettings(final NodeSettingsWO settings) {
        settings.addString(CFG_PATH, getPath());
        settings.addBoolean(CFG_PATH_VALID, m_pathValid);
        settings.addString(CFG_FILE_NAME, m_fileName);
        settings.addBoolean(CFG_LOCAL_UPLOAD, m_localUpload);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @JsonIgnore
    public void loadFromNodeSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
        setPath(settings.getString(CFG_PATH));

        // added with 3.2
        setPathValid(settings.getBoolean(CFG_PATH_VALID, DEFAULT_PATH_VALID));

        // added with 4.2.2
        setFileName(settings.getString(CFG_FILE_NAME, DEFAULT_FILE_NAME));
        setLocalUpload(settings.getBoolean(CFG_LOCAL_UPLOAD));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("name=");
        sb.append(m_fileName);
        sb.append(", path=");
        sb.append(m_path);
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
                .append(m_localUpload)
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
        FileUploadNodeValue other = (FileUploadNodeValue)obj;
        return new EqualsBuilder()
                .append(m_path, other.m_path)
                .append(m_pathValid, other.m_pathValid)
                .append(m_fileName, other.m_fileName)
                .append(m_localUpload, other.m_localUpload)
                .isEquals();
    }
}
