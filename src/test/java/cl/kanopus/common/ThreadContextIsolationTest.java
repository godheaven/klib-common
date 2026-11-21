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

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;

class ThreadContextIsolationTest {

    @AfterEach
    void cleanup() {
        ThreadContext.destroyContext();
    }

    @Test
    void valuesAreNotVisibleFromOtherThreads() throws Exception {
        ThreadContext.initContext();
        ThreadContext.setToken("main-token");
        ThreadContext.setUserId(1L);

        ExecutorService executor = Executors.newSingleThreadExecutor();
        try {
            Future<Object[]> other = executor.submit(() -> new Object[]{ThreadContext.getToken(), ThreadContext.getUserId(), ThreadContext.getThreadUniqueId()});
            Object[] values = other.get(5, TimeUnit.SECONDS);
            assertNull(values[0]);
            assertNull(values[1]);
            assertNull(values[2]);
        } finally {
            executor.shutdownNow();
        }
        assertEquals("main-token", ThreadContext.getToken());
    }

    @Test
    void pooledThreadsDoNotLeakValuesBetweenTasks() throws Exception {
        int threads = 4;
        int tasks = 200;
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        CountDownLatch start = new CountDownLatch(1);
        try {
            List<Future<String>> results = new ArrayList<>();
            for (int i = 0; i < tasks; i++) {
                final long id = i;
                Callable<String> task = () -> {
                    start.await();
                    // a previous task on this pooled thread must not leave anything behind
                    if (ThreadContext.getToken() != null || ThreadContext.getUserId() != null || ThreadContext.getThreadUniqueId() != null) {
                        return "leak before init in task " + id;
                    }
                    try (ThreadContext.Scope scope = ThreadContext.open()) {
                        assertNotNull(scope);
                        ThreadContext.setToken("token-" + id);
                        ThreadContext.setUserId(id);
                        Thread.yield();
                        if (!("token-" + id).equals(ThreadContext.getToken()) || ThreadContext.getUserId() != id) {
                            return "mixed values in task " + id;
                        }
                    }
                    return ThreadContext.getToken() == null && MDC.get("threadUniqueId") == null ? null : "not cleaned in task " + id;
                };
                results.add(executor.submit(task));
            }
            start.countDown();
            for (Future<String> f : results) {
                assertNull(f.get(10, TimeUnit.SECONDS));
            }
        } finally {
            executor.shutdownNow();
        }
    }

    @Test
    void initContextDiscardsPreviousValues() {
        ThreadContext.setToken("stale");
        ThreadContext.initContext();
        assertNull(ThreadContext.getToken());
        assertNotNull(ThreadContext.getThreadUniqueId());
        assertTrue(ThreadContext.getTotalTimeMillis() >= 0);
    }

    @Test
    void getThreadUniqueIdDoesNotInitialize() {
        ThreadContext.destroyContext();
        assertNull(ThreadContext.getThreadUniqueId());
        assertTrue(ThreadContext.getContextMap().isEmpty());
        assertEquals(0, ThreadContext.getTotalTimeMillis());
    }

    @Test
    void threadIdAndAccessors() {
        assertEquals(String.valueOf(Thread.currentThread().threadId()), ThreadContext.getThreadId());
        ThreadContext.setUserName("name");
        ThreadContext.setCompanyId("c1");
        ThreadContext.setAppKey("app");
        ThreadContext.addObject("custom", 5);
        assertEquals("name", ThreadContext.getUserName());
        assertEquals("c1", ThreadContext.getCompanyId());
        assertEquals("app", ThreadContext.getAppKey());
        assertEquals(5, ThreadContext.getObject("custom"));
        assertEquals(5, ThreadContext.getContextMap().get("custom"));
        ThreadContext.setAppKey(null);
        assertNull(ThreadContext.getAppKey());
        ThreadContext.destroyContext();
        assertNull(ThreadContext.getObject("custom"));
        assertNull(MDC.get("threadAppKey"));
    }
}
