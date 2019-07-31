package uk.gov.ons.ctp.common.firestore;

import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.common.error.CTPException.Fault;

/**
 * This is a Firestore utility class to help test code interact with Firestore.
 *
 * <p>If running in on a developers laptop you can see which environment you'll connect to by
 * running this command: 'gcloud config list --format 'value(core.project)'. You can also check by
 * running this code and looking for FirestoreService.java startup log line, which looks like
 * 'Connected to Firestore project: census-rh-peterb'.
 *
 * <p>You can change to, say, rh-dev, by running 'gcloud config set project census-rh-dev', or
 * failing that by getting the gcloud connection command from the Google Cloud Platform site from
 * 'Kubernetes Engine, Clusters, Connect button', which gives a full command of say 'gcloud beta
 * container clusters get-credentials rh-k8s-cluster --region europe-west2 --project census-rh-dev'
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FirestoreWait {
  private static final Logger log = LoggerFactory.getLogger(FirestoreWait.class);

  // This is the name of the collection to search, eg, 'case'
  @NonNull private String collection;

  // This is the key of the target object in the collection. eg,
  // 'f868fcfc-7280-40ea-ab01-b173ac245da3'
  @NonNull private String key;

  // This is an optional argument to specify the timestamp that the an object must have
  // been updated after. Waiting will continue until the until the update timestamp of the
  // object is greater than this value, or the timeout period is reached. This value is the
  // number of milliseconds since the epoch.
  private Long newerThan;

  // This is an optional path to a field whose content we check to decide if an
  // object has been updated, eg, 'contact.forename' or 'state'. If the target object does not
  // contain the field with the expected value then waiting will continue until it does, or the
  // timeout is reached.
  private String contentCheckPath;

  // This is the value that a field must contain if 'contentCheckPath' has been specified.
  private String expectedValue;

  // This specifies the number of milliseconds that the caller is prepared to wait for an object
  // to appear in Firestore.
  @NonNull private Long timeout;

  /**
   * This method allows the caller to wait for an object to appear in Firestore. If the object is
   * found within the timeout period then it returns with the update time of the object, otherwise
   * it returns with null.
   *
   * <p>The caller can optionally wait for an object to be updated by specifying the updated
   * timestamp or by the content of a named field. If both criteria are specified then both
   * conditions must be satisfied before we regard the object has having arrived in Firestore.
   *
   * @return The update timestamp of a found object, or null if not found within the timeout.
   * @throws CTPException in the event of a failure being detected. This will be of type
   *     Fault.VALIDATION_FAILED if any arguments fail validation, or type Fault.SYSTEM_ERROR if
   *     there is a Firestore exception.
   */
  public Long waitForObject() throws CTPException {
    final long startTime = System.currentTimeMillis();
    final long timeoutLimit = startTime + timeout;

    log.info(
        "Firestore wait. Looking for for collection '"
            + collection
            + "' to contain '"
            + key
            + "' "
            + "for up to '"
            + timeout
            + "'");

    if (newerThan != null) {
      log.info("Firestore wait. Update timestamp must be newer than '" + newerThan + "'");
    } else {
      log.info("Firestore wait. Not waiting on object update timestamp");
    }

    if (contentCheckPath != null) {
      log.info(
          "Firestore wait. Content of '"
              + contentCheckPath
              + "' to contain '"
              + expectedValue
              + "'");
    } else {
      log.info("Firestore wait. Not waiting on object state");
    }

    // Validate matching path+value arguments
    if (contentCheckPath != null ^ expectedValue != null) {
      String errorMessage =
          "Mismatched 'path' and 'value' arguments."
              + " Either both must be supplied or neither supplied";
      log.error(errorMessage);
      throw new CTPException(Fault.VALIDATION_FAILED, errorMessage);
    }

    // Wait until the object appears in Firestore, or we timeout waiting
    boolean found = false;
    long objectUpdateTimestamp;
    do {
      objectUpdateTimestamp =
          FirestoreService.instance()
              .objectExists(collection, key, newerThan, contentCheckPath, expectedValue);
      if (objectUpdateTimestamp > 0) {
        log.debug("Found object");
        found = true;
        break;
      }

      try {
        Thread.sleep(10);
      } catch (InterruptedException e) {
        break;
      }
    } while (System.currentTimeMillis() < timeoutLimit);

    if (!found) {
      log.debug("Failed to find object");
      return null;
    }

    return objectUpdateTimestamp;
  }
}
