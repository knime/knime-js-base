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

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.js.core.JSONViewContent;

/**
 *
 * @author Adrian Nembach, KNIME.com
 */
public class DecisionTreeViewValue extends JSONViewContent {

    static final String SELECTED_KEYS = "selectedKeys";
    static final String NODE_STATUS = "nodeStatus";

    private String m_Title;
    private String m_Subtitle;
    private int[] m_nodeStatus;
    private String[] m_selection;
    private boolean m_publishSelection;
    private boolean m_subscribeSelection;
    private double m_scale = 1.0;

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveToNodeSettings(final NodeSettingsWO settings) {
        settings.addString(DecisionTreeViewConfig.TITLE, m_Title);
        settings.addString(DecisionTreeViewConfig.SUBTITLE, m_Subtitle);
        settings.addIntArray("nodeStatus", m_nodeStatus);
        settings.addStringArray(SELECTED_KEYS, m_selection);
        settings.addBoolean(DecisionTreeViewConfig.PUBLISH_SELECTION, m_publishSelection);
        settings.addBoolean(DecisionTreeViewConfig.SUBSCRIBE_SELECTION, m_subscribeSelection);
        // since 3.3.2
        settings.addDouble(DecisionTreeViewConfig.SCALE, m_scale);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadFromNodeSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
        setTitle(settings.getString(DecisionTreeViewConfig.TITLE));
        setSubtitle(settings.getString(DecisionTreeViewConfig.SUBTITLE));
        setNodeStatus(settings.getIntArray(NODE_STATUS));
        setSelection(settings.getStringArray(SELECTED_KEYS));
        setPublishSelection(settings.getBoolean(DecisionTreeViewConfig.PUBLISH_SELECTION));
        setSubscribeSelection(settings.getBoolean(DecisionTreeViewConfig.SUBSCRIBE_SELECTION));
        // since 3.3.2
        setScale(settings.getDouble(DecisionTreeViewConfig.SCALE, 1.0));
    }

    /**
     * @return the zoom
     */
    public double getScale() {
        return m_scale;
    }

    /**
     * @param zoom the zoom to set
     */
    public void setScale(final double zoom) {
        m_scale = zoom;
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
        DecisionTreeViewValue other = (DecisionTreeViewValue)obj;
        return new EqualsBuilder()
                .append(m_Title, other.m_Title)
                .append(m_Subtitle, other.m_Subtitle)
                .append(m_nodeStatus, other.m_nodeStatus)
                .append(m_selection, other.m_selection)
                .append(m_publishSelection, other.m_publishSelection)
                .append(m_subscribeSelection, other.m_subscribeSelection)
                .append(m_scale, other.m_scale)
                .isEquals();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(m_Title)
                .append(m_Subtitle)
                .append(m_nodeStatus)
                .append(m_selection)
                .append(m_publishSelection)
                .append(m_subscribeSelection)
                .append(m_scale)
                .toHashCode();
    }

    /**
     * @return the chartTitle
     */
    public String getTitle() {
        return m_Title;
    }

    /**
     * @param chartTitle the chartTitle to set
     */
    public void setTitle(final String chartTitle) {
        m_Title = chartTitle;
    }

    /**
     * @return the chartSubtitle
     */
    public String getSubtitle() {
        return m_Subtitle;
    }

    /**
     * @param chartSubtitle the chartSubtitle to set
     */
    public void setSubtitle(final String chartSubtitle) {
        m_Subtitle = chartSubtitle;
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

}
