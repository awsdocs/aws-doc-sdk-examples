# snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
# snippet-sourcedescription:[generate_presigned_post.py demonstrates how to generate and use a presigned URL to upload a file.]
# snippet-service:[s3]
# snippet-keyword:[Amazon Simple Storage Service (Amazon S3)]
# snippet-keyword:[Python]
# snippet-keyword:[snippet]
# snippet-sourcedate:[2019-03-22]
# snippet-sourceauthor:[AWS]

# Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
#
# This file is licensed under the Apache License, Version 2.0 (the "License").
# You may not use this file except in compliance with the License. A copy of the
# License is located at
#
# http://aws.amazon.com/apache2.0/
#
# This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
# OF ANY KIND, either express or implied. See the License for the specific
# language governing permissions and limitations under the License.

import base64
import json
import logging
import requests
import boto3
from botocore.exceptions import ClientError


def use_presigned_url_in_html_page(url, fields):
    """Demonstrate how to use a presigned S3 URL to upload a file using an HTML page

    :param url: 'url' value returned by S3Client.generate_presigned_post()
    :param fields: 'fields' dictionary returned by S3Client.generate_presigned_post()

    Copy the URL and fields key:values into an HTML form as demonstrated below.

    <html>
      <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
      </head>
      <body>
        <!-- Copy the 'url' value returned by S3Client.generate_presigned_post() -->
        <form action="URL_VALUE" method="post" enctype="multipart/form-data">
          <!-- Copy the 'fields' dictionary key:values returned by S3Client.generate_presigned_post() -->
          <input type="hidden" name="key" value="VALUE" />
          <input type="hidden" name="AWSAccessKeyId" value="VALUE" />
          <input type="hidden" name="policy" value="VALUE" />
          <input type="hidden" name="signature" value="VALUE" />
        File:
          <input type="file"   name="file" /> <br />
          <input type="submit" name="submit" value="Upload to Amazon S3" />
        </form>
      </body>
    </html>

    """
    pass


def create_presigned_post(bucket_name, object_name,
                          fields=None, conditions=None, expiration=3600):
    """Generate a presigned URL S3 POST request to upload a file

    :param bucket_name: string
    :param object_name: string
    :param fields: Dictionary of prefilled form fields
    :param conditions: List of conditions to include in the policy
    :param expiration: Time in seconds for the presigned URL to remain valid
    :return: Dictionary with the following keys:
        url: URL to post to
        fields: Dictionary of form fields and values to submit with the POST
    :return: None if error.
    """

    # Generate a presigned S3 POST URL
    s3_client = boto3.client('s3')
    try:
        response = s3_client.generate_presigned_post(Bucket=bucket_name,
                                                     Key=object_name,
                                                     Fields=fields,
                                                     Conditions=conditions,
                                                     ExpiresIn=expiration)
    except ClientError as e:
        logging.error(e)
        return None

    # The response contains the presigned URL and required fields
    return response


def main():
    """Exercise create_presigned_post()"""

    # Set these values before running the program
    bucket_name = 'BUCKET_NAME'
    object_name = 'OBJECT_NAME'
    # If the presigned URL is used in an HTML page, the object name
    # can include a subdirectory prefix, as shown below.
    # object_name = 'presigned-uploads/${filename}'
    fields = {}
    conditions = []
    expiration = 60*60*24  # Upload must occur within 24 hours

    # Set up logging
    logging.basicConfig(level=logging.DEBUG,
                        format='%(levelname)s: %(asctime)s: %(message)s')

    # Generate a presigned S3 POST URL
    response = create_presigned_post(bucket_name, object_name,
                                     fields, conditions, expiration=expiration)
    if response is None:
        exit(1)
    logging.info(f'Presigned S3 POST URL: {response["url"]}')
    logging.info("Contents of 'fields' dictionary:")
    logging.info(json.dumps(response['fields']))

    # Write presigned URL and fields to files
    with open('post_url.txt', 'w') as f:
        f.write(response['url'])
    with open('post_fields.json', 'w') as f:
        f.write(json.dumps(response['fields']))

    # FYI: The generated policy can be examined by decoding it
    policy_decoded = base64.b64decode(response['fields']['policy'])

    # Demonstrate how an HTML page can use the presigned URL to upload a file
    use_presigned_url_in_html_page(response['url'], response['fields'])

    # Demonstrate how another Python program can use the presigned URL to upload a file
    # Use the Python requests package, which must be installed manually.
    #    pip install requests
    with open(object_name, 'rb') as f:
        files = {'file': (object_name, f)}
        http_response = requests.post(response['url'], data=response['fields'], files=files)
    # If successful, returns HTTP Status Code 204
    logging.info(f'File upload HTTP status code: {http_response.status_code}')


if __name__ == '__main__':
    main()
