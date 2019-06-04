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
 *   05.05.2014 (Christian Albrecht, KNIME AG, Zurich, Switzerland): created
 */
package org.knime.js.base.node.viz.generic3;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.util.Collections;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.border.Border;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.knime.core.data.DataTableSpec;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.NotConfigurableException;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.util.ViewUtils;
import org.knime.core.node.util.dialog.FieldsTableModel.Column;
import org.knime.core.node.util.dialog.OutFieldsTable;
import org.knime.core.node.util.dialog.OutFieldsTableModel;
import org.knime.core.node.workflow.FlowVariable;
import org.knime.js.base.node.ui.JSSnippetTextArea;
import org.knime.js.base.template.DefaultTemplateController;
import org.knime.js.base.template.JSTemplate;
import org.knime.js.base.template.TemplatesPanel;
import org.knime.js.core.JSONWebNode;
import org.osgi.framework.FrameworkUtil;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

/**
 *
 * @author Christian Albrecht, KNIME.com GmbH, Konstanz, Germany
 */
final class GenericJSViewNodeDialogPane extends NodeDialogPane {

    private static final String SCRIPT_TAB = "JavaScript View";
    private static final String ID_WEB_RES = "org.knime.js.core.webResources";
    private static final String ATTR_RES_BUNDLE_ID = "webResourceBundleID";
    private static final String ATTR_RES_BUNDLE_NAME = "name";
    private static final String ATTR_RES_BUNDLE_VERSION = "version";
    private static final String ATTR_RES_BUNDLE_DEBUG = "debug";
    private static final String ATTR_RES_BUNDLE_DESCRIPTION = "description";

    private static final NodeLogger LOGGER = NodeLogger.getLogger(GenericJSViewNodeDialogPane.class);

    private BiMap<String, String> m_availableLibraries;
    private final GenericJSViewConfig m_config;
    private final Class<?> m_templateMetaCategory;
    private DefaultTemplateController m_templatesController;

    private final GenericJSNodePanel m_panel;

    private Border m_paddingBorder = BorderFactory.createEmptyBorder(3, 3, 3, 3);
    private Border m_lineBorder = BorderFactory.createLineBorder(new Color(200, 200, 200), 1);

    /**
     * Initializes new dialog pane.
     */
    GenericJSViewNodeDialogPane(final Class<?> templateMetaCategory) {
        m_config = new GenericJSViewConfig();
        m_templateMetaCategory = templateMetaCategory;

        m_panel = new GenericJSNodePanel(templateMetaCategory, m_config, getAvailableLibraries(), false) {
            private static final long serialVersionUID = 6002087063627485974L;
            /**
             * {@inheritDoc}
             */
            @Override
            public void applyTemplate(final JSTemplate template, final DataTableSpec spec,
                final Map<String, FlowVariable> flowVariables) {
                super.applyTemplate(template, spec, flowVariables);
                setSelected(SCRIPT_TAB);
            }
        };

        addTab(SCRIPT_TAB, m_panel);
        addTab("Image Generation", initImageGenerationLayout());
        addTab("Templates", initTemplatesPanel());
    }

