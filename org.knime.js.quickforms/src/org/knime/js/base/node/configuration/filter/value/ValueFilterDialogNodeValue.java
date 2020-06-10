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

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.dialog.DialogNodeValue;
import org.knime.core.node.util.filter.NameFilterConfiguration.EnforceOption;
import org.knime.js.base.node.base.filter.value.ValueFilterNodeValue;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * The value for the value filter configuration node
 *
 * @author Christian Albrecht, KNIME GmbH, Konstanz, Germany
 */
public class ValueFilterDialogNodeValue extends ValueFilterNodeValue implements DialogNodeValue {

    protected static final String CFG_ENFORCE_OPT = "enforce-option";
    public static final EnforceOption DEFAULT_ENFORCE_OPT = EnforceOption.EnforceInclusion;
    private EnforceOption m_enforceOption = DEFAULT_ENFORCE_OPT;

    /**
     * @return the currently active enforce inclusion/exclusion policy
     */
    public EnforceOption getEnforceOption() {
        return m_enforceOption;
    }

    /**
     * @param opt the new active enforce inclusion/exclusion policy
     */
    public void setEnforceOption(final EnforceOption opt) {
        m_enforceOption = opt;
    }

    /**
     * Ordered list of values to be excluded by the filter. The corresponding include list is given by
     * {@link ValueFilterNodeValue#m_values}.
     */
    String[] m_excludes = DEFAULT_EXCLUDES;

    /**
     * Config key for the excluded values
     */
    protected static final String CFG_EXCLUDES = "excludes";

    private static final String[] DEFAULT_EXCLUDES = new String[0];

    /**
     * @param list the excludes to set
     */
    public void setExcludes(final String[] list) {
        m_excludes = list;
    }

    /**
     * @return The values to be excluded by the filter.
     */
    public String[] getExcludes() {
        return m_excludes;
    }

