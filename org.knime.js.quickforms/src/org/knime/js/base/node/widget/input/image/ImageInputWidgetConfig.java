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
 *   22 May 2019 (albrecht): created
 */
package org.knime.js.base.node.widget.input.image;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.js.base.node.base.input.string.StringNodeConfig;
import org.knime.js.base.node.base.input.string.StringNodeValue;
import org.knime.js.base.node.widget.LabeledFlowVariableWidgetConfig;

/**
 * The config for the string widget node
 *
 * @author Christian Albrecht, KNIME GmbH, Konstanz, Germany
 */
public class ImageInputWidgetConfig extends LabeledFlowVariableWidgetConfig<StringNodeValue> {

    private final StringNodeConfig m_stringConfig;

    /**
     * Instantiate a new config object
     */
    public ImageInputWidgetConfig() {
        m_stringConfig = new StringNodeConfig();
    }

    /**
     * @return the stringConfig
     */
    public StringNodeConfig getStringConfig() {
        return m_stringConfig;
    }

    /**
     * @return the regex
     */
    String getRegex() {
        return m_stringConfig.getRegex();
    }

    /**
     * @param regex The regex to set
     */
    void setRegex(final String regex) {
        m_stringConfig.setRegex(regex);
    }

    /**
     * @return the errorMessage
     */
    String getErrorMessage() {
        return m_stringConfig.getErrorMessage();
    }

    /**
     * @param errorMessage the errorMessage to set
     */
    void setErrorMessage(final String errorMessage) {
        m_stringConfig.setErrorMessage(errorMessage);
    }

    /**
     * @return the editorType
     */
    public String getEditorType() {
        return m_stringConfig.getEditorType();
    }

    /**
     * @param editorType the editorType to set
     */
    public void setEditorType(final String editorType) {
        m_stringConfig.setEditorType(editorType);
    }

    /**
     * @return the multilineEditorWidth
     */
    public int getMultilineEditorWidth() {
        return m_stringConfig.getMultilineEditorWidth();
    }

    /**
     * @param multilineEditorWidth the multilineEditorWidth to set
     */
    public void setMultilineEditorWidth(final int multilineEditorWidth) {
        m_stringConfig.setMultilineEditorWidth(multilineEditorWidth);
    }

    /**
     * @return the multilineEditorHeight
     */
    public int getMultilineEditorHeight() {
        return m_stringConfig.getMultilineEditorHeight();
    }

    /**
     * @param multilineEditorHeight the multilineEditorHeight to set
     */
    public void setMultilineEditorHeight(final int multilineEditorHeight) {
        m_stringConfig.setMultilineEditorHeight(multilineEditorHeight);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected StringNodeValue createEmptyValue() {
        return new StringNodeValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveSettings(final NodeSettingsWO settings) {
        super.saveSettings(settings);
        m_stringConfig.saveSettings(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
        super.loadSettings(settings);
        m_stringConfig.loadSettings(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadSettingsInDialog(final NodeSettingsRO settings) {
        super.loadSettingsInDialog(settings);
        m_stringConfig.loadSettingsInDialog(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString());
        sb.append(", ");
        sb.append(m_stringConfig.toString());
        return sb.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder().appendSuper(super.hashCode())
                .append(m_stringConfig)
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
        ImageInputWidgetConfig other = (ImageInputWidgetConfig)obj;
        return new EqualsBuilder().appendSuper(super.equals(obj))
                .append(m_stringConfig, other.m_stringConfig)
                .isEquals();
    }

}
