package org.apodhrad.p2diff;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import difflib.DiffUtils;
import difflib.Patch;
import freemarker.template.Configuration;
import freemarker.template.Template;

public class DiffForFile {

	/**
	 * Wrapping tag name
	 */
	public static final String TAG = "span";

	private File baseDir;

	private DiffFile originalFile;
	private DiffFile revisedFile;

	private Configuration cfg;
	public String template = "hidden.html";

	/**
	 * 
	 * @param originalPath
	 * @param revisedPath
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public DiffForFile(File originalFile, File revisedFile) throws IOException {
		if (originalFile == null && revisedFile == null) {
			throw new IllegalArgumentException("Cannot generate diff for two NULL objects!");
		}
		this.originalFile = new DiffFile(originalFile);
		this.revisedFile = new DiffFile(revisedFile);
	}

	public void setBaseDir(File baseDir) {
		if (!baseDir.exists()) {
			throw new IllegalArgumentException();
		}
		if (!baseDir.isDirectory()) {
			throw new IllegalArgumentException();
		}
		this.baseDir = baseDir;
	}

	public File getBaseDir() {
		return baseDir;
	}

	/**
	 * Convert Diff to HTML
	 * 
	 * @param unifiedDiff
	 * @return
	 */
	private String convertToHTML(String unifiedDiff) {
		String result;

		if (unifiedDiff.startsWith("+++") || unifiedDiff.startsWith("---"))
			result = generateTag("info", unifiedDiff);
		else if (unifiedDiff.startsWith("@@") && unifiedDiff.endsWith("@@"))
			result = generateTag("range", unifiedDiff);
		else if (unifiedDiff.startsWith("+"))
			result = generateTag("added", unifiedDiff);
		else if (unifiedDiff.startsWith("-"))
			result = generateTag("deleted", unifiedDiff);
		else
			result = unifiedDiff;

		return result;
	}

	public List<String> generateDiff() throws IOException {

		List<String> originalLines = originalFile.getLines();
		List<String> revisedLines = revisedFile.getLines();

		Patch patch = DiffUtils.diff(originalLines, revisedLines);
		if (patch.getDeltas().isEmpty()) {
			return new ArrayList<String>();
		}

		String originalPath = originalFile.getPath();
		String revisedPath = revisedFile.getPath();

		return DiffUtils.generateUnifiedDiff(originalPath, revisedPath, originalLines, patch, 1);
	}

	/**
	 * Generate tag
	 * 
	 * @param className
	 *            Name of CSS class
	 * @param content
	 *            Tag content
	 * @return
	 */
	private String generateTag(String className, String content) {
		return "<" + TAG + " class=\"" + className + "\">" + content + "</" + TAG + ">";
	}

	/**
	 * Generate HTML file
	 * 
	 * @return
	 * @throws Exception
	 */
	public String generate() throws Exception {
		List<String> htmlDiff = new ArrayList<String>();
		for (String line : generateDiff()) {
			htmlDiff.add(this.convertToHTML(line));
		}

		configurateTemplateSystem();

		Template temp = cfg.getTemplate(this.template);

		Map<String, Object> root = new HashMap<String, Object>();
		root.put("diff", htmlDiff);

		StringWriter sw = new StringWriter();
		temp.process(root, sw);

		return sw.toString();
	}

	private void configurateTemplateSystem() throws IOException {
		cfg = new Configuration(Configuration.VERSION_2_3_22);
		cfg.setDefaultEncoding("UTF-8");
		cfg.setClassForTemplateLoading(this.getClass(), "/templates");
	}

	private class DiffFile {

		private File file;

		public DiffFile(File file) {
			this.file = file;
		}

		public List<String> getLines() throws IOException {
			if (file == null) {
				return new ArrayList<String>();
			}
			return FileUtils.readLines(file);
		}

		public String getPath() {
			if (file == null) {
				return "null";
			}
			if (baseDir != null) {
				return baseDir.toURI().relativize(file.toURI()).getPath();
			}
			return file.getPath();
		}
	}

}
