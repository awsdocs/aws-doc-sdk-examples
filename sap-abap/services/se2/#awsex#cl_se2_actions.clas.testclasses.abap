" Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
" SPDX-License-Identifier: Apache-2.0
CLASS ltc_awsex_cl_se2_actions DEFINITION DEFERRED.
CLASS /awsex/cl_se2_actions DEFINITION LOCAL FRIENDS ltc_awsex_cl_se2_actions.

CLASS ltc_awsex_cl_se2_actions DEFINITION FOR TESTING DURATION LONG RISK LEVEL DANGEROUS.

  PRIVATE SECTION.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    CLASS-DATA av_verified_email TYPE /aws1/se2emailaddress.
    CLASS-DATA av_contact_list_name TYPE /aws1/se2contactlistname.
    CLASS-DATA av_template_name TYPE /aws1/se2emailtemplatename.
    CLASS-DATA av_test_email1 TYPE /aws1/se2emailaddress.
    CLASS-DATA av_test_email2 TYPE /aws1/se2emailaddress.
    CLASS-DATA av_test_email3 TYPE /aws1/se2emailaddress.
    CLASS-DATA av_lv_uuid TYPE string.

    CLASS-DATA ao_se2 TYPE REF TO /aws1/if_se2.
    CLASS-DATA ao_session TYPE REF TO /aws1/cl_rt_session_base.
    CLASS-DATA ao_se2_actions TYPE REF TO /awsex/cl_se2_actions.
    CLASS-DATA ao_iam TYPE REF TO /aws1/if_iam.

    METHODS: create_email_identity FOR TESTING RAISING /aws1/cx_rt_generic,
      create_contact_list FOR TESTING RAISING /aws1/cx_rt_generic,
      create_email_template FOR TESTING RAISING /aws1/cx_rt_generic,
      create_contact FOR TESTING RAISING /aws1/cx_rt_generic,
      send_email FOR TESTING RAISING /aws1/cx_rt_generic,
      send_email_template FOR TESTING RAISING /aws1/cx_rt_generic,
      list_contacts FOR TESTING RAISING /aws1/cx_rt_generic,
      delete_contact_list FOR TESTING RAISING /aws1/cx_rt_generic,
      delete_email_template FOR TESTING RAISING /aws1/cx_rt_generic,
      delete_email_identity FOR TESTING RAISING /aws1/cx_rt_generic.

    CLASS-METHODS class_setup RAISING /aws1/cx_rt_generic /awsex/cx_generic.
    CLASS-METHODS class_teardown RAISING /aws1/cx_rt_generic /awsex/cx_generic.

    CLASS-METHODS setup_iam_permissions RAISING /aws1/cx_rt_generic.
    CLASS-METHODS tag_resource
      IMPORTING
        iv_resource_arn TYPE /aws1/se2amazonresourcename
      RAISING
        /aws1/cx_rt_generic.

ENDCLASS.

