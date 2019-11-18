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
 *   3 Jun 2019 (albrecht): created
 */
package org.knime.js.base.node.base.input.filechooser;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.knime.js.base.node.base.LabeledConfig;
import org.knime.js.base.node.base.LabeledNodeRepresentation;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * The base representation for the file chooser configuration and widget node
 *
 * @author Christian Albrecht, KNIME GmbH, Konstanz, Germany
 * @param <VAL> the value implementation of the node
 */
@JsonAutoDetect
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public class FileChooserNodeRepresentation<VAL extends FileChooserNodeValue> extends LabeledNodeRepresentation<VAL> {

    private final boolean m_selectWorkflows;
    private final boolean m_selectDirectories;
    private final boolean m_selectDataFiles;
    private final boolean m_useDefaultMountId;
    private final String m_customMountId;
    private final String m_rootDir;
    private final String[] m_fileTypes;
    private final boolean m_multipleSelection;
    private final String m_errorMessage;
    private Object m_tree;

    @JsonCreator
    protected FileChooserNodeRepresentation(@JsonProperty("label") final String label,
        @JsonProperty("description") final String description,
        @JsonProperty("required") final boolean required,
        @JsonProperty("defaultValue") final VAL defaultValue,
        @JsonProperty("currentValue") final VAL currentValue,
        @JsonProperty("selectWorkflows") final boolean selectWorkflows,
        @JsonProperty("selectDirectories") final boolean selectDirectories,
        @JsonProperty("selectDataFiles") final boolean selectDataFiles,
        @JsonProperty("useDefaultMountId") final boolean useDefaultMountId,
        @JsonProperty("customMountId") final String customMountId,
        @JsonProperty("rootDir") final String rootDir,
        @JsonProperty("fileTypes") final String[] fileTypes,
        @JsonProperty("multipleSelection") final boolean multipleSelection,
        @JsonProperty("errorMessage") final String errorMessage,
        @JsonProperty("tree") final Object tree) {
        super(label, description, required, defaultValue, currentValue);
        m_selectWorkflows = selectWorkflows;
        m_selectDirectories = selectDirectories;
        m_selectDataFiles = selectDataFiles;
        m_useDefaultMountId = useDefaultMountId;
        m_customMountId = customMountId;
        m_rootDir = rootDir;
        m_fileTypes = fileTypes;
        m_multipleSelection = multipleSelection;
        m_errorMessage = errorMessage;
        m_tree = tree;
    }

    /**
     * @param currentValue The value currently used by the node
     * @param defaultValue The default value of the node
     * @param fileChooserConfig The config of the node
     * @param labelConfig The label config of the node
     */
    public FileChooserNodeRepresentation(final VAL currentValue, final VAL defaultValue,
        final FileChooserNodeConfig fileChooserConfig, final LabeledConfig labelConfig) {
        super(currentValue, defaultValue, labelConfig);
        m_selectWorkflows = fileChooserConfig.getSelectWorkflows();
        m_selectDirectories = fileChooserConfig.getSelectDirectories();
        m_selectDataFiles = fileChooserConfig.getSelectDataFiles();
        m_useDefaultMountId = fileChooserConfig.getDefaultMountId();
        m_customMountId = fileChooserConfig.getCustomMountId();
        m_rootDir = fileChooserConfig.getRootDir();
        m_fileTypes = fileChooserConfig.getFileTypes();
        m_multipleSelection = fileChooserConfig.getMultipleSelection();
        m_errorMessage = fileChooserConfig.getErrorMessage();
    }

    /**
     * @return the selectWorkflows
     */
    @JsonProperty("selectWorkflows")
    public boolean getSelectWorkflows() {
        return m_selectWorkflows;
    }

    /**
     * @return the selectDirectories
     */
    @JsonProperty("selectDirectories")
    public boolean getSelectDirectories() {
        return m_selectDirectories;
    }

    /**
     * @return the selectDataFiles
     */
    @JsonProperty("selectDataFiles")
    public boolean getSelectDataFiles() {
        return m_selectDataFiles;
    }

    /**
     * @return the useDefaultMountId
     */
    @JsonProperty("useDefaultMountId")
    public boolean getUseDefaultMountId() {
        return m_useDefaultMountId;
    }

    /**
     * @return the customMountId
     */
    @JsonProperty("customMountId")
    public String getCustomMountId() {
        return m_customMountId;
    }

    /**
     * @return the rootDir
     */
    @JsonProperty("rootDir")
    public String getRootDir() {
        return m_rootDir;
    }

    /**
     * @return the fileTypes
     */
    @JsonProperty("fileTypes")
    public String[] getFileTypes() {
        return m_fileTypes;
    }

    /**
     * @return if multipleSelection enabled
     */
    @JsonProperty("multipleSelection")
    public boolean getMultipleSelection() {
        return m_multipleSelection;
    }

    /**
     * @return the errorMessage
     */
    @JsonProperty("errorMessage")
    public String getErrorMessage() {
        return m_errorMessage;
    }

    /**
     * @return the tree
     */
    @JsonProperty("tree")
    public Object getTree() {
        return m_tree;
    }

    /**
     * @param tree the tree to set
     */
    @JsonProperty("tree")
    public void setTree(final Object tree) {
        m_tree = tree;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString());
        sb.append(", ");
        sb.append("selectWorkflows=");
        sb.append(m_selectWorkflows);
        sb.append(", ");
        sb.append("selectDirectories=");
        sb.append(m_selectDirectories);
        sb.append(", ");
        sb.append("selectDataFiles=");
        sb.append(m_selectDataFiles);
        sb.append(", ");
        sb.append("useDefaultMountId=");
        sb.append(m_useDefaultMountId);
        sb.append(", ");
        sb.append("customMountId=");
        sb.append(m_customMountId);
        sb.append(", ");
        sb.append("rootDir=");
        sb.append(m_rootDir);
        sb.append(", ");
        sb.append("fileTypes=");
        sb.append(m_fileTypes);
        sb.append(", ");
        sb.append("multipleSelection=");
        sb.append(m_multipleSelection);
        sb.append(", ");
        sb.append("errorMessage=");
        sb.append(m_errorMessage);
        return sb.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder().appendSuper(super.hashCode())
                .append(m_selectWorkflows)
                .append(m_selectDirectories)
                .append(m_selectDataFiles)
                .append(m_useDefaultMountId)
                .append(m_customMountId)
                .append(m_rootDir)
                .append(m_fileTypes)
                .append(m_multipleSelection)
                .append(m_errorMessage)
                .append(m_tree)
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
        @SuppressWarnings("unchecked")
        FileChooserNodeRepresentation<VAL> other = (FileChooserNodeRepresentation<VAL>)obj;
        return new EqualsBuilder().appendSuper(super.equals(obj))
                .append(m_selectWorkflows, other.m_selectWorkflows)
                .append(m_selectDirectories, other.m_selectDirectories)
                .append(m_selectDataFiles, other.m_selectDataFiles)
                .append(m_useDefaultMountId, other.m_useDefaultMountId)
                .append(m_customMountId, other.m_customMountId)
                .append(m_rootDir, other.m_rootDir)
                .append(m_fileTypes, other.m_fileTypes)
                .append(m_multipleSelection, other.m_multipleSelection)
                .append(m_errorMessage, other.m_errorMessage)
                .isEquals();
    }

}
