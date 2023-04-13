package pt.unl.fct.di.apdc.firstwebapp.resources;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import com.google.gson.Gson;
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.QueryResults;
import com.google.cloud.datastore.Transaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pt.unl.fct.di.apdc.firstwebapp.util.AuthToken;
import pt.unl.fct.di.apdc.firstwebapp.util.TokenUsernameData;

@Path("List")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class ListResource {

	public ListResource() {

	}
	
	private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
	private final Gson g = new Gson();
	private AuthTokenverification tokenchecker = new AuthTokenverification();

	
	@POST
	@Path("/pfp")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getUSERPFP(AuthToken token) {
		
		if (tokenchecker.validToken(token)) {
			Transaction txn = datastore.newTransaction();
			Key userKey = datastore.newKeyFactory().setKind("User").newKey(token.username);
			Entity child = txn.get(userKey);
			if (child != null) {
				String jsonResponse = g.toJson(child.getString("photoURL"));
				txn.rollback();
			return Response.ok(jsonResponse).build();
			}
		return null;
		
	}
		return null;
	}
	
	@POST
	@Path("/v1")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response DeleteUserRole(AuthToken token) {
		if (tokenchecker.validToken(token)) {
			Query<Entity> query = Query.newEntityQueryBuilder().setKind("User").build();
			QueryResults<Entity> results = datastore.run(query);
			List<Map<String, Object>> entities = new ArrayList<>();
			if (token.role == 0)
				while (results.hasNext()) {
					Entity entity = results.next();
					if (entity.getLong("Role") == 0 && entity.getBoolean("State")!=false&&entity.getBoolean("Public")==true) 
					{
						Map<String, Object> entityData = new HashMap<>();
						entityData.put("username", entity.getKey().toString().split("name=")[1].split("}")[0]);
						entityData.put("email", entity.getString("email"));
						entityData.put("Name", entity.getString("Name"));
						entities.add(entityData);
					}

				}
			if (token.role == 1) {
				while (results.hasNext()) {
					Entity entity = results.next();
					if (entity.getLong("Role") == 0) {
						Map<String, Object> entityData = new HashMap<>();
						entityData.put("username", entity.getKey().toString().split("name=")[1].split("}")[0]);
						for (String property : entity.getNames()) {
							if (!property.equals("password"))
								entityData.put(property, entity.getValue(property).get().toString().split("value=")[0]);
						}
						entities.add(entityData);
					}
				}
			}

			if (token.role == 2) {
				while (results.hasNext()) {
					Entity entity = results.next();
					if (entity.getLong("Role") < 2) {
						Map<String, Object> entityData = new HashMap<>();
						entityData.put("username", entity.getKey().toString().split("name=")[1].split("}")[0]);
						for (String property : entity.getNames()) {
							if (!property.equals("password"))
								entityData.put(property, entity.getValue(property).get().toString().split("value=")[0]);
						}
						entities.add(entityData);
					}
				}
			}
			if (token.role > 2) {
				while (results.hasNext()) {
					Entity entity = results.next();
					Map<String, Object> entityData = new HashMap<>();
					entityData.put("username", entity.getKey().toString().split("name=")[1].split("}")[0]);
					for (String property : entity.getNames()) {
						if (!property.equals("password"))
							entityData.put(property, entity.getValue(property).get().toString().split("value=")[0]);
					}
					entities.add(entityData);
				}
			}

			
			String jsonResponse = g.toJson(entities);
			return Response.ok(jsonResponse).build();
		}
		return Response.status(Status.EXPECTATION_FAILED).entity("INVALID TOKEN PLEASE RELOG").build();
	}

	@POST
	@Path("/FriendList")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response CheckFriendList( TokenUsernameData data) {
		AuthToken token=data.getToken();
		String username=data.getUsername();
		if (tokenchecker.validToken(token)) {
			Query<Entity> query = Query.newEntityQueryBuilder().setKind("FriendList").build();
			QueryResults<Entity> results = datastore.run(query);
			List<Map<String, Object>> entities = new ArrayList<>();
			if (token.role == 0 && !token.username.equals(username))
				return Response.status(Status.FORBIDDEN).entity("YOU CANT CHECK OTHERS FRIENDS").build();
			
				while (results.hasNext()) {
					Entity entity = results.next();
					if (!entity.getString("friendUsername").equals(username)) {
						Map<String, Object> entityData = new HashMap<>();
						entityData.put("Friend username", entity.getKey().toString().split("name=")[1].split("}")[0]);
						entities.add(entityData);
					}
				}
			String jsonResponse = g.toJson(entities);
			return Response.ok(jsonResponse).build();
		}
		return Response.status(Status.EXPECTATION_FAILED).entity("INVALID TOKEN PLEASE RELOG").build();
	}

	@DELETE
	@Path("/order66")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response MAYHEM(AuthToken token) {
		Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
		Query<Entity> query = Query.newEntityQueryBuilder().build();
		QueryResults<Entity> results = datastore.run(query);
		while (results.hasNext()) {
			Entity entity = results.next();
			datastore.delete(entity.getKey());
		}
		return Response.ok("what have you done D:").build();
	}

}
