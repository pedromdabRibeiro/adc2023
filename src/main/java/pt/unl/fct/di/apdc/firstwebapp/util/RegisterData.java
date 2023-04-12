package pt.unl.fct.di.apdc.firstwebapp.util;

	
public class RegisterData {
	public String username;
	public String password;
	public String confpassword;
	public String email;
	public String Name;
	public boolean  Openprofile;
	public int TelNumb, Phonenumber;
	public String Job, placeOfWork,MainAddress,SecondaryAddress;
	public int NIF;
	public String photoURL;
	
	public RegisterData() {
		
	}
	public RegisterData(String username, String password,String confpassword,String email, String Name,boolean  Openprofile, int TelNumb,int Phonenumber
	,String Job, String placeOfWork, String MainAddress,String SecondaryAddress, int NIF,String photoURL) {
		this.username=username;
		this.password=password;
		this.email=email;
		this.Name=Name;
		this.confpassword=confpassword;
		this.Openprofile=Openprofile;
		this.TelNumb=TelNumb;
		this.Phonenumber=Phonenumber;
		this.Job=Job;
		this.placeOfWork=placeOfWork;
		this.MainAddress=MainAddress;
		this.SecondaryAddress=SecondaryAddress;
		this.NIF=NIF;
		this.photoURL=photoURL;
	}
	
	
	public Boolean validuserdata() {
		if(username!=null&&password.equals(confpassword)&&email!=null&&Name!=null&&password!=null)
		return true;
		
		return false;
	}
}
