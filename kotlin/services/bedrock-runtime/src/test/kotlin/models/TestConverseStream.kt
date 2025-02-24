package models

import com.example.bedrockruntime.models.amazon.nova.text.converseStream
import java.util.stream.Stream

/**
 * Test class for streaming text generation on Amazon Bedrock using the ConverseStream API.
 */
class TestConverseStream : AbstractModelTest() {
    /**
     * Provides test configurations for Amazon Bedrock models that support streaming.
     * Creates test cases that validate each model's ability to generate
     * and return streaming text responses.
     */
    override fun modelProvider(): Stream<ModelTest> {
        return listOf(
            ModelTest("Amazon Nova", ::converseStream)
        ).stream()
    }
}