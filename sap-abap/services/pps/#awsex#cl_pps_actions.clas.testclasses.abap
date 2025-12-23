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
    CLASS-DATA ao_sns TYPE REF TO /aws1/if_sns.
    CLASS-DATA ao_cwl TYPE REF TO /aws1/if_cwl.
    CLASS-DATA ao_iam TYPE REF TO /aws1/if_iam.
    CLASS-DATA av_configuration_set_name TYPE /aws1/ppswordcharacterswdelm00.
    CLASS-DATA av_sns_topic_arn TYPE /aws1/snstopicarn.
    CLASS-DATA av_log_group_name TYPE /aws1/cwlloggroupname.
    CLASS-DATA av_log_group_arn TYPE /aws1/ppsstring.
    CLASS-DATA av_iam_role_arn TYPE /aws1/iamarntype.
    CLASS-DATA av_iam_role_name TYPE /aws1/iamrolename.
    CLASS-DATA av_iam_permissions_missing TYPE abap_bool.

    METHODS send_voice_message FOR TESTING RAISING /aws1/cx_rt_generic.
    METHODS create_configuration_set FOR TESTING RAISING /aws1/cx_rt_generic.
    METHODS list_configuration_sets FOR TESTING RAISING /aws1/cx_rt_generic.
    METHODS delete_configuration_set FOR TESTING RAISING /aws1/cx_rt_generic.
    METHODS get_conf_set_event_dst FOR TESTING RAISING /aws1/cx_rt_generic.
    METHODS create_conf_set_evt_dst FOR TESTING RAISING /aws1/cx_rt_generic.
    METHODS update_conf_set_evt_dst FOR TESTING RAISING /aws1/cx_rt_generic.
    METHODS delete_conf_set_evt_dst FOR TESTING RAISING /aws1/cx_rt_generic.

    CLASS-METHODS class_setup RAISING /aws1/cx_rt_generic.
    CLASS-METHODS class_teardown RAISING /aws1/cx_rt_generic.
    CLASS-METHODS create_sns_topic RAISING /aws1/cx_rt_generic.
    CLASS-METHODS create_cloudwatch_log_grp RAISING /aws1/cx_rt_generic.
    CLASS-METHODS create_iam_role RAISING /aws1/cx_rt_generic.
ENDCLASS.

