---
skip: true
---
****** CreateEmailIdentity ******
Starts the process of verifying an email identity. An identity is an email
address or domain that you use when you send email. Before you can use an
identity to send email, you first have to verify it. By verifying an identity,
you demonstrate that you're the owner of the identity, and that you've given
Amazon SES API v2 permission to send email from the identity.
When you verify an email address, Amazon SES sends an email to the address.
Your email address is verified as soon as you follow the link in the
verification email.
When you verify a domain without specifying the DkimSigningAttributes object,
this operation provides a set of DKIM tokens. You can convert these tokens into
CNAME records, which you then add to the DNS configuration for your domain.
Your domain is verified when Amazon SES detects these records in the DNS
configuration for your domain. This verification method is known as Easy_DKIM_.
Alternatively, you can perform the verification process by providing your own
public-private key pair. This verification method is known as Bring Your Own
DKIM (BYODKIM). To use BYODKIM, your call to the CreateEmailIdentity operation
has to include the DkimSigningAttributes object. When you specify this object,
you provide a selector (a component of the DNS record name that identifies the
public key to use for DKIM authentication) and a private key.
When you verify a domain, this operation provides a set of DKIM tokens, which
you can convert into CNAME tokens. You add these CNAME tokens to the DNS
configuration for your domain. Your domain is verified when Amazon SES detects
these records in the DNS configuration for your domain. For some DNS providers,
it can take 72 hours or more to complete the domain verification process.
Additionally, you can associate an existing configuration set with the email
identity that you're verifying.
***** Request Syntax *****





   POST /v2/email/identities HTTP/1.1
Content-type: application/json

    {

   "

____ConfigurationSetName
___
   ": "

    string

   ",
   "

____DkimSigningAttributes
___
   ":

    {

   "

____DomainSigningPrivateKey
___
   ": "

    string

   ",
      "

____DomainSigningSelector
___
   ": "

    string

   ",
      "

____NextSigningKeyLength
___
   ": "

    string

   "
   },
   "

____EmailIdentity
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
   ]
}

***** URI Request Parameters *****
The request does not use any URI parameters.
***** Request Body *****
The request accepts the following data in JSON format.
  ConfigurationSetName
      The configuration set to use by default when sending from this identity.
      Note that any configuration set defined in the email sending request
      takes precedence.
      Type: String
      Required: No
  DkimSigningAttributes
      If your request includes this object, Amazon SES configures the identity
      to use Bring Your Own DKIM (BYODKIM) for DKIM authentication purposes,
      or, configures the key length to be used for Easy_DKIM_.
      You can only specify this object if the email identity is a domain, as
      opposed to an address.
      Type: DkimSigningAttributes_object
      Required: No
  EmailIdentity
      The email address or domain to verify.
      Type: String
      Length Constraints: Minimum length of 1.
      Required: Yes
  Tags
      An array of objects that define the tags (keys and values) to associate
      with the email identity.
      Type: Array of Tag_objects
      Required: No
***** Response Syntax *****





   HTTP/1.1 200
Content-type: application/json

    {

   "

____DkimAttributes
___
   ":

    {

   "

____CurrentSigningKeyLength
___
   ": "


     string


   ",
      "

____LastKeyGenerationTimestamp
___
   ":


     number


   ,
      "

____NextSigningKeyLength
___
   ": "


     string


   ",
      "

____SigningAttributesOrigin
___
   ": "


     string


   ",
      "

____SigningEnabled
___
   ":


     boolean


   ,
      "

____Status
___
   ": "


     string


   ",
      "

____Tokens
___
   ": [ "


     string


   " ]
   },
   "

____IdentityType
___
   ": "


     string


   ",
   "

____VerifiedForSendingStatus
___
   ":


     boolean


   }

***** Response Elements *****
If the action is successful, the service sends back an HTTP 200 response.
The following data is returned in JSON format by the service.
  DkimAttributes
      An object that contains information about the DKIM attributes for the
      identity.
      Type: DkimAttributes_object
  IdentityType
      The email identity type. Note: the MANAGED_DOMAIN identity type is not
      supported.
      Type: String
      Valid Values: EMAIL_ADDRESS | DOMAIN | MANAGED_DOMAIN
  VerifiedForSendingStatus
      Specifies whether or not the identity is verified. You can only send
      email from verified email addresses or domains. For more information
      about verifying identities, see the Amazon_Pinpoint_User_Guide_.
      Type: Boolean
***** Errors *****
For information about the errors that are common to all actions, see Common
Errors_.
  AlreadyExistsException
      The resource specified in your request already exists.
      HTTP Status Code: 400
  BadRequestException
      The input you provided is invalid.
      HTTP Status Code: 400
  ConcurrentModificationException
      The resource is being modified by another operation or thread.
      HTTP Status Code: 500
  LimitExceededException
      There are too many instances of the specified resource type.
      HTTP Status Code: 400
  NotFoundException
      The resource you attempted to access doesn't exist.
      HTTP Status Code: 404
  TooManyRequestsException
      Too many requests have been made to the operation.
      HTTP Status Code: 429
