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
  Create a script that uses curl commands to download the python API documents
  for each

  API used. The python API is the lower snake case of the API Call, for
  instance, CreateContact becomes create_contact.


  python API Ref urls look like
  https://boto3.amazonaws.com/v1/documentation/api/latest/reference/services/sesv2/client/create_contact.html


  Initialize the destination files with a markdown yaml header with a single

  property `skip: true`.


  After the curl command, include a pipe that uses `pup` to select only the id
  with the API name: `pup "#{apiname}"`, pipe that through the program
  `html2text -nobs -utf8`, and finally append to the file `10_{apiname}.md`.

  One complete command would be:

  ```
  echo "---\nskip: true\n---" > 10_create_contact.md curl
  https://boto3.amazonaws.com/v1/documentation/api/latest/reference/services/sesv2/client/create_contact.html | pup "#create_contact" | html2text -nobs -utf8 >> 10_create_contact.md
  ```
---

```bash
echo "---\nskip: true\n---" > 10_create_contact.md
curl https://boto3.amazonaws.com/v1/documentation/api/latest/reference/services/sesv2/client/create_contact.html | pup "#main-col-body" | html2text -nobs -utf8 >> 10_create_contact.md

echo "---\nskip: true\n---" > 10_create_contact_list.md
curl https://boto3.amazonaws.com/v1/documentation/api/latest/reference/services/sesv2/client/create_contact_list.html | pup "#main-col-body" | html2text -nobs -utf8 >> 10_create_contact_list.md

echo "---\nskip: true\n---" > 10_create_email_identity.md
curl https://boto3.amazonaws.com/v1/documentation/api/latest/reference/services/sesv2/client/create_email_identity.html | pup "#main-col-body" | html2text -nobs -utf8 >> 10_create_email_identity.md

echo "---\nskip: true\n---" > 10_create_email_template.md
curl https://boto3.amazonaws.com/v1/documentation/api/latest/reference/services/sesv2/client/create_email_template.html | pup "#main-col-body" | html2text -nobs -utf8 >> 10_create_email_template.md

echo "---\nskip: true\n---" > 10_list_contacts.md
curl https://boto3.amazonaws.com/v1/documentation/api/latest/reference/services/sesv2/client/list_contacts.html | pup "#main-col-body" | html2text -nobs -utf8 >> 10_list_contacts.md

echo "---\nskip: true\n---" > 10_send_email.md
curl https://boto3.amazonaws.com/v1/documentation/api/latest/reference/services/sesv2/client/send_email.html | pup "#main-col-body" | html2text -nobs -utf8 >> 10_send_email.md
```
