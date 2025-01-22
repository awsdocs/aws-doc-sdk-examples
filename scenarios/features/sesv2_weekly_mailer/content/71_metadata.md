---
combined: true
debug:
  engine: bedrock
  finish: end_turn
  id: null
  model: anthropic.claude-3-sonnet-20240229-v1:0
  usage: null
isolated: false
skip: true
prompt: >
  Generate yaml for each sesv2 action in the workflow, in each language.

  IDs: sesv2_{Action}

  Java: github: javav2/example_code/ses tags: sesv2.java2.newsletter.{action}

  Python: github: python/example_code/sesv2 tags: python.example_code.sesv2.{action}

  Rust: github: rustv1/examples/ses tags: sesv2.rust.{example}
---

```yaml
sesv2_CreateContact:
  title: Add a contact to a contact list using Amazon SES v2
  category:
  languages:
    Java:
      versions:
        - sdk_version: 2
          github: javav2/example_code/ses
          excerpts:
            - description:
              snippet_tags:
                - sesv2.java2.newsletter.CreateContact
    Python:
      versions:
        - sdk_version: 3
          github: python/example_code/sesv2
          excerpts:
            - description:
              snippet_tags:
                - python.example_code.sesv2.CreateContact
    Rust:
      versions:
        - sdk_version: 1
          github: rustv1/examples/ses
          excerpts:
            - description:
              snippet_tags:
                - sesv2.rust.create-contact
  services:
    sesv2: { CreateContact }

sesv2_CreateContactList:
  title: Create a contact list for Amazon SES v2
  category:
  languages:
    Java:
      versions:
        - sdk_version: 2
          github: javav2/example_code/ses
          excerpts:
            - description:
              snippet_tags:
                - sesv2.java2.newsletter.CreateContactList
    Python:
      versions:
        - sdk_version: 3
          github: python/example_code/sesv2
          excerpts:
            - description:
              snippet_tags:
                - python.example_code.sesv2.CreateContactList
    Rust:
      versions:
        - sdk_version: 1
          github: rustv1/examples/ses
          excerpts:
            - description:
              snippet_tags:
                - sesv2.rust.create-contact-list
  services:
    sesv2: { CreateContactList }

sesv2_CreateEmailIdentity:
  title: Create an email identity for Amazon SES v2
  category:
  languages:
    Java:
      versions:
        - sdk_version: 2
          github: javav2/example_code/ses
          excerpts:
            - description:
              snippet_tags:
                - sesv2.java2.newsletter.CreateEmailIdentity
    Python:
      versions:
        - sdk_version: 3
          github: python/example_code/sesv2
          excerpts:
            - description:
              snippet_tags:
                - python.example_code.sesv2.CreateEmailIdentity
    Rust:
      versions:
        - sdk_version: 1
          github: rustv1/examples/ses
          excerpts:
            - description:
              snippet_tags:
                - sesv2.rust.create-email-identity
  services:
    sesv2: { CreateEmailIdentity }

sesv2_CreateEmailTemplate:
  title: Create an email template for Amazon SES v2
  category:
  languages:
    Java:
      versions:
        - sdk_version: 2
          github: javav2/example_code/ses
          excerpts:
            - description:
              snippet_tags:
                - sesv2.java2.newsletter.CreateEmailTemplate
    Python:
      versions:
        - sdk_version: 3
          github: python/example_code/sesv2
          excerpts:
            - description:
              snippet_tags:
                - python.example_code.sesv2.CreateEmailTemplate
    Rust:
      versions:
        - sdk_version: 1
          github: rustv1/examples/ses
          excerpts:
            - description:
              snippet_tags:
                - sesv2.rust.create-email-template
  services:
    sesv2: { CreateEmailTemplate }

sesv2_DeleteContactList:
  title: Delete a contact list for Amazon SES v2
  category:
  languages:
    Java:
      versions:
        - sdk_version: 2
          github: javav2/example_code/ses
          excerpts:
            - description:
              snippet_tags:
                - sesv2.java2.newsletter.DeleteContactList
    Python:
      versions:
        - sdk_version: 3
          github: python/example_code/sesv2
          excerpts:
            - description:
              snippet_tags:
                - python.example_code.sesv2.DeleteContactList
    Rust:
      versions:
        - sdk_version: 1
          github: rustv1/examples/ses
          excerpts:
            - description:
              snippet_tags:
                - sesv2.rust.delete-contact-list
  services:
    sesv2: { DeleteContactList }

sesv2_DeleteEmailIdentity:
  title: Delete an email identity for Amazon SES v2
  category:
  languages:
    Java:
      versions:
        - sdk_version: 2
          github: javav2/example_code/ses
          excerpts:
            - description:
              snippet_tags:
                - sesv2.java2.newsletter.DeleteEmailIdentity
    Python:
      versions:
        - sdk_version: 3
          github: python/example_code/sesv2
          excerpts:
            - description:
              snippet_tags:
                - python.example_code.sesv2.DeleteEmailIdentity
    Rust:
      versions:
        - sdk_version: 1
          github: rustv1/examples/ses
          excerpts:
            - description:
              snippet_tags:
                - sesv2.rust.delete-email-identity
  services:
    sesv2: { DeleteEmailIdentity }

sesv2_DeleteEmailTemplate:
  title: Delete an email template for Amazon SES v2
  category:
  languages:
    Java:
      versions:
        - sdk_version: 2
          github: javav2/example_code/ses
          excerpts:
            - description:
              snippet_tags:
                - sesv2.java2.newsletter.DeleteEmailTemplate
    Python:
      versions:
        - sdk_version: 3
          github: python/example_code/sesv2
          excerpts:
            - description:
              snippet_tags:
                - python.example_code.sesv2.DeleteEmailTemplate
    Rust:
      versions:
        - sdk_version: 1
          github: rustv1/examples/ses
          excerpts:
            - description:
              snippet_tags:
                - sesv2.rust.delete-email-template
  services:
    sesv2: { DeleteEmailTemplate }

sesv2_ListContacts:
  title: List contacts in a contact list for Amazon SES v2
  category:
  languages:
    Java:
      versions:
        - sdk_version: 2
          github: javav2/example_code/ses
          excerpts:
            - description:
              snippet_tags:
                - sesv2.java2.newsletter.ListContacts
    Python:
      versions:
        - sdk_version: 3
          github: python/example_code/sesv2
          excerpts:
            - description:
              snippet_tags:
                - python.example_code.sesv2.ListContacts
    Rust:
      versions:
        - sdk_version: 1
          github: rustv1/examples/ses
          excerpts:
            - description:
              snippet_tags:
                - sesv2.rust.list-contacts
  services:
    sesv2: { ListContacts }

sesv2_SendEmail:
  title: Send an email using Amazon SES v2
  category:
  languages:
    Java:
      versions:
        - sdk_version: 2
          github: javav2/example_code/ses
          excerpts:
            - description:
              snippet_tags:
                - sesv2.java2.newsletter.SendEmail
    Python:
      versions:
        - sdk_version: 3
          github: python/example_code/sesv2
          excerpts:
            - description:
              snippet_tags:
                - python.example_code.sesv2.SendEmail
    Rust:
      versions:
        - sdk_version: 1
          github: rustv1/examples/ses
          excerpts:
            - description:
              snippet_tags:
                - sesv2.rust.send-email
  services:
    sesv2: { SendEmail }
```
