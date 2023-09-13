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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import java.io.IOException;
import java.io.InputStream;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class FSATests {

    @Mock
    private ExtractTextService textService;

    @Mock
    private TranslateService translateService;

    @Mock
    private PollyService pollyService;

    @Mock
    private S3Service s3Service;

    @Mock
    private DetectSentimentService sentimentService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testFSA() throws IOException {
        // Mock behavior for textService.
        lenient().when(textService.getCardText(anyString(), anyString())).thenReturn("Your mock text");

        // Mock behavior for sentimentService.
        lenient().when(sentimentService.detectTheDominantLanguage(anyString())).thenReturn("en");

        // Mock behavior for translateService.
        lenient().when(translateService.translateText(anyString(), anyString())).thenReturn("Translated text");

        InputStream mockInputStream = mock(InputStream.class);
        lenient().when(pollyService.synthesize(anyString())).thenReturn(mockInputStream);

        // Mock behavior for s3Service.
        lenient().when(s3Service.putAudio(any(InputStream.class), anyString(), anyString())).thenReturn("MockedAudioFile");
        DetectSentimentService detectSentimentService = new DetectSentimentService();
        String text = textService.getCardText(FSAApplicationResources.STORAGE_BUCKET, "french.png");
        detectSentimentService.detectSentiments(text);
        String lanCode = sentimentService.detectTheDominantLanguage(text);
        String translatedText = translateService.translateText(lanCode, text);
        System.out.println(translatedText);
        InputStream is = pollyService.synthesize(translatedText);
        s3Service.putAudio(is, "bucket", "french.png");
        System.out.println("You have successfully added the FSA audio file in the S3 bucket");

        // Verify that the mocked methods were called as expected.
        verify(textService).getCardText(FSAApplicationResources.STORAGE_BUCKET, "french.png");
        verify(sentimentService).detectTheDominantLanguage(text);
        verify(translateService).translateText(lanCode, text);
        verify(pollyService).synthesize(translatedText);
        verify(s3Service).putAudio(is, "bucket", "french.png");
    }
}