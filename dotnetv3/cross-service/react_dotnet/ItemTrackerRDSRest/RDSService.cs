using System.Xml;
using Amazon;
using Amazon.RDSDataService;
using Amazon.RDSDataService.Model;
using Amazon.SimpleEmail;
using Amazon.SimpleEmail.Model;

namespace ItemTrackerRDSRest
{
    public class RDSService
    {

        private string secretArcVal = "arn:aws:secretsmanager:us-east-1:814548047983:secret:sqlscott2-WEJX1b";
        private string resourceArnVal = "arn:aws:rds:us-east-1:814548047983:cluster:database-4";

        public string SecretArcVal
        {
            get => secretArcVal;
            set => secretArcVal = value;
        }

        public string ResourceArnVal
        {
            get => resourceArnVal;
            set => resourceArnVal = value;
        }

        // Get Items data from the database.
        public async Task<List<WorkItem>> GetItemsData(int arch)
        {
            var dataClient = new AmazonRDSDataServiceClient(RegionEndpoint.USEast1);
            var username = "User";
            List<WorkItem> records = new List<WorkItem>();
            var sqlStatement = "Select * FROM work where username = '" + username + "' and archive = " + arch + string.Empty;

            // Use trailing comma in multi-line initializers.
            var sqlRequest = new ExecuteStatementRequest
            {
                SecretArn = this.SecretArcVal,
                Sql = sqlStatement,
                Database = "jobs",
                ResourceArn = this.ResourceArnVal
            };

            CancellationToken token = CancellationToken.None;
            var response = await dataClient.ExecuteStatementAsync(sqlRequest, token);
            List<List<Field>> dataList = response.Records;
            var index = 0;

            foreach (var list in dataList)
            {
                var item = new WorkItem();
                index = 0;

                foreach (object myField in list)
                {
                    Field field = (Field)myField;
                    var value = field.StringValue;

                    if (index == 0)
                    {
                        item.Id = value;
                    }
                    else if (index == 1)
                    {
                        item.Date = value;
                    }
                    else if (index == 2)
                    {
                        item.Description = value;
                    }
                    else if (index == 3)
                    {
                        item.Guide = value;
                    }
                    else if (index == 4)
                    {
                        item.Status = value;
                    }
                    else if (index == 5)
                    {
                        item.Name = value;
                    }

                    // Increment the index.
                    index++;
                }

                records.Add(item);
            }

            return records;
        }

        // Inject a new submission.
        public async Task<string> injestNewSubmission(WorkItem item)
        {
            var dataClient = new AmazonRDSDataServiceClient(RegionEndpoint.USEast1);
            var workId = Guid.NewGuid().ToString();
            var name = "user";
            var sqlDate = DateTime.Now.ToString("yyyy-MM-dd HH:mm:ss");
            var guide = item.Guide;
            var description = item.Description;
            var status = item.Status;
            int arc = 0;

            var sqlStatement = "INSERT INTO work (idwork, username,date,description, guide, status, archive) VALUES('" + workId + "', '" + name + "', '" + sqlDate + "','" + description + "','" + guide + "','" + status + "','" + arc + "');";
            var sqlRequest = new ExecuteStatementRequest
            {
                SecretArn = this.SecretArcVal,
                Sql = sqlStatement,
                Database = "jobs",
                ResourceArn = this.ResourceArnVal,
            };

            CancellationToken token = CancellationToken.None;
            await dataClient.ExecuteStatementAsync(sqlRequest, token);
            return "Item " + workId + " was successfully inserted into the database";
        }

        // Gets data to place into an emial message.
        public async Task<string> GetItemsReport(int arch)
        {
            var dataClient = new AmazonRDSDataServiceClient(RegionEndpoint.USEast1);
            var username = "User";
            List<WorkItem> records = new List<WorkItem>();
            var sqlStatement = "Select * FROM work where username = '" + username + "' and archive = " + arch + string.Empty;

            var sqlRequest = new ExecuteStatementRequest
            {
                SecretArn = this.SecretArcVal,
                Sql = sqlStatement,
                Database = "jobs",
                ResourceArn = this.ResourceArnVal
            };

            CancellationToken token = CancellationToken.None;
            var response = await dataClient.ExecuteStatementAsync(sqlRequest, token);
            List<List<Field>> dataList = response.Records;
            var index = 0;

            foreach (var list in dataList)
            {
                var item = new WorkItem();
                index = 0;

                foreach (object myField in list)
                {
                    Field field = (Field)myField;
                    string value = field.StringValue;

                    if (index == 0)
                    {
                        item.Id = value;
                    }
                    else if (index == 1)
                    {
                        item.Date = value;
                    }
                    else if (index == 2)
                    {
                        item.Description = value;
                    }
                    else if (index == 3)
                    {
                        item.Guide = value;
                    }
                    else if (index == 4)
                    {
                        item.Status = value;
                    }
                    else if (index == 5)
                    {
                        item.Name = value;
                    }

                    // Increment the index.
                    index++;
                }

                records.Add(item);
            }

            string xml = this.GenerateXML(records);
            return xml;
        }

        // Modfies a record in the Work table.
        public async Task<string> FlipItemArchive(string id)
        {
            var dataClient = new AmazonRDSDataServiceClient(RegionEndpoint.USEast1);
            int arc = 1;

            // Specify the SQL statement to update data.
            var sqlStatement = "update work set archive = '" + arc + "' where idwork ='" + id + "' ";
            var sqlRequest = new ExecuteStatementRequest
            {
                SecretArn = this.SecretArcVal,
                Sql = sqlStatement,
                Database = "jobs",
                ResourceArn = this.ResourceArnVal
            };

            CancellationToken token = CancellationToken.None;
            await dataClient.ExecuteStatementAsync(sqlRequest, token);
            return "Item " + id + " was successfully updated";
        }

        // Converts the list to XML.
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

        // Sends an email message using SES.
        public async void SendMessage(string text, string toAddress)
        {
            var sesClient = new AmazonSimpleEmailServiceClient(RegionEndpoint.USWest2);
            var sender = "scmacdon@amazon.com";
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
                Data = "Amazon Report"
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
    }
}