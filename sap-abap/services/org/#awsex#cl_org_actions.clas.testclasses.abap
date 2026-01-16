" Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
" SPDX-License-Identifier: Apache-2.0
CLASS ltc_awsex_cl_org_actions DEFINITION DEFERRED.
CLASS /awsex/cl_org_actions DEFINITION LOCAL FRIENDS ltc_awsex_cl_org_actions.

CLASS ltc_awsex_cl_org_actions DEFINITION FOR TESTING DURATION LONG RISK LEVEL DANGEROUS.

  PRIVATE SECTION.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    CLASS-DATA ao_org TYPE REF TO /aws1/if_org.
    CLASS-DATA ao_session TYPE REF TO /aws1/cl_rt_session_base.
    CLASS-DATA ao_org_actions TYPE REF TO /awsex/cl_org_actions.
    CLASS-DATA av_test_root_id TYPE /aws1/orgrootid.
    CLASS-DATA av_created_policy_id TYPE /aws1/orgpolicyid.
    CLASS-DATA at_policy_ids TYPE TABLE OF /aws1/orgpolicyid.

    CLASS-METHODS class_setup RAISING /aws1/cx_rt_generic.
    CLASS-METHODS class_teardown RAISING /aws1/cx_rt_generic.

    METHODS create_policy FOR TESTING RAISING /aws1/cx_rt_generic.
    METHODS list_policies FOR TESTING RAISING /aws1/cx_rt_generic.
    METHODS describe_policy FOR TESTING RAISING /aws1/cx_rt_generic.
    METHODS attach_policy FOR TESTING RAISING /aws1/cx_rt_generic.
    METHODS detach_policy FOR TESTING RAISING /aws1/cx_rt_generic.
    METHODS delete_policy FOR TESTING RAISING /aws1/cx_rt_generic.

    CLASS-METHODS:
      create_test_policy
        IMPORTING iv_policy_name          TYPE /aws1/orgpolicyname
        RETURNING VALUE(ov_policy_id)     TYPE /aws1/orgpolicyid
        RAISING   /aws1/cx_rt_generic,
      tag_resource
        IMPORTING iv_resource_id TYPE /aws1/orgtaggableresourceid
        RAISING   /aws1/cx_rt_generic,
      cleanup_policy
        IMPORTING iv_policy_id TYPE /aws1/orgpolicyid
        RAISING   /aws1/cx_rt_generic.

ENDCLASS.

