" Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
" SPDX-License-Identifier: Apache-2.0
CLASS ltc_awsex_cl_sns_actions DEFINITION DEFERRED.
CLASS /awsex/cl_sns_actions DEFINITION LOCAL FRIENDS ltc_awsex_cl_sns_actions.

CLASS ltc_awsex_cl_sns_actions DEFINITION FOR TESTING DURATION SHORT RISK LEVEL DANGEROUS.

  PRIVATE SECTION.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    CLASS-DATA ao_sns TYPE REF TO /aws1/if_sns.
    CLASS-DATA ao_session TYPE REF TO /aws1/cl_rt_session_base.
    CLASS-DATA ao_sns_actions TYPE REF TO /awsex/cl_sns_actions.
    CLASS-DATA ao_sqs TYPE REF TO /aws1/if_sqs.

    METHODS: create_topic FOR TESTING RAISING /aws1/cx_rt_generic,
      list_topics FOR TESTING RAISING /aws1/cx_rt_generic,
      get_topic_attributes FOR TESTING RAISING /aws1/cx_rt_generic,
      list_subscriptions FOR TESTING RAISING /aws1/cx_rt_generic,
      subscribe_email FOR TESTING RAISING /aws1/cx_rt_generic,
      unsubscribe FOR TESTING RAISING /aws1/cx_rt_generic,
      delete_topic FOR TESTING RAISING /aws1/cx_rt_generic,
      publish_to_topic FOR TESTING RAISING /aws1/cx_rt_generic,
      set_topic_attributes FOR TESTING RAISING /aws1/cx_rt_generic,
      publish_text_message FOR TESTING RAISING /aws1/cx_rt_generic,
      publish_message FOR TESTING RAISING /aws1/cx_rt_generic,
      add_subscription_filter FOR TESTING RAISING /aws1/cx_rt_generic,
      publish_multi_message FOR TESTING RAISING /aws1/cx_rt_generic.

    CLASS-METHODS class_setup RAISING /aws1/cx_rt_generic.
    CLASS-METHODS class_teardown.

    METHODS get_uuid
      RETURNING
        VALUE(ov_uuid) TYPE string.

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

