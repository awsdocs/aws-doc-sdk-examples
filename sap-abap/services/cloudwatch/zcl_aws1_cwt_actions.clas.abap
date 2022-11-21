" """"""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""
" "  Copyright 2022 Amazon.com, Inc. or its affiliates. All Rights
" "  Reserved.
" "  SPDX-License-Identifier: MIT-0
" """"""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""

class ZCL_AWS1_CWT_ACTIONS definition
  public
  final
  create public .

public section.

  methods DELETE_ALARMS
    importing
      !IT_ALARM_NAMES type /AWS1/CL_CWTALARMNAMES_W=>TT_ALARMNAMES .
  methods DESCRIBE_ALARMS
    importing
      !IT_ALARM_NAMES type /AWS1/CL_CWTALARMNAMES_W=>TT_ALARMNAMES
    exporting
      !OO_RESULT type ref to /AWS1/CL_CWTDESCRALARMSOUTPUT .
  methods DISABLE_ALARM_ACTIONS
    importing
      !IT_ALARM_NAMES type /AWS1/CL_CWTALARMNAMES_W=>TT_ALARMNAMES .
  methods ENABLE_ALARM_ACTIONS
    importing
      !IT_ALARM_NAMES type /AWS1/CL_CWTALARMNAMES_W=>TT_ALARMNAMES .
  methods LIST_METRICS
    importing
      !IV_NAMESPACE type /AWS1/CWTNAMESPACE
    exporting
      !OO_RESULT type ref to /AWS1/CL_CWTLISTMETRICSOUTPUT .
  methods PUT_METRIC_ALARM
    importing
      !IV_ALARM_NAME type /AWS1/CWTALARMNAME
      !IV_METRIC_NAME type /AWS1/CWTMETRICNAME
      !IV_NAMESPACE type /AWS1/CWTNAMESPACE
      !IV_COMPARISON_OPERATOR type /AWS1/CWTCOMPARISONOPERATOR
      !IV_STATISTIC type /AWS1/CWTSTATISTIC
      !IV_THRESHOLD type /AWS1/RT_DOUBLE_AS_STRING
      !IV_ALARM_DESCRIPTION type /AWS1/CWTALARMDESCRIPTION
      !IV_ACTIONS_ENABLED type /AWS1/CWTACTIONSENABLED
      !IV_EVALUATION_PERIODS type /AWS1/CWTEVALUATIONPERIODS
      !IT_DIMENSIONS type /AWS1/CL_CWTDIMENSION=>TT_DIMENSIONS
      !IV_UNIT type /AWS1/CWTSTANDARDUNIT
      !IV_PERIOD type /AWS1/CWTPERIOD .
protected section.
private section.
ENDCLASS.



CLASS ZCL_AWS1_CWT_ACTIONS IMPLEMENTATION.


  METHOD delete_alarms.

    CONSTANTS: cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_cwt) = /aws1/cl_cwt_factory=>create( lo_session ).

    "snippet-start:[cwt.abapv1.delete_alarms]
    TRY.
        lo_cwt->deletealarms(
          it_alarmnames = it_alarm_names
        ).
        MESSAGE 'Alarms deleted' TYPE 'I'.
      CATCH /aws1/cx_cwtresourcenotfound .
        MESSAGE 'Resource being access is not found.' TYPE 'E'.
    ENDTRY.
    "snippet-end:[cwt.abapv1.delete_alarms]

  ENDMETHOD.


  METHOD describe_alarms.

    CONSTANTS: cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_cwt) = /aws1/cl_cwt_factory=>create( lo_session ).

    "snippet-start:[cwt.abapv1.describe_alarms]
    TRY.
        oo_result = lo_cwt->describealarms(                 " oo_result is returned for testing purpose "
          it_alarmnames = it_alarm_names
        ).
        MESSAGE 'Alarms retrieved' TYPE 'I'.
      CATCH /aws1/cx_rt_service_generic INTO DATA(lo_exception).
        DATA(lv_error) = |"{ lo_exception->av_err_code }" - { lo_exception->av_err_msg }|.
        MESSAGE lv_error TYPE 'E'.
    ENDTRY.
    "snippet-end:[cwt.abapv1.describe_alarms]

  ENDMETHOD.


  METHOD disable_alarm_actions.

    CONSTANTS: cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_cwt) = /aws1/cl_cwt_factory=>create( lo_session ).

    "snippet-start:[cwt.abapv1.disable_alarm_actions]

    "Disables actions on the specified alarm. "
    TRY.
        lo_cwt->disablealarmactions(
          it_alarmnames = it_alarm_names
        ).
        MESSAGE 'Alarm actions disabled' TYPE 'I'.
      CATCH /aws1/cx_rt_service_generic INTO DATA(lo_exception).
        DATA(lv_error) = |"{ lo_exception->av_err_code }" - { lo_exception->av_err_msg }|.
        MESSAGE lv_error TYPE 'E'.
    ENDTRY.
    "snippet-end:[cwt.abapv1.disable_alarm_actions]

  ENDMETHOD.


  METHOD enable_alarm_actions.

    CONSTANTS: cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_cwt) = /aws1/cl_cwt_factory=>create( lo_session ).

    "snippet-start:[cwt.abapv1.enable_alarm_actions]

    "Enable actions on the specified alarm."
    TRY.
        lo_cwt->enablealarmactions(
          it_alarmnames = it_alarm_names
        ).
        MESSAGE 'Alarm actions enabled' TYPE 'I'.
      CATCH /aws1/cx_rt_service_generic INTO DATA(lo_exception).
        DATA(lv_error) = |"{ lo_exception->av_err_code }" - { lo_exception->av_err_msg }|.
        MESSAGE lv_error TYPE 'E'.
    ENDTRY.
    "snippet-end:[cwt.abapv1.enable_alarm_actions]

  ENDMETHOD.


  METHOD list_metrics.

    CONSTANTS: cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_cwt) = /aws1/cl_cwt_factory=>create( lo_session ).

    "snippet-start:[cwt.abapv1.list_metrics]
    "The following list-metrics example displays the metrics for Amazon Cloudwatch."
    TRY.
        oo_result = lo_cwt->listmetrics(            " oo_result is returned for testing purpose "
          iv_namespace = iv_namespace
        ).
        DATA(lt_metrics) = oo_result->get_metrics( ).
        MESSAGE 'Metrics retrieved' TYPE 'I'.
      CATCH /aws1/cx_cwtinvparamvalueex .
        MESSAGE 'The specified argument was not valid.' TYPE 'E'.
    ENDTRY.
    "snippet-end:[cwt.abapv1.list_metrics]

  ENDMETHOD.


  METHOD put_metric_alarm.

    CONSTANTS: cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

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
          it_dimensions                = it_dimensions
        ).
        MESSAGE 'Alarm created' TYPE 'I'.
      CATCH /aws1/cx_cwtlimitexceededfault.
        MESSAGE 'The request processing has exceeded the limit' TYPE 'E'.
    ENDTRY.
    "snippet-end:[cwt.abapv1.put_metric_alarm]

  ENDMETHOD.
ENDCLASS.
