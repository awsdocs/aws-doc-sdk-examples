using Amazon.Glacier;
using Amazon.Glacier.Model;
using System;
using System.Collections.Generic;
using System.Threading.Tasks;

namespace ListTagsForVaultExample
{
    class ListTagsForVault
    {
        static async Task Main(string[] args)
        {
            var client = new AmazonGlacierClient();
            var vaultName = "example-vault";

            var request = new ListTagsForVaultRequest
            {
                AccountId = "-",
                VaultName = vaultName,
            };

            var response = await client.ListTagsForVaultAsync(request);

            if (response.Tags.Count > 0)
            {
                foreach (KeyValuePair<string, string> tag in response.Tags)
                {
                    Console.WriteLine($"Key: {tag.Key}, value: {tag.Value}");
                }
            }
            else
            {
                Console.WriteLine($"{vaultName} has not tags.");
            }
        }
    }
}
