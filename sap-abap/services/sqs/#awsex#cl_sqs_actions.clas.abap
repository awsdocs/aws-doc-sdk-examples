" Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
" SPDX-License-Identifier: Apache-2.0
CLASS /awsex/cl_sqs_actions DEFINITION
  PUBLIC
  FINAL
  CREATE PUBLIC .

  PUBLIC SECTION.
  PROTECTED SECTION.
  PRIVATE SECTION.

    METHODS create_queue
      IMPORTING
                !iv_queue_name   TYPE /aws1/sqsstring
      RETURNING
                VALUE(oo_result) TYPE REF TO /aws1/cl_sqscreatequeueresult
      RAISING   /aws1/cx_rt_generic.
    METHODS delete_queue
      IMPORTING
                !iv_queue_url TYPE /aws1/sqsstring
      RAISING   /aws1/cx_rt_generic.
    METHODS send_message
      IMPORTING
                !iv_queue_url    TYPE /aws1/sqsstring
                !iv_message      TYPE /aws1/sqsstring
      RETURNING
                VALUE(oo_result) TYPE REF TO /aws1/cl_sqssendmessageresult
      RAISING   /aws1/cx_rt_generic.
    METHODS receive_message
      IMPORTING
                !iv_queue_url    TYPE /aws1/sqsstring
      RETURNING
                VALUE(oo_result) TYPE REF TO /aws1/cl_sqsreceivemsgresult
      RAISING   /aws1/cx_rt_generic.
    METHODS get_queue_url
      IMPORTING
                !iv_queue_name   TYPE /aws1/sqsstring
      RETURNING
                VALUE(oo_result) TYPE REF TO /aws1/cl_sqsgetqueueurlresult
      RAISING   /aws1/cx_rt_generic.
    METHODS list_queues
      RETURNING
                VALUE(oo_result) TYPE REF TO /aws1/cl_sqslistqueuesresult
      RAISING   /aws1/cx_rt_generic.
    METHODS long_polling_on_msg_receipt
      IMPORTING
                !iv_queue_url    TYPE /aws1/sqsstring
                !iv_wait_time    TYPE /aws1/sqsinteger
      RETURNING
                VALUE(oo_result) TYPE REF TO /aws1/cl_sqsreceivemsgresult
      RAISING   /aws1/cx_rt_generic.
    METHODS long_polling_on_create_queue
      IMPORTING
                !iv_queue_name   TYPE /aws1/sqsstring
                !iv_wait_time    TYPE /aws1/sqsstring
      RETURNING
                VALUE(oo_result) TYPE REF TO /aws1/cl_sqscreatequeueresult
      RAISING   /aws1/cx_rt_generic.
    METHODS send_message_batch
      IMPORTING
                !iv_queue_url    TYPE /aws1/sqsstring
                !it_messages     TYPE /aws1/cl_sqssendmsgbtcreqentry=>tt_sendmsgbatchreqentrylist
      RETURNING
                VALUE(oo_result) TYPE REF TO /aws1/cl_sqssendmsgbatchresult
      RAISING   /aws1/cx_rt_generic.
    METHODS delete_message
      IMPORTING
                !iv_queue_url      TYPE /aws1/sqsstring
                !iv_receipt_handle TYPE /aws1/sqsstring
      RAISING   /aws1/cx_rt_generic.
    METHODS delete_message_batch
      IMPORTING
                !iv_queue_url    TYPE /aws1/sqsstring
                !it_entries      TYPE /aws1/cl_sqsdelmsgbtcreqentry=>tt_deletemsgbatchreqentrylist
      RETURNING
                VALUE(oo_result) TYPE REF TO /aws1/cl_sqsdeletemsgbatchrslt
      RAISING   /aws1/cx_rt_generic.
ENDCLASS.



