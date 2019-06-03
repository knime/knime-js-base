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
package org.knime.js.base.node.configuration.input.filechooser;

import static org.knime.js.base.node.base.input.filechooser.FileChooserDialogUtil.createBrowseButton;
import static org.knime.js.base.node.base.input.filechooser.FileChooserDialogUtil.getFileTypes;
import static org.knime.js.base.node.base.input.filechooser.FileChooserDialogUtil.getTypeForFileStore;
import static org.knime.js.core.settings.DialogUtil.DEF_TEXTFIELD_WIDTH;
import static org.knime.js.core.settings.DialogUtil.addTripelToPanel;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.NotConfigurableException;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.js.base.node.base.input.filechooser.FileChooserNodeConfig;
import org.knime.js.base.node.base.input.filechooser.FileChooserNodeConfig.SelectionType;
import org.knime.js.base.node.base.input.filechooser.FileChooserNodeValue.FileItem;
import org.knime.js.base.node.base.input.filechooser.FileChooserValidator;
import org.knime.js.base.node.base.input.filechooser.FileStoreContainer;
import org.knime.js.base.node.configuration.FlowVariableDialogNodeNodeDialog;
import org.knime.workbench.explorer.filesystem.AbstractExplorerFileStore;

/**
 * The dialog for the file chooser configuration node
 *
 * @author Christian Albrecht, KNIME GmbH, Konstanz, Germany
 */
public class FileChooserDialogNodeNodeDialog extends FlowVariableDialogNodeNodeDialog<FileChooserDialogNodeValue>
    implements FileStoreContainer {

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
    private final FileChooserInputDialogNodeConfig m_config;

    /**
     * Constructor, inits fields calls layout routines
     */
    public FileChooserDialogNodeNodeDialog() {
        m_config = new FileChooserInputDialogNodeConfig();

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
        FileChooserNodeConfig fileChooserConfig = m_config.getFileChooserConfig();
        m_validator =
            new FileChooserValidator(fileChooserConfig.getSelectWorkflows(), fileChooserConfig.getSelectDirectories(),
                fileChooserConfig.getSelectDataFiles(), fileChooserConfig.getFileTypes());
        title = "Select file";
        description = "Please select the default file";
        m_fileChooserButton = createBrowseButton(m_defaultPathField, m_validator, this, title, description, false);

        m_validExtensionsField = new JTextField(DEF_TEXTFIELD_WIDTH);
        m_validExtensionsField.addFocusListener(new FocusListener() {
            @Override
            public void focusLost(final FocusEvent e) {
                try {
                    m_validator.setFileTypes(getFileTypes(m_validExtensionsField.getText()));
                } catch (Exception exc) {
                    NodeLogger.getLogger(
                        FileChooserDialogNodeNodeDialog.class).debug("Unable to update file suffixes", exc);
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
    protected String getValueString(final NodeSettingsRO settings) throws InvalidSettingsException {
        FileChooserDialogNodeValue value = new FileChooserDialogNodeValue();
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
     * {@inheritDoc}
     */
    @Override
    protected void fillPanel(final JPanel panelWithGBLayout, final GridBagConstraints gbc) {
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

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadSettingsFrom(final NodeSettingsRO settings, final PortObjectSpec[] specs) throws NotConfigurableException {
        m_config.loadSettingsInDialog(settings);
        loadSettingsFrom(m_config);
        FileChooserNodeConfig fileChooserConfig = m_config.getFileChooserConfig();
        m_selectWorkflowBox.setSelected(fileChooserConfig.getSelectWorkflows());
        m_selectDirBox.setSelected(fileChooserConfig.getSelectDirectories());
        m_selectDataFilesBox.setSelected(fileChooserConfig.getSelectDataFiles());
        m_outputTypeBox.setSelected(fileChooserConfig.getOutputType());
        m_multipleselectCheckBox.setSelected(fileChooserConfig.getMultipleSelection());
        m_useDefaultMountIdBox.setSelected(fileChooserConfig.getDefaultMountId());
        m_customMountIdField.setText(fileChooserConfig.getCustomMountId());
        m_rootDirField.setText(fileChooserConfig.getRootDir());
        String path = "";
        FileItem[] items = m_config.getDefaultValue().getItems();
        if (items != null && items.length > 0) {
            path = items[0].getPath();
        }
        m_defaultPathField.setText(path);

        String[] fileExtensions = fileChooserConfig.getFileTypes();
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

        m_validator.setSelectWorkflows(fileChooserConfig.getSelectWorkflows());
        m_validator.setSelectDirectories(fileChooserConfig.getSelectDirectories());
        m_validator.setSelectDataFiles(fileChooserConfig.getSelectDataFiles());
        m_validator.setFileTypes(getFileTypes(m_validExtensionsField.getText()));
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
        FileChooserNodeConfig fileChooserConfig = m_config.getFileChooserConfig();
        fileChooserConfig.setSelectWorkflows(m_selectWorkflowBox.isSelected());
        fileChooserConfig.setSelectDirectories(m_selectDirBox.isSelected());
        fileChooserConfig.setSelectDataFiles(m_selectDataFilesBox.isSelected());
        fileChooserConfig.setOutputType(m_outputTypeBox.isSelected());
        fileChooserConfig.setMultipleSelection(m_multipleselectCheckBox.isSelected());
        fileChooserConfig.setDefaultMountId(m_useDefaultMountIdBox.isSelected());
        fileChooserConfig.setCustomMountId(m_customMountIdField.getText());
        fileChooserConfig.setRootDir(m_rootDirField.getText());
        fileChooserConfig.setFileTypes(getFileTypes(m_validExtensionsField.getText()));
        FileItem[] items = m_config.getDefaultValue().getItems();
        FileItem oldItem = null;
        if (items != null && items.length > 0) {
            oldItem = items[0];
        }
        SelectionType type = getTypeForFileStore(m_fileStore, m_defaultPathField.getText(), oldItem);
        String path = m_defaultPathField.getText();
        FileItem defaultItem = new FileItem(path, type);
        m_config.getDefaultValue().setItems(new FileItem[]{defaultItem});
        m_config.saveSettings(settings);
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

}
