package pt.unl.fct.di.apdc.firstwebapp.resources;

import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.PathElement;
import com.google.cloud.datastore.Transaction;
import pt.unl.fct.di.apdc.firstwebapp.util.AuthToken;

@Path("Logout")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class LogOutResource {

	public LogOutResource() {

	}

	private static final Logger LOG = Logger.getLogger(LoginResource.class.getName());
	private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
	private AuthTokenverification tokenchecker = new AuthTokenverification();

	@DELETE
	@Path("/logout")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response Logout(AuthToken token) {
		Transaction txn = datastore.newTransaction();
		{
			LOG.fine("Attempt to logout user: " + token.username );
			if (tokenchecker.validToken(token)) {
				Key ctrsKey = datastore.newKeyFactory().addAncestor(PathElement.of("User", token.username))
						.setKind("AuthToken").newKey(token.tokenID);
				txn.delete(ctrsKey);
				txn.commit();
				return Response.ok().build();
			}
			txn.rollback();

			return Response.status(Status.FORBIDDEN).entity("INVALID TOKEN PLEASE RELOG").build();
		}

	}

}
