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

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.js.core.JSONViewContent;
import org.knime.js.core.settings.numberFormat.NumberFormatSettings;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * View representation for the decision tree view.
 *
 * @author Adrian Nembach, KNIME.com
 */
@JsonAutoDetect
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public class DecisionTreeViewRepresentation extends JSONViewContent {


    private JSDecisionTree m_tree;
    private NumberFormatSettings m_numberFormat;

    private boolean m_enableViewConfiguration;
    private boolean m_enableTitleChange;
    private boolean m_enableSubtitleChange;
    private boolean m_enableSelection;
    private String m_backgroundColor;
    private String m_dataAreaColor;
    private String m_nodeBackgroundColor;
    private boolean m_displayFullscreenButton;
    private boolean m_displaySelectionResetButton;
    private String m_tableId;
    private boolean m_enableZooming;
    private int m_truncationLimit;


    /**
     * {@inheritDoc}
     */
    @Override
    public void saveToNodeSettings(final NodeSettingsWO settings) {
        settings.addBoolean(DecisionTreeViewConfig.ENABLE_CONFIG, m_enableViewConfiguration);
        settings.addBoolean(DecisionTreeViewConfig.ENABLE_TTILE_CHANGE, m_enableTitleChange);
        settings.addBoolean(DecisionTreeViewConfig.ENABLE_SUBTTILE_CHANGE, m_enableSubtitleChange);
        settings.addBoolean(DecisionTreeViewConfig.ENABLE_SELECTION, m_enableSelection);
        settings.addString(DecisionTreeViewConfig.BACKGROUND_COLOR, m_backgroundColor);
        settings.addString(DecisionTreeViewConfig.DATA_AREA_COLOR, m_dataAreaColor);
        settings.addBoolean(DecisionTreeViewConfig.DISPLAY_FULLSCREEN_BUTTON, m_displayFullscreenButton);
        settings.addString("TableId", m_tableId);
        m_numberFormat.saveToNodeSettings(settings);
        settings.addBoolean(DecisionTreeViewConfig.ENABLE_ZOOMING, m_enableZooming);
        settings.addString(DecisionTreeViewConfig.NODE_BACKGROUND_COLOR, m_nodeBackgroundColor);
        settings.addBoolean(DecisionTreeViewConfig.DISPLAY_SELECTION_RESET_BUTTON, m_displaySelectionResetButton);
        settings.addInt(DecisionTreeViewConfig.TRUNCATION_LIMIT, m_truncationLimit);
        // don't save decision tree representation (for now)
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadFromNodeSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
        setEnableSubtitleChange(settings.getBoolean(DecisionTreeViewConfig.ENABLE_SUBTTILE_CHANGE));
        setEnableViewConfiguration(settings.getBoolean(DecisionTreeViewConfig.ENABLE_CONFIG));
        setEnableTitleChange(settings.getBoolean(DecisionTreeViewConfig.ENABLE_TTILE_CHANGE));
        setEnableSelection(settings.getBoolean(DecisionTreeViewConfig.ENABLE_SELECTION));
        setBackgroundColor(settings.getString(DecisionTreeViewConfig.BACKGROUND_COLOR));
        setDataAreaColor(settings.getString(DecisionTreeViewConfig.DATA_AREA_COLOR));
        setNodeBackgroundColor(settings.getString(DecisionTreeViewConfig.NODE_BACKGROUND_COLOR));
        NumberFormatSettings numberFormat = new NumberFormatSettings();
        numberFormat.loadFromNodeSettings(settings);
        setNumberFormat(numberFormat);
        setDisplayFullscreenButton(settings.getBoolean(DecisionTreeViewConfig.DISPLAY_FULLSCREEN_BUTTON));
        setTableId(settings.getString("TableId"));
        setEnableZooming(settings.getBoolean(DecisionTreeViewConfig.ENABLE_ZOOMING));
        setDisplaySelectionResetButton(settings.getBoolean(DecisionTreeViewConfig.DISPLAY_SELECTION_RESET_BUTTON));

        //added with 3.3.2
        setTruncationLimit(settings.getInt(DecisionTreeViewConfig.TRUNCATION_LIMIT, DecisionTreeViewConfig.DEFAULT_TRUNCATION_LIMIT));

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
        DecisionTreeViewRepresentation other = (DecisionTreeViewRepresentation)obj;
        return new EqualsBuilder()
                .append(m_enableViewConfiguration, other.m_enableViewConfiguration)
                .append(m_enableTitleChange, other.m_enableTitleChange)
                .append(m_enableSubtitleChange, other.m_enableSubtitleChange)
                .append(m_enableSelection, other.m_enableSelection)
                .append(m_tree, other.m_tree)
                .append(m_dataAreaColor, other.m_dataAreaColor)
                .append(m_backgroundColor, other.m_backgroundColor)
                .append(m_nodeBackgroundColor, other.m_nodeBackgroundColor)
                .append(m_numberFormat, other.m_numberFormat)
                .append(m_displayFullscreenButton, other.m_displayFullscreenButton)
                .append(m_tableId, other.m_tableId)
                .append(m_displaySelectionResetButton, other.m_displaySelectionResetButton)
                .append(m_truncationLimit, other.m_truncationLimit)
                .isEquals();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(m_enableViewConfiguration)
            .append(m_enableTitleChange)
            .append(m_enableSubtitleChange)
            .append(m_enableSelection)
            .append(m_tree)
            .append(m_dataAreaColor)
            .append(m_backgroundColor)
            .append(m_nodeBackgroundColor)
            .append(m_numberFormat)
            .append(m_displayFullscreenButton)
            .append(m_tableId)
            .append(m_displaySelectionResetButton)
            .append(m_truncationLimit)
            .toHashCode();
    }

    /**
     * @return the enableViewConfiguration
     */
    public boolean getEnableViewConfiguration() {
        return m_enableViewConfiguration;
    }

    /**
     * @param enableViewConfiguration the enableViewConfiguration to set
     */
    public void setEnableViewConfiguration(final boolean enableViewConfiguration) {
        m_enableViewConfiguration = enableViewConfiguration;
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
     * @return the tree
     */
    public JSDecisionTree getTree() {
        return m_tree;
    }

    /**
     * @param tree the tree to set
     */
    public void setTree(final JSDecisionTree tree) {
        m_tree = tree;
    }

    /**
     * @return the backgroundColor
     */
    public String getBackgroundColor() {
        return m_backgroundColor;
    }

    /**
     * @param backgroundColor the backgroundColor to set
     */
    public void setBackgroundColor(final String backgroundColor) {
        m_backgroundColor = backgroundColor;
    }

    /**
     * @return the dataAreaColor
     */
    public String getDataAreaColor() {
        return m_dataAreaColor;
    }

    /**
     * @param dataAreaColor the dataAreaColor to set
     */
    public void setDataAreaColor(final String dataAreaColor) {
        m_dataAreaColor = dataAreaColor;
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
     * @return the tableId
     */
    public String getTableId() {
        return m_tableId;
    }

    /**
     * @param tableId the tableId to set
     */
    public void setTableId(final String tableId) {
        m_tableId = tableId;
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
     * @return the nodeBackgroundColor
     */
    public String getNodeBackgroundColor() {
        return m_nodeBackgroundColor;
    }

    /**
     * @param nodeBackgroundColor the nodeBackgroundColor to set
     */
    public void setNodeBackgroundColor(final String nodeBackgroundColor) {
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

}
