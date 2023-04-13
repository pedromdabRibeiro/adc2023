package pt.unl.fct.di.apdc.firstwebapp.resources;


import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.google.appengine.repackaged.org.apache.commons.codec.digest.DigestUtils;
import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.Transaction;

import pt.unl.fct.di.apdc.firstwebapp.util.RegisterData;

@Path("/register")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class RegisterResource {

	public RegisterResource() {
	}

	private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
	
	@PUT
	@Path("/v1")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response registerUser2(RegisterData data) {
		Transaction txn = datastore.newTransaction();
		if (!data.validuserdata()) {
			txn.rollback();
			return Response.status(Status.FORBIDDEN).entity("Incorrect registration data").build();
			}
		Key userKey = datastore.newKeyFactory().setKind("User").newKey(data.username);
		if (txn.get(userKey) == null) {
			Entity person = Entity.newBuilder(userKey).set("password",(DigestUtils.sha512Hex(data.password))).set("email", data.email).set("Name", data.Name)
					.set("Public", data.Openprofile).set("Tp#", data.TelNumb).set("P#", data.Phonenumber).set("Occupation", data.Job)
					.set("Place of Work", data.placeOfWork).set("Main Addr", data.MainAddress).set("Sec Address", data.SecondaryAddress)
					.set("photoURL", data.photoURL).set("nif",data.NIF).set("State",false).set("Role",0).build();
			txn.add(person);
			txn.commit();
			return Response.ok().build();
		}
		if(txn.isActive())
			txn.rollback();
		return Response.status(Status.FORBIDDEN).entity("User already exists").build();
		
		
	}
	@PUT
	@Path("/v2")
	public Response registerSUser() {
		Transaction txn = datastore.newTransaction();
		
		Key userKey = datastore.newKeyFactory().setKind("User").newKey("Su");
		if (txn.get(userKey) == null) {
			Entity person = Entity.newBuilder(userKey).set("password",(DigestUtils.sha512Hex("1"))).set("email", "email").set("Name", "superUser")
					.set("Public", false ).set("Tp#", 1).set("P#", 1).set("Occupation","")
					.set("Place of Work", "").set("Main Addr", "").set("Sec Address","").set("photoURL", "")
					.set("nif",1).set("State",true).set("Role",3).build();
			txn.add(person);
			txn.commit();
			return Response.ok().build();
		}
		return Response.status(Status.FORBIDDEN).entity("User already exists").build();
		
	}

}