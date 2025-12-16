" Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
" SPDX-License-Identifier: Apache-2.0
CLASS ltc_awsex_cl_cwt_actions DEFINITION FOR TESTING DURATION LONG RISK LEVEL DANGEROUS.

  PRIVATE SECTION.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    CLASS-DATA ao_cwt TYPE REF TO /aws1/if_cwt.
    CLASS-DATA ao_s3 TYPE REF TO /aws1/if_s3.
    CLASS-DATA ao_session TYPE REF TO /aws1/cl_rt_session_base.
    CLASS-DATA ao_cwt_actions TYPE REF TO /awsex/cl_cwt_actions.
    CLASS-DATA av_bucket_name TYPE /aws1/s3_bucketname.
    CLASS-DATA av_alarm_name TYPE /aws1/cwtalarmname.
    DATA lv_found TYPE abap_bool VALUE abap_false.

    CLASS-METHODS class_setup RAISING /aws1/cx_rt_generic cx_uuid_error.
    CLASS-METHODS class_teardown RAISING /aws1/cx_rt_generic.

    METHODS put_metric_alarm FOR TESTING RAISING /aws1/cx_rt_generic cx_uuid_error.
    METHODS delete_alarms FOR TESTING RAISING /aws1/cx_rt_generic cx_uuid_error.
    METHODS describe_alarms FOR TESTING RAISING /aws1/cx_rt_generic cx_uuid_error.
    METHODS enable_alarm_actions FOR TESTING RAISING /aws1/cx_rt_generic cx_uuid_error.
    METHODS disable_alarm_actions FOR TESTING RAISING /aws1/cx_rt_generic cx_uuid_error.
    METHODS list_metrics FOR TESTING  RAISING /aws1/cx_rt_generic cx_uuid_error.
    METHODS put_metric_data FOR TESTING RAISING /aws1/cx_rt_generic cx_uuid_error.
    METHODS put_metric_data_set FOR TESTING RAISING /aws1/cx_rt_generic cx_uuid_error.
    METHODS get_metric_statistics FOR TESTING RAISING /aws1/cx_rt_generic cx_uuid_error.
    METHODS get_metric_alarms FOR TESTING RAISING /aws1/cx_rt_generic cx_uuid_error.

ENDCLASS.       "ltc_awsex_cl_cwt_actions