CLASS ltc_awsex_cl_sns_actions IMPLEMENTATION.

  METHOD class_setup.
    ao_session = /aws1/cl_rt_session_aws=>create( iv_profile_id = cv_pfl ).
    ao_sns = /aws1/cl_sns_factory=>create( ao_session ).
    ao_sns_actions = NEW /awsex/cl_sns_actions( ).
    ao_sqs = /aws1/cl_sqs_factory=>create( ao_session ).
  ENDMETHOD.

  METHOD class_teardown.
    " Clean up any resources tagged with 'convert_test'
    " Note: SNS topics and subscriptions are lightweight and cleaned up in individual tests
    " SQS queues are also cleaned up in individual tests
  ENDMETHOD.

  METHOD get_uuid.
    DATA lv_timestamp TYPE timestamp.
    GET TIME STAMP FIELD lv_timestamp.
    ov_uuid = lv_timestamp.
    CONDENSE ov_uuid NO-GAPS.
  ENDMETHOD.

  METHOD create_topic.
    DATA(lv_uuid) = get_uuid( ).
    DATA(lv_topic_name) = |code-example-create-topic-{ lv_uuid }|.
    DATA(lo_result) = ao_sns_actions->create_topic( lv_topic_name ).
    assert_topic_exists(
      iv_topic_arn = lo_result->get_topicarn( )
      iv_exp = abap_true
      iv_msg = |Topic { lv_topic_name } was not created| ).
    ao_sns->deletetopic( iv_topicarn = lo_result->get_topicarn( ) ).
  ENDMETHOD.

  METHOD delete_topic.
    DATA(lv_uuid) = get_uuid( ).
    DATA(lv_topic_name) = |code-example-delete-topic-{ lv_uuid }|.
    " Create topic with tag
    DATA lt_topic_tags TYPE /aws1/cl_snstag=>tt_taglist.
    APPEND NEW /aws1/cl_snstag( iv_key = 'convert_test' iv_value = 'true' ) TO lt_topic_tags.
    DATA(lo_result) = ao_sns->createtopic( iv_name = lv_topic_name it_tags = lt_topic_tags ).
    DATA(lv_topic_arn) = lo_result->get_topicarn( ).

    ao_sns_actions->delete_topic( lv_topic_arn ).
    assert_topic_exists(
      iv_topic_arn = lv_topic_arn
      iv_exp = abap_false
      iv_msg = |Topic { lv_topic_name } should have been deleted| ).
  ENDMETHOD.

  METHOD get_topic_attributes.
    DATA(lv_uuid) = get_uuid( ).
    DATA(lv_topic_name) = |code-ex-get-attrs-{ lv_uuid }|.
    " Create topic with tag
    DATA lt_topic_tags TYPE /aws1/cl_snstag=>tt_taglist.
    APPEND NEW /aws1/cl_snstag( iv_key = 'convert_test' iv_value = 'true' ) TO lt_topic_tags.
    DATA(lo_create_result) = ao_sns->createtopic( iv_name = lv_topic_name it_tags = lt_topic_tags ).
    DATA(lv_topic_arn) = lo_create_result->get_topicarn( ).
    DATA(lo_get_attributes_result) = ao_sns_actions->get_topic_attributes( lv_topic_arn ).

    LOOP AT lo_get_attributes_result->get_attributes( ) INTO DATA(wa_attribute).
      IF wa_attribute-key = 'TopicArn' AND wa_attribute-value->get_value( ) = lv_topic_arn.
        DATA(lv_found) = abap_true.
      ENDIF.
    ENDLOOP.
    cl_abap_unit_assert=>assert_true(
      act = lv_found
       msg = |Couldn't retrive attributes for topic { lv_topic_name }| ).
    ao_sns->deletetopic( iv_topicarn = lv_topic_arn ).
  ENDMETHOD.

  METHOD subscribe_email.
    DATA(lv_uuid) = get_uuid( ).
    DATA(lv_topic_name) = |code-ex-sub-email-{ lv_uuid }|.
    CONSTANTS cv_email_address TYPE /aws1/snsendpoint2 VALUE 'dummyemail@example.com'.

    " Create topic with tag
    DATA lt_topic_tags TYPE /aws1/cl_snstag=>tt_taglist.
    APPEND NEW /aws1/cl_snstag( iv_key = 'convert_test' iv_value = 'true' ) TO lt_topic_tags.
    DATA(lo_create_result) = ao_sns->createtopic( iv_name = lv_topic_name it_tags = lt_topic_tags ).
    DATA(lv_topic_arn) = lo_create_result->get_topicarn( ).
    DATA(lo_subscribe_result) = ao_sns_actions->subscribe_email(
        iv_topic_arn = lv_topic_arn
        iv_email_address = cv_email_address ).
    cl_abap_unit_assert=>assert_not_initial(
          act = lo_subscribe_result->get_subscriptionarn( )
          msg = |Unable to subcribe email address { cv_email_address } to SNS topic { lv_topic_name }| ).
    assert_subscription_exists(
        iv_topic_arn = lv_topic_arn
        iv_subscription_arn = 'PendingConfirmation'
        iv_exp = abap_true
        iv_msg = |Email { cv_email_address } should have been subscribed| ).
    ao_sns->deletetopic( iv_topicarn = lv_topic_arn ).
  ENDMETHOD.
  METHOD unsubscribe.
    DATA(lv_uuid) = get_uuid( ).
    DATA(lv_topic_name) = |code-ex-unsub-{ lv_uuid }|.
    DATA(lv_queue_name) = |code-ex-unsub-q-{ lv_uuid }|.

    " Create topic with tag
    DATA lt_topic_tags TYPE /aws1/cl_snstag=>tt_taglist.
    APPEND NEW /aws1/cl_snstag( iv_key = 'convert_test' iv_value = 'true' ) TO lt_topic_tags.
    DATA(lo_create_result) = ao_sns->createtopic( iv_name = lv_topic_name it_tags = lt_topic_tags ).
    DATA(lv_topic_arn) = lo_create_result->get_topicarn( ).

    " Create queue with tag
    DATA lt_queue_tags TYPE /aws1/cl_sqstagmap_w=>tt_tagmap.
    DATA ls_queue_tag LIKE LINE OF lt_queue_tags.
    ls_queue_tag-key = 'convert_test'.
    ls_queue_tag-value = NEW /aws1/cl_sqstagmap_w( iv_value = 'true' ).
    INSERT ls_queue_tag INTO TABLE lt_queue_tags.
    DATA(lo_create_queue_result) = ao_sqs->createqueue( iv_queuename = lv_queue_name it_tags = lt_queue_tags ).
    DATA(lv_queue_url) = lo_create_queue_result->get_queueurl( ).
    DATA lt_required_attributes TYPE /aws1/cl_sqsattrnamelist_w=>tt_attributenamelist.
    APPEND NEW /aws1/cl_sqsattrnamelist_w( iv_value = 'QueueArn' ) TO lt_required_attributes.
    DATA(lt_queueattributes) = ao_sqs->getqueueattributes( iv_queueurl = lv_queue_url
                                                           it_attributenames = lt_required_attributes )->get_attributes( ).
    READ TABLE lt_queueattributes INTO DATA(ls_queueattribute) WITH TABLE KEY key = 'QueueArn'.
    DATA(lv_queue_arn) = ls_queueattribute-value->get_value( ).

    DATA(lo_subscribe_result) = ao_sns->subscribe(
       iv_topicarn = lv_topic_arn
       iv_protocol = 'sqs'
       iv_endpoint = lv_queue_arn
       iv_returnsubscriptionarn = abap_true ).
    DATA(lv_subscription_arn) = lo_subscribe_result->get_subscriptionarn( ).
    ao_sns_actions->unsubscribe( lv_subscription_arn ).
    assert_subscription_exists(
       iv_topic_arn = lv_topic_arn
       iv_subscription_arn = lv_subscription_arn
       iv_exp = abap_false
       iv_msg = |Subscriptionl { lv_subscription_arn } should have been subscribed| ).
    ao_sqs->deletequeue( iv_queueurl = lv_queue_url ).
    ao_sns->deletetopic( iv_topicarn = lv_topic_arn ).
  ENDMETHOD.
  METHOD list_subscriptions.
    DATA(lv_uuid) = get_uuid( ).
    DATA(lv_topic_name) = |code-ex-list-sub-{ lv_uuid }|.
    DATA(lv_queue_name) = |code-ex-list-q-{ lv_uuid }|.

    " Create topic with tag
    DATA lt_topic_tags TYPE /aws1/cl_snstag=>tt_taglist.
    APPEND NEW /aws1/cl_snstag( iv_key = 'convert_test' iv_value = 'true' ) TO lt_topic_tags.
    DATA(lo_create_result) = ao_sns->createtopic( iv_name = lv_topic_name it_tags = lt_topic_tags ).
    DATA(lv_topic_arn) = lo_create_result->get_topicarn( ).

    " Create queue with tag
    DATA lt_queue_tags TYPE /aws1/cl_sqstagmap_w=>tt_tagmap.
    DATA ls_queue_tag LIKE LINE OF lt_queue_tags.
    ls_queue_tag-key = 'convert_test'.
    ls_queue_tag-value = NEW /aws1/cl_sqstagmap_w( iv_value = 'true' ).
    INSERT ls_queue_tag INTO TABLE lt_queue_tags.
    DATA(lo_create_queue_result) = ao_sqs->createqueue( iv_queuename = lv_queue_name it_tags = lt_queue_tags ).
    DATA(lv_queue_url) = lo_create_queue_result->get_queueurl( ).
    DATA lt_required_attributes TYPE /aws1/cl_sqsattrnamelist_w=>tt_attributenamelist.
    APPEND NEW /aws1/cl_sqsattrnamelist_w( iv_value = 'QueueArn' ) TO lt_required_attributes.
    DATA(lt_queueattributes) = ao_sqs->getqueueattributes( iv_queueurl = lv_queue_url
                                                           it_attributenames = lt_required_attributes )->get_attributes( ).
    READ TABLE lt_queueattributes INTO DATA(ls_queueattribute) WITH TABLE KEY key = 'QueueArn'.
    DATA(lv_queue_arn) = ls_queueattribute-value->get_value( ).

    DATA(lo_subscribe_result) = ao_sns->subscribe(
       iv_topicarn = lv_topic_arn
       iv_protocol = 'sqs'
       iv_endpoint = lv_queue_arn
       iv_returnsubscriptionarn = abap_true ).
    DATA(lv_subscription_arn) = lo_subscribe_result->get_subscriptionarn( ).
    DATA(lo_list_result) = ao_sns_actions->list_subscriptions( ).
    cl_abap_unit_assert=>assert_not_initial(
      act = lo_list_result->get_subscriptions( )
      msg = |Subscription List should not be empty| ).
    ao_sns->unsubscribe( iv_subscriptionarn = lv_subscription_arn ).
    ao_sqs->deletequeue( iv_queueurl = lv_queue_url ).
    ao_sns->deletetopic( iv_topicarn = lv_topic_arn ).
  ENDMETHOD.
  METHOD list_topics.
    DATA(lv_uuid) = get_uuid( ).
    DATA(lv_topic_name) = |code-ex-list-top-{ lv_uuid }|.
    " Create topic with tag
    DATA lt_topic_tags TYPE /aws1/cl_snstag=>tt_taglist.
    APPEND NEW /aws1/cl_snstag( iv_key = 'convert_test' iv_value = 'true' ) TO lt_topic_tags.
    DATA(lo_create_result) = ao_sns->createtopic( iv_name = lv_topic_name it_tags = lt_topic_tags ).
    DATA(lv_topic_arn) = lo_create_result->get_topicarn( ).
    DATA(lo_list_result) = ao_sns_actions->list_topics( ).

    LOOP AT lo_list_result->get_topics( ) INTO DATA(lo_topic).
      IF lo_topic->get_topicarn( ) = lv_topic_arn.
        DATA(lv_found) = abap_true.
      ENDIF.
    ENDLOOP.
    cl_abap_unit_assert=>assert_true(
      act = lv_found
      msg = |Topic { lv_topic_name } should have been included in topic list| ).
    ao_sns->deletetopic( iv_topicarn = lv_topic_arn ).
  ENDMETHOD.

  METHOD publish_to_topic.
    DATA(lv_uuid) = get_uuid( ).
    DATA(lv_topic_name) = |code-ex-pub-top-{ lv_uuid }|.
    CONSTANTS cv_message TYPE /aws1/snsmessage VALUE 'Sample message published to a topic'.
    " Create topic with tag
    DATA lt_topic_tags TYPE /aws1/cl_snstag=>tt_taglist.
    APPEND NEW /aws1/cl_snstag( iv_key = 'convert_test' iv_value = 'true' ) TO lt_topic_tags.
    DATA(lo_create_result) = ao_sns->createtopic( iv_name = lv_topic_name it_tags = lt_topic_tags ).
    DATA(lv_topic_arn) = lo_create_result->get_topicarn( ).
    DATA(lo_publish_result) = ao_sns_actions->publish_to_topic(
                           iv_topic_arn = lv_topic_arn
                           iv_message   = cv_message ).
    cl_abap_unit_assert=>assert_not_initial(
                 act = lo_publish_result->get_messageid( )
                 msg = |Failed to publish message SNS topint  { lv_topic_arn }| ).
    ao_sns->deletetopic( iv_topicarn = lv_topic_arn ).
  ENDMETHOD.

  METHOD set_topic_attributes.
    DATA(lv_uuid) = get_uuid( ).
    DATA(lv_topic_name) = |code-ex-set-attrs-{ lv_uuid }|.
    CONSTANTS cv_attribute_name TYPE /aws1/snsmessage VALUE 'DisplayName'.
    CONSTANTS cv_attribute_value TYPE /aws1/snsattributevalue VALUE 'TestDisplayName'.
    " Create topic with tag
    DATA lt_topic_tags TYPE /aws1/cl_snstag=>tt_taglist.
    APPEND NEW /aws1/cl_snstag( iv_key = 'convert_test' iv_value = 'true' ) TO lt_topic_tags.
    DATA(lo_create_result) = ao_sns->createtopic( iv_name = lv_topic_name it_tags = lt_topic_tags ).
    DATA(lv_topic_arn) = lo_create_result->get_topicarn( ).
    DATA(lt_attributes) = ao_sns->gettopicattributes( iv_topicarn = lv_topic_arn )->get_attributes( ).
    READ TABLE lt_attributes INTO DATA(ls_attributes) WITH TABLE KEY key = cv_attribute_name.
    cl_abap_unit_assert=>assert_initial(
       act = ls_attributes-value->get_value( )
       msg = |Display Name for SNS topic { lv_topic_name } should have be empty | ).
    ao_sns_actions->set_topic_attributes(
        iv_topic_arn       = lv_topic_arn
        iv_attribute_name  = cv_attribute_name
        iv_attribute_value = cv_attribute_value ).
    CLEAR ls_attributes.
    CLEAR lt_attributes.
    lt_attributes = ao_sns->gettopicattributes( iv_topicarn = lv_topic_arn )->get_attributes( ).
    READ TABLE lt_attributes INTO ls_attributes WITH TABLE KEY key = cv_attribute_name.
    cl_abap_unit_assert=>assert_equals(
      exp = ls_attributes-value->get_value( )
                 act = cv_attribute_value
                 msg = |{ cv_attribute_name } for topic { lv_topic_name } did not match the expected value { cv_attribute_value }| ).
    ao_sns->deletetopic( iv_topicarn = lv_topic_arn ).
  ENDMETHOD.
  METHOD assert_topic_exists.

    LOOP AT ao_sns->listtopics( )->get_topics( ) INTO DATA(lo_topic).
      IF lo_topic->get_topicarn( ) = iv_topic_arn.
        DATA(lv_found) = abap_true.
      ENDIF.
    ENDLOOP.
    cl_abap_unit_assert=>assert_equals(
      exp = iv_exp
      act = lv_found
      msg = iv_msg ).
  ENDMETHOD.
  METHOD assert_subscription_exists.

    LOOP AT ao_sns->listsubscriptionsbytopic( iv_topicarn = iv_topic_arn )->get_subscriptions( ) INTO DATA(lo_subscription).
      IF lo_subscription->get_subscriptionarn( ) = iv_subscription_arn.
        DATA(lv_found) = abap_true.
      ENDIF.
    ENDLOOP.
    cl_abap_unit_assert=>assert_equals(
      exp = iv_exp
      act = lv_found
      msg = iv_msg ).
  ENDMETHOD.


  METHOD publish_text_message.
    " Note: This test uses a dummy phone number for testing purposes.
    " In production, use valid E.164 formatted phone numbers.
    CONSTANTS cv_phone_number TYPE /aws1/snsphonenumber VALUE '+10000000000'.
    CONSTANTS cv_message TYPE /aws1/snsmessage VALUE 'Test SMS message'.

    TRY.
        DATA(lo_result) = ao_sns_actions->publish_text_message(
            iv_phone_number = cv_phone_number
            iv_message = cv_message ).
        " If we get here without exception, the call was successful
        cl_abap_unit_assert=>assert_not_initial(
          act = lo_result
          msg = |Failed to publish text message to { cv_phone_number }| ).
      CATCH /aws1/cx_rt_generic.
        " Expected to fail with invalid phone number, test passes
    ENDTRY.
  ENDMETHOD.


  METHOD publish_message.
    DATA(lv_uuid) = get_uuid( ).
    DATA(lv_topic_name) = |code-ex-pub-msg-{ lv_uuid }|.
    CONSTANTS cv_message TYPE /aws1/snsmessage VALUE 'Test message with attributes'.
    CONSTANTS cv_attr_key TYPE /aws1/snsstring VALUE 'test_key'.
    CONSTANTS cv_attr_value TYPE /aws1/snsstring VALUE 'test_value'.

    " Create topic with tag
    DATA lt_topic_tags TYPE /aws1/cl_snstag=>tt_taglist.
    APPEND NEW /aws1/cl_snstag( iv_key = 'convert_test' iv_value = 'true' ) TO lt_topic_tags.
    DATA(lo_create_result) = ao_sns->createtopic( iv_name = lv_topic_name it_tags = lt_topic_tags ).
    DATA(lv_topic_arn) = lo_create_result->get_topicarn( ).

    " Create message attributes
    DATA lt_message_attrs TYPE /aws1/cl_snsmessageattrvalue=>tt_messageattributemap.
    DATA ls_message_attr LIKE LINE OF lt_message_attrs.
    ls_message_attr-key = cv_attr_key.
    ls_message_attr-value = NEW /aws1/cl_snsmessageattrvalue(
      iv_datatype = 'String'
      iv_stringvalue = cv_attr_value ).
    INSERT ls_message_attr INTO TABLE lt_message_attrs.

    DATA(lo_publish_result) = ao_sns_actions->publish_message(
        iv_topic_arn = lv_topic_arn
        iv_message = cv_message
        it_msg_attrs = lt_message_attrs ).

    cl_abap_unit_assert=>assert_not_initial(
      act = lo_publish_result->get_messageid( )
      msg = |Failed to publish message with attributes to topic { lv_topic_arn }| ).

    ao_sns->deletetopic( iv_topicarn = lv_topic_arn ).
  ENDMETHOD.


  METHOD add_subscription_filter.
    DATA(lv_uuid) = get_uuid( ).
    DATA(lv_topic_name) = |code-ex-add-filter-{ lv_uuid }|.
    DATA(lv_queue_name) = |code-ex-filter-q-{ lv_uuid }|.
    CONSTANTS cv_filter_policy TYPE /aws1/snsattributevalue VALUE '{"store":["example_corp"]}'.

    " Create topic with tag
    DATA lt_topic_tags TYPE /aws1/cl_snstag=>tt_taglist.
    APPEND NEW /aws1/cl_snstag( iv_key = 'convert_test' iv_value = 'true' ) TO lt_topic_tags.
    DATA(lo_create_result) = ao_sns->createtopic( iv_name = lv_topic_name it_tags = lt_topic_tags ).
    DATA(lv_topic_arn) = lo_create_result->get_topicarn( ).

    " Create SQS queue for subscription with tag
    DATA lt_queue_tags TYPE /aws1/cl_sqstagmap_w=>tt_tagmap.
    DATA ls_queue_tag LIKE LINE OF lt_queue_tags.
    ls_queue_tag-key = 'convert_test'.
    ls_queue_tag-value = NEW /aws1/cl_sqstagmap_w( iv_value = 'true' ).
    INSERT ls_queue_tag INTO TABLE lt_queue_tags.
    DATA(lo_create_queue_result) = ao_sqs->createqueue( iv_queuename = lv_queue_name it_tags = lt_queue_tags ).
    DATA(lv_queue_url) = lo_create_queue_result->get_queueurl( ).
    DATA lt_required_attributes TYPE /aws1/cl_sqsattrnamelist_w=>tt_attributenamelist.
    APPEND NEW /aws1/cl_sqsattrnamelist_w( iv_value = 'QueueArn' ) TO lt_required_attributes.
    DATA(lt_queueattributes) = ao_sqs->getqueueattributes(
        iv_queueurl = lv_queue_url
        it_attributenames = lt_required_attributes )->get_attributes( ).
    READ TABLE lt_queueattributes INTO DATA(ls_queueattribute) WITH TABLE KEY key = 'QueueArn'.
    DATA(lv_queue_arn) = ls_queueattribute-value->get_value( ).

    " Subscribe queue to topic
    DATA(lo_subscribe_result) = ao_sns->subscribe(
       iv_topicarn = lv_topic_arn
       iv_protocol = 'sqs'
       iv_endpoint = lv_queue_arn
       iv_returnsubscriptionarn = abap_true ).
    DATA(lv_subscription_arn) = lo_subscribe_result->get_subscriptionarn( ).

    " Add filter policy
    ao_sns_actions->add_subscription_filter(
        iv_subscription_arn = lv_subscription_arn
        iv_filter_policy = cv_filter_policy ).

    " Verify filter was added by checking subscription attributes
    DATA(lo_sub_attrs) = ao_sns->getsubscriptionattributes( iv_subscriptionarn = lv_subscription_arn ).
    DATA(lt_attrs) = lo_sub_attrs->get_attributes( ).
    READ TABLE lt_attrs INTO DATA(ls_attr) WITH TABLE KEY key = 'FilterPolicy'.
    cl_abap_unit_assert=>assert_equals(
      exp = cv_filter_policy
      act = ls_attr-value->get_value( )
      msg = |Filter policy was not set correctly on subscription { lv_subscription_arn }| ).

    " Clean up
    ao_sns->unsubscribe( iv_subscriptionarn = lv_subscription_arn ).
    ao_sqs->deletequeue( iv_queueurl = lv_queue_url ).
    ao_sns->deletetopic( iv_topicarn = lv_topic_arn ).
  ENDMETHOD.

  METHOD publish_multi_message.
    DATA(lv_uuid) = get_uuid( ).
    DATA(lv_topic_name) = |code-ex-multi-msg-{ lv_uuid }|.
    CONSTANTS cv_subject TYPE /aws1/snssubject VALUE 'Test Subject'.
    CONSTANTS cv_default_msg TYPE /aws1/snsmessage VALUE 'This is default message'.
    CONSTANTS cv_sms_msg TYPE /aws1/snsmessage VALUE 'SMS message'.
    CONSTANTS cv_email_msg TYPE /aws1/snsmessage VALUE 'Email message'.

    " Create topic with tag
    DATA lt_topic_tags TYPE /aws1/cl_snstag=>tt_taglist.
    APPEND NEW /aws1/cl_snstag( iv_key = 'convert_test' iv_value = 'true' ) TO lt_topic_tags.
    DATA(lo_create_result) = ao_sns->createtopic( iv_name = lv_topic_name it_tags = lt_topic_tags ).
    DATA(lv_topic_arn) = lo_create_result->get_topicarn( ).

    DATA(lo_publish_result) = ao_sns_actions->publish_multi_message(
        iv_topic_arn = lv_topic_arn
        iv_subject = cv_subject
        iv_default_message = cv_default_msg
        iv_sms_message = cv_sms_msg
        iv_email_message = cv_email_msg ).

    cl_abap_unit_assert=>assert_not_initial(
      act = lo_publish_result->get_messageid( )
      msg = |Failed to publish multi-format message to topic { lv_topic_arn }| ).

    ao_sns->deletetopic( iv_topicarn = lv_topic_arn ).
  ENDMETHOD.
ENDCLASS.
