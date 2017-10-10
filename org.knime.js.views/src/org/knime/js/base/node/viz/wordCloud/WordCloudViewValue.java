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
public class WordCloudViewValue extends JSONViewContent {

    private String m_title;
    private String m_subtitle;
    private float m_minFontSize;
    private float m_maxFontSize;
    private WordCloudFontScaleType m_fontScaleType;
    private WordCloudSpiralType m_spiralType;
    private int m_numOrientations;
    private int m_startAngle;
    private int m_endAngle;

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
     * {@inheritDoc}
     */
    @Override
    public void saveToNodeSettings(final NodeSettingsWO settings) {
        settings.addString(WordCloudViewConfig.CFG_TITLE, m_title);
        settings.addString(WordCloudViewConfig.CFG_SUBTITLE, m_subtitle);
        settings.addFloat(WordCloudViewConfig.CFG_MIN_FONT_SIZE, m_minFontSize);
        settings.addFloat(WordCloudViewConfig.CFG_MAX_FONT_SIZE, m_maxFontSize);
        settings.addString(WordCloudViewConfig.CFG_FONT_SCALE_TYPE, m_fontScaleType.toValue());
        settings.addString(WordCloudViewConfig.CFG_SPIRAL_TYPE, m_spiralType.toValue());
        settings.addInt(WordCloudViewConfig.CFG_NUM_ORIENTATIONS, m_numOrientations);
        settings.addInt(WordCloudViewConfig.CFG_START_ANGLE, m_startAngle);
        settings.addInt(WordCloudViewConfig.CFG_END_ANGLE, m_endAngle);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadFromNodeSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
        m_title = settings.getString(WordCloudViewConfig.CFG_TITLE);
        m_subtitle = settings.getString(WordCloudViewConfig.CFG_SUBTITLE);
        m_minFontSize = settings.getFloat(WordCloudViewConfig.CFG_MIN_FONT_SIZE);
        m_maxFontSize = settings.getFloat(WordCloudViewConfig.CFG_MAX_FONT_SIZE);
        m_fontScaleType = WordCloudFontScaleType.forValue(settings.getString(WordCloudViewConfig.CFG_FONT_SCALE_TYPE));
        m_spiralType = WordCloudSpiralType.forValue(settings.getString(WordCloudViewConfig.CFG_SPIRAL_TYPE));
        m_numOrientations = settings.getInt(WordCloudViewConfig.CFG_NUM_ORIENTATIONS);
        m_startAngle = settings.getInt(WordCloudViewConfig.CFG_START_ANGLE);
        m_endAngle = settings.getInt(WordCloudViewConfig.CFG_END_ANGLE);
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
        WordCloudViewValue other = (WordCloudViewValue)obj;
        return new EqualsBuilder()
                .append(m_title, other.m_title)
                .append(m_subtitle, other.m_subtitle)
                .append(m_minFontSize, other.m_minFontSize)
                .append(m_maxFontSize, other.m_maxFontSize)
                .append(m_fontScaleType, other.m_fontScaleType)
                .append(m_spiralType, other.m_spiralType)
                .append(m_numOrientations, other.m_numOrientations)
                .append(m_startAngle, other.m_startAngle)
                .isEquals();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(m_title)
                .append(m_subtitle)
                .append(m_minFontSize)
                .append(m_maxFontSize)
                .append(m_fontScaleType)
                .append(m_spiralType)
                .append(m_numOrientations)
                .append(m_startAngle)
                .toHashCode();
    }

}
