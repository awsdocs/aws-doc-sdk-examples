" """"""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""
" "  Copyright 2022 Amazon.com, Inc. or its affiliates. All Rights
" "  Reserved.
" "  SPDX-License-Identifier: MIT-0
" """"""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""

class ZCL_AWS1_SQS_ACTIONS definition
  public
  final
  create public .

public section.
protected section.
private section.

  methods CREATE_QUEUE
    importing
      !IV_QUEUE_NAME type /AWS1/SQSSTRING
    returning
      value(OO_RESULT) type ref to /AWS1/CL_SQSCREATEQUEUERESULT .
  methods DELETE_QUEUE
    importing
      !IV_QUEUE_URL type /AWS1/SQSSTRING .
  methods SEND_MESSAGE
    importing
      !IV_QUEUE_URL type /AWS1/SQSSTRING
      !IV_MESSAGE type /AWS1/SQSSTRING
    returning
      value(OO_RESULT) type ref to /AWS1/CL_SQSSENDMESSAGERESULT .
  methods RECEIVE_MESSAGE
    importing
      !IV_QUEUE_URL type /AWS1/SQSSTRING
    returning
      value(OO_RESULT) type ref to /AWS1/CL_SQSRECEIVEMSGRESULT .
  methods GET_QUEUE_URL
    importing
      !IV_QUEUE_NAME type /AWS1/SQSSTRING
    returning
      value(OO_RESULT) type ref to /AWS1/CL_SQSGETQUEUEURLRESULT .
  methods LIST_QUEUES
    returning
      value(OO_RESULT) type ref to /AWS1/CL_SQSLISTQUEUESRESULT .
  methods LONG_POLLING_ON_MSG_RECEIPT
    importing
      !IV_QUEUE_URL type /AWS1/SQSSTRING
      !IV_WAIT_TIME type /AWS1/SQSINTEGER
    returning
      value(OO_RESULT) type ref to /AWS1/CL_SQSRECEIVEMSGRESULT .
  methods LONG_POLLING_ON_CREATE_QUEUE
    importing
      !IV_QUEUE_NAME type /AWS1/SQSSTRING
      !IV_WAIT_TIME type /AWS1/SQSSTRING
    returning
      value(OO_RESULT) type ref to /AWS1/CL_SQSCREATEQUEUERESULT .
ENDCLASS.



CLASS ZCL_AWS1_SQS_ACTIONS IMPLEMENTATION.


  METHOD create_queue.
    CONSTANTS: cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_sqs) = /aws1/cl_sqs_factory=>create( lo_session ).

    " snippet-start:[sqs.abapv1.create_queue]
    TRY.
        oo_result = lo_sqs->createqueue( iv_queuename = iv_queue_name ).        " oo_result is returned for testing purposes. "
        MESSAGE 'SQS queue created.' TYPE 'I'.
      CATCH /aws1/cx_sqsqueuedeldrecently.
        MESSAGE 'After deleting a queue, wait 60 seconds before creating another queue with the same name.' TYPE 'E'.
      CATCH /aws1/cx_sqsqueuenameexists.
        MESSAGE 'A queue with this name already exists.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[sqs.abapv1.create_queue]
  ENDMETHOD.


  METHOD delete_queue.
    CONSTANTS: cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_sqs) = /aws1/cl_sqs_factory=>create( lo_session ).

    " snippet-start:[sqs.abapv1.delete_queue]
    TRY.
        lo_sqs->deletequeue( iv_queueurl = iv_queue_url ).
        MESSAGE 'SQS queue deleted' TYPE 'I'.
    ENDTRY.
    " snippet-end:[sqs.abapv1.delete_queue]
  ENDMETHOD.


  METHOD get_queue_url.
    CONSTANTS: cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_sqs) = /aws1/cl_sqs_factory=>create( lo_session ).

    " snippet-start:[sqs.abapv1.get_queue_url]
    TRY.
        oo_result = lo_sqs->getqueueurl( iv_queuename = iv_queue_name ).        " oo_result is returned for testing purposes. "
        MESSAGE 'Queue URL retrieved.' TYPE 'I'.
      CATCH /aws1/cx_sqsqueuedoesnotexist.
        MESSAGE 'The requested queue does not exist.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[sqs.abapv1.get_queue_url]
  ENDMETHOD.


  METHOD list_queues.
    CONSTANTS: cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_sqs) = /aws1/cl_sqs_factory=>create( lo_session ).

    " snippet-start:[sqs.abapv1.list_queues]
    TRY.
        oo_result = lo_sqs->listqueues( ).        " oo_result is returned for testing purposes. "
        MESSAGE 'Retrieved list of queues.' TYPE 'I'.
    ENDTRY.
    " snippet-end:[sqs.abapv1.list_queues]
  ENDMETHOD.


  METHOD long_polling_on_create_queue.
    CONSTANTS: cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_sqs) = /aws1/cl_sqs_factory=>create( lo_session ).

    " snippet-start:[sqs.abapv1.long_polling_on_create_queue]
    TRY.
        DATA lt_attributes TYPE /aws1/cl_sqsqueueattrmap_w=>tt_queueattributemap.
        DATA ls_attribute TYPE /aws1/cl_sqsqueueattrmap_w=>ts_queueattributemap_maprow.
        ls_attribute-key = 'ReceiveMessageWaitTimeSeconds'.               " Time in seconds for long polling, such as how long the call waits for a message to arrive in the queue before returning. "
        ls_attribute-value = NEW /aws1/cl_sqsqueueattrmap_w( iv_value = iv_wait_time ).
        INSERT ls_attribute INTO TABLE lt_attributes.
        oo_result = lo_sqs->createqueue(                  " oo_result is returned for testing purposes. "
                iv_queuename = iv_queue_name
                it_attributes = lt_attributes
            ).
        MESSAGE 'SQS queue created.' TYPE 'I'.
      CATCH /aws1/cx_sqsqueuedeldrecently.
        MESSAGE 'After deleting a queue, wait 60 seconds before creating another queue with the same name.' TYPE 'E'.
      CATCH /aws1/cx_sqsqueuenameexists.
        MESSAGE 'A queue with this name already exists.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[sqs.abapv1.long_polling_on_create_queue]
  ENDMETHOD.


  METHOD long_polling_on_msg_receipt.
    CONSTANTS: cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_sqs) = /aws1/cl_sqs_factory=>create( lo_session ).

    " snippet-start:[sqs.abapv1.long_polling_on_msg_receipt]
    TRY.
        oo_result = lo_sqs->receivemessage(           " oo_result is returned for testing purposes. "
                iv_queueurl = iv_queue_url
                iv_waittimeseconds = iv_wait_time     " Time in seconds for long polling, such as how long the call waits for a message to arrive in the queue before returning. "
            ).
        DATA(lt_messages) = oo_result->get_messages( ).
        MESSAGE 'Message received from SQS queue.' TYPE 'I'.
      CATCH /aws1/cx_sqsoverlimit.
        MESSAGE 'Maximum number of in-flight messages reached.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[sqs.abapv1.long_polling_on_msg_receipt]
  ENDMETHOD.


  METHOD receive_message.
    CONSTANTS: cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_sqs) = /aws1/cl_sqs_factory=>create( lo_session ).

    " snippet-start:[sqs.abapv1.receive_message]
    TRY.
        oo_result = lo_sqs->receivemessage( iv_queueurl = iv_queue_url ).    " oo_result is returned for testing purposes. "
        DATA(lt_messages) = oo_result->get_messages( ).
        MESSAGE 'Message received from SQS queue.' TYPE 'I'.
      CATCH /aws1/cx_sqsoverlimit.
        MESSAGE 'Maximum number of in-flight messages reached.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[sqs.abapv1.receive_message]
  ENDMETHOD.


  METHOD send_message.
    CONSTANTS: cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_sqs) = /aws1/cl_sqs_factory=>create( lo_session ).

    " snippet-start:[sqs.abapv1.send_message]
    TRY.
        oo_result = lo_sqs->sendmessage(              " oo_result is returned for testing purposes. "
           iv_queueurl = iv_queue_url
           iv_messagebody = iv_message
        ).
        MESSAGE 'Message sent to SQS queue.' TYPE 'I'.
      CATCH /aws1/cx_sqsinvalidmsgconts.
        MESSAGE 'Message contains non-valid characters.' TYPE 'E'.
      CATCH /aws1/cx_sqsunsupportedop.
        MESSAGE 'Operation not supported.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[sqs.abapv1.send_message]
  ENDMETHOD.
ENDCLASS.
