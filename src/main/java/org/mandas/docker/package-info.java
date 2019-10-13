@Style(
		visibility = ImplementationVisibility.PACKAGE, 
		builderVisibility = BuilderVisibility.PACKAGE, 
		depluralize = true,
		add = "*",
		put = "add*",
		jdkOnly = true)
package org.mandas.docker;

import org.immutables.value.Value.Style;
import org.immutables.value.Value.Style.BuilderVisibility;
import org.immutables.value.Value.Style.ImplementationVisibility;