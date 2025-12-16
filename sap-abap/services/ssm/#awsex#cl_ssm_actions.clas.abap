" Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
" Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
" SPDX-License-Identifier: Apache-2.0
CLASS /awsex/cl_ssm_actions DEFINITION
  PUBLIC
  FINAL
  CREATE PUBLIC .

  PUBLIC SECTION.

    METHODS create_document
      IMPORTING
        !iv_content         TYPE /aws1/ssmdocumentcontent
        !iv_name            TYPE /aws1/ssmdocumentname
        !iv_document_type   TYPE /aws1/ssmdocumenttype DEFAULT 'Command'
      RETURNING
        VALUE(oo_result)    TYPE REF TO /aws1/cl_ssmcreatedocresult
      RAISING
        /aws1/cx_rt_generic.

    METHODS delete_document
      IMPORTING
        !iv_name TYPE /aws1/ssmdocumentname
      RAISING
        /aws1/cx_rt_generic.

    METHODS send_command
      IMPORTING
        !iv_document_name   TYPE /aws1/ssmdocumentarn
        !it_instance_ids    TYPE /aws1/cl_ssminstanceidlist_w=>tt_instanceidlist
        !iv_timeout_seconds TYPE /aws1/ssmtimeoutseconds DEFAULT 3600
      RETURNING
        VALUE(ov_command_id) TYPE /aws1/ssmcommandid
      RAISING
        /aws1/cx_rt_generic.

    METHODS describe_document
      IMPORTING
        !iv_name         TYPE /aws1/ssmdocumentarn
      RETURNING
        VALUE(ov_status) TYPE /aws1/ssmdocumentstatus
      RAISING
        /aws1/cx_rt_generic.

    METHODS list_command_invocations
      IMPORTING
        !iv_instance_id TYPE /aws1/ssminstanceid
      RAISING
        /aws1/cx_rt_generic.

    METHODS create_maintenance_window
      IMPORTING
        !iv_name                         TYPE /aws1/ssmmaintenancewindowname
        !iv_schedule                     TYPE /aws1/ssmmaintenancewindowschd
        !iv_duration                     TYPE /aws1/ssmmaintenancewindowdu00
        !iv_cutoff                       TYPE /aws1/ssmmaintenancewindowcu00
        !iv_allow_unassociated_targets   TYPE /aws1/ssmmaintenancewindowal00
      RETURNING
        VALUE(ov_window_id)              TYPE /aws1/ssmmaintenancewindowid
      RAISING
        /aws1/cx_rt_generic.

    METHODS delete_maintenance_window
      IMPORTING
        !iv_window_id TYPE /aws1/ssmmaintenancewindowid
      RAISING
        /aws1/cx_rt_generic.

    METHODS update_maintenance_window
      IMPORTING
        !iv_window_id                    TYPE /aws1/ssmmaintenancewindowid
        !iv_name                         TYPE /aws1/ssmmaintenancewindowname OPTIONAL
        !iv_enabled                      TYPE /aws1/ssmmaintenancewindowenbd OPTIONAL
        !iv_schedule                     TYPE /aws1/ssmmaintenancewindowschd OPTIONAL
        !iv_duration                     TYPE /aws1/ssmmaintenancewindowdu00 OPTIONAL
        !iv_cutoff                       TYPE /aws1/ssmmaintenancewindowcu00 OPTIONAL
        !iv_allow_unassociated_targets   TYPE /aws1/ssmmaintenancewindowal00 OPTIONAL
      RAISING
        /aws1/cx_rt_generic.

    METHODS create_ops_item
      IMPORTING
        !iv_title            TYPE /aws1/ssmopsitemtitle
        !iv_source           TYPE /aws1/ssmopsitemsource
        !iv_category         TYPE /aws1/ssmopsitemcategory
        !iv_severity         TYPE /aws1/ssmopsitemseverity
        !iv_description      TYPE /aws1/ssmopsitemdescription
      RETURNING
        VALUE(ov_ops_item_id) TYPE /aws1/ssmopsitemid
      RAISING
        /aws1/cx_rt_generic.

    METHODS delete_ops_item
      IMPORTING
        !iv_ops_item_id TYPE /aws1/ssmopsitemid
      RAISING
        /aws1/cx_rt_generic.

    METHODS describe_ops_items
      IMPORTING
        !iv_ops_item_id TYPE /aws1/ssmopsitemid
      RETURNING
        VALUE(ov_found) TYPE abap_bool
      RAISING
        /aws1/cx_rt_generic.

    METHODS update_ops_item
      IMPORTING
        !iv_ops_item_id  TYPE /aws1/ssmopsitemid
        !iv_title        TYPE /aws1/ssmopsitemtitle OPTIONAL
        !iv_description  TYPE /aws1/ssmopsitemdescription OPTIONAL
        !iv_status       TYPE /aws1/ssmopsitemstatus OPTIONAL
      RAISING
        /aws1/cx_rt_generic.

  PROTECTED SECTION.
  PRIVATE SECTION.
