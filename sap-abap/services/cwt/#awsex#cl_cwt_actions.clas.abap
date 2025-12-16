" Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
" SPDX-License-Identifier: Apache-2.0
CLASS /awsex/cl_cwt_actions DEFINITION
  PUBLIC
  FINAL
  CREATE PUBLIC .

  PUBLIC SECTION.

    METHODS delete_alarms
      IMPORTING
        !it_alarm_names TYPE /aws1/cl_cwtalarmnames_w=>tt_alarmnames
      RAISING
        /aws1/cx_rt_generic .
    METHODS describe_alarms
      IMPORTING
        !it_alarm_names TYPE /aws1/cl_cwtalarmnames_w=>tt_alarmnames
      EXPORTING
        !oo_result      TYPE REF TO /aws1/cl_cwtdescralarmsoutput
      RAISING
        /aws1/cx_rt_generic .
    METHODS disable_alarm_actions
      IMPORTING
        !it_alarm_names TYPE /aws1/cl_cwtalarmnames_w=>tt_alarmnames
      RAISING
        /aws1/cx_rt_generic .
    METHODS enable_alarm_actions
      IMPORTING
        !it_alarm_names TYPE /aws1/cl_cwtalarmnames_w=>tt_alarmnames
      RAISING
        /aws1/cx_rt_generic .
    METHODS list_metrics
      IMPORTING
        !iv_namespace TYPE /aws1/cwtnamespace
      EXPORTING
        !oo_result    TYPE REF TO /aws1/cl_cwtlistmetricsoutput
      RAISING
        /aws1/cx_rt_generic .
    METHODS put_metric_alarm
      IMPORTING
        !iv_alarm_name          TYPE /aws1/cwtalarmname
        !iv_metric_name         TYPE /aws1/cwtmetricname
        !iv_namespace           TYPE /aws1/cwtnamespace
        !iv_comparison_operator TYPE /aws1/cwtcomparisonoperator
        !iv_statistic           TYPE /aws1/cwtstatistic
        !iv_threshold           TYPE /aws1/rt_double_as_string
        !iv_alarm_description   TYPE /aws1/cwtalarmdescription
        !iv_actions_enabled     TYPE /aws1/cwtactionsenabled
        !iv_evaluation_periods  TYPE /aws1/cwtevaluationperiods
        !it_dimensions          TYPE /aws1/cl_cwtdimension=>tt_dimensions
        !iv_unit                TYPE /aws1/cwtstandardunit
        !iv_period              TYPE /aws1/cwtperiod
      RAISING
        /aws1/cx_rt_generic .
    METHODS put_metric_data
      IMPORTING
        !iv_namespace   TYPE /aws1/cwtnamespace
        !iv_metric_name TYPE /aws1/cwtmetricname
        !iv_value       TYPE /aws1/rt_double_as_string
        !iv_unit        TYPE /aws1/cwtstandardunit
      RAISING
        /aws1/cx_rt_generic .
    METHODS put_metric_data_set
      IMPORTING
        !iv_namespace   TYPE /aws1/cwtnamespace
        !iv_metric_name TYPE /aws1/cwtmetricname
        !iv_timestamp   TYPE /aws1/cwttimestamp
        !iv_unit        TYPE /aws1/cwtstandardunit
        !it_values      TYPE /aws1/cl_cwtvalues_w=>tt_values
        !it_counts      TYPE /aws1/cl_cwtcounts_w=>tt_counts
      RAISING
        /aws1/cx_rt_generic .
    METHODS get_metric_statistics
      IMPORTING
        !iv_namespace   TYPE /aws1/cwtnamespace
        !iv_metric_name TYPE /aws1/cwtmetricname
        !iv_start_time  TYPE /aws1/cwttimestamp
        !iv_end_time    TYPE /aws1/cwttimestamp
        !iv_period      TYPE /aws1/cwtperiod
        !it_statistics  TYPE /aws1/cl_cwtstatistics_w=>tt_statistics
      EXPORTING
        !oo_result      TYPE REF TO /aws1/cl_cwtgetmettatsoutput
      RAISING
        /aws1/cx_rt_generic .
    METHODS get_metric_alarms
      IMPORTING
        !iv_namespace   TYPE /aws1/cwtnamespace
        !iv_metric_name TYPE /aws1/cwtmetricname
      EXPORTING
        !oo_result      TYPE REF TO /aws1/cl_cwtdscalrmsformetri01
      RAISING
        /aws1/cx_rt_generic .
  PROTECTED SECTION.
  PRIVATE SECTION.
