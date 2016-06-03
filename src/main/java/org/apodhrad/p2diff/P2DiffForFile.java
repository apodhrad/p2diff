package org.apodhrad.p2diff;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

import difflib.DiffUtils;
import difflib.Patch;

public class P2DiffForFile {

	private File baseDir;

	private DiffFile originalFile;
	private DiffFile revisedFile;

	/**
	 * 
	 * @param originalPath
	 * @param revisedPath
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public P2DiffForFile(File originalFile, File revisedFile) throws IOException {
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

	public List<String> generateDiff() throws IOException {
		List<String> diff = new ArrayList<String>();

		if (originalFile.isBinaryFile() || revisedFile.isBinaryFile()) {
			if (originalFile.file == null) {
				diff.add("There is the new binary file " + revisedFile.file.getName());
				return diff;
			}
			if (revisedFile.file == null) {
				diff.add("The binary file " + originalFile.file.getName() + " was removed");
				return diff;
			}
			if (FileUtils.checksumCRC32(originalFile.file) == FileUtils.checksumCRC32(revisedFile.file)) {
				return diff;
			}
			diff.add("Binary files " + originalFile + " and " + revisedFile + " differ");
			return diff;
		}

		List<String> originalLines = originalFile.getLines();
		List<String> revisedLines = revisedFile.getLines();

		if (originalLines.isEmpty() && revisedLines.isEmpty()) {
			return diff;
		}

		Patch patch = DiffUtils.diff(originalLines, revisedLines);
		if (patch.getDeltas().isEmpty()) {
			return new ArrayList<String>();
		}

		String originalPath = originalFile.getPath();
		String revisedPath = revisedFile.getPath();

		return DiffUtils.generateUnifiedDiff(originalPath, revisedPath, originalLines, patch, 1);
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

		public boolean isBinaryFile() throws FileNotFoundException, IOException {
			if (file == null) {
				return false;
			}
			FileInputStream in = new FileInputStream(file);
			int size = in.available();
			if (size > 1024)
				size = 1024;
			byte[] data = new byte[size];
			in.read(data);
			in.close();

			int ascii = 0;
			int other = 0;

			for (int i = 0; i < data.length; i++) {
				byte b = data[i];
				if (b < 0x09)
					return true;

				if (b == 0x09 || b == 0x0A || b == 0x0C || b == 0x0D)
					ascii++;
				else if (b >= 0x20 && b <= 0x7E)
					ascii++;
				else
					other++;
			}

			if (other == 0)
				return false;

			return 100 * other / (ascii + other) > 95;
		}
	}

}
