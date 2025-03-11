" Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
" SPDX-License-Identifier: Apache-2.0

CLASS zcl_aws1_kns_actions DEFINITION
  PUBLIC
  FINAL
  CREATE PUBLIC .

  PUBLIC SECTION.

    METHODS create_stream
      IMPORTING
      !iv_stream_name TYPE /aws1/knsstreamname
      !iv_shard_count TYPE /aws1/knspositiveintegerobject .
    METHODS delete_stream
      IMPORTING
      !iv_stream_name TYPE /aws1/knsstreamname .
    METHODS describe_stream
      IMPORTING
      !iv_stream_name TYPE /aws1/knsstreamname
      EXPORTING
      !oo_result TYPE REF TO /aws1/cl_knsdescrstreamoutput .
    METHODS get_records
      IMPORTING
      !iv_shard_iterator TYPE /aws1/knssharditerator
      EXPORTING
      !oo_result TYPE REF TO /aws1/cl_knsgetrecordsoutput .
    METHODS list_streams
      IMPORTING
      !iv_limit TYPE /aws1/knsliststreamsinputlimit
      EXPORTING
      !oo_result TYPE REF TO /aws1/cl_knsliststreamsoutput .
    METHODS put_record
      IMPORTING
      !iv_stream_name TYPE /aws1/knsstreamname
      !iv_data TYPE /aws1/knsdata
      !iv_partition_key TYPE /aws1/knspartitionkey
      EXPORTING
      !oo_result TYPE REF TO /aws1/cl_knsputrecordoutput .
    METHODS register_stream_consumer
      IMPORTING
      !iv_stream_arn TYPE /aws1/knsstreamarn
      !iv_consumer_name TYPE /aws1/knsconsumername
      EXPORTING
      !oo_result TYPE REF TO /aws1/cl_knsregstreamconsout .
  PROTECTED SECTION.
  PRIVATE SECTION.
ENDCLASS.



