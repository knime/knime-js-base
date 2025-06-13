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
 *   8 May 2025 (Robin Gerling): created
 */
package org.knime.js.base.node.configuration.filter.value;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;

import org.junit.jupiter.api.Test;
import org.knime.js.base.node.configuration.IntegratedComponentDialogTestBase;

import com.fasterxml.jackson.core.JsonProcessingException;

class ValueFilterConfigurationComponentDialogTest extends IntegratedComponentDialogTestBase {

    @Test
    void testValueFilterConfiguration() throws JsonProcessingException {
        final var dialogData = getComponentDialog(getTopLevelNodeId(3));
        final var paramName = "value-filter-3";
        final var data = dialogData.getDataFor(paramName);
        assertThatJson(data).inPath("$.column").isString().isEqualTo("Cluster Membership");
        assertThatJson(data).inPath("$.values.manuallySelected").isArray().isEmpty();
        assertThatJson(data).inPath("$.values.manuallyDeselected").isArray().containsExactly("Cluster_0", "Cluster_1",
            "Cluster_2", "Cluster_3");
        assertThatJson(data).inPath("$.values.includeUnknownColumns").isBoolean().isTrue();
        final var schema = dialogData.getSchemaFor(paramName);
        assertThatJson(schema).inPath("$.properties.column.type").isString().isEqualTo("string");
        assertThatJson(schema).inPath("$.properties.column.title").isString().isEqualTo("Column");
        assertThatJson(schema).inPath("$.properties.values.type").isString().isEqualTo("object");
        assertThatJson(schema).inPath("$.properties.values.title").isString().isEqualTo("Values");
        final var uiSchema = dialogData.getUiSchema();
        assertThatJson(uiSchema).inPath("$.elements[0].type").isString().isEqualTo("Group");
        assertThatJson(uiSchema).inPath("$.elements[0].elements[0].type").isString().isEqualTo("Control");
        assertThatJson(uiSchema).inPath("$.elements[0].elements[0].scope").isString()
            .isEqualTo(String.format("#/properties/model/properties/%s/properties/column", paramName));
        assertThatJson(uiSchema).inPath("$.elements[0].elements[0].options.possibleValues").isArray().hasSize(1);

        assertThatJson(uiSchema).inPath("$.elements[0].elements[1].type").isString().isEqualTo("Control");
        assertThatJson(uiSchema).inPath("$.elements[0].elements[1].options.format").isString()
            .isEqualTo("manualTwinlist");
        assertThatJson(uiSchema).inPath("$.elements[0].elements[1].scope").isString()
            .isEqualTo(String.format("#/properties/model/properties/%s/properties/values", paramName));
        assertThatJson(uiSchema).inPath("$.elements[0].elements[1].providedOptions").isArray()
            .containsExactly("possibleValues");
        final var initialUpdates = dialogData.getInitialUpdates();
        assertThatJson(initialUpdates).inPath("[0].providedOptionName").isString().isEqualTo("possibleValues");
        assertThatJson(initialUpdates).inPath("[0].values[0].value").isArray().hasSize(4);
        assertThatJson(initialUpdates).inPath("[0].values[0].value[0].id").isString().isEqualTo("Cluster_0");
    }

    @Test
    void testValueFilterConfigurationWithLockedColumn() throws JsonProcessingException {
        final var dialogData = getComponentDialog(getTopLevelNodeId(3));
        final var paramName = "value-filter-with-locked-column-4";
        final var data = dialogData.getDataFor(paramName);
        assertThatJson(data).inPath("$.column").isString().isEqualTo("Cluster Membership");
        assertThatJson(data).inPath("$.values.manuallySelected").isArray().isEmpty();
        assertThatJson(data).inPath("$.values.manuallyDeselected").isArray().containsExactly("Cluster_0", "Cluster_1",
            "Cluster_2", "Cluster_3");
        assertThatJson(data).inPath("$.values.includeUnknownColumns").isBoolean().isTrue();

        final var schema = dialogData.getSchemaFor(paramName);
        assertThatJson(schema).inPath("$.properties.values.type").isString().isEqualTo("object");
        assertThatJson(schema).inPath("$.properties.values.title").isString().isEqualTo("The label");
        assertThatJson(schema).inPath("$.properties.values.description").isString().isEqualTo("The description");
        final var uiSchema = dialogData.getUiSchema();
        assertThatJson(uiSchema).inPath("$.elements[1].type").isString().isEqualTo("Control");
        assertThatJson(uiSchema).inPath("$.elements[1].options.format").isString().isEqualTo("manualTwinlist");
        assertThatJson(uiSchema).inPath("$.elements[1].scope").isString()
            .isEqualTo(String.format("#/properties/model/properties/%s/properties/values", paramName));
        assertThatJson(uiSchema).inPath("$.elements[1].options.possibleValues").isArray().hasSize(4);
    }

    @Test
    void testValueFilterConfigurationWithLimitNumberOfOptions() throws JsonProcessingException {
        final var dialogData = getComponentDialog(getTopLevelNodeId(3));

        final var uiSchema = dialogData.getUiSchema();
        assertThatJson(uiSchema).inPath("$.elements[2].elements[1].type").isString().isEqualTo("Control");
        assertThatJson(uiSchema).inPath("$.elements[2].elements[1].options.twinlistSize").isNumber();
        assertThatJson(uiSchema).inPath("$.elements[2].elements[1].scope").isString()
            .contains("value-filter-with-limited-options-5");

        assertThatJson(uiSchema).inPath("$.elements[3].type").isString().isEqualTo("Control");
        assertThatJson(uiSchema).inPath("$.elements[3].options.twinlistSize").isNumber();
        assertThatJson(uiSchema).inPath("$.elements[3].scope").isString()
            .contains("value-filter-with-locked-column-and-limited-options-6");

    }

