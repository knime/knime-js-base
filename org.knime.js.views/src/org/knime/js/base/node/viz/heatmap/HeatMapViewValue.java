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
 *   Aug 3, 2018 (awalter): created
 */
package org.knime.js.base.node.viz.heatmap;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.js.core.JSONViewContent;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * The view value of the Heatmap node.
 *
 * @author Alison Walter, KNIME GmbH, Konstanz, Germany
 */
@JsonAutoDetect
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public class HeatMapViewValue extends JSONViewContent {

    private String m_chartTitle;
    private String m_chartSubtitle;
    private boolean m_showToolTips;

    private boolean m_continuousGradient;

    private final static String CFG_SELECTION = "selection";
    private String[] m_selection;
    private boolean m_publishSelection;
    private boolean m_subscribeSelection;
    private boolean m_subscribeFilter;
    private boolean m_showSelectedRowsOnly;


    private int m_initialPageSize;
    private final static String CFG_CURRENT_PAGE = "currentPage";
    private int m_currentPage;

    private final static String CFG_ZOOM_X = "zoomX";
    private double m_zoomX;
    private final static String CFG_ZOOM_Y = "zoomY";
    private double m_zoomY;
    private final static String CFG_ZOOM_K = "zoomK";
    private double m_zoomK;

    // -- General getters & setters --

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
     * @return the showToolTips
     */
    public boolean getShowToolTips() {
        return m_showToolTips;
    }

    /**
     * @param showToolTips the showToolTips to set
     */
    public void setShowToolTips(final boolean showToolTips) {
        m_showToolTips = showToolTips;
    }

    // -- Gradient getters & setters --

    /**
     * @return the continuousGradient
     */
    public boolean getContinuousGradient() {
        return m_continuousGradient;
    }

    /**
     * @param continuousGradient the continuousGradient to set
     */
    public void setContinuousGradient(final boolean continuousGradient) {
        m_continuousGradient = continuousGradient;
    }

    // -- Selection getters & setters --

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
     * @return the showSelectedRowsOnly
     */
    public boolean getShowSelectedRowsOnly() {
        return m_showSelectedRowsOnly;
    }

    /**
     * @param showSelectedRowsOnly the showSelectedRowsOnly to set
     */
    public void setShowSelectedRowsOnly(final boolean showSelectedRowsOnly) {
        m_showSelectedRowsOnly = showSelectedRowsOnly;
    }

    // -- Filter getters & setters --

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

    // -- Paging getters & setters --

    /**
     * @return the initialPageSize
     */
    public int getInitialPageSize() {
        return m_initialPageSize;
    }

    /**
     * @param initialPageSize the initialPageSize to set
     */
    public void setInitialPageSize(final int initialPageSize) {
        m_initialPageSize = initialPageSize;
    }

    /**
     * @return the currentPage
     */
    public int getCurrentPage() {
        return m_currentPage;
    }

    /**
     * @param currentPage the currentPage to set
     */
    public void setCurrentPage(final int currentPage) {
        m_currentPage = currentPage;
    }

    // -- Zoom & Panning getters & setters --

    /**
     * @return the zoomX
     */
    public double getZoomX() {
        return m_zoomX;
    }

    /**
     * @param zoomX the zoomX to set
     */
    public void setZoomX(final double zoomX) {
        m_zoomX = zoomX;
    }

    /**
     * @return the zoomY
     */
    public double getZoomY() {
        return m_zoomY;
    }

    /**
     * @param zoomY the zoomY to set
     */
    public void setZoomY(final double zoomY) {
        m_zoomY = zoomY;
    }

    /**
     * @return the zoomK
     */
    public double getZoomK() {
        return m_zoomK;
    }

    /**
     * @param zoomK the zoomK to set
     */
    public void setZoomK(final double zoomK) {
        m_zoomK = zoomK;
    }

    // -- Load & Save Settings --

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveToNodeSettings(final NodeSettingsWO settings) {
        settings.addString(HeatMapViewConfig.CFG_CHART_TITLE, m_chartTitle);
        settings.addString(HeatMapViewConfig.CFG_CHART_SUBTITLE, m_chartSubtitle);
        settings.addBoolean(HeatMapViewConfig.CFG_SHOW_TOOL_TIPS, m_showToolTips);

        settings.addBoolean(HeatMapViewConfig.CFG_CONTINUOUS_GRADIENT, m_continuousGradient);

        settings.addStringArray(CFG_SELECTION, m_selection);
        settings.addBoolean(HeatMapViewConfig.CFG_PUBLISH_SELECTION, m_publishSelection);
        settings.addBoolean(HeatMapViewConfig.CFG_SUBSCRIBE_SELECTION, m_subscribeSelection);
        settings.addBoolean(HeatMapViewConfig.CFG_SUBSCRIBE_FILTER, m_subscribeFilter);
        settings.addBoolean(HeatMapViewConfig.CFG_SHOW_SELECTED_ROWS_ONLY, m_showSelectedRowsOnly);

        settings.addInt(HeatMapViewConfig.CFG_INITIAL_PAGE_SIZE, m_initialPageSize);
        settings.addInt(CFG_CURRENT_PAGE, m_currentPage);

        settings.addDouble(CFG_ZOOM_X, m_zoomX);
        settings.addDouble(CFG_ZOOM_Y, m_zoomY);
        settings.addDouble(CFG_ZOOM_K, m_zoomK);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadFromNodeSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
        m_chartTitle = settings.getString(HeatMapViewConfig.CFG_CHART_TITLE);
        m_chartSubtitle = settings.getString(HeatMapViewConfig.CFG_CHART_SUBTITLE);
        m_showToolTips = settings.getBoolean(HeatMapViewConfig.CFG_SHOW_TOOL_TIPS);

        m_continuousGradient = settings.getBoolean(HeatMapViewConfig.CFG_CONTINUOUS_GRADIENT);

        m_selection = settings.getStringArray(CFG_SELECTION);
        m_publishSelection = settings.getBoolean(HeatMapViewConfig.CFG_PUBLISH_SELECTION);
        m_subscribeSelection = settings.getBoolean(HeatMapViewConfig.CFG_SUBSCRIBE_SELECTION);
        m_subscribeFilter = settings.getBoolean(HeatMapViewConfig.CFG_SUBSCRIBE_FILTER);
        m_showSelectedRowsOnly = settings.getBoolean(HeatMapViewConfig.CFG_SHOW_SELECTED_ROWS_ONLY);

        m_initialPageSize = settings.getInt(HeatMapViewConfig.CFG_INITIAL_PAGE_SIZE);
        m_currentPage = settings.getInt(CFG_CURRENT_PAGE);

        m_zoomX = settings.getDouble(CFG_ZOOM_X);
        m_zoomY = settings.getDouble(CFG_ZOOM_Y);
        m_zoomK = settings.getDouble(CFG_ZOOM_K);
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
        final HeatMapViewValue other = (HeatMapViewValue) obj;
        return new EqualsBuilder()
                .append(m_chartTitle, other.getChartTitle())
                .append(m_chartSubtitle, other.getChartSubtitle())
                .append(m_showToolTips, other.getShowToolTips())
                .append(m_continuousGradient, other.getContinuousGradient())
                .append(m_selection, other.getSelection())
                .append(m_showSelectedRowsOnly, other.getShowSelectedRowsOnly())
                .append(m_publishSelection, other.getPublishSelection())
                .append(m_subscribeSelection, other.getSubscribeSelection())
                .append(m_subscribeFilter, other.getSubscribeFilter())
                .append(m_initialPageSize, other.getInitialPageSize())
                .append(m_currentPage, other.getCurrentPage())
                .append(m_zoomX, other.getZoomX())
                .append(m_zoomY, other.getZoomY())
                .append(m_zoomK, other.getZoomK())
                .isEquals();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(m_chartTitle)
                .append(m_chartSubtitle)
                .append(m_showToolTips)
                .append(m_continuousGradient)
                .append(m_selection)
                .append(m_publishSelection)
                .append(m_subscribeSelection)
                .append(m_subscribeFilter)
                .append(m_showSelectedRowsOnly)
                .append(m_initialPageSize)
                .append(m_currentPage)
                .append(m_zoomX)
                .append(m_zoomY)
                .append(m_zoomK)
                .toHashCode();
    }
}
