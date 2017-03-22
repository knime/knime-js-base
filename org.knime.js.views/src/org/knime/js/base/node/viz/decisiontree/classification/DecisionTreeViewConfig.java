/*
 * ------------------------------------------------------------------------
 *
 *  Copyright by KNIME GmbH, Konstanz, Germany
 *  Website: http://www.knime.org; Email: contact@knime.org
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
 *   08.11.2016 (Adrian): created
 */
package org.knime.js.base.node.viz.decisiontree.classification;

import java.awt.Color;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.js.core.CSSUtils;
import org.knime.js.core.settings.numberFormat.NumberFormatSettings;

/**
 *
 * @author Adrian Nembach, KNIME.com
 */
public class DecisionTreeViewConfig {

    static final String ENABLE_CONFIG = "enableViewConfiguration";
    static final String ENABLE_TTILE_CHANGE = "enableTitleChange";
    static final String ENABLE_SUBTTILE_CHANGE = "enableSubtitleChange";
    static final String TITLE = "chartTitle";
    static final String SUBTITLE = "chartSubtitle";
    static final String GENERATE_IMAGE = "generateImage";
    static final String HIDE_IN_WIZARD = "hideInWizard";
    static final String BACKGROUND_COLOR = "backgroundColor";
    static final String DATA_AREA_COLOR = "dataAreaColor";
    static final String NODE_BACKGROUND_COLOR = "nodeBackgroundColor";
    static final String NODE_STATUS = "nodeStatus";
    static final String EXPANDED_LEVEL = "expandedLevel";
    static final String NODE_STATUS_FROM_VIEW = "nodeStatusFromView";
    static final String MAX_ROWS = "maxRows";
    static final String ENABLE_SELECTION = "enableSelection";
    static final String SELECTION_COLUMN_NAME = "selectionColumnName";
    static final String PUBLISH_SELECTION = "publishSelection";
    static final String SUBSCRIBE_SELECTION = "subscribeSelection";
    static final String DISPLAY_FULLSCREEN_BUTTON = "displayFullscreenButton";
    static final String ENABLE_ZOOMING = "enableZooming";
    static final String DISPLAY_SELECTION_RESET_BUTTON = "displaySelectionResetButton";
    static final String TRUNCATION_LIMIT = "truncationLimit";
    static final String SCALE = "scale";
    static final String SHOW_ZOOM_RESET_BUTTON = "showZoomResetButton";

    static final Color DEFAULT_BACKGROUND_COLOR = new Color(255, 255, 255);
    static final Color DEFAULT_DATA_AREA_COLOR = DEFAULT_BACKGROUND_COLOR;
    static final Color DEFAULT_NODE_BACKGROUND_COLOR = DEFAULT_BACKGROUND_COLOR;
    static final int DEFAULT_MAX_ROWS = 10000;
    static final String DEFAULT_SELECTION_COLUMN_NAME = "Selected (Decision Tree View)";
    static final boolean DEFAULT_GENERATE_IMAGE = true;
    final static boolean DEFAULT_DISPLAY_FULLSCREEN_BUTTON = true;
    final static boolean DEFAULT_PUBLISH_SELECTION = true;
    final static boolean DEFAULT_SUBSCRIBE_SELECTION = true;
    final static int DEFAULT_EXPANDED_LEVEL = 1;
    static final boolean DEFAULT_NODE_STATUS_FROM_VIEW = false;
    static final boolean DEFAULT_ENABLE_SELECTION = true;
    static final boolean DEFAULT_ENABLE_ZOOMING = true;
    static final boolean DEFAULT_ENABLE_CONFIG = true;
    static final boolean DEFAULT_ENABLE_TITLE_CHANGE = true;
    static final boolean DEFAULT_ENABLE_SUBTITLE_CHANGE = true;
    static final boolean DEFAULT_DISPLAY_SELECTION_RESET_BUTTON = true;
    static final NumberFormatSettings DEFAULT_NUMBER_FORMAT = new NumberFormatSettings();
    static final int DEFAULT_TRUNCATION_LIMIT = 25;
    static final double DEFAULT_SCALE = 1.0;
    static final boolean DEFAULT_SHOW_ZOOM_RESET_BUTTON = false;