CLASS ZCL_AWS1_KNS_ACTIONS IMPLEMENTATION.


  METHOD create_stream.

    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_kns) = /aws1/cl_kns_factory=>create( lo_session ).

    "snippet-start:[kns.abapv1.create_stream]
    TRY.
        lo_kns->createstream(
            iv_streamname = iv_stream_name
            iv_shardcount = iv_shard_count ).
        MESSAGE 'Stream created.' TYPE 'I'.
      CATCH /aws1/cx_knsinvalidargumentex.
        MESSAGE 'The specified argument was not valid.' TYPE 'E'.
      CATCH /aws1/cx_knslimitexceededex.
        MESSAGE 'The request processing has failed because of a limit exceed exception.' TYPE 'E'.
      CATCH /aws1/cx_knsresourceinuseex.
        MESSAGE 'The request processing has failed because the resource is in use.' TYPE 'E'.
    ENDTRY.
    "snippet-end:[kns.abapv1.create_stream]

  ENDMETHOD.


  METHOD delete_stream.

    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_kns) = /aws1/cl_kns_factory=>create( lo_session ).

    "snippet-start:[kns.abapv1.delete_stream]
    TRY.
        lo_kns->deletestream(
            iv_streamname = iv_stream_name ).
        MESSAGE 'Stream deleted.' TYPE 'I'.
      CATCH /aws1/cx_knslimitexceededex.
        MESSAGE 'The request processing has failed because of a limit exceed exception.' TYPE 'E'.
      CATCH /aws1/cx_knsresourceinuseex.
        MESSAGE 'The request processing has failed because the resource is in use.' TYPE 'E'.
    ENDTRY.
    "snippet-end:[kns.abapv1.delete_stream]

  ENDMETHOD.


  METHOD describe_stream.

    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_kns) = /aws1/cl_kns_factory=>create( lo_session ).

    "snippet-start:[kns.abapv1.describe_stream]
    TRY.
        oo_result = lo_kns->describestream(
            iv_streamname = iv_stream_name ).
        DATA(lt_stream_description) = oo_result->get_streamdescription( ).
        MESSAGE 'Streams retrieved.' TYPE 'I'.
      CATCH /aws1/cx_knslimitexceededex.
        MESSAGE 'The request processing has failed because of a limit exceed exception.' TYPE 'E'.
      CATCH /aws1/cx_knsresourcenotfoundex.
        MESSAGE 'Resource being accessed is not found.' TYPE 'E'.
    ENDTRY.
    "snippet-end:[kns.abapv1.describe_stream]



  ENDMETHOD.


  METHOD get_records.

    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_kns) = /aws1/cl_kns_factory=>create( lo_session ).

    "snippet-start:[kns.abapv1.get_records]
    TRY.
        oo_result = lo_kns->getrecords(             " oo_result is returned for testing purposes. "
            iv_sharditerator = iv_shard_iterator ).
        DATA(lt_records) = oo_result->get_records( ).
        MESSAGE 'Record retrieved.' TYPE 'I'.
      CATCH /aws1/cx_knsexpirediteratorex.
        MESSAGE 'Iterator expired.' TYPE 'E'.
      CATCH /aws1/cx_knsinvalidargumentex.
        MESSAGE 'The specified argument was not valid.' TYPE 'E'.
      CATCH /aws1/cx_knskmsaccessdeniedex.
        MESSAGE 'You do not have permission to perform this AWS KMS action.' TYPE 'E'.
      CATCH /aws1/cx_knskmsdisabledex.
        MESSAGE 'KMS key used is disabled.' TYPE 'E'.
      CATCH /aws1/cx_knskmsinvalidstateex.
        MESSAGE 'KMS key used is in an invalid state. ' TYPE 'E'.
      CATCH /aws1/cx_knskmsnotfoundex.
        MESSAGE 'KMS key used is not found.' TYPE 'E'.
      CATCH /aws1/cx_knskmsoptinrequired.
        MESSAGE 'KMS key option is required.' TYPE 'E'.
      CATCH /aws1/cx_knskmsthrottlingex.
        MESSAGE 'The rate of requests to AWS KMS is exceeding the request quotas.' TYPE 'E'.
      CATCH /aws1/cx_knsprovthruputexcdex.
        MESSAGE 'The request rate for the stream is too high, or the requested data is too large for the available throughput.' TYPE 'E'.
      CATCH /aws1/cx_knsresourcenotfoundex.
        MESSAGE 'Resource being accessed is not found.' TYPE 'E'.
    ENDTRY.
    "snippet-end:[kns.abapv1.get_records]

  ENDMETHOD.


  METHOD list_streams.

    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_kns) = /aws1/cl_kns_factory=>create( lo_session ).

    "snippet-start:[kns.abapv1.list_streams]
    TRY.
        oo_result = lo_kns->liststreams(        " oo_result is returned for testing purposes. "
            "Set Limit to specify that a maximum of streams should be returned."
            iv_limit = iv_limit ).
        DATA(lt_streams) = oo_result->get_streamnames( ).
        MESSAGE 'Streams listed.' TYPE 'I'.
      CATCH /aws1/cx_knslimitexceededex.
        MESSAGE 'The request processing has failed because of a limit exceed exception.' TYPE 'E'.
    ENDTRY.
    "snippet-end:[kns.abapv1.list_streams]

  ENDMETHOD.


  METHOD put_record.

    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_kns) = /aws1/cl_kns_factory=>create( lo_session ).

    "snippet-start:[kns.abapv1.put_record]
    TRY.
        oo_result = lo_kns->putrecord(            " oo_result is returned for testing purposes. "
            iv_streamname = iv_stream_name
            iv_data       = iv_data
            iv_partitionkey = iv_partition_key ).
        MESSAGE 'Record created.' TYPE 'I'.
      CATCH /aws1/cx_knsinvalidargumentex.
        MESSAGE 'The specified argument was not valid.' TYPE 'E'.
      CATCH /aws1/cx_knskmsaccessdeniedex.
        MESSAGE 'You do not have permission to perform this AWS KMS action.' TYPE 'E'.
      CATCH /aws1/cx_knskmsdisabledex.
        MESSAGE 'KMS key used is disabled.' TYPE 'E'.
      CATCH /aws1/cx_knskmsinvalidstateex.
        MESSAGE 'KMS key used is in an invalid state. ' TYPE 'E'.
      CATCH /aws1/cx_knskmsnotfoundex.
        MESSAGE 'KMS key used is not found.' TYPE 'E'.
      CATCH /aws1/cx_knskmsoptinrequired.
        MESSAGE 'KMS key option is required.' TYPE 'E'.
      CATCH /aws1/cx_knskmsthrottlingex.
        MESSAGE 'The rate of requests to AWS KMS is exceeding the request quotas.' TYPE 'E'.
      CATCH /aws1/cx_knsprovthruputexcdex.
        MESSAGE 'The request rate for the stream is too high, or the requested data is too large for the available throughput.' TYPE 'E'.
      CATCH /aws1/cx_knsresourcenotfoundex.
        MESSAGE 'Resource being accessed is not found.' TYPE 'E'.
    ENDTRY.
    "snippet-end:[kns.abapv1.put_record]
  ENDMETHOD.


  METHOD register_stream_consumer.

    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_kns) = /aws1/cl_kns_factory=>create( lo_session ).

    "snippet-start:[kns.abapv1.register_stream_consumer]
    TRY.
        oo_result = lo_kns->registerstreamconsumer(       " oo_result is returned for testing purposes. "
            iv_streamarn = iv_stream_arn
            iv_consumername = iv_consumer_name ).
        MESSAGE 'Stream consumer registered.' TYPE 'I'.
      CATCH /aws1/cx_knsinvalidargumentex.
        MESSAGE 'The specified argument was not valid.' TYPE 'E'.
      CATCH /aws1/cx_sgmresourcelimitexcd.
        MESSAGE 'You have reached the limit on the number of resources.' TYPE 'E'.
      CATCH /aws1/cx_sgmresourceinuse.
        MESSAGE 'Resource being accessed is in use.' TYPE 'E'.
      CATCH /aws1/cx_sgmresourcenotfound.
        MESSAGE 'Resource being accessed is not found.' TYPE 'E'.
    ENDTRY.
    "snippet-end:[kns.abapv1.register_stream_consumer]
  ENDMETHOD.
ENDCLASS.
