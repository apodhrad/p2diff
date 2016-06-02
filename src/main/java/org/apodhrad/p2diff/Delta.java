package org.apodhrad.p2diff;

import java.io.File;

public class Delta implements Comparable<Delta> {

	private String path;
	private File originalFile;
	private File revisedFile;

	public Delta(String path, File originalFile, File revisedFile) {
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
		} else if (originalFile.getName().equals(revisedFile.getName())) {
			status = "changed";
		}
		return status;
	}

	public String getOriginalVersion() {
		return "1.2.2";
	}

	public String getRevisedVersion() {
		return "1.2.3";
	}

	public String getDiff() {
		return "cool diff";
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
		result = prime * result + ((originalFile == null) ? 0 : originalFile.hashCode());
		result = prime * result + ((path == null) ? 0 : path.hashCode());
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
