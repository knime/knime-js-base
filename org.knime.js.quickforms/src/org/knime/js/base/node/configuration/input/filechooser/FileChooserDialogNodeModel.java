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

import static org.knime.js.base.node.base.input.filechooser.FileChooserNodeUtil.createSpec;
import static org.knime.js.base.node.base.input.filechooser.FileChooserNodeUtil.getFirstFile;
import static org.knime.js.base.node.base.input.filechooser.FileChooserNodeUtil.getValidatedItems;

import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.RowKey;
import org.knime.core.data.def.DefaultRow;
import org.knime.core.data.def.StringCell;
import org.knime.core.node.BufferedDataContainer;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.port.PortObject;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.port.PortType;
import org.knime.core.util.Pair;
import org.knime.js.base.node.base.input.filechooser.FileChooserNodeConfig;
import org.knime.js.base.node.base.input.filechooser.FileChooserNodeValue.FileItem;
import org.knime.js.base.node.base.input.filechooser.FileChooserValidator;
import org.knime.js.base.node.configuration.DialogNodeModel;

/**
 * Node model for the file chooser configuration node
 *
 * @author Christian Albrecht, KNIME GmbH, Konstanz, Germany
 */
public class FileChooserDialogNodeModel extends
    DialogNodeModel<FileChooserDialogNodeRepresentation, FileChooserDialogNodeValue, FileChooserInputDialogNodeConfig> {

    /**
     * Creates a new file chooser configuration node model
     */
    public FileChooserDialogNodeModel() {
        super(new PortType[0], new PortType[]{BufferedDataTable.TYPE});
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected PortObjectSpec[] configure(final PortObjectSpec[] inSpecs) throws InvalidSettingsException {
        createAndPushFlowVariable();
        FileChooserInputDialogNodeConfig config = getConfig();
        return new PortObjectSpec[]{createSpec(config.getFileChooserConfig(), config.getFlowVariableConfig())};
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected PortObject[] execute(final PortObject[] inObjects, final ExecutionContext exec) throws Exception {
        createAndPushFlowVariable();
        FileChooserInputDialogNodeConfig config = getConfig();
        DataTableSpec outSpec = createSpec(config.getFileChooserConfig(), config.getFlowVariableConfig());
        BufferedDataContainer cont = exec.createDataContainer(outSpec, true);
        Pair<FileItem[], Boolean> pair = getValidatedItems(getRelevantValue());
        FileItem[] items = pair.getFirst();
        if (pair.getSecond()) {
            setWarningMessage("Some values contained no path information and were omitted.");
        }
        for (int i = 0; i < items.length; i++) {
            DataRow row;
            if (config.getFileChooserConfig().getOutputType()) {
                row = new DefaultRow(RowKey.createRowKey(Long.valueOf(i)), new StringCell(items[i].getPath()),
                    new StringCell(items[i].getType()));
            } else {
                row = new DefaultRow(RowKey.createRowKey(Long.valueOf(i)), new StringCell(items[i].getPath()));
            }
            cont.addRowToTable(row);
        }
        cont.close();
        return new PortObject[]{cont.getTable()};
    }

    private void createAndPushFlowVariable() throws InvalidSettingsException {
        validateDialogValue(getRelevantValue());
        FileItem item = getFirstFile(getRelevantValue());
        String varIdentifier = getConfig().getFlowVariableName();
        pushFlowVariableString(varIdentifier, item.getPath());
        if (getConfig().getFileChooserConfig().getOutputType()) {
            pushFlowVariableString(varIdentifier + " (type)", item.getType());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FileChooserDialogNodeValue createEmptyDialogValue() {
        return new FileChooserDialogNodeValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FileChooserInputDialogNodeConfig createEmptyConfig() {
        return new FileChooserInputDialogNodeConfig();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected FileChooserDialogNodeRepresentation getRepresentation() {
        return new FileChooserDialogNodeRepresentation(getRelevantValue(), getConfig());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void validateDialogValue(final FileChooserDialogNodeValue value) throws InvalidSettingsException {
        FileChooserNodeConfig c = getConfig().getFileChooserConfig();
        FileChooserValidator validator = new FileChooserValidator(c.getSelectWorkflows(), c.getSelectDirectories(),
            c.getSelectDataFiles(), c.getFileTypes());
        String validationResult = validator.validateViewValue(value);
        if (validationResult != null) {
            throw new InvalidSettingsException(validationResult);
        }
        super.validateDialogValue(value);
    }

}
