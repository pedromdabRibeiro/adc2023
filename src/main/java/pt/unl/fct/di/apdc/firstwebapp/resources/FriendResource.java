package pt.unl.fct.di.apdc.firstwebapp.resources;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

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
import com.google.cloud.datastore.PathElement;
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.QueryResults;
import com.google.cloud.datastore.Transaction;

import pt.unl.fct.di.apdc.firstwebapp.util.AuthToken;
import pt.unl.fct.di.apdc.firstwebapp.util.TokenUsernameData;

@Path("Friends")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class FriendResource {

	public FriendResource() {

	}

	private AuthTokenverification tokenchecker = new AuthTokenverification();
	private static final Logger LOG = Logger.getLogger(LoginResource.class.getName());
	private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

	@POST
	@Path("/add")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response sendFriendRequest(TokenUsernameData data) {
		AuthToken token = data.getToken();
		String username = data.getUsername();
		Transaction txn = datastore.newTransaction();
		{
			LOG.fine("Attempt to verify authToken: " + token.username);
			if (tokenchecker.validToken(token)) {
				Key userKey = datastore.newKeyFactory().setKind("User").newKey(token.username);
				Key friendKey = datastore.newKeyFactory().setKind("User").newKey(username);
				Key listKey = datastore.newKeyFactory().addAncestor(PathElement.of("User", username))
						.setKind("ReqFriendList").newKey(token.username);

				if (txn.get(userKey) != null && txn.get(friendKey) != null)
					if (txn.get(listKey) == null) {
						Entity person = Entity.newBuilder(listKey).set("RequestUsername", token.username)
								.set("username", username).build();
						txn.put(person);
						txn.commit();
						if (txn.isActive()) {
						    txn.rollback();
						  }
						
						return Response.ok("Sent friend request").build();
					} else {
						return Response.status(Status.BAD_REQUEST)
								.entity("Already sent friend request or has friend already added").build();
					}
				txn.rollback();
				return Response.status(Status.FORBIDDEN).entity("Incorrect username").build();
			}
			return Response.status(Status.EXPECTATION_FAILED).entity("INVALID TOKEN PLEASE RELOG").build();
		}
	}

	@POST
	@Path("/accept")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response AcceptFriendRequest(TokenUsernameData data) {
		AuthToken token = data.getToken();
		String username = data.getUsername();
		Transaction txn = datastore.newTransaction();
		{
			LOG.fine("Attempt to verify authToken: " + token.username);
			if (tokenchecker.validToken(token)) {
				Key userKey = datastore.newKeyFactory().setKind("User").newKey(token.username);
				Key friendKey = datastore.newKeyFactory().setKind("User").newKey(username);
				Key reqlistKey = datastore.newKeyFactory().addAncestor(PathElement.of("User", token.username))
						.setKind("ReqFriendList").newKey(data.username);
				Key FrlistKey = datastore.newKeyFactory().addAncestor(PathElement.of("User", username))
						.setKind("FriendList").newKey(token.username);
				Key ownlistKey = datastore.newKeyFactory().addAncestor(PathElement.of("User", token.username))
						.setKind("FriendList").newKey(username);
				if (txn.get(userKey) != null && txn.get(friendKey) != null)
					if (txn.get(reqlistKey) != null) {
						Entity myentry = Entity.newBuilder(FrlistKey).set("friendUsername", username).build();
						Entity friendentry = Entity.newBuilder(ownlistKey).set("friendUsername", token.username)
								.build();
						txn.delete(reqlistKey);
						txn.add(myentry, friendentry);
						txn.commit();
						if (txn.isActive()) {
						    txn.rollback();
						  }
						
						return Response.ok("Accepted friend request").build();
					} else {
						txn.rollback();
						return Response.status(Status.BAD_REQUEST).entity("No such friend request").build();
					}
				txn.rollback();
				return Response.status(Status.FORBIDDEN).entity("Incorrect username").build();
			}
			txn.rollback();
			return Response.status(Status.EXPECTATION_FAILED).entity("INVALID TOKEN PLEASE RELOG").build();
		}
	}

	@DELETE
	@Path("/decline")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response DeclineFriendRequest(TokenUsernameData data) {
		AuthToken token = data.getToken();
		String username = data.getUsername();
		Transaction txn = datastore.newTransaction();
		{	LOG.fine("Attempt to verify authToken: " + token.username);
			if (tokenchecker.validToken(token)) {
			Key userKey = datastore.newKeyFactory().setKind("User").newKey(token.username);
			Key friendKey = datastore.newKeyFactory().setKind("User").newKey(username);
			Key reqlistKey = datastore.newKeyFactory().addAncestor(PathElement.of("User", token.username))
					.setKind("ReqFriendList").newKey(data.username);
			if (txn.get(userKey) != null && txn.get(friendKey) != null)
				if (txn.get(reqlistKey) != null) {
					txn.delete(reqlistKey);
					txn.commit();
					if (txn.isActive()) {
					    txn.rollback();
					  }
					
					return Response.ok("Declined friend request").build();
				} else {txn.rollback();
					return Response.status(Status.BAD_REQUEST).entity("No such friend request").build();
				}
			txn.rollback();
			return Response.status(Status.FORBIDDEN).entity("Incorrect username").build();
		}
			txn.rollback();
			return Response.status(Status.EXPECTATION_FAILED).entity("INVALID TOKEN PLEASE RELOG").build();
		}
	}

	@DELETE
	@Path("/remove")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response DeleteFriend(TokenUsernameData data) {
		AuthToken token = data.getToken();
		String username = data.getUsername();
		Transaction txn = datastore.newTransaction();
		{
			LOG.fine("Attempt to verify authToken: " + token.username);
			if (tokenchecker.validToken(token)) {
			Key userKey = datastore.newKeyFactory().setKind("User").newKey(token.username);
			Key friendKey = datastore.newKeyFactory().setKind("User").newKey(username);
			Key FrlistKey = datastore.newKeyFactory().addAncestor(PathElement.of("User", username))
					.setKind("FriendList").newKey(token.username);
			Key ownlistKey = datastore.newKeyFactory().addAncestor(PathElement.of("User", token.username))
					.setKind("FriendList").newKey(username);
			if (txn.get(userKey) != null && txn.get(friendKey) != null)
				if (txn.get(ownlistKey) != null) {
					txn.delete(ownlistKey);
					txn.delete(FrlistKey);
					txn.commit();
					if (txn.isActive()) {
					    txn.rollback();
					  }
					return Response.ok("Deleted friend ").build();
				} else {txn.rollback();
					return Response.status(Status.BAD_REQUEST).entity("No such friend ").build();
				}
			txn.rollback();
			return Response.status(Status.FORBIDDEN).entity("Incorrect username").build();
		}txn.rollback();
		return Response.status(Status.EXPECTATION_FAILED).entity("INVALID TOKEN PLEASE RELOG").build();
	}
	}

	public void DeleteFriendprivate(String username) {

		Transaction txn = datastore.newTransaction();
		Query<Entity> query = Query.newEntityQueryBuilder().setKind("FriendList").build();
		QueryResults<Entity> results = datastore.run(query);
		List<Map<String, Object>> entities = new ArrayList<>();

		while (results.hasNext()) {
			Entity entity = results.next();
			if (!entity.getString("friendUsername").equals(username)) {
				Map<String, Object> entityData = new HashMap<>();
				entityData.put("Friend username", entity.getKey().toString().split("name=")[1].split("}")[0]);
				entities.add(entityData);
				String entityUsername = entity.getKey().toString().split("name=")[1].split("}")[0];
				Key userKey = datastore.newKeyFactory().setKind("User").newKey(entityUsername);
				Key friendKey = datastore.newKeyFactory().setKind("User").newKey(username);
				Key FrlistKey = datastore.newKeyFactory().addAncestor(PathElement.of("User", username))
						.setKind("FriendList").newKey(entityUsername);
				Key ownlistKey = datastore.newKeyFactory().addAncestor(PathElement.of("User", entityUsername))
						.setKind("FriendList").newKey(username);
				if (txn.get(userKey) != null && txn.get(friendKey) != null)
					if (txn.get(ownlistKey) != null) {
						txn.delete(ownlistKey);
						txn.delete(FrlistKey);

					}
			}
		}
		txn.commit();
		if (txn.isActive()) {
		    txn.rollback();
		  }
		
	}
}
