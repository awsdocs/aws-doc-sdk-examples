// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0


import com.example.bedrockruntime.InvokeModelWithResponseStream;


import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.io.IOException;
import java.net.URL;
import java.util.Base64;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class ImageToTextAsyncTest extends TestBase {

    @Test
    @Tag("IntegrationTest")
    void InvokeClaude3WithResponseStream() {
        var silent = false;
        String sampleImageURL = "https://upload.wikimedia.org/wikipedia/commons/thumb/8/89/PD-US_table_updated.svg/431px-PD-US_table_updated.svg.png";
        var generatedText = InvokeModelWithResponseStream.invokeClaude3Sonnet(encodeImage(sampleImageURL), silent);
        ;
        assertNotNullOrEmpty(generatedText);
        System.out.println("Test async invoke Claude with response stream passed.");
    }
    public static String encodeImage(String imageUrl) {
        byte[] imageBytes = new byte[8];
        try (var imageResponse = new URL(imageUrl).openStream()) {
            imageBytes = org.apache.commons.io.IOUtils.toByteArray(imageResponse); // Use Apache Commons IO for efficient handling
        } catch (IOException ioException) {
            System.err.println("Error: " + ioException.getMessage());
        }
        // Base64 encode the image bytes
        return Base64.getEncoder().encodeToString(imageBytes);
    }
}