    private String m_title;
    private String m_subtitle;
    private boolean m_generateImage = DEFAULT_GENERATE_IMAGE;
    private boolean m_hideInWizard;
    private boolean m_enableConfig = DEFAULT_ENABLE_CONFIG;
    private boolean m_enableTitleChange = DEFAULT_ENABLE_TITLE_CHANGE;
    private boolean m_enableSubtitleChange = DEFAULT_ENABLE_SUBTITLE_CHANGE;
    private boolean m_enableSelection = DEFAULT_ENABLE_SELECTION;
    private Color m_backgroundColor = DEFAULT_BACKGROUND_COLOR;
    private Color m_dataAreaColor = DEFAULT_DATA_AREA_COLOR;
    private Color m_nodeBackgroundColor = DEFAULT_NODE_BACKGROUND_COLOR;
    private int m_expandedLevel = DEFAULT_EXPANDED_LEVEL;
    private boolean m_nodeStatusFromView;
    private int m_maxRows = DEFAULT_MAX_ROWS;
    private String m_selectionColumnName = DEFAULT_SELECTION_COLUMN_NAME;
    private boolean m_publishSelection = DEFAULT_PUBLISH_SELECTION;
    private boolean m_subscribeSelection = DEFAULT_SUBSCRIBE_SELECTION;
    private boolean m_displayFullScreenButton = DEFAULT_DISPLAY_FULLSCREEN_BUTTON;
    private boolean m_enableZooming = DEFAULT_ENABLE_ZOOMING;
    private boolean m_displaySelectionResetButton = DEFAULT_DISPLAY_SELECTION_RESET_BUTTON;
    private int m_truncationLimit = DEFAULT_TRUNCATION_LIMIT;
    private double m_scale = DEFAULT_SCALE;
    private boolean m_showZoomResetButton = DEFAULT_SHOW_ZOOM_RESET_BUTTON;

    private NumberFormatSettings m_numberFormat = DEFAULT_NUMBER_FORMAT;

    private int[] m_nodeStatus;

    /**
     * Save settings of node.
     *
     * @param settings
     */
    public void saveSettings(final NodeSettingsWO settings) {
        settings.addBoolean(ENABLE_CONFIG, isEnableViewConfiguration());
        settings.addBoolean(GENERATE_IMAGE, m_generateImage);
        settings.addBoolean(HIDE_IN_WIZARD, m_hideInWizard);
        settings.addBoolean(ENABLE_TTILE_CHANGE, isEnableTitleChange());
        settings.addBoolean(ENABLE_SUBTTILE_CHANGE, isEnableSubtitleChange());
        settings.addString(TITLE, m_title);
        settings.addString(SUBTITLE, m_subtitle);
        settings.addString(BACKGROUND_COLOR, getBackgroundColorString());
        settings.addString(DATA_AREA_COLOR, getDataAreaColorString());
        settings.addString(NODE_BACKGROUND_COLOR, getNodeBackgroundColorString());
        settings.addIntArray(NODE_STATUS, m_nodeStatus);
        settings.addInt(EXPANDED_LEVEL, m_expandedLevel);
        settings.addBoolean(NODE_STATUS_FROM_VIEW, m_nodeStatusFromView);
        settings.addInt(MAX_ROWS, m_maxRows);
        settings.addBoolean(ENABLE_SELECTION, m_enableSelection);
        settings.addString(SELECTION_COLUMN_NAME, m_selectionColumnName);
        m_numberFormat.saveToNodeSettings(settings);
        settings.addBoolean(PUBLISH_SELECTION, m_publishSelection);
        settings.addBoolean(SUBSCRIBE_SELECTION, m_subscribeSelection);
        settings.addBoolean(DISPLAY_FULLSCREEN_BUTTON, m_displayFullScreenButton);
        settings.addBoolean(ENABLE_ZOOMING, m_enableZooming);
        settings.addBoolean(DISPLAY_SELECTION_RESET_BUTTON, m_displaySelectionResetButton);
        settings.addInt(TRUNCATION_LIMIT, m_truncationLimit);
        settings.addDouble(SCALE, m_scale);
        settings.addBoolean(SHOW_ZOOM_RESET_BUTTON, m_showZoomResetButton);
    }

