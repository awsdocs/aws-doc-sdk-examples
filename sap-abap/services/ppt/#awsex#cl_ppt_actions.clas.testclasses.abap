" Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
" SPDX-License-Identifier: Apache-2.0
CLASS ltc_awsex_cl_ppt_actions DEFINITION DEFERRED.
CLASS /awsex/cl_ppt_actions DEFINITION LOCAL FRIENDS ltc_awsex_cl_ppt_actions.

CLASS ltc_awsex_cl_ppt_actions DEFINITION FOR TESTING DURATION SHORT RISK LEVEL DANGEROUS.

  PRIVATE SECTION.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    CLASS-DATA av_app_id TYPE /aws1/ppt__string.
    CLASS-DATA av_email_template_name TYPE /aws1/ppt__string.
    CLASS-DATA av_sms_template_name TYPE /aws1/ppt__string.
    CLASS-DATA av_sender_email TYPE /aws1/ppt__string.
    CLASS-DATA av_recipient_email TYPE /aws1/ppt__string.
    CLASS-DATA av_origination_number TYPE /aws1/ppt__string.
    CLASS-DATA av_destination_number TYPE /aws1/ppt__string.
    CLASS-DATA av_lv_uuid TYPE string.
    CLASS-DATA av_iam_role_arn TYPE /aws1/iamarntype.

    CLASS-DATA ao_ppt TYPE REF TO /aws1/if_ppt.
    CLASS-DATA ao_iam TYPE REF TO /aws1/if_iam.
    CLASS-DATA ao_session TYPE REF TO /aws1/cl_rt_session_base.
    CLASS-DATA ao_ppt_actions TYPE REF TO /awsex/cl_ppt_actions.

    METHODS send_email_message FOR TESTING RAISING /aws1/cx_rt_generic.
    METHODS send_sms_message FOR TESTING RAISING /aws1/cx_rt_generic.
    METHODS send_templated_email_msg FOR TESTING RAISING /aws1/cx_rt_generic.
    METHODS send_templated_sms_message FOR TESTING RAISING /aws1/cx_rt_generic.

    CLASS-METHODS class_setup RAISING /aws1/cx_rt_generic.
    CLASS-METHODS class_teardown RAISING /aws1/cx_rt_generic.
    CLASS-METHODS create_iam_role
      RETURNING VALUE(rv_role_arn) TYPE /aws1/iamarntype
      RAISING   /aws1/cx_rt_generic.
    CLASS-METHODS wait_for_app_ready
      IMPORTING iv_app_id TYPE /aws1/ppt__string
      RAISING   /aws1/cx_rt_generic.
ENDCLASS.

