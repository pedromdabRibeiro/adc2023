package pt.unl.fct.di.apdc.firstwebapp.util;

public class UpdateUserRoleRequest {
  private String username;
  private String role;
  private AuthToken token;
  public UpdateUserRoleRequest() {
  }
  public UpdateUserRoleRequest(String username,String role, AuthToken token) {
	  this.username=username;
	  this.role=role;
	  this.token=token;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public AuthToken getToken() {
	  return token;
  }
  public String getRole() {
    return role;
  }

  public void setRole(String role) {
    this.role = role;
  }
}