package pt.unl.fct.di.apdc.firstwebapp.util;

	
public class TokenUsernameData {
	public String username;
	private  AuthToken token;
	 
	public TokenUsernameData() {
		
	}
	public TokenUsernameData(String username,  AuthToken token) {
		this.username=username;
		this.token=token;
	}
	public AuthToken getToken() {
		return token;
	}
	public String getUsername() {
		return username;
	}
}
