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
package org.knime.js.base.node.configuration.filter.value;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.knime.core.node.dialog.DialogNodePanel;
import org.knime.core.node.dialog.SubNodeDescriptionProvider;
import org.knime.core.node.util.filter.NameFilterConfiguration.EnforceOption;
import org.knime.core.webui.node.dialog.PersistSchema;
import org.knime.core.webui.node.dialog.WebDialogNodeRepresentation;
import org.knime.core.webui.node.dialog.defaultdialog.jsonforms.JsonFormsDataUtil;
import org.knime.core.webui.node.dialog.defaultdialog.jsonforms.renderers.DialogElementRendererSpec;
import org.knime.core.webui.node.dialog.defaultdialog.setting.filter.util.ManualFilter;
import org.knime.core.webui.node.dialog.defaultdialog.widget.choices.StringChoice;
import org.knime.core.webui.node.dialog.defaultdialog.widget.updates.StateProvider;
import org.knime.js.base.dialog.selection.multiple.MultipleSelectionsComponentFactory;
import org.knime.js.base.node.base.filter.value.ValueFilterNodeRepresentation;
import org.knime.js.base.node.base.filter.value.ValueFilterNodeValue;
import org.knime.js.base.node.configuration.filter.MultipleEntrySelectionRendererUtil;
import org.knime.js.base.node.configuration.renderers.LabeledGroupRenderer;
import org.knime.js.base.node.configuration.renderers.StaticChoicesDropdownRenderer;
import org.knime.js.base.node.configuration.selection.value.DomainFromColumnDropdownProvider;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * The dialog representation of the value filter configuration node
 *
 * @author Christian Albrecht, KNIME GmbH, Konstanz, Germany
 */
