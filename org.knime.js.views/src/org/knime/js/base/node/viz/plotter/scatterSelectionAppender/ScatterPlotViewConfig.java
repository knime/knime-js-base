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
 *   14.05.2014 (Christian Albrecht, KNIME AG, Zurich, Switzerland): created
 */
package org.knime.js.base.node.viz.plotter.scatterSelectionAppender;

import java.awt.Color;
import java.util.Map;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.util.StringHistory;
import org.knime.js.base.node.viz.plotter.line.LinePlotViewConfig;
import org.knime.js.core.components.datetime.ConversionDateTimeFormat;
import org.knime.js.core.components.datetime.SettingsModelDateTimeOptions;

/**
 *
 * @author Christian Albrecht, KNIME AG, Zurich, Switzerland, University of Konstanz
 */
final class ScatterPlotViewConfig {

    static final int DEFAULT_MAX_ROWS = 2500;
    static final String DEFAULT_SELECTION_COLUMN_NAME = "Selected (Scatter Plot)";
    static final int DEFAULT_WIDTH = 800;
    static final int DEFAULT_HEIGHT = 600;
    static final int DEFAULT_DOT_SIZE = 3;
    static final String COLOR_STRING_PREFIX = "rgba(";
    static final Color DEFAULT_BACKGROUND_COLOR = new Color(255, 255, 255);
    static final Color DEFAULT_DATA_AREA_COLOR = new Color(230, 230, 230);
    static final Color DEFAULT_GRID_COLOR = new Color(255, 255, 255);
    final static boolean DEFAULT_DISPLAY_FULLSCREEN_BUTTON = true;
    final static boolean DEFAULT_PUBLISH_SELECTION = true;
    final static boolean DEFAULT_ENABLE_SHOW_SELECTED_ONLY = true;
    final static boolean DEFAULT_SUBSCRIBE_SELECTION = true;
    final static boolean DEFAULT_SUBSCRIBE_FILTER = true;
    final static boolean DEFAULT_SHOW_WARNING_IN_VIEW = true;
    final static boolean DEFAULT_REPORT_ON_MISSING_VALUES = true;

    static final String HIDE_IN_WIZARD = "hideInWizard";
    static final String GENERATE_IMAGE = "generateImage";
    static final String SHOW_LEGEND = "showLegend";
    final static String CFG_DISPLAY_FULLSCREEN_BUTTON = "displayFullscreenButton";
    static final String AUTO_RANGE_AXES = "autoRange";
    static final String USE_DOMAIN_INFO = "useDomainInformation";
    static final String SHOW_GRID = "showGrid";
    static final String SHOW_CROSSHAIR = "showCrosshair";
    static final String SNAP_TO_POINTS = "snapToPoints";
    static final String RESIZE_TO_WINDOW = "resizeToWindow";
    static final String ENABLE_CONFIG = "enableViewConfiguration";
    static final String ENABLE_TTILE_CHANGE = "enableTitleChange";
    static final String ENABLE_SUBTTILE_CHANGE = "enableSubtitleChange";
    static final String ENABLE_X_COL_CHANGE = "enableXColumnChange";
    static final String ENABLE_Y_COL_CHANGE = "enableYColumnChange";
    static final String ENABLE_X_LABEL_EDIT = "enableXAxisLabelEdit";
    static final String ENABLE_Y_LABEL_EDIT = "enableYAxisLabelEdit";
    static final String ENABLE_DOT_SIZE_CHANGE = "enableDotSizeChange";
    static final String ENABLE_SWITCH_LEGEND = "enableSwitchLegend";
    static final String ENABLE_ZOOMING = "enableZooming";
    static final String ENABLE_DRAG_ZOOMING = "enableDragZooming";
    static final String ENABLE_PANNING = "enablePanning";
    static final String SHOW_ZOOM_RESET_BUTTON = "showZoomResetButton";
    static final String ENABLE_SELECTION = "enableSelection";
    static final String ENABLE_RECTANGLE_SELECTION = "enableRectangleSelection";
    static final String ENABLE_LASSO_SELECTION = "enableLassoSelection";
    final static String CFG_PUBLISH_SELECTION = "publishSelection";
    final static String CFG_ENABLE_SHOW_SELECTED_ONLY = "enableShowSelectedOnly";
    final static String CFG_SUBSCRIBE_SELECTION = "subscribeSelection";
    final static String CFG_SUBSCRIBE_FILTER = "subscribeFilter";
    static final String CHART_TITLE = "chartTitle";
    static final String CHART_SUBTITLE = "chartSubtitle";
    static final String X_COL = "xCol";
    static final String Y_COL = "yCol";
    static final String MAX_ROWS = "maxRows";
    static final String SELECTION_COLUMN_NAME = "selectionColumnName";
    static final String X_AXIS_LABEL = "xAxisLabel";
    static final String Y_AXIS_LABEL = "yAxisLabel";
    static final String X_AXIS_MIN = "xAxisMin";
    static final String X_AXIS_MAX = "xAxisMax";
    static final String Y_AXIS_MIN = "yAxisMin";
    static final String Y_AXIS_MAX = "yAxisMax";
    static final String DOT_SIZE = "dot_size";
    static final String DATE_FORMAT = "date_format"; // legacy date&time format
    static final String IMAGE_WIDTH = "imageWidth";
    static final String IMAGE_HEIGHT = "imageHeight";
    static final String BACKGROUND_COLOR = "backgroundColor";
    static final String DATA_AREA_COLOR = "dataAreaColor";
    static final String GRID_COLOR = "gridColor";
    static final String SHOW_WARNING_IN_VIEW = "showWarningInView";
    static final String REPORT_ON_MISSING_VALUES = "reportOnMissingValues";
    static final String DATE_TIME_FORMATS = "dateTimeFormats";  // new date&time formats
    static final String CUSTOM_CSS = "customCSS";

