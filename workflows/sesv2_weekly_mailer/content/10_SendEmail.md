---
skip: true
---
****** SendEmail ******
Sends an email message. You can use the Amazon SES API v2 to send the following
types of messages:
    * Simple – A standard email message. When you create this type of message,
      you specify the sender, the recipient, and the message body, and Amazon
      SES assembles the message for you.
    * Raw – A raw, MIME-formatted email message. When you send this type of
      email, you have to specify all of the message headers, as well as the
      message body. You can use this message type to send messages that contain
      attachments. The message that you specify has to be a valid MIME message.
    * Templated – A message that contains personalization tags. When you send
      this type of email, Amazon SES API v2 automatically replaces the tags
      with values that you specify.
***** Request Syntax *****





   POST /v2/email/outbound-emails HTTP/1.1
Content-type: application/json

    {

   "

____ConfigurationSetName
___
   ": "

    string

   ",
   "

____Content
___
   ":

    {

   "

____Raw
___
   ":

    {

   "

____Data
___
   ":

    blob

   },
      "

____Simple
___
   ":

    {

   "

____Body
___
   ":

    {

   "

____Html
___
   ":

    {

   "

____Charset
___
   ": "

    string

   ",
               "

____Data
___
   ": "

    string

   "
            },
            "

____Text
___
   ":

    {

   "

____Charset
___
   ": "

    string

   ",
               "

____Data
___
   ": "

    string

   "
            }
         },
         "

____Headers
___
   ": [

    {

   "

____Name
___
   ": "

    string

   ",
               "

____Value
___
   ": "

    string

   "
            }
         ],
         "

____Subject
___
   ":

    {

   "

____Charset
___
   ": "

    string

   ",
            "

____Data
___
   ": "

    string

   "
         }
      },
      "

____Template
___
   ":

    {

   "

____Headers
___
   ": [

    {

   "

____Name
___
   ": "

    string

   ",
               "

____Value
___
   ": "

    string

   "
            }
         ],
         "

____TemplateArn
___
   ": "

    string

   ",
         "

____TemplateData
___
   ": "

    string

   ",
         "

____TemplateName
___
   ": "

    string

   "
      }
   },
   "

____Destination
___
   ":

    {

   "

____BccAddresses
___
   ": [ "

    string

   " ],
      "

____CcAddresses
___
   ": [ "

    string

   " ],
      "

____ToAddresses
___
   ": [ "

    string

   " ]
   },
   "

____EmailTags
___
   ": [

    {

   "

____Name
___
   ": "

    string

   ",
         "

____Value
___
   ": "

    string

   "
      }
   ],
   "

____FeedbackForwardingEmailAddress
___
   ": "

    string

   ",
   "

____FeedbackForwardingEmailAddressIdentityArn
___
   ": "

    string

   ",
   "

____FromEmailAddress
___
   ": "

    string

   ",
   "

____FromEmailAddressIdentityArn
___
   ": "

    string

   ",
   "

____ListManagementOptions
___
   ":

    {

   "

____ContactListName
___
   ": "

    string

   ",
      "

____TopicName
___
   ": "

    string

   "
   },
   "

____ReplyToAddresses
___
   ": [ "

    string

   " ]
}

***** URI Request Parameters *****
The request does not use any URI parameters.
***** Request Body *****
The request accepts the following data in JSON format.
  ConfigurationSetName
      The name of the configuration set to use when sending the email.
      Type: String
      Required: No
  Content
      An object that contains the body of the message. You can send either a
      Simple message, Raw message, or a Templated message.
      Type: EmailContent_object
      Required: Yes
  Destination
      An object that contains the recipients of the email message.
      Type: Destination_object
      Required: No
  EmailTags
      A list of tags, in the form of name/value pairs, to apply to an email
      that you send using the SendEmail operation. Tags correspond to
      characteristics of the email that you define, so that you can publish
      email sending events.
      Type: Array of MessageTag_objects
      Required: No
  FeedbackForwardingEmailAddress
      The address that you want bounce and complaint notifications to be sent
      to.
      Type: String
      Required: No
  FeedbackForwardingEmailAddressIdentityArn
      This parameter is used only for sending authorization. It is the ARN of
      the identity that is associated with the sending authorization policy
      that permits you to use the email address specified in the
      FeedbackForwardingEmailAddress parameter.
      For example, if the owner of example.com (which has ARN arn:aws:ses:us-
      east-1:123456789012:identity/example.com) attaches a policy to it that
      authorizes you to use feedback@example.com, then you would specify the
      FeedbackForwardingEmailAddressIdentityArn to be arn:aws:ses:us-east-1:
      123456789012:identity/example.com, and the FeedbackForwardingEmailAddress
      to be feedback@example.com.
      For more information about sending authorization, see the Amazon_SES
      Developer_Guide_.
      Type: String
      Required: No
  FromEmailAddress
      The email address to use as the "From" address for the email. The address
      that you specify has to be verified.
      Type: String
      Required: No
  FromEmailAddressIdentityArn
      This parameter is used only for sending authorization. It is the ARN of
      the identity that is associated with the sending authorization policy
      that permits you to use the email address specified in the
      FromEmailAddress parameter.
      For example, if the owner of example.com (which has ARN arn:aws:ses:us-
      east-1:123456789012:identity/example.com) attaches a policy to it that
      authorizes you to use sender@example.com, then you would specify the
      FromEmailAddressIdentityArn to be arn:aws:ses:us-east-1:123456789012:
      identity/example.com, and the FromEmailAddress to be sender@example.com.
      For more information about sending authorization, see the Amazon_SES
      Developer_Guide_.
      For Raw emails, the FromEmailAddressIdentityArn value overrides the X-
      SES-SOURCE-ARN and X-SES-FROM-ARN headers specified in raw email message
      content.
      Type: String
      Required: No
  ListManagementOptions
      An object used to specify a list or topic to which an email belongs,
      which will be used when a contact chooses to unsubscribe.
      Type: ListManagementOptions_object
      Required: No
  ReplyToAddresses
      The "Reply-to" email addresses for the message. When the recipient
      replies to the message, each Reply-to address receives the reply.
      Type: Array of strings
      Required: No
***** Response Syntax *****





   HTTP/1.1 200
Content-type: application/json

    {

   "

____MessageId
___
   ": "


     string


   "
}

***** Response Elements *****
If the action is successful, the service sends back an HTTP 200 response.
The following data is returned in JSON format by the service.
  MessageId
      A unique identifier for the message that is generated when the message is
      accepted.
      * Note *
      It's possible for Amazon SES to accept a message without sending it. For
      example, this can happen when the message that you're trying to send has
      an attachment that contains a virus, or when you send a templated email
      that contains invalid personalization content.
      Type: String
***** Errors *****
For information about the errors that are common to all actions, see Common
Errors_.
  AccountSuspendedException
      The message can't be sent because the account's ability to send email has
      been permanently restricted.
      HTTP Status Code: 400
  BadRequestException
      The input you provided is invalid.
      HTTP Status Code: 400
  LimitExceededException
      There are too many instances of the specified resource type.
      HTTP Status Code: 400
  MailFromDomainNotVerifiedException
      The message can't be sent because the sending domain isn't verified.
      HTTP Status Code: 400
  MessageRejected
      The message can't be sent because it contains invalid content.
      HTTP Status Code: 400
  NotFoundException
      The resource you attempted to access doesn't exist.
      HTTP Status Code: 404
  SendingPausedException
      The message can't be sent because the account's ability to send email is
      currently paused.
      HTTP Status Code: 400
  TooManyRequestsException
      Too many requests have been made to the operation.
      HTTP Status Code: 429
