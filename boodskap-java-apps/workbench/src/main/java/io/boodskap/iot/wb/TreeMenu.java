package io.boodskap.iot.wb;

import java.io.Serializable;

public class TreeMenu implements Serializable{
	
	private static final long serialVersionUID = -7259163961342148551L;
	
	private final TreeMenuType type;
	private final String label;
	private final Object userData;

	public TreeMenu(TreeMenuType type, String label) {
		this(type, label, null);
	}

	public TreeMenu(TreeMenuType type, String label, Object userData) {
		this.type = type;
		this.label = label;
		this.userData = userData;
	}
	
	public String getLabel() {
		return label;
	}

	public Object getUserData() {
		return userData;
	}

	public TreeMenuType getType() {
		return type;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((label == null) ? 0 : label.hashCode());
		result = prime * result + ((userData == null) ? 0 : userData.hashCode());
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
		TreeMenu other = (TreeMenu) obj;
		if (label == null) {
			if (other.label != null)
				return false;
		} else if (!label.equals(other.label))
			return false;
		if (userData == null) {
			if (other.userData != null)
				return false;
		} else if (!userData.equals(other.userData))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return label;
	}

}
