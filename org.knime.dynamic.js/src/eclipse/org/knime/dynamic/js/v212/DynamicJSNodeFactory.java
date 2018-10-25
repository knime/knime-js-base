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
import org.knime.dynamic.js.DynamicJSNodeSetFactory;
import org.knime.dynamicjsnode.v212.KnimeNodeDocument;
import org.knime.dynamicnode.v212.DynamicFullDescription;
import org.osgi.framework.Bundle;

/**
 *
 * @author Christian Albrecht, KNIME AG, Zurich, Switzerland
 * @since 3.0
 */
public class DynamicJSNodeFactory extends DynamicNodeFactory<DynamicJSNodeModel> implements
		WizardNodeFactoryExtension<DynamicJSNodeModel, DynamicJSViewRepresentation, DynamicJSViewValue> {

	private static final NodeLogger LOGGER = NodeLogger.getLogger(DynamicJSNodeFactory.class);

	static final String NODE_PLUGIN = "nodePlugin";
	static final String PLUGIN_FOLDER = "pluginFolder";

	private File m_nodeDir;
	private String m_pluginName;
	private String m_configFolder;
	private String m_nodeFolder;
	private KnimeNodeDocument m_doc;

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
		super.loadAdditionalFactorySettings(config);
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
     * {@inheritDoc}
     */
    @Override
    protected boolean isDeprecatedInternal() {
        return m_doc.getKnimeNode().getDeprecated();
    }

}
