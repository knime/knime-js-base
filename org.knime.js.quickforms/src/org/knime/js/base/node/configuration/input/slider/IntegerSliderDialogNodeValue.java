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
 *   May 23, 2019 (Daniel Bogenrieder): created
 */
package org.knime.js.base.node.configuration.input.slider;


import org.knime.core.node.NodeSettingsRO;
import org.knime.core.util.JsonUtil;
import org.knime.core.webui.node.dialog.WebDialogValue.WebDialogContent;
import org.knime.js.base.node.base.input.slider.SliderNodeValue;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.json.JsonException;
import jakarta.json.JsonNumber;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonString;
import jakarta.json.JsonValue;

/**
 * The config for the slider configuration node.
 *
 * @author Daniel Bogenrieder, KNIME GmbH, Konstanz, Germany
 */
public class IntegerSliderDialogNodeValue extends SliderNodeValue implements WebDialogContent {

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadFromNodeSettingsInDialog(final NodeSettingsRO settings) {
        setDouble(settings.getDouble(CFG_DOUBLE, DEFAULT_DOUBLE));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @JsonIgnore
    public void loadFromString(final String fromCmdLine) throws UnsupportedOperationException {
        Double number = null;
        try {
            number = Double.parseDouble(fromCmdLine);
        } catch (Exception e) {
            throw new UnsupportedOperationException("Could not parse '" + fromCmdLine + "' as double type.");
        }
        setDouble(number);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadFromJson(final JsonValue json) throws JsonException {
        if (json instanceof JsonNumber) {
            m_double = ((JsonNumber)json).doubleValue();
        } else if (json instanceof JsonString) {
            loadFromString(((JsonString)json).getString());
        } else if (json instanceof JsonObject) {
            try {
                m_double = ((JsonObject) json).getJsonNumber("integer").doubleValue();
            } catch (Exception e) {
                throw new JsonException("Expected double value for key 'integer'."  , e);
            }
        } else {
            throw new JsonException("Expected JSON object or JSON number, but got " + json.getValueType());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JsonValue toJson() {
        final JsonObjectBuilder builder = JsonUtil.getProvider().createObjectBuilder();
        builder.add("type", "integer");

        if (getDouble() == null) {
            builder.addNull("default");
        } else {
            builder.add("default", getDouble());
        }
        return builder.build();
    }

}
