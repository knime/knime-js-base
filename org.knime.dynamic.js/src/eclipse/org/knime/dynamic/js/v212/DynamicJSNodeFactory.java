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
 *   24.04.2015 (Christian Albrecht, KNIME AG, Zurich, Switzerland): created
 */
package org.knime.dynamic.js.v212;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Locale;

import org.apache.xmlbeans.XmlException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.knime.core.node.DynamicNodeFactory;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NoDescriptionProxy;
import org.knime.core.node.NodeDescription;
import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.NodeView;
import org.knime.core.node.config.ConfigRO;
import org.knime.core.node.config.ConfigWO;
import org.knime.core.node.wizard.WizardNodeFactoryExtension;
import org.knime.core.util.FileUtil;
import org.knime.core.webui.node.dialog.NodeDialog;
import org.knime.core.webui.node.dialog.NodeDialogFactory;
import org.knime.core.webui.node.dialog.SettingsType;
import org.knime.core.webui.node.dialog.defaultdialog.DefaultNodeDialog;
import org.knime.dynamic.js.DynamicJSNodeSetFactory;
import org.knime.dynamicjsnode.v212.KnimeNodeDocument;
import org.knime.dynamicnode.v212.DynamicFullDescription;
import org.knime.node.parameters.NodeParameters;
import org.osgi.framework.Bundle;

/**
 *
 * @author Christian Albrecht, KNIME AG, Zurich, Switzerland
 * @since 3.0
 */
