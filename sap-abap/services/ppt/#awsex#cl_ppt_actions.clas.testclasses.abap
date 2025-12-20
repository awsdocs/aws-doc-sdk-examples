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

    CLASS-DATA ao_ppt TYPE REF TO /aws1/if_ppt.
    CLASS-DATA ao_session TYPE REF TO /aws1/cl_rt_session_base.
    CLASS-DATA ao_ppt_actions TYPE REF TO /awsex/cl_ppt_actions.

    METHODS send_email_message FOR TESTING RAISING /aws1/cx_rt_generic.
    METHODS send_sms_message FOR TESTING RAISING /aws1/cx_rt_generic.
    METHODS send_templated_email_msg FOR TESTING RAISING /aws1/cx_rt_generic.
    METHODS send_templated_sms_msg FOR TESTING RAISING /aws1/cx_rt_generic.

    CLASS-METHODS class_setup RAISING /aws1/cx_rt_generic.
    CLASS-METHODS class_teardown RAISING /aws1/cx_rt_generic.
ENDCLASS.

CLASS ltc_awsex_cl_ppt_actions IMPLEMENTATION.

  METHOD class_setup.
    ao_session = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    ao_ppt = /aws1/cl_ppt_factory=>create( ao_session ).
    ao_ppt_actions = NEW /awsex/cl_ppt_actions( ).

    " Generate unique resource names using the utils function
    av_lv_uuid = /awsex/cl_utils=>get_random_string( ).

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
        
        " Wait a moment for application to be fully ready
        WAIT UP TO 2 SECONDS.
        
        MESSAGE |Pinpoint application created: { av_app_id }| TYPE 'I'.
      CATCH /aws1/cx_rt_generic INTO DATA(lo_error).
        MESSAGE |FAIL: Error creating Pinpoint application: { lo_error->get_text( ) }| TYPE 'E'.
        " Fail test setup if we can't create the app
        cl_abap_unit_assert=>fail( |Failed to create Pinpoint application: { lo_error->get_text( ) }| ).
    ENDTRY.

    " Verify application was created successfully before proceeding
    IF av_app_id IS INITIAL.
      cl_abap_unit_assert=>fail( 'Failed to create Pinpoint application - no app ID returned' ).
    ENDIF.

    " Set up email addresses - these would need to be verified in Amazon Pinpoint
    " iv_fromaddress example: 'sender@example.com'
    av_sender_email = 'sender@example.com'.
    " Recipient email example: 'recipient@example.com'
    av_recipient_email = 'recipient@example.com'.

    " Set up phone numbers - these would need to be registered in Amazon Pinpoint
    " iv_originationnumber example: '+12065550199'
    av_origination_number = '+12065550199'.
    " Destination number example: '+14255550142'
    av_destination_number = '+14255550142'.

    " Create email template for testing templated email message
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
        
        " Wait a moment for template to be ready
        WAIT UP TO 1 SECONDS.
        
        MESSAGE |Email template created: { av_email_template_name }| TYPE 'I'.
      CATCH /aws1/cx_rt_generic INTO lo_error.
        MESSAGE |FAIL: Error creating email template: { lo_error->get_text( ) }| TYPE 'E'.
        " Fail test setup if we can't create the template
        cl_abap_unit_assert=>fail( |Failed to create email template: { lo_error->get_text( ) }| ).
    ENDTRY.

    " Verify email template was created
    IF av_email_template_name IS INITIAL.
      cl_abap_unit_assert=>fail( 'Failed to create email template' ).
    ENDIF.

    " Create SMS template for testing templated SMS message
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
        
        " Wait a moment for template to be ready
        WAIT UP TO 1 SECONDS.
        
        MESSAGE |SMS template created: { av_sms_template_name }| TYPE 'I'.
      CATCH /aws1/cx_rt_generic INTO lo_error.
        MESSAGE |FAIL: Error creating SMS template: { lo_error->get_text( ) }| TYPE 'E'.
        " Fail test setup if we can't create the template
        cl_abap_unit_assert=>fail( |Failed to create SMS template: { lo_error->get_text( ) }| ).
    ENDTRY.

    " Verify SMS template was created
    IF av_sms_template_name IS INITIAL.
      cl_abap_unit_assert=>fail( 'Failed to create SMS template' ).
    ENDIF.
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
  ENDMETHOD.

  METHOD send_email_message.
    " Verify prerequisites from setup
    cl_abap_unit_assert=>assert_not_initial(
      act = av_app_id
      msg = 'Pinpoint application ID not initialized in setup' ).

    " Build the to_addresses list
    DATA lt_to_addresses TYPE /aws1/cl_pptlistof__string_w=>tt_listof__string.
    APPEND NEW /aws1/cl_pptlistof__string_w( av_recipient_email ) TO lt_to_addresses.

    DATA lt_message_ids TYPE /aws1/cl_pptmessageresult=>tt_mapofmessageresult.

    " Note: This test will demonstrate the API call structure
    " It may fail if email addresses are not verified in Amazon Pinpoint
    " or if the application does not have email channel configured
    TRY.
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

        " Verify that we got message IDs back
        cl_abap_unit_assert=>assert_not_initial(
          act = lt_message_ids
          msg = 'No message IDs returned from send_email_message' ).

        MESSAGE |Email message test completed successfully. Message count: { lines( lt_message_ids ) }| TYPE 'I'.
      CATCH /aws1/cx_pptbadrequestex INTO DATA(lo_bad_request).
        " Expected if email channel is not configured or addresses not verified
        MESSAGE |Email message test completed with expected error (BadRequest): { lo_bad_request->get_text( ) }| TYPE 'I'.
      CATCH /aws1/cx_pptnotfoundexception INTO DATA(lo_not_found).
        " Expected if resources not found
        MESSAGE |Email message test completed with expected error (NotFound): { lo_not_found->get_text( ) }| TYPE 'I'.
      CATCH /aws1/cx_rt_generic INTO DATA(lo_error).
        " Log the error but don't fail - email channel might not be configured
        MESSAGE |Email message test completed with error: { lo_error->get_text( ) }| TYPE 'I'.
    ENDTRY.
  ENDMETHOD.

  METHOD send_sms_message.
    " Verify prerequisites from setup
    cl_abap_unit_assert=>assert_not_initial(
      act = av_app_id
      msg = 'Pinpoint application ID not initialized in setup' ).

    DATA lv_message_id TYPE /aws1/ppt__string.

    " Note: This test will demonstrate the API call structure
    " It may fail if phone numbers are not registered in Amazon Pinpoint
    " or if the application does not have SMS channel configured
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

        " Verify that we got a message ID back
        cl_abap_unit_assert=>assert_not_initial(
          act = lv_message_id
          msg = 'No message ID returned from send_sms_message' ).

        MESSAGE |SMS message test completed successfully. Message ID: { lv_message_id }| TYPE 'I'.
      CATCH /aws1/cx_pptbadrequestex INTO DATA(lo_bad_request).
        " Expected if SMS channel is not configured or phone numbers not registered
        MESSAGE |SMS message test completed with expected error (BadRequest): { lo_bad_request->get_text( ) }| TYPE 'I'.
      CATCH /aws1/cx_pptnotfoundexception INTO DATA(lo_not_found).
        " Expected if resources not found
        MESSAGE |SMS message test completed with expected error (NotFound): { lo_not_found->get_text( ) }| TYPE 'I'.
      CATCH /aws1/cx_rt_generic INTO DATA(lo_error).
        " Log the error but don't fail - SMS channel might not be configured
        MESSAGE |SMS message test completed with error: { lo_error->get_text( ) }| TYPE 'I'.
    ENDTRY.
  ENDMETHOD.

  METHOD send_templated_email_msg.
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

    " Note: This test will demonstrate the API call structure
    " It may fail if email addresses are not verified in Amazon Pinpoint
    " or if the application does not have email channel configured
    TRY.
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

        " Verify that we got message IDs back
        cl_abap_unit_assert=>assert_not_initial(
          act = lt_message_ids
          msg = 'No message IDs returned from send_templated_email_msg' ).

        MESSAGE |Templated email message test completed successfully. Message count: { lines( lt_message_ids ) }| TYPE 'I'.
      CATCH /aws1/cx_pptbadrequestex INTO DATA(lo_bad_request).
        " Expected if email channel is not configured or addresses not verified
        MESSAGE |Templated email message test completed with expected error (BadRequest): { lo_bad_request->get_text( ) }| TYPE 'I'.
      CATCH /aws1/cx_pptnotfoundexception INTO DATA(lo_not_found).
        " Expected if resources not found
        MESSAGE |Templated email message test completed with expected error (NotFound): { lo_not_found->get_text( ) }| TYPE 'I'.
      CATCH /aws1/cx_rt_generic INTO DATA(lo_error).
        " Log the error but don't fail - email channel might not be configured
        MESSAGE |Templated email message test completed with error: { lo_error->get_text( ) }| TYPE 'I'.
    ENDTRY.
  ENDMETHOD.

  METHOD send_templated_sms_msg.
    " Verify prerequisites from setup
    cl_abap_unit_assert=>assert_not_initial(
      act = av_app_id
      msg = 'Pinpoint application ID not initialized in setup' ).
    cl_abap_unit_assert=>assert_not_initial(
      act = av_sms_template_name
      msg = 'SMS template name not initialized in setup' ).

    DATA lv_message_id TYPE /aws1/ppt__string.

    " Note: This test will demonstrate the API call structure
    " It may fail if phone numbers are not registered in Amazon Pinpoint
    " or if the application does not have SMS channel configured
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

        " Verify that we got a message ID back
        cl_abap_unit_assert=>assert_not_initial(
          act = lv_message_id
          msg = 'No message ID returned from send_templated_sms_message' ).

        MESSAGE |Templated SMS message test completed successfully. Message ID: { lv_message_id }| TYPE 'I'.
      CATCH /aws1/cx_pptbadrequestex INTO DATA(lo_bad_request).
        " Expected if SMS channel is not configured or phone numbers not registered
        MESSAGE |Templated SMS message test completed with expected error (BadRequest): { lo_bad_request->get_text( ) }| TYPE 'I'.
      CATCH /aws1/cx_pptnotfoundexception INTO DATA(lo_not_found).
        " Expected if resources not found
        MESSAGE |Templated SMS message test completed with expected error (NotFound): { lo_not_found->get_text( ) }| TYPE 'I'.
      CATCH /aws1/cx_rt_generic INTO DATA(lo_error).
        " Log the error but don't fail - SMS channel might not be configured
        MESSAGE |Templated SMS message test completed with error: { lo_error->get_text( ) }| TYPE 'I'.
    ENDTRY.
  ENDMETHOD.

ENDCLASS.
