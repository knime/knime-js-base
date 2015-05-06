package org.knime.dynamic.node.generation;

import java.util.Collection;

import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSetFactory;
import org.knime.core.node.config.ConfigRO;

public class AbstractDynamicNodeSetFactory implements NodeSetFactory {

	public AbstractDynamicNodeSetFactory() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public Collection<String> getNodeFactoryIds() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Class<? extends NodeFactory<? extends NodeModel>> getNodeFactory(
			String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getCategoryPath(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getAfterID(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ConfigRO getAdditionalSettings(String id) {
		// TODO Auto-generated method stub
		return null;
	}

}