    /*private JPanel initViewLayout() {
        JPanel wrapperPanel = new JPanel(new BorderLayout());
        wrapperPanel.setBorder(m_paddingBorder);
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.X_AXIS));
        topPanel.setBorder(m_lineBorder);
        topPanel.add(Box.createHorizontalStrut(10));
        topPanel.add(new JLabel("Maximum number of rows: "));
        m_maxRowsSpinner.setMaximumSize(new Dimension(100, 20));
        m_maxRowsSpinner.setMinimumSize(new Dimension(100, 20));
        m_maxRowsSpinner.setPreferredSize(new Dimension(100, 20));
        topPanel.add(m_maxRowsSpinner);
        topPanel.add(Box.createHorizontalStrut(10));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = gbc.gridy = 0;
        wrapperPanel.add(topPanel, BorderLayout.NORTH);

        JPanel p = new JPanel(new BorderLayout());

        JSplitPane leftPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true);
        leftPane.setBorder(m_noBorder);
        leftPane.setDividerLocation(120);
        JPanel topLeftPanel = new JPanel(new BorderLayout(2, 2));
        topLeftPanel.setBorder(m_paddingBorder);
        topLeftPanel.add(new JLabel("Flow Variables"), BorderLayout.NORTH);
        JScrollPane flowVarScroller = new JScrollPane(m_flowVarList);
        topLeftPanel.add(flowVarScroller, BorderLayout.CENTER);
        topLeftPanel.setPreferredSize(new Dimension(400, 130));
        JPanel bottomLeftPanel = new JPanel(new BorderLayout(2, 2));
        bottomLeftPanel.setBorder(m_paddingBorder);
        bottomLeftPanel.add(new JLabel("CSS"), BorderLayout.NORTH);
        JScrollPane cssScroller = new RTextScrollPane(m_cssTextArea);
        bottomLeftPanel.add(cssScroller, BorderLayout.CENTER);
        bottomLeftPanel.setPreferredSize(new Dimension(400, 400));
        leftPane.setTopComponent(topLeftPanel);
        leftPane.setBottomComponent(bottomLeftPanel);

        JSplitPane rightPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true);
        rightPane.setBorder(m_noBorder);
        rightPane.setDividerLocation(120);
        JPanel topRightPanel = new JPanel(new BorderLayout(2, 2));
        topRightPanel.setBorder(m_paddingBorder);
        topRightPanel.add(new JLabel("Dependencies"), BorderLayout.NORTH);
        JScrollPane dependenciesScroller = new JScrollPane(m_dependenciesTable);
        topRightPanel.add(dependenciesScroller, BorderLayout.CENTER);
        topRightPanel.setPreferredSize(new Dimension(400, 130));
        JPanel bottomRightPanel = new JPanel(new BorderLayout(2, 2));
        bottomRightPanel.setBorder(m_paddingBorder);
        bottomRightPanel.add(new JLabel("JavaScript"), BorderLayout.NORTH);
        JScrollPane jsScroller = new RTextScrollPane(m_jsTextArea);
        bottomRightPanel.add(jsScroller, BorderLayout.CENTER);
        bottomRightPanel.setPreferredSize(new Dimension(400, 400));
        rightPane.setTopComponent(topRightPanel);
        rightPane.setBottomComponent(bottomRightPanel);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true);
        splitPane.setBorder(m_noBorder);
        splitPane.setDividerLocation(0.5);
        splitPane.setLeftComponent(leftPane);
        splitPane.setRightComponent(rightPane);

        p.add(splitPane, BorderLayout.CENTER);

        JSplitPane outFieldsPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true);
        outFieldsPane.setBorder(m_noBorder);
        outFieldsPane.setTopComponent(p);
        //m_outFieldsTable.getTable().setMaximumSize(new Dimension(m_outFieldsTable.getWidth(), 50));
        m_outFieldsTable.setBorder(BorderFactory.createTitledBorder("Output Flow Variables"));
        m_outFieldsTable.setPreferredSize(m_outFieldsTable.getMinimumSize());
        outFieldsPane.setBottomComponent(m_outFieldsTable);
        outFieldsPane.setDividerLocation(0.8);
        outFieldsPane.setResizeWeight(0.7);


        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridy++;
        wrapperPanel.add(outFieldsPane, BorderLayout.CENTER);

        return wrapperPanel;
    }*/

    private static OutFieldsTable createOutVariableTable() {
        OutFieldsTable table = new OutFieldsTable(true, true);
        OutFieldsTableModel model = (OutFieldsTableModel)table.getTable().getModel();
        table.getTable().getColumnModel().getColumn(model.getIndex(
            Column.REPLACE_EXISTING)).setPreferredWidth(10);
        table.getTable().getColumnModel().getColumn(model.getIndex(
            Column.DATA_TYPE)).setPreferredWidth(20);
        return table;
    }

