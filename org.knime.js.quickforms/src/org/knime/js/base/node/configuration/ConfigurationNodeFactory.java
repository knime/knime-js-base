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
package org.knime.js.base.node.configuration;

import java.io.IOException;

import org.apache.xmlbeans.XmlException;
import org.knime.core.node.NodeDescription;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeView;
import org.knime.core.webui.node.dialog.NodeDialog;
import org.knime.core.webui.node.dialog.NodeDialogFactory;
import org.knime.core.webui.node.dialog.SettingsType;
import org.knime.core.webui.node.dialog.defaultdialog.DefaultNodeDialog;
import org.knime.core.webui.node.impl.WebUINodeConfiguration;
import org.knime.core.webui.node.impl.WebUINodeFactory;
import org.xml.sax.SAXException;

/**
 * Factory for a configuration node. Until we release the new webUI dialogs, this node will use the old dialog
 * implementation unless the system property <code>org.knime.configuration.ui.mode</code> is set to <code>js</code> or
 * the executor is running in headless mode (remote workflow editing).
 *
 *
 * @author Christian Albrecht, KNIME GmbH, Konstanz, Germany
 * @author Marc Bux, KNIME GmbH, Berlin, Germany
 * @param <T> the type of the node model this factory creates
 */
@SuppressWarnings("restriction")
public abstract class ConfigurationNodeFactory<T extends NodeModel> extends NodeFactory<T>
    implements NodeDialogFactory {

    private final WebUINodeConfiguration m_config;

    private final Class<? extends ConfigurationNodeSettings> m_settingsClass;

    /**
     * Constructor for the configuration node factory.
     *
     * @param config the configuration for the webUI node used to create the node description in case the webUI dialog
     *            is used.
     * @param settingsClass
     */
    protected ConfigurationNodeFactory(final WebUINodeConfiguration config,
        final Class<? extends ConfigurationNodeSettings> settingsClass) {
        super(true);
        m_config = config;
        m_settingsClass = settingsClass;
        init();
    }

    /**
     * Feature flag for webUI configuration dialogs in local AP.
     */
    private static final boolean SYSPROP_WEBUI_DIALOG_AP =
        "js".equals(System.getProperty("org.knime.configuration.ui.mode"));

    /**
     * If we are headless and a dialog is required (i.e. remote workflow editing), we enforce webUI dialogs.
     */
    private static final boolean SYSPROP_HEADLESS = Boolean.getBoolean("java.awt.headless");

    private static final boolean HAS_WEBUI_DIALOG = SYSPROP_HEADLESS || SYSPROP_WEBUI_DIALOG_AP;

    @Override
    protected NodeDescription createNodeDescription() throws SAXException, IOException, XmlException {
        return HAS_WEBUI_DIALOG ? WebUINodeFactory.createNodeDescription(m_config) : super.createNodeDescription();
    }

    @Override
    protected boolean hasDialog() {
        return !HAS_WEBUI_DIALOG;
    }

    @Override
    public boolean hasNodeDialog() {
        return HAS_WEBUI_DIALOG;
    }

    @Override
    public NodeDialog createNodeDialog() {
        return new DefaultNodeDialog(SettingsType.MODEL, m_settingsClass);
    }

    @Override
    protected int getNrNodeViews() {
        return 0;
    }

    @Override
    public NodeView<T> createNodeView(final int viewIndex, final T nodeModel) {
        return null;
    }

}
