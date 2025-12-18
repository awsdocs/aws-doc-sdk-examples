" SPDX-License-Identifier: Apache-2.0
CLASS /awsex/cl_hll_actions DEFINITION
  PUBLIC
  FINAL
  CREATE PUBLIC .

  PUBLIC SECTION.
    "! <p class="shorttext synchronized" lang="en">Creates a new HealthLake data store</p>
    "! @parameter iv_datastore_name | <p class="shorttext synchronized" lang="en">The name of the data store</p>
    "! @parameter oo_result | <p class="shorttext synchronized" lang="en">The response object containing data store information</p>
    "! @raising /aws1/cx_rt_generic | <p class="shorttext synchronized" lang="en">Raising exception in case of an error</p>
    METHODS create_fhir_datastore
      IMPORTING
        !iv_datastore_name TYPE /aws1/hlldatastorename
      EXPORTING
        !oo_result         TYPE REF TO /aws1/cl_hllcrefhirdatastore01
      RAISING
        /aws1/cx_rt_generic .

    "! <p class="shorttext synchronized" lang="en">Describes a HealthLake data store</p>
    "! @parameter iv_datastore_id | <p class="shorttext synchronized" lang="en">The data store ID</p>
    "! @parameter oo_result | <p class="shorttext synchronized" lang="en">The response object containing data store properties</p>
    "! @raising /aws1/cx_rt_generic | <p class="shorttext synchronized" lang="en">Raising exception in case of an error</p>
    METHODS describe_fhir_datastore
      IMPORTING
        !iv_datastore_id TYPE /aws1/hlldatastoreid
      EXPORTING
        !oo_result       TYPE REF TO /aws1/cl_hlldscfhirdatastore01
      RAISING
        /aws1/cx_rt_generic .

    "! <p class="shorttext synchronized" lang="en">Lists all HealthLake data stores</p>
    "! @parameter oo_result | <p class="shorttext synchronized" lang="en">The response object containing list of data stores</p>
    "! @raising /aws1/cx_rt_generic | <p class="shorttext synchronized" lang="en">Raising exception in case of an error</p>
    METHODS list_fhir_datastores
      EXPORTING
        !oo_result TYPE REF TO /aws1/cl_hlllstfhirdatastore01
      RAISING
        /aws1/cx_rt_generic .

    "! <p class="shorttext synchronized" lang="en">Deletes a HealthLake data store</p>
    "! @parameter iv_datastore_id | <p class="shorttext synchronized" lang="en">The data store ID</p>
    "! @parameter oo_result | <p class="shorttext synchronized" lang="en">The response object</p>
    "! @raising /aws1/cx_rt_generic | <p class="shorttext synchronized" lang="en">Raising exception in case of an error</p>
    METHODS delete_fhir_datastore
      IMPORTING
        !iv_datastore_id TYPE /aws1/hlldatastoreid
      EXPORTING
        !oo_result       TYPE REF TO /aws1/cl_hlldelfhirdatastore01
      RAISING
        /aws1/cx_rt_generic .

    "! <p class="shorttext synchronized" lang="en">Starts a HealthLake import job</p>
    "! @parameter iv_job_name | <p class="shorttext synchronized" lang="en">The import job name</p>
    "! @parameter iv_datastore_id | <p class="shorttext synchronized" lang="en">The data store ID</p>
    "! @parameter iv_input_s3_uri | <p class="shorttext synchronized" lang="en">The input S3 URI</p>
    "! @parameter iv_job_output_s3_uri | <p class="shorttext synchronized" lang="en">The job output S3 URI</p>
    "! @parameter iv_kms_key_id | <p class="shorttext synchronized" lang="en">The KMS key ID</p>
    "! @parameter iv_data_access_role_arn | <p class="shorttext synchronized" lang="en">The data access role ARN</p>
    "! @parameter oo_result | <p class="shorttext synchronized" lang="en">The response object containing job information</p>
    "! @raising /aws1/cx_rt_generic | <p class="shorttext synchronized" lang="en">Raising exception in case of an error</p>
    METHODS start_fhir_import_job
      IMPORTING
        !iv_job_name              TYPE /aws1/hlljobname
        !iv_datastore_id          TYPE /aws1/hlldatastoreid
        !iv_input_s3_uri          TYPE /aws1/hlls3uri
        !iv_job_output_s3_uri     TYPE /aws1/hlls3uri
        !iv_kms_key_id            TYPE /aws1/hllencryptionkeyid
        !iv_data_access_role_arn  TYPE /aws1/hlliamrolearn
      EXPORTING
        !oo_result                TYPE REF TO /aws1/cl_hllstartfhirimpjobrsp
      RAISING
        /aws1/cx_rt_generic .

    "! <p class="shorttext synchronized" lang="en">Describes a HealthLake import job</p>
    "! @parameter iv_datastore_id | <p class="shorttext synchronized" lang="en">The data store ID</p>
    "! @parameter iv_job_id | <p class="shorttext synchronized" lang="en">The import job ID</p>
    "! @parameter oo_result | <p class="shorttext synchronized" lang="en">The response object containing job properties</p>
    "! @raising /aws1/cx_rt_generic | <p class="shorttext synchronized" lang="en">Raising exception in case of an error</p>
    METHODS describe_fhir_import_job
      IMPORTING
        !iv_datastore_id TYPE /aws1/hlldatastoreid
        !iv_job_id       TYPE /aws1/hlljobid
      EXPORTING
        !oo_result       TYPE REF TO /aws1/cl_hlldescrfhirimpjobrsp
      RAISING
        /aws1/cx_rt_generic .

    "! <p class="shorttext synchronized" lang="en">Lists HealthLake import jobs</p>
    "! @parameter iv_datastore_id | <p class="shorttext synchronized" lang="en">The data store ID</p>
    "! @parameter iv_submitted_after | <p class="shorttext synchronized" lang="en">Filter jobs submitted after this date</p>
    "! @parameter oo_result | <p class="shorttext synchronized" lang="en">The response object containing list of jobs</p>
    "! @raising /aws1/cx_rt_generic | <p class="shorttext synchronized" lang="en">Raising exception in case of an error</p>
    METHODS list_fhir_import_jobs
      IMPORTING
        !iv_datastore_id     TYPE /aws1/hlldatastoreid
        !iv_submitted_after  TYPE /aws1/hlltimestamp OPTIONAL
      EXPORTING
        !oo_result           TYPE REF TO /aws1/cl_hlllistfhirimpjobsrsp
      RAISING
        /aws1/cx_rt_generic .

    "! <p class="shorttext synchronized" lang="en">Starts a HealthLake export job</p>
    "! @parameter iv_job_name | <p class="shorttext synchronized" lang="en">The export job name</p>
    "! @parameter iv_datastore_id | <p class="shorttext synchronized" lang="en">The data store ID</p>
    "! @parameter iv_output_s3_uri | <p class="shorttext synchronized" lang="en">The output S3 URI</p>
    "! @parameter iv_kms_key_id | <p class="shorttext synchronized" lang="en">The KMS key ID</p>
    "! @parameter iv_data_access_role_arn | <p class="shorttext synchronized" lang="en">The data access role ARN</p>
    "! @parameter oo_result | <p class="shorttext synchronized" lang="en">The response object containing job information</p>
    "! @raising /aws1/cx_rt_generic | <p class="shorttext synchronized" lang="en">Raising exception in case of an error</p>
    METHODS start_fhir_export_job
      IMPORTING
        !iv_job_name              TYPE /aws1/hlljobname
        !iv_datastore_id          TYPE /aws1/hlldatastoreid
        !iv_output_s3_uri         TYPE /aws1/hlls3uri
        !iv_kms_key_id            TYPE /aws1/hllencryptionkeyid
        !iv_data_access_role_arn  TYPE /aws1/hlliamrolearn
      EXPORTING
        !oo_result                TYPE REF TO /aws1/cl_hllstartfhirexpjobrsp
      RAISING
        /aws1/cx_rt_generic .

    "! <p class="shorttext synchronized" lang="en">Describes a HealthLake export job</p>
    "! @parameter iv_datastore_id | <p class="shorttext synchronized" lang="en">The data store ID</p>
    "! @parameter iv_job_id | <p class="shorttext synchronized" lang="en">The export job ID</p>
    "! @parameter oo_result | <p class="shorttext synchronized" lang="en">The response object containing job properties</p>
    "! @raising /aws1/cx_rt_generic | <p class="shorttext synchronized" lang="en">Raising exception in case of an error</p>
    METHODS describe_fhir_export_job
      IMPORTING
        !iv_datastore_id TYPE /aws1/hlldatastoreid
        !iv_job_id       TYPE /aws1/hlljobid
      EXPORTING
        !oo_result       TYPE REF TO /aws1/cl_hlldescrfhirexpjobrsp
      RAISING
        /aws1/cx_rt_generic .

    "! <p class="shorttext synchronized" lang="en">Lists HealthLake export jobs</p>
    "! @parameter iv_datastore_id | <p class="shorttext synchronized" lang="en">The data store ID</p>
    "! @parameter iv_submitted_after | <p class="shorttext synchronized" lang="en">Filter jobs submitted after this date</p>
    "! @parameter oo_result | <p class="shorttext synchronized" lang="en">The response object containing list of jobs</p>
    "! @raising /aws1/cx_rt_generic | <p class="shorttext synchronized" lang="en">Raising exception in case of an error</p>
    METHODS list_fhir_export_jobs
      IMPORTING
        !iv_datastore_id     TYPE /aws1/hlldatastoreid
        !iv_submitted_after  TYPE /aws1/hlltimestamp OPTIONAL
      EXPORTING
        !oo_result           TYPE REF TO /aws1/cl_hlllistfhirexpjobsrsp
      RAISING
        /aws1/cx_rt_generic .

    "! <p class="shorttext synchronized" lang="en">Tags a HealthLake resource</p>
    "! @parameter iv_resource_arn | <p class="shorttext synchronized" lang="en">The resource ARN</p>
    "! @parameter it_tags | <p class="shorttext synchronized" lang="en">The tags to add</p>
    "! @raising /aws1/cx_rt_generic | <p class="shorttext synchronized" lang="en">Raising exception in case of an error</p>
    METHODS tag_resource
      IMPORTING
        !iv_resource_arn TYPE /aws1/hllamazonresourcename
        !it_tags         TYPE /aws1/cl_hlltag=>tt_taglist
      RAISING
        /aws1/cx_rt_generic .

    "! <p class="shorttext synchronized" lang="en">Lists tags for a HealthLake resource</p>
    "! @parameter iv_resource_arn | <p class="shorttext synchronized" lang="en">The resource ARN</p>
    "! @parameter ot_tags | <p class="shorttext synchronized" lang="en">The tags for the resource</p>
    "! @raising /aws1/cx_rt_generic | <p class="shorttext synchronized" lang="en">Raising exception in case of an error</p>
    METHODS list_tags_for_resource
      IMPORTING
        !iv_resource_arn TYPE /aws1/hllamazonresourcename
      EXPORTING
        !ot_tags         TYPE /aws1/cl_hlltag=>tt_taglist
      RAISING
        /aws1/cx_rt_generic .

    "! <p class="shorttext synchronized" lang="en">Untags a HealthLake resource</p>
    "! @parameter iv_resource_arn | <p class="shorttext synchronized" lang="en">The resource ARN</p>
    "! @parameter it_tag_keys | <p class="shorttext synchronized" lang="en">The tag keys to remove</p>
    "! @raising /aws1/cx_rt_generic | <p class="shorttext synchronized" lang="en">Raising exception in case of an error</p>
    METHODS untag_resource
      IMPORTING
        !iv_resource_arn TYPE /aws1/hllamazonresourcename
        !it_tag_keys     TYPE /aws1/cl_hlltagkeylist_w=>tt_tagkeylist
      RAISING
        /aws1/cx_rt_generic .

  PROTECTED SECTION.
  PRIVATE SECTION.
