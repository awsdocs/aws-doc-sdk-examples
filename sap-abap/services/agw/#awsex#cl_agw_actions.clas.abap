" Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
" SPDX-License-Identifier: Apache-2.0
CLASS /awsex/cl_agw_actions DEFINITION
  PUBLIC
  FINAL
  CREATE PUBLIC .

  PUBLIC SECTION.

    METHODS create_rest_api
      IMPORTING
        !iv_api_name        TYPE /aws1/agwstring
      EXPORTING
        !oo_result          TYPE REF TO /aws1/cl_agwrestapi
      RAISING
        /aws1/cx_rt_generic.

    METHODS add_rest_resource
      IMPORTING
        !iv_rest_api_id     TYPE /aws1/agwstring
        !iv_parent_id       TYPE /aws1/agwstring
        !iv_resource_path   TYPE /aws1/agwstring
      EXPORTING
        !oo_result          TYPE REF TO /aws1/cl_agwresource
      RAISING
        /aws1/cx_rt_generic.

    METHODS put_method
      IMPORTING
        !iv_rest_api_id     TYPE /aws1/agwstring
        !iv_resource_id     TYPE /aws1/agwstring
        !iv_http_method     TYPE /aws1/agwstring
      EXPORTING
        !oo_result          TYPE REF TO /aws1/cl_agwmethod
      RAISING
        /aws1/cx_rt_generic.

    METHODS put_method_response
      IMPORTING
        !iv_rest_api_id     TYPE /aws1/agwstring
        !iv_resource_id     TYPE /aws1/agwstring
        !iv_http_method     TYPE /aws1/agwstring
      EXPORTING
        !oo_result          TYPE REF TO /aws1/cl_agwmethodresponse
      RAISING
        /aws1/cx_rt_generic.

    METHODS put_integration
      IMPORTING
        !iv_rest_api_id     TYPE /aws1/agwstring
        !iv_resource_id     TYPE /aws1/agwstring
        !iv_http_method     TYPE /aws1/agwstring
        !iv_service_uri     TYPE /aws1/agwstring
        !iv_role_arn        TYPE /aws1/agwstring
        !iv_table_name      TYPE /aws1/agwstring
      EXPORTING
        !oo_result          TYPE REF TO /aws1/cl_agwintegration
      RAISING
        /aws1/cx_rt_generic.

    METHODS put_integration_response
      IMPORTING
        !iv_rest_api_id     TYPE /aws1/agwstring
        !iv_resource_id     TYPE /aws1/agwstring
        !iv_http_method     TYPE /aws1/agwstring
      EXPORTING
        !oo_result          TYPE REF TO /aws1/cl_agwintegrationrsp
      RAISING
        /aws1/cx_rt_generic.

    METHODS create_deployment
      IMPORTING
        !iv_rest_api_id     TYPE /aws1/agwstring
        !iv_stage_name      TYPE /aws1/agwstring
      EXPORTING
        !oo_result          TYPE REF TO /aws1/cl_agwdeployment
      RAISING
        /aws1/cx_rt_generic.

    METHODS get_rest_apis
      EXPORTING
        !oo_result          TYPE REF TO /aws1/cl_agwrestapis
      RAISING
        /aws1/cx_rt_generic.

    METHODS delete_rest_api
      IMPORTING
        !iv_rest_api_id     TYPE /aws1/agwstring
      RAISING
        /aws1/cx_rt_generic.

    METHODS get_resources
      IMPORTING
        !iv_rest_api_id     TYPE /aws1/agwstring
      EXPORTING
        !oo_result          TYPE REF TO /aws1/cl_agwresources
      RAISING
        /aws1/cx_rt_generic.

  PROTECTED SECTION.
  PRIVATE SECTION.
ENDCLASS.



