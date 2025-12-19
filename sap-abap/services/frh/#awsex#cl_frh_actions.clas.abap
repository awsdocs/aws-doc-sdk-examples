" Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
" SPDX-License-Identifier: Apache-2.0

CLASS /awsex/cl_frh_actions DEFINITION
  PUBLIC
  FINAL
  CREATE PUBLIC .

  PUBLIC SECTION.
    METHODS put_record
      IMPORTING
        !iv_deliv_stream_name TYPE /aws1/frhdeliverystreamname
        !iv_data              TYPE /aws1/frhdata
      RAISING
        /aws1/cx_rt_generic.

    METHODS put_record_batch
      IMPORTING
        !iv_deliv_stream_name TYPE /aws1/frhdeliverystreamname
        !it_records           TYPE /aws1/cl_frhrecord=>tt_putrecordbatchreqentrylist
      RAISING
        /aws1/cx_rt_generic.

    METHODS get_metric_statistics
      IMPORTING
        !iv_deliv_stream_name TYPE /aws1/frhdeliverystreamname
        !iv_metric_name       TYPE /aws1/cwtmetricname
        !iv_start_time        TYPE /aws1/cwttimestamp
        !iv_end_time          TYPE /aws1/cwttimestamp
        !iv_period            TYPE /aws1/cwtperiod
      EXPORTING
        !oo_result            TYPE REF TO /aws1/cl_cwtgetmettatsoutput
      RAISING
        /aws1/cx_rt_generic.

  PROTECTED SECTION.
  PRIVATE SECTION.
ENDCLASS.



CLASS /awsex/cl_frh_actions IMPLEMENTATION.

  METHOD put_record.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_frh) = /aws1/cl_frh_factory=>create( lo_session ).

    " snippet-start:[frh.abapv1.put_record]
    TRY.
        DATA(lo_record) = NEW /aws1/cl_frhrecord( iv_data = iv_data ).

        DATA(lo_result) = lo_frh->putrecord(
          iv_deliverystreamname = iv_deliv_stream_name
          io_record             = lo_record ).

        MESSAGE 'Record sent to Firehose delivery stream.' TYPE 'I'.
      CATCH /aws1/cx_frhresourcenotfoundex.
        MESSAGE 'Delivery stream not found.' TYPE 'E'.
      CATCH /aws1/cx_frhinvalidargumentex.
        MESSAGE 'Invalid argument provided.' TYPE 'E'.
      CATCH /aws1/cx_frhserviceunavailex.
        MESSAGE 'Service temporarily unavailable.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[frh.abapv1.put_record]
  ENDMETHOD.

  METHOD put_record_batch.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_frh) = /aws1/cl_frh_factory=>create( lo_session ).

    " snippet-start:[frh.abapv1.put_record_batch]
    TRY.
        DATA(lo_result) = lo_frh->putrecordbatch(
          iv_deliverystreamname = iv_deliv_stream_name
          it_records            = it_records ).

        DATA(lv_failed_count) = lo_result->get_failedputcount( ).

        IF lv_failed_count > 0.
          MESSAGE |{ lv_failed_count } records failed to send.| TYPE 'I'.
        ELSE.
          MESSAGE 'All records sent successfully to Firehose delivery stream.' TYPE 'I'.
        ENDIF.
      CATCH /aws1/cx_frhresourcenotfoundex.
        MESSAGE 'Delivery stream not found.' TYPE 'E'.
      CATCH /aws1/cx_frhinvalidargumentex.
        MESSAGE 'Invalid argument provided.' TYPE 'E'.
      CATCH /aws1/cx_frhserviceunavailex.
        MESSAGE 'Service temporarily unavailable.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[frh.abapv1.put_record_batch]
  ENDMETHOD.

  METHOD get_metric_statistics.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_cwt) = /aws1/cl_cwt_factory=>create( lo_session ).

    " snippet-start:[frh.abapv1.get_metric_statistics]
    TRY.
        " Create dimension for delivery stream name
        DATA(lo_dimension) = NEW /aws1/cl_cwtdimension(
          iv_name  = 'DeliveryStreamName'
          iv_value = CONV string( iv_deliv_stream_name ) ).

        DATA lt_dimensions TYPE /aws1/cl_cwtdimension=>tt_dimensions.
        APPEND lo_dimension TO lt_dimensions.

        " Create statistics list
        DATA lt_statistics TYPE /aws1/cl_cwtstatistics_w=>tt_statistics.
        APPEND NEW /aws1/cl_cwtstatistics_w( 'Sum' ) TO lt_statistics.

        " Get metric statistics from CloudWatch
        oo_result = lo_cwt->getmetricstatistics(
          iv_namespace   = 'AWS/Firehose'
          iv_metricname  = iv_metric_name
          it_dimensions  = lt_dimensions
          iv_starttime   = iv_start_time
          iv_endtime     = iv_end_time
          iv_period      = iv_period
          it_statistics  = lt_statistics ).

        MESSAGE 'Firehose metric statistics retrieved.' TYPE 'I'.
      CATCH /aws1/cx_cwtinvparamvalueex.
        MESSAGE 'Invalid parameter value.' TYPE 'E'.
      CATCH /aws1/cx_cwtinternalsvcfault.
        MESSAGE 'Internal service error.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[frh.abapv1.get_metric_statistics]
  ENDMETHOD.

ENDCLASS.
