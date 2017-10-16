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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.js.core.JSONViewContent;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * JSON serializable representation object for the word cloud view
 *
 * @author Christian Albrecht, KNIME GmbH, Konstanz, Germany
 */
@JsonAutoDetect
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public class WordCloudViewRepresentation extends JSONViewContent {

    private static final String CFG_DATA = "data";
    private static final String CFG_NUM_SETTINGS = "numSettings";
    private List<WordCloudData> m_data;
    private static final String CFG_IMAGE_GENERATION = "imageGeneration";
    private boolean m_isImageGeneration;

    private boolean m_displayFullscreenButton;
    private String m_font;
    private boolean m_enableViewConfig;
    private boolean m_enableTitleChange;
    private boolean m_enableSubtitleChange;
    private boolean m_enableFontSizeChange;
    private boolean m_enableScaleTypeChange;
    private boolean m_enableSpiralTypeChange;
    private boolean m_enableNumOrientationsChange;
    private boolean m_enableAnglesChange;

    /**
     * @return the data
     */
    public List<WordCloudData> getData() {
        return m_data;
    }

    /**
     * @param data the data to set
     */
    public void setData(final List<WordCloudData> data) {
        m_data = data;
    }

    /**
     * @return the isImageGeneration
     */
    public boolean getImageGeneration() {
        return m_isImageGeneration;
    }

    /**
     * @param isImageGeneration the isImageGeneration to set
     */
    public void setImageGeneration(final boolean isImageGeneration) {
        m_isImageGeneration = isImageGeneration;
    }

    /**
     * @return the displayFullscreenButton
     */
    public boolean isDisplayFullscreenButton() {
        return m_displayFullscreenButton;
    }

    /**
     * @param displayFullscreenButton the displayFullscreenButton to set
     */
    public void setDisplayFullscreenButton(final boolean displayFullscreenButton) {
        m_displayFullscreenButton = displayFullscreenButton;
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
     * {@inheritDoc}
     */
    @Override
    public void saveToNodeSettings(final NodeSettingsWO settings) {
        NodeSettingsWO dataSettings = settings.addNodeSettings(CFG_DATA);
        int numSettings = m_data == null ? 0 : m_data.size();
        dataSettings.addInt(CFG_NUM_SETTINGS, numSettings);
        for (int i = 0; i < numSettings; i++) {
            NodeSettingsWO indSettings = dataSettings.addNodeSettings(Integer.toString(i));
            m_data.get(i).saveToNodeSettings(indSettings);
        }
        settings.addBoolean(CFG_IMAGE_GENERATION, m_isImageGeneration);
        settings.addBoolean(WordCloudViewConfig.CFG_DISPLAY_FULLSCREEN_BUTTON, m_displayFullscreenButton);
        settings.addString(WordCloudViewConfig.CFG_FONT, m_font);
        settings.addBoolean(WordCloudViewConfig.CFG_ENABLE_CONFIG, m_enableViewConfig);
        settings.addBoolean(WordCloudViewConfig.CFG_ENABLE_TITLE_CHANGE, m_enableTitleChange);
        settings.addBoolean(WordCloudViewConfig.CFG_ENABLE_SUBTITLE_CHANGE, m_enableSubtitleChange);
        settings.addBoolean(WordCloudViewConfig.CFG_ENABLE_FONT_SIZE_CHANGE, m_enableFontSizeChange);
        settings.addBoolean(WordCloudViewConfig.CFG_ENABLE_SCALE_TYPE_CHANGE, m_enableScaleTypeChange);
        settings.addBoolean(WordCloudViewConfig.CFG_ENABLE_SPIRAL_TYPE_CHANGE, m_enableSpiralTypeChange);
        settings.addBoolean(WordCloudViewConfig.CFG_ENABLE_NUM_ORIENTATIONS_CHANGE, m_enableNumOrientationsChange);
        settings.addBoolean(WordCloudViewConfig.CFG_ENABLE_ANGLES_CHANGE, m_enableAnglesChange);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadFromNodeSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
        NodeSettingsRO dataSettings = settings.getNodeSettings(CFG_DATA);
        int numSettings = dataSettings.getInt(CFG_NUM_SETTINGS);
        if (numSettings > 0) {
            m_data = new ArrayList<WordCloudData>(numSettings);
            for (int i = 0; i < numSettings; i++) {
                NodeSettingsRO indSettings = dataSettings.getNodeSettings(Integer.toString(i));
                WordCloudData indData = new WordCloudData();
                indData.loadFromNodeSettings(indSettings);
                m_data.add(indData);
            }
        }
        m_isImageGeneration = settings.getBoolean(CFG_IMAGE_GENERATION);
        m_displayFullscreenButton = settings.getBoolean(WordCloudViewConfig.CFG_DISPLAY_FULLSCREEN_BUTTON);
        m_font = settings.getString(WordCloudViewConfig.CFG_FONT);
        m_enableViewConfig = settings.getBoolean(WordCloudViewConfig.CFG_ENABLE_CONFIG);
        m_enableTitleChange = settings.getBoolean(WordCloudViewConfig.CFG_ENABLE_TITLE_CHANGE);
        m_enableSubtitleChange = settings.getBoolean(WordCloudViewConfig.CFG_ENABLE_SUBTITLE_CHANGE);
        m_enableFontSizeChange = settings.getBoolean(WordCloudViewConfig.CFG_ENABLE_FONT_SIZE_CHANGE);
        m_enableScaleTypeChange = settings.getBoolean(WordCloudViewConfig.CFG_ENABLE_SCALE_TYPE_CHANGE);
        m_enableSpiralTypeChange = settings.getBoolean(WordCloudViewConfig.CFG_ENABLE_SPIRAL_TYPE_CHANGE);
        m_enableNumOrientationsChange = settings.getBoolean(WordCloudViewConfig.CFG_ENABLE_NUM_ORIENTATIONS_CHANGE);
        m_enableAnglesChange = settings.getBoolean(WordCloudViewConfig.CFG_ENABLE_ANGLES_CHANGE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj.getClass() != getClass()) {
            return false;
        }
        WordCloudViewRepresentation other = (WordCloudViewRepresentation)obj;
        return new EqualsBuilder()
                .append(m_data, other.m_data)
                .append(m_isImageGeneration, other.m_isImageGeneration)
                .append(m_displayFullscreenButton, other.m_displayFullscreenButton)
                .append(m_font, other.m_font)
                .append(m_enableViewConfig, other.m_enableViewConfig)
                .append(m_enableTitleChange, other.m_enableTitleChange)
                .append(m_enableSubtitleChange, other.m_enableSubtitleChange)
                .append(m_enableFontSizeChange, other.m_enableFontSizeChange)
                .append(m_enableScaleTypeChange, other.m_enableScaleTypeChange)
                .append(m_enableSpiralTypeChange, other.m_enableSpiralTypeChange)
                .append(m_enableNumOrientationsChange, other.m_enableNumOrientationsChange)
                .append(m_enableAnglesChange, other.m_enableAnglesChange)
                .isEquals();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(m_data)
                .append(m_isImageGeneration)
                .append(m_displayFullscreenButton)
                .append(m_font)
                .append(m_enableViewConfig)
                .append(m_enableTitleChange)
                .append(m_enableSubtitleChange)
                .append(m_enableFontSizeChange)
                .append(m_enableScaleTypeChange)
                .append(m_enableSpiralTypeChange)
                .append(m_enableNumOrientationsChange)
                .append(m_enableAnglesChange)
                .toHashCode();
    }

}
