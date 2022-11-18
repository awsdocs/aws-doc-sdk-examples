" """"""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""
" "  Copyright 2022 Amazon.com, Inc. or its affiliates. All Rights
" "  Reserved.
" "  SPDX-License-Identifier: MIT-0
" """"""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""

CLASS ltc_zcl_aws1_cwt_scenario DEFINITION FOR TESTING DURATION LONG RISK LEVEL HARMLESS.

  PRIVATE SECTION.
    CONSTANTS: cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA ao_cwt TYPE REF TO /aws1/if_cwt.
    DATA ao_s3 TYPE REF TO /aws1/if_s3.
    DATA ao_session TYPE REF TO /aws1/cl_rt_session_base.
    DATA ao_cwt_scenario TYPE REF TO zcl_aws1_cwt_scenario.

    METHODS: getting_started_with_cwt FOR TESTING.
    METHODS: setup RAISING /aws1/cx_rt_generic ycx_aws1_mit_generic.
ENDCLASS.       "ltc_Zcl_Aws1_Cwt_Scenario


CLASS ltc_zcl_aws1_cwt_scenario IMPLEMENTATION.

  METHOD setup.
    ao_session = /aws1/cl_rt_session_aws=>create( iv_profile_id = cv_pfl ).
    ao_cwt = /aws1/cl_cwt_factory=>create( ao_session ).
    ao_s3 = /aws1/cl_s3_factory=>create( ao_session ).
    ao_cwt_scenario = NEW zcl_aws1_cwt_scenario( ).
  ENDMETHOD.

  METHOD getting_started_with_cwt.

    DATA lv_alarm_name  TYPE  /aws1/cwtalarmname.
    DATA lt_alarmnames TYPE /aws1/cl_cwtalarmnames_w=>tt_alarmnames.
    DATA lo_alarmname TYPE REF TO /aws1/cl_cwtalarmnames_w.
    DATA lt_dimensions TYPE /aws1/cl_cwtdimension=>tt_dimensions.
    DATA lo_dimensions TYPE REF TO /aws1/cl_cwtdimension.
    DATA lo_alarm_list_result TYPE REF TO /aws1/cl_cwtdescralarmsoutput.
    DATA lv_bucket_name TYPE /aws1/s3_bucketname.
    DATA lv_uuid_16 TYPE sysuuid_x16.
    DATA lo_result TYPE REF TO /aws1/cl_cwtdescralarmsoutput.
    DATA lv_found TYPE abap_bool VALUE abap_false.

    CONSTANTS cv_metric_name  TYPE  /aws1/cwtmetricname VALUE 'NumberOfObjects'.
    CONSTANTS cv_namespace  TYPE  /aws1/cwtnamespace VALUE 'AWS/S3'.
    CONSTANTS cv_comparison_operator  TYPE  /aws1/cwtcomparisonoperator VALUE 'GreaterThanThreshold'.
    CONSTANTS cv_statistic  TYPE  /aws1/cwtstatistic VALUE 'Average'.
    CONSTANTS cv_threshold  TYPE  /aws1/rt_double_as_string VALUE 10.
    CONSTANTS cv_alarm_description  TYPE  /aws1/cwtalarmdescription VALUE 'Alarm when number of objects exceeds 10'.
    CONSTANTS cv_actions_enabled  TYPE  /aws1/cwtactionsenabled VALUE ' '.
    CONSTANTS cv_evaluation_periods TYPE  /aws1/cwtevaluationperiods VALUE 1.
    CONSTANTS cv_unit TYPE /aws1/cwtstandardunit VALUE 'Percent'.
    CONSTANTS cv_period TYPE /aws1/cwtperiod VALUE 86400.
    CONSTANTS cv_bucket_name TYPE /aws1/s3_bucketname VALUE 'code-example-cwt-'.

    "Create an S3 bucket
    lv_uuid_16 = cl_system_uuid=>create_uuid_x16_static( ).
    lv_bucket_name = cv_bucket_name && lv_uuid_16.
    TRANSLATE lv_bucket_name TO LOWER CASE.
    ao_s3->createbucket( iv_bucket = lv_bucket_name ).

    "Define alarm name
    lv_alarm_name = 'code-example-cwt-s3-alarm-' && lv_uuid_16.
    TRANSLATE lv_alarm_name TO LOWER CASE.

    "Create S3 dimensions
    CREATE OBJECT lo_dimensions
      EXPORTING
        iv_name  = 'StorageType'
        iv_value = 'AllStorageTypes'.
    INSERT lo_dimensions INTO TABLE lt_dimensions.

    CREATE OBJECT lo_dimensions
      EXPORTING
        iv_name  = 'BucketName'
        iv_value = lv_bucket_name.
    INSERT lo_dimensions INTO TABLE lt_dimensions.

    ao_cwt_scenario->getting_started_with_cwt(
      EXPORTING
        iv_alarm_name          = lv_alarm_name
        iv_metric_name         = cv_metric_name
        iv_namespace           = cv_namespace
        iv_comparison_operator = cv_comparison_operator
        iv_statistic           = cv_statistic
        iv_threshold           = cv_threshold
        iv_alarm_description   = cv_alarm_description
        iv_actions_enabled     = cv_actions_enabled
        iv_evaluation_periods  = cv_evaluation_periods
        it_dimensions          = lt_dimensions
        iv_unit                = cv_unit
        iv_period              = cv_period
      IMPORTING
        oo_result              = lo_result
        ).

    "Validation
    lv_found = abap_false.

    LOOP AT lo_result->get_metricalarms( ) INTO DATA(lo_alarms).
      IF lo_alarms->get_actionsenabled( ) = ' '.
        lv_found = abap_true.
      ENDIF.
    ENDLOOP.

    cl_abap_unit_assert=>assert_true(
      act = lv_found
      msg = |Alarm actions not disabled|
    ).

    CREATE OBJECT lo_alarmname EXPORTING iv_value = lv_alarm_name.
    INSERT lo_alarmname INTO TABLE lt_alarmnames.
    lo_alarm_list_result = ao_cwt->describealarms( it_alarmnames = lt_alarmnames ).
    lv_found = abap_false.
    LOOP AT lo_alarm_list_result->get_metricalarms( ) INTO lo_alarms.
      IF lo_alarms->get_alarmname( ) = lv_alarm_name.
        lv_found = abap_true.
      ENDIF.
    ENDLOOP.

    cl_abap_unit_assert=>assert_false(
      act = lv_found
      msg = |Alarm not deleted|
    ).

    ao_s3->deletebucket(
      iv_bucket = lv_bucket_name
    ).

  ENDMETHOD.
ENDCLASS.
