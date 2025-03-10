" Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
" SPDX-License-Identifier: Apache-2.0

CLASS zcl_aws1_sns_actions DEFINITION
  PUBLIC
  FINAL
  CREATE PUBLIC .

  PUBLIC SECTION.

    METHODS create_topic
      IMPORTING
      !iv_topic_name TYPE /aws1/snstopicname
      RETURNING
      VALUE(oo_result) TYPE REF TO /aws1/cl_snscreatetopicrsp .
    METHODS delete_topic
      IMPORTING
      !iv_topic_arn TYPE /aws1/snstopicarn .
    METHODS get_topic_attributes
      IMPORTING
      !iv_topic_arn TYPE /aws1/snstopicarn
      RETURNING
      VALUE(oo_result) TYPE REF TO /aws1/cl_snsgettopicattrsrsp .
    METHODS subscribe_email
      IMPORTING
      !iv_topic_arn TYPE /aws1/snstopicarn
      !iv_email_address TYPE /aws1/snsendpoint2
      RETURNING
      VALUE(oo_result) TYPE REF TO /aws1/cl_snssubscriberesponse .
    METHODS unsubscribe
      IMPORTING
      !iv_subscription_arn TYPE /aws1/snssubscriptionarn .
    METHODS list_subscriptions
      RETURNING
      VALUE(oo_result) TYPE REF TO /aws1/cl_snslstsubscriptions01 .
    METHODS list_topics
      RETURNING
      VALUE(oo_result) TYPE REF TO /aws1/cl_snslisttopicsresponse .
    METHODS publish_to_topic
      IMPORTING
      !iv_topic_arn TYPE /aws1/snsstring
      !iv_message TYPE /aws1/snsmessage
      RETURNING
      VALUE(oo_result) TYPE REF TO /aws1/cl_snspublishresponse .
    METHODS set_topic_attributes
      IMPORTING
      !iv_topic_arn TYPE /aws1/snstopicarn
      !iv_attribute_name TYPE /aws1/snsattributename
      !iv_attribute_value TYPE /aws1/snsattributevalue .
  PROTECTED SECTION.
  PRIVATE SECTION.
ENDCLASS.



