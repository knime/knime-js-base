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
 *  propagated with or for interoperation with KNIME. The owner of a Node
 *  may freely choose the license terms applicable to such Node, including
 *  when such Node is propagated with or for interoperation with KNIME.
 * ------------------------------------------------------------------------
 *
 * History
 *   13.05.2014 (Christian Albrecht, KNIME AG, Zurich, Switzerland): created
 */
package org.knime.js.base.node.viz.plotter.roc;

import java.awt.Color;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import org.knime.base.data.xml.SvgCell;
import org.knime.base.node.viz.roc.ROCCalculator;
import org.knime.base.node.viz.roc.ROCCurve;
import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.DataType;
import org.knime.core.data.DoubleValue;
import org.knime.core.data.StringValue;
import org.knime.core.data.container.ColumnRearranger;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.port.PortObject;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.port.PortType;
import org.knime.core.node.port.image.ImagePortObject;
import org.knime.core.node.port.image.ImagePortObjectSpec;
import org.knime.core.node.port.inactive.InactiveBranchPortObjectSpec;
import org.knime.core.node.util.filter.NameFilterConfiguration.FilterResult;
import org.knime.core.node.web.ValidationError;
import org.knime.core.node.wizard.CSSModifiable;
import org.knime.js.core.JSONDataTable;
import org.knime.js.core.layout.LayoutTemplateProvider;
import org.knime.js.core.layout.bs.JSONLayoutViewContent;
import org.knime.js.core.layout.bs.JSONLayoutViewContent.ResizeMethod;
import org.knime.js.core.node.AbstractSVGWizardNodeModel;
import org.knime.js.core.settings.ValueStore;

/**
 *
 * @author Christian Albrecht, KNIME AG, Zurich, Switzerland, University of Konstanz
 */
