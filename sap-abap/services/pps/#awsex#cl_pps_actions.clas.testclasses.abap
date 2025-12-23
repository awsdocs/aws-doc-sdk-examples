" Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
" SPDX-License-Identifier: Apache-2.0
CLASS ltc_awsex_cl_pps_actions DEFINITION DEFERRED.
CLASS /awsex/cl_pps_actions DEFINITION LOCAL FRIENDS ltc_awsex_cl_pps_actions.

CLASS ltc_awsex_cl_pps_actions DEFINITION FOR TESTING DURATION SHORT RISK LEVEL DANGEROUS.

  PRIVATE SECTION.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA ao_pps TYPE REF TO /aws1/if_pps.
    DATA ao_session TYPE REF TO /aws1/cl_rt_session_base.
    DATA ao_pps_actions TYPE REF TO /awsex/cl_pps_actions.
    DATA ao_sns TYPE REF TO /aws1/if_sns.
    DATA ao_cwl TYPE REF TO /aws1/if_cwl.
    DATA ao_iam TYPE REF TO /aws1/if_iam.
    DATA av_configuration_set_name TYPE /aws1/ppswordcharacterswdelm00.
    DATA av_sns_topic_arn TYPE /aws1/snstopicarn.
    DATA av_log_group_name TYPE /aws1/cwlloggroupname.
    DATA av_log_group_arn TYPE /aws1/ppsstring.
    DATA av_iam_role_arn TYPE /aws1/iamarntype.
    DATA av_iam_role_name TYPE /aws1/iamrolenametype.

    METHODS send_voice_message FOR TESTING RAISING /aws1/cx_rt_generic.
    METHODS create_configuration_set FOR TESTING RAISING /aws1/cx_rt_generic.
    METHODS list_configuration_sets FOR TESTING RAISING /aws1/cx_rt_generic.
    METHODS delete_configuration_set FOR TESTING RAISING /aws1/cx_rt_generic.
    METHODS get_conf_set_event_dst FOR TESTING RAISING /aws1/cx_rt_generic.
    METHODS create_conf_set_evt_dst FOR TESTING RAISING /aws1/cx_rt_generic.
    METHODS update_conf_set_evt_dst FOR TESTING RAISING /aws1/cx_rt_generic.
    METHODS delete_conf_set_evt_dst FOR TESTING RAISING /aws1/cx_rt_generic.

    METHODS setup RAISING /aws1/cx_rt_generic.
    METHODS teardown RAISING /aws1/cx_rt_generic.
ENDCLASS.

