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
 *   Aug 22, 2018 (awalter): created
 */
package org.knime.js.base.node.viz.tileView;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.js.core.JSONDataTable;
import org.knime.js.core.node.table.AbstractTableRepresentation;
import org.knime.js.core.settings.table.TableRepresentationSettings;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

/**
 * @author Alison Walter, KNIME GmbH, Konstanz, Germany
 */
@JsonAutoDetect
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public class TileViewRepresentation extends AbstractTableRepresentation {

    private boolean m_useNumCols;
    private boolean m_useColWidth;
    private int m_numCols;
    private int m_colWidth;
    private String m_labelCol;
    private boolean m_useRowID;

    private boolean m_alignLeft;
    private boolean m_alignRight;
    private boolean m_alignCenter;

    private TableRepresentationSettings m_settings = new TableRepresentationSettings();

    /** Serialization constructor. Don't use. */
    public TileViewRepresentation() { }

    /**
     * @return the useNumCols
     */
    public boolean getUseNumCols() {
        return m_useNumCols;
    }

    /**
     * @param useNumCols the useNumCols to set
     */
    public void setUseNumCols(final boolean useNumCols) {
        m_useNumCols = useNumCols;
    }

    /**
     * @return the useColWidth
     */
    public boolean getUseColWidth() {
        return m_useColWidth;
    }

    /**
     * @param useColWidth the useColWidth to set
     */
    public void setUseColWidth(final boolean useColWidth) {
        m_useColWidth = useColWidth;
    }

    /**
     * @return the numCols
     */
    public int getNumCols() {
        return m_numCols;
    }

    /**
     * @param numCols the numCols to set
     */
    public void setNumCols(final int numCols) {
        m_numCols = numCols;
    }

    /**
     * @return the colWidth
     */
    public int getColWidth() {
        return m_colWidth;
    }

    /**
     * @param colWidth the colWidth to set
     */
    public void setColWidth(final int colWidth) {
        m_colWidth = colWidth;
    }

    /**
     * @return the labelCol
     */
    public String getLabelCol() {
        return m_labelCol;
    }

    /**
     * @param labelCol the labelCol to set
     */
    public void setLabelCol(final String labelCol) {
        m_labelCol = labelCol;
    }

    /**
     * @return the useRowID
     */
    public boolean getUseRowID() {
        return m_useRowID;
    }

    /**
     * @param useRowID the useRowID to set
     */
    public void setUseRowID(final boolean useRowID) {
        m_useRowID = useRowID;
    }

    /**
     * @return the alignLeft
     */
    public boolean getAlignLeft() {
        return m_alignLeft;
    }

    /**
     * @param alignLeft the alignLeft to set
     */
    public void setAlignLeft(final boolean alignLeft) {
        m_alignLeft = alignLeft;
    }

    /**
     * @return the alignRight
     */
    public boolean getAlignRight() {
        return m_alignRight;
    }

    /**
     * @param alignRight the alignRight to set
     */
    public void setAlignRight(final boolean alignRight) {
        m_alignRight = alignRight;
    }

    /**
     * @return the alignCenter
     */
    public boolean getAlignCenter() {
        return m_alignCenter;
    }

    /**
     * @param alignCenter the alignCenter to set
     */
    public void setAlignCenter(final boolean alignCenter) {
        m_alignCenter = alignCenter;
    }

    /**
     * @return the settings
     */
    @Override
    @JsonUnwrapped
    public TableRepresentationSettings getSettings() {
        return m_settings;
    }

    /**
     * @param settings the settings to set
     */
    @Override
    public void setSettings(final TableRepresentationSettings settings) {
        m_settings = settings;
    }

    /**
     * Copy settings from dialog keeping the existing table data
     * @param settings the settings to set
     */
    @Override
    public void setSettingsFromDialog(final TableRepresentationSettings settings) {
        final JSONDataTable table = m_settings.getTable();
        m_settings = settings;
        m_settings.setTable(table);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @JsonIgnore
    public void saveToNodeSettings(final NodeSettingsWO settings) {
        m_settings.saveSettings(settings);
        settings.addBoolean(TileViewConfig.CFG_USE_NUM_COLS, m_useNumCols);
        settings.addBoolean(TileViewConfig.CFG_USE_COL_WIDTH, m_useColWidth);
        settings.addInt(TileViewConfig.CFG_NUM_COLS, m_numCols);
        settings.addInt(TileViewConfig.CFG_COL_WIDTH, m_colWidth);
        settings.addString(TileViewConfig.CFG_LABEL_COL, m_labelCol);
        settings.addBoolean(TileViewConfig.CFG_USE_ROW_ID, m_useRowID);
        settings.addBoolean(TileViewConfig.CFG_ALIGN_LEFT, m_alignLeft);
        settings.addBoolean(TileViewConfig.CFG_ALIGN_RIGHT, m_alignRight);
        settings.addBoolean(TileViewConfig.CFG_ALIGN_CENTER, m_alignCenter);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @JsonIgnore
    public void loadFromNodeSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
        m_settings.loadSettings(settings);
        m_useNumCols = settings.getBoolean(TileViewConfig.CFG_USE_NUM_COLS);
        m_useColWidth = settings.getBoolean(TileViewConfig.CFG_USE_COL_WIDTH);
        m_numCols = settings.getInt(TileViewConfig.CFG_NUM_COLS);
        m_colWidth = settings.getInt(TileViewConfig.CFG_COL_WIDTH);
        m_labelCol = settings.getString(TileViewConfig.CFG_LABEL_COL);
        m_useRowID = settings.getBoolean(TileViewConfig.CFG_USE_ROW_ID);
        m_alignLeft = settings.getBoolean(TileViewConfig.CFG_ALIGN_LEFT);
        m_alignRight = settings.getBoolean(TileViewConfig.CFG_ALIGN_RIGHT);
        m_alignCenter = settings.getBoolean(TileViewConfig.CFG_ALIGN_CENTER);
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
        final TileViewRepresentation other = (TileViewRepresentation)obj;
        return new EqualsBuilder()
                .append(m_settings, other.m_settings)
                .append(m_useNumCols, other.getUseNumCols())
                .append(m_useColWidth, other.getUseColWidth())
                .append(m_numCols, other.getNumCols())
                .append(m_colWidth, other.getColWidth())
                .append(m_labelCol, other.getLabelCol())
                .append(m_useRowID, other.getUseRowID())
                .append(m_alignLeft, other.getAlignLeft())
                .append(m_alignRight, other.getAlignRight())
                .append(m_alignCenter, other.getAlignCenter())
                .isEquals();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(m_settings)
                .append(m_useNumCols)
                .append(m_useColWidth)
                .append(m_numCols)
                .append(m_colWidth)
                .append(m_labelCol)
                .append(m_useRowID)
                .append(m_alignLeft)
                .append(m_alignRight)
                .append(m_alignCenter)
                .toHashCode();
    }

}
