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
 */
package org.knime.js.base.node.quickform.input.filechooser;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.NotConfigurableException;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.util.ThreadUtils;
import org.knime.js.base.node.quickform.QuickFormNodeDialog;
import org.knime.js.base.node.quickform.input.filechooser.FileChooserQuickFormConfig.SelectionType;
import org.knime.js.base.node.quickform.input.filechooser.FileChooserQuickFormValue.FileItem;
import org.knime.workbench.explorer.ExplorerMountTable;
import org.knime.workbench.explorer.dialogs.SpaceResourceSelectionDialog;
import org.knime.workbench.explorer.dialogs.Validator;
import org.knime.workbench.explorer.filesystem.AbstractExplorerFileStore;
import org.knime.workbench.explorer.view.AbstractContentProvider;
import org.knime.workbench.explorer.view.ContentObject;

/**
 * The dialog for the file chooser quick form node.
 *
 * @author Christian Albrecht, KNIME.com GmbH, Konstanz, Germany
 */
public class FileChooserQuickFormNodeDialog extends QuickFormNodeDialog implements FileStoreContainer {

    private final JCheckBox m_selectWorkflowBox;
    private final JCheckBox m_selectDirBox;
    private final JCheckBox m_selectDataFilesBox;
    private final JCheckBox m_outputTypeBox;
    private final JCheckBox m_useDefaultMountIdBox;
    private final JTextField m_customMountIdField;
    private final JTextField m_rootDirField;
    private final JButton m_rootDirChooserButton;
    private final JTextField m_defaultPathField;
    private final JCheckBox m_multipleselectCheckBox;
    private final JButton m_fileChooserButton;
    private final JTextField m_validExtensionsField;

    private final FileChooserValidator m_validator;
    private AbstractExplorerFileStore m_fileStore;
    private FileChooserQuickFormConfig m_config;

