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
 *   10 Oct 2025 (Robin Gerling): created
 */
package org.knime.js.base.node.configuration.input.filechooser;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.knime.js.base.node.base.input.filechooser.FileChooserNodeConfig.SelectionType;
import org.knime.js.base.node.base.input.filechooser.FileChooserNodeValue;
import org.knime.js.base.node.base.input.filechooser.FileChooserNodeValue.FileItem;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

class FileChooserDialogNodeValueToAndFromJsonTest {

    private static FileChooserDialogNodeValue createFileChooserDialogNodeValue(final FileItem[] items) {
        final var fileChooserDialogNodeValue =
            new FileChooserDialogNodeFactory().createNodeModel().createEmptyDialogValue();
        fileChooserDialogNodeValue.setItems(items);
        return fileChooserDialogNodeValue;
    }

    private static JsonNode setPathOfFirstItem(final JsonNode json, final String path) {
        var items = (ObjectNode)json.get(FileChooserNodeValue.CFG_ITEMS);
        items.put("item_0", path);
        return json;
    }

    @Test
    void testToFromDialogJSONWithoutItems() throws IOException {
        final var fileChooserDialogNodeValue = createFileChooserDialogNodeValue(new FileItem[0]);
        fileChooserDialogNodeValue.fromDialogJson(fileChooserDialogNodeValue.toDialogJson());
        assertThat(fileChooserDialogNodeValue.getItems())
            .isEqualTo(new FileItem[]{new FileItem("", SelectionType.UNKNOWN)});
    }

    @Test
    void testToFromDialogJSONWithItems() throws IOException {
        final var items = new FileItem[]{new FileItem("path/to/the/workflow/item", SelectionType.WORKFLOW)};
        final var fileChooserDialogNodeValue = createFileChooserDialogNodeValue(items);
        fileChooserDialogNodeValue.fromDialogJson(fileChooserDialogNodeValue.toDialogJson());
        assertThat(fileChooserDialogNodeValue.getItems()).isEqualTo(items);
    }

    @Test
    void testFromDialogJSONAddedItem() throws IOException {
        final var fileChooserDialogNodeValue = createFileChooserDialogNodeValue(new FileItem[0]);
        final var json = fileChooserDialogNodeValue.toDialogJson();
        final var newPath = "path/to/the/another/item";
        setPathOfFirstItem(json, newPath);
        fileChooserDialogNodeValue.fromDialogJson(json);
        assertThat(fileChooserDialogNodeValue.getItems())
            .isEqualTo(new FileItem[]{new FileItem(newPath, SelectionType.UNKNOWN)});
    }

    @Test
    void testFromDialogJSONReplacedItem() throws IOException {
        final var items = new FileItem[]{new FileItem("path/to/the/workflow/item", SelectionType.WORKFLOW)};
        final var fileChooserDialogNodeValue = createFileChooserDialogNodeValue(items);
        final var json = fileChooserDialogNodeValue.toDialogJson();
        final var newPath = "path/to/the/another/item";
        setPathOfFirstItem(json, newPath);
        fileChooserDialogNodeValue.fromDialogJson(json);
        assertThat(fileChooserDialogNodeValue.getItems())
            .isEqualTo(new FileItem[]{new FileItem(newPath, SelectionType.UNKNOWN)});
    }

    @Test
    void testFromDialogJSONRemovedItem() throws IOException {
        final var items = new FileItem[]{new FileItem("path/to/the/workflow/item", SelectionType.WORKFLOW)};
        final var fileChooserDialogNodeValue = createFileChooserDialogNodeValue(items);
        final var json = fileChooserDialogNodeValue.toDialogJson();
        final var newPath = "";
        setPathOfFirstItem(json, newPath);
        fileChooserDialogNodeValue.fromDialogJson(json);
        assertThat(fileChooserDialogNodeValue.getItems())
            .isEqualTo(new FileItem[]{new FileItem("", SelectionType.UNKNOWN)});
    }

}
