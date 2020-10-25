@Export
@Version("${project.osgi_framework_version}")
@Capability(
    namespace = EXTENDER_NAMESPACE,
    name = COMPONENT_CAPABILITY_NAME,
    version = COMPONENT_SPECIFICATION_VERSION
)
package org.osgi.framework;

import org.osgi.annotation.bundle.Capability;
import org.osgi.annotation.bundle.Export;
import org.osgi.annotation.versioning.Version;

import static org.osgi.namespace.extender.ExtenderNamespace.EXTENDER_NAMESPACE;
import static org.osgi.service.component.ComponentConstants.COMPONENT_CAPABILITY_NAME;
import static org.osgi.service.component.ComponentConstants.COMPONENT_SPECIFICATION_VERSION;
