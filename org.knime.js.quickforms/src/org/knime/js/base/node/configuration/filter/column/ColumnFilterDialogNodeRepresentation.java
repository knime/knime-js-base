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
 *   27 May 2019 (albrecht): created
 */
package org.knime.js.base.node.configuration.filter.column;

import java.util.Arrays;
import java.util.HashSet;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataTableSpec;
import org.knime.core.node.dialog.DialogNodePanel;
import org.knime.core.node.dialog.SubNodeDescriptionProvider;
import org.knime.core.webui.node.dialog.WebDialogNodeRepresentation.DefaultWebDialogNodeRepresentation;
import org.knime.core.webui.node.dialog.defaultdialog.jsonforms.renderers.LocalizedControlRendererSpec;
import org.knime.js.base.node.base.filter.column.ColumnFilterNodeRepresentation;
import org.knime.js.base.node.base.validation.modular.ModularValidatorConfig;
import org.knime.js.base.node.base.validation.modular.ModularValidatorConfigDeserializer;
import org.knime.js.base.node.base.validation.modular.ModularValidatorConfigSerializer;
import org.knime.js.base.node.configuration.filter.column.ColumnFilterDialogNodeModel.Version;
import org.knime.js.base.node.configuration.renderers.TypedStringFilterRenderer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * The dialog representation of the column filter configuration node
 *
 * @author Christian Albrecht, KNIME GmbH, Konstanz, Germany
 */
public class ColumnFilterDialogNodeRepresentation extends ColumnFilterNodeRepresentation<ColumnFilterDialogNodeValue>
    implements SubNodeDescriptionProvider<ColumnFilterDialogNodeValue>,
    DefaultWebDialogNodeRepresentation<ColumnFilterDialogNodeValue> {

    private final DataTableSpec m_spec;

    private final Version m_version;

    private final ModularValidatorConfig m_validatorConfig;

    @JsonCreator
    private ColumnFilterDialogNodeRepresentation(@JsonProperty("label") final String label, //
        @JsonProperty("description") final String description, //
        @JsonProperty("required") final boolean required, //
        @JsonProperty("defaultValue") final ColumnFilterDialogNodeValue defaultValue, //
        @JsonProperty("currentValue") final ColumnFilterDialogNodeValue currentValue, //
        @JsonProperty("possibleColumns") final String[] possibleColumns, //
        @JsonProperty("type") final String type, //
        @JsonProperty("limitNumberVisOptions") final boolean limitNumberVisOptions, //
        @JsonProperty("numberVisOptions") final Integer numberVisOptions, //
        @JsonProperty("spec") @JsonDeserialize(using = DataTableSpecDeserializer.class) final DataTableSpec spec,
        // Jackson simply sets validatorConfig to null if old JSON is parsed that doesn't contain the property yet
        @JsonProperty("validatorConfig") @JsonDeserialize(
            using = ModularValidatorConfigDeserializer.class) final ModularValidatorConfig validatorConfig,
        @JsonProperty("version") final Version version) {
        super(label, description, required, defaultValue, currentValue, possibleColumns, type, limitNumberVisOptions,
            numberVisOptions);
        m_spec = spec;
        m_validatorConfig =
            validatorConfig == null ? ColumnFilterDialogNodeModel.VALIDATOR_FACTORY.createConfig() : validatorConfig;
        m_version = version == null ? Version.PRE_4_1 : version;
    }

    /**
     * @param currentValue The value currently used by the node
     * @param config The config of the node
     * @param spec The current table spec
     * @deprecated as of KNIME AP 4.1.0 use
     *             {@link ColumnFilterDialogNodeRepresentation#ColumnFilterDialogNodeRepresentation(ColumnFilterDialogNodeValue, ColumnFilterDialogNodeConfig, DataTableSpec, Version)}
     *             instead
     */
    @Deprecated
    public ColumnFilterDialogNodeRepresentation(final ColumnFilterDialogNodeValue currentValue,
        final ColumnFilterDialogNodeConfig config, final DataTableSpec spec) {
        this(currentValue, config, spec, Version.PRE_4_1);
    }

    /**
     * @param currentValue The value currently used by the node
     * @param config The config of the node
     * @param spec The current table spec
     * @param version the version of the corresponding Column Filter Configuration node
     */
    public ColumnFilterDialogNodeRepresentation(final ColumnFilterDialogNodeValue currentValue,
        final ColumnFilterDialogNodeConfig config, final DataTableSpec spec, final Version version) {
        super(currentValue, config.getDefaultValue(), config.getColumnFilterConfig(), config.getLabelConfig());
        m_validatorConfig = config.getValidatorConfig();
        m_spec = spec;
        m_version = version;
    }

    /**
     * @return the validator configuration
     */
    @JsonProperty("validatorConfig")
    @JsonSerialize(using = ModularValidatorConfigSerializer.class)
    public ModularValidatorConfig getValidatorConfig() {
        return m_validatorConfig;
    }

    /**
     * @return the version of the corresponding Column Filter Configuration node
     */
    @JsonProperty("version")
    public Version getVersion() {
        return m_version;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DialogNodePanel<ColumnFilterDialogNodeValue> createDialogPanel() {
        return new ColumnFilterConfigurationPanel(this);
    }

    /**
     * @return Last known table spec
     */
    @JsonProperty("spec")
    @JsonSerialize(using = DataTableSpecSerializer.class)
    public DataTableSpec getSpec() {
        return m_spec;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString());
        sb.append(", ");
        sb.append("spec=");
        sb.append(m_spec.toString());
        return sb.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder() //
            .appendSuper(super.hashCode()) //
            .append(m_spec) //
            .append(m_validatorConfig) //
            .append(m_version) //
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
        ColumnFilterDialogNodeRepresentation other = (ColumnFilterDialogNodeRepresentation)obj;
        return new EqualsBuilder() //
            .appendSuper(super.equals(obj)) //
            .append(m_spec, other.m_spec) //
            .append(m_validatorConfig, other.m_validatorConfig) //
            .append(m_version, other.m_version) //
            .isEquals();
    }

    @Override
    public LocalizedControlRendererSpec getWebUIDialogControlSpec() {
        final var possibleColumns = new HashSet<>(Arrays.asList(getPossibleColumns()));
        final var possibleSpecs =
            m_spec.stream().filter(spec -> possibleColumns.contains(spec.getName())).toArray(DataColumnSpec[]::new);
        return new TypedStringFilterRenderer(this, possibleSpecs, isLimitNumberVisOptions(), getNumberVisOptions());
    }
}
