// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

using Amazon.IoT;
using Amazon.IoT.Model;
using Amazon.IotData;
using Amazon.IotData.Model;
using Microsoft.Extensions.Logging;
using System.Text.Json;

namespace IoTActions;

// snippet-start:[iot.dotnetv4.IoTWrapper]
/// <summary>
/// Wrapper methods to use Amazon IoT Core with .NET.
/// </summary>
public class IoTWrapper
{
    private readonly IAmazonIoT _amazonIoT;
    private readonly IAmazonIotData _amazonIotData;
    private readonly ILogger<IoTWrapper> _logger;

    /// <summary>
    /// Constructor for the IoT wrapper.
    /// </summary>
    /// <param name="amazonIoT">The injected IoT client.</param>
    /// <param name="amazonIotData">The injected IoT Data client.</param>
    /// <param name="logger">The injected logger.</param>
    public IoTWrapper(IAmazonIoT amazonIoT, IAmazonIotData amazonIotData, ILogger<IoTWrapper> logger)
    {
        _amazonIoT = amazonIoT;
        _amazonIotData = amazonIotData;
        _logger = logger;
    }

    // snippet-start:[iot.dotnetv4.CreateThing]
    /// <summary>
    /// Creates an AWS IoT Thing.
    /// </summary>
    /// <param name="thingName">The name of the Thing to create.</param>
    /// <returns>The ARN of the Thing created.</returns>
    public async Task<string> CreateThingAsync(string thingName)
    {
        try
        {
            var request = new CreateThingRequest
            {
                ThingName = thingName
            };

            var response = await _amazonIoT.CreateThingAsync(request);
            _logger.LogInformation($"Created Thing {thingName} with ARN {response.ThingArn}");
            return response.ThingArn;
        }
        catch (Exception ex)
        {
            _logger.LogError($"Error creating Thing {thingName}: {ex.Message}");
            throw;
        }
    }
    // snippet-end:[iot.dotnetv4.CreateThing]

    // snippet-start:[iot.dotnetv4.CreateKeysAndCertificate]
    /// <summary>
    /// Creates a device certificate for AWS IoT.
    /// </summary>
    /// <returns>The certificate details including ARN and certificate PEM.</returns>
    public async Task<(string CertificateArn, string CertificatePem, string CertificateId)> CreateKeysAndCertificateAsync()
    {
        try
        {
            var request = new CreateKeysAndCertificateRequest
            {
                SetAsActive = true
            };

            var response = await _amazonIoT.CreateKeysAndCertificateAsync(request);
            _logger.LogInformation($"Created certificate with ARN {response.CertificateArn}");
            return (response.CertificateArn, response.CertificatePem, response.CertificateId);
        }
        catch (Exception ex)
        {
            _logger.LogError($"Error creating certificate: {ex.Message}");
            throw;
        }
    }
    // snippet-end:[iot.dotnetv4.CreateKeysAndCertificate]

    // snippet-start:[iot.dotnetv4.AttachThingPrincipal]
    /// <summary>
    /// Attaches a certificate to an IoT Thing.
    /// </summary>
    /// <param name="thingName">The name of the Thing.</param>
    /// <param name="certificateArn">The ARN of the certificate to attach.</param>
    /// <returns>True if successful.</returns>
    public async Task<bool> AttachThingPrincipalAsync(string thingName, string certificateArn)
    {
        try
        {
            var request = new AttachThingPrincipalRequest
            {
                ThingName = thingName,
                Principal = certificateArn
            };

            await _amazonIoT.AttachThingPrincipalAsync(request);
            _logger.LogInformation($"Attached certificate {certificateArn} to Thing {thingName}");
            return true;
        }
        catch (Exception ex)
        {
            _logger.LogError($"Error attaching certificate to Thing: {ex.Message}");
            throw;
        }
    }
    // snippet-end:[iot.dotnetv4.AttachThingPrincipal]

    // snippet-start:[iot.dotnetv4.UpdateThing]
    /// <summary>
    /// Updates an IoT Thing with attributes.
    /// </summary>
    /// <param name="thingName">The name of the Thing to update.</param>
    /// <param name="attributes">Dictionary of attributes to add.</param>
    /// <returns>True if successful.</returns>
    public async Task<bool> UpdateThingAsync(string thingName, Dictionary<string, string> attributes)
    {
        try
        {
            var request = new UpdateThingRequest
            {
                ThingName = thingName,
                AttributePayload = new AttributePayload
                {
                    Attributes = attributes,
                    Merge = true
                }
            };

            await _amazonIoT.UpdateThingAsync(request);
            _logger.LogInformation($"Updated Thing {thingName} with attributes");
            return true;
        }
        catch (Exception ex)
        {
            _logger.LogError($"Error updating Thing attributes: {ex.Message}");
            throw;
        }
    }
    // snippet-end:[iot.dotnetv4.UpdateThing]

