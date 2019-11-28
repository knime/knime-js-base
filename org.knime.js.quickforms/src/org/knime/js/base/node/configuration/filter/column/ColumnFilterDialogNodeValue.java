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
 *   26 May 2019 (albrecht): created
 */
package org.knime.js.base.node.configuration.filter.column;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettings;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.dialog.DialogNodeValue;
import org.knime.js.base.node.base.filter.column.ColumnFilterNodeConfig;
import org.knime.js.base.node.base.filter.column.ColumnFilterNodeValue;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * The value for the column filter configuration node
 *
 * @author Christian Albrecht, KNIME GmbH, Konstanz, Germany
 */
public class ColumnFilterDialogNodeValue extends ColumnFilterNodeValue implements DialogNodeValue {

    /**
     * Constructs a new ColumnFilterDialogNodeValue.
     *
     * @deprecated as of KNIME AP 4.1.0 use {@link ColumnFilterDialogNodeValue#ColumnFilterDialogNodeValue(boolean)}
     *             instead
     */
    @Deprecated
    public ColumnFilterDialogNodeValue() {
        super();
    }

    /**
     * Constructs a new ColumnFilterDialogNodeValue.
     *
     * @param initializeFilterSettings set to true if the settings should be initialized
     */
    public ColumnFilterDialogNodeValue(final boolean initializeFilterSettings) {
        super(initializeFilterSettings);
    }

    /**
     * @return the settings
     */
    @Override
    @JsonProperty("settings")
    @JsonDeserialize(using = NodeSettingsDeserializer.class)
    public NodeSettings getSettings() {
        return super.getSettings();
    }

    /**
     * @param settings the settings to set
     */
    @Override
    @JsonProperty("settings")
    @JsonSerialize(using = NodeSettingsSerializer.class)
    public void setSettings(final NodeSettings settings) {
        super.setSettings(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @JsonIgnore
    public void loadFromNodeSettingsInDialog(final NodeSettingsRO settings) {
        setColumns(settings.getStringArray(CFG_COLUMNS, DEFAULT_COLUMNS));
        try {
            setSettings((NodeSettings) settings.getNodeSettings(ColumnFilterNodeConfig.CFG_COLUMN_FILTER));
        } catch (InvalidSettingsException e) {
            setSettings(null);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadFromString(final String fromCmdLine) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Parameterization of column filter not supported!");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @JsonIgnore
    public void loadFromJson(final JsonValue json) throws JsonException {
        if (json instanceof JsonArray) {
            JsonArray array = (JsonArray) json;
            setColumns(new String[array.size()]);
            for (int i = 0; i < array.size(); i++) {
                getColumns()[i] = array.getString(i);
            }
        } else if (json instanceof JsonObject) {
            try {
                JsonValue val = ((JsonObject) json).get(CFG_COLUMNS);
                if (JsonValue.NULL.equals(val)) {
                    setColumns(null);
                } else {
                    JsonArray array = ((JsonObject) json).getJsonArray(CFG_COLUMNS);
                    setColumns(new String[array.size()]);
                    for (int i = 0; i < array.size(); i++) {
                        getColumns()[i] = array.getString(i);
                    }
                }
            } catch (Exception e) {
                throw new JsonException("Expected valid string array for key '" + CFG_COLUMNS + ".", e);
            }
        } else {
            throw new JsonException("Expected JSON object or JSON array, but got " + json.getValueType());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @JsonIgnore
    public JsonValue toJson() {
        final JsonObjectBuilder builder = Json.createObjectBuilder();
        builder.add("type", "array");

        if (getColumns() == null) {
            builder.addNull("default");
        } else {
            JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
            for (String col : getColumns()) {
                arrayBuilder.add(col);
            }
            JsonObjectBuilder itemBuilder = Json.createObjectBuilder();
            itemBuilder.add("type", "string");
            builder.add("items", itemBuilder);
            builder.add("default", arrayBuilder);
        }
        return builder.build();
    }
}
