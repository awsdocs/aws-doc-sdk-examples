import {fileURLToPath} from "url";
import {askForPrompt, selectModel} from "./tools/user_input.js";
import {FoundationModels} from "./foundation_models.js";

const invokeModel = async (prompt, model) => {
    try {
        const modelModule = await model.module();
        const invoker = model.invoker(modelModule);
        return await invoker(prompt, model.modelId);
    } catch (err) {
        console.error(`Error invoking model ${model.modelId}:`, err);
        throw err;
    }
}

const runDemo = async () => {
    console.log("Welcome to the Amazon Bedrock Runtime client demo!");

    const model = await selectModel(Object.values(FoundationModels));
    const prompt = await askForPrompt();

    console.log(`Invoking ${model.modelName} with prompt '${prompt}'`);

    const response = await invokeModel(prompt, model);
    if (Array.isArray(response)) {
        response.forEach((str) => console.log(str));
    } else {
        console.log(response);
    }
}

if (process.argv[1] === fileURLToPath(import.meta.url)) {
    runDemo();
}
