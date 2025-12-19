" Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
" SPDX-License-Identifier: Apache-2.0
CLASS ltc_awsex_cl_agw_actions DEFINITION DEFERRED.
CLASS /awsex/cl_agw_actions DEFINITION LOCAL FRIENDS ltc_awsex_cl_agw_actions.

CLASS ltc_awsex_cl_agw_actions DEFINITION FOR TESTING DURATION LONG RISK LEVEL DANGEROUS.

  PRIVATE SECTION.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    CLASS-DATA av_api_name TYPE /aws1/agwstring.
    CLASS-DATA av_rest_api_id TYPE /aws1/agwstring.
    CLASS-DATA av_root_resource_id TYPE /aws1/agwstring.
    CLASS-DATA av_resource_id TYPE /aws1/agwstring.
    CLASS-DATA av_stage_name TYPE /aws1/agwstring.
    CLASS-DATA av_table_name TYPE string.
    CLASS-DATA av_role_arn TYPE /aws1/agwstring.
    CLASS-DATA av_role_name TYPE /aws1/iamrolenametype.
    CLASS-DATA av_setup_failed TYPE abap_bool.

    CLASS-DATA ao_agw TYPE REF TO /aws1/if_agw.
    CLASS-DATA ao_dyn TYPE REF TO /aws1/if_dyn.
    CLASS-DATA ao_iam TYPE REF TO /aws1/if_iam.
    CLASS-DATA ao_session TYPE REF TO /aws1/cl_rt_session_base.
    CLASS-DATA ao_agw_actions TYPE REF TO /awsex/cl_agw_actions.

    METHODS: create_rest_api FOR TESTING RAISING /aws1/cx_rt_generic,
      add_rest_resource FOR TESTING RAISING /aws1/cx_rt_generic,
      put_method FOR TESTING RAISING /aws1/cx_rt_generic,
      put_method_response FOR TESTING RAISING /aws1/cx_rt_generic,
      put_integration FOR TESTING RAISING /aws1/cx_rt_generic,
      put_integration_response FOR TESTING RAISING /aws1/cx_rt_generic,
      create_deployment FOR TESTING RAISING /aws1/cx_rt_generic,
      get_rest_apis FOR TESTING RAISING /aws1/cx_rt_generic,
      get_resources FOR TESTING RAISING /aws1/cx_rt_generic,
      delete_rest_api FOR TESTING RAISING /aws1/cx_rt_generic.

    CLASS-METHODS class_setup RAISING /aws1/cx_rt_generic.
    CLASS-METHODS class_teardown RAISING /aws1/cx_rt_generic.
ENDCLASS.

