---
skip: true
---
****** CreateContactList ******
Creates a contact list.
***** Request Syntax *****





   POST /v2/email/contact-lists HTTP/1.1
Content-type: application/json

    {

   "

____ContactListName
___
   ": "

    string

   ",
   "

____Description
___
   ": "

    string

   ",
   "

____Tags
___
   ": [

    {

   "

____Key
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

____Topics
___
   ": [

    {

   "

____DefaultSubscriptionStatus
___
   ": "

    string

   ",
         "

____Description
___
   ": "

    string

   ",
         "

____DisplayName
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
      }
   ]
}

***** URI Request Parameters *****
The request does not use any URI parameters.
***** Request Body *****
The request accepts the following data in JSON format.
  ContactListName
      The name of the contact list.
      Type: String
      Required: Yes
  Description
      A description of what the contact list is about.
      Type: String
      Required: No
  Tags
      The tags associated with a contact list.
      Type: Array of Tag_objects
      Required: No
  Topics
      An interest group, theme, or label within a list. A contact list can have
      multiple topics.
      Type: Array of Topic_objects
      Required: No
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
