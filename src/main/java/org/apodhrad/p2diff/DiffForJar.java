package org.apodhrad.p2diff;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apodhrad.jdownload.manager.util.UnpackUtils;
import org.apodhrad.p2diff.util.ResourceUtils;

import freemarker.template.TemplateException;

public class DiffForJar {

	/**
	 * Path to the original file
	 */
	private String originalResource;

	/**
	 * Path to the revised file
	 */
	private String revisedResource;

	private File newFile;

	private Map<String, Object> diff = new HashMap<String, Object>();

	public DiffForJar(String originalPath, String revisedPath, File newFile) throws Exception {
		this.originalResource = originalPath;
		this.revisedResource = revisedPath;
		this.newFile = newFile;

		List<File> files1 = extractJar(new File(originalResource).getAbsolutePath() + ".temp", this.originalResource);
		List<File> files2 = extractJar(new File(revisedResource).getAbsolutePath() + ".temp", this.revisedResource);

		diff = getDiffFromFiles(files1, files2);
	}

	/**
	 * Extract JAR archive to a folder and return its files
	 * 
	 * @param dest
	 * @param jar
	 * @return
	 * @throws IOException
	 */
	private ArrayList<File> extractJar(String dest, String jar) throws IOException {
		UnpackUtils.unpack(jar, dest);
		ArrayList<File> store = new ArrayList<File>();
		ResourceUtils.listf(new File(dest).getAbsolutePath(), store);
		return store;
	}

	private Map convertArrayToMap(Object[] array) {
		Map<Integer, Object> map = new HashMap<>();

		for (int i = 0; i < array.length; i++)
			map.put(i, array[i]);

		return map;
	}

	/**
	 * Converts name so it will be the same whatever origin it comes from
	 * 
	 * @param file
	 * @param resource
	 *            Origin
	 * @return
	 */
	private String getName(File file, String resource) {
		return file.getPath().replace(new File(resource).getAbsolutePath() + ".temp/", "");
	}

	/**
	 * Generates diff as a map for two JAR files
	 * 
	 * @param files1
	 * @param files2
	 * @return
	 * @throws Exception
	 */
	private Map<String, Object> getDiffFromFiles(List<File> files1, List<File> files2) throws Exception {
		for (int i = 0; i < files1.size(); i++) {
			for (int y = 0; y < files2.size(); y++) {
				if (!(files1.get(i).getName().startsWith(".") || files2.get(y).getName().startsWith("."))) {
					if (getName(files1.get(i), originalResource).equals(getName(files2.get(y), revisedResource))) {
						if (files1.get(i).isDirectory() || files2.get(y).isDirectory()) {
							if (getName(files1.get(i), originalResource)
									.equals(getName(files2.get(y), revisedResource))) {
								diff.put(getName(files1.get(i), originalResource), Status.SAME);
							}
						} else {
							DiffForFile dff = new DiffForFile(files1.get(i), files2.get(y));
							String generated = dff.generate();

							if (generated.equals(""))
								diff.put(getName(files1.get(i), originalResource), Status.SAME);
							else
								diff.put(getName(files1.get(i), originalResource), dff.generate());
						}
					}
				}
			}

			if (!diff.containsKey(getName(files1.get(i), originalResource))) {
				diff.put(getName(files1.get(i), originalResource), Status.DELETED);
			}
		}

		for (int y = 0; y < files2.size(); y++) {
			if (!diff.containsKey(getName(files2.get(y), revisedResource)))
				diff.put(getName(files2.get(y), revisedResource), Status.ADDED);
		}

		return diff;
	}

	/**
	 * Generate HTML and return it
	 * 
	 * @param template
	 * @return
	 * @throws IOException
	 * @throws TemplateException
	 */
	public String generateHTML(String template) throws IOException, TemplateException {
		HTMLGenerator generator = new HTMLGenerator(diff, newFile.getPath());
		return generator.generateHTML(template + ".html");
	}
}
