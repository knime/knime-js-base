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

import org.knime.core.data.DataTableSpec;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.js.core.node.table.TableConfig;
import org.knime.js.core.settings.table.TableSettings;

/**
 * @author Alison Walter, KNIME GmbH, Konstanz, Germany
 */
public class TileViewConfig implements TableConfig {

    static final String CFG_USE_NUM_COLS = "useNumCols";
    private static final boolean DEFAULT_USE_NUM_COLS = true;
    private boolean m_useNumCols = DEFAULT_USE_NUM_COLS;

    static final String CFG_USE_COL_WIDTH = "useColWidth";
    private static final boolean DEFAULT_USE_COL_WIDTH = false;
    private boolean m_useColWidth = DEFAULT_USE_COL_WIDTH;

    static final String CFG_NUM_COLS = "numCols";
    static final int MIN_NUM_COLS = 1;
    static final int MAX_NUM_COLS = 100;
    static final int DEFAULT_NUM_COLS = 1;
    private int m_numCols = DEFAULT_NUM_COLS;

    static final String CFG_COL_WIDTH = "colWidth";
    static final int MIN_COL_WIDTH = 30;
    static final int MAX_COL_WIDTH = 5000;
    static final int DEFAULT_COL_WIDTH = 180;
    private int m_colWidth = DEFAULT_COL_WIDTH;

    static final String CFG_LABEL_COL = "labelCol";
    private static final String DEFAULT_LABEL_COL = null;
    private String m_labelCol = DEFAULT_LABEL_COL;

    static final String CFG_USE_ROW_ID = "useRowID";
    private static final boolean DEFAULT_USE_ROW_ID = true;
    private boolean m_useRowID = DEFAULT_USE_ROW_ID;

    static final String CFG_ALIGN_LEFT = "alignLeft";
    private static final boolean DEFAULT_ALIGN_LEFT = true;
    private boolean m_alignLeft = DEFAULT_ALIGN_LEFT;

    static final String CFG_ALIGN_RIGHT = "alignRight";
    private static final boolean DEFAULT_ALIGN_RIGHT = false;
    private boolean m_alignRight = DEFAULT_ALIGN_RIGHT;

    static final String CFG_ALIGN_CENTER = "alignCenter";
    private static final boolean DEFAULT_ALIGN_CENTER = false;
    private boolean m_alignCenter = DEFAULT_ALIGN_CENTER;

    private TableSettings m_settings = new TableSettings();

