cmake_minimum_required(VERSION 2.8)
project(dynamodb-examples)

# Locate the aws sdk for c++ package.
find_package(aws-sdk-cpp)

set(EXAMPLES "")
list(APPEND EXAMPLES "batch_get_item")
list(APPEND EXAMPLES "create_table")
list(APPEND EXAMPLES "create_table_composite_key")
list(APPEND EXAMPLES "delete_item")
list(APPEND EXAMPLES "delete_table")
list(APPEND EXAMPLES "describe_table")
list(APPEND EXAMPLES "get_item")
list(APPEND EXAMPLES "list_tables")
list(APPEND EXAMPLES "put_item")
list(APPEND EXAMPLES "update_item")
list(APPEND EXAMPLES "update_table")


# The executables to build.
foreach(EXAMPLE IN LISTS EXAMPLES)
    add_executable(${EXAMPLE} ${EXAMPLE}.cpp)
    target_link_libraries(${EXAMPLE} aws-cpp-sdk-dynamodb)
endforeach()
