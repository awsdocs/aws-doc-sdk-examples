" """"""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""
" "  Copyright 2022 Amazon.com, Inc. or its affiliates. All Rights
" "  Reserved.
" "  SPDX-License-Identifier: MIT-0
" """"""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""

CLASS ltc_zcl_aws1_sqs_actions DEFINITION DEFERRED.
CLASS zcl_aws1_sqs_actions DEFINITION LOCAL FRIENDS ltc_zcl_aws1_sqs_actions.

CLASS ltc_zcl_aws1_sqs_actions DEFINITION FOR TESTING  DURATION MEDIUM RISK LEVEL HARMLESS.

  PRIVATE SECTION.
    CONSTANTS: cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA ao_sqs TYPE REF TO /aws1/if_sqs.
    DATA ao_session TYPE REF TO /aws1/cl_rt_session_base.
    DATA ao_sqs_actions TYPE REF TO zcl_aws1_sqs_actions.

    METHODS: create_queue FOR TESTING RAISING /aws1/cx_rt_generic,
      send_message FOR TESTING RAISING /aws1/cx_rt_generic,
      receive_message FOR TESTING RAISING /aws1/cx_rt_generic,
      list_queues FOR TESTING RAISING /aws1/cx_rt_generic,
      get_queue_url FOR TESTING RAISING /aws1/cx_rt_generic,
      delete_queue FOR TESTING RAISING /aws1/cx_rt_generic,
      long_polling_on_msg_receipt FOR TESTING RAISING /aws1/cx_rt_generic,
      long_polling_on_create_queue FOR TESTING RAISING /aws1/cx_rt_generic.

    METHODS: setup RAISING /aws1/cx_rt_generic ycx_aws1_mit_generic.

    METHODS: assert_queue_exists
      IMPORTING
                iv_queue_url TYPE /aws1/sqsstring
                iv_msg       TYPE string
      RAISING   /aws1/cx_rt_generic.


ENDCLASS.
CLASS ltc_zcl_aws1_sqs_actions IMPLEMENTATION.

  METHOD setup.
    ao_session = /aws1/cl_rt_session_aws=>create( iv_profile_id = cv_pfl ).
    ao_sqs = /aws1/cl_sqs_factory=>create( ao_session ).
    ao_sqs_actions = NEW zcl_aws1_sqs_actions( ).
  ENDMETHOD.
  METHOD create_queue.
    CONSTANTS: cv_queue_name TYPE /aws1/sqsstring VALUE 'code-example-create-queue'.
    DATA(lo_result) = ao_sqs_actions->create_queue( iv_queue_name = cv_queue_name ).
    assert_queue_exists(
          iv_queue_url = lo_result->get_queueurl( )
          iv_msg = |Queue { cv_queue_name } was not created|
        ).
    ao_sqs->deletequeue( iv_queueurl = lo_result->get_queueurl( ) ).
  ENDMETHOD.
  METHOD send_message.
    CONSTANTS: cv_queue_name TYPE /aws1/sqsstring VALUE 'code-example-send-message'.
    CONSTANTS: cv_message TYPE /aws1/sqsstring VALUE 'Sample text message to test send message action'.
    DATA(lo_create_result) = ao_sqs->createqueue( iv_queuename = cv_queue_name ).
    DATA(lo_send_result) = ao_sqs_actions->send_message(
            iv_queue_url = lo_create_result->get_queueurl( )
            iv_message = cv_message
        ).
    cl_abap_unit_assert=>assert_not_initial(
        act = lo_send_result->get_messageid( )
        msg = |Message sending failed|
  ).
    DATA(lo_receive_result) = ao_sqs->receivemessage( iv_queueurl = lo_create_result->get_queueurl( ) ).
    DATA lv_found TYPE abap_bool VALUE abap_false.
    LOOP AT  lo_receive_result->get_messages( ) INTO DATA(lo_message).
      IF  lo_message->get_messageid( ) = lo_send_result->get_messageid( ) AND lo_message->get_body( ) = cv_message.
        lv_found = abap_true.
      ENDIF.
    ENDLOOP.

    cl_abap_unit_assert=>assert_true(
        act = lv_found
        msg = |Did not receive message { cv_message }|
  ).
    ao_sqs->deletequeue( iv_queueurl = lo_create_result->get_queueurl( ) ).

  ENDMETHOD.
  METHOD receive_message.
    CONSTANTS: cv_queue_name TYPE /aws1/sqsstring VALUE 'code-example-receive-message'.
    CONSTANTS: cv_message TYPE /aws1/sqsstring VALUE 'Sample text message to test receive message action'.
    DATA(lo_create_result) = ao_sqs->createqueue( iv_queuename = cv_queue_name ).
    DATA(lo_send_result) = ao_sqs->sendmessage(
            iv_queueurl = lo_create_result->get_queueurl( )
            iv_messagebody = cv_message
        ).
    DATA(lo_receive_result) = ao_sqs_actions->receive_message( iv_queue_url = lo_create_result->get_queueurl( ) ).
    DATA lv_found TYPE abap_bool VALUE abap_false.
    LOOP AT  lo_receive_result->get_messages( ) INTO DATA(lo_message).
      IF  lo_message->get_messageid( ) = lo_send_result->get_messageid( ) AND lo_message->get_body( ) = cv_message.
        lv_found = abap_true.
      ENDIF.
    ENDLOOP.

    cl_abap_unit_assert=>assert_true(
        act = lv_found
        msg = |Did not receive message { cv_message }|
  ).
    ao_sqs->deletequeue( iv_queueurl = lo_create_result->get_queueurl( ) ).

  ENDMETHOD.
  METHOD long_polling_on_msg_receipt.
    CONSTANTS: cv_queue_name TYPE /aws1/sqsstring VALUE 'code-example-long-polling-on-msg-receipt'.
    CONSTANTS: cv_message TYPE /aws1/sqsstring VALUE 'Sample text message to test long polling on message receipt'.
    CONSTANTS: cv_wait_time TYPE /aws1/sqsinteger VALUE 10.
    DATA(lo_create_result) = ao_sqs->createqueue( iv_queuename = cv_queue_name ).
    DATA(lo_send_result) = ao_sqs->sendmessage(
            iv_queueurl = lo_create_result->get_queueurl( )
            iv_messagebody = cv_message
        ).
    DATA(lo_polling_result) = ao_sqs_actions->long_polling_on_msg_receipt(
                              iv_queue_url = lo_create_result->get_queueurl( )
                              iv_wait_time = cv_wait_time
                          ).
    DATA lv_found TYPE abap_bool VALUE abap_false.
    LOOP AT lo_polling_result->get_messages( ) INTO DATA(lo_message).
      IF  lo_message->get_messageid( ) = lo_send_result->get_messageid( ) AND lo_message->get_body( ) = cv_message.
        lv_found = abap_true.
      ENDIF.
    ENDLOOP.

    cl_abap_unit_assert=>assert_true(
        act = lv_found
        msg = |Did not receive message { cv_message }|
  ).
    ao_sqs->deletequeue( iv_queueurl = lo_create_result->get_queueurl( ) ).

  ENDMETHOD.
  METHOD long_polling_on_create_queue.
    CONSTANTS: cv_queue_name TYPE /aws1/sqsstring VALUE 'code-example-long-polling-on-create-queue'.
    CONSTANTS: cv_wait_time TYPE /aws1/sqsstring VALUE '10'.
    DATA(lo_create_result) = ao_sqs_actions->long_polling_on_create_queue(
                      iv_queue_name = cv_queue_name
                      iv_wait_time  = cv_wait_time
                  ).
    assert_queue_exists(
          iv_queue_url = lo_create_result->get_queueurl( )
          iv_msg = |Queue { cv_queue_name } was not created|
        ).

    DATA lt_attributes TYPE /aws1/cl_sqsattrnamelist_w=>tt_attributenamelist.
    APPEND NEW /aws1/cl_sqsattrnamelist_w( iv_value = 'ReceiveMessageWaitTimeSeconds' ) TO lt_attributes.
    DATA(lo_get_result) = ao_sqs->getqueueattributes(
                          iv_queueurl = lo_create_result->get_queueurl( )
                          it_attributenames = lt_attributes
                      ).
    LOOP AT lo_get_result->get_attributes( ) INTO DATA(lo_attribute).
      IF lo_attribute-key = 'ReceiveMessageWaitTimeSeconds'.
        cl_abap_unit_assert=>assert_equals(
             act = cv_wait_time
             exp = lo_attribute-value->get_value( )
             msg = |ReceiveMessageWaitTimeSeconds attribute for queue { cv_queue_name } did not match the expected value|
         ).
      ENDIF.
    ENDLOOP.
    ao_sqs->deletequeue( iv_queueurl = lo_create_result->get_queueurl( ) ).
  ENDMETHOD.
  METHOD get_queue_url.
    CONSTANTS: cv_queue_name TYPE /aws1/sqsstring VALUE 'code-example-get-queue-url'.
    DATA(lo_create_result) = ao_sqs->createqueue( iv_queuename = cv_queue_name ).
    DATA(lo_get_result) = ao_sqs_actions->get_queue_url( iv_queue_name = cv_queue_name ).
    cl_abap_unit_assert=>assert_equals(
        act = lo_create_result->get_queueurl( )
        exp = lo_get_result->get_queueurl( )
        msg = |Queue URL { lo_get_result->get_queueurl( ) } did not match expected value { lo_create_result->get_queueurl( ) }|
    ).
    ao_sqs->deletequeue( iv_queueurl = lo_create_result->get_queueurl( ) ).
  ENDMETHOD.
  METHOD list_queues.
    CONSTANTS: cv_queue_name TYPE /aws1/sqsstring VALUE 'code-example-list-queues'.
    DATA(lo_create_result) = ao_sqs->createqueue( iv_queuename = cv_queue_name ).

    DATA lv_found TYPE abap_bool VALUE abap_false.
    DATA lo_result TYPE REF TO /aws1/cl_sqslistqueuesresult.
    WHILE lv_found = abap_false AND sy-index <= 6.
      WAIT UP TO 10 SECONDS.                                  " just to make sure queue is ready for use after creation
      lo_result = ao_sqs_actions->list_queues( ).
      LOOP AT lo_result->get_queueurls( ) INTO DATA(lo_url).
        IF lo_url->get_value( ) = lo_create_result->get_queueurl( ).
          lv_found = abap_true.
        ENDIF.
      ENDLOOP.
    ENDWHILE.

    cl_abap_unit_assert=>assert_true(
      act = lv_found
      msg = |Queue { cv_queue_name } should have been included in queue list|
    ).
    ao_sqs->deletequeue( iv_queueurl = lo_create_result->get_queueurl( ) ).
  ENDMETHOD.
  METHOD delete_queue.
    CONSTANTS: cv_queue_name TYPE /aws1/sqsstring VALUE 'code-example-delete-queue'.
    DATA(lo_create_result) = ao_sqs->createqueue( iv_queuename = cv_queue_name ).
    ao_sqs_actions->delete_queue( iv_queue_url = lo_create_result->get_queueurl( ) ).

    DATA lv_found TYPE abap_bool VALUE abap_true.
    DATA lo_list_result TYPE REF TO /aws1/cl_sqslistqueuesresult.
    WHILE lv_found = abap_true AND sy-index <= 6.
      WAIT UP TO 10 SECONDS.                                    " Queue deletion can take up to 60 seconss
      lv_found = abap_false.
      lo_list_result = ao_sqs->listqueues( ).
      LOOP AT lo_list_result->get_queueurls( ) INTO DATA(lo_url).
        IF lo_url->get_value( ) = lo_create_result->get_queueurl( ).
          lv_found = abap_true.
        ENDIF.
      ENDLOOP.
    ENDWHILE.

    cl_abap_unit_assert=>assert_false(
          act = lv_found
          msg = |Queue { cv_queue_name } should have been deleted|
        ).
  ENDMETHOD.
  METHOD assert_queue_exists.
    DATA lv_found TYPE abap_bool VALUE abap_false.
    DATA lo_result TYPE REF TO /aws1/cl_sqslistqueuesresult.
    WHILE lv_found = abap_false AND sy-index <= 6.
      WAIT UP TO 10 SECONDS.                                  " just to make sure queue is ready for use after creation
      lo_result = ao_sqs->listqueues( ).
      LOOP AT lo_result->get_queueurls( ) INTO DATA(lo_url).
        IF lo_url->get_value( ) = iv_queue_url.
          lv_found = abap_true.
        ENDIF.
      ENDLOOP.
    ENDWHILE.

    cl_abap_unit_assert=>assert_true(
          act = lv_found
          msg = iv_msg
        ).

  ENDMETHOD.
ENDCLASS.