CLASS ltc_awsex_cl_pps_actions IMPLEMENTATION.

  METHOD setup.
    " Initialize session and clients for each test
    ao_session = /aws1/cl_rt_session_aws=>create( iv_profile_id = cv_pfl ).
    ao_pps = /aws1/cl_pps_factory=>create( ao_session ).
    ao_pps_actions = NEW /awsex/cl_pps_actions( ).
    ao_sns = /aws1/cl_sns_factory=>create( ao_session ).
    ao_cwl = /aws1/cl_cwl_factory=>create( ao_session ).
    ao_iam = /aws1/cl_iam_factory=>create( ao_session ).

    " Create unique test resource names
    DATA(lv_uuid) = /awsex/cl_utils=>get_random_string( ).
    av_configuration_set_name = |pps-test-{ lv_uuid }|.
  ENDMETHOD.

  METHOD teardown.
    " Clean up resources created during the test
    IF av_configuration_set_name IS NOT INITIAL.
      TRY.
          ao_pps->deleteconfigurationset( iv_configurationsetname = av_configuration_set_name ).
        CATCH /aws1/cx_rt_generic.
          " Ignore cleanup errors
      ENDTRY.
    ENDIF.

    IF av_sns_topic_arn IS NOT INITIAL.
      TRY.
          ao_sns->deletetopic( iv_topicarn = av_sns_topic_arn ).
        CATCH /aws1/cx_rt_generic.
      ENDTRY.
    ENDIF.

    IF av_log_group_name IS NOT INITIAL.
      TRY.
          ao_cwl->deleteloggroup( iv_loggroupname = av_log_group_name ).
        CATCH /aws1/cx_rt_generic.
      ENDTRY.
    ENDIF.

    IF av_iam_role_name IS NOT INITIAL.
      TRY.
          DATA(lo_list_inline) = ao_iam->listrolepolicies( iv_rolename = av_iam_role_name ).
          LOOP AT lo_list_inline->get_policynames( ) INTO DATA(lo_policy_name_wrapper).
            DATA(lv_inline_policy_name) = lo_policy_name_wrapper->get_value( ).
            TRY.
                ao_iam->deleterolepolicy(
                  iv_rolename = av_iam_role_name
                  iv_policyname = lv_inline_policy_name ).
              CATCH /aws1/cx_rt_generic.
            ENDTRY.
          ENDLOOP.
          ao_iam->deleterole( iv_rolename = av_iam_role_name ).
        CATCH /aws1/cx_rt_generic.
      ENDTRY.
    ENDIF.
  ENDMETHOD.

  METHOD send_voice_message.
    " Test send_voice_message with example phone numbers
    CONSTANTS:
      cv_origination_number TYPE /aws1/ppsnonemptystring VALUE '+12065550110',
      cv_caller_id          TYPE /aws1/ppsstring VALUE '+12065550199',
      cv_destination_number TYPE /aws1/ppsnonemptystring VALUE '+12065550142',
      cv_language_code      TYPE /aws1/ppsstring VALUE 'en-US',
      cv_voice_id           TYPE /aws1/ppsstring VALUE 'Matthew'.

    DATA lv_message_id TYPE /aws1/ppsstring.
    DATA lv_ssml_message TYPE /aws1/ppsnonemptystring.
    DATA lv_test_passed TYPE abap_bool VALUE abap_false.

    lv_ssml_message = '<speak>This is a test message.</speak>'.

    TRY.
        lv_message_id = ao_pps_actions->send_voice_message(
          iv_origination_number = cv_origination_number
          iv_caller_id          = cv_caller_id
          iv_destination_number = cv_destination_number
          iv_language_code      = cv_language_code
          iv_voice_id           = cv_voice_id
          iv_ssml_message       = lv_ssml_message ).

        cl_abap_unit_assert=>assert_not_initial(
          act = lv_message_id
          msg = 'SendVoiceMessage should return a message ID' ).
        lv_test_passed = abap_true.

      CATCH /aws1/cx_ppsbadrequestex INTO DATA(lo_bad_request).
        DATA(lv_bad_req_msg) = lo_bad_request->get_text( ).
        IF lv_bad_req_msg CS 'phone' OR lv_bad_req_msg CS 'number'
          OR lv_bad_req_msg CS 'origination' OR lv_bad_req_msg CS 'destination'.
          MESSAGE |BadRequestException (expected): { lv_bad_req_msg }| TYPE 'I'.
          lv_test_passed = abap_true.
        ELSE.
          RAISE EXCEPTION lo_bad_request.
        ENDIF.

      CATCH /aws1/cx_ppstoomanyrequestsex.
        MESSAGE 'TooManyRequestsException - rate limit reached' TYPE 'I'.
        lv_test_passed = abap_true.
    ENDTRY.

    cl_abap_unit_assert=>assert_true(
      act = lv_test_passed
      msg = 'Test must pass via success or expected exception' ).
  ENDMETHOD.

  METHOD create_configuration_set.
    " Test create_configuration_set
    DATA(lv_uuid) = /awsex/cl_utils=>get_random_string( ).
    DATA(lv_config_set_name) = |pps-create-{ lv_uuid }|.

    TRY.
        ao_pps_actions->create_configuration_set( iv_configuration_set_name = lv_config_set_name ).

        DATA(lo_list_result) = ao_pps->listconfigurationsets( ).
        DATA lv_found TYPE abap_bool VALUE abap_false.

        LOOP AT lo_list_result->get_configurationsets( ) INTO DATA(lo_config_set).
          IF lo_config_set->get_value( ) = lv_config_set_name.
            lv_found = abap_true.
            EXIT.
          ENDIF.
        ENDLOOP.

        cl_abap_unit_assert=>assert_true(
          act = lv_found
          msg = |Configuration set { lv_config_set_name } should exist| ).

        ao_pps->deleteconfigurationset( iv_configurationsetname = lv_config_set_name ).

      CATCH /aws1/cx_rt_generic INTO DATA(lo_ex).
        TRY.
            ao_pps->deleteconfigurationset( iv_configurationsetname = lv_config_set_name ).
          CATCH /aws1/cx_rt_generic.
        ENDTRY.
        RAISE EXCEPTION lo_ex.
    ENDTRY.
  ENDMETHOD.

  METHOD list_configuration_sets.
    " Test list_configuration_sets
    TRY.
        " Create a configuration set first
        ao_pps->createconfigurationset( iv_configurationsetname = av_configuration_set_name ).

        DATA(lo_result) = ao_pps_actions->list_configuration_sets( ).

        cl_abap_unit_assert=>assert_bound(
          act = lo_result
          msg = 'List result should not be null' ).

        DATA lv_found TYPE abap_bool VALUE abap_false.
        LOOP AT lo_result->get_configurationsets( ) INTO DATA(lo_config_set).
          IF lo_config_set->get_value( ) = av_configuration_set_name.
            lv_found = abap_true.
            EXIT.
          ENDIF.
        ENDLOOP.

        cl_abap_unit_assert=>assert_true(
          act = lv_found
          msg = |Configuration set { av_configuration_set_name } should be in list| ).

      CATCH /aws1/cx_rt_generic INTO DATA(lo_ex).
        RAISE EXCEPTION lo_ex.
    ENDTRY.
  ENDMETHOD.

  METHOD delete_configuration_set.
    " Test delete_configuration_set
    DATA(lv_uuid) = /awsex/cl_utils=>get_random_string( ).
    DATA(lv_config_set_name) = |pps-del-{ lv_uuid }|.

    TRY.
        ao_pps->createconfigurationset( iv_configurationsetname = lv_config_set_name ).

        ao_pps_actions->delete_configuration_set( iv_configuration_set_name = lv_config_set_name ).

        TRY.
            ao_pps->getconfseteventdestinations( iv_configurationsetname = lv_config_set_name ).
            cl_abap_unit_assert=>fail(
              msg = |Configuration set { lv_config_set_name } should not exist after deletion| ).

          CATCH /aws1/cx_ppsnotfoundexception.
            MESSAGE |Configuration set successfully deleted| TYPE 'I'.
        ENDTRY.

      CATCH /aws1/cx_rt_generic INTO DATA(lo_ex).
        TRY.
            ao_pps->deleteconfigurationset( iv_configurationsetname = lv_config_set_name ).
          CATCH /aws1/cx_rt_generic.
        ENDTRY.
        RAISE EXCEPTION lo_ex.
    ENDTRY.
  ENDMETHOD.

  METHOD get_conf_set_event_dst.
    " Test get_conf_set_event_dst
    TRY.
        " Create a configuration set first
        ao_pps->createconfigurationset( iv_configurationsetname = av_configuration_set_name ).

        DATA(lo_result) = ao_pps_actions->get_conf_set_event_dst(
          iv_configuration_set_name = av_configuration_set_name ).

        cl_abap_unit_assert=>assert_bound(
          act = lo_result
          msg = 'Get event destinations result should not be null' ).

      CATCH /aws1/cx_rt_generic INTO DATA(lo_ex).
        RAISE EXCEPTION lo_ex.
    ENDTRY.
  ENDMETHOD.

  METHOD create_conf_set_evt_dst.
    " Test create_conf_set_event_dst
    TRY.
        " Create configuration set
        ao_pps->createconfigurationset( iv_configurationsetname = av_configuration_set_name ).

        " Create SNS topic
        DATA(lv_topic_uuid) = /awsex/cl_utils=>get_random_string( ).
        DATA(lv_topic_name) = |pps-test-{ lv_topic_uuid }|.
        DATA(lo_topic_result) = ao_sns->createtopic( iv_name = lv_topic_name ).
        av_sns_topic_arn = lo_topic_result->get_topicarn( ).

        " Create event destination
        DATA(lv_uuid) = /awsex/cl_utils=>get_random_string( ).
        DATA(lv_event_dest_name) = |evt-dest-{ lv_uuid }|.

        DATA lt_event_types TYPE /aws1/cl_ppseventtypes_w=>tt_eventtypes.
        APPEND NEW /aws1/cl_ppseventtypes_w( 'INITIATED_CALL' ) TO lt_event_types.
        APPEND NEW /aws1/cl_ppseventtypes_w( 'COMPLETED_CALL' ) TO lt_event_types.

        DATA(lo_sns_destination) = NEW /aws1/cl_ppssnsdestination( av_sns_topic_arn ).
        DATA(lo_event_dest_def) = NEW /aws1/cl_ppseventdstdefinition(
          io_snsdestination = lo_sns_destination
          it_matchingeventtypes = lt_event_types
          iv_enabled = abap_true ).

        ao_pps_actions->create_conf_set_event_dst(
          iv_configuration_set_name = av_configuration_set_name
          iv_event_destination_name = lv_event_dest_name
          io_event_destination = lo_event_dest_def ).

        DATA(lo_get_result) = ao_pps->getconfseteventdestinations(
          iv_configurationsetname = av_configuration_set_name ).

        DATA lv_found TYPE abap_bool VALUE abap_false.
        LOOP AT lo_get_result->get_eventdestinations( ) INTO DATA(lo_event_dest).
          IF lo_event_dest->get_name( ) = lv_event_dest_name.
            lv_found = abap_true.
            EXIT.
          ENDIF.
        ENDLOOP.

        cl_abap_unit_assert=>assert_true(
          act = lv_found
          msg = |Event destination { lv_event_dest_name } should exist| ).

        ao_pps->deleteconfseteventdst(
          iv_configurationsetname = av_configuration_set_name
          iv_eventdestinationname = lv_event_dest_name ).

      CATCH /aws1/cx_rt_generic INTO DATA(lo_ex).
        RAISE EXCEPTION lo_ex.
    ENDTRY.
  ENDMETHOD.

  METHOD update_conf_set_evt_dst.
    " Test update_conf_set_event_dst
    TRY.
        " Create configuration set
        ao_pps->createconfigurationset( iv_configurationsetname = av_configuration_set_name ).

        " Create SNS topic
        DATA(lv_topic_uuid) = /awsex/cl_utils=>get_random_string( ).
        DATA(lv_topic_name) = |pps-test-{ lv_topic_uuid }|.
        DATA(lo_topic_result) = ao_sns->createtopic( iv_name = lv_topic_name ).
        av_sns_topic_arn = lo_topic_result->get_topicarn( ).

        " Create event destination
        DATA(lv_uuid) = /awsex/cl_utils=>get_random_string( ).
        DATA(lv_event_dest_name) = |evt-upd-{ lv_uuid }|.

        DATA lt_event_types TYPE /aws1/cl_ppseventtypes_w=>tt_eventtypes.
        APPEND NEW /aws1/cl_ppseventtypes_w( 'INITIATED_CALL' ) TO lt_event_types.

        DATA(lo_sns_destination) = NEW /aws1/cl_ppssnsdestination( av_sns_topic_arn ).
        DATA(lo_event_dest_def) = NEW /aws1/cl_ppseventdstdefinition(
          io_snsdestination = lo_sns_destination
          it_matchingeventtypes = lt_event_types
          iv_enabled = abap_true ).

        ao_pps->createconfseteventdst(
          iv_configurationsetname = av_configuration_set_name
          iv_eventdestinationname = lv_event_dest_name
          io_eventdestination = lo_event_dest_def ).

        " Update it
        CLEAR lt_event_types.
        APPEND NEW /aws1/cl_ppseventtypes_w( 'COMPLETED_CALL' ) TO lt_event_types.

        DATA(lo_updated_dest_def) = NEW /aws1/cl_ppseventdstdefinition(
          io_snsdestination = lo_sns_destination
          it_matchingeventtypes = lt_event_types
          iv_enabled = abap_false ).

        ao_pps_actions->update_conf_set_event_dst(
          iv_configuration_set_name = av_configuration_set_name
          iv_event_destination_name = lv_event_dest_name
          io_event_destination = lo_updated_dest_def ).

        DATA(lo_get_result) = ao_pps->getconfseteventdestinations(
          iv_configurationsetname = av_configuration_set_name ).

        DATA lv_found TYPE abap_bool VALUE abap_false.
        LOOP AT lo_get_result->get_eventdestinations( ) INTO DATA(lo_event_dest).
          IF lo_event_dest->get_name( ) = lv_event_dest_name.
            lv_found = abap_true.
            cl_abap_unit_assert=>assert_equals(
              act = lo_event_dest->get_enabled( )
              exp = abap_false
              msg = 'Event destination should be disabled' ).
            EXIT.
          ENDIF.
        ENDLOOP.

        cl_abap_unit_assert=>assert_true(
          act = lv_found
          msg = |Event destination { lv_event_dest_name } should exist| ).

        ao_pps->deleteconfseteventdst(
          iv_configurationsetname = av_configuration_set_name
          iv_eventdestinationname = lv_event_dest_name ).

      CATCH /aws1/cx_rt_generic INTO DATA(lo_ex).
        RAISE EXCEPTION lo_ex.
    ENDTRY.
  ENDMETHOD.

  METHOD delete_conf_set_evt_dst.
    " Test delete_conf_set_event_dst
    TRY.
        " Create configuration set
        ao_pps->createconfigurationset( iv_configurationsetname = av_configuration_set_name ).

        " Create SNS topic
        DATA(lv_topic_uuid) = /awsex/cl_utils=>get_random_string( ).
        DATA(lv_topic_name) = |pps-test-{ lv_topic_uuid }|.
        DATA(lo_topic_result) = ao_sns->createtopic( iv_name = lv_topic_name ).
        av_sns_topic_arn = lo_topic_result->get_topicarn( ).

        " Create event destination
        DATA(lv_uuid) = /awsex/cl_utils=>get_random_string( ).
        DATA(lv_event_dest_name) = |evt-del-{ lv_uuid }|.

        DATA lt_event_types TYPE /aws1/cl_ppseventtypes_w=>tt_eventtypes.
        APPEND NEW /aws1/cl_ppseventtypes_w( 'INITIATED_CALL' ) TO lt_event_types.

        DATA(lo_sns_destination) = NEW /aws1/cl_ppssnsdestination( av_sns_topic_arn ).
        DATA(lo_event_dest_def) = NEW /aws1/cl_ppseventdstdefinition(
          io_snsdestination = lo_sns_destination
          it_matchingeventtypes = lt_event_types
          iv_enabled = abap_true ).

        ao_pps->createconfseteventdst(
          iv_configurationsetname = av_configuration_set_name
          iv_eventdestinationname = lv_event_dest_name
          io_eventdestination = lo_event_dest_def ).

        " Delete it
        ao_pps_actions->delete_conf_set_event_dst(
          iv_configuration_set_name = av_configuration_set_name
          iv_event_destination_name = lv_event_dest_name ).

        DATA(lo_get_result) = ao_pps->getconfseteventdestinations(
          iv_configurationsetname = av_configuration_set_name ).

        DATA lv_found TYPE abap_bool VALUE abap_false.
        LOOP AT lo_get_result->get_eventdestinations( ) INTO DATA(lo_event_dest).
          IF lo_event_dest->get_name( ) = lv_event_dest_name.
            lv_found = abap_true.
            EXIT.
          ENDIF.
        ENDLOOP.

        cl_abap_unit_assert=>assert_false(
          act = lv_found
          msg = |Event destination { lv_event_dest_name } should not exist| ).

      CATCH /aws1/cx_rt_generic INTO DATA(lo_ex).
        RAISE EXCEPTION lo_ex.
    ENDTRY.
  ENDMETHOD.

ENDCLASS.