    private boolean m_hideInWizard = false;
    private boolean m_generateImage = true;
    private boolean m_showLegend = true;
    private boolean m_displayFullscreenButton = DEFAULT_DISPLAY_FULLSCREEN_BUTTON;
    private boolean m_autoRangeAxes = true;
    private boolean m_useDomainInfo = false;
    private boolean m_showGrid = true;
    private boolean m_showCrosshair = false;
    private boolean m_snapToPoints = false;
    private boolean m_resizeToWindow = true;
    private boolean m_enableViewConfiguration = true;
    private boolean m_enableTitleChange = true;
    private boolean m_enableSubtitleChange = true;
    private boolean m_enableXColumnChange = true;
    private boolean m_enableYColumnChange = true;
    private boolean m_enableXAxisLabelEdit = true;
    private boolean m_enableYAxisLabelEdit = true;
    private boolean m_enableDotSizeChange = false;
    private boolean m_enableSwitchLegend = true;
    private boolean m_enableZooming = true;
    private boolean m_enablePanning = true;
    private boolean m_enableDragZooming = false;
    private boolean m_showZoomResetButton = false;
    private boolean m_enableSelection = true;
    private boolean m_enableRectangleSelection = true;
    private boolean m_enableLassoSelection = false;
    private boolean m_publishSelection = DEFAULT_PUBLISH_SELECTION;
    private boolean m_subscribeSelection = DEFAULT_SUBSCRIBE_SELECTION;
    private boolean m_enableShowSelectedOnly = DEFAULT_ENABLE_SHOW_SELECTED_ONLY;
    private boolean m_subscribeFilter = DEFAULT_SUBSCRIBE_FILTER;
    private int m_maxRows = DEFAULT_MAX_ROWS;
    private String m_selectionColumnName = DEFAULT_SELECTION_COLUMN_NAME;
    private String m_chartTitle;
    private String m_chartSubtitle;
    private String m_xColumn;
    private String m_yColumn;
    private String m_xAxisLabel;
    private String m_yAxisLabel;
    private Double m_xAxisMin;
    private Double m_xAxisMax;
    private Double m_yAxisMin;
    private Double m_yAxisMax;
    private Integer m_dotSize = DEFAULT_DOT_SIZE;
    private int m_imageWidth = DEFAULT_WIDTH;
    private int m_imageHeight = DEFAULT_HEIGHT;
    private Color m_backgroundColor = DEFAULT_BACKGROUND_COLOR;
    private Color m_dataAreaColor = DEFAULT_DATA_AREA_COLOR;
    private Color m_gridColor = DEFAULT_GRID_COLOR;
    private boolean m_showWarningInView = DEFAULT_SHOW_WARNING_IN_VIEW;
    private boolean m_reportOnMissingValues = DEFAULT_REPORT_ON_MISSING_VALUES;
    private SettingsModelDateTimeOptions m_dateTimeFormats = new SettingsModelDateTimeOptions(DATE_TIME_FORMATS);
    private static Map<String, String> m_conversionDateTimeFormatMap = null;
    private String m_customCSS;

    /**
     * @return the hideInWizard
     */
    public boolean getHideInWizard() {
        return m_hideInWizard;
    }

    /**
     * @param hideInWizard the hideInWizard to set
     */
    public void setHideInWizard(final boolean hideInWizard) {
        m_hideInWizard = hideInWizard;
    }

    /**
     * @return the generateImage
     */
    public boolean getGenerateImage() {
        return m_generateImage;
    }

    /**
     * @param generateImage the generateImage to set
     */
    public void setGenerateImage(final boolean generateImage) {
        m_generateImage = generateImage;
    }

    /**
     * @return the showLegend
     */
    public boolean getShowLegend() {
        return m_showLegend;
    }

    /**
     * @param showLegend the showLegend to set
     */
    public void setShowLegend(final boolean showLegend) {
        m_showLegend = showLegend;
    }

    /**
     * @return the displayFullscreenButton
     */
    public boolean getDisplayFullscreenButton() {
        return m_displayFullscreenButton;
    }

    /**
     * @param displayFullscreenButton the displayFullscreenButton to set
     */
    public void setDisplayFullscreenButton(final boolean displayFullscreenButton) {
        m_displayFullscreenButton = displayFullscreenButton;
    }

    /**
     * @return the autoRangeAxes
     */
    public boolean getAutoRangeAxes() {
        return m_autoRangeAxes;
    }

    /**
     * @param autoRangeAxes the autoRangeAxes to set
     */
    public void setAutoRangeAxes(final boolean autoRangeAxes) {
        m_autoRangeAxes = autoRangeAxes;
    }

    /**
     * @return the useDomainInfo
     */
    public boolean getUseDomainInfo() {
        return m_useDomainInfo;
    }

    /**
     * @param useDomainInfo the useDomainInfo to set
     */
    public void setUseDomainInfo(final boolean useDomainInfo) {
        m_useDomainInfo = useDomainInfo;
    }

    /**
     * @return the showGrid
     */
    public boolean getShowGrid() {
        return m_showGrid;
    }

    /**
     * @param showGrid the showGrid to set
     */
    public void setShowGrid(final boolean showGrid) {
        m_showGrid = showGrid;
    }

    /**
     * @return the showCrosshair
     */
    public boolean getShowCrosshair() {
        return m_showCrosshair;
    }

