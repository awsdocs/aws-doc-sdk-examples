---
combined: true
debug:
  engine: bedrock
  finish: end_turn
  id: null
  model: anthropic.claude-3-sonnet-20240229-v1:0
  usage: null
isolated: true
prompt: >
  Describe the exact SESv2 API calls and parameters for this step:

  ## Send the Coupon Newsletter

  The sample JSON should come from a file, `sample_coupons.json`.
---

## Send the Coupon Newsletter

1. Retrieve the list of contacts from the `weekly-coupons-newsletter` contact list.

   - API Call: `ListContacts`
   - Parameters:
     - `ContactListName`: `weekly-coupons-newsletter`

2. Send an email using the `weekly-coupons` template to each contact in the list.

   - API Call: `SendEmail`
   - Parameters:
     - `Destination`:
       - `ToAddresses`: (List of email addresses from the `ListContacts` response)
     - `Content`:
       - `Template`:
         - `TemplateName`: `weekly-coupons`
         - `TemplateData`: JSON string representing an object with one key, `coupons`, which is an array of coupon items. Each coupon entry in the array should have one key, `details`, with the details of the coupon. See `sample_coupons.json`.
     - `FromEmailAddress`: (Use the verified email address)

For more information on using templates with SES v2, refer to the official documentation: https://docs.aws.amazon.com/ses/latest/dg/send-personalized-email-api.html
