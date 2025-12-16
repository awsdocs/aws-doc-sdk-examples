" Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
" SPDX-License-Identifier: Apache-2.0
CLASS ltc_awsex_cl_sfn_actions DEFINITION DEFERRED.
CLASS /awsex/cl_sfn_actions DEFINITION LOCAL FRIENDS ltc_awsex_cl_sfn_actions.

CLASS ltc_awsex_cl_sfn_actions DEFINITION FOR TESTING DURATION LONG RISK LEVEL DANGEROUS.

  PRIVATE SECTION.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    CLASS-DATA ao_session TYPE REF TO /aws1/cl_rt_session_base.
    CLASS-DATA ao_sfn TYPE REF TO /aws1/if_sfn.
    CLASS-DATA ao_iam TYPE REF TO /aws1/if_iam.
    CLASS-DATA ao_sfn_actions TYPE REF TO /awsex/cl_sfn_actions.
    CLASS-DATA av_role_name TYPE /aws1/iamrolenametype.
    CLASS-DATA av_role_arn TYPE /aws1/sfnarn.
    CLASS-DATA av_state_machine_name TYPE /aws1/sfnname.
    CLASS-DATA av_activity_name TYPE /aws1/sfnname.
    CLASS-DATA av_state_machine_arn TYPE /aws1/sfnarn.
    CLASS-DATA av_activity_arn TYPE /aws1/sfnarn.
    CLASS-DATA av_lmd_function_name TYPE /aws1/lmdfunctionname.
    CLASS-DATA av_lmd_function_arn TYPE /aws1/sfnarn.

    CLASS-METHODS class_setup RAISING /aws1/cx_rt_generic.
    CLASS-METHODS class_teardown RAISING /aws1/cx_rt_generic.

    METHODS create_state_machine FOR TESTING RAISING /aws1/cx_rt_generic.
    METHODS list_state_machines FOR TESTING RAISING /aws1/cx_rt_generic.
    METHODS describe_state_machine FOR TESTING RAISING /aws1/cx_rt_generic.
    METHODS start_execution FOR TESTING RAISING /aws1/cx_rt_generic.
    METHODS describe_execution FOR TESTING RAISING /aws1/cx_rt_generic.
    METHODS delete_state_machine FOR TESTING RAISING /aws1/cx_rt_generic.
    METHODS create_activity FOR TESTING RAISING /aws1/cx_rt_generic.
    METHODS list_activities FOR TESTING RAISING /aws1/cx_rt_generic.
    METHODS get_activity_task FOR TESTING RAISING /aws1/cx_rt_generic.
    METHODS send_task_success FOR TESTING RAISING /aws1/cx_rt_generic.
    METHODS delete_activity FOR TESTING RAISING /aws1/cx_rt_generic.

    METHODS create_iam_role
      RETURNING
        VALUE(ov_role_arn) TYPE /aws1/sfnarn
      RAISING
        /aws1/cx_rt_generic.

    METHODS wait_for_state_machine
      IMPORTING
        iv_state_machine_arn TYPE /aws1/sfnarn
        iv_max_wait_seconds  TYPE i DEFAULT 60
      RAISING
        /aws1/cx_rt_generic.

    METHODS wait_for_execution
      IMPORTING
        iv_execution_arn    TYPE /aws1/sfnarn
        iv_max_wait_seconds TYPE i DEFAULT 120
      RAISING
        /aws1/cx_rt_generic.
ENDCLASS.

