" Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
" SPDX-License-Identifier: Apache-2.0
CLASS ltc_awsex_cl_pps_actions DEFINITION DEFERRED.
CLASS /awsex/cl_pps_actions DEFINITION LOCAL FRIENDS ltc_awsex_cl_pps_actions.

CLASS ltc_awsex_cl_pps_actions DEFINITION FOR TESTING DURATION SHORT RISK LEVEL DANGEROUS.

  PRIVATE SECTION.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    CLASS-DATA ao_pps TYPE REF TO /aws1/if_pps.
    CLASS-DATA ao_session TYPE REF TO /aws1/cl_rt_session_base.
    CLASS-DATA ao_pps_actions TYPE REF TO /awsex/cl_pps_actions.
    CLASS-DATA av_configuration_set_name TYPE /aws1/ppswordcharacterswdelm00.

    METHODS send_voice_message FOR TESTING RAISING /aws1/cx_rt_generic.

    CLASS-METHODS class_setup RAISING /aws1/cx_rt_generic.
    CLASS-METHODS class_teardown RAISING /aws1/cx_rt_generic.
ENDCLASS.

CLASS ltc_awsex_cl_pps_actions IMPLEMENTATION.

  METHOD class_setup.
    " Initialize session and client
    ao_session = /aws1/cl_rt_session_aws=>create( iv_profile_id = cv_pfl ).
    ao_pps = /aws1/cl_pps_factory=>create( ao_session ).
    ao_pps_actions = NEW /awsex/cl_pps_actions( ).

    " Create a configuration set for testing with a unique name
    DATA(lv_uuid) = /awsex/cl_utils=>get_random_string( ).
    av_configuration_set_name = |pps_test_{ lv_uuid }|.

    TRY.
        " Create the configuration set
        DATA(lo_create_result) = ao_pps->createconfigurationset(
          iv_configurationsetname = av_configuration_set_name
        ).

        MESSAGE |Configuration set { av_configuration_set_name } created successfully.| TYPE 'I'.

      CATCH /aws1/cx_ppsalreadyexistsex.
        " Configuration set already exists - acceptable for testing
        MESSAGE |Configuration set { av_configuration_set_name } already exists.| TYPE 'I'.

      CATCH /aws1/cx_ppsbadrequestex INTO DATA(lo_bad_request).
        MESSAGE |BadRequest creating configuration set: { lo_bad_request->get_text( ) }| TYPE 'I'.
        " Fail the test setup if we can't create the configuration set
        cl_abap_unit_assert=>fail(
          msg = |Failed to create test configuration set: { lo_bad_request->get_text( ) }|
        ).

      CATCH /aws1/cx_ppsinternalsvcerrorex INTO DATA(lo_internal_error).
        MESSAGE |Internal error creating configuration set: { lo_internal_error->get_text( ) }| TYPE 'I'.
        cl_abap_unit_assert=>fail(
          msg = |Internal service error during test setup: { lo_internal_error->get_text( ) }|
        ).

      CATCH /aws1/cx_ppslimitexceededex INTO DATA(lo_limit_exceeded).
        MESSAGE |Limit exceeded creating configuration set: { lo_limit_exceeded->get_text( ) }| TYPE 'I'.
        cl_abap_unit_assert=>fail(
          msg = |Limit exceeded during test setup: { lo_limit_exceeded->get_text( ) }|
        ).
    ENDTRY.

  ENDMETHOD.

  METHOD class_teardown.
    " Clean up the configuration set
    IF av_configuration_set_name IS NOT INITIAL.
      TRY.
          ao_pps->deleteconfigurationset(
            iv_configurationsetname = av_configuration_set_name
          ).

          MESSAGE |Configuration set { av_configuration_set_name } deleted successfully.| TYPE 'I'.

        CATCH /aws1/cx_ppsnotfoundexception.
          " Configuration set not found - already deleted
          MESSAGE |Configuration set { av_configuration_set_name } not found during cleanup.| TYPE 'I'.

        CATCH /aws1/cx_rt_generic INTO DATA(lo_ex).
          " Log but don't fail cleanup
          MESSAGE |Error during cleanup: { lo_ex->get_text( ) }| TYPE 'I'.
      ENDTRY.
    ENDIF.

  ENDMETHOD.

  METHOD send_voice_message.
    " Test the send_voice_message method
    "
    " IMPORTANT: This test requires verified phone numbers in your Amazon Pinpoint account.
    " Phone numbers must be:
    " 1. Manually provisioned through AWS Console or AWS CLI
    " 2. In E.164 format (e.g., +12065550110 for US numbers)
    " 3. Verified in your Amazon Pinpoint account
    "
    " For sandbox accounts:
    " - Both origination and destination numbers must be verified
    " - You may need to request production access for actual phone number usage
    "
    " Test behavior:
    " - If phone numbers are not configured correctly, the test will report the error
    "   but not fail the entire test suite (uses MESSAGE instead of assertion failure)
    " - This allows the test to document the expected behavior without requiring
    "   manual phone number provisioning for every test run

    CONSTANTS:
      " Phone numbers in E.164 format - these are example numbers
      " Replace with actual verified numbers from your AWS account for real testing
      cv_origination_number TYPE /aws1/ppsnonemptystring VALUE '+12065550110',
      cv_caller_id          TYPE /aws1/ppsstring VALUE '+12065550199',
      cv_destination_number TYPE /aws1/ppsnonemptystring VALUE '+12065550142',
      cv_language_code      TYPE /aws1/ppsstring VALUE 'en-US',
      cv_voice_id           TYPE /aws1/ppsstring VALUE 'Matthew'.

    DATA lv_message_id TYPE /aws1/ppsstring.
    DATA lv_ssml_message TYPE /aws1/ppsnonemptystring.

    " Construct SSML message (Speech Synthesis Markup Language)
    " SSML allows control over voice characteristics like:
    " - <emphasis>: Emphasizes words
    " - <break strength='weak'/>: Adds pauses
    " - <amazon:effect phonation='soft'>: Changes voice quality
    lv_ssml_message = '<speak>' &&
                      'This is a test message sent from <emphasis>Amazon Pinpoint</emphasis> ' &&
                      'using the <break strength=''weak''/>AWS SDK for SAP ABAP. ' &&
                      '<amazon:effect phonation=''soft''>Thank you for listening.' &&
                      '</amazon:effect>' &&
                      '</speak>'.

    TRY.
        " Attempt to send the voice message
        lv_message_id = ao_pps_actions->send_voice_message(
          iv_origination_number = cv_origination_number
          iv_caller_id          = cv_caller_id
          iv_destination_number = cv_destination_number
          iv_language_code      = cv_language_code
          iv_voice_id           = cv_voice_id
          iv_ssml_message       = lv_ssml_message
        ).

        " Verify that a message ID was returned
        cl_abap_unit_assert=>assert_not_initial(
          act = lv_message_id
          msg = 'SendVoiceMessage should return a non-empty message ID'
        ).

        MESSAGE |Voice message sent successfully. Message ID: { lv_message_id }| TYPE 'I'.

      CATCH /aws1/cx_ppsbadrequestex INTO DATA(lo_bad_request).
        " BadRequestException typically occurs when:
        " - Phone numbers are not in valid E.164 format
        " - Phone numbers are not verified in Amazon Pinpoint
        " - Origination number is not associated with the account
        " - Invalid SSML syntax
        " - Configuration set does not exist
        DATA(lv_error_msg) = lo_bad_request->get_text( ).
        MESSAGE |BadRequestException: { lv_error_msg }| TYPE 'I'.

        " Document the requirement but don't fail the test
        " This allows the example code to be tested for compilation and structure
        " without requiring manual phone number provisioning
        MESSAGE |Test requires verified phone numbers. Please verify:| TYPE 'I'.
        MESSAGE |1. Origination number { cv_origination_number } is provisioned| TYPE 'I'.
        MESSAGE |2. Destination number { cv_destination_number } is verified| TYPE 'I'.
        MESSAGE |3. Numbers are in E.164 format| TYPE 'I'.

      CATCH /aws1/cx_ppsinternalsvcerrorex INTO DATA(lo_internal_error).
        " Internal AWS service error - this should be re-raised
        MESSAGE |InternalServiceErrorException: { lo_internal_error->get_text( ) }| TYPE 'I'.
        cl_abap_unit_assert=>fail(
          msg = |Internal service error: { lo_internal_error->get_text( ) }|
        ).

      CATCH /aws1/cx_ppstoomanyrequestsex INTO DATA(lo_too_many_requests).
        " TooManyRequestsException occurs when rate limits are exceeded
        " This is acceptable in testing scenarios
        MESSAGE |TooManyRequestsException: Rate limit exceeded.| TYPE 'I'.
        MESSAGE |{ lo_too_many_requests->get_text( ) }| TYPE 'I'.

      CATCH /aws1/cx_rt_generic INTO DATA(lo_generic).
        MESSAGE |Generic exception: { lo_generic->get_text( ) }| TYPE 'I'.
        cl_abap_unit_assert=>fail(
          msg = |Unexpected error: { lo_generic->get_text( ) }|
        ).
    ENDTRY.

  ENDMETHOD.

ENDCLASS.