CLASS ZCL_AWS1_SNS_ACTIONS IMPLEMENTATION.


  METHOD create_topic.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_sns) = /aws1/cl_sns_factory=>create( lo_session ).

    " snippet-start:[sns.abapv1.create_topic]
    TRY.
        oo_result = lo_sns->createtopic( iv_name = iv_topic_name ). " oo_result is returned for testing purposes. "
        MESSAGE 'SNS topic created' TYPE 'I'.
      CATCH /aws1/cx_snstopiclimitexcdex.
        MESSAGE 'Unable to create more topics. You have reached the maximum number of topics allowed.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[sns.abapv1.create_topic]
  ENDMETHOD.


  METHOD delete_topic.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_sns) = /aws1/cl_sns_factory=>create( lo_session ).

    " snippet-start:[sns.abapv1.delete_topic]
    TRY.
        lo_sns->deletetopic( iv_topicarn = iv_topic_arn ).
        MESSAGE 'SNS topic deleted.' TYPE 'I'.
      CATCH /aws1/cx_snsnotfoundexception.
        MESSAGE 'Topic does not exist.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[sns.abapv1.delete_topic]
  ENDMETHOD.


  METHOD get_topic_attributes.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_sns) = /aws1/cl_sns_factory=>create( lo_session ).

    " snippet-start:[sns.abapv1.get_topic_attributes]
    TRY.
        oo_result = lo_sns->gettopicattributes( iv_topicarn = iv_topic_arn ). " oo_result is returned for testing purposes. "
        DATA(lt_attributes) = oo_result->get_attributes( ).
        MESSAGE 'Retrieved attributes/properties of a topic.' TYPE 'I'.
      CATCH /aws1/cx_snsnotfoundexception.
        MESSAGE 'Topic does not exist.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[sns.abapv1.get_topic_attributes]
  ENDMETHOD.


  METHOD list_subscriptions.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_sns) = /aws1/cl_sns_factory=>create( lo_session ).

    " snippet-start:[sns.abapv1.list_subscriptions]
    TRY.
        oo_result = lo_sns->listsubscriptions( ).                " oo_result is returned for testing purposes. "
        DATA(lt_subscriptions) = oo_result->get_subscriptions( ).
        MESSAGE 'Retrieved list of subscribers.' TYPE 'I'.
      CATCH /aws1/cx_rt_generic.
        MESSAGE 'Unable to list subscribers.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[sns.abapv1.list_subscriptions]
  ENDMETHOD.


  METHOD list_topics.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_sns) = /aws1/cl_sns_factory=>create( lo_session ).

    " snippet-start:[sns.abapv1.list_topics]
    TRY.
        oo_result = lo_sns->listtopics( ).            " oo_result is returned for testing purposes. "
        DATA(lt_topics) = oo_result->get_topics( ).
        MESSAGE 'Retrieved list of topics.' TYPE 'I'.
      CATCH /aws1/cx_rt_generic.
        MESSAGE 'Unable to list topics.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[sns.abapv1.list_topics]
  ENDMETHOD.


  METHOD publish_to_topic.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_sns) = /aws1/cl_sns_factory=>create( lo_session ).

    " snippet-start:[sns.abapv1.publish_to_topic].
    TRY.
        oo_result = lo_sns->publish(              " oo_result is returned for testing purposes. "
          iv_topicarn = iv_topic_arn
          iv_message = iv_message ).
        MESSAGE 'Message published to SNS topic.' TYPE 'I'.
      CATCH /aws1/cx_snsnotfoundexception.
        MESSAGE 'Topic does not exist.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[sns.abapv1.publish_to_topic].
  ENDMETHOD.


  METHOD set_topic_attributes.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_sns) = /aws1/cl_sns_factory=>create( lo_session ).

    " snippet-start:[sns.abapv1.set_topic_attributes]
    TRY.
        lo_sns->settopicattributes(
            iv_topicarn = iv_topic_arn
            iv_attributename  = iv_attribute_name
            iv_attributevalue = iv_attribute_value ).
        MESSAGE 'Set/updated SNS topic attributes.' TYPE 'I'.
      CATCH /aws1/cx_snsnotfoundexception.
        MESSAGE 'Topic does not exist.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[sns.abapv1.set_topic_attributes]
  ENDMETHOD.


  METHOD subscribe_email.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_sns) = /aws1/cl_sns_factory=>create( lo_session ).

    " snippet-start:[sns.abapv1.subscribe_email]
    TRY.
        oo_result = lo_sns->subscribe(                      "oo_result is returned for testing purposes."
                iv_topicarn = iv_topic_arn
                iv_protocol = 'email'
                iv_endpoint = iv_email_address
                iv_returnsubscriptionarn = abap_true ).
        MESSAGE 'Email address subscribed to SNS topic.' TYPE 'I'.
      CATCH /aws1/cx_snsnotfoundexception.
        MESSAGE 'Topic does not exist.' TYPE 'E'.
      CATCH /aws1/cx_snssubscriptionlmte00.
        MESSAGE 'Unable to create subscriptions. You have reached the maximum number of subscriptions allowed.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[sns.abapv1.subscribe_email]
  ENDMETHOD.


  METHOD unsubscribe.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_sns) = /aws1/cl_sns_factory=>create( lo_session ).

    " snippet-start:[sns.abapv1.unsubscribe]
    TRY.
        lo_sns->unsubscribe( iv_subscriptionarn = iv_subscription_arn ).
        MESSAGE 'Subscription deleted.' TYPE 'I'.
      CATCH /aws1/cx_snsnotfoundexception.
        MESSAGE 'Subscription does not exist.' TYPE 'E'.
      CATCH /aws1/cx_snsinvalidparameterex.
        MESSAGE 'Subscription with "PendingConfirmation" status cannot be deleted/unsubscribed. Confirm subscription before performing unsubscribe operation.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[sns.abapv1.unsubscribe]
  ENDMETHOD.
ENDCLASS.
