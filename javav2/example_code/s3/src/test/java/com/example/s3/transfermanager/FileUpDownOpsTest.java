package com.example.s3.transfermanager;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class FileUpDownOpsTest {

    @Test
    @Order(1)
    void testFileUploadDownload() {
        FileUpDownOps fileUpDownOps = new FileUpDownOps(
                "bucketName",
                "key",
                "path/to/file/fileitself.ext",
                "downloadedFile.txt");
        String[] strings = fileUpDownOps.fileUploadDownload().split("|");
        assertNotNull(strings[0]);
        assertNotNull(strings[1]);
    }

}