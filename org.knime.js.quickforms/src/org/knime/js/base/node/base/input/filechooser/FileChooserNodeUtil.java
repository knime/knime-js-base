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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.def.StringCell;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.util.Pair;
import org.knime.js.base.node.base.FlowVariableConfig;
import org.knime.js.base.node.base.input.filechooser.FileChooserNodeConfig.SelectionType;
import org.knime.js.base.node.base.input.filechooser.FileChooserNodeValue.FileItem;

/**
 * Utility methods for the file chooser Configuration and Widget nodes
 *
 * @author Christian Albrecht, KNIME GmbH, Konstanz, Germany
 */
public class FileChooserNodeUtil {

    public static DataTableSpec createSpec(final FileChooserNodeConfig config, final FlowVariableConfig varConfig) {
        boolean outputType = config.getOutputType();
        String variableName = varConfig.getFlowVariableName();
        final DataColumnSpec pathSpec = new DataColumnSpecCreator(variableName, StringCell.TYPE).createSpec();
        DataColumnSpec[] columnSpecs = new DataColumnSpec[outputType ? 2 : 1];
        columnSpecs[0] = pathSpec;
        if (outputType) {
            final DataColumnSpec typeSpec =
                new DataColumnSpecCreator(variableName + "(type)", StringCell.TYPE).createSpec();
            columnSpecs[1] = typeSpec;
        }
        return new DataTableSpec(columnSpecs);
    }

    public static FileItem getFirstFile(final FileChooserNodeValue currentValue) throws InvalidSettingsException {
        FileItem[] items = currentValue.getItems();
        if (items == null || items.length <= 0) {
            throw new InvalidSettingsException("No paths provided.");
        }
        String path = items[0].getPath();
        if (path == null || path.isEmpty()) {
            throw new InvalidSettingsException("File item invalid. No path provided.");
        }
        return items[0];
    }

    /**
     * Returns a list of {@link FileItem} from the current node value
     *
     * @param currentValue the current value
     * @return a {@link Pair} with the first component being the array of {@link FileItem} and the second component a
     * flag if values were omitted.
     */
    public static Pair<FileItem[], Boolean> getValidatedItems(final FileChooserNodeValue currentValue) {
        FileItem[] allItems = currentValue.getItems();
        boolean valuesOmitted = false;
        List<FileItem> validatedItems = new ArrayList<FileItem>();
        for (FileItem item : allItems) {
            if (StringUtils.isEmpty(item.getPath())) {
                valuesOmitted = true;
                continue;
            }
            if (StringUtils.isEmpty(item.getType())) {
                item.setType(SelectionType.UNKNOWN);
            }
            validatedItems.add(item);
        }
        return new Pair<FileChooserNodeValue.FileItem[], Boolean>(validatedItems.toArray(new FileItem[0]),
            valuesOmitted);
    }

}
