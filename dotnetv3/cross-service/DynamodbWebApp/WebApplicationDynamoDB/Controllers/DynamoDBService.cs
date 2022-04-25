/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

using System;
using System.Collections.Generic;
using System.Threading.Tasks;
using System.Xml;
using Amazon;
using Amazon.DynamoDBv2;
using Amazon.DynamoDBv2.DocumentModel;
using Amazon.DynamoDBv2.Model;
using Amazon.S3;
using Amazon.S3.Model;

namespace WebApplicationDynamoDB.Controllers
{
    public class DynamoDBService
    {
        public async void ArchiveItemEC(string id)
        {
            var client = new AmazonDynamoDBClient(RegionEndpoint.USEast1);
            await UpdateRecord(client, id);
        }

        public async void S3Report()
        {
            var client = new AmazonDynamoDBClient(RegionEndpoint.USEast1);
            var xml = await GetActiveItems(client, "Open");
            await UploadObject(xml);
        }

        public async Task<string> GetSingleItem(string id)
        {
            var client = new AmazonDynamoDBClient();
            var xml = await GetItemAsync(client, id);
            return xml;
        }

        public async Task<string> ModSingleItem(string id)
        {
            var client = new AmazonDynamoDBClient();
            var xml = await GetItemAsync(client, id);
            return xml;
        }

        public async Task<string> ModStatus(string id, string status)
        {
            var client = new AmazonDynamoDBClient(RegionEndpoint.USWest2);
            await UpdateStatus(client, id, status);
            return "Item " + id + " was succefully updated";
        }

        public async Task UpdateStatus(AmazonDynamoDBClient client, string id, string status)
        {
            Table workTable = Table.LoadTable(client, "Work");
            var myGuid = Guid.NewGuid().ToString();

            // Add the new record to the Amazon DynamoDB table.
            var record = new Document();
            record["id"] = id;
            record["status"] = status;
            await workTable.UpdateItemAsync(record);
        }

        public async Task<string> GetItems(string status)
        {
            var client = new AmazonDynamoDBClient(RegionEndpoint.USEast1);
            var xml = await GetActiveItems(client, status);
            return xml;
        }

        public async Task<string> AddNewRecord(string description, string guide, string status)
        {
            var client = new AmazonDynamoDBClient(RegionEndpoint.USEast1);
            var guid = await InsertRecord(client, description, guide, status);
            return guid;
        }

        public async Task UploadObject(string report)
        {
            var bucketName = "bucketscottfoo73";
            var s3Client = new AmazonS3Client(RegionEndpoint.USEast1);

            // Make sure that the report name is unique.
            var reportName = "report_" + DateTime.Now.ToString("yyyy-MM-dd HH:mm:ss") + ".xml";

            var putRequest = new PutObjectRequest
            {
                BucketName = bucketName,
                Key = reportName,
                ContentBody = report,
                ContentType = "text/xml",
            };

            putRequest.Metadata.Add("x-amz-meta-title", "someTitle");
            PutObjectResponse response = await s3Client.PutObjectAsync(putRequest);
        }

        public async Task UpdateRecord(AmazonDynamoDBClient client, string id)
        {
            Table workTable = Table.LoadTable(client, "Work");
            var myGuid = Guid.NewGuid().ToString();

            // Add the new record to the Amazon DynamoDB table.
            var record = new Document();
            record["id"] = id;
            record["archive"] = "Close";
            await workTable.UpdateItemAsync(record);
        }

        public async Task<string> GetActiveItems(AmazonDynamoDBClient client, string status)
        {
            var request = new ScanRequest
            {
                TableName = "Work",
                ExpressionAttributeValues = new Dictionary<string, AttributeValue>
                {
                    {
                        ":val", new AttributeValue
                     {
                         S = status
                     }
                    }
                },
                ExpressionAttributeNames = new Dictionary<string, string>
                {
                    {
                        "#archive", "archive"
                    }
                },
                FilterExpression = "#archive = :val",
            };

            var response = await client.ScanAsync(request);
            WorkItem workItem;
            List<WorkItem> workList = new List<WorkItem>();
            foreach (Dictionary<string, AttributeValue> item
                     in response.Items)
            {
                Console.WriteLine("\nScanThreadTableUsePaging - printing.....");
                var index = 0;

                workItem = new WorkItem();
                foreach (KeyValuePair<string, AttributeValue> kvp in item)
                {
                    string attributeName = kvp.Key;
                    AttributeValue value = kvp.Value;

                    // Populate the WorkItem instance.
                    switch (index)
                    {
                        case 0:
                            workItem.Date = value.S;
                            break;

                        case 1:
                            workItem.Status = value.S;
                            break;

                        case 2:
                            workItem.Name = value.S;
                            break;

                        case 3:
                            break;

                        case 4:
                            workItem.Description = value.S;
                            break;

                        case 5:
                            workItem.Id = value.S;
                            break;

                        case 6:
                            workItem.Guide = value.S;
                            break;
                    }

                    index++;
                }

                // Push the WorkItem to the collection.
                workList.Add(workItem);
            }

            var xml = GenerateXML(workList);
            return xml;
        }