    /**
     * @param showCrosshair the showCrosshair to set
     */
    public void setShowCrosshair(final boolean showCrosshair) {
        m_showCrosshair = showCrosshair;
    }

    /**
     * @return the snapToPoints
     */
    public boolean getSnapToPoints() {
        return m_snapToPoints;
    }

    /**
     * @param snapToPoints the snapToPoints to set
     */
    public void setSnapToPoints(final boolean snapToPoints) {
        m_snapToPoints = snapToPoints;
    }

    /**
     * @return the enableSelection
     */
    public boolean getEnableSelection() {
        return m_enableSelection;
    }

    /**
     * @param enableSelection the enableSelection to set
     */
    public void setEnableSelection(final boolean enableSelection) {
        m_enableSelection = enableSelection;
    }

    /**
     * @return the enableRectangleSelection
     */
    public boolean getEnableRectangleSelection() {
        return m_enableRectangleSelection;
    }

    /**
     * @param enableRectangleSelection the enableRectangleSelection to set
     */
    public void setEnableRectangleSelection(final boolean enableRectangleSelection) {
        m_enableRectangleSelection = enableRectangleSelection;
    }

    /**
     * @return the enableLassoSelection
     */
    public boolean getEnableLassoSelection() {
        return m_enableLassoSelection;
    }

    /**
     * @param enableLassoSelection the enableLassoSelection to set
     */
    public void setEnableLassoSelection(final boolean enableLassoSelection) {
        m_enableLassoSelection = enableLassoSelection;
    }

    /**
     * @return the publishSelection
     */
    public boolean getPublishSelection() {
        return m_publishSelection;
    }

    /**
     * @param publishSelection the publishSelection to set
     */
    public void setPublishSelection(final boolean publishSelection) {
        m_publishSelection = publishSelection;
    }

    /**
     * @return the enableShowSelectedOnly
     */
    public boolean getEnableShowSelectedOnly() {
        return m_enableShowSelectedOnly;
    }

    /**
     * @param enableShowSelectedOnly the enableShowSelectedOnly to set
     */
    public void setEnableShowSelectedOnly(final boolean enableShowSelectedOnly) {
        m_enableShowSelectedOnly = enableShowSelectedOnly;
    }


    /**
     * @return the subscribeSelection
     */
    public boolean getSubscribeSelection() {
        return m_subscribeSelection;
    }

    /**
     * @param subscribeSelection the subscribeSelection to set
     */
    public void setSubscribeSelection(final boolean subscribeSelection) {
        m_subscribeSelection = subscribeSelection;
    }

    /**
     * @return the subscribeFilter
     */
    public boolean getSubscribeFilter() {
        return m_subscribeFilter;
    }

    /**
     * @param subscribeFilter the subscribeFilter to set
     */
    public void setSubscribeFilter(final boolean subscribeFilter) {
        m_subscribeFilter = subscribeFilter;
    }

    /**
     * @return the imageWidth
     */
    public int getImageWidth() {
        return m_imageWidth;
    }

    /**
     * @param imageWidth the imageWidth to set
     */
    public void setImageWidth(final int imageWidth) {
        m_imageWidth = imageWidth;
    }

    /**
     * @return the imageHeight
     */
    public int getImageHeight() {
        return m_imageHeight;
    }

    /**
     * @param imageHeight the imageHeight to set
     */
    public void setImageHeight(final int imageHeight) {
        m_imageHeight = imageHeight;
    }

    /**
     * @return the resizeToWindow
     */
    public boolean getResizeToWindow() {
        return m_resizeToWindow;
    }

    /**
     * @param resizeToWindow the resizeToWindow to set
     */
    public void setResizeToWindow(final boolean resizeToWindow) {
        m_resizeToWindow = resizeToWindow;
    }

    /**
     * @return the backgroundColor
     */
    public Color getBackgroundColor() {
        return m_backgroundColor;
    }

    /**
     * @return the backgroundColor as rgba string
     */
    public String getBackgroundColorString() {
        return LinePlotViewConfig.getRGBAStringFromColor(m_backgroundColor);
    }

    /**
     * @param backgroundColor the backgroundColor to set
     */
    public void setBackgroundColor(final Color backgroundColor) {
        m_backgroundColor = backgroundColor;
    }

    /**
     * @return the dataAreaColor
     */
    public Color getDataAreaColor() {
        return m_dataAreaColor;
    }

    /**
     * @return the data area color as rgba string
     */
    public String getDataAreaColorString() {
        return LinePlotViewConfig.getRGBAStringFromColor(m_dataAreaColor);
    }

    /**
     * @param dataAreaColor the dataAreaColor to set
     */
    public void setDataAreaColor(final Color dataAreaColor) {
        m_dataAreaColor = dataAreaColor;
    }

    /**
     * @return the gridColor
     */
    public Color getGridColor() {
        return m_gridColor;
    }

    /**
     * @return the grid color as rgba string
     */
    public String getGridColorString() {
        return LinePlotViewConfig.getRGBAStringFromColor(m_gridColor);
    }

    /**
     * @param gridColor the gridColor to set
     */
    public void setGridColor(final Color gridColor) {
        m_gridColor = gridColor;
    }

    /**
     * @return the chartTitle
     */
    public String getChartTitle() {
        return m_chartTitle;
    }

    /**
     * @param chartTitle the chartTitle to set
     */
    public void setChartTitle(final String chartTitle) {
        m_chartTitle = chartTitle;
    }

    /**
     * @return the chartSubtitle
     */
    public String getChartSubtitle() {
        return m_chartSubtitle;
    }

