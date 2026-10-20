/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 *  https://mozilla.org/MPL/2.0/.
 */
package org.mifos.connector.common.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public final class ContextUtil {

    private ContextUtil() {}

    // SimpleDateFormat and DecimalFormat keep mutable parse/format state, so a shared instance corrupts results
    // under concurrent use (e.g. NumberFormatException: For input string: "" when two transfers are sent at once).
    // One instance per thread keeps the exact formats and lenient parsing.
    private static final ThreadLocal<SimpleDateFormat> LOCAL_DATE_TIME_FORMAT = ThreadLocal
            .withInitial(() -> new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX"));
    private static final ThreadLocal<DecimalFormat> AMOUNT_FORMAT = ThreadLocal.withInitial(() -> new DecimalFormat("#.####"));
    private static final ThreadLocal<SimpleDateFormat> DATE_HEADER_FORMATTER = ThreadLocal.withInitial(() -> {
        SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.ENGLISH);
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        return format;
    });

    public static LocalDateTime parseDate(String date) {
        if (date == null) {
            return null;
        }
        try {
            return LocalDateTime.ofInstant(Instant.ofEpochMilli(LOCAL_DATE_TIME_FORMAT.get().parse(date).getTime()), ZoneOffset.UTC);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public static LocalDateTime parseMojaDate(String date) {
        if (date == null) {
            return null;
        }
        try {
            return LocalDateTime.ofInstant(Instant.ofEpochMilli(DATE_HEADER_FORMATTER.get().parse(date).getTime()), ZoneOffset.UTC);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public static String formatToDateHeader(long date) {
        return DATE_HEADER_FORMATTER.get().format(Date.from(Instant.ofEpochMilli(date)));
    }

    public static String formatDate(LocalDateTime date) {
        return date == null ? null : LOCAL_DATE_TIME_FORMAT.get().format(Date.from(date.toInstant(ZoneOffset.UTC)));
    }

    public static BigDecimal parseAmount(String amount) {
        return amount == null ? null : new BigDecimal(amount);
    }

    public static String formatAmount(BigDecimal amount) {
        return amount == null ? null : AMOUNT_FORMAT.get().format(amount);
    }
}
