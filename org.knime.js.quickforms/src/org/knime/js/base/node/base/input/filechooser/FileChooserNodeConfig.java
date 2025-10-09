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
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;

/**
 * Base config file for the file chooser configuration and widget nodes
 *
 * @author Christian Albrecht, KNIME GmbH, Konstanz, Germany
 */
public class FileChooserNodeConfig {

    public static final String CFG_SELECT_WORKFLOWS = "selectWorkflows";
    public static final boolean DEFAULT_SELECT_WORKFLOWS = false;
    private boolean m_selectWorkflows = DEFAULT_SELECT_WORKFLOWS;

    public static final String CFG_SELECT_DIRECTORIES = "selectDirectories";
    public static final boolean DEFAULT_SELECT_DIRECTORIES = false;
    private boolean m_selectDirectories = DEFAULT_SELECT_DIRECTORIES;

    public static final String CFG_SELECT_DATAFILES = "selectDataFiles";
    public static final boolean DEFAULT_SELECT_DATAFILES = true;
    private boolean m_selectDataFiles = DEFAULT_SELECT_DATAFILES;

    public static final String CFG_OUTPUT_TYPE = "outputType";
    public static final boolean DEFAULT_OUTPUT_TYPE = false;
    private boolean m_outputType = DEFAULT_OUTPUT_TYPE;

    public static final String CFG_ROOT_DIR = "rootDir";
    public static final String DEFAULT_ROOT_DIR = "";
    private String m_rootDir = DEFAULT_ROOT_DIR;

    public static final String CFG_DEFAULT_MOUNTID = "defaultMountId";
    public static final boolean DEFAULT_DEFAULT_MOUNTID = true;
    private boolean m_defaultMountId = DEFAULT_DEFAULT_MOUNTID;

    public static final String CFG_CUSTOM_MOUNTID = "customMountId";
    public static final String DEFAULT_CUSTOM_MOUNTID = "";
    private String m_customMountId = DEFAULT_CUSTOM_MOUNTID;

    public static final String CFG_FILE_TYPES = "types";
    private static final String[] DEFAULT_FILE_TYPES = new String[0];
    private String[] m_fileTypes = DEFAULT_FILE_TYPES;

    public static final String CFG_MULTIPLE_SELECTION = "multipleSelection";
    public static final boolean DEFAULT_MULTIPLE_SELECTION = false;
    private boolean m_multipleSelection = DEFAULT_MULTIPLE_SELECTION;

    public static final String CFG_ERROR_MESSAGE = "error_message";
    public static final String DEFAULT_ERROR_MESSAGE = "";
    private String m_errorMessage = DEFAULT_ERROR_MESSAGE;

    public static enum SelectionType {

        UNKNOWN("UNKNOWN"),
        WORKFLOW("WORKFLOW"),
        DIRECTORY("DIRECTORY"),
        DATA("DATA");

        private final String m_name;

        SelectionType(final String name) {
            m_name = name;
        }

        @Override
        public String toString() {
            return m_name;
        }

        public static SelectionType fromString(final String selectionType) {
            if (selectionType == null) {
                return UNKNOWN;
            }
            switch(selectionType) {
                case "WORKFLOW": return WORKFLOW;
                case "DIRECTORY": return DIRECTORY;
                case "DATA": return DATA;
                default: return UNKNOWN;
            }
        }
    }

    /**
     * @return the selectWorkflows
     */
    public boolean getSelectWorkflows() {
        return m_selectWorkflows;
    }

    /**
     * @param selectWorkflows the selectWorkflows to set
     */
    public void setSelectWorkflows(final boolean selectWorkflows) {
        m_selectWorkflows = selectWorkflows;
    }

    /**
     * @return the selectDirectories
     */
    public boolean getSelectDirectories() {
        return m_selectDirectories;
    }

    /**
     * @param selectDirectories the selectDirectories to set
     */
    public void setSelectDirectories(final boolean selectDirectories) {
        m_selectDirectories = selectDirectories;
    }

    /**
     * @return the selectDataFiles
     */
    public boolean getSelectDataFiles() {
        return m_selectDataFiles;
    }

    /**
     * @param selectDataFiles the selectDataFiles to set
     */
    public void setSelectDataFiles(final boolean selectDataFiles) {
        m_selectDataFiles = selectDataFiles;
    }

    /**
     * @return the outputType
     */
    public boolean getOutputType() {
        return m_outputType;
    }

    /**
     * @param outputType the outputType to set
     */
    public void setOutputType(final boolean outputType) {
        m_outputType = outputType;
    }

    /**
     * @return the rootDir
     */
    public String getRootDir() {
        return m_rootDir;
    }

    /**
     * @param rootDir the rootDir to set
     */
    public void setRootDir(final String rootDir) {
        m_rootDir = rootDir;
    }

    /**
     * @return the defaultMountId
     */
    public boolean getDefaultMountId() {
        return m_defaultMountId;
    }

    /**
     * @param defaultMountId the defaultMountId to set
     */
    public void setDefaultMountId(final boolean defaultMountId) {
        m_defaultMountId = defaultMountId;
    }

    /**
     * @return the customMountId
     */
    public String getCustomMountId() {
        return m_customMountId;
    }

    /**
     * @param customMountId the customMountId to set
     */
    public void setCustomMountId(final String customMountId) {
        m_customMountId = customMountId;
    }

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
     * @return the multipleSelection
     */
    public boolean getMultipleSelection() {
        return m_multipleSelection;
    }