    /**
     * Load method that should be used in the node model.
     *
     * @param settings
     * @throws InvalidSettingsException if there is no value for a specified key.
     */
    public void loadInModel(final NodeSettingsRO settings) throws InvalidSettingsException {
        setEnableViewConfiguration(settings.getBoolean(ENABLE_CONFIG));
        setEnableTitleChange(settings.getBoolean(ENABLE_TTILE_CHANGE));
        setEnableSubtitleChange(settings.getBoolean(ENABLE_SUBTTILE_CHANGE));
        setHideInWizard(settings.getBoolean(HIDE_IN_WIZARD));
        setGenerateImage(settings.getBoolean(GENERATE_IMAGE));
        setTitle(settings.getString(TITLE));
        setSubtitle(settings.getString(SUBTITLE));
        String bgColorString = settings.getString(BACKGROUND_COLOR);
        Color backgroundColor = CSSUtils.colorFromCssHexString(bgColorString);
        setBackgroundColor(backgroundColor);
        String dataColorString = settings.getString(DATA_AREA_COLOR);
        Color dataAreaColor = CSSUtils.colorFromCssHexString(dataColorString);
        setDataAreaColor(dataAreaColor);
        String nodeBgColorString = settings.getString(NODE_BACKGROUND_COLOR);
        setNodeBackgroundColor(CSSUtils.colorFromCssHexString(nodeBgColorString));
        setNodeStatus(settings.getIntArray(NODE_STATUS));
        NumberFormatSettings numberFormat = new NumberFormatSettings();
        numberFormat.loadFromNodeSettings(settings);
        setNumberFormat(numberFormat);
        setExpandedLevel(settings.getInt(EXPANDED_LEVEL));
        setNodeStatusFromView(settings.getBoolean(NODE_STATUS_FROM_VIEW));
        setMaxRows(settings.getInt(MAX_ROWS));
        setEnableSelection(settings.getBoolean(ENABLE_SELECTION));
        setSelectionColumnName(settings.getString(SELECTION_COLUMN_NAME));
        setPublishSelection(settings.getBoolean(PUBLISH_SELECTION));
        setSubscribeSelection(settings.getBoolean(SUBSCRIBE_SELECTION));
        setDisplayFullScreenButton(settings.getBoolean(DISPLAY_FULLSCREEN_BUTTON));
        setEnableZooming(settings.getBoolean(ENABLE_ZOOMING));
        setDisplaySelectionResetButton(settings.getBoolean(DISPLAY_SELECTION_RESET_BUTTON));
        setShowZoomResetButton(settings.getBoolean(SHOW_ZOOM_RESET_BUTTON, DEFAULT_SHOW_ZOOM_RESET_BUTTON));

        //added with 3.3.2
        setTruncationLimit(settings.getInt(TRUNCATION_LIMIT, DEFAULT_TRUNCATION_LIMIT));
        setScale(settings.getDouble(SCALE, DEFAULT_SCALE));

    }

