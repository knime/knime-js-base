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


    ValueFilterDialogNodeValue() {
        // originally, this would be the default initialised value, which is an empty array.
        // "values" needs to be null in order to enable distinction between an
        // empty selection and no selection
        super.setValues(null);
    }

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
     * Ordered list of values to be included by the filter.
     */
    private String[] m_includes = DEFAULT_INCLUDES;

    /**
     * Config key for the included values
     */
    protected static final String CFG_INCLUDES = "includes";

    private static final String[] DEFAULT_INCLUDES = new String[0];

    /**
     * Ordered list of values to be excluded by the filter.
     */
    String[] m_excludes = DEFAULT_EXCLUDES;

    /**
     * Config key for the excluded values
     */
    protected static final String CFG_EXCLUDES = "excludes";

    private static final String[] DEFAULT_EXCLUDES = new String[0];

    /**
     * @param includes An of strings that represents column values. This array will be
     *                 set as the new include list for filtering.
     */
    public void setIncludes(final String[] includes) {
        m_includes = includes;
    }

    /**
     * @return The values to be included by the filter.
     */
    public String[] getIncludes() {
        return m_includes;
    }

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
        Set<String> currentIncludes = new HashSet<String>(Arrays.asList(m_includes));
        Set<String> currentExcludes = new HashSet<String>(Arrays.asList(m_excludes));
        values.stream()
            .filter((val) -> ! currentIncludes.contains(val))
            .filter((val) -> ! currentExcludes.contains(val))
            .forEach((newValue) -> {
                if (m_enforceOption == EnforceOption.EnforceInclusion) { currentExcludes.add(newValue); }
                if (m_enforceOption == EnforceOption.EnforceExclusion) { currentIncludes.add(newValue); }
            });
        m_includes = currentIncludes.toArray(new String[0]);
        m_excludes = currentExcludes.toArray(new String[0]);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveToNodeSettings(final NodeSettingsWO settings) {
        // still need to save `values` (done in super) for backwards compatibility: If a node created
        // before explicit include/exclude lists (using the CFG_VALUES key) is included in the workflow and
        // there was no opportunity yet to update the configuration (can only be done when possible values
        // are known, cf #updateWithOldValues, we do not want to discard this information on save.
        super.saveToNodeSettings(settings);
        settings.addStringArray(CFG_INCLUDES, this.getIncludes());
        settings.addStringArray(CFG_EXCLUDES, this.getExcludes());
        settings.addString(CFG_ENFORCE_OPT, this.getEnforceOption().toString());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadFromNodeSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
        setColumn( settings.getString(CFG_COLUMN) );

        try {
            // see #loadFromNodeSettingsInDialog
            setValues(settings.getStringArray(CFG_VALUES));
        } catch (InvalidSettingsException e) {
            setValues(null);
        }

        // if an old value is set, these will be updated with what is in `values`
        this.setIncludes(settings.getStringArray(CFG_INCLUDES, DEFAULT_INCLUDES));
        this.setExcludes(settings.getStringArray(CFG_EXCLUDES, DEFAULT_EXCLUDES));

        this.setEnforceOption(EnforceOption.parse(
            settings.getString(CFG_ENFORCE_OPT, DEFAULT_ENFORCE_OPT.toString())
        ));
    }


    /**
     * If set, reconcile the field {@link ValueFilterNodeValue#m_values} in this object with include and exclude
     * lists ({@link ValueFilterDialogNodeValue#m_includes }, {@link ValueFilterDialogNodeValue#m_excludes}).
     * This is for backwards compatibility: `Values` used to describe the values that should be included by the
     * filtering. Now, we have explicit include and exclude lists.
     * @param possibleValues All possible values for the currently selected column. A possible value not
     *                       being among {@code this.getValues()} is explicitly excluded and should thus go
     *                       into the exclude list. This is different from being neither included nor
     *                       excluded (in that case, the value is added to either list according to the
     *                       active EnforceOption).
     */
    public void updateWithOldValues(final String[] possibleValues) {
        if (this.getValues() == null || possibleValues == null) return;
        String[] values = this.getValues();
        HashSet<String> inclSet = new HashSet<>(Arrays.asList(values));
        // excludes are all possible values that are not in includes
        // For instance, assume that in an old configuration, `values` only contains `a`. If we did not
        // construct an explicit exclude list here, and EnforceOption is set to EnforceExclusion, all other
        // possible values would be inserted into the include list, which will cause different behaviour.
        String[] excludes = Arrays.asList(possibleValues).stream()
                .filter(e -> !inclSet.contains(e))
                .toArray(String[]::new);
        this.setIncludes(values);
        this.setExcludes(excludes);
        // once includes and excludes are set, we no longer consider the old "values" configuration. To
        // distinguish between no selection and an empty selection, we set this to null.
        this.setValues(null);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "column=" + super.getColumn() + ", "
                + "includes=" + Arrays.toString(this.getIncludes())
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
                .append(this.getIncludes(), other.getIncludes())
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
                .append(this.getIncludes())
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

        // Backwards compat.: `values` used to list the values that should be included.
        // Because, in this case, the exclude list consists of all possible values that are are not in
        // `values`, we still set the field here explicitly and handle it later when we can determine the
        // possible values. See ValueFilterDialogNodeValue#updateWithOldValues.
        try {
            setValues(settings.getStringArray(CFG_VALUES));
        } catch (InvalidSettingsException e) {
            setValues(null); // to discern between absence and empty list
        }
        setIncludes(settings.getStringArray(CFG_INCLUDES, DEFAULT_INCLUDES));
        setExcludes(settings.getStringArray(CFG_EXCLUDES, DEFAULT_EXCLUDES));

        setEnforceOption( EnforceOption.parse(
            // default fallback in case setting cannot be read
            settings.getString(CFG_ENFORCE_OPT, DEFAULT_ENFORCE_OPT.toString()) ,
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
                    // If `CFG_VALUES` is set, use that (backwards compat.)
                    // Previously, the Row Filter Configuration node only considered an include list (the
                    // "values") and had no notion of an explicit exclude list.
                    JsonArray values = ((JsonObject) json).getJsonArray(CFG_VALUES);
                    setIncludes(jsonToStrArr(values));
                } else {
                    JsonArray includes = ((JsonObject) json).getJsonArray(CFG_INCLUDES);
                    JsonArray excludes = ((JsonObject) json).getJsonArray(CFG_EXCLUDES);
                    setIncludes(jsonToStrArr(includes));
                    setExcludes(jsonToStrArr(excludes));
                }
            } catch (Exception e) {
                throw new JsonException("Expected valid string array for keys " + CFG_INCLUDES + ", "
                    + CFG_EXCLUDES + ", or " + CFG_VALUES + ".", e);
            }

            try {
                JsonValue enforceOption = ((JsonObject) json).get(CFG_ENFORCE_OPT);
                if (!JsonValue.NULL.equals(enforceOption)) {
                    setEnforceOption(EnforceOption.parse(enforceOption.toString(), DEFAULT_ENFORCE_OPT));
                } else {
                    // backwards compatibility: if no such option is set, use a default.
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

        builder.add(CFG_INCLUDES, strArrToJson(this.getIncludes()));
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
