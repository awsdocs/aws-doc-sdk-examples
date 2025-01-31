// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. 
// SPDX-License-Identifier: Apache-2.0

using Amazon.Pinpoint;
using Microsoft.Extensions.Configuration;
using SendSmsMessage;

namespace PinpointTests;

public class SendSmsMessageTests
{
    private readonly IConfiguration _configuration;
    private readonly string _region;
    private readonly string _destinationNumber;
    private readonly string _originationNumber;
    private readonly string _registeredKeyword;
    private readonly string _senderId;
    private readonly string _appId;

    /// <summary>
    /// Constructor for the test class.
    /// </summary>
    public SendSmsMessageTests()
    {
        _configuration = new ConfigurationBuilder()
            .SetBasePath(Directory.GetCurrentDirectory())
            .AddJsonFile("testsettings.json") // Load test settings from .json file.
            .AddJsonFile("testsettings.local.json",
                true) // Optionally, load local settings.
            .Build();

        _region = "us-east-1";
        _destinationNumber = _configuration["DestinationNumber"]!;
        _originationNumber = _configuration["OriginationNumber"]!;
        _registeredKeyword = _configuration["RegisteredKeyword"]!;
        _senderId = _configuration["SenderId"]!;
        _appId = _configuration["AppId"]!;
    }

    /// <summary>
    /// SendSmsMessage should return exactly one message response.
    /// </summary>
    [Fact]
    [Trait("Category", "Integration")]
    public async Task SendSmsMessage_ShouldReturnOneResponse()
    {
        var messageResponse = await SendSmsMessageMainClass.SendSmsMessage(_region, _appId, _destinationNumber,
            _originationNumber, _registeredKeyword, _senderId, MessageType.TRANSACTIONAL);
        Assert.NotNull(messageResponse);
        Assert.Single(messageResponse.MessageResponse.Result);
    }
}