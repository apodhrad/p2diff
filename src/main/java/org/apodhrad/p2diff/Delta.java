package org.apodhrad.p2diff;

import java.io.File;

public class Delta {

	private String path;
	private File originalFile;
	private File revisedFile;

	public Delta(String path, File originalFile, File revisedFile) {
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

	public void setOriginalFile(File originalFile) {
		this.originalFile = originalFile;
	}

	public File getRevisedFile() {
		return revisedFile;
	}

	public void setRevisedFile(File revisedFile) {
		this.revisedFile = revisedFile;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((path == null) ? 0 : path.hashCode());
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
		if (path == null) {
			if (other.path != null)
				return false;
		} else if (!path.equals(other.path))
			return false;
		return true;
	}

}
