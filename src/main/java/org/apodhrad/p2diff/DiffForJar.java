package org.apodhrad.p2diff;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import freemarker.template.TemplateException;

public class DiffForJar {

	private DiffJar originalJar;
	private DiffJar revisedJar;

	private File newFile;

	private Map<String, Object> diff = new HashMap<String, Object>();

	public DiffForJar(File originalJar, File revisedJar) {
		this.originalJar = new DiffJar(originalJar);
		this.revisedJar = new DiffJar(revisedJar);
	}

	public List<String> generateDiff() throws IOException {
		List<String> diff = new ArrayList<String>();
		if (originalJar.computeCRC32() == revisedJar.computeCRC32()) {
			return diff;
		}

		Collection<String> originalPaths = originalJar.listRelativePaths();
		Collection<String> revisedPaths = revisedJar.listRelativePaths();

		Set<Delta> deltas = new HashSet<Delta>();

		for (String path : originalPaths) {
			deltas.add(new Delta(path, originalJar.getFile(path), revisedJar.getFile(path)));
		}
		for (String path : revisedPaths) {
			deltas.add(new Delta(path, originalJar.getFile(path), revisedJar.getFile(path)));
		}
		
		for (Delta delta: deltas) {
			DiffForFile dff = new DiffForFile(delta.getOriginalFile(), delta.getRevisedFile());
			dff.setBaseDir(originalJar.getExtractedJarFile().getParentFile());
			diff.addAll(dff.generateDiff());
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
