" Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
" SPDX-License-Identifier: Apache-2.0
CLASS ltc_awsex_cl_scd_actions DEFINITION DEFERRED.
CLASS /awsex/cl_scd_actions DEFINITION LOCAL FRIENDS ltc_awsex_cl_scd_actions.

CLASS ltc_awsex_cl_scd_actions DEFINITION FOR TESTING DURATION LONG RISK LEVEL DANGEROUS.

  PRIVATE SECTION.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    CLASS-DATA ao_session TYPE REF TO /aws1/cl_rt_session_base.
    CLASS-DATA ao_scd TYPE REF TO /aws1/if_scd.
    CLASS-DATA ao_scd_actions TYPE REF TO /awsex/cl_scd_actions.
    CLASS-DATA ao_sqs TYPE REF TO /aws1/if_sqs.
    CLASS-DATA ao_iam TYPE REF TO /aws1/if_iam.
    CLASS-DATA av_queue_url TYPE /aws1/sqsstring.
    CLASS-DATA av_queue_arn TYPE /aws1/sqsstring.
    CLASS-DATA av_role_arn TYPE /aws1/iamarntype.
    CLASS-DATA av_role_name TYPE /aws1/iamrolenametype.
    CLASS-DATA av_schedule_group_name TYPE /aws1/scdschedulegroupname.
    CLASS-DATA av_schedule_name TYPE /aws1/scdname.

    METHODS:
      create_schedule_group FOR TESTING RAISING /aws1/cx_rt_generic,
      create_schedule FOR TESTING RAISING /aws1/cx_rt_generic,
      delete_schedule FOR TESTING RAISING /aws1/cx_rt_generic,
      delete_schedule_group FOR TESTING RAISING /aws1/cx_rt_generic.

    CLASS-METHODS class_setup RAISING /aws1/cx_rt_generic.
    CLASS-METHODS class_teardown RAISING /aws1/cx_rt_generic.

    METHODS wait_for_schedule_deletion
      IMPORTING
        iv_name                TYPE /aws1/scdname
        iv_schedule_group_name TYPE /aws1/scdschedulegroupname.
ENDCLASS.

