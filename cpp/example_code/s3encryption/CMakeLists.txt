#   Copyright 2010-2017 Amazon.com, Inc. or its affiliates. All Rights Reserved.

#   This file is licensed under the Apache License, Version 2.0 (the "License").
#   You may not use this file except in compliance with the License. A copy of
#   the License is located at

#   http://aws.amazon.com/apache2.0/

#   This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
#   CONDITIONS OF ANY KIND, either express or implied. See the License for the
#   specific language governing permissions and limitations under the License.

cmake_minimum_required(VERSION 3.2)
project(s3Encryption)

find_package(aws-sdk-cpp)

# Link to shared libraries.
add_definitions(-DUSE_IMPORT_EXPORT)

if (APPLE)
    add_definitions(-DUNDER_MACOS)
endif()

add_executable(s3Encryption s3Encryption.cpp)
target_link_libraries(s3Encryption aws-cpp-sdk-s3)
target_link_libraries(s3Encryption aws-cpp-sdk-s3-encryption)

MESSAGE( STATUS "CMAKE_INCLUDE_PATH: " ${CMAKE_INCLUDE_PATH} )
