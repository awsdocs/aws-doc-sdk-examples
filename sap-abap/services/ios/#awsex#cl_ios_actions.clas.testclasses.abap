" Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
" SPDX-License-Identifier: Apache-2.0
CLASS ltc_awsex_cl_ios_actions DEFINITION DEFERRED.
CLASS /awsex/cl_ios_actions DEFINITION LOCAL FRIENDS ltc_awsex_cl_ios_actions.

CLASS ltc_awsex_cl_ios_actions DEFINITION FOR TESTING DURATION LONG RISK LEVEL DANGEROUS.

  PRIVATE SECTION.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    CLASS-DATA ao_ios TYPE REF TO /aws1/if_ios.
    CLASS-DATA ao_session TYPE REF TO /aws1/cl_rt_session_base.
    CLASS-DATA ao_ios_actions TYPE REF TO /awsex/cl_ios_actions.
    CLASS-DATA ao_iam TYPE REF TO /aws1/if_iam.

    CLASS-DATA gv_asset_model_id TYPE /aws1/iosid.
    CLASS-DATA gv_asset_id TYPE /aws1/iosid.
    CLASS-DATA gv_role_arn TYPE /aws1/iosiamarn.
    CLASS-DATA gv_role_name TYPE /aws1/iamrolenamestring.
    CLASS-DATA gv_temperature_property_id TYPE /aws1/iosid.
    CLASS-DATA gv_humidity_property_id TYPE /aws1/iosid.
    CLASS-DATA gv_uuid TYPE string.

    CLASS-METHODS class_setup RAISING /aws1/cx_rt_generic.
    CLASS-METHODS class_teardown RAISING /aws1/cx_rt_generic.

    METHODS create_asset_model FOR TESTING RAISING /aws1/cx_rt_generic.
    METHODS create_asset FOR TESTING RAISING /aws1/cx_rt_generic.
    METHODS list_asset_models FOR TESTING RAISING /aws1/cx_rt_generic.
    METHODS list_asset_model_properties FOR TESTING RAISING /aws1/cx_rt_generic.
    METHODS batch_put_asset_property_value FOR TESTING RAISING /aws1/cx_rt_generic.
    METHODS get_asset_property_value FOR TESTING RAISING /aws1/cx_rt_generic.
    METHODS create_portal FOR TESTING RAISING /aws1/cx_rt_generic.
    METHODS describe_portal FOR TESTING RAISING /aws1/cx_rt_generic.
    METHODS create_gateway FOR TESTING RAISING /aws1/cx_rt_generic.
    METHODS describe_gateway FOR TESTING RAISING /aws1/cx_rt_generic.
    METHODS delete_gateway FOR TESTING RAISING /aws1/cx_rt_generic.
    METHODS delete_portal FOR TESTING RAISING /aws1/cx_rt_generic.
    METHODS delete_asset FOR TESTING RAISING /aws1/cx_rt_generic.
    METHODS delete_asset_model FOR TESTING RAISING /aws1/cx_rt_generic.

    METHODS wait_for_asset_model_active
      IMPORTING
        iv_asset_model_id TYPE /aws1/iosid
      RAISING /aws1/cx_rt_generic.

    METHODS wait_for_asset_active
      IMPORTING
        iv_asset_id TYPE /aws1/iosid
      RAISING /aws1/cx_rt_generic.

    METHODS wait_for_portal_active
      IMPORTING
        iv_portal_id TYPE /aws1/iosid
      RAISING /aws1/cx_rt_generic.

    METHODS wait_for_asset_deleted
      IMPORTING
        iv_asset_id TYPE /aws1/iosid
      RAISING /aws1/cx_rt_generic.

    METHODS wait_for_asset_model_deleted
      IMPORTING
        iv_asset_model_id TYPE /aws1/iosid
      RAISING /aws1/cx_rt_generic.
ENDCLASS.

