package pt.unl.fct.di.apdc.firstwebapp.resources;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
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
import com.google.cloud.datastore.PathElement;
import com.google.cloud.datastore.Transaction;
import com.google.gson.Gson;

import pt.unl.fct.di.apdc.firstwebapp.util.AuthToken;
import pt.unl.fct.di.apdc.firstwebapp.util.LoginData;

@Path("login")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class LoginResource {

	private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
	private final Gson g = new Gson();

	public LoginResource() {
	}



	@POST
	@Path("/v1")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Response Login(LoginData data) {
		
		Transaction txn = datastore.newTransaction();
		{
		
			Key userKey = datastore.newKeyFactory().setKind("User").newKey(data.username);
			
			if (txn.get(userKey) != null) {
				if (txn.get(userKey).getString("password").equals(DigestUtils.sha512Hex(data.password))) 
					if(txn.get(userKey).getBoolean("State")==true){
					AuthToken at = new AuthToken(data.username,(int)(txn.get(userKey).getLong("Role")));
					Key ctrsKey = datastore.newKeyFactory()
							.addAncestor(PathElement.of("User", data.username))
							.setKind("AuthToken").newKey(at.tokenID);
					Entity authtoken = Entity.newBuilder(ctrsKey)
							.set("username",data.username)
							.set("DelTime", at.expirationData)
							.set("role",txn.get(userKey).getLong("Role") )
							.build();
					txn.put(authtoken);
					txn.commit();
					if (txn.isActive()) {
					    txn.rollback();
					  }
					
					return Response.ok().entity(g.toJson(at)).build();
				}
			}	txn.rollback();
			return Response.status(Status.FORBIDDEN).entity("Incorrect username or password").build();
		}
	}

/*
	@POST
	@Path("/v2")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Response loginUser2(LoginData data, 
			@Context HttpServletRequest request,
			@Context HttpHeaders headers) {
		LOG.fine("Attempt to login user: " + data.username);
		//KEYS SHOULD BE GENERATED OUTSIDE TRANSACTIONS
		//Construct the key from the username
		Key userKey = datastore.newKeyFactory().setKind("User").newKey(data.username);
		Key ctrsKey = datastore.newKeyFactory()
				.addAncestor(PathElement.of("User", data.username))
				.setKind("UserStats").newKey("counters");
		//Generate automatically a key
		Key logKey = datastore.allocateId(
				datastore.newKeyFactory()
				.addAncestor(PathElement.of("User", data.username))
				.setKind("UserLog").newKey()
				);
		Transaction txn = datastore.newTransaction();
		try {
			Entity user = txn.get(userKey);
			if(user == null) {
				//Username does not exist
				LOG.warning("Failed login attempt for username: " + data.username);
				return Response.status(Status.FORBIDDEN).build();
			}
			//We get the user stats from the storage
			Entity stats = txn.get(ctrsKey);
			if(stats == null) {
				stats = Entity.newBuilder(ctrsKey)
						.set("user_stats_logins", 0L)
						.set("user_stats_failed", 0L)
						.set("user_first_login", Timestamp.now())
						.set("user_last_login", Timestamp.now())
						.build();
			}
			String hashedPWD = (String) user.getString("password");
			if (hashedPWD.equals(DigestUtils.sha512Hex(data.password))) {
				//Password is correct
				//Construct the logs
				Entity log = Entity.newBuilder(logKey)
						.set("user_login_ip", request.getRemoteAddr())
						.set("user_login_host", request.getRemoteHost())
						.set("user_login_latlon", 
								//Does not index this property value
								StringValue.newBuilder(headers.getHeaderString("X-AppEngine-CityLatLong"))
								.setExcludeFromIndexes(true).build()
								)
						.set("user_login_city", headers.getHeaderString("X-AppEngine-City"))
						.set("user_login_country", headers.getHeaderString("X-AppEngine-Country"))
						.set("user_login_time",  Timestamp.now())
						.build();
				//Get the user statistics and updates it
				//Copying information every time a user logins maybe 
				Entity ustats = Entity.newBuilder(ctrsKey)
						.set("user_stats_logins", 1L + stats.getLong("user_stats_logins"))
						.set("user_stats_failed", 0L)
						.set("user_first_login", stats.getLong("user_first_login"))
						.set("user_last_login", Timestamp.now())
						.build();
				
				//Batch operation
				txn.put(log,ustats);
				txn.commit();
				
				//Return token
				AuthToken at = new AuthToken();
				LOG.info("User '" + data.username + "' logged in sucessfully");
				return Response.ok(g.toJson(at)).build();
			} else {
				//Incorrect password
				//Compying here is even woerse. Propose a better solution!
				Entity ustats = Entity.newBuilder(ctrsKey)
						.set("user_stats_logins", stats.getLong("user_stats_logins"))
						.set("user_stats_failed", 1L + stats.getLong("user_stats_failed"))
						.set("user_first_login", stats.getLong("user_first_login"))
						.set("user_last_login", stats.getLong("user_last_login"))
						.set("user_last_attempt",  Timestamp.now())
						.build();
				txn.put(ustats);
				txn.commit();
				LOG.warning("Wrong password for username: " + data.username);
				return Response.status(Status.FORBIDDEN).build();
			}
		} catch (Exception e) {
			txn.rollback();
			LOG.severe(e.getMessage());
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		} finally {
			if (txn.isActive()) {
				txn.rollback();
				return Response.status(Status.INTERNAL_SERVER_ERROR).build();
			}
		}
	}
	@GET
	@Path("/attempts")
	public Response attempts() {
		return Response.ok().entity("attempts="+attempts+" sucesses="+sucesses).build();
	}
	/*
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/LOGOUT")
	public Response Logout(AuthToken token) {
		localStorage.removeItem(token);
		}
	}
	*/
}