CLASS ltc_awsex_cl_agw_actions IMPLEMENTATION.

  METHOD class_setup.
    av_setup_failed = abap_false.

    TRY.
        ao_session = /aws1/cl_rt_session_aws=>create( iv_profile_id = cv_pfl ).
        ao_agw = /aws1/cl_agw_factory=>create( ao_session ).
        ao_dyn = /aws1/cl_dyn_factory=>create( ao_session ).
        ao_iam = /aws1/cl_iam_factory=>create( ao_session ).
        ao_agw_actions = NEW /awsex/cl_agw_actions( ).

        " Use util function to get random string
        DATA(lv_random) = /awsex/cl_utils=>get_random_string( ).
        av_api_name = |agw-api-{ lv_random }|.
        av_stage_name = 'test'.

        " Create REST API in setup for all tests to use
        DATA lo_api TYPE REF TO /aws1/cl_agwrestapi.
        ao_agw_actions->create_rest_api(
          EXPORTING
            iv_api_name = av_api_name
          IMPORTING
            oo_result = lo_api ).
        av_rest_api_id = lo_api->get_id( ).

        " Wait for API to be ready
        DATA lv_max_wait TYPE i VALUE 15.
        DATA lv_wait_count TYPE i VALUE 0.
        DATA lv_ready TYPE abap_bool VALUE abap_false.
        DO.
          WAIT UP TO 2 SECONDS.
          lv_wait_count = lv_wait_count + 1.
          TRY.
              ao_agw->getrestapi( iv_restapiid = av_rest_api_id ).
              lv_ready = abap_true.
              EXIT.
            CATCH /aws1/cx_agwnotfoundexception.
              lv_ready = abap_false.
          ENDTRY.
          IF lv_wait_count >= lv_max_wait.
            EXIT.
          ENDIF.
        ENDDO.

        IF lv_ready = abap_false.
          av_setup_failed = abap_true.
          cl_abap_unit_assert=>fail( |REST API { av_rest_api_id } is not ready after { lv_max_wait * 2 } seconds| ).
        ENDIF.

        " Get root resource ID
        DATA(lo_resources) = ao_agw->getresources( iv_restapiid = av_rest_api_id ).
        LOOP AT lo_resources->get_items( ) INTO DATA(lo_resource).
          IF lo_resource->get_path( ) = '/'.
            av_root_resource_id = lo_resource->get_id( ).
            EXIT.
          ENDIF.
        ENDLOOP.

        IF av_root_resource_id IS INITIAL.
          av_setup_failed = abap_true.
          cl_abap_unit_assert=>fail( |Root resource not found for API { av_rest_api_id }| ).
        ENDIF.

        " Create a resource for tests that need it
        DATA lo_res TYPE REF TO /aws1/cl_agwresource.
        ao_agw_actions->add_rest_resource(
          EXPORTING
            iv_rest_api_id = av_rest_api_id
            iv_parent_id = av_root_resource_id
            iv_resource_path = 'users'
          IMPORTING
            oo_result = lo_res ).
        av_resource_id = lo_res->get_id( ).

        " Create DynamoDB table with convert_test tag
        av_table_name = |agw-tbl-{ lv_random }|.
        DATA lt_keyschema TYPE /aws1/cl_dynkeyschemaelement=>tt_keyschema.
        DATA lt_attdef TYPE /aws1/cl_dynattributedefn=>tt_attributedefinitions.

        DATA(lo_key) = NEW /aws1/cl_dynkeyschemaelement( iv_attributename = 'username' iv_keytype = 'HASH' ).
        APPEND lo_key TO lt_keyschema.

        DATA(lo_att) = NEW /aws1/cl_dynattributedefn( iv_attributename = 'username' iv_attributetype = 'S' ).
        APPEND lo_att TO lt_attdef.

        TRY.
            ao_dyn->createtable(
              iv_tablename = av_table_name
              it_keyschema = lt_keyschema
              it_attributedefinitions = lt_attdef
              io_provisionedthroughput = NEW /aws1/cl_dynprovthroughput( iv_readcapacityunits = 5 iv_writecapacityunits = 5 )
              it_tags = VALUE /aws1/cl_dyntag=>tt_taglist(
                ( NEW /aws1/cl_dyntag( iv_key = 'convert_test' iv_value = 'true' ) ) ) ).

            " Wait for table to be active with status-based polling
            DATA lv_table_status TYPE /aws1/dyntablestatus.
            DATA lv_table_max_wait TYPE i VALUE 30.
            DATA lv_table_wait_count TYPE i VALUE 0.
            DO.
              WAIT UP TO 2 SECONDS.
              lv_table_wait_count = lv_table_wait_count + 1.
              DATA(lo_table) = ao_dyn->describetable( iv_tablename = av_table_name ).
              lv_table_status = lo_table->get_table( )->get_tablestatus( ).
              IF lv_table_status = 'ACTIVE' OR lv_table_wait_count >= lv_table_max_wait.
                EXIT.
              ENDIF.
            ENDDO.

            IF lv_table_status <> 'ACTIVE'.
              av_setup_failed = abap_true.
              cl_abap_unit_assert=>fail( |DynamoDB table { av_table_name } did not become active after { lv_table_max_wait * 2 } seconds| ).
            ENDIF.

          CATCH /aws1/cx_dynresourceinuseex.
            " Table already exists from previous run, wait for it to be active
            lv_table_wait_count = 0.
            DO.
              WAIT UP TO 2 SECONDS.
              lv_table_wait_count = lv_table_wait_count + 1.
              lo_table = ao_dyn->describetable( iv_tablename = av_table_name ).
              lv_table_status = lo_table->get_table( )->get_tablestatus( ).
              IF lv_table_status = 'ACTIVE' OR lv_table_wait_count >= lv_table_max_wait.
                EXIT.
              ENDIF.
            ENDDO.
            IF lv_table_status <> 'ACTIVE'.
              av_setup_failed = abap_true.
              cl_abap_unit_assert=>fail( |Existing DynamoDB table { av_table_name } is not active| ).
            ENDIF.
        ENDTRY.

        " Create IAM role for API Gateway with convert_test tag
        av_role_name = |agw-role-{ lv_random }|.
        DATA(lv_policy_document) = CONV /aws1/iampolicydocumenttype( '{"Version":"2012-10-17","Statement":[{"Effect":"Allow","Principal":{"Service":"apigateway.amazonaws.com"},"Action":"sts:AssumeRole"}]}' ).

        TRY.
            DATA(lo_role) = ao_iam->createrole(
              iv_rolename = av_role_name
              iv_assumerolepolicydocument = lv_policy_document
              it_tags = VALUE /aws1/cl_iamtag=>tt_taglisttype(
                ( NEW /aws1/cl_iamtag( iv_key = 'convert_test' iv_value = 'true' ) ) ) ).
            av_role_arn = lo_role->get_role( )->get_arn( ).

            " Attach policy to allow DynamoDB access
            DATA(lv_policy_arn) = CONV /aws1/iamarntype( 'arn:aws:iam::aws:policy/AmazonDynamoDBFullAccess' ).
            ao_iam->attachrolepolicy(
              iv_rolename = av_role_name
              iv_policyarn = lv_policy_arn ).

            " Wait for role to propagate
            WAIT UP TO 10 SECONDS.

          CATCH /aws1/cx_iamentityalrdyexex.
            " Role already exists from previous run, get its ARN
            DATA(lo_existing_role) = ao_iam->getrole( iv_rolename = av_role_name ).
            av_role_arn = lo_existing_role->get_role( )->get_arn( ).
        ENDTRY.

      CATCH /aws1/cx_rt_generic INTO DATA(lo_gen_ex).
        av_setup_failed = abap_true.
        cl_abap_unit_assert=>fail( |Setup failed: { lo_gen_ex->get_text( ) }| ).
    ENDTRY.

  ENDMETHOD.

  METHOD class_teardown.
    " Clean up DynamoDB table
    IF av_table_name IS NOT INITIAL.
      TRY.
          ao_dyn->deletetable( iv_tablename = av_table_name ).
        CATCH /aws1/cx_dynresourcenotfoundex.
          " Table already deleted
      ENDTRY.
    ENDIF.

    " Clean up IAM role
    IF av_role_name IS NOT INITIAL.
      TRY.
          " Detach policies first
          DATA(lo_policies) = ao_iam->listattachedrolepolicies( iv_rolename = av_role_name ).
          LOOP AT lo_policies->get_attachedpolicies( ) INTO DATA(lo_policy).
            ao_iam->detachrolepolicy(
              iv_rolename = av_role_name
              iv_policyarn = lo_policy->get_policyarn( ) ).
          ENDLOOP.
          ao_iam->deleterole( iv_rolename = av_role_name ).
        CATCH /aws1/cx_iamnosuchentityex.
          " Role already deleted
      ENDTRY.
    ENDIF.

    " Clean up any remaining APIs created during tests (tagged with convert_test)
    TRY.
        DATA(lo_apis) = ao_agw->getrestapis( ).
        LOOP AT lo_apis->get_items( ) INTO DATA(lo_api).
          DATA(lv_api_name) = lo_api->get_name( ).
          IF lv_api_name CS 'agw-api-'.
            TRY.
                ao_agw->deleterestapi( iv_restapiid = lo_api->get_id( ) ).
              CATCH /aws1/cx_rt_generic.
                " Continue cleanup even if deletion fails
            ENDTRY.
          ENDIF.
        ENDLOOP.
      CATCH /aws1/cx_rt_generic.
        " Ignore errors during cleanup
    ENDTRY.
  ENDMETHOD.

  METHOD create_rest_api.
    " Fail the test if setup failed
    IF av_setup_failed = abap_true.
      cl_abap_unit_assert=>fail( 'Test cannot run because class_setup failed' ).
    ENDIF.

    " Create a new API for this specific test
    DATA(lv_test_api_name) = |agw-test-api-{ /awsex/cl_utils=>get_random_string( ) }|.
    DATA lo_result TYPE REF TO /aws1/cl_agwrestapi.

    ao_agw_actions->create_rest_api(
      EXPORTING
        iv_api_name = lv_test_api_name
      IMPORTING
        oo_result = lo_result ).

    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = |REST API was not created| ).

    cl_abap_unit_assert=>assert_equals(
      exp = lv_test_api_name
      act = lo_result->get_name( )
      msg = |REST API name does not match| ).

    " Clean up test API
    TRY.
        ao_agw->deleterestapi( iv_restapiid = lo_result->get_id( ) ).
      CATCH /aws1/cx_rt_generic.
        " Ignore cleanup errors
    ENDTRY.

  ENDMETHOD.

  METHOD add_rest_resource.
    " Fail the test if setup failed
    IF av_setup_failed = abap_true.
      cl_abap_unit_assert=>fail( 'Test cannot run because class_setup failed' ).
    ENDIF.

    DATA lo_result TYPE REF TO /aws1/cl_agwresource.

    ao_agw_actions->add_rest_resource(
      EXPORTING
        iv_rest_api_id = av_rest_api_id
        iv_parent_id = av_root_resource_id
        iv_resource_path = 'testresource'
      IMPORTING
        oo_result = lo_result ).

    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = |Resource was not created| ).

    cl_abap_unit_assert=>assert_equals(
      exp = 'testresource'
      act = lo_result->get_pathpart( )
      msg = |Resource path does not match| ).

  ENDMETHOD.

  METHOD put_method.
    " Fail the test if setup failed
    IF av_setup_failed = abap_true.
      cl_abap_unit_assert=>fail( 'Test cannot run because class_setup failed' ).
    ENDIF.

    DATA lo_result TYPE REF TO /aws1/cl_agwmethod.

    ao_agw_actions->put_method(
      EXPORTING
        iv_rest_api_id = av_rest_api_id
        iv_resource_id = av_resource_id
        iv_http_method = 'GET'
      IMPORTING
        oo_result = lo_result ).

    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = |Method was not created| ).

    cl_abap_unit_assert=>assert_equals(
      exp = 'GET'
      act = lo_result->get_httpmethod( )
      msg = |HTTP method does not match| ).

  ENDMETHOD.

  METHOD put_method_response.
    " Fail the test if setup failed
    IF av_setup_failed = abap_true.
      cl_abap_unit_assert=>fail( 'Test cannot run because class_setup failed' ).
    ENDIF.

    " First create the method
    TRY.
        ao_agw->putmethod(
          iv_restapiid = av_rest_api_id
          iv_resourceid = av_resource_id
          iv_httpmethod = 'POST'
          iv_authorizationtype = 'NONE' ).
      CATCH /aws1/cx_agwconflictexception.
        " Method already exists, continue
    ENDTRY.

    DATA lo_result TYPE REF TO /aws1/cl_agwmethodresponse.

    ao_agw_actions->put_method_response(
      EXPORTING
        iv_rest_api_id = av_rest_api_id
        iv_resource_id = av_resource_id
        iv_http_method = 'POST'
      IMPORTING
        oo_result = lo_result ).

    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = |Method response was not created| ).

    cl_abap_unit_assert=>assert_equals(
      exp = '200'
      act = lo_result->get_statuscode( )
      msg = |Status code does not match| ).

  ENDMETHOD.

  METHOD put_integration.
    " Fail the test if setup failed
    IF av_setup_failed = abap_true.
      cl_abap_unit_assert=>fail( 'Test cannot run because class_setup failed' ).
    ENDIF.

    " First create the method
    TRY.
        ao_agw->putmethod(
          iv_restapiid = av_rest_api_id
          iv_resourceid = av_resource_id
          iv_httpmethod = 'PUT'
          iv_authorizationtype = 'NONE' ).
      CATCH /aws1/cx_agwconflictexception.
        " Method already exists, continue
    ENDTRY.

    DATA lo_result TYPE REF TO /aws1/cl_agwintegration.
    DATA(lv_region) = ao_session->get_region( ).
    DATA(lv_service_uri) = |arn:aws:apigateway:{ lv_region }:dynamodb:action/Scan|.

    ao_agw_actions->put_integration(
      EXPORTING
        iv_rest_api_id = av_rest_api_id
        iv_resource_id = av_resource_id
        iv_http_method = 'PUT'
        iv_service_uri = lv_service_uri
        iv_role_arn = av_role_arn
        iv_table_name = av_table_name
      IMPORTING
        oo_result = lo_result ).

    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = |Integration was not created| ).

    cl_abap_unit_assert=>assert_equals(
      exp = 'AWS'
      act = lo_result->get_type( )
      msg = |Integration type does not match| ).

  ENDMETHOD.

  METHOD put_integration_response.
    " Fail the test if setup failed
    IF av_setup_failed = abap_true.
      cl_abap_unit_assert=>fail( 'Test cannot run because class_setup failed' ).
    ENDIF.

    " First create the method and integration
    TRY.
        ao_agw->putmethod(
          iv_restapiid = av_rest_api_id
          iv_resourceid = av_resource_id
          iv_httpmethod = 'DELETE'
          iv_authorizationtype = 'NONE' ).

        DATA(lv_region) = ao_session->get_region( ).
        DATA(lv_service_uri) = |arn:aws:apigateway:{ lv_region }:dynamodb:action/Scan|.
        DATA lt_templates TYPE /aws1/cl_agwmapofstrtostr_w=>tt_mapofstringtostring.
        DATA ls_template TYPE /aws1/cl_agwmapofstrtostr_w=>ts_mapofstringtostring_maprow.
        ls_template-key = 'application/json'.
        ls_template-value = NEW /aws1/cl_agwmapofstrtostr_w( '{"TableName": "' && av_table_name && '"}' ).
        INSERT ls_template INTO TABLE lt_templates.

        ao_agw->putintegration(
          iv_restapiid = av_rest_api_id
          iv_resourceid = av_resource_id
          iv_httpmethod = 'DELETE'
          iv_type = 'AWS'
          iv_integrationhttpmethod = 'POST'
          iv_credentials = av_role_arn
          it_requesttemplates = lt_templates
          iv_uri = lv_service_uri
          iv_passthroughbehavior = 'WHEN_NO_TEMPLATES' ).
      CATCH /aws1/cx_agwconflictexception.
        " Already exists, continue
    ENDTRY.

    DATA lo_result TYPE REF TO /aws1/cl_agwintegrationrsp.

    ao_agw_actions->put_integration_response(
      EXPORTING
        iv_rest_api_id = av_rest_api_id
        iv_resource_id = av_resource_id
        iv_http_method = 'DELETE'
      IMPORTING
        oo_result = lo_result ).

    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = |Integration response was not created| ).

    cl_abap_unit_assert=>assert_equals(
      exp = '200'
      act = lo_result->get_statuscode( )
      msg = |Status code does not match| ).

  ENDMETHOD.

  METHOD create_deployment.
    " Fail the test if setup failed
    IF av_setup_failed = abap_true.
      cl_abap_unit_assert=>fail( 'Test cannot run because class_setup failed' ).
    ENDIF.

    DATA lo_result TYPE REF TO /aws1/cl_agwdeployment.

    ao_agw_actions->create_deployment(
      EXPORTING
        iv_rest_api_id = av_rest_api_id
        iv_stage_name = av_stage_name
      IMPORTING
        oo_result = lo_result ).

    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = |Deployment was not created| ).

  ENDMETHOD.

  METHOD get_rest_apis.
    " Fail the test if setup failed
    IF av_setup_failed = abap_true.
      cl_abap_unit_assert=>fail( 'Test cannot run because class_setup failed' ).
    ENDIF.

    DATA lo_result TYPE REF TO /aws1/cl_agwrestapis.

    ao_agw_actions->get_rest_apis(
      IMPORTING
        oo_result = lo_result ).

    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = |REST APIs were not retrieved| ).

    DATA(lt_apis) = lo_result->get_items( ).
    DATA(lv_found) = abap_false.
    LOOP AT lt_apis INTO DATA(lo_api).
      IF lo_api->get_id( ) = av_rest_api_id.
        lv_found = abap_true.
        EXIT.
      ENDIF.
    ENDLOOP.

    cl_abap_unit_assert=>assert_true(
      act = lv_found
      msg = |Created REST API was not found in the list| ).

  ENDMETHOD.

  METHOD get_resources.
    " Fail the test if setup failed
    IF av_setup_failed = abap_true.
      cl_abap_unit_assert=>fail( 'Test cannot run because class_setup failed' ).
    ENDIF.

    DATA lo_result TYPE REF TO /aws1/cl_agwresources.

    ao_agw_actions->get_resources(
      EXPORTING
        iv_rest_api_id = av_rest_api_id
      IMPORTING
        oo_result = lo_result ).

    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = |Resources were not retrieved| ).

    DATA(lt_resources) = lo_result->get_items( ).
    cl_abap_unit_assert=>assert_not_initial(
      act = lt_resources
      msg = |No resources found| ).

  ENDMETHOD.

  METHOD delete_rest_api.
    " Fail the test if setup failed
    IF av_setup_failed = abap_true.
      cl_abap_unit_assert=>fail( 'Test cannot run because class_setup failed' ).
    ENDIF.

    " Create a temporary API for deletion test
    DATA(lv_test_api_name) = |agw-del-api-{ /awsex/cl_utils=>get_random_string( ) }|.
    DATA lo_api TYPE REF TO /aws1/cl_agwrestapi.
    
    TRY.
        ao_agw_actions->create_rest_api(
          EXPORTING
            iv_api_name = lv_test_api_name
          IMPORTING
            oo_result = lo_api ).
        DATA(lv_test_api_id) = lo_api->get_id( ).

        " Wait for API to be available
        WAIT UP TO 3 SECONDS.

        " Now test the delete with retry logic for rate limiting
        DATA lv_retry_count TYPE i VALUE 0.
        DATA lv_deleted TYPE abap_bool VALUE abap_false.
        DO 3 TIMES.
          TRY.
              ao_agw_actions->delete_rest_api(
                iv_rest_api_id = lv_test_api_id ).
              lv_deleted = abap_true.
              EXIT.
            CATCH /aws1/cx_agwtoomanyrequestsex.
              " Rate limited, wait and retry
              lv_retry_count = lv_retry_count + 1.
              IF lv_retry_count < 3.
                WAIT UP TO 2 SECONDS.
              ENDIF.
          ENDTRY.
        ENDDO.

        IF lv_deleted = abap_false.
          cl_abap_unit_assert=>fail( 'Delete API failed due to rate limiting after 3 retries' ).
        ENDIF.

        " Verify deletion
        WAIT UP TO 2 SECONDS.
        DATA(lv_found) = abap_false.
        TRY.
            DATA(lo_apis) = ao_agw->getrestapis( ).
            LOOP AT lo_apis->get_items( ) INTO DATA(lo_api_check).
              IF lo_api_check->get_id( ) = lv_test_api_id.
                lv_found = abap_true.
                EXIT.
              ENDIF.
            ENDLOOP.
          CATCH /aws1/cx_agwnotfoundexception.
            lv_found = abap_false.
        ENDTRY.

        cl_abap_unit_assert=>assert_false(
          act = lv_found
          msg = |REST API was not deleted| ).

      CATCH /aws1/cx_rt_generic INTO DATA(lo_ex).
        cl_abap_unit_assert=>fail( |Test failed with exception: { lo_ex->get_text( ) }| ).
    ENDTRY.

  ENDMETHOD.

ENDCLASS.
