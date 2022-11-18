" """"""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""
" "  Copyright 2022 Amazon.com, Inc. or its affiliates. All Rights
" "  Reserved.
" "  SPDX-License-Identifier: MIT-0
" """"""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""

class ZCL_AWS1_CWT_SCENARIO definition
  public
  final
  create public .

public section.

  methods GETTING_STARTED_WITH_CWT
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
      !IV_PERIOD type /AWS1/CWTPERIOD
    exporting
      !OO_RESULT type ref to /AWS1/CL_CWTDESCRALARMSOUTPUT .
protected section.
private section.
ENDCLASS.



CLASS ZCL_AWS1_CWT_SCENARIO IMPLEMENTATION.


  METHOD getting_started_with_cwt.


    CONSTANTS: cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_cwt) = /aws1/cl_cwt_factory=>create( lo_session ).

    "This example scenario contains the following actions."
    " 1. Create a Cloudwatch alarm for the S3 bucket "
    " 2. Disable the Cloudwatch alarm actions "
    " 3. Describe the Cloudwatch alarm "
    " 4. Delete alarm "

    "snippet-start:[cwt.abapv1.getting_started_with_cwt]

    DATA lt_alarmnames TYPE /aws1/cl_cwtalarmnames_w=>tt_alarmnames.
    DATA lo_alarmname TYPE REF TO /aws1/cl_cwtalarmnames_w.

    "Create an alarm"
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

    "Create an ABAP internal table for the alarm created"
    CREATE OBJECT lo_alarmname EXPORTING iv_value = iv_alarm_name.
    INSERT lo_alarmname INTO TABLE lt_alarmnames.

    "Disable alarm actions"
    TRY.
        lo_cwt->disablealarmactions(
          it_alarmnames                = lt_alarmnames
        ).
        MESSAGE 'Alarm actions disabled' TYPE 'I'.
      CATCH /aws1/cx_rt_service_generic INTO DATA(lo_disablealarm_exception).
        DATA(lv_disablealarm_error) = |"{ lo_disablealarm_exception->av_err_code }" - { lo_disablealarm_exception->av_err_msg }|.
        MESSAGE lv_disablealarm_error TYPE 'E'.
    ENDTRY.

    "Describe alarm using the same ABAP internal table"
    TRY.
        oo_result = lo_cwt->describealarms(                       " oo_result is returned for testing purpose "
          it_alarmnames                = lt_alarmnames
        ).
        MESSAGE 'Alarms retrieved' TYPE 'I'.
      CATCH /aws1/cx_rt_service_generic INTO DATA(lo_describealarms_exception).
        DATA(lv_describealarms_error) = |"{ lo_describealarms_exception->av_err_code }" - { lo_describealarms_exception->av_err_msg }|.
        MESSAGE lv_describealarms_error TYPE 'E'.
    ENDTRY.

    "Delete alarm"
    TRY.
        lo_cwt->deletealarms(
          it_alarmnames = lt_alarmnames
        ).
        MESSAGE 'Alarms deleted' TYPE 'I'.
      CATCH /aws1/cx_cwtresourcenotfound .
        MESSAGE 'Resource being access is not found.' TYPE 'E'.
    ENDTRY.
    "snippet-end:[cwt.abapv1.getting_started_with_cwt]

  ENDMETHOD.
ENDCLASS.
