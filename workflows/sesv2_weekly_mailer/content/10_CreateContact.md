---
skip: true
---
****** CreateContact ******
Creates a contact, which is an end-user who is receiving the email, and adds
them to a contact list.
***** Request Syntax *****





   POST /v2/email/contact-lists/

    ContactListName

   /contacts HTTP/1.1
Content-type: application/json

    {

   "

____AttributesData
___
   ": "

    string

   ",
   "

____EmailAddress
___
   ": "

    string

   ",
   "

____TopicPreferences
___
   ": [

    {

   "

____SubscriptionStatus
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
   ],
   "

____UnsubscribeAll
___
   ":

    boolean

   }

***** URI Request Parameters *****
The request uses the following URI parameters.
  ContactListName
      The name of the contact list to which the contact should be added.
      Required: Yes
***** Request Body *****
The request accepts the following data in JSON format.
  AttributesData
      The attribute data attached to a contact.
      Type: String
      Required: No
  EmailAddress
      The contact's email address.
      Type: String
      Required: Yes
  TopicPreferences
      The contact's preferences for being opted-in to or opted-out of topics.
      Type: Array of TopicPreference_objects
      Required: No
  UnsubscribeAll
      A boolean value status noting if the contact is unsubscribed from all
      contact list topics.
      Type: Boolean
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
  NotFoundException
      The resource you attempted to access doesn't exist.
      HTTP Status Code: 404
  TooManyRequestsException
      Too many requests have been made to the operation.
      HTTP Status Code: 429
