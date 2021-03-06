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
 *   Sep 17, 2020 (Christian Albrecht, KNIME GmbH, Konstanz, Germany): created
 */
package org.knime.js.base.node.widget.output.image;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.js.core.JSONViewContent;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 *
 * @author Christian Albrecht, KNIME GmbH, Konstanz, Germany
 */
@JsonAutoDetect
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public class ImageOutputWidgetRepresentation extends JSONViewContent {

    private static final String DEFAULT_STRING = "";
    private static final String CFG_LABEL = "label";
    private String m_label;

    private static final String CFG_DESCRIPTION = "description";
    private String m_description;

    private int m_maxWidth;
    private int m_maxHeight;

    private static final String SETTINGS_FORMAT = "imageFormat";
    private String m_imageFormat = DEFAULT_STRING;

    private static final String SETTINGS_DATA = "imageData";
    private String m_imageData = DEFAULT_STRING;

    /**
     * @return the label
     */
    @JsonProperty("label")
    public String getLabel() {
        return m_label;
    }

    /**
     * @param label the label to set
     */
    public void setLabel(final String label) {
        m_label = label;
    }

    /**
     * @return the description
     */
    @JsonProperty("description")
    public String getDescription() {
        return m_description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(final String description) {
        m_description = description;
    }

    /**
     * @return the maxWidth
     */
    public int getMaxWidth() {
        return m_maxWidth;
    }

    /**
     * @param maxWidth the maxWidth to set
     */
    public void setMaxWidth(final int maxWidth) {
        m_maxWidth = maxWidth;
    }

    /**
     * @return the maxHeight
     */
    public int getMaxHeight() {
        return m_maxHeight;
    }

    /**
     * @param maxHeight the maxHeight to set
     */
    public void setMaxHeight(final int maxHeight) {
        m_maxHeight = maxHeight;
    }

    /**
     * @return the imageFormat
     */
    public String getImageFormat() {
        return m_imageFormat;
    }

    /**
     * @param imageFormat the imageFormat to set
     */
    public void setImageFormat(final String imageFormat) {
        m_imageFormat = imageFormat;
    }

    /**
     * @return the imageData
     */
    public String getImageData() {
        return m_imageData;
    }

    /**
     * @param imageData the imageData to set
     */
    public void setImageData(final String imageData) {
        m_imageData = imageData;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveToNodeSettings(final NodeSettingsWO settings) {
        settings.addString(CFG_LABEL, m_label);
        settings.addString(CFG_DESCRIPTION, m_description);
        settings.addInt(ImageOutputWidgetConfig.CFG_MAX_WIDTH, m_maxWidth);
        settings.addInt(ImageOutputWidgetConfig.CFG_MAX_HEIGHT, m_maxHeight);
        settings.addString(SETTINGS_FORMAT, m_imageFormat);
        settings.addString(SETTINGS_DATA, m_imageData);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadFromNodeSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
        m_imageFormat = settings.getString(SETTINGS_FORMAT);
        m_imageData = settings.getString(SETTINGS_DATA);

        //added later, load with default for backwards compatibility
        m_label = settings.getString(CFG_LABEL, DEFAULT_STRING);
        m_description = settings.getString(CFG_DESCRIPTION, DEFAULT_STRING);
        m_maxWidth = settings.getInt(ImageOutputWidgetConfig.CFG_MAX_WIDTH, ImageOutputWidgetConfig.DEFAULT_MAX_WIDTH);
        m_maxHeight = settings.getInt(ImageOutputWidgetConfig.CFG_MAX_HEIGHT, ImageOutputWidgetConfig.DEFAULT_MAX_HEIGHT);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(ImageOutputWidgetRepresentation.class);
        sb.append(", label=");
        sb.append(m_label);
        sb.append(", description=");
        sb.append(m_description);
        sb.append(", maxWidth=");
        sb.append(m_maxWidth);
        sb.append(", maxHeight=");
        sb.append(m_maxHeight);
        sb.append(", format=");
        sb.append(m_imageFormat);
        return sb.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder()
            .append(m_label)
            .append(m_description)
            .append(m_maxWidth)
            .append(m_maxHeight)
            .append(m_imageFormat)
            .append(m_imageData)
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
        ImageOutputWidgetRepresentation other = (ImageOutputWidgetRepresentation)obj;
        return new EqualsBuilder()
            .append(m_label, other.m_label)
            .append(m_description, other.m_description)
            .append(m_maxWidth, other.m_maxWidth)
            .append(m_maxHeight, other.m_maxHeight)
            .append(m_imageFormat, other.m_imageFormat)
            .append(m_imageData, other.m_imageData)
            .isEquals();
    }

}
