" Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
" SPDX-License-Identifier: Apache-2.0

CLASS /awsex/cl_ses_actions DEFINITION
  PUBLIC
  FINAL
  CREATE PUBLIC .

  PUBLIC SECTION.
    " Send an email using Amazon SES
    METHODS send_email
      IMPORTING
        iv_source          TYPE /aws1/sesaddress
        io_destination     TYPE REF TO /aws1/cl_sesdestination
        iv_subject         TYPE /aws1/sesmessagedata
        iv_text            TYPE /aws1/sesmessagedata
        iv_html            TYPE /aws1/sesmessagedata
        it_reply_tos       TYPE /aws1/cl_sesaddresslist_w=>tt_addresslist OPTIONAL
      RETURNING
        VALUE(ov_msg_id)   TYPE /aws1/sesmessageid
      RAISING
        /aws1/cx_rt_generic.

    " Send a templated email using Amazon SES
    METHODS send_templated_email
      IMPORTING
        iv_source          TYPE /aws1/sesaddress
        io_destination     TYPE REF TO /aws1/cl_sesdestination
        iv_template_name   TYPE /aws1/sestemplatename
        iv_template_data   TYPE /aws1/sestemplatedata
        it_reply_tos       TYPE /aws1/cl_sesaddresslist_w=>tt_addresslist OPTIONAL
      RETURNING
        VALUE(ov_msg_id)   TYPE /aws1/sesmessageid
      RAISING
        /aws1/cx_rt_generic.

    " Verify an email identity
    METHODS verify_email_identity
      IMPORTING
        iv_email_address TYPE /aws1/sesaddress
      RAISING
        /aws1/cx_rt_generic.

    " Verify a domain identity
    METHODS verify_domain_identity
      IMPORTING
        iv_domain_name     TYPE /aws1/sesdomain
      RETURNING
        VALUE(ov_token)    TYPE /aws1/sesverificationtoken
      RAISING
        /aws1/cx_rt_generic.

    " Get identity verification attributes
    METHODS get_id_verification_attrs
      IMPORTING
        iv_identity        TYPE /aws1/sesidentity
      RETURNING
        VALUE(ov_status)   TYPE /aws1/sesverificationstatus
      RAISING
        /aws1/cx_rt_generic.

    " Delete an identity
    METHODS delete_identity
      IMPORTING
        iv_identity TYPE /aws1/sesidentity
      RAISING
        /aws1/cx_rt_generic.

    " List identities
    METHODS list_identities
      IMPORTING
        iv_identity_type   TYPE /aws1/sesidentitytype
        iv_max_items       TYPE /aws1/sesmaxitems
      RETURNING
        VALUE(ot_identities) TYPE /aws1/cl_sesidentitylist_w=>tt_identitylist
      RAISING
        /aws1/cx_rt_generic.

    " Create a template
    METHODS create_template
      IMPORTING
        iv_name    TYPE /aws1/sestemplatename
        iv_subject TYPE /aws1/sessubjectpart
        iv_text    TYPE /aws1/sestextpart
        iv_html    TYPE /aws1/seshtmlpart
      RAISING
        /aws1/cx_rt_generic.

    " Delete a template
    METHODS delete_template
      IMPORTING
        iv_template_name TYPE /aws1/sestemplatename
      RAISING
        /aws1/cx_rt_generic.

    " Get a template
    METHODS get_template
      IMPORTING
        iv_template_name   TYPE /aws1/sestemplatename
      RETURNING
        VALUE(oo_template) TYPE REF TO /aws1/cl_sestemplate
      RAISING
        /aws1/cx_rt_generic.

    " List templates
    METHODS list_templates
      IMPORTING
        iv_max_items         TYPE /aws1/sesmaxitems DEFAULT 10
      RETURNING
        VALUE(ot_templates)  TYPE /aws1/cl_sestemplatemetadata=>tt_templatemetadatalist
      RAISING
        /aws1/cx_rt_generic.

    " Update a template
    METHODS update_template
      IMPORTING
        iv_name    TYPE /aws1/sestemplatename
        iv_subject TYPE /aws1/sessubjectpart
        iv_text    TYPE /aws1/sestextpart
        iv_html    TYPE /aws1/seshtmlpart
      RAISING
        /aws1/cx_rt_generic.

    " Create a receipt filter
    METHODS create_receipt_filter
      IMPORTING
        iv_filter_name         TYPE /aws1/sesreceiptfiltername
        iv_ip_address_or_range TYPE /aws1/sescidr
        iv_allow               TYPE abap_bool
      RAISING
        /aws1/cx_rt_generic.

    " List receipt filters
    METHODS list_receipt_filters
      RETURNING
        VALUE(ot_filters) TYPE /aws1/cl_sesreceiptfilter=>tt_receiptfilterlist
      RAISING
        /aws1/cx_rt_generic.

    " Delete a receipt filter
    METHODS delete_receipt_filter
      IMPORTING
        iv_filter_name TYPE /aws1/sesreceiptfiltername
      RAISING
        /aws1/cx_rt_generic.

    " Create a receipt rule set
    METHODS create_receipt_rule_set
      IMPORTING
        iv_rule_set_name TYPE /aws1/sesreceiptrulesetname
      RAISING
        /aws1/cx_rt_generic.

    " Create receipt rule
    METHODS create_receipt_rule
      IMPORTING
        iv_rule_set_name TYPE /aws1/sesreceiptrulesetname
        iv_rule_name     TYPE /aws1/sesreceiptrulename
        it_recipients    TYPE /aws1/cl_sesrecipientslist_w=>tt_recipientslist
        iv_bucket_name   TYPE /aws1/sess3bucketname
        iv_prefix        TYPE /aws1/sess3keyprefix
      RAISING
        /aws1/cx_rt_generic.

    " Describe a receipt rule set
    METHODS describe_receipt_rule_set
      IMPORTING
        iv_rule_set_name TYPE /aws1/sesreceiptrulesetname
      RETURNING
        VALUE(oo_result) TYPE REF TO /aws1/cl_sesdscreceiptrlsetrsp
      RAISING
        /aws1/cx_rt_generic.

    " Delete a receipt rule
    METHODS delete_receipt_rule
      IMPORTING
        iv_rule_set_name TYPE /aws1/sesreceiptrulesetname
        iv_rule_name     TYPE /aws1/sesreceiptrulename
      RAISING
        /aws1/cx_rt_generic.

    " Delete a receipt rule set
    METHODS delete_receipt_rule_set
      IMPORTING
        iv_rule_set_name TYPE /aws1/sesreceiptrulesetname
      RAISING
        /aws1/cx_rt_generic.

  PROTECTED SECTION.
  PRIVATE SECTION.
