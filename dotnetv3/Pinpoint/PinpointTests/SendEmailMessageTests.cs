// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. 
// SPDX-License-Identifier: Apache-2.0

using Microsoft.Extensions.Configuration;
using SendEmailMessage;

namespace PinpointTests;

public class SendEmailMessageTests
{
    private readonly IConfiguration _configuration;
    private readonly string _region;
    private readonly string _senderAddress;
    private readonly string _toAddress;
    private readonly string _appId;

    /// <summary>
    /// Constructor for the test class.
    /// </summary>
    public SendEmailMessageTests()
    {
        _configuration = new ConfigurationBuilder()
            .SetBasePath(Directory.GetCurrentDirectory())
            .AddJsonFile("testsettings.json") // Load test settings from .json file.
            .AddJsonFile("testsettings.local.json",
                true) // Optionally, load local settings.
            .Build();

        _region = "us-east-1";
        _senderAddress = _configuration["SenderAddress"]!;
        _toAddress = _configuration["ToAddress"]!;
        _appId = _configuration["AppId"]!;
    }

    /// <summary>
    /// SendEmail should return exactly one message response.
    /// </summary>
    [Fact]
    [Trait("Category", "Integration")]
    public async Task SendEmailMessage_ShouldReturnOneResponse()
    {
        var messageResponse = await SendEmailMainClass.SendEmailMessage(
            _region, _appId, _toAddress, _senderAddress);
        Assert.NotNull(messageResponse);
        Assert.Single(messageResponse.Result);
    }
}