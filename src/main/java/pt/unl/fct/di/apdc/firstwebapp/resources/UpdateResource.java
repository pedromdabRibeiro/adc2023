package pt.unl.fct.di.apdc.firstwebapp.resources;

import java.util.logging.Logger;

import javax.ws.rs.Consumes;

import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.google.cloud.datastore.Entity;
import com.google.appengine.repackaged.org.apache.commons.codec.digest.DigestUtils;
import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.Transaction;
import com.google.gson.Gson;

import pt.unl.fct.di.apdc.firstwebapp.util.AuthToken;
import pt.unl.fct.di.apdc.firstwebapp.util.UpdatePasswordRequest;
import pt.unl.fct.di.apdc.firstwebapp.util.UpdateUserAttRequest;

@Path("/Update")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class UpdateResource {

	private static final Logger LOG = Logger.getLogger(LoginResource.class.getName());
	private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
	private final Gson g = new Gson();
	private AuthTokenverification tokenchecker = new AuthTokenverification();

	public UpdateResource() {

	}

	@PUT
	@Path("/v1")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Response UpdateOwnRole(AuthToken token, @QueryParam("role") String role) {
		Transaction txn = datastore.newTransaction();
		{
			LOG.fine("Attempt to update user role: " + token.username + " to: " + role);

			Key userKey = datastore.newKeyFactory().setKind("User").newKey(token.username);
			if (txn.get(userKey) != null)
				if (Integer.parseInt(role) > 0)
					if (Integer.parseInt(role) < 4) {
						Entity child = txn.get(userKey);
						Entity updatedEntity = Entity.newBuilder(child.getKey()).set("Role", Integer.parseInt(role))
								.build();
						txn.update(updatedEntity);

						AuthToken at = new AuthToken(token.username, (int) txn.get(userKey).getLong("Role"));
						txn.commit();
						 if (txn.isActive()) {
							    txn.rollback();
							  }
							
						return Response.ok().entity(g.toJson(at)).build();
					}
			txn.rollback();
			return Response.status(Status.FORBIDDEN).entity("Invalid role").build();
		}
	}

	@POST
	@Path("/UpdateRole1")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Response UpdateUserRole(AuthToken token, @QueryParam("username") String username,@QueryParam("role")String role) {
		  
		if (tokenchecker.validToken(token)) {
			Transaction txn = datastore.newTransaction();
			{
				LOG.fine("Attempt to update user role: " + username + " to: " + role + "done by: " + token.username
						+ "with role permissions: " + token.role);

				Key userKey = datastore.newKeyFactory().setKind("User").newKey(username);
				if (txn.get(userKey) != null)
					if (Integer.parseInt(role) > 0 && Integer.parseInt(role) < 4) {
						Entity child = txn.get(userKey);
						if (token.role > 2 || (token.role == 2 && child.getLong("Role") < 2)) {
							Entity.Builder updatedEntity = Entity.newBuilder(child).set("Role", Integer.parseInt(role));
							txn.update(updatedEntity.build());
							txn.commit();
							return Response.ok().build();
						}
						txn.rollback();
						return Response.status(Status.FORBIDDEN).entity("WRONG PERMISSION FROM UPGRADER USER").build();
					}
				txn.rollback();
				return Response.status(Status.FORBIDDEN).entity("WRONG ROLE LEVEL").build();
			}
		}
		return Response.status(Status.FORBIDDEN).entity("INVALIDE TOKEN PLEASE RELOG").build();
	}

	@POST
	@Path("/UpdatePassword")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response UpdatePassword( UpdatePasswordRequest data) {
		String password=data.getPassword();
		String oldpw=data.getOldpw();
		String confpassword=data.getConfpassword();
		AuthToken token=data.getToken();
		if (tokenchecker.validToken(token)) {
			Transaction txn = datastore.newTransaction();
			{
				LOG.fine("Attempt to change user password:" + token.username);

				Key userKey = datastore.newKeyFactory().setKind("User").newKey(token.username);
				if (txn.get(userKey) != null && password.equals(confpassword)) {
					Entity child = txn.get(userKey);
					if (child.getString("password").equals(DigestUtils.sha512Hex(oldpw))) {
						Entity updatedEntity = Entity.newBuilder(child)
								.set("password", (DigestUtils.sha512Hex(password))).build();
						txn.update(updatedEntity);
						txn.commit();
						return Response.ok().build();
					}
					txn.rollback();
					return Response.status(Status.FORBIDDEN).entity("WRONG PASSWORD").build();

				}
				txn.rollback();
				return Response.status(Status.FORBIDDEN).entity("WRONG PERMISSION").build();
			}
		}
		return Response.status(Status.FORBIDDEN).entity("INVALID TOKEN PLEASE RELOG").build();
	}
	@POST
	@Path("/UpdateUser")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response UpdateUserAtt( UpdateUserAttRequest data) {
		
		AuthToken token=data.getToken();
		String username=data.getUsername();
		String email=data.getEmail();
		String Name=data.getName();
		Boolean Openprofile=data.getOpenprofile();
		Integer TelNumb=data.getTelNumb();
		Integer Phonenumber=data.getPhonenumber();
		String Job=data.getJob();
		String SecondaryAddress=data.getSecondaryAddress();
		String placeOfWork=data.getPlaceOfWork();
		String MainAddress=data.getMainAddress();
		Integer NIF=data.getNif();
		Boolean State=data.getState();
		Integer Role =data.getRole();
		String  pfp=data.getpfpURL();
		
		if (tokenchecker.validToken(token)) {
			Key userKey = datastore.newKeyFactory().setKind("User").newKey(username);
			Transaction txn = datastore.newTransaction();
			Entity child = txn.get(userKey);
			if (child != null)
				if ((token.role == 0 && token.username.equals(child.getKey().toString().split("name=")[1].split("}")[0])
						|| token.role > (int) child.getLong("Role")||token.role==3)) {
					if ((email.equals("")||email.equals(null)) &&token.role != 0)
						email = child.getString("email");
					if ((Name.equals("")||Name.equals(null))|&&| token.role != 0)
						Name = child.getString("Name");
					if (Openprofile == false)
						Openprofile= child.getBoolean("Public");
					if (TelNumb == null)
						TelNumb = (int) child.getLong("Tp#");
					if (Phonenumber == null)
						Phonenumber = (int) child.getLong("P#");
					if (Job.equals("")||Job.equals(null))
						Job = txn.get(userKey).getString("Occupation");
					if (SecondaryAddress.equals("")||SecondaryAddress.equals(null))
						SecondaryAddress = child.getString("Sec Address");
					if (placeOfWork.equals("")||placeOfWork.equals(null))
						placeOfWork = child.getString("Place of Work");
					if (MainAddress.equals("")||MainAddress.equals(null))
						MainAddress = child.getString("Main Addr");
					if (NIF == null)
						NIF = (int) child.getLong("nif");
					String password = child.getString("password");
					if(State==false)
						State=child.getBoolean("State");
					if(Role==null||Role>token.getRole())
						Role=(int) child.getLong("Role");
					if(pfp.equals(null)||pfp=="")
					pfp=child.getString("photoURL");
					
					Entity person = Entity.newBuilder(userKey).set("password", password).set("email", email)
							.set("Name", Name).set("Public", Openprofile).set("Tp#", TelNumb).set("P#", Phonenumber)
							.set("Occupation", Job).set("Place of Work", placeOfWork).set("Main Addr", MainAddress)
							.set("Sec Address", SecondaryAddress).set("photoURL", pfp).set("nif", NIF).set("State", State).set("Role", Role)
							.build();
					txn.put(person);
					txn.commit();
					if (txn.isActive()) {
					    txn.rollback();
					  }
					
					return Response.ok().build();
				}
			if (txn.isActive())
				txn.rollback();
			return Response.status(Status.FORBIDDEN).entity("User doesnt exists").build();

		}
		return Response.status(Status.EXPECTATION_FAILED).entity("INVALID TOKEN PLEASE RELOG").build();
	}

}
