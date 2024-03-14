import {fileURLToPath} from "url";
import {askForPrompt, selectModel} from "./tools/user_input.js";
import {FoundationModels} from "./foundation-models.js";
import {invokeClaude3} from "./models/anthropic/invoke_claude_3.js";
import {invokeTextCompletionApi} from "./anthropic/claude_instant.js";

export const runDemo = async () => {

    console.log("=".repeat(50));
    console.log("Welcome to the Amazon Bedrock Runtime client demo!");
    console.log("=".repeat(50));

    console.log("First, select a model:")
    let model;
    await selectModel(Object.values(FoundationModels))
        .then(selectedModel => model = selectedModel)
        .catch(error => console.error('An error occurred:', error));

    console.log(`Selected model: ${model.modelName}`);
    console.log("-".repeat(50));
    let prompt;
    await askForPrompt()
        .then(selectedPrompt => prompt = selectedPrompt);
    console.log("-".repeat(50));
    console.log(`Invoking ${model.modelName} with your prompt...`)
    console.log("-".repeat(50));

    let response;
    if (model === FoundationModels.CLAUDE_HAIKU || model === FoundationModels.CLAUDE_SONNET) {
        response = await invokeClaude3(model.modelId, prompt);
    } else if (model === FoundationModels.CLAUDE_INSTANT) {
        response = await invokeTextCompletionApi(prompt);
    }

    console.log(response);

}

// Invoke main function if this file was run directly.
if (process.argv[1] === fileURLToPath(import.meta.url)) {
    await runDemo();
}
