cmake_minimum_required(VERSION 2.8)
project(sqs-examples)

# Locate the aws sdk for c++ package.
find_package(aws-sdk-cpp)

set(EXAMPLES "")
list(APPEND EXAMPLES "list_queues")
list(APPEND EXAMPLES "create_queue")
list(APPEND EXAMPLES "get_queue_url")
list(APPEND EXAMPLES "delete_queue")
list(APPEND EXAMPLES "send_message")
list(APPEND EXAMPLES "receive_message")
list(APPEND EXAMPLES "change_message_visibility")
list(APPEND EXAMPLES "long_polling_on_create_queue")
list(APPEND EXAMPLES "long_polling_on_existing_queue")
list(APPEND EXAMPLES "long_polling_on_message_receipt")
list(APPEND EXAMPLES "dead_letter_queue")

# The executables to build.
foreach(EXAMPLE IN LISTS EXAMPLES)
    add_executable(${EXAMPLE} ${EXAMPLE}.cpp)
    target_link_libraries(${EXAMPLE} aws-cpp-sdk-sqs)
endforeach()


