using System;
using System.Collections.Generic;
using Amazon.Comprehend;
using Amazon.Comprehend.Model;

namespace ComprehendSamples1
{
    class UsingBatchAPIs
    {
        // Helper method for printing properties
        static private void PrintEntity(Entity entity)
        {
            Console.WriteLine("     Text: {0}, Type: {1}, Score: {2}, BeginOffset: {3} EndOffset: {4}",
                entity.Text, entity.Type, entity.Score, entity.BeginOffset, entity.EndOffset);
        }

        public static void Sample()
        {
            var comprehendClient = new AmazonComprehendClient(Amazon.RegionEndpoint.USWest2);

            var textList = new List<String>()
            {
                { "I love Seattle" },
                { "Today is Sunday" },
                { "Tomorrow is Monday" },
                { "I love Seattle" }
            };

            // Call detectEntities API
            Console.WriteLine("Calling BatchDetectEntities");
            var batchDetectEntitiesRequest = new BatchDetectEntitiesRequest()
            {
                TextList = textList,
                LanguageCode = "en"
            };
            var batchDetectEntitiesResponse = comprehendClient.BatchDetectEntities(batchDetectEntitiesRequest);

            foreach (var item in batchDetectEntitiesResponse.ResultList)
            {
                Console.WriteLine("Entities in {0}:", textList[item.Index]);
                foreach (Entity entity in item.Entities)
                    PrintEntity(entity);
            }

            // check if we need to retry failed requests
            if (batchDetectEntitiesResponse.ErrorList.Count != 0)
            {
                Console.WriteLine("Retrying Failed Requests");
                var textToRetry = new List<String>();
                foreach(var errorItem in batchDetectEntitiesResponse.ErrorList)
                    textToRetry.Add(textList[errorItem.Index]);

                batchDetectEntitiesRequest = new BatchDetectEntitiesRequest()
                {
                    TextList = textToRetry,
                    LanguageCode = "en"
                };

                batchDetectEntitiesResponse = comprehendClient.BatchDetectEntities(batchDetectEntitiesRequest);

                foreach(var item in batchDetectEntitiesResponse.ResultList)
                {
                    Console.WriteLine("Entities in {0}:", textList[item.Index]);
                    foreach (var entity in item.Entities)
                        PrintEntity(entity);
                }
            }
            Console.WriteLine("End of DetectEntities");
        }
    }
}