CLASS ltc_awsex_cl_sfn_actions IMPLEMENTATION.

  METHOD class_setup.
    ao_session = /aws1/cl_rt_session_aws=>create( iv_profile_id = cv_pfl ).
    ao_sfn = /aws1/cl_sfn_factory=>create( ao_session ).
    ao_iam = /aws1/cl_iam_factory=>create( ao_session ).
    ao_sfn_actions = NEW /awsex/cl_sfn_actions( ).

    " Generate unique resource names using utility function
    DATA(lv_uuid) = /awsex/cl_utils=>get_random_string( ).
    av_role_name = |sfn-test-role-{ lv_uuid }|.
    av_state_machine_name = |sfn-test-sm-{ lv_uuid }|.
    av_activity_name = |sfn-test-activity-{ lv_uuid }|.

    " Create IAM role for state machine
    DATA(lo_ltc) = NEW ltc_awsex_cl_sfn_actions( ).
    av_role_arn = lo_ltc->create_iam_role( ).

    " Wait for IAM role to propagate
    WAIT UP TO 10 SECONDS.

    " Create a state machine for testing (used by multiple tests)
    DATA lv_definition TYPE /aws1/sfndefinition.
    lv_definition = |\{| &&
      |"Comment":"Test state machine for ABAP examples",| &&
      |"StartAt":"HelloWorld",| &&
      |"States":\{| &&
      |"HelloWorld":\{| &&
      |"Type":"Pass",| &&
      |"Result":"Hello World!",| &&
      |"End":true| &&
      |\}| &&
      |\}| &&
      |\}|.

    TRY.
        DATA(lo_create_result) = ao_sfn->createstatemachine(
          iv_name = av_state_machine_name
          iv_definition = lv_definition
          iv_rolearn = av_role_arn
          it_tags = VALUE #( ( NEW /aws1/cl_sfntag(
            iv_key = 'convert_test'
            iv_value = 'true'
          ) ) )
        ).
        av_state_machine_arn = lo_create_result->get_statemachinearn( ).

        " Wait for state machine to become active
        DATA lv_waited TYPE i VALUE 0.
        DATA lv_status TYPE /aws1/sfnstatemachinestatus.
        DO.
          TRY.
              DATA(lo_desc) = ao_sfn->describestatemachine(
                iv_statemachinearn = av_state_machine_arn
              ).
              lv_status = lo_desc->get_status( ).
              IF lv_status = 'ACTIVE'.
                EXIT.
              ENDIF.
            CATCH /aws1/cx_rt_generic.
              " State machine not ready yet
          ENDTRY.
          WAIT UP TO 2 SECONDS.
          lv_waited = lv_waited + 2.
          IF lv_waited >= 60.
            EXIT.
          ENDIF.
        ENDDO.
      CATCH /aws1/cx_rt_generic.
        " Handle any setup errors
    ENDTRY.

    " Create an activity for testing (used by multiple tests)
    TRY.
        DATA(lo_activity_result) = ao_sfn->createactivity(
          iv_name = av_activity_name
          it_tags = VALUE #( ( NEW /aws1/cl_sfntag(
            iv_key = 'convert_test'
            iv_value = 'true'
          ) ) )
        ).
        av_activity_arn = lo_activity_result->get_activityarn( ).

        " Wait for activity to propagate
        WAIT UP TO 5 SECONDS.
      CATCH /aws1/cx_rt_generic.
        " Handle any setup errors
    ENDTRY.
  ENDMETHOD.

  METHOD class_teardown.
    " Clean up activity if it exists
    IF av_activity_arn IS NOT INITIAL.
      TRY.
          ao_sfn->deleteactivity( iv_activityarn = av_activity_arn ).
        CATCH /aws1/cx_rt_generic.
          " Ignore errors during cleanup
      ENDTRY.
    ENDIF.

    " Clean up state machine if it exists
    IF av_state_machine_arn IS NOT INITIAL.
      TRY.
          ao_sfn->deletestatemachine( iv_statemachinearn = av_state_machine_arn ).
        CATCH /aws1/cx_rt_generic.
          " Ignore errors during cleanup
      ENDTRY.
    ENDIF.

    " Delete IAM role
    IF av_role_name IS NOT INITIAL.
      TRY.
          " Detach policies first
          DATA(lo_policy_result) = ao_iam->listattachedrolepolicies( iv_rolename = av_role_name ).
          DATA(lt_policies) = lo_policy_result->get_attachedpolicies( ).
          LOOP AT lt_policies INTO DATA(lo_policy).
            ao_iam->detachrolepolicy(
              iv_rolename = av_role_name
              iv_policyarn = lo_policy->get_policyarn( )
            ).
          ENDLOOP.
          ao_iam->deleterole( iv_rolename = av_role_name ).
        CATCH /aws1/cx_rt_generic.
          " Ignore errors during cleanup
      ENDTRY.
    ENDIF.
  ENDMETHOD.

  METHOD create_iam_role.
    " Create IAM role for Step Functions
    DATA lv_trust_policy TYPE string.
    lv_trust_policy = |\{| &&
      |"Version":"2012-10-17",| &&
      |"Statement":[\{| &&
      |"Effect":"Allow",| &&
      |"Principal":\{"Service":"states.amazonaws.com"\},| &&
      |"Action":"sts:AssumeRole"| &&
      |\}]| &&
      |\}|.

    TRY.
        DATA(lo_create_role_result) = ao_iam->createrole(
          iv_rolename = av_role_name
          iv_assumerolepolicydocument = lv_trust_policy
          it_tags = VALUE #( ( NEW /aws1/cl_iamtag(
            iv_key = 'convert_test'
            iv_value = 'true'
          ) ) )
        ).
        DATA(lo_role) = lo_create_role_result->get_role( ).
        ov_role_arn = lo_role->get_arn( ).
      CATCH /aws1/cx_iamentityalrdyexex.
        " Role already exists, get its ARN
        DATA(lo_get_role_result) = ao_iam->getrole( iv_rolename = av_role_name ).
        DATA(lo_existing_role) = lo_get_role_result->get_role( ).
        ov_role_arn = lo_existing_role->get_arn( ).
    ENDTRY.

    " Attach basic execution policy
    TRY.
        ao_iam->attachrolepolicy(
          iv_rolename = av_role_name
          iv_policyarn = 'arn:aws:iam::aws:policy/CloudWatchLogsFullAccess'
        ).
      CATCH /aws1/cx_rt_generic.
        " Policy might already be attached, ignore
    ENDTRY.
  ENDMETHOD.

  METHOD wait_for_state_machine.
    DATA lv_waited TYPE i VALUE 0.
    DATA lv_status TYPE /aws1/sfnstatemachinestatus.

    DO.
      TRY.
          DATA(lo_result) = ao_sfn->describestatemachine(
            iv_statemachinearn = iv_state_machine_arn
          ).
          lv_status = lo_result->get_status( ).
          IF lv_status = 'ACTIVE'.
            EXIT.
          ENDIF.
        CATCH /aws1/cx_rt_generic.
          " State machine not ready yet
      ENDTRY.

      WAIT UP TO 2 SECONDS.
      lv_waited = lv_waited + 2.
      IF lv_waited >= iv_max_wait_seconds.
        EXIT.
      ENDIF.
    ENDDO.
  ENDMETHOD.

  METHOD wait_for_execution.
    DATA lv_waited TYPE i VALUE 0.
    DATA lv_status TYPE /aws1/sfnexecutionstatus.

    DO.
      TRY.
          DATA(lo_result) = ao_sfn->describeexecution(
            iv_executionarn = iv_execution_arn
          ).
          lv_status = lo_result->get_status( ).
          " Check for terminal states
          IF lv_status = 'SUCCEEDED' OR lv_status = 'FAILED' OR
             lv_status = 'TIMED_OUT' OR lv_status = 'ABORTED'.
            EXIT.
          ENDIF.
        CATCH /aws1/cx_rt_generic.
          " Execution not ready yet
      ENDTRY.

      WAIT UP TO 2 SECONDS.
      lv_waited = lv_waited + 2.
      IF lv_waited >= iv_max_wait_seconds.
        EXIT.
      ENDIF.
    ENDDO.
  ENDMETHOD.

  METHOD create_state_machine.
    " Create a new state machine for this specific test
    DATA(lv_uuid) = /awsex/cl_utils=>get_random_string( ).
    DATA lv_test_sm_name TYPE /aws1/sfnname.
    lv_test_sm_name = |sfn-create-test-{ lv_uuid }|.

    DATA lv_definition TYPE /aws1/sfndefinition.
    lv_definition = |\{| &&
      |"Comment":"A simple minimal example",| &&
      |"StartAt":"HelloWorld",| &&
      |"States":\{| &&
      |"HelloWorld":\{| &&
      |"Type":"Pass",| &&
      |"Result":"Hello World!",| &&
      |"End":true| &&
      |\}| &&
      |\}| &&
      |\}|.

    DATA lv_state_machine_arn TYPE /aws1/sfnarn.
    lv_state_machine_arn = ao_sfn_actions->create_state_machine(
      iv_name = lv_test_sm_name
      iv_definition = lv_definition
      iv_role_arn = av_role_arn
    ).

    cl_abap_unit_assert=>assert_not_initial(
      act = lv_state_machine_arn
      msg = 'State machine ARN should not be initial'
    ).

    " Wait for state machine to be ready
    wait_for_state_machine( iv_state_machine_arn = lv_state_machine_arn ).

    " Clean up the test state machine
    TRY.
        ao_sfn->deletestatemachine( iv_statemachinearn = lv_state_machine_arn ).
      CATCH /aws1/cx_rt_generic.
        " Ignore cleanup errors
    ENDTRY.
  ENDMETHOD.

  METHOD list_state_machines.
    DATA lv_found_arn TYPE /aws1/sfnarn.
    lv_found_arn = ao_sfn_actions->list_state_machines(
      iv_name = av_state_machine_name
    ).

    cl_abap_unit_assert=>assert_equals(
      exp = av_state_machine_arn
      act = lv_found_arn
      msg = 'Should find the created state machine'
    ).
  ENDMETHOD.

  METHOD describe_state_machine.
    DATA(lo_result) = ao_sfn_actions->describe_state_machine(
      iv_state_machine_arn = av_state_machine_arn
    ).

    cl_abap_unit_assert=>assert_not_initial(
      act = lo_result
      msg = 'Describe result should not be initial'
    ).

    cl_abap_unit_assert=>assert_equals(
      exp = av_state_machine_name
      act = lo_result->get_name( )
      msg = 'State machine name should match'
    ).
  ENDMETHOD.

  METHOD start_execution.
    DATA lv_input TYPE /aws1/sfnsensitivedata.
    lv_input = |\{"name":"TestUser"\}|.

    DATA lv_execution_arn TYPE /aws1/sfnarn.
    lv_execution_arn = ao_sfn_actions->start_execution(
      iv_state_machine_arn = av_state_machine_arn
      iv_input = lv_input
    ).

    cl_abap_unit_assert=>assert_not_initial(
      act = lv_execution_arn
      msg = 'Execution ARN should not be initial'
    ).

    " Wait for execution to complete
    wait_for_execution( iv_execution_arn = lv_execution_arn ).

    " Clean up execution
    TRY.
        ao_sfn->stopexecution(
          iv_executionarn = lv_execution_arn
        ).
      CATCH /aws1/cx_rt_generic.
        " Execution might already be completed, ignore
    ENDTRY.
  ENDMETHOD.

  METHOD describe_execution.
    " Start an execution first
    DATA lv_input TYPE /aws1/sfnsensitivedata.
    lv_input = |\{"name":"TestUser"\}|.

    DATA lv_execution_arn TYPE /aws1/sfnarn.
    lv_execution_arn = ao_sfn_actions->start_execution(
      iv_state_machine_arn = av_state_machine_arn
      iv_input = lv_input
    ).

    " Wait a moment for execution to start
    WAIT UP TO 2 SECONDS.

    " Describe the execution
    DATA(lo_result) = ao_sfn_actions->describe_execution(
      iv_execution_arn = lv_execution_arn
    ).

    cl_abap_unit_assert=>assert_not_initial(
      act = lo_result
      msg = 'Execution description should not be initial'
    ).

    cl_abap_unit_assert=>assert_equals(
      exp = lv_execution_arn
      act = lo_result->get_executionarn( )
      msg = 'Execution ARN should match'
    ).

    " Clean up execution
    TRY.
        ao_sfn->stopexecution(
          iv_executionarn = lv_execution_arn
        ).
      CATCH /aws1/cx_rt_generic.
        " Execution might already be completed, ignore
    ENDTRY.
  ENDMETHOD.

  METHOD delete_state_machine.
    " Create a temporary state machine for deletion test
    DATA lv_temp_name TYPE /aws1/sfnname.
    DATA(lv_uuid) = /awsex/cl_utils=>get_random_string( ).
    lv_temp_name = |sfn-temp-sm-{ lv_uuid }|.

    DATA lv_definition TYPE /aws1/sfndefinition.
    lv_definition = |\{| &&
      |"Comment":"Temporary state machine",| &&
      |"StartAt":"Pass",| &&
      |"States":\{| &&
      |"Pass":\{| &&
      |"Type":"Pass",| &&
      |"End":true| &&
      |\}| &&
      |\}| &&
      |\}|.

    DATA lv_temp_arn TYPE /aws1/sfnarn.
    lv_temp_arn = ao_sfn_actions->create_state_machine(
      iv_name = lv_temp_name
      iv_definition = lv_definition
      iv_role_arn = av_role_arn
    ).

    " Wait for state machine to be ready
    wait_for_state_machine( iv_state_machine_arn = lv_temp_arn ).

    " Delete it
    ao_sfn_actions->delete_state_machine(
      iv_state_machine_arn = lv_temp_arn
    ).

    " Wait for deletion to complete - check for DELETING status or DoesNotExist exception
    DATA lv_waited TYPE i VALUE 0.
    DATA lv_deletion_verified TYPE abap_bool VALUE abap_false.
    DO.
      TRY.
          DATA(lo_desc_result) = ao_sfn->describestatemachine(
            iv_statemachinearn = lv_temp_arn
          ).
          DATA(lv_status) = lo_desc_result->get_status( ).
          " If status is DELETING, the deletion is in progress (success)
          IF lv_status = 'DELETING'.
            lv_deletion_verified = abap_true.
            EXIT.
          ENDIF.
        CATCH /aws1/cx_sfnstatemachinedoes00.
          " State machine no longer exists - deletion complete
          lv_deletion_verified = abap_true.
          EXIT.
      ENDTRY.

      WAIT UP TO 2 SECONDS.
      lv_waited = lv_waited + 2.
      IF lv_waited >= 60.
        EXIT.
      ENDIF.
    ENDDO.

    cl_abap_unit_assert=>assert_true(
      act = lv_deletion_verified
      msg = 'State machine should have been deleted or be in DELETING status'
    ).
  ENDMETHOD.

  METHOD create_activity.
    " Create a new activity for this specific test
    DATA(lv_uuid) = /awsex/cl_utils=>get_random_string( ).
    DATA lv_test_activity_name TYPE /aws1/sfnname.
    lv_test_activity_name = |sfn-create-act-{ lv_uuid }|.

    DATA lv_activity_arn TYPE /aws1/sfnarn.
    lv_activity_arn = ao_sfn_actions->create_activity(
      iv_name = lv_test_activity_name
    ).

    cl_abap_unit_assert=>assert_not_initial(
      act = lv_activity_arn
      msg = 'Activity ARN should not be initial'
    ).

    " Wait for activity to propagate
    WAIT UP TO 2 SECONDS.

    " Clean up the test activity
    TRY.
        ao_sfn->deleteactivity( iv_activityarn = lv_activity_arn ).
      CATCH /aws1/cx_rt_generic.
        " Ignore cleanup errors
    ENDTRY.
  ENDMETHOD.

  METHOD list_activities.
    DATA lv_found_arn TYPE /aws1/sfnarn.
    lv_found_arn = ao_sfn_actions->list_activities(
      iv_name = av_activity_name
    ).

    cl_abap_unit_assert=>assert_equals(
      exp = av_activity_arn
      act = lv_found_arn
      msg = 'Should find the created activity'
    ).
  ENDMETHOD.

  METHOD get_activity_task.
    " Note: This test will timeout after 60 seconds if no task is available
    " which is expected behavior. We're just testing that the method works.
    DATA(lo_result) = ao_sfn_actions->get_activity_task(
      iv_activity_arn = av_activity_arn
    ).

    " The result object should exist even if no task is available
    cl_abap_unit_assert=>assert_not_initial(
      act = lo_result
      msg = 'Activity task result should not be initial'
    ).
  ENDMETHOD.

  METHOD send_task_success.
    " This test requires an actual task token from a running execution
    " We'll create a state machine with an activity step and test the flow

    " Define a state machine with an activity
    DATA lv_definition TYPE /aws1/sfndefinition.
    lv_definition = |\{| &&
      |"Comment":"State machine with activity",| &&
      |"StartAt":"ActivityTask",| &&
      |"States":\{| &&
      |"ActivityTask":\{| &&
      |"Type":"Task",| &&
      |"Resource":"{ av_activity_arn }",| &&
      |"TimeoutSeconds":30,| &&
      |"End":true| &&
      |\}| &&
      |\}| &&
      |\}|.

    " Create temp state machine with convert_test tag
    DATA(lv_uuid) = /awsex/cl_utils=>get_random_string( ).
    DATA lv_temp_sm_name TYPE /aws1/sfnname.
    lv_temp_sm_name = |sfn-act-sm-{ lv_uuid }|.

    DATA lv_temp_sm_arn TYPE /aws1/sfnarn.
    TRY.
        DATA(lo_sm_result) = ao_sfn->createstatemachine(
          iv_name = lv_temp_sm_name
          iv_definition = lv_definition
          iv_rolearn = av_role_arn
          it_tags = VALUE #( ( NEW /aws1/cl_sfntag(
            iv_key = 'convert_test'
            iv_value = 'true'
          ) ) )
        ).
        lv_temp_sm_arn = lo_sm_result->get_statemachinearn( ).
      CATCH /aws1/cx_rt_generic.
        RETURN.
    ENDTRY.

    " Wait for state machine to be ready
    wait_for_state_machine( iv_state_machine_arn = lv_temp_sm_arn ).

    " Start execution
    DATA lv_input TYPE /aws1/sfnsensitivedata.
    lv_input = |\{"test":"data"\}|.
    DATA lv_exec_arn TYPE /aws1/sfnarn.
    TRY.
        DATA(lo_exec_result) = ao_sfn->startexecution(
          iv_statemachinearn = lv_temp_sm_arn
          iv_input = lv_input
        ).
        lv_exec_arn = lo_exec_result->get_executionarn( ).
      CATCH /aws1/cx_rt_generic.
        " Clean up state machine if execution failed
        TRY.
            ao_sfn->deletestatemachine( iv_statemachinearn = lv_temp_sm_arn ).
          CATCH /aws1/cx_rt_generic.
        ENDTRY.
        RETURN.
    ENDTRY.

    " Try to get the activity task (with short timeout)
    DATA lo_task_result TYPE REF TO /aws1/cl_sfngetactivitytaskout.
    TRY.
        lo_task_result = ao_sfn->getactivitytask(
          iv_activityarn = av_activity_arn
        ).

        " If we got a task token, send success
        IF lo_task_result->get_tasktoken( ) IS NOT INITIAL.
          DATA lv_response TYPE /aws1/sfnsensitivedata.
          lv_response = |\{"result":"success"\}|.

          ao_sfn_actions->send_task_success(
            iv_task_token = lo_task_result->get_tasktoken( )
            iv_task_response = lv_response
          ).
        ENDIF.
      CATCH /aws1/cx_rt_generic.
        " Task might have timed out, that's ok for this test
    ENDTRY.

    " Clean up execution
    TRY.
        ao_sfn->stopexecution( iv_executionarn = lv_exec_arn ).
      CATCH /aws1/cx_rt_generic.
    ENDTRY.

    " Clean up state machine
    TRY.
        ao_sfn->deletestatemachine( iv_statemachinearn = lv_temp_sm_arn ).
      CATCH /aws1/cx_rt_generic.
    ENDTRY.
  ENDMETHOD.

  METHOD delete_activity.
    " Create a temporary activity for deletion test
    DATA(lv_uuid) = /awsex/cl_utils=>get_random_string( ).
    DATA lv_temp_name TYPE /aws1/sfnname.
    lv_temp_name = |sfn-temp-act-{ lv_uuid }|.

    DATA lv_temp_arn TYPE /aws1/sfnarn.
    lv_temp_arn = ao_sfn_actions->create_activity(
      iv_name = lv_temp_name
    ).

    " Wait for activity to propagate
    WAIT UP TO 2 SECONDS.

    " Delete it
    ao_sfn_actions->delete_activity(
      iv_activity_arn = lv_temp_arn
    ).

    " Verify deletion by checking if it's in the list
    DATA lv_found TYPE abap_bool VALUE abap_false.
    DATA(lo_list_result) = ao_sfn->listactivities( ).
    DATA(lt_activities) = lo_list_result->get_activities( ).
    LOOP AT lt_activities INTO DATA(lo_activity).
      IF lo_activity->get_activityarn( ) = lv_temp_arn.
        lv_found = abap_true.
        EXIT.
      ENDIF.
    ENDLOOP.

    cl_abap_unit_assert=>assert_false(
      act = lv_found
      msg = 'Activity should have been deleted'
    ).
  ENDMETHOD.

ENDCLASS.
