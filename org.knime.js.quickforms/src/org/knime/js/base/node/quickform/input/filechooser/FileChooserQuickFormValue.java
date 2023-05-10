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
 *   29.09.2014 (Christian Albrecht, KNIME AG, Zurich, Switzerland): created
 */
package org.knime.js.base.node.quickform.input.filechooser;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.dialog.DialogNodeValue;
import org.knime.core.util.JsonUtil;
import org.knime.js.base.node.quickform.input.filechooser.FileChooserQuickFormConfig.SelectionType;
import org.knime.js.core.JSONViewContent;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonException;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonString;
import jakarta.json.JsonValue;

/**
 *
 * @author Christian Albrecht, KNIME.com GmbH, Konstanz, Germany
 */
@JsonAutoDetect
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public class FileChooserQuickFormValue extends JSONViewContent implements DialogNodeValue {

    private static final String CFG_ITEMS = "items";
    private FileItem[] m_items = new FileItem[0];


    /**
     * @return the items
     */
    @JsonProperty("items")
    public FileItem[] getItems() {
        return m_items;
    }

    /**
     * @param items the items to set
     */
    @JsonProperty("items")
    public void setItems(final FileItem[] items) {
        m_items = items;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @JsonIgnore
    public void saveToNodeSettings(final NodeSettingsWO settings) {
        NodeSettingsWO itemSettings = settings.addNodeSettings(CFG_ITEMS);
        itemSettings.addInt("num_items", m_items.length);
        for (int i = 0; i < m_items.length; i++) {
            NodeSettingsWO singleItemSettings = itemSettings.addNodeSettings("item_" + i);
            m_items[i].saveToNodeSettings(singleItemSettings);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @JsonIgnore
    public void loadFromNodeSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
        NodeSettingsRO itemSettings = settings.getNodeSettings(CFG_ITEMS);
        int numItems = itemSettings.getInt("num_items");
        m_items = new FileItem[numItems];
        for (int i = 0; i < numItems; i++) {
            m_items[i] = new FileItem();
            NodeSettingsRO singleItemSettings = itemSettings.getNodeSettings("item_" + i);
            m_items[i].loadFromNodeSettings(singleItemSettings);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @JsonIgnore
    public void loadFromNodeSettingsInDialog(final NodeSettingsRO settings) {
        m_items = new FileItem[0];
        if (settings.containsKey(CFG_ITEMS)) {
            try {
                NodeSettingsRO itemSettings = settings.getNodeSettings(CFG_ITEMS);
                int numItems = itemSettings.getInt("num_items", 0);
                m_items = new FileItem[numItems];
                for (int i = 0; i < numItems; i++) {
                    m_items[i] = new FileItem();
                    NodeSettingsRO singleItemSettings = itemSettings.getNodeSettings("item_" + i);
                    m_items[i].loadFromNodeSettingsInDialog(singleItemSettings);
                }
            } catch (InvalidSettingsException e) { /* do nothing */ }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("items=");
        sb.append(StringUtils.join(m_items, ", "));
        return sb.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(m_items)
                .toHashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj.getClass() != getClass()) {
            return false;
        }
        FileChooserQuickFormValue other = (FileChooserQuickFormValue)obj;
        return new EqualsBuilder()
                .append(m_items, other.m_items)
                .isEquals();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadFromString(final String fromCmdLine) throws UnsupportedOperationException {
        FileItem item = new FileItem();
        item.setPath(fromCmdLine);
        setItems(new FileItem[]{item});
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadFromJson(final JsonValue json) throws JsonException {
        if (json instanceof JsonString) {
            loadFromString(((JsonString) json).getString());
        } else if (json instanceof JsonObject) {
            try {
                JsonValue val = ((JsonObject)json).get(CFG_ITEMS);
                if (JsonValue.NULL.equals(val)) {
                    m_items = new FileItem[0];
                } else {
                    JsonArray itemsArray = ((JsonObject)json).getJsonArray(CFG_ITEMS);
                    m_items = new FileItem[itemsArray.size()];
                    for (int i = 0; i < itemsArray.size(); i++) {
                        if (JsonValue.NULL.equals(itemsArray.get(i))) {
                            m_items[i] = null;
                        } else {
                            JsonObject item = itemsArray.getJsonObject(i);
                            m_items[i] = new FileItem();
                            if (JsonValue.NULL.equals(item.get(FileItem.CFG_PATH))) {
                                m_items[i].setPath(null);
                            } else {
                                m_items[i].setPath(item.getString(FileItem.CFG_PATH));
                            }
                            if (JsonValue.NULL.equals(item.get(FileItem.CFG_TYPE))) {
                                m_items[i].setType((String)null);
                            } else {
                                m_items[i].setType(item.getString(FileItem.CFG_TYPE));
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
    public JsonObject toJson() {
        JsonObjectBuilder builder = JsonUtil.getProvider().createObjectBuilder();
        if (m_items == null) {
            builder.addNull(CFG_ITEMS);
        } else {
            JsonArrayBuilder itemsBuilder = JsonUtil.getProvider().createArrayBuilder();
            for (FileItem item : m_items) {
                if (item == null) {
                    itemsBuilder.addNull();
                } else {
                    JsonObjectBuilder itemBuilder = JsonUtil.getProvider().createObjectBuilder();
                    String path = item.getPath();
                    if (path == null) {
                        itemBuilder.addNull(FileItem.CFG_PATH);
                    } else {
                        itemBuilder.add(FileItem.CFG_PATH, item.getPath());
                    }
                    String type = item.getType();
                    if (type == null) {
                        itemBuilder.addNull(FileItem.CFG_TYPE);
                    } else {
                        itemBuilder.add(FileItem.CFG_TYPE, item.getType());
                    }
                    itemsBuilder.add(itemBuilder);
                }
            }
            builder.add(CFG_ITEMS, itemsBuilder);
        }
        return builder.build();
    }


    static class FileItem {

        private static final String CFG_PATH = "path";
        private static final String DEFAULT_PATH = "";
        private String m_path = DEFAULT_PATH;
        private static final String CFG_TYPE = "type";
        private static final SelectionType DEFAULT_TYPE = SelectionType.UNKNOWN;
        private SelectionType m_type = DEFAULT_TYPE;

        private FileItem() { /* serialization constructor */ }

        /**
         * @param path the knime url path
         * @param type the item type
         */
        @JsonCreator
        public FileItem(@JsonProperty("path") final String path, @JsonProperty("type") final String type) {
            m_path = path;
            m_type = SelectionType.fromString(type);
        }

        public FileItem(final String path, final SelectionType type) {
            m_path = path;
            m_type = type;
        }

        /**
         * @return the path
         */
        @JsonProperty("path")
        public String getPath() {
            return m_path;
        }

        /**
         * @param path the path to set
         */
        @JsonProperty("path")
        public void setPath(final String path) {
            m_path = path;
        }

        /**
         * @return the type
         */
        @JsonProperty("type")
        public String getType() {
            return m_type.toString();
        }

        /**
         * @return the type
         */
        @JsonIgnore
        public SelectionType getSelectionType() {
            return m_type;
        }

        /**
         * @param type the type to set
         */
        @JsonProperty("type")
        public void setType(final String type) {
            m_type = SelectionType.fromString(type);
        }

        /**
         *
         * @param type the type to set
         */
        @JsonIgnore
        public void setType(final SelectionType type) {
            m_type = type;
        }


        @JsonIgnore
        public void saveToNodeSettings(final NodeSettingsWO settings) {
            settings.addString(CFG_PATH, getPath());
            settings.addString(CFG_TYPE, getType());
        }

        @JsonIgnore
        public void loadFromNodeSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
            setPath(settings.getString(CFG_PATH));
            setType(settings.getString(CFG_TYPE));
        }

        @JsonIgnore
        public void loadFromNodeSettingsInDialog(final NodeSettingsRO settings) {
            setPath(settings.getString(CFG_PATH, DEFAULT_PATH));
            setType(settings.getString(CFG_TYPE, DEFAULT_TYPE.toString()));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("path=");
            sb.append(m_path);
            sb.append(", type=");
            sb.append(m_type.toString());
            return sb.toString();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int hashCode() {
            return new HashCodeBuilder()
                    .append(m_path)
                    .append(m_type.toString())
                    .toHashCode();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean equals(final Object obj) {
            if (obj == null) {
                return false;
            }
            if (obj == this) {
                return true;
            }
            if (obj.getClass() != getClass()) {
                return false;
            }
            FileItem other = (FileItem)obj;
            return new EqualsBuilder()
                    .append(m_path, other.m_path)
                    .append(m_type.toString(), other.m_type.toString())
                    .isEquals();
        }

    }

}
