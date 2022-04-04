using System;
using System.Collections.Generic;
using System.Threading.Tasks;
using System.Xml;
using Amazon;
using Amazon.Rekognition;
using Amazon.Rekognition.Model;
using Amazon.S3;
using Amazon.S3.Model;
using Amazon.SimpleEmail;
using Amazon.SimpleEmail.Model;

namespace PhotoAnalyzerApp.Controllers
{
    public class AWSService
    {
        public async void SendMessage(string text, string toAddress)
        {
            var sesClient = new AmazonSimpleEmailServiceClient(RegionEndpoint.USWest2);
            var sender = "<Enter a valid email address>";
            var emailList = new List<string>();
            emailList.Add(toAddress);

            var destination = new Destination
            {
                ToAddresses = emailList
            };

            var content = new Content
            {
                Data = text
            };

            var sub = new Content
            {
                Data = "Amazon Rekognition Report"
            };

            var body = new Body
            {
                Text = content
            };

            var message = new Message
            {
                Subject = sub,
                Body = body
            };

            var request = new SendEmailRequest
            {
                Message = message,
                Destination = destination,
                Source = sender
            };

            try
            {
                await sesClient.SendEmailAsync(request);
            }
            catch (Exception ex)
            {
                Console.WriteLine(ex.Message);
            }
        }

        // Uses the Amazon Rekognition service to detect labels within an image.
        public async Task<List<WorkItem>> DetectLabels(string bucketName, string photo)
        {
            var rekognitionClient = new AmazonRekognitionClient(RegionEndpoint.USWest2);
            var labelList = new List<WorkItem>();
            var detectlabelsRequest = new DetectLabelsRequest
            {
                Image = new Image()
                {
                    S3Object = new Amazon.Rekognition.Model.S3Object()
                    {
                        Name = photo,
                        Bucket = bucketName,
                    },
                },
                MaxLabels = 10,
                MinConfidence = 75F,
            };

            try
            {
                DetectLabelsResponse detectLabelsResponse = await rekognitionClient.DetectLabelsAsync(detectlabelsRequest);
                Console.WriteLine("Detected labels for " + photo);
                WorkItem workItem;
                foreach (Label label in detectLabelsResponse.Labels)
                {
                    workItem = new WorkItem();
                    workItem.Key = photo;
                    workItem.Confidence = label.Confidence.ToString();
                    workItem.Name = label.Name;
                    labelList.Add(workItem);
                }

                return labelList;
            }
            catch (Exception ex)
            {
                Console.WriteLine(ex.Message);
            }

            return null;
        }

        // Returns object names in the S3 bucket.
        public async Task<List<string>> ListBucketNames(string bucketName)
        {
            var s3Client = new AmazonS3Client(RegionEndpoint.USWest2);
            var obList = new List<Amazon.S3.Model.S3Object>();
            var obName = new List<string>();

            var listRequest = new ListObjectsRequest
            {
                BucketName = bucketName,
            };

            ListObjectsResponse response = await s3Client.ListObjectsAsync(listRequest);
            obList = response.S3Objects;
            foreach (var myobj in obList)
                obName.Add(myobj.Key);
            return obName;
        }

        // Returns information about all images in the given S3 bucket.
        public async Task<string> ListBucketObjects(string bucketName)
        {
            var s3Client = new AmazonS3Client(RegionEndpoint.USWest2);
            var obList = new List<Amazon.S3.Model.S3Object>();
            var obName = new List<BucketItem>();

            var listRequest = new ListObjectsRequest
            {
                BucketName = bucketName,
            };

            ListObjectsResponse response = await s3Client.ListObjectsAsync(listRequest);
            obList = response.S3Objects;
            BucketItem myItem;

            foreach (var myobj in obList)
            {
                myItem = new BucketItem();
                myItem.Key = myobj.Key;
                myItem.Owner = myobj.Owner.DisplayName;
                long sizeLg = myobj.Size / 1024;
                myItem.Size = sizeLg.ToString();
                obName.Add(myItem);
            }

            var xml = GenerateXML(obName);
            return xml;
        }

        // Convert the list to XML to pass back to the view.
        private string GenerateXML(List<BucketItem> obList)
        {
            XmlDocument doc = new XmlDocument();
            XmlNode docNode = doc.CreateXmlDeclaration("1.0", "UTF-8", null);
            doc.AppendChild(docNode);

            XmlNode subsNode = doc.CreateElement("Items");
            doc.AppendChild(subsNode);

            // Iterate through the collection.
            foreach (BucketItem item in obList)
            {
                XmlNode subNode = doc.CreateElement("Item");
                subsNode.AppendChild(subNode);

                XmlNode name = doc.CreateElement("Key");
                name.AppendChild(doc.CreateTextNode(item.Key));
                subNode.AppendChild(name);

                XmlNode owner = doc.CreateElement("Owner");
                owner.AppendChild(doc.CreateTextNode(item.Owner));
                subNode.AppendChild(owner);

                XmlNode size = doc.CreateElement("Size");
                size.AppendChild(doc.CreateTextNode(item.Size));
                subNode.AppendChild(size);
            }

            return doc.OuterXml;
        }

        // Convert the list to XML to pass back to the view.
        public string GenerateXMLFromList(List<List<WorkItem>> obList)
        {
            XmlDocument doc = new XmlDocument();
            XmlNode docNode = doc.CreateXmlDeclaration("1.0", "UTF-8", null);
            doc.AppendChild(docNode);

            XmlNode subsNode = doc.CreateElement("Items");
            doc.AppendChild(subsNode);

            // Iterate through the outer list.
            foreach (var listItem in obList)
            {
                // Iterate through the collection of WorkItem objects.
                foreach (WorkItem item in listItem)
                {
                    XmlNode subNode = doc.CreateElement("Item");
                    subsNode.AppendChild(subNode);

                    XmlNode name = doc.CreateElement("Photo");
                    name.AppendChild(doc.CreateTextNode(item.Key));
                    subNode.AppendChild(name);

                    XmlNode owner = doc.CreateElement("Confidence");
                    owner.AppendChild(doc.CreateTextNode(item.Confidence));
                    subNode.AppendChild(owner);

                    XmlNode size = doc.CreateElement("Label");
                    size.AppendChild(doc.CreateTextNode(item.Name));
                    subNode.AppendChild(size);
                }
            }

            return doc.OuterXml;
        }
    }
}