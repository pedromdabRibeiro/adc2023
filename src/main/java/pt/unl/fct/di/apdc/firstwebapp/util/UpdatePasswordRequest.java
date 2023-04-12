package pt.unl.fct.di.apdc.firstwebapp.util;
public class UpdatePasswordRequest {
  private String oldpw;
  private String password;
  private String confpassword;
  private  AuthToken token;
  public UpdatePasswordRequest() {
  }

  public UpdatePasswordRequest(String oldpw, String password, String confpassword, AuthToken token) {
	  setOldpw(oldpw);
	  setPassword(password);
	  setConfpassword(confpassword);
	  this.token=token;
  }
  public AuthToken getToken() {
	    return token;
	  }
  public String getOldpw() {
    return oldpw;
  }

  public void setOldpw(String oldpw) {
    this.oldpw = oldpw;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getConfpassword() {
    return confpassword;
  }

  public void setConfpassword(String confpassword) {
    this.confpassword = confpassword;
  }
}