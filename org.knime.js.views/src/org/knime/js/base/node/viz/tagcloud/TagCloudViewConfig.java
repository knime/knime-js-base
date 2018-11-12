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
 *   2 Oct 2017 (albrecht): created
 */
package org.knime.js.base.node.viz.tagcloud;

import java.awt.Color;

import org.knime.core.data.DataTableSpec;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.js.core.CSSUtils;

/**
 * Settings class holding the tag cloud view config values
 *
 * @author Christian Albrecht, KNIME GmbH, Konstanz, Germany
 */
public class TagCloudViewConfig {

    private final static String CFG_HIDE_IN_WIZARD = "hideInWizard";
    private final static boolean DEFAULT_HIDE_IN_WIZARD = false;
    private boolean m_hideInWizard = DEFAULT_HIDE_IN_WIZARD;

    private static final String CFG_GENERATE_IMAGE = "generateImage";
    private final static boolean DEFAULT_GENERATE_IMAGE = false;
    private boolean m_generateImage = DEFAULT_GENERATE_IMAGE;

    static final String CFG_WARNINGS_IN_VIEW = "warningsInView";
    private final static boolean DEFAULT_WARNINGS_IN_VIEW = true;
    private boolean m_showWarningsInView = DEFAULT_WARNINGS_IN_VIEW;

    private static final String CFG_REPORT_MISSING_VALUES = "reportMissingValues";
    private final static boolean DEFAULT_REPORT_MISSING_VALUES = true;
    private boolean m_reportMissingValues = DEFAULT_REPORT_MISSING_VALUES;

    final static String CFG_RESIZE_TO_WINDOW = "resizeToWindow";
    private final static boolean DEFAULT_RESIZE_TO_WINDOW = true;
    private boolean m_resizeToWindow = DEFAULT_RESIZE_TO_WINDOW;

    final static String CFG_IMAGE_WIDTH = "imageWidth";
    private final static int DEFAULT_IMAGE_WIDTH = 800;
    private int m_imageWidth = DEFAULT_IMAGE_WIDTH;

    final static String CFG_IMAGE_HEIGHT = "imageHeight";
    private final static int DEFAULT_IMAGE_HEIGHT = 600;
    private int m_imageHeight = DEFAULT_IMAGE_HEIGHT;

    final static String CFG_MAX_WORDS = "maxWords";
    private final static int DEFAULT_MAX_WORDS = 250;
    private int m_maxWords = DEFAULT_MAX_WORDS;

    final static String CFG_AGGREGATE_WORDS = "aggregateWords";
    private final static boolean DEFAULT_AGGREGATE_WORDS = true;
    private boolean m_aggregateWords = DEFAULT_AGGREGATE_WORDS;

    final static String CFG_IGNORE_TERM_TAGS = "ignoreTermTags";
    private final static boolean DEFAULT_IGNORE_TERM_TAGS = false;
    private boolean m_ignoreTermTags = DEFAULT_IGNORE_TERM_TAGS;

    final static String CFG_DISPLAY_FULLSCREEN_BUTTON = "displayFullscreenButton";
    private final static boolean DEFAULT_DISPLAY_FULLSCREEN_BUTTON = true;
    private boolean m_displayFullscreenButton = DEFAULT_DISPLAY_FULLSCREEN_BUTTON;

    final static String CFG_DISPLAY_REFRESH_BUTTON = "displayRefreshButton";
    private final static boolean DEFAULT_DISPLAY_REFRESH_BUTTON = true;
    private boolean m_displayRefreshButton = DEFAULT_DISPLAY_REFRESH_BUTTON;

    final static String CFG_DISPLAY_CLEAR_SELECTION_BUTTON = "displayClearSelectionButton";
    private final static boolean DEFAULT_DISPLAY_CLEAR_SELECTION_BUTTON = true;
    private boolean m_displayClearSelectionButton = DEFAULT_DISPLAY_CLEAR_SELECTION_BUTTON;

    final static String CFG_DISABLE_ANIMATIONS = "disableAnimations";
    private final static boolean DEFAULT_DISABLE_ANIMATIONS = false;
    private boolean m_disableAnimations = DEFAULT_DISABLE_ANIMATIONS;

    final static String CFG_TITLE = "title";
    private final static String DEFAULT_TITLE = "";
    private String m_title = DEFAULT_TITLE;

    final static String CFG_SUBTITLE = "subtitle";
    private final static String DEFAULT_SUBTITLE = "";
    private String m_subtitle = DEFAULT_SUBTITLE;

    private final static String CFG_WORD_COLUMN = "wordColumn";
    private final static String DEFAULT_WORD_COLUMN = "";
    private String m_wordColumn = DEFAULT_WORD_COLUMN;

    private final static String CFG_SIZE_COLUMN = "sizeColumn";
    private final static String DEFAULT_SIZE_COLUMN = "";
    private String m_sizeColumn = DEFAULT_SIZE_COLUMN;

