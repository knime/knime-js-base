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

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.js.core.JSONViewContent;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * JSON serializable representation object for the tag cloud view
 *
 * @author Christian Albrecht, KNIME GmbH, Konstanz, Germany
 */
@JsonAutoDetect
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public class TagCloudViewValue extends JSONViewContent {

    private static final String CFG_SELECTION = "selection";
    private String[] m_selection;

    private String m_title;
    private String m_subtitle;
    private float m_minFontSize;
    private float m_maxFontSize;
    private TagCloudFontScaleType m_fontScaleType;
    private TagCloudSpiralType m_spiralType;
    private int m_numOrientations;
    private int m_startAngle;
    private int m_endAngle;
    private boolean m_publishSelection;
    private boolean m_subscribeSelection;
    private boolean m_showSelectedOnly;
    private boolean m_subscribeFilter;

    /* Workaround for font measuring and collision detection difficulty in PhantomJS,
     * as well as not having seeded randoms available (reproducible results) */
    private final static String CFG_SVG_FROM_VIEW = "svgFromView";
    private String m_svgFromView;

    /**
     * @return the selection
     */
    public String[] getSelection() {
        return m_selection;
    }

    /**
     * @param selection the selection to set
     */
    public void setSelection(final String[] selection) {
        m_selection = selection;
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
     * @return the showSelectedOnly
     */
    public boolean getShowSelectedOnly() {
        return m_showSelectedOnly;
    }

    /**
     * @param showSelectedOnly the showSelectedOnly to set
     */
    public void setShowSelectedOnly(final boolean showSelectedOnly) {
        m_showSelectedOnly = showSelectedOnly;
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
     * @return the svgFromView
     */
    public String getSvgFromView() {
        return m_svgFromView;
    }

    /**
     * @param svgFromView the svgFromView to set
     */
    public void setSvgFromView(final String svgFromView) {
        m_svgFromView = svgFromView;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveToNodeSettings(final NodeSettingsWO settings) {
        settings.addStringArray(CFG_SELECTION, m_selection);
        settings.addString(TagCloudViewConfig.CFG_TITLE, m_title);
        settings.addString(TagCloudViewConfig.CFG_SUBTITLE, m_subtitle);
        settings.addFloat(TagCloudViewConfig.CFG_MIN_FONT_SIZE, m_minFontSize);
        settings.addFloat(TagCloudViewConfig.CFG_MAX_FONT_SIZE, m_maxFontSize);
        settings.addString(TagCloudViewConfig.CFG_FONT_SCALE_TYPE, m_fontScaleType.toValue());
        settings.addString(TagCloudViewConfig.CFG_SPIRAL_TYPE, m_spiralType.toValue());
        settings.addInt(TagCloudViewConfig.CFG_NUM_ORIENTATIONS, m_numOrientations);
        settings.addInt(TagCloudViewConfig.CFG_START_ANGLE, m_startAngle);
        settings.addInt(TagCloudViewConfig.CFG_END_ANGLE, m_endAngle);
        settings.addBoolean(TagCloudViewConfig.CFG_PUBLISH_SELECTION, m_publishSelection);
        settings.addBoolean(TagCloudViewConfig.CFG_SUBSCRIBE_SELECTION, m_subscribeSelection);
        settings.addBoolean(TagCloudViewConfig.CFG_DEFAULT_SHOW_SELECTED_ONLY, m_showSelectedOnly);
        settings.addBoolean(TagCloudViewConfig.CFG_SUBSCRIBE_FILTER, m_subscribeFilter);

        settings.addString(CFG_SVG_FROM_VIEW, m_svgFromView);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadFromNodeSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
        m_selection = settings.getStringArray(CFG_SELECTION);
        m_title = settings.getString(TagCloudViewConfig.CFG_TITLE);
        m_subtitle = settings.getString(TagCloudViewConfig.CFG_SUBTITLE);
        m_minFontSize = settings.getFloat(TagCloudViewConfig.CFG_MIN_FONT_SIZE);
        m_maxFontSize = settings.getFloat(TagCloudViewConfig.CFG_MAX_FONT_SIZE);
        m_fontScaleType = TagCloudFontScaleType.forValue(settings.getString(TagCloudViewConfig.CFG_FONT_SCALE_TYPE));
        m_spiralType = TagCloudSpiralType.forValue(settings.getString(TagCloudViewConfig.CFG_SPIRAL_TYPE));
        m_numOrientations = settings.getInt(TagCloudViewConfig.CFG_NUM_ORIENTATIONS);
        m_startAngle = settings.getInt(TagCloudViewConfig.CFG_START_ANGLE);
        m_endAngle = settings.getInt(TagCloudViewConfig.CFG_END_ANGLE);
        m_publishSelection = settings.getBoolean(TagCloudViewConfig.CFG_PUBLISH_SELECTION);
        m_subscribeSelection = settings.getBoolean(TagCloudViewConfig.CFG_SUBSCRIBE_SELECTION);
        m_showSelectedOnly = settings.getBoolean(TagCloudViewConfig.CFG_DEFAULT_SHOW_SELECTED_ONLY);
        m_subscribeFilter = settings.getBoolean(TagCloudViewConfig.CFG_SUBSCRIBE_FILTER);

        m_svgFromView = settings.getString(CFG_SVG_FROM_VIEW);
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
        TagCloudViewValue other = (TagCloudViewValue)obj;
        return new EqualsBuilder()
                .append(m_selection, other.m_selection)
                .append(m_title, other.m_title)
                .append(m_subtitle, other.m_subtitle)
                .append(m_minFontSize, other.m_minFontSize)
                .append(m_maxFontSize, other.m_maxFontSize)
                .append(m_fontScaleType, other.m_fontScaleType)
                .append(m_spiralType, other.m_spiralType)
                .append(m_numOrientations, other.m_numOrientations)
                .append(m_startAngle, other.m_startAngle)
                .append(m_svgFromView, other.m_svgFromView)
                .append(m_publishSelection, other.m_publishSelection)
                .append(m_subscribeSelection, other.m_subscribeSelection)
                .append(m_showSelectedOnly, other.m_showSelectedOnly)
                .append(m_subscribeFilter, other.m_subscribeFilter)
                .isEquals();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(m_selection)
                .append(m_title)
                .append(m_subtitle)
                .append(m_minFontSize)
                .append(m_maxFontSize)
                .append(m_fontScaleType)
                .append(m_spiralType)
                .append(m_numOrientations)
                .append(m_startAngle)
                .append(m_svgFromView)
                .append(m_publishSelection)
                .append(m_subscribeSelection)
                .append(m_showSelectedOnly)
                .append(m_subscribeFilter)
                .toHashCode();
    }

}