    /**
     * Load model to be used in the dialog.
     * Fills in defaults if key does not exist.
     *
     * @param settings
     */
    public void loadInDialog(final NodeSettingsRO settings) {
        setEnableViewConfiguration(settings.getBoolean(ENABLE_CONFIG, true));
        setEnableTitleChange(settings.getBoolean(ENABLE_TTILE_CHANGE, true));
        setEnableSubtitleChange(settings.getBoolean(ENABLE_SUBTTILE_CHANGE, true));
        setHideInWizard(settings.getBoolean(HIDE_IN_WIZARD, false));
        setGenerateImage(settings.getBoolean(GENERATE_IMAGE, true));
        setTitle(settings.getString(TITLE, null));
        setSubtitle(settings.getString(SUBTITLE, null));

        String bgColorString = settings.getString(BACKGROUND_COLOR, null);
        Color backgroundColor = bgColorString == null ? DEFAULT_BACKGROUND_COLOR : CSSUtils.colorFromCssHexString(bgColorString);
        setBackgroundColor(backgroundColor);
        String dataColorString = settings.getString(DATA_AREA_COLOR, null);
        Color dataAreaColor = dataColorString == null ? DEFAULT_DATA_AREA_COLOR : CSSUtils.colorFromCssHexString(dataColorString);
        setDataAreaColor(dataAreaColor);
        String nodeBgColorString = settings.getString(NODE_BACKGROUND_COLOR, null);
        setNodeBackgroundColor(nodeBgColorString == null ? DEFAULT_NODE_BACKGROUND_COLOR : CSSUtils.colorFromCssHexString(nodeBgColorString));

        setNodeStatus(settings.getIntArray(NODE_STATUS, null));

        NumberFormatSettings numberFormat = new NumberFormatSettings();
        numberFormat.loadFromNodeSettingsInDialog(settings);
        setNumberFormat(numberFormat);
        setExpandedLevel(settings.getInt(EXPANDED_LEVEL, DEFAULT_EXPANDED_LEVEL));
        setNodeStatusFromView(settings.getBoolean(NODE_STATUS_FROM_VIEW, DEFAULT_NODE_STATUS_FROM_VIEW));
        setMaxRows(settings.getInt(MAX_ROWS, DEFAULT_MAX_ROWS));
        setEnableSelection(settings.getBoolean(ENABLE_SELECTION, DEFAULT_ENABLE_SELECTION));
        setSelectionColumnName(settings.getString(SELECTION_COLUMN_NAME, DEFAULT_SELECTION_COLUMN_NAME));
        setPublishSelection(settings.getBoolean(PUBLISH_SELECTION, DEFAULT_PUBLISH_SELECTION));
        setSubscribeSelection(settings.getBoolean(SUBSCRIBE_SELECTION, DEFAULT_SUBSCRIBE_SELECTION));
        setDisplayFullScreenButton(settings.getBoolean(DISPLAY_FULLSCREEN_BUTTON, DEFAULT_DISPLAY_FULLSCREEN_BUTTON));
        setEnableZooming(settings.getBoolean(EXPANDED_LEVEL, DEFAULT_ENABLE_ZOOMING));
        setDisplaySelectionResetButton(settings.getBoolean(DISPLAY_SELECTION_RESET_BUTTON, DEFAULT_DISPLAY_SELECTION_RESET_BUTTON));
        setTruncationLimit(settings.getInt(TRUNCATION_LIMIT, DEFAULT_TRUNCATION_LIMIT));
        setScale(settings.getDouble(SCALE, DEFAULT_SCALE));
        setShowZoomResetButton(settings.getBoolean(SHOW_ZOOM_RESET_BUTTON, DEFAULT_SHOW_ZOOM_RESET_BUTTON));
    }


    /**
     * @return the chartTitle
     */
    public String getTitle() {
        return m_title;
    }
    /**
     * @param chartTitle the chartTitle to set
     */
    public void setTitle(final String chartTitle) {
        m_title = chartTitle;
    }
    /**
     * @return the chartSubtitle
     */
    public String getSubtitle() {
        return m_subtitle;
    }
    /**
     * @param chartSubtitle the chartSubtitle to set
     */
    public void setSubtitle(final String chartSubtitle) {
        m_subtitle = chartSubtitle;
    }
    /**
     * @return the generateImage
     */
    public boolean isGenerateImage() {
        return m_generateImage;
    }
    /**
     * @param generateImage the generateImage to set
     */
    public void setGenerateImage(final boolean generateImage) {
        m_generateImage = generateImage;
    }
    /**
     * @return the hideInWizard
     */
    public boolean isHideInWizard() {
        return m_hideInWizard;
    }
    /**
     * @param hideInWizard the hideInWizard to set
     */
    public void setHideInWizard(final boolean hideInWizard) {
        m_hideInWizard = hideInWizard;
    }

    /**
     * @return the enableConfig
     */
    public boolean isEnableViewConfiguration() {
        return m_enableConfig;
    }

    /**
     * @param enableConfig the enableConfig to set
     */
    public void setEnableViewConfiguration(final boolean enableConfig) {
        m_enableConfig = enableConfig;
    }

    /**
     * @return the enableTitleChange
     */
    public boolean isEnableTitleChange() {
        return m_enableTitleChange;
    }

    /**
     * @param enableTitleChange the enableTitleChange to set
     */
    public void setEnableTitleChange(final boolean enableTitleChange) {
        m_enableTitleChange = enableTitleChange;
    }

    /**
     * @return the enableSubTitleChange
     */
    public boolean isEnableSubtitleChange() {
        return m_enableSubtitleChange;
    }

    /**
     * @param enableSubTitleChange the enableSubTitleChange to set
     */
    public void setEnableSubtitleChange(final boolean enableSubTitleChange) {
        m_enableSubtitleChange = enableSubTitleChange;
    }