    private final static String CFG_USE_SIZE_PROP = "sizeProp";
    private final static boolean DEFAULT_USE_SIZE_PROP = false;
    private boolean m_useSizeProp = DEFAULT_USE_SIZE_PROP;

    final static String CFG_USE_COLOR_PROP = "colorProp";
    private final static boolean DEFAULT_USE_COLOR_PROP = false;
    private boolean m_useColorProp = DEFAULT_USE_COLOR_PROP;

    final static String CFG_FONT = "font";
    private final static String DEFAULT_FONT = "Impact";
    private String m_font = DEFAULT_FONT;

    final static String CFG_MIN_FONT_SIZE = "minFontSize";
    private final static float DEFAULT_MIN_FONT_SIZE = 10f;
    private float m_minFontSize = DEFAULT_MIN_FONT_SIZE;

    final static String CFG_MAX_FONT_SIZE = "maxFontSize";
    private final static float DEFAULT_MAX_FONT_SIZE = 100f;
    private float m_maxFontSize = DEFAULT_MAX_FONT_SIZE;

    final static String CFG_FONT_SCALE_TYPE = "fontScaleType";
    private final static TagCloudFontScaleType DEFAULT_FONT_SCALE_TYPE = TagCloudFontScaleType.LINEAR;
    private TagCloudFontScaleType m_fontScaleType = DEFAULT_FONT_SCALE_TYPE;

    final static String CFG_FONT_BOLD = "fontBold";
    private final static boolean DEFAULT_FONT_BOLD = false;
    private boolean m_fontBold = DEFAULT_FONT_BOLD;

    final static String CFG_SPIRAL_TYPE = "spiralType";
    private final static TagCloudSpiralType DEFAULT_SPIRAL_TYPE = TagCloudSpiralType.ARCHIMEDEAN;
    private TagCloudSpiralType m_spiralType = DEFAULT_SPIRAL_TYPE;

    final static String CFG_NUM_ORIENTATIONS = "numOrientations";
    private final static int DEFAULT_NUM_ORIENTATIONS = 5;
    private int m_numOrientations = DEFAULT_NUM_ORIENTATIONS;

    final static String CFG_START_ANGLE = "startAngle";
    private final static int DEFAULT_START_ANGLE = -60;
    private int m_startAngle = DEFAULT_START_ANGLE;

    final static String CFG_END_ANGLE = "endAngle";
    private final static int DEFAULT_END_ANGLE = 60;
    private int m_endAngle = DEFAULT_END_ANGLE;

    final static String CFG_ENABLE_CONFIG = "enableViewConfiguration";
    private final static boolean DEFAULT_ENABLE_CONFIG = true;
    private boolean m_enableViewConfig = DEFAULT_ENABLE_CONFIG;

    final static String CFG_ENABLE_TITLE_CHANGE = "enableTitleChange";
    private final static boolean DEFAULT_ENABLE_TITLE_CHANGE = true;
    private boolean m_enableTitleChange = DEFAULT_ENABLE_TITLE_CHANGE;

    final static String CFG_ENABLE_SUBTITLE_CHANGE = "enableSubtitleChange";
    private final static boolean DEFAULT_ENABLE_SUBTITLE_CHANGE = true;
    private boolean m_enableSubtitleChange = DEFAULT_ENABLE_SUBTITLE_CHANGE;

    final static String CFG_ENABLE_FONT_SIZE_CHANGE = "enableFontSizeChange";
    private final static boolean DEFAULT_ENABLE_FONT_SIZE_CHANGE = true;
    private boolean m_enableFontSizeChange = DEFAULT_ENABLE_FONT_SIZE_CHANGE;

    final static String CFG_ENABLE_SCALE_TYPE_CHANGE = "enableScaleTypeChange";
    private final static boolean DEFAULT_ENABLE_SCALE_TYPE_CHANGE = true;
    private boolean m_enableScaleTypeChange = DEFAULT_ENABLE_SCALE_TYPE_CHANGE;

    final static String CFG_ENABLE_SPIRAL_TYPE_CHANGE = "enableSpiralTypeChange";
    private final static boolean DEFAULT_ENABLE_SPIRAL_TYPE_CHANGE = true;
    private boolean m_enableSpiralTypeChange = DEFAULT_ENABLE_SPIRAL_TYPE_CHANGE;

    final static String CFG_ENABLE_NUM_ORIENTATIONS_CHANGE = "enableNumOrientationsChange";
    private final static boolean DEFAULT_ENABLE_NUM_ORIENTATIONS_CHANGE = true;
    private boolean m_enableNumOrientationsChange = DEFAULT_ENABLE_NUM_ORIENTATIONS_CHANGE;

    final static String CFG_ENABLE_ANGLES_CHANGE = "enableAnglesChange";
    private final static boolean DEFAULT_ENABLE_ANGLES_CHANGE = true;
    private boolean m_enableAnglesChange = DEFAULT_ENABLE_ANGLES_CHANGE;

