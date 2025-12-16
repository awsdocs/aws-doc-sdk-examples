" Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
" SPDX-License-Identifier: Apache-2.0

CLASS ltc_awsex_cl_ses_actions DEFINITION DEFERRED.
CLASS /awsex/cl_ses_actions DEFINITION LOCAL FRIENDS ltc_awsex_cl_ses_actions.

CLASS ltc_awsex_cl_ses_actions DEFINITION FOR TESTING DURATION LONG RISK LEVEL DANGEROUS.

  PRIVATE SECTION.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    CLASS-DATA ao_ses TYPE REF TO /aws1/if_ses.
    CLASS-DATA ao_s3 TYPE REF TO /aws1/if_s3.
    CLASS-DATA ao_session TYPE REF TO /aws1/cl_rt_session_base.
    CLASS-DATA ao_ses_actions TYPE REF TO /awsex/cl_ses_actions.
    CLASS-DATA av_email TYPE /aws1/sesaddress.
    CLASS-DATA av_domain TYPE /aws1/sesdomain.
    CLASS-DATA av_template_name TYPE /aws1/sestemplatename.
    CLASS-DATA av_filter_name TYPE /aws1/sesreceiptfiltername.
    CLASS-DATA av_rule_set_name TYPE /aws1/sesreceiptrulesetname.
    CLASS-DATA av_bucket_name TYPE /aws1/s3_bucketname.

    CLASS-METHODS class_setup RAISING /aws1/cx_rt_generic.
    CLASS-METHODS class_teardown.

    METHODS verify_email_identity FOR TESTING RAISING /aws1/cx_rt_generic.
    METHODS verify_domain_identity FOR TESTING RAISING /aws1/cx_rt_generic.
    METHODS get_identity_status FOR TESTING RAISING /aws1/cx_rt_generic.
    METHODS list_identities FOR TESTING RAISING /aws1/cx_rt_generic.
    METHODS send_email FOR TESTING RAISING /aws1/cx_rt_generic.
    METHODS create_template FOR TESTING RAISING /aws1/cx_rt_generic.
    METHODS get_template FOR TESTING RAISING /aws1/cx_rt_generic.
    METHODS list_templates FOR TESTING RAISING /aws1/cx_rt_generic.
    METHODS send_templated_email FOR TESTING RAISING /aws1/cx_rt_generic.
    METHODS update_template FOR TESTING RAISING /aws1/cx_rt_generic.
    METHODS delete_template FOR TESTING RAISING /aws1/cx_rt_generic.
    METHODS create_receipt_filter FOR TESTING RAISING /aws1/cx_rt_generic.
    METHODS list_receipt_filters FOR TESTING RAISING /aws1/cx_rt_generic.
    METHODS delete_receipt_filter FOR TESTING RAISING /aws1/cx_rt_generic.
    METHODS create_receipt_rule_set FOR TESTING RAISING /aws1/cx_rt_generic.
    METHODS create_s3_copy_rule FOR TESTING RAISING /aws1/cx_rt_generic.
    METHODS describe_receipt_rule_set FOR TESTING RAISING /aws1/cx_rt_generic.
    METHODS delete_receipt_rule FOR TESTING RAISING /aws1/cx_rt_generic.
    METHODS delete_receipt_rule_set FOR TESTING RAISING /aws1/cx_rt_generic.
    METHODS delete_identity FOR TESTING RAISING /aws1/cx_rt_generic.

    METHODS wait_for_identity_verification
      IMPORTING
        iv_identity TYPE /aws1/sesidentity
        iv_timeout  TYPE i DEFAULT 300.
ENDCLASS.