    /**
     * @return the backgroundColor
     */
    public Color getBackgroundColor() {
        return m_backgroundColor;
    }

    /**
     * @return the data area color as rgba string
     */
    public String getBackgroundColorString() {
//        return LinePlotViewConfig.getRGBAStringFromColor(m_backgroundColor);
        return CSSUtils.cssHexStringFromColor(m_backgroundColor);
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
        return CSSUtils.cssHexStringFromColor(m_dataAreaColor);
//        return LinePlotViewConfig.getRGBAStringFromColor(m_dataAreaColor);
    }

    /**
     * @param dataAreaColor the dataAreaColor to set
     */
    public void setDataAreaColor(final Color dataAreaColor) {
        m_dataAreaColor = dataAreaColor;
    }

    /**
     * @return the nodeStatus
     */
    public int[] getNodeStatus() {
        return m_nodeStatus;
    }

    /**
     * @param nodeStatus the nodeStatus to set
     */
    public void setNodeStatus(final int[] nodeStatus) {
        m_nodeStatus = nodeStatus;
    }

    /**
     * @return the numberFormat
     */
    public NumberFormatSettings getNumberFormat() {
        return m_numberFormat;
    }

    /**
     * @param numberFormat the numberFormat to set
     */
    public void setNumberFormat(final NumberFormatSettings numberFormat) {
        m_numberFormat = numberFormat;
    }

    /**
     * @return the expandedLevel
     */
    public int getExpandedLevel() {
        return m_expandedLevel;
    }

    /**
     * @param expandedLevel the expandedLevel to set
     */
    public void setExpandedLevel(final int expandedLevel) {
        m_expandedLevel = expandedLevel;
    }

    /**
     * @return the nodeStatusFromView
     */
    public boolean isNodeStatusFromView() {
        return m_nodeStatusFromView;
    }

    /**
     * @param nodeStatusFromView the nodeStatusFromView to set
     */
    public void setNodeStatusFromView(final boolean nodeStatusFromView) {
        m_nodeStatusFromView = nodeStatusFromView;
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
     * @return the displayFullScreenButton
     */
    public boolean getDisplayFullScreenButton() {
        return m_displayFullScreenButton;
    }

    /**
     * @param displayFullScreenButton the displayFullScreenButton to set
     */
    public void setDisplayFullScreenButton(final boolean displayFullScreenButton) {
        m_displayFullScreenButton = displayFullScreenButton;
    }

    /**
     * @return the enableZooming
     */
    public boolean getEnableZooming() {
        return m_enableZooming;
    }

    /**
     * @param enableZooming the enableZooming to set
     */
    public void setEnableZooming(final boolean enableZooming) {
        m_enableZooming = enableZooming;
    }

    /**
     * @return the css hex string representation of the node background color
     */
    public String getNodeBackgroundColorString() {
        return CSSUtils.cssHexStringFromColor(m_nodeBackgroundColor);
    }

    /**
     * @return the nodeBackgroundColor
     */
    public Color getNodeBackgroundColor() {
        return m_nodeBackgroundColor;
    }

    /**
     * @param nodeBackgroundColor the nodeBackgroundColor to set
     */
    public void setNodeBackgroundColor(final Color nodeBackgroundColor) {
        m_nodeBackgroundColor = nodeBackgroundColor;
    }

    /**
     * @return the displaySelectionResetButton
     */
    public boolean getDisplaySelectionResetButton() {
        return m_displaySelectionResetButton;
    }

    /**
     * @param displaySelectionResetButton the displaySelectionResetButton to set
     */
    public void setDisplaySelectionResetButton(final boolean displaySelectionResetButton) {
        m_displaySelectionResetButton = displaySelectionResetButton;
    }

    /**
     * @return the truncationLimit
     */
    public int getTruncationLimit() {
        return m_truncationLimit;
    }

    /**
     * @param truncationLimit the truncationLimit to set
     */
    public void setTruncationLimit(final int truncationLimit) {
        m_truncationLimit = truncationLimit;
    }

    /**
     * @return the scale
     */
    public double getScale() {
        return m_scale;
    }

    /**
     * @param scale the scale to set
     */
    public void setScale(final double scale) {
        m_scale = scale;
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

}
