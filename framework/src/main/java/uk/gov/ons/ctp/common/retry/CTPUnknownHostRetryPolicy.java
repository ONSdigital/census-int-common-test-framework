package uk.gov.ons.ctp.common.retry;


import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.RetryContext;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.context.RetryContextSupport;
import org.springframework.util.ClassUtils;

import java.util.Collections;
import java.util.List;

/**
 * A RetryPolicy that will retry ONLY when the thrown exception's cause belongs to a list of retryableExceptions (see
 * the retryForException implementation).
 *
 * By default, it will retry 3 times and only if the thrown exception's cause is an unchecked exception.
 *
 * This RetryPolicy is used in our Spring Integration flows when picking up a message off a queue.
 *
 */
@Slf4j
public class CTPUnknownHostRetryPolicy implements RetryPolicy {

    private static final int DEFAULT_MAX_ATTEMPTS = 3;
    private static final String RUNTIME_EXCEPTION = "java.lang.RuntimeException";
    private static final String UNKNOWN_HOST_EXCEPTION = "org.springframework.web.client.RestClientException";

    private volatile int maxAttempts;
    private volatile List<String> retryableExceptions;  // TODO Make it a List<Class<? extends Throwable>
    private volatile List<String> infinitleyRetryableExceptions;  // TODO Make it a List<Class<? extends Throwable>


    public CTPUnknownHostRetryPolicy() {
        this(DEFAULT_MAX_ATTEMPTS);
    }

    public CTPUnknownHostRetryPolicy(int maxAttempts) {
        this(maxAttempts, Collections.singletonList(RUNTIME_EXCEPTION), Collections.singletonList(UNKNOWN_HOST_EXCEPTION));
    }

    public CTPUnknownHostRetryPolicy(int maxAttempts, List<String> retryableExceptions, List<String> infinitleyRetryableExceptions) {
        this.maxAttempts = maxAttempts;
        this.retryableExceptions = retryableExceptions;
        this.infinitleyRetryableExceptions = infinitleyRetryableExceptions;
    }

    /**
     * To decide if a retrial is required.
     *
     * @param context the RetryContext
     * @return true if retrial is required
     */
    public boolean canRetry(RetryContext context) {
        Throwable lastThrowable = context.getLastThrowable();
        return lastThrowable == null || this.isInfiniteException(lastThrowable) || (this.retryForException(lastThrowable) &&
                context.getRetryCount() < this.maxAttempts);
    }

    /**
     * Has to be there as per interface RetryPolicy
     * @param status the RetryContext
     */
    public void close(RetryContext status) {
    }

    /**
     * Identical implementation to SimpleRetryPolicy
     * @param context the RetryContext
     * @param throwable the Throwable
     */
    public void registerThrowable(RetryContext context, Throwable throwable) {
        CTPRetryContext simpleContext = (CTPRetryContext)context;
        simpleContext.registerThrowable(throwable);
    }

    /**
     * Identical implementation to SimpleRetryPolicy
     *
     * @param parent the RetryContext
     * @return the RetryContext
     */
    public RetryContext open(RetryContext parent) {
        return new CTPRetryContext(parent);
    }

    /**
     * To determine if a retry is required for the given Throwable
     *
     * @param ex the Throwable to check for retry
     * @return true if a retry is required
     */
    private boolean retryForException(Throwable ex) {
        try {
            for (String className : retryableExceptions) {
                if (Class.forName(className).isInstance(ex.getCause())) {
                    return true;
                }
            }
        } catch (ClassNotFoundException e) {
            log.error("msg {} - cause {}", e.getMessage(), e.getCause());
        }
        return false;
    }

    private boolean isInfiniteException(Throwable ex) {
        try {
            for (String className : infinitleyRetryableExceptions) {
                if (Class.forName(className).isInstance(ex.getCause())) {
                    if (ex.getMessage().contains("org.apache.http.conn.HttpHostConnectException"))
                        return true;
                }
            }
        } catch (ClassNotFoundException e) {
            log.error("msg {} - cause {}", e.getMessage(), e.getCause());
        }
        return false;
    }

    /**
     * Identical implementation to SimpleRetryPolicy
     * @return a representation string
     */
    public String toString() {
        return ClassUtils.getShortName(this.getClass()) + "[maxAttempts=" + this.maxAttempts + "]";
    }

    /**
     * To mirror implementation in SimpleRetryPolicy
     */
    private static class CTPRetryContext extends RetryContextSupport {
        /**
         * Method to mirror implementation in SimpleRetryPolicy
         * @param parent the RetryContext
         */
        public CTPRetryContext(RetryContext parent) {
            super(parent);
        }
    }
}

