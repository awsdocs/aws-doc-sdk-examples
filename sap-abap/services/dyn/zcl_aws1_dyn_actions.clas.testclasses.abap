" """"""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""
" "  Copyright 2022 Amazon.com, Inc. or its affiliates. All Rights
" "  Reserved.
" "  SPDX-License-Identifier: MIT-0
" """"""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""

CLASS ltc_zcl_aws1_dyn_actions DEFINITION DEFERRED.
CLASS zcl_aws1_dyn_actions DEFINITION LOCAL FRIENDS ltc_zcl_aws1_dyn_actions.

CLASS ltc_zcl_aws1_dyn_actions DEFINITION FOR TESTING
  DURATION LONG
  RISK LEVEL HARMLESS.

  PROTECTED SECTION.
    METHODS test_dyn FOR TESTING RAISING /aws1/cx_rt_generic.

  PRIVATE SECTION.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA ao_dyn TYPE REF TO /aws1/if_dyn.
    DATA ao_session TYPE REF TO /aws1/cl_rt_session_base.
    DATA ao_dyn_actions TYPE REF TO zcl_aws1_dyn_actions.
    DATA av_table_name TYPE /aws1/dyntablename.

    METHODS setup RAISING /aws1/cx_rt_generic.
    METHODS teardown RAISING /aws1/cx_rt_generic.

    METHODS delete_table RAISING /aws1/cx_rt_generic.
    METHODS assert_table_exists RAISING /aws1/cx_rt_generic.
    METHODS assert_table_notexists RAISING /aws1/cx_rt_generic.

ENDCLASS.

CLASS ltc_zcl_aws1_dyn_actions IMPLEMENTATION.

  METHOD setup.
    ao_session = /aws1/cl_rt_session_aws=>create( iv_profile_id = cv_pfl ).
    ao_dyn = /aws1/cl_dyn_factory=>create( ao_session ).
    ao_dyn_actions = NEW zcl_aws1_dyn_actions( ).
  ENDMETHOD.

  METHOD teardown.
    delete_table( ).
  ENDMETHOD.

  METHOD test_dyn.
    DATA(lv_table) = ao_dyn_actions->create_table( ).
    av_table_name = lv_table->get_tabledescription( )->ask_tablename( ).
    assert_table_exists( ).
    DATA(lv_table_description) = ao_dyn_actions->describe_table(
      av_table_name ).
    ao_dyn_actions->describe_table( av_table_name ).
    ao_dyn_actions->list_tables( ).
    ao_dyn_actions->put_item( av_table_name ).
    ao_dyn_actions->get_item( av_table_name ).
    ao_dyn_actions->query_items( av_table_name ).
    ao_dyn_actions->scan_items( av_table_name ).
    ao_dyn_actions->update_item( av_table_name ).
    ao_dyn_actions->delete_item( av_table_name ).
    ao_dyn_actions->delete_table( av_table_name ).
    assert_table_notexists( ).
  ENDMETHOD.

  METHOD assert_table_exists.
    DATA(lv_status) = ao_dyn->describetable( iv_tablename = av_table_name )->get_table( )->get_tablestatus( ).
    lv_status = ao_dyn->describetable( iv_tablename = av_table_name )->get_table( )->get_tablestatus( ).
    cl_abap_unit_assert=>assert_equals(
            exp = lv_status
            act = 'ACTIVE'
            msg = |Expected the table to be in 'ACTIVE' status but received { lv_status }| ).
  ENDMETHOD.

  METHOD assert_table_notexists.
    TRY.
        DATA(lv_status) = ao_dyn->describetable( iv_tablename = av_table_name )->get_table( )->get_tablestatus( ).
        /aws1/cl_rt_assert_abap=>assert_missed_exception( iv_exception = |/AWS1/CX_RT_SERVICE_GENERIC| ).
      CATCH /aws1/cx_rt_service_generic.
      "ignore. expected since the table does not exist
    ENDTRY.
  ENDMETHOD.

  METHOD delete_table.
    TRY.
        DATA(lo_resp) = ao_dyn->deletetable( av_table_name ).
        DATA(lv_status) = ao_dyn->describetable( iv_tablename = av_table_name )->get_table( )->get_tablestatus( ).
        cl_abap_unit_assert=>assert_equals(
                      exp = lv_status
                      act = 'DELETING'
                      msg = |Expected the table to be in 'DELETING' status but received { lv_status }| ).
        ao_dyn->get_waiter( )->tablenotexists(
          iv_max_wait_time = 200
          iv_tablename     = av_table_name ).
        lv_status = ao_dyn->describetable( iv_tablename = av_table_name )->get_table( )->get_tablestatus( ).
      " expecting an exception
        /aws1/cl_rt_assert_abap=>assert_missed_exception( iv_exception = |/AWS1/CX_DYNRESOURCENOTFOUNDEX| ).
      CATCH /aws1/cx_dynresourcenotfoundex.
        " good, it is deleted
    ENDTRY.
  ENDMETHOD.

ENDCLASS.