    /**
     * @param chartSubtitle the chartSubtitle to set
     */
    public void setChartSubtitle(final String chartSubtitle) {
        m_chartSubtitle = chartSubtitle;
    }

    /**
     * @return the xColumn
     */
    public String getxColumn() {
        return m_xColumn;
    }

    /**
     * @param xColumn the xColumn to set
     */
    public void setxColumn(final String xColumn) {
        m_xColumn = xColumn;
    }

    /**
     * @return the yColumn
     */
    public String getyColumn() {
        return m_yColumn;
    }

    /**
     * @param yColumn the yColumn to set
     */
    public void setyColumn(final String yColumn) {
        m_yColumn = yColumn;
    }

    /**
     * @return the maxRows
     */
    public int getMaxRows() {
        return m_maxRows;
    }

    /**
     * @param maxRows the maxRows to set
     */
    public void setMaxRows(final int maxRows) {
        m_maxRows = maxRows;
    }

    /**
     * @return the selectionColumnName
     */
    public String getSelectionColumnName() {
        return m_selectionColumnName;
    }

    /**
     * @param selectionColumnName the selectionColumnName to set
     */
    public void setSelectionColumnName(final String selectionColumnName) {
        m_selectionColumnName = selectionColumnName;
    }

    /**
     * @return the xAxisLabel
     */
    public String getxAxisLabel() {
        return m_xAxisLabel;
    }

    /**
     * @param xAxisLabel the xAxisLabel to set
     */
    public void setxAxisLabel(final String xAxisLabel) {
        m_xAxisLabel = xAxisLabel;
    }

    /**
     * @return the yAxisLabel
     */
    public String getyAxisLabel() {
        return m_yAxisLabel;
    }

    /**
     * @param yAxisLabel the yAxisLabel to set
     */
    public void setyAxisLabel(final String yAxisLabel) {
        m_yAxisLabel = yAxisLabel;
    }

    /**
     * @return the allowViewConfiguration
     */
    public boolean getEnableViewConfiguration() {
        return m_enableViewConfiguration;
    }

    /**
     * @param enableViewConfiguration the allowViewConfiguration to set
     */
    public void setEnableViewConfiguration(final boolean enableViewConfiguration) {
        m_enableViewConfiguration = enableViewConfiguration;
    }

    /**
     * @return the allowTitleChange
     */
    public boolean getEnableTitleChange() {
        return m_enableTitleChange;
    }

    /**
     * @param enableTitleChange the allowTitleChange to set
     */
    public void setEnableTitleChange(final boolean enableTitleChange) {
        m_enableTitleChange = enableTitleChange;
    }

    /**
     * @return the allowSubtitleChange
     */
    public boolean getEnableSubtitleChange() {
        return m_enableSubtitleChange;
    }

    /**
     * @param enableSubtitleChange the allowSubtitleChange to set
     */
    public void setEnableSubtitleChange(final boolean enableSubtitleChange) {
        m_enableSubtitleChange = enableSubtitleChange;
    }

    /**
     * @return the enableXColumnChange
     */
    public boolean getEnableXColumnChange() {
        return m_enableXColumnChange;
    }

    /**
     * @param enableXColumnChange the enableXColumnChange to set
     */
    public void setEnableXColumnChange(final boolean enableXColumnChange) {
        m_enableXColumnChange = enableXColumnChange;
    }

    /**
     * @return the enableYColumnChange
     */
    public boolean getEnableYColumnChange() {
        return m_enableYColumnChange;
    }

    /**
     * @param enableYColumnChange the enableYColumnChange to set
     */
    public void setEnableYColumnChange(final boolean enableYColumnChange) {
        m_enableYColumnChange = enableYColumnChange;
    }

    /**
     * @return the enableXAxisLabelEdit
     */
    public boolean getEnableXAxisLabelEdit() {
        return m_enableXAxisLabelEdit;
    }

    /**
     * @param enableXAxisLabelEdit the enableXAxisLabelEdit to set
     */
    public void setEnableXAxisLabelEdit(final boolean enableXAxisLabelEdit) {
        m_enableXAxisLabelEdit = enableXAxisLabelEdit;
    }

    /**
     * @return the enableYAxisLabelEdit
     */
    public boolean getEnableYAxisLabelEdit() {
        return m_enableYAxisLabelEdit;
    }

    /**
     * @param enableYAxisLabelEdit the enableYAxisLabelEdit to set
     */
    public void setEnableYAxisLabelEdit(final boolean enableYAxisLabelEdit) {
        m_enableYAxisLabelEdit = enableYAxisLabelEdit;
    }

    /**
     * @return the allowDotSizeChange
     */
    public boolean getEnableDotSizeChange() {
        return m_enableDotSizeChange;
    }

    /**
     * @param enableDotSizeChange the allowDotSizeChange to set
     */
    public void setEnableDotSizeChange(final boolean enableDotSizeChange) {
        m_enableDotSizeChange = enableDotSizeChange;
    }

    /**
     * @return the xAxisMin
     */
    public Double getxAxisMin() {
        return m_xAxisMin;
    }

    /**
     * @param xAxisMin the xAxisMin to set
     */
    public void setxAxisMin(final Double xAxisMin) {
        m_xAxisMin = xAxisMin;
    }

    /**
     * @return the xAxisMax
     */
    public Double getxAxisMax() {
        return m_xAxisMax;
    }

    /**
     * @param xAxisMax the xAxisMax to set
     */
    public void setxAxisMax(final Double xAxisMax) {
        m_xAxisMax = xAxisMax;
    }

    /**
     * @return the yAxisMin
     */
    public Double getyAxisMin() {
        return m_yAxisMin;
    }