    @Test
    void testValueFilterConfigurationList() throws JsonProcessingException {
        final var dialogData = getComponentDialog(getTopLevelNodeId(3));

        final var uiSchema = dialogData.getUiSchema();
        assertThatJson(uiSchema).inPath("$.elements[4].elements[1].type").isString().isEqualTo("Control");
        assertThatJson(uiSchema).inPath("$.elements[4].elements[1].scope").isString()
            .isEqualTo("#/properties/model/properties/list-7/properties/values");
        assertThatJson(uiSchema).inPath("$.elements[4].elements[1].options.format").isString()
            .isEqualTo("multiSelectListBox");
        assertThatJson(uiSchema).inPath("$.elements[4].elements[1].providedOptions").isArray()
            .containsExactly("possibleValues");

        assertThatJson(uiSchema).inPath("$.elements[5].type").isString().isEqualTo("Control");
        assertThatJson(uiSchema).inPath("$.elements[5].scope").isString()
            .isEqualTo("#/properties/model/properties/list-locked-11/properties/values");
        assertThatJson(uiSchema).inPath("$.elements[5].options.format").isString().isEqualTo("multiSelectListBox");
        assertThatJson(uiSchema).inPath("$.elements[5].options.possibleValues").isArray().hasSize(4);
    }

    @Test
    void testValueFilterConfigurationCheckboxes() throws JsonProcessingException {
        final var dialogData = getComponentDialog(getTopLevelNodeId(3));

        final var uiSchema = dialogData.getUiSchema();
        assertThatJson(uiSchema).inPath("$.elements[6].elements[1].type").isString().isEqualTo("Control");
        assertThatJson(uiSchema).inPath("$.elements[6].elements[1].scope").isString()
            .isEqualTo("#/properties/model/properties/checkboxes-horizontal-8/properties/values");
        assertThatJson(uiSchema).inPath("$.elements[6].elements[1].options.format").isString().isEqualTo("checkboxes");
        assertThatJson(uiSchema).inPath("$.elements[6].elements[1].options.checkboxLayout").isString()
            .isEqualTo("horizontal");
        assertThatJson(uiSchema).inPath("$.elements[6].elements[1].providedOptions").isArray()
            .containsExactly("possibleValues");

        assertThatJson(uiSchema).inPath("$.elements[7].type").isString().isEqualTo("Control");
        assertThatJson(uiSchema).inPath("$.elements[7].scope").isString()
            .isEqualTo("#/properties/model/properties/checkboxes-horizontal-locked-13/properties/values");
        assertThatJson(uiSchema).inPath("$.elements[7].options.format").isString().isEqualTo("checkboxes");
        assertThatJson(uiSchema).inPath("$.elements[7].options.possibleValues").isArray().hasSize(4);
        assertThatJson(uiSchema).inPath("$.elements[7].options.checkboxLayout").isString().isEqualTo("horizontal");

        assertThatJson(uiSchema).inPath("$.elements[8].elements[1].type").isString().isEqualTo("Control");
        assertThatJson(uiSchema).inPath("$.elements[8].elements[1].scope").isString()
            .isEqualTo("#/properties/model/properties/checkboxes-vertical-9/properties/values");
        assertThatJson(uiSchema).inPath("$.elements[8].elements[1].options.format").isString().isEqualTo("checkboxes");
        assertThatJson(uiSchema).inPath("$.elements[8].elements[1].options.checkboxLayout").isString()
            .isEqualTo("vertical");
        assertThatJson(uiSchema).inPath("$.elements[8].elements[1].providedOptions").isArray()
            .containsExactly("possibleValues");

        assertThatJson(uiSchema).inPath("$.elements[9].type").isString().isEqualTo("Control");
        assertThatJson(uiSchema).inPath("$.elements[9].scope").isString()
            .isEqualTo("#/properties/model/properties/checkboxes-vertical-locked-14/properties/values");
        assertThatJson(uiSchema).inPath("$.elements[9].options.format").isString().isEqualTo("checkboxes");
        assertThatJson(uiSchema).inPath("$.elements[9].options.checkboxLayout").isString().isEqualTo("vertical");
        assertThatJson(uiSchema).inPath("$.elements[9].options.possibleValues").isArray().hasSize(4);

    }

    @Test
    void testValueFilterConfigurationComboBox() throws JsonProcessingException {
        final var dialogData = getComponentDialog(getTopLevelNodeId(3));

        final var uiSchema = dialogData.getUiSchema();
        assertThatJson(uiSchema).inPath("$.elements[10].elements[1].type").isString().isEqualTo("Control");
        assertThatJson(uiSchema).inPath("$.elements[10].elements[1].scope").isString()
            .isEqualTo("#/properties/model/properties/combobox-10/properties/values");
        assertThatJson(uiSchema).inPath("$.elements[10].elements[1].options.format").isString().isEqualTo("comboBox");
        assertThatJson(uiSchema).inPath("$.elements[10].elements[1].providedOptions").isArray()
            .containsExactly("possibleValues");

        assertThatJson(uiSchema).inPath("$.elements[11].type").isString().isEqualTo("Control");
        assertThatJson(uiSchema).inPath("$.elements[11].scope").isString()
            .isEqualTo("#/properties/model/properties/combobox-locked-15/properties/values");
        assertThatJson(uiSchema).inPath("$.elements[11].options.format").isString().isEqualTo("comboBox");
        assertThatJson(uiSchema).inPath("$.elements[11].options.possibleValues").isArray().hasSize(4);

    }

}
