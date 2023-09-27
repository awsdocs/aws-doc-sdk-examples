/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
import com.example.fsa.services.DetectSentimentService;
import com.example.fsa.services.ExtractTextService;
import com.example.fsa.services.PollyService;
import com.example.fsa.services.S3Service;
import com.example.fsa.services.TranslateService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import java.io.IOException;
import java.io.InputStream;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FSATests {

    // Specify the key to use. 
    private static final String objectName = "Enter the object name here";
    
    // Specify the bucket name.
    private static final String bucketName = "Enter the bucket name here";

    private ExtractTextService textService;
    private TranslateService translateService;
    private PollyService pollyService;
    private S3Service s3Service;
    private DetectSentimentService sentimentService;

    @BeforeEach
    public void setUp() {
        textService = new ExtractTextService();
        translateService = new TranslateService();
        pollyService = new PollyService();
        s3Service = new S3Service();
        sentimentService = new DetectSentimentService();
    }

    @Test
    @Tag("IntegrationTest")
    @Order(1)
    public void testTextExtraction() {
        String text = textService.getCardText(bucketName, objectName);
        Assertions.assertNotNull(text);
    }

    @Test
    @Tag("IntegrationTest")
    @Order(2)
    public void testSentimentAnalysis() {
        String text = textService.getCardText(bucketName, objectName);
        String sentiment = String.valueOf(sentimentService.detectSentiments(text));
        Assertions.assertNotNull(sentiment);
    }

    @Test
    @Tag("IntegrationTest")
    @Order(3)
    public void testTranslation() {
        String text = textService.getCardText(bucketName, objectName);
        String lanCode = sentimentService.detectTheDominantLanguage(text);
        String translatedText = translateService.translateText(lanCode, text);
        Assertions.assertNotNull(lanCode);
        Assertions.assertNotNull(translatedText);
    }

    @Test
    @Tag("IntegrationTest")
    @Order(4)
    public void testAudioSynthesis() throws IOException {
        String text = textService.getCardText(bucketName, objectName);
        String lanCode = sentimentService.detectTheDominantLanguage(text);
        String translatedText = translateService.translateText(lanCode, text);
        InputStream is = pollyService.synthesize(translatedText);
        Assertions.assertNotNull(is);
    }

    @Test
    @Tag("IntegrationTest")
    @Order(5)
    public void testS3Upload() throws IOException {
        String text = textService.getCardText(bucketName, objectName);
        String lanCode = sentimentService.detectTheDominantLanguage(text);
        String translatedText = translateService.translateText(lanCode, text);
        InputStream is = pollyService.synthesize(translatedText);
        String mp3File = objectName + ".mp3";
        String uploadResult = s3Service.putAudio(is, bucketName, mp3File);
        Assertions.assertNotNull(uploadResult);
    }
}