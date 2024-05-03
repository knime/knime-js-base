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
package org.knime.js.base.node.widget.output.text;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.js.base.util.LabeledViewConfig;

/**
 *
 * @author Christian Albrecht, KNIME GmbH, Konstanz, Germany
 */
public class TextOutputWidgetConfig extends LabeledViewConfig {

    /** Format as shown in the web page. */
    public enum OutputTextFormat {
        /** Ordinary text. */
        Text,
        /** Preformatted text (respects line breaks). */
        Preformatted,
        /** Text w/ html tags. */
        Html;
    }

    static final String CFG_TEXT = "text";
    private static final String DEFAULT_TEXT = "";
    private String m_text = DEFAULT_TEXT;

    private static final String CFG_FORMAT = "textFormat";
    private static final OutputTextFormat DEFAULT_FORMAT = OutputTextFormat.Text;
    private OutputTextFormat m_textFormat = DEFAULT_FORMAT;

    private static final String CFG_SANITIZE_INPUT = "sanitizeInput";
    private static final boolean DEFAULT_SANITIZE_INPUT = false;
    private boolean m_sanitizeInput = DEFAULT_SANITIZE_INPUT;

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
     * @return the textFormat
     */
    public OutputTextFormat getTextFormat() {
        return m_textFormat;
    }

    /**
     * @param textFormat the textFormat to set
     */
    public void setTextFormat(final OutputTextFormat textFormat) {
        m_textFormat = textFormat;
    }

    /**
     * @return the sanitizeInput
     */
    public boolean isSanitizeInput() {
        return m_sanitizeInput;
    }

    /**
     * @param sanitizeInput the sanitizeInput to set
     */
    public void setSanitizeInput(final boolean sanitizeInput) {
        m_sanitizeInput = sanitizeInput;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveSettings(final NodeSettingsWO settings) {
        super.saveSettings(settings);
        settings.addString(CFG_FORMAT, m_textFormat.toString());
        settings.addString(CFG_TEXT, m_text);
        settings.addBoolean(CFG_SANITIZE_INPUT, m_sanitizeInput);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
        super.loadSettings(settings);
        m_textFormat = OutputTextFormat.valueOf(settings.getString(CFG_FORMAT));
        m_text = settings.getString(CFG_TEXT);

        // added with 5.2
        m_sanitizeInput = settings.getBoolean(CFG_SANITIZE_INPUT, DEFAULT_SANITIZE_INPUT);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadSettingsInDialog(final NodeSettingsRO settings) {
        super.loadSettingsInDialog(settings);
        try {
            m_textFormat = OutputTextFormat.valueOf(settings.getString(CFG_FORMAT, DEFAULT_FORMAT.toString()));
        } catch (Exception e) {
            m_textFormat = DEFAULT_FORMAT;
        }
        m_text = settings.getString(CFG_TEXT, DEFAULT_TEXT);

        // added with 5.2
        m_sanitizeInput = settings.getBoolean(CFG_SANITIZE_INPUT, DEFAULT_SANITIZE_INPUT);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString());
        sb.append(", format=");
        sb.append(m_textFormat.toString());
        return sb.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .appendSuper(super.hashCode())
                .append(m_textFormat)
                .append(m_text)
                .append(m_sanitizeInput)
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
        TextOutputWidgetConfig other = (TextOutputWidgetConfig)obj;
        return new EqualsBuilder()
                .appendSuper(super.equals(obj))
                .append(m_textFormat, other.m_textFormat)
                .append(m_text, other.m_text)
                .append(m_sanitizeInput, other.m_sanitizeInput)
                .isEquals();
    }

}
