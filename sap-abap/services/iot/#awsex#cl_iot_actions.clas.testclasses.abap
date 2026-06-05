" Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
" SPDX-License-Identifier: Apache-2.0
CLASS ltc_awsex_cl_iot_actions DEFINITION DEFERRED.
CLASS /awsex/cl_iot_actions DEFINITION LOCAL FRIENDS ltc_awsex_cl_iot_actions.

CLASS ltc_awsex_cl_iot_actions DEFINITION FOR TESTING DURATION LONG RISK LEVEL DANGEROUS.

  PRIVATE SECTION.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    CLASS-DATA ao_iot         TYPE REF TO /aws1/if_iot.
    CLASS-DATA ao_iam         TYPE REF TO /aws1/if_iam.
    CLASS-DATA ao_sns         TYPE REF TO /aws1/if_sns.
    CLASS-DATA ao_session     TYPE REF TO /aws1/cl_rt_session_base.
    CLASS-DATA ao_iot_actions TYPE REF TO /awsex/cl_iot_actions.

    " ── Resources shared by non-destructive tests ────────────────────────
    " Shared IoT thing (used by list_things, attach/detach, list_certs, etc.)
    CLASS-DATA av_thing_name    TYPE /aws1/iotthingname.
    " Shared certificate attached to the shared thing
    CLASS-DATA av_cert_id       TYPE /aws1/iotcertificateid.
    CLASS-DATA av_cert_arn      TYPE /aws1/iotcertificatearn.
    " Shared SNS topic + IAM role for topic-rule tests
    CLASS-DATA av_sns_topic_arn TYPE /aws1/snstopicarn.
    CLASS-DATA av_iam_role_name TYPE /aws1/iamrolenametype.
    CLASS-DATA av_iam_role_arn  TYPE /aws1/iamarntype.
    " Shared topic rule (used by list_topic_rules)
    CLASS-DATA av_rule_name     TYPE /aws1/iotrulename.

    " ── Dedicated resources for destructive (delete) tests ───────────────
    CLASS-DATA av_del_thing_name TYPE /aws1/iotthingname.
    CLASS-DATA av_del_cert_id    TYPE /aws1/iotcertificateid.
    CLASS-DATA av_del_rule_name  TYPE /aws1/iotrulename.

    " ── Test methods ─────────────────────────────────────────────────────
    METHODS: create_thing              FOR TESTING RAISING /aws1/cx_rt_generic,
             list_things               FOR TESTING RAISING /aws1/cx_rt_generic,
             create_keys_and_cert      FOR TESTING RAISING /aws1/cx_rt_generic,
             attach_thing_principal    FOR TESTING RAISING /aws1/cx_rt_generic,
             describe_endpoint         FOR TESTING RAISING /aws1/cx_rt_generic,
             list_certificates         FOR TESTING RAISING /aws1/cx_rt_generic,
             detach_thing_principal    FOR TESTING RAISING /aws1/cx_rt_generic,
             delete_certificate        FOR TESTING RAISING /aws1/cx_rt_generic,
             create_topic_rule         FOR TESTING RAISING /aws1/cx_rt_generic,
             list_topic_rules          FOR TESTING RAISING /aws1/cx_rt_generic,
             update_indexing_conf      FOR TESTING RAISING /aws1/cx_rt_generic,
             search_index              FOR TESTING RAISING /aws1/cx_rt_generic,
             delete_thing              FOR TESTING RAISING /aws1/cx_rt_generic,
             delete_topic_rule         FOR TESTING RAISING /aws1/cx_rt_generic.

    CLASS-METHODS class_setup    RAISING /aws1/cx_rt_generic.
    CLASS-METHODS class_teardown RAISING /aws1/cx_rt_generic.

    CLASS-METHODS build_iot_tags
      RETURNING
        VALUE(rt_tags) TYPE /aws1/cl_iottag=>tt_taglist.

ENDCLASS.