ENDCLASS.



CLASS /awsex/cl_hll_actions IMPLEMENTATION.


  METHOD create_fhir_datastore.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_hll) = /aws1/cl_hll_factory=>create( lo_session ).

    " snippet-start:[hll.abapv1.create_fhir_datastore]
    TRY.
        " iv_datastore_name = 'MyHealthLakeDataStore'
        oo_result = lo_hll->createfhirdatastore(
          iv_datastorename = iv_datastore_name
          iv_datastoretypeversion = 'R4'
        ).
        MESSAGE 'Data store created successfully.' TYPE 'I'.
      CATCH /aws1/cx_hllvalidationex INTO DATA(lo_validation_ex).
        DATA(lv_error) = |Validation error: { lo_validation_ex->av_err_code }-{ lo_validation_ex->av_err_msg }|.
        MESSAGE lv_error TYPE 'E'.
      CATCH /aws1/cx_hllinternalserverex INTO DATA(lo_internal_ex).
        lv_error = |Internal server error: { lo_internal_ex->av_err_code }-{ lo_internal_ex->av_err_msg }|.
        MESSAGE lv_error TYPE 'E'.
      CATCH /aws1/cx_hllthrottlingex INTO DATA(lo_throttling_ex).
        lv_error = |Throttling error: { lo_throttling_ex->av_err_code }-{ lo_throttling_ex->av_err_msg }|.
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
        " iv_datastore_id = 'a1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6'
        oo_result = lo_hll->describefhirdatastore(
          iv_datastoreid = iv_datastore_id
        ).
        DATA(lo_datastore_properties) = oo_result->get_datastoreproperties( ).
        IF lo_datastore_properties IS BOUND.
          DATA(lv_datastore_name) = lo_datastore_properties->get_datastorename( ).
          DATA(lv_datastore_status) = lo_datastore_properties->get_datastorestatus( ).
          MESSAGE 'Data store described successfully.' TYPE 'I'.
        ENDIF.
      CATCH /aws1/cx_hllresourcenotfoundex INTO DATA(lo_notfound_ex).
        DATA(lv_error) = |Resource not found: { lo_notfound_ex->av_err_code }-{ lo_notfound_ex->av_err_msg }|.
        MESSAGE lv_error TYPE 'E'.
      CATCH /aws1/cx_hllvalidationex INTO DATA(lo_validation_ex).
        lv_error = |Validation error: { lo_validation_ex->av_err_code }-{ lo_validation_ex->av_err_msg }|.
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
        DATA(lv_datastore_count) = lines( lt_datastores ).
        MESSAGE |Found { lv_datastore_count } data store(s).| TYPE 'I'.
      CATCH /aws1/cx_hllvalidationex INTO DATA(lo_validation_ex).
        DATA(lv_error) = |Validation error: { lo_validation_ex->av_err_code }-{ lo_validation_ex->av_err_msg }|.
        MESSAGE lv_error TYPE 'E'.
      CATCH /aws1/cx_hllthrottlingex INTO DATA(lo_throttling_ex).
        lv_error = |Throttling error: { lo_throttling_ex->av_err_code }-{ lo_throttling_ex->av_err_msg }|.
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
        " iv_datastore_id = 'a1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6'
        oo_result = lo_hll->deletefhirdatastore(
          iv_datastoreid = iv_datastore_id
        ).
        MESSAGE 'Data store deleted successfully.' TYPE 'I'.
      CATCH /aws1/cx_hllaccessdeniedex INTO DATA(lo_access_ex).
        DATA(lv_error) = |Access denied: { lo_access_ex->av_err_code }-{ lo_access_ex->av_err_msg }|.
        MESSAGE lv_error TYPE 'E'.
      CATCH /aws1/cx_hllconflictexception INTO DATA(lo_conflict_ex).
        lv_error = |Conflict error: { lo_conflict_ex->av_err_code }-{ lo_conflict_ex->av_err_msg }|.
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
        " iv_job_name = 'MyImportJob'
        " iv_input_s3_uri = 's3://my-bucket/import/data.ndjson'
        " iv_job_output_s3_uri = 's3://my-bucket/import/output/'
        " iv_kms_key_id = 'arn:aws:kms:us-east-1:123456789012:key/12345678-1234-1234-1234-123456789012'
        " iv_data_access_role_arn = 'arn:aws:iam::123456789012:role/HealthLakeImportRole'
        oo_result = lo_hll->startfhirimportjob(
          iv_jobname = iv_job_name
          io_inputdataconfig = NEW /aws1/cl_hllinputdataconfig( iv_input_s3_uri )
          io_joboutputdataconfig = NEW /aws1/cl_hlloutputdataconfig(
            io_s3configuration = NEW /aws1/cl_hlls3configuration(
              iv_s3uri = iv_job_output_s3_uri
              iv_kmskeyid = iv_kms_key_id
            )
          )
          iv_dataaccessrolearn = iv_data_access_role_arn
          iv_datastoreid = iv_datastore_id
        ).
        DATA(lv_job_id) = oo_result->get_jobid( ).
        MESSAGE |Import job started with ID { lv_job_id }.| TYPE 'I'.
      CATCH /aws1/cx_hllvalidationex INTO DATA(lo_validation_ex).
        DATA(lv_error) = |Validation error: { lo_validation_ex->av_err_code }-{ lo_validation_ex->av_err_msg }|.
        MESSAGE lv_error TYPE 'E'.
      CATCH /aws1/cx_hllthrottlingex INTO DATA(lo_throttling_ex).
        lv_error = |Throttling error: { lo_throttling_ex->av_err_code }-{ lo_throttling_ex->av_err_msg }|.
        MESSAGE lv_error TYPE 'E'.
      CATCH /aws1/cx_hllaccessdeniedex INTO DATA(lo_access_ex).
        lv_error = |Access denied: { lo_access_ex->av_err_code }-{ lo_access_ex->av_err_msg }|.
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
        " iv_datastore_id = 'a1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6'
        " iv_job_id = 'a1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6'
        oo_result = lo_hll->describefhirimportjob(
          iv_datastoreid = iv_datastore_id
          iv_jobid = iv_job_id
        ).
        DATA(lo_import_job_properties) = oo_result->get_importjobproperties( ).
        IF lo_import_job_properties IS BOUND.
          DATA(lv_job_status) = lo_import_job_properties->get_jobstatus( ).
          MESSAGE |Import job status: { lv_job_status }.| TYPE 'I'.
        ENDIF.
      CATCH /aws1/cx_hllresourcenotfoundex INTO DATA(lo_notfound_ex).
        DATA(lv_error) = |Resource not found: { lo_notfound_ex->av_err_code }-{ lo_notfound_ex->av_err_msg }|.
        MESSAGE lv_error TYPE 'E'.
      CATCH /aws1/cx_hllvalidationex INTO DATA(lo_validation_ex).
        lv_error = |Validation error: { lo_validation_ex->av_err_code }-{ lo_validation_ex->av_err_msg }|.
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
        " iv_datastore_id = 'a1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6'
        oo_result = lo_hll->listfhirimportjobs(
          iv_datastoreid = iv_datastore_id
          iv_submittedafter = iv_submitted_after
        ).
        DATA(lt_import_jobs) = oo_result->get_importjobpropertieslist( ).
        DATA(lv_job_count) = lines( lt_import_jobs ).
        MESSAGE |Found { lv_job_count } import job(s).| TYPE 'I'.
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
        " iv_job_name = 'MyExportJob'
        " iv_output_s3_uri = 's3://my-bucket/export/output/'
        " iv_kms_key_id = 'arn:aws:kms:us-east-1:123456789012:key/12345678-1234-1234-1234-123456789012'
        " iv_data_access_role_arn = 'arn:aws:iam::123456789012:role/HealthLakeExportRole'
        oo_result = lo_hll->startfhirexportjob(
          iv_jobname = iv_job_name
          io_outputdataconfig = NEW /aws1/cl_hlloutputdataconfig(
            io_s3configuration = NEW /aws1/cl_hlls3configuration(
              iv_s3uri = iv_output_s3_uri
              iv_kmskeyid = iv_kms_key_id
            )
          )
          iv_dataaccessrolearn = iv_data_access_role_arn
          iv_datastoreid = iv_datastore_id
        ).
        DATA(lv_job_id) = oo_result->get_jobid( ).
        MESSAGE |Export job started with ID { lv_job_id }.| TYPE 'I'.
      CATCH /aws1/cx_hllvalidationex INTO DATA(lo_validation_ex).
        DATA(lv_error) = |Validation error: { lo_validation_ex->av_err_code }-{ lo_validation_ex->av_err_msg }|.
        MESSAGE lv_error TYPE 'E'.
      CATCH /aws1/cx_hllthrottlingex INTO DATA(lo_throttling_ex).
        lv_error = |Throttling error: { lo_throttling_ex->av_err_code }-{ lo_throttling_ex->av_err_msg }|.
        MESSAGE lv_error TYPE 'E'.
      CATCH /aws1/cx_hllaccessdeniedex INTO DATA(lo_access_ex).
        lv_error = |Access denied: { lo_access_ex->av_err_code }-{ lo_access_ex->av_err_msg }|.
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
        " iv_datastore_id = 'a1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6'
        " iv_job_id = 'a1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6'
        oo_result = lo_hll->describefhirexportjob(
          iv_datastoreid = iv_datastore_id
          iv_jobid = iv_job_id
        ).
        DATA(lo_export_job_properties) = oo_result->get_exportjobproperties( ).
        IF lo_export_job_properties IS BOUND.
          DATA(lv_job_status) = lo_export_job_properties->get_jobstatus( ).
          MESSAGE |Export job status: { lv_job_status }.| TYPE 'I'.
        ENDIF.
      CATCH /aws1/cx_hllresourcenotfoundex INTO DATA(lo_notfound_ex).
        DATA(lv_error) = |Resource not found: { lo_notfound_ex->av_err_code }-{ lo_notfound_ex->av_err_msg }|.
        MESSAGE lv_error TYPE 'E'.
      CATCH /aws1/cx_hllvalidationex INTO DATA(lo_validation_ex).
        lv_error = |Validation error: { lo_validation_ex->av_err_code }-{ lo_validation_ex->av_err_msg }|.
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
        " iv_datastore_id = 'a1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6'
        oo_result = lo_hll->listfhirexportjobs(
          iv_datastoreid = iv_datastore_id
          iv_submittedafter = iv_submitted_after
        ).
        DATA(lt_export_jobs) = oo_result->get_exportjobpropertieslist( ).
        DATA(lv_job_count) = lines( lt_export_jobs ).
        MESSAGE |Found { lv_job_count } export job(s).| TYPE 'I'.
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
        " iv_resource_arn = 'arn:aws:healthlake:us-east-1:123456789012:datastore/fhir/a1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6'
        lo_hll->tagresource(
          iv_resourcearn = iv_resource_arn
          it_tags = it_tags
        ).
        MESSAGE 'Resource tagged successfully.' TYPE 'I'.
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
        " iv_resource_arn = 'arn:aws:healthlake:us-east-1:123456789012:datastore/fhir/a1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6'
        DATA(lo_result) = lo_hll->listtagsforresource(
          iv_resourcearn = iv_resource_arn
        ).
        ot_tags = lo_result->get_tags( ).
        DATA(lv_tag_count) = lines( ot_tags ).
        MESSAGE |Found { lv_tag_count } tag(s).| TYPE 'I'.
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
        " iv_resource_arn = 'arn:aws:healthlake:us-east-1:123456789012:datastore/fhir/a1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6'
        lo_hll->untagresource(
          iv_resourcearn = iv_resource_arn
          it_tagkeys = it_tag_keys
        ).
        MESSAGE 'Resource untagged successfully.' TYPE 'I'.
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