ENDCLASS.



CLASS /AWSEX/CL_SSM_ACTIONS IMPLEMENTATION.


  METHOD create_document.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_ssm) = /aws1/cl_ssm_factory=>create( lo_session ).

    " snippet-start:[ssm.abapv1.create_document]
    TRY.
        oo_result = lo_ssm->createdocument(
          iv_name = iv_name
          iv_content = iv_content
          iv_documenttype = iv_document_type
        ).
        MESSAGE 'SSM document created.' TYPE 'I'.
      CATCH /aws1/cx_ssmdocalreadyexists.
        MESSAGE 'Document already exists.' TYPE 'I'.
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
        MESSAGE 'SSM document deleted.' TYPE 'I'.
      CATCH /aws1/cx_ssminvaliddocument.
        MESSAGE 'Document does not exist.' TYPE 'E'.
      CATCH /aws1/cx_ssmassocdinstances.
        MESSAGE 'Document is associated with instances.' TYPE 'E'.
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
          iv_documentname = iv_document_name
          it_instanceids = it_instance_ids
          iv_timeoutseconds = iv_timeout_seconds
        ).
        ov_command_id = lo_result->get_command( )->get_commandid( ).
        MESSAGE 'Command sent to instances.' TYPE 'I'.
      CATCH /aws1/cx_ssminvaliddocument.
        MESSAGE 'The specified document does not exist.' TYPE 'E'.
      CATCH /aws1/cx_ssminvalidinstanceid.
        MESSAGE 'The specified instance ID is not valid.' TYPE 'E'.
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
        ov_status = lo_result->get_document( )->get_status( ).
      CATCH /aws1/cx_ssminvaliddocument.
        MESSAGE 'The specified document does not exist.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[ssm.abapv1.describe_document]
  ENDMETHOD.


  METHOD list_command_invocations.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_ssm) = /aws1/cl_ssm_factory=>create( lo_session ).

    " snippet-start:[ssm.abapv1.list_command_invocations]
    TRY.
        " Use paginator to get all command invocations for the instance
        DATA(lo_paginator) = lo_ssm->get_paginator( ).
        DATA(lo_iterator) = lo_paginator->listcommandinvocations(
          iv_instanceid = iv_instance_id
        ).

        DATA lv_count TYPE i VALUE 0.
        DATA lv_displayed TYPE i VALUE 0.

        " Count total invocations
        DATA lt_invocations TYPE /aws1/cl_ssmcommandinvocation=>tt_commandinvocationlist.
        WHILE lo_iterator->has_next( ).
          DATA(lo_page) = lo_iterator->get_next( ).
          APPEND LINES OF lo_page->get_commandinvocations( ) TO lt_invocations.
        ENDWHILE.

        lv_count = lines( lt_invocations ).
        MESSAGE |{ lv_count } command invocation(s) found for instance { iv_instance_id }.| TYPE 'I'.

        " Display up to 10 command invocations
        IF lv_count > 10.
          MESSAGE 'Displaying the first 10 commands:' TYPE 'I'.
          lv_displayed = 10.
        ELSE.
          lv_displayed = lv_count.
        ENDIF.

        DATA lv_index TYPE i VALUE 1.
        LOOP AT lt_invocations INTO DATA(lo_invocation).
          IF lv_index > lv_displayed.
            EXIT.
          ENDIF.
          DATA(lv_timestamp) = lo_invocation->get_requesteddatetime( ).
          " Convert timestamp to readable format would be done here
          MESSAGE |Command invocation at { lv_timestamp }| TYPE 'I'.
          lv_index = lv_index + 1.
        ENDLOOP.

      CATCH /aws1/cx_ssminvalidinstanceid.
        MESSAGE 'The specified instance ID is not valid.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[ssm.abapv1.list_command_invocations]
  ENDMETHOD.


  METHOD create_maintenance_window.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_ssm) = /aws1/cl_ssm_factory=>create( lo_session ).

    " snippet-start:[ssm.abapv1.create_maintenance_window]
    TRY.
        DATA(lo_result) = lo_ssm->createmaintenancewindow(
          iv_name = iv_name
          iv_schedule = iv_schedule
          iv_duration = iv_duration
          iv_cutoff = iv_cutoff
          iv_allowunassociatedtargets = iv_allow_unassociated_targets
        ).
        ov_window_id = lo_result->get_windowid( ).
        MESSAGE 'Maintenance window created.' TYPE 'I'.
      CATCH /aws1/cx_ssmresrclimitexcdex.
        MESSAGE 'Resource limit exceeded.' TYPE 'E'.
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
        MESSAGE 'Internal server error occurred.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[ssm.abapv1.delete_maintenance_window]
  ENDMETHOD.


  METHOD update_maintenance_window.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_ssm) = /aws1/cl_ssm_factory=>create( lo_session ).

    " snippet-start:[ssm.abapv1.update_maintenance_window]
    TRY.
        " Only pass non-initial parameters to avoid validation errors
        " AWS SSM validates that duration >= 1 and schedule has proper format
        IF iv_name IS NOT INITIAL.
          " Updating name
          lo_ssm->updatemaintenancewindow(
            iv_windowid = iv_window_id
            iv_name = iv_name
            iv_enabled = iv_enabled
            iv_allowunassociatedtargets = iv_allow_unassociated_targets
          ).
        ELSE.
          " Just updating enabled status
          lo_ssm->updatemaintenancewindow(
            iv_windowid = iv_window_id
            iv_enabled = iv_enabled
            iv_allowunassociatedtargets = iv_allow_unassociated_targets
          ).
        ENDIF.
        MESSAGE 'Maintenance window updated.' TYPE 'I'.
      CATCH /aws1/cx_ssmdoesnotexistex.
        MESSAGE 'Maintenance window does not exist.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[ssm.abapv1.update_maintenance_window]
  ENDMETHOD.


  METHOD create_ops_item.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_ssm) = /aws1/cl_ssm_factory=>create( lo_session ).

    " snippet-start:[ssm.abapv1.create_ops_item]
    TRY.
        DATA(lo_result) = lo_ssm->createopsitem(
          iv_title = iv_title
          iv_source = iv_source
          iv_category = iv_category
          iv_severity = iv_severity
          iv_description = iv_description
        ).
        ov_ops_item_id = lo_result->get_opsitemid( ).
        MESSAGE 'OpsItem created.' TYPE 'I'.
      CATCH /aws1/cx_ssmopsitemlimitexcdex.
        MESSAGE 'OpsItem limit exceeded.' TYPE 'E'.
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
        MESSAGE 'Invalid OpsItem ID.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[ssm.abapv1.delete_ops_item]
  ENDMETHOD.


  METHOD describe_ops_items.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_ssm) = /aws1/cl_ssm_factory=>create( lo_session ).

    " snippet-start:[ssm.abapv1.describe_ops_items]
    TRY.
        " Create filter for specific OpsItem ID
        DATA lt_filters TYPE /aws1/cl_ssmopsitemfilter=>tt_opsitemfilters.
        DATA lt_filter_values TYPE /aws1/cl_ssmopsitemfiltvals_w=>tt_opsitemfiltervalues.

        APPEND NEW /aws1/cl_ssmopsitemfiltvals_w( iv_value = iv_ops_item_id ) TO lt_filter_values.
        APPEND NEW /aws1/cl_ssmopsitemfilter(
          iv_key = 'OpsItemId'
          it_values = lt_filter_values
          iv_operator = 'Equal'
        ) TO lt_filters.

        " Use paginator to get all OpsItems matching the filter
        DATA(lo_paginator) = lo_ssm->get_paginator( ).
        DATA(lo_iterator) = lo_paginator->describeopsitems(
          it_opsitemfilters = lt_filters
        ).

        ov_found = abap_false.
        WHILE lo_iterator->has_next( ).
          DATA(lo_page) = lo_iterator->get_next( ).
          LOOP AT lo_page->get_opsitemsummaries( ) INTO DATA(lo_item).
            MESSAGE |The item title is { lo_item->get_title( ) } and the status is { lo_item->get_status( ) }| TYPE 'I'.
            ov_found = abap_true.
          ENDLOOP.
        ENDWHILE.

      CATCH /aws1/cx_ssminternalservererr.
        MESSAGE 'Internal server error occurred.' TYPE 'E'.
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
          iv_status = iv_status
        ).
        MESSAGE 'OpsItem updated.' TYPE 'I'.
      CATCH /aws1/cx_ssmopsitemnotfoundex.
        MESSAGE 'OpsItem does not exist.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[ssm.abapv1.update_ops_item]
  ENDMETHOD.
ENDCLASS.
