" Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
" SPDX-License-Identifier: Apache-2.0
CLASS /awsex/cl_ios_actions DEFINITION
  PUBLIC
  FINAL
  CREATE PUBLIC .

  PUBLIC SECTION.

    METHODS create_asset_model
      IMPORTING
                !iv_asset_model_name TYPE /aws1/iosname
                !it_properties       TYPE /aws1/cl_iosassetmodelprpdefn=>tt_assetmodelpropertydefns
      RETURNING
                VALUE(oo_result)     TYPE REF TO /aws1/cl_ioscreassetmodelrsp
      RAISING   /aws1/cx_rt_generic.
    METHODS create_asset
      IMPORTING
                !iv_asset_name    TYPE /aws1/iosname
                !iv_asset_model_id TYPE /aws1/iosid
      RETURNING
                VALUE(oo_result)  TYPE REF TO /aws1/cl_ioscreateassetrsp
      RAISING   /aws1/cx_rt_generic.
    METHODS list_asset_models
      RETURNING
                VALUE(oo_result) TYPE REF TO /aws1/cl_ioslistassetmodelsrsp
      RAISING   /aws1/cx_rt_generic.
    METHODS list_asset_model_properties
      IMPORTING
                !iv_asset_model_id TYPE /aws1/iosid
      RETURNING
                VALUE(oo_result)   TYPE REF TO /aws1/cl_ioslstastmodelprpsrsp
      RAISING   /aws1/cx_rt_generic.
    METHODS batch_put_asset_property_value
      IMPORTING
                !iv_asset_id TYPE /aws1/iosid
                !it_entries  TYPE /aws1/cl_iosputastprpvalueentr=>tt_putassetprpvalueentries
      RAISING   /aws1/cx_rt_generic.
    METHODS get_asset_property_value
      IMPORTING
                !iv_asset_id    TYPE /aws1/iosid
                !iv_property_id TYPE /aws1/iosid
      RETURNING
                VALUE(oo_result) TYPE REF TO /aws1/cl_iosgetastprpvaluersp
      RAISING   /aws1/cx_rt_generic.
    METHODS create_gateway
      IMPORTING
                !iv_gateway_name        TYPE /aws1/iosgatewayname
                !iv_core_device_thing_name TYPE /aws1/ioscoredevicethingname
      RETURNING
                VALUE(oo_result)        TYPE REF TO /aws1/cl_ioscreategatewayrsp
      RAISING   /aws1/cx_rt_generic.
    METHODS describe_gateway
      IMPORTING
                !iv_gateway_id   TYPE /aws1/iosid
      RETURNING
                VALUE(oo_result) TYPE REF TO /aws1/cl_iosdescrgatewayrsp
      RAISING   /aws1/cx_rt_generic.
    METHODS delete_gateway
      IMPORTING
                !iv_gateway_id TYPE /aws1/iosid
      RAISING   /aws1/cx_rt_generic.
    METHODS delete_asset
      IMPORTING
                !iv_asset_id TYPE /aws1/iosid
      RAISING   /aws1/cx_rt_generic.
    METHODS delete_asset_model
      IMPORTING
                !iv_asset_model_id TYPE /aws1/iosid
      RAISING   /aws1/cx_rt_generic.
  PROTECTED SECTION.
  PRIVATE SECTION.
ENDCLASS.



