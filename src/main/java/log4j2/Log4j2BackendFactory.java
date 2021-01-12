package log4j2;

import com.google.common.flogger.backend.LoggerBackend;
import com.google.common.flogger.backend.system.BackendFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;

/**
 * BackendFactory for log4j2.
 *
 * <p>To configure this backend for Flogger set the following system property (also see {@link
 * com.google.common.flogger.backend.system.DefaultPlatform}):
 *
 * <ul>
 *   <li>{@code flogger.backend_factory=
 *       com.google.common.flogger.backend.log4j2.Log4j2BackendFactory#getInstance}
 * </ul>
 */
public final class Log4j2BackendFactory extends BackendFactory {
    private static final Log4j2BackendFactory INSTANCE = new Log4j2BackendFactory();

    private Log4j2BackendFactory() {}

    /** This method is expected to be called via reflection (and might otherwise be unused). */
    public static BackendFactory getInstance() {
        return INSTANCE;
    }

    @Override
    public LoggerBackend create(String loggingClassName) {
        // Compute the logger name exactly the same way as in SimpleBackendFactory.
        // The logger name must match the name of the logging class so that we can return it from
        // Log4j2LoggerBackend#getLoggerName().
        // We cast org.apache.logging.log4j.core.Logger here so that
        // we can access the methods only avilable under org.apache.logging.log4j.core.Logger.
        // TODO(b/27920233): Strip inner/nested classes when deriving logger name.
        Logger logger = (Logger) LogManager.getLogger(loggingClassName.replace('$', '.'));
        return new Log4j2LoggerBackend(logger);
    }

    @Override
    public String toString() {
        return "Log4j2 backend";
    }
}