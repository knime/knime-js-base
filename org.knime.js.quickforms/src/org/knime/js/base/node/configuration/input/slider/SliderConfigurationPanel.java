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
 *   May 24, 2019 (Daniel Bogenrieder): created
 */
package org.knime.js.base.node.configuration.input.slider;

import javax.swing.BoundedRangeModel;
import javax.swing.GroupLayout;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.knime.core.node.InvalidSettingsException;
import org.knime.js.base.node.configuration.AbstractDialogNodeConfigurationPanel;

/**
 * The component dialog panel for the slider configuration node
 *
 * @author Daniel Bogenrieder, KNIME GmbH, Konstanz, Germany
 */
@SuppressWarnings("serial")
public class SliderConfigurationPanel extends AbstractDialogNodeConfigurationPanel<SliderDialogNodeValue> {

    private final JSlider m_component;
    private final JTextArea m_component_text;
    private final JPanel m_panel;
    private final GroupLayout layout;
    /**
     * @param representation the dialog node settings
     */
    public SliderConfigurationPanel(final SliderDialogNodeRepresentation representation) {
        super(representation.getLabel(), representation.getDescription(), representation.getDefaultValue());
        m_panel = new JPanel();
        layout = new GroupLayout(m_panel);
        double min = 0;
        double max = 100;
        if(representation.isUseCustomMin()) {
            min = representation.getCustomMin();
        }
        if(representation.isUseCustomMax()) {
            max = representation.getCustomMax();
        }
        m_component_text = new JTextArea();
        m_component_text.setText(representation.getDefaultValue().getDouble().toString());
        m_component = new JSlider();
        m_component.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(final ChangeEvent e) {
                m_component_text.setText(((Integer)m_component.getValue()).toString());
            }
        });
        BoundedRangeModel sliderModel = m_component.getModel();
        sliderModel.setMaximum((int)max);
        sliderModel.setMinimum((int)min);
        m_component.setPaintTicks(true);

        m_component.setPaintLabels(true);
        // m_component.setLabelTable(m_component.createStandardLabels(2));
        // m_component.setPreferredSize(new JTextField(DialogUtil.DEF_TEXTFIELD_WIDTH).getPreferredSize());
        m_component.setValue(representation.getDefaultValue().getDouble().intValue());
        layout.setHorizontalGroup(
            layout.createSequentialGroup()
               .addComponent(m_component)
               .addComponent(m_component_text)
         );
        setComponent(m_panel);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void resetToDefault() {
        m_component.setValue(getDefaultValue().getDouble().intValue());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected SliderDialogNodeValue createNodeValue() throws InvalidSettingsException {
        SliderDialogNodeValue value = new SliderDialogNodeValue();
        value.setDouble((double)m_component.getValue());
        return value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadNodeValue(final SliderDialogNodeValue value) {
        super.loadNodeValue(value);
        if (value != null) {
            m_component.setValue(value.getDouble().intValue());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setEnabled(final boolean enabled) {
        super.setEnabled(enabled);
        m_component.setEnabled(enabled);
    }

}