CLASS ltc_awsex_cl_scd_actions IMPLEMENTATION.

  METHOD class_setup.
    ao_session = /aws1/cl_rt_session_aws=>create( iv_profile_id = cv_pfl ).
    ao_scd = /aws1/cl_scd_factory=>create( ao_session ).
    ao_scd_actions = NEW /awsex/cl_scd_actions( ).
    ao_sqs = /aws1/cl_sqs_factory=>create( ao_session ).
    ao_iam = /aws1/cl_iam_factory=>create( ao_session ).

    DATA lv_account_id TYPE string.
    lv_account_id = ao_session->get_account_id( ).

    " Generate unique names for test resources
    DATA(lv_uuid) = /awsex/cl_utils=>get_random_string( ).
    DATA lv_uuid_string TYPE string.
    lv_uuid_string = lv_uuid.
    av_schedule_group_name = |scd-test-grp-{ lv_uuid_string }|.
    av_schedule_name = |scd-test-sch-{ lv_uuid_string }|.

    " Create an SQS queue as a target
    DATA lv_queue_name TYPE /aws1/sqsstring.
    lv_queue_name = |scd-test-queue-{ lv_uuid_string }|.

    TRY.
        " Create SQS queue with tags for cleanup
        DATA(lo_tags) = NEW /aws1/cl_sqstagmap_w( ).
        lo_tags->add( iv_key = 'convert_test' iv_value = 'true' ).

        DATA(lo_create_result) = ao_sqs->createqueue(
          iv_queuename = lv_queue_name
          io_tags = lo_tags ).
        av_queue_url = lo_create_result->get_queueurl( ).

        " Get queue ARN
        DATA(lo_attrs) = ao_sqs->getqueueattributes(
          iv_queueurl = av_queue_url
          it_attributenames = VALUE /aws1/cl_sqsattrnamelist_w=>tt_attributenamelist(
            ( NEW /aws1/cl_sqsattrnamelist_w( 'QueueArn' ) ) ) ).

        av_queue_arn = lo_attrs->get_attributes( )->get_item( 'QueueArn' )->get_value( ).

        " Create IAM role for EventBridge Scheduler with permissions
        av_role_name = |scd-test-role-{ lv_uuid_string }|.

        " Assume role policy document for EventBridge Scheduler
        DATA(lv_assume_role_policy) = |\{| &&
          |"Version": "2012-10-17",| &&
          |"Statement": [| &&
          |\{| &&
          |"Effect": "Allow",| &&
          |"Principal": \{"Service": "scheduler.amazonaws.com"\},| &&
          |"Action": "sts:AssumeRole"| &&
          |\}]| &&
          |\}|.

        DATA(lo_role_result) = ao_iam->createrole(
          iv_rolename = av_role_name
          iv_assumerolepolicydocument = lv_assume_role_policy
          it_tags = VALUE /aws1/cl_iamtag=>tt_taglisttype(
            ( NEW /aws1/cl_iamtag( iv_key = 'convert_test' iv_value = 'true' ) ) ) ).
        av_role_arn = lo_role_result->get_role( )->get_arn( ).

        " Inline policy to allow sending messages to SQS
        DATA(lv_policy_document) = |\{| &&
          |"Version": "2012-10-17",| &&
          |"Statement": [| &&
          |\{| &&
          |"Effect": "Allow",| &&
          |"Action": "sqs:SendMessage",| &&
          |"Resource": "{ av_queue_arn }"| &&
          |\}]| &&
          |\}|.

        ao_iam->putrolepolicy(
          iv_rolename = av_role_name
          iv_policyname = 'SQSSendMessagePolicy'
          iv_policydocument = lv_policy_document ).

        " Wait for IAM role to propagate
        WAIT UP TO 10 SECONDS.

      CATCH /aws1/cx_rt_generic INTO DATA(lo_ex).
        cl_abap_unit_assert=>fail( msg = |Setup failed: { lo_ex->if_message~get_text( ) }| ).
    ENDTRY.

  ENDMETHOD.

  METHOD class_teardown.
    " Clean up resources
    TRY.
        " Delete IAM role policy first
        IF av_role_name IS NOT INITIAL.
          TRY.
              ao_iam->deleterolepolicy(
                iv_rolename = av_role_name
                iv_policyname = 'SQSSendMessagePolicy' ).
            CATCH /aws1/cx_iamnosuchentityex.
              " Already deleted
          ENDTRY.

          " Delete IAM role
          TRY.
              ao_iam->deleterole( iv_rolename = av_role_name ).
            CATCH /aws1/cx_iamnosuchentityex.
              " Already deleted
          ENDTRY.
        ENDIF.

        " Delete SQS queue
        IF av_queue_url IS NOT INITIAL.
          TRY.
              ao_sqs->deletequeue( iv_queueurl = av_queue_url ).
            CATCH /aws1/cx_rt_generic.
              " Queue already deleted or doesn't exist
          ENDTRY.
        ENDIF.

      CATCH /aws1/cx_rt_generic.
        " Best effort cleanup - ignore errors
    ENDTRY.
  ENDMETHOD.

  METHOD create_schedule_group.
    " Create a unique schedule group name for this test
    DATA(lv_uuid) = /awsex/cl_utils=>get_random_string( ).
    DATA lv_uuid_string TYPE string.
    lv_uuid_string = lv_uuid.
    DATA(lv_test_group_name) = |scd-csg-{ lv_uuid_string }|.

    " Create schedule group
    DATA(lv_schedule_group_arn) = ao_scd_actions->create_schedule_group(
      iv_name = lv_test_group_name ).

    " Assert that ARN is returned
    cl_abap_unit_assert=>assert_not_initial(
      act = lv_schedule_group_arn
      msg = |Schedule group ARN should not be initial| ).

    " Verify the schedule group was created by getting its details
    DATA(lo_get_result) = ao_scd->getschedulegroup( iv_name = lv_test_group_name ).
    cl_abap_unit_assert=>assert_equals(
      exp = lv_test_group_name
      act = lo_get_result->get_name( )
      msg = |Schedule group name should match| ).

    " Clean up
    ao_scd->deleteschedulegroup( iv_name = lv_test_group_name ).
  ENDMETHOD.

  METHOD create_schedule.
    " First create a schedule group
    DATA(lv_uuid) = /awsex/cl_utils=>get_random_string( ).
    DATA lv_uuid_string TYPE string.
    lv_uuid_string = lv_uuid.
    DATA(lv_test_group_name) = |scd-cs-grp-{ lv_uuid_string }|.
    DATA(lv_test_schedule_name) = |scd-cs-sch-{ lv_uuid_string }|.

    ao_scd->createschedulegroup( iv_name = lv_test_group_name ).

    " Wait for schedule group to be ready
    WAIT UP TO 2 SECONDS.

    " Create schedule
    DATA(lv_schedule_arn) = ao_scd_actions->create_schedule(
      iv_name = lv_test_schedule_name
      iv_schedule_expression = 'rate(30 minutes)'
      iv_schedule_group_name = lv_test_group_name
      iv_target_arn = av_queue_arn
      iv_role_arn = av_role_arn
      iv_input = '{"test": "message"}'
      iv_delete_after_completion = abap_false
      iv_use_flexible_time_win = abap_false ).

    " Assert that ARN is returned
    cl_abap_unit_assert=>assert_not_initial(
      act = lv_schedule_arn
      msg = |Schedule ARN should not be initial| ).

    " Verify the schedule was created
    DATA(lo_get_result) = ao_scd->getschedule(
      iv_name = lv_test_schedule_name
      iv_groupname = lv_test_group_name ).
    cl_abap_unit_assert=>assert_equals(
      exp = lv_test_schedule_name
      act = lo_get_result->get_name( )
      msg = |Schedule name should match| ).

    " Clean up
    ao_scd->deleteschedule(
      iv_name = lv_test_schedule_name
      iv_groupname = lv_test_group_name ).
    wait_for_schedule_deletion(
      iv_name = lv_test_schedule_name
      iv_schedule_group_name = lv_test_group_name ).
    ao_scd->deleteschedulegroup( iv_name = lv_test_group_name ).
  ENDMETHOD.

  METHOD delete_schedule.
    " First create a schedule group and schedule
    DATA(lv_uuid) = /awsex/cl_utils=>get_random_string( ).
    DATA lv_uuid_string TYPE string.
    lv_uuid_string = lv_uuid.
    DATA(lv_test_group_name) = |scd-ds-grp-{ lv_uuid_string }|.
    DATA(lv_test_schedule_name) = |scd-ds-sch-{ lv_uuid_string }|.

    ao_scd->createschedulegroup( iv_name = lv_test_group_name ).
    WAIT UP TO 2 SECONDS.

    " Create schedule
    DATA(lo_target) = NEW /aws1/cl_scdtarget(
      iv_arn = av_queue_arn
      iv_rolearn = av_role_arn
      iv_input = '{"test": "message"}' ).

    DATA(lo_flexible_window) = NEW /aws1/cl_scdflexibletimewindow(
      iv_mode = 'OFF' ).

    ao_scd->createschedule(
      iv_name = lv_test_schedule_name
      iv_scheduleexpression = 'rate(30 minutes)'
      iv_groupname = lv_test_group_name
      io_target = lo_target
      io_flexibletimewindow = lo_flexible_window ).

    WAIT UP TO 2 SECONDS.

    " Delete the schedule
    ao_scd_actions->delete_schedule(
      iv_name = lv_test_schedule_name
      iv_schedule_group_name = lv_test_group_name ).

    " Verify the schedule was deleted
    TRY.
        ao_scd->getschedule(
          iv_name = lv_test_schedule_name
          iv_groupname = lv_test_group_name ).
        cl_abap_unit_assert=>fail( msg = |Schedule should have been deleted| ).
      CATCH /aws1/cx_scdresourcenotfoundex.
        " Expected - schedule was deleted
    ENDTRY.

    " Clean up
    wait_for_schedule_deletion(
      iv_name = lv_test_schedule_name
      iv_schedule_group_name = lv_test_group_name ).
    ao_scd->deleteschedulegroup( iv_name = lv_test_group_name ).
  ENDMETHOD.

  METHOD delete_schedule_group.
    " Create a schedule group
    DATA(lv_uuid) = /awsex/cl_utils=>get_random_string( ).
    DATA lv_uuid_string TYPE string.
    lv_uuid_string = lv_uuid.
    DATA(lv_test_group_name) = |scd-dsg-{ lv_uuid_string }|.

    ao_scd->createschedulegroup( iv_name = lv_test_group_name ).
    WAIT UP TO 2 SECONDS.

    " Delete the schedule group
    ao_scd_actions->delete_schedule_group( iv_name = lv_test_group_name ).

    " Wait a bit for deletion to complete
    WAIT UP TO 2 SECONDS.

    " Verify the schedule group was deleted
    TRY.
        ao_scd->getschedulegroup( iv_name = lv_test_group_name ).
        cl_abap_unit_assert=>fail( msg = |Schedule group should have been deleted| ).
      CATCH /aws1/cx_scdresourcenotfoundex.
        " Expected - schedule group was deleted
    ENDTRY.
  ENDMETHOD.

  METHOD wait_for_schedule_deletion.
    " Wait for schedule deletion to complete (needed before deleting group)
    DATA lv_max_attempts TYPE i VALUE 10.
    DATA lv_attempts TYPE i VALUE 0.

    WHILE lv_attempts < lv_max_attempts.
      TRY.
          ao_scd->getschedule(
            iv_name = iv_name
            iv_groupname = iv_schedule_group_name ).
          " Schedule still exists, wait and try again
          WAIT UP TO 2 SECONDS.
          lv_attempts = lv_attempts + 1.
        CATCH /aws1/cx_scdresourcenotfoundex.
          " Schedule deleted successfully
          RETURN.
      ENDTRY.
    ENDWHILE.
  ENDMETHOD.

ENDCLASS.
