package org.apodhrad.p2diff.file;

import java.io.File;

import org.apodhrad.p2diff.util.P2BundleUtils;

public class Delta implements Comparable<Delta> {

	private static final String UNKNOWN_VERSION = "-";

	private String path;
	private String diff;
	
	private File originalFile;
	private File revisedFile;

	public Delta(String path, File originalFile, File revisedFile) {
		if (path == null) {
			throw new IllegalArgumentException();
		}
		if (originalFile == null && revisedFile == null) {
			throw new IllegalArgumentException();
		}

		this.path = path;
		this.originalFile = originalFile;
		this.revisedFile = revisedFile;
	}

	public String getPath() {
		return path;
	}

	public File getOriginalFile() {
		return originalFile;
	}

	public File getRevisedFile() {
		return revisedFile;
	}

	public String getStatus() {
		String status = "same";
		if (originalFile == null) {
			status = "added";
		} else if (revisedFile == null) {
			status = "deleted";
		} else if (!getOriginalVersion().equals(getRevisedVersion())) {
			status = "changed";
		}
		return status;
	}

	public String getOriginalVersion() {
		return getVersion(originalFile);
	}

	public String getRevisedVersion() {
		return getVersion(revisedFile);
	}

	protected String getVersion(File file) {
		if (file != null) {
			String version = P2BundleUtils.getBundleVersion(file);
			if (version != null) {
				return version;
			}
		}
		return UNKNOWN_VERSION;
	}
	
	public String setDiff(String diff) {
		return this.diff = diff;
	}

	public String getDiff() {
		return diff;
	}

	@Override
	public int compareTo(Delta delta) {
		return getPath().compareTo(delta.getPath());
	}

	@Override
	public String toString() {
		return "Delta [path=" + path + ", originalFile=" + originalFile + ", revisedFile=" + revisedFile + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + path.hashCode();
		result = prime * result + ((originalFile == null) ? 0 : originalFile.hashCode());
		result = prime * result + ((revisedFile == null) ? 0 : revisedFile.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Delta other = (Delta) obj;
		if (originalFile == null) {
			if (other.originalFile != null)
				return false;
		} else if (!originalFile.equals(other.originalFile))
			return false;
		if (path == null) {
			if (other.path != null)
				return false;
		} else if (!path.equals(other.path))
			return false;
		if (revisedFile == null) {
			if (other.revisedFile != null)
				return false;
		} else if (!revisedFile.equals(other.revisedFile))
			return false;
		return true;
	}

}
