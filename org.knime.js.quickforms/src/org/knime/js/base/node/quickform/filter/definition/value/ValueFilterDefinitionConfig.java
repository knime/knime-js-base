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
 *   14 Apr 2018 (albrecht): created
 */
package org.knime.js.base.node.quickform.filter.definition.value;

import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.StringValue;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.js.base.dialog.selection.multiple.MultipleSelectionsComponentFactory;

/**
 *
 * @author Christian Albrecht, KNIME GmbH, Konstanz, Germany
 */
public class ValueFilterDefinitionConfig {

    private static final String CFG_HIDE_WIZARD = "hideInWizard";
    private static final boolean DEFAULT_HIDE_WIZARD = false;
    private boolean m_hideInWizard = DEFAULT_HIDE_WIZARD;

    private static final String CFG_CUSTOM_CSS = "customCSS";
    private static final String DEFAULT_CUSTOM_CSS = "";
    private String m_customCSS = DEFAULT_CUSTOM_CSS;

    private static final String CFG_MERGE_WITH_EXISTING_FILTERS_TABLE = "mergeWithExistingFiltersTable";
    private static final boolean DEFAULT_MERGE_WITH_EXISTING_FILTERS_TABLE = true;
    private boolean m_mergeWithExistingFiltersTable = DEFAULT_MERGE_WITH_EXISTING_FILTERS_TABLE;

    private static final String CFG_MERGE_WITH_EXISTING_FILTERS_MODEL = "mergeWithExistingFiltersModel";
    private static final boolean DEFAULT_MERGE_WITH_EXISTING_FILTERS_MODEL = true;
    private boolean m_mergeWithExistingFiltersModel = DEFAULT_MERGE_WITH_EXISTING_FILTERS_MODEL;

    private static final String CFG_USE_LABEL = "useLabel";
    private static final boolean DEFAULT_USE_LABEL = false;
    private boolean m_useLabel;

    static final String CFG_LABEL = "label";
    private String m_label;

    private static final String CFG_CUSTOM_LABEL = "customLabel";
    private static final boolean DEFAULT_CUSTOM_LABEL = false;
    private boolean m_customLabel;

    static final String CFG_USE_MULTIPLE = "useMultiple";
    private static final boolean DEFAULT_USE_MULTIPLE = true;
    private boolean m_useMultiple = DEFAULT_USE_MULTIPLE;

    static final String CFG_TYPE = "type";
    private static final String DEFAULT_TYPE = MultipleSelectionsComponentFactory.TWINLIST;
    private String m_type = DEFAULT_TYPE;

    static final String CFG_LIMIT_NUMBER_VIS_OPTIONS = "limit_number_visible_options";
    private static final boolean DEFAULT_LIMIT_NUMBER_VIS_OPTIONS = false;
    private boolean m_limitNumberVisOptions = DEFAULT_LIMIT_NUMBER_VIS_OPTIONS;

    static final String CFG_NUMBER_VIS_OPTIONS = "number_visible_options";
    private static final Integer DEFAULT_NUMBER_VIS_OPTIONS = 10;
    private Integer m_numberVisOptions = DEFAULT_NUMBER_VIS_OPTIONS;

    static final String CFG_COLUMN = "column";
    private static final String DEFAULT_COLUMN = "";
    private String m_column = DEFAULT_COLUMN;

    private static final String CFG_FILTER_VALUES = "filterValues";
    private ValueFilterPanelConfiguration m_filterValues = new ValueFilterPanelConfiguration(CFG_FILTER_VALUES);

    /**
     * @return the hideInWizard
     */
    public boolean isHideInWizard() {
        return m_hideInWizard;
    }

    /**
     * @param hideInWizard the hideInWizard to set
     */
    public void setHideInWizard(final boolean hideInWizard) {
        m_hideInWizard = hideInWizard;
    }

    /**
     * @return the customCSS
     */
    public String getCustomCSS() {
        return m_customCSS;
    }

    /**
     * @param customCSS the customCSS to set
     */
    public void setCustomCSS(final String customCSS) {
        m_customCSS = customCSS;
    }

    /**
     * @return the mergeWithExistingFiltersTable
     */
    public boolean isMergeWithExistingFiltersTable() {
        return m_mergeWithExistingFiltersTable;
    }

    /**
     * @param mergeWithExistingFiltersTable the mergeWithExistingFiltersTable to set
     */
    public void setMergeWithExistingFiltersTable(final boolean mergeWithExistingFiltersTable) {
        m_mergeWithExistingFiltersTable = mergeWithExistingFiltersTable;
    }

    /**
     * @return the mergeWithExistingFiltersModel
     */
    public boolean isMergeWithExistingFiltersModel() {
        return m_mergeWithExistingFiltersModel;
    }

    /**
     * @param mergeWithExistingFiltersModel the mergeWithExistingFiltersModel to set
     */
    public void setMergeWithExistingFiltersModel(final boolean mergeWithExistingFiltersModel) {
        m_mergeWithExistingFiltersModel = mergeWithExistingFiltersModel;
    }

    /**
     * @return the useLabel
     */
    public boolean isUseLabel() {
        return m_useLabel;
    }

    /**
     * @param useLabel the useLabel to set
     */
    public void setUseLabel(final boolean useLabel) {
        m_useLabel = useLabel;
    }

    /**
     * @return the label
     */
    public String getLabel() {
        return m_label;
    }

    /**
     * @param label the label to set
     */
    public void setLabel(final String label) {
        m_label = label;
    }

    /**
     * @return the customLabel
     */
    public boolean isCustomLabel() {
        return m_customLabel;
    }