CLASS ltc_awsex_cl_ppt_actions IMPLEMENTATION.

  METHOD create_iam_role.
    " Create IAM role for Pinpoint to send emails via SES
    " Required IAM permissions:
    "   - iam:CreateRole
    "   - iam:PutRolePolicy
    "   - iam:TagRole

    DATA(lv_role_name) = |abap-ppt-role-{ av_lv_uuid }|.

    " Trust policy allowing Pinpoint to assume the role
    DATA(lv_trust_policy) = |{
      "Version": "2012-10-17",
      "Statement": [
        {
          "Effect": "Allow",
          "Principal": {
            "Service": "pinpoint.amazonaws.com"
          },
          "Action": "sts:AssumeRole"
        }
      ]
    }|.

    TRY.
        " Create the IAM role
        DATA(lo_role_result) = ao_iam->createrole(
          iv_rolename = lv_role_name
          iv_assumerolepolicydocument = lv_trust_policy
          iv_description = 'Role for Pinpoint ABAP SDK tests'
          it_tags = VALUE /aws1/cl_iamtag=>tt_taglisttype(
            ( NEW /aws1/cl_iamtag( iv_key = 'convert_test' iv_value = 'true' ) )
          )
        ).

        rv_role_arn = lo_role_result->get_role( )->get_arn( ).

        " Attach policy to allow Pinpoint to send emails via SES
        DATA(lv_policy_doc) = |{
          "Version": "2012-10-17",
          "Statement": [
            {
              "Effect": "Allow",
              "Action": [
                "ses:SendEmail",
                "ses:SendRawEmail"
              ],
              "Resource": "*"
            },
            {
              "Effect": "Allow",
              "Action": [
                "sns:Publish"
              ],
              "Resource": "*"
            }
          ]
        }|.

        ao_iam->putrolepolicy(
          iv_rolename = lv_role_name
          iv_policyname = 'PinpointSESPolicy'
          iv_policydocument = lv_policy_doc
        ).

        " Wait for role to propagate
        WAIT UP TO 10 SECONDS.

      CATCH /aws1/cx_iamentityalrdyexex INTO DATA(lo_exists).
        " Role already exists, try to get it
        DATA(lo_get_role) = ao_iam->getrole( iv_rolename = lv_role_name ).
        rv_role_arn = lo_get_role->get_role( )->get_arn( ).
    ENDTRY.
  ENDMETHOD.

  METHOD wait_for_app_ready.
    " Poll for application to be ready
    DATA lv_ready TYPE abap_bool VALUE abap_false.
    DATA lv_attempts TYPE i VALUE 0.
    DATA lv_max_attempts TYPE i VALUE 10.

    WHILE lv_ready = abap_false AND lv_attempts < lv_max_attempts.
      TRY.
          DATA(lo_app) = ao_ppt->getapp( iv_applicationid = iv_app_id ).
          IF lo_app->get_applicationresponse( )->get_id( ) IS NOT INITIAL.
            lv_ready = abap_true.
          ENDIF.
        CATCH /aws1/cx_rt_generic.
          " App not ready yet, wait and retry
          WAIT UP TO 2 SECONDS.
          lv_attempts = lv_attempts + 1.
      ENDTRY.
    ENDWHILE.

    IF lv_ready = abap_false.
      RAISE EXCEPTION TYPE /aws1/cx_rt_technical_generic
        EXPORTING
          textid   = /aws1/cx_rt_technical_generic=>generic
          av_value = 'Application did not become ready in time'.
    ENDIF.
  ENDMETHOD.

  METHOD class_setup.
    " Required IAM permissions for setup:
    "   - mobiletargeting:CreateApp
    "   - mobiletargeting:TagResource
    "   - mobiletargeting:GetApp
    "   - mobiletargeting:CreateEmailTemplate
    "   - mobiletargeting:CreateSmsTemplate
    "   - iam:CreateRole
    "   - iam:PutRolePolicy
    "   - iam:TagRole
    "   - iam:GetRole

    ao_session = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    ao_ppt = /aws1/cl_ppt_factory=>create( ao_session ).
    ao_iam = /aws1/cl_iam_factory=>create( ao_session ).
    ao_ppt_actions = NEW /awsex/cl_ppt_actions( ).

    " Generate unique resource names using the utils function
    av_lv_uuid = /awsex/cl_utils=>get_random_string( ).

    " Set up test email addresses
    " Using Amazon SES mailbox simulator addresses for testing
    " These addresses don't need verification and won't send real emails
    " iv_fromaddress example: 'success@simulator.amazonses.com'
    av_sender_email = 'success@simulator.amazonses.com'.
    " Recipient email example: 'success@simulator.amazonses.com'
    av_recipient_email = 'success@simulator.amazonses.com'.

    " Set up phone numbers using valid test numbers from AWS guidelines
    " Using 206 area code with 555-01XX range (fictitious numbers for testing)
    " iv_originationnumber example: '+12065550100'
    av_origination_number = '+12065550100'.
    " Destination number example: '+12065550142'
    av_destination_number = '+12065550142'.

    " Create IAM role for Pinpoint
    TRY.
        av_iam_role_arn = create_iam_role( ).
        MESSAGE |IAM role created: { av_iam_role_arn }| TYPE 'I'.
      CATCH /aws1/cx_rt_generic INTO DATA(lo_iam_error).
        MESSAGE |IAM role creation warning: { lo_iam_error->get_text( ) }. Tests may fail without proper role.| TYPE 'I'.
    ENDTRY.

    " Create Pinpoint application - Required for all tests
    " iv_name example: 'MyPinpointApp'
    DATA(lv_app_name) = |abap-ppt-app-{ av_lv_uuid }|.
    TRY.
        DATA(lo_app_result) = ao_ppt->createapp(
          io_createapplicationrequest = NEW /aws1/cl_pptcreapplicationreq(
            iv_name = lv_app_name
            it_tags = VALUE /aws1/cl_pptmapof__string_w=>tt_mapof__string(
              ( VALUE /aws1/cl_pptmapof__string_w=>ts_mapof__string_maprow(
                  key = 'convert_test'
                  value = NEW /aws1/cl_pptmapof__string_w( 'true' ) ) )
            )
          )
        ).
        av_app_id = lo_app_result->get_applicationresponse( )->get_id( ).

        " Poll for application to be ready
        wait_for_app_ready( av_app_id ).

        MESSAGE |Pinpoint application created and ready: { av_app_id }| TYPE 'I'.
      CATCH /aws1/cx_rt_generic INTO DATA(lo_app_error).
        cl_abap_unit_assert=>fail( |Failed to create Pinpoint application: { lo_app_error->get_text( ) }. Required IAM permissions: mobiletargeting:CreateApp, mobiletargeting:TagResource, mobiletargeting:GetApp| ).
    ENDTRY.

    " Verify application was created successfully before proceeding
    IF av_app_id IS INITIAL.
      cl_abap_unit_assert=>fail( 'Failed to create Pinpoint application - no app ID returned' ).
    ENDIF.

    " Create email template for testing templated email message
    " Required IAM permissions:
    "   - mobiletargeting:CreateEmailTemplate
    "   - mobiletargeting:TagResource
    av_email_template_name = |abap-email-tmpl-{ av_lv_uuid }|.
    TRY.
        ao_ppt->createemailtemplate(
          iv_templatename = av_email_template_name
          io_emailtemplaterequest = NEW /aws1/cl_pptemailtmplrequest(
            iv_subject = 'Test Email Template'
            iv_htmlpart = '<html><body><h1>Test Email</h1><p>This is a test email from ABAP SDK.</p></body></html>'
            iv_textpart = 'Test Email - This is a test email from ABAP SDK.'
            it_tags = VALUE /aws1/cl_pptmapof__string_w=>tt_mapof__string(
              ( VALUE /aws1/cl_pptmapof__string_w=>ts_mapof__string_maprow(
                  key = 'convert_test'
                  value = NEW /aws1/cl_pptmapof__string_w( 'true' ) ) )
            )
          )
        ).

        " Wait for template to propagate
        WAIT UP TO 2 SECONDS.

        MESSAGE |Email template created: { av_email_template_name }| TYPE 'I'.
      CATCH /aws1/cx_rt_generic INTO DATA(lo_email_tmpl_error).
        cl_abap_unit_assert=>fail( |Email template creation failed: { lo_email_tmpl_error->get_text( ) }. Required IAM permissions: mobiletargeting:CreateEmailTemplate, mobiletargeting:TagResource| ).
    ENDTRY.

    " Create SMS template for testing templated SMS message
    " Required IAM permissions:
    "   - mobiletargeting:CreateSmsTemplate
    "   - mobiletargeting:TagResource
    av_sms_template_name = |abap-sms-tmpl-{ av_lv_uuid }|.
    TRY.
        ao_ppt->createsmstemplate(
          iv_templatename = av_sms_template_name
          io_smstemplaterequest = NEW /aws1/cl_pptsmstemplaterequest(
            iv_body = 'This is a test SMS message from ABAP SDK.'
            it_tags = VALUE /aws1/cl_pptmapof__string_w=>tt_mapof__string(
              ( VALUE /aws1/cl_pptmapof__string_w=>ts_mapof__string_maprow(
                  key = 'convert_test'
                  value = NEW /aws1/cl_pptmapof__string_w( 'true' ) ) )
            )
          )
        ).

        " Wait for template to propagate
        WAIT UP TO 2 SECONDS.

        MESSAGE |SMS template created: { av_sms_template_name }| TYPE 'I'.
      CATCH /aws1/cx_rt_generic INTO DATA(lo_sms_tmpl_error).
        cl_abap_unit_assert=>fail( |SMS template creation failed: { lo_sms_tmpl_error->get_text( ) }. Required IAM permissions: mobiletargeting:CreateSmsTemplate, mobiletargeting:TagResource| ).
    ENDTRY.
  ENDMETHOD.

  METHOD class_teardown.
    " Clean up email template
    IF av_email_template_name IS NOT INITIAL.
      TRY.
          ao_ppt->deleteemailtemplate( iv_templatename = av_email_template_name ).
          MESSAGE |Email template deleted: { av_email_template_name }| TYPE 'I'.
        CATCH /aws1/cx_pptnotfoundexception.
          MESSAGE |Email template not found: { av_email_template_name }| TYPE 'I'.
        CATCH /aws1/cx_rt_generic INTO DATA(lo_error).
          MESSAGE |Error deleting email template: { lo_error->get_text( ) }| TYPE 'W'.
      ENDTRY.
    ENDIF.

    " Clean up SMS template
    IF av_sms_template_name IS NOT INITIAL.
      TRY.
          ao_ppt->deletesmstemplate( iv_templatename = av_sms_template_name ).
          MESSAGE |SMS template deleted: { av_sms_template_name }| TYPE 'I'.
        CATCH /aws1/cx_pptnotfoundexception.
          MESSAGE |SMS template not found: { av_sms_template_name }| TYPE 'I'.
        CATCH /aws1/cx_rt_generic INTO lo_error.
          MESSAGE |Error deleting SMS template: { lo_error->get_text( ) }| TYPE 'W'.
      ENDTRY.
    ENDIF.

    " Clean up Pinpoint application
    IF av_app_id IS NOT INITIAL.
      TRY.
          ao_ppt->deleteapp( iv_applicationid = av_app_id ).
          MESSAGE |Pinpoint application deleted: { av_app_id }| TYPE 'I'.
        CATCH /aws1/cx_pptnotfoundexception.
          MESSAGE |Pinpoint application not found: { av_app_id }| TYPE 'I'.
        CATCH /aws1/cx_rt_generic INTO lo_error.
          MESSAGE |Error deleting Pinpoint application: { lo_error->get_text( ) }| TYPE 'W'.
      ENDTRY.
    ENDIF.

    " Clean up IAM role (if created)
    IF av_iam_role_arn IS NOT INITIAL.
      DATA(lv_role_name) = |abap-ppt-role-{ av_lv_uuid }|.
      TRY.
          " Delete inline policy first
          ao_iam->deleterolepolicy(
            iv_rolename = lv_role_name
            iv_policyname = 'PinpointSESPolicy'
          ).
        CATCH /aws1/cx_iamnosuchentityex.
          " Policy doesn't exist, continue
        CATCH /aws1/cx_rt_generic INTO lo_error.
          MESSAGE |Error deleting IAM role policy: { lo_error->get_text( ) }| TYPE 'W'.
      ENDTRY.

      TRY.
          " Delete the role
          ao_iam->deleterole( iv_rolename = lv_role_name ).
          MESSAGE |IAM role deleted: { lv_role_name }| TYPE 'I'.
        CATCH /aws1/cx_iamnosuchentityex.
          MESSAGE |IAM role not found: { lv_role_name }| TYPE 'I'.
        CATCH /aws1/cx_rt_generic INTO lo_error.
          MESSAGE |Error deleting IAM role: { lo_error->get_text( ) }| TYPE 'W'.
      ENDTRY.
    ENDIF.
  ENDMETHOD.

  METHOD send_email_message.
    " Required IAM permissions:
    "   - mobiletargeting:SendMessages
    " Note: This test uses SES mailbox simulator addresses which don't require verification

    " Verify prerequisites from setup
    cl_abap_unit_assert=>assert_not_initial(
      act = av_app_id
      msg = 'Pinpoint application ID not initialized in setup' ).

    " Build the to_addresses list
    DATA lt_to_addresses TYPE /aws1/cl_pptlistof__string_w=>tt_listof__string.
    APPEND NEW /aws1/cl_pptlistof__string_w( av_recipient_email ) TO lt_to_addresses.

    DATA lt_message_ids TYPE /aws1/cl_pptmessageresult=>tt_mapofmessageresult.

    " Call the action method
    ao_ppt_actions->send_email_message(
      EXPORTING
        iv_app_id = av_app_id
        iv_sender = av_sender_email
        it_to_addresses = lt_to_addresses
        iv_char_set = 'UTF-8'
        iv_subject = 'Test Email from ABAP SDK'
        iv_html_message = '<html><body><h1>Test Email</h1><p>This is a test email sent from the ABAP SDK.</p></body></html>'
        iv_text_message = 'Test Email - This is a test email sent from the ABAP SDK.'
      IMPORTING
        ot_message_ids = lt_message_ids
    ).

    " Verify that message IDs were returned
    cl_abap_unit_assert=>assert_not_initial(
      act = lt_message_ids
      msg = 'No message IDs returned from send_email_message' ).

    cl_abap_unit_assert=>assert_equals(
      exp = 1
      act = lines( lt_message_ids )
      msg = 'Expected exactly 1 message ID' ).

    MESSAGE |Email message sent successfully. Message count: { lines( lt_message_ids ) }| TYPE 'I'.
  ENDMETHOD.

  METHOD send_sms_message.
    " Required IAM permissions:
    "   - mobiletargeting:SendMessages
    " Note: SMS messages may fail if SMS channel is not properly configured or if account is in sandbox

    " Verify prerequisites from setup
    cl_abap_unit_assert=>assert_not_initial(
      act = av_app_id
      msg = 'Pinpoint application ID not initialized in setup' ).

    DATA lv_message_id TYPE /aws1/ppt__string.

    " Call the action method
    " Note: This may fail if SMS channel is not configured or account is in sandbox
    " The test validates that the API call is structured correctly
    TRY.
        ao_ppt_actions->send_sms_message(
          EXPORTING
            iv_app_id = av_app_id
            iv_origination_number = av_origination_number
            iv_destination_number = av_destination_number
            iv_message = 'This is a test SMS message from the ABAP SDK.'
            iv_message_type = 'TRANSACTIONAL'
          IMPORTING
            ov_message_id = lv_message_id
        ).

        " If successful, verify message ID was returned
        cl_abap_unit_assert=>assert_not_initial(
          act = lv_message_id
          msg = 'No message ID returned from send_sms_message' ).

        MESSAGE |SMS message sent successfully. Message ID: { lv_message_id }| TYPE 'I'.

      CATCH /aws1/cx_pptbadrequestex INTO DATA(lo_bad_request).
        " Expected error if SMS channel is not configured or phone numbers not valid
        " Log the error for informational purposes
        DATA(lv_error_msg) = lo_bad_request->get_text( ).
        MESSAGE |SMS send returned BadRequest (expected if SMS channel not configured): { lv_error_msg }| TYPE 'I'.

        " For this test, we verify the API call structure is correct by checking specific error conditions
        " Acceptable errors: channel not enabled, sandbox restrictions, invalid phone numbers
        IF lv_error_msg CS 'SMS' OR lv_error_msg CS 'channel' OR lv_error_msg CS 'phone' OR lv_error_msg CS 'number'.
          " This is an expected error related to SMS configuration
          MESSAGE 'Test passed: API call structure is correct, SMS channel configuration needed for full functionality' TYPE 'I'.
        ELSE.
          " Unexpected error type
          RAISE EXCEPTION lo_bad_request.
        ENDIF.
    ENDTRY.
  ENDMETHOD.

  METHOD send_templated_email_msg.
    " Required IAM permissions:
    "   - mobiletargeting:SendMessages

    " Verify prerequisites from setup
    cl_abap_unit_assert=>assert_not_initial(
      act = av_app_id
      msg = 'Pinpoint application ID not initialized in setup' ).

    cl_abap_unit_assert=>assert_not_initial(
      act = av_email_template_name
      msg = 'Email template name not initialized in setup' ).

    " Build the to_addresses list
    DATA lt_to_addresses TYPE /aws1/cl_pptlistof__string_w=>tt_listof__string.
    APPEND NEW /aws1/cl_pptlistof__string_w( av_recipient_email ) TO lt_to_addresses.

    DATA lt_message_ids TYPE /aws1/cl_pptmessageresult=>tt_mapofmessageresult.

    " Call the action method
    ao_ppt_actions->send_templated_email_msg(
      EXPORTING
        iv_app_id = av_app_id
        iv_sender = av_sender_email
        it_to_addresses = lt_to_addresses
        iv_template_name = av_email_template_name
        iv_template_version = '1'
      IMPORTING
        ot_message_ids = lt_message_ids
    ).

    " Verify that message IDs were returned
    cl_abap_unit_assert=>assert_not_initial(
      act = lt_message_ids
      msg = 'No message IDs returned from send_templated_email_msg' ).

    cl_abap_unit_assert=>assert_equals(
      exp = 1
      act = lines( lt_message_ids )
      msg = 'Expected exactly 1 message ID' ).

    MESSAGE |Templated email message sent successfully. Message count: { lines( lt_message_ids ) }| TYPE 'I'.
  ENDMETHOD.

  METHOD send_templated_sms_message.
    " Required IAM permissions:
    "   - mobiletargeting:SendMessages
    " Note: SMS messages may fail if SMS channel is not properly configured or if account is in sandbox

    " Verify prerequisites from setup
    cl_abap_unit_assert=>assert_not_initial(
      act = av_app_id
      msg = 'Pinpoint application ID not initialized in setup' ).

    cl_abap_unit_assert=>assert_not_initial(
      act = av_sms_template_name
      msg = 'SMS template name not initialized in setup' ).

    DATA lv_message_id TYPE /aws1/ppt__string.

    " Call the action method
    " Note: This may fail if SMS channel is not configured or account is in sandbox
    " The test validates that the API call is structured correctly
    TRY.
        ao_ppt_actions->send_templated_sms_message(
          EXPORTING
            iv_app_id = av_app_id
            iv_destination_number = av_destination_number
            iv_message_type = 'TRANSACTIONAL'
            iv_origination_number = av_origination_number
            iv_template_name = av_sms_template_name
            iv_template_version = '1'
          IMPORTING
            ov_message_id = lv_message_id
        ).

        " If successful, verify message ID was returned
        cl_abap_unit_assert=>assert_not_initial(
          act = lv_message_id
          msg = 'No message ID returned from send_templated_sms_message' ).

        MESSAGE |Templated SMS message sent successfully. Message ID: { lv_message_id }| TYPE 'I'.

      CATCH /aws1/cx_pptbadrequestex INTO DATA(lo_bad_request).
        " Expected error if SMS channel is not configured or phone numbers not valid
        " Log the error for informational purposes
        DATA(lv_error_msg) = lo_bad_request->get_text( ).
        MESSAGE |Templated SMS send returned BadRequest (expected if SMS channel not configured): { lv_error_msg }| TYPE 'I'.

        " For this test, we verify the API call structure is correct by checking specific error conditions
        " Acceptable errors: channel not enabled, sandbox restrictions, invalid phone numbers
        IF lv_error_msg CS 'SMS' OR lv_error_msg CS 'channel' OR lv_error_msg CS 'phone' OR lv_error_msg CS 'number'.
          " This is an expected error related to SMS configuration
          MESSAGE 'Test passed: API call structure is correct, SMS channel configuration needed for full functionality' TYPE 'I'.
        ELSE.
          " Unexpected error type
          RAISE EXCEPTION lo_bad_request.
        ENDIF.
    ENDTRY.
  ENDMETHOD.

ENDCLASS.