    /**
     * @param yAxisMin the yAxisMin to set
     */
    public void setyAxisMin(final Double yAxisMin) {
        m_yAxisMin = yAxisMin;
    }

    /**
     * @return the yAxisMax
     */
    public Double getyAxisMax() {
        return m_yAxisMax;
    }

    /**
     * @param yAxisMax the yAxisMax to set
     */
    public void setyAxisMax(final Double yAxisMax) {
        m_yAxisMax = yAxisMax;
    }

   /**
     * @return the enableSwitchLegend
     */
    public boolean getEnableSwitchLegend() {
        return m_enableSwitchLegend;
    }

    /**
     * @param enableSwitchLegend the enableSwitchLegend to set
     */
    public void setEnableSwitchLegend(final boolean enableSwitchLegend) {
        m_enableSwitchLegend = enableSwitchLegend;
    }

    /**
     * @return the allowZooming
     */
    public boolean getEnableZooming() {
        return m_enableZooming;
    }

    /**
     * @param enableZooming the allowZooming to set
     */
    public void setEnableZooming(final boolean enableZooming) {
        m_enableZooming = enableZooming;
    }

    /**
     * @return the allowPanning
     */
    public boolean getEnablePanning() {
        return m_enablePanning;
    }

    /**
     * @param enablePanning the allowPanning to set
     */
    public void setEnablePanning(final boolean enablePanning) {
        m_enablePanning = enablePanning;
    }

    /**
     * @return the enableDragZooming
     */
    public boolean getEnableDragZooming() {
        return m_enableDragZooming;
    }

    /**
     * @param enableDragZooming the enableDragZooming to set
     */
    public void setEnableDragZooming(final boolean enableDragZooming) {
        m_enableDragZooming = enableDragZooming;
    }

    /**
     * @return the showZoomResetButton
     */
    public boolean getShowZoomResetButton() {
        return m_showZoomResetButton;
    }

    /**
     * @param showZoomResetButton the showZoomResetButton to set
     */
    public void setShowZoomResetButton(final boolean showZoomResetButton) {
        m_showZoomResetButton = showZoomResetButton;
    }

    /**
     * @return the dotSize
     */
    public Integer getDotSize() {
        return m_dotSize;
    }

    /**
     * @param dotSize the dotSize to set
     */
    public void setDotSize(final Integer dotSize) {
        m_dotSize = dotSize;
    }

        /**
     * @return the showWarningInView
     */
    public boolean getShowWarningInView() {
        return m_showWarningInView;
    }

    /**
     * @param showWarningInView the showWarningInView to set
     */
    public void setShowWarningInView(final boolean showWarningInView) {
        m_showWarningInView = showWarningInView;
    }

    /**
     * @return the reportOnMissingValues
     */
    public boolean getReportOnMissingValues() {
        return m_reportOnMissingValues;
    }

    /**
     * @param reportOnMissingValues the reportOnMissingValues to set
     */
    public void setReportOnMissingValues(final boolean reportOnMissingValues) {
        m_reportOnMissingValues = reportOnMissingValues;
    }

    /**
     * @return the dateTimeFormats
     */
    public SettingsModelDateTimeOptions getDateTimeFormats() {
        return m_dateTimeFormats;
    }

    /**
     * @param dateTimeFormats the dateTimeFormats to set
     */
    public void setDateTimeFormats(final SettingsModelDateTimeOptions dateTimeFormats) {
        m_dateTimeFormats = dateTimeFormats;
    }

    /**
     * @return the customCSS
     */
    public String getCustomCSS() {
        return m_customCSS;
    }

    /**
     * @param customCSS the customCSS to set
     */
    public void setCustomCSS(final String customCSS) {
        m_customCSS = customCSS;
    }