    /** Constructors, inits fields calls layout routines. */
    FileChooserQuickFormNodeDialog() {
        m_config = new FileChooserQuickFormConfig();

        m_selectWorkflowBox = new JCheckBox("Select workflows");
        m_selectWorkflowBox.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(final ChangeEvent e) {
                  m_validator.setSelectWorkflows(m_selectWorkflowBox.isSelected());
            }
        });

        m_selectDirBox = new JCheckBox("Select directories");
        m_selectDirBox.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(final ChangeEvent e) {
                m_validator.setSelectDirectories(m_selectDirBox.isSelected());
            }
        });

        m_selectDataFilesBox = new JCheckBox("Select data files");
        m_selectDataFilesBox.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(final ChangeEvent e) {
                m_validator.setSelectDataFiles(m_selectDataFilesBox.isSelected());
                m_validExtensionsField.setEnabled(m_selectDataFilesBox.isSelected());
            }
        });

        m_outputTypeBox = new JCheckBox("Output selected item type");
        m_multipleselectCheckBox = new JCheckBox("Allow multiple selection");
        m_rootDirField = new JTextField(DEF_TEXTFIELD_WIDTH);
        FileChooserValidator dirVal = new FileChooserValidator(false, true, false, null);
        String title = "Select directory";
        String description = "Please select the root directory";
        m_rootDirChooserButton = createBrowseButton(m_rootDirField, dirVal, null, title, description, true);

        m_useDefaultMountIdBox = new JCheckBox("Use default mount id of target");
        m_useDefaultMountIdBox.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(final ChangeEvent e) {
                m_customMountIdField.setEnabled(!m_useDefaultMountIdBox.isSelected());
            }
        });

        m_customMountIdField = new JTextField(DEF_TEXTFIELD_WIDTH);
        m_defaultPathField = new JTextField(DEF_TEXTFIELD_WIDTH);
        m_validator = new FileChooserValidator(m_config.getSelectWorkflows(),
            m_config.getSelectDirectories(), m_config.getSelectDataFiles(), m_config.getFileTypes());
        title = "Select file";
        description = "Please select the default file";
        m_fileChooserButton = createBrowseButton(m_defaultPathField, m_validator, this, title, description, false);

        m_validExtensionsField = new JTextField(DEF_TEXTFIELD_WIDTH);
        m_validExtensionsField.addFocusListener(new FocusListener() {
            @Override
            public void focusLost(final FocusEvent e) {
                try {
                    m_validator.setFileTypes(getFileTypes());
                } catch (Exception exc) {
                    NodeLogger.getLogger(
                        FileChooserQuickFormNodeDialog.class).debug(
                                    "Unable to update file suffixes", exc);
                }
            }

            @Override
            public void focusGained(final FocusEvent e) { /* do nothing */ }
        });
        createAndAddTab();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected final void fillPanel(final JPanel panelWithGBLayout, final GridBagConstraints gbc) {

        JPanel selectPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc2 = new GridBagConstraints();
        gbc2.insets = new Insets(0, 0, 0, 0);
        gbc2.anchor = GridBagConstraints.NORTHWEST;
        gbc2.fill = GridBagConstraints.BOTH;
        gbc2.weightx = 1;
        gbc2.weighty = 0;
        gbc2.gridx = 0;
        gbc2.gridy = 0;
        selectPanel.add(m_selectDataFilesBox, gbc2);
        gbc2.gridx++;
        selectPanel.add(m_selectWorkflowBox, gbc2);
        gbc2.gridx++;
        selectPanel.add(m_selectDirBox, gbc2);

        addPairToPanel("Selection Types:", selectPanel, panelWithGBLayout, gbc);
        addPairToPanel(" ", m_outputTypeBox, panelWithGBLayout, gbc);
        addPairToPanel("Valid File Extensions:", m_validExtensionsField, panelWithGBLayout, gbc);
        addPairToPanel(" ", m_multipleselectCheckBox, panelWithGBLayout, gbc);
        addPairToPanel(" ", new JPanel(), panelWithGBLayout, gbc);
        addPairToPanel(" ", m_useDefaultMountIdBox, panelWithGBLayout, gbc);
        addPairToPanel("Custom Mount ID:", m_customMountIdField, panelWithGBLayout, gbc);
        addPairToPanel(" ", new JPanel(), panelWithGBLayout, gbc);
        addTripelToPanel("Root Path:", m_rootDirField, m_rootDirChooserButton, panelWithGBLayout, gbc);
        addTripelToPanel("Default File:", m_defaultPathField, m_fileChooserButton, panelWithGBLayout, gbc);
    }

    static JButton createBrowseButton(final JTextField textField, final FileChooserValidator validator,
            final FileStoreContainer fileStoreContainer, final String title, final String description, final boolean remoteOnly) {
        final JButton browseButton = new JButton("Browse");
        browseButton.addActionListener(new ActionListener() {
            /**
             * {@inheritDoc}
             */
            @Override
            public void actionPerformed(final ActionEvent arg0) {
                Display.getDefault().syncExec(ThreadUtils.runnableWithContext(new Runnable() {
                    @Override
                    public void run() {
                        // collect all non-local mount ids
                        List<String> mountIDs = new ArrayList<String>();
                        for (Map.Entry<String, AbstractContentProvider> entry
                                : ExplorerMountTable
                                .getMountedContent().entrySet()) {
                            String mountID = entry.getKey();
                            AbstractContentProvider acp = entry.getValue();
                            if (remoteOnly ? acp.isRemote() && acp.canHostDataFiles() : acp.canHostDataFiles()) {
                                mountIDs.add(mountID);
                            }
                        }
                        if (mountIDs.isEmpty()) {
                            MessageBox box = new MessageBox(Display.getDefault().getActiveShell(), SWT.ICON_INFORMATION | SWT.OK);
                            box.setText("No item for selection");
                            box.setMessage("No server mountpoint was found for selection of root path. Please log into a server you want to use for browsing.");
                            box.open();
                            return;
                        }
                        ContentObject initialSelection = null;
                        AbstractExplorerFileStore selectedFileStore = null;
                        if (fileStoreContainer != null) {
                            selectedFileStore = fileStoreContainer.getFileStore();
                        }
                        if (selectedFileStore != null && selectedFileStore.toString().equals(textField.getText())) {
                            initialSelection = ContentObject.forFile(selectedFileStore);
                        }

                        SpaceResourceSelectionDialog dialog =
                                new SpaceResourceSelectionDialog(Display
                                        .getDefault().getActiveShell(),
                                        mountIDs.toArray(new String[0]), initialSelection);
                        dialog.setTitle(title);
                        dialog.setDescription(description);
                        dialog.setValidator(validator);

                        if (Window.OK == dialog.open()) {
                            AbstractExplorerFileStore selectedItem = dialog.getSelection();
                            if (fileStoreContainer != null) {
                                fileStoreContainer.setFileStore(selectedItem);
                                textField.setText(selectedItem.toString());
                            } else {
                                textField.setText(selectedItem.getFullName());
                            }
                        }
                    }
                }));
            }
        });
        return browseButton;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadSettingsFrom(final NodeSettingsRO settings, final PortObjectSpec[] specs)
            throws NotConfigurableException {
        m_config.loadSettingsInDialog(settings);
        loadSettingsFrom(m_config);

        m_selectWorkflowBox.setSelected(m_config.getSelectWorkflows());
        m_selectDirBox.setSelected(m_config.getSelectDirectories());
        m_selectDataFilesBox.setSelected(m_config.getSelectDataFiles());
        m_outputTypeBox.setSelected(m_config.getOutputType());
        m_multipleselectCheckBox.setSelected(m_config.getMultipleSelection());
        m_useDefaultMountIdBox.setSelected(m_config.getDefaultMountId());
        m_customMountIdField.setText(m_config.getCustomMountId());
        m_rootDirField.setText(m_config.getRootDir());
        String path = "";
        FileItem[] items = m_config.getDefaultValue().getItems();
        if (items != null && items.length > 0) {
            path = items[0].getPath();
        }
        m_defaultPathField.setText(path);

        String[] fileExtensions = m_config.getFileTypes();
        String text;
        if (fileExtensions == null || fileExtensions.length == 0) {
            text = "";
        } else {
            if (fileExtensions.length > 1) {
                // since 3.1 the first element should have a pattern "ext1|ext2|ext3..."
                // need to support backward compatibility
                if (fileExtensions[0].contains("|")) {
                    // 3.1
                    text = fileExtensions[0].replace('|', ',');
                } else {
                    // older version
                    text = String.join(",", fileExtensions);
                }
            } else {
                text = fileExtensions[0];
            }
        }
        m_validExtensionsField.setText(text);

        m_validator.setSelectWorkflows(m_config.getSelectWorkflows());
        m_validator.setSelectDirectories(m_config.getSelectDirectories());
        m_validator.setSelectDataFiles(m_config.getSelectDataFiles());
        m_validator.setFileTypes(getFileTypes());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AbstractExplorerFileStore getFileStore() {
        return m_fileStore;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setFileStore(final AbstractExplorerFileStore fileStore) {
        m_fileStore = fileStore;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) throws InvalidSettingsException {
        if (!m_selectWorkflowBox.isSelected() && !m_selectDirBox.isSelected() && !m_selectDataFilesBox.isSelected()){
            throw new InvalidSettingsException("No selection type chosen. Please select at least one type.");
        }
        if (m_fileStore != null && m_fileStore.toString().equals(m_defaultPathField.getText())) {
            String validationMessage = m_validator.validateSelectionValue(m_fileStore, null);
            if (validationMessage != null) {
                throw new InvalidSettingsException(validationMessage);
            }
        }
        saveSettingsTo(m_config);
        m_config.setSelectWorkflows(m_selectWorkflowBox.isSelected());
        m_config.setSelectDirectories(m_selectDirBox.isSelected());
        m_config.setSelectDataFiles(m_selectDataFilesBox.isSelected());
        m_config.setOutputType(m_outputTypeBox.isSelected());
        m_config.setMultipleSelection(m_multipleselectCheckBox.isSelected());
        m_config.setDefaultMountId(m_useDefaultMountIdBox.isSelected());
        m_config.setCustomMountId(m_customMountIdField.getText());
        m_config.setRootDir(m_rootDirField.getText());
        m_config.setFileTypes(getFileTypes());
        FileItem[] items = m_config.getDefaultValue().getItems();
        FileItem oldItem = null;
        if (items != null && items.length > 0) {
            oldItem = items[0];
        }
        SelectionType type = FileChooserQuickFormNodeDialog.getTypeForFileStore(
            m_fileStore, m_defaultPathField.getText(), oldItem);
        String path = m_defaultPathField.getText();
        FileItem defaultItem = new FileItem(path, type);
        m_config.getDefaultValue().setItems(new FileItem[]{defaultItem});
        m_config.saveSettings(settings);
    }

    static SelectionType getTypeForFileStore(final AbstractExplorerFileStore fileStore, final String path, final FileItem oldValue) {
        // determine type if possible
        SelectionType type = SelectionType.UNKNOWN;
        if (fileStore != null && fileStore.toString().equals(path)) {
            if (AbstractExplorerFileStore.isWorkflow(fileStore)) {
                type = SelectionType.WORKFLOW;
            } else if (AbstractExplorerFileStore.isWorkflowGroup(fileStore)) {
                type = SelectionType.DIRECTORY;
            } else if (AbstractExplorerFileStore.isDataFile(fileStore)) {
                type = SelectionType.DATA;
            }
        } else if (oldValue != null && StringUtils.equals(path, oldValue.getPath())) {
            // if values didn't change and determining type was not possible, take previous value
            type = SelectionType.fromString(oldValue.getType());
        }
        return type;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getValueString(final NodeSettingsRO settings) throws InvalidSettingsException {
        FileChooserQuickFormValue value = new FileChooserQuickFormValue();
        value.loadFromNodeSettings(settings);
        FileItem[] items = value.getItems();
        String valueString = null;
        if (items != null && items.length > 0) {
            StringBuilder builder = new StringBuilder(items[0].getPath());
            for (int i = 1; i < items.length; i++) {
                builder.append(",\n");
                builder.append(items[i].getPath());
            }
            valueString = builder.toString();
        }
        return valueString;
    }

    /**
     * @return String[] file types
     */
    private String[] getFileTypes() {
        String s = m_validExtensionsField.getText().trim();
        if (s.isEmpty()) {
            return new String[0];
        }
        String[] fileTypes = s.split(",");
        List<String> filteredFileTypes = new ArrayList<String>();
        for (String type : fileTypes) {
            s = type.trim();
            if (s.isEmpty()) {
                continue;
            }
            if (s.startsWith(".")) {
                filteredFileTypes.add(s);
            } else {
                filteredFileTypes.add("." + s);
            }
        }
        if (filteredFileTypes.size() == 0) {
            return new String[0];
        }
        return filteredFileTypes.toArray(new String[filteredFileTypes.size()]);
    }

    /**
     * Validate dialog selection with current settings
     */
    protected final static class FileChooserValidator extends Validator {

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
        public FileChooserValidator(final boolean selectWorkflows, final boolean selectDir, final boolean selectFiles, final String[] fileTypes) {
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
         * Validates a value against this Validator instance.
         * @param value the value to be validated
         * @return an error string or null if value validates
         */
        public String validateViewValue(final FileChooserQuickFormValue value) {
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
}