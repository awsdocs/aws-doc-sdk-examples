" Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
" SPDX-License-Identifier: Apache-2.0
CLASS /awsex/cl_org_actions DEFINITION
  PUBLIC
  FINAL
  CREATE PUBLIC .

  PUBLIC SECTION.
    METHODS create_policy
      IMPORTING
        !iv_policy_name        TYPE /aws1/orgpolicyname
        !iv_policy_description TYPE /aws1/orgpolicydescription
        !iv_policy_content     TYPE /aws1/orgpolicycontent
        !iv_policy_type        TYPE /aws1/orgpolicytype
      EXPORTING
        !oo_result             TYPE REF TO /aws1/cl_orgcreatepolicyrsp
      RAISING
        /aws1/cx_rt_generic.

    METHODS list_policies
      IMPORTING
        !iv_filter TYPE /aws1/orgpolicytype
      EXPORTING
        !oo_result TYPE REF TO /aws1/cl_orglistpolresponse
      RAISING
        /aws1/cx_rt_generic.

    METHODS describe_policy
      IMPORTING
        !iv_policy_id TYPE /aws1/orgpolicyid
      EXPORTING
        !oo_result    TYPE REF TO /aws1/cl_orgdescrpolicyrsp
      RAISING
        /aws1/cx_rt_generic.

    METHODS attach_policy
      IMPORTING
        !iv_policy_id TYPE /aws1/orgpolicyid
        !iv_target_id TYPE /aws1/orgpolicytargetid
      RAISING
        /aws1/cx_rt_generic.

    METHODS detach_policy
      IMPORTING
        !iv_policy_id TYPE /aws1/orgpolicyid
        !iv_target_id TYPE /aws1/orgpolicytargetid
      RAISING
        /aws1/cx_rt_generic.

    METHODS delete_policy
      IMPORTING
        !iv_policy_id TYPE /aws1/orgpolicyid
      RAISING
        /aws1/cx_rt_generic.

  PROTECTED SECTION.
  PRIVATE SECTION.
ENDCLASS.



CLASS /AWSEX/CL_ORG_ACTIONS IMPLEMENTATION.


  METHOD create_policy.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_org) = /aws1/cl_org_factory=>create( lo_session ).

    " snippet-start:[org.abapv1.create_policy]
    TRY.
        oo_result = lo_org->createpolicy(       " oo_result is returned for testing purposes. "
          iv_name        = iv_policy_name
          iv_description = iv_policy_description
          iv_content     = iv_policy_content
          iv_type        = iv_policy_type ).
        MESSAGE 'Policy created.' TYPE 'I'.
      CATCH /aws1/cx_orgaccessdeniedex.
        MESSAGE 'You do not have permission to create a policy.' TYPE 'E'.
      CATCH /aws1/cx_orgduplicatepolicyex.
        MESSAGE 'A policy with this name already exists.' TYPE 'E'.
      CATCH /aws1/cx_orgmalformedplydocex.
        MESSAGE 'The policy content is malformed.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[org.abapv1.create_policy]
  ENDMETHOD.


  METHOD list_policies.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_org) = /aws1/cl_org_factory=>create( lo_session ).

    " snippet-start:[org.abapv1.list_policies]
    TRY.
        oo_result = lo_org->listpolicies(       " oo_result is returned for testing purposes. "
          iv_filter = iv_filter ).
        DATA(lt_policies) = oo_result->get_policies( ).
        MESSAGE 'Retrieved list of policies.' TYPE 'I'.
      CATCH /aws1/cx_orgaccessdeniedex.
        MESSAGE 'You do not have permission to list policies.' TYPE 'E'.
      CATCH /aws1/cx_orgawsorgsnotinuseex.
        MESSAGE 'Your account is not a member of an organization.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[org.abapv1.list_policies]
  ENDMETHOD.


  METHOD describe_policy.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_org) = /aws1/cl_org_factory=>create( lo_session ).

    " snippet-start:[org.abapv1.describe_policy]
    TRY.
        oo_result = lo_org->describepolicy(     " oo_result is returned for testing purposes. "
          iv_policyid = iv_policy_id ).
        DATA(lo_policy) = oo_result->get_policy( ).
        MESSAGE 'Retrieved policy details.' TYPE 'I'.
      CATCH /aws1/cx_orgaccessdeniedex.
        MESSAGE 'You do not have permission to describe the policy.' TYPE 'E'.
      CATCH /aws1/cx_orgpolicynotfoundex.
        MESSAGE 'The specified policy does not exist.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[org.abapv1.describe_policy]
  ENDMETHOD.


  METHOD attach_policy.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_org) = /aws1/cl_org_factory=>create( lo_session ).

    " snippet-start:[org.abapv1.attach_policy]
    TRY.
        lo_org->attachpolicy(
          iv_policyid = iv_policy_id
          iv_targetid = iv_target_id ).
        MESSAGE 'Policy attached to target.' TYPE 'I'.
      CATCH /aws1/cx_orgaccessdeniedex.
        MESSAGE 'You do not have permission to attach the policy.' TYPE 'E'.
      CATCH /aws1/cx_orgpolicynotfoundex.
        MESSAGE 'The specified policy does not exist.' TYPE 'E'.
      CATCH /aws1/cx_orgtargetnotfoundex.
        MESSAGE 'The specified target does not exist.' TYPE 'E'.
      CATCH /aws1/cx_orgduplicateplyatta00.
        MESSAGE 'The policy is already attached to the target.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[org.abapv1.attach_policy]
  ENDMETHOD.


  METHOD detach_policy.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_org) = /aws1/cl_org_factory=>create( lo_session ).

    " snippet-start:[org.abapv1.detach_policy]
    TRY.
        lo_org->detachpolicy(
          iv_policyid = iv_policy_id
          iv_targetid = iv_target_id ).
        MESSAGE 'Policy detached from target.' TYPE 'I'.
      CATCH /aws1/cx_orgaccessdeniedex.
        MESSAGE 'You do not have permission to detach the policy.' TYPE 'E'.
      CATCH /aws1/cx_orgpolicynotfoundex.
        MESSAGE 'The specified policy does not exist.' TYPE 'E'.
      CATCH /aws1/cx_orgtargetnotfoundex.
        MESSAGE 'The specified target does not exist.' TYPE 'E'.
      CATCH /aws1/cx_orgpolicynotattex.
        MESSAGE 'The policy is not attached to the target.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[org.abapv1.detach_policy]
  ENDMETHOD.


  METHOD delete_policy.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_org) = /aws1/cl_org_factory=>create( lo_session ).

    " snippet-start:[org.abapv1.delete_policy]
    TRY.
        lo_org->deletepolicy(
          iv_policyid = iv_policy_id ).
        MESSAGE 'Policy deleted.' TYPE 'I'.
      CATCH /aws1/cx_orgaccessdeniedex.
        MESSAGE 'You do not have permission to delete the policy.' TYPE 'E'.
      CATCH /aws1/cx_orgpolicynotfoundex.
        MESSAGE 'The specified policy does not exist.' TYPE 'E'.
      CATCH /aws1/cx_orgpolicyinuseex.
        MESSAGE 'The policy is still attached to one or more targets.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[org.abapv1.delete_policy]
  ENDMETHOD.

ENDCLASS.