    /** Saves current parameters to settings object.
     * @param settings To save to.
     */
    public void saveSettings(final NodeSettingsWO settings) {
        settings.addBoolean(HIDE_IN_WIZARD, getHideInWizard());
        settings.addBoolean(GENERATE_IMAGE, getGenerateImage());

        settings.addBoolean(SHOW_LEGEND, getShowLegend());
        settings.addBoolean(AUTO_RANGE_AXES, getAutoRangeAxes());
        settings.addBoolean(USE_DOMAIN_INFO, getUseDomainInfo());
        settings.addBoolean(SHOW_GRID, getShowGrid());
        settings.addBoolean(SHOW_CROSSHAIR, getShowCrosshair());
        settings.addBoolean(SNAP_TO_POINTS, getSnapToPoints());
        settings.addBoolean(RESIZE_TO_WINDOW, getResizeToWindow());

        settings.addBoolean(ENABLE_CONFIG, getEnableViewConfiguration());
        settings.addBoolean(ENABLE_TTILE_CHANGE, getEnableTitleChange());
        settings.addBoolean(ENABLE_SUBTTILE_CHANGE, getEnableSubtitleChange());
        settings.addBoolean(ENABLE_X_COL_CHANGE, getEnableXColumnChange());
        settings.addBoolean(ENABLE_Y_COL_CHANGE, getEnableYColumnChange());
        settings.addBoolean(ENABLE_X_LABEL_EDIT, getEnableXAxisLabelEdit());
        settings.addBoolean(ENABLE_Y_LABEL_EDIT, getEnableYAxisLabelEdit());
        settings.addBoolean(ENABLE_DOT_SIZE_CHANGE, getEnableDotSizeChange());
        settings.addBoolean(ENABLE_ZOOMING, getEnableZooming());
        settings.addBoolean(ENABLE_DRAG_ZOOMING, getEnableDragZooming());
        settings.addBoolean(ENABLE_PANNING, getEnablePanning());
        settings.addBoolean(SHOW_ZOOM_RESET_BUTTON, getShowZoomResetButton());
        settings.addBoolean(ENABLE_SELECTION, getEnableSelection());
        settings.addBoolean(ENABLE_RECTANGLE_SELECTION, getEnableRectangleSelection());
        settings.addBoolean(ENABLE_LASSO_SELECTION, getEnableLassoSelection());

        settings.addString(CHART_TITLE, getChartTitle());
        settings.addString(CHART_SUBTITLE, getChartSubtitle());
        settings.addString(X_COL, getxColumn());
        settings.addString(Y_COL, getyColumn());
        settings.addInt(MAX_ROWS, getMaxRows());
        settings.addString(SELECTION_COLUMN_NAME, getSelectionColumnName());
        settings.addString(X_AXIS_LABEL, getxAxisLabel());
        settings.addString(Y_AXIS_LABEL, getyAxisLabel());
        settings.addString(X_AXIS_MIN, getxAxisMin() == null ? null : getxAxisMin().toString());
        settings.addString(X_AXIS_MAX, getxAxisMax() == null ? null : getxAxisMax().toString());
        settings.addString(Y_AXIS_MIN, getyAxisMin() == null ? null : getyAxisMin().toString());
        settings.addString(Y_AXIS_MAX, getyAxisMax() == null ? null : getyAxisMax().toString());
        settings.addString(DOT_SIZE, getDotSize() == null ? null : getDotSize().toString());

        settings.addInt(IMAGE_WIDTH, getImageWidth());
        settings.addInt(IMAGE_HEIGHT, getImageHeight());
        settings.addString(BACKGROUND_COLOR, getBackgroundColorString());
        settings.addString(DATA_AREA_COLOR, getDataAreaColorString());
        settings.addString(GRID_COLOR, getGridColorString());

        //added with 3.3
        settings.addBoolean(CFG_DISPLAY_FULLSCREEN_BUTTON, getDisplayFullscreenButton());
        settings.addBoolean(CFG_PUBLISH_SELECTION, getPublishSelection());
        settings.addBoolean(CFG_SUBSCRIBE_SELECTION, getSubscribeSelection());
        settings.addBoolean(CFG_ENABLE_SHOW_SELECTED_ONLY, getEnableShowSelectedOnly());
        settings.addBoolean(CFG_SUBSCRIBE_FILTER, getSubscribeFilter());

        //added with 3.4
        settings.addBoolean(ENABLE_SWITCH_LEGEND, getEnableSwitchLegend());
        settings.addBoolean(SHOW_WARNING_IN_VIEW, getShowWarningInView());
        settings.addBoolean(REPORT_ON_MISSING_VALUES, getReportOnMissingValues());

        //added with 3.5
        m_dateTimeFormats.saveSettingsTo(settings);

        //added with 3.6
        settings.addString(CUSTOM_CSS, m_customCSS);
    }

    /** Loads parameters in NodeModel.
     * @param settings To load from.
     * @throws InvalidSettingsException If incomplete or wrong.
     */
    public void loadSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
        setHideInWizard(settings.getBoolean(HIDE_IN_WIZARD));
        setGenerateImage(settings.getBoolean(GENERATE_IMAGE));

        setShowLegend(settings.getBoolean(SHOW_LEGEND));
        setAutoRangeAxes(settings.getBoolean(AUTO_RANGE_AXES));
        setUseDomainInfo(settings.getBoolean(USE_DOMAIN_INFO));
        setShowGrid(settings.getBoolean(SHOW_GRID));
        setShowCrosshair(settings.getBoolean(SHOW_CROSSHAIR));
        setSnapToPoints(settings.getBoolean(SNAP_TO_POINTS));
        setResizeToWindow(settings.getBoolean(RESIZE_TO_WINDOW));

        setEnableViewConfiguration(settings.getBoolean(ENABLE_CONFIG));
        setEnableTitleChange(settings.getBoolean(ENABLE_TTILE_CHANGE));
        setEnableSubtitleChange(settings.getBoolean(ENABLE_SUBTTILE_CHANGE));
        setEnableXColumnChange(settings.getBoolean(ENABLE_X_COL_CHANGE));
        setEnableYColumnChange(settings.getBoolean(ENABLE_Y_COL_CHANGE));
        setEnableXAxisLabelEdit(settings.getBoolean(ENABLE_X_LABEL_EDIT));
        setEnableYAxisLabelEdit(settings.getBoolean(ENABLE_Y_LABEL_EDIT));
        setEnableDotSizeChange(settings.getBoolean(ENABLE_DOT_SIZE_CHANGE));
        setEnableZooming(settings.getBoolean(ENABLE_ZOOMING));
        setEnableDragZooming(settings.getBoolean(ENABLE_DRAG_ZOOMING));
        setEnablePanning(settings.getBoolean(ENABLE_PANNING));
        setShowZoomResetButton(settings.getBoolean(SHOW_ZOOM_RESET_BUTTON));
        setEnableSelection(settings.getBoolean(ENABLE_SELECTION));
        setEnableRectangleSelection(settings.getBoolean(ENABLE_RECTANGLE_SELECTION));
        setEnableLassoSelection(settings.getBoolean(ENABLE_LASSO_SELECTION));

