# AWS SDK for Ruby Code Examples for DynamoDB

## Purpose

These examples demonstrate how to read multiple items from tables using a batch of 
GetItem requests. 

## Prerequisites 

- You must have an AWS account, and have your default credentials and AWS Regions configured
as described in [Configuring the AWS SDK for Ruby](https://docs.aws.amazon.com/sdk-for-ruby/v3/developer-guide/setup-config.html)
in the AWS SDK for Ruby Developer Guide. 
- RubyGems 3.1.2 or later 
- AWS SDK for Ruby. For download and installation instructions, see [Installing the AWS SDK for Ruby](https://docs.aws.amazon.com/sdk-for-ruby/v3/developer-guide/setup-install.html)
- Ruby 2.6 or later. After you install Ruby, add the path to Ruby in your environment
 variables so that you can run Ruby from any command prompt. 
- RSpec 4.0 or later (to run unit tests)
- Download run DynamoDB on your computer. For more information, see 
[Setting Up DynamoDB Local (Downloadable Version)](https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/DynamoDBLocal.html)
- Set up an AWS access key to use the AWS SDKs. For more information, 
see [Setting Up DynamoDB (Web Service)](https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/SettingUp.DynamoWebService.html)
To use the Amazon DynamoDB web service:
    1. [Sign Up for AWS](https://portal.aws.amazon.com/billing/signup#/)
    2. [Get an AWS access key (used to access DynamoDB programmatically if you do not
    plan on only using the console)](https://signin.aws.amazon.com/signin?redirect_uri=https%3A%2F%2Fconsole.aws.amazon.com%2Fiam%2F%3Fstate%3DhashArgs%2523%26isauthcode%3Dtrue&client_id=arn%3Aaws%3Aiam%3A%3A015428540659%3Auser%2Fiam&forceMobileApp=0)
    3. [Configure your credentials (also used to access DynamoDB programmatically)](https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/Tools.CLI.html)

##  Running the Code 

### dynamodb_ruby_example_read_multiple_items/dynamodb_ruby_example_read_multiple_items.rb

This example illustrates how to use the AWS SDK for Ruby to implement the BatchGetItem 
operation, which returns the attributes of one or more items from one or more tables. 
Requested items are identified by primary key.
                                                                          
**IMPORTANT**: If you request more than 100 items, or use over 16MB of data, 
BatchGetItem returns a ValidationException with the message "Too many items requested 
for the BatchGetItem call."

####Response Elements

`HTTP/1.1 200 OK`
- If the action is successful, the service sends back an HTTP 200 response.

## Sample Request 
The following example Query request reads items from table(s) and uses the hash key to 
identify retrievable items. 

    dynamodb.batch_get_item({
        request_items: { # required
            "TableName" => {
                 keys: [ # required
                   {
                    "AttributeName" => "value", # value <Hash,Array,String,Numeric,Boolean,IO,Set,nil>
                   },
                  ],
                  attributes_to_get: ["AttributeName"],
                  consistent_read: false,
                  projection_expression: "ProjectionExpression",
                  expression_attribute_names: {
                     "ExpressionAttributeNameVariable" => "AttributeName",
                    },
                   },
                  },
            "TableName" => {
                 keys: [ # required
                    {
                     "AttributeName" => "value", # value <Hash,Array,String,Numeric,Boolean,IO,Set,nil>
                     },
                    ],
                    attributes_to_get: ["AttributeName"],
                    consistent_read: false,
                    projection_expression: "ProjectionExpression",
                    expression_attribute_names: {
                        "ExpressionAttributeNameVariable" => "AttributeName",
                       },
                      },
                    },
          return_consumed_capacity: "INDEXES", # accepts INDEXES, TOTAL, NONE
         })

 
## Sample Response
    (Types::BatchGetItemOutput) — Returns a response object which responds to the following methods:
    #responses => Hash<String,Array<Hash<String,Types::AttributeValue>>>
    #unprocessed_keys => Hash<String,Types::KeysAndAttributes>
    #consumed_capacity => Array<Types::ConsumedCapacity>
    
  - The following data is returned in JSON format by the service:
      - ConsumedCapacity
        The read capacity units consumed by the entire BatchGetItem operation.
        
        Each element consists of:
        
        TableName - The table that consumed the provisioned throughput.
        CapacityUnits - The total number of capacity units consumed.
        Type: Array of ConsumedCapacity objects
       
      - Responses
        A map of table name to a list of items. Each object in Responses consists of a table name, along with a map of attribute data consisting of the data type and attribute value.
        
        Type: String to array of string to AttributeValue object maps map
        
        Key Length Constraints: Minimum length of 3. Maximum length of 255.
        
        Key Pattern: [a-zA-Z0-9_.-]+
        
        Key Length Constraints: Maximum length of 65535.
        
       - UnprocessedKeys
         A map of tables and their respective keys that were not processed with the current response. The UnprocessedKeys value is in the same form as RequestItems, so the value can be provided directly to a subsequent BatchGetItem operation. For more information, see RequestItems in the Request Parameters section.
         
         Each element consists of:
         
         Keys - An array of primary key attribute values that define specific items in the table.
         ProjectionExpression - One or more attributes to be retrieved from the table or index. By default, all attributes are returned. If a requested attribute is not found, it does not appear in the result.
         ConsistentRead - The consistency of a read operation. If set to true, then a strongly consistent read is used; otherwise, an eventually consistent read is used.
         If there are no unprocessed keys remaining, the response contains an empty UnprocessedKeys map.
         
         Type: String to KeysAndAttributes object map
         
         Key Length Constraints: Minimum length of 3. Maximum length of 255.
         
         Key Pattern: [a-zA-Z0-9_.-]+





## Testing the Amazon DynamoDB Ruby files
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