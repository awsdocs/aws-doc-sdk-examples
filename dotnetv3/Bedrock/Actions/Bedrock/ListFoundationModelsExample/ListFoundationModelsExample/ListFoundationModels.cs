// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. 
// SPDX-License-Identifier:  Apache-2.0

using System.Text;
using Amazon;
using Amazon.Bedrock;
using Amazon.Bedrock.Model;
using Amazon.Internal;
using Amazon.Runtime;

namespace ListFoundationModelsExample
{
    internal class ListFoundationModels
    {
        static async Task Main(string[] args)
        {
            // Specify a region endpoint where Amazon Bedrock is available. For a list of supported region see https://docs.aws.amazon.com/bedrock/latest/userguide/what-is-bedrock.html#bedrock-regions
            AmazonBedrockClient client = new(RegionEndpoint.USWest2);

            await ListFoundationModelsWithNoFilter(client);

            await ListFoundationModelsSupportingFineTuning(client);

            await ListFoundationModelsSupportingProvisionedInference(client);

            await ListFoundationModelsSupportingOnDemandInference(client);

            await ListFoundationModelsSupportingTextOuput(client);

            await ListFoundationModelsSupportingImageOuput(client);

            await ListFoundationModelsSupportingEmbeddingOuput(client);

            await ListFoundationModelsSupportingProvisionedInferenceAndEmbeddingOutput(client);

        }

        private static async Task ListFoundationModelsWithNoFilter(AmazonBedrockClient client)
        {
            Console.WriteLine("List foundation models with no filter");

            ListFoundationModelsResponse response = await client.ListFoundationModelsAsync(new ListFoundationModelsRequest()
            {
            });

            if (response?.HttpStatusCode == System.Net.HttpStatusCode.OK)
            {
                foreach (var fm in response.ModelSummaries)
                {
                    WriteToConsole(fm);
                }
            }
            else
            {
                Console.WriteLine("Something wrong happened");
            }

            Console.WriteLine();
        }

        private static async Task ListFoundationModelsSupportingFineTuning(AmazonBedrockClient client)
        {
            Console.WriteLine("List foundation models supporting fine tuning");

            ListFoundationModelsResponse response = await client.ListFoundationModelsAsync(new ListFoundationModelsRequest()
            {
                ByCustomizationType = ModelCustomization.FINE_TUNING
            });

            if (response?.HttpStatusCode == System.Net.HttpStatusCode.OK)
            {
                foreach (var fm in response.ModelSummaries)
                {
                    WriteToConsole(fm);
                }
            }
            else
            {
                Console.WriteLine("Something wrong happened");
            }

            Console.WriteLine();
        }

        private static async Task ListFoundationModelsSupportingProvisionedInference(AmazonBedrockClient client)
        {
            Console.WriteLine("List foundation models supporting provisioned inference");

            ListFoundationModelsResponse response = await client.ListFoundationModelsAsync(new ListFoundationModelsRequest()
            {
                ByInferenceType = InferenceType.PROVISIONED
            });

            if (response?.HttpStatusCode == System.Net.HttpStatusCode.OK)
            {
                foreach (var fm in response.ModelSummaries)
                {
                    WriteToConsole(fm);
                }
            }
            else
            {
                Console.WriteLine("Something wrong happened");
            }

            Console.WriteLine();
        }

        private static async Task ListFoundationModelsSupportingOnDemandInference(AmazonBedrockClient client)
        {
            Console.WriteLine("List foundation models supporting on demand inference");

            ListFoundationModelsResponse response = await client.ListFoundationModelsAsync(new ListFoundationModelsRequest()
            {
                ByInferenceType = InferenceType.ON_DEMAND
            });

            if (response?.HttpStatusCode == System.Net.HttpStatusCode.OK)
            {
                foreach (var fm in response.ModelSummaries)
                {
                    WriteToConsole(fm);
                }
            }
            else
            {
                Console.WriteLine("Something wrong happened");
            }

            Console.WriteLine();
        }

        private static async Task ListFoundationModelsSupportingTextOuput(AmazonBedrockClient client)
        {
            Console.WriteLine("List foundation models supporting text Output");

            ListFoundationModelsResponse response = await client.ListFoundationModelsAsync(new ListFoundationModelsRequest()
            {
                ByOutputModality = ModelModality.TEXT
            });

            if (response?.HttpStatusCode == System.Net.HttpStatusCode.OK)
            {
                foreach (var fm in response.ModelSummaries)
                {
                    WriteToConsole(fm);
                }
            }
            else
            {
                Console.WriteLine("Something wrong happened");
            }

            Console.WriteLine();
        }

        private static async Task ListFoundationModelsSupportingImageOuput(AmazonBedrockClient client)
        {
            Console.WriteLine("List foundation models supporting image Output");

            ListFoundationModelsResponse response = await client.ListFoundationModelsAsync(new ListFoundationModelsRequest()
            {
                ByOutputModality = ModelModality.IMAGE
            });

            if (response?.HttpStatusCode == System.Net.HttpStatusCode.OK)
            {
                foreach (var fm in response.ModelSummaries)
                {
                    WriteToConsole(fm);
                }
            }
            else
            {
                Console.WriteLine("Something wrong happened");
            }

            Console.WriteLine();
        }

        private static async Task ListFoundationModelsSupportingEmbeddingOuput(AmazonBedrockClient client)
        {
            Console.WriteLine("List foundation models supporting embedding Output");

            ListFoundationModelsResponse response = await client.ListFoundationModelsAsync(new ListFoundationModelsRequest()
            {
                ByOutputModality = ModelModality.EMBEDDING
            });

            if (response?.HttpStatusCode == System.Net.HttpStatusCode.OK)
            {
                foreach (var fm in response.ModelSummaries)
                {
                    WriteToConsole(fm);
                }
            }
            else
            {
                Console.WriteLine("Something wrong happened");
            }

            Console.WriteLine();
        }

        private static async Task ListFoundationModelsSupportingProvisionedInferenceAndEmbeddingOutput(AmazonBedrockClient client)
        {
            Console.WriteLine("List foundation models supporting provisioned inference and embedding output");

            ListFoundationModelsResponse response = await client.ListFoundationModelsAsync(new ListFoundationModelsRequest()
            {
                ByInferenceType = InferenceType.PROVISIONED,
                ByOutputModality = ModelModality.EMBEDDING
            });

            if (response?.HttpStatusCode == System.Net.HttpStatusCode.OK)
            {
                foreach (var fm in response.ModelSummaries)
                {
                    WriteToConsole(fm);
                }
            }
            else
            {
                Console.WriteLine("Something wrong happened");
            }

            Console.WriteLine();
        }

        private static void WriteToConsole(FoundationModelSummary fm)
        {
            Console.WriteLine($"{fm.ModelId}, Customization: {String.Join(", ", fm.CustomizationsSupported)}, Stream: {fm.ResponseStreamingSupported}, Input: {String.Join(", ", fm.InputModalities)}, Output: {String.Join(", ", fm.OutputModalities)}");
        }
    }
}