ENDCLASS.



CLASS /AWSEX/CL_CWT_ACTIONS IMPLEMENTATION.


  METHOD delete_alarms.

    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_cwt) = /aws1/cl_cwt_factory=>create( lo_session ).

    "snippet-start:[cwt.abapv1.delete_alarms]
    TRY.
        lo_cwt->deletealarms(
          it_alarmnames = it_alarm_names ).
        MESSAGE 'Alarms deleted.' TYPE 'I'.
      CATCH /aws1/cx_cwtresourcenotfound.
        MESSAGE 'Resource being accessed is not found.' TYPE 'E'.
    ENDTRY.
    "snippet-end:[cwt.abapv1.delete_alarms]

  ENDMETHOD.


  METHOD describe_alarms.

    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_cwt) = /aws1/cl_cwt_factory=>create( lo_session ).

    "snippet-start:[cwt.abapv1.describe_alarms]
    TRY.
        oo_result = lo_cwt->describealarms(                 " oo_result is returned for testing purposes. "
          it_alarmnames = it_alarm_names ).
        MESSAGE 'Alarms retrieved.' TYPE 'I'.
      CATCH /aws1/cx_rt_service_generic INTO DATA(lo_exception).
        DATA(lv_error) = |"{ lo_exception->av_err_code }" - { lo_exception->av_err_msg }|.
        MESSAGE lv_error TYPE 'E'.
    ENDTRY.
    "snippet-end:[cwt.abapv1.describe_alarms]

  ENDMETHOD.


  METHOD disable_alarm_actions.

    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_cwt) = /aws1/cl_cwt_factory=>create( lo_session ).

    "snippet-start:[cwt.abapv1.disable_alarm_actions]

    "Disables actions on the specified alarm. "
    TRY.
        lo_cwt->disablealarmactions(
          it_alarmnames = it_alarm_names ).
        MESSAGE 'Alarm actions disabled.' TYPE 'I'.
      CATCH /aws1/cx_rt_service_generic INTO DATA(lo_exception).
        DATA(lv_error) = |"{ lo_exception->av_err_code }" - { lo_exception->av_err_msg }|.
        MESSAGE lv_error TYPE 'E'.
    ENDTRY.
    "snippet-end:[cwt.abapv1.disable_alarm_actions]

  ENDMETHOD.


  METHOD enable_alarm_actions.

    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_cwt) = /aws1/cl_cwt_factory=>create( lo_session ).

    "snippet-start:[cwt.abapv1.enable_alarm_actions]

    "Enable actions on the specified alarm."
    TRY.
        lo_cwt->enablealarmactions(
          it_alarmnames = it_alarm_names ).
        MESSAGE 'Alarm actions enabled.' TYPE 'I'.
      CATCH /aws1/cx_rt_service_generic INTO DATA(lo_exception).
        DATA(lv_error) = |"{ lo_exception->av_err_code }" - { lo_exception->av_err_msg }|.
        MESSAGE lv_error TYPE 'E'.
    ENDTRY.
    "snippet-end:[cwt.abapv1.enable_alarm_actions]

  ENDMETHOD.


  METHOD list_metrics.

    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_cwt) = /aws1/cl_cwt_factory=>create( lo_session ).

    "snippet-start:[cwt.abapv1.list_metrics]
    "The following list-metrics example displays the metrics for Amazon CloudWatch."
    TRY.
        oo_result = lo_cwt->listmetrics(            " oo_result is returned for testing purposes. "
          iv_namespace = iv_namespace ).
        DATA(lt_metrics) = oo_result->get_metrics( ).
        MESSAGE 'Metrics retrieved.' TYPE 'I'.
      CATCH /aws1/cx_cwtinvparamvalueex.
        MESSAGE 'The specified argument was not valid.' TYPE 'E'.
    ENDTRY.
    "snippet-end:[cwt.abapv1.list_metrics]

  ENDMETHOD.


  METHOD put_metric_alarm.

    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_cwt) = /aws1/cl_cwt_factory=>create( lo_session ).

    "snippet-start:[cwt.abapv1.put_metric_alarm]
    TRY.
        lo_cwt->putmetricalarm(
          iv_alarmname                 = iv_alarm_name
          iv_comparisonoperator        = iv_comparison_operator
          iv_evaluationperiods         = iv_evaluation_periods
          iv_metricname                = iv_metric_name
          iv_namespace                 = iv_namespace
          iv_statistic                 = iv_statistic
          iv_threshold                 = iv_threshold
          iv_actionsenabled            = iv_actions_enabled
          iv_alarmdescription          = iv_alarm_description
          iv_unit                      = iv_unit
          iv_period                    = iv_period
          it_dimensions                = it_dimensions ).
        MESSAGE 'Alarm created.' TYPE 'I'.
      CATCH /aws1/cx_cwtlimitexceededfault.
        MESSAGE 'The request processing has exceeded the limit' TYPE 'E'.
    ENDTRY.
    "snippet-end:[cwt.abapv1.put_metric_alarm]

  ENDMETHOD.


  METHOD put_metric_data.

    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_cwt) = /aws1/cl_cwt_factory=>create( lo_session ).

    "snippet-start:[cwt.abapv1.put_metric_data]

    DATA lt_metricdata TYPE /aws1/cl_cwtmetricdatum=>tt_metricdata.

    "Create metric data object.
    DATA(lo_metricdatum) = NEW /aws1/cl_cwtmetricdatum(
      iv_metricname = iv_metric_name
      iv_value      = iv_value
      iv_unit       = iv_unit ).

    INSERT lo_metricdatum INTO TABLE lt_metricdata.

    TRY.
        lo_cwt->putmetricdata(
          iv_namespace   = iv_namespace
          it_metricdata  = lt_metricdata ).
        MESSAGE 'Metric data added.' TYPE 'I'.
      CATCH /aws1/cx_cwtinvparamvalueex.
        MESSAGE 'The specified argument was not valid.' TYPE 'E'.
    ENDTRY.
    "snippet-end:[cwt.abapv1.put_metric_data]

  ENDMETHOD.


  METHOD put_metric_data_set.

    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_cwt) = /aws1/cl_cwt_factory=>create( lo_session ).

    "snippet-start:[cwt.abapv1.put_metric_data_set]

    DATA lt_metricdata TYPE /aws1/cl_cwtmetricdatum=>tt_metricdata.

    "Create metric data object with values and counts.
    DATA(lo_metricdatum) = NEW /aws1/cl_cwtmetricdatum(
      iv_metricname = iv_metric_name
      iv_timestamp  = iv_timestamp
      iv_unit       = iv_unit
      it_values     = it_values
      it_counts     = it_counts ).

    INSERT lo_metricdatum INTO TABLE lt_metricdata.

    TRY.
        lo_cwt->putmetricdata(
          iv_namespace   = iv_namespace
          it_metricdata  = lt_metricdata ).
        MESSAGE 'Metric data set added.' TYPE 'I'.
      CATCH /aws1/cx_cwtinvparamvalueex.
        MESSAGE 'The specified argument was not valid.' TYPE 'E'.
    ENDTRY.
    "snippet-end:[cwt.abapv1.put_metric_data_set]

  ENDMETHOD.


  METHOD get_metric_statistics.

    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_cwt) = /aws1/cl_cwt_factory=>create( lo_session ).

    "snippet-start:[cwt.abapv1.get_metric_statistics]

    TRY.
        oo_result = lo_cwt->getmetricstatistics(              " oo_result is returned for testing purposes. "
          iv_namespace   = iv_namespace
          iv_metricname  = iv_metric_name
          iv_starttime   = iv_start_time
          iv_endtime     = iv_end_time
          iv_period      = iv_period
          it_statistics  = it_statistics ).
        MESSAGE 'Metric statistics retrieved.' TYPE 'I'.
      CATCH /aws1/cx_cwtinvparamvalueex.
        MESSAGE 'The specified argument was not valid.' TYPE 'E'.
    ENDTRY.
    "snippet-end:[cwt.abapv1.get_metric_statistics]

  ENDMETHOD.


  METHOD get_metric_alarms.

    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_cwt) = /aws1/cl_cwt_factory=>create( lo_session ).

    "snippet-start:[cwt.abapv1.get_metric_alarms]

    TRY.
        oo_result = lo_cwt->describealarmsformetric(          " oo_result is returned for testing purposes. "
          iv_namespace   = iv_namespace
          iv_metricname  = iv_metric_name ).
        MESSAGE 'Alarms for metric retrieved.' TYPE 'I'.
      CATCH /aws1/cx_rt_service_generic INTO DATA(lo_exception).
        DATA(lv_error) = |"{ lo_exception->av_err_code }" - { lo_exception->av_err_msg }|.
        MESSAGE lv_error TYPE 'E'.
    ENDTRY.
    "snippet-end:[cwt.abapv1.get_metric_alarms]

  ENDMETHOD.
ENDCLASS.
