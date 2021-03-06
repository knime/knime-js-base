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
 *   Jun 12, 2014 (winter): created
 */
package org.knime.js.base.node.quickform.input.string;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.js.base.node.quickform.QuickFormFlowVariableConfig;

/**
 * The config for the string input quick form node.
 *
 * @author Patrick Winter, KNIME AG, Zurich, Switzerland
 */
public class StringInputQuickFormConfig extends QuickFormFlowVariableConfig<StringInputQuickFormValue> {

    /**
     * Single-line editor (text input)
     */
    public static final String EDITOR_TYPE_SINGLE_LINE_STRING = "Single-line";

    /**
     * Multi-line editor (text area)
     */
    public static final String EDITOR_TYPE_MULTI_LINE_STRING = "Multi-line";

    private static final String CFG_REGEX = "regex";
    private static final String DEFAULT_REGEX = "";
    private String m_regex = DEFAULT_REGEX;
    private static final String CFG_ERROR_MESSAGE = "error_message";
    private static final String DEFAULT_ERROR_MESSAGE = "";
    private String m_errorMessage = DEFAULT_ERROR_MESSAGE;

    // added with 3.5

    private static final String CFG_EDITOR_TYPE = "editorType";
    private static final String DEFAULT_EDITOR_TYPE = EDITOR_TYPE_SINGLE_LINE_STRING;
    private String m_editorType = DEFAULT_EDITOR_TYPE;

    private static final String CFG_MULTI_LINE_EDITOR_WIDTH = "multilineEditorWidth";
    private static final int DEFAULT_MULTI_LINE_EDITOR_WIDTH = 60;
    private int m_multilineEditorWidth = DEFAULT_MULTI_LINE_EDITOR_WIDTH;
    private static final String CFG_MULTI_LINE_EDITOR_HEIGHT = "multilineEditorHeight";
    private static final int DEFAULT_MULTI_LINE_EDITOR_HEIGHT = 5;
    private int m_multilineEditorHeight = DEFAULT_MULTI_LINE_EDITOR_HEIGHT;

    /**
     * @return the regex
     */
    String getRegex() {
        return m_regex;
    }

    /**
     * @param regex The regex to set
     */
    void setRegex(final String regex) {
        m_regex = regex;
    }

    /**
     * @return the errorMessage
     */
    String getErrorMessage() {
        return m_errorMessage;
    }

    /**
     * @param errorMessage the errorMessage to set
     */
    void setErrorMessage(final String errorMessage) {
        m_errorMessage = errorMessage;
    }

    /**
     * @return the editorType
     */
    public String getEditorType() {
        return m_editorType;
    }

    /**
     * @param editorType the editorType to set
     */
    public void setEditorType(final String editorType) {
        m_editorType = editorType;
    }

    /**
     * @return the multilineEditorWidth
     */
    public int getMultilineEditorWidth() {
        return m_multilineEditorWidth;
    }

    /**
     * @param multilineEditorWidth the multilineEditorWidth to set
     */
    public void setMultilineEditorWidth(final int multilineEditorWidth) {
        m_multilineEditorWidth = multilineEditorWidth;
    }

    /**
     * @return the multilineEditorHeight
     */
    public int getMultilineEditorHeight() {
        return m_multilineEditorHeight;
    }

    /**
     * @param multilineEditorHeight the multilineEditorHeight to set
     */
    public void setMultilineEditorHeight(final int multilineEditorHeight) {
        m_multilineEditorHeight = multilineEditorHeight;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveSettings(final NodeSettingsWO settings) {
        super.saveSettings(settings);
        settings.addString(CFG_REGEX, m_regex);
        settings.addString(CFG_ERROR_MESSAGE, m_errorMessage);
        // added with 3.5
        settings.addString(CFG_EDITOR_TYPE, m_editorType);
        settings.addInt(CFG_MULTI_LINE_EDITOR_WIDTH, m_multilineEditorWidth);
        settings.addInt(CFG_MULTI_LINE_EDITOR_HEIGHT, m_multilineEditorHeight);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
        super.loadSettings(settings);
        m_regex = settings.getString(CFG_REGEX);
        m_errorMessage = settings.getString(CFG_ERROR_MESSAGE);
        // added with 3.5
        m_editorType = settings.getString(CFG_EDITOR_TYPE, DEFAULT_EDITOR_TYPE);
        m_multilineEditorWidth = settings.getInt(CFG_MULTI_LINE_EDITOR_WIDTH, DEFAULT_MULTI_LINE_EDITOR_WIDTH);
        m_multilineEditorHeight = settings.getInt(CFG_MULTI_LINE_EDITOR_HEIGHT, DEFAULT_MULTI_LINE_EDITOR_HEIGHT);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadSettingsInDialog(final NodeSettingsRO settings) {
        super.loadSettingsInDialog(settings);
        m_regex = settings.getString(CFG_REGEX, DEFAULT_REGEX);
        m_errorMessage = settings.getString(CFG_ERROR_MESSAGE, DEFAULT_ERROR_MESSAGE);
        // added with 3.5
        m_editorType = settings.getString(CFG_EDITOR_TYPE, DEFAULT_EDITOR_TYPE);
        m_multilineEditorWidth = settings.getInt(CFG_MULTI_LINE_EDITOR_WIDTH, DEFAULT_MULTI_LINE_EDITOR_WIDTH);
        m_multilineEditorHeight = settings.getInt(CFG_MULTI_LINE_EDITOR_HEIGHT, DEFAULT_MULTI_LINE_EDITOR_HEIGHT);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected StringInputQuickFormValue createEmptyValue() {
        return new StringInputQuickFormValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString());
        sb.append(", ");
        sb.append("regex=");
        sb.append(m_regex);
        sb.append(", ");
        sb.append("errorMessage=");
        sb.append(m_errorMessage);
        sb.append("editorType=");
        sb.append(m_editorType);
        sb.append("multilineEditorWidth=");
        sb.append(m_multilineEditorWidth);
        sb.append("multilineEditorHeight=");
        sb.append(m_multilineEditorHeight);
        return sb.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder().appendSuper(super.hashCode())
                .append(m_regex)
                .append(m_errorMessage)
                .append(m_editorType)
                .append(m_multilineEditorWidth)
                .append(m_multilineEditorHeight)
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
        StringInputQuickFormConfig other = (StringInputQuickFormConfig)obj;
        return new EqualsBuilder().appendSuper(super.equals(obj))
                .append(m_regex, other.m_regex)
                .append(m_errorMessage, other.m_errorMessage)
                .append(m_editorType, other.m_editorType)
                .append(m_multilineEditorWidth, other.m_multilineEditorWidth)
                .append(m_multilineEditorHeight, other.m_multilineEditorHeight)
                .isEquals();
    }

}
