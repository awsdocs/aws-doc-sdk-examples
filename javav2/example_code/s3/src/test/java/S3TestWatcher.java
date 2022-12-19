import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class S3TestWatcher implements TestWatcher {

    private static final Logger logger = LoggerFactory.getLogger(S3TestWatcher.class);

    @Override
    public void testSuccessful(ExtensionContext context) {
        logger.info("Test Successful for test {}: ", context.getDisplayName());
    }
    @Override
    public void testFailed(ExtensionContext context, Throwable cause) {
        logger.info("Test Failed for {}: ", context.getDisplayName());
        logger.error(cause.getMessage());
    }

}
