" Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
" SPDX-License-Identifier: Apache-2.0
CLASS ltc_awsex_cl_hll_actions DEFINITION DEFERRED.
CLASS /awsex/cl_hll_actions DEFINITION LOCAL FRIENDS ltc_awsex_cl_hll_actions.

CLASS ltc_awsex_cl_hll_actions DEFINITION FOR TESTING DURATION LONG RISK LEVEL DANGEROUS.

  PRIVATE SECTION.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    CLASS-DATA av_datastore_id TYPE /aws1/hlldatastoreid.
    CLASS-DATA av_datastore_arn TYPE /aws1/hlldatastorearn.
    CLASS-DATA av_import_bucket TYPE /aws1/s3_bucketname.
    CLASS-DATA av_export_bucket TYPE /aws1/s3_bucketname.
    CLASS-DATA av_role_arn TYPE /aws1/hlliamrolearn.
    CLASS-DATA av_kms_key_id TYPE /aws1/hllencryptionkeyid.

    CLASS-DATA ao_session TYPE REF TO /aws1/cl_rt_session_base.
    CLASS-DATA ao_hll TYPE REF TO /aws1/if_hll.
    CLASS-DATA ao_s3 TYPE REF TO /aws1/if_s3.
    CLASS-DATA ao_iam TYPE REF TO /aws1/if_iam.
    CLASS-DATA ao_kms TYPE REF TO /aws1/if_kms.
    CLASS-DATA ao_hll_actions TYPE REF TO /awsex/cl_hll_actions.

    CLASS-METHODS class_setup RAISING /aws1/cx_rt_generic.
    CLASS-METHODS class_teardown RAISING /aws1/cx_rt_generic.

    METHODS create_fhir_datastore FOR TESTING RAISING /aws1/cx_rt_generic.
    METHODS describe_fhir_datastore FOR TESTING RAISING /aws1/cx_rt_generic.
    METHODS list_fhir_datastores FOR TESTING RAISING /aws1/cx_rt_generic.
    METHODS tag_resource FOR TESTING RAISING /aws1/cx_rt_generic.
    METHODS list_tags_for_resource FOR TESTING RAISING /aws1/cx_rt_generic.
    METHODS untag_resource FOR TESTING RAISING /aws1/cx_rt_generic.
    METHODS start_fhir_import_job FOR TESTING RAISING /aws1/cx_rt_generic.
    METHODS describe_fhir_import_job FOR TESTING RAISING /aws1/cx_rt_generic.
    METHODS list_fhir_import_jobs FOR TESTING RAISING /aws1/cx_rt_generic.
    METHODS start_fhir_export_job FOR TESTING RAISING /aws1/cx_rt_generic.
    METHODS describe_fhir_export_job FOR TESTING RAISING /aws1/cx_rt_generic.
    METHODS list_fhir_export_jobs FOR TESTING RAISING /aws1/cx_rt_generic.
    METHODS delete_fhir_datastore FOR TESTING RAISING /aws1/cx_rt_generic.

    METHODS wait_for_datastore_active
      IMPORTING
        iv_datastore_id TYPE /aws1/hlldatastoreid
      RAISING
        /aws1/cx_rt_generic.

    METHODS wait_for_job_complete
      IMPORTING
        iv_datastore_id TYPE /aws1/hlldatastoreid
        iv_job_id       TYPE /aws1/hlljobid
        iv_job_type     TYPE string
      RAISING
        /aws1/cx_rt_generic.

ENDCLASS.

