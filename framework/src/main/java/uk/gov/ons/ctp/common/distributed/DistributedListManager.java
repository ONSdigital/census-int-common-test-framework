package uk.gov.ons.ctp.common.distributed;

import java.util.List;
import java.util.Map;


/**
 * Genericized Interface for apps to code against for distributed lists of lists.
 * This manager allows for the distributed storage/retrieval of arbitrary data of type T into
 * a list of lists, which like a map are keyed.
 * Effectively :
 * 
 * Map<String, List<T>>
 * 
 * where the manager will allow the caller to retrieve either just its own keyed list or
 * a super list representing the content of all other application instances using the same 
 * configured manager.
 *
 * @param <T> The List<type> to be stored
 */
public interface DistributedListManager<T> {
  
  /**
   * Store the list against the given key in the distributed store
   * @param listKey the key to store this instances list against
   * @param list the list to store
   */
  void saveList(String listKey, List<T> list);
  
  
  /**
   * get the list associated with the given key that was stored by this application instance
   * @param listKey the key
   * @return the instance list for the key
   */
  List<T> findListForInstance(String listKey);
  
  
  /**
   * get the list associated with the given key that was stored by any application instance
   * ie the list returned is the flattened, 'super' list of lists for key
   * so if 3 instances of the same app, using an identically configured list manager, store individually :
   * 
   * instance 1 store "keyA" : [1,2,3]
   * instance 2 store "keyA" : [4,5,6]
   * instance 3 store "keyA" : [7,8,9]
   * 
   * the returned list for "keyA" will be [1,2,3,4,5,6,7,8,9]
   * 
   * @param listKey the key
   * @return the super list for the key
   */
  List<T> findListForAllInstances(String listKey);
  
  /**
   * Get ALL the lists THIS manager instance has stored 
   * @return the map keyed by key name, of lists
   */
  Map<String, List<T>> findAllLists();
  
  
  /**
   * Remove from the store the instance list stored by key 
   * @param listKey the key
   */
  void deleteList(String listKey);
}
