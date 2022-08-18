// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

namespace AutoScale_Basics
{
    // snippet-start:[AutoScale.dotnetv3.AutoScale_Basics.EC2Methods]
    using Amazon.EC2;
    using Amazon.EC2.Model;

    /// <summary>
    /// The methods in this class create and delete an Amazon Elastic Compute
    /// Cloud (Amazon EC2) launch template for use by the Amazon EC2 Auto
    /// Scaling scenario.
    /// </summary>
    public class EC2Methods
    {
        /// <summary>
        /// Create a new Amazon EC2 launch template.
        /// </summary>
        /// <param name="imageId">The image Id to use for instances launched
        /// using the Amazon EC2 launch template.</param>
        /// <param name="instanceType">The type of EC2 instances to create.</param>
        /// <param name="launchTemplateName">The name of the launch template.</param>
        /// <returns>Returns the TemplaceID of the new launch template.</returns>
        public static async Task<string> CreateLaunchTemplateAsync(
            string imageId,
            string instanceType,
            string launchTemplateName)
        {
            var client = new AmazonEC2Client();

            var request = new CreateLaunchTemplateRequest
            {
                LaunchTemplateData = new RequestLaunchTemplateData
                {
                    ImageId = imageId,
                    InstanceType = instanceType,
                },
                LaunchTemplateName = launchTemplateName,
            };

            var response = await client.CreateLaunchTemplateAsync(request);

            return response.LaunchTemplate.LaunchTemplateId;
        }

        /// <summary>
        /// Deletes an Amazon EC2 launch template.
        /// </summary>
        /// <param name="launchTemplateId">The TemplateId of the launch template to
        /// delete.</param>
        /// <returns>The name of the EC2 launch template that was deleted.</returns>
        public static async Task<string> DeleteLaunchTemplateAsync(string launchTemplateId)
        {
            var client = new AmazonEC2Client();

            var request = new DeleteLaunchTemplateRequest
            {
                LaunchTemplateId = launchTemplateId,
            };

            var response = await client.DeleteLaunchTemplateAsync(request);
            return response.LaunchTemplate.LaunchTemplateName;
        }

        /// <summary>
        /// Retrieves a information about an EC2 launch template.
        /// </summary>
        /// <param name="launchTemplateName">The name of the EC2 launch template.</param>
        /// <returns>A Boolean value that indicates the success or failure of
        /// the operation.</returns>
        public static async Task<bool> DescribeLaunchTemplateAsync(string launchTemplateName)
        {
            var client = new AmazonEC2Client();

            var request = new DescribeLaunchTemplatesRequest
            {
                LaunchTemplateNames = new List<string> { launchTemplateName, },
            };

            var response = await client.DescribeLaunchTemplatesAsync(request);

            if (response.LaunchTemplates != null)
            {
                response.LaunchTemplates.ForEach(template =>
                {
                    Console.Write($"{template.LaunchTemplateName}\t");
                    Console.WriteLine(template.LaunchTemplateId);
                });

                return true;
            }

            return false;
        }
    }

    // snippet-end:[AutoScale.dotnetv3.AutoScale_Basics.EC2Methods]
}
