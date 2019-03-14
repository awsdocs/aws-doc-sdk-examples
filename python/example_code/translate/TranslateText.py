# snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
# snippet-sourcedescription:[TranslateText.py demonstrates how to use the translate text operation.]
# snippet-service:[translate]
# snippet-keyword:[Amazon Translate]
# snippet-keyword:[Python]
# snippet-sourcedate:[2019-03-13]
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

# snippet-start:[translate.python.translatetext.complete]
import boto3

translate = boto3.client('translate')
result = translate.translate_text(Text="Hello, World",
                                  SourceLanguageCode="en",
                                  TargetLanguageCode="de")
print(f'TranslatedText: {result["TranslatedText"]}')
print(f'SourceLanguageCode: {result["SourceLanguageCode"]}')
print(f'TargetLanguageCode: {result["TargetLanguageCode"]}')
# snippet-end:[translate.python.translatetext.complete]
