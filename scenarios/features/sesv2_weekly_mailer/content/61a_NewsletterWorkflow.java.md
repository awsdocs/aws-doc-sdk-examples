---
skip: true
prompt: Generate the java implementation of this workflow.
---

<file name="NewsletterWorkflow.java">

```java
package com.example.sesv2;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sesv2.SesV2Client;
import software.amazon.awssdk.services.sesv2.model.*;

/**
 * This class implements the SES v2 Coupon Newsletter Workflow.
 * It demonstrates how to use the Amazon Simple Email Service (SES) v2 to send a
 * coupon newsletter to a list of contacts.
 */
public class NewsletterWorkflow {
  private static final String CONTACT_LIST_NAME = "weekly-coupons-newsletter";
  private static final String TEMPLATE_NAME = "weekly-coupons";
  private static final String INTRO = """
      Welcome to the Amazon SES v2 Coupon Newsletter Workflow!

      This workflow will help you:
      1. Prepare a verified email identity and contact list for your newsletter.
      2. Gather subscriber email addresses and send them a welcome email.
      3. Send a weekly coupon newsletter to your subscribers using email templates.
      4. Monitor your sending activity and metrics in the AWS console.

      Let's get started!
      """;
  private final SesV2Client sesClient;
  private String verifiedEmail = "";

  public void test_setVerifiedEmail(String verifiedEmail) {
    this.verifiedEmail = verifiedEmail;
  }

  /**
   * Constructor for the Workflow class.
   *
   * @param sesClient The SesV2Client instance to be used for interacting with the
   *                  SES v2 service.
   */
  public NewsletterWorkflow(SesV2Client sesClient) {
    this.sesClient = sesClient;
  }

  /**
   * The main entry point of the application.
   *
   * @param args Command-line arguments.
   */
  public static void main(String[] args) {
    System.out.println(INTRO);
    SesV2Client sesClient = SesV2Client.builder()
        .region(Region.AWS_GLOBAL)
        .build();

    new NewsletterWorkflow(sesClient).run();
  }

  /**
   * Orchestrates the execution of the workflow steps.
   */
  public void run() {
    try {
      prepareApplication();
      gatherSubscriberEmails();
      sendCouponNewsletter();
      monitorAndReview();
    } catch (Exception e) {
    }
    cleanUp();
  }

  /**
   * Prepares the application by creating an email identity and a contact list.
   */
  public void prepareApplication() throws IOException {
    try {
      // 1. Create an email identity
      System.out.println("Enter the verified email address: ");
      Scanner scanner = new Scanner(System.in);
      this.verifiedEmail = scanner.nextLine();
      scanner.close();
      CreateEmailIdentityRequest createEmailIdentityRequest = CreateEmailIdentityRequest.builder()
          .emailIdentity(verifiedEmail)
          .build();
      sesClient.createEmailIdentity(createEmailIdentityRequest);
      System.out.println("Email identity created: " + verifiedEmail);
    } catch (AlreadyExistsException e) {
      System.out.println("Email identity already exists, skipping creation: " + verifiedEmail);
    } catch (NotFoundException e) {
      System.err.println("The provided email address is not verified: " + verifiedEmail);
      throw e;
    } catch (LimitExceededException e) {
      System.err
          .println("You have reached the limit for email identities. Please remove some identities and try again.");
      throw e;
    } catch (SesV2Exception e) {
      System.err.println("Error creating email identity: " + e.getMessage());
      throw e;
    }

    try {
      // 2. Create a contact list
      String contactListName = CONTACT_LIST_NAME;
      CreateContactListRequest createContactListRequest = CreateContactListRequest.builder()
          .contactListName(contactListName)
          .build();
      sesClient.createContactList(createContactListRequest);
      System.out.println("Contact list created: " + contactListName);
    } catch (AlreadyExistsException e) {
      System.out.println("Contact list already exists, skipping creation: weekly-coupons-newsletter");
    } catch (LimitExceededException e) {
      System.err.println("Limit for contact lists has been exceeded.");
      throw e;
    } catch (SesV2Exception e) {
      System.err.println("Error creating contact list: " + e.getMessage());
      throw e;
    }
    try {
      // Create an email template named "weekly-coupons"
      String newsletterHtml = Files.readString(Paths.get("resources/coupon_newsletter/coupon-newsletter.html"));
      String newsletterText = Files.readString(Paths.get("resources/coupon_newsletter/coupon-newsletter.txt"));

      CreateEmailTemplateRequest templateRequest = CreateEmailTemplateRequest.builder()
          .templateName(TEMPLATE_NAME)
          .templateContent(EmailTemplateContent.builder()
              .subject("Weekly Coupons Newsletter")
              .html(newsletterHtml)
              .text(newsletterText)
              .build())
          .build();

      sesClient.createEmailTemplate(templateRequest);

      System.out.println("Email template created: " + TEMPLATE_NAME);
    } catch (AlreadyExistsException e) {
      // If the template already exists, skip this step and proceed with the next
      // operation
      System.out.println("Email template already exists, skipping creation...");
    } catch (LimitExceededException e) {
      // If the limit for email templates is exceeded, fail the workflow and inform
      // the user
      System.err.println("You have reached the limit for email templates. Please remove some templates and try again.");
      throw e;
    } catch (Exception e) {
      System.err.println("Error occurred while creating email template: " + e.getMessage());
      throw e;
    }
  }

  /**
   * Helper method to create subscriber subaddresses.
   *
   * @param baseEmail The base email address (e.g., "user@example.com")
   * @return A list of three email addresses with subaddress extensions
   */
  private List<String> createSubscriberSubaddresses(String baseEmail) {
    List<String> subaddresses = new ArrayList<>();
    String[] parts = baseEmail.split("@");
    String username = parts[0];
    String domain = parts[1];

    for (int i = 1; i <= 3; i++) {
      String subaddress = username + "+ses-weekly-newsletter-" + i + "@" + domain;
      subaddresses.add(subaddress);
    }

    return subaddresses;
  }

  /**
   * Gathers subscriber email addresses and sends a welcome email to each new
   * subscriber.
   */
  public void gatherSubscriberEmails() throws IOException {
    Scanner scanner = new Scanner(System.in);
    System.out.print("Enter a base email address for subscribing to the newsletter: ");
    String baseEmail = scanner.nextLine();
    scanner.close();

    for (String emailAddress : createSubscriberSubaddresses(baseEmail)) {
      try {
        // Create a new contact with the provided email address in the
        // "weekly-coupons-newsletter" contact list
        CreateContactRequest contactRequest = CreateContactRequest.builder()
            .contactListName(CONTACT_LIST_NAME)
            .emailAddress(emailAddress)
            .build();

        sesClient.createContact(contactRequest);

        System.out.println("Contact created: " + emailAddress);

        // Send a welcome email to the new contact
        String welcomeHtml = Files.readString(Paths.get("resources/coupon_newsletter/welcome.html"));
        String welcomeText = Files.readString(Paths.get("resources/coupon_newsletter/welcome.txt"));

        SendEmailRequest welcomeEmailRequest = SendEmailRequest.builder()
            .fromEmailAddress(this.verifiedEmail)
            .destination(Destination.builder().toAddresses(emailAddress).build())
            .content(EmailContent.builder()
                .simple(
                    Message.builder()
                        .subject(Content.builder().data("Welcome to the Weekly Coupons Newsletter").build())
                        .body(Body.builder()
                            .text(Content.builder().data(welcomeText).build())
                            .html(Content.builder().data(welcomeHtml).build())
                            .build())
                        .build())
                .build())
            .build();
        SendEmailResponse welcomeEmailResponse = sesClient.sendEmail(welcomeEmailRequest);
        System.out.println("Welcome email sent: " + welcomeEmailResponse.messageId());
      } catch (AlreadyExistsException e) {
        // If the contact already exists, skip this step for that contact and proceed
        // with the next contact
        System.out.println("Contact already exists, skipping creation...");
      } catch (Exception e) {
        System.err.println("Error occurred while processing email address " + emailAddress + ": " + e.getMessage());
        throw e;
      }
    }
  }

  /**
   * Sends the coupon newsletter to the list of contacts.
   */
  public void sendCouponNewsletter() {
    try {
      // Retrieve the list of contacts from the "weekly-coupons-newsletter" contact
      // list
      ListContactsRequest contactListRequest = ListContactsRequest.builder()
          .contactListName(CONTACT_LIST_NAME)
          .build();
      ListContactsResponse contactListResponse = sesClient.listContacts(contactListRequest);
      List<String> contactEmails = contactListResponse.contacts().stream()
          .map(Contact::emailAddress)
          .toList();

      // Send an email using the "weekly-coupons" template to each contact in the list
      String coupons = Files.readString(Paths.get("resources/coupon_newsletter/sample_coupons.json"));
      for (String emailAddress : contactEmails) {
        SendEmailRequest newsletterRequest = SendEmailRequest.builder()
            .destination(Destination.builder().toAddresses(emailAddress).build())
            .content(EmailContent.builder()
                .template(Template.builder()
                    .templateName(TEMPLATE_NAME)
                    .templateData(coupons)
                    .build())
                .build())
            .fromEmailAddress(this.verifiedEmail)
            .listManagementOptions(ListManagementOptions.builder()
                .contactListName(CONTACT_LIST_NAME)
                .build())
            .build();
        SendEmailResponse newsletterResponse = sesClient.sendEmail(newsletterRequest);
        System.out.println("Newsletter sent to " + emailAddress + ": " + newsletterResponse.messageId());
      }
    } catch (NotFoundException e) {
      // If the contact list does not exist, fail the workflow and inform the user
      System.err.println("The contact list is missing. Please create the contact list and try again.");
    } catch (AccountSuspendedException e) {
      // If the account is suspended, fail the workflow and inform the user
      System.err.println("Your account is suspended. Please resolve the issue and try again.");
    } catch (MailFromDomainNotVerifiedException e) {
      // If the sending domain is not verified, fail the workflow and inform the user
      System.err.println("The sending domain is not verified. Please verify your domain and try again.");
      throw e;
    } catch (MessageRejectedException e) {
      // If the message is rejected due to invalid content, fail the workflow and
      // inform the user
      System.err.println("The message content is invalid. Please check your template and try again.");
      throw e;
    } catch (SendingPausedException e) {
      // If sending is paused, fail the workflow and inform the user
      System.err.println("Sending is currently paused for your account. Please resolve the issue and try again.");
      throw e;
    } catch (Exception e) {
      System.err.println("Error occurred while sending the newsletter: " + e.getMessage());
      e.printStackTrace();
    }
  }

  /**
   * Monitors and reviews the newsletter campaign.
   */
  public void monitorAndReview() {
    System.out.println("\nMonitor your sending activity using the SES Homepage in the AWS console:\n"
        + "https://console.aws.amazon.com/ses/home#/account\n"
        + "For more detailed monitoring, refer to the SES Developer Guide:\n"
        + "https://docs.aws.amazon.com/ses/latest/dg/monitor-sending-activity.html");
    Scanner scanner = new Scanner(System.in);
    scanner.nextLine();
    scanner.close();
  }

  /**
   * Cleans up the resources created during the workflow.
   */
  public void cleanUp() {
    try {
      // Delete the contact list
      DeleteContactListRequest deleteContactListRequest = DeleteContactListRequest.builder()
          .contactListName(CONTACT_LIST_NAME)
          .build();

      sesClient.deleteContactList(deleteContactListRequest);

      System.out.println("Contact list deleted: " + CONTACT_LIST_NAME);
    } catch (NotFoundException e) {
      // If the contact list does not exist, log the error and proceed
      System.out.println("Contact list not found. Skipping deletion...");
    } catch (Exception e) {
      System.err.println("Error occurred while deleting the contact list: " + e.getMessage());
      e.printStackTrace();
    }

    try {
      // Delete the template
      DeleteEmailTemplateRequest deleteTemplateRequest = DeleteEmailTemplateRequest.builder()
          .templateName(TEMPLATE_NAME)
          .build();

      sesClient.deleteEmailTemplate(deleteTemplateRequest);

      System.out.println("Email template deleted: " + TEMPLATE_NAME);
    } catch (NotFoundException e) {
      // If the email template does not exist, log the error and proceed
      System.out.println("Email template not found. Skipping deletion...");
    } catch (Exception e) {
      System.err.println("Error occurred while deleting the email template: " + e.getMessage());
      e.printStackTrace();
    }

    System.out.println("\nDo you want to delete the email identity? (y/n)");
    Scanner scanner = new Scanner(System.in);
    String input = scanner.nextLine();
    scanner.close();

    if (input.equalsIgnoreCase("y")) {
      try {
        // Delete the email identity
        DeleteEmailIdentityRequest deleteIdentityRequest = DeleteEmailIdentityRequest.builder()
            .emailIdentity(this.verifiedEmail)
            .build();

        sesClient.deleteEmailIdentity(deleteIdentityRequest);

        System.out.println("Email identity deleted: " + this.verifiedEmail);
      } catch (NotFoundException e) {
        // If the email identity does not exist, log the error and proceed
        System.out.println("Email identity not found. Skipping deletion...");
      } catch (Exception e) {
        System.err.println("Error occurred while deleting the email identity: " + e.getMessage());
        e.printStackTrace();
      }
    } else {
      System.out.println("Skipping email identity deletion.");
    }
  }
}
```

</file>
