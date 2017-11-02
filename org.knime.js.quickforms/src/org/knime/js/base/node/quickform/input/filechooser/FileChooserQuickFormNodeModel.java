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
 * ---------------------------------------------------------------------
 *
 * History
 *   29.09.2014 (Christian Albrecht, KNIME AG, Zurich, Switzerland): created
 */
package org.knime.js.base.node.quickform.input.filechooser;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataColumnSpecCreator;
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
import org.knime.core.node.web.ValidationError;
import org.knime.js.base.node.quickform.QuickFormNodeModel;
import org.knime.js.base.node.quickform.input.filechooser.FileChooserQuickFormConfig.SelectionType;
import org.knime.js.base.node.quickform.input.filechooser.FileChooserQuickFormNodeDialog.FileChooserValidator;
import org.knime.js.base.node.quickform.input.filechooser.FileChooserQuickFormValue.FileItem;

/**
 *
 * @author Christian Albrecht, KNIME.com GmbH, Konstanz, Germany
 */
public class FileChooserQuickFormNodeModel extends QuickFormNodeModel<FileChooserQuickFormRepresentation,
        FileChooserQuickFormValue, FileChooserQuickFormConfig> {

    /**
     * @param viewName
     */
    protected FileChooserQuickFormNodeModel(final String viewName) {
        super(new PortType[0], new PortType[]{BufferedDataTable.TYPE}, viewName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FileChooserQuickFormValue createEmptyViewValue() {
        return new FileChooserQuickFormValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getJavascriptObjectID() {
        return "org_knime_js_base_node_quickform_input_filechooser";
    }

    /** {@inheritDoc} */
    @Override
    protected PortObjectSpec[] configure(final PortObjectSpec[] inSpecs) throws InvalidSettingsException {
        createAndPushFlowVariable();
        return new PortObjectSpec[]{createSpec()};
    }

    /** {@inheritDoc} */
    @Override
    protected PortObject[] execute(final PortObject[] inObjects, final ExecutionContext exec) throws Exception {
        createAndPushFlowVariable();
        DataTableSpec outSpec = createSpec();
        BufferedDataContainer cont = exec.createDataContainer(outSpec, true);
        FileItem[] items = getValidatedItems();
        for (int i = 0; i < items.length; i++) {
            DataRow row;
            if (getConfig().getOutputType()) {
                row = new DefaultRow(RowKey.createRowKey(i), new StringCell(items[i].getPath()), new StringCell(items[i].getType()));
            } else {
                row = new DefaultRow(RowKey.createRowKey(i), new StringCell(items[i].getPath()));
            }
            cont.addRowToTable(row);
        }
        cont.close();
        return new PortObject[]{cont.getTable()};
    }

    private void createAndPushFlowVariable() throws InvalidSettingsException {
        ValidationError error = validateViewValue(getRelevantValue());
        if (error != null) {
            throw new InvalidSettingsException(error.getError());
        }
        FileItem item = getFirstFile();
        String varIdentifier = getConfig().getFlowVariableName();
        pushFlowVariableString(varIdentifier, item.getPath());
        if (getConfig().getOutputType()) {
            pushFlowVariableString(varIdentifier + " (type)", item.getType());
        }
    }

    private FileItem getFirstFile() throws InvalidSettingsException {
        FileItem[] items = getRelevantValue().getItems();
        if (items == null || items.length <= 0) {
            throw new InvalidSettingsException("No paths provided.");
        }
        String path = items[0].getPath();
        if (path == null || path.isEmpty()) {
            throw new InvalidSettingsException("File item invalid. No path provided.");
        }
        return items[0];
    }

    private DataTableSpec createSpec() {
        boolean outputType = getConfig().getOutputType();
        String variableName = getConfig().getFlowVariableName();
        final DataColumnSpec pathSpec = new DataColumnSpecCreator(variableName, StringCell.TYPE).createSpec();
        DataColumnSpec[] columnSpecs = new DataColumnSpec[outputType ? 2 : 1];
        columnSpecs[0] = pathSpec;
        if (outputType) {
            final DataColumnSpec typeSpec = new DataColumnSpecCreator(variableName + "(type)", StringCell.TYPE).createSpec();
            columnSpecs[1] = typeSpec;
        }
        return new DataTableSpec(columnSpecs);
    }

    private FileItem[] getValidatedItems() {
        FileItem[] allItems = getRelevantValue().getItems();
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
        if (valuesOmitted) {
            setWarningMessage("Some values contained no path information and were omitted.");
        }
        return validatedItems.toArray(new FileItem[0]);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ValidationError validateViewValue(final FileChooserQuickFormValue value) {
        FileChooserQuickFormConfig c = getConfig();
        FileChooserValidator validator = new FileChooserValidator(c.getSelectWorkflows(), c.getSelectDirectories(),
            c.getSelectDataFiles(), c.getFileTypes());
        String validationResult = validator.validateViewValue(value);
        if (validationResult != null) {
            return new ValidationError(validationResult);
        }
        return super.validateViewValue(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FileChooserQuickFormConfig createEmptyConfig() {
        return new FileChooserQuickFormConfig();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected FileChooserQuickFormRepresentation getRepresentation() {
        return new FileChooserQuickFormRepresentation(getRelevantValue(), getConfig());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void copyValueToConfig() {
        FileItem[] items = getViewValue().getItems();
        if (items != null && items.length > 0) {
            getConfig().getDefaultValue().setItems(getViewValue().getItems());
        }
        getConfig().getDefaultValue().setItems(new FileItem[0]);
    }
}
