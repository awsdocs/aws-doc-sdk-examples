" Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
" SPDX-License-Identifier: Apache-2.0
CLASS /awsex/cl_hll_actions DEFINITION
  PUBLIC
  FINAL
  CREATE PUBLIC .

  PUBLIC SECTION.
  PROTECTED SECTION.
  PRIVATE SECTION.

    METHODS create_fhir_datastore
      IMPORTING
        VALUE(iv_datastore_name) TYPE /aws1/hlldatastorename
      RETURNING
        VALUE(oo_result)         TYPE REF TO /aws1/cl_hllcrefhirdatastore01
      RAISING
        /aws1/cx_rt_generic .
    METHODS describe_fhir_datastore
      IMPORTING
        VALUE(iv_datastore_id) TYPE /aws1/hlldatastoreid
      RETURNING
        VALUE(oo_result)       TYPE REF TO /aws1/cl_hlldscfhirdatastore01
      RAISING
        /aws1/cx_rt_generic .
    METHODS list_fhir_datastores
      RETURNING
        VALUE(oo_result) TYPE REF TO /aws1/cl_hlllstfhirdatastore01
      RAISING
        /aws1/cx_rt_generic .
    METHODS delete_fhir_datastore
      IMPORTING
        VALUE(iv_datastore_id) TYPE /aws1/hlldatastoreid
      RETURNING
        VALUE(oo_result)       TYPE REF TO /aws1/cl_hlldelfhirdatastore01
      RAISING
        /aws1/cx_rt_generic .
    METHODS start_fhir_import_job
      IMPORTING
        VALUE(iv_datastore_id)   TYPE /aws1/hlldatastoreid
        VALUE(iv_input_s3_uri)   TYPE /aws1/hlls3uri
        VALUE(iv_job_output_uri) TYPE /aws1/hlls3uri
        VALUE(iv_dataaccess_arn) TYPE /aws1/iamrolearn
        VALUE(iv_kms_key_id)     TYPE /aws1/hllencryptionkeyid
      RETURNING
        VALUE(oo_result)         TYPE REF TO /aws1/cl_hllstfhirimportjob01
      RAISING
        /aws1/cx_rt_generic .
    METHODS describe_fhir_import_job
      IMPORTING
        VALUE(iv_datastore_id) TYPE /aws1/hlldatastoreid
        VALUE(iv_job_id)       TYPE /aws1/hlljobid
      RETURNING
        VALUE(oo_result)       TYPE REF TO /aws1/cl_hlldscfhirimportjb01
      RAISING
        /aws1/cx_rt_generic .
    METHODS list_fhir_import_jobs
      IMPORTING
        VALUE(iv_datastore_id) TYPE /aws1/hlldatastoreid
      RETURNING
        VALUE(oo_result)       TYPE REF TO /aws1/cl_hlllstfhirimportjb01
      RAISING
        /aws1/cx_rt_generic .
    METHODS start_fhir_export_job
      IMPORTING
        VALUE(iv_datastore_id)   TYPE /aws1/hlldatastoreid
        VALUE(iv_output_s3_uri)  TYPE /aws1/hlls3uri
        VALUE(iv_dataaccess_arn) TYPE /aws1/iamrolearn
        VALUE(iv_kms_key_id)     TYPE /aws1/hllencryptionkeyid
      RETURNING
        VALUE(oo_result)         TYPE REF TO /aws1/cl_hllstfhirexportjob01
      RAISING
        /aws1/cx_rt_generic .
    METHODS describe_fhir_export_job
      IMPORTING
        VALUE(iv_datastore_id) TYPE /aws1/hlldatastoreid
        VALUE(iv_job_id)       TYPE /aws1/hlljobid
      RETURNING
        VALUE(oo_result)       TYPE REF TO /aws1/cl_hlldscfhirexportjb01
      RAISING
        /aws1/cx_rt_generic .
    METHODS list_fhir_export_jobs
      IMPORTING
        VALUE(iv_datastore_id) TYPE /aws1/hlldatastoreid
      RETURNING
        VALUE(oo_result)       TYPE REF TO /aws1/cl_hlllstfhirexportjb01
      RAISING
        /aws1/cx_rt_generic .
    METHODS tag_resource
      IMPORTING
        VALUE(iv_resource_arn) TYPE /aws1/hllamazonresourcename
        VALUE(it_tags)         TYPE /aws1/cl_hlltag=>tt_taglist
      RAISING
        /aws1/cx_rt_generic .
    METHODS list_tags_for_resource
      IMPORTING
        VALUE(iv_resource_arn) TYPE /aws1/hllamazonresourcename
      RETURNING
        VALUE(oo_result)       TYPE REF TO /aws1/cl_hlllsttagsforresour01
      RAISING
        /aws1/cx_rt_generic .
    METHODS untag_resource
      IMPORTING
        VALUE(iv_resource_arn) TYPE /aws1/hllamazonresourcename
        VALUE(it_tag_keys)     TYPE /aws1/cl_hlltagkeylist_w=>tt_tagkeylist
      RAISING
        /aws1/cx_rt_generic .