public class ValueFilterDialogNodeRepresentation extends ValueFilterNodeRepresentation<ValueFilterDialogNodeValue>
    implements SubNodeDescriptionProvider<ValueFilterDialogNodeValue>,
    WebDialogNodeRepresentation<ValueFilterDialogNodeValue> {

    @JsonCreator
    private ValueFilterDialogNodeRepresentation(@JsonProperty("label") final String label,
        @JsonProperty("description") final String description, @JsonProperty("required") final boolean required,
        @JsonProperty("defaultValue") final ValueFilterDialogNodeValue defaultValue,
        @JsonProperty("currentValue") final ValueFilterDialogNodeValue currentValue,
        @JsonProperty("lockColumn") final boolean lockColumn,
        @JsonProperty("possibleValues") final Map<String, List<String>> possibleValues,
        @JsonProperty("type") final String type,
        @JsonProperty("limitNumberVisOptions") final boolean limitNumberVisOptions,
        @JsonProperty("numberVisOptions") final Integer numberVisOptions) {
        super(label, description, required, defaultValue, currentValue, lockColumn, possibleValues, type,
            limitNumberVisOptions, numberVisOptions);
    }

    /**
     * @param currentValue The value currently used by the node
     * @param config The config of the node
     */
    public ValueFilterDialogNodeRepresentation(final ValueFilterDialogNodeValue currentValue,
        final ValueFilterDialogNodeConfig config) {
        super(currentValue, config.getDefaultValue(), config.getValueFilterConfig(), config.getLabelConfig());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DialogNodePanel<ValueFilterDialogNodeValue> createDialogPanel() {
        return new ValueFilterConfigurationPanel(this);
    }

    @Override
    public DialogElementRendererSpec getWebUIDialogElementRendererSpec() {
        final var columnToDomainPossibleValues = getPossibleValues();
        if (isLockColumn()) {
            final var lockedColumn = getCurrentValue().getColumn();
            final List<String> lockedColumnValues = columnToDomainPossibleValues.containsKey(lockedColumn)
                ? columnToDomainPossibleValues.get(lockedColumn) : List.of();
            return getStaticValueRenderer(lockedColumnValues.toArray(String[]::new)).at("values");
        }
        final var columnDropdown = new StaticChoicesDropdownRenderer("Column", getPossibleColumns());
        final var domainStateProvider =
            new DomainFromColumnDropdownProvider(columnToDomainPossibleValues, columnDropdown);
        final var valueControl = getDynamicValueRenderer("Values", domainStateProvider);
        return new LabeledGroupRenderer(this, List.of(columnDropdown.at("column"), valueControl.at("values")));
    }

    @SuppressWarnings("rawtypes")
    private DialogElementRendererSpec getStaticValueRenderer(final String[] possibleValues) {
        return MultipleEntrySelectionRendererUtil.getWebUIDialogControlSpecByType(this, getType(), possibleValues,
            isLimitNumberVisOptions(), getNumberVisOptions(), true);
    }

    @SuppressWarnings("rawtypes")
    private DialogElementRendererSpec getDynamicValueRenderer(final String name,
        final StateProvider<List<StringChoice>> possibleValuesProvider) {
        return MultipleEntrySelectionRendererUtil.getWebUIDialogControlSpecByType(name, getType(),
            possibleValuesProvider, isLimitNumberVisOptions(), getNumberVisOptions(), true);
    }

    @Override
    public Optional<PersistSchema> getPersistSchema() {
        if (!getType().equals(MultipleSelectionsComponentFactory.TWINLIST)) {
            return Optional.empty();
        }
        return Optional.of(new PersistSchema.PersistTreeSchema.PersistTreeSchemaRecord(
            Map.of("values", new PersistSchema.PersistLeafSchema() {

                @Override
                public Optional<String[][]> getConfigPaths() {
                    return Optional.of(new String[][]{//
                        {ValueFilterNodeValue.CFG_VALUES}, //
                        {ValueFilterDialogNodeValue.CFG_EXCLUDES}, //
                        {ValueFilterDialogNodeValue.CFG_ENFORCE_OPT}, //
                    });
                }

            })));
    }

    @Override
    public JsonNode transformValueToDialogJson(final ValueFilterDialogNodeValue value) throws IOException {
        final var mapper = JsonFormsDataUtil.getMapper();
        final var root = mapper.createObjectNode();
        final var values = createValuesJsonNode(value, mapper);
        root.set("values", values);
        root.put("column", value.getColumn());
        return root;
    }

    private JsonNode createValuesJsonNode(final ValueFilterDialogNodeValue value, final ObjectMapper mapper) {
        return MultipleSelectionsComponentFactory.TWINLIST.equals(getType()) ? createManualFilterJsonNode(value, mapper)
            : mapper.valueToTree(value.getValues());
    }

    private static JsonNode createManualFilterJsonNode(final ValueFilterDialogNodeValue value,
        final ObjectMapper mapper) {
        final var manualFilter = new ManualFilter(value.getValues(), value.getExcludes(),
            value.getEnforceOption() == EnforceOption.EnforceExclusion);
        return mapper.valueToTree(manualFilter);
    }

    @Override
    public void setValueFromDialogJson(final JsonNode json, final ValueFilterDialogNodeValue value) throws IOException {

        final var column = json.get("column").asText(null);
        value.setColumn(column);
        setValuesFromJson(column, json.get("values"), value);

    }

    private void setValuesFromJson(final String selectedCol, final JsonNode json,
        final ValueFilterDialogNodeValue value) throws JsonProcessingException {
        if (MultipleSelectionsComponentFactory.TWINLIST.equals(getType())) {
            setManualFilterValue(json, value);
        } else {
            final var selectedValues = JsonFormsDataUtil.getMapper().treeToValue(json, String[].class);
            ValueFilterDialogUtils.getPossibleValuedForCol(selectedCol, getPossibleValues())
                .ifPresent(possibleValuedForCol -> ValueFilterDialogUtils.setIncludesAndExcludes(value, selectedValues,
                    possibleValuedForCol));
        }
    }

    private static void setManualFilterValue(final JsonNode json, final ValueFilterDialogNodeValue value)
        throws JsonProcessingException {
        final var manualFilter = JsonFormsDataUtil.getMapper().treeToValue(json, ManualFilter.class);
        value.setEnforceOption(
            manualFilter.m_includeUnknownColumns ? EnforceOption.EnforceExclusion : EnforceOption.EnforceInclusion);
        value.setExcludes(manualFilter.m_manuallyDeselected);
        value.setValues(manualFilter.m_manuallySelected);
    }

}
