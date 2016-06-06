package org.apodhrad.p2diff.html;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apodhrad.p2diff.file.Delta;

import freemarker.template.Configuration;
import freemarker.template.TemplateException;

public class HTMLGenerator {

	public static final String TAG_SPAN = "span";

	private Configuration cfg;
	private File diffFolder;
	private File target;

	public HTMLGenerator(File target) {
		this.target = target;

		cfg = new Configuration(Configuration.VERSION_2_3_22);
		cfg.setDefaultEncoding("UTF-8");
		cfg.setClassForTemplateLoading(this.getClass(), "/templates");
	}

	public File prepareDiffFolder(File target) throws IOException {
		if (diffFolder == null) {
			diffFolder = new File(target, "diff-reports");
			FileUtils.deleteQuietly(diffFolder);
			FileUtils.forceMkdir(diffFolder);
			FileUtils.forceMkdir(new File(diffFolder, "diffs"));
		}
		return diffFolder;
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
	private static String span(String className, String content) {
		return "<" + TAG_SPAN + " class=\"" + className + "\">" + content + "</" + TAG_SPAN + ">";
	}

	private File getDiffFile(String name) {
		String diffName = name.replaceAll("/", ".");
		if (!diffName.endsWith(".html")) {
			diffName += ".html";
		}
		return new File(diffFolder, "diffs/" + diffName);
	}

	public File generateDiff(List<String> diff, String diffName) throws Exception {
		prepareDiffFolder(target);

		File diffFile = getDiffFile(diffName);

		Map<String, Object> root = new HashMap<String, Object>();
		root.put("diff", convertToHTML(diff));

		FileWriter out = new FileWriter(diffFile);
		cfg.getTemplate("simple_diff.html").process(root, out);

		return diffFile;
	}

	public void generateDiffReport(List<String> diff) throws Exception {
		Map<String, Object> root = new HashMap<String, Object>();
		root.put("diff", convertToHTML(diff));

		generateReport("report_diff.html", root);
	}

	public void generateDeltaReport(Collection<Delta> deltas) throws Exception {
		Map<String, Object> root = new HashMap<String, Object>();
		root.put("deltas", deltas);

		generateReport("report_delta.html", root);
	}

	protected void generateReport(String template, Map<String, Object> properties)
			throws IOException, TemplateException {
		prepareDiffFolder(target);

		InputStream css = getClass().getResourceAsStream("/css/style.css");
		InputStream js = getClass().getResourceAsStream("/js/script.js");

		FileUtils.copyInputStreamToFile(css, new File(diffFolder, "css/style.css"));
		FileUtils.copyInputStreamToFile(js, new File(diffFolder, "js/script.js"));

		FileWriter out = new FileWriter(new File(diffFolder, "index.html"));
		cfg.getTemplate(template).process(properties, out);
	}

	/**
	 * Convert Diff line to HTML
	 * 
	 * @param unifiedDiff
	 * @return
	 */
	public static List<String> convertToHTML(List<String> diffLines) {
		List<String> result = new ArrayList<String>();

		for (String line : diffLines) {
			result.add(convertToHTML(line));
		}

		return result;
	}

	/**
	 * Convert Diff line to HTML
	 * 
	 * @param diffLine
	 * @return
	 */
	public static String convertToHTML(String diffLine) {
		String result;

		if (diffLine.startsWith("+++") || diffLine.startsWith("---"))
			result = span("info", diffLine);
		else if (diffLine.startsWith("@@") && diffLine.endsWith("@@"))
			result = span("range", diffLine);
		else if (diffLine.startsWith("+"))
			result = span("added", diffLine);
		else if (diffLine.startsWith("-"))
			result = span("deleted", diffLine);
		else if (diffLine.startsWith(" "))
			result = span("nochange", diffLine);
		else
			result = diffLine;

		return result;
	}

}
