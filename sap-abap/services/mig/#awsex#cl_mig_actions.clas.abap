CLASS /awsex/cl_mig_actions DEFINITION
  PUBLIC
  FINAL
  CREATE PUBLIC .

  PUBLIC SECTION.

    METHODS create_datastore
      IMPORTING
                !iv_datastore_name TYPE /aws1/migdatastorename
      EXPORTING
                !oo_result         TYPE REF TO /aws1/cl_migcreatedatastorersp
      RAISING   /aws1/cx_rt_generic.
    METHODS get_datastore_properties
      IMPORTING
                !iv_datastore_id TYPE /aws1/migdatastoreid
      EXPORTING
                !oo_result       TYPE REF TO /aws1/cl_miggetdatastorersp
      RAISING   /aws1/cx_rt_generic.
    METHODS list_datastores
      EXPORTING
                !oo_result TYPE REF TO /aws1/cl_miglistdatastoresrsp
      RAISING   /aws1/cx_rt_generic.
    METHODS delete_datastore
      IMPORTING
                !iv_datastore_id TYPE /aws1/migdatastoreid
      EXPORTING
                !oo_result       TYPE REF TO /aws1/cl_migdeletedatastorersp
      RAISING   /aws1/cx_rt_generic.
    METHODS start_dicom_import_job
      IMPORTING
                !iv_job_name           TYPE /aws1/migjobname
                !iv_datastore_id       TYPE /aws1/migdatastoreid
                !iv_role_arn           TYPE /aws1/migrolearn
                !iv_input_s3_uri       TYPE /aws1/migs3uri
                !iv_output_s3_uri      TYPE /aws1/migs3uri
      EXPORTING
                !oo_result             TYPE REF TO /aws1/cl_migstrtdicomimpjobrsp
      RAISING   /aws1/cx_rt_generic.
    METHODS get_dicom_import_job
      IMPORTING
                !iv_datastore_id TYPE /aws1/migdatastoreid
                !iv_job_id       TYPE /aws1/migjobid
      EXPORTING
                !oo_result       TYPE REF TO /aws1/cl_miggetdicomimpjobrsp
      RAISING   /aws1/cx_rt_generic.
    METHODS list_dicom_import_jobs
      IMPORTING
                !iv_datastore_id TYPE /aws1/migdatastoreid
      EXPORTING
                !oo_result       TYPE REF TO /aws1/cl_miglstdicomimpjobsrsp
      RAISING   /aws1/cx_rt_generic.
    METHODS search_image_sets
      IMPORTING
                !iv_datastore_id    TYPE /aws1/migdatastoreid
                !io_search_criteria TYPE REF TO /aws1/cl_migsearchcriteria
      EXPORTING
                !oo_result          TYPE REF TO /aws1/cl_migsearchimagesetsrsp
      RAISING   /aws1/cx_rt_generic.
    METHODS get_image_set
      IMPORTING
                !iv_datastore_id TYPE /aws1/migdatastoreid
                !iv_image_set_id TYPE /aws1/migimagesetid
                !iv_version_id   TYPE /aws1/migimagesetexternalvrsid OPTIONAL
      EXPORTING
                !oo_result       TYPE REF TO /aws1/cl_miggetimagesetrsp
      RAISING   /aws1/cx_rt_generic.
    METHODS get_image_set_metadata
      IMPORTING
                !iv_datastore_id TYPE /aws1/migdatastoreid
                !iv_image_set_id TYPE /aws1/migimagesetid
                !iv_version_id   TYPE /aws1/migimagesetexternalvrsid OPTIONAL
      EXPORTING
                !oo_result       TYPE REF TO /aws1/cl_miggetimagesetmetrsp
      RAISING   /aws1/cx_rt_generic.
    METHODS get_image_frame
      IMPORTING
                !iv_datastore_id   TYPE /aws1/migdatastoreid
                !iv_image_set_id   TYPE /aws1/migimagesetid
                !iv_image_frame_id TYPE /aws1/migimageframeid
      EXPORTING
                !oo_result         TYPE REF TO /aws1/cl_miggetimageframersp
      RAISING   /aws1/cx_rt_generic.
    METHODS list_image_set_versions
      IMPORTING
                !iv_datastore_id TYPE /aws1/migdatastoreid
                !iv_image_set_id TYPE /aws1/migimagesetid
      EXPORTING
                !oo_result       TYPE REF TO /aws1/cl_miglstimagesetvrssrsp
      RAISING   /aws1/cx_rt_generic.
    METHODS update_image_set_metadata
      IMPORTING
                !iv_datastore_id             TYPE /aws1/migdatastoreid
                !iv_image_set_id             TYPE /aws1/migimagesetid
                !iv_latest_version_id        TYPE /aws1/migimagesetexternalvrsid
                !io_metadata_updates         TYPE REF TO /aws1/cl_migmetadataupdates
                !iv_force                    TYPE /aws1/migboolean DEFAULT abap_false
      EXPORTING
                !oo_result                   TYPE REF TO /aws1/cl_migupdimagesetmetrsp
      RAISING   /aws1/cx_rt_generic.
    METHODS copy_image_set
      IMPORTING
                !iv_datastore_id                TYPE /aws1/migdatastoreid
                !iv_source_image_set_id         TYPE /aws1/migimagesetid
                !iv_source_version_id           TYPE /aws1/migimagesetexternalvrsid
                !iv_destination_image_set_id    TYPE /aws1/migimagesetid OPTIONAL
                !iv_destination_version_id      TYPE /aws1/migimagesetexternalvrsid OPTIONAL
                !iv_force                       TYPE /aws1/migboolean DEFAULT abap_false
      EXPORTING
                !oo_result                      TYPE REF TO /aws1/cl_migcopyimagesetrsp
      RAISING   /aws1/cx_rt_generic.
    METHODS delete_image_set
      IMPORTING
                !iv_datastore_id TYPE /aws1/migdatastoreid
                !iv_image_set_id TYPE /aws1/migimagesetid
      EXPORTING
                !oo_result       TYPE REF TO /aws1/cl_migdeleteimagesetrsp
      RAISING   /aws1/cx_rt_generic.
    METHODS tag_resource
      IMPORTING
                !iv_resource_arn TYPE /aws1/migarn
                !it_tags         TYPE /aws1/cl_migtagmap_w=>tt_tagmap
      RAISING   /aws1/cx_rt_generic.
    METHODS untag_resource
      IMPORTING
                !iv_resource_arn TYPE /aws1/migarn
                !it_tag_keys     TYPE /aws1/cl_migtagkeylist_w=>tt_tagkeylist
      RAISING   /aws1/cx_rt_generic.
    METHODS list_tags_for_resource
      IMPORTING
                !iv_resource_arn TYPE /aws1/migarn
      EXPORTING
                !oo_result       TYPE REF TO /aws1/cl_miglisttgsforresrcrsp
      RAISING   /aws1/cx_rt_generic.
  PROTECTED SECTION.
  PRIVATE SECTION.
