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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JTextField;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.knime.core.util.ThreadUtils;
import org.knime.js.base.node.base.input.filechooser.FileChooserNodeConfig.SelectionType;
import org.knime.js.base.node.base.input.filechooser.FileChooserNodeValue.FileItem;
import org.knime.workbench.explorer.ExplorerMountTable;
import org.knime.workbench.explorer.dialogs.SpaceResourceSelectionDialog;
import org.knime.workbench.explorer.filesystem.AbstractExplorerFileStore;
import org.knime.workbench.explorer.view.AbstractContentProvider;
import org.knime.workbench.explorer.view.ContentObject;

/**
 * Utility methods for dialog components of the file chooser Configuration and Widget nodes
 *
 * @author Christian Albrecht, KNIME GmbH, Konstanz, Germany
 */
public class FileChooserDialogUtil {

    /**
     * Create the browse button which displays the repository structure
     *
     * @param textField the text field to fill in the selected item(s)
     * @param validator the {@link FileChooserValidator} to validate the selection
     * @param fileStoreContainer the {@link FileStoreContainer} for selection
     * @param title the title of the browse dialog
     * @param description the description of the browse dialog
     * @param remoteOnly true if only remote items should be displayed
     * @return a {@link JButton} with the click action set
     */
    public static JButton createBrowseButton(final JTextField textField, final FileChooserValidator validator,
        final FileStoreContainer fileStoreContainer, final String title, final String description,
        final boolean remoteOnly) {
        final JButton browseButton = new JButton("Browse");
        browseButton.addActionListener(new ActionListener() {
            /**
             * {@inheritDoc}
             */
            @Override
            public void actionPerformed(final ActionEvent arg0) {
                Display display = Display.getCurrent();
                if (display == null) {
                    display = Display.getDefault();
                }
                display.asyncExec(ThreadUtils.runnableWithContext(new Runnable() {
                    @Override
                    public void run() {
                        // collect all non-local mount ids
                        List<String> mountIDs = new ArrayList<String>();
                        for (Map.Entry<String, AbstractContentProvider> entry : ExplorerMountTable.getMountedContent()
                            .entrySet()) {
                            String mountID = entry.getKey();
                            AbstractContentProvider acp = entry.getValue();
                            if (remoteOnly ? acp.isRemote() && acp.canHostDataFiles() : acp.canHostDataFiles()) {
                                mountIDs.add(mountID);
                            }
                        }
                        if (mountIDs.isEmpty()) {
                            MessageBox box =
                                new MessageBox(Display.getDefault().getActiveShell(), SWT.ICON_INFORMATION | SWT.OK);
                            box.setText("No item for selection");
                            box.setMessage(
                                "No server mountpoint was found for selection of root path. Please log into a server "
                                    + "you want to use for browsing.");
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

                        SpaceResourceSelectionDialog dialog = new SpaceResourceSelectionDialog(
                            Display.getDefault().getActiveShell(), mountIDs.toArray(new String[0]), initialSelection);
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
     * Tries to determine the selection type from a given file store
     *
     * @param fileStore the file store
     * @param path the path to the selected item
     * @param oldValue the previous item for convenience
     *
     * @return the determined {@link SelectionType}
     */
    public static SelectionType getTypeForFileStore(final AbstractExplorerFileStore fileStore, final String path,
        final FileItem oldValue) {
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
     * Extracts allowed file types from a text input
     *
     * @param validExtensions the text typed in by the user (comma separated list)
     * @return String[] file types the array of allowed file types
     */
    public static String[] getFileTypes(final String validExtensions) {
        String s = validExtensions.trim();
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

}
