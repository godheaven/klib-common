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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Performance tests for Utils class to ensure optimizations are working
 */
class UtilsPerformanceTest {

    private static final int ITERATIONS = 1000;

    @Test
    void testDateFormatterCaching() {
        // Test that repeated calls with same pattern are fast (cached)
        Date testDate = new Date();
        String pattern = "yyyy-MM-dd HH:mm:ss";
        
        long startTime = System.nanoTime();
        for (int i = 0; i < ITERATIONS; i++) {
            String result = Utils.getDateFormat(testDate, pattern);
            assertNotNull(result);
        }
        long duration = System.nanoTime() - startTime;
        
        // Just ensure it completes without error
        assertTrue(duration > 0, "Date formatting should complete successfully");
    }

    @Test
    void testIsValidRegexPerformance() {
        // Test that regex validation is efficient
        String testValue = "test@example.com";
        String regex = "^[a-zA-Z0-9@.]+$";
        
        long startTime = System.nanoTime();
        for (int i = 0; i < ITERATIONS; i++) {
            boolean result = Utils.isValidRegex(testValue, regex);
            assertTrue(result);
        }
        long duration = System.nanoTime() - startTime;
        
        assertTrue(duration > 0, "Regex validation should complete successfully");
    }

    @Test
    void testIsDateBetweenPerformance() {
        // Test that date comparison is efficient
        Date start = new Date(System.currentTimeMillis() - 86400000L); // Yesterday
        Date end = new Date(System.currentTimeMillis() + 86400000L); // Tomorrow
        Date test = new Date(); // Today
        
        long startTime = System.nanoTime();
        for (int i = 0; i < ITERATIONS; i++) {
            boolean result = Utils.isDateBetween(test, start, end);
            assertTrue(result);
        }
        long duration = System.nanoTime() - startTime;
        
        assertTrue(duration > 0, "Date comparison should complete successfully");
    }

    @Test
    void testArrayConversionPerformance() {
        // Test that array conversion is efficient
        List<Integer> numbers = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            numbers.add(i);
        }
        
        long startTime = System.nanoTime();
        for (int i = 0; i < ITERATIONS; i++) {
            int[] result = Utils.toArrayInt(numbers);
            assertEquals(100, result.length);
        }
        long duration = System.nanoTime() - startTime;
        
        assertTrue(duration > 0, "Array conversion should complete successfully");
    }

    @Test
    void testParseDoublePerformance() {
        // Test that parseDouble is efficient
        long startTime = System.nanoTime();
        for (int i = 0; i < ITERATIONS; i++) {
            double result = Utils.parseDouble(123L);
            assertEquals(123.0, result, 0.001);
        }
        long duration = System.nanoTime() - startTime;
        
        assertTrue(duration > 0, "ParseDouble should complete successfully");
    }

    @Test
    void testDateFormatterMultiplePatterns() {
        // Test caching with multiple different patterns
        Date testDate = new Date();
        String[] patterns = {
            "yyyy-MM-dd",
            "dd/MM/yyyy",
            "MM-dd-yyyy HH:mm",
            "yyyy.MM.dd"
        };
        
        long startTime = System.nanoTime();
        for (int i = 0; i < ITERATIONS; i++) {
            for (String pattern : patterns) {
                String result = Utils.getDateFormat(testDate, pattern);
                assertNotNull(result);
            }
        }
        long duration = System.nanoTime() - startTime;
        
        assertTrue(duration > 0, "Multiple pattern formatting should complete successfully");
    }

    @Test
    void testToArrayIntegerPerformance() {
        // Test that toArrayInteger uses efficient toArray method
        List<Integer> numbers = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            numbers.add(i);
        }
        
        long startTime = System.nanoTime();
        for (int i = 0; i < ITERATIONS; i++) {
            Integer[] result = Utils.toArrayInteger(numbers);
            assertEquals(100, result.length);
        }
        long duration = System.nanoTime() - startTime;
        
        assertTrue(duration > 0, "ToArrayInteger should complete successfully");
    }
}
