/*-
 * !--
 * For support and inquiries regarding this library, please contact:
 *   soporte@kanopus.cl
 *
 * Project website:
 *   https://www.kanopus.cl
 * %%
 * Copyright (C) 2025 Pablo Díaz Saavedra
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * --!
 */
package cl.kanopus.common;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.MDC;

/**
 * Per-thread request context (unique id, user, token, etc.).
 * <p>
 * Values are stored in a {@link ThreadLocal}, so they are never visible from another thread. Because threads are usually pooled, callers must always release the context with {@link #destroyContext()} (ideally in a {@code finally} block) or use the try-with-resources helper {@link #open()}:
 *
 * <pre>{@code
 * try (ThreadContext.Scope scope = ThreadContext.open()) {
 *     ThreadContext.setToken(token);
 *     // ...
 * }
 * }</pre>
 */
public class ThreadContext {

    private static final ThreadContext INSTANCE = new ThreadContext();
    private static final ThreadLocal<Map<String, Object>> CONTEXT = new ThreadLocal<>();

    private static final String KEY_THREAD_UNIQUE_ID = "threadUniqueId";
    private static final String KEY_THREAD_TIME_START = "threadTimeStart";
    private static final String KEY_THREAD_APP_KEY = "threadAppKey";
    private static final String KEY_THREAD_USER_EMAIL = "threadUserEmail";
    private static final String KEY_THREAD_USER_ID = "threadUserId";
    private static final String KEY_THREAD_USER_NAME = "threadUserName";
    private static final String KEY_THREAD_COMPANY_ID = "threadCompanyId";
    private static final String KEY_THREAD_TOKEN = "threadToken";

    private ThreadContext() {
    }

    /**
     * @return the singleton instance (it holds no state; all the API is static).
     * @deprecated the context is kept in a {@link ThreadLocal}; use the static methods directly.
     */
    @Deprecated(since = "4.20.0", forRemoval = true)
    public static ThreadContext getInstance() {
        return INSTANCE;
    }

    public static String getThreadId() {
        return String.valueOf(Thread.currentThread().threadId());
    }

    /**
     * Returns the unique id of the current thread context. The value is created by {@link #initContext()} and includes the context start timestamp concatenated with the thread name.
     *
     * @return the unique id of the current context, or {@code null} if the context has not been initialized
     */
    public static String getThreadUniqueId() {
        return (String) getObject(KEY_THREAD_UNIQUE_ID);
    }

    public static void setAppKey(String appKey) {
        addObject(KEY_THREAD_APP_KEY, appKey);
        if (appKey != null) {
            MDC.put(KEY_THREAD_APP_KEY, appKey);
        } else {
            MDC.remove(KEY_THREAD_APP_KEY);
        }
    }

    public static String getAppKey() {
        return (String) getObject(KEY_THREAD_APP_KEY);
    }

    public static String getToken() {
        return (String) getObject(KEY_THREAD_TOKEN);
    }

    public static void setToken(String token) {
        addObject(KEY_THREAD_TOKEN, token);
    }

    public static String getUserEmail() {
        return (String) getObject(KEY_THREAD_USER_EMAIL);
    }

    public static void setUserEmail(String user) {
        addObject(KEY_THREAD_USER_EMAIL, user);
    }

    public static Long getUserId() {
        return (Long) getObject(KEY_THREAD_USER_ID);
    }

    public static void setUserId(Long userId) {
        addObject(KEY_THREAD_USER_ID, userId);
    }

    public static String getUserName() {
        return (String) getObject(KEY_THREAD_USER_NAME);
    }

    public static void setUserName(String userName) {
        addObject(KEY_THREAD_USER_NAME, userName);
    }

    public static String getCompanyId() {
        return (String) getObject(KEY_THREAD_COMPANY_ID);
    }

    public static void setCompanyId(String companyId) {
        addObject(KEY_THREAD_COMPANY_ID, companyId);
    }

    /**
     * Returns the context map for the current thread. If no context exists, an empty (detached) map is returned.
     *
     * @return the context map for the current thread
     */
    @SuppressWarnings("rawtypes")
    public static Map getContextMap() {
        Map<String, Object> contextMap = CONTEXT.get();
        return contextMap != null ? contextMap : new HashMap<>();
    }

    /**
     * Adds an object to the current thread's context map under the specified key.
     *
     * @param objectKey
     *            the key under which to store the object
     * @param objectToInsert
     *            the object to store
     */
    public static void addObject(String objectKey, Object objectToInsert) {
        Map<String, Object> contextMap = CONTEXT.get();
        if (contextMap == null) {
            contextMap = new HashMap<>();
            CONTEXT.set(contextMap);
        }
        contextMap.put(objectKey, objectToInsert);
    }

    /**
     * Returns the object stored in the current thread's context map associated with the given key.
     *
     * @param objectName
     *            the name/key of the stored object. Usually the class name starting with a lowercase letter.
     * @return the object stored in the current thread's ThreadContext, or null if not present
     */
    public static Object getObject(String objectName) {
        Map<String, Object> contextMap = CONTEXT.get();
        return contextMap != null ? contextMap.get(objectName) : null;
    }

    public static long getTotalTimeMillis() {
        long totalTimeMillis = 0;
        if (getObject(KEY_THREAD_TIME_START) instanceof Date timeStart) {
            totalTimeMillis = System.currentTimeMillis() - timeStart.getTime();
        }
        return totalTimeMillis;
    }

    /**
     * Starts a fresh context for the current thread, discarding any value left by a previous task executed on the same (pooled) thread.
     */
    public static void initContext() {
        destroyContext();
        Date start = new Date();
        String threadUniqueId = start.getTime() + "_" + Thread.currentThread().getName();
        addObject(KEY_THREAD_UNIQUE_ID, threadUniqueId);
        addObject(KEY_THREAD_TIME_START, start);
        MDC.put(KEY_THREAD_UNIQUE_ID, threadUniqueId);
    }

    /**
     * Initializes a fresh context for the current thread and returns a handle that destroys it when closed, for use in try-with-resources.
     *
     * @return a scope whose {@link Scope#close()} calls {@link #destroyContext()}
     */
    public static Scope open() {
        initContext();
        return new Scope();
    }

    /**
     * Removes the context of the current thread and the MDC entries managed by this class.
     */
    public static void destroyContext() {
        CONTEXT.remove();
        MDC.remove(KEY_THREAD_UNIQUE_ID);
        MDC.remove(KEY_THREAD_APP_KEY);
    }

    /**
     * Handle returned by {@link ThreadContext#open()}; closing it destroys the current thread context.
     */
    public static final class Scope implements AutoCloseable {

        private Scope() {
        }

        @Override
        public void close() {
            destroyContext();
        }
    }
}
