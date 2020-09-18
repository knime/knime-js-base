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
 *   Sep 18, 2020 (Christian Albrecht, KNIME GmbH, Konstanz, Germany): created
 */
package org.knime.js.base.node.widget.filter.definition.value;

import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.util.filter.NameFilterConfiguration;

/**
 * Configuration for a generic filter that can includes and excludes possible string values and takes care on
 * additional/missing names using the enforce inclusion/exclusion option. It also supports filtering based on name
 * patterns.
 *
 * Subclass to enable certain methods to be called from the containing package. No additional functionality.
 *
 * @author Christian Albrecht, KNIME GmbH, Konstanz, Germany
 */
public class ValueFilterWidgetPanelConfiguration extends NameFilterConfiguration {

    /**
     * Creates a new name filter configuration with the given settings name. Also enables the name pattern filter.
     *
     * @param configRootName the config name to used to store the settings
     * @throws IllegalArgumentException If config name is null or empty
     */
    public ValueFilterWidgetPanelConfiguration(final String configRootName) {
        super(configRootName);
    }

    /**
     * Load config in dialog, init defaults if necessary.
     *
     * @param settings to load from.
     * @param names all names available for filtering
     */
    protected void loadConfigInDialog(final NodeSettingsRO settings, final String[] names) {
        super.loadConfigurationInDialog(settings, names);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected FilterResult applyTo(final String[] names) {
        return super.applyTo(names);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setIncludeList(final String[] includeList) {
        super.setIncludeList(includeList);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setExcludeList(final String[] excludeList) {
        super.setExcludeList(excludeList);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setEnforceOption(final EnforceOption enforceOption) {
        super.setEnforceOption(enforceOption);
    }
}
