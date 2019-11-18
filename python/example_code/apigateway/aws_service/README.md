# AWS API Gateway REST interface to an AWS Service

The sample program creates a REST interface to the Amazon Translate service's TranslateText
method. The service is called through an HTTPS GET method. The calling program does not need
to have AWS credentials or use an AWS SDK to access the service.

The sample program creates an interface to the Amazon Translate service, but interfaces to other
AWS services can be implemented by following the operations demonstrated in the program.

The generated service infrastructure can be examined in the AWS console. In the API Gateway
service window, explore the APIs, Usage Plans, and API Keys panes.

Each call to the service must specify an API key value in an `X-API-Key` header. The key is 
created as part of the service infrastructure.

The service's URL, API key value, and other IDs are stored in a configuration file that is created
as part of the service infrastructure. When the service infrastructure is deleted, the configuration
file is also deleted.

The text to translate is specified in a GET query parameter called `text`. The text can be in
any language supported by Amazon Translate. The service translates the specified text to French.
It is a trivial operation to translate the text to some other language. Refer to the source code
for details.   

Text specified on the command line using the `-t` or `--text` options is translated and output to 
the terminal. Multi-word phrases must be surrounded by double quotation marks, as in `-t "Hello, my
friend."`

Text specified in a file using the `-f` or `--file` options is translated and output to a disk file.
The output file name is constructed by appending the `.fr` extension to the input file name.

Before sending a large document, check the current size limit for Amazon Translate. At the time of
this writing, the size limit is 5000 bytes, which is about 500-1000 words. If the text is too
long to fit within an HTTPS GET request, it may be truncated or generate a `414: URI Too Long`
error. In such cases, it is better to either parse and translate sentences individually or pass
the text in the body of a POST request. Implementation of a POST interface to the TranslateText
method is left as an exercise for the reader.

Note: Text with embedded newlines is currently not supported by Amazon Translate. Such text results 
in a Serialization Exception.

## Repository files

* `aws_service.py` : Main program source file
* `Tale_of_Two_Cities.txt` : Sample text to translate 

## AWS infrastructure resources

* API Gateway REST API to the Amazon Translate TranslateText method
* API Gateway usage plan
* API Gateway API key
* AWS Identity and Access Management (IAM) role for the API Gateway resource

## Prerequisites

* Install Python 3.x.
* Install the AWS SDK for Python `boto3`. Instructions are at https://github.com/boto/boto3.
* Install the AWS CLI (Command Line Interface). Instructions are at 
  https://docs.aws.amazon.com/cli/latest/userguide/cli-chap-install.html.
* Configure the AWS CLI. Instructions are at 
  https://docs.aws.amazon.com/cli/latest/userguide/cli-chap-getting-started.html.
* Install the Python `requests` package, as in `pip install requests`.

## Instructions

To create the REST API infrastructure:

    python aws_service.py

To translate text:

    python aws_service.py -t "Text to translate"
    OR
    python aws_service.py --text "Text to translate"
    
To translate text contained in a file:

    python aws_service.py -f FileWithText.txt
    OR
    python aws_service.py --file FileWithText.txt
    
To delete the REST API infrastructure:

    python aws_service.py -d
    OR
    python aws_service.py --delete
