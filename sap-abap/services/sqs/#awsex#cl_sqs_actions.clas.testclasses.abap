" Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
" SPDX-License-Identifier: Apache-2.0

CLASS ltc_awsex_cl_sqs_actions DEFINITION DEFERRED.
CLASS /awsex/cl_sqs_actions DEFINITION LOCAL FRIENDS ltc_awsex_cl_sqs_actions.

CLASS ltc_awsex_cl_sqs_actions DEFINITION FOR TESTING DURATION MEDIUM RISK LEVEL DANGEROUS.

  PRIVATE SECTION.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    CLASS-DATA ao_sqs TYPE REF TO /aws1/if_sqs.
    CLASS-DATA ao_session TYPE REF TO /aws1/cl_rt_session_base.
    CLASS-DATA ao_sqs_actions TYPE REF TO /awsex/cl_sqs_actions.
    CLASS-DATA av_test_queue_url TYPE /aws1/sqsstring.
    CLASS-DATA av_test_queue_name TYPE /aws1/sqsstring.

    METHODS: create_queue FOR TESTING RAISING /aws1/cx_rt_generic,
      send_message FOR TESTING RAISING /aws1/cx_rt_generic,
      send_message_batch FOR TESTING RAISING /aws1/cx_rt_generic,
      receive_message FOR TESTING RAISING /aws1/cx_rt_generic,
      delete_message FOR TESTING RAISING /aws1/cx_rt_generic,
      delete_message_batch FOR TESTING RAISING /aws1/cx_rt_generic,
      list_queues FOR TESTING RAISING /aws1/cx_rt_generic,
      get_queue_url FOR TESTING RAISING /aws1/cx_rt_generic,
      delete_queue FOR TESTING RAISING /aws1/cx_rt_generic,
      long_polling_on_msg_receipt FOR TESTING RAISING /aws1/cx_rt_generic,
      long_polling_on_create_queue FOR TESTING RAISING /aws1/cx_rt_generic.

    CLASS-METHODS class_setup RAISING /aws1/cx_rt_generic /awsex/cx_generic.
    CLASS-METHODS class_teardown RAISING /aws1/cx_rt_generic.

    METHODS assert_queue_exists
      IMPORTING
                iv_queue_url TYPE /aws1/sqsstring
                iv_msg       TYPE string
      RAISING   /aws1/cx_rt_generic.


