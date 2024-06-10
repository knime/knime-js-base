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
 *   29 May 2019 (albrecht): created
 */
package org.knime.js.base.node.base.selection.column;

import java.util.Arrays;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.js.base.dialog.selection.single.SingleSelectionComponentFactory;

/**
 * Base config file for the column selection configuration and widget nodes
 *
 * @author Christian Albrecht, KNIME GmbH, Konstanz, Germany
 */
public class ColumnSelectionNodeConfig {

    private static final String CFG_POSSIBLE_COLUMNS = "possibleColumns";
    private static final String[] DEFAULT_POSSIBLE_COLUMNS = new String[0];
    private String[] m_possibleColumns = DEFAULT_POSSIBLE_COLUMNS;

    private static final String CFG_TYPE = "type";
    private static final String DEFAULT_TYPE = SingleSelectionComponentFactory.DROPDOWN;
    private String m_type = DEFAULT_TYPE;

    private static final String CFG_LIMIT_NUMBER_VIS_OPTIONS = "limit_number_visible_options";
    private static final boolean DEFAULT_LIMIT_NUMBER_VIS_OPTIONS = false;
    private boolean m_limitNumberVisOptions = DEFAULT_LIMIT_NUMBER_VIS_OPTIONS;

    private static final String CFG_NUMBER_VIS_OPTIONS = "number_visible_options";
    private static final Integer DEFAULT_NUMBER_VIS_OPTIONS = 5;
    private Integer m_numberVisOptions = DEFAULT_NUMBER_VIS_OPTIONS;

    /**
     * @return the possibleColumns
     */
    public String[] getPossibleColumns() {
        return m_possibleColumns;
    }

    /**
     * @param possibleColumns the possibleColumns to set
     */
    public void setPossibleColumns(final String[] possibleColumns) {
        m_possibleColumns = possibleColumns;
    }

    /**
     * @return the type
     */
    public String getType() {
        return m_type;
    }

    /**
     * @param type the type to set
     */
    public void setType(final String type) {
        m_type = type;
    }

    /**
     * @return the limitNumberVisOptions
     */
    public boolean isLimitNumberVisOptions() {
        return m_limitNumberVisOptions;
    }

    /**
     * @param limitNumberVisOptions the limitNumberVisOptions to set
     */
    public void setLimitNumberVisOptions(final boolean limitNumberVisOptions) {
        m_limitNumberVisOptions = limitNumberVisOptions;
    }

    /**
     * @return the numberVisOptions
     */
    public Integer getNumberVisOptions() {
        return m_numberVisOptions;
    }

    /**
     * @param numberVisOptions the numberVisOptions to set
     */
    public void setNumberVisOptions(final Integer numberVisOptions) {
        m_numberVisOptions = numberVisOptions;
    }

    /**
     * Saves the current settings
     *
     * @param settings the settings to save to
     */
    public void saveSettings(final NodeSettingsWO settings) {
        settings.addStringArray(CFG_POSSIBLE_COLUMNS, m_possibleColumns);
        settings.addString(CFG_TYPE, m_type);
        settings.addBoolean(CFG_LIMIT_NUMBER_VIS_OPTIONS, m_limitNumberVisOptions);
        settings.addInt(CFG_NUMBER_VIS_OPTIONS, m_numberVisOptions);
    }

    /**
     * Loads the config from saved settings
     *
     * @param settings the settings to load from
     * @throws InvalidSettingsException
     */
    public void loadSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
        m_possibleColumns = settings.getStringArray(CFG_POSSIBLE_COLUMNS);
        m_type = settings.getString(CFG_TYPE);
        m_limitNumberVisOptions = settings.getBoolean(CFG_LIMIT_NUMBER_VIS_OPTIONS);
        m_numberVisOptions = settings.getInt(CFG_NUMBER_VIS_OPTIONS);
    }

    /**
     * Loads the config from saved settings for dialog display
     *
     * @param settings the settings to load from
     */
    public void loadSettingsInDialog(final NodeSettingsRO settings) {
        m_possibleColumns = settings.getStringArray(CFG_POSSIBLE_COLUMNS, DEFAULT_POSSIBLE_COLUMNS);
        m_type = settings.getString(CFG_TYPE, DEFAULT_TYPE);
        m_limitNumberVisOptions = settings.getBoolean(CFG_LIMIT_NUMBER_VIS_OPTIONS, DEFAULT_LIMIT_NUMBER_VIS_OPTIONS);
        m_numberVisOptions = settings.getInt(CFG_NUMBER_VIS_OPTIONS, DEFAULT_NUMBER_VIS_OPTIONS);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("possibleColumns=");
        sb.append(Arrays.toString(m_possibleColumns));
        sb.append(", ");
        sb.append("type=");
        sb.append(m_type);
        sb.append(", ");
        sb.append("m_limitNumberVisOptions=");
        sb.append(m_limitNumberVisOptions);
        sb.append(", ");
        sb.append("m_numberVisOptions=");
        sb.append(m_numberVisOptions);
        return sb.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(m_possibleColumns)
                .append(m_type)
                .append(m_limitNumberVisOptions)
                .append(m_numberVisOptions)
                .toHashCode();
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
        ColumnSelectionNodeConfig other = (ColumnSelectionNodeConfig)obj;
        return new EqualsBuilder()
                .append(m_possibleColumns, other.m_possibleColumns)
                .append(m_type, other.m_type)
                .append(m_limitNumberVisOptions, other.m_limitNumberVisOptions)
                .append(m_numberVisOptions, other.m_numberVisOptions)
                .isEquals();
    }

}
