// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. 
// SPDX-License-Identifier: Apache-2.0

namespace BedrockRuntimeTests
{
    public class ActionTest_InvokeModelWithResponseStream : ActionTest_Base
    {
        protected string _action;

        public ActionTest_InvokeModelWithResponseStream()
        {
            _action = "InvokeModelWithResponseStream";
        }

        [Theory]
        [InlineData("AmazonTitanText")]
        [InlineData("AnthropicClaude")]
        [InlineData("CohereCommand", "Command")]
        [InlineData("CohereCommand", "Command_R")]
        [InlineData("MetaLlama", "Llama2")]
        [InlineData("MetaLlama", "Llama3")]
        [InlineData("Mistral")]
        [Trait("Category", "Integration")]
        public void RunTest(string model, string? subDir = null)
        {
            (int num, string str) = runTest(getTestFilePath(model, _action, subDir));
            Assert.Equal(0, num);
            Assert.NotEmpty(str);
        }
    }
}