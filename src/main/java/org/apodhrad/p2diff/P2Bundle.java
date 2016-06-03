package org.apodhrad.p2diff;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apodhrad.p2diff.file.Folder;
import org.apodhrad.p2diff.util.P2BundleUtils;

public class P2Bundle {

	private File jarFile;
	private Folder extractedJarFile;
	private File sourceJarFile;
	private Folder extractedSourceJarFile;

	public P2Bundle(File jarFile) {
		if (jarFile != null) {
			this.jarFile = jarFile;
			File sourceJarFile = P2BundleUtils.getSourceJarFile(jarFile);
			if (sourceJarFile.exists()) {
				this.sourceJarFile = sourceJarFile;
			}
		}
	}

	public File getExtractedJarFile() throws IOException {
		if (extractedJarFile == null && jarFile != null) {
			extractedJarFile = new Folder(P2BundleUtils.extractJarFile(jarFile));
		}
		return extractedJarFile;
	}

	public File getExtractedSourceJarFile() throws IOException {
		if (sourceJarFile == null) {
			return null;
		}
		if (extractedSourceJarFile == null) {
			extractedSourceJarFile = new Folder(P2BundleUtils.extractJarFile(sourceJarFile));
		}
		return extractedSourceJarFile;
	}

	public long computeCRC32() throws IOException {
		if (jarFile == null) {
			return 0l;
		}
		return FileUtils.checksumCRC32(jarFile);
	}

	public Collection<File> listFiles() throws IOException {
		if (jarFile == null) {
			return new HashSet<File>();
		}
		return FileUtils.listFiles(getExtractedJarFile(), TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
	}

	public Collection<String> listRelativePaths() throws IOException {
		List<String> relativePaths = new ArrayList<String>();
		for (File file : listFiles()) {
			if (file.getName().endsWith(".java")) {
				continue;
			}
			relativePaths.add(getExtractedJarFile().toURI().relativize(file.toURI()).getPath());
		}
		return relativePaths;
	}

	public File getFile(String relativePath) throws IOException {
		if (jarFile == null) {
			return null;
		}
		File file = new File(getExtractedJarFile(), relativePath);
		if (!file.exists()) {
			return null;
		}
		if (file.getName().endsWith(".class")) {
			File sourceFile = getSourceFile(file);
			if (sourceFile != null) {
				return sourceFile;
			}
		}
		return file;
	}

	public File getSourceFile(File classFile) throws IOException {
		if (!classFile.getName().endsWith(".class")) {
			throw new IllegalArgumentException("You can get source file only for java class!");
		}
		String sourceFileName = classFile.getName().replace(".class", ".java");
		File sourceFile = new File(classFile.getParentFile(), sourceFileName);
		if (sourceFile.exists()) {
			return sourceFile;
		}
		if (sourceJarFile == null) {
			return null;
		}
		String sourcePath = getExtractedJarFile().toURI().relativize(sourceFile.toURI()).getPath();
		sourceFile = new File(getExtractedSourceJarFile(), sourcePath);
		if (sourceFile.exists()) {
			return sourceFile;
		}
		return null;
	}

}
