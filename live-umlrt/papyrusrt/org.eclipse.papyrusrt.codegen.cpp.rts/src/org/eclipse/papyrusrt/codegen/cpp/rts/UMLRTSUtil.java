/*******************************************************************************
 * Copyright (c) 2014-2015 Zeligsoft (2009) Limited and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.papyrusrt.codegen.cpp.rts;

import java.net.URI;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.papyrusrt.xtumlrt.util.PathUtils;
import org.osgi.framework.Bundle;

public class UMLRTSUtil {

	public static String getRTSRootPath() {

		Bundle bundle = Platform.getBundle("org.eclipse.papyrusrt.rts");
		if (bundle != null) {
			Path path = new Path("umlrts");
			URL url = FileLocator.find(bundle, path, null);
			try {
				URI uri = FileLocator.resolve(url).toURI();
				return PathUtils.simplifyPath(uri.getRawPath());
			} catch (Exception e) {
				return "";
			}
		}
		return "";
	}
}
