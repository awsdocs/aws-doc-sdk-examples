cmake_minimum_required(VERSION 2.8)
project(cloudwatch-examples)

# Locate the aws sdk for c++ package.
find_package(aws-sdk-cpp)

set(EXAMPLES "")
list(APPEND EXAMPLES "cw_describe_alarms")
list(APPEND EXAMPLES "cw_delete_alarm")
list(APPEND EXAMPLES "cw_put_metric_alarm")
list(APPEND EXAMPLES "cw_enable_alarm_actions")
list(APPEND EXAMPLES "cw_disable_alarm_actions")
list(APPEND EXAMPLES "cw_list_metrics")
list(APPEND EXAMPLES "cw_put_metric_data")
list(APPEND EXAMPLES "cwe_put_rule")
list(APPEND EXAMPLES "cwe_put_targets")
list(APPEND EXAMPLES "cwe_put_events")
list(APPEND EXAMPLES "cwl_describe_subscription_filters")
list(APPEND EXAMPLES "cwl_delete_subscription_filter")
list(APPEND EXAMPLES "cwl_put_subscription_filter")

# The executables to build.
foreach(EXAMPLE IN LISTS EXAMPLES)
    add_executable(${EXAMPLE} ${EXAMPLE}.cpp)
    target_link_libraries(${EXAMPLE} aws-cpp-sdk-monitoring aws-cpp-sdk-events aws-cpp-sdk-logs)
endforeach()


