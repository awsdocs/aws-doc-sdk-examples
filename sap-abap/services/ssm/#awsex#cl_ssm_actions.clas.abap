" Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
" SPDX-License-Identifier: Apache-2.0
CLASS /awsex/cl_ssm_actions DEFINITION
  PUBLIC
  FINAL
  CREATE PUBLIC .

  PUBLIC SECTION.
    METHODS create_ops_item
      IMPORTING
        !iv_title       TYPE /aws1/ssmopsitemtitle
        !iv_source      TYPE /aws1/ssmopsitemsource
        !iv_category    TYPE /aws1/ssmopsitemcategory
        !iv_severity    TYPE /aws1/ssmopsitemseverity
        !iv_description TYPE /aws1/ssmopsitemdescription
      EXPORTING
        !oo_result      TYPE REF TO /aws1/cl_ssmcreateopsitemrsp
      RAISING
        /aws1/cx_rt_generic .
    METHODS delete_ops_item
      IMPORTING
        !iv_ops_item_id TYPE /aws1/ssmopsitemid
      RAISING
        /aws1/cx_rt_generic .
    METHODS describe_ops_items
      IMPORTING
        !iv_ops_item_id TYPE /aws1/ssmopsitemid
      RETURNING
        VALUE(rv_found) TYPE abap_bool
      RAISING
        /aws1/cx_rt_generic .
    METHODS update_ops_item
      IMPORTING
        !iv_ops_item_id TYPE /aws1/ssmopsitemid
        !iv_title       TYPE /aws1/ssmopsitemtitle OPTIONAL
        !iv_description TYPE /aws1/ssmopsitemdescription OPTIONAL
        !iv_status      TYPE /aws1/ssmopsitemstatus OPTIONAL
      RAISING
        /aws1/cx_rt_generic .
    METHODS create_maintenance_window
      IMPORTING
        !iv_name                       TYPE /aws1/ssmmaintenancewindowname
        !iv_schedule                   TYPE /aws1/ssmmaintenancewindowschd
        !iv_duration                   TYPE /aws1/ssmmaintenancewindowdu00
        !iv_cutoff                     TYPE /aws1/ssmmaintenancewindowcu00
        !iv_allow_unassociated_targets TYPE /aws1/ssmmaintenancewindowal00
      EXPORTING
        !oo_result                     TYPE REF TO /aws1/cl_ssmcremaintenancewi01
      RAISING
        /aws1/cx_rt_generic .
    METHODS delete_maintenance_window
      IMPORTING
        !iv_window_id TYPE /aws1/ssmmaintenancewindowid
      RAISING
        /aws1/cx_rt_generic .
    METHODS update_maintenance_window
      IMPORTING
        !iv_window_id                  TYPE /aws1/ssmmaintenancewindowid
        !iv_name                       TYPE /aws1/ssmmaintenancewindowname OPTIONAL
        !iv_enabled                    TYPE /aws1/ssmmaintenancewindowenbd OPTIONAL
        !iv_schedule                   TYPE /aws1/ssmmaintenancewindowschd OPTIONAL
        !iv_duration                   TYPE /aws1/ssmmaintenancewindowdu00 OPTIONAL
        !iv_cutoff                     TYPE /aws1/ssmmaintenancewindowcu00 OPTIONAL
        !iv_allow_unassociated_targets TYPE /aws1/ssmmaintenancewindowal00 OPTIONAL
      RAISING
        /aws1/cx_rt_generic .
    METHODS create_document
      IMPORTING
        !iv_content TYPE /aws1/ssmdocumentcontent
        !iv_name    TYPE /aws1/ssmdocumentname
      RAISING
        /aws1/cx_rt_generic .
    METHODS delete_document
      IMPORTING
        !iv_name TYPE /aws1/ssmdocumentname
      RAISING
        /aws1/cx_rt_generic .
    METHODS send_command
      IMPORTING
        !iv_document_name TYPE /aws1/ssmdocumentarn
        !it_instance_ids  TYPE /aws1/cl_ssminstanceidlist_w=>tt_instanceidlist
      RETURNING
        VALUE(rv_command_id) TYPE /aws1/ssmcommandid
      RAISING
        /aws1/cx_rt_generic .
    METHODS describe_document
      IMPORTING
        !iv_name           TYPE /aws1/ssmdocumentarn
      RETURNING
        VALUE(rv_status) TYPE /aws1/ssmdocumentstatus
      RAISING
        /aws1/cx_rt_generic .
    METHODS list_command_invocations
      IMPORTING
        !iv_instance_id TYPE /aws1/ssminstanceid
      RAISING
        /aws1/cx_rt_generic .
  PROTECTED SECTION.
  PRIVATE SECTION.
