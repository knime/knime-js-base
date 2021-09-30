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
 *   1 Jun 2019 (albrecht): created
 */
package org.knime.js.base.node.base.selection.singleMultiple;

import java.util.Arrays;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;

/**
 * Base abstract config file for the single and multiple selection configuration and widget nodes
 *
 * @author Christian Albrecht, KNIME GmbH, Konstanz, Germany
 */
public abstract class SingleMultipleSelectionNodeConfig {

    private static final String CFG_POSSIBLE_CHOICES = "possible_choices";
    private static final String[] DEFAULT_POSSIBLE_CHOICES = new String[0];
    private String[] m_possibleChoices = DEFAULT_POSSIBLE_CHOICES;

    private static final String CFG_TYPE = "type";

    private static final String CFG_LIMIT_NUMBER_VIS_OPTIONS = "limit_number_visible_options";
    private static final boolean DEFAULT_LIMIT_NUMBER_VIS_OPTIONS = false;
    private boolean m_limitNumberVisOptions = DEFAULT_LIMIT_NUMBER_VIS_OPTIONS;

    private static final String CFG_NUMBER_VIS_OPTIONS = "number_visible_options";
    private static final Integer DEFAULT_NUMBER_VIS_OPTIONS = 10;
    private Integer m_numberVisOptions = DEFAULT_NUMBER_VIS_OPTIONS;

    private static final String CFG_RE_EXECUTE_DOWNSTREAM_NODES = "re_execute_downstream_nodes";
    private static final Boolean DEFAULT_RE_EXECUTE_DOWNSTREAM_NODES = false;
    private Boolean m_reExecuteDownstreamNodes = DEFAULT_RE_EXECUTE_DOWNSTREAM_NODES;

    /**
     * @return the possibleChoices
     */
    public String[] getPossibleChoices() {
        return m_possibleChoices;
    }

    /**
     * @param possibleChoices the possibleChoices to set
     */
    public void setPossibleChoices(final String[] possibleChoices) {
        m_possibleChoices = possibleChoices;
    }

    /**
     * @return the type
     */
    public abstract String getType();

    /**
     * @param type the type to set
     */
    public abstract void setType(final String type);

    /**
     * @return the default selection type string
     */
    public abstract String getDefaultType();

    /**
     * @return the limitNumberVisOptions
     */
    public boolean getLimitNumberVisOptions() {
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
     * @return the reExecuteDownstreamNodes
     */
    public Boolean getReExecuteDownstreamNodes() {
        return m_reExecuteDownstreamNodes;
    }

    /**
     * @param reExecuteDownstreamNodes the reExecuteDownstreamNodes to set
     */
    public void setReExecuteDownstreamNodes(final Boolean reExecuteDownstreamNodes) {
        m_reExecuteDownstreamNodes = reExecuteDownstreamNodes;
    }

    /**
     * Saves the current settings
     *
     * @param settings the settings to save to
     */
    public void saveSettings(final NodeSettingsWO settings) {
        settings.addStringArray(CFG_POSSIBLE_CHOICES, m_possibleChoices);
        settings.addString(CFG_TYPE, getType());
        settings.addBoolean(CFG_LIMIT_NUMBER_VIS_OPTIONS, m_limitNumberVisOptions);
        settings.addInt(CFG_NUMBER_VIS_OPTIONS, m_numberVisOptions);
        settings.addBoolean(CFG_RE_EXECUTE_DOWNSTREAM_NODES, m_reExecuteDownstreamNodes);
    }

    /**
     * Loads the config from saved settings
     *
     * @param settings the settings to load from
     * @throws InvalidSettingsException
     */
    public void loadSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
        m_possibleChoices = settings.getStringArray(CFG_POSSIBLE_CHOICES);
        setType(settings.getString(CFG_TYPE));
        m_limitNumberVisOptions = settings.getBoolean(CFG_LIMIT_NUMBER_VIS_OPTIONS);
        m_numberVisOptions = settings.getInt(CFG_NUMBER_VIS_OPTIONS);
        m_reExecuteDownstreamNodes = settings.getBoolean(CFG_RE_EXECUTE_DOWNSTREAM_NODES, DEFAULT_RE_EXECUTE_DOWNSTREAM_NODES);
    }

    /**
     * Loads the config from saved settings for dialog display
     *
     * @param settings the settings to load from
     */
    public void loadSettingsInDialog(final NodeSettingsRO settings) {
        m_possibleChoices = settings.getStringArray(CFG_POSSIBLE_CHOICES, DEFAULT_POSSIBLE_CHOICES);
        setType(settings.getString(CFG_TYPE, getDefaultType()));
        m_limitNumberVisOptions = settings.getBoolean(CFG_LIMIT_NUMBER_VIS_OPTIONS, DEFAULT_LIMIT_NUMBER_VIS_OPTIONS);
        m_numberVisOptions = settings.getInt(CFG_NUMBER_VIS_OPTIONS, DEFAULT_NUMBER_VIS_OPTIONS);
        m_reExecuteDownstreamNodes = settings.getBoolean(CFG_RE_EXECUTE_DOWNSTREAM_NODES, DEFAULT_RE_EXECUTE_DOWNSTREAM_NODES);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("possibleChoices=");
        sb.append(Arrays.toString(m_possibleChoices));
        sb.append(", ");
        sb.append("type=");
        sb.append(getType());
        sb.append(", ");
        sb.append("m_limitNumberVisOptions=");
        sb.append(m_limitNumberVisOptions);
        sb.append(", ");
        sb.append("m_numberVisOptions=");
        sb.append(m_numberVisOptions);
        sb.append(", ");
        sb.append("m_reExecuteDownstreamNodes=");
        sb.append(m_reExecuteDownstreamNodes);
        return sb.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(m_possibleChoices)
                .append(getType())
                .append(m_limitNumberVisOptions)
                .append(m_numberVisOptions)
                .append(m_reExecuteDownstreamNodes)
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
        SingleMultipleSelectionNodeConfig other = (SingleMultipleSelectionNodeConfig)obj;
        return new EqualsBuilder()
                .append(m_possibleChoices, other.m_possibleChoices)
                .append(getType(), other.getType())
                .append(m_limitNumberVisOptions, other.m_limitNumberVisOptions)
                .append(m_numberVisOptions, other.m_numberVisOptions)
                .append(m_reExecuteDownstreamNodes, other.m_reExecuteDownstreamNodes)
                .isEquals();
    }

}