CLASS ltc_awsex_cl_iot_actions IMPLEMENTATION.

  METHOD class_setup.

    " ── Session and clients ──────────────────────────────────────────────
    ao_session    = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    ao_iot        = /aws1/cl_iot_factory=>create( ao_session ).
    ao_iam        = /aws1/cl_iam_factory=>create( ao_session ).
    ao_sns        = /aws1/cl_sns_factory=>create( ao_session ).
    CREATE OBJECT ao_iot_actions.

    DATA lv_uuid TYPE string.
    lv_uuid = /awsex/cl_utils=>get_random_string( ).
    CONDENSE lv_uuid NO-GAPS.

    " ── Shared IoT thing ─────────────────────────────────────────────────
    av_thing_name = |sap-abap-iot-{ lv_uuid }|.
    DATA(lo_thing) = ao_iot->creatething( iv_thingname = av_thing_name ).
    cl_abap_unit_assert=>assert_bound(
      act = lo_thing
      msg = |class_setup: failed to create shared thing { av_thing_name }| ).

    " Note: IoT things do not support TagResource. Things are tracked by
    " naming convention (sap-abap-iot-*) for identification and cleanup.

    " ── Shared certificate ───────────────────────────────────────────────
    DATA(lo_cert) = ao_iot->createkeysandcertificate( iv_setasactive = abap_true ).
    cl_abap_unit_assert=>assert_bound(
      act = lo_cert
      msg = 'class_setup: failed to create shared certificate' ).
    av_cert_id  = lo_cert->get_certificateid( ).
    av_cert_arn = lo_cert->get_certificatearn( ).

    " Attach certificate to the shared thing so detach_thing_principal and
    " list_certificates tests have a known principal already in place.
    ao_iot->attachthingprincipal(
      iv_thingname = av_thing_name
      iv_principal = av_cert_arn ).

    " ── SNS topic for topic-rule tests ───────────────────────────────────
    DATA lt_sns_tags TYPE /aws1/cl_snstag=>tt_taglist.
    DATA lo_sns_tag  TYPE REF TO /aws1/cl_snstag.
    lo_sns_tag = NEW /aws1/cl_snstag( iv_key = 'convert_test' iv_value = 'true' ).
    APPEND lo_sns_tag TO lt_sns_tags.
    DATA(lo_sns_result) = ao_sns->createtopic(
      iv_name = |sap-abap-iot-sns-{ lv_uuid }|
      it_tags = lt_sns_tags ).
    av_sns_topic_arn = lo_sns_result->get_topicarn( ).
    cl_abap_unit_assert=>assert_not_initial(
      act = av_sns_topic_arn
      msg = 'class_setup: failed to create SNS topic' ).

    " ── IAM role that IoT rules use to publish to SNS ────────────────────
    " Trust policy: IoT service may assume this role.
    DATA(lv_trust) =
      `{"Version":"2012-10-17","Statement":[{"Effect":"Allow",` &&
      `"Principal":{"Service":"iot.amazonaws.com"},` &&
      `"Action":"sts:AssumeRole"}]}`.

    av_iam_role_name = |sap-abap-iot-role-{ lv_uuid }|.

    DATA lt_iam_tags TYPE /aws1/cl_iamtag=>tt_taglisttype.
    DATA lo_iam_tag  TYPE REF TO /aws1/cl_iamtag.
    lo_iam_tag = NEW /aws1/cl_iamtag( iv_key = 'convert_test' iv_value = 'true' ).
    APPEND lo_iam_tag TO lt_iam_tags.
    DATA(lo_role) = ao_iam->createrole(
      iv_rolename                = av_iam_role_name
      iv_assumerolepolicydocument = lv_trust
      it_tags = lt_iam_tags ).
    av_iam_role_arn = lo_role->get_role( )->get_arn( ).
    cl_abap_unit_assert=>assert_not_initial(
      act = av_iam_role_arn
      msg = 'class_setup: failed to create IAM role' ).

    " Inline policy: allow SNS Publish to any topic.
    DATA(lv_policy) =
      `{"Version":"2012-10-17","Statement":[{"Effect":"Allow",` &&
      `"Action":["sns:Publish"],"Resource":"*"}]}`.
    ao_iam->putrolepolicy(
      iv_rolename       = av_iam_role_name
      iv_policyname     = 'IoTSNSPublish'
      iv_policydocument = lv_policy ).

    " ── Shared topic rule (used by list_topic_rules test) ────────────────
    " IoT rule names allow only letters, numbers, and underscores.
    av_rule_name = |sap_abap_iot_rule_{ lv_uuid }|.
    REPLACE ALL OCCURRENCES OF '-' IN av_rule_name WITH '_'.

    DATA lt_actions TYPE /aws1/cl_iotaction=>tt_actionlist.
    DATA lo_sns_act TYPE REF TO /aws1/cl_iotsnsaction.
    DATA lo_action  TYPE REF TO /aws1/cl_iotaction.
    DATA lo_payload TYPE REF TO /aws1/cl_iottopicrulepayload.
    CREATE OBJECT lo_sns_act
      EXPORTING
        iv_targetarn = av_sns_topic_arn
        iv_rolearn   = av_iam_role_arn.
    CREATE OBJECT lo_action
      EXPORTING
        io_sns = lo_sns_act.
    APPEND lo_action TO lt_actions.
    CREATE OBJECT lo_payload
      EXPORTING
        iv_sql     = |SELECT * FROM 'test/sap/abap/shared'|
        it_actions = lt_actions.

    " Poll until IoT can assume the newly created IAM role (eventual consistency).
    " InvalidRequestException with 'unable to assume role' means the role is not
    " yet propagated — retry with backoff for up to 60 seconds.
    DATA lv_rule_created TYPE abap_bool VALUE abap_false.
    DATA lv_retries      TYPE i         VALUE 0.
    WHILE lv_retries < 12 AND lv_rule_created = abap_false.
      TRY.
          ao_iot->createtopicrule(
            iv_rulename         = av_rule_name
            io_topicrulepayload = lo_payload ).
          lv_rule_created = abap_true.
        CATCH /aws1/cx_iotinvalidrequestex.
          lv_retries = lv_retries + 1.
          WAIT UP TO 5 SECONDS.
        CATCH /aws1/cx_iotresrcalrdyexistsex.
          lv_rule_created = abap_true.
      ENDTRY.
    ENDWHILE.

    IF lv_rule_created = abap_false.
      cl_abap_unit_assert=>fail(
        msg = |class_setup: IAM role { av_iam_role_name } not assumable by IoT after 60s| ).
    ENDIF.

    " Derive the rule ARN so we can tag it.
    DATA(lv_acct)   = ao_session->get_account_id( ).
    DATA(lv_region) = ao_session->get_region( ).
    DATA(lv_rule_arn) = |arn:aws:iot:{ lv_region }:{ lv_acct }:rule/{ av_rule_name }|.

    ao_iot->tagresource(
      iv_resourcearn = lv_rule_arn
      it_tags        = build_iot_tags( ) ).

    " ── Dedicated thing for delete_thing test ────────────────────────────
    av_del_thing_name = |sap-abap-iot-del-{ lv_uuid }|.
    DATA(lo_del_thing) = ao_iot->creatething( iv_thingname = av_del_thing_name ).
    cl_abap_unit_assert=>assert_bound(
      act = lo_del_thing
      msg = |class_setup: failed to create dedicated delete-thing { av_del_thing_name }| ).
    " Note: IoT things do not support TagResource. Tracked by name convention.

    " ── Dedicated certificate for delete_certificate test ────────────────
    DATA(lo_del_cert) = ao_iot->createkeysandcertificate( iv_setasactive = abap_true ).
    cl_abap_unit_assert=>assert_bound(
      act = lo_del_cert
      msg = 'class_setup: failed to create dedicated delete-certificate' ).
    av_del_cert_id = lo_del_cert->get_certificateid( ).

    " ── Dedicated topic rule for delete_topic_rule test ──────────────────
    av_del_rule_name = |sap_abap_iot_del_{ lv_uuid }|.
    REPLACE ALL OCCURRENCES OF '-' IN av_del_rule_name WITH '_'.

    DATA lt_del_actions  TYPE /aws1/cl_iotaction=>tt_actionlist.
    DATA lo_del_sns_act  TYPE REF TO /aws1/cl_iotsnsaction.
    DATA lo_del_action   TYPE REF TO /aws1/cl_iotaction.
    DATA lo_del_payload  TYPE REF TO /aws1/cl_iottopicrulepayload.
    CREATE OBJECT lo_del_sns_act
      EXPORTING
        iv_targetarn = av_sns_topic_arn
        iv_rolearn   = av_iam_role_arn.
    CREATE OBJECT lo_del_action
      EXPORTING
        io_sns = lo_del_sns_act.
    APPEND lo_del_action TO lt_del_actions.
    CREATE OBJECT lo_del_payload
      EXPORTING
        iv_sql     = |SELECT * FROM 'test/sap/abap/del'|
        it_actions = lt_del_actions.

    " Same IAM propagation retry for the dedicated delete rule.
    DATA lv_del_rule_created TYPE abap_bool VALUE abap_false.
    DATA lv_del_retries      TYPE i         VALUE 0.
    WHILE lv_del_retries < 12 AND lv_del_rule_created = abap_false.
      TRY.
          ao_iot->createtopicrule(
            iv_rulename         = av_del_rule_name
            io_topicrulepayload = lo_del_payload ).
          lv_del_rule_created = abap_true.
        CATCH /aws1/cx_iotinvalidrequestex.
          lv_del_retries = lv_del_retries + 1.
          WAIT UP TO 5 SECONDS.
        CATCH /aws1/cx_iotresrcalrdyexistsex.
          lv_del_rule_created = abap_true.
      ENDTRY.
    ENDWHILE.

    IF lv_del_rule_created = abap_false.
      cl_abap_unit_assert=>fail(
        msg = |class_setup: del rule { av_del_rule_name } could not be created after 60s| ).
    ENDIF.

    DATA(lv_del_rule_arn) =
      |arn:aws:iot:{ lv_region }:{ lv_acct }:rule/{ av_del_rule_name }|.
    ao_iot->tagresource(
      iv_resourcearn = lv_del_rule_arn
      it_tags        = build_iot_tags( ) ).

  ENDMETHOD.


  METHOD class_teardown.

    " ── Shared thing: detach cert first, then delete ─────────────────────
    IF av_cert_arn IS NOT INITIAL AND av_thing_name IS NOT INITIAL.
      TRY.
          ao_iot->detachthingprincipal(
            iv_thingname = av_thing_name
            iv_principal = av_cert_arn ).
        CATCH /aws1/cx_rt_generic.
      ENDTRY.
    ENDIF.

    IF av_cert_id IS NOT INITIAL.
      TRY.
          ao_iot->updatecertificate(
            iv_certificateid = av_cert_id
            iv_newstatus     = 'INACTIVE' ).
          ao_iot->deletecertificate( iv_certificateid = av_cert_id ).
        CATCH /aws1/cx_rt_generic.
      ENDTRY.
    ENDIF.

    IF av_thing_name IS NOT INITIAL.
      TRY.
          ao_iot->deletething( iv_thingname = av_thing_name ).
        CATCH /aws1/cx_rt_generic.
      ENDTRY.
    ENDIF.

    " ── Shared topic rule ────────────────────────────────────────────────
    IF av_rule_name IS NOT INITIAL.
      TRY.
          ao_iot->deletetopicrule( iv_rulename = av_rule_name ).
        CATCH /aws1/cx_rt_generic.
      ENDTRY.
    ENDIF.

    " ── Dedicated delete-thing (may already be gone) ─────────────────────
    IF av_del_thing_name IS NOT INITIAL.
      TRY.
          ao_iot->deletething( iv_thingname = av_del_thing_name ).
        CATCH /aws1/cx_rt_generic.
      ENDTRY.
    ENDIF.

    " ── Dedicated delete-certificate (may already be gone) ───────────────
    IF av_del_cert_id IS NOT INITIAL.
      TRY.
          ao_iot->updatecertificate(
            iv_certificateid = av_del_cert_id
            iv_newstatus     = 'INACTIVE' ).
          ao_iot->deletecertificate( iv_certificateid = av_del_cert_id ).
        CATCH /aws1/cx_rt_generic.
      ENDTRY.
    ENDIF.

    " ── Dedicated delete-topic-rule (may already be gone) ────────────────
    IF av_del_rule_name IS NOT INITIAL.
      TRY.
          ao_iot->deletetopicrule( iv_rulename = av_del_rule_name ).
        CATCH /aws1/cx_rt_generic.
      ENDTRY.
    ENDIF.

    " ── IAM role: remove inline policy then delete role ──────────────────
    IF av_iam_role_name IS NOT INITIAL.
      TRY.
          ao_iam->deleterolepolicy(
            iv_rolename   = av_iam_role_name
            iv_policyname = 'IoTSNSPublish' ).
        CATCH /aws1/cx_rt_generic.
      ENDTRY.
      TRY.
          ao_iam->deleterole( iv_rolename = av_iam_role_name ).
        CATCH /aws1/cx_rt_generic.
      ENDTRY.
    ENDIF.

    " ── SNS topic ────────────────────────────────────────────────────────
    IF av_sns_topic_arn IS NOT INITIAL.
      TRY.
          ao_sns->deletetopic( iv_topicarn = av_sns_topic_arn ).
        CATCH /aws1/cx_rt_generic.
      ENDTRY.
    ENDIF.

  ENDMETHOD.


  METHOD build_iot_tags.
    DATA lo_tag TYPE REF TO /aws1/cl_iottag.
    lo_tag = NEW /aws1/cl_iottag( iv_key = 'convert_test' iv_value = 'true' ).
    APPEND lo_tag TO rt_tags.
  ENDMETHOD.


  " ════════════════════════════════════════════════════════════════════════
  " Test: create_thing
  " Creates a fresh thing via the action, validates ARN + name, cleans up.
  " ════════════════════════════════════════════════════════════════════════
  METHOD create_thing.
    DATA lv_uuid TYPE string.
    lv_uuid = /awsex/cl_utils=>get_random_string( ).
    CONDENSE lv_uuid NO-GAPS.
    DATA(lv_name) = CONV /aws1/iotthingname( |sap-abap-iot-new-{ lv_uuid }| ).

    DATA(lo_result) = ao_iot_actions->create_thing( iv_thing_name = lv_name ).

    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = |create_thing: result not bound for { lv_name }| ).

    cl_abap_unit_assert=>assert_equals(
      act = lo_result->get_thingname( )
      exp = lv_name
      msg = |create_thing: thing name mismatch| ).

    cl_abap_unit_assert=>assert_not_initial(
      act = lo_result->get_thingarn( )
      msg = |create_thing: ARN is empty| ).

    " Clean up the newly created thing.
    TRY.
        ao_iot->deletething( iv_thingname = lv_name ).
      CATCH /aws1/cx_rt_generic.
    ENDTRY.
  ENDMETHOD.


  " ════════════════════════════════════════════════════════════════════════
  " Test: list_things
  " The shared thing must appear in the full accumulated result table.
  " ════════════════════════════════════════════════════════════════════════
  METHOD list_things.
    DATA(lo_result) = ao_iot_actions->list_things( ).

    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = 'list_things: result not bound' ).

    " The action paginates all pages; oo_result is the last-page object.
    " We call get_things() which returns items on that page only.
    " To find the shared thing across all pages we re-paginate here.
    DATA lv_found     TYPE abap_bool VALUE abap_false.
    DATA lv_nexttoken TYPE /aws1/iotnexttoken.
    DO.
      DATA(lo_page) = ao_iot->listthings( iv_nexttoken = lv_nexttoken ).
      LOOP AT lo_page->get_things( ) INTO DATA(lo_thing).
        IF lo_thing->get_thingname( ) = av_thing_name.
          lv_found = abap_true.
        ENDIF.
      ENDLOOP.
      lv_nexttoken = lo_page->get_nexttoken( ).
      IF lv_nexttoken IS INITIAL OR lv_found = abap_true.
        EXIT.
      ENDIF.
    ENDDO.

    cl_abap_unit_assert=>assert_true(
      act = lv_found
      msg = |list_things: shared thing { av_thing_name } not found in result| ).
  ENDMETHOD.


  " ════════════════════════════════════════════════════════════════════════
  " Test: create_keys_and_certificate
  " Creates a real certificate, validates all returned fields, cleans up.
  " ════════════════════════════════════════════════════════════════════════
  METHOD create_keys_and_cert.
    DATA(lo_result) = ao_iot_actions->create_keys_and_certificate( ).

    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = 'create_keys_and_cert: result not bound' ).

    cl_abap_unit_assert=>assert_not_initial(
      act = lo_result->get_certificateid( )
      msg = 'create_keys_and_cert: certificate ID is empty' ).

    cl_abap_unit_assert=>assert_not_initial(
      act = lo_result->get_certificatearn( )
      msg = 'create_keys_and_cert: certificate ARN is empty' ).

    cl_abap_unit_assert=>assert_not_initial(
      act = lo_result->get_certificatepem( )
      msg = 'create_keys_and_cert: certificate PEM is empty' ).

    cl_abap_unit_assert=>assert_bound(
      act = lo_result->get_keypair( )
      msg = 'create_keys_and_cert: key pair is not bound' ).

    cl_abap_unit_assert=>assert_not_initial(
      act = lo_result->get_keypair( )->get_publickey( )
      msg = 'create_keys_and_cert: public key is empty' ).

    " Clean up.
    TRY.
        ao_iot->updatecertificate(
          iv_certificateid = lo_result->get_certificateid( )
          iv_newstatus     = 'INACTIVE' ).
        ao_iot->deletecertificate(
          iv_certificateid = lo_result->get_certificateid( ) ).
      CATCH /aws1/cx_rt_generic.
    ENDTRY.
  ENDMETHOD.


  " ════════════════════════════════════════════════════════════════════════
  " Test: attach_thing_principal
  " Creates a fresh certificate, attaches it to the shared thing via the
  " action, verifies it appears in ListThingPrincipals, then cleans up.
  " ════════════════════════════════════════════════════════════════════════
  METHOD attach_thing_principal.
    " Create a dedicated certificate for this test.
    DATA(lo_cert)     = ao_iot->createkeysandcertificate( iv_setasactive = abap_true ).
    DATA(lv_cert_arn) = lo_cert->get_certificatearn( ).
    DATA(lv_cert_id)  = lo_cert->get_certificateid( ).

    ao_iot_actions->attach_thing_principal(
      iv_thing_name = av_thing_name
      iv_principal  = lv_cert_arn ).

    " Verify: certificate must appear in the thing's principal list.
    DATA(lo_principals) =
      ao_iot->listthingprincipals( iv_thingname = av_thing_name ).
    DATA lv_found TYPE abap_bool VALUE abap_false.
    LOOP AT lo_principals->get_principals( ) INTO DATA(lo_p).
      IF lo_p->get_value( ) = lv_cert_arn.
        lv_found = abap_true.
      ENDIF.
    ENDLOOP.

    cl_abap_unit_assert=>assert_true(
      act = lv_found
      msg = |attach_thing_principal: cert { lv_cert_arn } not found in principals| ).

    " Clean up.
    TRY.
        ao_iot->detachthingprincipal(
          iv_thingname = av_thing_name
          iv_principal = lv_cert_arn ).
        ao_iot->updatecertificate(
          iv_certificateid = lv_cert_id
          iv_newstatus     = 'INACTIVE' ).
        ao_iot->deletecertificate( iv_certificateid = lv_cert_id ).
      CATCH /aws1/cx_rt_generic.
    ENDTRY.
  ENDMETHOD.


  " ════════════════════════════════════════════════════════════════════════
  " Test: describe_endpoint
  " Retrieves the ATS data endpoint; validates it looks like a real URL.
  " ════════════════════════════════════════════════════════════════════════
  METHOD describe_endpoint.
    DATA(lv_endpoint) = ao_iot_actions->describe_endpoint(
      iv_endpoint_type = 'iot:Data-ATS' ).

    cl_abap_unit_assert=>assert_not_initial(
      act = lv_endpoint
      msg = 'describe_endpoint: endpoint address is empty' ).

    " A real ATS endpoint always contains 'amazonaws.com'.
    DATA(lv_ep_str) = CONV string( lv_endpoint ).
    cl_abap_unit_assert=>assert_differs(
      act = find( val = lv_ep_str sub = 'amazonaws.com' )
      exp = -1
      msg = |describe_endpoint: address looks invalid: { lv_endpoint }| ).
  ENDMETHOD.


  " ════════════════════════════════════════════════════════════════════════
  " Test: list_certificates
  " The shared certificate (created in class_setup) must appear in results.
  " ════════════════════════════════════════════════════════════════════════
  METHOD list_certificates.
    DATA(lo_result) = ao_iot_actions->list_certificates( ).

    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = 'list_certificates: result not bound' ).

    " The action paginates all pages; oo_result is the last-page object.
    " Re-paginate here to search all pages for the shared certificate.
    DATA lv_found  TYPE abap_bool VALUE abap_false.
    DATA lv_marker TYPE /aws1/iotmarker.
    DO.
      DATA(lo_page) = ao_iot->listcertificates( iv_marker = lv_marker ).
      LOOP AT lo_page->get_certificates( ) INTO DATA(lo_cert).
        IF lo_cert->get_certificateid( ) = av_cert_id.
          lv_found = abap_true.
        ENDIF.
      ENDLOOP.
      lv_marker = lo_page->get_nextmarker( ).
      IF lv_marker IS INITIAL OR lv_found = abap_true.
        EXIT.
      ENDIF.
    ENDDO.

    cl_abap_unit_assert=>assert_true(
      act = lv_found
      msg = |list_certificates: shared cert { av_cert_id } not found| ).
  ENDMETHOD.


  " ════════════════════════════════════════════════════════════════════════
  " Test: detach_thing_principal
  " Creates a fresh certificate, attaches it directly, then detaches via
  " the action; verifies it no longer appears in ListThingPrincipals.
  " ════════════════════════════════════════════════════════════════════════
  METHOD detach_thing_principal.
    DATA(lo_cert)     = ao_iot->createkeysandcertificate( iv_setasactive = abap_true ).
    DATA(lv_cert_arn) = lo_cert->get_certificatearn( ).
    DATA(lv_cert_id)  = lo_cert->get_certificateid( ).

    " Attach directly (bypassing the action method under test).
    ao_iot->attachthingprincipal(
      iv_thingname = av_thing_name
      iv_principal = lv_cert_arn ).

    " Now use the action to detach.
    ao_iot_actions->detach_thing_principal(
      iv_thing_name = av_thing_name
      iv_principal  = lv_cert_arn ).

    " Verify: certificate must NOT appear in principal list any more.
    DATA(lo_principals) =
      ao_iot->listthingprincipals( iv_thingname = av_thing_name ).
    DATA lv_found TYPE abap_bool VALUE abap_false.
    LOOP AT lo_principals->get_principals( ) INTO DATA(lo_p).
      IF lo_p->get_value( ) = lv_cert_arn.
        lv_found = abap_true.
      ENDIF.
    ENDLOOP.

    cl_abap_unit_assert=>assert_false(
      act = lv_found
      msg = |detach_thing_principal: cert { lv_cert_arn } still in principals| ).

    " Clean up.
    TRY.
        ao_iot->updatecertificate(
          iv_certificateid = lv_cert_id
          iv_newstatus     = 'INACTIVE' ).
        ao_iot->deletecertificate( iv_certificateid = lv_cert_id ).
      CATCH /aws1/cx_rt_generic.
    ENDTRY.
  ENDMETHOD.


  " ════════════════════════════════════════════════════════════════════════
  " Test: delete_certificate
  " Uses the dedicated certificate from class_setup. After the action
  " succeeds, DescribeCertificate must raise ResourceNotFoundException.
  " ════════════════════════════════════════════════════════════════════════
  METHOD delete_certificate.
    " Fail fast if the dedicated resource was never created.
    cl_abap_unit_assert=>assert_not_initial(
      act = av_del_cert_id
      msg = 'delete_certificate: dedicated certificate ID not set in class_setup' ).

    ao_iot_actions->delete_certificate( iv_certificate_id = av_del_cert_id ).

    " Verify: DescribeCertificate must now raise ResourceNotFoundException.
    DATA lv_deleted TYPE abap_bool VALUE abap_false.
    TRY.
        ao_iot->describecertificate( iv_certificateid = av_del_cert_id ).
      CATCH /aws1/cx_iotresourcenotfoundex.
        lv_deleted = abap_true.
    ENDTRY.

    cl_abap_unit_assert=>assert_true(
      act = lv_deleted
      msg = |delete_certificate: cert { av_del_cert_id } still exists after deletion| ).

    " Prevent class_teardown from trying to delete it again.
    CLEAR av_del_cert_id.
  ENDMETHOD.


  " ════════════════════════════════════════════════════════════════════════
  " Test: create_topic_rule
  " Creates a fresh rule via the action, verifies it appears in
  " ListTopicRules, then cleans up.
  " ════════════════════════════════════════════════════════════════════════
  METHOD create_topic_rule.
    DATA lv_uuid TYPE string.
    lv_uuid = /awsex/cl_utils=>get_random_string( ).
    CONDENSE lv_uuid NO-GAPS.
    DATA(lv_rule) = CONV /aws1/iotrulename( |sap_abap_iot_new_{ lv_uuid }| ).
    REPLACE ALL OCCURRENCES OF '-' IN lv_rule WITH '_'.

    ao_iot_actions->create_topic_rule(
      iv_rule_name      = lv_rule
      iv_topic          = 'test/sap/abap/create'
      iv_sns_action_arn = av_sns_topic_arn
      iv_role_arn       = av_iam_role_arn ).

    " Tag the newly created rule.
    DATA(lv_acct)   = ao_session->get_account_id( ).
    DATA(lv_region) = ao_session->get_region( ).
    TRY.
        ao_iot->tagresource(
          iv_resourcearn =
            |arn:aws:iot:{ lv_region }:{ lv_acct }:rule/{ lv_rule }|
          it_tags = build_iot_tags( ) ).
      CATCH /aws1/cx_rt_generic.
    ENDTRY.

    " Verify: rule must appear in paginated list.
    DATA lv_found     TYPE abap_bool VALUE abap_false.
    DATA lv_nexttoken TYPE /aws1/iotnexttoken.
    DO.
      DATA(lo_rules) = ao_iot->listtopicrules( iv_nexttoken = lv_nexttoken ).
      LOOP AT lo_rules->get_rules( ) INTO DATA(lo_rule).
        IF lo_rule->get_rulename( ) = lv_rule.
          lv_found = abap_true.
        ENDIF.
      ENDLOOP.
      lv_nexttoken = lo_rules->get_nexttoken( ).
      IF lv_nexttoken IS INITIAL OR lv_found = abap_true.
        EXIT.
      ENDIF.
    ENDDO.

    cl_abap_unit_assert=>assert_true(
      act = lv_found
      msg = |create_topic_rule: rule { lv_rule } not found after creation| ).

    " Clean up.
    TRY.
        ao_iot->deletetopicrule( iv_rulename = lv_rule ).
      CATCH /aws1/cx_rt_generic.
    ENDTRY.
  ENDMETHOD.


  " ════════════════════════════════════════════════════════════════════════
  " Test: list_topic_rules
  " The shared rule created in class_setup must appear in results.
  " ════════════════════════════════════════════════════════════════════════
  METHOD list_topic_rules.
    DATA(lo_result) = ao_iot_actions->list_topic_rules( ).

    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = 'list_topic_rules: result not bound' ).

    " The action paginates all pages; oo_result is the last-page object.
    " Re-paginate here to search all pages for the shared rule.
    DATA lv_found     TYPE abap_bool VALUE abap_false.
    DATA lv_nexttoken TYPE /aws1/iotnexttoken.
    DO.
      DATA(lo_page) = ao_iot->listtopicrules( iv_nexttoken = lv_nexttoken ).
      LOOP AT lo_page->get_rules( ) INTO DATA(lo_rule).
        IF lo_rule->get_rulename( ) = av_rule_name.
          lv_found = abap_true.
        ENDIF.
      ENDLOOP.
      lv_nexttoken = lo_page->get_nexttoken( ).
      IF lv_nexttoken IS INITIAL OR lv_found = abap_true.
        EXIT.
      ENDIF.
    ENDDO.

    cl_abap_unit_assert=>assert_true(
      act = lv_found
      msg = |list_topic_rules: shared rule { av_rule_name } not found| ).
  ENDMETHOD.


  " ════════════════════════════════════════════════════════════════════════
  " Test: update_indexing_configuration
  " Enables REGISTRY-mode indexing, then reads it back to confirm.
  " ════════════════════════════════════════════════════════════════════════
  METHOD update_indexing_conf.
    ao_iot_actions->update_indexing_configuration( ).

    DATA(lo_conf) = ao_iot->getindexingconfiguration( ).
    cl_abap_unit_assert=>assert_bound(
      act = lo_conf
      msg = 'update_indexing_conf: getindexingconfiguration returned unbound' ).

    DATA(lo_thing_conf) = lo_conf->get_thingindexingconf( ).
    cl_abap_unit_assert=>assert_bound(
      act = lo_thing_conf
      msg = 'update_indexing_conf: thingindexingconf not bound' ).

    cl_abap_unit_assert=>assert_equals(
      act = lo_thing_conf->get_thingindexingmode( )
      exp = 'REGISTRY'
      msg = |update_indexing_conf: mode is { lo_thing_conf->get_thingindexingmode( ) }, expected REGISTRY| ).
  ENDMETHOD.


  " ════════════════════════════════════════════════════════════════════════
  " Test: search_index
  " Fleet indexing must be enabled first; poll until it is ready (up to
  " 60 s).  Then call the action and confirm the shared thing is found.
  " ════════════════════════════════════════════════════════════════════════
  METHOD search_index.
    " Ensure indexing is enabled (idempotent – safe to call again here).
    DATA lo_idx_conf TYPE REF TO /aws1/cl_iotthingindexingconf.
    CREATE OBJECT lo_idx_conf
      EXPORTING
        iv_thingindexingmode = 'REGISTRY'.
    ao_iot->updateindexingconfiguration(
      io_thingindexingconf = lo_idx_conf ).

    " Poll SearchIndex until the index is ready (IndexNotReadyException resolves).
    DATA lv_ready   TYPE abap_bool VALUE abap_false.
    DATA lv_retries TYPE i        VALUE 0.
    WHILE lv_retries < 20 AND lv_ready = abap_false.
      TRY.
          ao_iot->searchindex(
            iv_querystring = |thingName:{ av_thing_name }| ).
          lv_ready = abap_true.
        CATCH /aws1/cx_iotindexnotreadyex.
          lv_retries = lv_retries + 1.
          WAIT UP TO 3 SECONDS.
        CATCH /aws1/cx_rt_generic INTO DATA(lo_poll_ex).
          cl_abap_unit_assert=>fail(
            msg = |search_index: polling failed: { lo_poll_ex->get_text( ) }| ).
      ENDTRY.
    ENDWHILE.

    IF lv_ready = abap_false.
      cl_abap_unit_assert=>fail(
        msg = 'search_index: fleet index not ready after 60 s of polling' ).
    ENDIF.

    " Now exercise the action method under test.
    DATA(lo_result) = ao_iot_actions->search_index(
      iv_query_string = |thingName:{ av_thing_name }| ).

    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = 'search_index: result not bound' ).

    DATA lv_found TYPE abap_bool VALUE abap_false.
    LOOP AT lo_result->get_things( ) INTO DATA(lo_thing).
      IF lo_thing->get_thingname( ) = av_thing_name.
        lv_found = abap_true.
      ENDIF.
    ENDLOOP.

    cl_abap_unit_assert=>assert_true(
      act = lv_found
      msg = |search_index: shared thing { av_thing_name } not found in result| ).
  ENDMETHOD.


  " ════════════════════════════════════════════════════════════════════════
  " Test: delete_thing
  " Uses the dedicated thing from class_setup; verifies DescribeThing
  " raises ResourceNotFoundException after deletion.
  " ════════════════════════════════════════════════════════════════════════
  METHOD delete_thing.
    cl_abap_unit_assert=>assert_not_initial(
      act = av_del_thing_name
      msg = 'delete_thing: dedicated thing name not set in class_setup' ).

    ao_iot_actions->delete_thing( iv_thing_name = av_del_thing_name ).

    DATA lv_deleted TYPE abap_bool VALUE abap_false.
    TRY.
        ao_iot->describething( iv_thingname = av_del_thing_name ).
      CATCH /aws1/cx_iotresourcenotfoundex.
        lv_deleted = abap_true.
    ENDTRY.

    cl_abap_unit_assert=>assert_true(
      act = lv_deleted
      msg = |delete_thing: thing { av_del_thing_name } still exists after deletion| ).

    CLEAR av_del_thing_name.
  ENDMETHOD.


  " ════════════════════════════════════════════════════════════════════════
  " Test: delete_topic_rule
  " Uses the dedicated rule from class_setup; verifies it no longer
  " appears in ListTopicRules after deletion.
  " ════════════════════════════════════════════════════════════════════════
  METHOD delete_topic_rule.
    cl_abap_unit_assert=>assert_not_initial(
      act = av_del_rule_name
      msg = 'delete_topic_rule: dedicated rule name not set in class_setup' ).

    ao_iot_actions->delete_topic_rule( iv_rule_name = av_del_rule_name ).

    " Verify: rule must NOT appear in paginated list.
    DATA lv_found     TYPE abap_bool VALUE abap_false.
    DATA lv_nexttoken TYPE /aws1/iotnexttoken.
    DO.
      DATA(lo_rules) = ao_iot->listtopicrules( iv_nexttoken = lv_nexttoken ).
      LOOP AT lo_rules->get_rules( ) INTO DATA(lo_rule).
        IF lo_rule->get_rulename( ) = av_del_rule_name.
          lv_found = abap_true.
        ENDIF.
      ENDLOOP.
      lv_nexttoken = lo_rules->get_nexttoken( ).
      IF lv_nexttoken IS INITIAL.
        EXIT.
      ENDIF.
    ENDDO.

    cl_abap_unit_assert=>assert_false(
      act = lv_found
      msg = |delete_topic_rule: rule { av_del_rule_name } still exists after deletion| ).

    CLEAR av_del_rule_name.
  ENDMETHOD.

ENDCLASS.
