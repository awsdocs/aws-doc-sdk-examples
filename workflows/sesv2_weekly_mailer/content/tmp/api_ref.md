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
  Create a script that uses curl commands to download the API documents for each
  API used.

  API Ref urls look like
  https://docs.aws.amazon.com/ses/latest/APIReference-V2/API_CreateContact.html

  Initialize the destination files with a markdown yaml header with a single
  property `skip: true`.

  After the curl command, include a pipe that uses `pup` to select only the
  main-col-body: `pup "#main-col-body"`, pipe that through the program
  `html2text -nobs -utf8`, and finally append to the file `10_{apiname}.md`.
---

```bash
echo "---\nskip: true\n---" > 10_CreateContact.md
curl https://docs.aws.amazon.com/ses/latest/APIReference-V2/API_CreateContact.html | pup "#main-col-body" | html2text -nobs -utf8 >> 10_CreateContact.md

echo "---\nskip: true\n---" > 10_CreateContactList.md
curl https://docs.aws.amazon.com/ses/latest/APIReference-V2/API_CreateContactList.html | pup "#main-col-body" | html2text -nobs -utf8 >> 10_CreateContactList.md

echo "---\nskip: true\n---" > 10_CreateEmailIdentity.md
curl https://docs.aws.amazon.com/ses/latest/APIReference-V2/API_CreateEmailIdentity.html | pup "#main-col-body" | html2text -nobs -utf8 >> 10_CreateEmailIdentity.md

echo "---\nskip: true\n---" > 10_CreateEmailTemplate.md
curl https://docs.aws.amazon.com/ses/latest/APIReference-V2/API_CreateEmailTemplate.html | pup "#main-col-body" | html2text -nobs -utf8 >> 10_CreateEmailTemplate.md

echo "---\nskip: true\n---" > 10_ListContacts.md
curl https://docs.aws.amazon.com/ses/latest/APIReference-V2/API_ListContacts.html | pup "#main-col-body" | html2text -nobs -utf8 >> 10_ListContacts.md

echo "---\nskip: true\n---" > 10_SendEmail.md
curl https://docs.aws.amazon.com/ses/latest/APIReference-V2/API_SendEmail.html | pup "#main-col-body" | html2text -nobs -utf8 >> 10_SendEmail.md
```