ENDCLASS.



CLASS /AWSEX/CL_MIG_ACTIONS IMPLEMENTATION.


  METHOD create_datastore.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_mig) = /aws1/cl_mig_factory=>create( lo_session ).

    " snippet-start:[mig.abapv1.createdatastore]
    TRY.
        " iv_datastore_name = 'my-datastore-name'
        oo_result = lo_mig->createdatastore( iv_datastorename = iv_datastore_name ).
        DATA(lv_datastore_id) = oo_result->get_datastoreid( ).
        MESSAGE 'Data store created.' TYPE 'I'.
      CATCH /aws1/cx_migaccessdeniedex.
        MESSAGE 'Access denied.' TYPE 'I'.
      CATCH /aws1/cx_migconflictexception.
        MESSAGE 'Conflict. Data store may already exist.' TYPE 'I'.
      CATCH /aws1/cx_miginternalserverex.
        MESSAGE 'Internal server error.' TYPE 'I'.
      CATCH /aws1/cx_migservicequotaexcdex.
        MESSAGE 'Service quota exceeded.' TYPE 'I'.
      CATCH /aws1/cx_migthrottlingex.
        MESSAGE 'Request throttled.' TYPE 'I'.
      CATCH /aws1/cx_migvalidationex.
        MESSAGE 'Validation error.' TYPE 'I'.
    ENDTRY.
    " snippet-end:[mig.abapv1.createdatastore]
  ENDMETHOD.


  METHOD get_datastore_properties.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_mig) = /aws1/cl_mig_factory=>create( lo_session ).

    " snippet-start:[mig.abapv1.getdatastore]
    TRY.
        " iv_datastore_id = '1234567890123456789012345678901234567890'
        oo_result = lo_mig->getdatastore( iv_datastoreid = iv_datastore_id ).
        DATA(lo_properties) = oo_result->get_datastoreproperties( ).
        DATA(lv_name) = lo_properties->get_datastorename( ).
        DATA(lv_status) = lo_properties->get_datastorestatus( ).
        MESSAGE 'Data store properties retrieved.' TYPE 'I'.
      CATCH /aws1/cx_migaccessdeniedex.
        MESSAGE 'Access denied.' TYPE 'I'.
      CATCH /aws1/cx_miginternalserverex.
        MESSAGE 'Internal server error.' TYPE 'I'.
      CATCH /aws1/cx_migresourcenotfoundex.
        MESSAGE 'Data store not found.' TYPE 'I'.
      CATCH /aws1/cx_migthrottlingex.
        MESSAGE 'Request throttled.' TYPE 'I'.
      CATCH /aws1/cx_migvalidationex.
        MESSAGE 'Validation error.' TYPE 'I'.
    ENDTRY.
    " snippet-end:[mig.abapv1.getdatastore]
  ENDMETHOD.


  METHOD list_datastores.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_mig) = /aws1/cl_mig_factory=>create( lo_session ).

    " snippet-start:[mig.abapv1.listdatastores]
    TRY.
        oo_result = lo_mig->listdatastores( ).
        DATA(lt_datastores) = oo_result->get_datastoresummaries( ).
        DATA(lv_count) = lines( lt_datastores ).
        MESSAGE |Found { lv_count } data stores.| TYPE 'I'.
      CATCH /aws1/cx_migaccessdeniedex.
        MESSAGE 'Access denied.' TYPE 'I'.
      CATCH /aws1/cx_miginternalserverex.
        MESSAGE 'Internal server error.' TYPE 'I'.
      CATCH /aws1/cx_migthrottlingex.
        MESSAGE 'Request throttled.' TYPE 'I'.
      CATCH /aws1/cx_migvalidationex.
        MESSAGE 'Validation error.' TYPE 'I'.
    ENDTRY.
    " snippet-end:[mig.abapv1.listdatastores]
  ENDMETHOD.


  METHOD delete_datastore.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_mig) = /aws1/cl_mig_factory=>create( lo_session ).

    " snippet-start:[mig.abapv1.deletedatastore]
    TRY.
        " iv_datastore_id = '1234567890123456789012345678901234567890'
        oo_result = lo_mig->deletedatastore( iv_datastoreid = iv_datastore_id ).
        MESSAGE 'Data store deleted.' TYPE 'I'.
      CATCH /aws1/cx_migaccessdeniedex.
        MESSAGE 'Access denied.' TYPE 'I'.
      CATCH /aws1/cx_migconflictexception.
        MESSAGE 'Conflict. Data store may contain resources.' TYPE 'I'.
      CATCH /aws1/cx_miginternalserverex.
        MESSAGE 'Internal server error.' TYPE 'I'.
      CATCH /aws1/cx_migresourcenotfoundex.
        MESSAGE 'Data store not found.' TYPE 'I'.
      CATCH /aws1/cx_migthrottlingex.
        MESSAGE 'Request throttled.' TYPE 'I'.
      CATCH /aws1/cx_migvalidationex.
        MESSAGE 'Validation error.' TYPE 'I'.
    ENDTRY.
    " snippet-end:[mig.abapv1.deletedatastore]
  ENDMETHOD.


  METHOD start_dicom_import_job.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_mig) = /aws1/cl_mig_factory=>create( lo_session ).

    " snippet-start:[mig.abapv1.startdicomimportjob]
    TRY.
        " iv_job_name = 'import-job-1'
        " iv_datastore_id = '1234567890123456789012345678901234567890'
        " iv_role_arn = 'arn:aws:iam::123456789012:role/ImportJobRole'
        " iv_input_s3_uri = 's3://my-bucket/input/'
        " iv_output_s3_uri = 's3://my-bucket/output/'
        oo_result = lo_mig->startdicomimportjob(
          iv_jobname = iv_job_name
          iv_datastoreid = iv_datastore_id
          iv_dataaccessrolearn = iv_role_arn
          iv_inputs3uri = iv_input_s3_uri
          iv_outputs3uri = iv_output_s3_uri ).
        DATA(lv_job_id) = oo_result->get_jobid( ).
        MESSAGE |DICOM import job started with ID: { lv_job_id }.| TYPE 'I'.
      CATCH /aws1/cx_migaccessdeniedex.
        MESSAGE 'Access denied.' TYPE 'I'.
      CATCH /aws1/cx_migconflictexception.
        MESSAGE 'Conflict error.' TYPE 'I'.
      CATCH /aws1/cx_miginternalserverex.
        MESSAGE 'Internal server error.' TYPE 'I'.
      CATCH /aws1/cx_migresourcenotfoundex.
        MESSAGE 'Resource not found.' TYPE 'I'.
      CATCH /aws1/cx_migservicequotaexcdex.
        MESSAGE 'Service quota exceeded.' TYPE 'I'.
      CATCH /aws1/cx_migthrottlingex.
        MESSAGE 'Request throttled.' TYPE 'I'.
      CATCH /aws1/cx_migvalidationex.
        MESSAGE 'Validation error.' TYPE 'I'.
    ENDTRY.
    " snippet-end:[mig.abapv1.startdicomimportjob]
  ENDMETHOD.


  METHOD get_dicom_import_job.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_mig) = /aws1/cl_mig_factory=>create( lo_session ).

    " snippet-start:[mig.abapv1.getdicomimportjob]
    TRY.
        " iv_datastore_id = '1234567890123456789012345678901234567890'
        " iv_job_id = '12345678901234567890123456789012'
        oo_result = lo_mig->getdicomimportjob(
          iv_datastoreid = iv_datastore_id
          iv_jobid = iv_job_id ).
        DATA(lo_job_props) = oo_result->get_jobproperties( ).
        DATA(lv_job_status) = lo_job_props->get_jobstatus( ).
        MESSAGE |Job status: { lv_job_status }.| TYPE 'I'.
      CATCH /aws1/cx_migaccessdeniedex.
        MESSAGE 'Access denied.' TYPE 'I'.
      CATCH /aws1/cx_migconflictexception.
        MESSAGE 'Conflict error.' TYPE 'I'.
      CATCH /aws1/cx_miginternalserverex.
        MESSAGE 'Internal server error.' TYPE 'I'.
      CATCH /aws1/cx_migresourcenotfoundex.
        MESSAGE 'Job not found.' TYPE 'I'.
      CATCH /aws1/cx_migthrottlingex.
        MESSAGE 'Request throttled.' TYPE 'I'.
      CATCH /aws1/cx_migvalidationex.
        MESSAGE 'Validation error.' TYPE 'I'.
    ENDTRY.
    " snippet-end:[mig.abapv1.getdicomimportjob]
  ENDMETHOD.


  METHOD list_dicom_import_jobs.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_mig) = /aws1/cl_mig_factory=>create( lo_session ).

    " snippet-start:[mig.abapv1.listdicomimportjobs]
    TRY.
        " iv_datastore_id = '1234567890123456789012345678901234567890'
        oo_result = lo_mig->listdicomimportjobs( iv_datastoreid = iv_datastore_id ).
        DATA(lt_jobs) = oo_result->get_jobsummaries( ).
        DATA(lv_count) = lines( lt_jobs ).
        MESSAGE |Found { lv_count } DICOM import jobs.| TYPE 'I'.
      CATCH /aws1/cx_migaccessdeniedex.
        MESSAGE 'Access denied.' TYPE 'I'.
      CATCH /aws1/cx_migconflictexception.
        MESSAGE 'Conflict error.' TYPE 'I'.
      CATCH /aws1/cx_miginternalserverex.
        MESSAGE 'Internal server error.' TYPE 'I'.
      CATCH /aws1/cx_migresourcenotfoundex.
        MESSAGE 'Resource not found.' TYPE 'I'.
      CATCH /aws1/cx_migthrottlingex.
        MESSAGE 'Request throttled.' TYPE 'I'.
      CATCH /aws1/cx_migvalidationex.
        MESSAGE 'Validation error.' TYPE 'I'.
    ENDTRY.
    " snippet-end:[mig.abapv1.listdicomimportjobs]
  ENDMETHOD.


  METHOD search_image_sets.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_mig) = /aws1/cl_mig_factory=>create( lo_session ).

    " snippet-start:[mig.abapv1.searchimagesets]
    TRY.
        " iv_datastore_id = '1234567890123456789012345678901234567890'
        oo_result = lo_mig->searchimagesets(
          iv_datastoreid = iv_datastore_id
          io_searchcriteria = io_search_criteria ).
        DATA(lt_imagesets) = oo_result->get_imagesetsmetadatasums( ).
        DATA(lv_count) = lines( lt_imagesets ).
        MESSAGE |Found { lv_count } image sets.| TYPE 'I'.
      CATCH /aws1/cx_migaccessdeniedex.
        MESSAGE 'Access denied.' TYPE 'I'.
      CATCH /aws1/cx_migconflictexception.
        MESSAGE 'Conflict error.' TYPE 'I'.
      CATCH /aws1/cx_miginternalserverex.
        MESSAGE 'Internal server error.' TYPE 'I'.
      CATCH /aws1/cx_migresourcenotfoundex.
        MESSAGE 'Resource not found.' TYPE 'I'.
      CATCH /aws1/cx_migthrottlingex.
        MESSAGE 'Request throttled.' TYPE 'I'.
      CATCH /aws1/cx_migvalidationex.
        MESSAGE 'Validation error.' TYPE 'I'.
    ENDTRY.
    " snippet-end:[mig.abapv1.searchimagesets]
  ENDMETHOD.


  METHOD get_image_set.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_mig) = /aws1/cl_mig_factory=>create( lo_session ).

    " snippet-start:[mig.abapv1.getimageset]
    TRY.
        " iv_datastore_id = '1234567890123456789012345678901234567890'
        " iv_image_set_id = '1234567890123456789012345678901234567890'
        " iv_version_id = '1' (optional)
        IF iv_version_id IS NOT INITIAL.
          oo_result = lo_mig->getimageset(
            iv_datastoreid = iv_datastore_id
            iv_imagesetid = iv_image_set_id
            iv_versionid = iv_version_id ).
        ELSE.
          oo_result = lo_mig->getimageset(
            iv_datastoreid = iv_datastore_id
            iv_imagesetid = iv_image_set_id ).
        ENDIF.
        DATA(lv_state) = oo_result->get_imagesetstate( ).
        MESSAGE |Image set retrieved with state: { lv_state }.| TYPE 'I'.
      CATCH /aws1/cx_migaccessdeniedex.
        MESSAGE 'Access denied.' TYPE 'I'.
      CATCH /aws1/cx_migconflictexception.
        MESSAGE 'Conflict error.' TYPE 'I'.
      CATCH /aws1/cx_miginternalserverex.
        MESSAGE 'Internal server error.' TYPE 'I'.
      CATCH /aws1/cx_migresourcenotfoundex.
        MESSAGE 'Image set not found.' TYPE 'I'.
      CATCH /aws1/cx_migthrottlingex.
        MESSAGE 'Request throttled.' TYPE 'I'.
      CATCH /aws1/cx_migvalidationex.
        MESSAGE 'Validation error.' TYPE 'I'.
    ENDTRY.
    " snippet-end:[mig.abapv1.getimageset]
  ENDMETHOD.


  METHOD get_image_set_metadata.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_mig) = /aws1/cl_mig_factory=>create( lo_session ).

    " snippet-start:[mig.abapv1.getimagesetmetadata]
    TRY.
        " iv_datastore_id = '1234567890123456789012345678901234567890'
        " iv_image_set_id = '1234567890123456789012345678901234567890'
        " iv_version_id = '1' (optional)
        IF iv_version_id IS NOT INITIAL.
          oo_result = lo_mig->getimagesetmetadata(
            iv_datastoreid = iv_datastore_id
            iv_imagesetid = iv_image_set_id
            iv_versionid = iv_version_id ).
        ELSE.
          oo_result = lo_mig->getimagesetmetadata(
            iv_datastoreid = iv_datastore_id
            iv_imagesetid = iv_image_set_id ).
        ENDIF.
        DATA(lv_metadata_blob) = oo_result->get_imagesetmetadatablob( ).
        MESSAGE 'Image set metadata retrieved.' TYPE 'I'.
      CATCH /aws1/cx_migaccessdeniedex.
        MESSAGE 'Access denied.' TYPE 'I'.
      CATCH /aws1/cx_migconflictexception.
        MESSAGE 'Conflict error.' TYPE 'I'.
      CATCH /aws1/cx_miginternalserverex.
        MESSAGE 'Internal server error.' TYPE 'I'.
      CATCH /aws1/cx_migresourcenotfoundex.
        MESSAGE 'Image set not found.' TYPE 'I'.
      CATCH /aws1/cx_migthrottlingex.
        MESSAGE 'Request throttled.' TYPE 'I'.
      CATCH /aws1/cx_migvalidationex.
        MESSAGE 'Validation error.' TYPE 'I'.
    ENDTRY.
    " snippet-end:[mig.abapv1.getimagesetmetadata]
  ENDMETHOD.


  METHOD get_image_frame.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_mig) = /aws1/cl_mig_factory=>create( lo_session ).

    " snippet-start:[mig.abapv1.getimageframe]
    TRY.
        " iv_datastore_id = '1234567890123456789012345678901234567890'
        " iv_image_set_id = '1234567890123456789012345678901234567890'
        " iv_image_frame_id = '1234567890123456789012345678901234567890'
        oo_result = lo_mig->getimageframe(
          iv_datastoreid = iv_datastore_id
          iv_imagesetid = iv_image_set_id
          io_imageframeinformation = NEW /aws1/cl_migimageframeinfmtion(
            iv_imageframeid = iv_image_frame_id ) ).
        DATA(lv_frame_blob) = oo_result->get_imageframeblob( ).
        MESSAGE 'Image frame retrieved.' TYPE 'I'.
      CATCH /aws1/cx_migaccessdeniedex.
        MESSAGE 'Access denied.' TYPE 'I'.
      CATCH /aws1/cx_migconflictexception.
        MESSAGE 'Conflict error.' TYPE 'I'.
      CATCH /aws1/cx_miginternalserverex.
        MESSAGE 'Internal server error.' TYPE 'I'.
      CATCH /aws1/cx_migresourcenotfoundex.
        MESSAGE 'Image frame not found.' TYPE 'I'.
      CATCH /aws1/cx_migthrottlingex.
        MESSAGE 'Request throttled.' TYPE 'I'.
      CATCH /aws1/cx_migvalidationex.
        MESSAGE 'Validation error.' TYPE 'I'.
    ENDTRY.
    " snippet-end:[mig.abapv1.getimageframe]
  ENDMETHOD.


  METHOD list_image_set_versions.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_mig) = /aws1/cl_mig_factory=>create( lo_session ).

    " snippet-start:[mig.abapv1.listimagesetversions]
    TRY.
        " iv_datastore_id = '1234567890123456789012345678901234567890'
        " iv_image_set_id = '1234567890123456789012345678901234567890'
        oo_result = lo_mig->listimagesetversions(
          iv_datastoreid = iv_datastore_id
          iv_imagesetid = iv_image_set_id ).
        DATA(lt_versions) = oo_result->get_imagesetpropertieslist( ).
        DATA(lv_count) = lines( lt_versions ).
        MESSAGE |Found { lv_count } image set versions.| TYPE 'I'.
      CATCH /aws1/cx_migaccessdeniedex.
        MESSAGE 'Access denied.' TYPE 'I'.
      CATCH /aws1/cx_migconflictexception.
        MESSAGE 'Conflict error.' TYPE 'I'.
      CATCH /aws1/cx_miginternalserverex.
        MESSAGE 'Internal server error.' TYPE 'I'.
      CATCH /aws1/cx_migresourcenotfoundex.
        MESSAGE 'Image set not found.' TYPE 'I'.
      CATCH /aws1/cx_migthrottlingex.
        MESSAGE 'Request throttled.' TYPE 'I'.
      CATCH /aws1/cx_migvalidationex.
        MESSAGE 'Validation error.' TYPE 'I'.
    ENDTRY.
    " snippet-end:[mig.abapv1.listimagesetversions]
  ENDMETHOD.


  METHOD update_image_set_metadata.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_mig) = /aws1/cl_mig_factory=>create( lo_session ).

    " snippet-start:[mig.abapv1.updateimagesetmetadata]
    TRY.
        " iv_datastore_id = '1234567890123456789012345678901234567890'
        " iv_image_set_id = '1234567890123456789012345678901234567890'
        " iv_latest_version_id = '1'
        " iv_force = abap_false
        oo_result = lo_mig->updateimagesetmetadata(
          iv_datastoreid = iv_datastore_id
          iv_imagesetid = iv_image_set_id
          iv_latestversionid = iv_latest_version_id
          io_updateimagesetmetupdates = io_metadata_updates
          iv_force = iv_force ).
        DATA(lv_new_version) = oo_result->get_latestversionid( ).
        MESSAGE |Image set metadata updated to version: { lv_new_version }.| TYPE 'I'.
      CATCH /aws1/cx_migaccessdeniedex.
        MESSAGE 'Access denied.' TYPE 'I'.
      CATCH /aws1/cx_migconflictexception.
        MESSAGE 'Conflict error.' TYPE 'I'.
      CATCH /aws1/cx_miginternalserverex.
        MESSAGE 'Internal server error.' TYPE 'I'.
      CATCH /aws1/cx_migresourcenotfoundex.
        MESSAGE 'Image set not found.' TYPE 'I'.
      CATCH /aws1/cx_migservicequotaexcdex.
        MESSAGE 'Service quota exceeded.' TYPE 'I'.
      CATCH /aws1/cx_migthrottlingex.
        MESSAGE 'Request throttled.' TYPE 'I'.
      CATCH /aws1/cx_migvalidationex.
        MESSAGE 'Validation error.' TYPE 'I'.
    ENDTRY.
    " snippet-end:[mig.abapv1.updateimagesetmetadata]
  ENDMETHOD.


  METHOD copy_image_set.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_mig) = /aws1/cl_mig_factory=>create( lo_session ).

    " snippet-start:[mig.abapv1.copyimageset]
    TRY.
        " iv_datastore_id = '1234567890123456789012345678901234567890'
        " iv_source_image_set_id = '1234567890123456789012345678901234567890'
        " iv_source_version_id = '1'
        " iv_destination_image_set_id = '1234567890123456789012345678901234567890' (optional)
        " iv_destination_version_id = '1' (optional)
        " iv_force = abap_false
        DATA(lo_source_info) = NEW /aws1/cl_migcpsrcimagesetinf00(
          iv_latestversionid = iv_source_version_id ).
        DATA(lo_copy_info) = NEW /aws1/cl_migcpimagesetinfmtion(
          io_sourceimageset = lo_source_info ).
        IF iv_destination_image_set_id IS NOT INITIAL AND
           iv_destination_version_id IS NOT INITIAL.
          DATA(lo_dest_info) = NEW /aws1/cl_migcopydstimageset(
            iv_imagesetid = iv_destination_image_set_id
            iv_latestversionid = iv_destination_version_id ).
          lo_copy_info = NEW /aws1/cl_migcpimagesetinfmtion(
            io_sourceimageset = lo_source_info
            io_destinationimageset = lo_dest_info ).
        ENDIF.
        oo_result = lo_mig->copyimageset(
          iv_datastoreid = iv_datastore_id
          iv_sourceimagesetid = iv_source_image_set_id
          io_copyimagesetinformation = lo_copy_info
          iv_force = iv_force ).
        DATA(lo_dest_props) = oo_result->get_dstimagesetproperties( ).
        DATA(lv_new_id) = lo_dest_props->get_imagesetid( ).
        MESSAGE |Image set copied with new ID: { lv_new_id }.| TYPE 'I'.
      CATCH /aws1/cx_migaccessdeniedex.
        MESSAGE 'Access denied.' TYPE 'I'.
      CATCH /aws1/cx_migconflictexception.
        MESSAGE 'Conflict error.' TYPE 'I'.
      CATCH /aws1/cx_miginternalserverex.
        MESSAGE 'Internal server error.' TYPE 'I'.
      CATCH /aws1/cx_migresourcenotfoundex.
        MESSAGE 'Image set not found.' TYPE 'I'.
      CATCH /aws1/cx_migservicequotaexcdex.
        MESSAGE 'Service quota exceeded.' TYPE 'I'.
      CATCH /aws1/cx_migthrottlingex.
        MESSAGE 'Request throttled.' TYPE 'I'.
      CATCH /aws1/cx_migvalidationex.
        MESSAGE 'Validation error.' TYPE 'I'.
    ENDTRY.
    " snippet-end:[mig.abapv1.copyimageset]
  ENDMETHOD.


  METHOD delete_image_set.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_mig) = /aws1/cl_mig_factory=>create( lo_session ).

    " snippet-start:[mig.abapv1.deleteimageset]
    TRY.
        " iv_datastore_id = '1234567890123456789012345678901234567890'
        " iv_image_set_id = '1234567890123456789012345678901234567890'
        oo_result = lo_mig->deleteimageset(
          iv_datastoreid = iv_datastore_id
          iv_imagesetid = iv_image_set_id ).
        MESSAGE 'Image set deleted.' TYPE 'I'.
      CATCH /aws1/cx_migaccessdeniedex.
        MESSAGE 'Access denied.' TYPE 'I'.
      CATCH /aws1/cx_migconflictexception.
        MESSAGE 'Conflict error.' TYPE 'I'.
      CATCH /aws1/cx_miginternalserverex.
        MESSAGE 'Internal server error.' TYPE 'I'.
      CATCH /aws1/cx_migresourcenotfoundex.
        MESSAGE 'Image set not found.' TYPE 'I'.
      CATCH /aws1/cx_migthrottlingex.
        MESSAGE 'Request throttled.' TYPE 'I'.
      CATCH /aws1/cx_migvalidationex.
        MESSAGE 'Validation error.' TYPE 'I'.
    ENDTRY.
    " snippet-end:[mig.abapv1.deleteimageset]
  ENDMETHOD.


  METHOD tag_resource.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_mig) = /aws1/cl_mig_factory=>create( lo_session ).

    " snippet-start:[mig.abapv1.tagresource]
    TRY.
        " iv_resource_arn = 'arn:aws:medical-imaging:us-east-1:123456789012:datastore/12345678901234567890123456789012'
        lo_mig->tagresource(
          iv_resourcearn = iv_resource_arn
          it_tags = it_tags ).
        MESSAGE 'Resource tagged successfully.' TYPE 'I'.
      CATCH /aws1/cx_migaccessdeniedex.
        MESSAGE 'Access denied.' TYPE 'I'.
      CATCH /aws1/cx_miginternalserverex.
        MESSAGE 'Internal server error.' TYPE 'I'.
      CATCH /aws1/cx_migresourcenotfoundex.
        MESSAGE 'Resource not found.' TYPE 'I'.
      CATCH /aws1/cx_migthrottlingex.
        MESSAGE 'Request throttled.' TYPE 'I'.
      CATCH /aws1/cx_migvalidationex.
        MESSAGE 'Validation error.' TYPE 'I'.
    ENDTRY.
    " snippet-end:[mig.abapv1.tagresource]
  ENDMETHOD.


  METHOD untag_resource.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_mig) = /aws1/cl_mig_factory=>create( lo_session ).

    " snippet-start:[mig.abapv1.untagresource]
    TRY.
        " iv_resource_arn = 'arn:aws:medical-imaging:us-east-1:123456789012:datastore/12345678901234567890123456789012'
        lo_mig->untagresource(
          iv_resourcearn = iv_resource_arn
          it_tagkeys = it_tag_keys ).
        MESSAGE 'Resource untagged successfully.' TYPE 'I'.
      CATCH /aws1/cx_migaccessdeniedex.
        MESSAGE 'Access denied.' TYPE 'I'.
      CATCH /aws1/cx_miginternalserverex.
        MESSAGE 'Internal server error.' TYPE 'I'.
      CATCH /aws1/cx_migresourcenotfoundex.
        MESSAGE 'Resource not found.' TYPE 'I'.
      CATCH /aws1/cx_migthrottlingex.
        MESSAGE 'Request throttled.' TYPE 'I'.
      CATCH /aws1/cx_migvalidationex.
        MESSAGE 'Validation error.' TYPE 'I'.
    ENDTRY.
    " snippet-end:[mig.abapv1.untagresource]
  ENDMETHOD.


  METHOD list_tags_for_resource.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_mig) = /aws1/cl_mig_factory=>create( lo_session ).

    " snippet-start:[mig.abapv1.listtagsforresource]
    TRY.
        " iv_resource_arn = 'arn:aws:medical-imaging:us-east-1:123456789012:datastore/12345678901234567890123456789012'
        oo_result = lo_mig->listtagsforresource( iv_resourcearn = iv_resource_arn ).
        DATA(lt_tags) = oo_result->get_tags( ).
        DATA(lv_count) = lines( lt_tags ).
        MESSAGE |Found { lv_count } tags for resource.| TYPE 'I'.
      CATCH /aws1/cx_migaccessdeniedex.
        MESSAGE 'Access denied.' TYPE 'I'.
      CATCH /aws1/cx_miginternalserverex.
        MESSAGE 'Internal server error.' TYPE 'I'.
      CATCH /aws1/cx_migresourcenotfoundex.
        MESSAGE 'Resource not found.' TYPE 'I'.
      CATCH /aws1/cx_migthrottlingex.
        MESSAGE 'Request throttled.' TYPE 'I'.
      CATCH /aws1/cx_migvalidationex.
        MESSAGE 'Validation error.' TYPE 'I'.
    ENDTRY.
    " snippet-end:[mig.abapv1.listtagsforresource]
  ENDMETHOD.
ENDCLASS.
