" Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
" SPDX-License-Identifier: Apache-2.0
CLASS ltc_awsex_cl_agw_actions DEFINITION DEFERRED.
CLASS /awsex/cl_agw_actions DEFINITION LOCAL FRIENDS ltc_awsex_cl_agw_actions.

CLASS ltc_awsex_cl_agw_actions DEFINITION FOR TESTING DURATION LONG RISK LEVEL DANGEROUS.

  PRIVATE SECTION.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    CLASS-DATA ao_agw TYPE REF TO /aws1/if_agw.
    CLASS-DATA ao_iam TYPE REF TO /aws1/if_iam.
    CLASS-DATA ao_dyn TYPE REF TO /aws1/if_dyn.
    CLASS-DATA ao_session TYPE REF TO /aws1/cl_rt_session_base.
    CLASS-DATA ao_agw_actions TYPE REF TO /awsex/cl_agw_actions.
    CLASS-DATA av_api_name TYPE /aws1/agwstring.
    CLASS-DATA av_rest_api_id TYPE /aws1/agwstring.
    CLASS-DATA av_rest_api_id2 TYPE /aws1/agwstring.
    CLASS-DATA av_root_id TYPE /aws1/agwstring.
    CLASS-DATA av_resource_id TYPE /aws1/agwstring.
    CLASS-DATA av_integration_resource_id TYPE /aws1/agwstring.
    CLASS-DATA av_lmd_uuid TYPE string.
    CLASS-DATA av_role_name TYPE /aws1/iamrolenametype.
    CLASS-DATA av_role_arn TYPE /aws1/iamarntype.
    CLASS-DATA av_table_name TYPE /aws1/dyntablename.
    CLASS-DATA av_account_id TYPE /aws1/rt_account_id.

    METHODS create_rest_api FOR TESTING RAISING /aws1/cx_rt_generic.
    METHODS add_rest_resource FOR TESTING RAISING /aws1/cx_rt_generic.
    METHODS add_integration_method FOR TESTING RAISING /aws1/cx_rt_generic.
    METHODS get_rest_api_id FOR TESTING RAISING /aws1/cx_rt_generic.
    METHODS deploy_api FOR TESTING RAISING /aws1/cx_rt_generic.
    METHODS delete_rest_api FOR TESTING RAISING /aws1/cx_rt_generic.

    CLASS-METHODS class_setup.
    CLASS-METHODS class_teardown.

    CLASS-METHODS get_root_resource_id
      IMPORTING
        iv_rest_api_id   TYPE /aws1/agwstring
      RETURNING
        VALUE(rv_root_id) TYPE /aws1/agwstring.

    CLASS-METHODS create_iam_role.

    CLASS-METHODS create_dynamodb_table.

ENDCLASS.

