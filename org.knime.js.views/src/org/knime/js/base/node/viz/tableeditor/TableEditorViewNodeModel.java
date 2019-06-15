/*
 * ------------------------------------------------------------------------
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
 * ------------------------------------------------------------------------
 *
 * History
 *   23.04.2014 (Christian Albrecht, KNIME AG, Zurich, Switzerland): created
 */
package org.knime.js.base.node.viz.tableeditor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.naming.OperationNotSupportedException;

import org.knime.core.data.BooleanValue;
import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.DataType;
import org.knime.core.data.DoubleValue;
import org.knime.core.data.IntValue;
import org.knime.core.data.LongValue;
import org.knime.core.data.container.CellFactory;
import org.knime.core.data.container.ColumnRearranger;
import org.knime.core.data.container.SingleCellFactory;
import org.knime.core.data.def.BooleanCell;
import org.knime.core.data.def.BooleanCell.BooleanCellFactory;
import org.knime.core.data.def.DefaultRow;
import org.knime.core.data.def.DoubleCell;
import org.knime.core.data.def.IntCell;
import org.knime.core.data.def.LongCell;
import org.knime.core.data.def.StringCell;
import org.knime.core.data.property.filter.FilterHandler;
import org.knime.core.node.BufferedDataContainer;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.BufferedDataTableHolder;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.port.PortObject;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.port.PortType;
import org.knime.core.node.util.filter.NameFilterConfiguration.FilterResult;
import org.knime.core.node.web.ValidationError;
import org.knime.core.node.wizard.CSSModifiable;
import org.knime.js.core.JSONDataTable;
import org.knime.js.core.node.AbstractWizardNodeModel;

/**
 *
 * @author Christian Albrecht, KNIME.com GmbH, Konstanz, Germany
 */
