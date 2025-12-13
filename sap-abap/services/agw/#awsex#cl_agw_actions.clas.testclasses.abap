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

    CLASS-METHODS class_setup RAISING /aws1/cx_rt_generic.
    CLASS-METHODS class_teardown RAISING /aws1/cx_rt_generic.

    METHODS get_root_resource_id
      IMPORTING
        iv_rest_api_id   TYPE /aws1/agwstring
      RETURNING
        VALUE(rv_root_id) TYPE /aws1/agwstring
      RAISING
        /aws1/cx_rt_generic.

    CLASS-METHODS create_iam_role
      RAISING
        /aws1/cx_rt_generic.

    CLASS-METHODS create_dynamodb_table
      RAISING
        /aws1/cx_rt_generic.

ENDCLASS.

CLASS ltc_awsex_cl_agw_actions IMPLEMENTATION.

  METHOD class_setup.
    ao_session = /aws1/cl_rt_session_aws=>create( iv_profile_id = cv_pfl ).
    ao_agw = /aws1/cl_agw_factory=>create( ao_session ).
    ao_iam = /aws1/cl_iam_factory=>create( ao_session ).
    ao_dyn = /aws1/cl_dyn_factory=>create( ao_session ).
    ao_agw_actions = NEW /awsex/cl_agw_actions( ).

    " Get account ID
    av_account_id = ao_session->get_account_id( ).

    " Generate unique identifiers using random string only
    DATA lv_random TYPE string.
    lv_random = /awsex/cl_utils=>get_random_string( ).
    TRANSLATE lv_random TO LOWER CASE.
    
    av_api_name = |agwtest{ lv_random }|.
    av_role_name = |agwtstrole{ lv_random }|.
    av_table_name = |agwtsttbl{ lv_random }|.
    av_lmd_uuid = lv_random.

    " Create IAM role and DynamoDB table for integration method test
    create_iam_role( ).
    create_dynamodb_table( ).

    " Create a primary REST API for testing
    TRY.
        DATA(lo_api) = ao_agw->createrestapi( iv_name = av_api_name ).
        av_rest_api_id = lo_api->get_id( ).
        
        " Tag the API
        DATA lt_tags TYPE /aws1/cl_agwmapofstrtostr_w=>tt_mapofstringtostring.
        DATA ls_tag TYPE /aws1/cl_agwmapofstrtostr_w=>ts_mapofstringtostring_maprow.
        ls_tag-key = 'convert_test'.
        ls_tag-value = NEW /aws1/cl_agwmapofstrtostr_w( iv_value = 'true' ).
        INSERT ls_tag INTO TABLE lt_tags.
        ao_agw->tagresource(
          iv_resourcearn = |arn:aws:apigateway:{ ao_session->get_region( ) }::/restapis/{ av_rest_api_id }|
          it_tags        = lt_tags ).
        
        " Get root resource ID
        av_root_id = get_root_resource_id( av_rest_api_id ).
      CATCH /aws1/cx_rt_generic INTO DATA(lo_ex).
        " If setup fails, tests will be skipped
    ENDTRY.

    " Wait for resources to be ready
    WAIT UP TO 5 SECONDS.
  ENDMETHOD.

  METHOD class_teardown.
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
          DATA(lo_attached_policies) = ao_iam->listattachedrolepolicies( iv_rolename = av_role_name ).
          LOOP AT lo_attached_policies->get_attachedpolicies( ) INTO DATA(lo_policy).
            ao_iam->detachrolepolicy(
              iv_rolename = av_role_name
              iv_policyarn = lo_policy->get_policyarn( ) ).
          ENDLOOP.
          
          " Delete inline policies
          DATA(lo_policy_names) = ao_iam->listrolepolicies( iv_rolename = av_role_name ).
          LOOP AT lo_policy_names->get_policynames( ) INTO DATA(lo_policy_name).
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

    " Clean up REST APIs
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

    " Clean up any APIs matching our test pattern with convert_test tag
    TRY.
        DATA(lo_apis) = ao_agw->getrestapis( ).
        LOOP AT lo_apis->get_items( ) INTO DATA(lo_api).
          DATA(lv_api_name) = lo_api->get_name( ).
          IF lv_api_name CP 'agwtest*' OR lv_api_name CP 'agwintg*'.
            TRY.
                DATA(lv_api_id) = lo_api->get_id( ).
                DATA(lv_tags) = ao_agw->gettags( 
                  iv_resourcearn = |arn:aws:apigateway:{ ao_session->get_region( ) }::/restapis/{ lv_api_id }| 
                )->get_tags( ).
                IF line_exists( lv_tags[ key = 'convert_test' ] ).
                  ao_agw->deleterestapi( iv_restapiid = lv_api_id ).
                  WAIT UP TO 2 SECONDS.
                ENDIF.
              CATCH /aws1/cx_rt_generic.
                " Continue with next API
            ENDTRY.
          ENDIF.
        ENDLOOP.
      CATCH /aws1/cx_rt_generic.
        " Ignore cleanup errors
    ENDTRY.
  ENDMETHOD.

  METHOD create_rest_api.
    DATA lo_result TYPE REF TO /aws1/cl_agwrestapi.
    DATA(lv_test_api_name) = |agwtestcreate{ av_lmd_uuid }|.

    ao_agw_actions->create_rest_api(
      EXPORTING
        iv_api_name = lv_test_api_name
      IMPORTING
        oo_result   = lo_result ).

    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = |REST API was not created| ).

    DATA(lv_new_api_id) = lo_result->get_id( ).

    cl_abap_unit_assert=>assert_not_initial(
      act = lv_new_api_id
      msg = |REST API ID should not be empty| ).

    " Tag the resource for cleanup
    TRY.
        DATA lt_tags TYPE /aws1/cl_agwmapofstrtostr_w=>tt_mapofstringtostring.
        DATA ls_tag TYPE /aws1/cl_agwmapofstrtostr_w=>ts_mapofstringtostring_maprow.
        ls_tag-key = 'convert_test'.
        ls_tag-value = NEW /aws1/cl_agwmapofstrtostr_w( iv_value = 'true' ).
        INSERT ls_tag INTO TABLE lt_tags.
        ao_agw->tagresource(
          iv_resourcearn = |arn:aws:apigateway:{ ao_session->get_region( ) }::/restapis/{ lv_new_api_id }|
          it_tags        = lt_tags ).
      CATCH /aws1/cx_rt_generic.
        " Tagging failed, but test should continue
    ENDTRY.

    " Clean up the test API immediately
    TRY.
        ao_agw->deleterestapi( iv_restapiid = lv_new_api_id ).
      CATCH /aws1/cx_rt_generic.
        " Ignore cleanup errors
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
    " Create a dedicated API for delete test
    DATA(lv_delete_api_name) = |agwtestdelete{ av_lmd_uuid }|.
    DATA lv_delete_api_id TYPE /aws1/agwstring.

    TRY.
        DATA(lo_api) = ao_agw->createrestapi( iv_name = lv_delete_api_name ).
        lv_delete_api_id = lo_api->get_id( ).

        " Tag the API
        DATA lt_tags TYPE /aws1/cl_agwmapofstrtostr_w=>tt_mapofstringtostring.
        DATA ls_tag TYPE /aws1/cl_agwmapofstrtostr_w=>ts_mapofstringtostring_maprow.
        ls_tag-key = 'convert_test'.
        ls_tag-value = NEW /aws1/cl_agwmapofstrtostr_w( iv_value = 'true' ).
        INSERT ls_tag INTO TABLE lt_tags.
        ao_agw->tagresource(
          iv_resourcearn = |arn:aws:apigateway:{ ao_session->get_region( ) }::/restapis/{ lv_delete_api_id }|
          it_tags        = lt_tags ).

        " Now test the delete
        ao_agw_actions->delete_rest_api( lv_delete_api_id ).

        " Verify the API is deleted
        ao_agw->getrestapi( iv_restapiid = lv_delete_api_id ).
        cl_abap_unit_assert=>fail( msg = 'API should have been deleted' ).
      CATCH /aws1/cx_agwnotfoundexception.
        " Expected - API was deleted successfully
      CATCH /aws1/cx_rt_generic INTO DATA(lo_ex).
        cl_abap_unit_assert=>fail( msg = |Delete test failed: { lo_ex->get_text( ) }| ).
    ENDTRY.
  ENDMETHOD.

  METHOD get_root_resource_id.
    DATA lo_resources TYPE REF TO /aws1/cl_agwresources.

    TRY.
        lo_resources = ao_agw->getresources( iv_restapiid = iv_rest_api_id ).

        LOOP AT lo_resources->get_items( ) INTO DATA(lo_resource).
          IF lo_resource->get_path( ) = '/'.
            rv_root_id = lo_resource->get_id( ).
            EXIT.
          ENDIF.
        ENDLOOP.
      CATCH /aws1/cx_rt_generic INTO DATA(lo_ex).
        cl_abap_unit_assert=>fail( msg = |Failed to get root resource: { lo_ex->get_text( ) }| ).
    ENDTRY.

    IF rv_root_id IS INITIAL.
      cl_abap_unit_assert=>fail( msg = 'Could not find root resource' ).
    ENDIF.
  ENDMETHOD.

  METHOD create_iam_role.
    " Create trust policy document for API Gateway
    DATA(lv_trust_policy) = `{` &&
      `"Version":"2012-10-17",` &&
      `"Statement":[{` &&
      `"Effect":"Allow",` &&
      `"Principal":{"Service":"apigateway.amazonaws.com"},` &&
      `"Action":"sts:AssumeRole"` &&
      `}]}`.

    TRY.
        " Create the IAM role
        DATA(lo_role) = ao_iam->createrole(
          iv_rolename = av_role_name
          iv_assumerolepolicydocument = lv_trust_policy ).
        av_role_arn = lo_role->get_role( )->get_arn( ).

        " Tag the role
        DATA(lt_tags) = VALUE /aws1/cl_iamtag=>tt_taglisttype(
          ( NEW /aws1/cl_iamtag( iv_key = 'convert_test' iv_value = 'true' ) ) ).
        ao_iam->tagrole(
          iv_rolename = av_role_name
          it_tags     = lt_tags ).

        " Create and attach policy for DynamoDB access
        DATA(lv_policy_doc) = `{` &&
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
        DATA(lo_existing_role) = ao_iam->getrole( iv_rolename = av_role_name ).
        av_role_arn = lo_existing_role->get_role( )->get_arn( ).
    ENDTRY.
  ENDMETHOD.

  METHOD create_dynamodb_table.
    " Create key schema
    DATA(lt_key_schema) = VALUE /aws1/cl_dynkeyschemaelement=>tt_keyschema(
      ( NEW /aws1/cl_dynkeyschemaelement(
          iv_attributename = 'id'
          iv_keytype = 'HASH' ) ) ).

    " Create attribute definitions
    DATA(lt_attributes) = VALUE /aws1/cl_dynattributedefn=>tt_attributedefinitions(
      ( NEW /aws1/cl_dynattributedefn(
          iv_attributename = 'id'
          iv_attributetype = 'S' ) ) ).

    " Create provisioned throughput
    DATA(lo_throughput) = NEW /aws1/cl_dynprovthroughput(
      iv_readcapacityunits = 5
      iv_writecapacityunits = 5 ).

    TRY.
        " Create the table
        ao_dyn->createtable(
          iv_tablename = av_table_name
          it_keyschema = lt_key_schema
          it_attributedefinitions = lt_attributes
          io_provisionedthroughput = lo_throughput ).

        " Wait for table to become active before tagging
        DATA lv_max_attempts TYPE i VALUE 10.
        DATA lv_attempt TYPE i VALUE 0.
        DATA lv_table_active TYPE abap_bool VALUE abap_false.

        WHILE lv_attempt < lv_max_attempts AND lv_table_active = abap_false.
          WAIT UP TO 3 SECONDS.
          TRY.
              DATA(lo_table_desc) = ao_dyn->describetable( iv_tablename = av_table_name ).
              IF lo_table_desc->get_table( )->get_tablestatus( ) = 'ACTIVE'.
                lv_table_active = abap_true.
              ENDIF.
            CATCH /aws1/cx_rt_generic.
              " Continue waiting
          ENDTRY.
          lv_attempt = lv_attempt + 1.
        ENDWHILE.

        " Tag the table after it's active
        IF lv_table_active = abap_true.
          DATA(lv_table_arn) = |arn:aws:dynamodb:{ ao_session->get_region( ) }:{ av_account_id }:table/{ av_table_name }|.
          DATA(lt_tags) = VALUE /aws1/cl_dyntag=>tt_taglist(
            ( NEW /aws1/cl_dyntag( iv_key = 'convert_test' iv_value = 'true' ) ) ).
          ao_dyn->tagresource(
            iv_resourcearn = lv_table_arn
            it_tags        = lt_tags ).
        ENDIF.

      CATCH /aws1/cx_dynresourceinuseex.
        " Table already exists, ignore
    ENDTRY.
  ENDMETHOD.

ENDCLASS.
