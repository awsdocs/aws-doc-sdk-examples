// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

namespace SNSExample.Controllers
{
    using System.Collections.Generic;
    using System.Threading.Tasks;
    using System.Xml;
    using Amazon;
    using Amazon.SimpleNotificationService;
    using Amazon.SimpleNotificationService.Model;
    using Amazon.Translate;
    using Amazon.Translate.Model;

    public class SnsService
    {
        private static readonly string TopicArn = "<PUT TOPIC ARN HERE>";

        public async Task<string> UnSubEmail(string email)
        {
            var client = new AmazonSimpleNotificationServiceClient(RegionEndpoint.USEast2);
            var arnValue = await GetSubArn(client, email);
            await RemoveSub(client, arnValue);
            return $"{email} was successfully deleted!";
        }

        public async Task<string> PubTopic(string body, string lang)
        {
            var client = new AmazonSimpleNotificationServiceClient(RegionEndpoint.USEast2);
            var message = string.Empty;

            switch (lang.ToLower())
            {
                case "french":
                    message = TranslateBody(body, "fr");
                    break;
                case "spanish":
                    message = TranslateBody(body, "es");
                    break;
                default:
                    message = body;
                    break;
            }

            var msgId = await PublishMessage(client, message);
            return msgId;
        }

        public async Task<string> SubEmail(string email)
        {
            var client = new AmazonSimpleNotificationServiceClient(RegionEndpoint.USEast2);
            var subArn = await SubscribeEmail(client, email);
            return subArn;
        }

        public async Task<string> GetSubs()
        {
            var client = new AmazonSimpleNotificationServiceClient(RegionEndpoint.USEast2);
            var subscriptions = await GetSubscriptionsListAsync(client);
            var val = DisplaySubscriptionList(subscriptions);
            return val;
        }

        public static async Task<string> RemoveSub(IAmazonSimpleNotificationService client, string subArn)
        {
            var request = new UnsubscribeRequest();
            request.SubscriptionArn = subArn;
            await client.UnsubscribeAsync(request);

            return string.Empty;
        }

        public static async Task<string> GetSubArn(IAmazonSimpleNotificationService client, string email)
        {
            var request = new ListSubscriptionsByTopicRequest();
            request.TopicArn = TopicArn;
            var subArn = string.Empty;

            var response = await client.ListSubscriptionsByTopicAsync(request);
            List<Subscription> allSubs = response.Subscriptions;

            // Get the ARN Value for this subscription.
            foreach (Subscription sub in allSubs)
            {
                if (sub.Endpoint.Equals(email))
                {
                    subArn = sub.SubscriptionArn;
                    return subArn;
                }
            }

            return string.Empty;
        }

        public static async Task<string> PublishMessage(IAmazonSimpleNotificationService client, string body)
        {
            var request = new PublishRequest();
            request.Message = body;
            request.TopicArn = TopicArn;

            var response = await client.PublishAsync(request);

            return response.MessageId;
        }

        public static async Task<string> SubscribeEmail(IAmazonSimpleNotificationService client, string email)
        {
            var request = new SubscribeRequest();
            request.Protocol = "email";
            request.Endpoint = email;
            request.TopicArn = TopicArn;
            request.ReturnSubscriptionArn = true;

            var response = await client.SubscribeAsync(request);

            return response.SubscriptionArn;
        }

        public static async Task<List<Subscription>> GetSubscriptionsListAsync(IAmazonSimpleNotificationService client)
        {
            var request = new ListSubscriptionsByTopicRequest
            {
                TopicArn = TopicArn,
            };
            var response = await client.ListSubscriptionsByTopicAsync(request);
            return response.Subscriptions;
        }

        public string DisplaySubscriptionList(List<Subscription> subscriptionList)
        {
            var email = string.Empty;
            List<string> emailList = new List<string>();
            foreach (var subscription in subscriptionList)
            {
                emailList.Add(subscription.Endpoint);
                email = subscription.Endpoint;
            }

            var xml = GenerateXML(emailList);
            return xml;
        }

        // Convert the list to XML to pass back to the view.
        private string GenerateXML(List<string> subsList)
        {
            XmlDocument doc = new XmlDocument();
            XmlNode docNode = doc.CreateXmlDeclaration("1.0", "UTF-8", null);
            doc.AppendChild(docNode);

            XmlNode subsNode = doc.CreateElement("Subs");
            doc.AppendChild(subsNode);

            // Iterate through the collection.
            foreach (string sub in subsList)
            {
                XmlNode subNode = doc.CreateElement("Sub");
                subsNode.AppendChild(subNode);

                XmlNode email = doc.CreateElement("email");
                email.AppendChild(doc.CreateTextNode(sub));
                subNode.AppendChild(email);
            }

            return doc.OuterXml;
        }

        private string TranslateBody(string msg, string lan)
        {
            var translateClient = new AmazonTranslateClient(RegionEndpoint.USEast2);
            var request = new TranslateTextRequest
            {
                SourceLanguageCode = "en",
                TargetLanguageCode = lan,
                Text = msg,
            };

            var response = translateClient.TranslateTextAsync(request);
            return response.Result.TranslatedText;
        }
    }
}
