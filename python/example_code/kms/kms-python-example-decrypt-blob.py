# Copyright 2010-2017 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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

import boto3

# Decrypt blob

blob = '\x01\x02\x02\x00xl\xf0\xbc5_\x1bJ\x14\xb6\xe0\x1e\xcfg.\xb3\x805\r\x02\xd0a\xd2s\xfd\xe8\xe8\x03"T\x17\xb2@\x01\xf2\xb2\xd5iMd%\x84\xca\xdf\xb0\xe1\xa5\x9c\x15O\x00\x00\x00h0f\x06\t*\x86H\x86\xf7\r\x01\x07\x06\xa0Y0W\x02\x01\x000R\x06\t*\x86H\x86\xf7\r\x01\x07\x010\x1e\x06\t`\x86H\x01e\x03\x04\x01.0\x11\x04\x0cU\xfd|\xc8\xd7\xd0\xf9*\x17\x8bF\xbf\x02\x01\x10\x80%"\x92\xda\xbf\xa0\xd0Z\xc9\xa56\xcf\xb8\xed\xc6\xf1.\xae\xd4\x84\xa6[s\x82\xfb\xe8\xe2\xd8\x05u\xf4h\xed\x9eF\xad\xb7\xfc'

client = boto3.client('kms')

response = client.decrypt(
    CiphertextBlob = blob
)

print(response['Plaintext'])
