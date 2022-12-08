" """"""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""
" "  Copyright 2022 Amazon.com, Inc. or its affiliates. All Rights
" "  Reserved.
" "  SPDX-License-Identifier: MIT-0
" """"""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""

CLASS ltc_zcl_aws1_sns_scenario DEFINITION DEFERRED.
CLASS zcl_aws1_sns_scenario DEFINITION LOCAL FRIENDS ltc_zcl_aws1_sns_scenario.

CLASS ltc_zcl_aws1_sns_scenario DEFINITION FOR TESTING  DURATION MEDIUM RISK LEVEL HARMLESS.

  PRIVATE SECTION.
    CONSTANTS: cv_pfl        TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO',
               cv_topic_name TYPE /aws1/snstopicname VALUE 'code-example-scenario-topic.fifo',
               cv_queue_name TYPE /aws1/sqsstring VALUE 'code-example-scenario-queue.fifo'.

    DATA ao_sns TYPE REF TO /aws1/if_sns.
    DATA ao_session TYPE REF TO /aws1/cl_rt_session_base.
    DATA ao_sns_scenario TYPE REF TO zcl_aws1_sns_scenario.
    DATA ao_sqs TYPE REF TO /aws1/if_sqs.

    DATA av_topic_arn  TYPE /aws1/snstopicarn.
    DATA av_queue_arn  TYPE /aws1/sqsstring.
    DATA av_queue_url  TYPE /aws1/sqsstring.

    METHODS fifo_topic_scenario FOR TESTING RAISING /aws1/cx_rt_generic.

    METHODS: setup RAISING /aws1/cx_rt_generic ycx_aws1_mit_generic,
      create_and_configure_queue RAISING /aws1/cx_rt_generic ycx_aws1_mit_generic,
      verify_message_delivery
        IMPORTING iv_message_id TYPE /aws1/snsmessageid
        RAISING   /aws1/cx_rt_generic ycx_aws1_mit_generic,
      delete_queue RAISING /aws1/cx_rt_generic ycx_aws1_mit_generic,
      assert_subscription_deleted RAISING /aws1/cx_rt_generic ycx_aws1_mit_generic,
      assert_queue_deleted RAISING   /aws1/cx_rt_generic ycx_aws1_mit_generic,
      assert_topic_deleted RAISING /aws1/cx_rt_generic ycx_aws1_mit_generic.

ENDCLASS.
CLASS ltc_zcl_aws1_sns_scenario IMPLEMENTATION.

  METHOD setup.
    ao_session = /aws1/cl_rt_session_aws=>create( iv_profile_id = cv_pfl ).
    ao_sns = /aws1/cl_sns_factory=>create( ao_session ).
    ao_sns_scenario = NEW zcl_aws1_sns_scenario( ).
    ao_sqs = /aws1/cl_sqs_factory=>create( ao_session ).

  ENDMETHOD.
  METHOD fifo_topic_scenario.

    "Create and configure FIFO queue.
    create_and_configure_queue( ).

    DATA lv_subscription_arn TYPE /aws1/snssubscriptionarn.
    DATA lv_message_id TYPE /aws1/snsmessageid.
    ao_sns_scenario->publish_message_to_fifo_topic(
          EXPORTING
            iv_topic_name = cv_topic_name
            iv_queue_arn  = av_queue_arn
          IMPORTING
            ov_subscription_arn = lv_subscription_arn
            ov_topic_arn = av_topic_arn
            ov_message_id = lv_message_id
        ).

    " Verify message delivery (message received by queue).
    verify_message_delivery( iv_message_id = lv_message_id ).

    " Delete subscription.
    ao_sns->unsubscribe( iv_subscriptionarn = lv_subscription_arn ).
    assert_subscription_deleted( ).

    "Delete FIFO queue.
    delete_queue( ).
    assert_queue_deleted( ).

    "Delete Amazon Simple Notification Service (Amazon SNS) topic.
    ao_sns->deletetopic( iv_topicarn = av_topic_arn ).
    assert_topic_deleted( ).

  ENDMETHOD.
  METHOD create_and_configure_queue.
    DATA lt_attributes TYPE /aws1/cl_sqsqueueattrmap_w=>tt_queueattributemap.
    DATA ls_attribute TYPE /aws1/cl_sqsqueueattrmap_w=>ts_queueattributemap_maprow.
    ls_attribute-key = 'FifoQueue'.
    ls_attribute-value = NEW /aws1/cl_sqsqueueattrmap_w( iv_value = 'true' ).
    INSERT ls_attribute INTO TABLE lt_attributes.

    DATA(lo_create_queue_result) = ao_sqs->createqueue(
          iv_queuename = cv_queue_name
          it_attributes = lt_attributes
    ).
    av_queue_url =  lo_create_queue_result->get_queueurl( ).
    cl_abap_unit_assert=>assert_not_initial(
          act = av_queue_url
          msg = |Failed to create queue { cv_queue_name }|
        ).

    DATA(lv_policydocument) = |\{ | &&
      |  "Version": "2008-10-17", | &&
      |  "Statement": [ | &&
      |    \{ | &&
      |      "Effect": "Allow", | &&
      |      "Principal": \{ | &&
      |         "Service": "sns.amazonaws.com" | &&
      |      \}, | &&
      |      "Action": "sqs:SendMessage", | &&
      |      "Resource": "arn:aws:sqs:*:*:code-example-scenario-queue.fifo", | &&
      |      "Condition": \{ | &&
      |         "ArnEquals": \{ | &&
      |           "aws:SourceArn": "arn:aws:sns:*:*:code-example-scenario-topic.fifo" | &&
      |         \} | &&
      |      \} | &&
      |    \} | &&
      |  ] | &&
      |\} |.


    CLEAR lt_attributes.
    CLEAR ls_attribute.
    ls_attribute-key = 'Policy'.
    ls_attribute-value = NEW /aws1/cl_sqsqueueattrmap_w( iv_value = lv_policydocument ).
    INSERT ls_attribute INTO TABLE lt_attributes.

    ao_sqs->setqueueattributes(
        iv_queueurl  = av_queue_url
        it_attributes = lt_attributes
    ).

    DATA lt_required_attributes TYPE /aws1/cl_sqsattrnamelist_w=>tt_attributenamelist.
    APPEND NEW /aws1/cl_sqsattrnamelist_w( iv_value = 'QueueArn' ) TO lt_required_attributes.
    DATA(lt_queueattributes) = ao_sqs->getqueueattributes( iv_queueurl = av_queue_url it_attributenames = lt_required_attributes )->get_attributes( ).
    READ TABLE lt_queueattributes INTO DATA(ls_queueattribute) WITH TABLE KEY key = 'QueueArn'.
    av_queue_arn = ls_queueattribute-value->get_value( ).

  ENDMETHOD.
  METHOD verify_message_delivery.
    WAIT UP TO 20 SECONDS. "Making sure that the message is received by the Amazon Simple Queue Service (Amazon SQS) queue.
    DATA(lo_result) = ao_sqs->receivemessage( iv_queueurl = av_queue_url ).

    DATA lv_found TYPE abap_bool VALUE abap_false.
    LOOP AT lo_result->get_messages( ) INTO DATA(lo_message).
      IF lo_message->get_body( ) CS iv_message_id.
        lv_found = abap_true.
      ENDIF.
    ENDLOOP.

    cl_abap_unit_assert=>assert_true(
      act = lv_found
      msg = |Received message did not match expected body contents|
    ).
  ENDMETHOD.
  METHOD delete_queue.
    ao_sqs->deletequeue( iv_queueurl = av_queue_url ).
    WAIT UP TO 60 SECONDS. "Queue deletion operation takes up to 60 seconds.
  ENDMETHOD.
  METHOD assert_subscription_deleted.
    DATA lv_found TYPE abap_bool VALUE abap_false.
    LOOP AT ao_sns->listsubscriptionsbytopic( iv_topicarn = av_topic_arn )->get_subscriptions( ) INTO DATA(lo_subscription).
      IF lo_subscription->get_endpoint( ) = av_queue_arn AND lo_subscription->get_protocol( ) = 'sqs'.
        lv_found = abap_true.
      ENDIF.
    ENDLOOP.

    cl_abap_unit_assert=>assert_false(
        act = lv_found
        msg = |Subscription should have been deleted|
    ).
  ENDMETHOD.
  METHOD assert_topic_deleted.
    DATA lv_found TYPE abap_bool VALUE abap_false.
    LOOP AT ao_sns->listtopics( )->get_topics( ) INTO DATA(lo_topic).
      IF lo_topic->get_topicarn( ) = av_topic_arn.
        lv_found = abap_true.
      ENDIF.
    ENDLOOP.

    cl_abap_unit_assert=>assert_false(
        act = lv_found
        msg = |Topic { cv_topic_name } should have been deleted|
    ).
  ENDMETHOD.
  METHOD assert_queue_deleted.
    DATA lv_found TYPE abap_bool VALUE abap_false.
    LOOP AT ao_sqs->listqueues( )->get_queueurls( ) INTO DATA(lo_url).
      IF lo_url->get_value( ) = av_queue_url.
        lv_found = abap_true.
      ENDIF.
    ENDLOOP.

    cl_abap_unit_assert=>assert_false(
        act = lv_found
        msg = |Queue { cv_queue_name } should have been deleted|
    ).
  ENDMETHOD.
ENDCLASS.
