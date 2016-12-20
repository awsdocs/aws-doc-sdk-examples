cmake_minimum_required(VERSION 2.8)
project(sqs-examples)

# Locate the aws sdk for c++ package.
find_package(aws-sdk-cpp)

set(EXAMPLES "")
list(APPEND EXAMPLES "sqs_list_queues")
list(APPEND EXAMPLES "sqs_create_queue")
list(APPEND EXAMPLES "sqs_get_queue_url")
list(APPEND EXAMPLES "sqs_delete_queue")
list(APPEND EXAMPLES "sqs_send_message")
list(APPEND EXAMPLES "sqs_receive_message")
list(APPEND EXAMPLES "sqs_change_message_visibility")
list(APPEND EXAMPLES "sqs_long_polling_on_create_queue")
list(APPEND EXAMPLES "sqs_long_polling_on_existing_queue")
list(APPEND EXAMPLES "sqs_long_polling_on_message_receipt")
list(APPEND EXAMPLES "sqs_dead_letter_queue")

# The executables to build.
foreach(EXAMPLE IN LISTS EXAMPLES)
    add_executable(${EXAMPLE} ${EXAMPLE}.cpp)
    target_link_libraries(${EXAMPLE} aws-cpp-sdk-sqs)
endforeach()


