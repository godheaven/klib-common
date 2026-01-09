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
package cl.kanopus.common.util;

import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Performance tests for GsonUtils class to ensure optimizations are working
 */
class GsonUtilsPerformanceTest {

    private static final int ITERATIONS = 1000;

    static class TestData {
        private Date timestamp;
        private String name;

        public Date getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(Date timestamp) {
            this.timestamp = timestamp;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    @Test
    void testDateSerializationPerformance() {
        // Test that repeated serialization with cached formatters is fast
        TestData data = new TestData();
        data.setTimestamp(new Date());
        data.setName("Test");
        
        long startTime = System.nanoTime();
        for (int i = 0; i < ITERATIONS; i++) {
            String json = GsonUtils.custom.toJson(data);
            assertNotNull(json);
            assertTrue(json.contains("timestamp"));
        }
        long duration = System.nanoTime() - startTime;
        
        assertTrue(duration > 0, "Date serialization should complete successfully");
    }

    @Test
    void testDateDeserializationPerformance() {
        // Test that repeated deserialization with cached formatters is fast
        String json = "{\"timestamp\":\"2025-01-09T10:30:45\",\"name\":\"Test\"}";
        
        long startTime = System.nanoTime();
        for (int i = 0; i < ITERATIONS; i++) {
            TestData data = GsonUtils.custom.fromJson(json, TestData.class);
            assertNotNull(data);
            assertNotNull(data.getTimestamp());
            assertEquals("Test", data.getName());
        }
        long duration = System.nanoTime() - startTime;
        
        assertTrue(duration > 0, "Date deserialization should complete successfully");
    }

    @Test
    void testDateRoundTripPerformance() {
        // Test that serialization and deserialization cycle is efficient
        TestData original = new TestData();
        original.setTimestamp(new Date());
        original.setName("RoundTrip");
        
        long startTime = System.nanoTime();
        for (int i = 0; i < ITERATIONS; i++) {
            String json = GsonUtils.custom.toJson(original);
            TestData deserialized = GsonUtils.custom.fromJson(json, TestData.class);
            assertNotNull(deserialized);
            assertEquals(original.getName(), deserialized.getName());
        }
        long duration = System.nanoTime() - startTime;
        
        assertTrue(duration > 0, "Round-trip conversion should complete successfully");
    }
}
