---
prompt: |
  Describe the exact SESv2 API calls and parameters for this step.
  ## Clean Up
---

## Clean up

1. Delete the contact list. This operation also deletes all contacts in the list, without needing separate calls.
   - Operation: **DeleteContactList**
     - `ContactListName`: `weekly-coupons-newsletter`
2. Delete the template.
   - Operation: **DeleteEmailTemplate**
     - `TemplateName`: `weekly-coupons`
3. Delete the email identity (optional). Ask the user before performing this step, as they may not want to re-verify the email identity.
   - Operation: **DeleteEmailIdentity**
     - `EmailIdentity`: Value of the `verified email` given by the user in part 1.