    /**
     * @param customLabel the customLabel to set
     */
    public void setCustomLabel(final boolean customLabel) {
        m_customLabel = customLabel;
    }

    /**
     * @return the column
     */
    public String getColumn() {
        return m_column;
    }

    /**
     * @param column the column to set
     */
    public void setColumn(final String column) {
        m_column = column;
    }

    /**
     * @return the filterValues
     */
    public ValueFilterPanelConfiguration getFilterValues() {
        return m_filterValues;
    }

    /**
     * @return the useMultiple
     */
    public boolean isUseMultiple() {
        return m_useMultiple;
    }

    /**
     * @param useMultiple the useMultiple to set
     */
    public void setUseMultiple(final boolean useMultiple) {
        m_useMultiple = useMultiple;
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
     * Save the current config to a given settings object
     * @param settings the settings to save to
     */
    public void saveSettings(final NodeSettingsWO settings) {
        settings.addBoolean(CFG_HIDE_WIZARD, m_hideInWizard);
        settings.addString(CFG_CUSTOM_CSS, m_customCSS);
        settings.addBoolean(CFG_MERGE_WITH_EXISTING_FILTERS_TABLE, m_mergeWithExistingFiltersTable);
        settings.addBoolean(CFG_MERGE_WITH_EXISTING_FILTERS_MODEL, m_mergeWithExistingFiltersModel);
        settings.addBoolean(CFG_USE_LABEL, m_useLabel);
        settings.addString(CFG_LABEL, m_label);
        settings.addBoolean(CFG_CUSTOM_LABEL, m_customLabel);

        settings.addString(CFG_COLUMN, m_column);
        m_filterValues.saveConfiguration(settings);
        settings.addBoolean(CFG_USE_MULTIPLE, m_useMultiple);
        settings.addString(CFG_TYPE, m_type);
        settings.addBoolean(CFG_LIMIT_NUMBER_VIS_OPTIONS, m_limitNumberVisOptions);
        settings.addInt(CFG_NUMBER_VIS_OPTIONS, m_numberVisOptions);
    }

    /**
     * Load setting from a given settings object
     * @param settings the settings to load from
     * @throws InvalidSettingsException on load error
     */
    public void loadSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
        m_hideInWizard = settings.getBoolean(CFG_HIDE_WIZARD);
        m_customCSS = settings.getString(CFG_CUSTOM_CSS);
        m_mergeWithExistingFiltersTable = settings.getBoolean(CFG_MERGE_WITH_EXISTING_FILTERS_TABLE);
        m_mergeWithExistingFiltersModel = settings.getBoolean(CFG_MERGE_WITH_EXISTING_FILTERS_MODEL);
        m_useLabel = settings.getBoolean(CFG_USE_LABEL);
        m_label = settings.getString(CFG_LABEL);
        m_customLabel = settings.getBoolean(CFG_CUSTOM_LABEL);

        m_column = settings.getString(CFG_COLUMN);
        m_filterValues.loadConfigurationInModel(settings);
        m_useMultiple = settings.getBoolean(CFG_USE_MULTIPLE);
        m_type = settings.getString(CFG_TYPE);
        m_limitNumberVisOptions = settings.getBoolean(CFG_LIMIT_NUMBER_VIS_OPTIONS);
        m_numberVisOptions = settings.getInt(CFG_NUMBER_VIS_OPTIONS);

    }

    /**
     * Load settings for a dialog (assuming defaults) from a given settings object
     * @param settings the settings to load from
     * @param spec the spec to load data from
     */
    public void loadSettingsInDialog(final NodeSettingsRO settings, final DataTableSpec spec) {
        m_hideInWizard = settings.getBoolean(CFG_HIDE_WIZARD, DEFAULT_HIDE_WIZARD);
        m_customCSS = settings.getString(CFG_CUSTOM_CSS, DEFAULT_CUSTOM_CSS);
        m_mergeWithExistingFiltersTable =
            settings.getBoolean(CFG_MERGE_WITH_EXISTING_FILTERS_TABLE, DEFAULT_MERGE_WITH_EXISTING_FILTERS_TABLE);
        m_mergeWithExistingFiltersModel =
            settings.getBoolean(CFG_MERGE_WITH_EXISTING_FILTERS_MODEL, DEFAULT_MERGE_WITH_EXISTING_FILTERS_MODEL);
        m_useLabel = settings.getBoolean(CFG_USE_LABEL, DEFAULT_USE_LABEL);
        m_label = settings.getString(CFG_LABEL, null);
        m_customLabel = settings.getBoolean(CFG_CUSTOM_LABEL, DEFAULT_CUSTOM_LABEL);

        m_column = settings.getString(CFG_COLUMN, DEFAULT_COLUMN);
        String[] possibleValues = new String[0];
        DataColumnSpec colSpec = spec.getColumnSpec(m_column);
        if (colSpec != null && colSpec.getType().isCompatible(StringValue.class)) {
            possibleValues = colSpec.getDomain().getValues().stream()
                    .map(cell -> ((StringValue)cell).getStringValue()).toArray(String[]::new);
        }
        m_filterValues.loadConfigInDialog(settings, possibleValues);
        m_useMultiple = settings.getBoolean(CFG_USE_MULTIPLE, DEFAULT_USE_MULTIPLE);
        m_type = settings.getString(CFG_TYPE, DEFAULT_TYPE);
        m_limitNumberVisOptions = settings.getBoolean(CFG_LIMIT_NUMBER_VIS_OPTIONS, DEFAULT_LIMIT_NUMBER_VIS_OPTIONS);
        m_numberVisOptions = settings.getInt(CFG_NUMBER_VIS_OPTIONS, DEFAULT_NUMBER_VIS_OPTIONS);
    }

}
