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
 *   3 Jun 2019 (albrecht): created
 */
package org.knime.js.base.node.configuration.input.filechooser;

import java.io.IOException;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.util.JsonUtil;
import org.knime.core.webui.node.dialog.WebDialogValue;
import org.knime.core.webui.node.dialog.defaultdialog.jsonforms.JsonFormsDataUtil;
import org.knime.js.base.node.base.input.filechooser.FileChooserNodeConfig.SelectionType;
import org.knime.js.base.node.base.input.filechooser.FileChooserNodeValue;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;

import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonException;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonString;
import jakarta.json.JsonValue;

/**
 * The value for the file chooser configuration node
 *
 * @author Christian Albrecht, KNIME GmbH, Konstanz, Germany
 */
public class FileChooserDialogNodeValue extends FileChooserNodeValue implements WebDialogValue {

    /**
     * {@inheritDoc}
     */
    @Override
    @JsonIgnore
    public void loadFromNodeSettingsInDialog(final NodeSettingsRO settings) {
        setItems(new FileItem[0]);
        if (settings.containsKey(CFG_ITEMS)) {
            try {
                NodeSettingsRO itemSettings = settings.getNodeSettings(CFG_ITEMS);
                int numItems = itemSettings.getInt(CFG_NUM_ITEMS, 0);
                setItems(new FileItem[numItems]);
                FileItem[] items = getItems();
                for (int i = 0; i < numItems; i++) {
                    items[i] = new FileItem();
                    NodeSettingsRO singleItemSettings = itemSettings.getNodeSettings("item_" + i);
                    items[i].loadFromNodeSettingsInDialog(singleItemSettings);
                }
            } catch (InvalidSettingsException e) {
                /* do nothing */ }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @JsonIgnore
    public void loadFromString(final String fromCmdLine) throws UnsupportedOperationException {
        FileItem item = new FileItem();
        item.setPath(fromCmdLine);
        setItems(new FileItem[]{item});
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @JsonIgnore
    public void loadFromJson(final JsonValue json) throws JsonException {
        if (json instanceof JsonString) {
            loadFromString(((JsonString)json).getString());
        } else if (json instanceof JsonObject || json instanceof JsonArray) {
            try {
                JsonValue val = json instanceof JsonObject ? ((JsonObject)json).get(CFG_ITEMS) : json;
                if (JsonValue.NULL.equals(val) && !(json instanceof JsonArray)) {
                    setItems(new FileItem[0]);
                } else {
                    JsonArray itemsArray =
                        json instanceof JsonArray ? (JsonArray)json : ((JsonObject)json).getJsonArray(CFG_ITEMS);
                    setItems(new FileItem[itemsArray.size()]);
                    FileItem[] items = getItems();
                    for (int i = 0; i < itemsArray.size(); i++) {
                        if (JsonValue.NULL.equals(itemsArray.get(i))) {
                            items[i] = null;
                        } else {
                            JsonObject item = itemsArray.getJsonObject(i);
                            items[i] = new FileItem();
                            if (JsonValue.NULL.equals(item.get(FileItem.CFG_PATH))) {
                                items[i].setPath(null);
                            } else {
                                items[i].setPath(item.getString(FileItem.CFG_PATH));
                            }
                            if (JsonValue.NULL.equals(item.get("fileType"))) {
                                items[i].setType((String)null);
                            } else {
                                items[i].setType(item.getString("fileType"));
                            }
                        }
                    }
                }
            } catch (Exception e) {
                throw new JsonException("Expected item values for key '" + CFG_ITEMS + "'.", e);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @JsonIgnore
    public JsonValue toJson() {
        final JsonObjectBuilder builder = JsonUtil.getProvider().createObjectBuilder();
        final JsonArrayBuilder arrayBuilder = JsonUtil.getProvider().createArrayBuilder();
        builder.add("type", "array");

        if (getItems() == null) {
            arrayBuilder.add(createItemJson(null));
        } else {
            for (FileItem item : getItems()) {
                arrayBuilder.add(createItemJson(item));
            }
        }
        JsonObjectBuilder itemBuilder = JsonUtil.getProvider().createObjectBuilder();
        itemBuilder.add("type", "object");
        builder.add("items", itemBuilder);
        builder.add("default", arrayBuilder);

        return builder.build();
    }

    private static JsonObject createItemJson(final FileItem item) {
        final JsonObjectBuilder itemBuilder = JsonUtil.getProvider().createObjectBuilder();
        final JsonObjectBuilder pathBuilder = JsonUtil.getProvider().createObjectBuilder();
        final JsonObjectBuilder typeBuilder = JsonUtil.getProvider().createObjectBuilder();

        pathBuilder.add("type", "string");
        typeBuilder.add("type", "string");

        if (item == null) {
            pathBuilder.addNull("default");
            typeBuilder.addNull("default");
        } else {
            final String path = item.getPath();
            if (path == null) {
                pathBuilder.addNull("default");
            } else {
                pathBuilder.add("default", path);
            }

            String type = item.getType();
            if (type == null) {
                typeBuilder.addNull("default");
            } else {
                typeBuilder.add("default", item.getType());
            }
        }
        itemBuilder.add("type", "object");
        itemBuilder.add(FileItem.CFG_PATH, pathBuilder.build());
        itemBuilder.add("fileType", typeBuilder.build());

        return itemBuilder.build();
    }

    static final String FIRST_ITEM = "item_0";

    static final String PREVIOUS_FIRST_ITEM = "prev_item_0";

    @Override
    public JsonNode toDialogJson() throws IOException {
        var mapper = JsonFormsDataUtil.getMapper();
        var rootNode = mapper.createObjectNode();
        var items = getItems();

        if (items.length > 0) {
            var item = items[0];
            rootNode.put(FIRST_ITEM, item.getPath());
            rootNode.set(PREVIOUS_FIRST_ITEM, mapper.valueToTree(item));
        } else {
            rootNode.put(FIRST_ITEM, "");
            rootNode.set(PREVIOUS_FIRST_ITEM, mapper.nullNode());
        }

        return mapper.createObjectNode().set(FileChooserNodeValue.CFG_ITEMS, rootNode);
    }

    @Override
    public void fromDialogJson(final JsonNode json) throws IOException {
        var fileChooser = json.get(FileChooserNodeValue.CFG_ITEMS);
        var path = fileChooser.path(FIRST_ITEM).asText("");

        final var previousItem = fileChooser.path(PREVIOUS_FIRST_ITEM);
        final var type = (path.isEmpty() || previousItem.isNull() || !path.equals(previousItem.path("path").asText()))
            ? SelectionType.UNKNOWN.name() //
            : previousItem.path("type").asText();

        setItems(new FileItem[]{new FileItem(path, type)});
    }
}
