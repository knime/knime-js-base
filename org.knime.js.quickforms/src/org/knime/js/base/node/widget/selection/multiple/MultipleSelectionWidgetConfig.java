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
package org.knime.js.base.node.widget.selection.multiple;

import static org.knime.js.base.node.parameters.filterandselection.EnableSearchAndIgnoreInvalidValuesParameters.CFG_IGNORE_INVALID_VALUES;
import static org.knime.js.base.node.parameters.filterandselection.EnableSearchAndIgnoreInvalidValuesParameters.DEFAULT_IGNORE_INVALID_VALUES;
import static org.knime.js.base.node.parameters.filterandselection.EnableSearchParameter.CFG_ENABLE_SEARCH;
import static org.knime.js.base.node.parameters.filterandselection.EnableSearchParameter.DEFAULT_ENABLE_SEARCH;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.js.base.node.base.selection.singleMultiple.MultipleSelectionNodeConfig;
import org.knime.js.base.node.base.selection.singleMultiple.SingleMultipleSelectionNodeValue;
import org.knime.js.base.node.widget.ReExecutableWidgetConfig;

/**
 * The config for the multiple selection widget node
 *
 * @author Christian Albrecht, KNIME GmbH, Konstanz, Germany
 */
public class MultipleSelectionWidgetConfig extends ReExecutableWidgetConfig<SingleMultipleSelectionNodeValue> {

    private final MultipleSelectionNodeConfig m_config;

    private boolean m_enableSearch = DEFAULT_ENABLE_SEARCH;

    private boolean m_ignoreInvalidValues = DEFAULT_IGNORE_INVALID_VALUES;

    /**
     * Instantiate a new config object
     */
    public MultipleSelectionWidgetConfig() {
        m_config = new MultipleSelectionNodeConfig();
    }

    /**
     * @return the config
     */
    public MultipleSelectionNodeConfig getSelectionConfig() {
        return m_config;
    }

    /**
     * @return the enableSearch
     */
    public boolean isEnableSearch() {
        return m_enableSearch;
    }

    /**
     * @param enableSearch the enableSearch to set
     */
    public void setEnableSearch(final boolean enableSearch) {
        m_enableSearch = enableSearch;
    }

    /**
     * @return the ignoreInvalidvalues
     */
    public boolean isIgnoreInvalidValues() {
        return m_ignoreInvalidValues;
    }

    /**
     * @param ignoreInvalidValues the ignoreInvalidValues to set
     */
    public void setIgnoreInvalidValues(final boolean ignoreInvalidValues) {
        m_ignoreInvalidValues = ignoreInvalidValues;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected SingleMultipleSelectionNodeValue createEmptyValue() {
        return new SingleMultipleSelectionNodeValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveSettings(final NodeSettingsWO settings) {
        super.saveSettings(settings);
        m_config.saveSettings(settings);
        settings.addBoolean(CFG_ENABLE_SEARCH, m_enableSearch);
        settings.addBoolean(CFG_IGNORE_INVALID_VALUES, m_ignoreInvalidValues);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
        super.loadSettings(settings);
        m_config.loadSettings(settings);
        // added with 5.3
        m_enableSearch = settings.getBoolean(CFG_ENABLE_SEARCH, DEFAULT_ENABLE_SEARCH);
        // added with 5.3.2
        m_ignoreInvalidValues = settings.getBoolean(CFG_IGNORE_INVALID_VALUES, DEFAULT_IGNORE_INVALID_VALUES);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadSettingsInDialog(final NodeSettingsRO settings) {
        super.loadSettingsInDialog(settings);
        m_config.loadSettingsInDialog(settings);
        m_enableSearch = settings.getBoolean(CFG_ENABLE_SEARCH, DEFAULT_ENABLE_SEARCH);
        m_ignoreInvalidValues = settings.getBoolean(CFG_IGNORE_INVALID_VALUES, DEFAULT_IGNORE_INVALID_VALUES);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString());
        sb.append(", ");
        sb.append(m_config.toString());
        sb.append(", enable search = ");
        sb.append(m_enableSearch);
        sb.append(", ignore_missing_values = ");
        sb.append(m_ignoreInvalidValues);
        return sb.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder() //
            .appendSuper(super.hashCode()) //
            .append(m_config) //
            .append(m_enableSearch) //
            .append(m_ignoreInvalidValues) //
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
        MultipleSelectionWidgetConfig other = (MultipleSelectionWidgetConfig)obj;
        return new EqualsBuilder()
                .appendSuper(super.equals(obj))
                .append(m_config, other.m_config)
                .append(m_enableSearch, other.m_enableSearch)
                .append(m_ignoreInvalidValues, other.m_ignoreInvalidValues)
                .isEquals();

    }

}
