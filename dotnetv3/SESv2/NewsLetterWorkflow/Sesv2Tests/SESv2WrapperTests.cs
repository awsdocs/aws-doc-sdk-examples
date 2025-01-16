// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

using System.Net;
using Amazon.SimpleEmailV2;
using Amazon.SimpleEmailV2.Model;
using Moq;
using Sesv2Scenario;
using Xunit.Extensions.Ordering;

namespace Sesv2Tests;

/// <summary>
/// Tests for the SESv2 Scenario.
/// </summary>
public class SESv2WrapperTests
{
    /// <summary>
    /// Run the preparation step of the scenario. Should return successful.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact]
    [Order(1)]
    [Trait("Category", "Unit")]
    public async Task TestPrepareApplication()
    {
        // Arrange.
        var mockSesV2Service = new Mock<IAmazonSimpleEmailServiceV2>();

        mockSesV2Service.Setup(client => client.CreateEmailIdentityAsync(
                It.IsAny<CreateEmailIdentityRequest>(),
                It.IsAny<CancellationToken>()))
            .Returns((CreateEmailIdentityResponse r,
                CancellationToken token) =>
            {
                return Task.FromResult(new CreateEmailIdentityResponse()
                {
                    IdentityType = IdentityType.EMAIL_ADDRESS,
                    HttpStatusCode = HttpStatusCode.OK,
                });
            });

        mockSesV2Service.Setup(client => client.CreateContactListAsync(
                It.IsAny<CreateContactListRequest>(),
                It.IsAny<CancellationToken>()))
            .Returns((CreateEmailIdentityResponse r,
                CancellationToken token) =>
            {
                return Task.FromResult(new CreateContactListResponse()
                {
                    HttpStatusCode = HttpStatusCode.OK,
                });
            });

        mockSesV2Service.Setup(client => client.CreateEmailTemplateAsync(
                It.IsAny<CreateEmailTemplateRequest>(),
                It.IsAny<CancellationToken>()))
            .Returns((CreateEmailTemplateResponse r,
                CancellationToken token) =>
            {
                return Task.FromResult(new CreateEmailTemplateResponse()
                {
                    HttpStatusCode = HttpStatusCode.OK,
                });
            });

        var sESv2Wrapper = new SESv2Wrapper(mockSesV2Service.Object);

        NewsletterWorkflow._sesv2Wrapper = sESv2Wrapper;
        NewsletterWorkflow._verifiedEmail = "test@example.com";

        // Act.
        var verifiedEmail = await NewsletterWorkflow.PrepareApplication();

        // Assert.
        Assert.Equal(NewsletterWorkflow._verifiedEmail, verifiedEmail);
    }

    [Fact]
    [Order(2)]
    [Trait("Category", "Unit")]
    public async Task TestGatherSubscriberEmailAddresses()
    {
        // Arrange
        var mockSesV2Service = new Mock<IAmazonSimpleEmailServiceV2>();

        mockSesV2Service.Setup(client => client.CreateContactAsync(
                It.IsAny<CreateContactRequest>(),
                It.IsAny<CancellationToken>()))
            .Returns((CreateContactRequest request, CancellationToken token) =>
            {
                return Task.FromResult(new CreateContactResponse()
                {
                    HttpStatusCode = HttpStatusCode.OK
                });
            });

        mockSesV2Service.Setup(client => client.SendEmailAsync(
                It.IsAny<SendEmailRequest>(),
                It.IsAny<CancellationToken>()))
            .Returns((SendEmailRequest request, CancellationToken token) =>
            {
                return Task.FromResult(new SendEmailResponse()
                {
                    HttpStatusCode = HttpStatusCode.OK
                });
            });

        var sESv2Wrapper = new SESv2Wrapper(mockSesV2Service.Object);

        NewsletterWorkflow._sesv2Wrapper = sESv2Wrapper;
        NewsletterWorkflow._verifiedEmail = "test@example.com";
        NewsletterWorkflow._baseEmailAddress = "test@example.com";

        // Act
        var result =
            await NewsletterWorkflow.GatherSubscriberEmailAddresses(NewsletterWorkflow
                ._verifiedEmail);

        // Assert
        Assert.True(result);
    }

