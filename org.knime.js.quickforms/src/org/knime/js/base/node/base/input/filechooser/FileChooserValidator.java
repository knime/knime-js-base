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

import org.knime.js.base.node.base.input.filechooser.FileChooserNodeConfig.SelectionType;
import org.knime.js.base.node.base.input.filechooser.FileChooserNodeValue.FileItem;
import org.knime.workbench.explorer.dialogs.Validator;
import org.knime.workbench.explorer.filesystem.AbstractExplorerFileStore;

/**
 * Validate file chooser dialog selection with current settings
 *
 * @author Christian Albrecht, KNIME GmbH, Konstanz, Germany
 */
public class FileChooserValidator extends Validator {

    private boolean m_selectWorkflows;
    private boolean m_selectDirectories;
    private boolean m_selectDataFiles;
    private String[] m_fileTypes;

    /**
     * Creates new validator using the defaults provided.
     * @param selectWorkflows true, if workflow are allowed to be selectable
     * @param selectDir true, if directories are allowed to be selectable
     * @param selectFiles true, if data files are allowed to be selectable
     * @param fileTypes optional array of file types to be choosable, when data files are selectable
     */
    public FileChooserValidator(final boolean selectWorkflows, final boolean selectDir, final boolean selectFiles,
        final String[] fileTypes) {
        m_selectWorkflows = selectWorkflows;
        m_selectDirectories = selectDir;
        m_selectDataFiles = selectFiles;
        m_fileTypes = fileTypes;
    }

    /**
     * @return the selectWorkflows
     */
    public boolean isSelectWorkflows() {
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
    public boolean isSelectDirectories() {
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
    public boolean isSelectDataFiles() {
        return m_selectDataFiles;
    }

    /**
     * @param selectDataFiles the selectDataFiles to set
     */
    public void setSelectDataFiles(final boolean selectDataFiles) {
        m_selectDataFiles = selectDataFiles;
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
     * Validates a file item against this Validator instance.
     * @param item the item to be validated
     * @return an error string or null if item validates
     */
    public String validateFileItem(final FileItem item) {
        String allowedTypes = buildErrorStringAllowedTypes();
        // check workflow type
        if (SelectionType.WORKFLOW == item.getSelectionType() && !isSelectWorkflows()) {
            return "Workflows can not be selected. " + allowedTypes;
        }
        // check workflow group type
        if (SelectionType.DIRECTORY == item.getSelectionType() && !isSelectDirectories()) {
            return "Workflow groups or directories can not be selected. " + allowedTypes;
        }
        // check file type
        if (SelectionType.DATA == item.getSelectionType()) {
            if (!isSelectDataFiles()) {
                return "Data files can not be selected. " + allowedTypes.toString();
            } else {
                if (getFileTypes() != null && getFileTypes().length > 0) {
                    // check allowed file extensions
                    int index = item.getPath().lastIndexOf(".");
                    boolean found = false;
                    String ext = null;
                    if (index >= 0 && index < item.getPath().length() - 1) {
                        ext = item.getPath().substring(index);
                        for (String validExt : getFileTypes()) {
                            if (validExt.equals(ext)) {
                                found = true;
                                break;
                            }
                        }
                    }
                    if (!found) {
                        String allowedFiles = "Only " + String.join(", ", getFileTypes()) + " allowed.";
                        if (ext != null) {
                            return ext + " is not a valid file extension. " + allowedFiles;
                        } else {
                            return "File extension of selected data file not valid. " + allowedFiles;
                        }
                    }
                }
            }
        }

        // all checks passed
        return null;

    }

    /**
     * Validates a value against this validator instance.
     * @param value the value to be validated
     * @return an error string or null if value validates
     */
    public String validateViewValue(final FileChooserNodeValue value) {
        FileItem[] items = value.getItems();
        if (items == null || items.length <= 0) {
            return "No file item present";
        }
        for (FileItem item : items) {
            String validationResult = validateFileItem(item);
            if (validationResult != null) {
                return validationResult;
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String validateSelectionValue(final AbstractExplorerFileStore selection, final String name) {
        boolean isWf = AbstractExplorerFileStore.isWorkflow(selection);
        boolean isDir = AbstractExplorerFileStore.isWorkflowGroup(selection);
        boolean isFile = AbstractExplorerFileStore.isDataFile(selection);

        // check unknown type
        if (!isWf && !isDir && !isFile) {
            return "Item can not be selected. " + buildErrorStringAllowedTypes();
        }
        // get type
        SelectionType type = SelectionType.UNKNOWN;
        if (isWf) {
            type = SelectionType.WORKFLOW;
        }
        if (isDir) {
            type = SelectionType.DIRECTORY;
        }
        if (isFile) {
            type = SelectionType.DATA;
        }
        FileItem tempItem = new FileItem(selection.getName(), type);
        return validateFileItem(tempItem);
    }

    private String buildErrorStringAllowedTypes() {
        StringBuilder allowedTypes = new StringBuilder();
        allowedTypes.append("Only ");
        if (isSelectWorkflows()) {
            allowedTypes.append("workflows");
        }
        if (isSelectDirectories()) {
            if (isSelectWorkflows()) {
                if (isSelectDataFiles()) {
                    allowedTypes.append(", ");
                } else {
                    allowedTypes.append(" and ");
                }
            }
            allowedTypes.append("directories");
        }
        if (isSelectDataFiles()) {
            if (isSelectWorkflows() || isSelectDirectories()) {
                allowedTypes.append(" and ");
            }
            allowedTypes.append("data files");
        }
        allowedTypes.append(" allowed.");
        return allowedTypes.toString();
    }
}
