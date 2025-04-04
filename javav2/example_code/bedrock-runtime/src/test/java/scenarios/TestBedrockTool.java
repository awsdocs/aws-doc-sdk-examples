package scenarios;

import com.example.bedrockruntime.scenario.BedrockActions;
import com.example.bedrockruntime.scenario.WeatherTool;
import org.junit.jupiter.api.*;
import software.amazon.awssdk.services.bedrockruntime.model.ContentBlock;
import software.amazon.awssdk.services.bedrockruntime.model.ConversationRole;
import software.amazon.awssdk.services.bedrockruntime.model.ConverseResponse;
import software.amazon.awssdk.services.bedrockruntime.model.Message;
import java.util.ArrayList;
import java.util.List;


import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestBedrockTool {
    private static String prompt = "How is the weather in New York";
    static BedrockActions bedrockActions = new BedrockActions();
    private static WeatherTool weatherTool = new WeatherTool();
    private static String modelId = "anthropic.claude-3-sonnet-20240229-v1:0";

    @Test
    @Tag("IntegrationTest")
    @Order(1)
    public void testCreateAssetModel() {
        List<Message> conversation = new ArrayList<>();
        ContentBlock block = ContentBlock.builder()
                .text(prompt)
                .build();

        List<ContentBlock> blockList = new ArrayList<>();
        blockList.add(block);

        Message message = Message.builder()
                .role(ConversationRole.USER)
                .content(blockList)
                .build();

        conversation.add(message);
        ConverseResponse bedrockResponse = bedrockActions.sendConverseRequestAsync(modelId, prompt, conversation, weatherTool.getToolSpec());

        // Assertions
        assertNotNull(bedrockResponse, "Response should not be null");
        assertNotNull(bedrockResponse.output().message(), "Output text should not be null");
        System.out.println("Test 1 passed");
    }
}

