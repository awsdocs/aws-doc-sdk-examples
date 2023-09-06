/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
import com.example.fsa.FSAApplicationResources;
import com.example.fsa.services.DetectSentimentService;
import com.example.fsa.services.ExtractTextService;
import com.example.fsa.services.PollyService;
import com.example.fsa.services.S3Service;
import com.example.fsa.services.TranslateService;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
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

    @Test
    @Tag("IntegrationTest")
    @Order(1)
    public void testFSA() throws IOException {
        ExtractTextService textService = new ExtractTextService();
        TranslateService translateService = new TranslateService();
        PollyService pollyService = new PollyService();
        S3Service s3Service = new S3Service();
        JSONParser parser = new JSONParser();
        DetectSentimentService sentimentService = new DetectSentimentService();


        DetectSentimentService detectSentimentService = new DetectSentimentService();
        String text = textService.getCardText(FSAApplicationResources.STORAGE_BUCKET, "french6.png");
        detectSentimentService.detectSentiments(text);
        String lanCode = sentimentService.detectTheDominantLanguage(text);
        String translatedText = translateService.translateText(lanCode, text);
        System.out.println(translatedText);
        InputStream is = pollyService.synthesize(translatedText);
        String audioFile = s3Service.putAudio(is, "french3.png");
        System.out.println("You have successfully added the FSA audio file in the S3 bucket");
        System.out.println("You have successfully converted the comment card!");
    }
}
