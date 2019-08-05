# Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
# This file is licensed under the Apache License, Version 2.0 (the "License").
# You may not use this file except in compliance with the License. A copy of
# the License is located at
# http://aws.amazon.com/apache2.0/
# This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
# CONDITIONS OF ANY KIND, either express or implied. See the License for the
# specific language governing permissions and limitations under the License.

cmake_minimum_required(VERSION 3.2)
project(iam-examples)
set (CMAKE_CXX_STANDARD 11)

# Locate the aws sdk for c++ package.
# Note: assume_role.cpp needs to link with the S3 and STS libraries
find_package(AWSSDK REQUIRED COMPONENTS iam s3 sts)

set(EXAMPLES "")
list(APPEND EXAMPLES "access_key_last_used")
list(APPEND EXAMPLES "assume_role")
list(APPEND EXAMPLES "attach_role_policy")
list(APPEND EXAMPLES "create_account_alias")
list(APPEND EXAMPLES "create_access_key")
list(APPEND EXAMPLES "create_policy")
list(APPEND EXAMPLES "create_role")
list(APPEND EXAMPLES "create_user")
list(APPEND EXAMPLES "delete_access_key")
list(APPEND EXAMPLES "delete_account_alias")
list(APPEND EXAMPLES "delete_policy")
list(APPEND EXAMPLES "delete_server_cert")
list(APPEND EXAMPLES "delete_user")
list(APPEND EXAMPLES "detach_role_policy")
list(APPEND EXAMPLES "get_policy")
list(APPEND EXAMPLES "get_server_cert")
list(APPEND EXAMPLES "list_access_keys")
list(APPEND EXAMPLES "list_account_aliases")
list(APPEND EXAMPLES "list_policies")
list(APPEND EXAMPLES "list_server_certs")
list(APPEND EXAMPLES "list_users")
list(APPEND EXAMPLES "put_role_policy")
list(APPEND EXAMPLES "update_access_key")
list(APPEND EXAMPLES "update_server_cert")
list(APPEND EXAMPLES "update_user")

# The executables to build.
foreach(EXAMPLE IN LISTS EXAMPLES)
    add_executable(${EXAMPLE} ${EXAMPLE}.cpp)
    target_link_libraries(${EXAMPLE} ${AWSSDK_LINK_LIBRARIES})
endforeach()
