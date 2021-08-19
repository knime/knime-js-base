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
 */
package org.knime.js.base.node.quickform.filter.definition.rangeslider;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.naming.OperationNotSupportedException;

import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.DoubleValue;
import org.knime.core.data.property.filter.FilterHandler;
import org.knime.core.data.property.filter.FilterModel;
import org.knime.core.data.property.filter.FilterModelRange;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.port.PortObject;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.port.PortType;
import org.knime.core.node.port.viewproperty.FilterDefinitionHandlerPortObject;
import org.knime.core.node.web.ValidationError;
import org.knime.core.node.wizard.CSSModifiable;
import org.knime.js.base.node.quickform.filter.definition.RangeFilterValue;
import org.knime.js.core.node.AbstractWizardNodeModel;
import org.knime.js.core.selections.json.AbstractColumnRangeSelection;
import org.knime.js.core.selections.json.NumericColumnRangeSelection;
import org.knime.js.core.selections.json.RangeSelection;
import org.knime.js.core.settings.slider.SliderNodeDialogUI;
import org.knime.js.core.settings.slider.SliderSettings;

/**
 * Model for the range slider filter node.
 *
 * @author Christian Albrecht, KNIME.com GmbH, Konstanz, Germany
 */
public class RangeSliderFilterNodeModel extends AbstractWizardNodeModel<RangeSliderFilterRepresentation,
        RangeFilterValue> implements CSSModifiable {

    private UUID m_filterId;

    private final RangeSliderFilterConfig m_config;

    /**
     * Creates a new range slider filter node model.
     *
     * @param viewName name for view creation
     */
    public RangeSliderFilterNodeModel(final String viewName) {
        super(new PortType[]{BufferedDataTable.TYPE},
            new PortType[]{BufferedDataTable.TYPE, FilterDefinitionHandlerPortObject.TYPE}, viewName);
        m_config = new RangeSliderFilterConfig();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getJavascriptObjectID() {
        return "org_knime_js_base_node_quickform_filter_slider";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected PortObjectSpec[] configure(final PortObjectSpec[] inSpecs) throws InvalidSettingsException {
        DataTableSpec spec = (DataTableSpec)inSpecs[0];
        setDomainRange(spec);
        DataTableSpec outSpec = setFilter(spec);
        String colName = m_config.getDomainColumn().getStringValue();
        DataColumnSpec colSpec = spec.getColumnSpec(colName);
        DataTableSpec modelSpec = colSpec == null ? new DataTableSpec() : new DataTableSpec(colSpec);
        return new PortObjectSpec[]{outSpec, modelSpec};
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected PortObject[] performExecute(final PortObject[] inObjects, final ExecutionContext exec) throws Exception {
        BufferedDataTable in = (BufferedDataTable)inObjects[0];
        DataTableSpec inSpec = in.getDataTableSpec();
        synchronized (getLock()) {
            setDomainRange(inSpec);
            DataTableSpec outSpec = setFilter(inSpec);
            BufferedDataTable changedSpecTable = exec.createSpecReplacerTable(in, outSpec);
            setFilterOnValue();
            String colName = m_config.getDomainColumn().getStringValue();
            DataColumnSpec colSpec = outSpec.getColumnSpec(colName);
            DataTableSpec modelSpec;
            if (m_config.getMergeWithExistingFiltersModel()) {
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

    private void setDomainRange(final DataTableSpec spec) {
        if (m_config.getSliderSettings() == null) {
            return;
        }
        String colName = m_config.getDomainColumn().getStringValue();
        if (colName != null) {
            if (!m_config.getCustomMin() || !m_config.getCustomMax()) {
                DataColumnSpec colSpec = spec.getColumnSpec(colName);
                if (colSpec == null) {
                    setWarningMessage(
                        "Configured range column " + colName + " is not available anymore. Slider will be disabled.");
                    disableSlider(true);
                    return;
                }
                disableSlider(false);
                boolean minFailed = false;
                boolean maxFailed = false;
                if (!m_config.getCustomMin()) {
                    DataCell min = colSpec.getDomain().getLowerBound();
                    if (min == null || !min.getType().isCompatible(DoubleValue.class)) {
                        minFailed = true;
                    } else {
                        m_config.getSliderSettings().setRangeMinValue(((DoubleValue)min).getDoubleValue());
                    }
                }
                if (!m_config.getCustomMax()) {
                    DataCell max = colSpec.getDomain().getUpperBound();
                    if (max == null || !max.getType().isCompatible(DoubleValue.class)) {
                        maxFailed = true;
                    } else {
                        m_config.getSliderSettings().setRangeMaxValue(((DoubleValue)max).getDoubleValue());
                    }
                }
                if (minFailed || maxFailed) {
                    StringBuilder builder = new StringBuilder();
                    builder.append("Selected range column ");
                    builder.append(colName);
                    builder.append(" has no ");
                    if (minFailed) {
                        builder.append("minimum ");
                        if (maxFailed) {
                            builder.append("or ");
                        }
                    }
                    if (maxFailed) {
                        builder.append("maximum ");
                    }
                    builder.append("domain values set. Using previously configured range values.");
                    setWarningMessage(builder.toString());
                }
            }
            for (int i = 0; i < m_config.getUseDomainExtends().length; i++) {
                SliderSettings sliderSettings = m_config.getSliderSettings();
                double min = sliderSettings.getRangeMinValue();
                double max = sliderSettings.getRangeMaxValue();
                int numHandles = sliderSettings.getStart().length;
                // recalculate start values
                if (m_config.getUseDomainExtends()[i]) {
                    sliderSettings.getStart()[i] =
                        SliderNodeDialogUI.calculateDomainExtendsStartValue(min, max, numHandles, i);
                }
            }
        }
    }

    private void disableSlider(final boolean disable) {
        RangeSliderFilterRepresentation rep = getViewRepresentation();
        if (rep != null) {
            rep.setDisabled(disable);
        }
    }

    private DataTableSpec setFilter(final DataTableSpec spec) throws InvalidSettingsException {
        String columnName = m_config.getDomainColumn().getStringValue();
        if (columnName == null) {
            throw new InvalidSettingsException("No domain column set");
        }
        RangeFilterValue value = getViewValue();
        double minimum;
        double maximum;
        if (value != null && value.getFilter() != null) {
            try {
                FilterModelRange filterModel = (FilterModelRange)value.getFilter().createFilterModel();
                minimum = filterModel.getMinimum().orElse(Double.NEGATIVE_INFINITY);
                maximum = filterModel.getMaximum().orElse(Double.POSITIVE_INFINITY);
            } catch (OperationNotSupportedException e) {
                throw new InvalidSettingsException(e);
            }
        } else {
            if (m_config.getSliderSettings() == null || m_config.getSliderSettings().getStart() == null) {
                setWarningMessage("No filter defined. Please configure the node.");
                return spec;
            }
            double[] filterValues = m_config.getSliderSettings().getStart();
            if (filterValues.length != 2) {
                throw new InvalidSettingsException(
                    "The filter settings for minimum and maximum are not in a correct format.");
            }
            minimum = Math.max(filterValues[0], Double.MIN_VALUE);
            maximum = Math.min(filterValues[1], Double.MAX_VALUE);
        }
        boolean[] fixSlider = m_config.getSliderSettings().getFix();
        if(m_config.getSliderSettings().getStep() != null) {
            double[] roundedExtremas = roundToNextStep(minimum, maximum);
            minimum = roundedExtremas[0];
            maximum = roundedExtremas[1];
        }
        if(fixSlider[0]) {
            minimum = Double.NEGATIVE_INFINITY;
        }
        if(fixSlider[2]) {
            maximum = Double.POSITIVE_INFINITY;
        }
        FilterModelRange model = FilterModel.newRangeModel(m_filterId, minimum, maximum, true, true);
        if (m_filterId == null) {
            m_filterId = model.getFilterUUID();
        }
        RangeSliderFilterRepresentation rep = getViewRepresentation();
        if (rep != null) {
            rep.setConfig(m_config);
            rep.setTableId(getTableId(0));
            rep.setFilterId(m_filterId.toString());
        }
        return getOutSpec(spec, columnName, FilterHandler.from(model));

    }

    /**
     * Function to round a value to the next available tick. If the maximum or minimum is passed in, it stays as is.
     *
     * @param value double which should be rounded to the next available tick
     */
    private double[] roundToNextStep(final double minimumVal, final double maximumVal) {
        double[] resultArray = new double[2];

        if (getViewRepresentation() != null && getViewRepresentation().getSliderSettings().getStep() != null) {
            resultArray[0] = calculateClosestValue(minimumVal, false);
            resultArray[1] = calculateClosestValue(maximumVal, true);
        }
        return resultArray;
    }

    private double calculateClosestValue(final double value, final boolean isMax) {
        final SliderSettings sliderSettings = getViewRepresentation().getSliderSettings();
        final BigDecimal stepSize = BigDecimal.valueOf(sliderSettings.getStep());
        final int decimalPlaces = stepSize.scale();
        final BigDecimal maximum =
            BigDecimal.valueOf(sliderSettings.getRangeMaxValue()).setScale(decimalPlaces, RoundingMode.UP);
        final BigDecimal minimum =
            BigDecimal.valueOf(sliderSettings.getRangeMinValue()).setScale(decimalPlaces, RoundingMode.DOWN);
        final BigDecimal valueBig;
        final boolean mod;

        // check for inifity values, as Big Decimals cannot handle does.
        if (value == Double.NEGATIVE_INFINITY || value == Double.POSITIVE_INFINITY) {
            return value;
        }

        if (!isMax) {
            valueBig = BigDecimal.valueOf(value).setScale(decimalPlaces, RoundingMode.HALF_DOWN);
        } else {
            valueBig = BigDecimal.valueOf(value).setScale(decimalPlaces, RoundingMode.HALF_UP);
        }

        if (BigDecimal.ZERO.compareTo(stepSize) != 0) {
            mod = BigDecimal.ZERO.compareTo(valueBig.subtract(minimum)
                .remainder(stepSize.setScale(decimalPlaces, RoundingMode.FLOOR))) == 0;
        } else {
            mod = true;
        }

        if (mod || valueBig.equals(minimum) || valueBig.equals(maximum)) {
            return valueBig.min(maximum).max(minimum).doubleValue();
        }
        //TODO: this leads to longer waiting times if there are many steps, should be refactored
        BigDecimal curStep = minimum;
        BigDecimal distance = BigDecimal.valueOf(Double.MAX_VALUE);
        BigDecimal closestStep = BigDecimal.ZERO;
        while (curStep.compareTo(maximum) < 0) {
            BigDecimal cdistance = curStep.subtract(valueBig).abs();
            if (cdistance.compareTo(distance) < -1){
                closestStep = curStep;
                distance = cdistance;
            }
            curStep =  curStep.add(stepSize);
        }

        final BigDecimal cdistance = maximum.subtract(valueBig).abs();

        if(cdistance.compareTo(distance) < 0){
            closestStep = maximum;
        }
        return closestStep.doubleValue();
    }

    private DataTableSpec getOutSpec(final DataTableSpec inSpec, final String columnName, final FilterHandler filter) {
        if (!inSpec.containsName(columnName)) {
            setWarningMessage("The defined filter column " + columnName
                + " is not part of the spec anymore. No filter definition appended. Slider will be disabled.");
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
            } else if (!m_config.getMergeWithExistingFiltersTable()) {
                // delete previously defined filters on demand
                cr.setFilterHandler(null);
            }
            cspecs[i] = cr.createSpec();
        }
        DataTableSpec outSpec = new DataTableSpec(cspecs);
        return outSpec;
    }

    private void setFilterOnValue() {
        RangeFilterValue value = getViewValue();
        boolean[] fixSlider = m_config.getSliderSettings().getFix();
        if (value != null && m_config.getSliderSettings() != null) {
            if (value.getFilter() == null) {
                RangeSelection filter = new RangeSelection();
                NumericColumnRangeSelection range = new NumericColumnRangeSelection();
                range.setColumnName(m_config.getDomainColumn().getStringValue());
                range.setMinimum(m_config.getSliderSettings().getStart()[0]);
                range.setMaximum(m_config.getSliderSettings().getStart()[1]);
                // Set the maximum to positive or negative infinity when the slider is fixed to one side.
                // As it is not possible to pass infinity values with jackson we need this hack.
                if(fixSlider[0]) {
                    range.setMinimum(Double.NEGATIVE_INFINITY);
                }
                if(fixSlider[2]) {
                    range.setMaximum(Double.POSITIVE_INFINITY);
                }
                filter.setColumns(new AbstractColumnRangeSelection[]{range});
                value.setFilter(filter);
            }
            value.getFilter().setId(getViewRepresentation().getFilterId());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RangeSliderFilterRepresentation createEmptyViewRepresentation() {
        return new RangeSliderFilterRepresentation(m_config);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RangeSliderFilterRepresentation getViewRepresentation() {
        RangeSliderFilterRepresentation rep = super.getViewRepresentation();
        synchronized(getLock()) {
            //make sure current table ids are used at all times
            if (rep != null) {
                rep.setTableId(getTableId(0));
            }
        }
        return rep;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RangeFilterValue createEmptyViewValue() {
        return new RangeFilterValue();
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
    public ValidationError validateViewValue(final RangeFilterValue viewContent) {
        NumericColumnRangeSelection selection = (NumericColumnRangeSelection)viewContent.getFilter().getColumns()[0];
        double min = selection.getMinimum();
        double max = selection.getMaximum();
        if (getViewRepresentation() != null && getViewRepresentation().getSliderSettings() != null) {
            SliderSettings settings = getViewRepresentation().getSliderSettings().clone();
            if(settings.getFix()[0]) {
                min = Double.NEGATIVE_INFINITY;
            } else if(settings.getFix()[2]) {
                max = Double.POSITIVE_INFINITY;
            }
            settings.setStart(new double[]{min, max});
            try {
                settings.validateSettings();
            } catch (InvalidSettingsException e) {
                return new ValidationError(e.getMessage());
            }
        }
        return null;
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
    protected void useCurrentValueAsDefault() {
        RangeFilterValue value = getViewValue();
        if (value != null && value.getFilter() != null && m_config.getSliderSettings() != null) {
            AbstractColumnRangeSelection[] columns = value.getFilter().getColumns();
            if (columns != null && columns.length > 0 && columns[0] instanceof NumericColumnRangeSelection) {
                NumericColumnRangeSelection filter = (NumericColumnRangeSelection)columns[0];
                double[] startValues = new double[]{filter.getMinimum(), filter.getMaximum()};
                m_config.setUseDomainExtends(new boolean[]{false, false});
                m_config.getSliderSettings().setStart(startValues);
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
        (new RangeSliderFilterConfig()).loadSettings(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadValidatedSettingsFrom(final NodeSettingsRO settings) throws InvalidSettingsException {
        m_config.loadSettings(settings);
    }
}
