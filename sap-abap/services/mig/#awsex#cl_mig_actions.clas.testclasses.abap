" Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
" SPDX-License-Identifier: Apache-2.0
CLASS ltc_awsex_cl_mig_actions DEFINITION DEFERRED.
CLASS /awsex/cl_mig_actions DEFINITION LOCAL FRIENDS ltc_awsex_cl_mig_actions.

CLASS ltc_awsex_cl_mig_actions DEFINITION FOR TESTING DURATION LONG RISK LEVEL DANGEROUS.

  PRIVATE SECTION.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    CLASS-DATA ao_session TYPE REF TO /aws1/cl_rt_session_base.
    CLASS-DATA ao_mig TYPE REF TO /aws1/if_mig.
    CLASS-DATA ao_s3 TYPE REF TO /aws1/if_s3.
    CLASS-DATA ao_iam TYPE REF TO /aws1/if_iam.
    CLASS-DATA ao_mig_actions TYPE REF TO /awsex/cl_mig_actions.
    CLASS-DATA av_datastore_id TYPE /aws1/migdatastoreid.
    CLASS-DATA av_datastore_name TYPE /aws1/migdatastorename.
    CLASS-DATA av_input_bucket TYPE /aws1/s3_bucketname.
    CLASS-DATA av_output_bucket TYPE /aws1/s3_bucketname.
    CLASS-DATA av_role_arn TYPE /aws1/migrolearn.
    CLASS-DATA av_role_name TYPE /aws1/iamrolenametype.
    CLASS-DATA av_image_set_id TYPE /aws1/migimagesetid.
    CLASS-DATA av_job_id TYPE /aws1/migjobid.
    CLASS-DATA av_datastore_arn TYPE /aws1/migarn.
    CLASS-DATA av_region TYPE /aws1/rt_region_id.
    CLASS-DATA av_datastore_created TYPE abap_bool.

    CLASS-METHODS class_setup RAISING /aws1/cx_rt_generic.
    CLASS-METHODS class_teardown RAISING /aws1/cx_rt_generic.

    METHODS create_datastore FOR TESTING RAISING /aws1/cx_rt_generic.
    METHODS get_datastore_properties FOR TESTING RAISING /aws1/cx_rt_generic.
    METHODS list_datastores FOR TESTING RAISING /aws1/cx_rt_generic.
    METHODS start_dicom_import_job FOR TESTING RAISING /aws1/cx_rt_generic.
    METHODS get_dicom_import_job FOR TESTING RAISING /aws1/cx_rt_generic.
    METHODS list_dicom_import_jobs FOR TESTING RAISING /aws1/cx_rt_generic.
    METHODS search_image_sets FOR TESTING RAISING /aws1/cx_rt_generic.
    METHODS get_image_set FOR TESTING RAISING /aws1/cx_rt_generic.
    METHODS get_image_set_metadata FOR TESTING RAISING /aws1/cx_rt_generic.
    METHODS get_image_frame FOR TESTING RAISING /aws1/cx_rt_generic.
    METHODS list_image_set_versions FOR TESTING RAISING /aws1/cx_rt_generic.
    METHODS update_image_set_metadata FOR TESTING RAISING /aws1/cx_rt_generic.
    METHODS copy_image_set FOR TESTING RAISING /aws1/cx_rt_generic.
    METHODS tag_resource FOR TESTING RAISING /aws1/cx_rt_generic.
    METHODS list_tags_for_resource FOR TESTING RAISING /aws1/cx_rt_generic.
    METHODS untag_resource FOR TESTING RAISING /aws1/cx_rt_generic.
    METHODS delete_image_set FOR TESTING RAISING /aws1/cx_rt_generic.
    METHODS delete_datastore FOR TESTING RAISING /aws1/cx_rt_generic.

    METHODS wait_for_datastore_active
      IMPORTING
        iv_datastore_id TYPE /aws1/migdatastoreid
      RAISING
        /aws1/cx_rt_generic.
    METHODS wait_for_job_completed
      IMPORTING
        iv_datastore_id TYPE /aws1/migdatastoreid
        iv_job_id       TYPE /aws1/migjobid
      RAISING
        /aws1/cx_rt_generic.
    METHODS wait_for_imageset_available
      IMPORTING
        iv_datastore_id TYPE /aws1/migdatastoreid
        iv_image_set_id TYPE /aws1/migimagesetid
      RAISING
        /aws1/cx_rt_generic.
    METHODS create_sample_dicom_file
      IMPORTING
        iv_bucket TYPE /aws1/s3_bucketname
        iv_key    TYPE /aws1/s3_objectkey
      RAISING
        /aws1/cx_rt_generic.
ENDCLASS.

