package application.portfolio.clientmodule.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtils {

    public static final DateTimeFormatter CREATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static final DateTimeFormatter DEADLINE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static String formatCreatedDate(LocalDateTime date) {
        return date != null ? date.format(CREATE_FORMATTER) : null;
    }

    public static LocalDateTime parseCreatedDate(String createdDateString) {
        return createdDateString != null ? LocalDateTime.parse(createdDateString, CREATE_FORMATTER) : null;
    }

    public static String formatDeadlineDate(LocalDate date) {
        return date != null ? date.format(DEADLINE_FORMATTER) : null;
    }

    public static LocalDate parseDeadlineDate(String deadlineString) {
        return deadlineString != null ? LocalDate.parse(deadlineString, DEADLINE_FORMATTER) : null;
    }
}