    [Fact]
    [Order(3)]
    [Trait("Category", "Unit")]
    public async Task TestSendCouponNewsletter()
    {
        // Arrange
        var mockSesV2Service = new Mock<IAmazonSimpleEmailServiceV2>();

        mockSesV2Service.Setup(client => client.ListContactsAsync(
                It.IsAny<ListContactsRequest>(),
                It.IsAny<CancellationToken>()))
            .Returns((ListContactsRequest request, CancellationToken token) =>
            {
                return Task.FromResult(new ListContactsResponse
                {
                    Contacts = new List<Contact>
                    {
                        new Contact { EmailAddress = "test1@example.com" },
                        new Contact { EmailAddress = "test2@example.com" }
                    },
                    HttpStatusCode = HttpStatusCode.OK
                });
            });

        mockSesV2Service.Setup(client => client.SendEmailAsync(
                It.IsAny<SendEmailRequest>(),
                It.IsAny<CancellationToken>()))
            .Returns((SendEmailRequest request, CancellationToken token) =>
            {
                return Task.FromResult(new SendEmailResponse
                {
                    HttpStatusCode = HttpStatusCode.OK
                });
            });

        var sESv2Wrapper = new SESv2Wrapper(mockSesV2Service.Object);

        NewsletterWorkflow._sesv2Wrapper = sESv2Wrapper;
        NewsletterWorkflow._verifiedEmail = "test@example.com";

        // Act
        var result =
            await NewsletterWorkflow.SendCouponNewsletter(NewsletterWorkflow
                ._verifiedEmail);

        // Assert
        Assert.True(result);
    }

    [Fact]
    [Order(4)]
    [Trait("Category", "Unit")]
    public void TestMonitorAndReview()
    {
        // Arrange

        // Act
        var result = NewsletterWorkflow.MonitorAndReview(false);

        // Assert
        Assert.True(result);
    }

    [Fact]
    [Order(5)]
    [Trait("Category", "Unit")]
    public async Task TestCleanup()
    {
        // Arrange
        var mockSesV2Service = new Mock<IAmazonSimpleEmailServiceV2>();

        mockSesV2Service.Setup(client => client.DeleteContactListAsync(
                It.IsAny<DeleteContactListRequest>(),
                It.IsAny<CancellationToken>()))
            .Returns((DeleteContactListRequest request, CancellationToken token) =>
            {
                return Task.FromResult(new DeleteContactListResponse()
                {
                    HttpStatusCode = HttpStatusCode.OK
                });
            });

        mockSesV2Service.Setup(client => client.DeleteEmailTemplateAsync(
                It.IsAny<DeleteEmailTemplateRequest>(),
                It.IsAny<CancellationToken>()))
            .Returns((DeleteEmailTemplateRequest request, CancellationToken token) =>
            {
                return Task.FromResult(new DeleteEmailTemplateResponse()
                {
                    HttpStatusCode = HttpStatusCode.OK
                });
            });

        mockSesV2Service.Setup(client => client.DeleteEmailIdentityAsync(
                It.IsAny<DeleteEmailIdentityRequest>(),
                It.IsAny<CancellationToken>()))
            .Returns((DeleteEmailIdentityRequest request, CancellationToken token) =>
            {
                return Task.FromResult(new DeleteEmailIdentityResponse()
                {
                    HttpStatusCode = HttpStatusCode.OK
                });
            });

        var sESv2Wrapper = new SESv2Wrapper(mockSesV2Service.Object);

        NewsletterWorkflow._sesv2Wrapper = sESv2Wrapper;
        NewsletterWorkflow._verifiedEmail = "test@example.com";

        // Act
        var result =
            await NewsletterWorkflow.Cleanup(NewsletterWorkflow._verifiedEmail, false);

        // Assert
        Assert.True(result);
    }
}