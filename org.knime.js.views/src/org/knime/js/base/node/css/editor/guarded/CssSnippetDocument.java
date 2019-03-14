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
 *   Sep 13, 2018 (daniel): created
 */
package org.knime.js.base.node.css.editor.guarded;

import javax.swing.text.BadLocationException;

import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.knime.rsyntaxtextarea.guarded.GuardedDocument;
import org.knime.rsyntaxtextarea.guarded.GuardedSection;

/**
 *  A document with guarded, non editable sections
 *  @author Daniel Bogenrieder, KNIME GmbH, Konstanz, Germany
 */
@SuppressWarnings("serial")
public class CssSnippetDocument extends GuardedDocument {

    private static final String IMPLEMENTATION_ERROR = "Implementation error.";

    private static final String GUARDED_SECTION_COMMENT_FORMAT =
        "/* Prepended stylesheet from (%s) */ \n%s\n /* End of prepended stylesheet */";

    private GuardedSection m_prependedStyle;

    /**
     * Creates a new guarded CSS document
     */
    public CssSnippetDocument() {
        super(SyntaxConstants.SYNTAX_STYLE_NONE);
    }


    /**
     * @param name Name of the to be created guarded section
     * @param text  The text that is in the guarded section
     * @param flowVariableName Name of the flow variable the text is from
     */
    public void insertNewGuardedSection(final String name, final String text, final String flowVariableName) {
        GuardedSection section = getGuardedSection(name);
        try {
            if (section != null) {
                String tempText = String.format(GUARDED_SECTION_COMMENT_FORMAT, flowVariableName, text);
                section.setText(tempText);
            } else {
                m_prependedStyle = addGuardedSection(name, 0);
                m_prependedStyle.setText(String.format(GUARDED_SECTION_COMMENT_FORMAT, flowVariableName, text));
            }
        } catch (BadLocationException e) {
            throw new IllegalStateException(IMPLEMENTATION_ERROR, e);
        }
    }

    @Override
    public void removeGuardedSection(final String name) {
        if(getGuardedSections().contains(name)) {
            super.removeGuardedSection(name);
            try {
                super.remove(0, m_prependedStyle.getText().length());
            } catch (BadLocationException e) {
                throw new IllegalStateException(IMPLEMENTATION_ERROR, e);
            }
        }
    }
}
