package uk.gov.ons.ctp.common.firestore;

import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.FieldPath;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.common.error.CTPException.Fault;

/**
 * This class is responsible for communication with Firestore.
 *
 * <p>It runs as a singleton so that it can reuse it's Firestore connection.
 */
class FirestoreService {
  private static final Logger log = LoggerFactory.getLogger(FirestoreService.class);

  private static FirestoreService instance = new FirestoreService();

  private Firestore firestore;
  private String gcpProject;

  public static synchronized FirestoreService instance() {
    return instance;
  }

  private FirestoreService() {
    firestore = FirestoreOptions.getDefaultInstance().getService();

    gcpProject = firestore.getOptions().getProjectId();
    log.info("Connected to Firestore project: " + gcpProject);
  }

  public long objectExists(
      String collectionName,
      String key,
      Long newerThan,
      String contentCheckPath,
      String expectedValue)
      throws CTPException {
    // Run a query
    String schema = gcpProject + "-" + collectionName;
    FieldPath fieldPathForId = FieldPath.documentId();
    ApiFuture<QuerySnapshot> query =
        firestore.collection(schema).whereEqualTo(fieldPathForId, key).get();

    // Wait for query to complete and get results
    QuerySnapshot querySnapshot;
    try {
      querySnapshot = query.get();
    } catch (Exception e) {
      String failureMessage =
          "Exception caught whilst attempting to find object in schema '"
              + schema
              + "' for key '"
              + key
              + "'";
      log.error(e, failureMessage);
      throw new CTPException(Fault.SYSTEM_ERROR, e, failureMessage);
    }

    // Bail out if nothing found
    if (querySnapshot.isEmpty()) {
      return -1;
    }

    // Get hold of candidate object
    QueryDocumentSnapshot targetDocument = querySnapshot.getDocuments().get(0);
    long objectUpdateMillis = targetDocument.getUpdateTime().toDate().getTime();

    // Optionally, only regard the object as existing if it is newer than the specified time
    if (newerThan != null) {
      if (!(objectUpdateMillis > newerThan)) {
        return -1;
      }
    }

    // Optionally, determine if named field is in the expected state
    if (contentCheckPath != null) {
      String[] parts = contentCheckPath.split("\\.");
      FieldPath fieldPath = FieldPath.of(parts);
      Object actualValue = targetDocument.get(fieldPath);

      if (!expectedValue.equals(actualValue)) {
        return -1;
      }
    }

    return objectUpdateMillis;
  }
}
