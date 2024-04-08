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
  Create the remaining SES v2 Actions Used links for the Cleanup step. Use the
  same format as the links 


  Current links:
    - [`CreateContact`](https://docs.aws.amazon.com/ses/latest/APIReference-V2/API_CreateContact.html)
    - [`CreateContactList`](https://docs.aws.amazon.com/ses/latest/APIReference-V2/API_CreateContactList.html)
    - [`CreateEmailIdentity`](https://docs.aws.amazon.com/ses/latest/APIReference-V2/API_CreateEmailIdentity.html)
    - [`CreateEmailTemplate`](https://docs.aws.amazon.com/ses/latest/APIReference-V2/API_CreateEmailTemplate.html)
    - [`ListContacts`](https://docs.aws.amazon.com/ses/latest/APIReference-V2/API_ListContacts.html)
    - [`SendEmail`](https://docs.aws.amazon.com/ses/latest/APIReference-V2/API_SendEmail.html) (with both Simple and Template formats)

  New links:
   - Operation: **DeleteContactList**
   - Operation: **DeleteEmailTemplate**
   - Operation: **DeleteEmailIdentity**
---

Here are the remaining SES v2 Actions Used links for the Cleanup step:

- [`DeleteContactList`](https://docs.aws.amazon.com/ses/latest/APIReference-V2/API_DeleteContactList.html)
- [`DeleteEmailTemplate`](https://docs.aws.amazon.com/ses/latest/APIReference-V2/API_DeleteEmailTemplate.html)
- [`DeleteEmailIdentity`](https://docs.aws.amazon.com/ses/latest/APIReference-V2/API_DeleteEmailIdentity.html)
