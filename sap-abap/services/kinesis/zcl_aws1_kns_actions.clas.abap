" """"""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""
" "  Copyright 2022 Amazon.com, Inc. or its affiliates. All Rights
" "  Reserved.
" "  SPDX-License-Identifier: MIT-0
" """"""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""

class ZCL_AWS1_KNS_ACTIONS definition
  public
  final
  create public .

public section.

  methods CREATE_STREAM
    importing
      !IV_STREAM_NAME type /AWS1/KNSSTREAMNAME
      !IV_SHARD_COUNT type /AWS1/KNSPOSITIVEINTEGEROBJECT .
  methods DELETE_STREAM
    importing
      !IV_STREAM_NAME type /AWS1/KNSSTREAMNAME .
  methods DESCRIBE_STREAM
    importing
      !IV_STREAM_NAME type /AWS1/KNSSTREAMNAME
    exporting
      !OO_RESULT type ref to /AWS1/CL_KNSDESCRSTREAMOUTPUT .
  methods GET_RECORDS
    importing
      !IV_SHARD_ITERATOR type /AWS1/KNSSHARDITERATOR
    exporting
      !OO_RESULT type ref to /AWS1/CL_KNSGETRECORDSOUTPUT .
  methods LIST_STREAMS
    importing
      !IV_LIMIT type /AWS1/KNSLISTSTREAMSINPUTLIMIT
    exporting
      !OO_RESULT type ref to /AWS1/CL_KNSLISTSTREAMSOUTPUT .
  methods PUT_RECORD
    importing
      !IV_STREAM_NAME type /AWS1/KNSSTREAMNAME
      !IV_DATA type /AWS1/KNSDATA
      !IV_PARTITION_KEY type /AWS1/KNSPARTITIONKEY
    exporting
      !OO_RESULT type ref to /AWS1/CL_KNSPUTRECORDOUTPUT .
  methods REGISTER_STREAM_CONSUMER
    importing
      !IV_STREAM_ARN type /AWS1/KNSSTREAMARN
      !IV_CONSUMER_NAME type /AWS1/KNSCONSUMERNAME
    exporting
      !OO_RESULT type ref to /AWS1/CL_KNSREGSTREAMCONSOUT .
protected section.
private section.
ENDCLASS.



CLASS ZCL_AWS1_KNS_ACTIONS IMPLEMENTATION.


  METHOD create_stream.

    CONSTANTS: cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_kns) = /aws1/cl_kns_factory=>create( lo_session ).

    "snippet-start:[kns.abapv1.create_stream]
    TRY.
        lo_kns->createstream(
            iv_streamname = iv_stream_name
            iv_shardcount = iv_shard_count
        ).
        MESSAGE 'Stream created.' TYPE 'I'.
      CATCH /aws1/cx_knsinvalidargumentex.
        MESSAGE 'The specified argument was not valid.' TYPE 'E'.
      CATCH /aws1/cx_knslimitexceededex .
        MESSAGE 'The request processing has failed because of a limit exceed exception.' TYPE 'E'.
      CATCH /aws1/cx_knsresourceinuseex .
        MESSAGE 'The request processing has failed because the resource is in use.' TYPE 'E'.
    ENDTRY.
    "snippet-end:[kns.abapv1.create_stream]

  ENDMETHOD.


  METHOD delete_stream.

    CONSTANTS: cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_kns) = /aws1/cl_kns_factory=>create( lo_session ).

    "snippet-start:[kns.abapv1.delete_stream]
    TRY.
        lo_kns->deletestream(
            iv_streamname = iv_stream_name
        ).
        MESSAGE 'Stream deleted.' TYPE 'I'.
      CATCH /aws1/cx_knslimitexceededex .
        MESSAGE 'The request processing has failed because of a limit exceed exception.' TYPE 'E'.
      CATCH /aws1/cx_knsresourceinuseex .
        MESSAGE 'The request processing has failed because the resource is in use.' TYPE 'E'.
    ENDTRY.
    "snippet-end:[kns.abapv1.delete_stream]

  ENDMETHOD.


  METHOD describe_stream.

    CONSTANTS: cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_kns) = /aws1/cl_kns_factory=>create( lo_session ).

    "snippet-start:[kns.abapv1.describe_stream]
    TRY.
        oo_result = lo_kns->describestream(
            iv_streamname = iv_stream_name
        ).
        DATA(lt_stream_description) = oo_result->get_streamdescription( ).
        MESSAGE 'Streams retrieved.' TYPE 'I'.
      CATCH /aws1/cx_knslimitexceededex .
        MESSAGE 'The request processing has failed because of a limit exceed exception.' TYPE 'E'.
      CATCH /aws1/cx_knsresourcenotfoundex .
        MESSAGE 'Resource being accessed is not found.' TYPE 'E'.
    ENDTRY.
    "snippet-end:[kns.abapv1.describe_stream]



  ENDMETHOD.


  METHOD get_records.

    CONSTANTS: cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_kns) = /aws1/cl_kns_factory=>create( lo_session ).

    "snippet-start:[kns.abapv1.get_records]
    TRY.
        oo_result = lo_kns->getrecords(             " oo_result is returned for testing purposes. "
            iv_sharditerator = iv_shard_iterator
        ).
        DATA(lt_records) = oo_result->get_records( ).
        MESSAGE 'Record retrieved.' TYPE 'I'.
      CATCH /aws1/cx_knsexpirediteratorex .
        MESSAGE 'Iterator expired.' TYPE 'E'.
      CATCH /aws1/cx_knsinvalidargumentex .
        MESSAGE 'The specified argument was not valid.' TYPE 'E'.
      CATCH /aws1/cx_knskmsaccessdeniedex .
        MESSAGE 'You do not have permission to perform this AWS KMS action.' TYPE 'E'.
      CATCH /aws1/cx_knskmsdisabledex .
        MESSAGE 'KMS key used is disabled.' TYPE 'E'.
      CATCH /aws1/cx_knskmsinvalidstateex .
        MESSAGE 'KMS key used is in an invalid state. ' TYPE 'E'.
      CATCH /aws1/cx_knskmsnotfoundex .
        MESSAGE 'KMS key used is not found.' TYPE 'E'.
      CATCH /aws1/cx_knskmsoptinrequired .
        MESSAGE 'KMS key option is required.' TYPE 'E'.
      CATCH /aws1/cx_knskmsthrottlingex .
        MESSAGE 'The rate of requests to AWS KMS is exceeding the request quotas.' TYPE 'E'.
      CATCH /aws1/cx_knsprovthruputexcdex .
        MESSAGE 'The request rate for the stream is too high, or the requested data is too large for the available throughput.' TYPE 'E'.
      CATCH /aws1/cx_knsresourcenotfoundex .
        MESSAGE 'Resource being accessed is not found.' TYPE 'E'.
    ENDTRY.
    "snippet-end:[kns.abapv1.get_records]

  ENDMETHOD.


  METHOD list_streams.

    CONSTANTS: cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_kns) = /aws1/cl_kns_factory=>create( lo_session ).

    "snippet-start:[kns.abapv1.list_streams]
    TRY.
        oo_result = lo_kns->liststreams(        " oo_result is returned for testing purposes. "
            "Set Limit to specify that a maximum of streams should be returned."
            iv_limit = iv_limit
        ).
        DATA(lt_streams) = oo_result->get_streamnames( ).
        MESSAGE 'Streams listed.' TYPE 'I'.
      CATCH /aws1/cx_knslimitexceededex .
        MESSAGE 'The request processing has failed because of a limit exceed exception.' TYPE 'E'.
    ENDTRY.
    "snippet-end:[kns.abapv1.list_streams]

  ENDMETHOD.


  METHOD put_record.

    CONSTANTS: cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_kns) = /aws1/cl_kns_factory=>create( lo_session ).

    "snippet-start:[kns.abapv1.put_record]
    TRY.
        oo_result = lo_kns->putrecord(            " oo_result is returned for testing purposes. "
            iv_streamname = iv_stream_name
            iv_data       = iv_data
            iv_partitionkey = iv_partition_key
        ).
        MESSAGE 'Record created.' TYPE 'I'.
      CATCH /aws1/cx_knsinvalidargumentex .
        MESSAGE 'The specified argument was not valid.' TYPE 'E'.
      CATCH /aws1/cx_knskmsaccessdeniedex .
        MESSAGE 'You do not have permission to perform this AWS KMS action.' TYPE 'E'.
      CATCH /aws1/cx_knskmsdisabledex .
        MESSAGE 'KMS key used is disabled.' TYPE 'E'.
      CATCH /aws1/cx_knskmsinvalidstateex .
        MESSAGE 'KMS key used is in an invalid state. ' TYPE 'E'.
      CATCH /aws1/cx_knskmsnotfoundex .
        MESSAGE 'KMS key used is not found.' TYPE 'E'.
      CATCH /aws1/cx_knskmsoptinrequired .
        MESSAGE 'KMS key option is required.' TYPE 'E'.
      CATCH /aws1/cx_knskmsthrottlingex .
        MESSAGE 'The rate of requests to AWS KMS is exceeding the request quotas.' TYPE 'E'.
      CATCH /aws1/cx_knsprovthruputexcdex .
        MESSAGE 'The request rate for the stream is too high, or the requested data is too large for the available throughput.' TYPE 'E'.
      CATCH /aws1/cx_knsresourcenotfoundex .
        MESSAGE 'Resource being accessed is not found.' TYPE 'E'.
    ENDTRY.
    "snippet-end:[kns.abapv1.put_record]
  ENDMETHOD.


  METHOD register_stream_consumer.

    CONSTANTS: cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_kns) = /aws1/cl_kns_factory=>create( lo_session ).

    "snippet-start:[kns.abapv1.register_stream_consumer]
    TRY.
        oo_result = lo_kns->registerstreamconsumer(       " oo_result is returned for testing purposes. "
            iv_streamarn = iv_stream_arn
            iv_consumername = iv_consumer_name
        ).
        MESSAGE 'Stream consumer registered.' TYPE 'I'.
      CATCH /aws1/cx_knsinvalidargumentex .
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
