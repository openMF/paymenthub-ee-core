/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 *  https://mozilla.org/MPL/2.0/.
 */
package org.mifos.connector.common.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.junit.jupiter.api.Test;

class ContextUtilTest {

    @Test
    void formatsAndParsesUnchanged() {
        assertThat(ContextUtil.parseDate("2026-10-20T06:18:43.012Z")).isEqualTo(LocalDateTime.of(2026, 10, 20, 6, 18, 43, 12_000_000));
        assertThat(ContextUtil.parseMojaDate("Tue, 20 Oct 2026 05:18:52 GMT")).isEqualTo(LocalDateTime.of(2026, 10, 20, 5, 18, 52));
        assertThat(ContextUtil.formatToDateHeader(1_792_473_532_000L)).isEqualTo("Tue, 20 Oct 2026 05:18:52 GMT");
        assertThat(ContextUtil.formatAmount(new BigDecimal("16.50000"))).isEqualTo("16.5");
        assertThat(ContextUtil.parseDate(null)).isNull();
    }

    @Test
    void parseDateIsThreadSafe() throws Exception {
        // A shared SimpleDateFormat fails this within a few hundred concurrent calls
        // (NumberFormatException / wrong dates).
        int threads = 16;
        int perThread = 500;
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        CountDownLatch start = new CountDownLatch(1);
        List<Future<Void>> results = new ArrayList<>();
        try {
            for (int t = 0; t < threads; t++) {
                int minute = t;
                String text = String.format("2026-10-20T06:%02d:43.012Z", minute);
                LocalDateTime expected = LocalDateTime.of(2026, 10, 20, 6, minute, 43, 12_000_000);
                Callable<Void> task = () -> {
                    start.await();
                    for (int i = 0; i < perThread; i++) {
                        assertThat(ContextUtil.parseDate(text)).isEqualTo(expected);
                        assertThat(ContextUtil.formatAmount(BigDecimal.valueOf(minute, 1)))
                                .isEqualTo(BigDecimal.valueOf(minute, 1).stripTrailingZeros().toPlainString());
                    }
                    return null;
                };
                results.add(pool.submit(task));
            }
            start.countDown();
            for (Future<Void> result : results) {
                result.get();
            }
        } finally {
            pool.shutdownNow();
        }
    }
}