    // snippet-start:[iot.dotnetv4.DescribeEndpoint]
    /// <summary>
    /// Gets the AWS IoT endpoint URL.
    /// </summary>
    /// <returns>The endpoint URL.</returns>
    public async Task<string> DescribeEndpointAsync()
    {
        try
        {
            var request = new DescribeEndpointRequest
            {
                EndpointType = "iot:Data-ATS"
            };

            var response = await _amazonIoT.DescribeEndpointAsync(request);
            _logger.LogInformation($"Retrieved endpoint: {response.EndpointAddress}");
            return response.EndpointAddress;
        }
        catch (Exception ex)
        {
            _logger.LogError($"Error describing endpoint: {ex.Message}");
            throw;
        }
    }
    // snippet-end:[iot.dotnetv4.DescribeEndpoint]

    // snippet-start:[iot.dotnetv4.ListCertificates]
    /// <summary>
    /// Lists all certificates associated with the account.
    /// </summary>
    /// <returns>List of certificate information.</returns>
    public async Task<List<Certificate>> ListCertificatesAsync()
    {
        try
        {
            var request = new ListCertificatesRequest();
            var response = await _amazonIoT.ListCertificatesAsync(request);
            
            _logger.LogInformation($"Retrieved {response.Certificates.Count} certificates");
            return response.Certificates;
        }
        catch (Exception ex)
        {
            _logger.LogError($"Error listing certificates: {ex.Message}");
            throw;
        }
    }
    // snippet-end:[iot.dotnetv4.ListCertificates]

    // snippet-start:[iot.dotnetv4.UpdateThingShadow]
    /// <summary>
    /// Updates the Thing's shadow with new state information.
    /// </summary>
    /// <param name="thingName">The name of the Thing.</param>
    /// <param name="shadowPayload">The shadow payload in JSON format.</param>
    /// <returns>True if successful.</returns>
    public async Task<bool> UpdateThingShadowAsync(string thingName, string shadowPayload)
    {
        try
        {
            var request = new UpdateThingShadowRequest
            {
                ThingName = thingName,
                Payload = new MemoryStream(System.Text.Encoding.UTF8.GetBytes(shadowPayload))
            };

            await _amazonIotData.UpdateThingShadowAsync(request);
            _logger.LogInformation($"Updated shadow for Thing {thingName}");
            return true;
        }
        catch (Exception ex)
        {
            _logger.LogError($"Error updating Thing shadow: {ex.Message}");
            throw;
        }
    }
    // snippet-end:[iot.dotnetv4.UpdateThingShadow]

    // snippet-start:[iot.dotnetv4.GetThingShadow]
    /// <summary>
    /// Gets the Thing's shadow information.
    /// </summary>
    /// <param name="thingName">The name of the Thing.</param>
    /// <returns>The shadow data as a string.</returns>
    public async Task<string> GetThingShadowAsync(string thingName)
    {
        try
        {
            var request = new GetThingShadowRequest
            {
                ThingName = thingName
            };

            var response = await _amazonIotData.GetThingShadowAsync(request);
            using var reader = new StreamReader(response.Payload);
            var shadowData = await reader.ReadToEndAsync();
            
            _logger.LogInformation($"Retrieved shadow for Thing {thingName}");
            return shadowData;
        }
        catch (Exception ex)
        {
            _logger.LogError($"Error getting Thing shadow: {ex.Message}");
            throw;
        }
    }
    // snippet-end:[iot.dotnetv4.GetThingShadow]

    // snippet-start:[iot.dotnetv4.CreateTopicRule]
    /// <summary>
    /// Creates an IoT topic rule.
    /// </summary>
    /// <param name="ruleName">The name of the rule.</param>
    /// <param name="snsTopicArn">The ARN of the SNS topic for the action.</param>
    /// <param name="roleArn">The ARN of the IAM role.</param>
    /// <returns>True if successful.</returns>
    public async Task<bool> CreateTopicRuleAsync(string ruleName, string snsTopicArn, string roleArn)
    {
        try
        {
            var request = new CreateTopicRuleRequest
            {
                RuleName = ruleName,
                TopicRulePayload = new TopicRulePayload
                {
                    Sql = "SELECT * FROM 'topic/subtopic'",
                    Description = $"Rule created by .NET example: {ruleName}",
                    Actions = new List<Amazon.IoT.Model.Action>
                    {
                        new Amazon.IoT.Model.Action
                        {
                            Sns = new SnsAction
                            {
                                TargetArn = snsTopicArn,
                                RoleArn = roleArn
                            }
                        }
                    },
                    RuleDisabled = false
                }
            };

            await _amazonIoT.CreateTopicRuleAsync(request);
            _logger.LogInformation($"Created IoT rule {ruleName}");
            return true;
        }
        catch (Exception ex)
        {
            _logger.LogError($"Error creating topic rule: {ex.Message}");
            throw;
        }
    }
    // snippet-end:[iot.dotnetv4.CreateTopicRule]

