import * as fs from "fs";
import { fileURLToPath } from "url";
import {askForPrompt, selectModel} from "./tools/user_input.js";

const configData = JSON.parse(fs.readFileSync('models.json', 'utf-8'));
const FoundationModels = Object.freeze(configData);

// Invoke the function if this file was run directly.
if (process.argv[1] === fileURLToPath(import.meta.url)) {
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
        .then(selectedPrompt => prompt = selectedPrompt );

    console.log(prompt);

}