CLASS /awsex/cl_agw_actions IMPLEMENTATION.


  METHOD create_rest_api.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_agw) = /aws1/cl_agw_factory=>create( lo_session ).

    " snippet-start:[agw.abapv1.create_rest_api]
    TRY.
        " iv_api_name = 'MyDemoAPI'
        oo_result = lo_agw->createrestapi(
          iv_name = iv_api_name ).
        MESSAGE 'REST API created successfully.' TYPE 'I'.
      CATCH /aws1/cx_agwbadrequestex INTO DATA(lo_bad_request_ex).
        MESSAGE lo_bad_request_ex->get_text( ) TYPE 'E'.
      CATCH /aws1/cx_agwconflictexception INTO DATA(lo_conflict_ex).
        MESSAGE lo_conflict_ex->get_text( ) TYPE 'E'.
      CATCH /aws1/cx_agwlimitexceededex INTO DATA(lo_limit_ex).
        MESSAGE lo_limit_ex->get_text( ) TYPE 'E'.
    ENDTRY.
    " snippet-end:[agw.abapv1.create_rest_api]
  ENDMETHOD.


  METHOD add_rest_resource.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_agw) = /aws1/cl_agw_factory=>create( lo_session ).

    " snippet-start:[agw.abapv1.create_resource]
    TRY.
        " iv_rest_api_id = 'abc123def4'
        " iv_parent_id = 'xyz789'
        " iv_resource_path = 'users'
        oo_result = lo_agw->createresource(
          iv_restapiid = iv_rest_api_id
          iv_parentid = iv_parent_id
          iv_pathpart = iv_resource_path ).
        MESSAGE 'Resource created successfully.' TYPE 'I'.
      CATCH /aws1/cx_agwbadrequestex INTO DATA(lo_bad_request_ex).
        MESSAGE lo_bad_request_ex->get_text( ) TYPE 'E'.
      CATCH /aws1/cx_agwconflictexception INTO DATA(lo_conflict_ex).
        MESSAGE lo_conflict_ex->get_text( ) TYPE 'E'.
      CATCH /aws1/cx_agwnotfoundexception INTO DATA(lo_not_found_ex).
        MESSAGE lo_not_found_ex->get_text( ) TYPE 'E'.
    ENDTRY.
    " snippet-end:[agw.abapv1.create_resource]
  ENDMETHOD.


  METHOD put_method.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_agw) = /aws1/cl_agw_factory=>create( lo_session ).

    " snippet-start:[agw.abapv1.put_method]
    TRY.
        " iv_rest_api_id = 'abc123def4'
        " iv_resource_id = 'xyz789'
        " iv_http_method = 'GET'
        oo_result = lo_agw->putmethod(
          iv_restapiid = iv_rest_api_id
          iv_resourceid = iv_resource_id
          iv_httpmethod = iv_http_method
          iv_authorizationtype = 'NONE' ).
        MESSAGE 'Method created successfully.' TYPE 'I'.
      CATCH /aws1/cx_agwbadrequestex INTO DATA(lo_bad_request_ex).
        MESSAGE lo_bad_request_ex->get_text( ) TYPE 'E'.
      CATCH /aws1/cx_agwconflictexception INTO DATA(lo_conflict_ex).
        MESSAGE lo_conflict_ex->get_text( ) TYPE 'E'.
      CATCH /aws1/cx_agwnotfoundexception INTO DATA(lo_not_found_ex).
        MESSAGE lo_not_found_ex->get_text( ) TYPE 'E'.
    ENDTRY.
    " snippet-end:[agw.abapv1.put_method]
  ENDMETHOD.


  METHOD put_method_response.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_agw) = /aws1/cl_agw_factory=>create( lo_session ).

    " snippet-start:[agw.abapv1.put_method_response]
    TRY.
        " iv_rest_api_id = 'abc123def4'
        " iv_resource_id = 'xyz789'
        " iv_http_method = 'GET'
        DATA lt_models TYPE /aws1/cl_agwmapofstrtostr_w=>tt_mapofstringtostring.
        DATA ls_model LIKE LINE OF lt_models.
        ls_model-key = 'application/json'.
        ls_model-value = 'Empty'.
        APPEND ls_model TO lt_models.

        oo_result = lo_agw->putmethodresponse(
          iv_restapiid = iv_rest_api_id
          iv_resourceid = iv_resource_id
          iv_httpmethod = iv_http_method
          iv_statuscode = '200'
          it_responsemodels = lt_models ).
        MESSAGE 'Method response created successfully.' TYPE 'I'.
      CATCH /aws1/cx_agwbadrequestex INTO DATA(lo_bad_request_ex).
        MESSAGE lo_bad_request_ex->get_text( ) TYPE 'E'.
      CATCH /aws1/cx_agwconflictexception INTO DATA(lo_conflict_ex).
        MESSAGE lo_conflict_ex->get_text( ) TYPE 'E'.
      CATCH /aws1/cx_agwnotfoundexception INTO DATA(lo_not_found_ex).
        MESSAGE lo_not_found_ex->get_text( ) TYPE 'E'.
    ENDTRY.
    " snippet-end:[agw.abapv1.put_method_response]
  ENDMETHOD.


  METHOD put_integration.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_agw) = /aws1/cl_agw_factory=>create( lo_session ).

    " snippet-start:[agw.abapv1.put_integration]
    TRY.
        " iv_rest_api_id = 'abc123def4'
        " iv_resource_id = 'xyz789'
        " iv_http_method = 'GET'
        " iv_service_uri = 'arn:aws:apigateway:us-east-1:dynamodb:action/Scan'
        " iv_role_arn = 'arn:aws:iam::123456789012:role/MyAPIGatewayRole'
        " iv_table_name = 'MyDynamoDBTable'
        DATA lt_templates TYPE /aws1/cl_agwmapofstrtostr_w=>tt_mapofstringtostring.
        DATA ls_template LIKE LINE OF lt_templates.
        ls_template-key = 'application/json'.
        ls_template-value = '{"TableName": "' && iv_table_name && '"}'.
        APPEND ls_template TO lt_templates.

        oo_result = lo_agw->putintegration(
          iv_restapiid = iv_rest_api_id
          iv_resourceid = iv_resource_id
          iv_httpmethod = iv_http_method
          iv_type = 'AWS'
          iv_integrationhttpmethod = 'POST'
          iv_credentials = iv_role_arn
          it_requesttemplates = lt_templates
          iv_uri = iv_service_uri
          iv_passthroughbehavior = 'WHEN_NO_TEMPLATES' ).
        MESSAGE 'Integration created successfully.' TYPE 'I'.
      CATCH /aws1/cx_agwbadrequestex INTO DATA(lo_bad_request_ex).
        MESSAGE lo_bad_request_ex->get_text( ) TYPE 'E'.
      CATCH /aws1/cx_agwconflictexception INTO DATA(lo_conflict_ex).
        MESSAGE lo_conflict_ex->get_text( ) TYPE 'E'.
      CATCH /aws1/cx_agwnotfoundexception INTO DATA(lo_not_found_ex).
        MESSAGE lo_not_found_ex->get_text( ) TYPE 'E'.
    ENDTRY.
    " snippet-end:[agw.abapv1.put_integration]
  ENDMETHOD.


  METHOD put_integration_response.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_agw) = /aws1/cl_agw_factory=>create( lo_session ).

    " snippet-start:[agw.abapv1.put_integration_response]
    TRY.
        " iv_rest_api_id = 'abc123def4'
        " iv_resource_id = 'xyz789'
        " iv_http_method = 'GET'
        DATA lt_templates TYPE /aws1/cl_agwmapofstrtostr_w=>tt_mapofstringtostring.
        DATA ls_template LIKE LINE OF lt_templates.
        ls_template-key = 'application/json'.
        ls_template-value = ''.
        APPEND ls_template TO lt_templates.

        oo_result = lo_agw->putintegrationresponse(
          iv_restapiid = iv_rest_api_id
          iv_resourceid = iv_resource_id
          iv_httpmethod = iv_http_method
          iv_statuscode = '200'
          it_responsetemplates = lt_templates ).
        MESSAGE 'Integration response created successfully.' TYPE 'I'.
      CATCH /aws1/cx_agwbadrequestex INTO DATA(lo_bad_request_ex).
        MESSAGE lo_bad_request_ex->get_text( ) TYPE 'E'.
      CATCH /aws1/cx_agwconflictexception INTO DATA(lo_conflict_ex).
        MESSAGE lo_conflict_ex->get_text( ) TYPE 'E'.
      CATCH /aws1/cx_agwnotfoundexception INTO DATA(lo_not_found_ex).
        MESSAGE lo_not_found_ex->get_text( ) TYPE 'E'.
    ENDTRY.
    " snippet-end:[agw.abapv1.put_integration_response]
  ENDMETHOD.


  METHOD create_deployment.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_agw) = /aws1/cl_agw_factory=>create( lo_session ).

    " snippet-start:[agw.abapv1.create_deployment]
    TRY.
        " iv_rest_api_id = 'abc123def4'
        " iv_stage_name = 'test'
        oo_result = lo_agw->createdeployment(
          iv_restapiid = iv_rest_api_id
          iv_stagename = iv_stage_name ).
        MESSAGE 'Deployment created successfully.' TYPE 'I'.
      CATCH /aws1/cx_agwbadrequestex INTO DATA(lo_bad_request_ex).
        MESSAGE lo_bad_request_ex->get_text( ) TYPE 'E'.
      CATCH /aws1/cx_agwconflictexception INTO DATA(lo_conflict_ex).
        MESSAGE lo_conflict_ex->get_text( ) TYPE 'E'.
      CATCH /aws1/cx_agwnotfoundexception INTO DATA(lo_not_found_ex).
        MESSAGE lo_not_found_ex->get_text( ) TYPE 'E'.
    ENDTRY.
    " snippet-end:[agw.abapv1.create_deployment]
  ENDMETHOD.


  METHOD get_rest_apis.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_agw) = /aws1/cl_agw_factory=>create( lo_session ).

    " snippet-start:[agw.abapv1.get_rest_apis]
    TRY.
        oo_result = lo_agw->getrestapis( ).
        MESSAGE 'Retrieved REST APIs successfully.' TYPE 'I'.
      CATCH /aws1/cx_agwbadrequestex INTO DATA(lo_bad_request_ex).
        MESSAGE lo_bad_request_ex->get_text( ) TYPE 'E'.
      CATCH /aws1/cx_agwnotfoundexception INTO DATA(lo_not_found_ex).
        MESSAGE lo_not_found_ex->get_text( ) TYPE 'E'.
    ENDTRY.
    " snippet-end:[agw.abapv1.get_rest_apis]
  ENDMETHOD.


  METHOD delete_rest_api.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_agw) = /aws1/cl_agw_factory=>create( lo_session ).

    " snippet-start:[agw.abapv1.delete_rest_api]
    TRY.
        " iv_rest_api_id = 'abc123def4'
        lo_agw->deleterestapi(
          iv_restapiid = iv_rest_api_id ).
        MESSAGE 'REST API deleted successfully.' TYPE 'I'.
      CATCH /aws1/cx_agwbadrequestex INTO DATA(lo_bad_request_ex).
        MESSAGE lo_bad_request_ex->get_text( ) TYPE 'E'.
      CATCH /aws1/cx_agwconflictexception INTO DATA(lo_conflict_ex).
        MESSAGE lo_conflict_ex->get_text( ) TYPE 'E'.
      CATCH /aws1/cx_agwnotfoundexception INTO DATA(lo_not_found_ex).
        MESSAGE lo_not_found_ex->get_text( ) TYPE 'E'.
    ENDTRY.
    " snippet-end:[agw.abapv1.delete_rest_api]
  ENDMETHOD.


  METHOD get_resources.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_agw) = /aws1/cl_agw_factory=>create( lo_session ).

    " snippet-start:[agw.abapv1.get_resources]
    TRY.
        " iv_rest_api_id = 'abc123def4'
        oo_result = lo_agw->getresources(
          iv_restapiid = iv_rest_api_id ).
        MESSAGE 'Retrieved resources successfully.' TYPE 'I'.
      CATCH /aws1/cx_agwbadrequestex INTO DATA(lo_bad_request_ex).
        MESSAGE lo_bad_request_ex->get_text( ) TYPE 'E'.
      CATCH /aws1/cx_agwnotfoundexception INTO DATA(lo_not_found_ex).
        MESSAGE lo_not_found_ex->get_text( ) TYPE 'E'.
    ENDTRY.
    " snippet-end:[agw.abapv1.get_resources]
  ENDMETHOD.
ENDCLASS.