CLASS ltc_awsex_cl_ios_actions IMPLEMENTATION.

  METHOD class_setup.
    ao_session = /aws1/cl_rt_session_aws=>create( iv_profile_id = cv_pfl ).
    ao_ios = /aws1/cl_ios_factory=>create( ao_session ).
    ao_ios_actions = NEW /awsex/cl_ios_actions( ).
    ao_iam = /aws1/cl_iam_factory=>create( ao_session ).

    " Generate UUID for unique resource names
    gv_uuid = /awsex/cl_utils=>get_random_string( ).

    " Create IAM role for portal with necessary trust policy and tag it
    gv_role_name = |IoTSiteWiseRole-{ gv_uuid }|.
    DATA lv_assume_role_policy TYPE /aws1/iampolicydocumenttype.

    lv_assume_role_policy = '{"Version":"2012-10-17","Statement":[{"Effect":"Allow","Principal":{"Service":"iotsitewise.amazonaws.com"},"Action":"sts:AssumeRole"}]}'.

    TRY.
        DATA(lo_role_result) = ao_iam->createrole(
          iv_rolename = gv_role_name
          iv_assumerolepolicydocument = lv_assume_role_policy
          it_tags = VALUE /aws1/cl_iamtag=>tt_taglisttype(
            ( NEW /aws1/cl_iamtag( iv_key = 'convert_test' iv_value = 'true' ) )
          )
        ).
        gv_role_arn = lo_role_result->get_role( )->get_arn( ).

        " Attach necessary policies to the role
        ao_iam->attachrolepolicy(
          iv_rolename = gv_role_name
          iv_policyarn = 'arn:aws:iam::aws:policy/AWSIoTSiteWiseMonitorPortalAccess'
        ).

        " Wait for role to propagate
        WAIT UP TO 10 SECONDS.
      CATCH /aws1/cx_iamentityalrdyexistsex.
        " If role already exists from a previous run, delete and recreate
        TRY.
            ao_iam->detachrolepolicy(
              iv_rolename = gv_role_name
              iv_policyarn = 'arn:aws:iam::aws:policy/AWSIoTSiteWiseMonitorPortalAccess'
            ).
          CATCH /aws1/cx_rt_generic.
        ENDTRY.
        ao_iam->deleterole( iv_rolename = gv_role_name ).
        WAIT UP TO 5 SECONDS.
        lo_role_result = ao_iam->createrole(
          iv_rolename = gv_role_name
          iv_assumerolepolicydocument = lv_assume_role_policy
          it_tags = VALUE /aws1/cl_iamtag=>tt_taglisttype(
            ( NEW /aws1/cl_iamtag( iv_key = 'convert_test' iv_value = 'true' ) )
          )
        ).
        gv_role_arn = lo_role_result->get_role( )->get_arn( ).
        ao_iam->attachrolepolicy(
          iv_rolename = gv_role_name
          iv_policyarn = 'arn:aws:iam::aws:policy/AWSIoTSiteWiseMonitorPortalAccess'
        ).
        WAIT UP TO 10 SECONDS.
    ENDTRY.

    " Create shared asset model with tags
    DATA lv_asset_model_name TYPE /aws1/iosname.
    lv_asset_model_name = |test-model-{ gv_uuid }|.
    DATA(lt_properties) = VALUE /aws1/cl_iosassetmodelprpdefn=>tt_assetmodelpropertydefns(
      (
        NEW /aws1/cl_iosassetmodelprpdefn(
          iv_name = 'temperature'
          iv_datatype = 'DOUBLE'
          io_type = NEW /aws1/cl_iospropertytype(
            io_measurement = NEW /aws1/cl_iosmeasurement( )
          )
        )
      )
      (
        NEW /aws1/cl_iosassetmodelprpdefn(
          iv_name = 'humidity'
          iv_datatype = 'DOUBLE'
          io_type = NEW /aws1/cl_iospropertytype(
            io_measurement = NEW /aws1/cl_iosmeasurement( )
          )
        )
      )
    ).

    DATA(lo_model_result) = ao_ios->createassetmodel(
      iv_assetmodelname = lv_asset_model_name
      iv_assetmodeldescription = 'Test asset model for unit tests'
      it_assetmodelproperties = lt_properties
      it_tags = VALUE /aws1/cl_iostagmap_w=>tt_tagmap(
        (
          VALUE /aws1/cl_iostagmap_w=>ts_tagmap_maprow(
            key = 'convert_test'
            value = NEW /aws1/cl_iostagmap_w( 'true' )
          )
        )
      )
    ).
    gv_asset_model_id = lo_model_result->get_assetmodelid( ).

    " Fail test if asset model creation failed
    IF gv_asset_model_id IS INITIAL.
      cl_abap_unit_assert=>fail( msg = 'Failed to create asset model in class_setup' ).
    ENDIF.

    wait_for_asset_model_active( gv_asset_model_id ).

    " Get property IDs
    DATA(lo_props_result) = ao_ios->listassetmodelproperties(
      iv_assetmodelid = gv_asset_model_id
    ).
    LOOP AT lo_props_result->get_assetmodelpropertysums( ) INTO DATA(lo_prop).
      IF lo_prop->get_name( ) = 'temperature'.
        gv_temperature_property_id = lo_prop->get_id( ).
      ELSEIF lo_prop->get_name( ) = 'humidity'.
        gv_humidity_property_id = lo_prop->get_id( ).
      ENDIF.
    ENDLOOP.

    IF gv_temperature_property_id IS INITIAL OR gv_humidity_property_id IS INITIAL.
      cl_abap_unit_assert=>fail( msg = 'Failed to retrieve property IDs in class_setup' ).
    ENDIF.

    " Create shared asset with tags
    DATA lv_asset_name TYPE /aws1/iosname.
    lv_asset_name = |test-asset-{ gv_uuid }|.
    DATA(lo_asset_result) = ao_ios->createasset(
      iv_assetname = lv_asset_name
      iv_assetmodelid = gv_asset_model_id
      it_tags = VALUE /aws1/cl_iostagmap_w=>tt_tagmap(
        (
          VALUE /aws1/cl_iostagmap_w=>ts_tagmap_maprow(
            key = 'convert_test'
            value = NEW /aws1/cl_iostagmap_w( 'true' )
          )
        )
      )
    ).
    gv_asset_id = lo_asset_result->get_assetid( ).

    IF gv_asset_id IS INITIAL.
      cl_abap_unit_assert=>fail( msg = 'Failed to create asset in class_setup' ).
    ENDIF.

    wait_for_asset_active( gv_asset_id ).
  ENDMETHOD.

  METHOD class_teardown.
    " Clean up resources in correct order

    " Delete asset first
    IF gv_asset_id IS NOT INITIAL.
      TRY.
          ao_ios->deleteasset( iv_assetid = gv_asset_id ).
          wait_for_asset_deleted( gv_asset_id ).
        CATCH /aws1/cx_rt_generic.
          " Ignore errors during cleanup - resource will be tagged
      ENDTRY.
    ENDIF.

    " Delete asset model
    IF gv_asset_model_id IS NOT INITIAL.
      TRY.
          ao_ios->deleteassetmodel( iv_assetmodelid = gv_asset_model_id ).
          wait_for_asset_model_deleted( gv_asset_model_id ).
        CATCH /aws1/cx_rt_generic.
          " Ignore errors during cleanup - resource will be tagged
      ENDTRY.
    ENDIF.

    " Delete IAM role
    IF gv_role_arn IS NOT INITIAL AND gv_role_name IS NOT INITIAL.
      TRY.
          " Detach policies first
          ao_iam->detachrolepolicy(
            iv_rolename = gv_role_name
            iv_policyarn = 'arn:aws:iam::aws:policy/AWSIoTSiteWiseMonitorPortalAccess'
          ).
          " Delete role
          ao_iam->deleterole( iv_rolename = gv_role_name ).
        CATCH /aws1/cx_rt_generic.
          " Ignore errors during cleanup
      ENDTRY.
    ENDIF.
  ENDMETHOD.

  METHOD create_asset_model.
    DATA lv_asset_model_name TYPE /aws1/iosname.
    lv_asset_model_name = |test-create-model-{ gv_uuid }|.

    " lv_asset_model_name = 'test-asset-model-create'
    DATA(lt_properties) = VALUE /aws1/cl_iosassetmodelprpdefn=>tt_assetmodelpropertydefns(
      (
        NEW /aws1/cl_iosassetmodelprpdefn(
          iv_name = 'test-temp'
          iv_datatype = 'DOUBLE'
          io_type = NEW /aws1/cl_iospropertytype(
            io_measurement = NEW /aws1/cl_iosmeasurement( )
          )
        )
      )
    ).

    DATA(lo_result) = ao_ios_actions->create_asset_model(
      iv_asset_model_name = lv_asset_model_name
      it_properties = lt_properties
    ).

    DATA(lv_temp_model_id) = lo_result->get_assetmodelid( ).
    cl_abap_unit_assert=>assert_not_initial(
      act = lv_temp_model_id
      msg = |Asset model { lv_asset_model_name } was not created|
    ).

    wait_for_asset_model_active( lv_temp_model_id ).

    " Tag the resource
    ao_ios->tagresource(
      iv_resourcearn = lo_result->get_assetmodelarn( )
      it_tags = VALUE /aws1/cl_iostagmap_w=>tt_tagmap(
        (
          VALUE /aws1/cl_iostagmap_w=>ts_tagmap_maprow(
            key = 'convert_test'
            value = NEW /aws1/cl_iostagmap_w( 'true' )
          )
        )
      )
    ).

    " Clean up
    TRY.
        ao_ios->deleteassetmodel( iv_assetmodelid = lv_temp_model_id ).
        wait_for_asset_model_deleted( lv_temp_model_id ).
      CATCH /aws1/cx_rt_generic.
        " Ignore cleanup errors - resource is tagged
    ENDTRY.
  ENDMETHOD.

  METHOD create_asset.
    " lv_asset_name = 'test-asset-create'
    DATA lv_asset_name TYPE /aws1/iosname.
    lv_asset_name = |test-create-asset-{ gv_uuid }|.

    DATA(lo_result) = ao_ios_actions->create_asset(
      iv_asset_name = lv_asset_name
      iv_asset_model_id = gv_asset_model_id
    ).

    DATA(lv_temp_asset_id) = lo_result->get_assetid( ).
    cl_abap_unit_assert=>assert_not_initial(
      act = lv_temp_asset_id
      msg = |Asset { lv_asset_name } was not created|
    ).

    wait_for_asset_active( lv_temp_asset_id ).

    " Tag the resource
    ao_ios->tagresource(
      iv_resourcearn = lo_result->get_assetarn( )
      it_tags = VALUE /aws1/cl_iostagmap_w=>tt_tagmap(
        (
          VALUE /aws1/cl_iostagmap_w=>ts_tagmap_maprow(
            key = 'convert_test'
            value = NEW /aws1/cl_iostagmap_w( 'true' )
          )
        )
      )
    ).

    " Clean up
    TRY.
        ao_ios->deleteasset( iv_assetid = lv_temp_asset_id ).
        wait_for_asset_deleted( lv_temp_asset_id ).
      CATCH /aws1/cx_rt_generic.
        " Ignore cleanup errors - resource is tagged
    ENDTRY.
  ENDMETHOD.

  METHOD list_asset_models.
    DATA(lo_result) = ao_ios_actions->list_asset_models( ).
    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = 'List asset models result should not be initial'
    ).

    " Verify our shared asset model is in the list
    DATA lv_found TYPE abap_bool.
    LOOP AT lo_result->get_assetmodelsummaries( ) INTO DATA(lo_model).
      IF lo_model->get_id( ) = gv_asset_model_id.
        lv_found = abap_true.
        EXIT.
      ENDIF.
    ENDLOOP.

    cl_abap_unit_assert=>assert_true(
      act = lv_found
      msg = 'Shared asset model should be in the list'
    ).
  ENDMETHOD.

  METHOD list_asset_model_properties.
    DATA(lo_result) = ao_ios_actions->list_asset_model_properties( gv_asset_model_id ).
    DATA(lt_prop_summaries) = lo_result->get_assetmodelpropertysums( ).

    cl_abap_unit_assert=>assert_equals(
      exp = 2
      act = lines( lt_prop_summaries )
      msg = 'Should have retrieved 2 properties'
    ).

    " Verify the properties are temperature and humidity
    DATA lv_found_temp TYPE abap_bool.
    DATA lv_found_humidity TYPE abap_bool.
    LOOP AT lt_prop_summaries INTO DATA(lo_prop).
      IF lo_prop->get_name( ) = 'temperature'.
        lv_found_temp = abap_true.
      ELSEIF lo_prop->get_name( ) = 'humidity'.
        lv_found_humidity = abap_true.
      ENDIF.
    ENDLOOP.

    cl_abap_unit_assert=>assert_true(
      act = lv_found_temp
      msg = 'Temperature property should be in the list'
    ).
    cl_abap_unit_assert=>assert_true(
      act = lv_found_humidity
      msg = 'Humidity property should be in the list'
    ).
  ENDMETHOD.

  METHOD batch_put_asset_property_value.
    " Get current timestamp
    DATA lv_timestamp TYPE timestamp.
    GET TIME STAMP FIELD lv_timestamp.
    DATA(lv_seconds) = lv_timestamp DIV 1000000.
    DATA(lv_nanos) = ( lv_timestamp MOD 1000000 ) * 1000.

    DATA(lt_entries) = VALUE /aws1/cl_iosputastprpvalueentr=>tt_putassetprpvalueentries(
      (
        NEW /aws1/cl_iosputastprpvalueentr(
          iv_entryid = '1'
          iv_assetid = gv_asset_id
          iv_propertyid = gv_humidity_property_id
          it_propertyvalues = VALUE /aws1/cl_iosassetpropertyvalue=>tt_assetpropertyvalues(
            (
              NEW /aws1/cl_iosassetpropertyvalue(
                io_value = NEW /aws1/cl_iosvariant(
                  iv_doublevalue = '65.0'
                )
                io_timestamp = NEW /aws1/cl_iostimeinnanos(
                  iv_timeinseconds = lv_seconds
                  iv_offsetinnanos = lv_nanos
                )
              )
            )
          )
        )
      )
      (
        NEW /aws1/cl_iosputastprpvalueentr(
          iv_entryid = '2'
          iv_assetid = gv_asset_id
          iv_propertyid = gv_temperature_property_id
          it_propertyvalues = VALUE /aws1/cl_iosassetpropertyvalue=>tt_assetpropertyvalues(
            (
              NEW /aws1/cl_iosassetpropertyvalue(
                io_value = NEW /aws1/cl_iosvariant(
                  iv_doublevalue = '23.5'
                )
                io_timestamp = NEW /aws1/cl_iostimeinnanos(
                  iv_timeinseconds = lv_seconds
                  iv_offsetinnanos = lv_nanos
                )
              )
            )
          )
        )
      )
    ).

    ao_ios_actions->batch_put_asset_property_value(
      iv_asset_id = gv_asset_id
      it_entries = lt_entries
    ).

    " Wait for data to propagate
    WAIT UP TO 5 SECONDS.

    " Verify data was written successfully by reading it back
    DATA(lo_get_result) = ao_ios->getassetpropertyvalue(
      iv_assetid = gv_asset_id
      iv_propertyid = gv_temperature_property_id
    ).

    cl_abap_unit_assert=>assert_bound(
      act = lo_get_result->get_propertyvalue( )
      msg = 'Property value should not be initial after batch put'
    ).
  ENDMETHOD.

  METHOD get_asset_property_value.
    DATA(lo_result) = ao_ios_actions->get_asset_property_value(
      iv_asset_id = gv_asset_id
      iv_property_id = gv_temperature_property_id
    ).

    DATA(lo_property_value) = lo_result->get_propertyvalue( ).
    cl_abap_unit_assert=>assert_bound(
      act = lo_property_value
      msg = 'Property value should not be initial'
    ).

    " Verify we can get the value
    DATA(lo_value) = lo_property_value->get_value( ).
    cl_abap_unit_assert=>assert_bound(
      act = lo_value
      msg = 'Property value variant should not be initial'
    ).
  ENDMETHOD.

  METHOD create_portal.
    " lv_portal_name = 'test-portal-create'
    DATA lv_portal_name TYPE /aws1/iosname.
    lv_portal_name = |test-portal-{ gv_uuid }|.
    " iv_portal_contact_email = 'test@example.com'
    DATA lv_email TYPE /aws1/iosemail VALUE 'test@example.com'.

    DATA(lo_result) = ao_ios_actions->create_portal(
      iv_portal_name = lv_portal_name
      iv_role_arn = gv_role_arn
      iv_portal_contact_email = lv_email
    ).

    DATA(lv_temp_portal_id) = lo_result->get_portalid( ).
    cl_abap_unit_assert=>assert_not_initial(
      act = lv_temp_portal_id
      msg = |Portal { lv_portal_name } was not created|
    ).

    wait_for_portal_active( lv_temp_portal_id ).

    " Tag the resource - Note: Portals take a long time to delete so we tag but don't clean up
    ao_ios->tagresource(
      iv_resourcearn = lo_result->get_portalarn( )
      it_tags = VALUE /aws1/cl_iostagmap_w=>tt_tagmap(
        (
          VALUE /aws1/cl_iostagmap_w=>ts_tagmap_maprow(
            key = 'convert_test'
            value = NEW /aws1/cl_iostagmap_w( 'true' )
          )
        )
      )
    ).

    " Note: Portal deletion takes a long time, so we leave it tagged for manual cleanup
    " User must delete portals manually using the convert_test tag
  ENDMETHOD.

  METHOD describe_portal.
    " Create portal first and tag it
    DATA lv_portal_name TYPE /aws1/iosname.
    lv_portal_name = |test-desc-portal-{ gv_uuid }|.
    DATA lv_email TYPE /aws1/iosemail VALUE 'test@example.com'.

    DATA(lo_create_result) = ao_ios->createportal(
      iv_portalname = lv_portal_name
      iv_rolearn = gv_role_arn
      iv_portalcontactemail = lv_email
      it_tags = VALUE /aws1/cl_iostagmap_w=>tt_tagmap(
        (
          VALUE /aws1/cl_iostagmap_w=>ts_tagmap_maprow(
            key = 'convert_test'
            value = NEW /aws1/cl_iostagmap_w( 'true' )
          )
        )
      )
    ).
    DATA(lv_temp_portal_id) = lo_create_result->get_portalid( ).
    wait_for_portal_active( lv_temp_portal_id ).

    DATA(lo_result) = ao_ios_actions->describe_portal( lv_temp_portal_id ).

    cl_abap_unit_assert=>assert_equals(
      exp = lv_temp_portal_id
      act = lo_result->get_portalid( )
      msg = 'Portal ID should match'
    ).

    cl_abap_unit_assert=>assert_not_initial(
      act = lo_result->get_portalstarturl( )
      msg = 'Portal URL should not be initial'
    ).

    " Note: Portal deletion takes a long time, so we leave it tagged for manual cleanup
  ENDMETHOD.

  METHOD create_gateway.
    " lv_gateway_name = 'test-gateway-create'
    DATA lv_gateway_name TYPE /aws1/iosgatewayname.
    lv_gateway_name = |test-gw-{ gv_uuid }|.
    " iv_core_device_thing_name = 'test-thing'
    DATA lv_thing_name TYPE /aws1/ioscoredevicethingname.
    lv_thing_name = |test-thing-{ gv_uuid }|.

    DATA(lo_result) = ao_ios_actions->create_gateway(
      iv_gateway_name = lv_gateway_name
      iv_core_device_thing_name = lv_thing_name
    ).

    DATA(lv_temp_gateway_id) = lo_result->get_gatewayid( ).
    cl_abap_unit_assert=>assert_not_initial(
      act = lv_temp_gateway_id
      msg = |Gateway { lv_gateway_name } was not created|
    ).

    " Tag the resource
    ao_ios->tagresource(
      iv_resourcearn = lo_result->get_gatewayarn( )
      it_tags = VALUE /aws1/cl_iostagmap_w=>tt_tagmap(
        (
          VALUE /aws1/cl_iostagmap_w=>ts_tagmap_maprow(
            key = 'convert_test'
            value = NEW /aws1/cl_iostagmap_w( 'true' )
          )
        )
      )
    ).

    " Clean up
    TRY.
        ao_ios->deletegateway( iv_gatewayid = lv_temp_gateway_id ).
      CATCH /aws1/cx_rt_generic.
        " Ignore cleanup errors - resource is tagged
    ENDTRY.
  ENDMETHOD.

  METHOD describe_gateway.
    " Create gateway first and tag it
    DATA lv_gateway_name TYPE /aws1/iosgatewayname.
    lv_gateway_name = |test-desc-gw-{ gv_uuid }|.
    DATA lv_thing_name TYPE /aws1/ioscoredevicethingname.
    lv_thing_name = |test-desc-thing-{ gv_uuid }|.

    DATA(lo_create_result) = ao_ios->creategateway(
      iv_gatewayname = lv_gateway_name
      io_gatewayplatform = NEW /aws1/cl_iosgatewayplatform(
        io_greengrassv2 = NEW /aws1/cl_iosgreengrassv2(
          iv_coredevicethingname = lv_thing_name
        )
      )
      it_tags = VALUE /aws1/cl_iostagmap_w=>tt_tagmap(
        (
          VALUE /aws1/cl_iostagmap_w=>ts_tagmap_maprow(
            key = 'convert_test'
            value = NEW /aws1/cl_iostagmap_w( 'true' )
          )
        )
      )
    ).
    DATA(lv_temp_gateway_id) = lo_create_result->get_gatewayid( ).

    DATA(lo_result) = ao_ios_actions->describe_gateway( lv_temp_gateway_id ).

    cl_abap_unit_assert=>assert_equals(
      exp = lv_temp_gateway_id
      act = lo_result->get_gatewayid( )
      msg = 'Gateway ID should match'
    ).

    cl_abap_unit_assert=>assert_equals(
      exp = lv_gateway_name
      act = lo_result->get_gatewayname( )
      msg = 'Gateway name should match'
    ).

    " Clean up
    TRY.
        ao_ios->deletegateway( iv_gatewayid = lv_temp_gateway_id ).
      CATCH /aws1/cx_rt_generic.
        " Ignore cleanup errors - resource is tagged
    ENDTRY.
  ENDMETHOD.

  METHOD delete_gateway.
    " Create gateway to delete and tag it
    DATA lv_gateway_name TYPE /aws1/iosgatewayname.
    lv_gateway_name = |test-del-gw-{ gv_uuid }|.
    DATA lv_thing_name TYPE /aws1/ioscoredevicethingname.
    lv_thing_name = |test-del-thing-{ gv_uuid }|.

    DATA(lo_create_result) = ao_ios->creategateway(
      iv_gatewayname = lv_gateway_name
      io_gatewayplatform = NEW /aws1/cl_iosgatewayplatform(
        io_greengrassv2 = NEW /aws1/cl_iosgreengrassv2(
          iv_coredevicethingname = lv_thing_name
        )
      )
      it_tags = VALUE /aws1/cl_iostagmap_w=>tt_tagmap(
        (
          VALUE /aws1/cl_iostagmap_w=>ts_tagmap_maprow(
            key = 'convert_test'
            value = NEW /aws1/cl_iostagmap_w( 'true' )
          )
        )
      )
    ).
    DATA(lv_temp_gateway_id) = lo_create_result->get_gatewayid( ).

    ao_ios_actions->delete_gateway( lv_temp_gateway_id ).

    " Verify deletion by attempting to describe (should fail)
    TRY.
        ao_ios->describegateway( iv_gatewayid = lv_temp_gateway_id ).
        cl_abap_unit_assert=>fail( msg = 'Gateway should have been deleted' ).
      CATCH /aws1/cx_iosresourcenotfoundex.
        " Expected exception - gateway was deleted
    ENDTRY.
  ENDMETHOD.

  METHOD delete_portal.
    " Create portal to delete and tag it
    DATA lv_portal_name TYPE /aws1/iosname.
    lv_portal_name = |test-del-portal-{ gv_uuid }|.
    DATA lv_email TYPE /aws1/iosemail VALUE 'test@example.com'.

    DATA(lo_create_result) = ao_ios->createportal(
      iv_portalname = lv_portal_name
      iv_rolearn = gv_role_arn
      iv_portalcontactemail = lv_email
      it_tags = VALUE /aws1/cl_iostagmap_w=>tt_tagmap(
        (
          VALUE /aws1/cl_iostagmap_w=>ts_tagmap_maprow(
            key = 'convert_test'
            value = NEW /aws1/cl_iostagmap_w( 'true' )
          )
        )
      )
    ).
    DATA(lv_temp_portal_id) = lo_create_result->get_portalid( ).
    wait_for_portal_active( lv_temp_portal_id ).

    ao_ios_actions->delete_portal( lv_temp_portal_id ).

    " Note: Portal deletion takes a very long time (60+ seconds), 
    " so we don't wait for completion here - the resource is tagged for cleanup
  ENDMETHOD.

  METHOD delete_asset.
    " Create asset to delete and tag it
    DATA lv_asset_name TYPE /aws1/iosname.
    lv_asset_name = |test-del-asset-{ gv_uuid }|.

    DATA(lo_asset_result) = ao_ios->createasset(
      iv_assetname = lv_asset_name
      iv_assetmodelid = gv_asset_model_id
      it_tags = VALUE /aws1/cl_iostagmap_w=>tt_tagmap(
        (
          VALUE /aws1/cl_iostagmap_w=>ts_tagmap_maprow(
            key = 'convert_test'
            value = NEW /aws1/cl_iostagmap_w( 'true' )
          )
        )
      )
    ).
    DATA(lv_temp_asset_id) = lo_asset_result->get_assetid( ).
    wait_for_asset_active( lv_temp_asset_id ).

    ao_ios_actions->delete_asset( lv_temp_asset_id ).
    wait_for_asset_deleted( lv_temp_asset_id ).
  ENDMETHOD.

  METHOD delete_asset_model.
    " Create asset model to delete and tag it
    DATA lv_asset_model_name TYPE /aws1/iosname.
    lv_asset_model_name = |test-del-model-{ gv_uuid }|.
    DATA(lt_properties) = VALUE /aws1/cl_iosassetmodelprpdefn=>tt_assetmodelpropertydefns(
      (
        NEW /aws1/cl_iosassetmodelprpdefn(
          iv_name = 'test-property'
          iv_datatype = 'DOUBLE'
          io_type = NEW /aws1/cl_iospropertytype(
            io_measurement = NEW /aws1/cl_iosmeasurement( )
          )
        )
      )
    ).

    DATA(lo_model_result) = ao_ios->createassetmodel(
      iv_assetmodelname = lv_asset_model_name
      it_assetmodelproperties = lt_properties
      it_tags = VALUE /aws1/cl_iostagmap_w=>tt_tagmap(
        (
          VALUE /aws1/cl_iostagmap_w=>ts_tagmap_maprow(
            key = 'convert_test'
            value = NEW /aws1/cl_iostagmap_w( 'true' )
          )
        )
      )
    ).
    DATA(lv_temp_model_id) = lo_model_result->get_assetmodelid( ).
    wait_for_asset_model_active( lv_temp_model_id ).

    ao_ios_actions->delete_asset_model( lv_temp_model_id ).
    wait_for_asset_model_deleted( lv_temp_model_id ).
  ENDMETHOD.

  METHOD wait_for_asset_model_active.
    DATA lv_max_attempts TYPE i VALUE 40.
    DATA lv_attempt TYPE i VALUE 0.
    DATA lv_state TYPE /aws1/iosassetmodelstate.

    WHILE lv_attempt < lv_max_attempts.
      TRY.
          DATA(lo_result) = ao_ios->describeassetmodel(
            iv_assetmodelid = iv_asset_model_id
          ).
          lv_state = lo_result->get_assetmodelstatus( )->get_state( ).

          IF lv_state = 'ACTIVE'.
            RETURN.
          ELSEIF lv_state = 'FAILED'.
            cl_abap_unit_assert=>fail( msg = 'Asset model creation failed' ).
          ENDIF.
        CATCH /aws1/cx_rt_generic.
          " Model not ready yet
      ENDTRY.

      WAIT UP TO 3 SECONDS.
      lv_attempt = lv_attempt + 1.
    ENDWHILE.

    cl_abap_unit_assert=>fail( msg = 'Asset model did not become active in time' ).
  ENDMETHOD.

  METHOD wait_for_asset_active.
    DATA lv_max_attempts TYPE i VALUE 40.
    DATA lv_attempt TYPE i VALUE 0.
    DATA lv_state TYPE /aws1/iosassetstate.

    WHILE lv_attempt < lv_max_attempts.
      TRY.
          DATA(lo_result) = ao_ios->describeasset(
            iv_assetid = iv_asset_id
          ).
          lv_state = lo_result->get_assetstatus( )->get_state( ).

          IF lv_state = 'ACTIVE'.
            RETURN.
          ELSEIF lv_state = 'FAILED'.
            cl_abap_unit_assert=>fail( msg = 'Asset creation failed' ).
          ENDIF.
        CATCH /aws1/cx_rt_generic.
          " Asset not ready yet
      ENDTRY.

      WAIT UP TO 3 SECONDS.
      lv_attempt = lv_attempt + 1.
    ENDWHILE.

    cl_abap_unit_assert=>fail( msg = 'Asset did not become active in time' ).
  ENDMETHOD.

  METHOD wait_for_portal_active.
    DATA lv_max_attempts TYPE i VALUE 40.
    DATA lv_attempt TYPE i VALUE 0.
    DATA lv_state TYPE /aws1/iosportalstate.

    WHILE lv_attempt < lv_max_attempts.
      TRY.
          DATA(lo_result) = ao_ios->describeportal(
            iv_portalid = iv_portal_id
          ).
          lv_state = lo_result->get_portalstatus( )->get_state( ).

          IF lv_state = 'ACTIVE'.
            RETURN.
          ELSEIF lv_state = 'FAILED'.
            cl_abap_unit_assert=>fail( msg = 'Portal creation failed' ).
          ENDIF.
        CATCH /aws1/cx_rt_generic.
          " Portal not ready yet
      ENDTRY.

      WAIT UP TO 3 SECONDS.
      lv_attempt = lv_attempt + 1.
    ENDWHILE.

    cl_abap_unit_assert=>fail( msg = 'Portal did not become active in time' ).
  ENDMETHOD.

  METHOD wait_for_asset_deleted.
    DATA lv_max_attempts TYPE i VALUE 40.
    DATA lv_attempt TYPE i VALUE 0.

    WHILE lv_attempt < lv_max_attempts.
      TRY.
          ao_ios->describeasset( iv_assetid = iv_asset_id ).
          WAIT UP TO 3 SECONDS.
          lv_attempt = lv_attempt + 1.
        CATCH /aws1/cx_iosresourcenotfoundex.
          " Asset deleted successfully
          RETURN.
      ENDTRY.
    ENDWHILE.

    cl_abap_unit_assert=>fail( msg = 'Asset was not deleted in time' ).
  ENDMETHOD.

  METHOD wait_for_asset_model_deleted.
    DATA lv_max_attempts TYPE i VALUE 40.
    DATA lv_attempt TYPE i VALUE 0.

    WHILE lv_attempt < lv_max_attempts.
      TRY.
          ao_ios->describeassetmodel( iv_assetmodelid = iv_asset_model_id ).
          WAIT UP TO 3 SECONDS.
          lv_attempt = lv_attempt + 1.
        CATCH /aws1/cx_iosresourcenotfoundex.
          " Asset model deleted successfully
          RETURN.
      ENDTRY.
    ENDWHILE.

    cl_abap_unit_assert=>fail( msg = 'Asset model was not deleted in time' ).
  ENDMETHOD.
ENDCLASS.
