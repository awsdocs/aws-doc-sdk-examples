package models

import com.example.bedrockruntime.models.amazon.titan.text.invokeModel
import java.util.stream.Stream

class TestInvokeModel : AbstractModelTest() {
    override fun modelProvider(): Stream<ModelTest> {
        return listOf(
            ModelTest("Amazon Titan Text", ::invokeModel)
        ).stream()
    }
}