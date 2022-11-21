" """"""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""
" "  Copyright 2022 Amazon.com, Inc. or its affiliates. All Rights
" "  Reserved.
" "  SPDX-License-Identifier: MIT-0
" """"""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""

CLASS ltc_zcl_aws1_sns_actions DEFINITION DEFERRED.
CLASS zcl_aws1_sns_actions DEFINITION LOCAL FRIENDS ltc_zcl_aws1_sns_actions.

CLASS ltc_zcl_aws1_sns_actions DEFINITION FOR TESTING  DURATION SHORT RISK LEVEL HARMLESS.

  PRIVATE SECTION.
    CONSTANTS: cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA ao_sns TYPE REF TO /aws1/if_sns.
    DATA ao_session TYPE REF TO /aws1/cl_rt_session_base.
    DATA ao_sns_actions TYPE REF TO zcl_aws1_sns_actions.

    METHODS: create_topic FOR TESTING RAISING /aws1/cx_rt_generic,
      list_topics FOR TESTING RAISING /aws1/cx_rt_generic,
      get_topic_attributes FOR TESTING RAISING /aws1/cx_rt_generic,
      list_subscriptions FOR TESTING RAISING /aws1/cx_rt_generic,
      subscribe_email FOR TESTING RAISING /aws1/cx_rt_generic,
      unsubscribe FOR TESTING RAISING /aws1/cx_rt_generic,
      delete_topic FOR TESTING RAISING /aws1/cx_rt_generic,
      publish_to_topic FOR TESTING RAISING /aws1/cx_rt_generic,
      set_topic_attributes FOR TESTING RAISING /aws1/cx_rt_generic.

    METHODS setup RAISING /aws1/cx_rt_generic ycx_aws1_mit_generic.

    METHODS assert_subscription_exists
      IMPORTING
                iv_topic_arn        TYPE /aws1/snstopicarn
                iv_subscription_arn TYPE /aws1/snssubscriptionarn
                iv_exp              TYPE abap_bool
                iv_msg              TYPE string
      RAISING   /aws1/cx_rt_generic.
    METHODS assert_topic_exists
      IMPORTING
                iv_topic_arn TYPE /aws1/snstopicarn
                iv_exp       TYPE abap_bool
                iv_msg       TYPE string
      RAISING   /aws1/cx_rt_generic.
ENDCLASS.

CLASS ltc_zcl_aws1_sns_actions IMPLEMENTATION.

  METHOD setup.
    ao_session = /aws1/cl_rt_session_aws=>create( iv_profile_id = cv_pfl ).
    ao_sns = /aws1/cl_sns_factory=>create( ao_session ).
    ao_sns_actions = NEW zcl_aws1_sns_actions( ).
  ENDMETHOD.
  METHOD create_topic.
    CONSTANTS: cv_topic_name TYPE /aws1/snstopicname VALUE 'code-example-create-topic'.
    DATA(lo_result) = ao_sns_actions->create_topic( iv_topic_name = cv_topic_name ).
    assert_topic_exists(
      iv_topic_arn = lo_result->get_topicarn( )
      iv_exp = abap_true
      iv_msg = |Topic { cv_topic_name } was not created|
    ).
    ao_sns->deletetopic( iv_topicarn = lo_result->get_topicarn( ) ).
  ENDMETHOD.
  METHOD delete_topic.
    CONSTANTS: cv_topic_name TYPE /aws1/snstopicname VALUE 'code-example-delete-topic'.
    DATA(lo_result) = ao_sns->createtopic( iv_name = cv_topic_name ).
    DATA(lv_topic_arn) = lo_result->get_topicarn( ).

    ao_sns_actions->delete_topic( iv_topic_arn = lv_topic_arn ).
    assert_topic_exists(
    iv_topic_arn = lv_topic_arn
    iv_exp = abap_false
    iv_msg = |Topic { cv_topic_name } should have been deleted|
  ).
  ENDMETHOD.
  METHOD get_topic_attributes.
    CONSTANTS: cv_topic_name TYPE /aws1/snstopicname VALUE 'code-example-get-topic-attributes'.
    DATA(lo_create_result) = ao_sns->createtopic( iv_name = cv_topic_name ).
    DATA(lv_topic_arn) = lo_create_result->get_topicarn( ).
    DATA(lo_get_attributes_result) = ao_sns_actions->get_topic_attributes( iv_topic_arn = lv_topic_arn ).
    DATA lv_found TYPE abap_bool VALUE abap_false.
    LOOP AT lo_get_attributes_result->get_attributes( ) INTO DATA(wa_attribute).
      IF wa_attribute-key = 'TopicArn' AND wa_attribute-value->get_value( ) = lv_topic_arn.
        lv_found = abap_true.
      ENDIF.
    ENDLOOP.
    cl_abap_unit_assert=>assert_true(
 act = lv_found
       msg = |Couldn't retrive attributes for topic { cv_topic_name }|
    ).
    ao_sns->deletetopic( iv_topicarn = lv_topic_arn ).
  ENDMETHOD.
  METHOD subscribe_email.
    CONSTANTS: cv_topic_name TYPE /aws1/snstopicname VALUE 'code-example-subscribe-email'.
    CONSTANTS: cv_email_address TYPE /aws1/snsendpoint2 VALUE 'dummyemail@example.com'.

    DATA(lo_create_result) = ao_sns->createtopic( iv_name = cv_topic_name ).
    DATA(lv_topic_arn) = lo_create_result->get_topicarn( ).
    DATA(lo_subscribe_result) =  ao_sns_actions->subscribe_email(
        iv_topic_arn = lv_topic_arn
        iv_email_address = cv_email_address
    ).
    cl_abap_unit_assert=>assert_not_initial(
          act = lo_subscribe_result->get_subscriptionarn( )
          msg = |Unable to subcribe email address { cv_email_address } to SNS topic { cv_topic_name }|
        ).
    assert_subscription_exists(
        iv_topic_arn = lv_topic_arn
        iv_subscription_arn = 'PendingConfirmation'
        iv_exp = abap_true
        iv_msg = |Email { cv_email_address } should have been subscribed|
      ).
    ao_sns->deletetopic( iv_topicarn = lv_topic_arn ).
  ENDMETHOD.
  METHOD unsubscribe.
    CONSTANTS: cv_topic_name TYPE /aws1/snstopicname VALUE 'code-example-unsubscribe'.
    CONSTANTS: cv_queue_name TYPE /aws1/sqsstring VALUE 'code-example-unsubscribe-queue'.

    DATA(lo_create_result) = ao_sns->createtopic( iv_name = cv_topic_name ).
    DATA(lv_topic_arn) = lo_create_result->get_topicarn( ).

    DATA(ao_sqs) = /aws1/cl_sqs_factory=>create( ao_session ).
    DATA(lo_create_queue_result) = ao_sqs->createqueue( iv_queuename = cv_queue_name ).
    DATA(lv_queue_url) =  lo_create_queue_result->get_queueurl( ).
    DATA lt_required_attributes TYPE /aws1/cl_sqsattrnamelist_w=>tt_attributenamelist.
    APPEND NEW /aws1/cl_sqsattrnamelist_w( iv_value = 'QueueArn' ) TO lt_required_attributes.
    DATA(lt_queueattributes) = ao_sqs->getqueueattributes( iv_queueurl = lv_queue_url it_attributenames = lt_required_attributes )->get_attributes( ).
    READ TABLE lt_queueattributes INTO DATA(ls_queueattribute) WITH TABLE KEY key = 'QueueArn'.
    DATA(lv_queue_arn) = ls_queueattribute-value->get_value( ).

    DATA(lo_subscribe_result) =  ao_sns->subscribe(
       iv_topicarn = lv_topic_arn
       iv_protocol = 'sqs'
       iv_endpoint = lv_queue_arn
       iv_returnsubscriptionarn = abap_true
    ).
    DATA(lv_subscription_arn) = lo_subscribe_result->get_subscriptionarn( ).
    ao_sns_actions->unsubscribe( iv_subscription_arn = lv_subscription_arn ).
    assert_subscription_exists(
       iv_topic_arn = lv_topic_arn
       iv_subscription_arn = lv_subscription_arn
       iv_exp = abap_false
       iv_msg = |Subscriptionl { lv_subscription_arn } should have been subscribed|
     ).
    ao_sqs->deletequeue( iv_queueurl = lv_queue_url ).
    ao_sns->deletetopic( iv_topicarn = lv_topic_arn ).
  ENDMETHOD.
  METHOD list_subscriptions.
    CONSTANTS: cv_topic_name TYPE /aws1/snstopicname VALUE 'code-example-list-subscriptions'.
    CONSTANTS: cv_queue_name TYPE /aws1/sqsstring VALUE 'code-example-list-queue'.

    DATA(lo_create_result) = ao_sns->createtopic( iv_name = cv_topic_name ).
    DATA(lv_topic_arn) = lo_create_result->get_topicarn( ).

    DATA(ao_sqs) = /aws1/cl_sqs_factory=>create( ao_session ).
    DATA(lo_create_queue_result) = ao_sqs->createqueue( iv_queuename = cv_queue_name ).
    DATA(lv_queue_url) =  lo_create_queue_result->get_queueurl( ).
    DATA lt_required_attributes TYPE /aws1/cl_sqsattrnamelist_w=>tt_attributenamelist.
    APPEND NEW /aws1/cl_sqsattrnamelist_w( iv_value = 'QueueArn' ) TO lt_required_attributes.
    DATA(lt_queueattributes) = ao_sqs->getqueueattributes( iv_queueurl = lv_queue_url it_attributenames = lt_required_attributes )->get_attributes( ).
    READ TABLE lt_queueattributes INTO DATA(ls_queueattribute) WITH TABLE KEY key = 'QueueArn'.
    DATA(lv_queue_arn) = ls_queueattribute-value->get_value( ).

    DATA(lo_subscribe_result) =  ao_sns->subscribe(
       iv_topicarn = lv_topic_arn
       iv_protocol = 'sqs'
       iv_endpoint = lv_queue_arn
       iv_returnsubscriptionarn = abap_true
    ).
    DATA(lv_subscription_arn) = lo_subscribe_result->get_subscriptionarn( ).
    DATA(lo_list_result) = ao_sns_actions->list_subscriptions( ).
    cl_abap_unit_assert=>assert_not_initial(
      act = lo_list_result->get_subscriptions( )
      msg = |Subscription List should not be empty|
    ).
    ao_sns->unsubscribe( iv_subscriptionarn = lv_subscription_arn ).
    ao_sqs->deletequeue( iv_queueurl = lv_queue_url ).
    ao_sns->deletetopic( iv_topicarn = lv_topic_arn ).
  ENDMETHOD.
  METHOD list_topics.
    CONSTANTS: cv_topic_name TYPE /aws1/snstopicname VALUE 'code-example-list-topics'.
    DATA(lo_create_result) = ao_sns->createtopic( iv_name = cv_topic_name ).
    DATA(lv_topic_arn) = lo_create_result->get_topicarn( ).
    DATA(lo_list_result) = ao_sns_actions->list_topics( ).
    DATA lv_found TYPE abap_bool VALUE abap_false.
    LOOP AT lo_list_result->get_topics( ) INTO DATA(lo_topic).
      IF lo_topic->get_topicarn( ) = lv_topic_arn.
        lv_found = abap_true.
      ENDIF.
    ENDLOOP.
    cl_abap_unit_assert=>assert_true(
      act = lv_found
      msg = |Topic { cv_topic_name } should have been included in topic list|
    ).
    ao_sns->deletetopic( iv_topicarn = lv_topic_arn ).
  ENDMETHOD.
  METHOD publish_to_topic.
    CONSTANTS: cv_topic_name TYPE /aws1/snstopicname VALUE 'code-example-publish-to-topic'.
    CONSTANTS: cv_message TYPE /aws1/snsmessage VALUE 'Sample message published to a topic'.
    DATA(lo_create_result) = ao_sns->createtopic( iv_name = cv_topic_name ).
    DATA(lv_topic_arn) = lo_create_result->get_topicarn( ).
    DATA(lo_publish_result) = ao_sns_actions->publish_to_topic(
                           iv_topic_arn = lv_topic_arn
                           iv_message   = cv_message
                       ).
    cl_abap_unit_assert=>assert_not_initial(
                 act = lo_publish_result->get_messageid( )
                 msg = |Failed to publish message SNS topint  { lv_topic_arn }|
               ).
    ao_sns->deletetopic( iv_topicarn = lv_topic_arn ).
  ENDMETHOD.
  METHOD set_topic_attributes.
    CONSTANTS: cv_topic_name TYPE /aws1/snstopicname VALUE 'code-example-set-topic-attributes'.
    CONSTANTS: cv_attribute_name TYPE /aws1/snsmessage VALUE 'DisplayName'.
    CONSTANTS: cv_attribute_value TYPE /aws1/snsattributevalue VALUE 'TestDisplayName'.
    DATA(lo_create_result) = ao_sns->createtopic( iv_name = cv_topic_name ).
    DATA(lv_topic_arn) = lo_create_result->get_topicarn( ).
    DATA(lt_attributes) = ao_sns->gettopicattributes( iv_topicarn = lv_topic_arn )->get_attributes( ).
    READ TABLE lt_attributes INTO DATA(ls_attributes) WITH TABLE KEY key = cv_attribute_name.
    cl_abap_unit_assert=>assert_initial(
       act = ls_attributes-value->get_value( )
       msg = |Display Name for SNS topic { cv_topic_name } should have be empty |
     ).
    ao_sns_actions->set_topic_attributes(
        iv_topic_arn       = lv_topic_arn
        iv_attribute_name  = cv_attribute_name
        iv_attribute_value = cv_attribute_value
    ).
    CLEAR ls_attributes.
    CLEAR lt_attributes.
    lt_attributes = ao_sns->gettopicattributes( iv_topicarn = lv_topic_arn )->get_attributes( ).
    READ TABLE lt_attributes INTO ls_attributes WITH TABLE KEY key = cv_attribute_name.
    cl_abap_unit_assert=>assert_equals(
    exp = ls_attributes-value->get_value( )
                 act = cv_attribute_value
                 msg = |{ cv_attribute_name } for topic { cv_topic_name } did not match the expected value { cv_attribute_value }|
               ).
    ao_sns->deletetopic( iv_topicarn = lv_topic_arn ).
  ENDMETHOD.
  METHOD assert_topic_exists.
    DATA lv_found TYPE abap_bool VALUE abap_false.
    LOOP AT ao_sns->listtopics( )->get_topics( ) INTO DATA(lo_topic).
      IF lo_topic->get_topicarn( ) = iv_topic_arn.
        lv_found = abap_true.
      ENDIF.
    ENDLOOP.
    cl_abap_unit_assert=>assert_equals(
      exp = iv_exp
      act = lv_found
      msg = iv_msg
    ).
  ENDMETHOD.
  METHOD assert_subscription_exists.
    DATA lv_found TYPE abap_bool VALUE abap_false.
    LOOP AT ao_sns->listsubscriptionsbytopic( iv_topicarn = iv_topic_arn )->get_subscriptions( ) INTO DATA(lo_subscription).
      IF lo_subscription->get_subscriptionarn( ) = iv_subscription_arn.
        lv_found = abap_true.
      ENDIF.
    ENDLOOP.
    cl_abap_unit_assert=>assert_equals(
      exp = iv_exp
      act = lv_found
      msg = iv_msg
    ).
  ENDMETHOD.
ENDCLASS.
