# AWS SDK for Ruby Code Examples for S3

## Purpose

This example explains how to use the SelectObjectContent operation to filter and parse 
object data into records, only returning records that match the specified SQL expression.

## Prerequisites 

- You must have an AWS account, and have your default credentials and AWS Regions configured
as described in [Configuring the AWS SDK for Ruby](https://docs.aws.amazon.com/sdk-for-ruby/v3/developer-guide/setup-config.html)
in the AWS SDK for Ruby Developer Guide. 
- RubyGems 3.1.2 or later 
- AWS SDK for Ruby. For download and installation instructions, see [Installing the AWS SDK for Ruby](https://docs.aws.amazon.com/sdk-for-ruby/v3/developer-guide/setup-install.html)
- Ruby 2.6 or later. After you install Ruby, add the path to Ruby in your environment
 variables so that you can run Ruby from any command prompt. 
- RSpec 4.0 or later (to run unit tests)
- Create an IAM administrator user 
   - Follow the instructions in the IAM User Guide to create your first IAM administrator user and group. 
    For more information, see [Creating Your First IAM Admin User and Group ](https://docs.aws.amazon.com/IAM/latest/UserGuide/getting-started_create-admin-group.html)
    - Sign in as an IAM user with your user name and password. Before you sign in as an IAM user, you
     can verify the sign-in link for IAM users in the IAM console. 
     On the IAM Dashboard, under IAM users sign-in link, you can see the sign-in link 
     for your AWS account. The URL for your sign-in link contains your AWS account ID 
     without dashes (‐).

- Permissions: You must have s3:GetObject permission for this operation. Amazon S3 
Select does not support anonymous access. For more information about permissions, 
see [Specifying Permissions in a Policy](https://docs.aws.amazon.com/AmazonS3/latest/dev/using-with-s3-actions.html)
    - If the object you are querying is encrypted with a customer-provided encryption 
    key (SSE-C), you must use https, and you must provide the encryption key in the 
    request.
    
## Limits

- The following limits apply when using Amazon S3 Select:
    - The maximum length of a SQL expression is 256 KB.
    - The maximum length of a record in the input or result is 1 MB.
    - Amazon S3 Select can only emit nested data using the JSON output format.

- Additional limitations apply when using Amazon S3 Select with Parquet objects:

    - Amazon S3 Select supports only columnar compression using GZIP or Snappy. Amazon S3 Select doesn't support whole-object compression for Parquet objects.
    - Amazon S3 Select doesn't support Parquet output. You must specify the output format as CSV or JSON.
    - The maximum uncompressed row group size is 256 MB.
    - You must use the data types specified in the object's schema.
    - Selecting on a repeated field returns only the last value.


## Object Data Formats

- You can use Amazon S3 Select to query objects that have the following format properties:
  
    - CSV, JSON, and Parquet - Objects must be in CSV, JSON, or Parquet format.
    - UTF-8 - UTF-8 is the only encoding type Amazon S3 Select supports.
    - GZIP or BZIP2 - CSV and JSON files can be compressed using GZIP or BZIP2. GZIP and BZIP2 are the only compression formats that Amazon S3 Select supports for CSV and JSON files. Amazon S3 Select supports columnar compression for Parquet using GZIP or Snappy. Amazon S3 Select does not support whole-object compression for Parquet objects.
    - Server-side encryption - Amazon S3 Select supports querying objects that are
     protected with server-side encryption. For objects that are encrypted with 
     customer-provided encryption keys (SSE-C), you must use HTTPS, and you must use 
     the headers that are documented in the GetObject. 
     For more information about SSE-C, see Server-Side Encryption 
     (Using Customer-Provided Encryption Keys) in the Amazon Simple Storage 
     Service Developer Guide. For objects that are encrypted with Amazon S3 managed encryption keys (SSE-S3) and customer master keys (CMKs) stored in AWS Key Management Service (SSE-KMS), server-side encryption is handled transparently, so you don't need to specify anything. For more information about server-side encryption, including SSE-S3 and SSE-KMS, see Protecting Data Using Server-Side Encryption in the Amazon Simple Storage Service Developer Guide.

    
##  Running the Code 



### s3-select-object-content/s3-select-object-content.rb

 - This example illustrates how to use the AWS SDK for Ruby to implement the 
  SelectObjectContent api, enabling the retrieval of a subset of data from 
 using simple SQL expressions. Streaming the responses as a 
 series of events, instead of returning the full response all at once, 
 there is an added performance benefit of process response messages as they come in. 
 Events are processed asynchronously, instead of needing to wait for the full response
  load before processing.

                
####Response Elements

   
   `HTTP/1.1 200 OK`
 
 - If the action is successful, the service sends back an HTTP 200 response.
    
   
   
    x-amz-id-2: GFihv3y6+kE7KG11GEkQhU7/2/cHR3Yb2fCb2S04nxI423Dqwg2XiQ0B/UZlzYQvPiBlZNRcovw=
    x-amz-request-id: 9F341CD3C4BA79E0
    Date: Tue, 17 Oct 2017 23:54:05 GMT
    
    A series of messages
    
 - x-amz-id-2: A special token that is used together with the x-amz-request-id header to help AWS troubleshoot problems. For information about AWS support using these request IDs, see Troubleshooting Amazon S3.
    - Type: String
    - Default: None
 - x-amz-request-id: A value created by Amazon S3 that uniquely identifies the request. This value is used together with the x-amz-id-2 header to help AWS troubleshoot problems. For information about AWS support using these request IDs, see Troubleshooting Amazon S3.
    - Type: String

#### CSV Input Data Request
   - When making an API call, you may pass CSVInput data as a hash. 
   The following describes how an uncompressed comma-separated values
    (CSV)-formatted input object is formatted.
    
          {
          file_header_info: "USE", # accepts USE, IGNORE, NONE
          comments: "Comments",
          quote_escape_character: "QuoteEscapeCharacter",
          record_delimiter: "RecordDelimiter",
          field_delimiter: "FieldDelimiter",
          quote_character: "QuoteCharacter",
          allow_quoted_record_delimiter: false,
          }
   - allow_quoted_record_delimiter ⇒ Boolean
    Specifies that CSV field values may contain quoted record delimiters and such records should be allowed.
   - comments ⇒ String
    A single character used to indicate that a row should be ignored when the character is present at the start of that row.
   - field_delimiter ⇒ String
    A single character used to separate individual fields in a record.
   - file_header_info ⇒ String
    Describes the first line of input.
   - quote_character ⇒ String
    A single character used for escaping when the field delimiter is part of the value.
   - quote_escape_character ⇒ String
    A single character used for escaping the quotation mark character inside an already escaped value.
   - record_delimiter ⇒ String
    A single character used to separate individual records in the inpu
    
  
## Sample Request 
The following example Query request reads items from table(s) and uses the hash key to 
identify retrievable items. 

    POST /exampleobject.csv?select&select-type=2 HTTP/1.1
    Host: examplebucket.s3.<Region>.amazonaws.com
    Date: Tue, 17 Oct 2017 01:49:52 GMT
    Authorization: authorization string
    Content-Length: content length

    <?xml version="1.0" encoding="UTF-8"?>
    <SelectRequest>
        <Expression>Select * from S3Object</Expression>
        <ExpressionType>SQL</ExpressionType>
        <InputSerialization>
            <CompressionType>GZIP</CompressionType>
            <CSV>
                <FileHeaderInfo>IGNORE</FileHeaderInfo>
                <RecordDelimiter>\n</RecordDelimiter>
                <FieldDelimiter>,</FieldDelimiter>
                <QuoteCharacter>"</QuoteCharacter>
                <QuoteEscapeCharacter>"</QuoteEscapeCharacter>
                <Comments>#</Comments>
            </CSV>
        </InputSerialization>
        <OutputSerialization>
            <CSV>
                <QuoteFields>ASNEEDED</QuoteFields>
                <RecordDelimiter>\n</RecordDelimiter>
                <FieldDelimiter>,</FieldDelimiter>
                <QuoteCharacter>"</QuoteCharacter>
                <QuoteEscapeCharacter>"</QuoteEscapeCharacter>
            </CSV>                               
        </OutputSerialization>
    </SelectRequest> 
 
 - The request accepts the following data in XML format.
 - SelectObjectContentRequest
    - Root level tag for the SelectObjectContentRequest parameters.
    - Required: Yes
   
- Expression
   - The expression that is used to query the object.
   - Type: String
   - Required: Yes
 
- ExpressionType
   - The type of the provided expression (for example, SQL).
   - Type: String
   - Valid Values: SQL
   - Required: Yes
   
- InputSerialization
   - Describes the format of the data in the object that is being queried.
   - Type: InputSerialization data type
   - Required: Yes
   
- OutputSerialization
   - Describes the format of the data that you want Amazon S3 to return in response.
   - Type: OutputSerialization data type
   - Required: Yes
   
- RequestProgress
   - Specifies if periodic request progress information should be enabled.
   - Type: RequestProgress data type
   - Required: No
   
- ScanRange
   - Specifies the byte range of the object to get the records from. A record is processed when its first byte is contained by the range. 
   This parameter is optional, but when specified, it must not be empty. 
   - Type: ScanRange data type
   - Required: No
 
 
####Response Syntax
    HTTP/1.1 200
    <?xml version="1.0" encoding="UTF-8"?>
    <Payload>
       <Records>
          <Payload>blob</Payload>
       </Records>
       <Stats>
          <Details>
             <BytesProcessed>long</BytesProcessed>
             <BytesReturned>long</BytesReturned>
             <BytesScanned>long</BytesScanned>
          </Details>
       </Stats>
       <Progress>
          <Details>
             <BytesProcessed>long</BytesProcessed>
             <BytesReturned>long</BytesReturned>
             <BytesScanned>long</BytesScanned>
          </Details>
       </Progress>
       <Cont>
       </Cont>
       <End>
       </End>
    </Payload>
 
 
 - Payload
    - Root level tag for the Payload parameters.
    - Required: Yes
   
 - Cont
    - The Continuation Event.
    - Type: ContinuationEvent data type
   
- End
    - The End Event.
    - Type: EndEvent data type
  
- Progress
   - The Progress Event.
   - Type: ProgressEvent data type
   
- Records
   - The Records Event.
   - Type: RecordsEvent data type
   
- Stats
   - The Stats Event.
   - Type: StatsEvent data type
  

## Testing the Amazon S3 Ruby files
You can test the Amazon SNS ruby code examples by running RSpec 4.0 and is located in the 
ses/spec folder.

You can execute the RSpec tests from a Ruby IDE, such as RubyMine, or from the command
line using a command line console. There is a green dot for every passing spec, and one
red `F` for every failing test. For example, the following message informs you that a 
test has passed:

    Finished in 0.00217 seconds (files took 0.13631 seconds to load)
    3 examples, 0 failures
    $ echo $?
    0
    $ 
    
### Command Line 
To execute RSpec tests from the command line, you can save the example files into a 
directory on your local machine, `cd` into the directory in which it is saved, and 
type `rspec` followed by the name of the spec file:

     $ rspec ses_get_statistics_spec.rb
     .
        
### Additional information
- As an AWS best practice, grant this code least privilege, or only the 
  permissions required to perform a task. For more information, see 
  [Grant Least Privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege) 
  in the *AWS Identity and Access Management 
  User Guide*.
- This code has not been tested in all AWS Regions. Some AWS services are 
  available only in specific Regions. For more information, see the 
  "AWS Regional Table" on the AWS website.
- Running this code might result in charges to your AWS account.

---
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0