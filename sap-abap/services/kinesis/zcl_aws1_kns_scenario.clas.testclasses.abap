" """"""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""
" "  Copyright 2022 Amazon.com, Inc. or its affiliates. All Rights
" "  Reserved.
" "  SPDX-License-Identifier: MIT-0
" """"""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""

CLASS ltc_zcl_aws1_kns_scenario DEFINITION FOR TESTING DURATION SHORT RISK LEVEL HARMLESS.

  PRIVATE SECTION.

    CONSTANTS: cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA ao_kns TYPE REF TO /aws1/if_kns.
    DATA ao_session TYPE REF TO /aws1/cl_rt_session_base.
    DATA ao_kns_scenario TYPE REF TO zcl_aws1_kns_scenario.
    DATA lv_found TYPE abap_bool VALUE abap_false.

    METHODS: getting_started_with_kns FOR TESTING.
    METHODS: setup RAISING /aws1/cx_rt_generic ycx_aws1_mit_generic.

ENDCLASS.       "ltc_Zcl_Aws1_Kns_Scenario


CLASS ltc_zcl_aws1_kns_scenario IMPLEMENTATION.

  METHOD setup.
    ao_session = /aws1/cl_rt_session_aws=>create( iv_profile_id = cv_pfl ).
    ao_kns = /aws1/cl_kns_factory=>create( ao_session ).
    ao_kns_scenario = NEW zcl_aws1_kns_scenario( ).
  ENDMETHOD.

  METHOD getting_started_with_kns.

    DATA lo_get_record_output TYPE REF TO /aws1/cl_knsgetrecordsoutput.
    DATA lt_record_list TYPE /aws1/cl_knsrecord=>tt_recordlist.
    DATA lv_record_data TYPE /aws1/knsdata.
    DATA lv_stream_name TYPE /aws1/knsstreamname.
    DATA lv_shardid TYPE /aws1/knsshardid.
    DATA lv_found TYPE abap_bool VALUE abap_false.
    DATA(lv_data) = /aws1/cl_rt_util=>string_to_xstring(
      `{`  &&
        `"word": "This",`  &&
        `"word": "is"` &&
        `"word": "a"` &&
        `"word": "code"` &&
        `"word": "example"` &&
      `}`
    ).

    CONSTANTS cv_shard_count TYPE /aws1/knspositiveintegerobject VALUE 1.
    CONSTANTS cv_partition_key TYPE /aws1/knspartitionkey VALUE '123'.
    CONSTANTS cv_sharditeratortype TYPE /aws1/knssharditeratortype VALUE 'TRIM_HORIZON'.

    "Define name.
    DATA(lv_uuid_16) = cl_system_uuid=>create_uuid_x16_static( ).
    lv_stream_name = 'code-example-kns-stream-' && lv_uuid_16.
    TRANSLATE lv_stream_name TO LOWER CASE.

    ao_kns_scenario->getting_started_with_kns(
      EXPORTING
        iv_stream_name        = lv_stream_name
        iv_shard_count        = cv_shard_count
        iv_partition_key      = cv_partition_key
        iv_sharditeratortype  = cv_sharditeratortype
        iv_data               = lv_data
      IMPORTING
        oo_result = lo_get_record_output
      ).

    lt_record_list = lo_get_record_output->get_records( ).

    "Validation.
    LOOP AT lt_record_list INTO DATA(lo_record).
      lv_record_data = lo_record->get_data( ).
    ENDLOOP.

    IF lv_record_data = lv_data.
      lv_found = abap_true.
    ENDIF.

    cl_abap_unit_assert=>assert_true(
       act                    = lv_found
       msg                    = |Record not found|
    ).

    lv_found = abap_true.
    IF  ao_kns->liststreams( iv_exclusivestartstreamname = lv_stream_name )->has_streamnames( ) = 'X'.
      lv_found = abap_false.
    ENDIF.

    cl_abap_unit_assert=>assert_false(
       act                    = lv_found
       msg                    = |Stream not deleted|
    ).
  ENDMETHOD.

ENDCLASS.
