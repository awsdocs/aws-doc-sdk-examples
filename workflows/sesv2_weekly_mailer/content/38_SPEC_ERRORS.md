---
combined: true
debug:
  engine: bedrock
  finish: end_turn
  id: null
  model: anthropic.claude-3-sonnet-20240229-v1:0
  usage: null
isolated: false
prompt: >
  For each operation across all spec calls, describe necessary API error
  handling - which errors should be handled and recovered from, and which should
  be passed to the end user?

  Do not consider common SDK exceptions, like TooManyRequestsException or
  BadRequestException.

  Format your output using the same section titles as the original spec. The
  answers will be copied into the original spec as given.
---

## Prepare the Application

**CreateEmailIdentity**

- `AlreadyExistsException`: If the identity already exists, skip this step and proceed with the next operation. This error can be safely ignored.
- `NotFoundException`: If the identity does not exist, fail the workflow and inform the user that the provided email address is not verified.
- `LimitExceededException`: If the limit for email identities is exceeded, fail the workflow and inform the user that they have reached the limit for email identities.

**CreateContactList**

- `AlreadyExistsException`: If the contact list already exists, skip this step and proceed with the next operation. This error can be safely ignored.
- `LimitExceededException`: If the limit for contact lists is exceeded, fail the workflow and inform the user that they have reached the limit for contact lists.

## Gather Subscriber Email Addresses

**CreateContact**

- `AlreadyExistsException`: If the contact already exists, skip this step for that contact and proceed with the next contact. This error can be safely ignored.

## Send the Coupon Newsletter

**CreateEmailTemplate**

- `AlreadyExistsException`: If the template already exists, skip this step and proceed with the next operation. This error can be safely ignored.
- `LimitExceededException`: If the limit for email templates is exceeded, fail the workflow and inform the user that they have reached the limit for email templates.

**ListContacts**

- `NotFoundException`: If the contact list does not exist, fail the workflow and inform the user that the contact list is missing.

**SendEmail**

- `AccountSuspendedException`: If the account is suspended, fail the workflow and inform the user that their account is suspended.
- `MailFromDomainNotVerifiedException`: If the sending domain is not verified, fail the workflow and inform the user that the sending domain is not verified.
- `MessageRejected`: If the message is rejected due to invalid content, fail the workflow and inform the user that the message content is invalid.
- `SendingPausedException`: If sending is paused, fail the workflow and inform the user that sending is currently paused for their account.

## Clean Up

**DeleteContactList**

- `NotFoundException`: If the contact list does not exist, skip this step and proceed with the next operation. This error can be safely ignored.

**DeleteEmailTemplate**

- `NotFoundException`: If the email template does not exist, skip this step and proceed with the next operation. This error can be safely ignored.

**DeleteEmailIdentity**

- `NotFoundException`: If the email identity does not exist, skip this step and proceed with the next operation. This error can be safely ignored.
