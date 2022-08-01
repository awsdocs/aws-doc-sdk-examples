namespace AutoScale_Basics
{
    using Amazon.EC2;
    using Amazon.EC2.Model;

    public class EC2Methods
    {
        public static async Task<string> CreateLaunchTemplateAsync(
            string imageId,
            string instanceType,
            string launchTemplateName)
        {
            AmazonEC2Client client = new AmazonEC2Client();

            var request = new CreateLaunchTemplateRequest
            {
                LaunchTemplateData = new RequestLaunchTemplateData
                {
                    ImageId =imageId,
                    InstanceType = instanceType,
                },
                LaunchTemplateName = launchTemplateName,
            };

            var response = await client.CreateLaunchTemplateAsync(request);

            return response.LaunchTemplate.LaunchTemplateId;
        }

        public static async Task<string> DeleteLaunchTemplateAsync(string launchTemplateId)
        {
            AmazonEC2Client client = new AmazonEC2Client();

            var request = new DeleteLaunchTemplateRequest
            {
                LaunchTemplateId = launchTemplateId,
            };

            var response = await client.DeleteLaunchTemplateAsync(request);
            return response.LaunchTemplate.LaunchTemplateName;
        }
    }
}
