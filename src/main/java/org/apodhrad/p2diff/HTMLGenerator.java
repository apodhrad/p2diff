package org.apodhrad.p2diff;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class HTMLGenerator {
	
	private Map<String, Object> diff = new HashMap<String, Object>();
	private Configuration cfg;
	private String htmlDiff;
	private String filename;
	
	HTMLGenerator(Map<String, Object> diff)
	{
		this(diff, "");
	}
	
	HTMLGenerator(Map<String, Object> diff, String filename) {
		this.filename = filename;
		this.diff = diff;
	}
	public String generateHTML(String template) throws IOException, TemplateException
	{
		configurateTemplateSystem();
		Template temp = cfg.getTemplate(template);

		Map<String, Object> root = new HashMap<String, Object>();
		
		root.put("filename", filename);
		root.put("diff", diff);
		
		StringWriter sw = new StringWriter();
		temp.process(root, sw);
		
		return sw.toString();
	}
	
	private void configurateTemplateSystem() throws IOException
	{
		cfg = new Configuration(Configuration.VERSION_2_3_22);
		cfg.setDefaultEncoding("UTF-8");
		cfg.setDirectoryForTemplateLoading(new File(this.getClass().getResource("/templates").getFile()));
	}
}
