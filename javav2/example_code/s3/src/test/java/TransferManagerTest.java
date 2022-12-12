import com.example.s3.transfermanager.UploadADirectory;
import com.example.s3.transfermanager.DownloadFile;
import com.example.s3.transfermanager.DownloadToDirectory;
import com.example.s3.transfermanager.UploadFile;
import com.example.s3.transfermanager.ObjectCopy;
import com.example.s3.transfermanager.S3ClientFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TransferManagerTest {

    private static final Logger logger = LoggerFactory.getLogger(TransferManagerTest.class);

    @AfterEach
    public void afterEach(TestInfo testInfo){
        logger.info("Test method [{}], succeeded", testInfo.getTestMethod().get().getName());
    }

    @Test
    @Order(1)
    public void uploadSingleFileWorks(){
        UploadFile upload = new UploadFile();
        String etag = upload.uploadFile(S3ClientFactory.transferManager, upload.bucketName,
            upload.key, upload.filePath);
        Assertions.assertNotNull(etag);
        upload.cleanUp();
    }

    @Test
    @Order(2)
    public void downloadSingleFileWorks(){
        DownloadFile download = new DownloadFile();
        Long fileLength = download.downloadFile(S3ClientFactory.transferManager, download.bucketName, download.key, download.downloadedFileWithPath);
        Assertions.assertNotEquals(0L, fileLength);
        download.cleanUp();
    }

    @Test
    @Order(3)
    public void copyObjectWorks(){
        ObjectCopy copy = new ObjectCopy();
        String etag = copy.copyObject(S3ClientFactory.transferManager, copy.bucketName, copy.key, copy.destinationBucket, copy.destinationKey);
        Assertions.assertNotNull(etag);
        copy.cleanUp();
    }

    @Test
    @Order(4)
    public void directoryUploadWorks(){
        UploadADirectory upload = new UploadADirectory();
        Integer numFailedUploads = upload.uploadDirectory(S3ClientFactory.transferManager, upload.sourceDirectory, upload.bucketName);
        Assertions.assertNotNull(numFailedUploads, "Bucket download failed to complete.");
        upload.cleanUp();

    }

    @Test
    @Order(5)
    public void directoryDownloadWorks(){
        DownloadToDirectory download = new DownloadToDirectory();
        Integer numFilesFailedToDownload = download.downloadObjectsToDirectory(S3ClientFactory.transferManager, download.destinationPath, download.bucketName);
        Assertions.assertNotNull(numFilesFailedToDownload, "Bucket download failed to complete.");
        download.cleanUp();
    }
}