ENDCLASS.



CLASS /AWSEX/CL_SSM_ACTIONS IMPLEMENTATION.


  METHOD create_ops_item.

    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_ssm) = /aws1/cl_ssm_factory=>create( lo_session ).

    " snippet-start:[ssm.abapv1.create_ops_item]
    TRY.
        oo_result = lo_ssm->createopsitem(
            iv_title = iv_title
            iv_source = iv_source
            iv_category = iv_category
            iv_severity = iv_severity
            iv_description = iv_description ).
        MESSAGE 'OpsItem created.' TYPE 'I'.
      CATCH /aws1/cx_ssmopsitemlimitexcdex.
        MESSAGE 'You have exceeded your open OpsItem limit.' TYPE 'I'.
      CATCH /aws1/cx_ssmopsitemalrdyexex.
        MESSAGE 'OpsItem already exists.' TYPE 'I'.
    ENDTRY.
    " snippet-end:[ssm.abapv1.create_ops_item]
  ENDMETHOD.


  METHOD delete_ops_item.

    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_ssm) = /aws1/cl_ssm_factory=>create( lo_session ).

    " snippet-start:[ssm.abapv1.delete_ops_item]
    TRY.
        lo_ssm->deleteopsitem( iv_opsitemid = iv_ops_item_id ).
        MESSAGE 'OpsItem deleted.' TYPE 'I'.
      CATCH /aws1/cx_ssmopsiteminvparamex.
        MESSAGE 'Invalid OpsItem parameter.' TYPE 'I'.
    ENDTRY.
    " snippet-end:[ssm.abapv1.delete_ops_item]
  ENDMETHOD.


  METHOD describe_ops_items.

    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_ssm) = /aws1/cl_ssm_factory=>create( lo_session ).

    " snippet-start:[ssm.abapv1.describe_ops_items]
    TRY.
        " Create filter for OpsItem ID
        DATA(lt_filters) = VALUE /aws1/cl_ssmopsitemfilter=>tt_opsitemfilters(
          ( NEW /aws1/cl_ssmopsitemfilter(
              iv_key = 'OpsItemId'
              it_values = VALUE /aws1/cl_ssmopsitemfiltvals_w=>tt_opsitemfiltervalues(
                ( NEW /aws1/cl_ssmopsitemfiltvals_w( iv_value = iv_ops_item_id ) )
              )
              iv_operator = 'Equal'
            ) )
        ).

        " Use paginator to get all results
        DATA(lo_paginator) = lo_ssm->get_paginator( ).
        DATA(lo_iterator) = lo_paginator->describeopsitems(
          it_opsitemfilters = lt_filters ).

        rv_found = abap_false.

        WHILE lo_iterator->has_next( ).
          DATA(lo_result) = CAST /aws1/cl_ssmdescropsitemsrsp( lo_iterator->get_next( ) ).
          LOOP AT lo_result->get_opsitemsummaries( ) INTO DATA(lo_item).
            DATA(lv_title) = lo_item->get_title( ).
            DATA(lv_status) = lo_item->get_status( ).
            MESSAGE |The OpsItem title is { lv_title } and the status is { lv_status }| TYPE 'I'.
            rv_found = abap_true.
          ENDLOOP.
        ENDWHILE.
      CATCH /aws1/cx_ssminternalservererr.
        MESSAGE 'Internal server error occurred.' TYPE 'I'.
    ENDTRY.
    " snippet-end:[ssm.abapv1.describe_ops_items]
  ENDMETHOD.


  METHOD update_ops_item.

    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_ssm) = /aws1/cl_ssm_factory=>create( lo_session ).

    " snippet-start:[ssm.abapv1.update_ops_item]
    TRY.
        lo_ssm->updateopsitem(
            iv_opsitemid = iv_ops_item_id
            iv_title = iv_title
            iv_description = iv_description
            iv_status = iv_status ).
        MESSAGE 'OpsItem updated.' TYPE 'I'.
      CATCH /aws1/cx_ssmopsitemnotfoundex.
        MESSAGE 'OpsItem not found.' TYPE 'I'.
      CATCH /aws1/cx_ssmopsiteminvparamex.
        MESSAGE 'Invalid OpsItem parameter.' TYPE 'I'.
    ENDTRY.
    " snippet-end:[ssm.abapv1.update_ops_item]
  ENDMETHOD.


  METHOD create_maintenance_window.

    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_ssm) = /aws1/cl_ssm_factory=>create( lo_session ).

    " snippet-start:[ssm.abapv1.create_maintenance_window]
    TRY.
        oo_result = lo_ssm->createmaintenancewindow(
            iv_name = iv_name
            iv_schedule = iv_schedule
            iv_duration = iv_duration
            iv_cutoff = iv_cutoff
            iv_allowunassociatedtargets = iv_allow_unassociated_targets ).
        MESSAGE 'Maintenance window created.' TYPE 'I'.
      CATCH /aws1/cx_ssmresrclimitexcdex.
        MESSAGE 'Resource limit exceeded.' TYPE 'I'.
    ENDTRY.
    " snippet-end:[ssm.abapv1.create_maintenance_window]
  ENDMETHOD.


  METHOD delete_maintenance_window.

    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_ssm) = /aws1/cl_ssm_factory=>create( lo_session ).

    " snippet-start:[ssm.abapv1.delete_maintenance_window]
    TRY.
        lo_ssm->deletemaintenancewindow( iv_windowid = iv_window_id ).
        MESSAGE 'Maintenance window deleted.' TYPE 'I'.
      CATCH /aws1/cx_ssminternalservererr.
        MESSAGE 'Internal server error occurred.' TYPE 'I'.
    ENDTRY.
    " snippet-end:[ssm.abapv1.delete_maintenance_window]
  ENDMETHOD.


  METHOD update_maintenance_window.

    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_ssm) = /aws1/cl_ssm_factory=>create( lo_session ).

    " snippet-start:[ssm.abapv1.update_maintenance_window]
    TRY.
        lo_ssm->updatemaintenancewindow(
            iv_windowid = iv_window_id
            iv_name = iv_name
            iv_enabled = iv_enabled
            iv_schedule = iv_schedule
            iv_duration = iv_duration
            iv_cutoff = iv_cutoff
            iv_allowunassociatedtargets = iv_allow_unassociated_targets ).
        MESSAGE 'Maintenance window updated.' TYPE 'I'.
      CATCH /aws1/cx_ssmdoesnotexistex.
        MESSAGE 'Maintenance window does not exist.' TYPE 'I'.
    ENDTRY.
    " snippet-end:[ssm.abapv1.update_maintenance_window]
  ENDMETHOD.


  METHOD create_document.

    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_ssm) = /aws1/cl_ssm_factory=>create( lo_session ).

    " snippet-start:[ssm.abapv1.create_document]
    TRY.
        lo_ssm->createdocument(
            iv_name = iv_name
            iv_content = iv_content
            iv_documenttype = 'Command' ).
        MESSAGE 'Document created.' TYPE 'I'.
      CATCH /aws1/cx_ssmdocalreadyexists.
        MESSAGE 'Document already exists.' TYPE 'I'.
      CATCH /aws1/cx_ssminvaliddoccontent.
        MESSAGE 'Invalid document content.' TYPE 'I'.
    ENDTRY.
    " snippet-end:[ssm.abapv1.create_document]
  ENDMETHOD.


  METHOD delete_document.

    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_ssm) = /aws1/cl_ssm_factory=>create( lo_session ).

    " snippet-start:[ssm.abapv1.delete_document]
    TRY.
        lo_ssm->deletedocument( iv_name = iv_name ).
        MESSAGE 'Document deleted.' TYPE 'I'.
      CATCH /aws1/cx_ssminvaliddocument.
        MESSAGE 'Invalid document.' TYPE 'I'.
      CATCH /aws1/cx_ssmassocdinstances.
        MESSAGE 'Document has associated instances.' TYPE 'I'.
    ENDTRY.
    " snippet-end:[ssm.abapv1.delete_document]
  ENDMETHOD.


  METHOD send_command.

    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_ssm) = /aws1/cl_ssm_factory=>create( lo_session ).

    " snippet-start:[ssm.abapv1.send_command]
    TRY.
        DATA(lo_result) = lo_ssm->sendcommand(
            it_instanceids = it_instance_ids
            iv_documentname = iv_document_name
            iv_timeoutseconds = 3600 ).
        DATA(lo_command) = lo_result->get_command( ).
        IF lo_command IS BOUND.
          rv_command_id = lo_command->get_commandid( ).
          MESSAGE 'Command sent successfully.' TYPE 'I'.
        ENDIF.
      CATCH /aws1/cx_ssminvaliddocument.
        MESSAGE 'Invalid document.' TYPE 'I'.
      CATCH /aws1/cx_ssminvalidinstanceid.
        MESSAGE 'Invalid instance ID.' TYPE 'I'.
    ENDTRY.
    " snippet-end:[ssm.abapv1.send_command]
  ENDMETHOD.


  METHOD describe_document.

    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_ssm) = /aws1/cl_ssm_factory=>create( lo_session ).

    " snippet-start:[ssm.abapv1.describe_document]
    TRY.
        DATA(lo_result) = lo_ssm->describedocument( iv_name = iv_name ).
        DATA(lo_document) = lo_result->get_document( ).
        IF lo_document IS BOUND.
          rv_status = lo_document->get_status( ).
          MESSAGE |Document status: { rv_status }| TYPE 'I'.
        ENDIF.
      CATCH /aws1/cx_ssminvaliddocument.
        MESSAGE 'Invalid document.' TYPE 'I'.
    ENDTRY.
    " snippet-end:[ssm.abapv1.describe_document]
  ENDMETHOD.


  METHOD list_command_invocations.

    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_ssm) = /aws1/cl_ssm_factory=>create( lo_session ).

    " snippet-start:[ssm.abapv1.list_command_invocations]
    TRY.
        " Use paginator to get all results
        DATA(lo_paginator) = lo_ssm->get_paginator( ).
        DATA(lo_iterator) = lo_paginator->listcommandinvocations(
          iv_instanceid = iv_instance_id ).

        DATA lv_count TYPE i VALUE 0.

        WHILE lo_iterator->has_next( ).
          DATA(lo_result) = CAST /aws1/cl_ssmlistcmdinvcsresult( lo_iterator->get_next( ) ).
          LOOP AT lo_result->get_commandinvocations( ) INTO DATA(lo_invocation).
            lv_count = lv_count + 1.
            DATA(lv_requested_datetime) = lo_invocation->get_requesteddatetime( ).
            MESSAGE |Command invocation requested at: { lv_requested_datetime }| TYPE 'I'.
          ENDLOOP.
        ENDWHILE.

        MESSAGE |{ lv_count } command invocation(s) found for instance { iv_instance_id }.| TYPE 'I'.
      CATCH /aws1/cx_ssminvalidinstanceid.
        MESSAGE 'Invalid instance ID.' TYPE 'I'.
      CATCH /aws1/cx_ssminvalidcommandid.
        MESSAGE 'Invalid command ID.' TYPE 'I'.
    ENDTRY.
    " snippet-end:[ssm.abapv1.list_command_invocations]
  ENDMETHOD.
ENDCLASS.
