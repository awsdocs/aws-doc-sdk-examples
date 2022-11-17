" """"""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""
" "  Copyright 2022 Amazon.com, Inc. or its affiliates. All Rights
" "  Reserved.
" "  SPDX-License-Identifier: MIT-0
" """"""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""

CLASS ltc_zcl_aws1_s3_scenario DEFINITION DEFERRED.
CLASS zcl_aws1_s3_scenario DEFINITION LOCAL FRIENDS ltc_zcl_aws1_s3_scenario.

CLASS ltc_zcl_aws1_s3_scenario DEFINITION FOR TESTING  DURATION SHORT RISK LEVEL HARMLESS.

  PRIVATE SECTION.
    CONSTANTS: cv_pfl            TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO',
               cv_bucket         TYPE /aws1/s3_bucketname VALUE 'code-example-scenario-bucket',
               cv_file           TYPE /aws1/s3_objectkey VALUE 's3_scenario_ex_file',
               cv_copy_to_folder TYPE /aws1/s3_bucketname VALUE 'code-example-scenario-folder'.

    DATA ao_s3 TYPE REF TO /aws1/if_s3.
    DATA ao_session TYPE REF TO /aws1/cl_rt_session_base.
    DATA ao_s3_scenario TYPE REF TO zcl_aws1_s3_scenario.

    METHODS getting_started_scenario FOR TESTING RAISING /aws1/cx_rt_generic.

    METHODS setup RAISING /aws1/cx_rt_generic ycx_aws1_mit_generic.

ENDCLASS.

CLASS ltc_zcl_aws1_s3_scenario IMPLEMENTATION.

  METHOD setup.
    ao_session = /aws1/cl_rt_session_aws=>create( iv_profile_id = cv_pfl ).
    ao_s3 = /aws1/cl_s3_factory=>create( ao_session ).
    ao_s3_scenario = NEW zcl_aws1_s3_scenario( ).

    DATA lv_param TYPE btcxpgpar.
    lv_param = |if=/dev/random of={ cv_file } bs=1M count=1 iflag=fullblock|.
    CALL FUNCTION 'SXPG_COMMAND_EXECUTE'
      EXPORTING
        commandname           = 'DB24DD'
        additional_parameters = lv_param
        operatingsystem       = 'ANYOS'
      EXCEPTIONS
        OTHERS                = 15.
    /aws1/cl_rt_assert_abap=>assert_subrc( iv_exp = 0 iv_msg = |Could not create { cv_file }| ).


  ENDMETHOD.
  METHOD getting_started_scenario.
    ao_s3_scenario->getting_started_with_s3(
      EXPORTING
        iv_bucket_name = cv_bucket
        iv_key = cv_file
        iv_copy_to_folder = cv_copy_to_folder
    ).

    DATA lv_found TYPE abap_bool VALUE abap_false.
    LOOP AT ao_s3->listbuckets( )->get_buckets( ) INTO DATA(lo_bucket).
      IF lo_bucket->get_name( ) = cv_bucket.
        lv_found = abap_true.
      ENDIF.
    ENDLOOP.

    cl_abap_unit_assert=>assert_equals(
      exp = abap_false
      act = lv_found
      msg = |Bucket { cv_bucket } should have been deleted|
    ).
  ENDMETHOD.
ENDCLASS.
