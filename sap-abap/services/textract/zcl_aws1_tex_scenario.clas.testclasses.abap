" """"""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""
" "  Copyright 2022 Amazon.com, Inc. or its affiliates. All Rights
" "  Reserved.
" "  SPDX-License-Identifier: MIT-0
" """"""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""

CLASS ltc_zcl_aws1_tex_scenario DEFINITION FOR TESTING DURATION SHORT RISK LEVEL HARMLESS.

  PRIVATE SECTION.

    CONSTANTS: cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA ao_tex TYPE REF TO /aws1/if_tex.
    DATA ao_session TYPE REF TO /aws1/cl_rt_session_base.
    DATA ao_tex_scenario TYPE REF TO zcl_aws1_tex_scenario.
    DATA lv_found TYPE abap_bool VALUE abap_false.

    METHODS: setup RAISING /aws1/cx_rt_generic ycx_aws1_mit_generic.
    METHODS: getting_started_with_tex FOR TESTING.

ENDCLASS.       "ltc_Zcl_Aws1_Tex_Scenario

CLASS ltc_zcl_aws1_tex_scenario IMPLEMENTATION.

  METHOD setup.
    ao_session = /aws1/cl_rt_session_aws=>create( iv_profile_id = cv_pfl ).
    ao_tex = /aws1/cl_tex_factory=>create( ao_session ).
    ao_tex_scenario = NEW zcl_aws1_tex_scenario( ).
  ENDMETHOD.


  METHOD getting_started_with_tex.

    DATA lv_found TYPE abap_bool VALUE abap_false.
    DATA lo_output TYPE REF TO /aws1/cl_texgetdocalyresponse.
    DATA lt_blocks TYPE /aws1/cl_texblock=>tt_blocklist.
    DATA lo_block TYPE REF TO  /aws1/cl_texblock.

    "Using an image from the Public Amazon Berkeley Objects Dataset.
    CONSTANTS cv_bucket_name TYPE /aws1/s3_bucketname VALUE 'amazon-berkeley-objects'.
    CONSTANTS cv_key_name TYPE /aws1/s3_bucketname VALUE 'images/small/e0/e0feb1eb.jpg'.

    "Analyze document.
    ao_tex_scenario->getting_started_with_tex(
      EXPORTING
        iv_s3object          = cv_key_name
        iv_s3bucket          = cv_bucket_name
      IMPORTING
        oo_result           = lo_output
      ).

    "Validation check.
    lv_found = abap_false.
    lt_blocks = lo_output->get_blocks( ).
    LOOP AT lt_blocks INTO lo_block.
      IF lo_block->get_text( ) = 'INGREDIENTS: POWDERED SUGAR* (CANE SUGAR,'.
        lv_found = abap_true.
      ENDIF.
    ENDLOOP.

    cl_abap_unit_assert=>assert_true(
      act = lv_found
      msg = |Analyze document failed|
    ).

  ENDMETHOD.
ENDCLASS.