CLASS ltc_awsex_cl_ses_actions IMPLEMENTATION.

  METHOD class_setup.
    ao_session = /aws1/cl_rt_session_aws=>create( iv_profile_id = cv_pfl ).
    ao_ses = /aws1/cl_ses_factory=>create( ao_session ).
    ao_s3 = /aws1/cl_s3_factory=>create( ao_session ).
    ao_ses_actions = NEW /awsex/cl_ses_actions( ).

    " Get unique identifiers
    DATA lv_uuid TYPE guid_32.
    TRY.
        lv_uuid = cl_system_uuid=>create_uuid_x16_static( ).
      CATCH cx_uuid_error.
        " Fallback to random string
        lv_uuid = /awsex/cl_utils=>get_random_string( ).
    ENDTRY.
    DATA lv_uuid_string TYPE string.
    lv_uuid_string = lv_uuid.
    TRANSLATE lv_uuid_string TO LOWER CASE.
    REPLACE ALL OCCURRENCES OF '-' IN lv_uuid_string WITH ''.
    
    av_email = |sestest{ lv_uuid_string(8) }@example.com|.
    av_domain = |sestest{ lv_uuid_string(8) }.example.com|.
    av_template_name = |ses-tmpl-{ lv_uuid_string(10) }|.
    av_filter_name = |ses-filter-{ lv_uuid_string(10) }|.
    av_rule_set_name = |ses-ruleset-{ lv_uuid_string(8) }|.
    DATA(lv_acct) = ao_session->get_account_id( ).
    av_bucket_name = |ses-test-bkt-{ lv_acct }-{ lv_uuid_string(8) }|.

    " Create S3 bucket for receipt rule tests
    /awsex/cl_utils=>create_bucket(
      iv_bucket = av_bucket_name
      io_s3 = ao_s3
      io_session = ao_session
    ).

    " Tag resources for cleanup
    DATA lt_tags TYPE /aws1/cl_s3_tag=>tt_tagset.
    DATA(lo_tag) = NEW /aws1/cl_s3_tag(
      iv_key = 'convert_test'
      iv_value = 'true'
    ).
    APPEND lo_tag TO lt_tags.

    DATA(lo_tagging) = NEW /aws1/cl_s3_tagging( it_tagset = lt_tags ).
    ao_s3->putbuckettagging(
      iv_bucket = av_bucket_name
      io_tagging = lo_tagging
    ).
  ENDMETHOD.

  METHOD class_teardown.
    " Clean up S3 bucket
    IF av_bucket_name IS NOT INITIAL.
      TRY.
          /awsex/cl_utils=>cleanup_bucket(
            io_s3 = ao_s3
            iv_bucket = av_bucket_name
          ).
        CATCH /aws1/cx_rt_generic.
          " Ignore cleanup errors
      ENDTRY.
    ENDIF.

    " Clean up template
    IF av_template_name IS NOT INITIAL.
      TRY.
          ao_ses->deletetemplate( iv_templatename = av_template_name ).
        CATCH /aws1/cx_rt_generic.
          " Ignore cleanup errors
      ENDTRY.
    ENDIF.

    " Clean up receipt filter
    IF av_filter_name IS NOT INITIAL.
      TRY.
          ao_ses->deletereceiptfilter( iv_filtername = av_filter_name ).
        CATCH /aws1/cx_rt_generic.
          " Ignore cleanup errors
      ENDTRY.
    ENDIF.

    " Clean up receipt rule set
    IF av_rule_set_name IS NOT INITIAL.
      TRY.
          ao_ses->deletereceiptruleset( iv_rulesetname = av_rule_set_name ).
        CATCH /aws1/cx_rt_generic.
          " Ignore cleanup errors
      ENDTRY.
    ENDIF.

    " Clean up identity
    IF av_email IS NOT INITIAL.
      TRY.
          ao_ses->deleteidentity( iv_identity = av_email ).
        CATCH /aws1/cx_rt_generic.
          " Ignore cleanup errors
      ENDTRY.
    ENDIF.
  ENDMETHOD.

  METHOD verify_email_identity.
    " av_email = 'test@example.com'
    ao_ses_actions->verify_email_identity( iv_email_address = av_email ).

    " Wait a moment for the verification request to propagate
    WAIT UP TO 2 SECONDS.

    " Verify that the identity exists in pending state
    DATA lt_identities TYPE /aws1/cl_sesidentitylist_w=>tt_identitylist.
    APPEND NEW /aws1/cl_sesidentitylist_w( iv_value = av_email ) TO lt_identities.

    DATA(lo_result) = ao_ses->getidentityverificationattrs(
      it_identities = lt_identities
    ).

    DATA(lt_attrs) = lo_result->get_verificationattributes( ).
    cl_abap_unit_assert=>assert_not_initial(
      act = lt_attrs
      msg = |Email identity { av_email } was not created|
    ).
  ENDMETHOD.

  METHOD verify_domain_identity.
    " av_domain = 'example.com'
    DATA(lv_token) = ao_ses_actions->verify_domain_identity( iv_domain_name = av_domain ).

    cl_abap_unit_assert=>assert_not_initial(
      act = lv_token
      msg = |Domain verification token was not returned for { av_domain }|
    ).

    " Wait a moment for the verification request to propagate
    WAIT UP TO 2 SECONDS.

    " Verify that the domain identity exists
    DATA lt_identities TYPE /aws1/cl_sesidentitylist_w=>tt_identitylist.
    APPEND NEW /aws1/cl_sesidentitylist_w( iv_value = av_domain ) TO lt_identities.

    DATA(lo_result) = ao_ses->getidentityverificationattrs(
      it_identities = lt_identities
    ).

    DATA(lt_attrs) = lo_result->get_verificationattributes( ).
    cl_abap_unit_assert=>assert_not_initial(
      act = lt_attrs
      msg = |Domain identity { av_domain } was not created|
    ).

    " Clean up domain identity
    TRY.
        ao_ses->deleteidentity( iv_identity = av_domain ).
      CATCH /aws1/cx_rt_generic.
        " Ignore cleanup errors
    ENDTRY.
  ENDMETHOD.

  METHOD get_identity_status.
    " Ensure identity exists
    TRY.
        ao_ses->verifyemailidentity( iv_emailaddress = av_email ).
      CATCH /aws1/cx_rt_generic.
        " May already exist
    ENDTRY.

    WAIT UP TO 2 SECONDS.

    " av_email = 'test@example.com'
    DATA(lv_status) = ao_ses_actions->get_identity_status( iv_identity = av_email ).

    cl_abap_unit_assert=>assert_not_initial(
      act = lv_status
      msg = |Could not get status for identity { av_email }|
    ).
  ENDMETHOD.

  METHOD list_identities.
    " Ensure at least one identity exists
    TRY.
        ao_ses->verifyemailidentity( iv_emailaddress = av_email ).
      CATCH /aws1/cx_rt_generic.
        " May already exist
    ENDTRY.

    WAIT UP TO 2 SECONDS.

    " iv_identity_type = 'EmailAddress'
    " iv_max_items = 10
    DATA(lt_identities) = ao_ses_actions->list_identities(
      iv_identity_type = 'EmailAddress'
      iv_max_items = 10
    ).

    cl_abap_unit_assert=>assert_not_initial(
      act = lt_identities
      msg = 'No identities were found'
    ).
  ENDMETHOD.

  METHOD send_email.
    " First verify sender email
    TRY.
        ao_ses->verifyemailidentity( iv_emailaddress = av_email ).
      CATCH /aws1/cx_rt_generic.
        " May already exist
    ENDTRY.

    " Wait for email to be verified (in sandbox mode, both sender and recipient must be verified)
    " Since we can't actually verify via email in tests, we'll just test the API call
    " and catch the expected error

    " iv_source = 'sender@example.com'
    " Create destination with to addresses
    DATA lt_to_addresses TYPE /aws1/cl_sesaddresslist_w=>tt_addresslist.
    APPEND NEW /aws1/cl_sesaddresslist_w( iv_value = av_email ) TO lt_to_addresses.

    DATA(lo_destination) = NEW /aws1/cl_sesdestination(
      it_toaddresses = lt_to_addresses
    ).

    " iv_subject = 'Test Subject'
    " iv_text = 'Test plain text body'
    " iv_html = '<p>Test HTML body</p>'
    TRY.
        DATA(lv_msg_id) = ao_ses_actions->send_email(
          iv_source = av_email
          io_destination = lo_destination
          iv_subject = 'Test Subject from SAP ABAP SDK'
          iv_text = 'This is the plain text version of the email.'
          iv_html = '<html><body><p>This is the HTML version of the email.</p></body></html>'
        ).

        " If sending succeeds (account out of sandbox), check message ID
        cl_abap_unit_assert=>assert_not_initial(
          act = lv_msg_id
          msg = 'No message ID was returned'
        ).
      CATCH /aws1/cx_sesmessagerejected.
        " Expected in sandbox mode without verification
      CATCH /aws1/cx_sesacctsendingpause00.
        " Expected if account is paused
    ENDTRY.
  ENDMETHOD.

  METHOD create_template.
    " iv_name = 'test-template'
    " iv_subject = 'Hello {{name}}'
    " iv_text = 'Plain text for {{name}}'
    " iv_html = '<p>HTML for {{name}}</p>'
    ao_ses_actions->create_template(
      iv_name = av_template_name
      iv_subject = 'Hello {{name}}'
      iv_text = 'Plain text greeting for {{name}}'
      iv_html = '<html><body><p>HTML greeting for {{name}}</p></body></html>'
    ).

    " Verify template was created
    DATA(lo_result) = ao_ses->gettemplate( iv_templatename = av_template_name ).
    DATA(lo_template) = lo_result->get_template( ).

    cl_abap_unit_assert=>assert_equals(
      exp = av_template_name
      act = lo_template->get_templatename( )
      msg = |Template { av_template_name } was not created correctly|
    ).
  ENDMETHOD.

  METHOD get_template.
    " Ensure template exists
    TRY.
        DATA(lo_template_obj) = NEW /aws1/cl_sestemplate(
          iv_templatename = av_template_name
          iv_subjectpart = 'Test Subject'
          iv_textpart = 'Test Text'
          iv_htmlpart = '<p>Test HTML</p>'
        ).
        ao_ses->createtemplate( io_template = lo_template_obj ).
      CATCH /aws1/cx_sesalreadyexistsex.
        " Already exists
    ENDTRY.

    " iv_template_name = 'test-template'
    DATA(lo_template) = ao_ses_actions->get_template( iv_template_name = av_template_name ).

    cl_abap_unit_assert=>assert_equals(
      exp = av_template_name
      act = lo_template->get_templatename( )
      msg = |Could not retrieve template { av_template_name }|
    ).
  ENDMETHOD.

  METHOD list_templates.
    " Ensure at least one template exists
    TRY.
        DATA(lo_template) = NEW /aws1/cl_sestemplate(
          iv_templatename = av_template_name
          iv_subjectpart = 'Test Subject'
          iv_textpart = 'Test Text'
          iv_htmlpart = '<p>Test HTML</p>'
        ).
        ao_ses->createtemplate( io_template = lo_template ).
      CATCH /aws1/cx_sesalreadyexistsex.
        " Already exists
    ENDTRY.

    " iv_max_items = 10
    DATA(lt_templates) = ao_ses_actions->list_templates( iv_max_items = 10 ).

    cl_abap_unit_assert=>assert_not_initial(
      act = lt_templates
      msg = 'No templates were found'
    ).

    " Verify our template is in the list
    DATA lv_found TYPE abap_bool.
    LOOP AT lt_templates INTO DATA(lo_tmpl).
      IF lo_tmpl->get_name( ) = av_template_name.
        lv_found = abap_true.
        EXIT.
      ENDIF.
    ENDLOOP.

    cl_abap_unit_assert=>assert_true(
      act = lv_found
      msg = |Template { av_template_name } not found in list|
    ).
  ENDMETHOD.

  METHOD send_templated_email.
    " First ensure template exists
    TRY.
        DATA(lo_template) = NEW /aws1/cl_sestemplate(
          iv_templatename = av_template_name
          iv_subjectpart = 'Hello {{name}}'
          iv_textpart = 'Plain text for {{name}}'
          iv_htmlpart = '<p>HTML for {{name}}</p>'
        ).
        ao_ses->createtemplate( io_template = lo_template ).
      CATCH /aws1/cx_sesalreadyexistsex.
        " Already exists
    ENDTRY.

    " Ensure sender is verified
    TRY.
        ao_ses->verifyemailidentity( iv_emailaddress = av_email ).
      CATCH /aws1/cx_rt_generic.
        " May already exist
    ENDTRY.

    " iv_source = 'sender@example.com'
    DATA lt_to_addresses TYPE /aws1/cl_sesaddresslist_w=>tt_addresslist.
    APPEND NEW /aws1/cl_sesaddresslist_w( iv_value = av_email ) TO lt_to_addresses.

    DATA(lo_destination) = NEW /aws1/cl_sesdestination(
      it_toaddresses = lt_to_addresses
    ).

    " iv_template_name = 'test-template'
    " iv_template_data = '{"name":"John Doe"}'
    TRY.
        DATA(lv_msg_id) = ao_ses_actions->send_templated_email(
          iv_source = av_email
          io_destination = lo_destination
          iv_template_name = av_template_name
          iv_template_data = '{"name":"ABAP Tester"}'
        ).

        cl_abap_unit_assert=>assert_not_initial(
          act = lv_msg_id
          msg = 'No message ID was returned'
        ).
      CATCH /aws1/cx_sesmessagerejected.
        " Expected in sandbox mode
      CATCH /aws1/cx_sesacctsendingpause00.
        " Expected if account is paused
    ENDTRY.
  ENDMETHOD.

  METHOD update_template.
    " Ensure template exists
    TRY.
        DATA(lo_template_obj) = NEW /aws1/cl_sestemplate(
          iv_templatename = av_template_name
          iv_subjectpart = 'Original Subject'
          iv_textpart = 'Original Text'
          iv_htmlpart = '<p>Original HTML</p>'
        ).
        ao_ses->createtemplate( io_template = lo_template_obj ).
      CATCH /aws1/cx_sesalreadyexistsex.
        " Already exists
    ENDTRY.

    " iv_name = 'test-template'
    " iv_subject = 'Updated Subject'
    " iv_text = 'Updated Text'
    " iv_html = '<p>Updated HTML</p>'
    ao_ses_actions->update_template(
      iv_name = av_template_name
      iv_subject = 'Updated Subject {{name}}'
      iv_text = 'Updated text for {{name}}'
      iv_html = '<html><body><p>Updated HTML for {{name}}</p></body></html>'
    ).

    " Verify template was updated
    DATA(lo_result) = ao_ses->gettemplate( iv_templatename = av_template_name ).
    DATA(lo_template) = lo_result->get_template( ).

    cl_abap_unit_assert=>assert_equals(
      exp = 'Updated Subject {{name}}'
      act = lo_template->get_subjectpart( )
      msg = |Template { av_template_name } was not updated correctly|
    ).
  ENDMETHOD.

  METHOD delete_template.
    " Ensure template exists
    TRY.
        DATA(lo_template) = NEW /aws1/cl_sestemplate(
          iv_templatename = av_template_name
          iv_subjectpart = 'Test Subject'
          iv_textpart = 'Test Text'
          iv_htmlpart = '<p>Test HTML</p>'
        ).
        ao_ses->createtemplate( io_template = lo_template ).
      CATCH /aws1/cx_sesalreadyexistsex.
        " Already exists
    ENDTRY.

    " iv_template_name = 'test-template'
    ao_ses_actions->delete_template( iv_template_name = av_template_name ).

    " Verify template was deleted
    TRY.
        ao_ses->gettemplate( iv_templatename = av_template_name ).
        cl_abap_unit_assert=>fail( msg = |Template { av_template_name } should have been deleted| ).
      CATCH /aws1/cx_sestmpldoesnotexistex.
        " Expected - template was deleted
    ENDTRY.
  ENDMETHOD.

  METHOD create_receipt_filter.
    " iv_filter_name = 'test-filter'
    " iv_ip_address_or_range = '192.0.2.0/24'
    " iv_allow = abap_false (Block)
    ao_ses_actions->create_receipt_filter(
      iv_filter_name = av_filter_name
      iv_ip_address_or_range = '192.0.2.0/24'
      iv_allow = abap_false
    ).

    " Verify filter was created
    DATA(lo_result) = ao_ses->listreceiptfilters( ).
    DATA(lt_filters) = lo_result->get_filters( ).

    DATA lv_found TYPE abap_bool.
    LOOP AT lt_filters INTO DATA(lo_filter).
      IF lo_filter->get_name( ) = av_filter_name.
        lv_found = abap_true.
        EXIT.
      ENDIF.
    ENDLOOP.

    cl_abap_unit_assert=>assert_true(
      act = lv_found
      msg = |Receipt filter { av_filter_name } was not created|
    ).
  ENDMETHOD.

  METHOD list_receipt_filters.
    " Ensure at least one filter exists
    TRY.
        DATA(lo_ip_filter) = NEW /aws1/cl_sesreceiptipfilter(
          iv_policy = 'Block'
          iv_cidr = '192.0.2.0/24'
        ).
        DATA(lo_filter) = NEW /aws1/cl_sesreceiptfilter(
          iv_name = av_filter_name
          io_ipfilter = lo_ip_filter
        ).
        ao_ses->createreceiptfilter( io_filter = lo_filter ).
      CATCH /aws1/cx_sesalreadyexistsex.
        " Already exists
    ENDTRY.

    DATA(lt_filters) = ao_ses_actions->list_receipt_filters( ).

    cl_abap_unit_assert=>assert_not_initial(
      act = lt_filters
      msg = 'No receipt filters were found'
    ).
  ENDMETHOD.

  METHOD delete_receipt_filter.
    " Ensure filter exists
    TRY.
        DATA(lo_ip_filter) = NEW /aws1/cl_sesreceiptipfilter(
          iv_policy = 'Block'
          iv_cidr = '192.0.2.0/24'
        ).
        DATA(lo_filter) = NEW /aws1/cl_sesreceiptfilter(
          iv_name = av_filter_name
          io_ipfilter = lo_ip_filter
        ).
        ao_ses->createreceiptfilter( io_filter = lo_filter ).
      CATCH /aws1/cx_sesalreadyexistsex.
        " Already exists
    ENDTRY.

    " iv_filter_name = 'test-filter'
    ao_ses_actions->delete_receipt_filter( iv_filter_name = av_filter_name ).

    " Verify filter was deleted
    DATA(lo_result) = ao_ses->listreceiptfilters( ).
    DATA(lt_filters) = lo_result->get_filters( ).

    DATA lv_found TYPE abap_bool.
    LOOP AT lt_filters INTO DATA(lo_filt).
      IF lo_filt->get_name( ) = av_filter_name.
        lv_found = abap_true.
        EXIT.
      ENDIF.
    ENDLOOP.

    cl_abap_unit_assert=>assert_false(
      act = lv_found
      msg = |Receipt filter { av_filter_name } should have been deleted|
    ).
  ENDMETHOD.

  METHOD create_receipt_rule_set.
    " iv_rule_set_name = 'test-rule-set'
    ao_ses_actions->create_receipt_rule_set( iv_rule_set_name = av_rule_set_name ).

    " Verify rule set was created
    DATA(lo_result) = ao_ses->describereceiptruleset( iv_rulesetname = av_rule_set_name ).

    cl_abap_unit_assert=>assert_not_initial(
      act = lo_result->get_metadata( )
      msg = |Receipt rule set { av_rule_set_name } was not created|
    ).
  ENDMETHOD.

  METHOD create_s3_copy_rule.
    " Ensure rule set exists
    TRY.
        ao_ses->createreceiptruleset( iv_rulesetname = av_rule_set_name ).
      CATCH /aws1/cx_sesalreadyexistsex.
        " Already exists
    ENDTRY.

    " iv_rule_set_name = 'test-rule-set'
    " iv_rule_name = 'test-rule'
    " it_recipients contains email addresses
    DATA lt_recipients TYPE /aws1/cl_sesrecipientslist_w=>tt_recipientslist.
    APPEND NEW /aws1/cl_sesrecipientslist_w( iv_value = av_email ) TO lt_recipients.

    " iv_bucket_name = 'test-bucket'
    " iv_prefix = 'emails/'
    ao_ses_actions->create_s3_copy_rule(
      iv_rule_set_name = av_rule_set_name
      iv_rule_name = 'test-s3-rule'
      it_recipients = lt_recipients
      iv_bucket_name = av_bucket_name
      iv_prefix = 'emails/'
    ).

    " Verify rule was created
    DATA(lo_result) = ao_ses->describereceiptruleset( iv_rulesetname = av_rule_set_name ).
    DATA(lt_rules) = lo_result->get_rules( ).

    cl_abap_unit_assert=>assert_not_initial(
      act = lt_rules
      msg = |Receipt rule was not created in rule set { av_rule_set_name }|
    ).
  ENDMETHOD.

  METHOD describe_receipt_rule_set.
    " Ensure rule set exists
    TRY.
        ao_ses->createreceiptruleset( iv_rulesetname = av_rule_set_name ).
      CATCH /aws1/cx_sesalreadyexistsex.
        " Already exists
    ENDTRY.

    " iv_rule_set_name = 'test-rule-set'
    DATA(lo_result) = ao_ses_actions->describe_receipt_rule_set(
      iv_rule_set_name = av_rule_set_name
    ).

    cl_abap_unit_assert=>assert_not_initial(
      act = lo_result->get_metadata( )
      msg = |Could not describe receipt rule set { av_rule_set_name }|
    ).
  ENDMETHOD.

  METHOD delete_receipt_rule.
    " Ensure rule set and rule exist
    TRY.
        ao_ses->createreceiptruleset( iv_rulesetname = av_rule_set_name ).
      CATCH /aws1/cx_sesalreadyexistsex.
    ENDTRY.

    DATA lt_recipients TYPE /aws1/cl_sesrecipientslist_w=>tt_recipientslist.
    APPEND NEW /aws1/cl_sesrecipientslist_w( iv_value = av_email ) TO lt_recipients.

    DATA(lo_s3_action) = NEW /aws1/cl_sess3action(
      iv_bucketname = av_bucket_name
      iv_objectkeyprefix = 'emails/'
    ).
    DATA(lo_action) = NEW /aws1/cl_sesreceiptaction( io_s3action = lo_s3_action ).
    DATA lt_actions TYPE /aws1/cl_sesreceiptaction=>tt_receiptactionslist.
    APPEND lo_action TO lt_actions.

    DATA(lo_rule) = NEW /aws1/cl_sesreceiptrule(
      iv_name = 'test-delete-rule'
      iv_enabled = abap_true
      it_recipients = lt_recipients
      it_actions = lt_actions
    ).

    TRY.
        ao_ses->createreceiptrule(
          iv_rulesetname = av_rule_set_name
          io_rule = lo_rule
        ).
      CATCH /aws1/cx_sesalreadyexistsex.
    ENDTRY.

    " iv_rule_set_name = 'test-rule-set'
    " iv_rule_name = 'test-rule'
    ao_ses_actions->delete_receipt_rule(
      iv_rule_set_name = av_rule_set_name
      iv_rule_name = 'test-delete-rule'
    ).

    " Verify rule was deleted
    DATA(lo_result) = ao_ses->describereceiptruleset( iv_rulesetname = av_rule_set_name ).
    DATA(lt_rules) = lo_result->get_rules( ).

    DATA lv_found TYPE abap_bool.
    LOOP AT lt_rules INTO DATA(lo_rule_item).
      IF lo_rule_item->get_name( ) = 'test-delete-rule'.
        lv_found = abap_true.
        EXIT.
      ENDIF.
    ENDLOOP.

    cl_abap_unit_assert=>assert_false(
      act = lv_found
      msg = 'Receipt rule should have been deleted'
    ).
  ENDMETHOD.

  METHOD delete_receipt_rule_set.
    " Create a unique rule set for deletion test
    DATA lv_uuid TYPE guid_32.
    TRY.
        lv_uuid = cl_system_uuid=>create_uuid_x16_static( ).
      CATCH cx_uuid_error.
        " Fallback to random string
        lv_uuid = /awsex/cl_utils=>get_random_string( ).
    ENDTRY.
    DATA lv_uuid_string TYPE string.
    lv_uuid_string = lv_uuid.
    TRANSLATE lv_uuid_string TO LOWER CASE.
    REPLACE ALL OCCURRENCES OF '-' IN lv_uuid_string WITH ''.
    DATA(lv_delete_rule_set) = |ses-del-rs-{ lv_uuid_string(10) }|.

    TRY.
        ao_ses->createreceiptruleset( iv_rulesetname = lv_delete_rule_set ).
      CATCH /aws1/cx_sesalreadyexistsex.
    ENDTRY.

    " iv_rule_set_name = 'test-rule-set'
    ao_ses_actions->delete_receipt_rule_set( iv_rule_set_name = lv_delete_rule_set ).

    " Verify rule set was deleted
    TRY.
        ao_ses->describereceiptruleset( iv_rulesetname = lv_delete_rule_set ).
        cl_abap_unit_assert=>fail( msg = |Rule set { lv_delete_rule_set } should have been deleted| ).
      CATCH /aws1/cx_sesrulesetdoesnotexex.
        " Expected - rule set was deleted
    ENDTRY.
  ENDMETHOD.

  METHOD delete_identity.
    " Ensure identity exists
    TRY.
        ao_ses->verifyemailidentity( iv_emailaddress = av_email ).
      CATCH /aws1/cx_rt_generic.
        " May already exist
    ENDTRY.

    WAIT UP TO 2 SECONDS.

    " iv_identity = 'test@example.com'
    ao_ses_actions->delete_identity( iv_identity = av_email ).

    " Wait for deletion to propagate
    WAIT UP TO 2 SECONDS.

    " Verify identity was deleted by checking status
    DATA lt_identities TYPE /aws1/cl_sesidentitylist_w=>tt_identitylist.
    APPEND NEW /aws1/cl_sesidentitylist_w( iv_value = av_email ) TO lt_identities.

    DATA(lo_result) = ao_ses->getidentityverificationattrs(
      it_identities = lt_identities
    ).

    DATA(lt_attrs) = lo_result->get_verificationattributes( ).
    " After deletion, the identity should not be in the verification attributes
    cl_abap_unit_assert=>assert_initial(
      act = lt_attrs
      msg = |Identity { av_email } should have been deleted|
    ).
  ENDMETHOD.

  METHOD wait_for_identity_verification.
    " Helper method to wait for identity verification
    " Note: This is not used in automated tests as email verification
    " cannot be completed programmatically
    DATA lv_start_time TYPE timestamp.
    DATA lv_current_time TYPE timestamp.
    DATA lv_elapsed TYPE i.

    GET TIME STAMP FIELD lv_start_time.

    DO.
      DATA(lv_status) = ao_ses_actions->get_identity_status( iv_identity = iv_identity ).

      IF lv_status = 'Success'.
        EXIT.
      ENDIF.

      WAIT UP TO 5 SECONDS.

      GET TIME STAMP FIELD lv_current_time.
      lv_elapsed = lv_current_time - lv_start_time.

      IF lv_elapsed >= iv_timeout.
        EXIT.
      ENDIF.
    ENDDO.
  ENDMETHOD.

ENDCLASS.
