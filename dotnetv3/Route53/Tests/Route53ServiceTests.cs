// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

using Amazon.Route53Domains;
using Amazon.Route53Domains.Model;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.Logging;
using Moq;
using Route53Actions;

namespace Route53Tests;

public class Route53ServiceTests
{
    private readonly IConfiguration _configuration;
    private readonly ILoggerFactory _loggerFactory;
    private readonly Route53Wrapper _route53Wrapper;
    private static string? _operationId;

    /// <summary>
    /// Constructor for the test class.
    /// </summary>
    public Route53ServiceTests()
    {
        _configuration = new ConfigurationBuilder()
            .SetBasePath(Directory.GetCurrentDirectory())
            .AddJsonFile("testsettings.json") // Load test settings from .json file.
            .AddJsonFile("testsettings.local.json",
                true) // Optionally load local settings.
            .Build();


        _loggerFactory = LoggerFactory.Create(builder =>
        {
            builder.AddConsole();
        });

        _route53Wrapper = new Route53Wrapper(
            new AmazonRoute53DomainsClient(),
            new Logger<Route53Wrapper>(_loggerFactory)
            );
    }

    /// <summary>
    /// List the domains for an account. Can be empty but should not be null.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact]
    [Order(1)]
    [Trait("Category", "Integration")]
    public async Task ListDomains_ShouldNotBeNull()
    {
        var result = await _route53Wrapper.ListDomains();

        Assert.NotNull(result);
    }

    /// <summary>
    /// List the billing records for an account. Can be empty but should not be null.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact]
    [Order(2)]
    [Trait("Category", "Integration")]
    public async Task ListBillingRecords_ShouldNotBeNull()
    {
        var result = await _route53Wrapper.ViewBilling(DateTime.Today.AddYears(-1),
            DateTime.Today);

        Assert.NotNull(result);
    }

    /// <summary>
    /// List domain prices for an account. Should include price records.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact]
    [Order(3)]
    [Trait("Category", "Integration")]
    public async Task ListPrices_ShouldHaveRecords()
    {
        var domainTypes = new List<string> { "net" };
        var prices = await _route53Wrapper.ListPrices(domainTypes);

        Assert.Contains(prices, price => price.Name.Equals("net"));
    }

    /// <summary>
    /// List domain suggestions for an account. Should include at least one suggestion.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact]
    [Order(3)]
    [Trait("Category", "Integration")]
    public async Task ListDomainSuggestions_ShouldReturnSuggestions()
    {
        var domainName = _configuration["DomainName"];
        var suggestions = await _route53Wrapper.GetDomainSuggestions(domainName, true);

        Assert.True(suggestions.Count > 0);
    }

    /// <summary>
    /// Check availability for a domain. Should return an availability string.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact]
    [Order(4)]
    [Trait("Category", "Integration")]
    public async Task CheckDomainAvailability_ShouldHaveRecords()
    {
        var domainName = _configuration["DomainName"];

        var response = await _route53Wrapper.CheckDomainAvailability(domainName);
        var availability = DomainAvailability.FindValue(response);
        Assert.NotNull(availability.Value);
    }

    /// <summary>
    /// Check transferability for a domain. Should return an transferability string.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact]
    [Order(5)]
    [Trait("Category", "Integration")]
    public async Task CheckDomainTransferability_ShouldHaveRecords()
    {
        var domainName = _configuration["DomainName"];

        var response = await _route53Wrapper.CheckDomainTransferability(domainName);
        var transferable = Transferable.FindValue(response);
        Assert.NotNull(transferable.Value);
    }

    /// <summary>
    /// Request domain registration. Should return an operation ID.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact]
    [Order(6)]
    [Trait("Category", "Integration")]
    public async Task RequestDomainRegistration_ShouldReturnOperationId()
    {
        string domainName = _configuration["DomainName"];
        ContactDetail contact = new ContactDetail();
        contact.CountryCode = CountryCode.FindValue(_configuration["Contact:CountryCode"]);
        contact.ContactType = ContactType.FindValue(_configuration["Contact:ContactType"]);

        _configuration.GetSection("Contact").Bind(contact);

        _operationId = await _route53Wrapper.RegisterDomain(
            domainName,
            Convert.ToBoolean(_configuration["AutoRenew"]),
            Convert.ToInt32(_configuration["DurationInYears"]),
            contact);

        Assert.NotNull(_operationId);
    }

    /// <summary>
    /// Request domain registration. Should not return an operation ID.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact]
    [Order(7)]
    [Trait("Category", "Integration")]
    public async Task RequestDomainRegistration_ShouldNotReturnOperationId()
    {
        string domainName = _configuration["DomainName"];
        ContactDetail contact = new ContactDetail();

        var stringResult = await _route53Wrapper.RegisterDomain(
                domainName,
                Convert.ToBoolean(_configuration["AutoRenew"]),
                Convert.ToInt32(_configuration["DurationInYears"]),
                contact);
        Assert.Null(stringResult);
    }

    /// <summary>
    /// Get details for an operation. Should return operation details.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact]
    [Order(8)]
    [Trait("Category", "Integration")]
    public async Task GetOperationalDetail_ShouldHaveDetails()
    {
        var operationDetails = await _route53Wrapper.GetOperationDetail(_operationId);

        Assert.Contains($"Operation {_operationId}:", operationDetails);
    }

    /// <summary>
    /// List the operations for an account. Should contain at least one operation.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact]
    [Order(9)]
    [Trait("Category", "Integration")]
    public async Task ListOperations_ShouldNotBeNull()
    {
        var operations = await _route53Wrapper.ListOperations(DateTime.Today);

        Assert.Contains(operations, summary => summary.OperationId == _operationId);
    }

    /// <summary>
    /// Get details for a domain. Should return domain details.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact]
    [Order(8)]
    [Trait("Category", "Unit")]
    public async Task GetDomainDetail_ShouldHaveDetails()
    {
        var mockRoute53Service = new Mock<IAmazonRoute53Domains>();
        string domainName = _configuration["DomainName"];
        ContactDetail contact = new ContactDetail();
        contact.CountryCode = CountryCode.FindValue(_configuration["Contact:CountryCode"]);
        contact.ContactType = ContactType.FindValue(_configuration["Contact:ContactType"]);

        _configuration.GetSection("Contact").Bind(contact);
        var response = new GetDomainDetailResponse()
        {
            DomainName = domainName,
            CreationDate = DateTime.Today.Date,
            AdminContact = contact,
            AutoRenew = Convert.ToBoolean(_configuration["AutoRenew"])
        };

        mockRoute53Service.Setup(ds =>
            ds.GetDomainDetailAsync(
                It.IsAny<GetDomainDetailRequest>(),
                CancellationToken.None).Result).Returns(response);

        var wrapper = new Route53Wrapper(mockRoute53Service.Object, new Logger<Route53Wrapper>(_loggerFactory));
        var domainDetails = await wrapper.GetDomainDetail(domainName);

        var detailsString = $"\tDomain {domainName}:\n" +
                      $"\tCreated on {DateTime.Today.Date.ToShortDateString()}.\n" +
                      $"\tAdmin contact is {contact.Email}.\n" +
                      $"\tAuto-renew is {_configuration["AutoRenew"]}.\n";

        Assert.Equal(detailsString, domainDetails);
    }
}