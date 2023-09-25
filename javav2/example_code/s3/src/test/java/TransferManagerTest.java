import com.example.s3.transfermanager.DownloadFile;
import com.example.s3.transfermanager.DownloadToDirectory;
import com.example.s3.transfermanager.ObjectCopy;
import com.example.s3.transfermanager.S3ClientFactory;
import com.example.s3.transfermanager.UploadADirectory;
import com.example.s3.transfermanager.UploadFile;
import com.example.s3.transfermanager.UploadStream;
import com.example.s3.util.AsyncExampleUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.transfer.s3.S3TransferManager;
import software.amazon.awssdk.transfer.s3.model.CompletedUpload;

import java.util.UUID;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(S3TestWatcher.class)
class TransferManagerTest {

    private static final Logger logger = LoggerFactory.getLogger(TransferManagerTest.class);

    @Test
    @Order(1)
    @Tag("IntegrationTest")
    public void uploadSingleFileWorks(){
        UploadFile upload = new UploadFile();
        String etag = upload.uploadFile(S3ClientFactory.transferManager, upload.bucketName,
            upload.key, upload.filePath);
        Assertions.assertNotNull(etag);
        upload.cleanUp();
    }

    @Test
    @Order(2)
    @Tag("IntegrationTest")
    public void downloadSingleFileWorks(){
        DownloadFile download = new DownloadFile();
        Long fileLength = download.downloadFile(S3ClientFactory.transferManager, download.bucketName, download.key, download.downloadedFileWithPath);
        Assertions.assertNotEquals(0L, fileLength);
        download.cleanUp();
    }

    @Test
    @Order(3)
    @Tag("IntegrationTest")
    public void copyObjectWorks(){
        ObjectCopy copy = new ObjectCopy();
        String etag = copy.copyObject(S3ClientFactory.transferManager, copy.bucketName, copy.key, copy.destinationBucket, copy.destinationKey);
        Assertions.assertNotNull(etag);
        copy.cleanUp();
    }

    @Test
    @Order(4)
    @Tag("IntegrationTest")
    public void directoryUploadWorks(){
        UploadADirectory upload = new UploadADirectory();
        Integer numFailedUploads = upload.uploadDirectory(S3ClientFactory.transferManager, upload.sourceDirectory, upload.bucketName);
        Assertions.assertNotNull(numFailedUploads, "Bucket download failed to complete.");
        upload.cleanUp();

    }

    @Test
    @Order(5)
    @Tag("IntegrationTest")
    public void directoryDownloadWorks(){
        DownloadToDirectory download = new DownloadToDirectory();
        Integer numFilesFailedToDownload = download.downloadObjectsToDirectory(S3ClientFactory.transferManager, download.destinationPath, download.bucketName);
        Assertions.assertNotNull(numFilesFailedToDownload, "Bucket download failed to complete.");
        download.cleanUp();
    }

    @Test
    @Order(6)
    @Tag("IntegrationTest")
    public void uploadStreamWorks(){
        String bucketName = "x-" + UUID.randomUUID();
        String key = UUID.randomUUID().toString();

        AsyncExampleUtils.createBucket(bucketName);
        try {
            UploadStream example = new UploadStream();
            CompletedUpload completedUpload = example.uploadStream(S3TransferManager.create(), bucketName, key);
            logger.info("Object {} etag: {}", key, completedUpload.response().eTag());
            logger.info("Object {} uploaded to bucket {}.", key, bucketName);
            Assertions.assertTrue(completedUpload.response().sdkHttpResponse().isSuccessful());
        } catch (SdkException e) {
            logger.error(e.getMessage(), e);
        } finally {
            AsyncExampleUtils.deleteObject(bucketName, key);
            AsyncExampleUtils.deleteBucket(bucketName);
        }
    }

    @BeforeAll
    public static void beforeAll(){
        logger.info("S3TransferManager tests starting ...");
    }

    @AfterAll
    public static void afterAll(){
        logger.info("... S3TransferManager tests finished");
    }
}