package org.apodhrad.p2diff;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.*;

import difflib.Delta;
import difflib.DiffUtils;
import difflib.Patch;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class DiffForFile {
	
	/**
	 * Path to the original file
	 */
	private String originalPath;
	
	/**
	 * Path to the revised file
	 */
	private String revisedPath;
	
	private List<String> originalLines;
	private List<String> revisedLines;
	
	/**
	 * Wrapping tag name
	 */
	private String tag = "span";
	
	private List<String> unifiedDiff;
	
	private Configuration cfg;
	public String template = "hidden.html";
	
	/**
	 * Assign paths and load files
	 * @param originalPath
	 * @param revisedPath
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	DiffForFile(String originalPath, String revisedPath) throws IOException, URISyntaxException
	{
		this.originalPath = originalPath;
		this.revisedPath = revisedPath; 
		
		originalLines = FileUtils.readLines(new File(originalPath));
		revisedLines = FileUtils.readLines(new File(revisedPath));
	}
	
	/**
	 * Convert Diff to HTML
	 * @param unifiedDiff
	 * @return
	 */
	private String convertToHTML(String unifiedDiff)
	{
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
	
	/**
	 * Generate tag
	 * @param className Name of CSS class
	 * @param content Tag content
	 * @return
	 */
	private String generateTag(String className, String content)
	{
		return "<" + tag + " class=\"" + className + "\">" + content + "</" + tag + ">";
	}
	
	/**
	 * Generate HTML file
	 * @return
	 * @throws Exception 
	 */
	public String generate() throws Exception
	{
		Patch patch = null;
		
		try {
		patch = DiffUtils.diff(originalLines, revisedLines);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (!originalLines.equals(revisedLines)) {
		
			unifiedDiff = DiffUtils.generateUnifiedDiff(originalPath, revisedPath, originalLines, patch, 1);
			
			List<String> htmlDiff = new ArrayList<String>();
			for (String line : unifiedDiff) {
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
		
		return "";
	}
	
	private void configurateTemplateSystem() throws IOException
	{
		cfg = new Configuration(Configuration.VERSION_2_3_22);
		cfg.setDefaultEncoding("UTF-8");
		cfg.setDirectoryForTemplateLoading(new File(this.getClass().getResource("/templates").getFile()));
	}
	
	//// SETTERS AND GETTERS
	
	public void setTag(String tag)
	{
		this.tag = tag; 
	}
	
	public String getTag()
	{
		return this.tag;
	}
	
	public List<String> getDiff()
	{
		return unifiedDiff;
	}
}
