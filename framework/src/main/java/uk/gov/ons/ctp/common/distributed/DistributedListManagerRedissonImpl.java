package uk.gov.ons.ctp.common.distributed;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.redisson.api.RBucket;
import org.redisson.api.RKeys;
import org.redisson.api.RedissonClient;

/**
 * 
 * A generic distributed list (a crude map effectively) (or lists plural) of things T
 * This is a Redisson specific implementation of the DistributedListManager.
 * Using this, application code does not need to know about the redisson specifics, other than 
 * obtaining the client connection.
 * @param <T> our thing type
 */
public class DistributedListManagerRedissonImpl<T> extends DistributedManagerBase implements DistributedListManager<T> {

  private Integer timeToLive;
  private RedissonClient redissonClient;

  /**
   * create the impl
   * @param keyRoot each list that gets saved with this impl we be stored with this prefix in its key
   * @param redissonClient the client connected to the underlying redis server
   * @param timeToLive the time that each list added will be allowed to live in seconds before the underlying redis server purges it
   */
  public DistributedListManagerRedissonImpl(String keyRoot, RedissonClient redissonClient, Integer timeToLive) {
    super(keyRoot);
    this.timeToLive = timeToLive;
    this.redissonClient = redissonClient;
  }

  @Override
  public void saveList(String key, List<T> list) {
    RBucket<List<T>> bucket = redissonClient.getBucket(createKey(key));
    bucket.set(list, timeToLive, TimeUnit.SECONDS);
  }

  @Override
  public List<T> findListForInstance(String key) {
    return getList(createKey(key));
  }

  @Override
  public List<T> findListForAllInstances(String key) {
    RKeys keys = redissonClient.getKeys();
    Iterable<String> matches = keys.getKeysByPattern(createAllInstancesKey(key));
    Map<String, List<T>> allLists = stream(matches).collect(Collectors.toMap(k -> k, k -> getList(k)));
    List<T> all = new ArrayList<>();
    allLists.values().forEach(all::addAll);
    return all;
  }

  /**
   * Get the entry for the given key as is - allows other methods to get using either a simple key or pre-prepared
   * or known composite key
   * @param key the key to look for as is without any root prefix
   * @return the list
   */
  private List<T> getList(String key) {
    RBucket<List<T>> bucket = redissonClient.getBucket(key);
    return bucket.get();
  }

  @Override
  public Map<String, List<T>> findAllLists() {
    RKeys keys = redissonClient.getKeys();
    Iterable<String> matches = keys.getKeysByPattern(createKey("*"));
    return stream(matches).collect(Collectors.toMap(k -> k, k -> getList(k)));
  }  
  
  
  /**
   * Convenience method
   * @param in an Iterable
   * @return a Stream of the iterable
   */
  private static <T> Stream<T> stream(Iterable<T> in) {
    return StreamSupport.stream(in.spliterator(), false);
  }

  @Override
  public void deleteList(String key) {
    RBucket<List<T>> bucket = redissonClient.getBucket(createKey(key));
    bucket.delete();
  }


}
