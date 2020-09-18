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
 *   Sep 18, 2020 (Christian Albrecht, KNIME GmbH, Konstanz, Germany): created
 */
package org.knime.js.base.node.widget.filter.definition.value;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import javax.naming.OperationNotSupportedException;

import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.StringValue;
import org.knime.core.data.def.StringCell.StringCellFactory;
import org.knime.core.data.property.filter.FilterHandler;
import org.knime.core.data.property.filter.FilterModel;
import org.knime.core.data.property.filter.FilterModelNominal;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.port.PortObject;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.port.PortType;
import org.knime.core.node.port.viewproperty.FilterDefinitionHandlerPortObject;
import org.knime.core.node.util.filter.NameFilterConfiguration.EnforceOption;
import org.knime.core.node.util.filter.NameFilterConfiguration.FilterResult;
import org.knime.core.node.web.ValidationError;
import org.knime.core.node.wizard.CSSModifiable;
import org.knime.js.base.node.widget.filter.definition.RangeFilterWidgetValue;
import org.knime.js.core.node.AbstractWizardNodeModel;
import org.knime.js.core.selections.json.AbstractColumnRangeSelection;
import org.knime.js.core.selections.json.NominalColumnRangeSelection;
import org.knime.js.core.selections.json.RangeSelection;

/**
 * Node model for the value filter definition node
 *
 * @author Christian Albrecht, KNIME GmbH, Konstanz, Germany
 */