        setChartTitle(settings.getString(CHART_TITLE));
        setChartSubtitle(settings.getString(CHART_SUBTITLE));
        setxColumn(settings.getString(X_COL));
        setyColumn(settings.getString(Y_COL));
        setMaxRows(settings.getInt(MAX_ROWS));
        setSelectionColumnName(settings.getString(SELECTION_COLUMN_NAME));
        setxAxisLabel(settings.getString(X_AXIS_LABEL));
        setyAxisLabel(settings.getString(Y_AXIS_LABEL));
        String xMin = settings.getString(X_AXIS_MIN);
        String xMax = settings.getString(X_AXIS_MAX);
        String yMin = settings.getString(Y_AXIS_MIN);
        String yMax = settings.getString(Y_AXIS_MAX);
        String dotSize = settings.getString(DOT_SIZE);
        setxAxisMin(xMin == null ? null : Double.parseDouble(xMin));
        setxAxisMax(xMax == null ? null : Double.parseDouble(xMax));
        setyAxisMin(yMin == null ? null : Double.parseDouble(yMin));
        setyAxisMax(yMax == null ? null : Double.parseDouble(yMax));
        setDotSize(dotSize == null ? null : Integer.parseInt(dotSize));

        setImageWidth(settings.getInt(IMAGE_WIDTH));
        setImageHeight(settings.getInt(IMAGE_HEIGHT));
        String bgColorString = settings.getString(BACKGROUND_COLOR);
        setBackgroundColor(LinePlotViewConfig.getColorFromString(bgColorString));
        String dataColorString = settings.getString(DATA_AREA_COLOR);
        setDataAreaColor(LinePlotViewConfig.getColorFromString(dataColorString));
        String gridColorString = settings.getString(GRID_COLOR);
        setGridColor(LinePlotViewConfig.getColorFromString(gridColorString));

        //added with 3.3
        setDisplayFullscreenButton(settings.getBoolean(CFG_DISPLAY_FULLSCREEN_BUTTON, DEFAULT_DISPLAY_FULLSCREEN_BUTTON));
        setPublishSelection(settings.getBoolean(CFG_PUBLISH_SELECTION, DEFAULT_PUBLISH_SELECTION));
        setSubscribeSelection(settings.getBoolean(CFG_SUBSCRIBE_SELECTION, DEFAULT_SUBSCRIBE_SELECTION));
        setEnableShowSelectedOnly(settings.getBoolean(CFG_ENABLE_SHOW_SELECTED_ONLY, DEFAULT_ENABLE_SHOW_SELECTED_ONLY));
        setSubscribeFilter(settings.getBoolean(CFG_SUBSCRIBE_FILTER, DEFAULT_SUBSCRIBE_FILTER));

        //added with 3.4
        setEnableSwitchLegend(settings.getBoolean(ENABLE_SWITCH_LEGEND, true));
        setShowWarningInView(settings.getBoolean(SHOW_WARNING_IN_VIEW, DEFAULT_SHOW_WARNING_IN_VIEW));
        setReportOnMissingValues(settings.getBoolean(REPORT_ON_MISSING_VALUES, DEFAULT_REPORT_ON_MISSING_VALUES));

        //added with 3.5
        if (settings.containsKey(DATE_TIME_FORMATS)) {
            m_dateTimeFormats.loadSettingsFrom(settings);
        } else {
            String legacyDateTimeFormat = settings.getString(DATE_FORMAT);
            String newDateTimeFormat = ConversionDateTimeFormat.oldToNew(legacyDateTimeFormat);
            m_dateTimeFormats.getGlobalDateTimeFormatModel().setStringValue(newDateTimeFormat);
            m_dateTimeFormats.getGlobalLocalDateTimeFormatModel().setStringValue(newDateTimeFormat);
            StringHistory.getInstance(SettingsModelDateTimeOptions.DATE_TIME_FORMAT_HISTORY_KEY).add(newDateTimeFormat);
        }

