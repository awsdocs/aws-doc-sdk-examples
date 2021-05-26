const mockDetectDominantLanguageCommand = jest.fn();
jest.mock("@aws-sdk/client-comprehend/commands/DetectDominantLanguageCommand", () => ({
    DynamoDB: function Comprehend() {
        this.DetectDominantLanguageCommand = mockDetectDominantLanguageCommand;
    },
}));
import { run } from "../../../cross-services/lex-bot/src/index";

test("has to mock comprehend#dominantlanguage", async (done) => {
    await run();
    expect(mockDetectDominantLanguageCommand).toHaveBeenCalled;
    done();
});

const mockTranslateTextCommand = jest.fn();
jest.mock("@aws-sdk/client-translate/commands/TranslateTextCommand", () => ({
    Translate: function Comprehend() {
        this.TranslateTextCommand = mockTranslateTextCommand;
    },
}));
import { run } from "../../../cross-services/lex-bot/src/index";

test("has to mock translate#translatetext", async (done) => {
    await run();
    expect(mockTranslateTextCommand).toHaveBeenCalled;
    done();
});


const mockPostTextCommand = jest.fn();
jest.mock("@aws-sdk/client-translate/commands/PostTextCommand", () => ({
    LexRuntime: function Comprehend() {
        this.PostTextCommand = mockPostTextCommand;
    },
}));
import { run } from "../../../cross-services/lex-bot/src/index";

test("has to mock lext#posttext", async (done) => {
    await run();
    expect(mockPostTextCommand).toHaveBeenCalled;
    done();
});
