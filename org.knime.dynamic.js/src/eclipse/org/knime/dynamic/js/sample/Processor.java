package org.knime.dynamic.js.sample;

import org.knime.core.node.ExecutionContext;
import org.knime.core.node.port.PortObject;
import org.knime.dynamic.js.v212.DynamicJSConfig;
import org.knime.dynamic.js.v212.DynamicJSProcessor;

public class Processor implements DynamicJSProcessor {
    /**
     * {@inheritDoc}
     */
    @Override
    public Object[] processInputObjects(final PortObject[] inObjects, final ExecutionContext exec, final DynamicJSConfig config) {
        System.out.println("Look at me, I'm processing away!");
        return inObjects;
    }
}