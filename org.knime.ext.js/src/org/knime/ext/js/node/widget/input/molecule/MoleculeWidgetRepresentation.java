/*
 * ------------------------------------------------------------------------
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
 * ------------------------------------------------------------------------
 */
package org.knime.ext.js.node.widget.input.molecule;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.knime.js.base.node.base.LabeledConfig;
import org.knime.js.base.node.base.LabeledNodeRepresentation;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * The representation for the molecule string input quick form node.
 *
 * @author Daniel Bogenrieder, KNIME AG, Zurich, Switzerland
 */
@JsonAutoDetect
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public class MoleculeWidgetRepresentation<VAL extends MoleculeWidgetValue> extends
    LabeledNodeRepresentation<MoleculeWidgetValue> {
    
    @JsonCreator
    private MoleculeWidgetRepresentation(@JsonProperty("label") final String label,
        @JsonProperty("description") final String description, @JsonProperty("required") final boolean required,
        @JsonProperty("defaultValue") final MoleculeWidgetValue defaultValue,
        @JsonProperty("currentValue") final MoleculeWidgetValue currentValue,
        @JsonProperty("sketcherLocation") String sketcherLocation, @JsonProperty("format") String format,
        @JsonProperty("sketcherPath") String sketcherPath,
        @JsonProperty("disableLineNotification") boolean disableLineNotifications) {
        super(label, description, required, defaultValue, currentValue);
        m_sketcherLocation = sketcherLocation;
        m_sketcherPath = sketcherPath;
        m_format = format;
        m_disableLineNotification = disableLineNotifications;
    }

    /**
     * @param currentValue The value currently used by the node
     * @param config The config of the node
     */
    public MoleculeWidgetRepresentation(final MoleculeWidgetValue currentValue, final MoleculeWidgetValue defaultValue,
        final MoleculeWidgetConfig config, final LabeledConfig labelConfig) {
        super(currentValue, defaultValue, labelConfig);
        m_format = config.getFormat();
        m_sketcherPath = config.getSketcherPath();
        m_disableLineNotification = config.isDisableLineNotifications();
    }

    private final String m_format;
    private String m_sketcherLocation;
    private final String m_sketcherPath;
    private final boolean m_disableLineNotification;

    /**
     * @return the sketcherLocation (injected by server)
     */
    @JsonProperty("sketcherLocation")
    public String getSketcherLocation() {
        return m_sketcherLocation;
    }

    /**
     * @param sketcherLocation the sketcherLocation to set (injected by server)
     */
    @JsonProperty("sketcherLocation")
    public void setSketcherLocation(final String sketcherLocation) {
        m_sketcherLocation = sketcherLocation;
    }
    
    /**
     * @return the format
     */
    @JsonProperty("format")
    public String getFormat() {
        return m_format;
    }
    
    /**
     * @return the optional sketcher location defined in node config
     */
    @JsonProperty("sketcherPath")
    public String getSketcherPath() {
		return m_sketcherPath;
	}

    /**
     * @return the format
     */
    @JsonProperty("disableLineNotification")
    public boolean isDisableLineNotification() {
        return m_disableLineNotification;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString());
        sb.append(", ");
        sb.append("format=");
        sb.append(m_format);
        sb.append(m_disableLineNotification);
        return sb.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder().appendSuper(super.hashCode())
                .append(m_format)
                .append(m_sketcherLocation)
                .append(m_sketcherPath)
                .append(m_disableLineNotification)
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
        MoleculeWidgetRepresentation other = (MoleculeWidgetRepresentation)obj;
        return new EqualsBuilder().appendSuper(super.equals(obj))
                .append(m_format, other.m_format)
                .append(m_sketcherLocation, other.m_sketcherLocation)
                .append(m_sketcherPath, other.m_sketcherPath)
                .append(m_disableLineNotification, other.m_disableLineNotification)
                .isEquals();
    }

}
