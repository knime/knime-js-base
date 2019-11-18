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
 *   Nov 14, 2019 (Adrian Nembach, KNIME GmbH, Konstanz, Germany): created
 */
package org.knime.js.base.node.base.validation;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.LinkedList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.knime.core.data.BoundedValue;
import org.knime.core.data.DataColumnDomain;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.DataType;
import org.knime.core.data.NominalValue;
import org.knime.core.data.container.ColumnRearranger;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.util.filter.NameFilterConfiguration.FilterResult;

/**
 * Filters a {@link DataTableSpec} by type and/or availability of domain information.
 *
 * @author Adrian Nembach, KNIME GmbH, Konstanz, Germany
 */
public final class InputSpecFilter {

    private final Config m_config;

    private InputSpecFilter(final Config config) {
        m_config = config;
    }

    /**
     * Filters a {@link DataTableSpec} according to the configuration provided in the constructor.
     *
     * @param spec the {@link DataTableSpec} to filter
     * @return the filtered {@link DataTableSpec}
     */
    public DataTableSpec filter(final DataTableSpec spec) {
        if (m_config.m_allowAllTypes && !m_config.m_filterColumnsWithoutDomain) {
            return spec;
        }
        final ColumnRearranger cr = createRearranger(spec);
        return cr.createSpec();
    }

    private static String[] columnsWithoutDomain(final DataTableSpec spec) {
        return spec.stream().filter(InputSpecFilter::hasNoDomain).map(DataColumnSpec::getName).toArray(String[]::new);
    }

    private static boolean hasNoDomain(final DataColumnSpec spec) {
        final DataColumnDomain domain = spec.getDomain();
        final DataType type = spec.getType();
        if (type.isCompatible(NominalValue.class)) {
            return !domain.hasValues();
        }
        if (type.isCompatible(BoundedValue.class)) {
            return !domain.hasBounds();
        }
        return false;
    }

    private ColumnRearranger createRearranger(final DataTableSpec spec) {
        final ColumnRearranger cr = new ColumnRearranger(spec);
        if (!m_config.m_allowAllTypes) {
            final FilterResult fr = m_config.m_typeFilterConfig.applyTo(spec);
            cr.keepOnly(fr.getIncludes());
        }
        if (m_config.m_filterColumnsWithoutDomain) {
            cr.remove(columnsWithoutDomain(cr.createSpec()));
        }
        return cr;
    }

    /**
     * Filters a {@link BufferedDataTable} according to the configuration provided in the constructor.
     *
     * @param table the {@link BufferedDataTable} to filter
     * @param exec the {@link ExecutionContext} used for filtering
     * @return a filtered {@link BufferedDataTable}
     * @throws CanceledExecutionException if the user cancels the execution during filtering
     */
    public BufferedDataTable filter(final BufferedDataTable table, final ExecutionContext exec)
        throws CanceledExecutionException {
        if (m_config.m_allowAllTypes) {
            return table;
        }
        return exec.createColumnRearrangeTable(table, createRearranger(table.getDataTableSpec()), exec);
    }

    /**
     * The configuration for an {@link InputSpecFilter}.
     *
     * @author Adrian Nembach, KNIME GmbH, Konstanz, Germany
     */
    public static class Config {

        private static final String CFG_ALLOW_ALL_TYPES = "allow_all_types";

        private static final String CFG_TYPE_FILTER = "type_filter";

        private static final String CFG_FILTER_COLUMNS_WITHOUT_DOMAIN = "filter_columns_without_domain";

        private static final boolean DEFAULT_ALLOW_ALL_TYPES = true;

        private static final boolean DEFAULT_FILTER_COLUMNS_WITHOUT_DOMAIN = false;

        private final TypeFilterConfig m_typeFilterConfig;

        private boolean m_allowAllTypes = DEFAULT_ALLOW_ALL_TYPES;

        private boolean m_filterColumnsWithoutDomain = DEFAULT_FILTER_COLUMNS_WITHOUT_DOMAIN;

        /**
         * Creates a {@link Config} that allows all types and does not filter column without domain information.
         */
        public Config() {
            m_typeFilterConfig = new TypeFilterConfig();
        }

        private Config(final Config toCopy) {
            m_typeFilterConfig = new TypeFilterConfig(toCopy.m_typeFilterConfig);
            m_allowAllTypes = toCopy.m_allowAllTypes;
            m_filterColumnsWithoutDomain = toCopy.m_filterColumnsWithoutDomain;
        }

        /**
         * @return a {@link InputSpecFilter} filter that filters according to the current configuration
         */
        public InputSpecFilter createFilter() {
            return new InputSpecFilter(new Config(this));
        }

        /**
         * Loads the configuration in the model.
         *
         * @param settings the {@link NodeSettingsRO} to load from
         * @throws InvalidSettingsException if the settings are invalid
         */
        public void loadSettingsInModel(final NodeSettingsRO settings) throws InvalidSettingsException {
            if (settings.containsKey(CFG_TYPE_FILTER)) {
                m_typeFilterConfig.loadConfigurationInModel(settings.getNodeSettings(CFG_TYPE_FILTER));
            }
            m_allowAllTypes = settings.getBoolean(CFG_ALLOW_ALL_TYPES, DEFAULT_ALLOW_ALL_TYPES);
            m_filterColumnsWithoutDomain =
                settings.getBoolean(CFG_FILTER_COLUMNS_WITHOUT_DOMAIN, DEFAULT_FILTER_COLUMNS_WITHOUT_DOMAIN);
        }