    /**
     * @param multipleSelection the multipleSelection to set
     */
    public void setMultipleSelection(final boolean multipleSelection) {
        m_multipleSelection = multipleSelection;
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
     * Saves the current settings
     *
     * @param settings the settings to save to
     */
    public void saveSettings(final NodeSettingsWO settings) {
        settings.addBoolean(CFG_SELECT_WORKFLOWS, m_selectWorkflows);
        settings.addBoolean(CFG_SELECT_DIRECTORIES, m_selectDirectories);
        settings.addBoolean(CFG_SELECT_DATAFILES, m_selectDataFiles);
        settings.addBoolean(CFG_OUTPUT_TYPE, m_outputType);
        settings.addString(CFG_ROOT_DIR, m_rootDir);
        settings.addBoolean(CFG_DEFAULT_MOUNTID, m_defaultMountId);
        settings.addString(CFG_CUSTOM_MOUNTID, m_customMountId);
        settings.addStringArray(CFG_FILE_TYPES, m_fileTypes);
        settings.addBoolean(CFG_MULTIPLE_SELECTION, m_multipleSelection);
        settings.addString(CFG_ERROR_MESSAGE, m_errorMessage);
    }

    /**
     * Loads the config from saved settings
     *
     * @param settings the settings to load from
     * @throws InvalidSettingsException
     */
    public void loadSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
        m_selectWorkflows = settings.getBoolean(CFG_SELECT_WORKFLOWS);
        m_selectDirectories = settings.getBoolean(CFG_SELECT_DIRECTORIES);
        m_selectDataFiles = settings.getBoolean(CFG_SELECT_DATAFILES);
        m_outputType = settings.getBoolean(CFG_OUTPUT_TYPE);
        m_rootDir = settings.getString(CFG_ROOT_DIR);
        m_defaultMountId = settings.getBoolean(CFG_DEFAULT_MOUNTID);
        m_customMountId = settings.getString(CFG_CUSTOM_MOUNTID);
        m_fileTypes = settings.getStringArray(CFG_FILE_TYPES);
        m_multipleSelection = settings.getBoolean(CFG_MULTIPLE_SELECTION);
        m_errorMessage = settings.getString(CFG_ERROR_MESSAGE);
    }

    /**
     * Loads the config from saved settings for dialog display
     *
     * @param settings the settings to load from
     */
    public void loadSettingsInDialog(final NodeSettingsRO settings) {
        m_selectWorkflows = settings.getBoolean(CFG_SELECT_WORKFLOWS, DEFAULT_SELECT_WORKFLOWS);
        m_selectDirectories = settings.getBoolean(CFG_SELECT_DIRECTORIES, DEFAULT_SELECT_DIRECTORIES);
        m_selectDataFiles = settings.getBoolean(CFG_SELECT_DATAFILES, DEFAULT_SELECT_DATAFILES);
        m_outputType = settings.getBoolean(CFG_OUTPUT_TYPE, DEFAULT_OUTPUT_TYPE);
        m_rootDir = settings.getString(CFG_ROOT_DIR, DEFAULT_ROOT_DIR);
        m_defaultMountId = settings.getBoolean(CFG_DEFAULT_MOUNTID, DEFAULT_DEFAULT_MOUNTID);
        m_customMountId = settings.getString(CFG_CUSTOM_MOUNTID, DEFAULT_CUSTOM_MOUNTID);
        m_fileTypes = settings.getStringArray(CFG_FILE_TYPES, DEFAULT_FILE_TYPES);
        m_multipleSelection = settings.getBoolean(CFG_MULTIPLE_SELECTION, DEFAULT_MULTIPLE_SELECTION);
        m_errorMessage = settings.getString(CFG_ERROR_MESSAGE, DEFAULT_ERROR_MESSAGE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("selectWorkflows=");
        sb.append(m_selectWorkflows);
        sb.append(", ");
        sb.append("selectDirectories=");
        sb.append(m_selectDirectories);
        sb.append(", ");
        sb.append("selectDataFiles=");
        sb.append(m_selectDataFiles);
        sb.append(", ");
        sb.append("outputType=");
        sb.append(m_outputType);
        sb.append(", ");
        sb.append("rootDir=");
        sb.append(m_rootDir);
        sb.append(", ");
        sb.append("useDefaultMountId=");
        sb.append(m_defaultMountId);
        sb.append(", ");
        sb.append("customMountId=");
        sb.append(m_customMountId);
        sb.append(", ");
        sb.append("fileTypes=");
        sb.append(m_fileTypes);
        sb.append(", ");
        sb.append("multipleSelection=");
        sb.append(m_multipleSelection);
        sb.append(", ");
        sb.append("errorMessage=");
        sb.append(m_errorMessage);
        sb.append(", ");
        return sb.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(m_selectWorkflows)
                .append(m_selectDirectories)
                .append(m_selectDataFiles)
                .append(m_outputType)
                .append(m_rootDir)
                .append(m_defaultMountId)
                .append(m_customMountId)
                .append(m_fileTypes)
                .append(m_multipleSelection)
                .append(m_errorMessage)
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
        FileChooserNodeConfig other = (FileChooserNodeConfig)obj;
        return new EqualsBuilder()
                .append(m_selectWorkflows, other.m_selectWorkflows)
                .append(m_selectDirectories, other.m_selectDirectories)
                .append(m_selectDataFiles, other.m_selectDataFiles)
                .append(m_outputType, other.m_outputType)
                .append(m_rootDir, other.m_rootDir)
                .append(m_defaultMountId, other.m_defaultMountId)
                .append(m_customMountId, other.m_customMountId)
                .append(m_fileTypes, other.m_fileTypes)
                .append(m_multipleSelection, other.m_multipleSelection)
                .append(m_errorMessage, other.m_errorMessage)
                .isEquals();
    }

}