CLASS /AWSEX/CL_IOS_ACTIONS IMPLEMENTATION.


  METHOD create_asset_model.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_ios) = /aws1/cl_ios_factory=>create( lo_session ).

    " snippet-start:[ios.abapv1.create_asset_model]
    TRY.
        oo_result = lo_ios->createassetmodel(
          iv_assetmodelname = iv_asset_model_name
          iv_assetmodeldescription = 'This is a sample asset model description.'
          it_assetmodelproperties = it_properties
        ). " oo_result is returned for testing purposes. "
        MESSAGE 'IoT SiteWise asset model created' TYPE 'I'.
      CATCH /aws1/cx_iosresrcalrdyexistsex.
        MESSAGE 'Asset model already exists.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[ios.abapv1.create_asset_model]
  ENDMETHOD.


  METHOD create_asset.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_ios) = /aws1/cl_ios_factory=>create( lo_session ).

    " snippet-start:[ios.abapv1.create_asset]
    TRY.
        oo_result = lo_ios->createasset(
          iv_assetname = iv_asset_name
          iv_assetmodelid = iv_asset_model_id
        ). " oo_result is returned for testing purposes. "
        MESSAGE 'IoT SiteWise asset created' TYPE 'I'.
      CATCH /aws1/cx_iosresourcenotfoundex.
        MESSAGE 'Asset model does not exist.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[ios.abapv1.create_asset]
  ENDMETHOD.


  METHOD list_asset_models.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_ios) = /aws1/cl_ios_factory=>create( lo_session ).

    " snippet-start:[ios.abapv1.list_asset_models]
    TRY.
        oo_result = lo_ios->listassetmodels( ). " oo_result is returned for testing purposes. "
        DATA(lt_asset_models) = oo_result->get_assetmodelsummaries( ).
        MESSAGE 'Retrieved list of asset models.' TYPE 'I'.
      CATCH /aws1/cx_rt_generic.
        MESSAGE 'Unable to list asset models.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[ios.abapv1.list_asset_models]
  ENDMETHOD.


  METHOD list_asset_model_properties.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_ios) = /aws1/cl_ios_factory=>create( lo_session ).

    " snippet-start:[ios.abapv1.list_asset_model_properties]
    TRY.
        oo_result = lo_ios->listassetmodelproperties(
          iv_assetmodelid = iv_asset_model_id
        ). " oo_result is returned for testing purposes. "
        DATA(lt_properties) = oo_result->get_assetmodelpropertysums( ).
        MESSAGE 'Retrieved list of asset model properties.' TYPE 'I'.
      CATCH /aws1/cx_rt_generic.
        MESSAGE 'Unable to list asset model properties.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[ios.abapv1.list_asset_model_properties]
  ENDMETHOD.


  METHOD batch_put_asset_property_value.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_ios) = /aws1/cl_ios_factory=>create( lo_session ).

    " snippet-start:[ios.abapv1.batch_put_asset_property_value]
    TRY.
        lo_ios->batchputassetpropertyvalue(
          it_entries = it_entries
        ).
        MESSAGE 'Data sent to IoT SiteWise asset successfully.' TYPE 'I'.
      CATCH /aws1/cx_iosresourcenotfoundex.
        MESSAGE 'Asset does not exist.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[ios.abapv1.batch_put_asset_property_value]
  ENDMETHOD.


  METHOD get_asset_property_value.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_ios) = /aws1/cl_ios_factory=>create( lo_session ).

    " snippet-start:[ios.abapv1.get_asset_property_value]
    TRY.
        oo_result = lo_ios->getassetpropertyvalue(
          iv_assetid = iv_asset_id
          iv_propertyid = iv_property_id
        ). " oo_result is returned for testing purposes. "
        MESSAGE 'Retrieved asset property value.' TYPE 'I'.
      CATCH /aws1/cx_iosresourcenotfoundex.
        MESSAGE 'Asset or property does not exist.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[ios.abapv1.get_asset_property_value]
  ENDMETHOD.


  METHOD create_gateway.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_ios) = /aws1/cl_ios_factory=>create( lo_session ).

    " snippet-start:[ios.abapv1.create_gateway]
    TRY.
        oo_result = lo_ios->creategateway(
          iv_gatewayname = iv_gateway_name
          io_gatewayplatform = NEW /aws1/cl_iosgatewayplatform(
            io_greengrassv2 = NEW /aws1/cl_iosgreengrassv2(
              iv_coredevicethingname = iv_core_device_thing_name
            )
          )
          it_tags = VALUE /aws1/cl_iostagmap_w=>tt_tagmap(
            (
              VALUE /aws1/cl_iostagmap_w=>ts_tagmap_maprow(
                key = 'Environment'
                value = NEW /aws1/cl_iostagmap_w( 'Production' )
              )
            )
          )
        ). " oo_result is returned for testing purposes. "
        MESSAGE 'IoT SiteWise gateway created' TYPE 'I'.
      CATCH /aws1/cx_iosresrcalrdyexistsex.
        MESSAGE 'Gateway already exists.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[ios.abapv1.create_gateway]
  ENDMETHOD.


  METHOD describe_gateway.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_ios) = /aws1/cl_ios_factory=>create( lo_session ).

    " snippet-start:[ios.abapv1.describe_gateway]
    TRY.
        oo_result = lo_ios->describegateway(
          iv_gatewayid = iv_gateway_id
        ). " oo_result is returned for testing purposes. "
        MESSAGE 'Retrieved gateway description.' TYPE 'I'.
      CATCH /aws1/cx_iosresourcenotfoundex.
        MESSAGE 'Gateway does not exist.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[ios.abapv1.describe_gateway]
  ENDMETHOD.


  METHOD delete_gateway.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_ios) = /aws1/cl_ios_factory=>create( lo_session ).

    " snippet-start:[ios.abapv1.delete_gateway]
    TRY.
        lo_ios->deletegateway(
          iv_gatewayid = iv_gateway_id
        ).
        MESSAGE 'IoT SiteWise gateway deleted.' TYPE 'I'.
      CATCH /aws1/cx_iosresourcenotfoundex.
        MESSAGE 'Gateway does not exist.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[ios.abapv1.delete_gateway]
  ENDMETHOD.


  METHOD delete_asset.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_ios) = /aws1/cl_ios_factory=>create( lo_session ).

    " snippet-start:[ios.abapv1.delete_asset]
    TRY.
        lo_ios->deleteasset(
          iv_assetid = iv_asset_id
        ).
        MESSAGE 'IoT SiteWise asset deleted.' TYPE 'I'.
      CATCH /aws1/cx_rt_generic.
        MESSAGE 'Unable to delete asset.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[ios.abapv1.delete_asset]
  ENDMETHOD.


  METHOD delete_asset_model.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_ios) = /aws1/cl_ios_factory=>create( lo_session ).

    " snippet-start:[ios.abapv1.delete_asset_model]
    TRY.
        lo_ios->deleteassetmodel(
          iv_assetmodelid = iv_asset_model_id
        ).
        MESSAGE 'IoT SiteWise asset model deleted.' TYPE 'I'.
      CATCH /aws1/cx_rt_generic.
        MESSAGE 'Unable to delete asset model.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[ios.abapv1.delete_asset_model]
  ENDMETHOD.
ENDCLASS.