        /**
         * Loads the configuration in the dialog.
         *
         * @param settings the {@link NodeSettingsRO} to load from
         */
        public void loadSettingsInDialog(final NodeSettingsRO settings) {
            if (settings.containsKey(CFG_TYPE_FILTER)) {
                NodeSettingsRO typeFilterSettings;
                try {
                    typeFilterSettings = settings.getNodeSettings(CFG_TYPE_FILTER);
                    m_typeFilterConfig.loadConfigurationInDialog(typeFilterSettings);
                } catch (InvalidSettingsException e) {
                    throw new IllegalStateException(String.format(
                        "Settings contained the key %s but didn't associate it with the expected subsettings.",
                        CFG_TYPE_FILTER));
                }
            }
            m_allowAllTypes = settings.getBoolean(CFG_ALLOW_ALL_TYPES, DEFAULT_ALLOW_ALL_TYPES);
            m_filterColumnsWithoutDomain =
                settings.getBoolean(CFG_FILTER_COLUMNS_WITHOUT_DOMAIN, DEFAULT_FILTER_COLUMNS_WITHOUT_DOMAIN);
        }

        /**
         * Saves the configuration into a {@link NodeSettingsWO settings}.
         *
         * @param settings the {@link NodeSettingsWO} to save to
         */
        public void saveSettings(final NodeSettingsWO settings) {
            m_typeFilterConfig.saveConfiguration(settings.addNodeSettings(CFG_TYPE_FILTER));
            settings.addBoolean(CFG_ALLOW_ALL_TYPES, m_allowAllTypes);
            settings.addBoolean(CFG_FILTER_COLUMNS_WITHOUT_DOMAIN, m_filterColumnsWithoutDomain);
        }
    }

    /**
     * The dialog for an {@link InputSpecFilter}.
     *
     * @author Adrian Nembach, KNIME GmbH, Konstanz, Germany
     */
    public static class Dialog {

        private final TypeFilterDialog m_typeFilterDialog = new TypeFilterDialog();

        private final JCheckBox m_allowAllTypes = new JCheckBox("Allow all types");

        private final JCheckBox m_filterColumnsWithoutDomain = new JCheckBox("Filter columns without domain");

        private final List<ChangeListener> m_listeners = new LinkedList<>();

        /**
         * Creates a new dialog instance.
         */
        public Dialog() {
            m_allowAllTypes.addChangeListener(e -> reactToAllowAllTypesChange());
            m_typeFilterDialog.addChangeListener(e -> fireChangeEvent());
            m_filterColumnsWithoutDomain.addChangeListener(e -> fireChangeEvent());
        }

        /**
         * @return the panel containing all components for the InputSpecFilter
         */
        public JPanel getPanel() {
            final JPanel panel = new JPanel(new GridBagLayout());
            panel.setBorder(BorderFactory.createTitledBorder(""));
            final GridBagConstraints gbc = new GridBagConstraints();
            gbc.anchor = GridBagConstraints.LINE_START;
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.gridx = 0;
            gbc.gridy = 0;
            panel.add(new JLabel("Allowed types:"), gbc);
            gbc.gridx = 1;
            panel.add(m_allowAllTypes, gbc);
            gbc.gridx = 2;
            panel.add(m_filterColumnsWithoutDomain, gbc);
            gbc.gridx = 0;
            gbc.gridy++;
            gbc.gridwidth = 3;
            gbc.fill = GridBagConstraints.BOTH;
            gbc.weightx = 1;
            gbc.weighty = 1;
            panel.add(m_typeFilterDialog.getPanel(), gbc);
            return panel;
        }

        /**
         * Loads the values from the config into the dialog.
         *
         * @param config the config to load from
         * @param spec the {@link DataTableSpec} of the input table
         */
        public void loadFromConfig(final Config config, final DataTableSpec spec) {
            m_allowAllTypes.setSelected(config.m_allowAllTypes);
            m_typeFilterDialog.loadConfiguration(config.m_typeFilterConfig, spec);
            m_filterColumnsWithoutDomain.setSelected(config.m_filterColumnsWithoutDomain);
            reactToAllowAllTypesChange();
        }

        /**
         * Saves the values from the dialog into config.
         *
         * @param config to save to
         */
        public void saveToConfig(final Config config) {
            config.m_allowAllTypes = m_allowAllTypes.isSelected();
            config.m_filterColumnsWithoutDomain = m_filterColumnsWithoutDomain.isSelected();
            m_typeFilterDialog.saveConfiguration(config.m_typeFilterConfig);
        }

        private void reactToAllowAllTypesChange() {
            m_typeFilterDialog.setEnabled(!m_allowAllTypes.isSelected());
            fireChangeEvent();
        }

        private void fireChangeEvent() {
            for (ChangeListener listener : m_listeners) {
                listener.stateChanged(new ChangeEvent(this));
            }
        }

        /**
         * Allows to add listeners that are notified whenever the filtering changes.
         *
         * @param listener to add
         */
        public void addListener(final ChangeListener listener) {
            m_listeners.add(listener);
        }

        /**
         * Allows to remove previously added listeners.
         *
         * @param listener to remove
         */
        public void removeListener(final ChangeListener listener) {
            m_listeners.remove(listener);
        }
    }
}
