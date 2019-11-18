/*
 * ------------------------------------------------------------------------
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
 * Created on Oct 10, 2013 by Patrick Winter, KNIME AG, Zurich, Switzerland
 */
package org.knime.js.base.node.base.validation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataValue;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.util.filter.NameFilterConfiguration.FilterResult;

/**
 * Configuration to the TypeFilterDialog.
 *
 * @author Adrian Nembach, KNIME GmbH, Konstanz, Germany
 */
final class TypeFilterConfig {

    private LinkedHashMap<String, Boolean> m_selections = new LinkedHashMap<>();

    /**
     * Creates a configuration to the DataValue filter panel.
     *
     */
    public TypeFilterConfig() {
    }

    /**
     * @param toCopy config to copy
     *
     */
    public TypeFilterConfig(final TypeFilterConfig toCopy) {
        m_selections = new LinkedHashMap<>(toCopy.m_selections);
    }

    /**
     * Loads the configuration from the given settings object. Fails if not valid.
     *
     * @param settings Settings object containing the configuration.
     * @throws InvalidSettingsException If settings are invalid
     */
    public void loadConfigurationInModel(final NodeSettingsRO settings) throws InvalidSettingsException {
        m_selections.clear();
        for (String key : settings.keySet()) {
            m_selections.put(key, settings.getBoolean(key, false));
        }
    }

    /**
     * Loads the configuration from the given settings object. Sets defaults if invalid.
     *
     * @param settings Settings object containing the configuration.
     */
    public void loadConfigurationInDialog(final NodeSettingsRO settings) {
        m_selections.clear();
        for (String key : settings.keySet()) {
            m_selections.put(key, settings.getBoolean(key, false));
        }
    }

    /**
     * Save the current configuration inside the given settings object.
     *
     * @param settings Settings object the current configuration will be put into.
     */
    public void saveConfiguration(final NodeSettingsWO settings) {
        for (Map.Entry<String, Boolean> entry : m_selections.entrySet()) {
            settings.addBoolean(entry.getKey(), entry.getValue());
        }
    }

    void loadDefaults(final List<Class<? extends DataValue>> valueClasses) {
        for (Class<? extends DataValue> valueClass : valueClasses) {
            if (!m_selections.containsKey(valueClass.getName())) {
                m_selections.put(valueClass.getName(), false);
            }
        }
    }

    /**
     * Applies this configuration to the column.
     *
     * @param columns The columns whose types to check
     * @return The filter result
     */
    public FilterResult applyTo(final Iterable<DataColumnSpec> columns) {
        List<String> includes = new ArrayList<>();
        List<String> excludes = new ArrayList<>();
        for (DataColumnSpec column : columns) {
            final Class<? extends DataValue> preferredValueClass = column.getType().getPreferredValueClass();
            String key = preferredValueClass.getName();
            if (m_selections.containsKey(key) && m_selections.get(key)) {
                includes.add(column.getName());
            } else {
                excludes.add(column.getName());
            }
        }
        return new FilterResult(includes, excludes, Collections.<String> emptyList(), Collections.<String> emptyList());
    }

    /**
     * @return the selections
     */
    Map<String, Boolean> getSelections() {
        return m_selections;
    }

    /**
     * @param selections the selections to set
     */
    void setSelections(final LinkedHashMap<String, Boolean> selections) {
        m_selections = selections;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        TypeFilterConfig other = (TypeFilterConfig)obj;
        return m_selections.equals(other.m_selections);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + m_selections.hashCode();
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (Entry<String, Boolean> entry : m_selections.entrySet()) {
            if (entry.getValue().booleanValue()) {
                builder.append(", " + entry.getKey());
            }
        }
        return m_selections.entrySet().stream().filter(e -> e.getValue().booleanValue()).map(Entry::getKey)
            .collect(Collectors.joining(", ", "Selected types: ", ""));
    }

}