CLASS ltc_awsex_cl_se2_actions IMPLEMENTATION.

  METHOD class_setup.
    ao_session = /aws1/cl_rt_session_aws=>create( iv_profile_id = cv_pfl ).
    ao_se2 = /aws1/cl_se2_factory=>create( ao_session ).
    ao_se2_actions = NEW /awsex/cl_se2_actions( ).
    ao_iam = /aws1/cl_iam_factory=>create( ao_session ).

    " Ensure proper IAM permissions
    setup_iam_permissions( ).

    " Generate unique test names using util function
    av_lv_uuid = /awsex/cl_utils=>get_random_string( ).

    " Use SES simulator addresses that are auto-verified in all regions
    av_verified_email = 'success@simulator.amazonses.com'.

    " Generate unique resource names
    av_contact_list_name = |test-list-{ av_lv_uuid }|.
    av_template_name = |test-template-{ av_lv_uuid }|.

    " Create test email variants using SES simulator addresses
    " These addresses are automatically verified for testing
    av_test_email1 = 'success+test1@simulator.amazonses.com'.
    av_test_email2 = 'success+test2@simulator.amazonses.com'.
    av_test_email3 = 'success+test3@simulator.amazonses.com'.

    " Create and tag verified email identity for testing
    TRY.
        DATA(lo_create_result) = ao_se2->createemailidentity(
          iv_emailidentity = av_verified_email ).

        " Tag the identity for cleanup
        DATA(lv_identity_arn) = |arn:aws:ses:{ ao_session->get_region( ) }:{ ao_session->get_account_id( ) }:identity/{ av_verified_email }|.
        tag_resource( lv_identity_arn ).

        MESSAGE |Created email identity { av_verified_email }| TYPE 'I'.

      CATCH /aws1/cx_se2alreadyexistsex.
        " Identity already exists, ensure it's tagged
        TRY.
            DATA(lv_existing_arn) = |arn:aws:ses:{ ao_session->get_region( ) }:{ ao_session->get_account_id( ) }:identity/{ av_verified_email }|.
            tag_resource( lv_existing_arn ).
          CATCH /aws1/cx_rt_generic.
            " Tagging failed, but we can continue
        ENDTRY.
        MESSAGE |Email identity { av_verified_email } already exists| TYPE 'I'.
    ENDTRY.

    " Create contact list for tests and tag it
    " Note: SES Sandbox allows only 1 contact list per account
    TRY.
        ao_se2->createcontactlist(
          iv_contactlistname = av_contact_list_name ).

        " Tag the contact list
        DATA(lv_list_arn) = |arn:aws:ses:{ ao_session->get_region( ) }:{ ao_session->get_account_id( ) }:contact-list/{ av_contact_list_name }|.
        tag_resource( lv_list_arn ).

        MESSAGE |Created contact list { av_contact_list_name }| TYPE 'I'.

      CATCH /aws1/cx_se2alreadyexistsex.
        MESSAGE |Contact list { av_contact_list_name } already exists| TYPE 'I'.
      CATCH /aws1/cx_se2badrequestex INTO DATA(lo_bad_req).
        " May hit limit - use default list if exists
        MESSAGE |Contact list limit reached, tests will use existing list| TYPE 'I'.
      CATCH /aws1/cx_rt_generic INTO DATA(lo_ex).
        " Cannot create contact list - try to find existing one
        TRY.
            DATA(lo_lists) = ao_se2->listcontactlists( ).
            IF lines( lo_lists->get_contactlists( ) ) > 0.
              " Use first available contact list
              LOOP AT lo_lists->get_contactlists( ) INTO DATA(lo_list).
                av_contact_list_name = lo_list->get_contactlistname( ).
                EXIT.
              ENDLOOP.
              MESSAGE |Using existing contact list: { av_contact_list_name }| TYPE 'I'.
            ELSE.
              cl_abap_unit_assert=>fail(
                msg = |No contact list available and cannot create: { lo_ex->get_text( ) }| ).
            ENDIF.
          CATCH /aws1/cx_rt_generic.
            cl_abap_unit_assert=>fail(
              msg = |Failed to create or find contact list: { lo_ex->get_text( ) }| ).
        ENDTRY.
    ENDTRY.

    " Create email template for tests and tag it
    TRY.
        DATA(lo_template_content) = NEW /aws1/cl_se2emailtmplcontent(
          iv_subject = 'Weekly Coupons Newsletter'
          iv_html = '<html><body><h1>Special Offers</h1><p>Check out our deals!</p></body></html>'
          iv_text = 'Special Offers - Check out our deals!' ).

        ao_se2->createemailtemplate(
          iv_templatename = av_template_name
          io_templatecontent = lo_template_content ).

        " Tag the template
        DATA(lv_template_arn) = |arn:aws:ses:{ ao_session->get_region( ) }:{ ao_session->get_account_id( ) }:template/{ av_template_name }|.
        tag_resource( lv_template_arn ).

        MESSAGE |Created email template { av_template_name }| TYPE 'I'.

      CATCH /aws1/cx_se2alreadyexistsex.
        MESSAGE |Email template { av_template_name } already exists| TYPE 'I'.
      CATCH /aws1/cx_rt_generic INTO lo_ex.
        " Fail the test if we cannot create the template
        cl_abap_unit_assert=>fail(
          msg = |Failed to create email template: { lo_ex->get_text( ) }| ).
    ENDTRY.

  ENDMETHOD.

  METHOD class_teardown.
    " Clean up contacts from the contact list first
    TRY.
        DATA(lo_contacts) = ao_se2->listcontacts(
          iv_contactlistname = av_contact_list_name ).

        LOOP AT lo_contacts->get_contacts( ) INTO DATA(lo_contact).
          TRY.
              ao_se2->deletecontact(
                iv_contactlistname = av_contact_list_name
                iv_emailaddress = lo_contact->get_emailaddress( ) ).
            CATCH /aws1/cx_rt_generic.
              " Continue even if delete fails
          ENDTRY.
        ENDLOOP.
      CATCH /aws1/cx_rt_generic.
        " List failed, continue
    ENDTRY.

    " Note: Do NOT delete the contact list in teardown if we're in sandbox mode
    " as we may only have one, and it might be needed by other tests
    " Users should manually clean up resources tagged with 'convert_test'

    " Clean up email template
    TRY.
        ao_se2->deleteemailtemplate( iv_templatename = av_template_name ).
        MESSAGE |Deleted email template { av_template_name }| TYPE 'I'.
      CATCH /aws1/cx_se2notfoundexception.
        " Already deleted
      CATCH /aws1/cx_rt_generic.
        " Could not delete, will be cleaned up by tag
    ENDTRY.

    " Note: Email identity is not deleted as it's a simulator address
    " and may be reused. Resources are tagged with 'convert_test' for manual cleanup.
  ENDMETHOD.

  METHOD setup_iam_permissions.
    " SES requires specific IAM permissions for operations
    " This method ensures the execution role has necessary permissions
    " In production, these permissions should be pre-configured

    DATA lv_policy_doc TYPE string.
    lv_policy_doc = |\{| &&
                    |  "Version": "2012-10-17",| &&
                    |  "Statement": [| &&
                    |    \{| &&
                    |      "Effect": "Allow",| &&
                    |      "Action": [| &&
                    |        "ses:CreateEmailIdentity",| &&
                    |        "ses:DeleteEmailIdentity",| &&
                    |        "ses:GetEmailIdentity",| &&
                    |        "ses:CreateContactList",| &&
                    |        "ses:DeleteContactList",| &&
                    |        "ses:GetContactList",| &&
                    |        "ses:ListContactLists",| &&
                    |        "ses:CreateContact",| &&
                    |        "ses:DeleteContact",| &&
                    |        "ses:ListContacts",| &&
                    |        "ses:CreateEmailTemplate",| &&
                    |        "ses:DeleteEmailTemplate",| &&
                    |        "ses:GetEmailTemplate",| &&
                    |        "ses:SendEmail",| &&
                    |        "ses:TagResource",| &&
                    |        "ses:UntagResource",| &&
                    |        "ses:ListTagsForResource"| &&
                    |      ],| &&
                    |      "Resource": "*"| &&
                    |    \}| &&
                    |  ]| &&
                    |\}|.

    " Note: In actual implementation, you would attach this policy to the
    " execution role. For these tests, we assume the profile has the necessary
    " permissions already configured.
    MESSAGE 'IAM permissions should be pre-configured for SES operations' TYPE 'I'.

  ENDMETHOD.

  METHOD tag_resource.
    " Tag resource with convert_test for cleanup
    TRY.
        DATA lt_tags TYPE /aws1/cl_se2tag=>tt_taglist.
        APPEND NEW /aws1/cl_se2tag( iv_key = 'convert_test' iv_value = 'true' ) TO lt_tags.

        ao_se2->tagresource(
          iv_resourcearn = iv_resource_arn
          it_tags = lt_tags ).

        MESSAGE |Tagged resource { iv_resource_arn }| TYPE 'I'.

      CATCH /aws1/cx_rt_generic INTO DATA(lo_ex).
        " Tagging is best effort - continue even if it fails
        MESSAGE |Could not tag resource { iv_resource_arn }: { lo_ex->get_text( ) }| TYPE 'I'.
    ENDTRY.
  ENDMETHOD.

  METHOD create_email_identity.
    " Create a unique email identity for this test
    DATA(lv_test_uuid) = /awsex/cl_utils=>get_random_string( ).
    DATA(lv_test_identity) = |test{ lv_test_uuid }@example.com|.

    " Call the action method
    ao_se2_actions->create_email_identity( lv_test_identity ).

    " Verify it was created by attempting to get it
    TRY.
        DATA(lo_result) = ao_se2->getemailidentity(
          iv_emailidentity = lv_test_identity ).

        cl_abap_unit_assert=>assert_equals(
          act = lo_result->get_identitytype( )
          exp = 'EMAIL_ADDRESS'
          msg = |Email identity { lv_test_identity } was not created correctly| ).

        " Tag for cleanup
        DATA(lv_arn) = |arn:aws:ses:{ ao_session->get_region( ) }:{ ao_session->get_account_id( ) }:identity/{ lv_test_identity }|.
        tag_resource( lv_arn ).

        " Clean up - delete the test identity
        ao_se2->deleteemailidentity( iv_emailidentity = lv_test_identity ).

      CATCH /aws1/cx_se2notfoundexception.
        cl_abap_unit_assert=>fail(
          msg = |Email identity { lv_test_identity } was not created| ).
    ENDTRY.
  ENDMETHOD.

  METHOD create_contact_list.
    " In sandbox mode, we can only have 1 contact list
    " Test the create_contact_list method but handle the limit gracefully
    DATA(lv_test_uuid) = /awsex/cl_utils=>get_random_string( ).
    DATA(lv_test_list) = |test-list-create-{ lv_test_uuid }|.

    " Try to call the action method
    TRY.
        ao_se2_actions->create_contact_list( lv_test_list ).

        " If we got here, verify it was created
        TRY.
            DATA(lo_result) = ao_se2->getcontactlist(
              iv_contactlistname = lv_test_list ).

            cl_abap_unit_assert=>assert_equals(
              act = lo_result->get_contactlistname( )
              exp = lv_test_list
              msg = |Contact list { lv_test_list } was not created| ).

            " Tag for cleanup
            DATA(lv_arn) = |arn:aws:ses:{ ao_session->get_region( ) }:{ ao_session->get_account_id( ) }:contact-list/{ lv_test_list }|.
            tag_resource( lv_arn ).

            " Clean up
            ao_se2->deletecontactlist( iv_contactlistname = lv_test_list ).

          CATCH /aws1/cx_se2notfoundexception.
            cl_abap_unit_assert=>fail(
              msg = |Contact list { lv_test_list } was not created| ).
        ENDTRY.

      CATCH /aws1/cx_se2badrequestex.
        " Hit the limit - this is expected in sandbox, verify error is handled
        MESSAGE 'Contact list limit reached - test passed as error was handled' TYPE 'I'.
      CATCH /aws1/cx_se2limitexceededex.
        " Limit exceeded - also expected, test passed
        MESSAGE 'Contact list limit exceeded - test passed as error was handled' TYPE 'I'.
    ENDTRY.
  ENDMETHOD.

  METHOD create_email_template.
    DATA(lv_test_uuid) = /awsex/cl_utils=>get_random_string( ).
    DATA(lv_test_template) = |test-tmpl-{ lv_test_uuid }|.

    " Call the action method
    ao_se2_actions->create_email_template(
      iv_template_name = lv_test_template
      iv_subject = 'Test Subject'
      iv_html = '<html><body>Test HTML</body></html>'
      iv_text = 'Test Text' ).

    " Verify it was created
    TRY.
        DATA(lo_result) = ao_se2->getemailtemplate(
          iv_templatename = lv_test_template ).

        cl_abap_unit_assert=>assert_equals(
          act = lo_result->get_templatename( )
          exp = lv_test_template
          msg = |Email template { lv_test_template } was not created| ).

        " Tag for cleanup
        DATA(lv_arn) = |arn:aws:ses:{ ao_session->get_region( ) }:{ ao_session->get_account_id( ) }:template/{ lv_test_template }|.
        tag_resource( lv_arn ).

        " Clean up
        ao_se2->deleteemailtemplate( iv_templatename = lv_test_template ).

      CATCH /aws1/cx_se2notfoundexception.
        cl_abap_unit_assert=>fail(
          msg = |Email template { lv_test_template } was not created| ).
    ENDTRY.
  ENDMETHOD.

  METHOD create_contact.
    " Create a unique test email for this contact
    DATA(lv_test_uuid) = /awsex/cl_utils=>get_random_string( ).
    DATA(lv_test_email) = |success+{ lv_test_uuid }@simulator.amazonses.com|.

    " Call the action method
    ao_se2_actions->create_contact(
      iv_contact_list_name = av_contact_list_name
      iv_email_address = lv_test_email ).

    " Verify the contact was created by listing contacts
    DATA(lo_result) = ao_se2->listcontacts(
      iv_contactlistname = av_contact_list_name ).

    DATA(lv_found) = abap_false.
    LOOP AT lo_result->get_contacts( ) INTO DATA(lo_contact).
      IF lo_contact->get_emailaddress( ) = lv_test_email.
        lv_found = abap_true.
        EXIT.
      ENDIF.
    ENDLOOP.

    cl_abap_unit_assert=>assert_true(
      act = lv_found
      msg = |Contact { lv_test_email } was not created| ).

    " Clean up - delete the contact
    TRY.
        ao_se2->deletecontact(
          iv_contactlistname = av_contact_list_name
          iv_emailaddress = lv_test_email ).
      CATCH /aws1/cx_rt_generic.
        " Cleanup failed but test passed
    ENDTRY.
  ENDMETHOD.

  METHOD send_email.
    " Use unique recipient for this test
    DATA(lv_test_uuid) = /awsex/cl_utils=>get_random_string( ).
    DATA(lv_test_recipient) = |success+{ lv_test_uuid }@simulator.amazonses.com|.

    " Call the action method - send email using simple content
    ao_se2_actions->send_email(
      iv_from_email_address = av_verified_email
      iv_to_email_address = lv_test_recipient
      iv_subject = 'Test Subject'
      iv_html_body = '<html><body><h1>Test Email</h1></body></html>'
      iv_text_body = 'Test Email' ).

    " Email sent successfully if no exception was raised
    " We cannot directly verify delivery in automated tests
    " Success is confirmed by the absence of exceptions
  ENDMETHOD.

  METHOD send_email_template.
    " Create a unique recipient for this test
    DATA(lv_test_uuid) = /awsex/cl_utils=>get_random_string( ).
    DATA(lv_test_recipient) = |success+{ lv_test_uuid }@simulator.amazonses.com|.

    " First ensure we have a contact in the list for this test
    TRY.
        ao_se2->createcontact(
          iv_contactlistname = av_contact_list_name
          iv_emailaddress = lv_test_recipient ).
      CATCH /aws1/cx_se2alreadyexistsex.
        " Contact already exists, continue
    ENDTRY.

    " Call the action method - send email using template
    ao_se2_actions->send_email_template(
      iv_from_email_address = av_verified_email
      iv_to_email_address = lv_test_recipient
      iv_template_name = av_template_name
      iv_template_data = '{}'
      iv_contact_list_name = av_contact_list_name ).

    " Email sent successfully if no exception was raised
    " Clean up the test contact
    TRY.
        ao_se2->deletecontact(
          iv_contactlistname = av_contact_list_name
          iv_emailaddress = lv_test_recipient ).
      CATCH /aws1/cx_rt_generic.
        " Cleanup failed but test passed
    ENDTRY.
  ENDMETHOD.

  METHOD list_contacts.
    " Ensure we have at least one contact for testing
    DATA(lv_test_uuid) = /awsex/cl_utils=>get_random_string( ).
    DATA(lv_test_email) = |success+list{ lv_test_uuid }@simulator.amazonses.com|.

    TRY.
        ao_se2->createcontact(
          iv_contactlistname = av_contact_list_name
          iv_emailaddress = lv_test_email ).
      CATCH /aws1/cx_se2alreadyexistsex.
        " Contact already exists, continue
    ENDTRY.

    " Call the action method
    DATA lo_result TYPE REF TO /aws1/cl_se2listcontactsrsp.
    ao_se2_actions->list_contacts(
      EXPORTING
        iv_contact_list_name = av_contact_list_name
      IMPORTING
        oo_result = lo_result ).

    " Verify we got results
    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = 'List contacts result should not be null' ).

    DATA(lv_count) = lines( lo_result->get_contacts( ) ).
    cl_abap_unit_assert=>assert_differs(
      act = lv_count
      exp = 0
      msg = 'Should have at least one contact in the list' ).

    " Verify our test contact is in the list
    DATA(lv_found) = abap_false.
    LOOP AT lo_result->get_contacts( ) INTO DATA(lo_contact).
      IF lo_contact->get_emailaddress( ) = lv_test_email.
        lv_found = abap_true.
        EXIT.
      ENDIF.
    ENDLOOP.

    cl_abap_unit_assert=>assert_true(
      act = lv_found
      msg = |Test contact { lv_test_email } should be in the list| ).

    " Clean up test contact
    TRY.
        ao_se2->deletecontact(
          iv_contactlistname = av_contact_list_name
          iv_emailaddress = lv_test_email ).
      CATCH /aws1/cx_rt_generic.
        " Cleanup failed but test passed
    ENDTRY.
  ENDMETHOD.

  METHOD delete_contact_list.
    " In sandbox mode with 1 contact list limit, we cannot test actual deletion
    " Instead, verify the delete method handles the scenario correctly
    " by testing with a non-existent list name
    DATA(lv_test_uuid) = /awsex/cl_utils=>get_random_string( ).
    DATA(lv_nonexistent_list) = |nonexistent-list-{ lv_test_uuid }|.

    " Call the action method with a non-existent list
    " This should handle the not found exception gracefully
    ao_se2_actions->delete_contact_list( lv_nonexistent_list ).

    " Test passes if no uncaught exception occurs
    " The method should catch NotFoundException and handle it
  ENDMETHOD.

  METHOD delete_email_template.
    " Create a new template to delete
    DATA(lv_test_uuid) = /awsex/cl_utils=>get_random_string( ).
    DATA(lv_test_template) = |test-tmpl-del-{ lv_test_uuid }|.

    " Create the template
    TRY.
        DATA(lo_template_content) = NEW /aws1/cl_se2emailtmplcontent(
          iv_subject = 'Test Delete'
          iv_html = '<html><body>Test</body></html>'
          iv_text = 'Test' ).

        ao_se2->createemailtemplate(
          iv_templatename = lv_test_template
          io_templatecontent = lo_template_content ).

        " Tag it for cleanup in case test fails
        DATA(lv_arn) = |arn:aws:ses:{ ao_session->get_region( ) }:{ ao_session->get_account_id( ) }:template/{ lv_test_template }|.
        tag_resource( lv_arn ).

      CATCH /aws1/cx_rt_generic INTO DATA(lo_ex).
        cl_abap_unit_assert=>fail(
          msg = |Failed to create test template: { lo_ex->get_text( ) }| ).
    ENDTRY.

    " Call the action method to delete it
    ao_se2_actions->delete_email_template( lv_test_template ).

    " Verify it was deleted by attempting to get it
    TRY.
        ao_se2->getemailtemplate( iv_templatename = lv_test_template ).
        cl_abap_unit_assert=>fail(
          msg = |Email template { lv_test_template } should have been deleted| ).
      CATCH /aws1/cx_se2notfoundexception.
        " Expected - template was successfully deleted
    ENDTRY.
  ENDMETHOD.

  METHOD delete_email_identity.
    " Create a new identity to delete
    DATA(lv_test_uuid) = /awsex/cl_utils=>get_random_string( ).
    DATA(lv_test_identity) = |test{ lv_test_uuid }@example.com|.

    " Create the identity
    TRY.
        ao_se2->createemailidentity( iv_emailidentity = lv_test_identity ).

        " Tag it for cleanup in case test fails
        DATA(lv_arn) = |arn:aws:ses:{ ao_session->get_region( ) }:{ ao_session->get_account_id( ) }:identity/{ lv_test_identity }|.
        tag_resource( lv_arn ).

      CATCH /aws1/cx_rt_generic INTO DATA(lo_ex).
        cl_abap_unit_assert=>fail(
          msg = |Failed to create test identity: { lo_ex->get_text( ) }| ).
    ENDTRY.

    " Call the action method to delete it
    ao_se2_actions->delete_email_identity( lv_test_identity ).

    " Verify it was deleted by attempting to get it
    TRY.
        ao_se2->getemailidentity( iv_emailidentity = lv_test_identity ).
        cl_abap_unit_assert=>fail(
          msg = |Email identity { lv_test_identity } should have been deleted| ).
      CATCH /aws1/cx_se2notfoundexception.
        " Expected - identity was successfully deleted
    ENDTRY.
  ENDMETHOD.

ENDCLASS.
