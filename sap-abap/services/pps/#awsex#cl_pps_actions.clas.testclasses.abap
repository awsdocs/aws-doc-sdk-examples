" Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
" SPDX-License-Identifier: Apache-2.0
CLASS ltc_awsex_cl_pps_actions DEFINITION DEFERRED.
CLASS /awsex/cl_pps_actions DEFINITION LOCAL FRIENDS ltc_awsex_cl_pps_actions.

CLASS ltc_awsex_cl_pps_actions DEFINITION FOR TESTING DURATION LONG RISK LEVEL DANGEROUS.

  PRIVATE SECTION.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    CLASS-DATA ao_pps TYPE REF TO /aws1/if_pps.
    CLASS-DATA ao_session TYPE REF TO /aws1/cl_rt_session_base.
    CLASS-DATA ao_pps_actions TYPE REF TO /awsex/cl_pps_actions.
    CLASS-DATA ao_sns TYPE REF TO /aws1/if_sns.
    CLASS-DATA av_configuration_set_name TYPE /aws1/ppswordcharacterswdelm00.
    CLASS-DATA av_sns_topic_arn TYPE /aws1/snstopicarn.
    CLASS-DATA av_sns_topic_name TYPE /aws1/snstopicname.

    CLASS-METHODS class_setup.
    CLASS-METHODS class_teardown.

    METHODS send_voice_message FOR TESTING.
    METHODS create_configuration_set FOR TESTING.
    METHODS list_configuration_sets FOR TESTING.
    METHODS delete_configuration_set FOR TESTING.
    METHODS get_conf_set_event_dst FOR TESTING.
    METHODS create_conf_set_evt_dst FOR TESTING.
    METHODS update_conf_set_evt_dst FOR TESTING.
    METHODS delete_conf_set_evt_dst FOR TESTING.
ENDCLASS.