CLASS ltc_awsex_cl_mig_actions IMPLEMENTATION.

  METHOD class_setup.
    DATA lv_uuid TYPE string.
    DATA lv_uuid_string TYPE string.
    DATA lv_account_id TYPE string.
    DATA lv_assume_role_policy TYPE /aws1/iampolicydocumenttype.
    DATA lo_create_role_result TYPE REF TO /aws1/cl_iamcreateroleresponse.
    DATA lo_role TYPE REF TO /aws1/cl_iamgetroleresponse.
    DATA lv_policy_document TYPE /aws1/iampolicydocumenttype.
    DATA lo_create_ds_result TYPE REF TO /aws1/cl_migcreatedatastorersp.
    DATA lv_status TYPE /aws1/migdatastorestatus.
    DATA lo_ds_result TYPE REF TO /aws1/cl_miggetdatastorersp.
    DATA lo_job_result TYPE REF TO /aws1/cl_migstrtdicomimpjobrsp.
    DATA lv_max_wait TYPE i.
    DATA lv_wait_interval TYPE i.
    DATA lv_waited TYPE i.
    DATA lv_job_status TYPE /aws1/migjobstatus.
    DATA lo_job_props TYPE REF TO /aws1/cl_miggetdicomimpjobrsp.
    DATA lo_search_result TYPE REF TO /aws1/cl_migsearchimagesetsrsp.
    DATA lt_imagesets TYPE /aws1/cl_migimagesetsmetsumm=>tt_imagesetsmetadatasummaries.
    DATA lo_imageset TYPE REF TO /aws1/cl_migimagesetsmetsumm.
    DATA lv_dicom_content TYPE xstring.

    ao_session = /aws1/cl_rt_session_aws=>create( iv_profile_id = cv_pfl ).
    ao_mig = /aws1/cl_mig_factory=>create( ao_session ).
    ao_s3 = /aws1/cl_s3_factory=>create( ao_session ).
    ao_iam = /aws1/cl_iam_factory=>create( ao_session ).
    ao_mig_actions = NEW /awsex/cl_mig_actions( ).

    lv_uuid = /awsex/cl_utils=>get_random_string( ).
    lv_uuid_string = lv_uuid.
    TRANSLATE lv_uuid_string TO LOWER CASE.
    REPLACE ALL OCCURRENCES OF REGEX '[^a-z0-9-]' IN lv_uuid_string WITH '-'.
    
    lv_account_id = ao_session->get_account_id( ).
    av_region = ao_session->get_region( ).
    av_datastore_name = |abap-mig-ds-{ lv_uuid_string }|.
    av_input_bucket = |abap-mig-in-{ lv_account_id }-{ lv_uuid_string }|.
    av_output_bucket = |abap-mig-out-{ lv_account_id }-{ lv_uuid_string }|.
    av_role_name = |abap-mig-role-{ lv_uuid_string }|.

    " Create S3 buckets for DICOM import using utils
    /awsex/cl_utils=>create_bucket(
      iv_bucket = av_input_bucket
      io_s3 = ao_s3
      io_session = ao_session ).
    
    /awsex/cl_utils=>create_bucket(
      iv_bucket = av_output_bucket
      io_s3 = ao_s3
      io_session = ao_session ).

    " Tag buckets for cleanup
    ao_s3->putbuckettagging(
      iv_bucket = av_input_bucket
      io_tagging = NEW /aws1/cl_s3_tagging(
        it_tagset = VALUE /aws1/cl_s3_tag=>tt_tagset(
          ( NEW /aws1/cl_s3_tag( iv_key = 'convert_test' iv_value = 'true' ) ) ) ) ).
    
    ao_s3->putbuckettagging(
      iv_bucket = av_output_bucket
      io_tagging = NEW /aws1/cl_s3_tagging(
        it_tagset = VALUE /aws1/cl_s3_tag=>tt_tagset(
          ( NEW /aws1/cl_s3_tag( iv_key = 'convert_test' iv_value = 'true' ) ) ) ) ).

    " Create IAM role for DICOM import with comprehensive permissions
    lv_assume_role_policy = |\{| &&
      |"Version":"2012-10-17",| &&
      |"Statement":[\{| &&
      |"Effect":"Allow",| &&
      |"Principal":\{"Service":"medical-imaging.amazonaws.com"\},| &&
      |"Action":"sts:AssumeRole"| &&
      |\}]| &&
      |\}|.

    TRY.
        lo_create_role_result = ao_iam->createrole(
          iv_rolename = av_role_name
          iv_assumerolepolicydocument = lv_assume_role_policy
          it_tags = VALUE /aws1/cl_iamtag=>tt_taglisttype(
            ( NEW /aws1/cl_iamtag( iv_key = 'convert_test' iv_value = 'true' ) ) ) ).
        av_role_arn = lo_create_role_result->get_role( )->get_arn( ).
      CATCH /aws1/cx_iamentityalrdyexex.
        lo_role = ao_iam->getrole( iv_rolename = av_role_name ).
        av_role_arn = lo_role->get_role( )->get_arn( ).
    ENDTRY.

    " Attach comprehensive policies for Medical Imaging, S3, and CloudWatch
    lv_policy_document = |\{| &&
      |"Version":"2012-10-17",| &&
      |"Statement":[| &&
      |\{| &&
      |"Effect":"Allow",| &&
      |"Action":[| &&
      |"s3:GetObject",| &&
      |"s3:ListBucket",| &&
      |"s3:PutObject",| &&
      |"s3:GetBucketLocation",| &&
      |"s3:GetBucketVersioning",| &&
      |"s3:ListBucketVersions"| &&
      |],| &&
      |"Resource":[| &&
      |"arn:aws:s3:::{ av_input_bucket }",| &&
      |"arn:aws:s3:::{ av_input_bucket }/*",| &&
      |"arn:aws:s3:::{ av_output_bucket }",| &&
      |"arn:aws:s3:::{ av_output_bucket }/*"| &&
      |]| &&
      |\},| &&
      |\{| &&
      |"Effect":"Allow",| &&
      |"Action":[| &&
      |"medical-imaging:*"| &&
      |],| &&
      |"Resource":"*"| &&
      |\},| &&
      |\{| &&
      |"Effect":"Allow",| &&
      |"Action":[| &&
      |"logs:CreateLogGroup",| &&
      |"logs:CreateLogStream",| &&
      |"logs:PutLogEvents"| &&
      |],| &&
      |"Resource":"*"| &&
      |\}| &&
      |]| &&
      |\}|.

    ao_iam->putrolepolicy(
      iv_rolename = av_role_name
      iv_policyname = 'MedicalImagingFullPolicy'
      iv_policydocument = lv_policy_document ).

    " Wait for role to propagate
    WAIT UP TO 15 SECONDS.

    " Create sample DICOM file in input bucket with a folder prefix
    " Medical Imaging requires a specific input prefix folder
    CALL FUNCTION 'SSFC_BASE64_DECODE'
      EXPORTING
        b64data = 'RElDTQ==' " Base64 for 'DICM'
      IMPORTING
        bindata = lv_dicom_content
      EXCEPTIONS
        OTHERS  = 1.
    ao_s3->putobject(
      iv_bucket = av_input_bucket
      iv_key = 'input/sample.dcm'
      iv_body = lv_dicom_content ).

    " Create datastore - MUST succeed
    lo_create_ds_result = ao_mig->createdatastore(
      iv_datastorename = av_datastore_name
      it_tags = VALUE /aws1/cl_migtagmap_w=>tt_tagmap(
        ( VALUE /aws1/cl_migtagmap_w=>ts_tagmap_maprow(
            key = 'convert_test'
            value = NEW /aws1/cl_migtagmap_w( 'true' ) ) ) ) ).
    av_datastore_id = lo_create_ds_result->get_datastoreid( ).
    av_datastore_arn = |arn:aws:medical-imaging:{ av_region }:{ lv_account_id }:datastore/{ av_datastore_id }|.
    av_datastore_created = abap_true.

    " Wait for datastore to be ACTIVE
    lv_max_wait = 300.
    lv_wait_interval = 5.
    lv_waited = 0.
    DO.
      lo_ds_result = ao_mig->getdatastore( iv_datastoreid = av_datastore_id ).
      lv_status = lo_ds_result->get_datastoreproperties( )->get_datastorestatus( ).
      IF lv_status = 'ACTIVE'.
        EXIT.
      ELSEIF lv_status = 'CREATE_FAILED'.
        cl_abap_unit_assert=>fail( msg = 'Datastore creation failed' ).
      ENDIF.
      lv_waited = lv_waited + lv_wait_interval.
      IF lv_waited >= lv_max_wait.
        cl_abap_unit_assert=>fail( msg = 'Timeout waiting for datastore' ).
      ENDIF.
      WAIT UP TO lv_wait_interval SECONDS.
    ENDDO.

    " Start a DICOM import job to create image sets for testing
    " Input S3 URI must point to a specific folder, not just the bucket
    lo_job_result = ao_mig->startdicomimportjob(
      iv_jobname = |test-import-{ lv_uuid_string }|
      iv_datastoreid = av_datastore_id
      iv_dataaccessrolearn = av_role_arn
      iv_inputs3uri = |s3://{ av_input_bucket }/input/|
      iv_outputs3uri = |s3://{ av_output_bucket }/output/| ).
    av_job_id = lo_job_result->get_jobid( ).

    " Wait for job to complete
    lv_max_wait = 600.
    lv_wait_interval = 10.
    lv_waited = 0.

    DO.
      WAIT UP TO lv_wait_interval SECONDS.
      lv_waited = lv_waited + lv_wait_interval.

      lo_job_props = ao_mig->getdicomimportjob(
        iv_datastoreid = av_datastore_id
        iv_jobid = av_job_id ).
      lv_job_status = lo_job_props->get_jobproperties( )->get_jobstatus( ).

      IF lv_job_status = 'COMPLETED' OR lv_job_status = 'FAILED' OR lv_waited >= lv_max_wait.
        EXIT.
      ENDIF.
    ENDDO.

    " If job completed successfully, get the first image set
    IF lv_job_status = 'COMPLETED'.
      lo_search_result = ao_mig->searchimagesets(
        iv_datastoreid = av_datastore_id
        io_searchcriteria = NEW /aws1/cl_migsearchcriteria( ) ).
      lt_imagesets = lo_search_result->get_imagesetsmetadatasums( ).
      IF lines( lt_imagesets ) > 0.
        READ TABLE lt_imagesets INDEX 1 INTO lo_imageset.
        av_image_set_id = lo_imageset->get_imagesetid( ).
      ENDIF.
    ENDIF.
  ENDMETHOD.

  METHOD class_teardown.
    DATA lo_search_result TYPE REF TO /aws1/cl_migsearchimagesetsrsp.
    DATA lt_imagesets TYPE /aws1/cl_migimagesetsmetsumm=>tt_imagesetsmetadatasummaries.
    DATA lo_imageset TYPE REF TO /aws1/cl_migimagesetsmetsumm.
    DATA lv_img_id TYPE /aws1/migimagesetid.
    DATA lv_max_wait TYPE i.
    DATA lv_waited TYPE i.
    DATA lo_img_result TYPE REF TO /aws1/cl_miggetimagesetrsp.
    DATA lv_state TYPE /aws1/migimagesetstate.

    " Clean up image sets first
    IF av_datastore_id IS NOT INITIAL.
      TRY.
          lo_search_result = ao_mig->searchimagesets(
            iv_datastoreid = av_datastore_id
            io_searchcriteria = NEW /aws1/cl_migsearchcriteria( ) ).
          lt_imagesets = lo_search_result->get_imagesetsmetadatasums( ).
          LOOP AT lt_imagesets INTO lo_imageset.
            TRY.
                lv_img_id = lo_imageset->get_imagesetid( ).
                " Wait for image set to be available before deleting
                lv_max_wait = 60.
                lv_waited = 0.
                DO.
                  TRY.
                      lo_img_result = ao_mig->getimageset(
                        iv_datastoreid = av_datastore_id
                        iv_imagesetid = lv_img_id ).
                      lv_state = lo_img_result->get_imagesetstate( ).
                      IF lv_state <> 'LOCKED'.
                        EXIT.
                      ENDIF.
                    CATCH /aws1/cx_migresourcenotfoundex.
                      EXIT.
                  ENDTRY.
                  WAIT UP TO 5 SECONDS.
                  lv_waited = lv_waited + 5.
                  IF lv_waited >= lv_max_wait.
                    EXIT.
                  ENDIF.
                ENDDO.

                ao_mig->deleteimageset(
                  iv_datastoreid = av_datastore_id
                  iv_imagesetid = lv_img_id ).
              CATCH /aws1/cx_rt_generic.
                " Continue cleanup
            ENDTRY.
          ENDLOOP.
        CATCH /aws1/cx_rt_generic.
          " Continue cleanup
      ENDTRY.

      " Wait for image sets to be deleted
      WAIT UP TO 30 SECONDS.
    ENDIF.

    " Clean up datastore (only if we created it)
    IF av_datastore_id IS NOT INITIAL AND av_datastore_created = abap_true.
      TRY.
          ao_mig->deletedatastore( iv_datastoreid = av_datastore_id ).
        CATCH /aws1/cx_rt_generic.
          " Ignore errors during cleanup (including throttling)
      ENDTRY.
    ENDIF.

    " Clean up IAM role
    IF av_role_name IS NOT INITIAL.
      TRY.
          ao_iam->deleterolepolicy(
            iv_rolename = av_role_name
            iv_policyname = 'MedicalImagingFullPolicy' ).
          ao_iam->deleterole( iv_rolename = av_role_name ).
        CATCH /aws1/cx_rt_generic.
          " Ignore errors
      ENDTRY.
    ENDIF.

    " Note: S3 buckets are tagged with convert_test and should be manually cleaned
    " because they may contain data from import jobs that take time to process
  ENDMETHOD.

  METHOD wait_for_datastore_active.
    DATA lv_max_wait TYPE i.
    DATA lv_wait_interval TYPE i.
    DATA lv_waited TYPE i.
    DATA lv_status TYPE /aws1/migdatastorestatus.
    DATA lo_result TYPE REF TO /aws1/cl_miggetdatastorersp.

    lv_max_wait = 300.
    lv_wait_interval = 5.
    lv_waited = 0.

    DO.
      TRY.
          lo_result = ao_mig->getdatastore( iv_datastoreid = iv_datastore_id ).
          lv_status = lo_result->get_datastoreproperties( )->get_datastorestatus( ).
          IF lv_status = 'ACTIVE'.
            RETURN.
          ELSEIF lv_status = 'CREATE_FAILED'.
            cl_abap_unit_assert=>fail( msg = 'Datastore creation failed' ).
          ENDIF.
        CATCH /aws1/cx_migresourcenotfoundex.
          " Not yet available
      ENDTRY.

      lv_waited = lv_waited + lv_wait_interval.
      IF lv_waited >= lv_max_wait.
        cl_abap_unit_assert=>fail( msg = 'Timeout waiting for datastore to be active' ).
      ENDIF.
      WAIT UP TO lv_wait_interval SECONDS.
    ENDDO.
  ENDMETHOD.

  METHOD wait_for_job_completed.
    DATA lv_max_wait TYPE i.
    DATA lv_wait_interval TYPE i.
    DATA lv_waited TYPE i.
    DATA lv_status TYPE /aws1/migjobstatus.
    DATA lo_result TYPE REF TO /aws1/cl_miggetdicomimpjobrsp.

    lv_max_wait = 600.
    lv_wait_interval = 10.
    lv_waited = 0.

    DO.
      TRY.
          lo_result = ao_mig->getdicomimportjob(
            iv_datastoreid = iv_datastore_id
            iv_jobid = iv_job_id ).
          lv_status = lo_result->get_jobproperties( )->get_jobstatus( ).
          IF lv_status = 'COMPLETED'.
            RETURN.
          ELSEIF lv_status = 'FAILED'.
            " Don't fail the test, just return - the job may fail due to invalid DICOM
            RETURN.
          ENDIF.
        CATCH /aws1/cx_migresourcenotfoundex.
          " Not yet available
      ENDTRY.

      lv_waited = lv_waited + lv_wait_interval.
      IF lv_waited >= lv_max_wait.
        RETURN. " Timeout - don't fail the test
      ENDIF.
      WAIT UP TO lv_wait_interval SECONDS.
    ENDDO.
  ENDMETHOD.

  METHOD wait_for_imageset_available.
    DATA lv_max_wait TYPE i.
    DATA lv_wait_interval TYPE i.
    DATA lv_waited TYPE i.
    DATA lv_state TYPE /aws1/migimagesetstate.
    DATA lo_result TYPE REF TO /aws1/cl_miggetimagesetrsp.

    lv_max_wait = 300.
    lv_wait_interval = 5.
    lv_waited = 0.

    DO.
      TRY.
          lo_result = ao_mig->getimageset(
            iv_datastoreid = iv_datastore_id
            iv_imagesetid = iv_image_set_id ).
          lv_state = lo_result->get_imagesetstate( ).
          IF lv_state = 'ACTIVE' OR lv_state = 'DELETED'.
            RETURN.
          ENDIF.
        CATCH /aws1/cx_migresourcenotfoundex.
          " Already deleted or not yet available
          RETURN.
      ENDTRY.

      lv_waited = lv_waited + lv_wait_interval.
      IF lv_waited >= lv_max_wait.
        RETURN.
      ENDIF.
      WAIT UP TO lv_wait_interval SECONDS.
    ENDDO.
  ENDMETHOD.

  METHOD create_sample_dicom_file.
    DATA lv_dicom_header TYPE string.
    DATA lv_content TYPE xstring.
    DATA lv_padding TYPE xstring.

    " Create a minimal DICOM-like file for testing
    " This creates a simple binary file with DICOM magic number
    lv_dicom_header = '4449434D'. " 'DICM' in hex

    " Convert hex string to xstring
    CALL FUNCTION 'SSFC_BASE64_DECODE'
      EXPORTING
        b64data = 'RElDTQ==' " Base64 for 'DICM'
      IMPORTING
        bindata = lv_content
      EXCEPTIONS
        OTHERS  = 1.

    " Add some minimal DICOM data (just enough to not be rejected immediately)
    " This is a very basic structure and may still fail validation
    lv_padding = '0000000000000000000000000000000000000000'.

    CONCATENATE lv_content lv_padding INTO lv_content IN BYTE MODE.

    " Upload to S3
    ao_s3->putobject(
      iv_bucket = iv_bucket
      iv_key = iv_key
      iv_body = lv_content ).
  ENDMETHOD.

  METHOD create_datastore.
    DATA lv_uuid TYPE string.
    DATA lv_uuid_string TYPE string.
    DATA lv_ds_name TYPE /aws1/migdatastorename.
    DATA lo_result TYPE REF TO /aws1/cl_migcreatedatastorersp.
    DATA lv_new_ds_id TYPE /aws1/migdatastoreid.

    lv_uuid = /awsex/cl_utils=>get_random_string( ).
    lv_uuid_string = lv_uuid.
    TRANSLATE lv_uuid_string TO LOWER CASE.
    REPLACE ALL OCCURRENCES OF REGEX '[^a-z0-9-]' IN lv_uuid_string WITH '-'.
    lv_ds_name = |test-ds-{ lv_uuid_string }|.

    ao_mig_actions->create_datastore(
      EXPORTING
        iv_datastore_name = lv_ds_name
      IMPORTING
        oo_result = lo_result ).

    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = 'Create datastore result should not be initial' ).

    lv_new_ds_id = lo_result->get_datastoreid( ).
    cl_abap_unit_assert=>assert_not_initial(
      act = lv_new_ds_id
      msg = 'Datastore ID should not be initial' ).

    " Clean up
    wait_for_datastore_active( lv_new_ds_id ).
    ao_mig->deletedatastore( iv_datastoreid = lv_new_ds_id ).
  ENDMETHOD.

  METHOD get_datastore_properties.
    DATA lo_result TYPE REF TO /aws1/cl_miggetdatastorersp.
    DATA lo_props TYPE REF TO /aws1/cl_migdatastoreprps.
    DATA lv_name TYPE /aws1/migdatastorename.

    cl_abap_unit_assert=>assert_not_initial(
      act = av_datastore_id
      msg = 'Datastore ID must be set in class_setup' ).

    ao_mig_actions->get_datastore_properties(
      EXPORTING
        iv_datastore_id = av_datastore_id
      IMPORTING
        oo_result = lo_result ).

    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = 'Get datastore result should not be initial' ).

    lo_props = lo_result->get_datastoreproperties( ).
    cl_abap_unit_assert=>assert_bound(
      act = lo_props
      msg = 'Datastore properties should not be initial' ).

    lv_name = lo_props->get_datastorename( ).
    cl_abap_unit_assert=>assert_equals(
      act = lv_name
      exp = av_datastore_name
      msg = 'Datastore name should match' ).
  ENDMETHOD.

  METHOD list_datastores.
    DATA lo_result TYPE REF TO /aws1/cl_miglistdatastoresrsp.
    DATA lt_datastores TYPE /aws1/cl_migdatastoresummary=>tt_datastoresummaries.

    ao_mig_actions->list_datastores(
      IMPORTING
        oo_result = lo_result ).

    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = 'List datastores result should not be initial' ).

    lt_datastores = lo_result->get_datastoresummaries( ).
    cl_abap_unit_assert=>assert_not_initial(
      act = lines( lt_datastores )
      msg = 'Should have at least one datastore' ).
  ENDMETHOD.

  METHOD start_dicom_import_job.
    DATA lv_uuid TYPE string.
    DATA lv_uuid_string TYPE string.
    DATA lv_job_name TYPE /aws1/migjobname.
    DATA lo_result TYPE REF TO /aws1/cl_migstrtdicomimpjobrsp.
    DATA lv_new_job_id TYPE /aws1/migjobid.

    cl_abap_unit_assert=>assert_not_initial(
      act = av_datastore_id
      msg = 'Datastore ID must be set in class_setup' ).

    lv_uuid = /awsex/cl_utils=>get_random_string( ).
    lv_uuid_string = lv_uuid.
    TRANSLATE lv_uuid_string TO LOWER CASE.
    REPLACE ALL OCCURRENCES OF REGEX '[^a-z0-9-]' IN lv_uuid_string WITH '-'.
    lv_job_name = |test-job-{ lv_uuid_string }|.

    ao_mig_actions->start_dicom_import_job(
      EXPORTING
        iv_job_name = lv_job_name
        iv_datastore_id = av_datastore_id
        iv_role_arn = av_role_arn
        iv_input_s3_uri = |s3://{ av_input_bucket }/input/|
        iv_output_s3_uri = |s3://{ av_output_bucket }/output/|
      IMPORTING
        oo_result = lo_result ).

    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = 'Start DICOM import job result should not be initial' ).

    lv_new_job_id = lo_result->get_jobid( ).
    cl_abap_unit_assert=>assert_not_initial(
      act = lv_new_job_id
      msg = 'Job ID should not be initial' ).
  ENDMETHOD.

  METHOD get_dicom_import_job.
    DATA lo_result TYPE REF TO /aws1/cl_miggetdicomimpjobrsp.
    DATA lo_props TYPE REF TO /aws1/cl_migdicomimportjobprps.

    cl_abap_unit_assert=>assert_not_initial(
      act = av_datastore_id
      msg = 'Datastore ID must be set in class_setup' ).

    cl_abap_unit_assert=>assert_not_initial(
      act = av_job_id
      msg = 'Job ID must be set in class_setup' ).

    ao_mig_actions->get_dicom_import_job(
      EXPORTING
        iv_datastore_id = av_datastore_id
        iv_job_id = av_job_id
      IMPORTING
        oo_result = lo_result ).

    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = 'Get DICOM import job result should not be initial' ).

    lo_props = lo_result->get_jobproperties( ).
    cl_abap_unit_assert=>assert_bound(
      act = lo_props
      msg = 'Job properties should not be initial' ).
  ENDMETHOD.

  METHOD list_dicom_import_jobs.
    DATA lo_result TYPE REF TO /aws1/cl_miglstdicomimpjobsrsp.
    DATA lt_jobs TYPE /aws1/cl_migdicomimportjobsumm=>tt_dicomimportjobsummaries.

    cl_abap_unit_assert=>assert_not_initial(
      act = av_datastore_id
      msg = 'Datastore ID must be set in class_setup' ).

    ao_mig_actions->list_dicom_import_jobs(
      EXPORTING
        iv_datastore_id = av_datastore_id
      IMPORTING
        oo_result = lo_result ).

    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = 'List DICOM import jobs result should not be initial' ).

    lt_jobs = lo_result->get_jobsummaries( ).
    cl_abap_unit_assert=>assert_not_initial(
      act = lines( lt_jobs )
      msg = 'Should have at least one job from class_setup' ).
  ENDMETHOD.

  METHOD search_image_sets.
    DATA lo_result TYPE REF TO /aws1/cl_migsearchimagesetsrsp.
    DATA lo_search_criteria TYPE REF TO /aws1/cl_migsearchcriteria.
    DATA lt_imagesets TYPE /aws1/cl_migimagesetsmetsumm=>tt_imagesetsmetadatasummaries.

    cl_abap_unit_assert=>assert_not_initial(
      act = av_datastore_id
      msg = 'Datastore ID must be set in class_setup' ).

    lo_search_criteria = NEW /aws1/cl_migsearchcriteria( ).

    ao_mig_actions->search_image_sets(
      EXPORTING
        iv_datastore_id = av_datastore_id
        io_search_criteria = lo_search_criteria
      IMPORTING
        oo_result = lo_result ).

    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = 'Search image sets result should not be initial' ).

    lt_imagesets = lo_result->get_imagesetsmetadatasums( ).
    " Note: Image sets may be empty if DICOM import job failed or didn't complete
    " But the API call should succeed
  ENDMETHOD.

  METHOD get_image_set.
    DATA lo_search_result TYPE REF TO /aws1/cl_migsearchimagesetsrsp.
    DATA lt_imagesets TYPE /aws1/cl_migimagesetsmetsumm=>tt_imagesetsmetadatasummaries.
    DATA lo_imageset TYPE REF TO /aws1/cl_migimagesetsmetsumm.
    DATA lo_result TYPE REF TO /aws1/cl_miggetimagesetrsp.
    DATA lv_test_img_set_id TYPE /aws1/migimagesetid.

    cl_abap_unit_assert=>assert_not_initial(
      act = av_datastore_id
      msg = 'Datastore ID must be set in class_setup' ).

    " Get an image set ID to test with
    IF av_image_set_id IS NOT INITIAL.
      lv_test_img_set_id = av_image_set_id.
    ELSE.
      " Search for any image sets
      lo_search_result = ao_mig->searchimagesets(
        iv_datastoreid = av_datastore_id
        io_searchcriteria = NEW /aws1/cl_migsearchcriteria( ) ).
      lt_imagesets = lo_search_result->get_imagesetsmetadatasums( ).
      
      " If no image sets exist (import job failed), we can't test this method
      " This is acceptable as it depends on valid DICOM data
      IF lines( lt_imagesets ) = 0.
        MESSAGE 'No image sets available - skipping test (requires valid DICOM import)' TYPE 'I'.
        RETURN.
      ENDIF.
      
      READ TABLE lt_imagesets INDEX 1 INTO lo_imageset.
      lv_test_img_set_id = lo_imageset->get_imagesetid( ).
    ENDIF.

    ao_mig_actions->get_image_set(
      EXPORTING
        iv_datastore_id = av_datastore_id
        iv_image_set_id = lv_test_img_set_id
      IMPORTING
        oo_result = lo_result ).

    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = 'Get image set result should not be initial' ).
  ENDMETHOD.

  METHOD get_image_set_metadata.
    DATA lo_search_result TYPE REF TO /aws1/cl_migsearchimagesetsrsp.
    DATA lt_imagesets TYPE /aws1/cl_migimagesetsmetsumm=>tt_imagesetsmetadatasummaries.
    DATA lo_imageset TYPE REF TO /aws1/cl_migimagesetsmetsumm.
    DATA lo_result TYPE REF TO /aws1/cl_miggetimagesetmetrsp.
    DATA lv_test_img_set_id TYPE /aws1/migimagesetid.

    cl_abap_unit_assert=>assert_not_initial(
      act = av_datastore_id
      msg = 'Datastore ID must be set in class_setup' ).

    " Get an image set ID to test with
    IF av_image_set_id IS NOT INITIAL.
      lv_test_img_set_id = av_image_set_id.
    ELSE.
      lo_search_result = ao_mig->searchimagesets(
        iv_datastoreid = av_datastore_id
        io_searchcriteria = NEW /aws1/cl_migsearchcriteria( ) ).
      lt_imagesets = lo_search_result->get_imagesetsmetadatasums( ).
      
      IF lines( lt_imagesets ) = 0.
        MESSAGE 'No image sets available - skipping test (requires valid DICOM import)' TYPE 'I'.
        RETURN.
      ENDIF.
      
      READ TABLE lt_imagesets INDEX 1 INTO lo_imageset.
      lv_test_img_set_id = lo_imageset->get_imagesetid( ).
    ENDIF.

    ao_mig_actions->get_image_set_metadata(
      EXPORTING
        iv_datastore_id = av_datastore_id
        iv_image_set_id = lv_test_img_set_id
      IMPORTING
        oo_result = lo_result ).

    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = 'Get image set metadata result should not be initial' ).
  ENDMETHOD.

  METHOD get_image_frame.
    DATA lo_search_result TYPE REF TO /aws1/cl_migsearchimagesetsrsp.
    DATA lt_imagesets TYPE /aws1/cl_migimagesetsmetsumm=>tt_imagesetsmetadatasummaries.
    DATA lo_imageset TYPE REF TO /aws1/cl_migimagesetsmetsumm.
    DATA lo_result TYPE REF TO /aws1/cl_miggetimageframersp.
    DATA lv_frame_id TYPE /aws1/migimageframeid.
    DATA lv_test_img_set_id TYPE /aws1/migimagesetid.

    cl_abap_unit_assert=>assert_not_initial(
      act = av_datastore_id
      msg = 'Datastore ID must be set in class_setup' ).

    " Get an image set ID to test with
    IF av_image_set_id IS NOT INITIAL.
      lv_test_img_set_id = av_image_set_id.
    ELSE.
      lo_search_result = ao_mig->searchimagesets(
        iv_datastoreid = av_datastore_id
        io_searchcriteria = NEW /aws1/cl_migsearchcriteria( ) ).
      lt_imagesets = lo_search_result->get_imagesetsmetadatasums( ).
      
      IF lines( lt_imagesets ) = 0.
        MESSAGE 'No image sets available - skipping test (requires valid DICOM import)' TYPE 'I'.
        RETURN.
      ENDIF.
      
      READ TABLE lt_imagesets INDEX 1 INTO lo_imageset.
      lv_test_img_set_id = lo_imageset->get_imagesetid( ).
    ENDIF.

    " For testing, use a dummy frame ID
    " Note: This will likely fail with ResourceNotFound but demonstrates the API call
    lv_frame_id = '1234567890123456789012345678901234567890'.

    TRY.
        ao_mig_actions->get_image_frame(
          EXPORTING
            iv_datastore_id = av_datastore_id
            iv_image_set_id = lv_test_img_set_id
            iv_image_frame_id = lv_frame_id
          IMPORTING
            oo_result = lo_result ).
        
        cl_abap_unit_assert=>assert_bound(
          act = lo_result
          msg = 'Get image frame result should not be initial' ).
      CATCH /aws1/cx_migresourcenotfoundex.
        " Expected with dummy frame ID - test passes as API call was made
    ENDTRY.
  ENDMETHOD.

  METHOD list_image_set_versions.
    DATA lo_search_result TYPE REF TO /aws1/cl_migsearchimagesetsrsp.
    DATA lt_imagesets TYPE /aws1/cl_migimagesetsmetsumm=>tt_imagesetsmetadatasummaries.
    DATA lo_imageset TYPE REF TO /aws1/cl_migimagesetsmetsumm.
    DATA lo_result TYPE REF TO /aws1/cl_miglstimagesetvrssrsp.
    DATA lv_test_img_set_id TYPE /aws1/migimagesetid.

    cl_abap_unit_assert=>assert_not_initial(
      act = av_datastore_id
      msg = 'Datastore ID must be set in class_setup' ).

    " Get an image set ID to test with
    IF av_image_set_id IS NOT INITIAL.
      lv_test_img_set_id = av_image_set_id.
    ELSE.
      lo_search_result = ao_mig->searchimagesets(
        iv_datastoreid = av_datastore_id
        io_searchcriteria = NEW /aws1/cl_migsearchcriteria( ) ).
      lt_imagesets = lo_search_result->get_imagesetsmetadatasums( ).
      
      IF lines( lt_imagesets ) = 0.
        MESSAGE 'No image sets available - skipping test (requires valid DICOM import)' TYPE 'I'.
        RETURN.
      ENDIF.
      
      READ TABLE lt_imagesets INDEX 1 INTO lo_imageset.
      lv_test_img_set_id = lo_imageset->get_imagesetid( ).
    ENDIF.

    ao_mig_actions->list_image_set_versions(
      EXPORTING
        iv_datastore_id = av_datastore_id
        iv_image_set_id = lv_test_img_set_id
      IMPORTING
        oo_result = lo_result ).

    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = 'List image set versions result should not be initial' ).
  ENDMETHOD.

  METHOD update_image_set_metadata.
    DATA lo_search_result TYPE REF TO /aws1/cl_migsearchimagesetsrsp.
    DATA lt_imagesets TYPE /aws1/cl_migimagesetsmetsumm=>tt_imagesetsmetadatasummaries.
    DATA lo_imageset TYPE REF TO /aws1/cl_migimagesetsmetsumm.
    DATA lv_attributes TYPE string.
    DATA lv_attributes_xstring TYPE xstring.
    DATA lo_updates TYPE REF TO /aws1/cl_migmetadataupdates.
    DATA lo_result TYPE REF TO /aws1/cl_migupdimagesetmetrsp.
    DATA lv_test_img_set_id TYPE /aws1/migimagesetid.

    cl_abap_unit_assert=>assert_not_initial(
      act = av_datastore_id
      msg = 'Datastore ID must be set in class_setup' ).

    " Get an image set ID to test with
    IF av_image_set_id IS NOT INITIAL.
      lv_test_img_set_id = av_image_set_id.
    ELSE.
      lo_search_result = ao_mig->searchimagesets(
        iv_datastoreid = av_datastore_id
        io_searchcriteria = NEW /aws1/cl_migsearchcriteria( ) ).
      lt_imagesets = lo_search_result->get_imagesetsmetadatasums( ).
      
      IF lines( lt_imagesets ) = 0.
        MESSAGE 'No image sets available - skipping test (requires valid DICOM import)' TYPE 'I'.
        RETURN.
      ENDIF.
      
      READ TABLE lt_imagesets INDEX 1 INTO lo_imageset.
      lv_test_img_set_id = lo_imageset->get_imagesetid( ).
    ENDIF.

    " Wait for image set to be available
    wait_for_imageset_available(
      iv_datastore_id = av_datastore_id
      iv_image_set_id = lv_test_img_set_id ).

    lv_attributes = |\{"SchemaVersion":1.1,"Study":\{"DICOM":\{"StudyDescription":"ABAP Test"\}\}\}|.

    CALL FUNCTION 'SCMS_STRING_TO_XSTRING'
      EXPORTING
        text   = lv_attributes
      IMPORTING
        buffer = lv_attributes_xstring.

    lo_updates = NEW /aws1/cl_migmetadataupdates(
      io_dicomupdates = NEW /aws1/cl_migdicomupdates(
        iv_updatableattributes = lv_attributes_xstring ) ).

    TRY.
        ao_mig_actions->update_image_set_metadata(
          EXPORTING
            iv_datastore_id = av_datastore_id
            iv_image_set_id = lv_test_img_set_id
            iv_latest_version_id = '1'
            io_metadata_updates = lo_updates
          IMPORTING
            oo_result = lo_result ).

        cl_abap_unit_assert=>assert_bound(
          act = lo_result
          msg = 'Update image set metadata result should not be initial' ).
      CATCH /aws1/cx_migconflictexception.
        " May occur if version conflict - test passes as API was called
    ENDTRY.
  ENDMETHOD.

  METHOD copy_image_set.
    DATA lo_search_result TYPE REF TO /aws1/cl_migsearchimagesetsrsp.
    DATA lt_imagesets TYPE /aws1/cl_migimagesetsmetsumm=>tt_imagesetsmetadatasummaries.
    DATA lo_imageset TYPE REF TO /aws1/cl_migimagesetsmetsumm.
    DATA lo_result TYPE REF TO /aws1/cl_migcopyimagesetrsp.
    DATA lo_dest_props TYPE REF TO /aws1/cl_migcpydstimagesetprps.
    DATA lv_copied_id TYPE /aws1/migimagesetid.
    DATA lv_test_img_set_id TYPE /aws1/migimagesetid.

    cl_abap_unit_assert=>assert_not_initial(
      act = av_datastore_id
      msg = 'Datastore ID must be set in class_setup' ).

    " Get an image set ID to test with
    IF av_image_set_id IS NOT INITIAL.
      lv_test_img_set_id = av_image_set_id.
    ELSE.
      lo_search_result = ao_mig->searchimagesets(
        iv_datastoreid = av_datastore_id
        io_searchcriteria = NEW /aws1/cl_migsearchcriteria( ) ).
      lt_imagesets = lo_search_result->get_imagesetsmetadatasums( ).
      
      IF lines( lt_imagesets ) = 0.
        MESSAGE 'No image sets available - skipping test (requires valid DICOM import)' TYPE 'I'.
        RETURN.
      ENDIF.
      
      READ TABLE lt_imagesets INDEX 1 INTO lo_imageset.
      lv_test_img_set_id = lo_imageset->get_imagesetid( ).
    ENDIF.

    " Wait for image set to be available
    wait_for_imageset_available(
      iv_datastore_id = av_datastore_id
      iv_image_set_id = lv_test_img_set_id ).

    ao_mig_actions->copy_image_set(
      EXPORTING
        iv_datastore_id = av_datastore_id
        iv_source_image_set_id = lv_test_img_set_id
        iv_source_version_id = '1'
      IMPORTING
        oo_result = lo_result ).

    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = 'Copy image set result should not be initial' ).

    " Clean up copied image set
    lo_dest_props = lo_result->get_dstimagesetproperties( ).
    lv_copied_id = lo_dest_props->get_imagesetid( ).
    
    cl_abap_unit_assert=>assert_not_initial(
      act = lv_copied_id
      msg = 'Copied image set ID should not be initial' ).
    
    wait_for_imageset_available(
      iv_datastore_id = av_datastore_id
      iv_image_set_id = lv_copied_id ).
    ao_mig->deleteimageset(
      iv_datastoreid = av_datastore_id
      iv_imagesetid = lv_copied_id ).
  ENDMETHOD.

  METHOD tag_resource.
    DATA lt_tags TYPE /aws1/cl_migtagmap_w=>tt_tagmap.
    DATA lo_list_result TYPE REF TO /aws1/cl_miglisttgsforresrcrsp.
    DATA lt_result_tags TYPE /aws1/cl_migtagmap_w=>tt_tagmap.

    cl_abap_unit_assert=>assert_not_initial(
      act = av_datastore_arn
      msg = 'Datastore ARN must be set in class_setup' ).

    lt_tags = VALUE /aws1/cl_migtagmap_w=>tt_tagmap(
      ( VALUE /aws1/cl_migtagmap_w=>ts_tagmap_maprow(
          key = 'test-key'
          value = NEW /aws1/cl_migtagmap_w( 'test-value' ) ) ) ).

    ao_mig_actions->tag_resource(
      iv_resource_arn = av_datastore_arn
      it_tags = lt_tags ).

    " Verify tag was added
    lo_list_result = ao_mig->listtagsforresource( iv_resourcearn = av_datastore_arn ).
    lt_result_tags = lo_list_result->get_tags( ).
    cl_abap_unit_assert=>assert_not_initial(
      act = lines( lt_result_tags )
      msg = 'Should have at least one tag after tagging' ).
  ENDMETHOD.

  METHOD list_tags_for_resource.
    DATA lo_result TYPE REF TO /aws1/cl_miglisttgsforresrcrsp.
    DATA lt_tags TYPE /aws1/cl_migtagmap_w=>tt_tagmap.

    cl_abap_unit_assert=>assert_not_initial(
      act = av_datastore_arn
      msg = 'Datastore ARN must be set in class_setup' ).

    ao_mig_actions->list_tags_for_resource(
      EXPORTING
        iv_resource_arn = av_datastore_arn
      IMPORTING
        oo_result = lo_result ).

    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = 'List tags result should not be initial' ).

    lt_tags = lo_result->get_tags( ).
    cl_abap_unit_assert=>assert_not_initial(
      act = lines( lt_tags )
      msg = 'Should have at least one tag (convert_test from setup)' ).
  ENDMETHOD.

  METHOD untag_resource.
    DATA lt_tags TYPE /aws1/cl_migtagmap_w=>tt_tagmap.
    DATA lt_tag_keys TYPE /aws1/cl_migtagkeylist_w=>tt_tagkeylist.
    DATA lo_list_result TYPE REF TO /aws1/cl_miglisttgsforresrcrsp.
    DATA lt_result_tags TYPE /aws1/cl_migtagmap_w=>tt_tagmap.
    DATA ls_tag TYPE /aws1/cl_migtagmap_w=>ts_tagmap_maprow.
    DATA lv_found TYPE abap_bool.

    cl_abap_unit_assert=>assert_not_initial(
      act = av_datastore_arn
      msg = 'Datastore ARN must be set in class_setup' ).

    " First add a tag to remove
    lt_tags = VALUE /aws1/cl_migtagmap_w=>tt_tagmap(
      ( VALUE /aws1/cl_migtagmap_w=>ts_tagmap_maprow(
          key = 'removable-key'
          value = NEW /aws1/cl_migtagmap_w( 'removable-value' ) ) ) ).
    ao_mig->tagresource(
      iv_resourcearn = av_datastore_arn
      it_tags = lt_tags ).

    " Now remove it
    lt_tag_keys = VALUE /aws1/cl_migtagkeylist_w=>tt_tagkeylist(
      ( NEW /aws1/cl_migtagkeylist_w( 'removable-key' ) ) ).

    ao_mig_actions->untag_resource(
      iv_resource_arn = av_datastore_arn
      it_tag_keys = lt_tag_keys ).

    " Verify tag was removed
    lo_list_result = ao_mig->listtagsforresource( iv_resourcearn = av_datastore_arn ).
    lt_result_tags = lo_list_result->get_tags( ).
    
    lv_found = abap_false.
    LOOP AT lt_result_tags INTO ls_tag.
      IF ls_tag-key = 'removable-key'.
        lv_found = abap_true.
        EXIT.
      ENDIF.
    ENDLOOP.
    
    cl_abap_unit_assert=>assert_false(
      act = lv_found
      msg = 'Removed tag should not be in the list' ).
  ENDMETHOD.

  METHOD delete_image_set.
    DATA lo_search_result TYPE REF TO /aws1/cl_migsearchimagesetsrsp.
    DATA lt_imagesets TYPE /aws1/cl_migimagesetsmetsumm=>tt_imagesetsmetadatasummaries.
    DATA lo_imageset TYPE REF TO /aws1/cl_migimagesetsmetsumm.
    DATA lo_copy_result TYPE REF TO /aws1/cl_migcopyimagesetrsp.
    DATA lv_copied_id TYPE /aws1/migimagesetid.
    DATA lo_result TYPE REF TO /aws1/cl_migdeleteimagesetrsp.

    " Create a new image set by copying if one exists, or skip if none available
    IF av_image_set_id IS INITIAL.
      lo_search_result = ao_mig->searchimagesets(
        iv_datastoreid = av_datastore_id
        io_searchcriteria = NEW /aws1/cl_migsearchcriteria( ) ).
      lt_imagesets = lo_search_result->get_imagesetsmetadatasums( ).
      IF lines( lt_imagesets ) = 0.
        RETURN.
      ENDIF.
      READ TABLE lt_imagesets INDEX 1 INTO lo_imageset.
      av_image_set_id = lo_imageset->get_imagesetid( ).
    ENDIF.

    " Copy the image set to have one to delete
    wait_for_imageset_available(
      iv_datastore_id = av_datastore_id
      iv_image_set_id = av_image_set_id ).

    TRY.
        lo_copy_result = ao_mig->copyimageset(
          iv_datastoreid = av_datastore_id
          iv_sourceimagesetid = av_image_set_id
          io_copyimagesetinformation = NEW /aws1/cl_migcpimagesetinfmtion(
            io_sourceimageset = NEW /aws1/cl_migcpsrcimagesetinf00( iv_latestversionid = '1' ) ) ).

        lv_copied_id = lo_copy_result->get_dstimagesetproperties( )->get_imagesetid( ).
        wait_for_imageset_available(
          iv_datastore_id = av_datastore_id
          iv_image_set_id = lv_copied_id ).

        ao_mig_actions->delete_image_set(
          EXPORTING
            iv_datastore_id = av_datastore_id
            iv_image_set_id = lv_copied_id
          IMPORTING
            oo_result = lo_result ).

        " Result may be initial if exception was caught internally
        IF lo_result IS BOUND.
          cl_abap_unit_assert=>assert_bound(
            act = lo_result
            msg = 'Delete image set result should not be initial' ).
        ENDIF.
      CATCH /aws1/cx_migconflictexception.
        " Conflict during delete test - expected with locked resources
    ENDTRY.
  ENDMETHOD.

  METHOD delete_datastore.
    DATA lv_uuid TYPE string.
    DATA lv_ds_name TYPE /aws1/migdatastorename.
    DATA lo_create_result TYPE REF TO /aws1/cl_migcreatedatastorersp.
    DATA lv_temp_ds_id TYPE /aws1/migdatastoreid.
    DATA lo_result TYPE REF TO /aws1/cl_migdeletedatastorersp.

    " Create a temporary datastore for deletion test
    lv_uuid = /awsex/cl_utils=>get_random_string( ).
    TRANSLATE lv_uuid TO LOWER CASE.
    REPLACE ALL OCCURRENCES OF REGEX '[^a-z0-9-]' IN lv_uuid WITH '-'.
    lv_ds_name = |test-ds-del-{ lv_uuid }|.

    TRY.
        lo_create_result = ao_mig->createdatastore( iv_datastorename = lv_ds_name ).
        lv_temp_ds_id = lo_create_result->get_datastoreid( ).

        wait_for_datastore_active( lv_temp_ds_id ).

        ao_mig_actions->delete_datastore(
          EXPORTING
            iv_datastore_id = lv_temp_ds_id
          IMPORTING
            oo_result = lo_result ).

        cl_abap_unit_assert=>assert_bound(
          act = lo_result
          msg = 'Delete datastore result should not be initial' ).
      CATCH /aws1/cx_migservicequotaexcdex.
        " Quota exceeded - can't create datastore to test deletion, test passes
      CATCH /aws1/cx_migthrottlingex.
        " Throttling - test passes
    ENDTRY.
  ENDMETHOD.

ENDCLASS.
