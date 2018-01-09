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
 *
 */
package org.knime.quickform.nodes.out.textarea;

import java.util.NoSuchElementException;

import org.knime.base.util.flowvariable.FlowVariableProvider;
import org.knime.base.util.flowvariable.FlowVariableResolver;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.util.node.quickform.out.TextAreaOutputQuickFormOutElement.TextFormat;
import org.knime.quickform.nodes.out.QuickFormOutConfiguration;

/**
 * Base configuration for node providing a text to the
 * quickform result.
 *
 * @author Bernd Wiswedel, KNIME AG, Zurich, Switzerland
 */
public class TextAreaOutputQuickFormOutConfiguration extends QuickFormOutConfiguration {

    private static final String CFG_TEXT = "text";
    private static final String CFG_FORMAT = "textFormat";

    private String m_text;
    private TextFormat m_textFormat = TextFormat.Text;

    /** @return the text */
    public String getText() {
        return m_text;
    }

    /** @param text the text to set */
    public void setText(final String text) {
        m_text = text;
    }

    /**
     * @param textFormat the format to set
     */
    public void setTextFormat(final TextFormat textFormat) {
        m_textFormat = textFormat == null ? TextFormat.Text : textFormat;
    }

    /**
     * @return the format (not null).
     */
    public TextFormat getTextFormat() {
        return m_textFormat;
    }

    /**
     * Returns final text (flow vars replaced).
     * @param provider the node model.
     * @return ...
     * @throws InvalidSettingsException if flow var is not available.
     */
    String parseTextAndReplaceVariables(final FlowVariableProvider provider) throws InvalidSettingsException {
        String flowVarCorrectedText;
        try {
            flowVarCorrectedText = FlowVariableResolver.parse(m_text, provider);
        } catch (NoSuchElementException nse) {
            throw new InvalidSettingsException(nse.getMessage(), nse);
        }
        return flowVarCorrectedText;
    }

    /** {@inheritDoc} */
    @Override
    public void saveSettingsTo(final NodeSettingsWO settings) {
        super.saveSettingsTo(settings);
        settings.addString(CFG_TEXT, m_text);
        settings.addString(CFG_FORMAT, m_textFormat.toString());
    }

    /** {@inheritDoc} */
    @Override
    public void loadSettingsInDialog(final NodeSettingsRO settings) {
        super.loadSettingsInDialog(settings);
        m_text = settings.getString(CFG_TEXT, "");
        try {
            m_textFormat = TextFormat.valueOf(settings.getString(CFG_FORMAT, TextFormat.Text.toString()));
        } catch (Exception e) {
            m_textFormat = TextFormat.Text;
        }
    }

    /** {@inheritDoc} */
    @Override
    public void loadSettingsInModel(final NodeSettingsRO settings)
            throws InvalidSettingsException {
        super.loadSettingsInModel(settings);
        m_text = settings.getString(CFG_TEXT);
        try {
            m_textFormat = TextFormat.valueOf(settings.getString(CFG_FORMAT));
        } catch (Exception e) {
            throw new InvalidSettingsException("Couldn't parse text format", e);
        }
    }

}