    final static String CFG_ENABLE_SELECTION = "enableSelection";
    private final static boolean DEFAULT_ENABLE_SELECTION = true;
    private boolean m_enableSelection = DEFAULT_ENABLE_SELECTION;

    private final static String CFG_SELECTION_COLUMN_NAME = "selectionColumnName";
    final static String DEFAULT_SELECTION_COLUMN_NAME = "Selected (JavaScript Tag Cloud)";
    private String m_selectionColumnName = DEFAULT_SELECTION_COLUMN_NAME;

    final static String CFG_SELECTION_COLOR = "selectionColor";
    private final static Color DEFAULT_SELECTION_COLOR = new Color(/*253, 180, 98*/ 51, 51, 51);
    private Color m_selectionColor = DEFAULT_SELECTION_COLOR;

    final static String CFG_PUBLISH_SELECTION = "publishSelection";
    private final static boolean DEFAULT_PUBLISH_SELECTION = true;
    private boolean m_publishSelection = DEFAULT_PUBLISH_SELECTION;

    final static String CFG_SUBSCRIBE_SELECTION = "subscribeSelection";
    private final static boolean DEFAULT_SUBSCRIBE_SELECTION = true;
    private boolean m_subscribeSelection = DEFAULT_SUBSCRIBE_SELECTION;

    final static String CFG_ENABLE_SHOW_SELECTED_ONLY = "enableShowSelectedOnly";
    private final static boolean DEFAULT_ENABLE_SHOW_SELECTED_ONLY = true;
    private boolean m_enableShowSelectedOnly = DEFAULT_ENABLE_SHOW_SELECTED_ONLY;

    final static String CFG_DEFAULT_SHOW_SELECTED_ONLY = "defaultShowSelectedOnly";
    private final static boolean DEFAULT_DEFAULT_SHOW_SELECTED_ONLY = false;
    private boolean m_defaultShowSelectedOnly = DEFAULT_DEFAULT_SHOW_SELECTED_ONLY;

    final static String CFG_SUBSCRIBE_FILTER = "subscribeFilter";
    private final static boolean DEFAULT_SUBSCRIBE_FILTER = true;
    private boolean m_subscribeFilter = DEFAULT_SUBSCRIBE_FILTER;

    final static String CFG_CUSTOM_CSS = "customCSS";
    private final static String DEFAULT_CUSTOM_CSS = "";
    private String m_customCSS = DEFAULT_CUSTOM_CSS;

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
     * @return the showWarningsInView
     */
    public boolean getShowWarningsInView() {
        return m_showWarningsInView;
    }

    /**
     * @param showWarningsInView the showWarningsInView to set
     */
    public void setShowWarningsInView(final boolean showWarningsInView) {
        m_showWarningsInView = showWarningsInView;
    }

    /**
     * @return the reportMissingValues
     */
    public boolean getReportMissingValues() {
        return m_reportMissingValues;
    }

