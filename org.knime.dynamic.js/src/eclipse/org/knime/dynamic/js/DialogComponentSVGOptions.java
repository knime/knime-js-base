/*
 * ------------------------------------------------------------------------
 *
 *  Copyright by KNIME GmbH, Konstanz, Germany
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
 *  KNIME and ECLIPSE being a combined program, KNIME GMBH herewith grants
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
 *   May 18, 2015 (Christian Albrecht): created
 */
package org.knime.dynamic.js;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NotConfigurableException;
import org.knime.core.node.defaultnodesettings.DialogComponent;
import org.knime.core.node.port.PortObjectSpec;

/**
 *
 * @author Christian Albrecht, KNIME.com AG, Zurich, Switzerland
 */
public class DialogComponentSVGOptions extends DialogComponent {

    private final JLabel m_widthLabel;
    private final JLabel m_heightLabel;
    private final JSpinner m_widthSpinner;
    private final JSpinner m_heightSpinner;
    private final JCheckBox m_allowFullscreenCheckBox;
    private final boolean m_showFullscreenOption;

    /**
     * @param model
     * @param label
     */
    public DialogComponentSVGOptions(final SettingsModelSVGOptions model, final String label) {
        super(model);
        JPanel panel = getComponentPanel();
        m_widthLabel = new JLabel("Image width");
        m_widthLabel.setPreferredSize(new Dimension(100, 22));
        m_heightLabel = new JLabel("Image height");
        m_heightLabel.setPreferredSize(new Dimension(100, 22));
        m_widthSpinner = new JSpinner(new SpinnerNumberModel(0, 0, null, 1));
        m_widthSpinner.setMaximumSize(new Dimension(100, 22));
        JSpinner.DefaultEditor editor =
                (JSpinner.DefaultEditor)m_widthSpinner.getEditor();
        editor.getTextField().setColumns(7);
        editor.getTextField().setFocusLostBehavior(JFormattedTextField.COMMIT);
        m_widthSpinner.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(final ChangeEvent e) {
                updateModel();
            }
        });
        m_heightSpinner = new JSpinner(new SpinnerNumberModel(0, 0, null, 1));
        m_heightSpinner.setMaximumSize(new Dimension(100, 22));
        editor = (JSpinner.DefaultEditor)m_heightSpinner.getEditor();
        editor.getTextField().setColumns(7);
        editor.getTextField().setFocusLostBehavior(JFormattedTextField.COMMIT);
        m_heightSpinner.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(final ChangeEvent e) {
                updateModel();
            }
        });
        m_allowFullscreenCheckBox = new JCheckBox("Scale view to window size");
        m_allowFullscreenCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                updateModel();
            }
        });
        m_showFullscreenOption = model.getShowFullscreenOption();

        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder(BorderFactory
                .createEtchedBorder(), label));
        panel.add(Box.createGlue());
        Box widthBox = Box.createHorizontalBox();
        widthBox.add(Box.createHorizontalGlue());
        widthBox.add(m_widthLabel);
        widthBox.add(m_widthSpinner);
        widthBox.add(Box.createHorizontalGlue());
        panel.add(widthBox);
        panel.add(Box.createVerticalStrut(5));

        Box heightBox = Box.createHorizontalBox();
        heightBox.add(Box.createHorizontalGlue());
        heightBox.add(m_heightLabel);
        heightBox.add(m_heightSpinner);
        heightBox.add(Box.createHorizontalGlue());
        panel.add(heightBox);

        if (m_showFullscreenOption) {
            Box fullscreenBox = Box.createHorizontalBox();
            fullscreenBox.add(Box.createHorizontalGlue());
            fullscreenBox.add(m_allowFullscreenCheckBox);
            fullscreenBox.add(Box.createHorizontalGlue());
            fullscreenBox.setPreferredSize(new Dimension(250, 22));
            panel.add(Box.createVerticalStrut(5));
            panel.add(fullscreenBox);
        }
        panel.add(Box.createGlue());

        // update the inputs, whenever the model changes
        model.prependChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(final ChangeEvent e) {
                updateComponent();
            }
        });

        updateComponent();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void updateComponent() {
        SettingsModelSVGOptions model = (SettingsModelSVGOptions)getModel();
        int width = model.getWidth();
        int height = model.getHeight();
        boolean fullscreen = model.getAllowFullscreen();
        if (!m_widthSpinner.getValue().equals(width)) {
            m_widthSpinner.setValue(width);
        }
        if (!m_heightSpinner.getValue().equals(height)) {
            m_heightSpinner.setValue(height);
        }
        if (!m_allowFullscreenCheckBox.isSelected() == fullscreen) {
            m_allowFullscreenCheckBox.setSelected(fullscreen);
        }
        setEnabledComponents(model.isEnabled());
    }

    private void updateModel() {
        SettingsModelSVGOptions model = (SettingsModelSVGOptions)getModel();
        model.setWidth((int)m_widthSpinner.getValue());
        model.setHeight((int)m_heightSpinner.getValue());
        model.setAllowFullscreen(m_allowFullscreenCheckBox.isSelected());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void validateSettingsBeforeSave() throws InvalidSettingsException {
        updateModel();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void checkConfigurabilityBeforeLoad(final PortObjectSpec[] specs) throws NotConfigurableException {
        // always ok
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setEnabledComponents(final boolean enabled) {
        m_widthSpinner.setEnabled(enabled);
        m_heightSpinner.setEnabled(enabled);
        m_allowFullscreenCheckBox.setEnabled(enabled);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setToolTipText(final String text) {
        getComponentPanel().setToolTipText(text);
    }

}
