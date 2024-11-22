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

import static org.knime.js.core.settings.DialogUtil.DEF_TEXTFIELD_WIDTH;

import java.awt.GridBagConstraints;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.NotConfigurableException;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.util.FilesHistoryPanel;
import org.knime.core.node.util.FilesHistoryPanel.LocationValidation;
import org.knime.js.base.node.base.input.fileupload.FileUploadNodeUtil;
import org.knime.js.base.node.base.input.fileupload.FileUploadObject;
import org.knime.js.base.node.base.input.fileupload.MultipleFileUploadNodeConfig;
import org.knime.js.base.node.base.input.fileupload.MultipleFileUploadNodeValue;
import org.knime.js.base.node.configuration.input.fileupload.FileUploadDialogNodeValue;
import org.knime.js.base.node.quickform.input.fileupload.FileUploadQuickFormNodeDialog;
import org.knime.js.base.node.widget.FlowVariableWidgetNodeDialog;

/**
 * Node dialog for the file upload widget node
 *
 * @author Daniel Bogenrieder, KNIME GmbH, Konstanz, Germany
 */
public class MultipleFileUploadWidgetNodeDialog extends FlowVariableWidgetNodeDialog<MultipleFileUploadNodeValue> {

    private final FilesHistoryPanel m_fileHistoryPanel;
    private final JTextField m_validExtensionsField;
    private final JSpinner m_timeoutSpinner;
    private final JCheckBox m_disableOutputBox;
    private final JCheckBox m_storeInWfDirBox;
    private final JCheckBox m_allowMultipleFiles;
    private final JCheckBox m_required;

    private MultipleFileUploadInputWidgetConfig m_config;

    /** Constructors, inits fields calls layout routines. */
    MultipleFileUploadWidgetNodeDialog() {
        m_config = new MultipleFileUploadInputWidgetConfig();
        m_fileHistoryPanel = new FilesHistoryPanel("file_upload_widget", LocationValidation.FileInput);
        m_validExtensionsField = new JTextField(DEF_TEXTFIELD_WIDTH);
        m_validExtensionsField.addFocusListener(new FocusListener() {
            @Override
            public void focusLost(final FocusEvent e) {
                try {
                    m_fileHistoryPanel.setSuffixes(FileUploadNodeUtil.getFileTypes(m_validExtensionsField));
                } catch (Exception exc) {
                    NodeLogger.getLogger(FileUploadQuickFormNodeDialog.class).debug("Unable to update file suffixes",
                        exc);
                }
            }

            @Override
            public void focusGained(final FocusEvent e) {
                // nothing to do
            }
        });
        m_timeoutSpinner = new JSpinner(new SpinnerNumberModel(1.0, 0.0, Integer.MAX_VALUE, 1.0));
        m_disableOutputBox = new JCheckBox();
        m_storeInWfDirBox = new JCheckBox();
        m_allowMultipleFiles = new JCheckBox();
        m_required = new JCheckBox();
        createAndAddTab();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getValueString(final NodeSettingsRO settings) throws InvalidSettingsException {
        FileUploadDialogNodeValue value = new FileUploadDialogNodeValue();
        value.loadFromNodeSettings(settings);
        return value.getPath();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected final void fillPanel(final JPanel panelWithGBLayout, final GridBagConstraints gbc) {
        addPairToPanel("Valid File Extensions:", m_validExtensionsField, panelWithGBLayout, gbc);
        addPairToPanel("Default File:", m_fileHistoryPanel, panelWithGBLayout, gbc);
        addPairToPanel("Timeout (s): ", m_timeoutSpinner, panelWithGBLayout, gbc);
        addPairToPanel("Disable output, if file does not exist: ", m_disableOutputBox, panelWithGBLayout, gbc);
        addPairToPanel("Store uploaded file in workflow directory: ", m_storeInWfDirBox, panelWithGBLayout, gbc);
        addPairToPanel("Allow multiple file uploads: ", m_allowMultipleFiles, panelWithGBLayout, gbc);
        addPairToPanel("File upload is required: ", m_required, panelWithGBLayout, gbc);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadSettingsFrom(final NodeSettingsRO settings, final PortObjectSpec[] specs)
        throws NotConfigurableException {
        m_config.loadSettingsInDialog(settings);
        loadSettingsFrom(m_config);
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
        if (m_config.getDefaultValue().getFiles().length > 0) {
            m_fileHistoryPanel.setSelectedFile(m_config.getDefaultValue().getFiles()[0].getPath());
        } else {
            m_fileHistoryPanel.setSelectedFile("");
        }
        m_fileHistoryPanel.setSuffixes(FileUploadNodeUtil.getFileTypes(m_validExtensionsField));
        m_timeoutSpinner.setValue((double)m_config.getTimeout() / 1000);
        m_disableOutputBox.setSelected(m_config.getDisableOutput());
        m_storeInWfDirBox.setSelected(m_config.isStoreInWfDir());
        m_allowMultipleFiles.setSelected(m_config.getFileUploadConfig().isMultipleFiles());
        m_required.setSelected(m_config.getLabelConfig().isRequired());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) throws InvalidSettingsException {
        saveSettingsTo(m_config);
        MultipleFileUploadNodeConfig fileUploadConfig = m_config.getFileUploadConfig();
        fileUploadConfig.setFileTypes(FileUploadNodeUtil.getFileTypes(m_validExtensionsField));
        fileUploadConfig.setTimeout((int)((double)m_timeoutSpinner.getValue() * 1000));
        fileUploadConfig.setDisableOutput(m_disableOutputBox.isSelected());
        String selectedFile = m_fileHistoryPanel.getSelectedFile();
        var files = new FileUploadObject[1];
        java.nio.file.Path path = Paths.get(selectedFile);
        try {
            var file = new File(selectedFile);
            if (file.exists()) {
                var size = Files.size(path);
                files[0] = new FileUploadObject(selectedFile, true,
                    FileUploadNodeUtil.getFileNameFromPath(selectedFile), "", size);
            } else {
                files = new FileUploadObject[0];
            }
        } catch (IOException e) {
            files = new FileUploadObject[0];
        }
        m_config.getDefaultValue().setFiles(files);
        m_config.setStoreInWfDir(m_storeInWfDirBox.isSelected());
        m_config.getFileUploadConfig().setMultipleFileMode(m_allowMultipleFiles.isSelected());
        m_config.getLabelConfig().setRequired(m_required.isSelected());
        m_config.saveSettings(settings);
    }

}
