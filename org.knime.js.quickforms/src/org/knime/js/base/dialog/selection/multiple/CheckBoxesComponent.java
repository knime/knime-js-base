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
 * History
 *   Apr 17, 2014 ("Patrick Winter"): created
 */
package org.knime.js.base.dialog.selection.multiple;

import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;

/**
 *
 * @author "Patrick Winter", KNIME AG, Zurich, Switzerland
 */
public class CheckBoxesComponent implements MultipleSelectionsComponent {

    private final JPanel m_panel = new JPanel();

    private List<JCheckBox> m_boxes = new ArrayList<JCheckBox>();

    private final boolean m_vertical;

    /**
     * @param vertical If true the radio buttons are aligned vertically, otherwise they are aligned
     */
    CheckBoxesComponent(final boolean vertical) {
        m_vertical = vertical;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setChoices(final String[] choices) {
        m_panel.removeAll();
        m_boxes.clear();
        int rows = m_vertical ? choices.length : 1;
        int cols = m_vertical ? 1 : choices.length;
        GridLayout layout = new GridLayout(rows, cols);
        m_panel.setLayout(layout);
        for (String choice : choices) {
            JCheckBox box = new JCheckBox(choice);
            m_boxes.add(box);
            m_panel.add(box);
        }
        m_panel.revalidate();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JComponent getComponent() {
        return m_panel;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String[] getSelections() {
        List<String> selections = new ArrayList<String>();
        for (JCheckBox box : m_boxes) {
            if (box.isSelected()) {
                selections.add(box.getText());
            }
        }
        return selections.toArray(new String[selections.size()]);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setSelections(final String[] selections) {
        List<String> selectionList = Arrays.asList(selections);
        for (JCheckBox box : m_boxes) {
            box.setSelected(selectionList.contains(box.getText()));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setEnabled(final boolean enabled) {
        m_panel.setEnabled(enabled);
        for (JCheckBox box : m_boxes) {
            box.setEnabled(enabled);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEnabled() {
        return m_panel.isEnabled();
    }

}