    /**
     * @param reportMissingValues the reportMissingValues to set
     */
    public void setReportMissingValues(final boolean reportMissingValues) {
        m_reportMissingValues = reportMissingValues;
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
     * @return the maxWords
     */
    public int getMaxWords() {
        return m_maxWords;
    }

    /**
     * @param maxWords the maxWords to set
     */
    public void setMaxWords(final int maxWords) {
        m_maxWords = maxWords;
    }

    /**
     * @return the aggregateWords
     */
    public boolean getAggregateWords() {
        return m_aggregateWords;
    }

    /**
     * @param aggregateWords the aggregateWords to set
     */
    public void setAggregateWords(final boolean aggregateWords) {
        m_aggregateWords = aggregateWords;
    }

    /**
     * @return the ignoreTermTags
     */
    public boolean getIgnoreTermTags() {
        return m_ignoreTermTags;
    }

    /**
     * @param ignoreTermTags the ignoreTermTags to set
     */
    public void setIgnoreTermTags(final boolean ignoreTermTags) {
        m_ignoreTermTags = ignoreTermTags;
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
     * @return the displayRefreshButton
     */
    public boolean getDisplayRefreshButton() {
        return m_displayRefreshButton;
    }

    /**
     * @param displayRefreshButton the displayRefreshButton to set
     */
    public void setDisplayRefreshButton(final boolean displayRefreshButton) {
        m_displayRefreshButton = displayRefreshButton;
    }

    /**
     * @return the displaySelectionResetButton
     */
    public boolean getDisplayClearSelectionButton() {
        return m_displayClearSelectionButton;
    }

    /**
     * @param displayClearSelectionButton the displayClearSelectionButton to set
     */
    public void setDisplayClearSelectionButton(final boolean displayClearSelectionButton) {
        m_displayClearSelectionButton = displayClearSelectionButton;
    }

    /**
     * @return the disableAnimations
     */
    public boolean getDisableAnimations() {
        return m_disableAnimations;
    }

    /**
     * @param disableAnimations the disableAnimations to set
     */
    public void setDisableAnimations(final boolean disableAnimations) {
        m_disableAnimations = disableAnimations;
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return m_title;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(final String title) {
        m_title = title;
    }

    /**
     * @return the subtitle
     */
    public String getSubtitle() {
        return m_subtitle;
    }

    /**
     * @param subtitle the subtitle to set
     */
    public void setSubtitle(final String subtitle) {
        m_subtitle = subtitle;
    }

    /**
     * @return the wordColumn
     */
    public String getWordColumn() {
        return m_wordColumn;
    }

    /**
     * @param wordColumn the wordColumn to set
     */
    public void setWordColumn(final String wordColumn) {
        m_wordColumn = wordColumn;
    }

    /**
     * @return the sizeColumn
     */
    public String getSizeColumn() {
        return m_sizeColumn;
    }

    /**
     * @param sizeColumn the sizeColumn to set
     */
    public void setSizeColumn(final String sizeColumn) {
        m_sizeColumn = sizeColumn;
    }

    /**
     * @return the useSizeProp
     */
    public boolean getUseSizeProp() {
        return m_useSizeProp;
    }

    /**
     * @param useSizeProp the useSizeProp to set
     */
    public void setUseSizeProp(final boolean useSizeProp) {
        m_useSizeProp = useSizeProp;
    }

    /**
     * @return the useColorProp
     */
    public boolean getUseColorProp() {
        return m_useColorProp;
    }

    /**
     * @param useColorProp the useColorProp to set
     */
    public void setUseColorProp(final boolean useColorProp) {
        m_useColorProp = useColorProp;
    }

    /**
     * @return the font
     */
    public String getFont() {
        return m_font;
    }

    /**
     * @param font the font to set
     */
    public void setFont(final String font) {
        m_font = font;
    }

    /**
     * @return the minFontSize
     */
    public float getMinFontSize() {
        return m_minFontSize;
    }

    /**
     * @param minFontSize the minFontSize to set
     */
    public void setMinFontSize(final float minFontSize) {
        m_minFontSize = minFontSize;
    }

    /**
     * @return the maxFontSize
     */
    public float getMaxFontSize() {
        return m_maxFontSize;
    }

    /**
     * @param maxFontSize the maxFontSize to set
     */
    public void setMaxFontSize(final float maxFontSize) {
        m_maxFontSize = maxFontSize;
    }

    /**
     * @return the fontScaleType
     */
    public TagCloudFontScaleType getFontScaleType() {
        return m_fontScaleType;
    }

    /**
     * @param fontScaleType the fontScaleType to set
     */
    public void setFontScaleType(final TagCloudFontScaleType fontScaleType) {
        m_fontScaleType = fontScaleType;
    }

    /**
     * @return the fontBold
     */
    public boolean getFontBold() {
        return m_fontBold;
    }

    /**
     * @param fontBold the fontBold to set
     */
    public void setFontBold(final boolean fontBold) {
        m_fontBold = fontBold;
    }

    /**
     * @return the spiralType
     */
    public TagCloudSpiralType getSpiralType() {
        return m_spiralType;
    }

    /**
     * @param spiralType the spiralType to set
     */
    public void setSpiralType(final TagCloudSpiralType spiralType) {
        m_spiralType = spiralType;
    }

    /**
     * @return the numOrientations
     */
    public int getNumOrientations() {
        return m_numOrientations;
    }

    /**
     * @param numOrientations the numOrientations to set
     */
    public void setNumOrientations(final int numOrientations) {
        m_numOrientations = numOrientations;
    }

    /**
     * @return the startAngle
     */
    public int getStartAngle() {
        return m_startAngle;
    }

    /**
     * @param startAngle the startAngle to set
     */
    public void setStartAngle(final int startAngle) {
        m_startAngle = startAngle;
    }

    /**
     * @return the endAngle
     */
    public int getEndAngle() {
        return m_endAngle;
    }

    /**
     * @param endAngle the endAngle to set
     */
    public void setEndAngle(final int endAngle) {
        m_endAngle = endAngle;
    }

    /**
     * @return the enableViewConfig
     */
    public boolean getEnableViewConfig() {
        return m_enableViewConfig;
    }

    /**
     * @param enableViewConfig the enableViewConfig to set
     */
    public void setEnableViewConfig(final boolean enableViewConfig) {
        m_enableViewConfig = enableViewConfig;
    }

    /**
     * @return the enableTitleChange
     */
    public boolean getEnableTitleChange() {
        return m_enableTitleChange;
    }

    /**
     * @param enableTitleChange the enableTitleChange to set
     */
    public void setEnableTitleChange(final boolean enableTitleChange) {
        m_enableTitleChange = enableTitleChange;
    }

    /**
     * @return the enableSubtitleChange
     */
    public boolean getEnableSubtitleChange() {
        return m_enableSubtitleChange;
    }

    /**
     * @param enableSubtitleChange the enableSubtitleChange to set
     */
    public void setEnableSubtitleChange(final boolean enableSubtitleChange) {
        m_enableSubtitleChange = enableSubtitleChange;
    }

    /**
     * @return the enableFontSizeChante
     */
    public boolean getEnableFontSizeChange() {
        return m_enableFontSizeChange;
    }

    /**
     * @param enableFontSizeChange the enableFontSizeChange to set
     */
    public void setEnableFontSizeChange(final boolean enableFontSizeChange) {
        m_enableFontSizeChange = enableFontSizeChange;
    }

    /**
     * @return the enableScaleTypeChange
     */
    public boolean getEnableScaleTypeChange() {
        return m_enableScaleTypeChange;
    }

    /**
     * @param enableScaleTypeChange the enableScaleTypeChange to set
     */
    public void setEnableScaleTypeChange(final boolean enableScaleTypeChange) {
        m_enableScaleTypeChange = enableScaleTypeChange;
    }

    /**
     * @return the enableSpiralTypeChange
     */
    public boolean getEnableSpiralTypeChange() {
        return m_enableSpiralTypeChange;
    }

    /**
     * @param enableSpiralTypeChange the enableSpiralTypeChange to set
     */
    public void setEnableSpiralTypeChange(final boolean enableSpiralTypeChange) {
        m_enableSpiralTypeChange = enableSpiralTypeChange;
    }

    /**
     * @return the enableNumOrientationsChange
     */
    public boolean getEnableNumOrientationsChange() {
        return m_enableNumOrientationsChange;
    }

    /**
     * @param enableNumOrientationsChange the enableNumOrientationsChange to set
     */
    public void setEnableNumOrientationsChange(final boolean enableNumOrientationsChange) {
        m_enableNumOrientationsChange = enableNumOrientationsChange;
    }

    /**
     * @return the enableAnglesChange
     */
    public boolean getEnableAnglesChange() {
        return m_enableAnglesChange;
    }

    /**
     * @param enableAnglesChange the enableAnglesChange to set
     */
    public void setEnableAnglesChange(final boolean enableAnglesChange) {
        m_enableAnglesChange = enableAnglesChange;
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
     * @return the selectionColor
     */
    public Color getSelectionColor() {
        return m_selectionColor;
    }

    /**
     * @return the selectionColor as hex string
     */
    public String getSelectionColorString() {
        return CSSUtils.cssHexStringFromColor(m_selectionColor);
    }

    /**
     * @param selectionColor the selectionColor to set
     */
    public void setSelectionColor(final Color selectionColor) {
        m_selectionColor = selectionColor;
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
     * @return the defaultShowSelectedOnly
     */
    public boolean getDefaultShowSelectedOnly() {
        return m_defaultShowSelectedOnly;
    }

    /**
     * @param defaultShowSelectedOnly the defaultShowSelectedOnly to set
     */
    public void setDefaultShowSelectedOnly(final boolean defaultShowSelectedOnly) {
        m_defaultShowSelectedOnly = defaultShowSelectedOnly;
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
        settings.addBoolean(CFG_HIDE_IN_WIZARD, m_hideInWizard);
        settings.addBoolean(CFG_GENERATE_IMAGE, m_generateImage);
        settings.addBoolean(CFG_WARNINGS_IN_VIEW, m_showWarningsInView);
        settings.addBoolean(CFG_REPORT_MISSING_VALUES, m_reportMissingValues);
        settings.addBoolean(CFG_RESIZE_TO_WINDOW, m_resizeToWindow);
        settings.addInt(CFG_IMAGE_WIDTH, m_imageWidth);
        settings.addInt(CFG_IMAGE_HEIGHT, m_imageHeight);
        settings.addInt(CFG_MAX_WORDS, m_maxWords);
        settings.addBoolean(CFG_AGGREGATE_WORDS, m_aggregateWords);
        settings.addBoolean(CFG_IGNORE_TERM_TAGS, m_ignoreTermTags);
        settings.addBoolean(CFG_DISPLAY_FULLSCREEN_BUTTON, m_displayFullscreenButton);
        settings.addBoolean(CFG_DISPLAY_REFRESH_BUTTON, m_displayRefreshButton);
        settings.addBoolean(CFG_DISPLAY_CLEAR_SELECTION_BUTTON, m_displayClearSelectionButton);
        settings.addBoolean(CFG_DISABLE_ANIMATIONS, m_disableAnimations);
        settings.addString(CFG_TITLE, m_title);
        settings.addString(CFG_SUBTITLE, m_subtitle);
        settings.addString(CFG_WORD_COLUMN, m_wordColumn);
        settings.addString(CFG_SIZE_COLUMN, m_sizeColumn);
        settings.addBoolean(CFG_USE_SIZE_PROP, m_useSizeProp);
        settings.addBoolean(CFG_USE_COLOR_PROP, m_useColorProp);
        settings.addString(CFG_FONT, m_font);
        settings.addFloat(CFG_MIN_FONT_SIZE, m_minFontSize);
        settings.addFloat(CFG_MAX_FONT_SIZE, m_maxFontSize);
        settings.addString(CFG_FONT_SCALE_TYPE, m_fontScaleType.toValue());
        settings.addBoolean(CFG_FONT_BOLD, m_fontBold);
        settings.addString(CFG_SPIRAL_TYPE, m_spiralType.toValue());
        settings.addInt(CFG_NUM_ORIENTATIONS, m_numOrientations);
        settings.addInt(CFG_START_ANGLE, m_startAngle);
        settings.addInt(CFG_END_ANGLE, m_endAngle);
        settings.addBoolean(CFG_ENABLE_CONFIG, m_enableViewConfig);
        settings.addBoolean(CFG_ENABLE_TITLE_CHANGE, m_enableTitleChange);
        settings.addBoolean(CFG_ENABLE_SUBTITLE_CHANGE, m_enableSubtitleChange);
        settings.addBoolean(CFG_ENABLE_FONT_SIZE_CHANGE, m_enableFontSizeChange);
        settings.addBoolean(CFG_ENABLE_SCALE_TYPE_CHANGE, m_enableScaleTypeChange);
        settings.addBoolean(CFG_ENABLE_SPIRAL_TYPE_CHANGE, m_enableSpiralTypeChange);
        settings.addBoolean(CFG_ENABLE_NUM_ORIENTATIONS_CHANGE, m_enableNumOrientationsChange);
        settings.addBoolean(CFG_ENABLE_ANGLES_CHANGE, m_enableAnglesChange);
        settings.addBoolean(CFG_ENABLE_SELECTION, m_enableSelection);
        settings.addString(CFG_SELECTION_COLUMN_NAME, m_selectionColumnName);
        settings.addString(CFG_SELECTION_COLOR, getSelectionColorString());
        settings.addBoolean(CFG_PUBLISH_SELECTION, m_publishSelection);
        settings.addBoolean(CFG_SUBSCRIBE_SELECTION, m_subscribeSelection);
        settings.addBoolean(CFG_ENABLE_SHOW_SELECTED_ONLY, m_enableShowSelectedOnly);
        settings.addBoolean(CFG_DEFAULT_SHOW_SELECTED_ONLY, m_defaultShowSelectedOnly);
        settings.addBoolean(CFG_SUBSCRIBE_FILTER, m_subscribeFilter);

        //added with 3.6
        settings.addString(CFG_CUSTOM_CSS, m_customCSS);
    }

    /** Loads parameters in NodeModel.
     * @param settings To load from.
     * @throws InvalidSettingsException If incomplete or wrong.
     */
    public void loadSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
        m_hideInWizard = settings.getBoolean(CFG_HIDE_IN_WIZARD);
        m_generateImage = settings.getBoolean(CFG_GENERATE_IMAGE);
        m_showWarningsInView = settings.getBoolean(CFG_WARNINGS_IN_VIEW);
        m_reportMissingValues = settings.getBoolean(CFG_REPORT_MISSING_VALUES);
        m_resizeToWindow = settings.getBoolean(CFG_RESIZE_TO_WINDOW);
        m_imageWidth = settings.getInt(CFG_IMAGE_WIDTH);
        m_imageHeight = settings.getInt(CFG_IMAGE_HEIGHT);
        m_maxWords = settings.getInt(CFG_MAX_WORDS);
        m_aggregateWords = settings.getBoolean(CFG_AGGREGATE_WORDS);
        m_ignoreTermTags = settings.getBoolean(CFG_IGNORE_TERM_TAGS);
        m_displayFullscreenButton = settings.getBoolean(CFG_DISPLAY_FULLSCREEN_BUTTON);
        m_displayRefreshButton = settings.getBoolean(CFG_DISPLAY_REFRESH_BUTTON);
        m_displayClearSelectionButton = settings.getBoolean(CFG_DISPLAY_CLEAR_SELECTION_BUTTON);
        m_disableAnimations = settings.getBoolean(CFG_DISABLE_ANIMATIONS);
        m_title = settings.getString(CFG_TITLE);
        m_subtitle = settings.getString(CFG_SUBTITLE);
        m_wordColumn = settings.getString(CFG_WORD_COLUMN);
        m_sizeColumn = settings.getString(CFG_SIZE_COLUMN);
        m_useSizeProp = settings.getBoolean(CFG_USE_SIZE_PROP);
        m_useColorProp = settings.getBoolean(CFG_USE_COLOR_PROP);
        m_font = settings.getString(CFG_FONT);
        m_minFontSize = settings.getFloat(CFG_MIN_FONT_SIZE);
        m_maxFontSize = settings.getFloat(CFG_MAX_FONT_SIZE);
        m_fontScaleType = TagCloudFontScaleType.forValue(settings.getString(CFG_FONT_SCALE_TYPE));
        m_fontBold = settings.getBoolean(CFG_FONT_BOLD);
        m_spiralType = TagCloudSpiralType.forValue(settings.getString(CFG_SPIRAL_TYPE));
        m_numOrientations = settings.getInt(CFG_NUM_ORIENTATIONS);
        m_startAngle = settings.getInt(CFG_START_ANGLE);
        m_endAngle = settings.getInt(CFG_END_ANGLE);
        m_enableViewConfig = settings.getBoolean(CFG_ENABLE_CONFIG);
        m_enableTitleChange = settings.getBoolean(CFG_ENABLE_TITLE_CHANGE);
        m_enableSubtitleChange = settings.getBoolean(CFG_ENABLE_SUBTITLE_CHANGE);
        m_enableFontSizeChange = settings.getBoolean(CFG_ENABLE_FONT_SIZE_CHANGE);
        m_enableScaleTypeChange = settings.getBoolean(CFG_ENABLE_SCALE_TYPE_CHANGE);
        m_enableSpiralTypeChange = settings.getBoolean(CFG_ENABLE_SPIRAL_TYPE_CHANGE);
        m_enableNumOrientationsChange = settings.getBoolean(CFG_ENABLE_NUM_ORIENTATIONS_CHANGE);
        m_enableAnglesChange = settings.getBoolean(CFG_ENABLE_ANGLES_CHANGE);
        m_enableSelection = settings.getBoolean(CFG_ENABLE_SELECTION);
        m_selectionColumnName = settings.getString(CFG_SELECTION_COLUMN_NAME);
        m_selectionColor = CSSUtils.colorFromCssHexString(settings.getString(CFG_SELECTION_COLOR));
        m_publishSelection = settings.getBoolean(CFG_PUBLISH_SELECTION);
        m_subscribeSelection = settings.getBoolean(CFG_SUBSCRIBE_SELECTION);
        m_enableShowSelectedOnly = settings.getBoolean(CFG_ENABLE_SHOW_SELECTED_ONLY);
        m_defaultShowSelectedOnly = settings.getBoolean(CFG_DEFAULT_SHOW_SELECTED_ONLY);
        m_subscribeFilter = settings.getBoolean(CFG_SUBSCRIBE_FILTER);

        //added with 3.6
        m_customCSS = settings.getString(CFG_CUSTOM_CSS, DEFAULT_CUSTOM_CSS);
    }

    /** Loads parameters in Dialog.
     * @param settings To load from.
     * @param spec The spec from the incoming data table
     */
    public void loadSettingsForDialog(final NodeSettingsRO settings, final DataTableSpec spec) {
        m_hideInWizard = settings.getBoolean(CFG_HIDE_IN_WIZARD, DEFAULT_HIDE_IN_WIZARD);
        m_generateImage = settings.getBoolean(CFG_GENERATE_IMAGE, DEFAULT_GENERATE_IMAGE);
        m_showWarningsInView = settings.getBoolean(CFG_WARNINGS_IN_VIEW, DEFAULT_WARNINGS_IN_VIEW);
        m_reportMissingValues = settings.getBoolean(CFG_REPORT_MISSING_VALUES, DEFAULT_REPORT_MISSING_VALUES);
        m_resizeToWindow = settings.getBoolean(CFG_RESIZE_TO_WINDOW, DEFAULT_RESIZE_TO_WINDOW);
        m_imageWidth = settings.getInt(CFG_IMAGE_WIDTH, DEFAULT_IMAGE_WIDTH);
        m_imageHeight = settings.getInt(CFG_IMAGE_HEIGHT, DEFAULT_IMAGE_HEIGHT);
        m_maxWords = settings.getInt(CFG_MAX_WORDS, DEFAULT_MAX_WORDS);
        m_aggregateWords = settings.getBoolean(CFG_AGGREGATE_WORDS, DEFAULT_AGGREGATE_WORDS);
        m_ignoreTermTags = settings.getBoolean(CFG_IGNORE_TERM_TAGS, DEFAULT_IGNORE_TERM_TAGS);
        m_displayFullscreenButton = settings.getBoolean(CFG_DISPLAY_FULLSCREEN_BUTTON, DEFAULT_DISPLAY_FULLSCREEN_BUTTON);
        m_displayRefreshButton = settings.getBoolean(CFG_DISPLAY_REFRESH_BUTTON, DEFAULT_DISPLAY_REFRESH_BUTTON);
        m_displayClearSelectionButton = settings.getBoolean(CFG_DISPLAY_CLEAR_SELECTION_BUTTON, DEFAULT_DISPLAY_CLEAR_SELECTION_BUTTON);
        m_disableAnimations = settings.getBoolean(CFG_DISABLE_ANIMATIONS, DEFAULT_DISABLE_ANIMATIONS);
        m_title = settings.getString(CFG_TITLE, DEFAULT_TITLE);
        m_subtitle = settings.getString(CFG_SUBTITLE, DEFAULT_SUBTITLE);
        m_wordColumn = settings.getString(CFG_WORD_COLUMN, DEFAULT_WORD_COLUMN);
        m_sizeColumn = settings.getString(CFG_SIZE_COLUMN, DEFAULT_SIZE_COLUMN);
        m_useSizeProp = settings.getBoolean(CFG_USE_SIZE_PROP, DEFAULT_USE_SIZE_PROP);
        m_useColorProp = settings.getBoolean(CFG_USE_COLOR_PROP, DEFAULT_USE_COLOR_PROP);
        m_font = settings.getString(CFG_FONT, DEFAULT_FONT);
        m_minFontSize = settings.getFloat(CFG_MIN_FONT_SIZE, DEFAULT_MIN_FONT_SIZE);
        m_maxFontSize = settings.getFloat(CFG_MAX_FONT_SIZE, DEFAULT_MAX_FONT_SIZE);
        m_fontScaleType = TagCloudFontScaleType.forValue(settings.getString(CFG_FONT_SCALE_TYPE, DEFAULT_FONT_SCALE_TYPE.toValue()));
        m_fontBold = settings.getBoolean(CFG_FONT_BOLD, DEFAULT_FONT_BOLD);
        m_spiralType = TagCloudSpiralType.forValue(settings.getString(CFG_SPIRAL_TYPE, DEFAULT_SPIRAL_TYPE.toValue()));
        m_numOrientations = settings.getInt(CFG_NUM_ORIENTATIONS, DEFAULT_NUM_ORIENTATIONS);
        m_startAngle = settings.getInt(CFG_START_ANGLE, DEFAULT_START_ANGLE);
        m_endAngle = settings.getInt(CFG_END_ANGLE, DEFAULT_END_ANGLE);
        m_enableViewConfig = settings.getBoolean(CFG_ENABLE_CONFIG, DEFAULT_ENABLE_CONFIG);
        m_enableTitleChange = settings.getBoolean(CFG_ENABLE_TITLE_CHANGE, DEFAULT_ENABLE_TITLE_CHANGE);
        m_enableSubtitleChange = settings.getBoolean(CFG_ENABLE_SUBTITLE_CHANGE, DEFAULT_ENABLE_SUBTITLE_CHANGE);
        m_enableFontSizeChange = settings.getBoolean(CFG_ENABLE_FONT_SIZE_CHANGE, DEFAULT_ENABLE_FONT_SIZE_CHANGE);
        m_enableScaleTypeChange = settings.getBoolean(CFG_ENABLE_SCALE_TYPE_CHANGE, DEFAULT_ENABLE_SCALE_TYPE_CHANGE);
        m_enableSpiralTypeChange = settings.getBoolean(CFG_ENABLE_SPIRAL_TYPE_CHANGE, DEFAULT_ENABLE_SPIRAL_TYPE_CHANGE);
        m_enableNumOrientationsChange = settings.getBoolean(CFG_ENABLE_NUM_ORIENTATIONS_CHANGE, DEFAULT_ENABLE_NUM_ORIENTATIONS_CHANGE);
        m_enableAnglesChange = settings.getBoolean(CFG_ENABLE_ANGLES_CHANGE, DEFAULT_ENABLE_ANGLES_CHANGE);
        m_enableSelection = settings.getBoolean(CFG_ENABLE_SELECTION, DEFAULT_ENABLE_SELECTION);
        m_selectionColumnName = settings.getString(CFG_SELECTION_COLUMN_NAME, DEFAULT_SELECTION_COLUMN_NAME);
        String defaultColorString = CSSUtils.cssHexStringFromColor(DEFAULT_SELECTION_COLOR);
        m_selectionColor = CSSUtils.colorFromCssHexString(settings.getString(CFG_SELECTION_COLOR, defaultColorString));
        m_publishSelection = settings.getBoolean(CFG_PUBLISH_SELECTION, DEFAULT_PUBLISH_SELECTION);
        m_subscribeSelection = settings.getBoolean(CFG_SUBSCRIBE_SELECTION, DEFAULT_SUBSCRIBE_SELECTION);
        m_enableShowSelectedOnly = settings.getBoolean(CFG_ENABLE_SHOW_SELECTED_ONLY, DEFAULT_ENABLE_SHOW_SELECTED_ONLY);
        m_defaultShowSelectedOnly = settings.getBoolean(CFG_DEFAULT_SHOW_SELECTED_ONLY, DEFAULT_DEFAULT_SHOW_SELECTED_ONLY);
        m_subscribeFilter = settings.getBoolean(CFG_SUBSCRIBE_FILTER, DEFAULT_SUBSCRIBE_FILTER);

        //added with 3.6
        m_customCSS = settings.getString(CFG_CUSTOM_CSS, DEFAULT_CUSTOM_CSS);
    }

}
