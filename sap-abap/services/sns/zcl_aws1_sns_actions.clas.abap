" """"""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""
" "  Copyright 2022 Amazon.com, Inc. or its affiliates. All Rights
" "  Reserved.
" "  SPDX-License-Identifier: MIT-0
" """"""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""

class ZCL_AWS1_SNS_ACTIONS definition
  public
  final
  create public .

public section.

  methods CREATE_TOPIC
    importing
      !IV_TOPIC_NAME type /AWS1/SNSTOPICNAME
    returning
      value(OO_RESULT) type ref to /AWS1/CL_SNSCREATETOPICRSP .
  methods DELETE_TOPIC
    importing
      !IV_TOPIC_ARN type /AWS1/SNSTOPICARN .
  methods GET_TOPIC_ATTRIBUTES
    importing
      !IV_TOPIC_ARN type /AWS1/SNSTOPICARN
    returning
      value(OO_RESULT) type ref to /AWS1/CL_SNSGETTOPICATTRSRSP .
  methods SUBSCRIBE_EMAIL
    importing
      !IV_TOPIC_ARN type /AWS1/SNSTOPICARN
      !IV_EMAIL_ADDRESS type /AWS1/SNSENDPOINT2
    returning
      value(OO_RESULT) type ref to /AWS1/CL_SNSSUBSCRIBERESPONSE .
  methods UNSUBSCRIBE
    importing
      !IV_SUBSCRIPTION_ARN type /AWS1/SNSSUBSCRIPTIONARN .
  methods LIST_SUBSCRIPTIONS
    returning
      value(OO_RESULT) type ref to /AWS1/CL_SNSLSTSUBSCRIPTIONS01 .
  methods LIST_TOPICS
    returning
      value(OO_RESULT) type ref to /AWS1/CL_SNSLISTTOPICSRESPONSE .
  methods PUBLISH_TO_TOPIC
    importing
      !IV_TOPIC_ARN type /AWS1/SNSSTRING
      !IV_MESSAGE type /AWS1/SNSMESSAGE
    returning
      value(OO_RESULT) type ref to /AWS1/CL_SNSPUBLISHRESPONSE .
  methods SET_TOPIC_ATTRIBUTES
    importing
      !IV_TOPIC_ARN type /AWS1/SNSTOPICARN
      !IV_ATTRIBUTE_NAME type /AWS1/SNSATTRIBUTENAME
      !IV_ATTRIBUTE_VALUE type /AWS1/SNSATTRIBUTEVALUE .
protected section.
private section.
ENDCLASS.



CLASS ZCL_AWS1_SNS_ACTIONS IMPLEMENTATION.


  METHOD create_topic.
    CONSTANTS: cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_sns) = /aws1/cl_sns_factory=>create( lo_session ).

    " snippet-start:[sns.abapv1.create_topic]
    TRY.
        oo_result = lo_sns->createtopic( iv_name = iv_topic_name ). " oo_result is returned for testing purpose "
        MESSAGE 'SNS topic created' TYPE 'I'.
      CATCH /aws1/cx_snstopiclimitexcdex.
        MESSAGE 'Unable to create more topics as you have reached the maximum number of topics allowed.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[sns.abapv1.create_topic]
  ENDMETHOD.


  METHOD delete_topic.
    CONSTANTS: cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_sns) = /aws1/cl_sns_factory=>create( lo_session ).

    " snippet-start:[sns.abapv1.delete_topic]
    TRY.
        lo_sns->deletetopic( iv_topicarn = iv_topic_arn ).
        MESSAGE 'SNS topic deleted' TYPE 'I'.
      CATCH /aws1/cx_snsnotfoundexception.
        MESSAGE 'Topic does not exist' TYPE 'E'.
    ENDTRY.
    " snippet-end:[sns.abapv1.delete_topic]
  ENDMETHOD.


  METHOD get_topic_attributes.
    CONSTANTS: cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_sns) = /aws1/cl_sns_factory=>create( lo_session ).

    " snippet-start:[sns.abapv1.get_topic_attributes]
    TRY.
        oo_result = lo_sns->gettopicattributes( iv_topicarn = iv_topic_arn ). " oo_result is returned for testing purpose "
        DATA(lt_attributes) = oo_result->get_attributes( ).
        MESSAGE 'Retrieved attributes/properties of a topic' TYPE 'I'.
      CATCH /aws1/cx_snsnotfoundexception.
        MESSAGE 'topic does NOT exist' TYPE 'E'.
    ENDTRY.
    " snippet-end:[sns.abapv1.get_topic_attributes]
  ENDMETHOD.


  METHOD list_subscriptions.
    CONSTANTS: cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_sns) = /aws1/cl_sns_factory=>create( lo_session ).

    " snippet-start:[sns.abapv1.list_subscriptions]
    TRY.
        oo_result = lo_sns->listsubscriptions( ).                " oo_result is returned for testing purpose "
        DATA(lt_subscriptions) = oo_result->get_subscriptions( ).
        MESSAGE 'Retrieved list of subscriber(s)' TYPE 'I'.
      CATCH /aws1/cx_rt_generic.
        MESSAGE 'Unable to list subscriber(s)' TYPE 'E'.
    ENDTRY.
    " snippet-end:[sns.abapv1.list_subscriptions]
  ENDMETHOD.


  METHOD list_topics.
    CONSTANTS: cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_sns) = /aws1/cl_sns_factory=>create( lo_session ).

    " snippet-start:[sns.abapv1.list_topics]
    TRY.
        oo_result = lo_sns->listtopics( ).            " oo_result is returned for testing purpose "
        DATA(lt_topics) = oo_result->get_topics( ).
        MESSAGE 'Retrieved list of topic(s)' TYPE 'I'.
      CATCH /aws1/cx_rt_generic.
        MESSAGE 'Unable to list topic(s)' TYPE 'E'.
    ENDTRY.
    " snippet-end:[sns.abapv1.list_topics]
  ENDMETHOD.


  METHOD publish_to_topic.
    CONSTANTS: cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_sns) = /aws1/cl_sns_factory=>create( lo_session ).

    " snippet-start:[sns.abapv1.publish_to_topic].
    TRY.
        oo_result = lo_sns->publish(              " oo_result is returned for testing purpose "
          iv_topicarn = iv_topic_arn
          iv_message = iv_message
        ).
        MESSAGE 'Message published to SNS topic' TYPE 'I'.
      CATCH /aws1/cx_snsnotfoundexception.
        MESSAGE 'Topic does not exist' TYPE 'E'.
    ENDTRY.
    " snippet-end:[sns.abapv1.publish_to_topic].
  ENDMETHOD.


  METHOD set_topic_attributes.
    CONSTANTS: cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_sns) = /aws1/cl_sns_factory=>create( lo_session ).

    " snippet-start:[sns.abapv1.set_topic_attributes]
    TRY.
        lo_sns->settopicattributes(
            iv_topicarn = iv_topic_arn
            iv_attributename  = iv_attribute_name
            iv_attributevalue = iv_attribute_value
        ).
        MESSAGE 'Set/Updated SNS topic attributes' TYPE 'I'.
      CATCH /aws1/cx_snsnotfoundexception.
        MESSAGE 'Topic does not exist' TYPE 'E'.
    ENDTRY.
    " snippet-end:[sns.abapv1.set_topic_attributes]
  ENDMETHOD.


  METHOD subscribe_email.
    CONSTANTS: cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_sns) = /aws1/cl_sns_factory=>create( lo_session ).

    " snippet-start:[sns.abapv1.subscribe_email]
    TRY.
        oo_result = lo_sns->subscribe(                      "oo_result is returned for testing purpose"
                iv_topicarn = iv_topic_arn
                iv_protocol = 'email'
                iv_endpoint = iv_email_address
                iv_returnsubscriptionarn = abap_true
            ).
        MESSAGE 'Email address subscribed to SNS topic' TYPE 'I'.
      CATCH /aws1/cx_snsnotfoundexception.
        MESSAGE 'Topic does not exist' TYPE 'E'.
      CATCH /aws1/cx_snssubscriptionlmte00.
        MESSAGE 'Unable to create subscriptions, you have reached the maximum number of subscriptions allowed.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[sns.abapv1.subscribe_email]
  ENDMETHOD.


  METHOD unsubscribe.
    CONSTANTS: cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_sns) = /aws1/cl_sns_factory=>create( lo_session ).

    " snippet-start:[sns.abapv1.unsubscribe]
    TRY.
        lo_sns->unsubscribe( iv_subscriptionarn = iv_subscription_arn ).
        MESSAGE 'Subscription Deleted' TYPE 'I'.
      CATCH /aws1/cx_snsnotfoundexception.
        MESSAGE 'Subscription does not exist' TYPE 'E'.
      CATCH /aws1/cx_snsinvalidparameterex.
        MESSAGE 'Subscription with "PendingConfirmation" status cannot be deleted/unsubscribed, subscription should be confirmed before perform unsubscribe operation' TYPE 'E'.
    ENDTRY.
    " snippet-end:[sns.abapv1.unsubscribe]
  ENDMETHOD.
ENDCLASS.
