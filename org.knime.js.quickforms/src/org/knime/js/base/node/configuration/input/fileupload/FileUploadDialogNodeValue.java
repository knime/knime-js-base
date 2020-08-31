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
 *   Jun 3, 2019 (Daniel Bogenrieder): created
 */
package org.knime.js.base.node.configuration.input.fileupload;

import javax.json.Json;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonString;
import javax.json.JsonValue;

import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.dialog.DialogNodeValue;
import org.knime.js.base.node.base.input.fileupload.FileUploadNodeValue;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * The value for the file upload configuration node
 *
 * @author Daniel Bogenrieder, KNIME GmbH, Konstanz, Germany
 */
public class FileUploadDialogNodeValue extends FileUploadNodeValue implements DialogNodeValue {

    /**
     * {@inheritDoc}
     */
    @Override
    @JsonIgnore
    public void loadFromNodeSettingsInDialog(final NodeSettingsRO settings) {
        setPath(settings.getString(CFG_PATH, DEFAULT_PATH));
        setPathValid(settings.getBoolean(CFG_PATH, DEFAULT_PATH_VALID));
        setFileName(settings.getString(CFG_FILE_NAME, DEFAULT_FILE_NAME));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadFromString(final String fromCmdLine) throws UnsupportedOperationException {
        setPath(fromCmdLine);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadFromJson(final JsonValue json) throws JsonException {
        if (json instanceof JsonString) {
            m_path = ((JsonString) json).getString();
        } else if (json instanceof JsonObject) {
            try {
                JsonValue jsonPath = ((JsonObject)json).get(CFG_PATH);
                if (JsonValue.NULL.equals(jsonPath)) {
                    m_path = null;
                } else {
                    m_path = ((JsonObject) json).getString(CFG_PATH);
                }
                JsonValue jsonFileName = ((JsonObject)json).get(CFG_FILE_NAME);
                if (JsonValue.NULL.equals(jsonFileName)) {
                    m_fileName = DEFAULT_FILE_NAME;
                } else {
                    m_fileName = ((JsonObject)json).getString(CFG_FILE_NAME);
                }
            } catch (Exception e) {
                throw new JsonException("Expected path value for key '" + CFG_PATH + "'.", e);
            }
        } else {
            throw new JsonException("Expected JSON object or JSON string, but got " + json.getValueType());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JsonObject toJson() {
        final JsonObjectBuilder builder = Json.createObjectBuilder();
        builder.add("type", "string");

        if (m_path == null) {
            builder.addNull("default");
        } else {
            builder.add("default", m_path);
        }
        if (m_fileName == null) {
            builder.addNull(CFG_FILE_NAME);
        } else {
            builder.add(CFG_FILE_NAME, m_fileName);
        }
        return builder.build();
    }

}
