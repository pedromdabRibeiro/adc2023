package pt.unl.fct.di.apdc.firstwebapp.util;

public class UpdateUserAttRequest {
	private String username;
	private String email;
	private String name;
	private Boolean openprofile;
	private Integer telNumb;
	private Integer phonenumber;
	private String job;
	private String placeOfWork;
	private String mainAddress;
	private String secondaryAddress;
	private Integer nif;
	private Boolean state;
	private AuthToken token;
	private Integer role;
	private String pfpURL;
	
	public UpdateUserAttRequest() {
	}

	public UpdateUserAttRequest(String username, String email, String name, Boolean openprofile, Integer telNumb,
			Integer phonenumber, String job, String placeOfWork, String mainAddress, String secondaryAddress,
			String pfpURL,Integer nif, Boolean state, AuthToken token, Integer Role) {
		this.username = username;
		this.email = email;
		this.name = name;
		this.openprofile = openprofile;
		this.telNumb = telNumb;
		this.phonenumber = phonenumber;
		this.job = job;
		this.placeOfWork = placeOfWork;
		this.mainAddress = mainAddress;
		this.secondaryAddress = secondaryAddress;
		this.nif = nif;
		this.state = state;
		this.token = token;
		this.role = Role;
		this.pfpURL = pfpURL;
	}

	public Integer getRole() {
		return role;
	}

	public String getUsername() {
		return username;
	}

	public String getEmail() {
		return email;
	}

	public String getName() {
		return name;
	}

	public Boolean getOpenprofile() {
		return openprofile;
	}

	public Integer getTelNumb() {
		return telNumb;
	}

	public Integer getPhonenumber() {
		return phonenumber;
	}

	public String getJob() {
		return job;
	}

	public String getPlaceOfWork() {
		return placeOfWork;
	}

	public String getMainAddress() {
		return mainAddress;
	}

	public String getSecondaryAddress() {
		return secondaryAddress;
	}

	public Integer getNif() {
		return nif;
	}

	public AuthToken getToken() {
		return token;
	}

	public Boolean getState() {
		return state;
	}
	public String getpfpURL() {
		return pfpURL;
	}
}