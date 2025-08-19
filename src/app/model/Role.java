package app.model;

public class Role {
	private String roleId, roleName, roleDescription;

	private int available;

	public String getRoleId() {
		return roleId;
	}

	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public String getRoleDescription() {
		return roleDescription;
	}

	public void setRoleDescription(String roleDescription) {
		this.roleDescription = roleDescription;
	}

	public int getAvailable() {
		return available;
	}

	public void setAvailable(int available) {
		this.available = available;
	}

	public Role(String roleId, String roleName, String roleDescription, int available) {
		this.roleId = roleId;
		this.roleName = roleName;
		this.roleDescription = roleDescription;
		this.available = available;
	}

	public Role(String roleName, String roleDescription, int available) {
		this.roleName = roleName;
		this.roleDescription = roleDescription;
		this.available = available;
	}

}
