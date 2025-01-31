---
skip: true
---
****** CreateEmailTemplate ******
Creates an email template. Email templates enable you to send personalized
email to one or more destinations in a single API operation. For more
information, see the Amazon_SES_Developer_Guide_.
You can execute this operation no more than once per second.
***** Request Syntax *****





   POST /v2/email/templates HTTP/1.1
Content-type: application/json

    {

   "

____TemplateContent
___
   ":

    {

   "

____Html
___
   ": "

    string

   ",
      "

____Subject
___
   ": "

    string

   ",
      "

____Text
___
   ": "

    string

   "
   },
   "

____TemplateName
___
   ": "

    string

   "
}

***** URI Request Parameters *****
The request does not use any URI parameters.
***** Request Body *****
The request accepts the following data in JSON format.
  TemplateContent
      The content of the email template, composed of a subject line, an HTML
      part, and a text-only part.
      Type: EmailTemplateContent_object
      Required: Yes
  TemplateName
      The name of the template.
      Type: String
      Length Constraints: Minimum length of 1.
      Required: Yes
***** Response Syntax *****





   HTTP/1.1 200

***** Response Elements *****
If the action is successful, the service sends back an HTTP 200 response with
an empty HTTP body.
***** Errors *****
For information about the errors that are common to all actions, see Common
Errors_.
  AlreadyExistsException
      The resource specified in your request already exists.
      HTTP Status Code: 400
  BadRequestException
      The input you provided is invalid.
      HTTP Status Code: 400
  LimitExceededException
      There are too many instances of the specified resource type.
      HTTP Status Code: 400
  TooManyRequestsException
      Too many requests have been made to the operation.
      HTTP Status Code: 429