    /**
     * Updates include and exclude lists with the given range of possible values.
     * @param values Values that, if not already contained in one of the lists, will be added to either
     *  include or exclude list according to the currently set EnforceOption
     */
    public void updateInclExcl(final List<String> values) {
        Set<String> currentIncludes = new HashSet<String>(Arrays.asList(m_values));
        Set<String> currentExcludes = new HashSet<String>(Arrays.asList(m_excludes));
        values.stream()
            .filter((val) -> ! currentIncludes.contains(val))
            .filter((val) -> ! currentExcludes.contains(val))
            .forEach((newValue) -> {
                if (m_enforceOption == EnforceOption.EnforceInclusion) { currentExcludes.add(newValue); }
                if (m_enforceOption == EnforceOption.EnforceExclusion) { currentIncludes.add(newValue); }
            });
        m_values = currentIncludes.toArray(new String[0]);
        m_excludes = currentExcludes.toArray(new String[0]);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveToNodeSettings(final NodeSettingsWO settings) {
        super.saveToNodeSettings(settings);
        settings.addStringArray(CFG_EXCLUDES, this.getExcludes());
        settings.addString(CFG_ENFORCE_OPT, this.getEnforceOption().toString());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadFromNodeSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
        super.loadFromNodeSettings(settings);
        setExcludes(settings.getStringArray(CFG_EXCLUDES, DEFAULT_EXCLUDES));
        setEnforceOption(EnforceOption.parse(settings.getString(CFG_ENFORCE_OPT, DEFAULT_ENFORCE_OPT.toString())));
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "column=" + super.getColumn() + ", "
                + "includes=" + Arrays.toString(this.getValues())
                + ", "
                + "excludes=" + Arrays.toString(this.getExcludes())
                + ", "
                + "enforce="
                + (this.getEnforceOption()==EnforceOption.EnforceInclusion ? "inclusion" : "exclusion");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object obj) {
        if (super.equals(obj) == false) {
            return false;
        }
        // still doing null and this checks in case super implementation changes
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj.getClass() != this.getClass()) {
            return false;
        }
        ValueFilterDialogNodeValue other = (ValueFilterDialogNodeValue)obj;
        return new EqualsBuilder()
                .append(this.getExcludes(), other.getExcludes())
                .append(this.getEnforceOption(), other.getEnforceOption())
                .isEquals();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(super.getColumn())
                .append(super.getValues())
                .append(this.getExcludes())
                .append(this.getEnforceOption().toString())
                .toHashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @JsonIgnore
    public void loadFromNodeSettingsInDialog(final NodeSettingsRO settings) {
        setColumn(settings.getString(CFG_COLUMN, DEFAULT_COLUMN));
        setValues(settings.getStringArray(CFG_VALUES, DEFAULT_VALUES));
        setExcludes(settings.getStringArray(CFG_EXCLUDES, DEFAULT_EXCLUDES));
        setEnforceOption( EnforceOption.parse(
            // default fallback in case setting cannot be read
            settings.getString(CFG_ENFORCE_OPT, DEFAULT_ENFORCE_OPT.toString()),
            // default fallback in case read setting cannot be parsed
            DEFAULT_ENFORCE_OPT)
            );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @JsonIgnore
    public void loadFromString(final String fromCmdLine) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Parameterization of Value Filter not supported!");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @JsonIgnore
    public void loadFromJson(final JsonValue json) throws JsonException {
        if (json instanceof JsonObject) {
            try {
                JsonValue val = ((JsonObject) json).get(CFG_COLUMN);
                if (JsonValue.NULL.equals(val)) {
                    setColumn(null);
                } else {
                    setColumn(((JsonObject) json).getString(CFG_COLUMN));
                }
            } catch (Exception e) {
                throw new JsonException("Expected string value for key '" + CFG_COLUMN + ".", e);
            }

            try {
                JsonValue val = ((JsonObject) json).get(CFG_VALUES);
                if (!JsonValue.NULL.equals(val)) {
                    JsonArray values = ((JsonObject) json).getJsonArray(CFG_VALUES);
                    setValues(jsonToStrArr(values));
                } else {
                    setValues(null);
                }
            } catch (Exception e) {
                throw new JsonException("Expected valid string array for key " + CFG_VALUES + ".", e);
            }

            try {
                JsonArray excludes = ((JsonObject) json).getJsonArray(CFG_EXCLUDES);
                if (!JsonValue.NULL.equals(excludes)) {
                    setExcludes(jsonToStrArr(excludes));
                } else {
                    setExcludes(null);
                }
            } catch (Exception e) {
                throw new JsonException("Expected valid string array for keys " + CFG_EXCLUDES + ".", e);
            }

            try {
                JsonValue enforceOption = ((JsonObject) json).get(CFG_ENFORCE_OPT);
                if (!JsonValue.NULL.equals(enforceOption)) {
                    setEnforceOption(EnforceOption.parse(enforceOption.toString(), DEFAULT_ENFORCE_OPT));
                } else {
                    setEnforceOption(DEFAULT_ENFORCE_OPT);
                }
            } catch (Exception e) {
                throw new JsonException("Expected valid string for key " + CFG_ENFORCE_OPT + ".");
            }

        } else {
            throw new JsonException("Expected JSON object, but got " + json.getValueType());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @JsonIgnore
    public JsonValue toJson() {
        final JsonObjectBuilder builder = Json.createObjectBuilder();
        final JsonObjectBuilder subBuilder = Json.createObjectBuilder();
        builder.add("type", "object");

        builder.add(CFG_COLUMN, createStringTypeBuilder(this.getColumn()).build());

        builder.add(CFG_ENFORCE_OPT, createStringTypeBuilder(this.getEnforceOption().toString()).build());

        builder.add(CFG_VALUES, strArrToJson(this.getValues()));
        builder.add(CFG_EXCLUDES, strArrToJson(this.getExcludes()));
        return builder.build();
    }

    private static JsonObjectBuilder createStringTypeBuilder(final String property) {
        final JsonObjectBuilder builder = Json.createObjectBuilder();
        builder.add("type", "string");
        if (property == null) {
            builder.addNull("default");
        } else {
            builder.add("default", property);
        }
        return builder;
    }

    private static String[] jsonToStrArr(final JsonArray array) {
        String[] strings = new String[array.size()];
        for (int i=0; i < array.size(); i++) {
            strings[i] = array.getString(i);
        }
        return strings;
    }

    private static JsonValue strArrToJson(final String[] strings) {
        final JsonObjectBuilder builder = Json.createObjectBuilder();
        builder.add("type", "array");
        if (strings == null) {
            builder.addNull("default");
        } else {
            JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
            for (String value : strings) {
                arrayBuilder.add(value);
            }
            JsonObjectBuilder itemBuilder = Json.createObjectBuilder();
            itemBuilder.add("type", "string");
            builder.add("items", itemBuilder);
            builder.add("default", arrayBuilder);
        }
        return builder.build();
    }

}