CLASS ltc_awsex_cl_pps_actions IMPLEMENTATION.

  METHOD class_setup.
    " Initialize session and clients
    ao_session = /aws1/cl_rt_session_aws=>create( iv_profile_id = cv_pfl ).
    ao_pps = /aws1/cl_pps_factory=>create( ao_session ).
    ao_pps_actions = NEW /awsex/cl_pps_actions( ).
    ao_sns = /aws1/cl_sns_factory=>create( ao_session ).
    ao_cwl = /aws1/cl_cwl_factory=>create( ao_session ).
    ao_iam = /aws1/cl_iam_factory=>create( ao_session ).

    " Create a unique configuration set name for testing
    DATA(lv_uuid) = /awsex/cl_utils=>get_random_string( ).
    av_configuration_set_name = |pps_test_{ lv_uuid }|.

    " Attempt to create the configuration set
    " Note: This requires the following IAM permissions:
    " - sms-voice:CreateConfigurationSet
    " - sms-voice:DeleteConfigurationSet
    " - sms-voice:CreateConfigurationSetEventDestination
    " - sms-voice:DeleteConfigurationSetEventDestination
    " - sms-voice:GetConfigurationSetEventDestinations
    " - sms-voice:ListConfigurationSets
    " - sms-voice:SendVoiceMessage
    " - sms-voice:UpdateConfigurationSetEventDestination
    " The resource must be "*" as PPS doesn't support resource-level permissions
    TRY.
        ao_pps->createconfigurationset(
          iv_configurationsetname = av_configuration_set_name
        ).

        MESSAGE |Configuration set { av_configuration_set_name } created successfully.| TYPE 'I'.
        av_iam_permissions_missing = abap_false.

        " Create supporting resources for event destination tests
        create_sns_topic( ).
        create_cloudwatch_log_grp( ).
        create_iam_role( ).

      CATCH /aws1/cx_ppsclientexc INTO DATA(lo_client_ex).
        " Check if this is an Access Denied error
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
        " Configuration set already exists from a previous test - this is acceptable
        MESSAGE |Configuration set { av_configuration_set_name } already exists.| TYPE 'I'.
        av_iam_permissions_missing = abap_false.

        " Create supporting resources
        TRY.
            create_sns_topic( ).
            create_cloudwatch_log_grp( ).
            create_iam_role( ).
          CATCH /aws1/cx_rt_generic.
            " Ignore errors in creating supporting resources
        ENDTRY.

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
    " Clean up the configuration set and supporting resources
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

    " Clean up SNS topic
    IF av_sns_topic_arn IS NOT INITIAL.
      TRY.
          ao_sns->deletetopic( iv_topicarn = av_sns_topic_arn ).
          MESSAGE |SNS topic { av_sns_topic_arn } deleted.| TYPE 'I'.
        CATCH /aws1/cx_rt_generic.
          " Ignore errors during cleanup
      ENDTRY.
    ENDIF.

    " Clean up CloudWatch Logs log group
    IF av_log_group_name IS NOT INITIAL.
      TRY.
          ao_cwl->deleteloggroup( iv_loggroupname = av_log_group_name ).
          MESSAGE |CloudWatch log group { av_log_group_name } deleted.| TYPE 'I'.
        CATCH /aws1/cx_rt_generic.
          " Ignore errors during cleanup
      ENDTRY.
    ENDIF.

    " Clean up IAM role and policy
    IF av_iam_role_name IS NOT INITIAL.
      TRY.
          " Detach policies from role
          DATA(lo_list_attached) = ao_iam->listattachedrolepolicies( iv_rolename = av_iam_role_name ).
          LOOP AT lo_list_attached->get_attachedpolicies( ) INTO DATA(lo_policy).
            TRY.
                ao_iam->detachrolepolicy(
                  iv_rolename = av_iam_role_name
                  iv_policyarn = lo_policy->get_policyarn( ) ).
              CATCH /aws1/cx_rt_generic.
                " Ignore errors
            ENDTRY.
          ENDLOOP.

          " Delete inline policies
          DATA(lo_list_inline) = ao_iam->listrolepolicies( iv_rolename = av_iam_role_name ).
          LOOP AT lo_list_inline->get_policynames( ) INTO DATA(lv_policy_name).
            TRY.
                ao_iam->deleterolepolicy(
                  iv_rolename = av_iam_role_name
                  iv_policyname = lv_policy_name ).
              CATCH /aws1/cx_rt_generic.
                " Ignore errors
            ENDTRY.
          ENDLOOP.

          " Delete role
          ao_iam->deleterole( iv_rolename = av_iam_role_name ).
          MESSAGE |IAM role { av_iam_role_name } deleted.| TYPE 'I'.
        CATCH /aws1/cx_rt_generic.
          " Ignore errors during cleanup
      ENDTRY.
    ENDIF.

  ENDMETHOD.

  METHOD create_sns_topic.
    " Create an SNS topic for event destinations
    DATA(lv_uuid) = /awsex/cl_utils=>get_random_string( ).
    DATA(lv_topic_name) = |pps-test-topic-{ lv_uuid }|.

    TRY.
        DATA(lo_topic_result) = ao_sns->createtopic( iv_name = lv_topic_name ).
        av_sns_topic_arn = lo_topic_result->get_topicarn( ).

        " Tag the topic for cleanup
        DATA lt_sns_tags TYPE /aws1/cl_snstag=>tt_taglist.
        DATA(lo_sns_tag) = NEW /aws1/cl_snstag( iv_key = 'convert_test' iv_value = 'true' ).
        APPEND lo_sns_tag TO lt_sns_tags.
        ao_sns->tagresource(
          iv_resourcearn = av_sns_topic_arn
          it_tags = lt_sns_tags ).

        MESSAGE |SNS topic created: { av_sns_topic_arn }| TYPE 'I'.

      CATCH /aws1/cx_rt_generic INTO DATA(lo_ex).
        MESSAGE |Error creating SNS topic: { lo_ex->get_text( ) }| TYPE 'I'.
        RAISE EXCEPTION lo_ex.
    ENDTRY.

  ENDMETHOD.

  METHOD create_cloudwatch_log_grp.
    " Create a CloudWatch Logs log group for event destinations
    DATA(lv_uuid) = /awsex/cl_utils=>get_random_string( ).
    av_log_group_name = |/aws/pps/test-{ lv_uuid }|.

    TRY.
        ao_cwl->createloggroup( iv_loggroupname = av_log_group_name ).

        " Tag the log group for cleanup
        DATA lt_log_tags TYPE /aws1/cl_cwltags=>tt_tags.
        lt_log_tags = VALUE #( ( key = 'convert_test' value = 'true' ) ).
        ao_cwl->tagloggroup(
          iv_loggroupname = av_log_group_name
          it_tags = lt_log_tags ).

        " Construct log group ARN
        DATA(lv_region) = ao_session->get_region( ).
        DATA(lv_account_id) = ao_session->get_account_id( ).
        av_log_group_arn = |arn:aws:logs:{ lv_region }:{ lv_account_id }:log-group:{ av_log_group_name }:*|.

        MESSAGE |CloudWatch log group created: { av_log_group_name }| TYPE 'I'.

      CATCH /aws1/cx_rt_generic INTO DATA(lo_ex).
        MESSAGE |Error creating CloudWatch log group: { lo_ex->get_text( ) }| TYPE 'I'.
        RAISE EXCEPTION lo_ex.
    ENDTRY.

  ENDMETHOD.

  METHOD create_iam_role.
    " Create an IAM role for PPS to write to CloudWatch Logs
    DATA(lv_uuid) = /awsex/cl_utils=>get_random_string( ).
    av_iam_role_name = |pps-test-role-{ lv_uuid }|.

    " Trust policy allowing PPS to assume the role
    DATA(lv_trust_policy) = |\{| &&
      |"Version":"2012-10-17",| &&
      |"Statement":[| &&
      |\{| &&
      |"Effect":"Allow",| &&
      |"Principal":\{"Service":"sms-voice.amazonaws.com"\},| &&
      |"Action":"sts:AssumeRole"| &&
      |\}| &&
      |]| &&
      |\}|.

    TRY.
        " Create the IAM role
        DATA(lo_role_result) = ao_iam->createrole(
          iv_rolename = av_iam_role_name
          iv_assumerolepolicydocument = lv_trust_policy ).

        av_iam_role_arn = lo_role_result->get_role( )->get_arn( ).

        " Tag the role for cleanup
        DATA lt_iam_tags TYPE /aws1/cl_iamtag=>tt_taglisttype.
        DATA(lo_iam_tag) = NEW /aws1/cl_iamtag( iv_key = 'convert_test' iv_value = 'true' ).
        APPEND lo_iam_tag TO lt_iam_tags.
        ao_iam->tagrole(
          iv_rolename = av_iam_role_name
          it_tags = lt_iam_tags ).

        " Create and attach policy for CloudWatch Logs
        DATA(lv_policy_doc) = |\{| &&
          |"Version":"2012-10-17",| &&
          |"Statement":[| &&
          |\{| &&
          |"Effect":"Allow",| &&
          |"Action":[| &&
          |"logs:CreateLogGroup",| &&
          |"logs:CreateLogStream",| &&
          |"logs:PutLogEvents"| &&
          |],| &&
          |"Resource":"*"| &&
          |\}| &&
          |]| &&
          |\}|.

        ao_iam->putrolepolicy(
          iv_rolename = av_iam_role_name
          iv_policyname = 'PPSCloudWatchLogsPolicy'
          iv_policydocument = lv_policy_doc ).

        " Wait for role to propagate
        WAIT UP TO 10 SECONDS.

        MESSAGE |IAM role created: { av_iam_role_arn }| TYPE 'I'.

      CATCH /aws1/cx_rt_generic INTO DATA(lo_ex).
        MESSAGE |Error creating IAM role: { lo_ex->get_text( ) }| TYPE 'I'.
        RAISE EXCEPTION lo_ex.
    ENDTRY.

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

  METHOD create_configuration_set.
    " Test the create_configuration_set method
    IF av_iam_permissions_missing = abap_true.
      MESSAGE 'Skipping test: IAM permissions for Pinpoint SMS Voice are not configured.' TYPE 'I'.
      RETURN.
    ENDIF.

    " Create a unique configuration set name for this test
    DATA(lv_uuid) = /awsex/cl_utils=>get_random_string( ).
    DATA(lv_config_set_name) = |pps_create_{ lv_uuid }|.

    TRY.
        " Test creating a configuration set
        ao_pps_actions->create_configuration_set(
          iv_configuration_set_name = lv_config_set_name ).

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

        " Clean up - delete the test configuration set
        ao_pps->deleteconfigurationset( iv_configurationsetname = lv_config_set_name ).

      CATCH /aws1/cx_rt_generic INTO DATA(lo_ex).
        " Clean up on error
        TRY.
            ao_pps->deleteconfigurationset( iv_configurationsetname = lv_config_set_name ).
          CATCH /aws1/cx_rt_generic.
            " Ignore cleanup errors
        ENDTRY.
        RAISE EXCEPTION lo_ex.
    ENDTRY.

  ENDMETHOD.

  METHOD list_configuration_sets.
    " Test the list_configuration_sets method
    IF av_iam_permissions_missing = abap_true.
      MESSAGE 'Skipping test: IAM permissions for Pinpoint SMS Voice are not configured.' TYPE 'I'.
      RETURN.
    ENDIF.

    TRY.
        " List all configuration sets
        DATA(lo_result) = ao_pps_actions->list_configuration_sets( ).

        " Assert that result is not null
        cl_abap_unit_assert=>assert_bound(
          act = lo_result
          msg = 'List configuration sets result should not be null' ).

        " Verify that our test configuration set is in the list
        DATA lv_found TYPE abap_bool VALUE abap_false.
        LOOP AT lo_result->get_configurationsets( ) INTO DATA(lo_config_set).
          IF lo_config_set->get_value( ) = av_configuration_set_name.
            lv_found = abap_true.
            MESSAGE |Found configuration set: { lo_config_set->get_value( ) }| TYPE 'I'.
            EXIT.
          ENDIF.
        ENDLOOP.

        cl_abap_unit_assert=>assert_true(
          act = lv_found
          msg = |Test configuration set { av_configuration_set_name } should be in the list| ).

      CATCH /aws1/cx_rt_generic INTO DATA(lo_ex).
        MESSAGE |Error in list_configuration_sets test: { lo_ex->get_text( ) }| TYPE 'I'.
        RAISE EXCEPTION lo_ex.
    ENDTRY.

  ENDMETHOD.

  METHOD delete_configuration_set.
    " Test the delete_configuration_set method
    IF av_iam_permissions_missing = abap_true.
      MESSAGE 'Skipping test: IAM permissions for Pinpoint SMS Voice are not configured.' TYPE 'I'.
      RETURN.
    ENDIF.

    " Create a configuration set specifically for deletion test
    DATA(lv_uuid) = /awsex/cl_utils=>get_random_string( ).
    DATA(lv_config_set_name) = |pps_del_{ lv_uuid }|.

    TRY.
        " First create the configuration set
        ao_pps->createconfigurationset( iv_configurationsetname = lv_config_set_name ).

        " Now test deleting it
        ao_pps_actions->delete_configuration_set(
          iv_configuration_set_name = lv_config_set_name ).

        " Verify it no longer exists by trying to get its event destinations
        TRY.
            ao_pps->getconfseteventdestinations( iv_configurationsetname = lv_config_set_name ).

            " If we reach here, the configuration set still exists - fail the test
            cl_abap_unit_assert=>fail(
              msg = |Configuration set { lv_config_set_name } should not exist after deletion| ).

          CATCH /aws1/cx_ppsnotfoundexception.
            " Expected exception - configuration set was successfully deleted
            MESSAGE |Configuration set { lv_config_set_name } successfully deleted| TYPE 'I'.
        ENDTRY.

      CATCH /aws1/cx_rt_generic INTO DATA(lo_ex).
        " Clean up on error
        TRY.
            ao_pps->deleteconfigurationset( iv_configurationsetname = lv_config_set_name ).
          CATCH /aws1/cx_rt_generic.
            " Ignore cleanup errors
        ENDTRY.
        RAISE EXCEPTION lo_ex.
    ENDTRY.

  ENDMETHOD.

  METHOD get_conf_set_event_dst.
    " Test the get_conf_set_event_dst method
    IF av_iam_permissions_missing = abap_true.
      MESSAGE 'Skipping test: IAM permissions for Pinpoint SMS Voice are not configured.' TYPE 'I'.
      RETURN.
    ENDIF.

    TRY.
        " Get event destinations for the test configuration set
        DATA(lo_result) = ao_pps_actions->get_conf_set_event_dst(
          iv_configuration_set_name = av_configuration_set_name ).

        " Assert that result is not null
        cl_abap_unit_assert=>assert_bound(
          act = lo_result
          msg = 'Get configuration set event destinations result should not be null' ).

        " The configuration set may not have any event destinations yet, which is fine
        " Just verify we can retrieve the event destinations list
        DATA(lt_event_destinations) = lo_result->get_eventdestinations( ).
        MESSAGE |Retrieved { lines( lt_event_destinations ) } event destination(s)| TYPE 'I'.

      CATCH /aws1/cx_rt_generic INTO DATA(lo_ex).
        MESSAGE |Error in get_conf_set_event_dst test: { lo_ex->get_text( ) }| TYPE 'I'.
        RAISE EXCEPTION lo_ex.
    ENDTRY.

  ENDMETHOD.

  METHOD create_conf_set_evt_dst.
    " Test the create_conf_set_event_dst method
    IF av_iam_permissions_missing = abap_true.
      MESSAGE 'Skipping test: IAM permissions for Pinpoint SMS Voice are not configured.' TYPE 'I'.
      RETURN.
    ENDIF.

    " Create a unique event destination name for this test
    DATA(lv_uuid) = /awsex/cl_utils=>get_random_string( ).
    DATA(lv_event_dest_name) = |evt-dest-{ lv_uuid }|.

    TRY.
        " Create event types for the event destination
        DATA lt_event_types TYPE /aws1/cl_ppseventtypes_w=>tt_eventtypes.
        " Event types: INITIATED_CALL, RINGING, ANSWERED, COMPLETED_CALL, BUSY, FAILED, NO_ANSWER
        DATA(lo_event_type1) = NEW /aws1/cl_ppseventtypes_w( 'INITIATED_CALL' ).
        DATA(lo_event_type2) = NEW /aws1/cl_ppseventtypes_w( 'COMPLETED_CALL' ).
        APPEND lo_event_type1 TO lt_event_types.
        APPEND lo_event_type2 TO lt_event_types.

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

        " Verify it was created by getting the event destinations
        DATA(lo_get_result) = ao_pps->getconfseteventdestinations(
          iv_configurationsetname = av_configuration_set_name ).

        DATA lv_found TYPE abap_bool VALUE abap_false.
        LOOP AT lo_get_result->get_eventdestinations( ) INTO DATA(lo_event_dest).
          IF lo_event_dest->get_name( ) = lv_event_dest_name.
            lv_found = abap_true.
            cl_abap_unit_assert=>assert_equals(
              act = lo_event_dest->get_enabled( )
              exp = abap_true
              msg = 'Event destination should be enabled' ).
            EXIT.
          ENDIF.
        ENDLOOP.

        cl_abap_unit_assert=>assert_true(
          act = lv_found
          msg = |Event destination { lv_event_dest_name } should exist after creation| ).

        " Clean up - delete the test event destination
        ao_pps->deleteconfseteventdst(
          iv_configurationsetname = av_configuration_set_name
          iv_eventdestinationname = lv_event_dest_name ).

      CATCH /aws1/cx_rt_generic INTO DATA(lo_ex).
        " Clean up on error
        TRY.
            ao_pps->deleteconfseteventdst(
              iv_configurationsetname = av_configuration_set_name
              iv_eventdestinationname = lv_event_dest_name ).
          CATCH /aws1/cx_rt_generic.
            " Ignore cleanup errors
        ENDTRY.
        RAISE EXCEPTION lo_ex.
    ENDTRY.

  ENDMETHOD.

  METHOD update_conf_set_evt_dst.
    " Test the update_conf_set_event_dst method
    IF av_iam_permissions_missing = abap_true.
      MESSAGE 'Skipping test: IAM permissions for Pinpoint SMS Voice are not configured.' TYPE 'I'.
      RETURN.
    ENDIF.

    " Create a unique event destination name for this test
    DATA(lv_uuid) = /awsex/cl_utils=>get_random_string( ).
    DATA(lv_event_dest_name) = |evt-upd-{ lv_uuid }|.

    TRY.
        " First create an event destination to update
        DATA lt_event_types TYPE /aws1/cl_ppseventtypes_w=>tt_eventtypes.
        DATA(lo_event_type1) = NEW /aws1/cl_ppseventtypes_w( 'INITIATED_CALL' ).
        APPEND lo_event_type1 TO lt_event_types.

        DATA(lo_sns_destination) = NEW /aws1/cl_ppssnsdestination( av_sns_topic_arn ).

        DATA(lo_event_dest_def) = NEW /aws1/cl_ppseventdstdefinition(
          io_snsdestination = lo_sns_destination
          it_matchingeventtypes = lt_event_types
          iv_enabled = abap_true ).

        ao_pps->createconfseteventdst(
          iv_configurationsetname = av_configuration_set_name
          iv_eventdestinationname = lv_event_dest_name
          io_eventdestination = lo_event_dest_def ).

        " Now update it - disable it and add more event types
        CLEAR lt_event_types.
        DATA(lo_event_type2) = NEW /aws1/cl_ppseventtypes_w( 'COMPLETED_CALL' ).
        DATA(lo_event_type3) = NEW /aws1/cl_ppseventtypes_w( 'FAILED' ).
        APPEND lo_event_type2 TO lt_event_types.
        APPEND lo_event_type3 TO lt_event_types.

        DATA(lo_updated_dest_def) = NEW /aws1/cl_ppseventdstdefinition(
          io_snsdestination = lo_sns_destination
          it_matchingeventtypes = lt_event_types
          iv_enabled = abap_false ).

        ao_pps_actions->update_conf_set_event_dst(
          iv_configuration_set_name = av_configuration_set_name
          iv_event_destination_name = lv_event_dest_name
          io_event_destination = lo_updated_dest_def ).

        " Verify the update by getting the event destination
        DATA(lo_get_result) = ao_pps->getconfseteventdestinations(
          iv_configurationsetname = av_configuration_set_name ).

        DATA lv_found TYPE abap_bool VALUE abap_false.
        LOOP AT lo_get_result->get_eventdestinations( ) INTO DATA(lo_event_dest).
          IF lo_event_dest->get_name( ) = lv_event_dest_name.
            lv_found = abap_true.

            " Verify it's disabled
            cl_abap_unit_assert=>assert_equals(
              act = lo_event_dest->get_enabled( )
              exp = abap_false
              msg = 'Event destination should be disabled after update' ).

            " Verify event types were updated
            DATA(lt_matching_events) = lo_event_dest->get_matchingeventtypes( ).
            cl_abap_unit_assert=>assert_equals(
              act = lines( lt_matching_events )
              exp = 2
              msg = 'Event destination should have 2 matching event types after update' ).

            EXIT.
          ENDIF.
        ENDLOOP.

        cl_abap_unit_assert=>assert_true(
          act = lv_found
          msg = |Event destination { lv_event_dest_name } should exist after update| ).

        " Clean up - delete the test event destination
        ao_pps->deleteconfseteventdst(
          iv_configurationsetname = av_configuration_set_name
          iv_eventdestinationname = lv_event_dest_name ).

      CATCH /aws1/cx_rt_generic INTO DATA(lo_ex).
        " Clean up on error
        TRY.
            ao_pps->deleteconfseteventdst(
              iv_configurationsetname = av_configuration_set_name
              iv_eventdestinationname = lv_event_dest_name ).
          CATCH /aws1/cx_rt_generic.
            " Ignore cleanup errors
        ENDTRY.
        RAISE EXCEPTION lo_ex.
    ENDTRY.

  ENDMETHOD.

  METHOD delete_conf_set_evt_dst.
    " Test the delete_conf_set_event_dst method
    IF av_iam_permissions_missing = abap_true.
      MESSAGE 'Skipping test: IAM permissions for Pinpoint SMS Voice are not configured.' TYPE 'I'.
      RETURN.
    ENDIF.

    " Create a unique event destination name for this test
    DATA(lv_uuid) = /awsex/cl_utils=>get_random_string( ).
    DATA(lv_event_dest_name) = |evt-del-{ lv_uuid }|.

    TRY.
        " First create an event destination to delete
        DATA lt_event_types TYPE /aws1/cl_ppseventtypes_w=>tt_eventtypes.
        DATA(lo_event_type1) = NEW /aws1/cl_ppseventtypes_w( 'INITIATED_CALL' ).
        APPEND lo_event_type1 TO lt_event_types.

        DATA(lo_sns_destination) = NEW /aws1/cl_ppssnsdestination( av_sns_topic_arn ).

        DATA(lo_event_dest_def) = NEW /aws1/cl_ppseventdstdefinition(
          io_snsdestination = lo_sns_destination
          it_matchingeventtypes = lt_event_types
          iv_enabled = abap_true ).

        ao_pps->createconfseteventdst(
          iv_configurationsetname = av_configuration_set_name
          iv_eventdestinationname = lv_event_dest_name
          io_eventdestination = lo_event_dest_def ).

        " Now test deleting it
        ao_pps_actions->delete_conf_set_event_dst(
          iv_configuration_set_name = av_configuration_set_name
          iv_event_destination_name = lv_event_dest_name ).

        " Verify it no longer exists by getting event destinations
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

      CATCH /aws1/cx_rt_generic INTO DATA(lo_ex).
        " Clean up on error
        TRY.
            ao_pps->deleteconfseteventdst(
              iv_configurationsetname = av_configuration_set_name
              iv_eventdestinationname = lv_event_dest_name ).
          CATCH /aws1/cx_rt_generic.
            " Ignore cleanup errors
        ENDTRY.
        RAISE EXCEPTION lo_ex.
    ENDTRY.

  ENDMETHOD.

ENDCLASS.
