// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0


import com.example.acm.AddTagsToCertificate;
import com.example.acm.DeleteCert;
import com.example.acm.DescribeCert;
import com.example.acm.ImportCert;
import com.example.acm.ListCertTags;
import com.example.acm.RemoveTagsFromCert;
import com.example.acm.RequestCert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ACMTests {

    private static String certificatePath = "";
    private static String privateKeyPath = "";

    private static String certificateArn;

    @BeforeAll
    public static void setUp() {

        certificatePath = "C:\\Users\\scmacdon\\cert_example\\certificate.pem";
        privateKeyPath = "C:\\Users\\scmacdon\\cert_example\\private_key.pem";
    }

    @Test
    @Tag("IntegrationTest")
    @Order(1)
    public void testImportCert() {
        assertDoesNotThrow(() -> {
            certificateArn = ImportCert.importCertificate(certificatePath, privateKeyPath);
            assertNotNull(certificateArn);
        });
    }

    @Test
    @Tag("IntegrationTest")
    @Order(2)
    public void testAddTags() {
        assertDoesNotThrow(() -> {
            AddTagsToCertificate.addTags(certificateArn);
        });
    }

    @Test
    @Tag("IntegrationTest")
    @Order(3)
    public void testDescribeCert() {
        assertDoesNotThrow(() -> {
            DescribeCert.describeCertificate(certificateArn);
        });
    }

    @Test
    @Tag("IntegrationTest")
    @Order(4)
    public void testListCertTags() {
        assertDoesNotThrow(() -> {
            ListCertTags.listCertTags(certificateArn);
        });
    }

    @Test
    @Tag("IntegrationTest")
    @Order(5)
    public void testRemoveTagsFromCert() {
        assertDoesNotThrow(() -> {
            RemoveTagsFromCert.removeTags(certificateArn);
        });
    }


    @Test
    @Tag("IntegrationTest")
    @Order(6)
    public void testRequestCert() {
        assertDoesNotThrow(() -> {
            RequestCert.requestCertificate();
        });
    }

    @Test
    @Tag("IntegrationTest")
    @Order(7)
    public void testDeleteCert() {
        assertDoesNotThrow(() -> {
            DeleteCert.deleteCertificate(certificateArn);
        });
    }
}
