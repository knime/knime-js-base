/*
 * ------------------------------------------------------------------------
 *
 *  Copyright by KNIME GmbH, Konstanz, Germany
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
 *   2 Oct 2017 (albrecht): created
 */
package org.knime.js.base.node.viz.wordCloud;

import org.knime.core.data.DataTableSpec;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;

/**
 * Settings class holding the word cloud view config values
 *
 * @author Christian Albrecht, KNIME GmbH, Konstanz, Germany
 */
public class WordCloudViewConfig {

    private final static String CFG_HIDE_IN_WIZARD = "hideInWizard";
    private final static boolean DEFAULT_HIDE_IN_WIZARD = false;
    private boolean m_hideInWizard = DEFAULT_HIDE_IN_WIZARD;

    private static final String CFG_GENERATE_IMAGE = "generateImage";
    private final static boolean DEFAULT_GENERATE_IMAGE = true;
    private boolean m_generateImage = DEFAULT_GENERATE_IMAGE;

    final static String CFG_MAX_WORDS = "maxWords";
    private final static int DEFAULT_MAX_WORDS = 250;
    private int m_maxWords = DEFAULT_MAX_WORDS;

    final static String CFG_DISPLAY_FULLSCREEN_BUTTON = "displayFullscreenButton";
    final static boolean DEFAULT_DISPLAY_FULLSCREEN_BUTTON = true;
    private boolean m_displayFullscreenButton = DEFAULT_DISPLAY_FULLSCREEN_BUTTON;

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
    private final static WordCloudFontScaleType DEFAULT_FONT_SCALE_TYPE = WordCloudFontScaleType.LINEAR;
    private WordCloudFontScaleType m_fontScaleType = DEFAULT_FONT_SCALE_TYPE;

    final static String CFG_SPIRAL_TYPE = "spiralType";
    private final static WordCloudSpiralType DEFAULT_SPIRAL_TYPE = WordCloudSpiralType.ARCHIMEDEAN;
    private WordCloudSpiralType m_spiralType = DEFAULT_SPIRAL_TYPE;

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
    public WordCloudFontScaleType getFontScaleType() {
        return m_fontScaleType;
    }

    /**
     * @param fontScaleType the fontScaleType to set
     */
    public void setFontScaleType(final WordCloudFontScaleType fontScaleType) {
        m_fontScaleType = fontScaleType;
    }

    /**
     * @return the spiralType
     */
    public WordCloudSpiralType getSpiralType() {
        return m_spiralType;
    }

    /**
     * @param spiralType the spiralType to set
     */
    public void setSpiralType(final WordCloudSpiralType spiralType) {
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

    /** Saves current parameters to settings object.
     * @param settings To save to.
     */
    public void saveSettings(final NodeSettingsWO settings) {
        settings.addBoolean(CFG_HIDE_IN_WIZARD, m_hideInWizard);
        settings.addBoolean(CFG_GENERATE_IMAGE, m_generateImage);
        settings.addInt(CFG_MAX_WORDS, m_maxWords);
        settings.addBoolean(CFG_DISPLAY_FULLSCREEN_BUTTON, m_displayFullscreenButton);
        settings.addString(CFG_TITLE, m_title);
        settings.addString(CFG_SUBTITLE, m_subtitle);
        settings.addString(CFG_WORD_COLUMN, m_wordColumn);
        settings.addString(CFG_SIZE_COLUMN, m_sizeColumn);
        settings.addBoolean(CFG_USE_SIZE_PROP, m_useSizeProp);
        settings.addString(CFG_FONT, m_font);
        settings.addFloat(CFG_MIN_FONT_SIZE, m_minFontSize);
        settings.addFloat(CFG_MAX_FONT_SIZE, m_maxFontSize);
        settings.addString(CFG_FONT_SCALE_TYPE, m_fontScaleType.toValue());
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
    }

    /** Loads parameters in NodeModel.
     * @param settings To load from.
     * @throws InvalidSettingsException If incomplete or wrong.
     */
    public void loadSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
        m_hideInWizard = settings.getBoolean(CFG_HIDE_IN_WIZARD);
        m_generateImage = settings.getBoolean(CFG_GENERATE_IMAGE);
        m_maxWords = settings.getInt(CFG_MAX_WORDS);
        m_displayFullscreenButton = settings.getBoolean(CFG_DISPLAY_FULLSCREEN_BUTTON);
        m_title = settings.getString(CFG_TITLE);
        m_subtitle = settings.getString(CFG_SUBTITLE);
        m_wordColumn = settings.getString(CFG_WORD_COLUMN);
        m_sizeColumn = settings.getString(CFG_SIZE_COLUMN);
        m_useSizeProp = settings.getBoolean(CFG_USE_SIZE_PROP);
        m_font = settings.getString(CFG_FONT);
        m_minFontSize = settings.getFloat(CFG_MIN_FONT_SIZE);
        m_maxFontSize = settings.getFloat(CFG_MAX_FONT_SIZE);
        m_fontScaleType = WordCloudFontScaleType.forValue(settings.getString(CFG_FONT_SCALE_TYPE));
        m_spiralType = WordCloudSpiralType.forValue(settings.getString(CFG_SPIRAL_TYPE));
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
    }

    /** Loads parameters in Dialog.
     * @param settings To load from.
     * @param spec The spec from the incoming data table
     */
    public void loadSettingsForDialog(final NodeSettingsRO settings, final DataTableSpec spec) {
        m_hideInWizard = settings.getBoolean(CFG_HIDE_IN_WIZARD, DEFAULT_HIDE_IN_WIZARD);
        m_generateImage = settings.getBoolean(CFG_GENERATE_IMAGE, DEFAULT_GENERATE_IMAGE);
        m_maxWords = settings.getInt(CFG_MAX_WORDS, DEFAULT_MAX_WORDS);
        m_displayFullscreenButton = settings.getBoolean(CFG_DISPLAY_FULLSCREEN_BUTTON, DEFAULT_DISPLAY_FULLSCREEN_BUTTON);
        m_title = settings.getString(CFG_TITLE, DEFAULT_TITLE);
        m_subtitle = settings.getString(CFG_SUBTITLE, DEFAULT_SUBTITLE);
        m_wordColumn = settings.getString(CFG_WORD_COLUMN, DEFAULT_WORD_COLUMN);
        m_sizeColumn = settings.getString(CFG_SIZE_COLUMN, DEFAULT_SIZE_COLUMN);
        m_useSizeProp = settings.getBoolean(CFG_USE_SIZE_PROP, DEFAULT_USE_SIZE_PROP);
        m_font = settings.getString(CFG_FONT, DEFAULT_FONT);
        m_minFontSize = settings.getFloat(CFG_MIN_FONT_SIZE, DEFAULT_MIN_FONT_SIZE);
        m_maxFontSize = settings.getFloat(CFG_MAX_FONT_SIZE, DEFAULT_MAX_FONT_SIZE);
        m_fontScaleType = WordCloudFontScaleType.forValue(settings.getString(CFG_FONT_SCALE_TYPE, DEFAULT_FONT_SCALE_TYPE.toValue()));
        m_spiralType = WordCloudSpiralType.forValue(settings.getString(CFG_SPIRAL_TYPE, DEFAULT_SPIRAL_TYPE.toValue()));
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
    }

}
