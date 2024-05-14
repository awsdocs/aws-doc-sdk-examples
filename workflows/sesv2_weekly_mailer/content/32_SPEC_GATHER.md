---
combined: true
debug:
  engine: bedrock
  finish: end_turn
  id: null
  model: anthropic.claude-3-sonnet-20240229-v1:0
  usage: null
isolated: true
prompt: |
  Describe the exact SESv2 API calls and parameters for this step.

  ## Gather Subscriber Email Addresses
---

For the "Gather Subscriber Email Addresses" step, the following SES v2 API calls and parameters should be used:

1. **CreateContact**

   - `ContactListName`: `weekly-coupons-newsletter`
   - `EmailAddress`: The email address provided by the user.

2. **SendEmail**
   - `FromEmailAddress`: Use the `verified_email` address provided in Prepare the Application.
   - `Destination.ToAddresses`: The email address provided by the user.
   - `Content.Simple.Subject.Data`: "Welcome to the Weekly Coupons Newsletter"
   - `Content.Simple.Body.Html.Data`: Read the content from the `welcome.html` file.

For each email address provided by the user:

1. Use the `CreateContact` call to create a new contact in the `weekly-coupons-newsletter` contact list with the provided email address.

2. Use the `SendEmail` call to send a welcome email to the new contact. The `FromEmailAddress` should be the verified email address provided by the user when preparing the application. The `Destination.ToAddresses` should be set to the email address provided by the user. The email subject should be "Welcome to the Weekly Coupons Newsletter", and the HTML content of the email should be read from the `welcome.html` file.

Repeat these two steps for each email address provided by the user.
