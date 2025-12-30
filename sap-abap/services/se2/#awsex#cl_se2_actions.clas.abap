" Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
" SPDX-License-Identifier: Apache-2.0
CLASS /awsex/cl_se2_actions DEFINITION
  PUBLIC
  FINAL
  CREATE PUBLIC .

  PUBLIC SECTION.
    METHODS create_email_identity
      IMPORTING
        !iv_email_identity TYPE /aws1/se2identity
      RAISING
        /aws1/cx_rt_generic.

    METHODS create_contact_list
      IMPORTING
        !iv_contact_list_name TYPE /aws1/se2contactlistname
      RAISING
        /aws1/cx_rt_generic.

    METHODS create_email_template
      IMPORTING
        !iv_template_name TYPE /aws1/se2emailtemplatename
        !iv_subject       TYPE /aws1/se2emailtemplatesubject
        !iv_html          TYPE /aws1/se2emailtemplatehtml
        !iv_text          TYPE /aws1/se2emailtemplatetext
      RAISING
        /aws1/cx_rt_generic.

    METHODS create_contact
      IMPORTING
        !iv_contact_list_name TYPE /aws1/se2contactlistname
        !iv_email_address     TYPE /aws1/se2emailaddress
      RAISING
        /aws1/cx_rt_generic.

    METHODS send_email
      IMPORTING
        !iv_from_email_address TYPE /aws1/se2emailaddress
        !iv_to_email_address   TYPE /aws1/se2emailaddress
        !iv_subject            TYPE /aws1/se2messagedata
        !iv_html_body          TYPE /aws1/se2messagedata
        !iv_text_body          TYPE /aws1/se2messagedata
      RAISING
        /aws1/cx_rt_generic.

    METHODS send_email_template
      IMPORTING
        !iv_from_email_address TYPE /aws1/se2emailaddress
        !iv_to_email_address   TYPE /aws1/se2emailaddress
        !iv_template_name      TYPE /aws1/se2emailtemplatename
        !iv_template_data      TYPE /aws1/se2emailtemplatedata
        !iv_contact_list_name  TYPE /aws1/se2contactlistname
      RAISING
        /aws1/cx_rt_generic.

    METHODS list_contacts
      IMPORTING
        !iv_contact_list_name TYPE /aws1/se2contactlistname
      EXPORTING
        !oo_result            TYPE REF TO /aws1/cl_se2listcontactsrsp
      RAISING
        /aws1/cx_rt_generic.

    METHODS delete_contact_list
      IMPORTING
        !iv_contact_list_name TYPE /aws1/se2contactlistname
      RAISING
        /aws1/cx_rt_generic.

    METHODS delete_email_template
      IMPORTING
        !iv_template_name TYPE /aws1/se2emailtemplatename
      RAISING
        /aws1/cx_rt_generic.

    METHODS delete_email_identity
      IMPORTING
        !iv_email_identity TYPE /aws1/se2identity
      RAISING
        /aws1/cx_rt_generic.

  PROTECTED SECTION.
  PRIVATE SECTION.
ENDCLASS.