public class TableEditorViewNodeModel extends AbstractWizardNodeModel<TableEditorViewRepresentation,
        TableEditorViewValue> implements BufferedDataTableHolder, CSSModifiable {

    private static final NodeLogger LOGGER = NodeLogger.getLogger(TableEditorViewNodeModel.class);

    private final TableEditorViewConfig m_config;
    private BufferedDataTable m_table;

    /**
     * @param viewName The name of the interactive view
     */
    protected TableEditorViewNodeModel(final String viewName) {
        super(new PortType[]{BufferedDataTable.TYPE}, new PortType[]{BufferedDataTable.TYPE}, viewName);
        m_config = new TableEditorViewConfig();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getJavascriptObjectID() {
        return "org.knime.js.base.node.viz.tableeditor";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected PortObjectSpec[] configure(final PortObjectSpec[] inSpecs) throws InvalidSettingsException {
        DataTableSpec tableSpec = (DataTableSpec)inSpecs[0];
        if (m_config.getEnableSelection()) {
            ColumnRearranger rearranger = createColumnAppender(tableSpec, null);
            tableSpec = rearranger.createSpec();
        }
        return new PortObjectSpec[]{tableSpec};
    }

    private ColumnRearranger createColumnAppender(final DataTableSpec spec, final List<String> selectionList) {
        String newColName = m_config.getSelectionColumnName();
        if (newColName == null || newColName.trim().isEmpty()) {
            newColName = TableEditorViewConfig.DEFAULT_SELECTION_COLUMN_NAME;
        }
        newColName = DataTableSpec.getUniqueColumnName(spec, newColName);
        DataColumnSpec outColumnSpec =
                new DataColumnSpecCreator(newColName, DataType.getType(BooleanCell.class)).createSpec();
        ColumnRearranger rearranger = new ColumnRearranger(spec);
        CellFactory fac = new SingleCellFactory(outColumnSpec) {

            private int m_rowIndex = 0;

            @Override
            public DataCell getCell(final DataRow row) {
                if (++m_rowIndex > m_config.getMaxRows()) {
                    return DataType.getMissingCell();
                }
                if (selectionList != null) {
                    if (selectionList.contains(row.getKey().toString())) {
                            /*return selectAll ? BooleanCell.FALSE : BooleanCell.TRUE;*/
                        return BooleanCell.TRUE;
                    } else {
                        return BooleanCell.FALSE;
                    }
                }
                /*return selectAll ? BooleanCell.TRUE : BooleanCell.FALSE;*/
                return BooleanCell.FALSE;
            }
        };
        rearranger.append(fac);
        return rearranger;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveCurrentValue(final NodeSettingsWO content) {
        // nothing to do
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TableEditorViewRepresentation createEmptyViewRepresentation() {
        return new TableEditorViewRepresentation();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TableEditorViewValue createEmptyViewValue() {
        return new TableEditorViewValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TableEditorViewRepresentation getViewRepresentation() {
        TableEditorViewRepresentation rep = super.getViewRepresentation();
        synchronized (getLock()) {
            if (rep.getTable() == null && m_table != null) {
                // set internal table
                try {
                    JSONDataTable jT = createJSONTableFromBufferedDataTable(m_table, null);
                    rep.setTable(jT);
                } catch (Exception e) {
                    LOGGER.error("Could not create JSON table: " + e.getMessage(), e);
                }
            }
        }
        return rep;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isHideInWizard() {
        return m_config.getHideInWizard();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setHideInWizard(final boolean hide) {
        m_config.setHideInWizard(hide);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCssStyles() {
        return m_config.getCustomCSS();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setCssStyles(final String styles) {
        m_config.setCustomCSS(styles);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ValidationError validateViewValue(final TableEditorViewValue value) {
        // no validation done here
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BufferedDataTable[] getInternalTables() {
        return new BufferedDataTable[]{m_table};
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setInternalTables(final BufferedDataTable[] tables) {
        m_table = tables[0];
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected PortObject[] performExecute(final PortObject[] inObjects, final ExecutionContext exec) throws Exception {
        BufferedDataTable out;
        synchronized (getLock()) {
            TableEditorViewRepresentation viewRepresentation = getViewRepresentation();
            if (viewRepresentation.getTable() == null) {
                if (m_table == null) {
                    m_table = (BufferedDataTable)inObjects[0];
                }
                JSONDataTable jsonTable = createJSONTableFromBufferedDataTable(m_table, exec.createSubExecutionContext(0.5));
                viewRepresentation.setTable(jsonTable);
                copyConfigToRepresentation();
            }

            out = m_table;
            TableEditorViewValue viewValue = getViewValue();

            // apply edit changes
            Optional<String> dataHash = viewRepresentation.getTable().getDataHash();
            boolean isSameHash = dataHash.isPresent() ? dataHash.get().equals(viewValue.getTableHash()) : false;
            if (viewValue != null && isSameHash && viewValue.getEditorChanges().getChanges().size() > 0) {
                // if there are editor changes and hash of the input table has not changed, we apply the changes

                DataTableSpec spec = m_table.getDataTableSpec();
                Map<String, Map<String, Object>> editorChanges = viewValue.getEditorChanges().getChanges();
                BufferedDataContainer dc = exec.createDataContainer(spec);
                String[] columnNames = spec.getColumnNames();
                List<String> editableColumns = Arrays.asList(viewRepresentation.getEditableColumns());
                // set of columns which were editable, but then have become read-only in the node config dialog
                Set<String> conflictEditColumns = new HashSet<String>();

                // iterate over each row from the input table and apply the changes where appropriate
                for (DataRow row : m_table) {
                    String rowKey = row.getKey().getString();
                    Map<String, Object> rowEditorChanges = editorChanges.get(rowKey);

                    DataCell[] copy = new DataCell[row.getNumCells()];
                    for (int i = 0; i < row.getNumCells(); i++) {
                        String colName = columnNames[i];
                        DataCell cell = row.getCell(i);
                        if (rowEditorChanges != null && rowEditorChanges.containsKey(colName)) {
                            // check whether the columns is still editable
                            if (editableColumns.contains(colName)) {
                                Object value = rowEditorChanges.get(colName);
                                DataType type = spec.getColumnSpec(i).getType();
                                if (value == null) {
                                    copy[i] = DataType.getMissingCell();
                                } else if (type.isCompatible(BooleanValue.class)) {
                                    copy[i] = BooleanCellFactory.create((Boolean) value);
                                } else if (type.isCompatible(IntValue.class) && value instanceof Integer) {
                                    copy[i] = new IntCell((Integer) value);
                                } else if (type.isCompatible(LongValue.class) && value instanceof Integer) {
                                    copy[i] = new LongCell(((Integer) value).longValue());
                                } else if (type.isCompatible(DoubleValue.class) && (value instanceof Double || value instanceof Integer)) {
                                    if (value instanceof Double) {
                                        copy[i] = new DoubleCell((Double) value);
                                    } else {
                                        copy[i] = new DoubleCell((Integer) value);
                                    }
                                }  else if (type.getCellClass().equals(StringCell.class)) {
                                    copy[i] = new StringCell(value.toString());
                                } else {
                                    throw new OperationNotSupportedException("Type " + type.getName() + " is not supported for editing.");
                                }
                            } else {
                                // if editor filter setting has changed, do not apply the change and raise a warning
                                copy[i] = cell;
                                conflictEditColumns.add(colName);
                            }
                        } else {
                            copy[i] = cell;
                        }
                    }
                    dc.addRowToTable(new DefaultRow(row.getKey(), copy));
                }
                if (conflictEditColumns.size() > 0) {
                    setWarningMessage("The column(s) " + String.join(",", conflictEditColumns) + " have become not editable. Saved changes for these columns are ignored.");
                }
                dc.close();
                out = dc.getTable();
            }

            if (m_config.getEnableSelection()) {
                List<String> selectionList = null;
                if (viewValue != null) {
                    if (viewValue.getSelection() != null) {
                        selectionList = Arrays.asList(viewValue.getSelection());
                    }
                }
                ColumnRearranger rearranger = createColumnAppender(out.getDataTableSpec(), selectionList);
                out = exec.createColumnRearrangeTable(out, rearranger, exec.createSubExecutionContext(0.5));
            }
            setSubscriptionFilterIds(out.getDataTableSpec());
        }
        exec.setProgress(1);
        return new PortObject[]{out};
    }

    private JSONDataTable createJSONTableFromBufferedDataTable(final BufferedDataTable table, final ExecutionContext exec) throws CanceledExecutionException {
        FilterResult filter = m_config.getColumnFilterConfig().applyTo(table.getDataTableSpec());
        //ColumnRearranger rearranger = new ColumnRearranger(table.getDataTableSpec());
        //rearranger.keepOnly(filter.getIncludes());
        //BufferedDataTable filteredTable = exec.createColumnRearrangeTable(table, rearranger, exec.createSubExecutionContext(0.5));
        JSONDataTable jsonTable = JSONDataTable.newBuilder()
                .setDataTable(table)
                .setId(getTableId(0))
                .setFirstRow(1)
                .setMaxRows(m_config.getMaxRows())
                .setExcludeColumns(filter.getExcludes())
                .keepFilterColumns(true)
                .calculateDataHash(true)
                .build(exec);
        if (m_config.getMaxRows() < table.size()) {
            setWarningMessage("Only the first " + m_config.getMaxRows() + " rows are displayed.");
        }
        return jsonTable;
    }

    private void setSubscriptionFilterIds(final DataTableSpec spec) {
        TableEditorViewRepresentation viewRepresentation = getViewRepresentation();
        if (viewRepresentation != null) {
            List<String> idList = new ArrayList<String>();
            for (int i = 0; i < spec.getNumColumns(); i++) {
                Optional<FilterHandler> filterHandler = spec.getColumnSpec(i).getFilterHandler();
                if (filterHandler.isPresent()) {
                    idList.add(filterHandler.get().getModel().getFilterUUID().toString());
                }
            }
            viewRepresentation.setSubscriptionFilterIds(idList.toArray(new String[0]));
        }
    }

    private void copyConfigToRepresentation() {
        synchronized(getLock()) {
            TableEditorViewRepresentation viewRepresentation = getViewRepresentation();
            TableEditorViewValue viewValue = getViewValue();
            viewRepresentation.setEnablePaging(m_config.getEnablePaging());
            viewRepresentation.setInitialPageSize(m_config.getIntialPageSize());
            viewRepresentation.setEnablePageSizeChange(m_config.getEnablePageSizeChange());
            viewRepresentation.setAllowedPageSizes(m_config.getAllowedPageSizes());
            viewRepresentation.setPageSizeShowAll(m_config.getPageSizeShowAll());
            viewRepresentation.setEnableJumpToPage(m_config.getEnableJumpToPage());
            viewRepresentation.setDisplayRowColors(m_config.getDisplayRowColors());
            viewRepresentation.setDisplayRowIds(m_config.getDisplayRowIds());
            viewRepresentation.setDisplayColumnHeaders(m_config.getDisplayColumnHeaders());
            viewRepresentation.setDisplayRowIndex(m_config.getDisplayRowIndex());
            viewRepresentation.setFixedHeaders(m_config.getFixedHeaders());
            viewRepresentation.setTitle(m_config.getTitle());
            viewRepresentation.setSubtitle(m_config.getSubtitle());
            viewRepresentation.setEnableSelection(m_config.getEnableSelection());
            viewRepresentation.setEnableSearching(m_config.getEnableSearching());
            viewRepresentation.setEnableColumnSearching(m_config.getEnableColumnSearching());
            viewRepresentation.setEnableSorting(m_config.getEnableSorting());
            viewRepresentation.setEnableClearSortButton(m_config.getEnableClearSortButton());
            viewRepresentation.setEnableGlobalNumberFormat(m_config.getEnableGlobalNumberFormat());
            viewRepresentation.setGlobalNumberFormatDecimals(m_config.getGlobalNumberFormatDecimals());

            //added with 3.3
            viewRepresentation.setDisplayFullscreenButton(m_config.getDisplayFullscreenButton());
            viewRepresentation.setEnableHideUnselected(m_config.getEnableHideUnselected());

            //added with 3.4
            viewRepresentation.setDisplayMissingValueAsQuestionMark(m_config.getDisplayMissingValueAsQuestionMark());
            viewRepresentation.setDateTimeFormats(m_config.getDateTimeFormats().getJSONSerializableObject());

            //added with 3.5
            viewRepresentation.setSingleSelection(m_config.getSingleSelection());
            viewRepresentation.setEnableClearSelectionButton(m_config.getEnableClearSelectionButton());

            //editor settings
            viewRepresentation.setEditableColumns(m_config.getEditableColumnFilterConfig().applyTo(m_table.getDataTableSpec()).getIncludes());

            if (isViewValueEmpty()) {
                viewValue.setPublishSelection(m_config.getPublishSelection());
                viewValue.setSubscribeSelection(m_config.getSubscribeSelection());
                viewValue.setPublishFilter(m_config.getPublishFilter());
                viewValue.setSubscribeFilter(m_config.getSubscribeFilter());

                //added with 3.4
                viewValue.setHideUnselected(m_config.getHideUnselected() && !m_config.getSingleSelection());

                //editor settings
                viewValue.setEditorChanges(m_config.getEditorChanges());
                viewValue.setTableHash(m_config.getTableHash());
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void performReset() {
        m_table = null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void useCurrentValueAsDefault() {
        TableEditorViewValue viewValue = getViewValue();
        m_config.setHideUnselected(viewValue.getHideUnselected());
        m_config.setPublishSelection(viewValue.getPublishSelection());
        m_config.setSubscribeSelection(viewValue.getSubscribeSelection());
        m_config.setPublishFilter(viewValue.getPublishFilter());
        m_config.setSubscribeFilter(viewValue.getSubscribeFilter());
        // editor settings
        m_config.setEditorChanges(viewValue.getEditorChanges());
        m_config.setTableHash(viewValue.getTableHash());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) {
        m_config.saveSettings(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void validateSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
        (new TableEditorViewConfig()).loadSettings(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadValidatedSettingsFrom(final NodeSettingsRO settings) throws InvalidSettingsException {
        m_config.loadSettings(settings);
    }

}