    // snippet-start:[iot.dotnetv4.ListTopicRules]
    /// <summary>
    /// Lists all IoT topic rules.
    /// </summary>
    /// <returns>List of topic rules.</returns>
    public async Task<List<TopicRuleListItem>> ListTopicRulesAsync()
    {
        try
        {
            var request = new ListTopicRulesRequest();
            var response = await _amazonIoT.ListTopicRulesAsync(request);
            
            _logger.LogInformation($"Retrieved {response.Rules.Count} IoT rules");
            return response.Rules;
        }
        catch (Exception ex)
        {
            _logger.LogError($"Error listing topic rules: {ex.Message}");
            throw;
        }
    }
    // snippet-end:[iot.dotnetv4.ListTopicRules]

    // snippet-start:[iot.dotnetv4.SearchIndex]
    /// <summary>
    /// Searches for IoT Things using the search index.
    /// </summary>
    /// <param name="queryString">The search query string.</param>
    /// <returns>List of Things that match the search criteria.</returns>
    public async Task<List<ThingDocument>> SearchIndexAsync(string queryString)
    {
        try
        {
            var request = new SearchIndexRequest
            {
                IndexName = "AWS_Things",
                QueryString = queryString
            };

            var response = await _amazonIoT.SearchIndexAsync(request);
            _logger.LogInformation($"Search found {response.Things.Count} Things");
            return response.Things;
        }
        catch (Exception ex)
        {
            _logger.LogError($"Error searching index: {ex.Message}");
            throw;
        }
    }
    // snippet-end:[iot.dotnetv4.SearchIndex]

    // snippet-start:[iot.dotnetv4.DetachThingPrincipal]
    /// <summary>
    /// Detaches a certificate from an IoT Thing.
    /// </summary>
    /// <param name="thingName">The name of the Thing.</param>
    /// <param name="certificateArn">The ARN of the certificate to detach.</param>
    /// <returns>True if successful.</returns>
    public async Task<bool> DetachThingPrincipalAsync(string thingName, string certificateArn)
    {
        try
        {
            var request = new DetachThingPrincipalRequest
            {
                ThingName = thingName,
                Principal = certificateArn
            };

            await _amazonIoT.DetachThingPrincipalAsync(request);
            _logger.LogInformation($"Detached certificate {certificateArn} from Thing {thingName}");
            return true;
        }
        catch (Exception ex)
        {
            _logger.LogError($"Error detaching certificate from Thing: {ex.Message}");
            throw;
        }
    }
    // snippet-end:[iot.dotnetv4.DetachThingPrincipal]

    // snippet-start:[iot.dotnetv4.DeleteCertificate]
    /// <summary>
    /// Deletes an IoT certificate.
    /// </summary>
    /// <param name="certificateId">The ID of the certificate to delete.</param>
    /// <returns>True if successful.</returns>
    public async Task<bool> DeleteCertificateAsync(string certificateId)
    {
        try
        {
            // First, update the certificate to inactive state
            var updateRequest = new UpdateCertificateRequest
            {
                CertificateId = certificateId,
                NewStatus = CertificateStatus.INACTIVE
            };
            await _amazonIoT.UpdateCertificateAsync(updateRequest);

            // Then delete the certificate
            var deleteRequest = new DeleteCertificateRequest
            {
                CertificateId = certificateId
            };

            await _amazonIoT.DeleteCertificateAsync(deleteRequest);
            _logger.LogInformation($"Deleted certificate {certificateId}");
            return true;
        }
        catch (Exception ex)
        {
            _logger.LogError($"Error deleting certificate: {ex.Message}");
            throw;
        }
    }
    // snippet-end:[iot.dotnetv4.DeleteCertificate]

    // snippet-start:[iot.dotnetv4.DeleteThing]
    /// <summary>
    /// Deletes an IoT Thing.
    /// </summary>
    /// <param name="thingName">The name of the Thing to delete.</param>
    /// <returns>True if successful.</returns>
    public async Task<bool> DeleteThingAsync(string thingName)
    {
        try
        {
            var request = new DeleteThingRequest
            {
                ThingName = thingName
            };

            await _amazonIoT.DeleteThingAsync(request);
            _logger.LogInformation($"Deleted Thing {thingName}");
            return true;
        }
        catch (Exception ex)
        {
            _logger.LogError($"Error deleting Thing: {ex.Message}");
            throw;
        }
    }
    // snippet-end:[iot.dotnetv4.DeleteThing]

    // snippet-start:[iot.dotnetv4.ListThings]
    /// <summary>
    /// Lists IoT Things with pagination support.
    /// </summary>
    /// <returns>List of Things.</returns>
    public async Task<List<ThingAttribute>> ListThingsAsync()
    {
        try
        {
            var request = new ListThingsRequest();
            var response = await _amazonIoT.ListThingsAsync(request);
            
            _logger.LogInformation($"Retrieved {response.Things.Count} Things");
            return response.Things;
        }
        catch (Exception ex)
        {
            _logger.LogError($"Error listing Things: {ex.Message}");
            throw;
        }
    }
    // snippet-end:[iot.dotnetv4.ListThings]
}
// snippet-end:[iot.dotnetv4.IoTWrapper]
