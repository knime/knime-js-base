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

import java.util.ArrayList;
import java.util.List;

import javax.swing.JTextField;

/**
 * Utility methods for the file upload Configuration and Widget nodes
 *
 * @author Daniel Bogenrieder, KNIME GmbH, Konstanz, Germany
 */
public final class FileUploadNodeUtil {

    private FileUploadNodeUtil() {
        /* utility class */ }

    /**
     * @param m_validExtensionsField the text field with valid extension string
     * @return String[] file types
     */
    public static String[] getFileTypes(final JTextField m_validExtensionsField) {
        return getFileTypes(m_validExtensionsField.getText());
    }

    /**
     * Get the valid extensions from a string of comma separated extensions
     *
     * @param validExtensions an array of valid extensions to transform
     * @return all valid extensions including an entry which combines all extension by |.
     *         {@link #extractExtensions(String[])}
     */
    public static String[] getFileTypes(final String validExtensions) {
        if (validExtensions.trim().isEmpty()) {
            return new String[0];
        }
        String[] fileTypes = validExtensions.split(",");
        List<String> filteredFileTypes = new ArrayList<String>();
        for (String type : fileTypes) {
            final var trimmedType = type.trim();
            if (trimmedType.isEmpty()) {
                continue;
            }
            if (trimmedType.startsWith(".")) {
                filteredFileTypes.add(trimmedType);
            } else {
                filteredFileTypes.add("." + trimmedType);
            }
        }
        if (filteredFileTypes.size() == 0) {
            return new String[0];
        } else if (filteredFileTypes.size() > 1) {
            // first all the file types, then all of them separately
            // use | because of FilesHistoryPanel behaviour
            filteredFileTypes.add(0, String.join("|", filteredFileTypes));
        }
        return filteredFileTypes.toArray(new String[filteredFileTypes.size()]);
    }

    /**
     * This method is to be used on a result of the {@link #getFileTypes(JTextField)} method to extract only the file
     * extensions without the leading dot and without the entry combined by |. E.g. if the input is:
     *
     * <pre>
     *  ".txt", ".csv", ".tsv", ".xlsx", ".txt|.csv|.tsv|.xlsx"
     * </pre>
     *
     * the output will be:
     *
     * <pre>
     *  "txt", "csv", "tsv", "xlsx"
     * </pre>
     *
     * @param fileTypes the file types as returned by {@link #getFileTypes(JTextField)}
     * @return the file extensions without the leading dot and without the entry combined by |
     */
    public static String[] extractExtensions(final String[] fileTypes) {
        if (fileTypes.length == 0) {
            return fileTypes;
        }
        List<String> extensions = new ArrayList<>(fileTypes.length);
        for (String type : fileTypes) {
            if (type.contains("|")) {
                // skip the combined entry
                continue;
            }
            String extension = type.startsWith(".") ? type.substring(1) : type;
            extensions.add(extension);
        }
        return extensions.toArray(new String[0]);

    }

    /**
     * Tries to determine the file name of an arbitrary path
     *
     * @param path The path including the file name as last component, can be file system path or a url string
     * @return the file name or the path itself if the file name can not be deduced from it
     */
    public static String getFileNameFromPath(final String path) {
        int index = path.lastIndexOf('/');
        if (index < 0) {
            index = path.lastIndexOf('\\');
        }
        if (index + 1 >= path.length()) {
            index = -1;
        }
        return index < 0 ? path : path.substring(index + 1);
    }

    /**
     * @param files
     * @param otherFiles
     * @return
     */
    public static boolean checkUploadFilesEquality(final FileUploadObject[] files,
        final FileUploadObject[] otherFiles) {
        if (files.length != otherFiles.length) {
            return false;
        } else {
            for (var i = 0; i < files.length; i++) {

                var isEqual = files[i].equals(otherFiles[i]);
                if (isEqual == false) {
                    return false;
                }
            }
        }
        return true;
    }
}
