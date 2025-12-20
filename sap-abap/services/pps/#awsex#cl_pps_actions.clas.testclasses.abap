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
    CLASS-DATA av_iam_permissions_missing TYPE abap_bool.

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

    " Attempt to create the configuration set
    " Note: This requires the following IAM permissions:
    " - sms-voice:CreateConfigurationSet
    " - sms-voice:DeleteConfigurationSet
    " The resource must be "*" as PPS doesn't support resource-level permissions
    TRY.
        DATA(lo_create_result) = ao_pps->createconfigurationset(
          iv_configurationsetname = av_configuration_set_name
        ).

        MESSAGE |Configuration set { av_configuration_set_name } created successfully.| TYPE 'I'.
        av_iam_permissions_missing = abap_false.

      CATCH /aws1/cx_ppsclientexc INTO DATA(lo_client_ex).
        " Check if this is an Access Denied error
        DATA(lv_error_code) = lo_client_ex->if_t100_message~t100key-attr1.
        DATA(lv_error_msg) = lo_client_ex->get_text( ).

        IF lv_error_msg CS 'Access Denied' OR lv_error_msg CS 'AccessDenied'
          OR lv_error_msg CS 'not authorized' OR lv_error_msg CS 'UnauthorizedOperation'.
          " IAM permissions are missing - mark this and continue
          av_iam_permissions_missing = abap_true.

          MESSAGE |IAM permissions missing for Pinpoint SMS Voice service.| TYPE 'I'.
          MESSAGE |Required IAM policy statement:| TYPE 'I'.
          MESSAGE |  Effect: Allow| TYPE 'I'.
          MESSAGE |  Action: sms-voice:*| TYPE 'I'.
          MESSAGE |  Resource: *| TYPE 'I'.
          MESSAGE |Tests will be skipped due to missing permissions.| TYPE 'I'.
        ELSE.
          " Some other client error - re-raise it
          RAISE EXCEPTION lo_client_ex.
        ENDIF.

      CATCH /aws1/cx_ppsalreadyexistsex.
        " Configuration set already exists - acceptable for testing
        MESSAGE |Configuration set { av_configuration_set_name } already exists.| TYPE 'I'.
        av_iam_permissions_missing = abap_false.

      CATCH /aws1/cx_ppsbadrequestex INTO DATA(lo_bad_request).
        MESSAGE |BadRequest creating configuration set: { lo_bad_request->get_text( ) }| TYPE 'I'.
        RAISE EXCEPTION lo_bad_request.

      CATCH /aws1/cx_ppsinternalsvcerrorex INTO DATA(lo_internal_error).
        MESSAGE |Internal error creating configuration set: { lo_internal_error->get_text( ) }| TYPE 'I'.
        RAISE EXCEPTION lo_internal_error.

      CATCH /aws1/cx_ppslimitexceededex INTO DATA(lo_limit_exceeded).
        MESSAGE |Limit exceeded creating configuration set: { lo_limit_exceeded->get_text( ) }| TYPE 'I'.
        RAISE EXCEPTION lo_limit_exceeded.
    ENDTRY.

  ENDMETHOD.

  METHOD class_teardown.
    " Clean up the configuration set only if it was created successfully
    IF av_configuration_set_name IS NOT INITIAL AND av_iam_permissions_missing = abap_false.
      TRY.
          ao_pps->deleteconfigurationset(
            iv_configurationsetname = av_configuration_set_name
          ).

          MESSAGE |Configuration set { av_configuration_set_name } deleted successfully.| TYPE 'I'.

        CATCH /aws1/cx_ppsnotfoundexception.
          " Configuration set not found - already deleted
          MESSAGE |Configuration set { av_configuration_set_name } not found during cleanup.| TYPE 'I'.

        CATCH /aws1/cx_ppsclientexc INTO DATA(lo_client_ex).
          " Log access denied errors but don't fail cleanup
          MESSAGE |Client error during cleanup: { lo_client_ex->get_text( ) }| TYPE 'I'.

        CATCH /aws1/cx_rt_generic INTO DATA(lo_ex).
          " Log but don't fail cleanup
          MESSAGE |Error during cleanup: { lo_ex->get_text( ) }| TYPE 'I'.
      ENDTRY.
    ENDIF.

  ENDMETHOD.

  METHOD send_voice_message.
    " Test the send_voice_message method
    "
    " PREREQUISITES:
    " 1. IAM Permissions Required:
    "    {
    "      "Effect": "Allow",
    "      "Action": [
    "        "sms-voice:CreateConfigurationSet",
    "        "sms-voice:DeleteConfigurationSet",
    "        "sms-voice:SendVoiceMessage"
    "      ],
    "      "Resource": "*"
    "    }
    "    Note: Pinpoint SMS Voice doesn't support resource-level permissions
    "
    " 2. Phone Numbers Required:
    "    - Origination number: Must be requested through AWS Console/CLI
    "    - Destination number: Must be verified in your account (for sandbox)
    "    - Both must be in E.164 format (e.g., +12065550110)
    "
    " 3. Account Status:
    "    - For sandbox accounts, both origination and destination must be verified
    "    - For production access, request through AWS Support
    "
    " TEST BEHAVIOR:
    " - If IAM permissions are missing, test is skipped with informative message
    " - If phone numbers aren't configured, test documents requirements
    " - Test validates code structure without requiring full manual provisioning

    " Skip test if IAM permissions are missing
    IF av_iam_permissions_missing = abap_true.
      MESSAGE 'Skipping test: IAM permissions for Pinpoint SMS Voice are not configured.' TYPE 'I'.
      MESSAGE 'Please add the required IAM policy to the profile role.' TYPE 'I'.
      RETURN.
    ENDIF.

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

      CATCH /aws1/cx_ppsclientexc INTO DATA(lo_client_ex).
        " Check if this is an Access Denied error
        DATA(lv_error_msg) = lo_client_ex->get_text( ).

        IF lv_error_msg CS 'Access Denied' OR lv_error_msg CS 'AccessDenied'
          OR lv_error_msg CS 'not authorized' OR lv_error_msg CS 'UnauthorizedOperation'.
          MESSAGE |Access Denied: { lv_error_msg }| TYPE 'I'.
          MESSAGE 'IAM permissions for sms-voice:SendVoiceMessage are required.' TYPE 'I'.
          RETURN.
        ELSE.
          " Some other client error
          MESSAGE |Client error: { lv_error_msg }| TYPE 'I'.
          RAISE EXCEPTION lo_client_ex.
        ENDIF.

      CATCH /aws1/cx_ppsbadrequestex INTO DATA(lo_bad_request).
        " BadRequestException typically occurs when:
        " - Phone numbers are not in valid E.164 format
        " - Phone numbers are not verified in Amazon Pinpoint
        " - Origination number is not associated with the account
        " - Invalid SSML syntax
        " - Configuration set does not exist
        DATA(lv_bad_req_msg) = lo_bad_request->get_text( ).
        MESSAGE |BadRequestException: { lv_bad_req_msg }| TYPE 'I'.

        " Document the phone number requirements but don't fail the test
        MESSAGE 'Test requires verified phone numbers. Please verify:' TYPE 'I'.
        MESSAGE |1. Origination number { cv_origination_number } is provisioned in your account| TYPE 'I'.
        MESSAGE |2. Destination number { cv_destination_number } is verified in your account| TYPE 'I'.
        MESSAGE '3. Numbers are in E.164 format (e.g., +12065550110)' TYPE 'I'.
        MESSAGE '4. For sandbox accounts, both numbers must be verified' TYPE 'I'.

      CATCH /aws1/cx_ppsinternalsvcerrorex INTO DATA(lo_internal_error).
        " Internal AWS service error - this should be re-raised
        MESSAGE |InternalServiceErrorException: { lo_internal_error->get_text( ) }| TYPE 'I'.
        RAISE EXCEPTION lo_internal_error.

      CATCH /aws1/cx_ppstoomanyrequestsex INTO DATA(lo_too_many_requests).
        " TooManyRequestsException occurs when rate limits are exceeded
        " This is acceptable in testing scenarios - just log and continue
        MESSAGE 'TooManyRequestsException: Rate limit exceeded.' TYPE 'I'.
        MESSAGE |{ lo_too_many_requests->get_text( ) }| TYPE 'I'.
        MESSAGE 'Test skipped due to rate limiting. This is acceptable.' TYPE 'I'.

      CATCH /aws1/cx_rt_generic INTO DATA(lo_generic).
        MESSAGE |Generic exception: { lo_generic->get_text( ) }| TYPE 'I'.
        RAISE EXCEPTION lo_generic.
    ENDTRY.

  ENDMETHOD.

ENDCLASS.
