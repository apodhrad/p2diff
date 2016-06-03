package org.apodhrad.p2diff.html;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apodhrad.p2diff.P2DiffApp;
import org.apodhrad.p2diff.file.Delta;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class HTMLGenerator {

	/**
	 * Wrapping tag name
	 */
	public static final String TAG = "span";

	private Map<String, Object> diff = new HashMap<String, Object>();
	private Configuration cfg;
	private String filename;

	public HTMLGenerator() {
	}

	public HTMLGenerator(Map<String, Object> diff) {
		this(diff, "");
	}

	public HTMLGenerator(Map<String, Object> diff, String filename) {
		this.filename = filename;
		this.diff = diff;
	}

	public String generateHTML(String template) throws IOException, TemplateException {
		configurateTemplateSystem();
		Template temp = cfg.getTemplate(template);

		Map<String, Object> root = new HashMap<String, Object>();

		root.put("filename", filename);
		root.put("diff", diff);

		StringWriter sw = new StringWriter();
		temp.process(root, sw);

		return sw.toString();
	}

	private void configurateTemplateSystem() throws IOException {
		cfg = new Configuration(Configuration.VERSION_2_3_22);
		cfg.setDefaultEncoding("UTF-8");
		cfg.setClassForTemplateLoading(this.getClass(), "/templates");
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
	public static String generateTag(String className, String content) {
		return "<" + TAG + " class=\"" + className + "\">" + content + "</" + TAG + ">";
	}

	/**
	 * Generate HTML file
	 * 
	 * @return
	 * @throws Exception
	 */
	public void generateDiffReport(List<String> diff, File target) throws Exception {
		File diffDir = new File(target, "diff-reports");
		FileUtils.forceMkdir(diffDir);

		InputStream css = P2DiffApp.class.getResourceAsStream("/css/style.css");
		InputStream js = P2DiffApp.class.getResourceAsStream("/js/script.js");

		FileUtils.copyInputStreamToFile(css, new File(diffDir, "css/style.css"));
		FileUtils.copyInputStreamToFile(js, new File(diffDir, "js/script.js"));

		List<String> htmlDiff = convertToHTML(diff);

		configurateTemplateSystem();

		Template temp = cfg.getTemplate("report_diff.html");

		Map<String, Object> root = new HashMap<String, Object>();
		root.put("diff", htmlDiff);

		FileWriter sw = new FileWriter(new File(diffDir, "index.html"));
		temp.process(root, sw);
	}

	public void generateDiffReport2(List<Delta> deltas, File target) throws Exception {
		File diffDir = new File(target, "diff-reports");
		FileUtils.forceMkdir(diffDir);

		InputStream css = P2DiffApp.class.getResourceAsStream("/css/style.css");
		InputStream js = P2DiffApp.class.getResourceAsStream("/js/script.js");

		FileUtils.copyInputStreamToFile(css, new File(diffDir, "css/style.css"));
		FileUtils.copyInputStreamToFile(js, new File(diffDir, "js/script.js"));


		configurateTemplateSystem();

		Template temp = cfg.getTemplate("report_delta.html");

		Map<String, Object> root = new HashMap<String, Object>();
		root.put("deltas", deltas);

		FileWriter sw = new FileWriter(new File(diffDir, "index.html"));
		temp.process(root, sw);
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
			result = generateTag("info", diffLine);
		else if (diffLine.startsWith("@@") && diffLine.endsWith("@@"))
			result = generateTag("range", diffLine);
		else if (diffLine.startsWith("+"))
			result = generateTag("added", diffLine);
		else if (diffLine.startsWith("-"))
			result = generateTag("deleted", diffLine);
		else if (diffLine.startsWith(" "))
			result = generateTag("nochange", diffLine);
		else
			result = diffLine;

		return result;
	}

}
