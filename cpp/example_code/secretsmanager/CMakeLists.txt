# Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
# This file is licensed under the Apache License, Version 2.0 (the "License").
# You may not use this file except in compliance with the License. A copy of
# the License is located at
# http://aws.amazon.com/apache2.0/
# This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
# CONDITIONS OF ANY KIND, either express or implied. See the License for the
# specific language governing permissions and limitations under the License.


# Minimal CMakeLists.txt for the AWS SDK for C++.
cmake_minimum_required(VERSION 3.3)
set(CMAKE_CXX_STANDARD 11)
project(secretsmanager-example LANGUAGES CXX)

# Use shared libraries
set(BUILD_SHARED_LIBS ON CACHE STRING "Link to shared libraries by default.")

find_package(AWSSDK REQUIRED COMPONENTS secretsmanager)
find_package(ZLIB)

set(EXAMPLES "")
list(APPEND EXAMPLES "get_secret_value")
list(APPEND EXAMPLES "create_secret_with_string")

#build and link executable
foreach(EXAMPLE IN LISTS EXAMPLES)
    add_executable(${EXAMPLE} ${EXAMPLE}.cpp)
    set_compiler_warnings(${EXAMPLE})
    set_compiler_flags(${EXAMPLE})
    target_link_libraries(${EXAMPLE} ${AWSSDK_LINK_LIBRARIES} ZLIB::ZLIB)
endforeach()

