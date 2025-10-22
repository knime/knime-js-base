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
 *   Apr 24, 2025 (Paul Bärnreuther): created
 */
package org.knime.js.base.node.configuration.input.filechooser;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;

import org.junit.jupiter.api.Test;
import org.knime.js.base.node.configuration.IntegratedComponentDialogTestBase;

import com.fasterxml.jackson.core.JsonProcessingException;

class FilechooserConfigurationComponentDialogTest extends IntegratedComponentDialogTestBase {

    @Test
    void testFileChooserConfigurationComponentDialog() throws JsonProcessingException {
        final var dialogData = getComponentDialog(getTopLevelNodeId(2));
        final var paramName = "file-chooser-input-3";
        final var data = dialogData.getDataFor(paramName);
        assertThatJson(data).inPath("$.items.item_0").isString().isEqualTo("knime://LOCAL/Data/iris_setosa.csv");
        assertThatJson(data).inPath("$.items.prev_item_0.path").isString()
            .isEqualTo("knime://LOCAL/Data/iris_setosa.csv");
        assertThatJson(data).inPath("$.items.prev_item_0.type").isString().isEqualTo("UNKNOWN");
        final var schema = dialogData.getSchemaFor(paramName);
        assertThatJson(schema).inPath("$.properties.items.properties.item_0.type").isString().isEqualTo("string");
        assertThatJson(schema).inPath("$.properties.items.properties.item_0.title").isString()
            .isEqualTo("File chooser label");
        assertThatJson(schema).inPath("$.properties.items.properties.item_0.description").isString()
            .isEqualTo("File chooser description");
        final var uiSchema = dialogData.getUiSchema();
        assertThatJson(uiSchema).inPath("$.elements[0].type").isString().isEqualTo("Control");
        assertThatJson(uiSchema).inPath("$.elements[0].scope").isString()
            .isEqualTo(String.format("#/properties/model/properties/%s/properties/items/properties/item_0", paramName));
        final var persistSchema = dialogData.getPersistSchema();
        assertThatJson(persistSchema)
            .inPath(
                String.format("$.properties.model.properties.%s.properties.items.properties.item_0.type", paramName))
            .isString().isEqualTo("leaf");
        assertThatJson(persistSchema).inPath(
            String.format("$.properties.model.properties.%s.properties.items.properties.item_0.configPaths", paramName))
            .isArray().isEqualTo(new String[][]{{"item_0", "path"}, {"item_0", "type"}});
    }

}