ENDCLASS.



CLASS /awsex/cl_hll_actions IMPLEMENTATION.


  METHOD create_fhir_datastore.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_hll) = /aws1/cl_hll_factory=>create( lo_session ).

    " snippet-start:[hll.abapv1.create_fhir_datastore]
    TRY.
        " iv_datastore_name = 'MyHealthLakeDataStore'.
        oo_result = lo_hll->createfhirdatastore(
          iv_datastorename = iv_datastore_name
          iv_datastoretypeversion = 'R4'
        ).
        DATA(lv_datastore_id) = oo_result->get_datastoreid( ).
        MESSAGE 'HealthLake data store created with ID: ' && lv_datastore_id TYPE 'I'.
      CATCH /aws1/cx_hllvalidationex INTO DATA(lo_validation_ex).
        DATA(lv_error) = |Validation error: { lo_validation_ex->av_err_code }-{ lo_validation_ex->av_err_msg }|.
        MESSAGE lv_error TYPE 'E'.
      CATCH /aws1/cx_hllinternalserverex INTO DATA(lo_server_ex).
        lv_error = |Internal server error: { lo_server_ex->av_err_code }-{ lo_server_ex->av_err_msg }|.
        MESSAGE lv_error TYPE 'E'.
    ENDTRY.
    " snippet-end:[hll.abapv1.create_fhir_datastore]
  ENDMETHOD.


  METHOD describe_fhir_datastore.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_hll) = /aws1/cl_hll_factory=>create( lo_session ).

    " snippet-start:[hll.abapv1.describe_fhir_datastore]
    TRY.
        " iv_datastore_id = 'a1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6'.
        oo_result = lo_hll->describefhirdatastore(
          iv_datastoreid = iv_datastore_id
        ).
        DATA(lo_properties) = oo_result->get_datastoreproperties( ).
        DATA(lv_name) = lo_properties->get_datastorename( ).
        DATA(lv_status) = lo_properties->get_datastorestatus( ).
        DATA(lv_arn) = lo_properties->get_datastorearn( ).
        MESSAGE 'Data store name: ' && lv_name && ', Status: ' && lv_status && ', ARN: ' && lv_arn TYPE 'I'.
      CATCH /aws1/cx_hllvalidationex INTO DATA(lo_validation_ex).
        DATA(lv_error) = |Validation error: { lo_validation_ex->av_err_code }-{ lo_validation_ex->av_err_msg }|.
        MESSAGE lv_error TYPE 'E'.
      CATCH /aws1/cx_hllresourcenotfoundex INTO DATA(lo_notfound_ex).
        lv_error = |Resource not found: { lo_notfound_ex->av_err_code }-{ lo_notfound_ex->av_err_msg }|.
        MESSAGE lv_error TYPE 'E'.
    ENDTRY.
    " snippet-end:[hll.abapv1.describe_fhir_datastore]
  ENDMETHOD.


  METHOD list_fhir_datastores.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_hll) = /aws1/cl_hll_factory=>create( lo_session ).

    " snippet-start:[hll.abapv1.list_fhir_datastores]
    TRY.
        oo_result = lo_hll->listfhirdatastores( ).
        DATA(lt_datastores) = oo_result->get_datastorepropertieslist( ).
        DATA(lv_count) = lines( lt_datastores ).
        MESSAGE 'Found ' && lv_count && ' HealthLake data stores' TYPE 'I'.
      CATCH /aws1/cx_hllvalidationex INTO DATA(lo_validation_ex).
        DATA(lv_error) = |Validation error: { lo_validation_ex->av_err_code }-{ lo_validation_ex->av_err_msg }|.
        MESSAGE lv_error TYPE 'E'.
      CATCH /aws1/cx_hllinternalserverex INTO DATA(lo_server_ex).
        lv_error = |Internal server error: { lo_server_ex->av_err_code }-{ lo_server_ex->av_err_msg }|.
        MESSAGE lv_error TYPE 'E'.
    ENDTRY.
    " snippet-end:[hll.abapv1.list_fhir_datastores]
  ENDMETHOD.


  METHOD delete_fhir_datastore.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_hll) = /aws1/cl_hll_factory=>create( lo_session ).

    " snippet-start:[hll.abapv1.delete_fhir_datastore]
    TRY.
        " iv_datastore_id = 'a1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6'.
        oo_result = lo_hll->deletefhirdatastore(
          iv_datastoreid = iv_datastore_id
        ).
        DATA(lv_status) = oo_result->get_datastorestatus( ).
        MESSAGE 'Data store deletion initiated. Status: ' && lv_status TYPE 'I'.
      CATCH /aws1/cx_hllvalidationex INTO DATA(lo_validation_ex).
        DATA(lv_error) = |Validation error: { lo_validation_ex->av_err_code }-{ lo_validation_ex->av_err_msg }|.
        MESSAGE lv_error TYPE 'E'.
      CATCH /aws1/cx_hllresourcenotfoundex INTO DATA(lo_notfound_ex).
        lv_error = |Resource not found: { lo_notfound_ex->av_err_code }-{ lo_notfound_ex->av_err_msg }|.
        MESSAGE lv_error TYPE 'E'.
    ENDTRY.
    " snippet-end:[hll.abapv1.delete_fhir_datastore]
  ENDMETHOD.


  METHOD start_fhir_import_job.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_hll) = /aws1/cl_hll_factory=>create( lo_session ).

    " snippet-start:[hll.abapv1.start_fhir_import_job]
    TRY.
        DATA(lo_input_config) = NEW /aws1/cl_hllinputdataconfig(
          iv_s3uri = iv_input_s3_uri
        ).
        DATA(lo_job_output_config) = NEW /aws1/cl_hlls3configuration(
          iv_s3uri = iv_job_output_uri
          iv_kmskeyid = iv_kms_key_id
        ).
        DATA(lo_output_config) = NEW /aws1/cl_hlloutputdataconfig(
          io_s3configuration = lo_job_output_config
        ).
        " iv_datastore_id = 'a1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6'.
        " iv_input_s3_uri = 's3://my-bucket/input/'.
        " iv_job_output_uri = 's3://my-bucket/output/'.
        " iv_dataaccess_arn = 'arn:aws:iam::123456789012:role/HealthLakeRole'.
        oo_result = lo_hll->startfhirimportjob(
          iv_datastoreid = iv_datastore_id
          io_inputdataconfig = lo_input_config
          io_joboutputdataconfig = lo_output_config
          iv_dataaccessrolearn = iv_dataaccess_arn
        ).
        DATA(lv_job_id) = oo_result->get_jobid( ).
        MESSAGE 'FHIR import job started with ID: ' && lv_job_id TYPE 'I'.
      CATCH /aws1/cx_hllvalidationex INTO DATA(lo_validation_ex).
        DATA(lv_error) = |Validation error: { lo_validation_ex->av_err_code }-{ lo_validation_ex->av_err_msg }|.
        MESSAGE lv_error TYPE 'E'.
      CATCH /aws1/cx_hllresourcenotfoundex INTO DATA(lo_notfound_ex).
        lv_error = |Resource not found: { lo_notfound_ex->av_err_code }-{ lo_notfound_ex->av_err_msg }|.
        MESSAGE lv_error TYPE 'E'.
    ENDTRY.
    " snippet-end:[hll.abapv1.start_fhir_import_job]
  ENDMETHOD.


  METHOD describe_fhir_import_job.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_hll) = /aws1/cl_hll_factory=>create( lo_session ).

    " snippet-start:[hll.abapv1.describe_fhir_import_job]
    TRY.
        " iv_datastore_id = 'a1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6'.
        " iv_job_id = 'c0e6b5d791293b68764e47ef18164dab'.
        oo_result = lo_hll->describefhirimportjob(
          iv_datastoreid = iv_datastore_id
          iv_jobid = iv_job_id
        ).
        DATA(lo_properties) = oo_result->get_importjobproperties( ).
        DATA(lv_status) = lo_properties->get_jobstatus( ).
        DATA(lv_job_name) = lo_properties->get_jobname( ).
        MESSAGE 'Import job: ' && lv_job_name && ', Status: ' && lv_status TYPE 'I'.
      CATCH /aws1/cx_hllvalidationex INTO DATA(lo_validation_ex).
        DATA(lv_error) = |Validation error: { lo_validation_ex->av_err_code }-{ lo_validation_ex->av_err_msg }|.
        MESSAGE lv_error TYPE 'E'.
      CATCH /aws1/cx_hllresourcenotfoundex INTO DATA(lo_notfound_ex).
        lv_error = |Resource not found: { lo_notfound_ex->av_err_code }-{ lo_notfound_ex->av_err_msg }|.
        MESSAGE lv_error TYPE 'E'.
    ENDTRY.
    " snippet-end:[hll.abapv1.describe_fhir_import_job]
  ENDMETHOD.


  METHOD list_fhir_import_jobs.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_hll) = /aws1/cl_hll_factory=>create( lo_session ).

    " snippet-start:[hll.abapv1.list_fhir_import_jobs]
    TRY.
        " iv_datastore_id = 'a1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6'.
        oo_result = lo_hll->listfhirimportjobs(
          iv_datastoreid = iv_datastore_id
        ).
        DATA(lt_jobs) = oo_result->get_importjobpropertieslist( ).
        DATA(lv_count) = lines( lt_jobs ).
        MESSAGE 'Found ' && lv_count && ' import jobs' TYPE 'I'.
      CATCH /aws1/cx_hllvalidationex INTO DATA(lo_validation_ex).
        DATA(lv_error) = |Validation error: { lo_validation_ex->av_err_code }-{ lo_validation_ex->av_err_msg }|.
        MESSAGE lv_error TYPE 'E'.
      CATCH /aws1/cx_hllresourcenotfoundex INTO DATA(lo_notfound_ex).
        lv_error = |Resource not found: { lo_notfound_ex->av_err_code }-{ lo_notfound_ex->av_err_msg }|.
        MESSAGE lv_error TYPE 'E'.
    ENDTRY.
    " snippet-end:[hll.abapv1.list_fhir_import_jobs]
  ENDMETHOD.


  METHOD start_fhir_export_job.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_hll) = /aws1/cl_hll_factory=>create( lo_session ).

    " snippet-start:[hll.abapv1.start_fhir_export_job]
    TRY.
        DATA(lo_s3_config) = NEW /aws1/cl_hlls3configuration(
          iv_s3uri = iv_output_s3_uri
          iv_kmskeyid = iv_kms_key_id
        ).
        DATA(lo_output_config) = NEW /aws1/cl_hlloutputdataconfig(
          io_s3configuration = lo_s3_config
        ).
        " iv_datastore_id = 'a1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6'.
        " iv_output_s3_uri = 's3://my-bucket/export/'.
        " iv_dataaccess_arn = 'arn:aws:iam::123456789012:role/HealthLakeRole'.
        oo_result = lo_hll->startfhirexportjob(
          iv_datastoreid = iv_datastore_id
          io_outputdataconfig = lo_output_config
          iv_dataaccessrolearn = iv_dataaccess_arn
        ).
        DATA(lv_job_id) = oo_result->get_jobid( ).
        MESSAGE 'FHIR export job started with ID: ' && lv_job_id TYPE 'I'.
      CATCH /aws1/cx_hllvalidationex INTO DATA(lo_validation_ex).
        DATA(lv_error) = |Validation error: { lo_validation_ex->av_err_code }-{ lo_validation_ex->av_err_msg }|.
        MESSAGE lv_error TYPE 'E'.
      CATCH /aws1/cx_hllresourcenotfoundex INTO DATA(lo_notfound_ex).
        lv_error = |Resource not found: { lo_notfound_ex->av_err_code }-{ lo_notfound_ex->av_err_msg }|.
        MESSAGE lv_error TYPE 'E'.
    ENDTRY.
    " snippet-end:[hll.abapv1.start_fhir_export_job]
  ENDMETHOD.


  METHOD describe_fhir_export_job.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_hll) = /aws1/cl_hll_factory=>create( lo_session ).

    " snippet-start:[hll.abapv1.describe_fhir_export_job]
    TRY.
        " iv_datastore_id = 'a1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6'.
        " iv_job_id = 'c0e6b5d791293b68764e47ef18164dab'.
        oo_result = lo_hll->describefhirexportjob(
          iv_datastoreid = iv_datastore_id
          iv_jobid = iv_job_id
        ).
        DATA(lo_properties) = oo_result->get_exportjobproperties( ).
        DATA(lv_status) = lo_properties->get_jobstatus( ).
        DATA(lv_job_name) = lo_properties->get_jobname( ).
        MESSAGE 'Export job: ' && lv_job_name && ', Status: ' && lv_status TYPE 'I'.
      CATCH /aws1/cx_hllvalidationex INTO DATA(lo_validation_ex).
        DATA(lv_error) = |Validation error: { lo_validation_ex->av_err_code }-{ lo_validation_ex->av_err_msg }|.
        MESSAGE lv_error TYPE 'E'.
      CATCH /aws1/cx_hllresourcenotfoundex INTO DATA(lo_notfound_ex).
        lv_error = |Resource not found: { lo_notfound_ex->av_err_code }-{ lo_notfound_ex->av_err_msg }|.
        MESSAGE lv_error TYPE 'E'.
    ENDTRY.
    " snippet-end:[hll.abapv1.describe_fhir_export_job]
  ENDMETHOD.


  METHOD list_fhir_export_jobs.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_hll) = /aws1/cl_hll_factory=>create( lo_session ).

    " snippet-start:[hll.abapv1.list_fhir_export_jobs]
    TRY.
        " iv_datastore_id = 'a1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6'.
        oo_result = lo_hll->listfhirexportjobs(
          iv_datastoreid = iv_datastore_id
        ).
        DATA(lt_jobs) = oo_result->get_exportjobpropertieslist( ).
        DATA(lv_count) = lines( lt_jobs ).
        MESSAGE 'Found ' && lv_count && ' export jobs' TYPE 'I'.
      CATCH /aws1/cx_hllvalidationex INTO DATA(lo_validation_ex).
        DATA(lv_error) = |Validation error: { lo_validation_ex->av_err_code }-{ lo_validation_ex->av_err_msg }|.
        MESSAGE lv_error TYPE 'E'.
      CATCH /aws1/cx_hllresourcenotfoundex INTO DATA(lo_notfound_ex).
        lv_error = |Resource not found: { lo_notfound_ex->av_err_code }-{ lo_notfound_ex->av_err_msg }|.
        MESSAGE lv_error TYPE 'E'.
    ENDTRY.
    " snippet-end:[hll.abapv1.list_fhir_export_jobs]
  ENDMETHOD.


  METHOD tag_resource.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_hll) = /aws1/cl_hll_factory=>create( lo_session ).

    " snippet-start:[hll.abapv1.tag_resource]
    TRY.
        " iv_resource_arn = 'arn:aws:healthlake:us-east-1:123456789012:datastore/fhir/a1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6'.
        lo_hll->tagresource(
          iv_resourcearn = iv_resource_arn
          it_tags = it_tags
        ).
        MESSAGE 'Resource tagged successfully' TYPE 'I'.
      CATCH /aws1/cx_hllvalidationex INTO DATA(lo_validation_ex).
        DATA(lv_error) = |Validation error: { lo_validation_ex->av_err_code }-{ lo_validation_ex->av_err_msg }|.
        MESSAGE lv_error TYPE 'E'.
      CATCH /aws1/cx_hllresourcenotfoundex INTO DATA(lo_notfound_ex).
        lv_error = |Resource not found: { lo_notfound_ex->av_err_code }-{ lo_notfound_ex->av_err_msg }|.
        MESSAGE lv_error TYPE 'E'.
    ENDTRY.
    " snippet-end:[hll.abapv1.tag_resource]
  ENDMETHOD.


  METHOD list_tags_for_resource.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_hll) = /aws1/cl_hll_factory=>create( lo_session ).

    " snippet-start:[hll.abapv1.list_tags_for_resource]
    TRY.
        " iv_resource_arn = 'arn:aws:healthlake:us-east-1:123456789012:datastore/fhir/a1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6'.
        oo_result = lo_hll->listtagsforresource(
          iv_resourcearn = iv_resource_arn
        ).
        DATA(lt_tags) = oo_result->get_tags( ).
        DATA(lv_count) = lines( lt_tags ).
        MESSAGE 'Found ' && lv_count && ' tags on resource' TYPE 'I'.
      CATCH /aws1/cx_hllvalidationex INTO DATA(lo_validation_ex).
        DATA(lv_error) = |Validation error: { lo_validation_ex->av_err_code }-{ lo_validation_ex->av_err_msg }|.
        MESSAGE lv_error TYPE 'E'.
      CATCH /aws1/cx_hllresourcenotfoundex INTO DATA(lo_notfound_ex).
        lv_error = |Resource not found: { lo_notfound_ex->av_err_code }-{ lo_notfound_ex->av_err_msg }|.
        MESSAGE lv_error TYPE 'E'.
    ENDTRY.
    " snippet-end:[hll.abapv1.list_tags_for_resource]
  ENDMETHOD.


  METHOD untag_resource.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_hll) = /aws1/cl_hll_factory=>create( lo_session ).

    " snippet-start:[hll.abapv1.untag_resource]
    TRY.
        " iv_resource_arn = 'arn:aws:healthlake:us-east-1:123456789012:datastore/fhir/a1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6'.
        lo_hll->untagresource(
          iv_resourcearn = iv_resource_arn
          it_tagkeys = it_tag_keys
        ).
        MESSAGE 'Resource untagged successfully' TYPE 'I'.
      CATCH /aws1/cx_hllvalidationex INTO DATA(lo_validation_ex).
        DATA(lv_error) = |Validation error: { lo_validation_ex->av_err_code }-{ lo_validation_ex->av_err_msg }|.
        MESSAGE lv_error TYPE 'E'.
      CATCH /aws1/cx_hllresourcenotfoundex INTO DATA(lo_notfound_ex).
        lv_error = |Resource not found: { lo_notfound_ex->av_err_code }-{ lo_notfound_ex->av_err_msg }|.
        MESSAGE lv_error TYPE 'E'.
    ENDTRY.
    " snippet-end:[hll.abapv1.untag_resource]
  ENDMETHOD.
ENDCLASS.
