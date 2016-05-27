package org.apodhrad.p2diff;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import com.google.common.io.Files;

import freemarker.template.TemplateException;

public class DiffManager {
	
	/**
	 * Original resource
	 */
	private File zip1;
	
	/**
	 * Revised resource
	 */
	private File zip2;
	
	/**
	 * Algorithm for filtering results (regex)
	 */
	private String filter;
	
	private Map<String, Object> diff = new HashMap<String, Object>();
	
	private File target;
	
	/**
	 * Map of files which are prepared for a HTML conversion
	 */
	private Map<File, File> prepared = new HashMap<File, File>();
	
	DiffManager(File zip1, File zip2, File target)
	{
		this(zip1, zip2, "(.*)", target);
	}
	
	DiffManager(File zip1, File zip2, String filter, File target)
	{
		this.zip1 = zip1;
		this.zip2 = zip2;
		this.filter = filter;
		this.target = target;
	}
	
	/**
	 * Returns the same path whatever the origin is
	 * @param path
	 * @param resource
	 * @return
	 */
	private String getPath(String path, File resource)
	{
		return path.replace(resource.getPath() + "/", "");
	}
	
	/**
	 * Compare two files
	 * @param file1
	 * @param file2
	 * @return
	 * @throws IOException
	 */
	private boolean compare(File file1, File file2) throws IOException
	{
		if (!file1.isDirectory() && !file2.isDirectory())
			return Files.asByteSource(file1).contentEquals(Files.asByteSource(file2));
		else
			return file1.getName().equals(file2.getName());
	}
	
	/**
	 * Generate diff as a map and save
	 * @throws IOException 
	 * @throws Exception
	 */
	private void generateDiff() throws IOException 
	{
		ArrayList<File> filesInZip1 = new ArrayList<File>();
		ArrayList<File> filesInZip2 = new ArrayList<File>();;
		
		Resource.listf(zip1.getPath(), filesInZip1);
		Resource.listf(zip2.getPath(), filesInZip2);	

		for (File file1 : filesInZip1) {
			for (File file2 : filesInZip2) {
				if (file1.getName().matches(filter) && file2.getName().matches(filter)) {
					String[] onlyNameF1 = getPath(file1.getPath(), zip1).split("_");
					String[] onlyNameF2 = getPath(file2.getPath(), zip2).split("_");
					
					if (!(file1.getName().equals(zip1.getName()) || file2.getName().equals(zip2.getName()))) {
						if (!(file1.isDirectory() || file2.isDirectory())) {
							if (file1.getName().equals(file2.getName())) {
								if (compare(file1, file2)) {
									if (compare(file1, file2)) {
										diff.put(getPath(file1.getPath(), zip1), Status.SAME);
									} else
										diff.put(getPath(file1.getPath(), zip1), Status.RENAMED);
								}
							}
							
						    if ((FilenameUtils.getExtension(file1.getPath()).equals("jar")) && (FilenameUtils.getExtension(file2.getPath()).equals("jar"))) {
						    	if ((!compare(file1, file2)) && onlyNameF1[0].equals(onlyNameF2[0])) {
									prepared.put(file1, file2);
									diff.put(getPath(file1.getPath(), zip1), getPath(file2.getPath(), zip2));
								}
						    }
						}
					}
				}
			}
		}
		
		for (File file1 : filesInZip1) {
			if (file1.getName().matches(filter)) {
				if (!(file1.getName().equals(zip1.getName()))) {
					if (!(file1.isDirectory())) {	
						if (!diff.containsKey(getPath(file1.getPath(), zip1))) {
							diff.put(getPath(file1.getPath(), zip1), Status.DELETED);
						}
					}
				}
			}
		}
		
		for (File file2 : filesInZip2) {
			if (file2.getName().matches(filter)) {
				if (!(file2.getName().equals(zip2.getName()))) {
					if (!(file2.isDirectory())) {
						if (!diff.containsKey(getPath(file2.getPath(), zip2)))
							diff.put(getPath(file2.getPath(), zip2), Status.ADDED);
					}
				}
			}
		}
	}
	
	/**
	 * Generate HTML and return it
	 * @return
	 * @throws IOException 
	 * @throws TemplateException 
	 * @throws Exception
	 */
	public String generateHTML() throws IOException, TemplateException 
	{
		generateDiff();
		
		prepared.forEach((file1, file2) -> {
			try {
				File file = new File(target.getParent() + "/" + getPath(file1.getPath(), zip1) + "-diff.html");
				FileUtils.writeStringToFile(file, (new DiffForJar(file1.getPath(), file2.getPath(), file).generateHTML("inner_layout")));
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		
		HTMLGenerator generator = new HTMLGenerator(diff);
		return generator.generateHTML("layout.html");
	}
	
	public void generate() throws IOException, TemplateException {
		InputStream css = Diff.class.getResourceAsStream("/css/style.css");
		InputStream js = Diff.class.getResourceAsStream("/js/script.js");
		
		FileUtils.writeStringToFile(new File(target.getParent() + "/css/style.css"), readInputStream(css));
		FileUtils.writeStringToFile(new File(target.getParent() + "/js/script.js"), readInputStream(js));
		
		FileUtils.writeStringToFile(target, generateHTML());
	}
	
	private static String readInputStream(InputStream is) throws IOException {
		StringWriter writer = new StringWriter();
		IOUtils.copy(is, writer);
		return writer.toString();
	}
	
}