final class ROCCurveNodeModel extends AbstractSVGWizardNodeModel<ROCCurveViewRepresentation,
        ROCCurveViewValue> implements LayoutTemplateProvider, CSSModifiable {

    private static final NodeLogger LOGGER = NodeLogger.getLogger(ROCCurveNodeModel.class);

    private final ROCCurveViewConfig m_config;

    private BufferedDataTable m_table;

    static final String ROC_CALCULATOR_WARNING_ID = "RocCalculatorWarning";

    private ValueStore m_valueStore;

    /**
     * Creates a new model instance.
     */
    ROCCurveNodeModel(final String viewName) {
        super(new PortType[]{BufferedDataTable.TYPE, new PortType(BufferedDataTable.class, true)},
            new PortType[]{ImagePortObject.TYPE, BufferedDataTable.TYPE}, viewName);
        m_config = new ROCCurveViewConfig();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected PortObjectSpec[] configure(final PortObjectSpec[] inSpecs) throws InvalidSettingsException {

        DataTableSpec tableSpec = (DataTableSpec)inSpecs[0];

        if (!tableSpec.containsName(m_config.getRocSettings().getClassColumn())) {
            throw new InvalidSettingsException("Class column '"
                    + m_config.getRocSettings().getClassColumn() + " ' does not exist");
        }

        FilterResult res = m_config.getRocSettings().getNumericCols().applyTo(tableSpec);
        if (res.getIncludes().length == 0) {
            throw new InvalidSettingsException("No curves included");
        }

        if (m_config.getRocSettings().getPositiveClass() == null) {
            throw new InvalidSettingsException(
                    "No value for the positive class chosen");
        }

        PortObjectSpec imageSpec;
        if (generateImage()) {
            imageSpec = new ImagePortObjectSpec(SvgCell.TYPE);
        } else {
            imageSpec = InactiveBranchPortObjectSpec.INSTANCE;
        }
        return new PortObjectSpec[]{imageSpec, ROCCalculator.OUT_SPEC};
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ROCCurveViewRepresentation createEmptyViewRepresentation() {
        return new ROCCurveViewRepresentation();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ROCCurveViewValue createEmptyViewValue() {
        return new ROCCurveViewValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getJavascriptObjectID() {
        return "org.knime.js.base.node.viz.plotter.roc";
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
    public ValidationError validateViewValue(final ROCCurveViewValue viewContent) {
        synchronized (getLock()) {
            // validate value, nothing to do atm
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveCurrentValue(final NodeSettingsWO content) {
        // do nothing
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void performExecuteCreateView(final PortObject[] inData, final ExecutionContext exec) throws Exception {
        synchronized (getLock()) {
            BufferedDataTable table = (BufferedDataTable)inData[0];
            BufferedDataTable colorTable = (BufferedDataTable)inData[1];

            ROCCurveViewRepresentation representation = getViewRepresentation();
            if (representation.getCurves() == null) {

                // Fix for AP-5696: JS ROC Plot chokes if some of the previously selected cols are no longer available
                FilterResult res = m_config.getRocSettings().getNumericCols().applyTo(table.getSpec());

                ROCCalculator calc = new ROCCalculator(Arrays.asList(res.getIncludes()),
                    m_config.getRocSettings().getClassColumn(), m_config.getRocSettings().getMaxPoints(),
                    m_config.getRocSettings().getPositiveClass().toString(),
                    m_config.getIgnoreMissingValues());

                calc.calculateCurveData(table, exec);

                // Make curves serializable
                List<ROCCurve> calcCurves = calc.getOutputCurves();
                JSONROCCurve[] curves = new JSONROCCurve[calcCurves.size()];
                for (int i = 0; i < calcCurves.size(); i++) {
                    curves[i] = new JSONROCCurve(calcCurves.get(i));
                }
                representation.setCurves(curves);

                if (colorTable != null) {
                    HashMap<String, String> colors = new HashMap<>();
                    for (DataRow row : colorTable) {
                        String col = row.getCell(0).toString();
                        Color c = colorTable.getSpec().getRowColor(row).getColor();
                        colors.put(col, ROCCurveViewConfig.getRGBAStringFromColor(c));
                    }
                    String[] col = new String[colors.size()];
                    for (int i = 0; i < representation.getCurves().length; i++) {
                        ROCCurve curve = representation.getCurves()[i];
                        String color = colors.get(curve.getName());
                        col[i] = color;
                    }
                    representation.setColors(col);
                }

                m_table = calc.getOutputTable();
                copyConfigToView();

                String warnMsg = calc.getWarningMessage();
                if (warnMsg != null && !warnMsg.isEmpty()) {
                    setWarningMessage(warnMsg);
                    if (m_config.getShowWarningInView()) {
                        getViewRepresentation().getWarnings().setWarningMessage(warnMsg, ROC_CALCULATOR_WARNING_ID);
                    }
                }

                // don't use staggered rendering and resizing for image creation
                representation.setId(getTableId(0));
                representation.setEnableStaggeredRendering(false);
                representation.setResizeToWindow(false);
            }
        }
    }

    private void copyConfigToView() {
        ROCCurveViewRepresentation representation = getViewRepresentation();
        representation.setResizeToWindow(m_config.getResizeToWindow());
        representation.setShowGrid(m_config.getShowGrid());
        representation.setImageHeight(m_config.getImageHeight());
        representation.setImageWidth(m_config.getImageWidth());
        representation.setLineWidth(m_config.getLineWidth());
        representation.setGridColor(m_config.getGridColor());
        representation.setBackgroundColor(m_config.getBackgroundColor());
        representation.setDataAreaColor(m_config.getDataAreaColor());
        representation.setShowArea(m_config.getShowArea());
        representation.setShowLegend(m_config.getShowLegend());
        // added with 3.3
        representation.setDisplayFullscreenButton(m_config.getDisplayFullscreenButton());

        representation.setEnableControls(m_config.getEnableControls());
        representation.setEnableEditSubtitle(m_config.getEnableEditSubtitle());
        representation.setEnableEditTitle(m_config.getEnableEditTitle());
        representation.setEnableEditXAxisLabel(m_config.getEnableEditXAxisLabel());
        representation.setEnableEditYAxisLabel(m_config.getEnableEditYAxisLabel());

        // added with 3.4
        representation.setShowWarningInView(m_config.getShowWarningInView());

        if (m_valueStore == null) {
            m_valueStore = new ValueStore();
        } else if (isViewValueEmpty()) {
            m_valueStore.clear();
        }
        ROCCurveViewValue value = getViewValue();
        m_valueStore.storeAndTransfer(ROCCurveViewConfig.TITLE, m_config.getTitle(), value::setTitle);
        m_valueStore.storeAndTransfer(ROCCurveViewConfig.SUBTITLE, m_config.getSubtitle(), value::setSubtitle);
        m_valueStore.storeAndTransfer(ROCCurveViewConfig.X_AXIS_TITLE, m_config.getxAxisTitle(), value::setxAxisTitle);
        m_valueStore.storeAndTransfer(ROCCurveViewConfig.Y_AXIS_TITLE, m_config.getyAxisTitle(), value::setyAxisTitle);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected PortObject[] performExecuteCreatePortObjects(final PortObject svgImageFromView,
        final PortObject[] inObjects, final ExecutionContext exec) throws Exception {
        synchronized (getLock()) {
            ROCCurveViewRepresentation representation = getViewRepresentation();
            // enable staggered rendering and resizing for interactive view
            representation.setEnableStaggeredRendering(true);
            representation.setResizeToWindow(m_config.getResizeToWindow());
        }
        exec.setProgress(1);
        return new PortObject[]{svgImageFromView, m_table};
    }

    private ColumnRearranger createNumericColumnRearranger(final DataTableSpec in) {
        ColumnRearranger c = new ColumnRearranger(in);
        for (DataColumnSpec colSpec : in) {
            DataType type = colSpec.getType();
            if (!type.isCompatible(DoubleValue.class) && !type.isCompatible(StringValue.class)) {
                c.remove(colSpec.getName());
            }
        }
        return c;
    }

    private int getOrdinalFromStringValue(final String stringValue, final JSONDataTable table, final int colID) {
        LinkedHashSet<Object> possibleValues = table.getSpec().getPossibleValues().get(colID);
        if (possibleValues != null) {
            int ordinal = 0;
            for (Object value : possibleValues) {
                if (value != null && value.equals(stringValue)) {
                    return ordinal;
                }
                ordinal++;
            }
        }
        return -1;
    }

    private Map<String, String> getSymbolMap(final LinkedHashSet<Object> linkedHashSet) {
        Map<String, String> symbolMap = new HashMap<String, String>();
        Integer ordinal = 0;
        for (Object value : linkedHashSet) {
            symbolMap.put(ordinal.toString(), value.toString());
            ordinal++;
        }
        return symbolMap;
    }

    private String getColorForColumn(final String colKey, final JSONDataTable colorTable) {
        if (colKey != null && colorTable != null) {
            for (int row = 0; row < colorTable.getRows().length; row++) {
                if (colKey.equals(colorTable.getRows()[row].getData()[0].toString())) {
                    return colorTable.getSpec().getRowColorValues()[row];
                }
            }
        }
        return null;
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
    protected void saveSettingsTo(final NodeSettingsWO settings) {
        m_config.saveSettings(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void validateSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
        ROCCurveViewConfig cfg = new ROCCurveViewConfig();
        cfg.loadSettings(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadValidatedSettingsFrom(final NodeSettingsRO settings) throws InvalidSettingsException {
        m_config.loadSettings(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void useCurrentValueAsDefault() {
        synchronized (getLock()) {
            copyValueToConfig();
        }
    }

    /**
     *
     */
    private void copyValueToConfig() {
        ROCCurveViewValue val = getViewValue();
        m_config.setTitle(val.getTitle());
        m_config.setSubtitle(val.getSubtitle());
        m_config.setxAxisTitle(val.getxAxisTitle());
        m_config.setyAxisTitle(val.getyAxisTitle());


    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean generateImage() {
        return m_config.getGenerateImage();
    }

    private Double getMinimumFromColumns(final DataTableSpec spec, final String... columnNames) {
        double minimum = Double.MAX_VALUE;
        for (String column : columnNames) {
            DataColumnSpec colSpec = spec.getColumnSpec(column);
            if (colSpec != null) {
                DataCell lowerCell = colSpec.getDomain().getLowerBound();
                if ((lowerCell != null) && lowerCell.getType().isCompatible(DoubleValue.class)) {
                    minimum = Math.min(minimum, ((DoubleValue)lowerCell).getDoubleValue());
                }
            }
        }
        if (minimum < Double.MAX_VALUE) {
            return minimum;
        }
        return null;
    }

    private Double getMaximumFromColumns(final DataTableSpec spec, final String... columnNames) {
        double maximum = Double.MIN_VALUE;
        for (String column : columnNames) {
            DataColumnSpec colSpec = spec.getColumnSpec(column);
            if (colSpec != null) {
                DataCell upperCell = colSpec.getDomain().getUpperBound();
                if ((upperCell != null) && upperCell.getType().isCompatible(DoubleValue.class)) {
                    maximum = Math.max(maximum, ((DoubleValue)upperCell).getDoubleValue());
                }
            }
        }
        if (maximum > Double.MIN_VALUE) {
            return maximum;
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JSONLayoutViewContent getLayoutTemplate() {
        JSONLayoutViewContent template = new JSONLayoutViewContent();
        if (m_config.getResizeToWindow()) {
            template.setResizeMethod(ResizeMethod.ASPECT_RATIO_16by9);
        } else {
            template.setResizeMethod(ResizeMethod.VIEW_LOWEST_ELEMENT);
        }
        return template;
    }

}
