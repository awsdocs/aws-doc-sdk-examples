package models

import com.example.bedrockruntime.models.amazon.titan.text.invokeModel
import java.util.stream.Stream

/**
 * Test class for generative AI models on Amazon Bedrock using the InvokeModel API.
 */
class TestInvokeModel : AbstractModelTest() {
    /**
     * Provides test configurations for generative AI models on Amazon Bedrock.
     * Creates test cases that validate each model's ability to generate
     * and return text or byte[] responses.
     */
    override fun modelProvider(): Stream<ModelTest> {
        return listOf(
            ModelTest("Amazon Titan Text", ::invokeModel)
        ).stream()
    }
}