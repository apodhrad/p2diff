package org.apodhrad.p2diff.util;

import java.io.File;

public class ResourceUtils {

	public static File getResourceFile(String path) {
		return new File(ResourceUtils.class.getResource(path).getFile());
	}

}