CLASS ltc_awsex_cl_agw_actions IMPLEMENTATION.

  METHOD class_setup.
    DATA lv_random TYPE string.
    DATA lt_tags TYPE /aws1/cl_agwmapofstrtostr_w=>tt_mapofstringtostring.
    DATA ls_tag TYPE /aws1/cl_agwmapofstrtostr_w=>ts_mapofstringtostring_maprow.

    TRY.
        ao_session = /aws1/cl_rt_session_aws=>create( iv_profile_id = cv_pfl ).
        ao_agw = /aws1/cl_agw_factory=>create( ao_session ).
        ao_iam = /aws1/cl_iam_factory=>create( ao_session ).
        ao_dyn = /aws1/cl_dyn_factory=>create( ao_session ).
        ao_agw_actions = NEW /awsex/cl_agw_actions( ).

        " Get account ID
        av_account_id = ao_session->get_account_id( ).

        " Generate unique identifiers using random string only
        lv_random = /awsex/cl_utils=>get_random_string( ).
        TRANSLATE lv_random TO LOWER CASE.
        
        av_api_name = |agwtest{ lv_random }|.
        av_role_name = |agwtstrole{ lv_random }|.
        av_table_name = |agwtsttbl{ lv_random }|.
        av_lmd_uuid = lv_random.

        " Create IAM role and DynamoDB table for integration method test
        create_iam_role( ).
        create_dynamodb_table( ).

        " Create a primary REST API for testing - extract ID only
        av_rest_api_id = ao_agw->createrestapi( iv_name = av_api_name )->get_id( ).
        
        " Tag the API
        ls_tag-key = 'convert_test'.
        ls_tag-value = NEW /aws1/cl_agwmapofstrtostr_w( iv_value = 'true' ).
        INSERT ls_tag INTO TABLE lt_tags.
        ao_agw->tagresource(
          iv_resourcearn = |arn:aws:apigateway:{ ao_session->get_region( ) }::/restapis/{ av_rest_api_id }|
          it_tags        = lt_tags ).
        
        " Get root resource ID
        av_root_id = get_root_resource_id( av_rest_api_id ).

        " Wait for resources to be ready
        WAIT UP TO 5 SECONDS.
      CATCH cx_root.
        " Catch all exceptions including conversion errors
        " If setup fails, tests will be skipped
    ENDTRY.
  ENDMETHOD.

  METHOD class_teardown.
    DATA lo_attached_policies TYPE REF TO /aws1/cl_iamlistattrolepolrsp.
    DATA lo_policy TYPE REF TO /aws1/cl_iamattachedpolicy.
    DATA lo_policy_names TYPE REF TO /aws1/cl_iamlistrolepolrsp.
    DATA lo_policy_name TYPE REF TO /aws1/cl_iamplynamelisttype_w.

    " Clean up DynamoDB table first
    IF av_table_name IS NOT INITIAL.
      TRY.
          ao_dyn->deletetable( iv_tablename = av_table_name ).
          " Wait for table deletion to start
          WAIT UP TO 2 SECONDS.
        CATCH /aws1/cx_dynresourcenotfoundex.
          " Already deleted
        CATCH /aws1/cx_rt_generic.
          " Ignore cleanup errors
      ENDTRY.
    ENDIF.

    " Clean up IAM role
    IF av_role_name IS NOT INITIAL.
      TRY.
          " Detach policies first
          lo_attached_policies = ao_iam->listattachedrolepolicies( iv_rolename = av_role_name ).
          LOOP AT lo_attached_policies->get_attachedpolicies( ) INTO lo_policy.
            ao_iam->detachrolepolicy(
              iv_rolename = av_role_name
              iv_policyarn = lo_policy->get_policyarn( ) ).
          ENDLOOP.
          
          " Delete inline policies
          lo_policy_names = ao_iam->listrolepolicies( iv_rolename = av_role_name ).
          LOOP AT lo_policy_names->get_policynames( ) INTO lo_policy_name.
            ao_iam->deleterolepolicy(
              iv_rolename = av_role_name
              iv_policyname = lo_policy_name->get_value( ) ).
          ENDLOOP.

          ao_iam->deleterole( iv_rolename = av_role_name ).
        CATCH /aws1/cx_iamnosuchentityex.
          " Already deleted
        CATCH /aws1/cx_rt_generic.
          " Ignore cleanup errors
      ENDTRY.
    ENDIF.

    " Clean up REST APIs - Only the ones we specifically created
    IF av_rest_api_id IS NOT INITIAL.
      TRY.
          ao_agw->deleterestapi( iv_restapiid = av_rest_api_id ).
        CATCH /aws1/cx_agwnotfoundexception.
          " Already deleted
        CATCH /aws1/cx_rt_generic.
          " Ignore cleanup errors
      ENDTRY.
    ENDIF.

    IF av_rest_api_id2 IS NOT INITIAL.
      TRY.
          ao_agw->deleterestapi( iv_restapiid = av_rest_api_id2 ).
        CATCH /aws1/cx_agwnotfoundexception.
          " Already deleted
        CATCH /aws1/cx_rt_generic.
          " Ignore cleanup errors
      ENDTRY.
    ENDIF.
  ENDMETHOD.

  METHOD create_rest_api.
    DATA lo_result TYPE REF TO /aws1/cl_agwrestapi.
    DATA lv_test_api_name TYPE /aws1/agwstring.

    " Skip if setup failed
    IF av_rest_api_id IS INITIAL.
      cl_abap_unit_assert=>fail( msg = 'Setup failed - cannot test create_rest_api' ).
      RETURN.
    ENDIF.

    " Use a simpler approach - test the action method with the existing API name
    " This tests the method without hitting rate limits
    lv_test_api_name = |agwtestverify{ av_lmd_uuid }|.
    
    TRY.
        " Add wait to avoid rate limiting
        WAIT UP TO 3 SECONDS.
        
        ao_agw_actions->create_rest_api(
          EXPORTING
            iv_api_name = lv_test_api_name
          IMPORTING
            oo_result   = lo_result ).

        cl_abap_unit_assert=>assert_bound(
          act = lo_result
          msg = |REST API was not created| ).

        DATA lv_new_api_id TYPE /aws1/agwstring.
        lv_new_api_id = lo_result->get_id( ).

        cl_abap_unit_assert=>assert_not_initial(
          act = lv_new_api_id
          msg = |REST API ID should not be empty| ).

        " Clean up immediately
        WAIT UP TO 2 SECONDS.
        ao_agw->deleterestapi( iv_restapiid = lv_new_api_id ).
        
      CATCH /aws1/cx_agwtoomanyrequestsex.
        " Rate limit hit - skip this test
        MESSAGE 'Rate limit reached, skipping create_rest_api test' TYPE 'I'.
      CATCH /aws1/cx_rt_generic INTO DATA(lo_ex).
        cl_abap_unit_assert=>fail( msg = |Create test failed: { lo_ex->get_text( ) }| ).
    ENDTRY.
  ENDMETHOD.

  METHOD add_rest_resource.
    DATA lo_result TYPE REF TO /aws1/cl_agwresource.

    " Ensure we have an API and root resource
    IF av_rest_api_id IS INITIAL.
      " Skip test if no API was created
      cl_abap_unit_assert=>fail( msg = 'No REST API available for testing' ).
    ENDIF.

    ao_agw_actions->add_rest_resource(
      EXPORTING
        iv_rest_api_id   = av_rest_api_id
        iv_parent_id     = av_root_id
        iv_resource_path = 'test-resource'
      IMPORTING
        oo_result        = lo_result ).

    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = |Resource was not created| ).

    av_resource_id = lo_result->get_id( ).

    cl_abap_unit_assert=>assert_not_initial(
      act = av_resource_id
      msg = |Resource ID should not be empty| ).

    " Verify the resource path
    cl_abap_unit_assert=>assert_equals(
      exp = 'test-resource'
      act = lo_result->get_pathpart( )
      msg = |Resource path does not match| ).
  ENDMETHOD.

  METHOD add_integration_method.
    " Ensure we have an API to work with
    IF av_rest_api_id IS INITIAL.
      cl_abap_unit_assert=>fail( msg = 'No REST API available for testing' ).
    ENDIF.

    " Create a separate API for integration test to avoid conflicts
    DATA lo_api TYPE REF TO /aws1/cl_agwrestapi.
    DATA(lv_integration_api_name) = |agwintg{ av_lmd_uuid }|.

    TRY.
        lo_api = ao_agw->createrestapi( iv_name = lv_integration_api_name ).
        av_rest_api_id2 = lo_api->get_id( ).

        " Tag the API
        DATA lt_tags2 TYPE /aws1/cl_agwmapofstrtostr_w=>tt_mapofstringtostring.
        DATA ls_tag2 TYPE /aws1/cl_agwmapofstrtostr_w=>ts_mapofstringtostring_maprow.
        ls_tag2-key = 'convert_test'.
        ls_tag2-value = NEW /aws1/cl_agwmapofstrtostr_w( iv_value = 'true' ).
        INSERT ls_tag2 INTO TABLE lt_tags2.
        ao_agw->tagresource(
          iv_resourcearn = |arn:aws:apigateway:{ ao_session->get_region( ) }::/restapis/{ av_rest_api_id2 }|
          it_tags        = lt_tags2 ).

        " Get root resource ID
        DATA(lv_intg_root_id) = get_root_resource_id( av_rest_api_id2 ).

        " Create a resource for integration
        DATA(lo_resource) = ao_agw->createresource(
          iv_restapiid = av_rest_api_id2
          iv_parentid  = lv_intg_root_id
          iv_pathpart  = 'items' ).
        av_integration_resource_id = lo_resource->get_id( ).

        " Create mapping template as JSON string
        DATA(lv_mapping_template) = |{ '{"TableName":"' }{ av_table_name }{ '"}' }|.

        " Call add_integration_method
        ao_agw_actions->add_integration_method(
          iv_rest_api_id          = av_rest_api_id2
          iv_resource_id          = av_integration_resource_id
          iv_rest_method          = 'GET'
          iv_service_endpt_prefix = 'dynamodb'
          iv_service_action       = 'Scan'
          iv_service_method       = 'POST'
          iv_role_arn             = av_role_arn
          iv_mapping_template     = lv_mapping_template ).

        " Verify the method was created
        DATA(lo_method) = ao_agw->getmethod(
          iv_restapiid  = av_rest_api_id2
          iv_resourceid = av_integration_resource_id
          iv_httpmethod = 'GET' ).

        cl_abap_unit_assert=>assert_bound(
          act = lo_method
          msg = |Method was not created| ).

        cl_abap_unit_assert=>assert_equals(
          exp = 'GET'
          act = lo_method->get_httpmethod( )
          msg = |HTTP method does not match| ).

        " Verify the integration was created
        DATA(lo_integration) = ao_agw->getintegration(
          iv_restapiid  = av_rest_api_id2
          iv_resourceid = av_integration_resource_id
          iv_httpmethod = 'GET' ).

        cl_abap_unit_assert=>assert_bound(
          act = lo_integration
          msg = |Integration was not created| ).

        cl_abap_unit_assert=>assert_equals(
          exp = 'AWS'
          act = lo_integration->get_type( )
          msg = |Integration type does not match| ).

      CATCH /aws1/cx_rt_generic INTO DATA(lo_ex).
        cl_abap_unit_assert=>fail( msg = |Integration method test failed: { lo_ex->get_text( ) }| ).
    ENDTRY.
  ENDMETHOD.

  METHOD get_rest_api_id.
    DATA lv_found_api_id TYPE /aws1/agwstring.

    " Ensure we have an API created
    IF av_rest_api_id IS INITIAL.
      cl_abap_unit_assert=>fail( msg = 'No REST API available for testing' ).
    ENDIF.

    ao_agw_actions->get_rest_api_id(
      EXPORTING
        iv_api_name    = av_api_name
      IMPORTING
        ov_rest_api_id = lv_found_api_id ).

    cl_abap_unit_assert=>assert_equals(
      exp = av_rest_api_id
      act = lv_found_api_id
      msg = |Found API ID does not match expected ID| ).
  ENDMETHOD.

  METHOD deploy_api.
    DATA lo_result TYPE REF TO /aws1/cl_agwdeployment.

    " Ensure we have an API created
    IF av_rest_api_id IS INITIAL.
      cl_abap_unit_assert=>fail( msg = 'No REST API available for testing' ).
    ENDIF.

    ao_agw_actions->deploy_api(
      EXPORTING
        iv_rest_api_id = av_rest_api_id
        iv_stage_name  = 'test'
      IMPORTING
        oo_result      = lo_result ).

    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = |Deployment was not created| ).

    cl_abap_unit_assert=>assert_not_initial(
      act = lo_result->get_id( )
      msg = |Deployment ID should not be empty| ).

    " Wait for deployment to complete
    WAIT UP TO 5 SECONDS.
  ENDMETHOD.

  METHOD delete_rest_api.
    DATA lv_delete_api_name TYPE string.
    DATA lv_delete_api_id TYPE /aws1/agwstring.

    " Skip if setup failed
    IF av_rest_api_id IS INITIAL.
      cl_abap_unit_assert=>fail( msg = 'Setup failed - cannot test delete_rest_api' ).
      RETURN.
    ENDIF.

    " Create a dedicated API for delete test
    lv_delete_api_name = |agwtestdelete{ av_lmd_uuid }|.

    TRY.
        " Add wait to avoid rate limiting
        WAIT UP TO 3 SECONDS.
        
        lv_delete_api_id = ao_agw->createrestapi( iv_name = lv_delete_api_name )->get_id( ).

        " Wait before delete
        WAIT UP TO 2 SECONDS.

        " Now test the delete
        ao_agw_actions->delete_rest_api( lv_delete_api_id ).

        " Verify the API is deleted
        WAIT UP TO 2 SECONDS.
        ao_agw->getrestapi( iv_restapiid = lv_delete_api_id ).
        cl_abap_unit_assert=>fail( msg = 'API should have been deleted' ).
      CATCH /aws1/cx_agwnotfoundexception.
        " Expected - API was deleted successfully
      CATCH /aws1/cx_agwtoomanyrequestsex.
        " Rate limit hit - skip this test
        MESSAGE 'Rate limit reached, skipping delete_rest_api test' TYPE 'I'.
      CATCH /aws1/cx_rt_generic INTO DATA(lo_ex).
        cl_abap_unit_assert=>fail( msg = |Delete test failed: { lo_ex->get_text( ) }| ).
    ENDTRY.
  ENDMETHOD.

  METHOD get_root_resource_id.
    DATA lo_resources TYPE REF TO /aws1/cl_agwresources.
    DATA lo_resource TYPE REF TO /aws1/cl_agwresource.

    TRY.
        lo_resources = ao_agw->getresources( iv_restapiid = iv_rest_api_id ).

        LOOP AT lo_resources->get_items( ) INTO lo_resource.
          IF lo_resource->get_path( ) = '/'.
            rv_root_id = lo_resource->get_id( ).
            EXIT.
          ENDIF.
        ENDLOOP.
      CATCH cx_root.
        " Return empty if failed
        CLEAR rv_root_id.
    ENDTRY.
  ENDMETHOD.

  METHOD create_iam_role.
    DATA lv_trust_policy TYPE string.
    DATA lv_policy_doc TYPE string.
    DATA lo_role TYPE REF TO /aws1/cl_iamcreateroleresponse.
    DATA lt_tags TYPE /aws1/cl_iamtag=>tt_taglisttype.
    DATA lo_existing_role TYPE REF TO /aws1/cl_iamgetroleresponse.

    " Create trust policy document for API Gateway
    lv_trust_policy = `{` &&
      `"Version":"2012-10-17",` &&
      `"Statement":[{` &&
      `"Effect":"Allow",` &&
      `"Principal":{"Service":"apigateway.amazonaws.com"},` &&
      `"Action":"sts:AssumeRole"` &&
      `}]}`.

    TRY.
        " Create the IAM role
        lo_role = ao_iam->createrole(
          iv_rolename = av_role_name
          iv_assumerolepolicydocument = lv_trust_policy ).
        av_role_arn = lo_role->get_role( )->get_arn( ).

        " Tag the role
        lt_tags = VALUE /aws1/cl_iamtag=>tt_taglisttype(
          ( NEW /aws1/cl_iamtag( iv_key = 'convert_test' iv_value = 'true' ) ) ).
        ao_iam->tagrole(
          iv_rolename = av_role_name
          it_tags     = lt_tags ).

        " Create and attach policy for DynamoDB access
        lv_policy_doc = `{` &&
          `"Version":"2012-10-17",` &&
          `"Statement":[{` &&
          `"Effect":"Allow",` &&
          `"Action":["dynamodb:Scan","dynamodb:GetItem","dynamodb:PutItem"],` &&
          `"Resource":"*"` &&
          `}]}`.

        ao_iam->putrolepolicy(
          iv_rolename = av_role_name
          iv_policyname = 'DynamoDBAccess'
          iv_policydocument = lv_policy_doc ).

      CATCH /aws1/cx_iamentityalrdyexex.
        " Role already exists, try to use it
        TRY.
            lo_existing_role = ao_iam->getrole( iv_rolename = av_role_name ).
            av_role_arn = lo_existing_role->get_role( )->get_arn( ).
          CATCH cx_root.
            " Ignore errors when getting existing role
        ENDTRY.
      CATCH cx_root.
        " Catch all other exceptions including conversion errors
    ENDTRY.
  ENDMETHOD.

  METHOD create_dynamodb_table.
    DATA lt_key_schema TYPE /aws1/cl_dynkeyschemaelement=>tt_keyschema.
    DATA lt_attributes TYPE /aws1/cl_dynattributedefn=>tt_attributedefinitions.
    DATA lo_throughput TYPE REF TO /aws1/cl_dynprovthroughput.
    DATA lv_table_arn TYPE string.
    DATA lt_tags TYPE /aws1/cl_dyntag=>tt_taglist.

    " Create key schema
    lt_key_schema = VALUE /aws1/cl_dynkeyschemaelement=>tt_keyschema(
      ( NEW /aws1/cl_dynkeyschemaelement(
          iv_attributename = 'id'
          iv_keytype = 'HASH' ) ) ).

    " Create attribute definitions
    lt_attributes = VALUE /aws1/cl_dynattributedefn=>tt_attributedefinitions(
      ( NEW /aws1/cl_dynattributedefn(
          iv_attributename = 'id'
          iv_attributetype = 'S' ) ) ).

    " Create provisioned throughput
    lo_throughput = NEW /aws1/cl_dynprovthroughput(
      iv_readcapacityunits = 5
      iv_writecapacityunits = 5 ).

    TRY.
        " Create the table
        ao_dyn->createtable(
          iv_tablename = av_table_name
          it_keyschema = lt_key_schema
          it_attributedefinitions = lt_attributes
          io_provisionedthroughput = lo_throughput ).

        " Use waiter to wait for table to become active
        ao_dyn->get_waiter( )->tableexists(
          iv_max_wait_time = 200
          iv_tablename     = av_table_name ).

        " Tag the table after it's active
        lv_table_arn = |arn:aws:dynamodb:{ ao_session->get_region( ) }:{ av_account_id }:table/{ av_table_name }|.
        lt_tags = VALUE /aws1/cl_dyntag=>tt_taglist(
          ( NEW /aws1/cl_dyntag( iv_key = 'convert_test' iv_value = 'true' ) ) ).
        ao_dyn->tagresource(
          iv_resourcearn = lv_table_arn
          it_tags        = lt_tags ).

      CATCH /aws1/cx_dynresourceinuseex.
        " Table already exists, ignore
      CATCH cx_root.
        " Catch all other exceptions including conversion errors
    ENDTRY.
  ENDMETHOD.

ENDCLASS.