CLASS ltc_awsex_cl_org_actions IMPLEMENTATION.

  METHOD class_setup.
    ao_session = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    ao_org = /aws1/cl_org_factory=>create( ao_session ).
    ao_org_actions = NEW /awsex/cl_org_actions( ).

    " Get the organization root ID - this is required for all tests
    DATA(lo_roots_result) = ao_org->listroots( ).
    DATA(lt_roots) = lo_roots_result->get_roots( ).
    IF lines( lt_roots ) = 0.
      " If no roots found, we cannot proceed with tests
      RAISE EXCEPTION TYPE /aws1/cx_rt_service_generic
        EXPORTING
          av_err_code = 'NoOrganizationRoot'
          av_err_msg  = 'No organization root found. Tests require an AWS Organization.'.
    ENDIF.

    READ TABLE lt_roots INDEX 1 INTO DATA(lo_root).
    av_test_root_id = lo_root->get_id( ).

    " Enable tag policy type if not already enabled
    TRY.
        ao_org->enablepolicytype(
          iv_rootid     = av_test_root_id
          iv_policytype = 'TAG_POLICY' ).
      CATCH /aws1/cx_orgplytypealrdyenbdex.
        " Policy type already enabled, continue
    ENDTRY.

    " Create a policy for the tests that need an existing policy
    DATA lv_uuid TYPE sysuuid_c32.
    DATA lv_uuid_string TYPE string.
    DATA lv_policy_name TYPE /aws1/orgpolicyname.

    TRY.
        CALL FUNCTION 'GUID_CREATE'
          IMPORTING
            ev_guid_32 = lv_uuid.
      CATCH cx_uuid_error.
        lv_uuid = 'SETUP12345'.
    ENDTRY.
    lv_uuid_string = lv_uuid.
    lv_policy_name = |ABAP_Setup_{ lv_uuid_string(8) }|.

    av_created_policy_id = create_test_policy( lv_policy_name ).
    tag_resource( av_created_policy_id ).
    APPEND av_created_policy_id TO at_policy_ids.

  ENDMETHOD.

  METHOD class_teardown.
    " Clean up all test policies
    LOOP AT at_policy_ids INTO DATA(lv_policy_id).
      cleanup_policy( lv_policy_id ).
    ENDLOOP.
  ENDMETHOD.

  METHOD create_test_policy.
    DATA lv_policy_content TYPE /aws1/orgpolicycontent.

    " TAG_POLICY content
    lv_policy_content = '{"tags":{"CostCenter":{"tag_key":{"@@assign":"CostCenter"},"tag_value":{"@@assign":["AWS"]}}}}'.

    DATA(lo_create_result) = ao_org->createpolicy(
      iv_name        = iv_policy_name
      iv_description = 'Test policy for ABAP SDK'
      iv_content     = lv_policy_content
      iv_type        = 'TAG_POLICY' ).

    ov_policy_id = lo_create_result->get_policy( )->get_policysummary( )->get_id( ).
  ENDMETHOD.

  METHOD tag_resource.
    " Tag the resource with 'convert_test' tag
    DATA(lt_tags) = VALUE /aws1/cl_orgtag=>tt_tags(
      ( NEW /aws1/cl_orgtag(
          iv_key   = 'convert_test'
          iv_value = 'true' ) ) ).

    TRY.
        ao_org->tagresource(
          iv_resourceid = iv_resource_id
          it_tags       = lt_tags ).
      CATCH /aws1/cx_rt_generic.
        " Ignore tagging errors - not critical for tests
    ENDTRY.
  ENDMETHOD.

  METHOD cleanup_policy.
    TRY.
        " First detach the policy if it's attached
        TRY.
            ao_org->detachpolicy(
              iv_policyid = iv_policy_id
              iv_targetid = av_test_root_id ).
          CATCH /aws1/cx_orgpolicynotattex.
            " Policy not attached, continue
        ENDTRY.

        " Then delete the policy
        ao_org->deletepolicy( iv_policyid = iv_policy_id ).
      CATCH /aws1/cx_orgpolicynotfoundex.
        " Policy already deleted, continue
    ENDTRY.
  ENDMETHOD.

  METHOD create_policy.
    DATA lv_uuid TYPE sysuuid_c32.
    DATA lv_uuid_string TYPE string.
    DATA lv_policy_name TYPE /aws1/orgpolicyname.
    DATA lv_policy_content TYPE /aws1/orgpolicycontent.
    DATA lo_result TYPE REF TO /aws1/cl_orgcreatepolicyrsp.

    " Generate unique policy name
    TRY.
        CALL FUNCTION 'GUID_CREATE'
          IMPORTING
            ev_guid_32 = lv_uuid.
      CATCH cx_uuid_error.
        lv_uuid = 'TEST123456'.
    ENDTRY.
    lv_uuid_string = lv_uuid.
    lv_policy_name = |ABAP_Test_{ lv_uuid_string(8) }|.

    " TAG_POLICY content
    lv_policy_content = '{"tags":{"CostCenter":{"tag_key":{"@@assign":"CostCenter"},"tag_value":{"@@assign":["AWS2","AWS"]},"enforced_for":{"@@assign":["ec2:instance","ec2:volume"]}}}}'.

    ao_org_actions->create_policy(
      EXPORTING
        iv_policy_name        = lv_policy_name
        iv_policy_description = 'Test policy for ABAP SDK'
        iv_policy_content     = lv_policy_content
        iv_policy_type        = 'TAG_POLICY'
      IMPORTING
        oo_result             = lo_result ).

    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = 'Create policy result should not be null' ).

    DATA(lo_policy) = lo_result->get_policy( ).
    cl_abap_unit_assert=>assert_bound(
      act = lo_policy
      msg = 'Policy should not be null' ).

    " Store policy ID for cleanup
    DATA(lv_new_policy_id) = lo_policy->get_policysummary( )->get_id( ).
    tag_resource( lv_new_policy_id ).
    APPEND lv_new_policy_id TO at_policy_ids.

  ENDMETHOD.

  METHOD list_policies.
    DATA lo_result TYPE REF TO /aws1/cl_orglistpolresponse.

    ao_org_actions->list_policies(
      EXPORTING
        iv_filter = 'TAG_POLICY'
      IMPORTING
        oo_result = lo_result ).

    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = 'List policies result should not be null' ).

    DATA(lt_policies) = lo_result->get_policies( ).
    cl_abap_unit_assert=>assert_true(
      act = xsdbool( lines( lt_policies ) > 0 )
      msg = 'At least one policy should exist' ).

  ENDMETHOD.

  METHOD describe_policy.
    DATA lo_result TYPE REF TO /aws1/cl_orgdescrpolicyrsp.

    " Use the policy created in class_setup
    ao_org_actions->describe_policy(
      EXPORTING
        iv_policy_id = av_created_policy_id
      IMPORTING
        oo_result    = lo_result ).

    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = 'Describe policy result should not be null' ).

    DATA(lo_policy) = lo_result->get_policy( ).
    cl_abap_unit_assert=>assert_bound(
      act = lo_policy
      msg = 'Policy should not be null' ).

    DATA(lo_summary) = lo_policy->get_policysummary( ).
    cl_abap_unit_assert=>assert_equals(
      exp = av_created_policy_id
      act = lo_summary->get_id( )
      msg = 'Policy ID should match' ).

  ENDMETHOD.

  METHOD attach_policy.
    " Use the policy created in class_setup
    " Attach the policy to the root
    ao_org_actions->attach_policy(
      iv_policy_id = av_created_policy_id
      iv_target_id = av_test_root_id ).

    " Verify the policy is attached by listing policies for the target
    DATA(lo_list_result) = ao_org->listpoliciesfortarget(
      iv_targetid = av_test_root_id
      iv_filter   = 'TAG_POLICY' ).

    DATA lv_found TYPE abap_bool.
    LOOP AT lo_list_result->get_policies( ) INTO DATA(lo_policy_summary).
      IF lo_policy_summary->get_id( ) = av_created_policy_id.
        lv_found = abap_true.
        EXIT.
      ENDIF.
    ENDLOOP.

    cl_abap_unit_assert=>assert_true(
      act = lv_found
      msg = 'Policy should be attached to target' ).

  ENDMETHOD.

  METHOD detach_policy.
    " Use the policy created in class_setup
    " Ensure the policy is attached first
    TRY.
        ao_org->attachpolicy(
          iv_policyid = av_created_policy_id
          iv_targetid = av_test_root_id ).
      CATCH /aws1/cx_orgduplicateplyatta00.
        " Policy already attached, continue
    ENDTRY.

    " Detach the policy
    ao_org_actions->detach_policy(
      iv_policy_id = av_created_policy_id
      iv_target_id = av_test_root_id ).

    " Verify the policy is detached by listing policies for the target
    DATA(lo_list_result) = ao_org->listpoliciesfortarget(
      iv_targetid = av_test_root_id
      iv_filter   = 'TAG_POLICY' ).

    DATA lv_found TYPE abap_bool VALUE abap_false.
    LOOP AT lo_list_result->get_policies( ) INTO DATA(lo_policy_summary).
      IF lo_policy_summary->get_id( ) = av_created_policy_id.
        lv_found = abap_true.
        EXIT.
      ENDIF.
    ENDLOOP.

    cl_abap_unit_assert=>assert_false(
      act = lv_found
      msg = 'Policy should not be attached to target' ).

  ENDMETHOD.

  METHOD delete_policy.
    DATA lv_uuid TYPE sysuuid_c32.
    DATA lv_uuid_string TYPE string.
    DATA lv_policy_name TYPE /aws1/orgpolicyname.
    DATA lv_policy_id TYPE /aws1/orgpolicyid.

    " Generate unique policy name for deletion test
    TRY.
        CALL FUNCTION 'GUID_CREATE'
          IMPORTING
            ev_guid_32 = lv_uuid.
      CATCH cx_uuid_error.
        lv_uuid = 'TEST789012'.
    ENDTRY.
    lv_uuid_string = lv_uuid.
    lv_policy_name = |ABAP_Del_{ lv_uuid_string(8) }|.

    " Create a policy to delete
    lv_policy_id = create_test_policy( lv_policy_name ).
    tag_resource( lv_policy_id ).

    " Delete the policy
    ao_org_actions->delete_policy(
      iv_policy_id = lv_policy_id ).

    " Verify the policy is deleted by trying to describe it
    TRY.
        ao_org->describepolicy( iv_policyid = lv_policy_id ).
        cl_abap_unit_assert=>fail( msg = 'Policy should have been deleted' ).
      CATCH /aws1/cx_orgpolicynotfoundex.
        " Expected exception, policy was successfully deleted
    ENDTRY.

  ENDMETHOD.

ENDCLASS.
