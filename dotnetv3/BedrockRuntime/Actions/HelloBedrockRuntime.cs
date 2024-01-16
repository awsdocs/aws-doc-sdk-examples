// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

namespace BedrockRuntimeActions
{
    internal class HelloBedrockRuntime
    {
        private static readonly string CLAUDE = "anthropic.claude-v2";

        static async Task Main(string[] args)
        {
            await TextToText();
        }

        private static async Task TextToText()
        {
            string prompt = "In one sentence, what is a large-language model?";
            await Invoke(CLAUDE, prompt);
        }

        private static async Task Invoke(string modelId, string prompt)
        {
            switch (modelId)
            {
                case var _ when modelId == CLAUDE:
                    Console.WriteLine(await InvokeModelAsync.InvokeClaudeAsync(prompt));
                    break;
                default:
                    Console.WriteLine($"Unknown model ID: {modelId}. Valid model IDs are: {CLAUDE}.");
                    break;
            };
        }
    }
}