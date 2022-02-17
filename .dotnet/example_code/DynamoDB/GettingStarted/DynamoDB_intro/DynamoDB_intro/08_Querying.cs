// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0
// snippet-sourcedescription:[ ]
// snippet-service:[dynamodb]
// snippet-keyword:[dotNET]
// snippet-keyword:[Amazon DynamoDB]
// snippet-keyword:[Code Sample]
// snippet-keyword:[ ]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[ ]
// snippet-sourceauthor:[AWS]
// snippet-start:[dynamodb.dotNET.CodeExample.08_Querying] 
using System;
using System.Threading.Tasks;
using Amazon.DynamoDBv2.Model;
using Amazon.DynamoDBv2.DocumentModel;
using System.Collections.Generic;

namespace DynamoDB_intro
{
    public static partial class DdbIntro
    {
        public static async Task<List<List<Document>>> SearchListing_async(Search search)
        {
            List<List<Document>> docsList = new List<List<Document>>();
            
            do
            {
                try
                {
                    var getNextBatch = search.GetNextSetAsync();
                    var docList = await getNextBatch;
                    docsList.Add(docList);
                }
                catch (Exception)
                {
                    return null;
                }
            } while (!search.IsDone);
            
            return docsList;
        }

        public static async Task<QueryResponse> ClientQuerying_async(QueryRequest qRequest)
        {
            var response = await Client.QueryAsync(qRequest);
            return response;
        }
    }
}
// snippet-end:[dynamodb.dotNET.CodeExample.08_Querying]