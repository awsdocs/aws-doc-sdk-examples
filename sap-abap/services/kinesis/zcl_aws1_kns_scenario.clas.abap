" """"""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""
" "  Copyright 2022 Amazon.com, Inc. or its affiliates. All Rights
" "  Reserved.
" "  SPDX-License-Identifier: MIT-0
" """"""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""

class ZCL_AWS1_KNS_SCENARIO definition
  public
  final
  create public .

public section.

  methods GETTING_STARTED_WITH_KNS
    importing
      !IV_STREAM_NAME type /AWS1/KNSSTREAMNAME
      !IV_PARTITION_KEY type /AWS1/KNSPARTITIONKEY
      !IV_DATA type /AWS1/KNSDATA
      !IV_SHARD_COUNT type /AWS1/KNSPOSITIVEINTEGEROBJECT
      !IV_SHARDITERATORTYPE type /AWS1/KNSSHARDITERATORTYPE
    exporting
      !OO_RESULT type ref to /AWS1/CL_KNSGETRECORDSOUTPUT .
protected section.
private section.
ENDCLASS.



CLASS ZCL_AWS1_KNS_SCENARIO IMPLEMENTATION.


  METHOD getting_started_with_kns.

    CONSTANTS: cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_kns) = /aws1/cl_kns_factory=>create( lo_session ).

    "This example scenario contains the following actions:"
    " 1. Create a stream. "
    " 2. Put a record in a stream. "
    " 3. Create a shard iterator. "
    " 4. Read the record. "
    " 5. Delete the stream. "

    "snippet-start:[kns.abapv1.getting_started_with_kns]

    DATA lo_stream_describe_result TYPE REF TO /aws1/cl_knsdescrstreamoutput.
    DATA lo_stream_description TYPE REF TO /aws1/cl_knsstreamdescription.
    DATA lo_sharditerator TYPE REF TO /aws1/cl_knsgetsharditerator01.
    DATA lo_record_result TYPE REF TO /aws1/cl_knsputrecordoutput.

    "Create stream."
    TRY.
        lo_kns->createstream(
            iv_streamname = iv_stream_name
            iv_shardcount = iv_shard_count
        ).
        MESSAGE 'Stream created.' TYPE 'I'.
      CATCH /aws1/cx_knsinvalidargumentex.
        MESSAGE 'The specified argument was not valid.' TYPE 'E'.
      CATCH /aws1/cx_knslimitexceededex .
        MESSAGE 'The request processing has failed because of a limit exceeded exception.' TYPE 'E'.
      CATCH /aws1/cx_knsresourceinuseex .
        MESSAGE 'The request processing has failed because the resource is in use.' TYPE 'E'.
    ENDTRY.

    "Wait for stream to becomes active."
    lo_stream_describe_result = lo_kns->describestream( iv_streamname = iv_stream_name ).
    lo_stream_description = lo_stream_describe_result->get_streamdescription( ).
    WHILE lo_stream_description->get_streamstatus( ) <> 'ACTIVE'.
      IF sy-index = 30.
        EXIT.               "maximum 5 minutes"
      ENDIF.
      WAIT UP TO 10 SECONDS.
      lo_stream_describe_result = lo_kns->describestream( iv_streamname = iv_stream_name ).
      lo_stream_description =  lo_stream_describe_result->get_streamdescription( ).
    ENDWHILE.

    "Create record."
    TRY.
        lo_record_result = lo_kns->putrecord(
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

    "Create a shard iterator in order to read the record."
    TRY.
        lo_sharditerator = lo_kns->getsharditerator(
          iv_shardid = lo_record_result->get_shardid( )
          iv_sharditeratortype = iv_sharditeratortype
          iv_streamname = iv_stream_name
      ).
        MESSAGE 'Shard iterator created.' TYPE 'I'.
      CATCH /aws1/cx_knsinvalidargumentex.
        MESSAGE 'The specified argument was not valid.' TYPE 'E'.
      CATCH /aws1/cx_knsprovthruputexcdex .
        MESSAGE 'The request rate for the stream is too high, or the requested data is too large for the available throughput.' TYPE 'E'.
      CATCH /aws1/cx_sgmresourcenotfound.
        MESSAGE 'Resource being accessed is not found.' TYPE 'E'.
    ENDTRY.

    "Read the record."
    TRY.
        oo_result = lo_kns->getrecords(                    " oo_result is returned for testing purposes. "
            iv_sharditerator   = lo_sharditerator->get_sharditerator( )
        ).
        MESSAGE 'Shard iterator created.' TYPE 'I'.
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

    "Delete stream."
    TRY.
        lo_kns->deletestream(
            iv_streamname = iv_stream_name
        ).
        MESSAGE 'Stream deleted.' TYPE 'I'.
      CATCH /aws1/cx_knslimitexceededex .
        MESSAGE 'The request processing has failed because of a limit exceeded exception.' TYPE 'E'.
      CATCH /aws1/cx_knsresourceinuseex .
        MESSAGE 'The request processing has failed because the resource is in use.' TYPE 'E'.
    ENDTRY.
    "snippet-end:[kns.abapv1.getting_started_with_kns]

  ENDMETHOD.
ENDCLASS.
