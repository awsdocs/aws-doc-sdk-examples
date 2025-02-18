// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// snippet-start:[BedrockRuntime.dotnetv3.InvokeModel_AmazonNovaImageGeneration]
// Use the native inference API to create an image with Amazon Nova Canvas.

using System;
using System.IO;
using System.Text.Json;
using System.Text.Json.Nodes;
using Amazon;
using Amazon.BedrockRuntime;
using Amazon.BedrockRuntime.Model;

// Create a Bedrock Runtime client in the AWS Region you want to use.
var client = new AmazonBedrockRuntimeClient(RegionEndpoint.USEast1);

// Set the model ID.
var modelId = "amazon.nova-canvas-v1:0";

// Define the image generation prompt for the model.
var prompt = "A stylized picture of a cute old steampunk robot.";

// Create a random seed between 0 and 858,993,459
int seed = new Random().Next(0, 858993460);

//Format the request payload using the model's native structure.
var nativeRequest = JsonSerializer.Serialize(new
{
    taskType = "TEXT_IMAGE",
    textToImageParams = new
    {
        text = prompt
    },
    imageGenerationConfig = new
    {
        seed,
        quality = "standard",
        width = 512,
        height = 512,
        numberOfImages = 1
    }
});

// Create a request with the model ID and the model's native request payload.
var request = new InvokeModelRequest()
{
    ModelId = modelId,
    Body = new MemoryStream(System.Text.Encoding.UTF8.GetBytes(nativeRequest)),
    ContentType = "application/json"
};

try
{
    // Send the request to the Bedrock Runtime and wait for the response.
    var response = await client.InvokeModelAsync(request);

    // Decode the response body.
    var modelResponse = await JsonNode.ParseAsync(response.Body);

    // Extract the image data.
    var base64Image = modelResponse["images"]?[0].ToString() ?? "";

    // Save the image in a local folder
    string savedPath = AmazonNovaCanvas.InvokeModel.SaveBase64Image(base64Image);
    Console.WriteLine($"Image saved to: {savedPath}");
}
catch (AmazonBedrockRuntimeException e)
{
    Console.WriteLine($"ERROR: Can't invoke '{modelId}'. Reason: {e.Message}");
    throw;
}

// snippet-end:[BedrockRuntime.dotnetv3.InvokeModel_AmazonNovaImageGeneration]

// Create a partial class to make the top-level script testable.
namespace AmazonNovaCanvas
{
    public partial class InvokeModel
    {
        public static string SaveBase64Image(string base64String, string outputFolderName = "generated-images")
        {
            // Get the directory where the script is located
            string scriptDirectory = AppDomain.CurrentDomain.BaseDirectory;

            // Navigate to the script's folder
            if (scriptDirectory.Contains("bin"))
            {
                scriptDirectory = Directory.GetParent(scriptDirectory)?.Parent?.Parent?.Parent?.FullName
                    ?? throw new DirectoryNotFoundException("Could not find script directory");
            }

            // Combine script directory with output folder
            string outputPath = Path.Combine(scriptDirectory, outputFolderName);

            // Create directory if it doesn't exist
            if (!Directory.Exists(outputPath))
            {
                Directory.CreateDirectory(outputPath);
            }

            // Remove base64 header if present (e.g., "data:image/jpeg;base64,")
            string base64Data = base64String;
            if (base64String.Contains(","))
            {
                base64Data = base64String.Split(',')[1];
            }

            // Convert base64 to bytes
            byte[] imageBytes = Convert.FromBase64String(base64Data);

            // Find the next available number
            int fileNumber = 1;
            string filePath;
            do
            {
                string paddedNumber = fileNumber.ToString("D2"); // Pads with leading zero
                filePath = Path.Combine(outputPath, $"image_{paddedNumber}.jpg");
                fileNumber++;
            } while (File.Exists(filePath));

            // Save the image
            File.WriteAllBytes(filePath, imageBytes);

            return filePath;
        }
    }
}