@SuppressWarnings("restriction")
public class DynamicJSNodeFactory extends DynamicNodeFactory<DynamicJSNodeModel> implements
		WizardNodeFactoryExtension<DynamicJSNodeModel, DynamicJSViewRepresentation, DynamicJSViewValue>,
		NodeDialogFactory {

	private static final NodeLogger LOGGER = NodeLogger.getLogger(DynamicJSNodeFactory.class);

	static final String NODE_PLUGIN = "nodePlugin";
	static final String PLUGIN_FOLDER = "pluginFolder";

    /**
     * Feature flag for webUI widget dialogs in local AP.
     */
    private static final boolean SYSPROP_WEBUI_DIALOG_AP = "js".equals(System.getProperty("org.knime.widget.ui.mode"));

    /**
     * If we are headless and a dialog is required (i.e. remote workflow editing), we enforce webUI dialogs.
     */
    private static final boolean SYSPROP_HEADLESS = Boolean.getBoolean("java.awt.headless");

    private static final boolean HAS_WEBUI_DIALOG = SYSPROP_HEADLESS || SYSPROP_WEBUI_DIALOG_AP;

	private File m_nodeDir;
	private String m_pluginName;
	private String m_configFolder;
	private String m_nodeFolder;
	private KnimeNodeDocument m_doc;

    private Class<? extends NodeParameters> m_parametersClass;

	@Override
	public NodeDescription createNodeDescription() {
	    if (m_doc != null) {
	        return new DynamicJSNodeDescription212Proxy(m_doc, m_nodeDir);
	    }
		return new NoDescriptionProxy(getClass());
	}

	@Override
	public void loadAdditionalFactorySettings(final ConfigRO config)
			throws InvalidSettingsException {
	    String confString = config.getString(DynamicJSNodeSetFactory.NODE_DIR_CONF);
        String[] confParts = confString.split(":");
	    if (confParts.length != 3) {
	        throw new InvalidSettingsException("Error reading factory settings. Expected pluginName:configFolder:nodeFolder, but was " + confString);
	    }
	    m_pluginName = confParts[0];
	    m_configFolder = confParts[1];
	    m_nodeFolder = confParts[2];
	    Bundle bundle = Platform.getBundle(m_pluginName);
        URL configURL = bundle.getEntry(m_configFolder);
        try {
            File configFolder = FileUtil.resolveToPath(FileLocator.toFileURL(configURL)).toFile();
            m_nodeDir = new File(configFolder, m_nodeFolder);
            if (!configFolder.exists() || !m_nodeDir.exists()) {
                throw new IOException("Node folder " + m_nodeDir.getAbsolutePath() + " does not exist.");
            }
        } catch (IOException | URISyntaxException e) {
            LOGGER.error("Error retrieving node description folder for " + m_pluginName + ", " + m_configFolder);
            throw new InvalidSettingsException(e);
        }

        try {
			m_doc = KnimeNodeDocument.Factory.parse(new File(m_nodeDir, "node.xml"));
		} catch (XmlException | IOException e) {
			LOGGER.error("Error reading node config: " + e.getMessage(), e);
			throw new InvalidSettingsException(e);
		}

        loadParametersClass();

		super.loadAdditionalFactorySettings(config);
	}

    /**
     * Attempts to load a NodeParameters class from the org.knime.dynamic.js.base._root_ plugin, where root is replaced
     * by the lowercase name of the folder in which the node.xml of the node can be found. The class should be named
     * like the folder of the node.xml with the first char being upper case and a "NodeParameters" suffix.
     */
    @SuppressWarnings("unchecked")
    private void loadParametersClass() {
        try {
            final var className =
                m_nodeFolder.substring(0, 1).toUpperCase(Locale.ENGLISH) + m_nodeFolder.substring(1) + "NodeParameters";
            final var classPackage = "org.knime.dynamic.js.base." + m_nodeFolder.toLowerCase(Locale.ENGLISH);
            final var fullClassName = classPackage + "." + className;
            final var clazz = Class.forName(fullClassName);

            if (NodeParameters.class.isAssignableFrom(clazz)) {
                m_parametersClass = (Class<? extends NodeParameters>)clazz;
                return;
            }
            LOGGER.debug("Found a class with suffix NodeParameters (" + fullClassName
                + "), but it does not implement the NodeParameters interface, falling back to legacy dialog.");
        } catch (ClassNotFoundException | LinkageError e) { //NOSONAR
            /**
             * We do not log this as an error because not having a NodeParameters class is valid for nearly all nodes
             * using this class.
             */
        }
    }

	@Override
	public void saveAdditionalFactorySettings(final ConfigWO config) {
		config.addString(DynamicJSNodeSetFactory.NODE_DIR_CONF, m_pluginName + ":" + m_configFolder + ":" + m_nodeFolder);
		super.saveAdditionalFactorySettings(config);
	}

	@Override
	public DynamicJSNodeModel createNodeModel() {
		return new DynamicJSNodeModel(m_doc.getKnimeNode(), m_nodeDir.getAbsolutePath(), getInteractiveViewName());
	}

	@Override
	public int getNrNodeViews() {
		return 0;
	}

	@Override
	public NodeView<DynamicJSNodeModel> createNodeView(final int viewIndex,
			final DynamicJSNodeModel nodeModel) {
		return null;
	}

	@Override
	public boolean hasDialog() {
        if (HAS_WEBUI_DIALOG && m_parametersClass != null) {
            return false;
        }
	    DynamicFullDescription desc = m_doc.getKnimeNode().getFullDescription();
		boolean hasDialog = desc.getOptions() != null;
		hasDialog |= desc.getTabArray() != null && desc.getTabArray().length > 0;
		return hasDialog;
	}

	@Override
	public NodeDialogPane createNodeDialogPane() {
		return new DynamicJSNodeDialog(m_doc.getKnimeNode());
	}

    /**
     * @since 5.10
     */
    @Override
    public boolean hasNodeDialog() {
        return HAS_WEBUI_DIALOG && m_parametersClass != null;
    }

    /**
     * @since 5.10
     */
    @Override
    public NodeDialog createNodeDialog() {
        if (m_parametersClass != null) {
            return new DefaultNodeDialog(SettingsType.MODEL, m_parametersClass);
        }
        throw new IllegalStateException("No WebUI parameters class available");
    }

	/**
     * {@inheritDoc}
     */
    @Override
    protected boolean isDeprecatedInternal() {
        return m_doc.getKnimeNode().getDeprecated();
    }

}
