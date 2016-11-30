package uk.gov.ons.ctp.common.distributed;

import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

public class DistributedLockManagerRedissonImpl extends DistributedManagerBase implements DistributedLockManager {

  private Integer timeToLive;
  private RedissonClient redissonClient;
  // this is the managers list of locks it has created and that have not been deleted from redis
  // each entry in this set is the raw key, ie not the 'decorated' key
  private Set<String> locks;

  /**
   * create the impl
   * 
   * @param keyRoot each lock that is written with this impl will be stored with
   *          this prefix in its key
   * @param redissonClient the client connected to the underlying redis server
   * @param timeToLive the time that each lock added will be allowed to live in
   *          seconds before the underlying redis server purges it
   */
  public DistributedLockManagerRedissonImpl(String keyRoot, RedissonClient redissonClient, Integer timeToLive) {
    super(keyRoot);
    this.locks = Collections.synchronizedSet(new TreeSet<String>());
    this.timeToLive = timeToLive;
    this.redissonClient = redissonClient;
  }

  @Override
  public boolean isLocked(String key) {
    boolean locked = false;
    if (locks.contains(key)) {
      RLock lock = redissonClient.getLock(createKey(key));
      if (lock != null) {
        locked = lock.isLocked();
      }
    }
    return locked;
  }

  @Override
  public boolean lock(String key) {
    boolean locked = false;
    RLock lock = redissonClient.getLock(createKey(key));
    if (lock != null) {
      locked = lock.tryLock();
      if (locked) {
        locked = lock.expire(timeToLive, TimeUnit.SECONDS);
        locks.add(key);
      }
    }
    return locked;
  }

  @Override
  public void unlock(String key) {
    if (locks.contains(key)) {
      RLock lock = redissonClient.getLock(createKey(key));
      if (lock != null) {
        if (lock.isHeldByCurrentThread()) {
          lock.unlock();
          locks.remove(key);
        }
      }
    }
  }
}
