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
  ## Prepare the Application
---

For the "Prepare the Application" step, the following SES v2 API calls and parameters should be used:

1. **CreateEmailIdentity**

   Parameters:

   - `EmailIdentity`: Value of the `verified email` given by the user.

2. **CreateContactList**

   Parameters:

   - `ContactListName`: `weekly-coupons-newsletter`

3. Create an email template named `weekly-coupons` with the following content:

   - API Call: `CreateEmailTemplate`
   - Parameters:
     - `TemplateName`: `weekly-coupons`
     - `TemplateContent`:
       - `Subject`: `Weekly Coupons Newsletter`
       - `Html`: Read from the `coupon-newsletter.html` file
       - `Text`: Read from the `coupon-newsletter.txt` file