CLASS /awsex/cl_se2_actions IMPLEMENTATION.

  METHOD create_email_identity.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_se2) = /aws1/cl_se2_factory=>create( lo_session ).

    " snippet-start:[se2.abapv1.create_email_identity]
    TRY.
        lo_se2->createemailidentity(
          iv_emailidentity = iv_email_identity ).
        MESSAGE 'Email identity created successfully.' TYPE 'I'.
      CATCH /aws1/cx_se2alreadyexistsex.
        MESSAGE 'Email identity already exists.' TYPE 'I'.
      CATCH /aws1/cx_se2badrequestex INTO DATA(lo_bad_request).
        MESSAGE lo_bad_request TYPE 'I' DISPLAY LIKE 'E'.
      CATCH /aws1/cx_se2limitexceededex INTO DATA(lo_limit_exceeded).
        MESSAGE lo_limit_exceeded TYPE 'I' DISPLAY LIKE 'E'.
    ENDTRY.
    " snippet-end:[se2.abapv1.create_email_identity]
  ENDMETHOD.

  METHOD create_contact_list.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_se2) = /aws1/cl_se2_factory=>create( lo_session ).

    " snippet-start:[se2.abapv1.create_contact_list]
    TRY.
        lo_se2->createcontactlist(
          iv_contactlistname = iv_contact_list_name ).
        MESSAGE 'Contact list created successfully.' TYPE 'I'.
      CATCH /aws1/cx_se2alreadyexistsex.
        MESSAGE 'Contact list already exists.' TYPE 'I'.
      CATCH /aws1/cx_se2badrequestex INTO DATA(lo_bad_request).
        MESSAGE 'Bad request - contact list limit may be reached.' TYPE 'I'.
        " Re-raise the exception so the caller can handle it
        RAISE EXCEPTION lo_bad_request.
      CATCH /aws1/cx_se2limitexceededex INTO DATA(lo_limit_exceeded).
        MESSAGE 'Limit exceeded - contact list limit reached.' TYPE 'I'.
        " Re-raise the exception so the caller can handle it
        RAISE EXCEPTION lo_limit_exceeded.
    ENDTRY.
    " snippet-end:[se2.abapv1.create_contact_list]
  ENDMETHOD.

  METHOD create_email_template.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_se2) = /aws1/cl_se2_factory=>create( lo_session ).

    " snippet-start:[se2.abapv1.create_email_template]
    TRY.
        DATA(lo_template_content) = NEW /aws1/cl_se2emailtmplcontent(
          iv_subject = iv_subject
          iv_html = iv_html
          iv_text = iv_text ).

        lo_se2->createemailtemplate(
          iv_templatename = iv_template_name
          io_templatecontent = lo_template_content ).
        MESSAGE 'Email template created successfully.' TYPE 'I'.
      CATCH /aws1/cx_se2alreadyexistsex.
        MESSAGE 'Email template already exists.' TYPE 'I'.
      CATCH /aws1/cx_se2badrequestex.
        MESSAGE 'Bad request.' TYPE 'E'.
      CATCH /aws1/cx_se2limitexceededex.
        MESSAGE 'Limit exceeded.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[se2.abapv1.create_email_template]
  ENDMETHOD.

  METHOD create_contact.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_se2) = /aws1/cl_se2_factory=>create( lo_session ).

    " snippet-start:[se2.abapv1.create_contact]
    TRY.
        lo_se2->createcontact(
          iv_contactlistname = iv_contact_list_name
          iv_emailaddress = iv_email_address ).
        MESSAGE 'Contact created successfully.' TYPE 'I'.
      CATCH /aws1/cx_se2alreadyexistsex.
        MESSAGE 'Contact already exists.' TYPE 'I'.
      CATCH /aws1/cx_se2badrequestex.
        MESSAGE 'Bad request.' TYPE 'E'.
      CATCH /aws1/cx_se2notfoundexception.
        MESSAGE 'Contact list not found.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[se2.abapv1.create_contact]
  ENDMETHOD.

  METHOD send_email.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_se2) = /aws1/cl_se2_factory=>create( lo_session ).

    " snippet-start:[se2.abapv1.send_email]
    TRY.
        " Create destination with recipient address
        DATA lt_to_addresses TYPE /aws1/cl_se2emailaddresslist_w=>tt_emailaddresslist.
        APPEND NEW /aws1/cl_se2emailaddresslist_w( iv_value = iv_to_email_address ) TO lt_to_addresses.
        DATA(lo_destination) = NEW /aws1/cl_se2destination(
          it_toaddresses = lt_to_addresses ).

        " Create message content
        DATA(lo_subject) = NEW /aws1/cl_se2content( iv_data = iv_subject ).
        DATA(lo_text_body) = NEW /aws1/cl_se2content( iv_data = iv_text_body ).
        DATA(lo_html_body) = NEW /aws1/cl_se2content( iv_data = iv_html_body ).
        DATA(lo_body) = NEW /aws1/cl_se2body(
          io_text = lo_text_body
          io_html = lo_html_body ).
        DATA(lo_message) = NEW /aws1/cl_se2message(
          io_subject = lo_subject
          io_body = lo_body ).

        DATA(lo_content) = NEW /aws1/cl_se2emailcontent(
          io_simple = lo_message ).

        " Send the email
        lo_se2->sendemail(
          iv_fromemailaddress = iv_from_email_address
          io_destination = lo_destination
          io_content = lo_content ).
        MESSAGE 'Email sent successfully.' TYPE 'I'.
      CATCH /aws1/cx_se2accountsuspendedex INTO DATA(lo_account_suspended).
        MESSAGE 'Account suspended.' TYPE 'I'.
        RAISE EXCEPTION lo_account_suspended.
      CATCH /aws1/cx_se2badrequestex INTO DATA(lo_bad_request).
        MESSAGE 'Bad request.' TYPE 'I'.
        RAISE EXCEPTION lo_bad_request.
      CATCH /aws1/cx_se2messagerejected INTO DATA(lo_message_rejected).
        MESSAGE 'Message rejected - check email verification.' TYPE 'I'.
        RAISE EXCEPTION lo_message_rejected.
    ENDTRY.
    " snippet-end:[se2.abapv1.send_email]
  ENDMETHOD.

  METHOD send_email_template.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_se2) = /aws1/cl_se2_factory=>create( lo_session ).

    " snippet-start:[se2.abapv1.send_email_template]
    TRY.
        " Create destination with recipient address
        DATA lt_to_addresses TYPE /aws1/cl_se2emailaddresslist_w=>tt_emailaddresslist.
        APPEND NEW /aws1/cl_se2emailaddresslist_w( iv_value = iv_to_email_address ) TO lt_to_addresses.
        DATA(lo_destination) = NEW /aws1/cl_se2destination(
          it_toaddresses = lt_to_addresses ).

        " Create template reference
        DATA(lo_template) = NEW /aws1/cl_se2template(
          iv_templatename = iv_template_name
          iv_templatedata = iv_template_data ).

        DATA(lo_content) = NEW /aws1/cl_se2emailcontent(
          io_template = lo_template ).

        " Create list management options
        DATA(lo_list_mgmt) = NEW /aws1/cl_se2listmanagementopts(
          iv_contactlistname = iv_contact_list_name ).

        " Send the email using template
        lo_se2->sendemail(
          iv_fromemailaddress = iv_from_email_address
          io_destination = lo_destination
          io_content = lo_content
          io_listmanagementoptions = lo_list_mgmt ).
        MESSAGE 'Email sent using template successfully.' TYPE 'I'.
      CATCH /aws1/cx_se2accountsuspendedex INTO DATA(lo_account_suspended).
        MESSAGE 'Account suspended.' TYPE 'I'.
        RAISE EXCEPTION lo_account_suspended.
      CATCH /aws1/cx_se2badrequestex INTO DATA(lo_bad_request).
        MESSAGE 'Bad request.' TYPE 'I'.
        RAISE EXCEPTION lo_bad_request.
      CATCH /aws1/cx_se2messagerejected INTO DATA(lo_message_rejected).
        MESSAGE 'Message rejected - check email verification.' TYPE 'I'.
        RAISE EXCEPTION lo_message_rejected.
    ENDTRY.
    " snippet-end:[se2.abapv1.send_email_template]
  ENDMETHOD.

  METHOD list_contacts.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_se2) = /aws1/cl_se2_factory=>create( lo_session ).

    " snippet-start:[se2.abapv1.list_contacts]
    TRY.
        oo_result = lo_se2->listcontacts(
          iv_contactlistname = iv_contact_list_name ).
        DATA(lv_count) = lines( oo_result->get_contacts( ) ).
        MESSAGE |Retrieved { lv_count } contacts from list.| TYPE 'I'.
      CATCH /aws1/cx_se2badrequestex INTO DATA(lo_bad_request).
        MESSAGE 'Bad request.' TYPE 'I'.
        RAISE EXCEPTION lo_bad_request.
      CATCH /aws1/cx_se2notfoundexception INTO DATA(lo_not_found).
        MESSAGE 'Contact list not found.' TYPE 'I'.
        RAISE EXCEPTION lo_not_found.
    ENDTRY.
    " snippet-end:[se2.abapv1.list_contacts]
  ENDMETHOD.

  METHOD delete_contact_list.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_se2) = /aws1/cl_se2_factory=>create( lo_session ).

    " snippet-start:[se2.abapv1.delete_contact_list]
    TRY.
        lo_se2->deletecontactlist(
          iv_contactlistname = iv_contact_list_name ).
        MESSAGE 'Contact list deleted successfully.' TYPE 'I'.
      CATCH /aws1/cx_se2notfoundexception.
        MESSAGE 'Contact list not found.' TYPE 'I'.
      CATCH /aws1/cx_se2badrequestex INTO DATA(lo_bad_request).
        MESSAGE 'Bad request.' TYPE 'I'.
        RAISE EXCEPTION lo_bad_request.
    ENDTRY.
    " snippet-end:[se2.abapv1.delete_contact_list]
  ENDMETHOD.

  METHOD delete_email_template.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_se2) = /aws1/cl_se2_factory=>create( lo_session ).

    " snippet-start:[se2.abapv1.delete_email_template]
    TRY.
        lo_se2->deleteemailtemplate(
          iv_templatename = iv_template_name ).
        MESSAGE 'Email template deleted successfully.' TYPE 'I'.
      CATCH /aws1/cx_se2notfoundexception.
        MESSAGE 'Email template not found.' TYPE 'I'.
      CATCH /aws1/cx_se2badrequestex INTO DATA(lo_bad_request).
        MESSAGE 'Bad request.' TYPE 'I'.
        RAISE EXCEPTION lo_bad_request.
    ENDTRY.
    " snippet-end:[se2.abapv1.delete_email_template]
  ENDMETHOD.

  METHOD delete_email_identity.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_se2) = /aws1/cl_se2_factory=>create( lo_session ).

    " snippet-start:[se2.abapv1.delete_email_identity]
    TRY.
        lo_se2->deleteemailidentity(
          iv_emailidentity = iv_email_identity ).
        MESSAGE 'Email identity deleted successfully.' TYPE 'I'.
      CATCH /aws1/cx_se2notfoundexception.
        MESSAGE 'Email identity not found.' TYPE 'I'.
      CATCH /aws1/cx_se2badrequestex INTO DATA(lo_bad_request).
        MESSAGE 'Bad request.' TYPE 'I'.
        RAISE EXCEPTION lo_bad_request.
    ENDTRY.
    " snippet-end:[se2.abapv1.delete_email_identity]
  ENDMETHOD.

ENDCLASS.
