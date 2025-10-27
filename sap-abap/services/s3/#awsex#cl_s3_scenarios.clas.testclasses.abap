" Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
" SPDX-License-Identifier: Apache-2.0

CLASS ltc_awsex_cl_s3_scenario DEFINITION DEFERRED.
CLASS /awsex/cl_s3_scenarios DEFINITION LOCAL FRIENDS ltc_awsex_cl_s3_scenario.

CLASS ltc_awsex_cl_s3_scenario DEFINITION FOR TESTING DURATION SHORT RISK LEVEL DANGEROUS.

  PRIVATE SECTION.
    CONSTANTS: cv_pfl            TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO',
               cv_file           TYPE /aws1/s3_objectkey VALUE 's3_scenario_ex_file',
               cv_copy_to_folder TYPE /aws1/s3_bucketname VALUE 'code-example-scenario-folder'.

    DATA av_bucket         TYPE /aws1/s3_bucketname.

    DATA ao_s3 TYPE REF TO /aws1/if_s3.
    DATA ao_session TYPE REF TO /aws1/cl_rt_session_base.
    DATA ao_s3_scenario TYPE REF TO /awsex/cl_s3_scenarios.

    METHODS getting_started_scenario FOR TESTING RAISING /aws1/cx_rt_generic.
    METHODS presigner_get_scenario FOR TESTING RAISING /aws1/cx_rt_generic cx_uuid_error.
    METHODS setup RAISING /aws1/cx_rt_generic /awsex/cx_generic.
    METHODS teardown RAISING /aws1/cx_rt_generic /awsex/cx_generic.

ENDCLASS.

CLASS ltc_awsex_cl_s3_scenario IMPLEMENTATION.

  METHOD setup.
    DATA lv_param TYPE btcxpgpar.
    ao_session = /aws1/cl_rt_session_aws=>create( iv_profile_id = cv_pfl ).
    DATA(lv_acct) = ao_session->get_account_id( ).
    av_bucket = |sap-abap-s3-scenario-bucket-{ lv_acct }|.

    ao_s3 = /aws1/cl_s3_factory=>create( ao_session ).
    ao_s3_scenario = NEW /awsex/cl_s3_scenarios( ).


    lv_param = |if=/dev/random of={ cv_file } bs=1M count=1 iflag=fullblock|.
    CALL FUNCTION 'SXPG_COMMAND_EXECUTE'
      EXPORTING
        commandname           = 'DB24DD'
        additional_parameters = lv_param
        operatingsystem       = 'ANYOS'
      EXCEPTIONS
        OTHERS                = 15.
    /aws1/cl_rt_assert_abap=>assert_subrc( iv_exp = 0
                                           iv_msg = |Could not create { cv_file }| ).

  ENDMETHOD.

  METHOD teardown.
    /awsex/cl_utils=>cleanup_bucket( io_s3 = ao_s3
                                       iv_bucket = av_bucket ).

  ENDMETHOD.



  METHOD getting_started_scenario.
    ao_s3_scenario->getting_started_with_s3(
      iv_bucket_name = av_bucket
        iv_key = cv_file
        iv_copy_to_folder = cv_copy_to_folder ).


    LOOP AT ao_s3->listbuckets( )->get_buckets( ) INTO DATA(lo_bucket).
      IF lo_bucket->get_name( ) = av_bucket.
        DATA(lv_found) = abap_true.
      ENDIF.
    ENDLOOP.

    cl_abap_unit_assert=>assert_equals(
      exp = abap_false
      act = lv_found
      msg = |Bucket { av_bucket } should have been deleted| ).
  ENDMETHOD.

  METHOD presigner_get_scenario.
    " we don't show the customer the bucket creation in this scenario.
    " So we'll create a separate bucket just for this scenario
    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_s3) = /aws1/cl_s3_factory=>create( lo_session ).

    DATA(lv_region) = CONV /aws1/s3_bucketlocationcnstrnt( lo_session->get_region( ) ).
    DATA lo_constraint TYPE REF TO /aws1/cl_s3_createbucketconf.
    IF lv_region = 'us-east-1'.
      CLEAR lo_constraint.
    ELSE.
      lo_constraint = NEW /aws1/cl_s3_createbucketconf( lv_region ).
    ENDIF.

    DATA(lv_uuid) = cl_system_uuid=>if_system_uuid_static~create_uuid_c32( ).
    TRANSLATE lv_uuid TO LOWER CASE.
    DATA(lv_bucket_name) = |sap-abap-s3-scenario-presigner-{ lv_uuid }|.

    lo_s3->createbucket(
        iv_bucket = lv_bucket_name
        io_createbucketconfiguration  = lo_constraint ).


    DATA(lv_url) = ao_s3_scenario->presigner_get(
      iv_bucket_name = lv_bucket_name
      iv_key = cv_file ).
    ASSERT lv_url IS NOT INITIAL.

    " cleanup
    lo_s3->deleteobject( iv_bucket = lv_bucket_name iv_key = cv_file ).
    lo_s3->deletebucket( iv_bucket = lv_bucket_name ).

  ENDMETHOD.

ENDCLASS.
