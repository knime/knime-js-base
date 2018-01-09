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
package org.knime.dynamic.js;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.xmlbeans.XmlException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSetFactory;
import org.knime.core.node.NodeSettings;
import org.knime.core.node.config.ConfigRO;
import org.knime.core.util.FileUtil;
import org.osgi.framework.Bundle;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 *
 * @author Christian Albrecht, KNIME AG, Zurich, Switzerland
 */
public class DynamicJSNodeSetFactory implements NodeSetFactory {

	private static final NodeLogger LOGGER = NodeLogger
			.getLogger(DynamicJSNodeSetFactory.class);

	private static final String DYNAMIC_FUNCTION = "js";
	private static final String CONFIG_ID = "org.knime.dynamic.node.generation.dynamicNodes";

	/** Config string used for factory creation.
	 * @since 3.0*/
	public static final String NODE_DIR_CONF = "nodeDir";

	private List<File> m_configFolders = new ArrayList<File>();
    private final Map<String, Class<? extends NodeFactory<? extends NodeModel>>> m_factories =
        new HashMap<String, Class<? extends NodeFactory<? extends NodeModel>>>();
	private final Map<String, String> m_paths = new HashMap<String, String>();
	private final Map<String, String> m_afterIDs = new HashMap<String, String>();

	@Override
	public Collection<String> getNodeFactoryIds() {
	    Collection<String> factoryIds = new ArrayList<String>();
	    IExtensionRegistry registry = Platform.getExtensionRegistry();
        IConfigurationElement[] configurationElements = registry.getConfigurationElementsFor(CONFIG_ID);
        for (IConfigurationElement configElement : configurationElements) {
            if (DYNAMIC_FUNCTION.equals(configElement.getAttribute("function"))) {
                try {
                    final String pluginName = configElement.getContributor().getName();
                    Bundle bundle = Platform.getBundle(pluginName);
                    final String configFolderRelative = configElement.getAttribute("path");
                    URL configURL = bundle.getEntry(configFolderRelative);
                    File configFolder = FileUtil.resolveToPath(FileLocator.toFileURL(configURL)).toFile();
                    m_configFolders.add(configFolder);
                    String[] nodeList = configFolder.list(new FilenameFilter() {

                        @Override
                        public boolean accept(final File dir, final String name) {
                            File configDir = new File(dir, name);
                            boolean accept = configDir.isDirectory();
                            File nodeConfig = new File(configDir, "node.xml");
                            accept &= nodeConfig.exists();
                            if (accept) {
                                try {
                                    String nodeID = pluginName + ":" + configFolderRelative + ":" + name;
                                    getFactoryClass(nodeConfig, nodeID);
                                } catch (XmlException | IOException | ParserConfigurationException | SAXException e) {
                                    LOGGER.warn("Node config in folder " + configDir
                                            + " could not be read. " + e.getMessage()
                                            + " Skipping folder.", e);
                                    return false;
                                }
                            }
                            return accept;
                        }
                    });
                    for (int i = 0; i < nodeList.length; i++) {
                        nodeList[i] = pluginName + ":" + configFolderRelative + ":" + nodeList[i];
                    }
                    factoryIds.addAll(Arrays.asList(nodeList));
                } catch (Exception e) {
                    LOGGER.warn("Error initializing config folder: " + e.getMessage(), e);
                }
            }
        }
		return factoryIds;
	}

	@Override
	public Class<? extends NodeFactory<? extends NodeModel>> getNodeFactory(final String id) {
		return m_factories.get(id);
	}

	@Override
	public String getCategoryPath(final String id) {
		return m_paths.get(id);
	}

	@Override
	public String getAfterID(final String id) {
		return m_afterIDs.get(id);
	}

	@Override
	public ConfigRO getAdditionalSettings(final String id) {
		NodeSettings s = new NodeSettings("root");
		s.addString(NODE_DIR_CONF, id);
		return s;
	}

	private void getFactoryClass(final File nodeConfig, final String nodeID) throws ParserConfigurationException, SAXException, IOException, XmlException {
	    DocumentBuilderFactory fac = DocumentBuilderFactory.newInstance();
        fac.setNamespaceAware(true);
        DocumentBuilder parser = fac.newDocumentBuilder();
        Document doc;
        synchronized (parser) {
            doc = parser.parse(new FileInputStream(nodeConfig));
        }

        String namespaceUri = doc.getDocumentElement().getNamespaceURI();
        if (namespaceUri != null) {
            if (namespaceUri.equals(org.knime.dynamicjsnode.v212.KnimeNodeDocument.type.getContentModel().getName().getNamespaceURI())) {
                org.knime.dynamicjsnode.v212.KnimeNodeDocument node = org.knime.dynamicjsnode.v212.KnimeNodeDocument.Factory.parse(nodeConfig);
                String categoryPath = node.getKnimeNode().getCategoryPath();
                if (!node.validate()) {
                    throw new XmlException("Node config XML did not validate against Dynamic JavaScript Node v2.12 schema.");
                }
                m_factories.put(nodeID, org.knime.dynamic.js.v212.DynamicJSNodeFactory.class);
                m_paths.put(nodeID, categoryPath == null ? "unknown" : categoryPath);
                m_afterIDs.put(nodeID, node.getKnimeNode().getAfterID());
            } else if (namespaceUri.equals(org.knime.dynamicjsnode.v30.KnimeNodeDocument.type.getContentModel().getName().getNamespaceURI())) {
                org.knime.dynamicjsnode.v30.KnimeNodeDocument node = org.knime.dynamicjsnode.v30.KnimeNodeDocument.Factory.parse(nodeConfig);
                String categoryPath = node.getKnimeNode().getCategoryPath();
                if (!node.validate()) {
                    throw new XmlException("Node config XML did not validate against Dynamic JavaScript Node v3.0 schema.");
                }
                m_factories.put(nodeID, org.knime.dynamic.js.v30.DynamicJSNodeFactory.class);
                m_paths.put(nodeID, categoryPath == null ? "unknown" : categoryPath);
                m_afterIDs.put(nodeID, node.getKnimeNode().getAfterID());
            } else {
                throw new XmlException("Unsupported namespace for node description in " + nodeConfig.getCanonicalPath() + ": " + namespaceUri);
            }
        }
	}
}