CLASS /AWSEX/CL_SQS_ACTIONS IMPLEMENTATION.


  METHOD create_queue.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

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
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

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
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

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
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

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
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

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
                it_attributes = lt_attributes ).
        MESSAGE 'SQS queue created.' TYPE 'I'.
      CATCH /aws1/cx_sqsqueuedeldrecently.
        MESSAGE 'After deleting a queue, wait 60 seconds before creating another queue with the same name.' TYPE 'E'.
      CATCH /aws1/cx_sqsqueuenameexists.
        MESSAGE 'A queue with this name already exists.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[sqs.abapv1.long_polling_on_create_queue]
  ENDMETHOD.


  METHOD long_polling_on_msg_receipt.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_sqs) = /aws1/cl_sqs_factory=>create( lo_session ).

    " snippet-start:[sqs.abapv1.long_polling_on_msg_receipt]
    TRY.
        oo_result = lo_sqs->receivemessage(           " oo_result is returned for testing purposes. "
                iv_queueurl = iv_queue_url
                iv_waittimeseconds = iv_wait_time ).    " Time in seconds for long polling, such as how long the call waits for a message to arrive in the queue before returning. " ).
        DATA(lt_messages) = oo_result->get_messages( ).
        MESSAGE 'Message received from SQS queue.' TYPE 'I'.
      CATCH /aws1/cx_sqsoverlimit.
        MESSAGE 'Maximum number of in-flight messages reached.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[sqs.abapv1.long_polling_on_msg_receipt]
  ENDMETHOD.


  METHOD receive_message.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

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
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_sqs) = /aws1/cl_sqs_factory=>create( lo_session ).

    " snippet-start:[sqs.abapv1.send_message]
    TRY.
        oo_result = lo_sqs->sendmessage(              " oo_result is returned for testing purposes. "
           iv_queueurl = iv_queue_url
           iv_messagebody = iv_message ).
        MESSAGE 'Message sent to SQS queue.' TYPE 'I'.
      CATCH /aws1/cx_sqsinvalidmsgconts.
        MESSAGE 'Message contains non-valid characters.' TYPE 'E'.
      CATCH /aws1/cx_sqsunsupportedop.
        MESSAGE 'Operation not supported.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[sqs.abapv1.send_message]
  ENDMETHOD.


  METHOD send_message_batch.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_sqs) = /aws1/cl_sqs_factory=>create( lo_session ).

    " snippet-start:[sqs.abapv1.send_message_batch]
    TRY.
        oo_result = lo_sqs->sendmessagebatch(         " oo_result is returned for testing purposes. "
           iv_queueurl = iv_queue_url
           it_entries = it_messages ).
        MESSAGE 'Messages sent to SQS queue.' TYPE 'I'.
      CATCH /aws1/cx_sqsbtcentidsnotdist00.
        MESSAGE 'Two or more batch entries in the request have the same ID.' TYPE 'E'.
      CATCH /aws1/cx_sqsbatchreqtoolong.
        MESSAGE 'The length of all the messages put together is more than the limit.' TYPE 'E'.
      CATCH /aws1/cx_sqsemptybatchrequest.
        MESSAGE 'The batch request does not contain any entries.' TYPE 'E'.
      CATCH /aws1/cx_sqsinvbatchentryid.
        MESSAGE 'The ID of a batch entry in a batch request is not valid.' TYPE 'E'.
      CATCH /aws1/cx_sqstoomanyentriesin00.
        MESSAGE 'The batch request contains more entries than allowed.' TYPE 'E'.
      CATCH /aws1/cx_sqsunsupportedop.
        MESSAGE 'Operation not supported.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[sqs.abapv1.send_message_batch]
  ENDMETHOD.


  METHOD delete_message.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_sqs) = /aws1/cl_sqs_factory=>create( lo_session ).

    " snippet-start:[sqs.abapv1.delete_message]
    TRY.
        lo_sqs->deletemessage(
           iv_queueurl = iv_queue_url
           iv_receipthandle = iv_receipt_handle ).
        MESSAGE 'Message deleted from SQS queue.' TYPE 'I'.
      CATCH /aws1/cx_sqsinvalididformat.
        MESSAGE 'The specified receipt handle is not valid.' TYPE 'E'.
      CATCH /aws1/cx_sqsreceipthandleisinv.
        MESSAGE 'The specified receipt handle is not valid for the current version.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[sqs.abapv1.delete_message]
  ENDMETHOD.


  METHOD delete_message_batch.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_sqs) = /aws1/cl_sqs_factory=>create( lo_session ).

    " snippet-start:[sqs.abapv1.delete_message_batch]
    TRY.
        oo_result = lo_sqs->deletemessagebatch(       " oo_result is returned for testing purposes. "
           iv_queueurl = iv_queue_url
           it_entries = it_entries ).
        MESSAGE 'Messages deleted from SQS queue.' TYPE 'I'.
      CATCH /aws1/cx_sqsbtcentidsnotdist00.
        MESSAGE 'Two or more batch entries in the request have the same ID.' TYPE 'E'.
      CATCH /aws1/cx_sqsemptybatchrequest.
        MESSAGE 'The batch request does not contain any entries.' TYPE 'E'.
      CATCH /aws1/cx_sqsinvbatchentryid.
        MESSAGE 'The ID of a batch entry in a batch request is not valid.' TYPE 'E'.
      CATCH /aws1/cx_sqstoomanyentriesin00.
        MESSAGE 'The batch request contains more entries than allowed.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[sqs.abapv1.delete_message_batch]
  ENDMETHOD.
ENDCLASS.