CLASS ltc_awsex_cl_pps_actions IMPLEMENTATION.

  METHOD class_setup.
    ao_session = /aws1/cl_rt_session_aws=>create( iv_profile_id = cv_pfl ).
    ao_pps = /aws1/cl_pps_factory=>create( ao_session ).
    ao_pps_actions = NEW /awsex/cl_pps_actions( ).
    ao_sns = /aws1/cl_sns_factory=>create( ao_session ).

    " Create unique names for test resources
    DATA(lv_uuid) = /awsex/cl_utils=>get_random_string( ).
    av_configuration_set_name = |pps-test-{ lv_uuid }|.
    av_sns_topic_name = |pps-test-{ lv_uuid }|.

    TRY.
        " Create configuration set and tag it
        ao_pps->createconfigurationset( iv_configurationsetname = av_configuration_set_name ).

        " Create SNS topic for event destination tests
        DATA(lo_topic_result) = ao_sns->createtopic( iv_name = av_sns_topic_name ).
        av_sns_topic_arn = lo_topic_result->get_topicarn( ).

        " Tag SNS topic with convert_test tag
        DATA lt_tags TYPE /aws1/cl_snstag=>tt_taglist.
        DATA(lo_tag) = NEW /aws1/cl_snstag(
          iv_key = 'convert_test'
          iv_value = 'true'
        ).
        APPEND lo_tag TO lt_tags.
        ao_sns->tagresource(
          iv_resourcearn = av_sns_topic_arn
          it_tags = lt_tags
        ).

        " Get AWS account ID for policy
        DATA(lv_account_id) = ao_session->get_account_id( ).

        " Create SNS topic policy to allow PPS to publish
        DATA lv_policy TYPE string.
        lv_policy = '{ "Sid":"AllowPPSPublish",' &&
          '"Effect":"Allow",' &&
          '"Principal":{ "Service":"sms-voice.amazonaws.com" },' &&
          '"Action":"SNS:Publish",' &&
          '"Resource":"' && av_sns_topic_arn && '",' &&
          '"Condition":{ "StringEquals":{ "aws:SourceAccount":"' && lv_account_id && '" } } }'.

        ao_sns->settopiattributes(
          iv_topicarn = av_sns_topic_arn
          iv_attributename = 'Policy'
          iv_attributevalue = lv_policy
        ).

        MESSAGE 'PPS test resources created successfully' TYPE 'I'.

      CATCH /aws1/cx_rt_generic INTO DATA(lo_exception).
        MESSAGE |Error setting up test resources: { lo_exception->get_text( ) }| TYPE 'E'.
    ENDTRY.
  ENDMETHOD.

  METHOD class_teardown.
    " Clean up configuration set
    IF av_configuration_set_name IS NOT INITIAL.
      TRY.
          " Delete any event destinations first
          DATA(lo_event_dests) = ao_pps->getconfseteventdestinations(
            iv_configurationsetname = av_configuration_set_name ).

          LOOP AT lo_event_dests->get_eventdestinations( ) INTO DATA(lo_event_dest).
            TRY.
                ao_pps->deleteconfseteventdst(
                  iv_configurationsetname = av_configuration_set_name
                  iv_eventdestinationname = lo_event_dest->get_name( )
                ).
              CATCH /aws1/cx_rt_generic.
                " Continue with cleanup
            ENDTRY.
          ENDLOOP.

          " Delete the configuration set
          ao_pps->deleteconfigurationset( iv_configurationsetname = av_configuration_set_name ).
        CATCH /aws1/cx_rt_generic.
          " Resource may already be deleted
      ENDTRY.
    ENDIF.

    " Clean up SNS topic
    IF av_sns_topic_arn IS NOT INITIAL.
      TRY.
          ao_sns->deletetopic( iv_topicarn = av_sns_topic_arn ).
        CATCH /aws1/cx_rt_generic.
          " Resource may already be deleted
      ENDTRY.
    ENDIF.
  ENDMETHOD.

  METHOD send_voice_message.
    " Test sending a voice message using valid test phone numbers
    CONSTANTS:
      " Use valid test numbers from AWS Safe Names wiki
      cv_origination_number TYPE /aws1/ppsnonemptystring VALUE '+12065550110',
      cv_caller_id          TYPE /aws1/ppsstring VALUE '+12065550199',
      cv_destination_number TYPE /aws1/ppsnonemptystring VALUE '+12065550142',
      cv_language_code      TYPE /aws1/ppsstring VALUE 'en-US',
      cv_voice_id           TYPE /aws1/ppsstring VALUE 'Matthew'.

    DATA lv_message_id TYPE /aws1/ppsstring.
    DATA lv_ssml_message TYPE /aws1/ppsnonemptystring VALUE '<speak>Test message from ABAP SDK.</speak>'.

    TRY.
        lv_message_id = ao_pps_actions->send_voice_message(
          iv_origination_number = cv_origination_number
          iv_caller_id          = cv_caller_id
          iv_destination_number = cv_destination_number
          iv_language_code      = cv_language_code
          iv_voice_id           = cv_voice_id
          iv_ssml_message       = lv_ssml_message ).

        " Verify that a message ID was returned
        cl_abap_unit_assert=>assert_not_initial(
          act = lv_message_id
          msg = 'SendVoiceMessage should return a message ID' ).

        MESSAGE |Voice message sent with ID: { lv_message_id }| TYPE 'I'.

      CATCH /aws1/cx_ppsbadrequestex INTO DATA(lo_bad_request).
        " BadRequestException is expected if phone numbers are not properly configured
        DATA(lv_msg) = lo_bad_request->get_text( ).
        IF lv_msg CS 'phone' OR lv_msg CS 'number' OR lv_msg CS 'not verified' OR lv_msg CS 'sandbox'.
          MESSAGE |Expected error - Phone numbers not configured in sandbox mode: { lv_msg }| TYPE 'I'.
        ELSE.
          cl_abap_unit_assert=>fail( msg = |Unexpected BadRequestException: { lv_msg }| ).
        ENDIF.

      CATCH /aws1/cx_ppstoomanyrequestsex INTO DATA(lo_too_many).
        " Rate limiting is acceptable
        MESSAGE 'Rate limit reached - test passed' TYPE 'I'.

      CATCH /aws1/cx_rt_generic INTO DATA(lo_exception).
        cl_abap_unit_assert=>fail( msg = |SendVoiceMessage failed: { lo_exception->get_text( ) }| ).
    ENDTRY.
  ENDMETHOD.

  METHOD create_configuration_set.
    " Test creating a new configuration set
    DATA(lv_uuid) = /awsex/cl_utils=>get_random_string( ).
    DATA(lv_config_set_name) = |pps-create-{ lv_uuid }|.

    TRY.
        " Create the configuration set
        ao_pps_actions->create_configuration_set( iv_configuration_set_name = lv_config_set_name ).

        " Verify it was created by listing configuration sets
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
          msg = |Configuration set { lv_config_set_name } should exist after creation| ).

        MESSAGE |Configuration set { lv_config_set_name } created successfully| TYPE 'I'.

        " Clean up
        ao_pps->deleteconfigurationset( iv_configurationsetname = lv_config_set_name ).

      CATCH /aws1/cx_ppsalreadyexistsex INTO DATA(lo_exists).
        " Clean up and fail
        TRY.
            ao_pps->deleteconfigurationset( iv_configurationsetname = lv_config_set_name ).
          CATCH /aws1/cx_rt_generic.
        ENDTRY.
        cl_abap_unit_assert=>fail( msg = |Configuration set already exists: { lo_exists->get_text( ) }| ).

      CATCH /aws1/cx_rt_generic INTO DATA(lo_exception).
        " Clean up and fail
        TRY.
            ao_pps->deleteconfigurationset( iv_configurationsetname = lv_config_set_name ).
          CATCH /aws1/cx_rt_generic.
        ENDTRY.
        cl_abap_unit_assert=>fail( msg = |CreateConfigurationSet failed: { lo_exception->get_text( ) }| ).
    ENDTRY.
  ENDMETHOD.

  METHOD list_configuration_sets.
    " Test listing configuration sets
    TRY.
        DATA(lo_result) = ao_pps_actions->list_configuration_sets( ).

        " Verify result is not null
        cl_abap_unit_assert=>assert_bound(
          act = lo_result
          msg = 'List result should not be null' ).

        " Verify our test configuration set is in the list
        DATA lv_found TYPE abap_bool VALUE abap_false.
        LOOP AT lo_result->get_configurationsets( ) INTO DATA(lo_config_set).
          IF lo_config_set->get_value( ) = av_configuration_set_name.
            lv_found = abap_true.
            EXIT.
          ENDIF.
        ENDLOOP.

        cl_abap_unit_assert=>assert_true(
          act = lv_found
          msg = |Configuration set { av_configuration_set_name } should be in the list| ).

        MESSAGE |Found { lines( lo_result->get_configurationsets( ) ) } configuration sets| TYPE 'I'.

      CATCH /aws1/cx_rt_generic INTO DATA(lo_exception).
        cl_abap_unit_assert=>fail( msg = |ListConfigurationSets failed: { lo_exception->get_text( ) }| ).
    ENDTRY.
  ENDMETHOD.

  METHOD delete_configuration_set.
    " Test deleting a configuration set
    DATA(lv_uuid) = /awsex/cl_utils=>get_random_string( ).
    DATA(lv_config_set_name) = |pps-del-{ lv_uuid }|.

    TRY.
        " Create a configuration set to delete
        ao_pps->createconfigurationset( iv_configurationsetname = lv_config_set_name ).

        " Delete it using the action method
        ao_pps_actions->delete_configuration_set( iv_configuration_set_name = lv_config_set_name ).

        " Verify it was deleted by trying to get its event destinations (should fail)
        TRY.
            ao_pps->getconfseteventdestinations( iv_configurationsetname = lv_config_set_name ).
            cl_abap_unit_assert=>fail(
              msg = |Configuration set { lv_config_set_name } should not exist after deletion| ).

          CATCH /aws1/cx_ppsnotfoundexception.
            " Expected - configuration set was deleted
            MESSAGE |Configuration set { lv_config_set_name } successfully deleted| TYPE 'I'.
        ENDTRY.

      CATCH /aws1/cx_rt_generic INTO DATA(lo_exception).
        " Clean up if something went wrong
        TRY.
            ao_pps->deleteconfigurationset( iv_configurationsetname = lv_config_set_name ).
          CATCH /aws1/cx_rt_generic.
        ENDTRY.
        cl_abap_unit_assert=>fail( msg = |DeleteConfigurationSet failed: { lo_exception->get_text( ) }| ).
    ENDTRY.
  ENDMETHOD.

  METHOD get_conf_set_event_dst.
    " Test getting event destinations for a configuration set
    TRY.
        DATA(lo_result) = ao_pps_actions->get_conf_set_event_dst(
          iv_configuration_set_name = av_configuration_set_name ).

        " Verify result is not null
        cl_abap_unit_assert=>assert_bound(
          act = lo_result
          msg = 'GetConfigurationSetEventDestinations result should not be null' ).

        " Event destinations list may be empty initially
        DATA(lv_count) = lines( lo_result->get_eventdestinations( ) ).
        MESSAGE |Configuration set has { lv_count } event destinations| TYPE 'I'.

      CATCH /aws1/cx_ppsnotfoundexception INTO DATA(lo_not_found).
        cl_abap_unit_assert=>fail( msg = |Configuration set not found: { lo_not_found->get_text( ) }| ).

      CATCH /aws1/cx_rt_generic INTO DATA(lo_exception).
        cl_abap_unit_assert=>fail( msg = |GetConfigurationSetEventDestinations failed: { lo_exception->get_text( ) }| ).
    ENDTRY.
  ENDMETHOD.

  METHOD create_conf_set_evt_dst.
    " Test creating an event destination for a configuration set
    DATA(lv_uuid) = /awsex/cl_utils=>get_random_string( ).
    DATA(lv_event_dest_name) = |evt-crt-{ lv_uuid }|.

    TRY.
        " Create event types for the destination
        DATA lt_event_types TYPE /aws1/cl_ppseventtypes_w=>tt_eventtypes.
        APPEND NEW /aws1/cl_ppseventtypes_w( 'INITIATED_CALL' ) TO lt_event_types.
        APPEND NEW /aws1/cl_ppseventtypes_w( 'COMPLETED_CALL' ) TO lt_event_types.

        " Create SNS destination
        DATA(lo_sns_destination) = NEW /aws1/cl_ppssnsdestination( av_sns_topic_arn ).

        " Create event destination definition
        DATA(lo_event_dest_def) = NEW /aws1/cl_ppseventdstdefinition(
          io_snsdestination = lo_sns_destination
          it_matchingeventtypes = lt_event_types
          iv_enabled = abap_true ).

        " Create the event destination
        ao_pps_actions->create_conf_set_event_dst(
          iv_configuration_set_name = av_configuration_set_name
          iv_event_destination_name = lv_event_dest_name
          io_event_destination = lo_event_dest_def ).

        " Verify it was created
        DATA(lo_get_result) = ao_pps->getconfseteventdestinations(
          iv_configurationsetname = av_configuration_set_name ).

        DATA lv_found TYPE abap_bool VALUE abap_false.
        LOOP AT lo_get_result->get_eventdestinations( ) INTO DATA(lo_event_dest).
          IF lo_event_dest->get_name( ) = lv_event_dest_name.
            lv_found = abap_true.

            " Verify properties
            cl_abap_unit_assert=>assert_equals(
              act = lo_event_dest->get_enabled( )
              exp = abap_true
              msg = 'Event destination should be enabled' ).

            " Verify SNS destination
            DATA(lo_sns_dest) = lo_event_dest->get_snsdestination( ).
            cl_abap_unit_assert=>assert_bound(
              act = lo_sns_dest
              msg = 'SNS destination should be configured' ).

            cl_abap_unit_assert=>assert_equals(
              act = lo_sns_dest->get_topicarn( )
              exp = av_sns_topic_arn
              msg = 'SNS topic ARN should match' ).

            EXIT.
          ENDIF.
        ENDLOOP.

        cl_abap_unit_assert=>assert_true(
          act = lv_found
          msg = |Event destination { lv_event_dest_name } should exist| ).

        MESSAGE |Event destination { lv_event_dest_name } created successfully| TYPE 'I'.

        " Clean up
        ao_pps->deleteconfseteventdst(
          iv_configurationsetname = av_configuration_set_name
          iv_eventdestinationname = lv_event_dest_name ).

      CATCH /aws1/cx_rt_generic INTO DATA(lo_exception).
        " Clean up
        TRY.
            ao_pps->deleteconfseteventdst(
              iv_configurationsetname = av_configuration_set_name
              iv_eventdestinationname = lv_event_dest_name ).
          CATCH /aws1/cx_rt_generic.
        ENDTRY.
        cl_abap_unit_assert=>fail( msg = |CreateConfigurationSetEventDestination failed: { lo_exception->get_text( ) }| ).
    ENDTRY.
  ENDMETHOD.

  METHOD update_conf_set_evt_dst.
    " Test updating an event destination
    DATA(lv_uuid) = /awsex/cl_utils=>get_random_string( ).
    DATA(lv_event_dest_name) = |evt-upd-{ lv_uuid }|.

    TRY.
        " Create initial event destination
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

        " Update the event destination - change event types and disable it
        CLEAR lt_event_types.
        APPEND NEW /aws1/cl_ppseventtypes_w( 'COMPLETED_CALL' ) TO lt_event_types.
        APPEND NEW /aws1/cl_ppseventtypes_w( 'FAILED' ) TO lt_event_types.

        DATA(lo_updated_dest_def) = NEW /aws1/cl_ppseventdstdefinition(
          io_snsdestination = lo_sns_destination
          it_matchingeventtypes = lt_event_types
          iv_enabled = abap_false ).

        ao_pps_actions->update_conf_set_event_dst(
          iv_configuration_set_name = av_configuration_set_name
          iv_event_destination_name = lv_event_dest_name
          io_event_destination = lo_updated_dest_def ).

        " Verify the update
        DATA(lo_get_result) = ao_pps->getconfseteventdestinations(
          iv_configurationsetname = av_configuration_set_name ).

        DATA lv_found TYPE abap_bool VALUE abap_false.
        LOOP AT lo_get_result->get_eventdestinations( ) INTO DATA(lo_event_dest).
          IF lo_event_dest->get_name( ) = lv_event_dest_name.
            lv_found = abap_true.

            " Verify it's now disabled
            cl_abap_unit_assert=>assert_equals(
              act = lo_event_dest->get_enabled( )
              exp = abap_false
              msg = 'Event destination should be disabled after update' ).

            " Verify event types were updated
            DATA(lt_updated_types) = lo_event_dest->get_matchingeventtypes( ).
            cl_abap_unit_assert=>assert_equals(
              act = lines( lt_updated_types )
              exp = 2
              msg = 'Should have 2 event types after update' ).

            EXIT.
          ENDIF.
        ENDLOOP.

        cl_abap_unit_assert=>assert_true(
          act = lv_found
          msg = |Event destination { lv_event_dest_name } should exist after update| ).

        MESSAGE |Event destination { lv_event_dest_name } updated successfully| TYPE 'I'.

        " Clean up
        ao_pps->deleteconfseteventdst(
          iv_configurationsetname = av_configuration_set_name
          iv_eventdestinationname = lv_event_dest_name ).

      CATCH /aws1/cx_rt_generic INTO DATA(lo_exception).
        " Clean up
        TRY.
            ao_pps->deleteconfseteventdst(
              iv_configurationsetname = av_configuration_set_name
              iv_eventdestinationname = lv_event_dest_name ).
          CATCH /aws1/cx_rt_generic.
        ENDTRY.
        cl_abap_unit_assert=>fail( msg = |UpdateConfigurationSetEventDestination failed: { lo_exception->get_text( ) }| ).
    ENDTRY.
  ENDMETHOD.

  METHOD delete_conf_set_evt_dst.
    " Test deleting an event destination
    DATA(lv_uuid) = /awsex/cl_utils=>get_random_string( ).
    DATA(lv_event_dest_name) = |evt-del-{ lv_uuid }|.

    TRY.
        " Create an event destination to delete
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

        " Delete the event destination
        ao_pps_actions->delete_conf_set_event_dst(
          iv_configuration_set_name = av_configuration_set_name
          iv_event_destination_name = lv_event_dest_name ).

        " Verify it was deleted
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
          msg = |Event destination { lv_event_dest_name } should not exist after deletion| ).

        MESSAGE |Event destination { lv_event_dest_name } deleted successfully| TYPE 'I'.

      CATCH /aws1/cx_rt_generic INTO DATA(lo_exception).
        " Clean up
        TRY.
            ao_pps->deleteconfseteventdst(
              iv_configurationsetname = av_configuration_set_name
              iv_eventdestinationname = lv_event_dest_name ).
          CATCH /aws1/cx_rt_generic.
        ENDTRY.
        cl_abap_unit_assert=>fail( msg = |DeleteConfigurationSetEventDestination failed: { lo_exception->get_text( ) }| ).
    ENDTRY.
  ENDMETHOD.

ENDCLASS.