        // Convert the list to XML to pass back to the view.
        private string GenerateXML(List<WorkItem> workList)
        {
            XmlDocument doc = new XmlDocument();
            XmlNode docNode = doc.CreateXmlDeclaration("1.0", "UTF-8", null);
            doc.AppendChild(docNode);

            XmlNode subsNode = doc.CreateElement("Items");
            doc.AppendChild(subsNode);

            // Iterate through the collection.
            foreach (WorkItem item in workList)
            {
                XmlNode subNode = doc.CreateElement("Item");
                subsNode.AppendChild(subNode);

                XmlNode id = doc.CreateElement("Id");
                id.AppendChild(doc.CreateTextNode(item.Id));
                subNode.AppendChild(id);

                XmlNode name = doc.CreateElement("Name");
                name.AppendChild(doc.CreateTextNode(item.Name));
                subNode.AppendChild(name);

                XmlNode date = doc.CreateElement("Date");
                date.AppendChild(doc.CreateTextNode(item.Date));
                subNode.AppendChild(date);

                XmlNode description = doc.CreateElement("Description");
                description.AppendChild(doc.CreateTextNode(item.Description));
                subNode.AppendChild(description);

                XmlNode guide = doc.CreateElement("Guide");
                guide.AppendChild(doc.CreateTextNode(item.Guide));
                subNode.AppendChild(guide);

                XmlNode status = doc.CreateElement("Status");
                status.AppendChild(doc.CreateTextNode(item.Status));
                subNode.AppendChild(status);
            }

            return doc.OuterXml;
        }

        public async Task<string> InsertRecord(IAmazonDynamoDB client, string description, string guide, string status)
        {
            Table workTable = Table.LoadTable(client, "Work");
            var myGuid = Guid.NewGuid().ToString();

            // Add the new record to the Amazon DynamoDB table.
            var record = new Document();
            record["id"] = myGuid;
            record["archive"] = "Open";
            record["date"] = DateTime.Now.ToString("yyyy-MM-dd HH:mm:ss");
            record["description"] = description;
            record["guide"] = guide;
            record["status"] = status;
            record["username"] = "user";

            await workTable.PutItemAsync(record);
            return myGuid;
        }

        public async Task<string> GetItemAsync(IAmazonDynamoDB client, string id)
        {
            var response = await client.QueryAsync(new QueryRequest
            {
                TableName = "Work",
                KeyConditionExpression = "id = :v_Id",
                ExpressionAttributeValues = new Dictionary<string, AttributeValue>
               {
                {
                ":v_Id",
                new AttributeValue
                {
                  S = id,
                }
                },
               },
            });

            WorkItem workItem;
            List<WorkItem> workList = new List<WorkItem>();
            foreach (Dictionary<string, AttributeValue> item
                     in response.Items)
            {
                Console.WriteLine("\nScanThreadTableUsePaging - printing.....");
                var index = 0;
                workItem = new WorkItem();
                foreach (KeyValuePair<string, AttributeValue> kvp in item)
                {
                    string attributeName = kvp.Key;
                    AttributeValue value = kvp.Value;

                    // Populate the WorkItem instance.
                    switch (index)
                    {
                        case 0:
                            workItem.Date = value.S;
                            break;

                        case 1:
                            workItem.Status = value.S;
                            break;

                        case 2:
                            workItem.Name = value.S;
                            break;

                        case 3:
                            break;

                        case 4:
                            workItem.Description = value.S;
                            break;

                        case 5:
                            workItem.Id = value.S;
                            break;

                        case 6:
                            workItem.Guide = value.S;
                            break;
                    }

                    index++;
                }

                // Push the WorkItem to the collection.
                workList.Add(workItem);
            }

            var xml = GenerateXML(workList);
            return xml;
        }
    }
}