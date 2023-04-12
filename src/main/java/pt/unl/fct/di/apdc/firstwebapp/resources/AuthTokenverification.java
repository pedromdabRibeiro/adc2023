package pt.unl.fct.di.apdc.firstwebapp.resources;

import java.util.logging.Logger;


import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.PathElement;
import com.google.cloud.datastore.Transaction;

import pt.unl.fct.di.apdc.firstwebapp.util.AuthToken;


public class AuthTokenverification {

	public AuthTokenverification() {

	}

	private static final Logger LOG = Logger.getLogger(LoginResource.class.getName());
	private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

	public boolean validToken(AuthToken token) {
		Transaction txn = datastore.newTransaction();
		{
			LOG.fine("Attempt to verify authToken: " + token.username);

			Key userKey = datastore.newKeyFactory().setKind("User").newKey(token.username);
			Key ctrsKey = datastore.newKeyFactory().addAncestor(PathElement.of("User", token.username))
					.setKind("AuthToken").newKey(token.tokenID);
			Entity childtoken = txn.get(ctrsKey);
			if (txn.get(userKey) != null)
				if (txn.get(ctrsKey) != null)
					if (childtoken.getLong("DelTime") == token.expirationData)
						if (!(System.currentTimeMillis() >= token.expirationData)) {
							if (childtoken.getString("username").equals(token.username))
								return true;
							return false;
						} else {
							txn.delete(ctrsKey);
							txn.commit();
							return false;
						}
			txn.rollback();
			return false;
		}
	}

}