ENDCLASS.



CLASS /AWSEX/CL_SES_ACTIONS IMPLEMENTATION.


  METHOD send_email.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_ses) = /aws1/cl_ses_factory=>create( lo_session ).

    " snippet-start:[ses.abapv1.send_email]
    " Create message object
    DATA(lo_subject) = NEW /aws1/cl_sescontent( iv_data = iv_subject ).
    DATA(lo_text_body) = NEW /aws1/cl_sescontent( iv_data = iv_text ).
    DATA(lo_html_body) = NEW /aws1/cl_sescontent( iv_data = iv_html ).
    DATA(lo_body) = NEW /aws1/cl_sesbody(
      io_text = lo_text_body
      io_html = lo_html_body
    ).
    DATA(lo_message) = NEW /aws1/cl_sesmessage(
      io_subject = lo_subject
      io_body = lo_body
    ).

    TRY.
        " Send email
        DATA(lo_result) = lo_ses->sendemail(
          iv_source = iv_source
          io_destination = io_destination
          io_message = lo_message
          it_replytoaddresses = it_reply_tos
        ).
        ov_msg_id = lo_result->get_messageid( ).
        MESSAGE 'Email sent successfully' TYPE 'I'.
      CATCH /aws1/cx_sesacctsendingpause00 INTO DATA(lo_ex1).
        DATA(lv_error) = |Account sending paused: { lo_ex1->get_text( ) }|.
        MESSAGE lv_error TYPE 'I'.
        RAISE EXCEPTION lo_ex1.
      CATCH /aws1/cx_sesmessagerejected INTO DATA(lo_ex2).
        lv_error = |Message rejected: { lo_ex2->get_text( ) }|.
        MESSAGE lv_error TYPE 'I'.
        RAISE EXCEPTION lo_ex2.
      CATCH /aws1/cx_rt_generic INTO DATA(lo_ex_generic).
        lv_error = |An error occurred: { lo_ex_generic->get_text( ) }|.
        MESSAGE lv_error TYPE 'I'.
        RAISE EXCEPTION lo_ex_generic.
    ENDTRY.
    " snippet-end:[ses.abapv1.send_email]
  ENDMETHOD.


  METHOD send_templated_email.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_ses) = /aws1/cl_ses_factory=>create( lo_session ).

    " snippet-start:[ses.abapv1.send_templated_email]
    TRY.
        " Send templated email
        DATA(lo_result) = lo_ses->sendtemplatedemail(
          iv_source = iv_source
          io_destination = io_destination
          iv_template = iv_template_name
          iv_templatedata = iv_template_data
          it_replytoaddresses = it_reply_tos
        ).
        ov_msg_id = lo_result->get_messageid( ).
        MESSAGE 'Templated email sent successfully' TYPE 'I'.
      CATCH /aws1/cx_sestmpldoesnotexistex INTO DATA(lo_ex1).
        DATA(lv_error) = |Template does not exist: { lo_ex1->get_text( ) }|.
        MESSAGE lv_error TYPE 'I'.
        RAISE EXCEPTION lo_ex1.
      CATCH /aws1/cx_rt_generic INTO DATA(lo_ex_generic).
        lv_error = |An error occurred: { lo_ex_generic->get_text( ) }|.
        MESSAGE lv_error TYPE 'I'.
        RAISE EXCEPTION lo_ex_generic.
    ENDTRY.
    " snippet-end:[ses.abapv1.send_templated_email]
  ENDMETHOD.


  METHOD verify_email_identity.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_ses) = /aws1/cl_ses_factory=>create( lo_session ).

    " snippet-start:[ses.abapv1.verify_email_identity]
    TRY.
        lo_ses->verifyemailidentity( iv_emailaddress = iv_email_address ).
        MESSAGE 'Email verification initiated' TYPE 'I'.
      CATCH /aws1/cx_rt_generic INTO DATA(lo_ex).
        DATA(lv_error) = |An error occurred: { lo_ex->get_text( ) }|.
        MESSAGE lv_error TYPE 'I'.
        RAISE EXCEPTION lo_ex.
    ENDTRY.
    " snippet-end:[ses.abapv1.verify_email_identity]
  ENDMETHOD.


  METHOD verify_domain_identity.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_ses) = /aws1/cl_ses_factory=>create( lo_session ).

    " snippet-start:[ses.abapv1.verify_domain_identity]
    TRY.
        DATA(lo_result) = lo_ses->verifydomainidentity( iv_domain = iv_domain_name ).
        ov_token = lo_result->get_verificationtoken( ).
        MESSAGE 'Domain verification initiated' TYPE 'I'.
      CATCH /aws1/cx_rt_generic INTO DATA(lo_ex).
        DATA(lv_error) = |An error occurred: { lo_ex->get_text( ) }|.
        MESSAGE lv_error TYPE 'I'.
        RAISE EXCEPTION lo_ex.
    ENDTRY.
    " snippet-end:[ses.abapv1.verify_domain_identity]
  ENDMETHOD.


  METHOD get_id_verification_attrs.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_ses) = /aws1/cl_ses_factory=>create( lo_session ).

    " snippet-start:[ses.abapv1.get_identity_verification_attributes]
    DATA lt_identities TYPE /aws1/cl_sesidentitylist_w=>tt_identitylist.
    APPEND NEW /aws1/cl_sesidentitylist_w( iv_value = iv_identity ) TO lt_identities.

    TRY.
        DATA(lo_result) = lo_ses->getidentityverificationattrs(
          it_identities = lt_identities
        ).

        DATA(lt_attrs) = lo_result->get_verificationattributes( ).
        IF lt_attrs IS NOT INITIAL.
          LOOP AT lt_attrs ASSIGNING FIELD-SYMBOL(<ls_attr>).
            ov_status = <ls_attr>-value->get_verificationstatus( ).
            EXIT.
          ENDLOOP.
        ELSE.
          ov_status = 'NotFound'.
        ENDIF.
      CATCH /aws1/cx_rt_generic INTO DATA(lo_ex).
        DATA(lv_error) = |An error occurred: { lo_ex->get_text( ) }|.
        MESSAGE lv_error TYPE 'I'.
        RAISE EXCEPTION lo_ex.
    ENDTRY.
    " snippet-end:[ses.abapv1.get_identity_verification_attributes]
  ENDMETHOD.


  METHOD delete_identity.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_ses) = /aws1/cl_ses_factory=>create( lo_session ).

    " snippet-start:[ses.abapv1.delete_identity]
    TRY.
        lo_ses->deleteidentity( iv_identity = iv_identity ).
        MESSAGE 'Identity deleted successfully' TYPE 'I'.
      CATCH /aws1/cx_rt_generic INTO DATA(lo_ex).
        DATA(lv_error) = |An error occurred: { lo_ex->get_text( ) }|.
        MESSAGE lv_error TYPE 'I'.
        RAISE EXCEPTION lo_ex.
    ENDTRY.
    " snippet-end:[ses.abapv1.delete_identity]
  ENDMETHOD.


  METHOD list_identities.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_ses) = /aws1/cl_ses_factory=>create( lo_session ).

    " snippet-start:[ses.abapv1.list_identities]
    TRY.
        DATA(lo_result) = lo_ses->listidentities(
          iv_identitytype = iv_identity_type
          iv_maxitems = iv_max_items
        ).
        ot_identities = lo_result->get_identities( ).
        MESSAGE 'Identities retrieved successfully' TYPE 'I'.
      CATCH /aws1/cx_rt_generic INTO DATA(lo_ex).
        DATA(lv_error) = |An error occurred: { lo_ex->get_text( ) }|.
        MESSAGE lv_error TYPE 'I'.
        RAISE EXCEPTION lo_ex.
    ENDTRY.
    " snippet-end:[ses.abapv1.list_identities]
  ENDMETHOD.


  METHOD create_template.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_ses) = /aws1/cl_ses_factory=>create( lo_session ).

    " snippet-start:[ses.abapv1.create_template]
    DATA(lo_template) = NEW /aws1/cl_sestemplate(
      iv_templatename = iv_name
      iv_subjectpart = iv_subject
      iv_textpart = iv_text
      iv_htmlpart = iv_html
    ).

    TRY.
        lo_ses->createtemplate( io_template = lo_template ).
        MESSAGE 'Template created successfully' TYPE 'I'.
      CATCH /aws1/cx_sesalreadyexistsex INTO DATA(lo_ex1).
        DATA(lv_error) = |Template already exists: { lo_ex1->get_text( ) }|.
        MESSAGE lv_error TYPE 'I'.
        RAISE EXCEPTION lo_ex1.
      CATCH /aws1/cx_sesinvalidtemplateex INTO DATA(lo_ex2).
        lv_error = |Invalid template: { lo_ex2->get_text( ) }|.
        MESSAGE lv_error TYPE 'I'.
        RAISE EXCEPTION lo_ex2.
      CATCH /aws1/cx_rt_generic INTO DATA(lo_ex_generic).
        lv_error = |An error occurred: { lo_ex_generic->get_text( ) }|.
        MESSAGE lv_error TYPE 'I'.
        RAISE EXCEPTION lo_ex_generic.
    ENDTRY.
    " snippet-end:[ses.abapv1.create_template]
  ENDMETHOD.


  METHOD delete_template.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_ses) = /aws1/cl_ses_factory=>create( lo_session ).

    " snippet-start:[ses.abapv1.delete_template]
    TRY.
        lo_ses->deletetemplate( iv_templatename = iv_template_name ).
        MESSAGE 'Template deleted successfully' TYPE 'I'.
      CATCH /aws1/cx_rt_generic INTO DATA(lo_ex).
        DATA(lv_error) = |An error occurred: { lo_ex->get_text( ) }|.
        MESSAGE lv_error TYPE 'I'.
        RAISE EXCEPTION lo_ex.
    ENDTRY.
    " snippet-end:[ses.abapv1.delete_template]
  ENDMETHOD.


  METHOD get_template.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_ses) = /aws1/cl_ses_factory=>create( lo_session ).

    " snippet-start:[ses.abapv1.get_template]
    TRY.
        DATA(lo_result) = lo_ses->gettemplate( iv_templatename = iv_template_name ).
        oo_template = lo_result->get_template( ).
        MESSAGE 'Template retrieved successfully' TYPE 'I'.
      CATCH /aws1/cx_sestmpldoesnotexistex INTO DATA(lo_ex1).
        DATA(lv_error) = |Template does not exist: { lo_ex1->get_text( ) }|.
        MESSAGE lv_error TYPE 'I'.
        RAISE EXCEPTION lo_ex1.
      CATCH /aws1/cx_rt_generic INTO DATA(lo_ex_generic).
        lv_error = |An error occurred: { lo_ex_generic->get_text( ) }|.
        MESSAGE lv_error TYPE 'I'.
        RAISE EXCEPTION lo_ex_generic.
    ENDTRY.
    " snippet-end:[ses.abapv1.get_template]
  ENDMETHOD.


  METHOD list_templates.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_ses) = /aws1/cl_ses_factory=>create( lo_session ).

    " snippet-start:[ses.abapv1.list_templates]
    TRY.
        DATA(lo_result) = lo_ses->listtemplates( iv_maxitems = iv_max_items ).
        ot_templates = lo_result->get_templatesmetadata( ).
        MESSAGE 'Templates retrieved successfully' TYPE 'I'.
      CATCH /aws1/cx_rt_generic INTO DATA(lo_ex).
        DATA(lv_error) = |An error occurred: { lo_ex->get_text( ) }|.
        MESSAGE lv_error TYPE 'I'.
        RAISE EXCEPTION lo_ex.
    ENDTRY.
    " snippet-end:[ses.abapv1.list_templates]
  ENDMETHOD.


  METHOD update_template.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_ses) = /aws1/cl_ses_factory=>create( lo_session ).

    " snippet-start:[ses.abapv1.update_template]
    DATA(lo_template) = NEW /aws1/cl_sestemplate(
      iv_templatename = iv_name
      iv_subjectpart = iv_subject
      iv_textpart = iv_text
      iv_htmlpart = iv_html
    ).

    TRY.
        lo_ses->updatetemplate( io_template = lo_template ).
        MESSAGE 'Template updated successfully' TYPE 'I'.
      CATCH /aws1/cx_sestmpldoesnotexistex INTO DATA(lo_ex1).
        DATA(lv_error) = |Template does not exist: { lo_ex1->get_text( ) }|.
        MESSAGE lv_error TYPE 'I'.
        RAISE EXCEPTION lo_ex1.
      CATCH /aws1/cx_sesinvalidtemplateex INTO DATA(lo_ex2).
        lv_error = |Invalid template: { lo_ex2->get_text( ) }|.
        MESSAGE lv_error TYPE 'I'.
        RAISE EXCEPTION lo_ex2.
      CATCH /aws1/cx_rt_generic INTO DATA(lo_ex_generic).
        lv_error = |An error occurred: { lo_ex_generic->get_text( ) }|.
        MESSAGE lv_error TYPE 'I'.
        RAISE EXCEPTION lo_ex_generic.
    ENDTRY.
    " snippet-end:[ses.abapv1.update_template]
  ENDMETHOD.


  METHOD create_receipt_filter.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_ses) = /aws1/cl_ses_factory=>create( lo_session ).

    " snippet-start:[ses.abapv1.create_receipt_filter]
    " iv_allow = abap_true means 'Allow', abap_false means 'Block'
    DATA(lv_policy) = COND /aws1/sesreceiptfilterpolicy(
      WHEN iv_allow = abap_true THEN 'Allow'
      ELSE 'Block'
    ).

    DATA(lo_ip_filter) = NEW /aws1/cl_sesreceiptipfilter(
      iv_policy = lv_policy
      iv_cidr = iv_ip_address_or_range
    ).

    DATA(lo_filter) = NEW /aws1/cl_sesreceiptfilter(
      iv_name = iv_filter_name
      io_ipfilter = lo_ip_filter
    ).

    TRY.
        lo_ses->createreceiptfilter( io_filter = lo_filter ).
        MESSAGE 'Receipt filter created successfully' TYPE 'I'.
      CATCH /aws1/cx_sesalreadyexistsex INTO DATA(lo_ex1).
        DATA(lv_error) = |Filter already exists: { lo_ex1->get_text( ) }|.
        MESSAGE lv_error TYPE 'I'.
        RAISE EXCEPTION lo_ex1.
      CATCH /aws1/cx_rt_generic INTO DATA(lo_ex_generic).
        lv_error = |An error occurred: { lo_ex_generic->get_text( ) }|.
        MESSAGE lv_error TYPE 'I'.
        RAISE EXCEPTION lo_ex_generic.
    ENDTRY.
    " snippet-end:[ses.abapv1.create_receipt_filter]
  ENDMETHOD.


  METHOD list_receipt_filters.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_ses) = /aws1/cl_ses_factory=>create( lo_session ).

    " snippet-start:[ses.abapv1.list_receipt_filters]
    TRY.
        DATA(lo_result) = lo_ses->listreceiptfilters( ).
        ot_filters = lo_result->get_filters( ).
        MESSAGE 'Receipt filters retrieved successfully' TYPE 'I'.
      CATCH /aws1/cx_rt_generic INTO DATA(lo_ex).
        DATA(lv_error) = |An error occurred: { lo_ex->get_text( ) }|.
        MESSAGE lv_error TYPE 'I'.
        RAISE EXCEPTION lo_ex.
    ENDTRY.
    " snippet-end:[ses.abapv1.list_receipt_filters]
  ENDMETHOD.


  METHOD delete_receipt_filter.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_ses) = /aws1/cl_ses_factory=>create( lo_session ).

    " snippet-start:[ses.abapv1.delete_receipt_filter]
    TRY.
        lo_ses->deletereceiptfilter( iv_filtername = iv_filter_name ).
        MESSAGE 'Receipt filter deleted successfully' TYPE 'I'.
      CATCH /aws1/cx_rt_generic INTO DATA(lo_ex).
        DATA(lv_error) = |An error occurred: { lo_ex->get_text( ) }|.
        MESSAGE lv_error TYPE 'I'.
        RAISE EXCEPTION lo_ex.
    ENDTRY.
    " snippet-end:[ses.abapv1.delete_receipt_filter]
  ENDMETHOD.


  METHOD create_receipt_rule_set.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_ses) = /aws1/cl_ses_factory=>create( lo_session ).

    " snippet-start:[ses.abapv1.create_receipt_rule_set]
    TRY.
        lo_ses->createreceiptruleset( iv_rulesetname = iv_rule_set_name ).
        MESSAGE 'Receipt rule set created successfully' TYPE 'I'.
      CATCH /aws1/cx_sesalreadyexistsex INTO DATA(lo_ex1).
        DATA(lv_error) = |Rule set already exists: { lo_ex1->get_text( ) }|.
        MESSAGE lv_error TYPE 'I'.
        RAISE EXCEPTION lo_ex1.
      CATCH /aws1/cx_rt_generic INTO DATA(lo_ex_generic).
        lv_error = |An error occurred: { lo_ex_generic->get_text( ) }|.
        MESSAGE lv_error TYPE 'I'.
        RAISE EXCEPTION lo_ex_generic.
    ENDTRY.
    " snippet-end:[ses.abapv1.create_receipt_rule_set]
  ENDMETHOD.


  METHOD create_receipt_rule.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_ses) = /aws1/cl_ses_factory=>create( lo_session ).

    " snippet-start:[ses.abapv1.create_receipt_rule]
    " Create S3 action for copying emails to S3
    DATA(lo_s3_action) = NEW /aws1/cl_sess3action(
      iv_bucketname = iv_bucket_name
      iv_objectkeyprefix = iv_prefix
    ).

    " Create receipt action with S3 action
    DATA(lo_action) = NEW /aws1/cl_sesreceiptaction(
      io_s3action = lo_s3_action
    ).

    " Create list of actions
    DATA lt_actions TYPE /aws1/cl_sesreceiptaction=>tt_receiptactionslist.
    APPEND lo_action TO lt_actions.

    " Create receipt rule
    DATA(lo_rule) = NEW /aws1/cl_sesreceiptrule(
      iv_name = iv_rule_name
      iv_enabled = abap_true
      it_recipients = it_recipients
      it_actions = lt_actions
    ).

    TRY.
        lo_ses->createreceiptrule(
          iv_rulesetname = iv_rule_set_name
          io_rule = lo_rule
        ).
        MESSAGE 'Receipt rule created successfully' TYPE 'I'.
      CATCH /aws1/cx_sesinvalids3confex INTO DATA(lo_ex1).
        DATA(lv_error) = |Invalid S3 configuration: { lo_ex1->get_text( ) }|.
        MESSAGE lv_error TYPE 'I'.
        RAISE EXCEPTION lo_ex1.
      CATCH /aws1/cx_rt_generic INTO DATA(lo_ex_generic).
        lv_error = |An error occurred: { lo_ex_generic->get_text( ) }|.
        MESSAGE lv_error TYPE 'I'.
        RAISE EXCEPTION lo_ex_generic.
    ENDTRY.
    " snippet-end:[ses.abapv1.create_receipt_rule]
  ENDMETHOD.


  METHOD describe_receipt_rule_set.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_ses) = /aws1/cl_ses_factory=>create( lo_session ).

    " snippet-start:[ses.abapv1.describe_receipt_rule_set]
    TRY.
        oo_result = lo_ses->describereceiptruleset(
          iv_rulesetname = iv_rule_set_name
        ).
        MESSAGE 'Receipt rule set described successfully' TYPE 'I'.
      CATCH /aws1/cx_sesrulesetdoesnotexex INTO DATA(lo_ex1).
        DATA(lv_error) = |Rule set does not exist: { lo_ex1->get_text( ) }|.
        MESSAGE lv_error TYPE 'I'.
        RAISE EXCEPTION lo_ex1.
      CATCH /aws1/cx_rt_generic INTO DATA(lo_ex_generic).
        lv_error = |An error occurred: { lo_ex_generic->get_text( ) }|.
        MESSAGE lv_error TYPE 'I'.
        RAISE EXCEPTION lo_ex_generic.
    ENDTRY.
    " snippet-end:[ses.abapv1.describe_receipt_rule_set]
  ENDMETHOD.


  METHOD delete_receipt_rule.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_ses) = /aws1/cl_ses_factory=>create( lo_session ).

    " snippet-start:[ses.abapv1.delete_receipt_rule]
    TRY.
        lo_ses->deletereceiptrule(
          iv_rulesetname = iv_rule_set_name
          iv_rulename = iv_rule_name
        ).
        MESSAGE 'Receipt rule deleted successfully' TYPE 'I'.
      CATCH /aws1/cx_sesrulesetdoesnotexex INTO DATA(lo_ex1).
        DATA(lv_error) = |Rule set does not exist: { lo_ex1->get_text( ) }|.
        MESSAGE lv_error TYPE 'I'.
        RAISE EXCEPTION lo_ex1.
      CATCH /aws1/cx_rt_generic INTO DATA(lo_ex_generic).
        lv_error = |An error occurred: { lo_ex_generic->get_text( ) }|.
        MESSAGE lv_error TYPE 'I'.
        RAISE EXCEPTION lo_ex_generic.
    ENDTRY.
    " snippet-end:[ses.abapv1.delete_receipt_rule]
  ENDMETHOD.


  METHOD delete_receipt_rule_set.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_ses) = /aws1/cl_ses_factory=>create( lo_session ).

    " snippet-start:[ses.abapv1.delete_receipt_rule_set]
    TRY.
        lo_ses->deletereceiptruleset( iv_rulesetname = iv_rule_set_name ).
        MESSAGE 'Receipt rule set deleted successfully' TYPE 'I'.
      CATCH /aws1/cx_sescannotdeleteex INTO DATA(lo_ex1).
        DATA(lv_error) = |Cannot delete rule set: { lo_ex1->get_text( ) }|.
        MESSAGE lv_error TYPE 'I'.
        RAISE EXCEPTION lo_ex1.
      CATCH /aws1/cx_rt_generic INTO DATA(lo_ex_generic).
        lv_error = |An error occurred: { lo_ex_generic->get_text( ) }|.
        MESSAGE lv_error TYPE 'I'.
        RAISE EXCEPTION lo_ex_generic.
    ENDTRY.
    " snippet-end:[ses.abapv1.delete_receipt_rule_set]
  ENDMETHOD.
ENDCLASS.