    @SuppressWarnings("javadoc")
    public TileViewConfig() {
        super();
        m_settings.setSelectionColumnName("Selected (Tile View)");
    }

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
     * {@inheritDoc}
     */
    @Override
    public TableSettings getSettings() {
        return m_settings;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setSettings(final TableSettings settings) {
        m_settings = settings;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveSettings(final NodeSettingsWO settings) {
        m_settings.saveSettings(settings);
        settings.addBoolean(CFG_USE_NUM_COLS, m_useNumCols);
        settings.addBoolean(CFG_USE_COL_WIDTH, m_useColWidth);
        settings.addInt(CFG_NUM_COLS, m_numCols);
        settings.addInt(CFG_COL_WIDTH, m_colWidth);
        settings.addString(CFG_LABEL_COL, m_labelCol);
        settings.addBoolean(CFG_USE_ROW_ID, m_useRowID);
        settings.addBoolean(CFG_ALIGN_LEFT, m_alignLeft);
        settings.addBoolean(CFG_ALIGN_RIGHT, m_alignRight);
        settings.addBoolean(CFG_ALIGN_CENTER, m_alignCenter);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
        final int numCols = settings.getInt(CFG_NUM_COLS);
        final int colWidth = settings.getInt(CFG_COL_WIDTH);
        final int initPageSize = settings.getInt("initialPageSize");
        final int maxRows = settings.getInt("maxRows");
        final int decimalPlaces = settings.getInt("globalNumberFormatDecimals");
        final boolean enableNumCols = settings.getBoolean(CFG_USE_NUM_COLS);
        final boolean enableColWidth = settings.getBoolean(CFG_USE_COL_WIDTH);
        final boolean enablePaging = settings.getBoolean("enablePaging");
        final boolean enableDecimalPlaces = settings.getBoolean("enableGlobalNumberFormat");
        validateConfig(numCols, colWidth, initPageSize, maxRows, decimalPlaces, enableNumCols, enableColWidth,
            enablePaging, enableDecimalPlaces);

        m_settings.loadSettings(settings);
        m_useNumCols = settings.getBoolean(CFG_USE_NUM_COLS);
        m_useColWidth = settings.getBoolean(CFG_USE_COL_WIDTH);
        m_labelCol = settings.getString(CFG_LABEL_COL);
        m_useRowID = settings.getBoolean(CFG_USE_ROW_ID);
        m_alignLeft = settings.getBoolean(CFG_ALIGN_LEFT);
        m_alignRight = settings.getBoolean(CFG_ALIGN_RIGHT);
        m_alignCenter = settings.getBoolean(CFG_ALIGN_CENTER);
        m_numCols = numCols;
        m_colWidth = colWidth;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadSettingsForDialog(final NodeSettingsRO settings, final DataTableSpec spec) {
        m_settings.loadSettingsForDialog(settings, spec);
        m_useNumCols = settings.getBoolean(CFG_USE_NUM_COLS, DEFAULT_USE_NUM_COLS);
        m_useColWidth = settings.getBoolean(CFG_USE_COL_WIDTH, DEFAULT_USE_COL_WIDTH);
        m_numCols = settings.getInt(CFG_NUM_COLS, DEFAULT_NUM_COLS);
        m_colWidth = settings.getInt(CFG_COL_WIDTH, DEFAULT_COL_WIDTH);
        m_labelCol = settings.getString(CFG_LABEL_COL, DEFAULT_LABEL_COL);
        m_useRowID = settings.getBoolean(CFG_USE_ROW_ID, DEFAULT_USE_ROW_ID);
        m_alignLeft = settings.getBoolean(CFG_ALIGN_LEFT, DEFAULT_ALIGN_LEFT);
        m_alignRight = settings.getBoolean(CFG_ALIGN_RIGHT, DEFAULT_ALIGN_RIGHT);
        m_alignCenter = settings.getBoolean(CFG_ALIGN_CENTER, DEFAULT_ALIGN_CENTER);
    }

    static void validateConfig(final int numCols, final int colWidth, final int initPageSize, final int maxRows,
        final int decimalPlaces, final boolean enableNumCols, final boolean enableColWidth, final boolean enablePaging,
        final boolean enableDecimalPlaces) throws InvalidSettingsException {
        String errorMsg = "";
        if (maxRows < 0) {
            errorMsg += "No. of rows to display (" + maxRows + ") cannot be negative.\n";
        }
        if ((numCols < MIN_NUM_COLS || numCols > MAX_NUM_COLS) && enableNumCols) {
            if (!errorMsg.isEmpty()) {
                errorMsg+="\n";
            }
            errorMsg += "Invalid number of tiles per row, expected an integer between " + MIN_NUM_COLS + " and "
                + MAX_NUM_COLS + " but received " + numCols + ".\n";
        }
        if ((colWidth < MIN_COL_WIDTH || colWidth > MAX_COL_WIDTH) && enableColWidth) {
            if (!errorMsg.isEmpty()) {
                errorMsg+="\n";
            }
            errorMsg += "Invalid tile width, expected an integer between 3" + MIN_COL_WIDTH + " and " + MAX_COL_WIDTH
                + " but received " + colWidth + ".\n";
        }
        if (initPageSize < 1 && enablePaging) {
            if (!errorMsg.isEmpty()) {
                errorMsg+="\n";
            }
            errorMsg += "Initial page size (" + initPageSize + ") cannot be less than 1.\n";
        }
        if (numCols > initPageSize && enablePaging && enableNumCols) {
            if (!errorMsg.isEmpty()) {
                errorMsg+="\n";
            }
            errorMsg += "The number of tiles per row (" + numCols + ") cannot be greater than the initial page size ("
                + initPageSize + "). Check the \"Options\" and \"Interactivity\" tabs.\n";
        }
        if (decimalPlaces < 0 && enableDecimalPlaces) {
            if (!errorMsg.isEmpty()) {
                errorMsg+="\n";
            }
            errorMsg += "Decimal places (" + decimalPlaces + ") cannot be negative.\n";
        }
        if (!errorMsg.isEmpty()) {
            throw new InvalidSettingsException(errorMsg);
        }
    }

}
