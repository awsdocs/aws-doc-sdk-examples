// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.sesv2;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import software.amazon.awssdk.services.sesv2.SesV2Client;
import software.amazon.awssdk.services.sesv2.model.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class NewsletterWorkflowTest {
  private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
  private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
  private NewsletterWorkflow scenario;

  private AutoCloseable closeable;

  @Mock
  private SesV2Client sesClient;

  @Mock
  private NewsletterScanner scanner;

  @Before
  public void openMocks() {
    closeable = MockitoAnnotations.openMocks(this);
    scenario = new NewsletterWorkflow(sesClient, scanner);
    System.setOut(new PrintStream(outContent));
    System.setErr(new PrintStream(errContent));
    outContent.reset();
    errContent.reset();
  }

  @After
  public void releaseMocks() throws Exception {
    closeable.close();
  }

  // Prepare Application Tests
  @Test
  public void test_prepareApplication_success() throws IOException {
    // Mock the necessary AWS SDK calls and responses
    when(scanner.nextLine()).thenReturn("test@example.com");

    CreateEmailIdentityResponse emailIdentityResponse = CreateEmailIdentityResponse.builder().build();
    when(sesClient.createEmailIdentity(any(CreateEmailIdentityRequest.class))).thenReturn(emailIdentityResponse);

    CreateContactListResponse contactListResponse = CreateContactListResponse.builder().build();
    when(sesClient.createContactList(any(CreateContactListRequest.class))).thenReturn(contactListResponse);

    CreateEmailTemplateResponse createEmailTemplateResponse = CreateEmailTemplateResponse.builder().build();
    when(sesClient.createEmailTemplate(any(CreateEmailTemplateRequest.class))).thenReturn(createEmailTemplateResponse);

    scenario.prepareApplication();

    assertThat(outContent.toString(), containsString("Email identity created: test@example.com"));
    assertThat(outContent.toString(), containsString("Contact list created: weekly-coupons-newsletter"));
  }

  @Test
  public void test_prepareApplication_error_identityAlreadyExists() {
    // Mock the necessary AWS SDK calls and responses
    when(scanner.nextLine()).thenReturn("test@example.com");

    when(sesClient.createEmailIdentity(any(CreateEmailIdentityRequest.class))).thenThrow(AlreadyExistsException.class);

    CreateContactListResponse contactListResponse = CreateContactListResponse.builder().build();
    when(sesClient.createContactList(any(CreateContactListRequest.class))).thenReturn(contactListResponse);

    try {
      scenario.prepareApplication();
    } catch (Exception e) {
    }

    assertThat(outContent.toString(),
        containsString("Email identity already exists, skipping creation: test@example.com"));
    assertThat(outContent.toString(), containsString("Contact list created: weekly-coupons-newsletter"));
  }

  @Test
  public void test_prepareApplication_error_identityNotFound() {
    // Mock the necessary AWS SDK calls and responses
    when(scanner.nextLine()).thenReturn("test@example.com");

    when(sesClient.createEmailIdentity(any(CreateEmailIdentityRequest.class))).thenThrow(NotFoundException.class);

    try {
      scenario.prepareApplication();
    } catch (Exception e) {
    }

    assertThat(errContent.toString(), containsString("The provided email address is not verified: test@example.com"));
  }

  @Test
  public void test_prepareApplication_error_identityLimitExceeded() {
    // Mock the necessary AWS SDK calls and responses
    when(scanner.nextLine()).thenReturn("test@example.com");

    when(sesClient.createEmailIdentity(any(CreateEmailIdentityRequest.class))).thenThrow(LimitExceededException.class);

    try {
      scenario.prepareApplication();
    } catch (Exception e) {
    }

    assertThat(errContent.toString(), containsString(
        "You have reached the limit for email identities. Please remove some identities and try again."));
  }

  @Test
  public void test_prepareApplication_error_contactListLimitExceeded() {
    // Mock the necessary AWS SDK calls and responses
    when(scanner.nextLine()).thenReturn("test@example.com");

    CreateEmailIdentityResponse emailIdentityResponse = CreateEmailIdentityResponse.builder().build();
    when(sesClient.createEmailIdentity(any(CreateEmailIdentityRequest.class))).thenReturn(emailIdentityResponse);

    when(sesClient.createContactList(any(CreateContactListRequest.class))).thenThrow(LimitExceededException.class);

    try {
      scenario.prepareApplication();
    } catch (Exception e) {
    }

    assertThat(outContent.toString(), containsString("Email identity created: test@example.com"));
    assertThat(errContent.toString(), containsString("Limit for contact lists has been exceeded."));
  }

  @Test
  public void test_prepareApplication_error_templateAlreadyExists() {
    // Mock the necessary AWS SDK calls and responses
    when(scanner.nextLine()).thenReturn("test@example.com");

    CreateEmailIdentityResponse emailIdentityResponse = CreateEmailIdentityResponse.builder().build();
    when(sesClient.createEmailIdentity(any(CreateEmailIdentityRequest.class))).thenReturn(emailIdentityResponse);

    CreateContactListResponse contactListResponse = CreateContactListResponse.builder().build();
    when(sesClient.createContactList(any(CreateContactListRequest.class))).thenReturn(contactListResponse);

    when(sesClient.createEmailTemplate(any(CreateEmailTemplateRequest.class))).thenThrow(AlreadyExistsException.class);

    try {
      scenario.prepareApplication();
    } catch (Exception e) {
    }

    String output = outContent.toString();
    assertThat(output, containsString("Email template already exists, skipping creation..."));
  }

  @Test
  public void test_prepareApplication_error_templateLimitExceeded() {
    // Mock the necessary AWS SDK calls and responses
    when(scanner.nextLine()).thenReturn("test@example.com");

    CreateEmailIdentityResponse emailIdentityResponse = CreateEmailIdentityResponse.builder().build();
    when(sesClient.createEmailIdentity(any(CreateEmailIdentityRequest.class))).thenReturn(emailIdentityResponse);

    CreateContactListResponse contactListResponse = CreateContactListResponse.builder().build();
    when(sesClient.createContactList(any(CreateContactListRequest.class))).thenReturn(contactListResponse);

    when(sesClient.createEmailTemplate(any(CreateEmailTemplateRequest.class))).thenThrow(LimitExceededException.class);

    try {
      scenario.prepareApplication();
    } catch (Exception e) {
    }

    String errorOutput = errContent.toString();
    assertThat(errorOutput,
        containsString("You have reached the limit for email templates. Please remove some templates and try again."));
  }

  // Gather Subscriber Emails Tests

  @Test
  public void test_gatherSubscriberEmails_success() throws IOException {
    // Mock the necessary AWS SDK calls and responses
    when(scanner.nextLine()).thenReturn("user@example.com");

    CreateContactResponse contactResponse = CreateContactResponse.builder().build();
    when(sesClient.createContact(any(CreateContactRequest.class))).thenReturn(contactResponse);

    SendEmailResponse welcomeEmailResponse = SendEmailResponse.builder().messageId("message-id").build();
    when(sesClient.sendEmail(any(SendEmailRequest.class))).thenReturn(welcomeEmailResponse);

    scenario.gatherSubscriberEmails();

    String output = outContent.toString();
    for (int i = 1; i <= 3; i++) {
      String expectedEmail = "user+ses-weekly-newsletter-" + i + "@example.com";
      assertThat(output, containsString("Contact created: " + expectedEmail));
      assertThat(output, containsString("Welcome email sent: message-id"));
    }
  }

  @Test
  public void test_gatherSubscriberEmails_error_contactAlreadyExists() {
    // Mock the necessary AWS SDK calls and responses
    when(scanner.nextLine()).thenReturn("user@example.com");

    when(sesClient.createContact(any(CreateContactRequest.class))).thenThrow(
        AlreadyExistsException.class);

    when(sesClient.createContact(
        eq(CreateContactRequest.builder().contactListName(NewsletterWorkflow.CONTACT_LIST_NAME)
            .emailAddress("user+ses-weekly-newsletter-2@example.com").build())))
        .thenReturn(
            CreateContactResponse.builder().build());

    SendEmailResponse welcomeEmailResponse = SendEmailResponse.builder().messageId("message-id").build();
    when(sesClient.sendEmail(any(SendEmailRequest.class))).thenReturn(
        welcomeEmailResponse);

    try {
      scenario.gatherSubscriberEmails();
    } catch (Exception e) {
    }

    String output = outContent.toString();
    assertThat(output, containsString("Contact already exists, skipping creation..."));
    assertThat(output, containsString("Contact already exists, skipping creation..."));
    assertThat(output, containsString("Welcome email sent: message-id"));
  }

  @Test
  public void test_gatherSubscriberEmails_error_sendEmailFailed() {
    // Mock the necessary AWS SDK calls and responses
    String baseEmail = "user@example.com";
    when(scanner.nextLine()).thenReturn(baseEmail);

    CreateContactResponse contactResponse = CreateContactResponse.builder().build();
    when(sesClient.createContact(any(CreateContactRequest.class))).thenReturn(
        contactResponse);

    when(sesClient.sendEmail(any(SendEmailRequest.class))).thenThrow(
        SesV2Exception.class);

    try {
      scenario.gatherSubscriberEmails();
    } catch (Exception e) {
    }

    String errorOutput = errContent.toString();
    String expectedEmail = "user+ses-weekly-newsletter-1@example.com";
    assertThat(errorOutput, containsString("Error occurred while processing email address " + expectedEmail));
  }

  // Send Coupon Newsletter Tests

  @Test
  public void test_sendCouponNewsletter_success() {
    ListContactsResponse contactListResponse = ListContactsResponse.builder()
        .contacts(
            Contact.builder().emailAddress("user+ses-weekly-newsletter-1@example.com").build(),
            Contact.builder().emailAddress("user+ses-weekly-newsletter-2@example.com").build(),
            Contact.builder().emailAddress("user+ses-weekly-newsletter-3@example.com").build())
        .build();
    when(sesClient.listContacts(any(ListContactsRequest.class))).thenReturn(
        contactListResponse);

    SendEmailResponse newsletterResponse = SendEmailResponse.builder().messageId("message-id").build();
    when(sesClient.sendEmail(any(SendEmailRequest.class))).thenReturn(
        newsletterResponse);

    scenario.sendCouponNewsletter();

    String output = outContent.toString();
    for (int i = 1; i <= 3; i++) {

      assertThat(output,
          containsString("Newsletter sent to user+ses-weekly-newsletter-" + i + "@example.com: message-id"));
    }
  }

  @Test
  public void test_sendCouponNewsletter_error_contactListNotFound() {
    // Mock the necessary AWS SDK calls and responses
    CreateEmailTemplateResponse templateResponse = CreateEmailTemplateResponse.builder().build();
    when(sesClient.createEmailTemplate(any(CreateEmailTemplateRequest.class))).thenReturn(templateResponse);

    when(sesClient.listContacts(any(ListContactsRequest.class))).thenThrow(
        NotFoundException.class);

    try {
      scenario.sendCouponNewsletter();
    } catch (Exception e) {
    }

    String errorOutput = errContent.toString();
    assertThat(errorOutput,
        containsString("The contact list is missing. Please create the contact list and try again."));
  }

  @Test
  public void test_sendCouponNewsletter_error_accountSuspended() {
    // Mock the necessary AWS SDK calls and responses
    CreateEmailTemplateResponse templateResponse = CreateEmailTemplateResponse.builder().build();
    when(sesClient.createEmailTemplate(any(CreateEmailTemplateRequest.class))).thenReturn(templateResponse);

    ListContactsResponse contactListResponse = ListContactsResponse.builder()
        .contacts(Contact.builder().emailAddress("user@example.com").build())
        .build();
    when(sesClient.listContacts(any(ListContactsRequest.class))).thenReturn(
        contactListResponse);

    when(sesClient.sendEmail(any(SendEmailRequest.class))).thenThrow(
        AccountSuspendedException.class);

    try {
      scenario.sendCouponNewsletter();
    } catch (Exception e) {
    }

    String errorOutput = errContent.toString();
    assertThat(errorOutput, containsString("Your account is suspended. Please resolve the issue and try again."));
  }

  @Test
  public void test_sendCouponNewsletter_error_domainNotVerified() {
    // Mock the necessary AWS SDK calls and responses
    CreateEmailTemplateResponse templateResponse = CreateEmailTemplateResponse.builder().build();
    when(sesClient.createEmailTemplate(any(CreateEmailTemplateRequest.class))).thenReturn(templateResponse);

    ListContactsResponse contactListResponse = ListContactsResponse.builder()
        .contacts(Contact.builder().emailAddress("user@example.com").build())
        .build();
    when(sesClient.listContacts(any(ListContactsRequest.class))).thenReturn(
        contactListResponse);

    when(sesClient.sendEmail(any(SendEmailRequest.class))).thenThrow(
        MailFromDomainNotVerifiedException.class);

    try {
      scenario.sendCouponNewsletter();
    } catch (Exception e) {
    }

    String errorOutput = errContent.toString();
    assertThat(errorOutput,
        containsString("The sending domain is not verified. Please verify your domain and try again."));
  }

  @Test
  public void test_sendCouponNewsletter_error_messageRejected() {
    // Mock the necessary AWS SDK calls and responses
    CreateEmailTemplateResponse templateResponse = CreateEmailTemplateResponse.builder().build();
    when(sesClient.createEmailTemplate(any(CreateEmailTemplateRequest.class))).thenReturn(templateResponse);

    ListContactsResponse contactListResponse = ListContactsResponse.builder()
        .contacts(Contact.builder().emailAddress("user@example.com").build())
        .build();
    when(sesClient.listContacts(any(ListContactsRequest.class))).thenReturn(
        contactListResponse);

    when(sesClient.sendEmail(any(SendEmailRequest.class))).thenThrow(
        MessageRejectedException.class);

    try {
      scenario.sendCouponNewsletter();
    } catch (Exception e) {
    }

    String errorOutput = errContent.toString();
    assertThat(errorOutput,
        containsString("The message content is invalid. Please check your template and try again."));
  }

  @Test
  public void test_sendCouponNewsletter_error_sendingPaused() {
    // Mock the necessary AWS SDK calls and responses
    CreateEmailTemplateResponse templateResponse = CreateEmailTemplateResponse.builder().build();
    when(sesClient.createEmailTemplate(any(CreateEmailTemplateRequest.class))).thenReturn(templateResponse);

    ListContactsResponse contactListResponse = ListContactsResponse.builder()
        .contacts(Contact.builder().emailAddress("user@example.com").build())
        .build();
    when(sesClient.listContacts(any(ListContactsRequest.class))).thenReturn(
        contactListResponse);

    when(sesClient.sendEmail(any(SendEmailRequest.class))).thenThrow(
        SendingPausedException.class);

    try {
      scenario.sendCouponNewsletter();
    } catch (Exception e) {
    }

    String errorOutput = errContent.toString();
    assertThat(
        errorOutput,
        containsString("Sending is currently paused for your account. Please resolve the issue and try again."));
  }

  // Clean Up Tests

  @Test
  public void test_cleanUp_success() {
    // Mock the necessary AWS SDK calls and responses
    scenario.test_setVerifiedEmail("test@example.com");
    when(scanner.nextLine()).thenReturn("y");

    DeleteContactListResponse deleteContactListResponse = DeleteContactListResponse.builder().build();
    when(sesClient.deleteContactList(any(DeleteContactListRequest.class))).thenReturn(deleteContactListResponse);

    DeleteEmailTemplateResponse deleteTemplateResponse = DeleteEmailTemplateResponse.builder().build();
    when(sesClient.deleteEmailTemplate(any(DeleteEmailTemplateRequest.class))).thenReturn(deleteTemplateResponse);

    DeleteEmailIdentityResponse deleteIdentityResponse = DeleteEmailIdentityResponse.builder().build();
    when(sesClient.deleteEmailIdentity(any(DeleteEmailIdentityRequest.class))).thenReturn(deleteIdentityResponse);

    scenario.cleanUp();

    String output = outContent.toString();
    assertThat(output, containsString("Contact list deleted: weekly-coupons-newsletter"));
    assertThat(output, containsString("Email template deleted: weekly-coupons"));
    assertThat(output, containsString("Email identity deleted: test@example.com"));
  }

  @Test
  public void test_cleanUp_error_contactListNotFound() {
    // Mock the necessary AWS SDK calls and responses
    scenario.test_setVerifiedEmail("test@example.com");
    when(sesClient.deleteContactList(any(DeleteContactListRequest.class))).thenThrow(NotFoundException.class);

    DeleteEmailTemplateResponse deleteTemplateResponse = DeleteEmailTemplateResponse.builder().build();
    when(sesClient.deleteEmailTemplate(any(DeleteEmailTemplateRequest.class))).thenReturn(deleteTemplateResponse);

    DeleteEmailIdentityResponse deleteIdentityResponse = DeleteEmailIdentityResponse.builder().build();
    when(sesClient.deleteEmailIdentity(any(DeleteEmailIdentityRequest.class))).thenReturn(deleteIdentityResponse);

    when(scanner.nextLine()).thenReturn("y");

    try {
      scenario.cleanUp();
    } catch (Exception e) {
    }

    String output = outContent.toString();
    assertThat(output, containsString("Contact list not found. Skipping deletion..."));
    assertThat(output, containsString("Email template deleted: weekly-coupons"));
    assertThat(output, containsString("Email identity deleted: test@example.com"));
  }

  @Test
  public void test_cleanUp_error_templateNotFound() {
    // Mock the necessary AWS SDK calls and responses
    scenario.test_setVerifiedEmail("test@example.com");
    DeleteContactListResponse deleteContactListResponse = DeleteContactListResponse.builder().build();
    when(sesClient.deleteContactList(any(DeleteContactListRequest.class))).thenReturn(deleteContactListResponse);

    when(sesClient.deleteEmailTemplate(any(DeleteEmailTemplateRequest.class))).thenThrow(NotFoundException.class);

    DeleteEmailIdentityResponse deleteIdentityResponse = DeleteEmailIdentityResponse.builder().build();
    when(sesClient.deleteEmailIdentity(any(DeleteEmailIdentityRequest.class))).thenReturn(deleteIdentityResponse);

    when(scanner.nextLine()).thenReturn("y");

    try {
      scenario.cleanUp();
    } catch (Exception e) {
    }

    String output = outContent.toString();
    assertThat(output, containsString("Contact list deleted: weekly-coupons-newsletter"));
    assertThat(output, containsString("Email template not found. Skipping deletion..."));
    assertThat(output, containsString("Email identity deleted: test@example.com"));
  }

  @Test
  public void test_cleanUp_error_identityNotFound() {
    // Mock the necessary AWS SDK calls and responses
    scenario.test_setVerifiedEmail("test@example.com");
    DeleteContactListResponse deleteContactListResponse = DeleteContactListResponse.builder().build();
    when(sesClient.deleteContactList(any(DeleteContactListRequest.class))).thenReturn(deleteContactListResponse);

    DeleteEmailTemplateResponse deleteTemplateResponse = DeleteEmailTemplateResponse.builder().build();
    when(sesClient.deleteEmailTemplate(any(DeleteEmailTemplateRequest.class))).thenReturn(deleteTemplateResponse);

    when(sesClient.deleteEmailIdentity(any(DeleteEmailIdentityRequest.class))).thenThrow(NotFoundException.class);

    when(scanner.nextLine()).thenReturn("y");

    try {
      scenario.cleanUp();
    } catch (Exception e) {
    }

    String output = outContent.toString();
    assertThat(output, containsString("Contact list deleted: weekly-coupons-newsletter"));
    assertThat(output, containsString("Email template deleted: weekly-coupons"));
    assertThat(output, containsString("Email identity not found. Skipping deletion..."));
  }

  @Test
  public void test_cleanUp_skipIdentityDeletion() {
    // Mock the necessary AWS SDK calls and responses
    scenario.test_setVerifiedEmail("test@example.com");
    DeleteContactListResponse deleteContactListResponse = DeleteContactListResponse.builder().build();
    when(sesClient.deleteContactList(any(DeleteContactListRequest.class))).thenReturn(deleteContactListResponse);

    DeleteEmailTemplateResponse deleteTemplateResponse = DeleteEmailTemplateResponse.builder().build();
    when(sesClient.deleteEmailTemplate(any(DeleteEmailTemplateRequest.class))).thenReturn(deleteTemplateResponse);

    when(scanner.nextLine()).thenReturn("n");

    try {
      scenario.cleanUp();
    } catch (Exception e) {
    }

    String output = outContent.toString();
    assertThat(output, containsString("Contact list deleted: weekly-coupons-newsletter"));
    assertThat(output, containsString("Email template deleted: weekly-coupons"));
    assertThat(output, containsString("Skipping email identity deletion."));
  }
}