    private JPanel initImageGenerationLayout() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(m_paddingBorder);

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.X_AXIS));
        topPanel.setBorder(m_lineBorder);
        topPanel.add(Box.createHorizontalStrut(10));
        topPanel.add(m_panel.getGenerateViewCheckBox());
        topPanel.add(Box.createHorizontalGlue());
        topPanel.add(new JLabel("Additional wait time after initialization in ms: "));
        JSpinner waitTimeSpinner = m_panel.getWaitTimeSpinner();
        waitTimeSpinner.setMaximumSize(new Dimension(100, 20));
        waitTimeSpinner.setMinimumSize(new Dimension(100, 20));
        waitTimeSpinner.setPreferredSize(new Dimension(100, 20));
        topPanel.add(waitTimeSpinner);
        topPanel.add(Box.createHorizontalStrut(10));
        panel.add(topPanel, BorderLayout.NORTH);

        JPanel bottomPanel = new JPanel(new BorderLayout(2, 2));
        bottomPanel.setBorder(m_paddingBorder);
        bottomPanel.add(new JLabel("JavaScript to retrieve generated SVG as string"), BorderLayout.NORTH);
        JSSnippetTextArea svgTextArea = m_panel.getJsSVGTextArea();
        svgTextArea.setRows(10);
        JScrollPane svgScroller = new RTextScrollPane(svgTextArea);
        bottomPanel.add(svgScroller, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.CENTER);

        return panel;
    }

    /** Create the templates tab. */
    private JPanel initTemplatesPanel() {
        final GenericJSNodePanel preview =
            new GenericJSNodePanel(m_templateMetaCategory, m_config, getAvailableLibraries(), true);

        m_templatesController = new DefaultTemplateController(m_panel, preview);
        final TemplatesPanel templatesPanel =
            new TemplatesPanel(Collections.<Class<?>> singleton(m_templateMetaCategory), m_templatesController);
        return templatesPanel;
    }

    /**
     * {@inheritDoc}
     */

    @Override
    protected void loadSettingsFrom(final NodeSettingsRO settings, final PortObjectSpec[] specs)
            throws NotConfigurableException {
        PortObjectSpec[] s = new PortObjectSpec[specs.length];
        for (int i = 0; i < specs.length; i++) {
            s[i] = specs[i] == null ? new DataTableSpec() : specs[i];
        }
        ViewUtils.invokeAndWaitInEDT(new Runnable() {
            @Override
            public void run() {
                loadSettingsFromInternal(settings, s);
            }
        });
    }

    @SuppressWarnings("unchecked")
    protected void loadSettingsFromInternal(final NodeSettingsRO settings, final PortObjectSpec[] specs)  {
        m_config.loadSettingsForDialog(settings);
        DataTableSpec spec = specs.length > 0 ? (DataTableSpec)specs[0] : null;
        try {
            m_panel.loadSettingsFrom(settings, spec, getAvailableFlowVariables());
        } catch (NotConfigurableException e) {
            LOGGER.error("Unable to load settings: ", e);
        }

        m_templatesController.setDataTableSpec(spec);
        m_templatesController.setFlowVariables(getAvailableFlowVariables());
    }

    private static BiMap<String, String> getAvailableLibraries() {
        BiMap<String, String> availableLibraries = HashBiMap.create();
        availableLibraries.put("D3_4.2.6", "D3 - Version 4.2.6");
        availableLibraries.put("plotly.js-1.47.4", "Plotly.js - Version 1.47.4");
        availableLibraries.put("jQuery_3.1.1", "jQuery - Version 3.1.1");
        availableLibraries.put("jQueryUi_1.12.1", "jQuery UI - Version 1.12.1");
        return availableLibraries;
    }

    @SuppressWarnings("unused")
    private static BiMap<String, String> getAllAvailableLibraries() {
        BiMap<String, String> availableLibraries = HashBiMap.create();
        String libBundleName = FrameworkUtil.getBundle(JSONWebNode.class).getSymbolicName();

        IExtensionRegistry registry = Platform.getExtensionRegistry();
        IExtensionPoint point = registry.getExtensionPoint(ID_WEB_RES);
        if (point == null) {
            throw new IllegalStateException("Invalid extension point id: " + ID_WEB_RES);
        }

        for (IExtension ext : point.getExtensions()) {
            IConfigurationElement[] elements = ext.getConfigurationElements();
            for (IConfigurationElement e : elements) {
                String bundleId = e.getDeclaringExtension().getNamespaceIdentifier();
                // Only load elements from library plugin
                if (!bundleId.equalsIgnoreCase(libBundleName)) {
                    continue;
                }
                String resBundleID = e.getAttribute(ATTR_RES_BUNDLE_ID);
                String resBundleName = e.getAttribute(ATTR_RES_BUNDLE_NAME);
                String resBundleVersion = e.getAttribute(ATTR_RES_BUNDLE_VERSION);
                boolean resBundleDebug = Boolean.parseBoolean(e.getAttribute(ATTR_RES_BUNDLE_DEBUG));
                String resBundleDisplay = resBundleName + " - Version " + resBundleVersion;
                if (resBundleDebug) {
                    resBundleDisplay += " - Debug";
                }
                availableLibraries.forcePut(resBundleID, resBundleDisplay);
            }
        }
        return availableLibraries;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) throws InvalidSettingsException {
        m_panel.saveSettingsTo(settings);
    }
}