public class ValueFilterDefinitionWidgetNodeModel
    extends AbstractWizardNodeModel<ValueFilterDefinitionWidgetRepresentation, RangeFilterWidgetValue>
    implements CSSModifiable {

    private final ValueFilterDefinitionWidgetConfig m_config;

    /**
     * @param viewName
     */
    public ValueFilterDefinitionWidgetNodeModel(final String viewName) {
        super(new PortType[]{BufferedDataTable.TYPE},
            new PortType[]{BufferedDataTable.TYPE, FilterDefinitionHandlerPortObject.TYPE}, viewName);
        m_config = new ValueFilterDefinitionWidgetConfig();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ValueFilterDefinitionWidgetRepresentation createEmptyViewRepresentation() {
        return new ValueFilterDefinitionWidgetRepresentation();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RangeFilterWidgetValue createEmptyViewValue() {
        return new RangeFilterWidgetValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getJavascriptObjectID() {
        return "org.knime.js.base.node.widget.filter.definition.value";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isHideInWizard() {
        return m_config.isHideInWizard();
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
    public ValidationError validateViewValue(final RangeFilterWidgetValue viewContent) {
        /* no validation atm */
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveCurrentValue(final NodeSettingsWO content) {
        /* nothing to do */
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected PortObjectSpec[] configure(final PortObjectSpec[] inSpecs) throws InvalidSettingsException {
        DataTableSpec spec = (DataTableSpec)inSpecs[0];
        DataTableSpec outSpec = setFilter(spec);
        String colName = m_config.getColumn();
        DataColumnSpec colSpec = spec.getColumnSpec(colName);
        DataTableSpec modelSpec = colSpec == null ? new DataTableSpec() : new DataTableSpec(colSpec);
        return new PortObjectSpec[] {outSpec, modelSpec};
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected PortObject[] performExecute(final PortObject[] inObjects, final ExecutionContext exec) throws Exception {
        BufferedDataTable in = (BufferedDataTable)inObjects[0];
        DataTableSpec inSpec = in.getDataTableSpec();
        synchronized (getLock()) {
            DataTableSpec outSpec = setFilter(inSpec);
            BufferedDataTable changedSpecTable = exec.createSpecReplacerTable(in, outSpec);
            setFilterOnValue(outSpec);
            String colName = m_config.getColumn();
            DataColumnSpec colSpec = outSpec.getColumnSpec(colName);
            DataTableSpec modelSpec;
            if (m_config.isMergeWithExistingFiltersModel()) {
                List<DataColumnSpec> allColSpecs = new ArrayList<>();
                for (final DataColumnSpec columnSpec : outSpec) {
                    if (columnSpec.getFilterHandler().isPresent()) {
                        allColSpecs.add(columnSpec);
                    }
                }
                modelSpec = new DataTableSpec(allColSpecs.toArray(new DataColumnSpec[allColSpecs.size()]));
            } else {
                modelSpec = new DataTableSpec(colSpec);
            }
            FilterDefinitionHandlerPortObject viewModel =
                    new FilterDefinitionHandlerPortObject(modelSpec, "Filter definition on \"" + colName + "\"");

            return new PortObject[]{changedSpecTable, viewModel};
        }
    }

    private DataTableSpec setFilter(final DataTableSpec spec) throws InvalidSettingsException {
        String colName = m_config.getColumn();
        if (colName == null || colName.isEmpty()) {
            throw new InvalidSettingsException("No domain column set");
        }
        RangeFilterWidgetValue value = getViewValue();
        String[] possibleValues = getPossibleValuesForColumn(colName, spec);
        FilterModelNominal model;
        if (value != null && value.getFilter() != null) {
            try {
                model = (FilterModelNominal)value.getFilter().createFilterModel();
            } catch (OperationNotSupportedException e) {
                throw new InvalidSettingsException(e);
            }
        } else {
            FilterResult filterResult =
                    m_config.getFilterValues().applyTo(possibleValues);
            Stream<DataCell> valStream = Arrays.stream(filterResult.getIncludes())
                    .map(val -> StringCellFactory.create(val));
            final List<DataCell> filterCells = new ArrayList<DataCell>();
            if (m_config.isUseMultiple()) {
                valStream.forEachOrdered(val -> filterCells.add(val));
            } else {
                valStream.findFirst().ifPresent(val -> filterCells.add(val));
            }
            model = FilterModel.newNominalModel(filterCells);
        }
        ValueFilterDefinitionWidgetRepresentation rep = getViewRepresentation();
        if (rep != null) {
            rep.setTableID(getTableId(0));
            rep.setFilterID(model.getFilterUUID().toString());
            rep.setPossibleValues(possibleValues);
            rep.setConfig(m_config);
        }
        return getOutSpec(spec, colName, FilterHandler.from(model));
    }

    static String[] getPossibleValuesForColumn(final String colName, final DataTableSpec spec) {
        DataColumnSpec columnSpec = spec.getColumnSpec(colName);
        String[] possibleValues = new String[0];
        if (columnSpec != null && columnSpec.getDomain().getValues() != null) {
            possibleValues = columnSpec.getDomain().getValues().stream()
                    .map(cell -> ((StringValue)cell).getStringValue()).toArray(String[]::new);
        }
        return possibleValues;
    }

    private DataTableSpec getOutSpec(final DataTableSpec inSpec, final String columnName, final FilterHandler filter) {
        if (!inSpec.containsName(columnName)) {
            setWarningMessage("The defined filter column " + columnName
                + " is not part of the spec anymore. No filter definition appended. Filter will be disabled.");
        }
        DataColumnSpec[] cspecs = new DataColumnSpec[inSpec.getNumColumns()];
        for (int i = 0; i < cspecs.length; i++) {
            DataColumnSpec cspec = inSpec.getColumnSpec(i);
            DataColumnSpecCreator cr = new DataColumnSpecCreator(cspec);
            if (cspec.getName().equals(columnName)) {
                if (cspec.getFilterHandler().isPresent()) {
                    setWarningMessage("A filter handler on column " + columnName
                        + " already exists. Overwriting previous definition.");
                }
                // set new filter
                cr.setFilterHandler(filter);
            } else if (!m_config.isMergeWithExistingFiltersTable()) {
                // delete previously defined filters on demand
                cr.setFilterHandler(null);
            }
            cspecs[i] = cr.createSpec();
        }
        DataTableSpec outSpec = new DataTableSpec(cspecs);
        return outSpec;
    }

    private void setFilterOnValue(final DataTableSpec spec) {
        RangeFilterWidgetValue value = getViewValue();
        if (value != null) {
            if (value.getFilter() == null) {
                RangeSelection filter = new RangeSelection();
                NominalColumnRangeSelection range = new NominalColumnRangeSelection();
                String columnName = m_config.getColumn();
                range.setColumnName(columnName);
                String[] possibleValues = getPossibleValuesForColumn(columnName, spec);
                FilterResult filterResult =
                        m_config.getFilterValues().applyTo(possibleValues);
                String[] includes = filterResult.getIncludes();
                if (!m_config.isUseMultiple() && includes.length > 1) {
                    includes = new String[] {includes[0]};
                }
                range.setValues(includes);
                filter.setColumns(new AbstractColumnRangeSelection[]{range});
                value.setFilter(filter);
            }
            value.getFilter().setId(getViewRepresentation().getFilterID());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ValueFilterDefinitionWidgetRepresentation getViewRepresentation() {
        ValueFilterDefinitionWidgetRepresentation rep = super.getViewRepresentation();
        synchronized(getLock()) {
            //make sure current table ids are used at all times
            if (rep != null) {
                rep.setTableID(getTableId(0));
            }
        }
        return rep;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void performReset() {
        // nothing to do
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void useCurrentValueAsDefault() {
        RangeFilterWidgetValue value = getViewValue();
        if (value != null && value.getFilter() != null) {
            AbstractColumnRangeSelection[] columns = value.getFilter().getColumns();
            if (columns != null && columns.length > 0 && columns[0] instanceof NominalColumnRangeSelection) {
                NominalColumnRangeSelection filter = (NominalColumnRangeSelection)columns[0];
                ValueFilterWidgetPanelConfiguration filterConfig = m_config.getFilterValues();
                filterConfig.setIncludeList(filter.getValues());
                filterConfig.setExcludeList(new String[0]);
                filterConfig.setEnforceOption(EnforceOption.EnforceInclusion);
            }
        }
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
        (new ValueFilterDefinitionWidgetConfig()).loadSettings(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadValidatedSettingsFrom(final NodeSettingsRO settings) throws InvalidSettingsException {
        m_config.loadSettings(settings);
    }

}
