package pt.unl.fct.di.apdc.firstwebapp.resources;

import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;

import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.Transaction;
import pt.unl.fct.di.apdc.firstwebapp.util.AuthToken;
import pt.unl.fct.di.apdc.firstwebapp.util.TokenUsernameData;

@Path("Delete")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class DeletionResource {

	public DeletionResource() {

	}

	private static final Logger LOG = Logger.getLogger(LoginResource.class.getName());
	private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
	private AuthTokenverification tokenchecker= new AuthTokenverification();
	@DELETE
	@Path("/v1")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response DeleteUserRole(TokenUsernameData data) {
		AuthToken token=data.getToken();
		String username=data.getUsername();
		Transaction txn = datastore.newTransaction();
		{	LOG.fine("Attempt to delete user: " + username +"With permissions from role:"+ token.role);
			if(tokenchecker.validToken(token)) {
			Key userKey = datastore.newKeyFactory().setKind("User").newKey(username);
			Entity child = txn.get(userKey);
			if (txn.get(userKey) != null && ((token.username.equals(username)) || token.role > (int)child.getLong("Role"))) {
				if(!child.getString("photoURL").equals("")) {
					deleteObject(child.getString("photoURL").split("https://storage.googleapis.com/apdc-2023-avaliacao-individual.appspot.com/")[1]);
				}
				FriendResource friendResource = new FriendResource();
				friendResource.DeleteFriendprivate(username);
				txn.delete(userKey);
				txn.commit();
				if (txn.isActive()) {
				    txn.rollback();
				  }
				
				return Response.ok().build();
			}
			txn.rollback();
			return Response.status(Status.FORBIDDEN).entity("WRONG PERMISSION").build();
		}
			return Response.status(Status.EXPECTATION_FAILED).entity("INVALID TOKEN PLEASE RELOG").build();
			}
		
	}

	public static void deleteObject( String objectName) {
	    // The ID of your GCP project
	     String projectId = "apdc-2023-avaliacao-individual";

	    // The ID of your GCS bucket
	     String bucketName = "apdc-2023-avaliacao-individual.appspot.com";

	    // The ID of your GCS object
	    // String objectName = "your-object-name";

	    Storage storage = StorageOptions.newBuilder().setProjectId(projectId).build().getService();
	    Blob blob = storage.get(bucketName, objectName);
	    if (blob == null) {
	      System.out.println("The object " + objectName + " wasn't found in " + bucketName);
	      return;
	    }

	    // Optional: set a generation-match precondition to avoid potential race
	    // conditions and data corruptions. The request to upload returns a 412 error if
	    // the object's generation number does not match your precondition.
	    Storage.BlobSourceOption precondition =
	        Storage.BlobSourceOption.generationMatch(blob.getGeneration());

	    storage.delete(bucketName, objectName, precondition);

	    System.out.println("Object " + objectName + " was deleted from " + bucketName);
	}
	}
	
