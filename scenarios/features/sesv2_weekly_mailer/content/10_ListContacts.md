---
skip: true
---
****** ListContacts ******
Lists the contacts present in a specific contact list.
***** Request Syntax *****





   GET /v2/email/contact-lists/

    ContactListName

   /contacts?NextToken=

    NextToken

   &PageSize=

    PageSize

   HTTP/1.1
Content-type: application/json

    {

   "

____Filter
___
   ":

    {

   "

____FilteredStatus
___
   ": "

    string

   ",
      "

____TopicFilter
___
   ":

    {

   "

____TopicName
___
   ": "

    string

   ",
         "

____UseDefaultIfPreferenceUnavailable
___
   ":

    boolean

   }
   }
}

***** URI Request Parameters *****
The request uses the following URI parameters.
  ContactListName
      The name of the contact list.
      Required: Yes
  NextToken
      A string token indicating that there might be additional contacts
      available to be listed. Use the token provided in the Response to use in
      the subsequent call to ListContacts with the same parameters to retrieve
      the next page of contacts.
  PageSize
      The number of contacts that may be returned at once, which is dependent
      on if there are more or less contacts than the value of the PageSize. Use
      this parameter to paginate results. If additional contacts exist beyond
      the specified limit, the NextToken element is sent in the response. Use
      the NextToken value in subsequent requests to retrieve additional
      contacts.
***** Request Body *****
The request accepts the following data in JSON format.
  Filter
      A filter that can be applied to a list of contacts.
      Type: ListContactsFilter_object
      Required: No
***** Response Syntax *****





   HTTP/1.1 200
Content-type: application/json

    {

   "

____Contacts
___
   ": [

    {

   "

____EmailAddress
___
   ": "


     string


   ",
         "

____LastUpdatedTimestamp
___
   ":


     number


   ,
         "

____TopicDefaultPreferences
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
   ],
   "

____NextToken
___
   ": "


     string


   "
}

***** Response Elements *****
If the action is successful, the service sends back an HTTP 200 response.
The following data is returned in JSON format by the service.
  Contacts
      The contacts present in a specific contact list.
      Type: Array of Contact_objects
  NextToken
      A string token indicating that there might be additional contacts
      available to be listed. Copy this token to a subsequent call to
      ListContacts with the same parameters to retrieve the next page of
      contacts.
      Type: String
***** Errors *****
For information about the errors that are common to all actions, see Common
Errors_.
  BadRequestException
      The input you provided is invalid.
      HTTP Status Code: 400
  NotFoundException
      The resource you attempted to access doesn't exist.
      HTTP Status Code: 404
  TooManyRequestsException
      Too many requests have been made to the operation.
      HTTP Status Code: 429
