/*
 * ------------------------------------------------------------------------
 *  Copyright by KNIME GmbH, Konstanz, Germany
 *  Website: http://www.knime.org; Email: contact@knime.org
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
 *
 * History
 *   Oct 14, 2013 (Patrick Winter, KNIME.com AG, Zurich, Switzerland): created
 */
package org.knime.js.base.node.quickform.input.filechooser;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.knime.core.node.InvalidSettingsException;
import org.knime.js.base.node.quickform.QuickFormDialogPanel;
import org.knime.js.base.node.quickform.QuickFormNodeDialog;
import org.knime.js.base.node.quickform.input.filechooser.FileChooserQuickFormConfig.SelectionType;
import org.knime.js.base.node.quickform.input.filechooser.FileChooserQuickFormNodeDialog.FileChooserValidator;
import org.knime.js.base.node.quickform.input.filechooser.FileChooserQuickFormValue.FileItem;
import org.knime.workbench.explorer.filesystem.AbstractExplorerFileStore;

/**
 * The sub node dialog panel for the file upload quick form node.
 *
 * @author Christian Albrecht, KNIME.com GmbH, Konstanz, Germany
 */
@SuppressWarnings("serial")
public class FileChooserQuickFormDialogPanel extends QuickFormDialogPanel<FileChooserQuickFormValue> implements FileStoreContainer {

    private final FileChooserValidator m_validator;
    private final JTextField m_defaultPathField;
    private final JButton m_fileChooserButton;
    private AbstractExplorerFileStore m_fileStore;
    private FileChooserQuickFormValue m_value;

    /**
     * @param representation The dialog representation
     *
     */
    public FileChooserQuickFormDialogPanel(final FileChooserQuickFormRepresentation representation) {
        super(representation.getDefaultValue());
        m_validator = new FileChooserValidator(representation.getSelectWorkflows(),
            representation.getSelectDirectories(), representation.getSelectDataFiles(), representation.getFileTypes());
        JPanel panel = new JPanel(new GridBagLayout());
        m_defaultPathField = new JTextField(QuickFormNodeDialog.DEF_TEXTFIELD_WIDTH);
        FileItem[] items = representation.getDefaultValue().getItems();
        if (items != null && items.length > 0) {
            m_defaultPathField.setText(items[0].getPath());
        }
        String title = "Select file";
        String description = "Please select the file to override the default";
        m_fileChooserButton = FileChooserQuickFormNodeDialog.createBrowseButton(m_defaultPathField, m_validator, this, title, description, false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.weightx = 1;
        panel.add(m_defaultPathField, gbc);
        gbc.weightx = 0;
        panel.add(m_fileChooserButton, gbc);
        setComponent(panel);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FileChooserQuickFormValue createNodeValue() throws InvalidSettingsException {
        FileChooserQuickFormValue value = new FileChooserQuickFormValue();
        String path = m_defaultPathField.getText();
        FileChooserQuickFormValue oldValue = m_value == null ? getDefaultValue() : m_value;
        FileItem[] items = oldValue.getItems();
        FileItem oldItem = new FileItem(null, SelectionType.UNKNOWN);
        if (items != null && items.length == 1) {
            oldItem = items[0];
        }
        SelectionType type = FileChooserQuickFormNodeDialog.getTypeForFileStore(
            m_fileStore, m_defaultPathField.getText(), oldItem);
        FileItem item = new FileItem(path, type);
        value.setItems(new FileItem[]{item});
        return value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadNodeValue(final FileChooserQuickFormValue value) {
        super.loadNodeValue(value);
        m_value = value;
        if (value != null && value.getItems() != null && value.getItems().length > 0) {
            setPath(value.getItems()[0].getPath());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setEnabled(final boolean enabled) {
        super.setEnabled(enabled);
        m_defaultPathField.setEnabled(enabled);
        m_fileChooserButton.setEnabled(enabled);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void resetToDefault() {
        String path = "";
        FileItem[] items = getDefaultValue().getItems();
        if (items != null && items.length > 0) {
            path = items[0].getPath();
        }
        setPath(path);
    }

    private void setPath(final String path) {
        m_defaultPathField.setText(path);
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