ENDCLASS.
CLASS ltc_awsex_cl_sqs_actions IMPLEMENTATION.

  METHOD class_setup.
    ao_session = /aws1/cl_rt_session_aws=>create( iv_profile_id = cv_pfl ).
    ao_sqs = /aws1/cl_sqs_factory=>create( ao_session ).
    ao_sqs_actions = NEW /awsex/cl_sqs_actions( ).

    " Create a test queue with unique name and tag it for cleanup
    DATA(lv_uuid) = /awsex/cl_utils=>get_random_string( ).
    av_test_queue_name = |sqs-actions-test-{ lv_uuid }|.

    " Create test queue with tags
    DATA lt_tags TYPE /aws1/cl_sqstagmap_w=>tt_tagmap.
    DATA ls_tag TYPE /aws1/cl_sqstagmap_w=>ts_tagmap_maprow.
    ls_tag-key = 'convert_test'.
    ls_tag-value = NEW /aws1/cl_sqstagmap_w( iv_value = 'true' ).
    INSERT ls_tag INTO TABLE lt_tags.

    DATA(lo_create_result) = ao_sqs->createqueue(
      iv_queuename = av_test_queue_name
      it_tags = lt_tags ).
    av_test_queue_url = lo_create_result->get_queueurl( ).

    " Wait for queue to be available
    DATA lv_ready TYPE abap_bool VALUE abap_false.
    DO 6 TIMES.
      WAIT UP TO 5 SECONDS.
      TRY.
          ao_sqs->getqueueurl( iv_queuename = av_test_queue_name ).
          lv_ready = abap_true.
          EXIT.
        CATCH /aws1/cx_sqsqueuedoesnotexist.
          " Queue not ready yet
      ENDTRY.
    ENDDO.
  ENDMETHOD.

  METHOD class_teardown.
    " Clean up test queue
    IF av_test_queue_url IS NOT INITIAL.
      TRY.
          ao_sqs->deletequeue( iv_queueurl = av_test_queue_url ).
        CATCH /aws1/cx_rt_generic.
          " Ignore errors during cleanup
      ENDTRY.
    ENDIF.
  ENDMETHOD.

  METHOD create_queue.
    DATA(lv_uuid) = /awsex/cl_utils=>get_random_string( ).
    DATA(lv_queue_name) = |code-example-create-queue-{ lv_uuid }|.

    " Create queue with tags
    DATA lt_tags TYPE /aws1/cl_sqstagmap_w=>tt_tagmap.
    DATA ls_tag TYPE /aws1/cl_sqstagmap_w=>ts_tagmap_maprow.
    ls_tag-key = 'convert_test'.
    ls_tag-value = NEW /aws1/cl_sqstagmap_w( iv_value = 'true' ).
    INSERT ls_tag INTO TABLE lt_tags.

    DATA(lo_result) = ao_sqs->createqueue(
      iv_queuename = lv_queue_name
      it_tags = lt_tags ).
    DATA(lv_queue_url) = lo_result->get_queueurl( ).

    " Test the action method
    DATA(lo_action_result) = ao_sqs_actions->create_queue( lv_queue_name ).

    cl_abap_unit_assert=>assert_not_initial(
      act = lo_action_result->get_queueurl( )
      msg = |Queue { lv_queue_name } was not created| ).

    " Cleanup
    TRY.
        ao_sqs->deletequeue( iv_queueurl = lv_queue_url ).
        ao_sqs->deletequeue( iv_queueurl = lo_action_result->get_queueurl( ) ).
      CATCH /aws1/cx_sqsqueuedoesnotexist.
        " Already deleted
    ENDTRY.
  ENDMETHOD.
  METHOD send_message.
    CONSTANTS cv_message TYPE /aws1/sqsstring VALUE 'Sample text message to test send message action'.

    DATA(lo_send_result) = ao_sqs_actions->send_message(
      iv_queue_url = av_test_queue_url
      iv_message = cv_message ).

    cl_abap_unit_assert=>assert_not_initial(
      act = lo_send_result->get_messageid( )
      msg = |Message sending failed| ).

    " Verify message was sent by receiving it
    DATA(lo_receive_result) = ao_sqs->receivemessage(
      iv_queueurl = av_test_queue_url
      iv_maxnumberofmessages = 10 ).

    DATA lv_found TYPE abap_bool VALUE abap_false.
    LOOP AT lo_receive_result->get_messages( ) INTO DATA(lo_message).
      IF lo_message->get_messageid( ) = lo_send_result->get_messageid( ) AND lo_message->get_body( ) = cv_message.
        lv_found = abap_true.
      ENDIF.
    ENDLOOP.

    cl_abap_unit_assert=>assert_true(
      act = lv_found
      msg = |Did not receive message { cv_message }| ).

    " Clean up messages from the queue
    LOOP AT lo_receive_result->get_messages( ) INTO lo_message.
      ao_sqs->deletemessage(
        iv_queueurl = av_test_queue_url
        iv_receipthandle = lo_message->get_receipthandle( ) ).
    ENDLOOP.
  ENDMETHOD.
  METHOD receive_message.
    CONSTANTS cv_message TYPE /aws1/sqsstring VALUE 'Sample text message to test receive message action'.

    " Send a message first
    DATA(lo_send_result) = ao_sqs->sendmessage(
      iv_queueurl = av_test_queue_url
      iv_messagebody = cv_message ).

    " Test the action method
    DATA(lo_receive_result) = ao_sqs_actions->receive_message( av_test_queue_url ).

    DATA lv_found TYPE abap_bool VALUE abap_false.
    LOOP AT lo_receive_result->get_messages( ) INTO DATA(lo_message).
      IF lo_message->get_messageid( ) = lo_send_result->get_messageid( ) AND lo_message->get_body( ) = cv_message.
        lv_found = abap_true.
      ENDIF.
    ENDLOOP.

    cl_abap_unit_assert=>assert_true(
      act = lv_found
      msg = |Did not receive message { cv_message }| ).

    " Clean up messages from the queue
    LOOP AT lo_receive_result->get_messages( ) INTO lo_message.
      ao_sqs->deletemessage(
        iv_queueurl = av_test_queue_url
        iv_receipthandle = lo_message->get_receipthandle( ) ).
    ENDLOOP.
  ENDMETHOD.
  METHOD long_polling_on_msg_receipt.
    CONSTANTS cv_message TYPE /aws1/sqsstring VALUE 'Sample text message to test long polling on message receipt'.
    CONSTANTS cv_wait_time TYPE /aws1/sqsinteger VALUE 5.

    " Send a message first
    DATA(lo_send_result) = ao_sqs->sendmessage(
      iv_queueurl = av_test_queue_url
      iv_messagebody = cv_message ).

    " Test the action method with long polling
    DATA(lo_polling_result) = ao_sqs_actions->long_polling_on_msg_receipt(
      iv_queue_url = av_test_queue_url
      iv_wait_time = cv_wait_time ).

    DATA lv_found TYPE abap_bool VALUE abap_false.
    LOOP AT lo_polling_result->get_messages( ) INTO DATA(lo_message).
      IF lo_message->get_messageid( ) = lo_send_result->get_messageid( ) AND lo_message->get_body( ) = cv_message.
        lv_found = abap_true.
      ENDIF.
    ENDLOOP.

    cl_abap_unit_assert=>assert_true(
      act = lv_found
      msg = |Did not receive message { cv_message }| ).

    " Clean up messages from the queue
    LOOP AT lo_polling_result->get_messages( ) INTO lo_message.
      ao_sqs->deletemessage(
        iv_queueurl = av_test_queue_url
        iv_receipthandle = lo_message->get_receipthandle( ) ).
    ENDLOOP.
  ENDMETHOD.
  METHOD long_polling_on_create_queue.
    DATA(lv_uuid) = /awsex/cl_utils=>get_random_string( ).
    DATA(lv_queue_name) = |code-example-long-poll-create-{ lv_uuid }|.
    CONSTANTS cv_wait_time TYPE /aws1/sqsstring VALUE '5'.

    " Create queue with tags
    DATA lt_tags TYPE /aws1/cl_sqstagmap_w=>tt_tagmap.
    DATA ls_tag TYPE /aws1/cl_sqstagmap_w=>ts_tagmap_maprow.
    ls_tag-key = 'convert_test'.
    ls_tag-value = NEW /aws1/cl_sqstagmap_w( iv_value = 'true' ).
    INSERT ls_tag INTO TABLE lt_tags.

    " Test the action method
    DATA(lo_create_result) = ao_sqs_actions->long_polling_on_create_queue(
      iv_queue_name = lv_queue_name
      iv_wait_time = cv_wait_time ).

    cl_abap_unit_assert=>assert_not_initial(
      act = lo_create_result->get_queueurl( )
      msg = |Queue { lv_queue_name } was not created| ).

    " Verify the ReceiveMessageWaitTimeSeconds attribute
    DATA lt_attributes TYPE /aws1/cl_sqsattrnamelist_w=>tt_attributenamelist.
    APPEND NEW /aws1/cl_sqsattrnamelist_w( iv_value = 'ReceiveMessageWaitTimeSeconds' ) TO lt_attributes.
    DATA(lo_get_result) = ao_sqs->getqueueattributes(
      iv_queueurl = lo_create_result->get_queueurl( )
      it_attributenames = lt_attributes ).

    DATA lv_found TYPE abap_bool VALUE abap_false.
    LOOP AT lo_get_result->get_attributes( ) INTO DATA(lo_attribute).
      IF lo_attribute-key = 'ReceiveMessageWaitTimeSeconds'.
        cl_abap_unit_assert=>assert_equals(
          act = lo_attribute-value->get_value( )
          exp = cv_wait_time
          msg = |ReceiveMessageWaitTimeSeconds attribute did not match| ).
        lv_found = abap_true.
      ENDIF.
    ENDLOOP.

    cl_abap_unit_assert=>assert_true(
      act = lv_found
      msg = |ReceiveMessageWaitTimeSeconds attribute not found| ).

    " Cleanup
    ao_sqs->deletequeue( iv_queueurl = lo_create_result->get_queueurl( ) ).
  ENDMETHOD.
  METHOD get_queue_url.
    " Test with the existing test queue
    DATA(lo_get_result) = ao_sqs_actions->get_queue_url( av_test_queue_name ).

    cl_abap_unit_assert=>assert_equals(
      act = lo_get_result->get_queueurl( )
      exp = av_test_queue_url
      msg = |Queue URL did not match expected value| ).
  ENDMETHOD.

  METHOD list_queues.
    " Test the action method
    DATA(lo_result) = ao_sqs_actions->list_queues( ).

    " Verify our test queue is in the list
    DATA lv_found TYPE abap_bool VALUE abap_false.
    LOOP AT lo_result->get_queueurls( ) INTO DATA(lo_url).
      IF lo_url->get_value( ) = av_test_queue_url.
        lv_found = abap_true.
        EXIT.
      ENDIF.
    ENDLOOP.

    cl_abap_unit_assert=>assert_true(
      act = lv_found
      msg = |Test queue should be in the list| ).
  ENDMETHOD.

  METHOD delete_queue.
    DATA(lv_uuid) = /awsex/cl_utils=>get_random_string( ).
    DATA(lv_queue_name) = |code-example-delete-queue-{ lv_uuid }|.

    " Create queue with tags
    DATA lt_tags TYPE /aws1/cl_sqstagmap_w=>tt_tagmap.
    DATA ls_tag TYPE /aws1/cl_sqstagmap_w=>ts_tagmap_maprow.
    ls_tag-key = 'convert_test'.
    ls_tag-value = NEW /aws1/cl_sqstagmap_w( iv_value = 'true' ).
    INSERT ls_tag INTO TABLE lt_tags.

    DATA(lo_create_result) = ao_sqs->createqueue(
      iv_queuename = lv_queue_name
      it_tags = lt_tags ).
    DATA(lv_queue_url) = lo_create_result->get_queueurl( ).

    " Test the action method
    ao_sqs_actions->delete_queue( lv_queue_url ).

    " Verify queue is deleted
    DATA lv_found TYPE abap_bool VALUE abap_true.
    DATA lo_list_result TYPE REF TO /aws1/cl_sqslistqueuesresult.
    DO 6 TIMES.
      WAIT UP TO 10 SECONDS.
      lv_found = abap_false.
      lo_list_result = ao_sqs->listqueues( ).
      LOOP AT lo_list_result->get_queueurls( ) INTO DATA(lo_url).
        IF lo_url->get_value( ) = lv_queue_url.
          lv_found = abap_true.
          EXIT.
        ENDIF.
      ENDLOOP.
      IF lv_found = abap_false.
        EXIT.
      ENDIF.
    ENDDO.

    cl_abap_unit_assert=>assert_false(
      act = lv_found
      msg = |Queue should have been deleted| ).
  ENDMETHOD.
  METHOD assert_queue_exists.
    DATA lv_found TYPE abap_bool VALUE abap_false.
    DATA lo_result TYPE REF TO /aws1/cl_sqslistqueuesresult.
    WHILE lv_found = abap_false AND sy-index <= 6.
      WAIT UP TO 10 SECONDS.                                  " Making sure that the queue is ready for use after creation.
      lo_result = ao_sqs->listqueues( ).
      LOOP AT lo_result->get_queueurls( ) INTO DATA(lo_url).
        IF lo_url->get_value( ) = iv_queue_url.
          lv_found = abap_true.
        ENDIF.
      ENDLOOP.
    ENDWHILE.

    cl_abap_unit_assert=>assert_true(
          act = lv_found
          msg = iv_msg ).

  ENDMETHOD.

  METHOD send_message_batch.
    " Create batch messages
    DATA lt_messages TYPE /aws1/cl_sqssendmsgbtcreqentry=>tt_sendmsgbatchreqentrylist.
    DATA lv_counter TYPE i.
    DO 3 TIMES.
      lv_counter = sy-index.
      DATA(lv_id) = |msg-{ lv_counter }|.
      DATA(lv_body) = |Sample message { lv_counter } for batch test|.
      APPEND NEW /aws1/cl_sqssendmsgbtcreqentry(
        iv_id = lv_id
        iv_messagebody = lv_body
      ) TO lt_messages.
    ENDDO.

    " Test the action method
    DATA(lo_batch_result) = ao_sqs_actions->send_message_batch(
      iv_queue_url = av_test_queue_url
      it_messages = lt_messages ).

    " Verify successful sends
    cl_abap_unit_assert=>assert_equals(
      act = lines( lo_batch_result->get_successful( ) )
      exp = 3
      msg = |Expected 3 successful messages| ).

    " Verify no failures
    cl_abap_unit_assert=>assert_equals(
      act = lines( lo_batch_result->get_failed( ) )
      exp = 0
      msg = |Expected no failed messages| ).

    " Verify messages can be received
    DATA(lo_receive_result) = ao_sqs->receivemessage(
      iv_queueurl = av_test_queue_url
      iv_maxnumberofmessages = 10 ).

    cl_abap_unit_assert=>assert_equals(
      act = lines( lo_receive_result->get_messages( ) )
      exp = 3
      msg = |Expected 3 messages in queue| ).

    " Clean up messages from the queue
    LOOP AT lo_receive_result->get_messages( ) INTO DATA(lo_message).
      ao_sqs->deletemessage(
        iv_queueurl = av_test_queue_url
        iv_receipthandle = lo_message->get_receipthandle( ) ).
    ENDLOOP.
  ENDMETHOD.

  METHOD delete_message.
    CONSTANTS cv_message TYPE /aws1/sqsstring VALUE 'Sample message to test delete message'.

    " Send a message
    DATA(lo_send_result) = ao_sqs->sendmessage(
      iv_queueurl = av_test_queue_url
      iv_messagebody = cv_message ).

    " Receive the message to get receipt handle
    DATA(lo_receive_result) = ao_sqs->receivemessage(
      iv_queueurl = av_test_queue_url ).

    DATA(lt_messages) = lo_receive_result->get_messages( ).
    cl_abap_unit_assert=>assert_not_initial(
      act = lt_messages
      msg = |No messages received| ).

    READ TABLE lt_messages INDEX 1 INTO DATA(lo_message).
    DATA(lv_receipt_handle) = lo_message->get_receipthandle( ).

    " Test the action method
    ao_sqs_actions->delete_message(
      iv_queue_url = av_test_queue_url
      iv_receipt_handle = lv_receipt_handle ).

    " Verify message is deleted - receive should return empty
    DATA(lo_verify_result) = ao_sqs->receivemessage(
      iv_queueurl = av_test_queue_url
      iv_waittimeseconds = 1 ).

    cl_abap_unit_assert=>assert_equals(
      act = lines( lo_verify_result->get_messages( ) )
      exp = 0
      msg = |Message should have been deleted| ).
  ENDMETHOD.

  METHOD delete_message_batch.
    " Send multiple messages
    DATA lt_send_entries TYPE /aws1/cl_sqssendmsgbtcreqentry=>tt_sendmsgbatchreqentrylist.
    DO 3 TIMES.
      DATA(lv_id) = |msg-{ sy-index }|.
      DATA(lv_body) = |Message { sy-index } for batch delete test|.
      APPEND NEW /aws1/cl_sqssendmsgbtcreqentry(
        iv_id = lv_id
        iv_messagebody = lv_body
      ) TO lt_send_entries.
    ENDDO.

    ao_sqs->sendmessagebatch(
      iv_queueurl = av_test_queue_url
      it_entries = lt_send_entries ).

    " Receive messages to get receipt handles
    DATA(lo_receive_result) = ao_sqs->receivemessage(
      iv_queueurl = av_test_queue_url
      iv_maxnumberofmessages = 10 ).

    DATA(lt_received_messages) = lo_receive_result->get_messages( ).
    cl_abap_unit_assert=>assert_equals(
      act = lines( lt_received_messages )
      exp = 3
      msg = |Expected 3 messages to be received| ).

    " Build batch delete entries
    DATA lt_delete_entries TYPE /aws1/cl_sqsdelmsgbtcreqentry=>tt_deletemsgbatchreqentrylist.
    DATA lv_counter TYPE i VALUE 1.
    LOOP AT lt_received_messages INTO DATA(lo_msg).
      APPEND NEW /aws1/cl_sqsdelmsgbtcreqentry(
        iv_id = |del-{ lv_counter }|
        iv_receipthandle = lo_msg->get_receipthandle( )
      ) TO lt_delete_entries.
      lv_counter = lv_counter + 1.
    ENDLOOP.

    " Test the action method
    DATA(lo_delete_result) = ao_sqs_actions->delete_message_batch(
      iv_queue_url = av_test_queue_url
      it_entries = lt_delete_entries ).

    " Verify successful deletes
    cl_abap_unit_assert=>assert_equals(
      act = lines( lo_delete_result->get_successful( ) )
      exp = 3
      msg = |Expected 3 successful deletes| ).

    " Verify no failures
    cl_abap_unit_assert=>assert_equals(
      act = lines( lo_delete_result->get_failed( ) )
      exp = 0
      msg = |Expected no failed deletes| ).

    " Verify queue is empty
    DATA(lo_verify_result) = ao_sqs->receivemessage(
      iv_queueurl = av_test_queue_url
      iv_waittimeseconds = 1 ).

    cl_abap_unit_assert=>assert_equals(
      act = lines( lo_verify_result->get_messages( ) )
      exp = 0
      msg = |Queue should be empty after batch delete| ).
  ENDMETHOD.
ENDCLASS.