CLASS ltc_awsex_cl_cwt_actions IMPLEMENTATION.

  METHOD class_setup.
    ao_session = /aws1/cl_rt_session_aws=>create( iv_profile_id = cv_pfl ).
    ao_cwt = /aws1/cl_cwt_factory=>create( ao_session ).
    ao_s3 = /aws1/cl_s3_factory=>create( ao_session ).
    ao_cwt_actions = NEW /awsex/cl_cwt_actions( ).

    " Create an S3 bucket to be used for all tests that need one
    " Tagged with 'convert_test' for manual cleanup if needed
    DATA lv_uuid_16 TYPE sysuuid_x16.
    CONSTANTS cv_bucket_name TYPE /aws1/s3_bucketname VALUE 'code-example-cwt-'.
    
    lv_uuid_16 = cl_system_uuid=>create_uuid_x16_static( ).
    av_bucket_name = cv_bucket_name && lv_uuid_16.
    TRANSLATE av_bucket_name TO LOWER CASE.

    /awsex/cl_utils=>create_bucket(
      iv_bucket = av_bucket_name
      io_s3 = ao_s3
      io_session = ao_session ).

    " Tag the bucket with 'convert_test'
    DATA lt_tags TYPE /aws1/cl_s3_tag=>tt_tagset.
    DATA(lo_tag) = NEW /aws1/cl_s3_tag(
      iv_key = 'convert_test'
      iv_value = 'true' ).
    INSERT lo_tag INTO TABLE lt_tags.

    TRY.
        ao_s3->putbuckettagging(
          iv_bucket = av_bucket_name
          io_tagging = NEW /aws1/cl_s3_tagging( it_tagset = lt_tags ) ).
      CATCH /aws1/cx_rt_generic.
        " Tagging might fail but we continue
    ENDTRY.

    " Create a base alarm for shared tests
    " Tagged resources will be cleaned up
    av_alarm_name = 'code-example-cwt-alarm-' && lv_uuid_16.
    TRANSLATE av_alarm_name TO LOWER CASE.

  ENDMETHOD.

  METHOD class_teardown.
    " Clean up the shared S3 bucket
    " Note: We clean up S3 bucket in teardown since alarms don't depend on it
    TRY.
        /awsex/cl_utils=>cleanup_bucket(
          iv_bucket = av_bucket_name
          io_s3 = ao_s3 ).
      CATCH /aws1/cx_rt_generic.
        " Ignore errors on cleanup
    ENDTRY.
  ENDMETHOD.

  METHOD put_metric_alarm.

    DATA lv_alarm_name  TYPE /aws1/cwtalarmname.
    DATA lt_alarmnames TYPE /aws1/cl_cwtalarmnames_w=>tt_alarmnames.
    DATA lo_alarmname TYPE REF TO /aws1/cl_cwtalarmnames_w.
    DATA lt_dimensions TYPE /aws1/cl_cwtdimension=>tt_dimensions.
    DATA lo_dimensions TYPE REF TO /aws1/cl_cwtdimension.
    DATA lo_alarm_list_result TYPE REF TO /aws1/cl_cwtdescralarmsoutput.
    DATA lv_uuid_16 TYPE sysuuid_x16.

    CONSTANTS cv_metric_name  TYPE /aws1/cwtmetricname VALUE 'NumberOfObjects'.
    CONSTANTS cv_namespace  TYPE /aws1/cwtnamespace VALUE 'AWS/S3'.
    CONSTANTS cv_comparison_operator  TYPE /aws1/cwtcomparisonoperator VALUE 'GreaterThanThreshold'.
    CONSTANTS cv_statistic  TYPE /aws1/cwtstatistic VALUE 'Average'.
    CONSTANTS cv_threshold  TYPE /aws1/rt_double_as_string VALUE 10.
    CONSTANTS cv_alarm_description  TYPE /aws1/cwtalarmdescription VALUE 'Alarm when number of objects exceeds 10'.
    CONSTANTS cv_actions_enabled  TYPE /aws1/cwtactionsenabled VALUE ' '.
    CONSTANTS cv_evaluation_periods TYPE /aws1/cwtevaluationperiods VALUE 1.
    CONSTANTS cv_unit TYPE /aws1/cwtstandardunit VALUE 'Percent'.
    CONSTANTS cv_period TYPE /aws1/cwtperiod VALUE 86400.

    " Use the shared bucket from class_setup
    " Define alarm name.
    lv_uuid_16 = cl_system_uuid=>create_uuid_x16_static( ).
    lv_alarm_name = 'code-example-cwt-test-' && lv_uuid_16.
    TRANSLATE lv_alarm_name TO LOWER CASE.

    "Create Amazon S3 dimensions.
    lo_dimensions = NEW #( iv_name = 'StorageType'
                           iv_value = 'AllStorageTypes' ).
    INSERT lo_dimensions INTO TABLE lt_dimensions.

    lo_dimensions = NEW #( iv_name = 'BucketName'
                           iv_value = av_bucket_name ).
    INSERT lo_dimensions INTO TABLE lt_dimensions.

    ao_cwt_actions->put_metric_alarm(
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
        iv_period              = cv_period ).

    "Describe alarm.
    lo_alarm_list_result = ao_cwt->describealarms( it_alarmnames = lt_alarmnames ).

    lv_found = abap_false.

    LOOP AT lo_alarm_list_result->get_metricalarms( ) INTO DATA(lo_alarms).
      IF lo_alarms->get_alarmname( ) = lv_alarm_name.
        lv_found = abap_true.
      ENDIF.
    ENDLOOP.

    cl_abap_unit_assert=>assert_true(
      act = lv_found
      msg = |Alarm not found| ).

    "Clean up.
    lo_alarmname = NEW #( iv_value = lv_alarm_name ).
    INSERT lo_alarmname INTO TABLE lt_alarmnames.
    ao_cwt->deletealarms( it_alarmnames = lt_alarmnames ).

  ENDMETHOD.

  METHOD delete_alarms.

    DATA lv_alarm_name  TYPE /aws1/cwtalarmname.
    DATA lt_alarmnames TYPE /aws1/cl_cwtalarmnames_w=>tt_alarmnames.
    DATA lo_alarmname TYPE REF TO /aws1/cl_cwtalarmnames_w.
    DATA lt_dimensions TYPE /aws1/cl_cwtdimension=>tt_dimensions.
    DATA lo_dimensions TYPE REF TO /aws1/cl_cwtdimension.
    DATA lo_alarm_list_result TYPE REF TO /aws1/cl_cwtdescralarmsoutput.
    DATA lv_uuid_16 TYPE sysuuid_x16.

    CONSTANTS cv_metric_name  TYPE /aws1/cwtmetricname VALUE 'NumberOfObjects'.
    CONSTANTS cv_namespace  TYPE /aws1/cwtnamespace VALUE 'AWS/S3'.
    CONSTANTS cv_comparison_operator  TYPE /aws1/cwtcomparisonoperator VALUE 'GreaterThanThreshold'.
    CONSTANTS cv_statistic  TYPE /aws1/cwtstatistic VALUE 'Average'.
    CONSTANTS cv_threshold  TYPE /aws1/rt_double_as_string VALUE 10.
    CONSTANTS cv_alarm_description  TYPE /aws1/cwtalarmdescription VALUE 'Alarm when number of objects exceeds 10'.
    CONSTANTS cv_actions_enabled  TYPE /aws1/cwtactionsenabled VALUE ' '.
    CONSTANTS cv_evaluation_periods TYPE /aws1/cwtevaluationperiods VALUE 1.
    CONSTANTS cv_unit TYPE /aws1/cwtstandardunit VALUE 'Percent'.
    CONSTANTS cv_period TYPE /aws1/cwtperiod VALUE 86400.

    "Define alarm name.
    lv_uuid_16 = cl_system_uuid=>create_uuid_x16_static( ).
    lv_alarm_name = 'code-example-cwt-test-' && lv_uuid_16.
    TRANSLATE lv_alarm_name TO LOWER CASE.

    "Create Amazon S3 dimensions.
    lo_dimensions = NEW #( iv_name = 'StorageType'
                           iv_value = 'AllStorageTypes' ).
    INSERT lo_dimensions INTO TABLE lt_dimensions.

    lo_dimensions = NEW #( iv_name = 'BucketName'
                           iv_value = av_bucket_name ).
    INSERT lo_dimensions INTO TABLE lt_dimensions.

    ao_cwt->putmetricalarm(
      iv_alarmname          = lv_alarm_name
        iv_metricname         = cv_metric_name
        iv_namespace           = cv_namespace
        iv_comparisonoperator = cv_comparison_operator
        iv_statistic           = cv_statistic
        iv_threshold           = cv_threshold
        iv_alarmdescription   = cv_alarm_description
        iv_actionsenabled     = cv_actions_enabled
        iv_evaluationperiods  = cv_evaluation_periods
        iv_unit                = cv_unit
        iv_period              = cv_period
        it_dimensions          = lt_dimensions ).

    "Test delete_alarm.
    lo_alarmname = NEW #( iv_value = lv_alarm_name ).
    INSERT lo_alarmname INTO TABLE lt_alarmnames.

    ao_cwt_actions->delete_alarms( lt_alarmnames ).

    "Describe alarm.
    lo_alarm_list_result = ao_cwt->describealarms( it_alarmnames = lt_alarmnames ).

    "Validation.
    lv_found = abap_false.
    LOOP AT lo_alarm_list_result->get_metricalarms( ) INTO DATA(lo_alarms).
      IF lo_alarms->get_alarmname( ) = lv_alarm_name.
        lv_found = abap_true.
      ENDIF.
    ENDLOOP.

    cl_abap_unit_assert=>assert_false(
      act = lv_found
      msg = |Alarm not deleted| ).

  ENDMETHOD.

  METHOD describe_alarms.

    DATA lv_alarm_name  TYPE /aws1/cwtalarmname.
    DATA lt_alarmnames TYPE /aws1/cl_cwtalarmnames_w=>tt_alarmnames.
    DATA lo_alarmname TYPE REF TO /aws1/cl_cwtalarmnames_w.
    DATA lt_dimensions TYPE /aws1/cl_cwtdimension=>tt_dimensions.
    DATA lo_dimensions TYPE REF TO /aws1/cl_cwtdimension.
    DATA lo_alarm_list_result TYPE REF TO /aws1/cl_cwtdescralarmsoutput.
    DATA lv_uuid_16 TYPE sysuuid_x16.

    CONSTANTS cv_metric_name  TYPE /aws1/cwtmetricname VALUE 'NumberOfObjects'.
    CONSTANTS cv_namespace  TYPE /aws1/cwtnamespace VALUE 'AWS/S3'.
    CONSTANTS cv_comparison_operator  TYPE /aws1/cwtcomparisonoperator VALUE 'GreaterThanThreshold'.
    CONSTANTS cv_statistic  TYPE /aws1/cwtstatistic VALUE 'Average'.
    CONSTANTS cv_threshold  TYPE /aws1/rt_double_as_string VALUE 10.
    CONSTANTS cv_alarm_description  TYPE /aws1/cwtalarmdescription VALUE 'Alarm when number of objects exceeds 10'.
    CONSTANTS cv_actions_enabled  TYPE /aws1/cwtactionsenabled VALUE ' '.
    CONSTANTS cv_evaluation_periods TYPE /aws1/cwtevaluationperiods VALUE 1.
    CONSTANTS cv_unit TYPE /aws1/cwtstandardunit VALUE 'Percent'.
    CONSTANTS cv_period TYPE /aws1/cwtperiod VALUE 86400.

    "Define alarm name.
    lv_uuid_16 = cl_system_uuid=>create_uuid_x16_static( ).
    lv_alarm_name = 'code-example-cwt-test-' && lv_uuid_16.
    TRANSLATE lv_alarm_name TO LOWER CASE.

    "Create Amazon S3 dimensions.
    lo_dimensions = NEW #( iv_name = 'StorageType'
                           iv_value = 'AllStorageTypes' ).
    INSERT lo_dimensions INTO TABLE lt_dimensions.

    lo_dimensions = NEW #( iv_name = 'BucketName'
                           iv_value = av_bucket_name ).
    INSERT lo_dimensions INTO TABLE lt_dimensions.

    ao_cwt->putmetricalarm(
      iv_alarmname          = lv_alarm_name
        iv_metricname         = cv_metric_name
        iv_namespace           = cv_namespace
        iv_comparisonoperator = cv_comparison_operator
        iv_statistic           = cv_statistic
        iv_threshold           = cv_threshold
        iv_alarmdescription   = cv_alarm_description
        iv_actionsenabled     = cv_actions_enabled
        iv_evaluationperiods  = cv_evaluation_periods
        iv_unit                = cv_unit
        iv_period              = cv_period
        it_dimensions          = lt_dimensions ).

    "Test describe_alarms.
    lo_alarmname = NEW #( iv_value = lv_alarm_name ).
    INSERT lo_alarmname INTO TABLE lt_alarmnames.

    ao_cwt_actions->describe_alarms(
      EXPORTING it_alarm_names = lt_alarmnames
      IMPORTING oo_result = lo_alarm_list_result ).

    "Validation.
    lv_found = abap_false.

    LOOP AT lo_alarm_list_result->get_metricalarms( ) INTO DATA(lo_alarms).
      IF lo_alarms->get_alarmname( ) = lv_alarm_name.
        lv_found = abap_true.
      ENDIF.
    ENDLOOP.

    cl_abap_unit_assert=>assert_true(
      act = lv_found
      msg = |Alarm not found| ).

    "Clean up.
    ao_cwt->deletealarms( it_alarmnames = lt_alarmnames ).

  ENDMETHOD.

  METHOD enable_alarm_actions.

    DATA lv_alarm_name  TYPE /aws1/cwtalarmname.
    DATA lt_alarmnames TYPE /aws1/cl_cwtalarmnames_w=>tt_alarmnames.
    DATA lo_alarmname TYPE REF TO /aws1/cl_cwtalarmnames_w.
    DATA lt_dimensions TYPE /aws1/cl_cwtdimension=>tt_dimensions.
    DATA lo_dimensions TYPE REF TO /aws1/cl_cwtdimension.
    DATA lo_alarm_list_result TYPE REF TO /aws1/cl_cwtdescralarmsoutput.
    DATA lv_uuid_16 TYPE sysuuid_x16.

    CONSTANTS cv_metric_name  TYPE /aws1/cwtmetricname VALUE 'NumberOfObjects'.
    CONSTANTS cv_namespace  TYPE /aws1/cwtnamespace VALUE 'AWS/S3'.
    CONSTANTS cv_comparison_operator  TYPE /aws1/cwtcomparisonoperator VALUE 'GreaterThanThreshold'.
    CONSTANTS cv_statistic  TYPE /aws1/cwtstatistic VALUE 'Average'.
    CONSTANTS cv_threshold  TYPE /aws1/rt_double_as_string VALUE 10.
    CONSTANTS cv_alarm_description  TYPE /aws1/cwtalarmdescription VALUE 'Alarm when number of objects exceeds 10'.
    CONSTANTS cv_actions_enabled  TYPE /aws1/cwtactionsenabled VALUE ' '.
    CONSTANTS cv_evaluation_periods TYPE /aws1/cwtevaluationperiods VALUE 1.
    CONSTANTS cv_unit TYPE /aws1/cwtstandardunit VALUE 'Percent'.
    CONSTANTS cv_period TYPE /aws1/cwtperiod VALUE 86400.

    "Define alarm name.
    lv_uuid_16 = cl_system_uuid=>create_uuid_x16_static( ).
    lv_alarm_name = 'code-example-cwt-test-' && lv_uuid_16.
    TRANSLATE lv_alarm_name TO LOWER CASE.

    "Create Amazon S3 dimensions.
    lo_dimensions = NEW #( iv_name = 'StorageType'
                           iv_value = 'AllStorageTypes' ).
    INSERT lo_dimensions INTO TABLE lt_dimensions.

    lo_dimensions = NEW #( iv_name = 'BucketName'
                           iv_value = av_bucket_name ).
    INSERT lo_dimensions INTO TABLE lt_dimensions.

    ao_cwt->putmetricalarm(
      iv_alarmname          = lv_alarm_name
        iv_metricname         = cv_metric_name
        iv_namespace           = cv_namespace
        iv_comparisonoperator = cv_comparison_operator
        iv_statistic           = cv_statistic
        iv_threshold           = cv_threshold
        iv_alarmdescription   = cv_alarm_description
        iv_actionsenabled     = cv_actions_enabled
        iv_evaluationperiods  = cv_evaluation_periods
        iv_unit                = cv_unit
        iv_period              = cv_period
        it_dimensions          = lt_dimensions ).

    "Testing enable_alarm_actions.
    lo_alarmname = NEW #( iv_value = lv_alarm_name ).
    INSERT lo_alarmname INTO TABLE lt_alarmnames.

    ao_cwt_actions->enable_alarm_actions( lt_alarmnames ).

    "Validation.
    lo_alarm_list_result = ao_cwt->describealarms(
      it_alarmnames = lt_alarmnames ).

    lv_found = abap_false.

    LOOP AT lo_alarm_list_result->get_metricalarms( ) INTO DATA(lo_alarms).
      IF lo_alarms->get_actionsenabled( ) = 'X'.
        lv_found = abap_true.
      ENDIF.
    ENDLOOP.

    cl_abap_unit_assert=>assert_true(
      act = lv_found
      msg = |Alarm actions not enabled| ).

    "Clean up.
    ao_cwt->deletealarms( it_alarmnames = lt_alarmnames ).

  ENDMETHOD.

  METHOD disable_alarm_actions.

    DATA lv_alarm_name  TYPE /aws1/cwtalarmname.
    DATA lt_alarmnames TYPE /aws1/cl_cwtalarmnames_w=>tt_alarmnames.
    DATA lo_alarmname TYPE REF TO /aws1/cl_cwtalarmnames_w.
    DATA lt_dimensions TYPE /aws1/cl_cwtdimension=>tt_dimensions.
    DATA lo_dimensions TYPE REF TO /aws1/cl_cwtdimension.
    DATA lo_alarm_list_result TYPE REF TO /aws1/cl_cwtdescralarmsoutput.
    DATA lv_uuid_16 TYPE sysuuid_x16.

    CONSTANTS cv_metric_name  TYPE /aws1/cwtmetricname VALUE 'NumberOfObjects'.
    CONSTANTS cv_namespace  TYPE /aws1/cwtnamespace VALUE 'AWS/S3'.
    CONSTANTS cv_comparison_operator  TYPE /aws1/cwtcomparisonoperator VALUE 'GreaterThanThreshold'.
    CONSTANTS cv_statistic  TYPE /aws1/cwtstatistic VALUE 'Average'.
    CONSTANTS cv_threshold  TYPE /aws1/rt_double_as_string VALUE 10.
    CONSTANTS cv_alarm_description  TYPE /aws1/cwtalarmdescription VALUE 'Alarm when number of objects exceeds 10'.
    CONSTANTS cv_actions_enabled  TYPE /aws1/cwtactionsenabled VALUE 'X'.
    CONSTANTS cv_evaluation_periods TYPE /aws1/cwtevaluationperiods VALUE 1.
    CONSTANTS cv_unit TYPE /aws1/cwtstandardunit VALUE 'Percent'.
    CONSTANTS cv_period TYPE /aws1/cwtperiod VALUE 86400.

    "Define alarm name.
    lv_uuid_16 = cl_system_uuid=>create_uuid_x16_static( ).
    lv_alarm_name = 'code-example-cwt-test-' && lv_uuid_16.
    TRANSLATE lv_alarm_name TO LOWER CASE.

    "Create Amazon S3 dimensions.
    lo_dimensions = NEW #( iv_name = 'StorageType'
                           iv_value = 'AllStorageTypes' ).
    INSERT lo_dimensions INTO TABLE lt_dimensions.

    lo_dimensions = NEW #( iv_name = 'BucketName'
                           iv_value = av_bucket_name ).
    INSERT lo_dimensions INTO TABLE lt_dimensions.

    ao_cwt->putmetricalarm(
      iv_alarmname          = lv_alarm_name
        iv_metricname         = cv_metric_name
        iv_namespace           = cv_namespace
        iv_comparisonoperator = cv_comparison_operator
        iv_statistic           = cv_statistic
        iv_threshold           = cv_threshold
        iv_alarmdescription   = cv_alarm_description
        iv_actionsenabled     = cv_actions_enabled
        iv_evaluationperiods  = cv_evaluation_periods
        iv_unit                = cv_unit
        iv_period              = cv_period
        it_dimensions          = lt_dimensions ).

    "Testing disable_alarm_actions.
    lo_alarmname = NEW #( iv_value = lv_alarm_name ).
    INSERT lo_alarmname INTO TABLE lt_alarmnames.

    ao_cwt_actions->disable_alarm_actions( lt_alarmnames ).

    "Validation.
    lo_alarm_list_result = ao_cwt->describealarms(
      it_alarmnames = lt_alarmnames ).

    lv_found = abap_false.

    LOOP AT lo_alarm_list_result->get_metricalarms( ) INTO DATA(lo_alarms).
      IF lo_alarms->get_actionsenabled( ) = ' '.
        lv_found = abap_true.
      ENDIF.
    ENDLOOP.

    cl_abap_unit_assert=>assert_true(
      act = lv_found
      msg = |Alarm actions not disabled| ).

    "Clean up.
    ao_cwt->deletealarms( it_alarmnames = lt_alarmnames ).

  ENDMETHOD.

  METHOD list_metrics.

    DATA lv_alarm_name  TYPE /aws1/cwtalarmname.
    DATA lt_alarmnames TYPE /aws1/cl_cwtalarmnames_w=>tt_alarmnames.
    DATA lo_alarmname TYPE REF TO /aws1/cl_cwtalarmnames_w.
    DATA lt_dimensions TYPE /aws1/cl_cwtdimension=>tt_dimensions.
    DATA lo_dimensions TYPE REF TO /aws1/cl_cwtdimension.
    DATA lo_list_metrics_result TYPE REF TO /aws1/cl_cwtlistmetricsoutput.
    DATA lv_uuid_16 TYPE sysuuid_x16.
    DATA lt_metrics TYPE /aws1/cl_cwtmetric=>tt_metrics.

    CONSTANTS cv_metric_name  TYPE /aws1/cwtmetricname VALUE 'NumberOfObjects'.
    CONSTANTS cv_namespace  TYPE /aws1/cwtnamespace VALUE 'AWS/S3'.
    CONSTANTS cv_comparison_operator  TYPE /aws1/cwtcomparisonoperator VALUE 'GreaterThanThreshold'.
    CONSTANTS cv_statistic  TYPE /aws1/cwtstatistic VALUE 'Average'.
    CONSTANTS cv_threshold  TYPE /aws1/rt_double_as_string VALUE 10.
    CONSTANTS cv_alarm_description  TYPE /aws1/cwtalarmdescription VALUE 'Alarm when number of objects exceeds 10'.
    CONSTANTS cv_actions_enabled  TYPE /aws1/cwtactionsenabled VALUE 'X'.
    CONSTANTS cv_evaluation_periods TYPE /aws1/cwtevaluationperiods VALUE 1.
    CONSTANTS cv_unit TYPE /aws1/cwtstandardunit VALUE 'Percent'.
    CONSTANTS cv_period TYPE /aws1/cwtperiod VALUE 86400.

    "Define alarm name.
    lv_uuid_16 = cl_system_uuid=>create_uuid_x16_static( ).
    lv_alarm_name = 'code-example-cwt-test-' && lv_uuid_16.
    TRANSLATE lv_alarm_name TO LOWER CASE.

    "Create Amazon S3 dimensions.
    lo_dimensions = NEW #( iv_name = 'StorageType'
                           iv_value = 'AllStorageTypes' ).
    INSERT lo_dimensions INTO TABLE lt_dimensions.

    lo_dimensions = NEW #( iv_name = 'BucketName'
                           iv_value = av_bucket_name ).
    INSERT lo_dimensions INTO TABLE lt_dimensions.

    ao_cwt->putmetricalarm(
      iv_alarmname           = lv_alarm_name
        iv_metricname          = cv_metric_name
        iv_namespace           = cv_namespace
        iv_comparisonoperator  = cv_comparison_operator
        iv_statistic           = cv_statistic
        iv_threshold           = cv_threshold
        iv_alarmdescription    = cv_alarm_description
        iv_actionsenabled      = cv_actions_enabled
        iv_evaluationperiods   = cv_evaluation_periods
        iv_unit                = cv_unit
        iv_period              = cv_period
        it_dimensions          = lt_dimensions ).

    "Testing list_metrics.
    ao_cwt_actions->list_metrics( EXPORTING iv_namespace = 'AWS/S3' IMPORTING oo_result = lo_list_metrics_result ).
    lt_metrics = lo_list_metrics_result->get_metrics( ).

    "Validation.
    lv_found = abap_false.

    LOOP AT lt_metrics INTO DATA(lo_metrics).
      IF lo_metrics->get_namespace( ) = 'AWS/S3'.
        lv_found = abap_true.
      ENDIF.
    ENDLOOP.

    cl_abap_unit_assert=>assert_true(
      act = lv_found
      msg = |No metric found| ).

    "Clean up.
    lo_alarmname = NEW #( iv_value = lv_alarm_name ).
    INSERT lo_alarmname INTO TABLE lt_alarmnames.
    ao_cwt->deletealarms( it_alarmnames = lt_alarmnames ).

  ENDMETHOD.


  METHOD put_metric_data.

    CONSTANTS cv_namespace TYPE /aws1/cwtnamespace VALUE 'doc-example-metric'.
    " Example: cv_metric_name TYPE /aws1/cwtmetricname VALUE 'page_views'
    CONSTANTS cv_metric_name TYPE /aws1/cwtmetricname VALUE 'page_views'.
    " Example: cv_unit TYPE /aws1/cwtstandardunit VALUE 'Count'
    CONSTANTS cv_unit TYPE /aws1/cwtstandardunit VALUE 'Count'.

    DATA lv_value TYPE /aws1/rt_double_as_string.
    DATA lv_uuid_16 TYPE sysuuid_x16.
    DATA lv_namespace TYPE /aws1/cwtnamespace.

    "Create unique namespace for test.
    lv_uuid_16 = cl_system_uuid=>create_uuid_x16_static( ).
    lv_namespace = cv_namespace && '-' && lv_uuid_16.
    TRANSLATE lv_namespace TO LOWER CASE.

    "Put metric data.
    lv_value = '10'.
    ao_cwt_actions->put_metric_data(
      iv_namespace   = lv_namespace
      iv_metric_name = cv_metric_name
      iv_value       = lv_value
      iv_unit        = cv_unit ).

    "Wait for metric to propagate.
    DATA lv_start_time TYPE timestamp.
    DATA lv_current_time TYPE timestamp.
    GET TIME STAMP FIELD lv_start_time.
    lv_current_time = lv_start_time.
    WHILE ( lv_current_time - lv_start_time ) < 60.
      WAIT UP TO 1 SECONDS.
      GET TIME STAMP FIELD lv_current_time.
    ENDWHILE.

    "Validation - Check if metric exists.
    DATA(lo_metrics_result) = ao_cwt->listmetrics(
      iv_namespace = lv_namespace
      iv_metricname = cv_metric_name ).

    lv_found = abap_false.
    LOOP AT lo_metrics_result->get_metrics( ) INTO DATA(lo_metric).
      IF lo_metric->get_metricname( ) = cv_metric_name AND
         lo_metric->get_namespace( ) = lv_namespace.
        lv_found = abap_true.
      ENDIF.
    ENDLOOP.

    cl_abap_unit_assert=>assert_true(
      act = lv_found
      msg = |Metric not found| ).

  ENDMETHOD.


  METHOD put_metric_data_set.

    CONSTANTS cv_namespace TYPE /aws1/cwtnamespace VALUE 'doc-example-metric'.
    " Example: cv_metric_name TYPE /aws1/cwtmetricname VALUE 'page_views'
    CONSTANTS cv_metric_name TYPE /aws1/cwtmetricname VALUE 'page_views'.
    " Example: cv_unit TYPE /aws1/cwtstandardunit VALUE 'Count'
    CONSTANTS cv_unit TYPE /aws1/cwtstandardunit VALUE 'Count'.

    DATA lt_values TYPE /aws1/cl_cwtvalues_w=>tt_values.
    DATA lt_counts TYPE /aws1/cl_cwtcounts_w=>tt_counts.
    DATA lv_timestamp TYPE /aws1/cwttimestamp.
    DATA lv_uuid_16 TYPE sysuuid_x16.
    DATA lv_namespace TYPE /aws1/cwtnamespace.
    DATA lv_temp_tstmpl TYPE timestampl.

    "Create unique namespace for test.
    lv_uuid_16 = cl_system_uuid=>create_uuid_x16_static( ).
    lv_namespace = cv_namespace && '-' && lv_uuid_16.
    TRANSLATE lv_namespace TO LOWER CASE.

    "Create timestamp in TIMESTAMPL format.
    GET TIME STAMP FIELD lv_temp_tstmpl.
    lv_timestamp = lv_temp_tstmpl.

    "Create values and counts.
    DATA(lo_value) = NEW /aws1/cl_cwtvalues_w( '10' ).
    INSERT lo_value INTO TABLE lt_values.
    lo_value = NEW /aws1/cl_cwtvalues_w( '20' ).
    INSERT lo_value INTO TABLE lt_values.
    lo_value = NEW /aws1/cl_cwtvalues_w( '30' ).
    INSERT lo_value INTO TABLE lt_values.

    DATA(lo_count) = NEW /aws1/cl_cwtcounts_w( '1' ).
    INSERT lo_count INTO TABLE lt_counts.
    lo_count = NEW /aws1/cl_cwtcounts_w( '2' ).
    INSERT lo_count INTO TABLE lt_counts.
    lo_count = NEW /aws1/cl_cwtcounts_w( '3' ).
    INSERT lo_count INTO TABLE lt_counts.

    "Put metric data set.
    ao_cwt_actions->put_metric_data_set(
      iv_namespace   = lv_namespace
      iv_metric_name = cv_metric_name
      iv_timestamp   = lv_timestamp
      iv_unit        = cv_unit
      it_values      = lt_values
      it_counts      = lt_counts ).

    "Wait for metric to propagate.
    DATA lv_start_time TYPE timestamp.
    DATA lv_current_time TYPE timestamp.
    GET TIME STAMP FIELD lv_start_time.
    lv_current_time = lv_start_time.
    WHILE ( lv_current_time - lv_start_time ) < 60.
      WAIT UP TO 1 SECONDS.
      GET TIME STAMP FIELD lv_current_time.
    ENDWHILE.

    "Validation - Check if metric exists.
    DATA(lo_metrics_result) = ao_cwt->listmetrics(
      iv_namespace = lv_namespace
      iv_metricname = cv_metric_name ).

    lv_found = abap_false.
    LOOP AT lo_metrics_result->get_metrics( ) INTO DATA(lo_metric).
      IF lo_metric->get_metricname( ) = cv_metric_name AND
         lo_metric->get_namespace( ) = lv_namespace.
        lv_found = abap_true.
      ENDIF.
    ENDLOOP.

    cl_abap_unit_assert=>assert_true(
      act = lv_found
      msg = |Metric not found| ).

  ENDMETHOD.


  METHOD get_metric_statistics.

    CONSTANTS cv_namespace TYPE /aws1/cwtnamespace VALUE 'AWS/S3'.
    " Example: cv_metric_name TYPE /aws1/cwtmetricname VALUE 'NumberOfObjects'
    CONSTANTS cv_metric_name TYPE /aws1/cwtmetricname VALUE 'NumberOfObjects'.
    " Example: cv_period TYPE /aws1/cwtperiod VALUE 86400 (24 hours)
    CONSTANTS cv_period TYPE /aws1/cwtperiod VALUE 86400.

    DATA lt_statistics TYPE /aws1/cl_cwtstatistics_w=>tt_statistics.
    DATA lo_statistic TYPE REF TO /aws1/cl_cwtstatistics_w.
    DATA lo_stats_result TYPE REF TO /aws1/cl_cwtgetmettatsoutput.
    DATA lv_start_time TYPE /aws1/cwttimestamp.
    DATA lv_end_time TYPE /aws1/cwttimestamp.
    DATA lv_temp_tstmpl TYPE timestampl.

    "Set time range - last 7 days.
    "Get current timestamp and ensure it's in proper TIMESTAMPL format.
    GET TIME STAMP FIELD lv_temp_tstmpl.
    lv_end_time = lv_temp_tstmpl.
    lv_start_time = lv_temp_tstmpl - ( 7 * 24 * 3600 ).

    "Create statistics list.
    " Example: 'Average', 'Minimum', 'Maximum'
    lo_statistic = NEW /aws1/cl_cwtstatistics_w( 'Average' ).
    INSERT lo_statistic INTO TABLE lt_statistics.
    lo_statistic = NEW /aws1/cl_cwtstatistics_w( 'Minimum' ).
    INSERT lo_statistic INTO TABLE lt_statistics.
    lo_statistic = NEW /aws1/cl_cwtstatistics_w( 'Maximum' ).
    INSERT lo_statistic INTO TABLE lt_statistics.

    "Get metric statistics.
    ao_cwt_actions->get_metric_statistics(
      EXPORTING
        iv_namespace   = cv_namespace
        iv_metric_name = cv_metric_name
        iv_start_time  = lv_start_time
        iv_end_time    = lv_end_time
        iv_period      = cv_period
        it_statistics  = lt_statistics
      IMPORTING
        oo_result      = lo_stats_result ).

    "Validation - Check if result is returned.
    cl_abap_unit_assert=>assert_bound(
      act = lo_stats_result
      msg = |Statistics result not returned| ).

  ENDMETHOD.


  METHOD get_metric_alarms.

    DATA lv_alarm_name  TYPE /aws1/cwtalarmname.
    DATA lt_alarmnames TYPE /aws1/cl_cwtalarmnames_w=>tt_alarmnames.
    DATA lo_alarmname TYPE REF TO /aws1/cl_cwtalarmnames_w.
    DATA lt_dimensions TYPE /aws1/cl_cwtdimension=>tt_dimensions.
    DATA lo_dimensions TYPE REF TO /aws1/cl_cwtdimension.
    DATA lo_alarms_result TYPE REF TO /aws1/cl_cwtdscalrmsformetri01.
    DATA lv_uuid_16 TYPE sysuuid_x16.
    DATA lv_retry_count TYPE i.
    DATA lv_max_retries TYPE i VALUE 5.

    CONSTANTS cv_metric_name  TYPE /aws1/cwtmetricname VALUE 'NumberOfObjects'.
    CONSTANTS cv_namespace  TYPE /aws1/cwtnamespace VALUE 'AWS/S3'.
    CONSTANTS cv_comparison_operator  TYPE /aws1/cwtcomparisonoperator VALUE 'GreaterThanThreshold'.
    CONSTANTS cv_statistic  TYPE /aws1/cwtstatistic VALUE 'Average'.
    CONSTANTS cv_threshold  TYPE /aws1/rt_double_as_string VALUE 10.
    CONSTANTS cv_alarm_description  TYPE /aws1/cwtalarmdescription VALUE 'Alarm when number of objects exceeds 10'.
    CONSTANTS cv_actions_enabled  TYPE /aws1/cwtactionsenabled VALUE ' '.
    CONSTANTS cv_evaluation_periods TYPE /aws1/cwtevaluationperiods VALUE 1.
    CONSTANTS cv_unit TYPE /aws1/cwtstandardunit VALUE 'Percent'.
    CONSTANTS cv_period TYPE /aws1/cwtperiod VALUE 86400.

    "Define alarm name.
    lv_uuid_16 = cl_system_uuid=>create_uuid_x16_static( ).
    lv_alarm_name = 'code-example-cwt-test-' && lv_uuid_16.
    TRANSLATE lv_alarm_name TO LOWER CASE.

    "Create Amazon S3 dimensions.
    lo_dimensions = NEW #( iv_name = 'StorageType'
                           iv_value = 'AllStorageTypes' ).
    INSERT lo_dimensions INTO TABLE lt_dimensions.

    lo_dimensions = NEW #( iv_name = 'BucketName'
                           iv_value = av_bucket_name ).
    INSERT lo_dimensions INTO TABLE lt_dimensions.

    "Create alarm.
    ao_cwt->putmetricalarm(
      iv_alarmname          = lv_alarm_name
        iv_metricname         = cv_metric_name
        iv_namespace           = cv_namespace
        iv_comparisonoperator = cv_comparison_operator
        iv_statistic           = cv_statistic
        iv_threshold           = cv_threshold
        iv_alarmdescription   = cv_alarm_description
        iv_actionsenabled     = cv_actions_enabled
        iv_evaluationperiods  = cv_evaluation_periods
        iv_unit                = cv_unit
        iv_period              = cv_period
        it_dimensions          = lt_dimensions ).

    "Wait for alarm to propagate - retry up to 5 times
    lv_found = abap_false.
    lv_retry_count = 0.
    
    WHILE lv_retry_count < lv_max_retries AND lv_found = abap_false.
      "Wait before checking
      IF lv_retry_count > 0.
        WAIT UP TO 3 SECONDS.
      ENDIF.
      
      "Test get_metric_alarms - pass dimensions, statistic, period, and unit for exact match.
      ao_cwt_actions->get_metric_alarms(
        EXPORTING
          iv_namespace   = cv_namespace
          iv_metric_name = cv_metric_name
          it_dimensions  = lt_dimensions
          iv_statistic   = cv_statistic
          iv_period      = cv_period
          iv_unit        = cv_unit
        IMPORTING
          oo_result      = lo_alarms_result ).

      "Validation.
      LOOP AT lo_alarms_result->get_metricalarms( ) INTO DATA(lo_alarm).
        IF lo_alarm->get_alarmname( ) = lv_alarm_name.
          lv_found = abap_true.
          EXIT.
        ENDIF.
      ENDLOOP.
      
      lv_retry_count = lv_retry_count + 1.
    ENDWHILE.

    cl_abap_unit_assert=>assert_true(
      act = lv_found
      msg = |Alarm not found in metric alarms after { lv_retry_count } retries| ).

    "Clean up.
    lo_alarmname = NEW #( iv_value = lv_alarm_name ).
    INSERT lo_alarmname INTO TABLE lt_alarmnames.
    ao_cwt->deletealarms( it_alarmnames = lt_alarmnames ).

  ENDMETHOD.


ENDCLASS.
