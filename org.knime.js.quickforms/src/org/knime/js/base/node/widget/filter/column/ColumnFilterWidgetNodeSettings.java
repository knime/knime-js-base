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
 *   AI Migration
 */
package org.knime.js.base.node.widget.filter.column;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.js.base.node.widget.util.WidgetNodeSettingsBase;
import org.knime.node.parameters.NodeParameters;
import org.knime.node.parameters.Widget;
import org.knime.node.parameters.layout.After;
import org.knime.node.parameters.layout.Layout;
import org.knime.node.parameters.layout.Section;
import org.knime.node.parameters.migration.LoadDefaultsForAbsentFields;
import org.knime.node.parameters.persistence.NodeParametersPersistor;
import org.knime.node.parameters.persistence.Persist;
import org.knime.node.parameters.persistence.Persistor;
import org.knime.node.parameters.persistence.legacy.LegacyColumnFilterPersistor;
import org.knime.node.parameters.widget.choices.filter.ColumnFilter;
import org.knime.node.parameters.widget.choices.filter.ColumnFilterWidget;
import org.knime.node.parameters.widget.choices.util.AllColumnsProvider;
import org.knime.node.parameters.widget.number.NumberInputWidget;
import org.knime.node.parameters.widget.text.TextAreaWidget;
import org.knime.node.parameters.widget.text.TextInputWidget;

/**
 * Settings for the Column Filter Widget node.
 *
 * @author AI Migration
 */
@SuppressWarnings("restriction")
@LoadDefaultsForAbsentFields
public final class ColumnFilterWidgetNodeSettings implements NodeParameters {

    @Section(title = "Column Filter Configuration")
    @After(WidgetNodeSettingsBase.CommonSections.ValidationSection.class)
    interface FilterSection {
    }

    @Widget(title = "Label", description = "A descriptive label that will be shown in the dialog")
    @Layout(WidgetNodeSettingsBase.CommonSections.LabelSection.class)
    @TextInputWidget
    @Persist(configKey = "label")
    String m_label = "Column Filter";

    @Widget(title = "Description", description = "Some lines of description that will be shown for instance in the node description of the component exposing a dialog")
    @Layout(WidgetNodeSettingsBase.CommonSections.LabelSection.class)
    @TextAreaWidget
    @Persist(configKey = "description")
    String m_description = "Enter Description";

    @Widget(title = "Parameter Name", description = "Parameter identifier for external parameterization (e.g. batch execution).")
    @Layout(WidgetNodeSettingsBase.CommonSections.ParameterSection.class)
    @TextInputWidget
    @Persist(configKey = "flowVariableName")
    String m_parameter = "column-filter";

    @Widget(title = "Hide in Dialog", description = "If the widget is hidden, it cannot be shown in the dialog, and becomes unreachable.")
    @Layout(WidgetNodeSettingsBase.CommonSections.ParameterSection.class)
    @Persist(configKey = "hideInWizard")
    boolean m_hideInWizard = false;

    @Widget(title = "Custom CSS", description = "Enter custom CSS styling for this widget")
    @Layout(WidgetNodeSettingsBase.CommonSections.InputSection.class)
    @TextAreaWidget
    @Persist(configKey = "customCSS")
    String m_customCSS = "";

    // Column filter specific settings
    @Widget(title = "Default Column Filter", description = "Configure the default column filter settings")
    @Layout(FilterSection.class)
    @ColumnFilterWidget(choicesProvider = AllColumnsProvider.class)
    @Persistor(ColumnFilterPersistor.class)
    ColumnFilter m_columnFilter = new ColumnFilter();

    @Widget(title = "Enable Search", description = "Enable search functionality for the column filter")
    @Layout(FilterSection.class)
    @Persist(configKey = "enableSearch")
    boolean m_enableSearch = false;

    @Widget(title = "Limit number of visible options", description = "If enabled, limit the number of visible options")
    @Layout(FilterSection.class)
    @Persist(configKey = "limitNumberVisOptions")
    boolean m_limitNumberVisOptions = false;

    @Widget(title = "Number of visible options", description = "Number of visible options for the filter")
    @Layout(FilterSection.class)
    @NumberInputWidget
    @Persist(configKey = "numberVisOptions")
    int m_numberVisOptions = 5;

    /**
     * Custom persistor for Column Filter Widget that handles the widget-specific save format.
     * The widget saves column filter configuration differently from regular nodes.
     */
    static class ColumnFilterPersistor implements NodeParametersPersistor<ColumnFilter> {

        @Override
        public ColumnFilter load(final NodeSettingsRO settings) throws InvalidSettingsException {
            try {
                // Try to load from defaultValue (widget format)
                if (settings.containsKey("defaultValue")) {
                    NodeSettingsRO defaultSettings = settings.getNodeSettings("defaultValue");
                    
                    // The widget saves both "columns" array and "columnFilter" settings
                    if (defaultSettings.containsKey("columnFilter")) {
                        // Load from the widget's columnFilter settings
                        NodeSettingsRO columnFilterSettings = defaultSettings.getNodeSettings("columnFilter");
                        return LegacyColumnFilterPersistor.load(columnFilterSettings, "");
                    } else if (defaultSettings.containsKey("columns")) {
                        // This is widget value format with columns array - create filter from columns
                        String[] columns = defaultSettings.getStringArray("columns");
                        ColumnFilter filter = new ColumnFilter();
                        // Convert columns array to ColumnFilter - include these columns
                        if (columns != null && columns.length > 0) {
                            filter = new ColumnFilter(columns);
                        }
                        return filter;
                    } else {
                        // This might be a direct column filter configuration
                        return LegacyColumnFilterPersistor.load(defaultSettings, "");
                    }
                }
                
                // Try to load directly from settings (non-widget format)
                return LegacyColumnFilterPersistor.load(settings, "");
            } catch (Exception e) {
                // If loading fails, return empty filter
            }

            // Return empty filter if loading failed
            return new ColumnFilter();
        }

        @Override
        public void save(final ColumnFilter columnFilter, final NodeSettingsWO settings) {
            NodeSettingsWO defaultSettings = settings.addNodeSettings("defaultValue");
            if (columnFilter != null) {
                // Save both the columns array and the columnFilter configuration in widget format
                
                // 1. Save the columns array (the selected columns)
                String[] selectedColumns = columnFilter.m_manualFilter != null ? 
                    columnFilter.m_manualFilter.m_manuallySelected : new String[0];
                defaultSettings.addStringArray("columns", selectedColumns != null ? selectedColumns : new String[0]);
                
                // 2. Save the columnFilter configuration
                NodeSettingsWO columnFilterSettings = defaultSettings.addNodeSettings("columnFilter");
                LegacyColumnFilterPersistor.save(columnFilter, columnFilterSettings, "");
            } else {
                // Save empty values in the widget format
                defaultSettings.addStringArray("columns", new String[0]);
                NodeSettingsWO columnFilterSettings = defaultSettings.addNodeSettings("columnFilter");
                LegacyColumnFilterPersistor.save(new ColumnFilter(), columnFilterSettings, "");
            }
        }

        @Override
        public String[][] getConfigPaths() {
            return new String[][] {{"defaultValue"}, {"defaultValue", "columns"}, {"defaultValue", "columnFilter"}};
        }
    }
}
