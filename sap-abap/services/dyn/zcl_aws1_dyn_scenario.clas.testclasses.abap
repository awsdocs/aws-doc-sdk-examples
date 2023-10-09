" """"""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""
" "  Copyright 2022 Amazon.com, Inc. or its affiliates. All Rights
" "  Reserved.
" "  SPDX-License-Identifier: MIT-0
" """"""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""

CLASS ltc_zcl_aws1_dyn_scenario DEFINITION DEFERRED.
CLASS zcl_aws1_dyn_scenario DEFINITION LOCAL FRIENDS ltc_zcl_aws1_dyn_scenario.

CLASS ltc_zcl_aws1_dyn_scenario DEFINITION FOR TESTING
  DURATION LONG
  RISK LEVEL HARMLESS.

  PROTECTED SECTION.
    METHODS test_dyn FOR TESTING RAISING /aws1/cx_rt_generic.

  PRIVATE SECTION.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA ao_dyn TYPE REF TO /aws1/if_dyn.
    DATA ao_session TYPE REF TO /aws1/cl_rt_session_base.
    DATA ao_dyn_scenario TYPE REF TO zcl_aws1_dyn_scenario.
    DATA av_table_name TYPE /aws1/dyntablename.

    METHODS setup RAISING /aws1/cx_rt_generic.

    METHODS assert_table_not_exist
       IMPORTING iv_table_name TYPE string
       RAISING /aws1/cx_rt_generic.

ENDCLASS.

CLASS ltc_zcl_aws1_dyn_scenario IMPLEMENTATION.

  METHOD setup.
    ao_session = /aws1/cl_rt_session_aws=>create( iv_profile_id = cv_pfl ).
    ao_dyn = /aws1/cl_dyn_factory=>create( ao_session ).
    ao_dyn_scenario = NEW zcl_aws1_dyn_scenario( ).
  ENDMETHOD.

  METHOD test_dyn.
    DATA(av_table_name) = |code-example-getting-startted-with-tables|.
    ao_dyn_scenario->getting_started_with_tables( av_table_name ).
    assert_table_not_exist( iv_table_name = av_table_name ).
  ENDMETHOD.

  METHOD assert_table_not_exist.
    TRY.
        DATA(lv_status) = ao_dyn->describetable( iv_tablename = iv_table_name )->get_table( )->get_tablestatus( ).
      " expecting an exception
        /aws1/cl_rt_assert_abap=>assert_missed_exception( iv_exception = |/AWS1/CX_DYNRESOURCENOTFOUNDEX| ).
      CATCH /aws1/cx_dynresourcenotfoundex.
      " good, it is deleted
    ENDTRY.
  ENDMETHOD.

ENDCLASS.
