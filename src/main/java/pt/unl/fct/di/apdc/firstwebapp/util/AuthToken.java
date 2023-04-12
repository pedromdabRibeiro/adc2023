package pt.unl.fct.di.apdc.firstwebapp.util;

import java.util.UUID;

public class AuthToken {
  public static final long EXPIRATION_TIME = 1000 * 60 * 60 * 2; // 2h
  public String username;
  public String tokenID;
  public long creationData;
  public long expirationData;
  public int role;

  public AuthToken(String username, int role) {
    this.role = role;
    this.username = username;
    this.tokenID = UUID.randomUUID().toString();
    this.creationData = System.currentTimeMillis();
    this.expirationData = this.creationData + AuthToken.EXPIRATION_TIME;
  }

  public AuthToken() {
    // TODO Auto-generated constructor stub
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getTokenID() {
    return tokenID;
  }

  public void setTokenID(String tokenID) {
    this.tokenID = tokenID;
  }

  public long getCreationData() {
    return creationData;
  }

  public void setCreationData(long creationData) {
    this.creationData = creationData;
  }

  public long getExpirationData() {
    return expirationData;
  }

  public void setExpirationData(long expirationData) {
    this.expirationData = expirationData;
  }

  public int getRole() {
    return role;
  }
  public void setRole(int role) {
	     this.role=role;
	  }
}
