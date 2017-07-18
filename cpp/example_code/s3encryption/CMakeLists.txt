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

find_package(AWSSDK REQUIRED)

Message(STATUS "AWSSDK_LIB_DIR: ${AWSSDK_LIB_DIR}")
Message(STATUS "AWSSDK_INC_DIR: ${AWSSDK_INCLUDE_DIR}")
Message(STATUS "AWSSDK_BIN_DIR: ${AWSSDK_BIN_DIR}")

SET(SERVICE_LIST sqs s3 transfer dynamodb s3-encryption apigateway)
AWSSDK_CPY_DYN_LIBS(SERVICE_LIST "" ./)


SET(SERVICE s3-encryption transfer apigateway)
AWSSDK_LIB_DEPS(SERVICE DEPS)
Message(STATUS "${SERVICE} depends on libs: ${DEPS}")

AWSSDK_DETERMINE_LIBS_TO_LINK(SERVICE OUTPUT)

Message(STATUS "All linked libs: ${OUTPUT}")

if (NOT WIN32)
    find_package(PkgConfig)
    pkg_check_modules(DEPS REQUIRED ${OUTPUT}) 
endif()

if (APPLE)
    add_definitions(-DUNDER_MACOS)
endif()

link_directories("${AWSSDK_LIB_DIR}")
add_executable(s3Encryption s3Encryption.cpp)

if (WIN32)
    add_definitions(-DUSE_IMPORT_EXPORT)
    target_link_libraries(s3Encryption ${OUTPUT})
    target_include_directories(s3Encryption PUBLIC ${AWSSDK_INCLUDE_DIR})
else()
    target_link_libraries(s3Encryption ${DEPS_LIBRARIES})
    target_include_directories(s3Encryption PRIVATE ${DEPS_INCLUDE_DIRS})
    target_compile_options(s3Encryption PRIVATE ${DEPS_CFLAGS_OTHER})
endif()

