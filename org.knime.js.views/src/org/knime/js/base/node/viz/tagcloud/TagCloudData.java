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
 *   6 Oct 2017 (albrecht): created
 */
package org.knime.js.base.node.viz.tagcloud;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 *
 * @author Christian Albrecht, KNIME GmbH, Konstanz, Germany
 */
@JsonAutoDetect
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public class TagCloudData {

    private static final String CFG_ID = "id";
    private String m_id;

    private static final String CFG_ROW_IDS = "rowIDs";
    private String[] m_rowIDs;

    private static final String CFG_TEXT = "text";
    private String m_text;

    private static final String CFG_COLOR = "color";
    private String m_color;

    private static final String CFG_SIZE = "size";
    private double m_size;

    private static final String CFG_OPACITY = "opacity";
    private double m_opacity;

    /**
     * @return the id
     */
    public String getId() {
        return m_id;
    }

    /**
     * @param id the id to set
     */
    public void setId(final String id) {
        m_id = id;
    }

    /**
     * @return the rowIDs
     */
    public String[] getRowIDs() {
        return m_rowIDs;
    }

    /**
     * @param rowIDs the rowIDs to set
     */
    public void setRowIDs(final String[] rowIDs) {
        m_rowIDs = rowIDs;
    }

    /**
     * @return the text
     */
    public String getText() {
        return m_text;
    }

    /**
     * @param text the text to set
     */
    public void setText(final String text) {
        m_text = text;
    }

    /**
     * @return the color
     */
    public String getColor() {
        return m_color;
    }

    /**
     * @param color the color to set
     */
    public void setColor(final String color) {
        m_color = color;
    }

    /**
     * @return the size
     */
    public double getSize() {
        return m_size;
    }

    /**
     * @param size the size to set
     */
    public void setSize(final double size) {
        m_size = size;
    }

    /**
     * @return the opacity
     */
    public double getOpacity() {
        return m_opacity;
    }

    /**
     * @param opacity the opacity to set
     */
    public void setOpacity(final double opacity) {
        m_opacity = opacity;
    }

    void saveToNodeSettings(final NodeSettingsWO settings) {
        settings.addString(CFG_ID, m_id);
        settings.addStringArray(CFG_ROW_IDS, m_rowIDs);
        settings.addString(CFG_TEXT, m_text);
        settings.addString(CFG_COLOR, m_color);
        settings.addDouble(CFG_SIZE, m_size);
        settings.addDouble(CFG_OPACITY, m_opacity);
    }

    void loadFromNodeSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
        m_rowIDs = settings.getStringArray(CFG_ROW_IDS);
        m_text = settings.getString(CFG_TEXT);
        m_color = settings.getString(CFG_COLOR);
        m_size = settings.getDouble(CFG_SIZE);
        m_opacity = settings.getDouble(CFG_OPACITY);

        //added with 3.5.2
        m_id = settings.getString(CFG_ID, null);
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
        TagCloudData other = (TagCloudData)obj;
        return new EqualsBuilder()
                .append(m_id, other.m_id)
                .append(m_rowIDs, other.m_rowIDs)
                .append(m_text, other.m_text)
                .append(m_color, other.m_color)
                .append(m_size, other.m_size)
                .append(m_opacity, other.m_opacity)
                .isEquals();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(m_id)
                .append(m_rowIDs)
                .append(m_text)
                .append(m_color)
                .append(m_size)
                .append(m_opacity)
                .toHashCode();
    }
}
