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
    CLASS-DATA av_role_arn TYPE /aws1/iamrolearn.
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

    TRY.
        DATA(lo_account_result) = /awsex/cl_utils=>get_account_id( ao_session ).
        DATA(lv_account_id) = lo_account_result->get_account( ).
        DATA(lv_uuid) = /awsex/cl_utils=>get_uuid( ).
        DATA(lv_region) = ao_session->get_region( ).

        av_import_bucket = |sap-hll-import-{ lv_account_id }-{ lv_uuid }|.
        av_export_bucket = |sap-hll-export-{ lv_account_id }-{ lv_uuid }|.

        /awsex/cl_utils=>create_bucket(
          iv_bucket_name = av_import_bucket
          iv_region = CONV string( lv_region )
          io_s3 = ao_s3
        ).

        DATA(lt_import_tags) = VALUE /aws1/cl_s3_tag=>tt_tagset(
          ( NEW /aws1/cl_s3_tag( iv_key = 'convert_test' iv_value = 'true' ) )
        ).
        DATA(lo_tagging) = NEW /aws1/cl_s3_tagging( it_tagset = lt_import_tags ).
        ao_s3->putbuckettagging(
          iv_bucket = av_import_bucket
          io_tagging = lo_tagging
        ).

        /awsex/cl_utils=>create_bucket(
          iv_bucket_name = av_export_bucket
          iv_region = CONV string( lv_region )
          io_s3 = ao_s3
        ).

        DATA(lt_export_tags) = VALUE /aws1/cl_s3_tag=>tt_tagset(
          ( NEW /aws1/cl_s3_tag( iv_key = 'convert_test' iv_value = 'true' ) )
        ).
        DATA(lo_export_tagging) = NEW /aws1/cl_s3_tagging( it_tagset = lt_export_tags ).
        ao_s3->putbuckettagging(
          iv_bucket = av_export_bucket
          io_tagging = lo_export_tagging
        ).

        DATA(lv_role_name) = |SAPHLLRole{ lv_uuid }|.
        DATA(lv_trust_policy) = '{"Version":"2012-10-17","Statement":[{"Effect":"Allow","Principal":{"Service":"healthlake.amazonaws.com"},"Action":"sts:AssumeRole"}]}'.

        DATA(lo_create_role_result) = ao_iam->createrole(
          iv_rolename = lv_role_name
          iv_assumerolepolicydocument = lv_trust_policy
        ).
        av_role_arn = lo_create_role_result->get_role( )->get_arn( ).

        DATA(lt_role_tags) = VALUE /aws1/cl_iamtag=>tt_taglisttype(
          ( NEW /aws1/cl_iamtag( iv_key = 'convert_test' iv_value = 'true' ) )
        ).
        ao_iam->tagrole(
          iv_rolename = lv_role_name
          it_tags = lt_role_tags
        ).

        DATA(lv_policy_doc) = |{"'Version'":"'2012-10-17'","'Statement'":[{"'Effect'":"'Allow'","'Action'":[|
          && |"'s3:GetObject'","'s3:PutObject'","'s3:ListBucket'","'s3:GetBucketLocation'"],|
          && |"'Resource'":[|
          && |"'arn:aws:s3:::{ av_import_bucket }/*'","'arn:aws:s3:::{ av_import_bucket }'",|
          && |"'arn:aws:s3:::{ av_export_bucket }/*'","'arn:aws:s3:::{ av_export_bucket }'"|
          && |]},{"'Effect'":"'Allow'","'Action'":[|
          && |"'kms:Decrypt'","'kms:GenerateDataKey'"],|
          && |"'Resource'":"'*'"}]}|.
        lv_policy_doc = replace( val = lv_policy_doc sub = `'` with = `"` occ = 0 ).

        ao_iam->putrole policy(
          iv_rolename = lv_role_name
          iv_policyname = 'HealthLakePolicy'
          iv_policydocument = lv_policy_doc
        ).

        DATA(lv_key_policy) = |{"'Version'":"'2012-10-17'","'Statement'":[|
          && |{"'Sid'":"'Enable IAM User Permissions'","'Effect'":"'Allow'",|
          && |"'Principal'":{"'AWS'":"'arn:aws:iam::{ lv_account_id }:root'"},|
          && |"'Action'":"'kms:*'","'Resource'":"'*'"},|
          && |{"'Sid'":"'Allow HealthLake'","'Effect'":"'Allow'",|
          && |"'Principal'":{"'Service'":"'healthlake.amazonaws.com'"},|
          && |"'Action'":[|
          && |"'kms:Decrypt'","'kms:GenerateDataKey'"],|
          && |"'Resource'":"'*'"}]}|.
        lv_key_policy = replace( val = lv_key_policy sub = `'` with = `"` occ = 0 ).

        DATA(lo_create_key_result) = ao_kms->createkey(
          iv_description = 'Key for HealthLake encryption'
          iv_policy = lv_key_policy
        ).

        av_kms_key_id = lo_create_key_result->get_keymetadata( )->get_keyid( ).

        DATA(lt_kms_tags) = VALUE /aws1/cl_kmsTag=>tt_taglist(
          ( NEW /aws1/cl_kmstag( iv_tagkey = 'convert_test' iv_tagvalue = 'true' ) )
        ).
        ao_kms->tagresource(
          iv_keyid = av_kms_key_id
          it_tags = lt_kms_tags
        ).

        DATA(lv_fhir_data) = '{"resourceType":"Patient","id":"example","name":[{"use":"official","family":"Chalmers","given":["Peter","James"]}]}'.
        ao_s3->putobject(
          iv_bucket = av_import_bucket
          iv_key = 'patient_example.ndjson'
          iv_body = /awsex/cl_utils=>string_to_xstring( lv_fhir_data )
        ).

      CATCH /aws1/cx_rt_generic INTO DATA(lo_ex).
        cl_abap_unit_assert=>fail( |Failed to create resources: { lo_ex->get_text( ) }| ).
    ENDTRY.

  ENDMETHOD.

  METHOD class_teardown.
    TRY.
        IF av_role_arn IS NOT INITIAL.
          TRY.
              DATA(lv_role_name) = substring_after( val = av_role_arn sub = 'role/' ).
              ao_iam->deleterolepolicy(
                iv_rolename = lv_role_name
                iv_policyname = 'HealthLakePolicy'
              ).
              ao_iam->deleterole( iv_rolename = lv_role_name ).
            CATCH /aws1/cx_rt_generic.
          ENDTRY.
        ENDIF.

        IF av_kms_key_id IS NOT INITIAL.
          TRY.
              ao_kms->schedulekey deletion(
                iv_keyid = av_kms_key_id
                iv_pendingwindowindays = 7
              ).
            CATCH /aws1/cx_rt_generic.
          ENDTRY.
        ENDIF.

      CATCH /aws1/cx_rt_generic.
    ENDTRY.

  ENDMETHOD.

  METHOD create_fhir_datastore.
    DATA(lv_uuid) = /awsex/cl_utils=>get_uuid( ).
    DATA(lv_datastore_name) = |SAPTestDataStore{ lv_uuid }|.

    DATA(lo_result) = ao_hll_actions->create_fhir_datastore( iv_datastore_name = lv_datastore_name ).

    cl_abap_unit_assert=>assert_not_initial(
      act = lo_result
      msg = 'Create FHIR datastore result should not be initial'
    ).

    av_datastore_id = lo_result->get_datastoreid( ).
    av_datastore_arn = lo_result->get_datastorearn( ).

    cl_abap_unit_assert=>assert_not_initial(
      act = av_datastore_id
      msg = 'Datastore ID should not be initial'
    ).

    DATA(lt_tags) = VALUE /aws1/cl_hlltag=>tt_taglist(
      ( NEW /aws1/cl_hlltag( iv_key = 'convert_test' iv_value = 'true' ) )
    ).
    ao_hll->tagresource(
      iv_resourcearn = av_datastore_arn
      it_tags = lt_tags
    ).

    wait_for_datastore_active( av_datastore_id ).
  ENDMETHOD.

  METHOD describe_fhir_datastore.
    cl_abap_unit_assert=>assert_not_initial(
      act = av_datastore_id
      msg = 'Datastore ID should be available from create test'
    ).

    DATA(lo_result) = ao_hll_actions->describe_fhir_datastore( iv_datastore_id = av_datastore_id ).

    cl_abap_unit_assert=>assert_not_initial(
      act = lo_result
      msg = 'Describe result should not be initial'
    ).

    DATA(lo_props) = lo_result->get_datastoreproperties( ).
    cl_abap_unit_assert=>assert_not_initial(
      act = lo_props
      msg = 'Datastore properties should not be initial'
    ).
  ENDMETHOD.

  METHOD list_fhir_datastores.
    DATA(lo_result) = ao_hll_actions->list_fhir_datastores( ).

    cl_abap_unit_assert=>assert_not_initial(
      act = lo_result
      msg = 'List datastores result should not be initial'
    ).

    DATA(lt_datastores) = lo_result->get_datastorepropertieslist( ).
    cl_abap_unit_assert=>assert_not_initial(
      act = lt_datastores
      msg = 'Datastore list should not be empty'
    ).
  ENDMETHOD.

  METHOD tag_resource.
    cl_abap_unit_assert=>assert_not_initial(
      act = av_datastore_arn
      msg = 'Datastore ARN should be available'
    ).

    DATA(lt_tags) = VALUE /aws1/cl_hlltag=>tt_taglist(
      ( NEW /aws1/cl_hlltag( iv_key = 'Environment' iv_value = 'Test' ) )
      ( NEW /aws1/cl_hlltag( iv_key = 'Project' iv_value = 'ABAP-SDK' ) )
    ).

    ao_hll_actions->tag_resource(
      iv_resource_arn = av_datastore_arn
      it_tags = lt_tags
    ).

    DATA(lo_list_result) = ao_hll->listtagsforresource( iv_resourcearn = av_datastore_arn ).
    DATA(lt_result_tags) = lo_list_result->get_tags( ).

    cl_abap_unit_assert=>assert_not_initial(
      act = lt_result_tags
      msg = 'Tags should be present on resource'
    ).
  ENDMETHOD.

  METHOD list_tags_for_resource.
    cl_abap_unit_assert=>assert_not_initial(
      act = av_datastore_arn
      msg = 'Datastore ARN should be available'
    ).

    DATA(lo_result) = ao_hll_actions->list_tags_for_resource( iv_resource_arn = av_datastore_arn ).

    cl_abap_unit_assert=>assert_not_initial(
      act = lo_result
      msg = 'List tags result should not be initial'
    ).

    DATA(lt_tags) = lo_result->get_tags( ).
    cl_abap_unit_assert=>assert_not_initial(
      act = lt_tags
      msg = 'Tags list should not be empty'
    ).
  ENDMETHOD.

  METHOD untag_resource.
    cl_abap_unit_assert=>assert_not_initial(
      act = av_datastore_arn
      msg = 'Datastore ARN should be available'
    ).

    DATA lt_tag_keys TYPE /aws1/cl_hlltagkeylist_w=>tt_tagkeylist.
    APPEND NEW /aws1/cl_hlltagkeylist_w( 'Environment' ) TO lt_tag_keys.

    ao_hll_actions->untag_resource(
      iv_resource_arn = av_datastore_arn
      it_tag_keys = lt_tag_keys
    ).

    DATA(lo_list_result) = ao_hll->listtagsforresource( iv_resourcearn = av_datastore_arn ).
    DATA(lt_remaining_tags) = lo_list_result->get_tags( ).

    DATA lv_env_found TYPE abap_bool VALUE abap_false.
    LOOP AT lt_remaining_tags INTO DATA(lo_tag).
      IF lo_tag->get_key( ) = 'Environment'.
        lv_env_found = abap_true.
      ENDIF.
    ENDLOOP.

    cl_abap_unit_assert=>assert_false(
      act = lv_env_found
      msg = 'Environment tag should have been removed'
    ).
  ENDMETHOD.

  METHOD start_fhir_import_job.
    cl_abap_unit_assert=>assert_not_initial(
      act = av_datastore_id
      msg = 'Datastore ID should be available'
    ).

    DATA(lv_input_uri) = |s3://{ av_import_bucket }/|.
    DATA(lv_output_uri) = |s3://{ av_export_bucket }/import-output/|.

    DATA(lo_result) = ao_hll_actions->start_fhir_import_job(
      iv_datastore_id = av_datastore_id
      iv_input_s3_uri = lv_input_uri
      iv_job_output_uri = lv_output_uri
      iv_dataaccess_arn = av_role_arn
      iv_kms_key_id = av_kms_key_id
    ).

    cl_abap_unit_assert=>assert_not_initial(
      act = lo_result
      msg = 'Start import job result should not be initial'
    ).

    DATA(lv_job_id) = lo_result->get_jobid( ).
    cl_abap_unit_assert=>assert_not_initial(
      act = lv_job_id
      msg = 'Job ID should not be initial'
    ).
  ENDMETHOD.

  METHOD describe_fhir_import_job.
    cl_abap_unit_assert=>assert_not_initial(
      act = av_datastore_id
      msg = 'Datastore ID should be available'
    ).

    DATA(lo_list_result) = ao_hll->listfhirimportjobs( iv_datastoreid = av_datastore_id ).
    DATA(lt_jobs) = lo_list_result->get_importjobpropertieslist( ).

    IF lines( lt_jobs ) > 0.
      DATA(lo_job) = lt_jobs[ 1 ].
      DATA(lv_job_id) = lo_job->get_jobid( ).

      DATA(lo_result) = ao_hll_actions->describe_fhir_import_job(
        iv_datastore_id = av_datastore_id
        iv_job_id = lv_job_id
      ).

      cl_abap_unit_assert=>assert_not_initial(
        act = lo_result
        msg = 'Describe import job result should not be initial'
      ).
    ENDIF.
  ENDMETHOD.

  METHOD list_fhir_import_jobs.
    cl_abap_unit_assert=>assert_not_initial(
      act = av_datastore_id
      msg = 'Datastore ID should be available'
    ).

    DATA(lo_result) = ao_hll_actions->list_fhir_import_jobs( iv_datastore_id = av_datastore_id ).

    cl_abap_unit_assert=>assert_not_initial(
      act = lo_result
      msg = 'List import jobs result should not be initial'
    ).
  ENDMETHOD.

  METHOD start_fhir_export_job.
    cl_abap_unit_assert=>assert_not_initial(
      act = av_datastore_id
      msg = 'Datastore ID should be available'
    ).

    DATA(lv_output_uri) = |s3://{ av_export_bucket }/export-output/|.

    DATA(lo_result) = ao_hll_actions->start_fhir_export_job(
      iv_datastore_id = av_datastore_id
      iv_output_s3_uri = lv_output_uri
      iv_dataaccess_arn = av_role_arn
      iv_kms_key_id = av_kms_key_id
    ).

    cl_abap_unit_assert=>assert_not_initial(
      act = lo_result
      msg = 'Start export job result should not be initial'
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
      msg = 'Datastore ID should be available'
    ).

    DATA(lo_list_result) = ao_hll->listfhirexportjobs( iv_datastoreid = av_datastore_id ).
    DATA(lt_jobs) = lo_list_result->get_exportjobpropertieslist( ).

    IF lines( lt_jobs ) > 0.
      DATA(lo_job) = lt_jobs[ 1 ].
      DATA(lv_job_id) = lo_job->get_jobid( ).

      DATA(lo_result) = ao_hll_actions->describe_fhir_export_job(
        iv_datastore_id = av_datastore_id
        iv_job_id = lv_job_id
      ).

      cl_abap_unit_assert=>assert_not_initial(
        act = lo_result
        msg = 'Describe export job result should not be initial'
      ).
    ENDIF.
  ENDMETHOD.

  METHOD list_fhir_export_jobs.
    cl_abap_unit_assert=>assert_not_initial(
      act = av_datastore_id
      msg = 'Datastore ID should be available'
    ).

    DATA(lo_result) = ao_hll_actions->list_fhir_export_jobs( iv_datastore_id = av_datastore_id ).

    cl_abap_unit_assert=>assert_not_initial(
      act = lo_result
      msg = 'List export jobs result should not be initial'
    ).
  ENDMETHOD.

  METHOD delete_fhir_datastore.
    cl_abap_unit_assert=>assert_not_initial(
      act = av_datastore_id
      msg = 'Datastore ID should be available'
    ).

    DATA(lo_result) = ao_hll_actions->delete_fhir_datastore( iv_datastore_id = av_datastore_id ).

    cl_abap_unit_assert=>assert_not_initial(
      act = lo_result
      msg = 'Delete FHIR datastore result should not be initial'
    ).

    DATA(lv_status) = lo_result->get_datastorestatus( ).
    cl_abap_unit_assert=>assert_equals(
      act = lv_status
      exp = 'DELETING'
      msg = 'Datastore status should be DELETING'
    ).

    CLEAR av_datastore_id.
  ENDMETHOD.

  METHOD wait_for_datastore_active.
    DATA lv_counter TYPE i VALUE 0.
    DATA lv_max_minutes TYPE i VALUE 40.
    DATA lv_status TYPE /aws1/hlldatastorestatus.

    WHILE lv_counter < lv_max_minutes.
      DATA(lo_result) = ao_hll->describefhirdatastore( iv_datastoreid = iv_datastore_id ).
      lv_status = lo_result->get_datastoreproperties( )->get_datastorestatus( ).

      IF lv_status = 'ACTIVE'.
        EXIT.
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
      ELSEIF iv_job_type = 'EXPORT'.
        DATA(lo_export_result) = ao_hll->describefhirexportjob(
          iv_datastoreid = iv_datastore_id
          iv_jobid = iv_job_id
        ).
        lv_status = lo_export_result->get_exportjobproperties( )->get_jobstatus( ).
      ENDIF.

      IF lv_status = 'COMPLETED' OR lv_status = 'COMPLETED_WITH_ERRORS' OR lv_status = 'FAILED'.
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
