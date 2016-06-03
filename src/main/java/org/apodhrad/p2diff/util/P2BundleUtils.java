package org.apodhrad.p2diff.util;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apodhrad.jdownload.manager.util.UnpackUtils;

public class P2BundleUtils {

	public static File extractJarFile(File jarFile) throws IOException {
		return extractJarFile(jarFile, true);
	}

	public static File extractJarFile(File jarFile, boolean delete) throws IOException {
		String name = jarFile.getName().substring(0, jarFile.getName().length() - 4);
		File extractedJarFile = new File(jarFile.getParentFile(), name);
		if (delete) {
			FileUtils.deleteQuietly(extractedJarFile);
		}
		UnpackUtils.unpack(jarFile, extractedJarFile);
		return extractedJarFile;
	}

	public static File getSourceJarFile(File jarFile) {
		String version = getBundleVersion(jarFile); 
		String sourceName = getBundleName(jarFile) + ".source";
		if (version != null) {
			sourceName += "_" + version;
		}
		return new File(jarFile.getParentFile(), sourceName + ".jar");
	}
	
	public static String getBundleName(File jarFile) {
		String name = jarFile.getName().substring(0, jarFile.getName().length() - 4);
		if (name.contains("_")) {
			return name.split("_")[0];
		} else {
			return name;
		}
	}
	
	public static String getBundleVersion(File jarFile) {
		String name = jarFile.getName().substring(0, jarFile.getName().length() - 4);
		if (name.contains("_")) {
			return name.split("_")[1];
		} else {
			return null;
		}
	}
}