CLASS ltc_awsex_cl_hll_actions IMPLEMENTATION.

  METHOD class_setup.
    ao_session = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    ao_hll = /aws1/cl_hll_factory=>create( ao_session ).
    ao_s3 = /aws1/cl_s3_factory=>create( ao_session ).
    ao_iam = /aws1/cl_iam_factory=>create( ao_session ).
    ao_kms = /aws1/cl_kms_factory=>create( ao_session ).
    ao_hll_actions = NEW /awsex/cl_hll_actions( ).

    DATA lv_account_id TYPE string.
    lv_account_id = ao_session->get_account_id( ).
    DATA lv_uuid TYPE sysuuid_c32.
    lv_uuid = cl_system_uuid=>create_uuid_c32_static( ).
    DATA lv_uuid_string TYPE string.
    lv_uuid_string = lv_uuid.
    lv_uuid_string = to_lower( lv_uuid_string ).

    " Create S3 buckets for import and export using util function
    av_import_bucket = |sap-hll-import-{ lv_account_id }-{ lv_uuid_string(8) }|.
    av_export_bucket = |sap-hll-export-{ lv_account_id }-{ lv_uuid_string(8) }|.

    " Create import bucket - ignore if it already exists
    TRY.
        /awsex/cl_utils=>create_bucket(
          iv_bucket = av_import_bucket
          io_s3 = ao_s3
          io_session = ao_session
        ).
      CATCH /aws1/cx_s3_bucketalrdyownedbyyou.
        " Bucket already exists and is owned by us - this is fine
      CATCH /aws1/cx_rt_generic INTO DATA(lo_ex).
        cl_abap_unit_assert=>fail( |Failed to create import bucket: { lo_ex->get_text( ) }| ).
    ENDTRY.

    " Create export bucket - ignore if it already exists
    TRY.
        /awsex/cl_utils=>create_bucket(
          iv_bucket = av_export_bucket
          io_s3 = ao_s3
          io_session = ao_session
        ).
      CATCH /aws1/cx_s3_bucketalrdyownedbyyou.
        " Bucket already exists and is owned by us - this is fine
      CATCH /aws1/cx_rt_generic INTO lo_ex.
        cl_abap_unit_assert=>fail( |Failed to create export bucket: { lo_ex->get_text( ) }| ).
    ENDTRY.

    " Tag buckets with convert_test for cleanup
    TRY.
        ao_s3->putbuckettagging(
          iv_bucket = av_import_bucket
          io_tagging = NEW /aws1/cl_s3_tagging(
            it_tagset = VALUE /aws1/cl_s3_tag=>tt_tagset(
              ( NEW /aws1/cl_s3_tag( iv_key = 'convert_test' iv_value = 'true' ) )
            )
          )
        ).
      CATCH /aws1/cx_rt_generic.
        " Ignore tagging errors
    ENDTRY.

    TRY.
        ao_s3->putbuckettagging(
          iv_bucket = av_export_bucket
          io_tagging = NEW /aws1/cl_s3_tagging(
            it_tagset = VALUE /aws1/cl_s3_tag=>tt_tagset(
              ( NEW /aws1/cl_s3_tag( iv_key = 'convert_test' iv_value = 'true' ) )
            )
          )
        ).
      CATCH /aws1/cx_rt_generic.
        " Ignore tagging errors
    ENDTRY.

    " Create sample FHIR data in import bucket
    TRY.
        DATA lv_fhir_data TYPE string.
        lv_fhir_data = '{"resourceType":"Patient","id":"example","name":[{"family":"Test","given":["John"]}]}'.

        ao_s3->putobject(
          iv_bucket = av_import_bucket
          iv_key = 'patient_example.ndjson'
          iv_body = /aws1/cl_rt_util=>string_to_xstring( lv_fhir_data )
        ).
      CATCH /aws1/cx_rt_generic INTO lo_ex.
        cl_abap_unit_assert=>fail( |Failed to create sample data: { lo_ex->get_text( ) }| ).
    ENDTRY.

    " Create IAM role for HealthLake
    DATA lv_role_name TYPE /aws1/iamrolenametype.
    lv_role_name = |SAPHLLRole{ lv_uuid_string(8) }|.

    DATA lv_trust_policy TYPE string.
    lv_trust_policy = |\{| &&
                      |"Version":"2012-10-17",| &&
                      |"Statement":[\{| &&
                      |"Effect":"Allow",| &&
                      |"Principal":\{"Service":"healthlake.amazonaws.com"\},| &&
                      |"Action":"sts:AssumeRole"| &&
                      |\}]| &&
                      |\}|.

    TRY.
        DATA(lo_create_role_result) = ao_iam->createrole(
          iv_rolename = lv_role_name
          iv_assumerolepolicydocument = lv_trust_policy
          it_tags = VALUE /aws1/cl_iamtag=>tt_taglisttype(
            ( NEW /aws1/cl_iamtag( iv_key = 'convert_test' iv_value = 'true' ) )
          )
        ).

        av_role_arn = lo_create_role_result->get_role( )->get_arn( ).

      CATCH /aws1/cx_iamentityalrdyexists.
        " Role already exists - get the role ARN
        DATA(lo_get_role_result) = ao_iam->getrole( iv_rolename = lv_role_name ).
        av_role_arn = lo_get_role_result->get_role( )->get_arn( ).
      CATCH /aws1/cx_rt_generic INTO lo_ex.
        cl_abap_unit_assert=>fail( |Failed to create IAM role: { lo_ex->get_text( ) }| ).
    ENDTRY.

    " Attach necessary policies to the role for S3 and KMS access
    TRY.
        DATA lv_policy_document TYPE string.
        lv_policy_document = |\{| &&
                            |"Version":"2012-10-17",| &&
                            |"Statement":[\{| &&
                            |"Effect":"Allow",| &&
                            |"Action":["s3:GetObject","s3:PutObject","s3:ListBucket","kms:Decrypt","kms:GenerateDataKey"],| &&
                            |"Resource":["arn:aws:s3:::{ av_import_bucket }/*","arn:aws:s3:::{ av_import_bucket }",| &&
                            |"arn:aws:s3:::{ av_export_bucket }/*","arn:aws:s3:::{ av_export_bucket }","*"]| &&
                            |\}]| &&
                            |\}|.

        ao_iam->putrolepolicy(
          iv_rolename = lv_role_name
          iv_policyname = 'HLLAccessPolicy'
          iv_policydocument = lv_policy_document
        ).
      CATCH /aws1/cx_rt_generic INTO lo_ex.
        " Policy might already be attached, which is fine
    ENDTRY.

    " Wait for IAM role to propagate
    WAIT UP TO 10 SECONDS.

    " Create KMS key for encryption
    TRY.
        DATA lv_key_policy TYPE string.
        lv_key_policy = |\{| &&
                       |"Version":"2012-10-17",| &&
                       |"Statement":[\{| &&
                       |"Sid":"Enable IAM User Permissions",| &&
                       |"Effect":"Allow",| &&
                       |"Principal":\{"AWS":"arn:aws:iam::{ lv_account_id }:root"\},| &&
                       |"Action":"kms:*",| &&
                       |"Resource":"*"| &&
                       |\},\{| &&
                       |"Sid":"Allow HealthLake to use the key",| &&
                       |"Effect":"Allow",| &&
                       |"Principal":\{"Service":"healthlake.amazonaws.com"\},| &&
                       |"Action":["kms:Decrypt","kms:GenerateDataKey"],| &&
                       |"Resource":"*"| &&
                       |\}]| &&
                       |\}|.

        DATA(lo_create_key_result) = ao_kms->createkey(
          iv_description = 'Key for HealthLake test'
          iv_keyusage = 'ENCRYPT_DECRYPT'
          iv_policy = lv_key_policy
          it_tags = VALUE /aws1/cl_kmstag=>tt_taglist(
            ( NEW /aws1/cl_kmstag( iv_tagkey = 'convert_test' iv_tagvalue = 'true' ) )
          )
        ).

        av_kms_key_id = lo_create_key_result->get_keymetadata( )->get_keyid( ).

      CATCH /aws1/cx_rt_generic INTO lo_ex.
        cl_abap_unit_assert=>fail( |Failed to create KMS key: { lo_ex->get_text( ) }| ).
    ENDTRY.

  ENDMETHOD.

  METHOD class_teardown.
    " Note: HealthLake datastores can take a long time to delete
    " We tag resources with 'convert_test' for manual cleanup if needed
    TRY.
        " Delete datastore if it exists
        IF av_datastore_id IS NOT INITIAL.
          TRY.
              ao_hll->deletefhirdatastore( iv_datastoreid = av_datastore_id ).
            CATCH /aws1/cx_rt_generic.
              " Ignore errors during cleanup
          ENDTRY.
        ENDIF.

        " Clean up S3 buckets - do not delete as datastore may still reference them
        " They are tagged for manual cleanup

        " Clean up IAM role
        IF av_role_arn IS NOT INITIAL.
          DATA lv_role_name TYPE /aws1/iamrolenametype.
          SPLIT av_role_arn AT '/' INTO DATA(lv_dummy) lv_role_name.

          TRY.
              ao_iam->deleterolepolicy(
                iv_rolename = lv_role_name
                iv_policyname = 'HLLAccessPolicy'
              ).
            CATCH /aws1/cx_rt_generic.
          ENDTRY.

          TRY.
              ao_iam->deleterole( iv_rolename = lv_role_name ).
            CATCH /aws1/cx_rt_generic.
          ENDTRY.
        ENDIF.

        " Schedule KMS key deletion
        IF av_kms_key_id IS NOT INITIAL.
          TRY.
              ao_kms->schedulekeydeletion(
                iv_keyid = av_kms_key_id
                iv_pendingwindowindays = 7
              ).
            CATCH /aws1/cx_rt_generic.
          ENDTRY.
        ENDIF.

      CATCH /aws1/cx_rt_generic.
        " Ignore cleanup errors
    ENDTRY.
  ENDMETHOD.

  METHOD create_fhir_datastore.
    DATA lv_uuid TYPE sysuuid_c32.
    lv_uuid = cl_system_uuid=>create_uuid_c32_static( ).
    DATA lv_uuid_string TYPE string.
    lv_uuid_string = lv_uuid.
    lv_uuid_string = to_lower( lv_uuid_string ).

    DATA lv_datastore_name TYPE /aws1/hlldatastorename.
    lv_datastore_name = |SAPDS{ lv_uuid_string(20) }|.

    DATA lo_result TYPE REF TO /aws1/cl_hllcrefhirdatastore01.
    ao_hll_actions->create_fhir_datastore(
      EXPORTING
        iv_datastore_name = lv_datastore_name
      IMPORTING
        oo_result = lo_result
    ).

    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = 'Create FHIR datastore result should not be initial'
    ).

    av_datastore_id = lo_result->get_datastoreid( ).
    av_datastore_arn = lo_result->get_datastorearn( ).

    cl_abap_unit_assert=>assert_not_initial(
      act = av_datastore_id
      msg = 'Datastore ID should not be initial'
    ).

    " Tag the datastore with convert_test for cleanup
    DATA lt_tags TYPE /aws1/cl_hlltag=>tt_taglist.
    APPEND NEW /aws1/cl_hlltag(
      iv_key = 'convert_test'
      iv_value = 'true'
    ) TO lt_tags.

    TRY.
        ao_hll->tagresource(
          iv_resourcearn = av_datastore_arn
          it_tags = lt_tags
        ).
      CATCH /aws1/cx_rt_generic.
        " Ignore tagging errors, but datastore is created
    ENDTRY.

    " Wait for datastore to become active
    wait_for_datastore_active( av_datastore_id ).
  ENDMETHOD.

  METHOD describe_fhir_datastore.
    cl_abap_unit_assert=>assert_not_initial(
      act = av_datastore_id
      msg = 'Datastore ID must be created first'
    ).

    DATA lo_result TYPE REF TO /aws1/cl_hlldscfhirdatastore01.
    ao_hll_actions->describe_fhir_datastore(
      EXPORTING
        iv_datastore_id = av_datastore_id
      IMPORTING
        oo_result = lo_result
    ).

    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = 'Describe FHIR datastore result should not be initial'
    ).

    DATA(lo_properties) = lo_result->get_datastoreproperties( ).
    cl_abap_unit_assert=>assert_bound(
      act = lo_properties
      msg = 'Datastore properties should be available'
    ).

    cl_abap_unit_assert=>assert_equals(
      act = lo_properties->get_datastoreid( )
      exp = av_datastore_id
      msg = 'Datastore ID should match'
    ).
  ENDMETHOD.

  METHOD list_fhir_datastores.
    DATA lo_result TYPE REF TO /aws1/cl_hlllstfhirdatastore01.
    ao_hll_actions->list_fhir_datastores(
      IMPORTING
        oo_result = lo_result
    ).

    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = 'List FHIR datastores result should not be initial'
    ).

    DATA(lt_datastores) = lo_result->get_datastorepropertieslist( ).
    cl_abap_unit_assert=>assert_not_initial(
      act = lt_datastores
      msg = 'Should have at least one datastore'
    ).

    " Check if our datastore is in the list
    DATA lv_found TYPE abap_bool VALUE abap_false.
    LOOP AT lt_datastores INTO DATA(lo_datastore).
      IF lo_datastore->get_datastoreid( ) = av_datastore_id.
        lv_found = abap_true.
        EXIT.
      ENDIF.
    ENDLOOP.

    cl_abap_unit_assert=>assert_true(
      act = lv_found
      msg = 'Created datastore should be in the list'
    ).
  ENDMETHOD.

  METHOD tag_resource.
    cl_abap_unit_assert=>assert_not_initial(
      act = av_datastore_arn
      msg = 'Datastore ARN must be available'
    ).

    DATA lt_tags TYPE /aws1/cl_hlltag=>tt_taglist.
    APPEND NEW /aws1/cl_hlltag(
      iv_key = 'Environment'
      iv_value = 'Test'
    ) TO lt_tags.
    APPEND NEW /aws1/cl_hlltag(
      iv_key = 'Project'
      iv_value = 'SAP-ABAP-SDK'
    ) TO lt_tags.

    ao_hll_actions->tag_resource(
      iv_resource_arn = av_datastore_arn
      it_tags = lt_tags
    ).

    " No exception means success
    cl_abap_unit_assert=>assert_true(
      act = abap_true
      msg = 'Tag resource completed successfully'
    ).
  ENDMETHOD.

  METHOD list_tags_for_resource.
    cl_abap_unit_assert=>assert_not_initial(
      act = av_datastore_arn
      msg = 'Datastore ARN must be available'
    ).

    DATA lt_tags TYPE /aws1/cl_hlltag=>tt_taglist.
    ao_hll_actions->list_tags_for_resource(
      EXPORTING
        iv_resource_arn = av_datastore_arn
      IMPORTING
        ot_tags = lt_tags
    ).

    cl_abap_unit_assert=>assert_not_initial(
      act = lt_tags
      msg = 'Should have tags'
    ).

    " Check for our test tags
    DATA lv_found_env TYPE abap_bool VALUE abap_false.
    DATA lv_found_project TYPE abap_bool VALUE abap_false.
    LOOP AT lt_tags INTO DATA(lo_tag).
      IF lo_tag->get_key( ) = 'Environment' AND lo_tag->get_value( ) = 'Test'.
        lv_found_env = abap_true.
      ENDIF.
      IF lo_tag->get_key( ) = 'Project' AND lo_tag->get_value( ) = 'SAP-ABAP-SDK'.
        lv_found_project = abap_true.
      ENDIF.
    ENDLOOP.

    cl_abap_unit_assert=>assert_true(
      act = lv_found_env
      msg = 'Environment tag should be found'
    ).

    cl_abap_unit_assert=>assert_true(
      act = lv_found_project
      msg = 'Project tag should be found'
    ).
  ENDMETHOD.

  METHOD untag_resource.
    cl_abap_unit_assert=>assert_not_initial(
      act = av_datastore_arn
      msg = 'Datastore ARN must be available'
    ).

    DATA lt_tag_keys TYPE /aws1/cl_hlltagkeylist_w=>tt_tagkeylist.
    APPEND NEW /aws1/cl_hlltagkeylist_w( 'Environment' ) TO lt_tag_keys.

    ao_hll_actions->untag_resource(
      iv_resource_arn = av_datastore_arn
      it_tag_keys = lt_tag_keys
    ).

    " Verify tag was removed
    DATA lt_tags TYPE /aws1/cl_hlltag=>tt_taglist.
    ao_hll_actions->list_tags_for_resource(
      EXPORTING
        iv_resource_arn = av_datastore_arn
      IMPORTING
        ot_tags = lt_tags
    ).

    DATA lv_found TYPE abap_bool VALUE abap_false.
    LOOP AT lt_tags INTO DATA(lo_tag).
      IF lo_tag->get_key( ) = 'Environment'.
        lv_found = abap_true.
        EXIT.
      ENDIF.
    ENDLOOP.

    cl_abap_unit_assert=>assert_false(
      act = lv_found
      msg = 'Environment tag should be removed'
    ).
  ENDMETHOD.

  METHOD start_fhir_import_job.
    cl_abap_unit_assert=>assert_not_initial(
      act = av_datastore_id
      msg = 'Datastore ID must be available'
    ).

    DATA lv_uuid TYPE sysuuid_c32.
    lv_uuid = cl_system_uuid=>create_uuid_c32_static( ).
    DATA lv_uuid_string TYPE string.
    lv_uuid_string = lv_uuid.

    DATA lv_job_name TYPE /aws1/hlljobname.
    lv_job_name = |SAPIJ{ lv_uuid_string(20) }|.

    DATA lv_input_s3_uri TYPE /aws1/hlls3uri.
    lv_input_s3_uri = |s3://{ av_import_bucket }/patient_example.ndjson|.

    DATA lv_output_s3_uri TYPE /aws1/hlls3uri.
    lv_output_s3_uri = |s3://{ av_import_bucket }/output/|.

    DATA lo_result TYPE REF TO /aws1/cl_hllstartfhirimpjobrsp.
    ao_hll_actions->start_fhir_import_job(
      EXPORTING
        iv_job_name = lv_job_name
        iv_datastore_id = av_datastore_id
        iv_input_s3_uri = lv_input_s3_uri
        iv_job_output_s3_uri = lv_output_s3_uri
        iv_kms_key_id = av_kms_key_id
        iv_data_access_role_arn = av_role_arn
      IMPORTING
        oo_result = lo_result
    ).

    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = 'Start FHIR import job result should not be initial'
    ).

    DATA(lv_job_id) = lo_result->get_jobid( ).
    cl_abap_unit_assert=>assert_not_initial(
      act = lv_job_id
      msg = 'Job ID should not be initial'
    ).

    " Note: We don't wait for job completion as it can take a long time
  ENDMETHOD.

  METHOD describe_fhir_import_job.
    cl_abap_unit_assert=>assert_not_initial(
      act = av_datastore_id
      msg = 'Datastore ID must be available'
    ).

    " List jobs to get a job ID
    DATA(lo_list_result) = ao_hll->listfhirimportjobs(
      iv_datastoreid = av_datastore_id
    ).

    DATA(lt_jobs) = lo_list_result->get_importjobpropertieslist( ).
    cl_abap_unit_assert=>assert_not_initial(
      act = lt_jobs
      msg = 'Should have at least one import job'
    ).

    READ TABLE lt_jobs INDEX 1 INTO DATA(lo_job).
    DATA(lv_job_id) = lo_job->get_jobid( ).

    DATA lo_result TYPE REF TO /aws1/cl_hlldescrfhirimpjobrsp.
    ao_hll_actions->describe_fhir_import_job(
      EXPORTING
        iv_datastore_id = av_datastore_id
        iv_job_id = lv_job_id
      IMPORTING
        oo_result = lo_result
    ).

    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = 'Describe FHIR import job result should not be initial'
    ).

    DATA(lo_properties) = lo_result->get_importjobproperties( ).
    cl_abap_unit_assert=>assert_bound(
      act = lo_properties
      msg = 'Import job properties should be available'
    ).

    cl_abap_unit_assert=>assert_equals(
      act = lo_properties->get_jobid( )
      exp = lv_job_id
      msg = 'Job ID should match'
    ).
  ENDMETHOD.

  METHOD list_fhir_import_jobs.
    cl_abap_unit_assert=>assert_not_initial(
      act = av_datastore_id
      msg = 'Datastore ID must be available'
    ).

    DATA lo_result TYPE REF TO /aws1/cl_hlllistfhirimpjobsrsp.
    ao_hll_actions->list_fhir_import_jobs(
      EXPORTING
        iv_datastore_id = av_datastore_id
      IMPORTING
        oo_result = lo_result
    ).

    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = 'List FHIR import jobs result should not be initial'
    ).

    DATA(lt_jobs) = lo_result->get_importjobpropertieslist( ).
    cl_abap_unit_assert=>assert_not_initial(
      act = lt_jobs
      msg = 'Should have at least one import job'
    ).
  ENDMETHOD.

  METHOD start_fhir_export_job.
    cl_abap_unit_assert=>assert_not_initial(
      act = av_datastore_id
      msg = 'Datastore ID must be available'
    ).

    DATA lv_uuid TYPE sysuuid_c32.
    lv_uuid = cl_system_uuid=>create_uuid_c32_static( ).
    DATA lv_uuid_string TYPE string.
    lv_uuid_string = lv_uuid.

    DATA lv_job_name TYPE /aws1/hlljobname.
    lv_job_name = |SAPEJ{ lv_uuid_string(20) }|.

    DATA lv_output_s3_uri TYPE /aws1/hlls3uri.
    lv_output_s3_uri = |s3://{ av_export_bucket }/output/|.

    DATA lo_result TYPE REF TO /aws1/cl_hllstartfhirexpjobrsp.
    ao_hll_actions->start_fhir_export_job(
      EXPORTING
        iv_job_name = lv_job_name
        iv_datastore_id = av_datastore_id
        iv_output_s3_uri = lv_output_s3_uri
        iv_kms_key_id = av_kms_key_id
        iv_data_access_role_arn = av_role_arn
      IMPORTING
        oo_result = lo_result
    ).

    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = 'Start FHIR export job result should not be initial'
    ).

    DATA(lv_job_id) = lo_result->get_jobid( ).
    cl_abap_unit_assert=>assert_not_initial(
      act = lv_job_id
      msg = 'Job ID should not be initial'
    ).
  ENDMETHOD.

  METHOD describe_fhir_export_job.
    cl_abap_unit_assert=>assert_not_initial(
      act = av_datastore_id
      msg = 'Datastore ID must be available'
    ).

    " List jobs to get a job ID
    DATA(lo_list_result) = ao_hll->listfhirexportjobs(
      iv_datastoreid = av_datastore_id
    ).

    DATA(lt_jobs) = lo_list_result->get_exportjobpropertieslist( ).
    cl_abap_unit_assert=>assert_not_initial(
      act = lt_jobs
      msg = 'Should have at least one export job'
    ).

    READ TABLE lt_jobs INDEX 1 INTO DATA(lo_job).
    DATA(lv_job_id) = lo_job->get_jobid( ).

    DATA lo_result TYPE REF TO /aws1/cl_hlldescrfhirexpjobrsp.
    ao_hll_actions->describe_fhir_export_job(
      EXPORTING
        iv_datastore_id = av_datastore_id
        iv_job_id = lv_job_id
      IMPORTING
        oo_result = lo_result
    ).

    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = 'Describe FHIR export job result should not be initial'
    ).

    DATA(lo_properties) = lo_result->get_exportjobproperties( ).
    cl_abap_unit_assert=>assert_bound(
      act = lo_properties
      msg = 'Export job properties should be available'
    ).

    cl_abap_unit_assert=>assert_equals(
      act = lo_properties->get_jobid( )
      exp = lv_job_id
      msg = 'Job ID should match'
    ).
  ENDMETHOD.

  METHOD list_fhir_export_jobs.
    cl_abap_unit_assert=>assert_not_initial(
      act = av_datastore_id
      msg = 'Datastore ID must be available'
    ).

    DATA lo_result TYPE REF TO /aws1/cl_hlllistfhirexpjobsrsp.
    ao_hll_actions->list_fhir_export_jobs(
      EXPORTING
        iv_datastore_id = av_datastore_id
      IMPORTING
        oo_result = lo_result
    ).

    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = 'List FHIR export jobs result should not be initial'
    ).

    DATA(lt_jobs) = lo_result->get_exportjobpropertieslist( ).
    cl_abap_unit_assert=>assert_not_initial(
      act = lt_jobs
      msg = 'Should have at least one export job'
    ).
  ENDMETHOD.

  METHOD delete_fhir_datastore.
    cl_abap_unit_assert=>assert_not_initial(
      act = av_datastore_id
      msg = 'Datastore ID must be available'
    ).

    DATA lo_result TYPE REF TO /aws1/cl_hlldelfhirdatastore01.
    ao_hll_actions->delete_fhir_datastore(
      EXPORTING
        iv_datastore_id = av_datastore_id
      IMPORTING
        oo_result = lo_result
    ).

    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = 'Delete FHIR datastore result should not be initial'
    ).

    DATA(lv_status) = lo_result->get_datastorestatus( ).
    cl_abap_unit_assert=>assert_equals(
      act = lv_status
      exp = 'DELETING'
      msg = 'Datastore status should be DELETING'
    ).

    " Clear the ID so class_teardown doesn't try to delete again
    CLEAR av_datastore_id.
  ENDMETHOD.

  METHOD wait_for_datastore_active.
    DATA lv_counter TYPE i VALUE 0.
    DATA lv_max_minutes TYPE i VALUE 40.
    DATA lv_status TYPE /aws1/hlldatastorestatus.

    WHILE lv_counter < lv_max_minutes.
      DATA(lo_result) = ao_hll->describefhirdatastore(
        iv_datastoreid = iv_datastore_id
      ).

      lv_status = lo_result->get_datastoreproperties( )->get_datastorestatus( ).

      IF lv_status = 'ACTIVE'.
        EXIT.
      ELSEIF lv_status = 'CREATE_FAILED'.
        cl_abap_unit_assert=>fail( |Datastore creation failed after { lv_counter } minutes| ).
      ENDIF.

      WAIT UP TO 60 SECONDS.
      lv_counter = lv_counter + 1.
    ENDWHILE.

    IF lv_status <> 'ACTIVE'.
      cl_abap_unit_assert=>fail( |Datastore did not become active after { lv_max_minutes } minutes| ).
    ENDIF.
  ENDMETHOD.

  METHOD wait_for_job_complete.
    DATA lv_counter TYPE i VALUE 0.
    DATA lv_max_minutes TYPE i VALUE 20.
    DATA lv_status TYPE /aws1/hlljobstatus.

    WHILE lv_counter < lv_max_minutes.
      IF iv_job_type = 'IMPORT'.
        DATA(lo_import_result) = ao_hll->describefhirimportjob(
          iv_datastoreid = iv_datastore_id
          iv_jobid = iv_job_id
        ).
        lv_status = lo_import_result->get_importjobproperties( )->get_jobstatus( ).
      ELSE.
        DATA(lo_export_result) = ao_hll->describefhirexportjob(
          iv_datastoreid = iv_datastore_id
          iv_jobid = iv_job_id
        ).
        lv_status = lo_export_result->get_exportjobproperties( )->get_jobstatus( ).
      ENDIF.

      IF lv_status = 'COMPLETED' OR lv_status = 'COMPLETED_WITH_ERRORS'.
        EXIT.
      ENDIF.

      WAIT UP TO 60 SECONDS.
      lv_counter = lv_counter + 1.
    ENDWHILE.

    IF lv_status <> 'COMPLETED' AND lv_status <> 'COMPLETED_WITH_ERRORS'.
      cl_abap_unit_assert=>fail( |Job did not complete after { lv_max_minutes } minutes| ).
    ENDIF.
  ENDMETHOD.

ENDCLASS.
