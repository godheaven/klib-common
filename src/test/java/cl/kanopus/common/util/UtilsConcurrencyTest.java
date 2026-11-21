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

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;

class UtilsConcurrencyTest {

    private static final int THREADS = 8;
    private static final int ITERATIONS = 2_000;

    @Test
    void formattersAreThreadSafe() throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(THREADS);
        CountDownLatch start = new CountDownLatch(1);
        try {
            List<Future<String>> futures = new ArrayList<>();
            for (int t = 0; t < THREADS; t++) {
                final int seed = t;
                Callable<String> task = () -> {
                    start.await();
                    for (int i = 0; i < ITERATIONS; i++) {
                        int n = seed * 100_000 + i;
                        LocalDate ld = LocalDate.of(2000 + (n % 50), 1 + (n % 12), 1 + (n % 28));
                        Date date = Date.from(ld.atTime(12, 0).atZone(ZoneId.systemDefault()).toInstant());
                        String expectedDate = String.format("%02d/%02d/%04d", ld.getDayOfMonth(), ld.getMonthValue(), ld.getYear());

                        String formatted = Utils.getDateFormat(date);
                        if (!expectedDate.equals(formatted)) {
                            return "date format: expected " + expectedDate + " got " + formatted;
                        }
                        Date parsed = Utils.getDate(expectedDate);
                        if (!Utils.getLocalDate(parsed).equals(ld)) {
                            return "date parse: expected " + ld + " got " + parsed;
                        }
                        if (!(expectedDate + " 12:00:00").equals(Utils.getDateTimeFormat(date))) {
                            return "datetime format " + Utils.getDateTimeFormat(date);
                        }
                        if (!"12:00:00".equals(Utils.getTimeFormat(date))) {
                            return "time format " + Utils.getTimeFormat(date);
                        }
                        if (!Utils.isDateEquals(date, parsed)) {
                            return "isDateEquals";
                        }
                        String number = Utils.getNumberFormat(n, false);
                        if (!String.valueOf(n).equals(number)) {
                            return "number format: expected " + n + " got " + number;
                        }
                        String padded = Utils.getNumberFormat(n, false, 10);
                        if (padded.length() != 10 || !padded.trim().equals(String.valueOf(n))) {
                            return "padding format " + padded;
                        }
                        String decimal = Utils.getDecimalFormat(n + 0.5);
                        if (!decimal.endsWith(",5")) {
                            return "decimal format " + decimal;
                        }
                    }
                    return null;
                };
                futures.add(executor.submit(task));
            }
            start.countDown();
            for (Future<String> f : futures) {
                assertNull(f.get(60, TimeUnit.SECONDS));
            }
        } finally {
            executor.shutdownNow();
        }
    }
}