        //added with 3.6
        m_customCSS = settings.getString(CUSTOM_CSS, "");
    }

    /** Loads parameters in Dialog.
     * @param settings To load from.
     */
    public void loadSettingsForDialog(final NodeSettingsRO settings) {
        setHideInWizard(settings.getBoolean(HIDE_IN_WIZARD, false));
        setGenerateImage(settings.getBoolean(GENERATE_IMAGE, true));

        setShowLegend(settings.getBoolean(SHOW_LEGEND, true));
        setAutoRangeAxes(settings.getBoolean(AUTO_RANGE_AXES, true));
        setUseDomainInfo(settings.getBoolean(USE_DOMAIN_INFO, false));
        setShowGrid(settings.getBoolean(SHOW_GRID, true));
        setShowCrosshair(settings.getBoolean(SHOW_CROSSHAIR, false));
        setSnapToPoints(settings.getBoolean(SNAP_TO_POINTS, false));
        setResizeToWindow(settings.getBoolean(RESIZE_TO_WINDOW, true));

        setEnableViewConfiguration(settings.getBoolean(ENABLE_CONFIG, false));
        setEnableTitleChange(settings.getBoolean(ENABLE_TTILE_CHANGE, false));
        setEnableSubtitleChange(settings.getBoolean(ENABLE_SUBTTILE_CHANGE, false));
        setEnableXColumnChange(settings.getBoolean(ENABLE_X_COL_CHANGE, false));
        setEnableYColumnChange(settings.getBoolean(ENABLE_Y_COL_CHANGE, false));
        setEnableXAxisLabelEdit(settings.getBoolean(ENABLE_X_LABEL_EDIT, false));
        setEnableYAxisLabelEdit(settings.getBoolean(ENABLE_Y_LABEL_EDIT, false));
        setEnableDotSizeChange(settings.getBoolean(ENABLE_DOT_SIZE_CHANGE, false));
        setEnableZooming(settings.getBoolean(ENABLE_ZOOMING, true));
        setEnableDragZooming(settings.getBoolean(ENABLE_DRAG_ZOOMING, false));
        setEnablePanning(settings.getBoolean(ENABLE_PANNING, true));
        setShowZoomResetButton(settings.getBoolean(SHOW_ZOOM_RESET_BUTTON, false));
        setEnableSelection(settings.getBoolean(ENABLE_SELECTION, true));
        setEnableRectangleSelection(settings.getBoolean(ENABLE_RECTANGLE_SELECTION, false));
        setEnableLassoSelection(settings.getBoolean(ENABLE_LASSO_SELECTION, false));

        setChartTitle(settings.getString(CHART_TITLE, null));
        setChartSubtitle(settings.getString(CHART_SUBTITLE, null));
        setxColumn(settings.getString(X_COL, null));
        setyColumn(settings.getString(Y_COL, null));
        setMaxRows(settings.getInt(MAX_ROWS, DEFAULT_MAX_ROWS));
        setSelectionColumnName(settings.getString(SELECTION_COLUMN_NAME, DEFAULT_SELECTION_COLUMN_NAME));
        setxAxisLabel(settings.getString(X_AXIS_LABEL, null));
        setyAxisLabel(settings.getString(Y_AXIS_LABEL, null));
        String xMin = settings.getString(X_AXIS_MIN, null);
        String xMax = settings.getString(X_AXIS_MAX, null);
        String yMin = settings.getString(Y_AXIS_MIN, null);
        String yMax = settings.getString(Y_AXIS_MAX, null);
        String dotSize = settings.getString(DOT_SIZE, "3");
        setxAxisMin(xMin == null ? null : Double.parseDouble(xMin));
        setxAxisMax(xMax == null ? null : Double.parseDouble(xMax));
        setyAxisMin(yMin == null ? null : Double.parseDouble(yMin));
        setyAxisMax(yMax == null ? null : Double.parseDouble(yMax));
        setDotSize(dotSize == null ? null : Integer.parseInt(dotSize));

        setImageWidth(settings.getInt(IMAGE_WIDTH, DEFAULT_WIDTH));
        setImageHeight(settings.getInt(IMAGE_HEIGHT, DEFAULT_HEIGHT));

        String bgColorString = settings.getString(BACKGROUND_COLOR, null);
        Color backgroundColor = DEFAULT_BACKGROUND_COLOR;
        try {
            backgroundColor = LinePlotViewConfig.getColorFromString(bgColorString);
        } catch (InvalidSettingsException e) { /* do nothing */ }
        setBackgroundColor(backgroundColor);
        String dataColorString = settings.getString(DATA_AREA_COLOR, null);
        Color dataAreaColor = DEFAULT_DATA_AREA_COLOR;
        try {
            dataAreaColor = LinePlotViewConfig.getColorFromString(dataColorString);
        } catch (InvalidSettingsException e) { /* do nothing */ }
        setDataAreaColor(dataAreaColor);
        String gridColorString = settings.getString(GRID_COLOR, null);
        Color gridColor = DEFAULT_GRID_COLOR;
        try {
            gridColor = LinePlotViewConfig.getColorFromString(gridColorString);
        } catch (InvalidSettingsException e) { /* do nothing */ }
        setGridColor(gridColor);

        //added with 3.3
        setDisplayFullscreenButton(settings.getBoolean(CFG_DISPLAY_FULLSCREEN_BUTTON, DEFAULT_DISPLAY_FULLSCREEN_BUTTON));
        setPublishSelection(settings.getBoolean(CFG_PUBLISH_SELECTION, DEFAULT_PUBLISH_SELECTION));
        setSubscribeSelection(settings.getBoolean(CFG_SUBSCRIBE_SELECTION, DEFAULT_SUBSCRIBE_SELECTION));
        setEnableShowSelectedOnly(settings.getBoolean(CFG_ENABLE_SHOW_SELECTED_ONLY, DEFAULT_ENABLE_SHOW_SELECTED_ONLY));
        setSubscribeFilter(settings.getBoolean(CFG_SUBSCRIBE_FILTER, DEFAULT_SUBSCRIBE_FILTER));

        //added with 3.4
        setEnableSwitchLegend(settings.getBoolean(ENABLE_SWITCH_LEGEND, true));
        setShowWarningInView(settings.getBoolean(SHOW_WARNING_IN_VIEW, DEFAULT_SHOW_WARNING_IN_VIEW));
        setReportOnMissingValues(settings.getBoolean(REPORT_ON_MISSING_VALUES, DEFAULT_REPORT_ON_MISSING_VALUES));

        //added with 3.5
        if (settings.containsKey(DATE_TIME_FORMATS)) {
            try {
                m_dateTimeFormats.loadSettingsFrom(settings);
            } catch (InvalidSettingsException e) {
                // return default
            }
        } else {
            String legacyDateTimeFormat = settings.getString(DATE_FORMAT, null);
            if (legacyDateTimeFormat != null) {
                String newDateTimeFormat = ConversionDateTimeFormat.oldToNew(legacyDateTimeFormat);
                m_dateTimeFormats.getGlobalDateTimeFormatModel().setStringValue(newDateTimeFormat);
                m_dateTimeFormats.getGlobalLocalDateTimeFormatModel().setStringValue(newDateTimeFormat);
                StringHistory.getInstance(SettingsModelDateTimeOptions.DATE_TIME_FORMAT_HISTORY_KEY).add(newDateTimeFormat);
            }
        }

        //added with 3.6
        m_customCSS  = settings.getString(CUSTOM_CSS, "");
    }
}
