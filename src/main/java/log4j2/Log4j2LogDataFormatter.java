package log4j2;

import static java.util.logging.Level.WARNING;

import com.google.common.flogger.backend.LogData;
import com.google.common.flogger.backend.Metadata;
import com.google.common.flogger.backend.SimpleMessageFormatter;
import com.google.common.flogger.backend.SimpleMessageFormatter.SimpleLogHandler;
import java.util.logging.Level;

/** Helper to format LogData */
final class Log4j2LogDataFormatter {
    private Log4j2LogDataFormatter() {}

    /**
     * Formats the log message and any metadata for the given {@link LogData}, calling the supplied
     * receiver object with the results.
     */
    static void format(LogData logData, SimpleLogHandler receiver) {
        SimpleMessageFormatter.format(logData, receiver);
    }

    /**
     * Formats the log message in response to an exception during a previous logging attempt. A
     * synthetic error message is generated from the original log data and the given exception is set
     * as the cause. The level of this record is the maximum of WARNING or the original level.
     */
    static void formatBadLogData(
            RuntimeException error, LogData badLogData, SimpleLogHandler receiver) {
        StringBuilder errorMsg =
                new StringBuilder("LOGGING ERROR: ").append(error.getMessage()).append('\n');
        int length = errorMsg.length();
        try {
            appendLogData(badLogData, errorMsg);
        } catch (RuntimeException e) {
            // Reset partially written buffer when an error occurs.
            errorMsg.setLength(length);
            errorMsg.append("Cannot append LogData: ").append(e);
        }

        // Re-target this log message as a warning (or above) since it indicates a real bug.
        Level level =
                badLogData.getLevel().intValue() < WARNING.intValue() ? WARNING : badLogData.getLevel();

        receiver.handleFormattedLogMessage(level, errorMsg.toString(), error);
    }

    /** Appends the given {@link LogData} to the given {@link StringBuilder}. */
    static void appendLogData(LogData data, StringBuilder out) {
        out.append("  original message: ");
        if (data.getTemplateContext() == null) {
            out.append(data.getLiteralArgument());
        } else {
            // We know that there's at least one argument to display here.
            out.append(data.getTemplateContext().getMessage());
            out.append("\n  original arguments:");
            for (Object arg : data.getArguments()) {
                //out.append("\n    ").append(MessageUtils.safeToString(arg));
            }
        }
        Metadata metadata = data.getMetadata();
        if (metadata.size() > 0) {
            out.append("\n  metadata:");
            for (int n = 0; n < metadata.size(); n++) {
                out.append("\n    ");
                out.append(metadata.getKey(n).getLabel()).append(": ").append(metadata.getValue(n));
            }
        }
        out.append("\n  level: ").append(data.getLevel());
        out.append("\n  timestamp (nanos): ").append(data.getTimestampNanos());
        out.append("\n  class: ").append(data.getLogSite().getClassName());
        out.append("\n  method: ").append(data.getLogSite().getMethodName());
        out.append("\n  line number: ").append(data.getLogSite().getLineNumber());
    }
}

