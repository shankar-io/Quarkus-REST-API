package log4j2;

import com.google.common.flogger.backend.LogData;
import com.google.common.flogger.backend.LoggerBackend;
import org.apache.logging.log4j.core.Logger;

import java.util.logging.Level;

final class Log4j2LoggerBackend extends LoggerBackend {

    /** Converts java.util.logging.Level to org.apache.log4j.Level. */
    static org.apache.logging.log4j.Level toLog4jLevel(java.util.logging.Level level) {
        int logLevel = level.intValue();
        if (logLevel >= java.util.logging.Level.SEVERE.intValue()) {
            return org.apache.logging.log4j.Level.ERROR;
        } else if (logLevel >= java.util.logging.Level.WARNING.intValue()) {
            return org.apache.logging.log4j.Level.WARN;
        } else if (logLevel >= java.util.logging.Level.INFO.intValue()) {
            return org.apache.logging.log4j.Level.INFO;
        } else if (logLevel >= java.util.logging.Level.FINE.intValue()) {
            return org.apache.logging.log4j.Level.DEBUG;
        }
        return org.apache.logging.log4j.Level.TRACE;
    }

    private final Logger logger;

    // VisibleForTesting
    Log4j2LoggerBackend(Logger logger) {
        this.logger = logger;
    }

    @Override
    public String getLoggerName() {
        // Logger#getName() returns exactly the name that we used to create the Logger in
        // Log4jBackendFactory. It matches the name of the logging class.
        return logger.getName();
    }

    @Override
    public boolean isLoggable(Level level) {
        return logger.isEnabled(toLog4jLevel(level));
    }

    private void log(Log4j2SimpleLogEvent logEntry, boolean wasForced) {
        if (wasForced || logger.isEnabled(logEntry.getLevel())) {
            logger.get().log(logEntry.asLoggingEvent());
        }
    }

    @Override
    public void log(LogData logData) {
        log(Log4j2SimpleLogEvent.create(logger, logData), logData.wasForced());
    }

    @Override
    public void handleError(RuntimeException error, LogData badData) {
        log(Log4j2SimpleLogEvent.error(logger, error, badData), badData.wasForced());
    }
}
