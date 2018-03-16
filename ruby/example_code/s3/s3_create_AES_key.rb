# Copyright 2010-2018 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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

require 'openssl'

# Create key
cipher = OpenSSL::Cipher::AES.new(128, :CBC)
cipher.encrypt
key = cipher.random_key

# Convert it to text and display the text value
encoded_string = Base64.encode64(key)
puts encoded_string

# To decode the encoded string:
#   key = encodd_string.unpack("m*")[0]
