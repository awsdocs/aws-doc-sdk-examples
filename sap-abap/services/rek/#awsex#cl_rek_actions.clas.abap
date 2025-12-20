" Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
" SPDX-License-Identifier: Apache-2.0
CLASS /awsex/cl_rek_actions DEFINITION
  PUBLIC
  FINAL
  CREATE PUBLIC .

  PUBLIC SECTION.

    METHODS create_collection
      IMPORTING
        !iv_collection_id TYPE /aws1/rekcollectionid
      EXPORTING
        !oo_result        TYPE REF TO /aws1/cl_rekcreatecollresponse
      RAISING
        /aws1/cx_rt_generic.

    METHODS delete_collection
      IMPORTING
        !iv_collection_id TYPE /aws1/rekcollectionid
      RAISING
        /aws1/cx_rt_generic.

    METHODS describe_collection
      IMPORTING
        !iv_collection_id TYPE /aws1/rekcollectionid
      EXPORTING
        !oo_result        TYPE REF TO /aws1/cl_rekdescrcollresponse
      RAISING
        /aws1/cx_rt_generic.

    METHODS index_faces
      IMPORTING
        !iv_collection_id TYPE /aws1/rekcollectionid
        !iv_s3_bucket     TYPE /aws1/reks3bucket
        !iv_s3_key        TYPE /aws1/reks3objectname
        !iv_external_id   TYPE /aws1/rekexternalimageid
        !iv_max_faces     TYPE /aws1/rekmaxfacestoindex
      EXPORTING
        !oo_result        TYPE REF TO /aws1/cl_rekindexfacesresponse
      RAISING
        /aws1/cx_rt_generic.

    METHODS list_faces
      IMPORTING
        !iv_collection_id TYPE /aws1/rekcollectionid
        !iv_max_results   TYPE /aws1/rekpagesize
      EXPORTING
        !oo_result        TYPE REF TO /aws1/cl_reklistfacesresponse
      RAISING
        /aws1/cx_rt_generic.

    METHODS search_faces_by_image
      IMPORTING
        !iv_collection_id      TYPE /aws1/rekcollectionid
        !iv_s3_bucket          TYPE /aws1/reks3bucket
        !iv_s3_key             TYPE /aws1/reks3objectname
        !iv_threshold          TYPE /aws1/rt_float_as_string
        !iv_max_faces          TYPE /aws1/rekmaxfaces
      EXPORTING
        !oo_result             TYPE REF TO /aws1/cl_reksrchfacesbyimage01
      RAISING
        /aws1/cx_rt_generic.

    METHODS search_faces
      IMPORTING
        !iv_collection_id TYPE /aws1/rekcollectionid
        !iv_face_id       TYPE /aws1/rekfaceid
        !iv_threshold     TYPE /aws1/rt_float_as_string
        !iv_max_faces     TYPE /aws1/rekmaxfaces
      EXPORTING
        !oo_result        TYPE REF TO /aws1/cl_reksearchfacesrsp
      RAISING
        /aws1/cx_rt_generic.

    METHODS delete_faces
      IMPORTING
        !iv_collection_id TYPE /aws1/rekcollectionid
        !it_face_ids      TYPE /aws1/cl_rekfaceidlist_w=>tt_faceidlist
      EXPORTING
        !oo_result        TYPE REF TO /aws1/cl_rekdeletefacesrsp
      RAISING
        /aws1/cx_rt_generic.

    METHODS list_collections
      IMPORTING
        !iv_max_results TYPE /aws1/rekpagesize
      EXPORTING
        !oo_result      TYPE REF TO /aws1/cl_reklistcollsresponse
      RAISING
        /aws1/cx_rt_generic.

    METHODS detect_faces
      IMPORTING
        !iv_s3_bucket TYPE /aws1/reks3bucket
        !iv_s3_key    TYPE /aws1/reks3objectname
      EXPORTING
        !oo_result    TYPE REF TO /aws1/cl_rekdetectfacesrsp
      RAISING
        /aws1/cx_rt_generic.

    METHODS compare_faces
      IMPORTING
        !iv_source_s3_bucket TYPE /aws1/reks3bucket
        !iv_source_s3_key    TYPE /aws1/reks3objectname
        !iv_target_s3_bucket TYPE /aws1/reks3bucket
        !iv_target_s3_key    TYPE /aws1/reks3objectname
        !iv_similarity       TYPE /aws1/rt_float_as_string
      EXPORTING
        !oo_result           TYPE REF TO /aws1/cl_rekcomparefacesrsp
      RAISING
        /aws1/cx_rt_generic.

    METHODS detect_labels
      IMPORTING
        !iv_s3_bucket  TYPE /aws1/reks3bucket
        !iv_s3_key     TYPE /aws1/reks3objectname
        !iv_max_labels TYPE /aws1/rekuinteger
      EXPORTING
        !oo_result     TYPE REF TO /aws1/cl_rekdetectlabelsrsp
      RAISING
        /aws1/cx_rt_generic.

    METHODS detect_moderation_labels
      IMPORTING
        !iv_s3_bucket TYPE /aws1/reks3bucket
        !iv_s3_key    TYPE /aws1/reks3objectname
      EXPORTING
        !oo_result    TYPE REF TO /aws1/cl_rekdetectmderationl01
      RAISING
        /aws1/cx_rt_generic.

    METHODS detect_text
      IMPORTING
        !iv_s3_bucket TYPE /aws1/reks3bucket
        !iv_s3_key    TYPE /aws1/reks3objectname
      EXPORTING
        !oo_result    TYPE REF TO /aws1/cl_rekdetecttextresponse
      RAISING
        /aws1/cx_rt_generic.

    METHODS recognize_celebrities
      IMPORTING
        !iv_s3_bucket TYPE /aws1/reks3bucket
        !iv_s3_key    TYPE /aws1/reks3objectname
      EXPORTING
        !oo_result    TYPE REF TO /aws1/cl_rekrecognizecelebri01
      RAISING
        /aws1/cx_rt_generic.

  PROTECTED SECTION.
  PRIVATE SECTION.
ENDCLASS.



CLASS /AWSEX/CL_REK_ACTIONS IMPLEMENTATION.


  METHOD create_collection.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_rek) = /aws1/cl_rek_factory=>create( lo_session ).

    " snippet-start:[rek.abapv1.createcollection]
    TRY.
        oo_result = lo_rek->createcollection(
          iv_collectionid = iv_collection_id ).
        MESSAGE 'Collection created successfully.' TYPE 'I'.
      CATCH /aws1/cx_rekresrcalrdyexistsex.
        MESSAGE 'Collection already exists.' TYPE 'E'.
      CATCH /aws1/cx_rekinvalidparameterex.
        MESSAGE 'Invalid parameter value.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[rek.abapv1.createcollection]
  ENDMETHOD.


  METHOD delete_collection.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_rek) = /aws1/cl_rek_factory=>create( lo_session ).

    " snippet-start:[rek.abapv1.deletecollection]
    TRY.
        lo_rek->deletecollection(
          iv_collectionid = iv_collection_id ).
        MESSAGE 'Collection deleted successfully.' TYPE 'I'.
      CATCH /aws1/cx_rekresourcenotfoundex.
        MESSAGE 'Collection not found.' TYPE 'E'.
      CATCH /aws1/cx_rekinvalidparameterex.
        MESSAGE 'Invalid parameter value.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[rek.abapv1.deletecollection]
  ENDMETHOD.


  METHOD describe_collection.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_rek) = /aws1/cl_rek_factory=>create( lo_session ).

    " snippet-start:[rek.abapv1.describecollection]
    TRY.
        oo_result = lo_rek->describecollection(
          iv_collectionid = iv_collection_id ).
        DATA(lv_face_count) = oo_result->get_facecount( ).
        MESSAGE 'Collection described successfully.' TYPE 'I'.
      CATCH /aws1/cx_rekresourcenotfoundex.
        MESSAGE 'Collection not found.' TYPE 'E'.
      CATCH /aws1/cx_rekinvalidparameterex.
        MESSAGE 'Invalid parameter value.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[rek.abapv1.describecollection]
  ENDMETHOD.


  METHOD index_faces.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_rek) = /aws1/cl_rek_factory=>create( lo_session ).

    " snippet-start:[rek.abapv1.indexfaces]
    TRY.
        " Create S3 object reference for the image
        DATA(lo_s3object) = NEW /aws1/cl_reks3object(
          iv_bucket = iv_s3_bucket
          iv_name = iv_s3_key ).

        " Create image object
        DATA(lo_image) = NEW /aws1/cl_rekimage(
          io_s3object = lo_s3object ).

        " Index faces in the image
        oo_result = lo_rek->indexfaces(
          iv_collectionid = iv_collection_id
          io_image = lo_image
          iv_externalimageid = iv_external_id
          iv_maxfaces = iv_max_faces ).

        DATA(lt_face_records) = oo_result->get_facerecords( ).
        MESSAGE 'Faces indexed successfully.' TYPE 'I'.
      CATCH /aws1/cx_rekresourcenotfoundex.
        MESSAGE 'Collection not found.' TYPE 'E'.
      CATCH /aws1/cx_rekinvalids3objectex.
        MESSAGE 'Invalid S3 object.' TYPE 'E'.
      CATCH /aws1/cx_rekinvalidparameterex.
        MESSAGE 'Invalid parameter value.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[rek.abapv1.indexfaces]
  ENDMETHOD.


  METHOD list_faces.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_rek) = /aws1/cl_rek_factory=>create( lo_session ).

    " snippet-start:[rek.abapv1.listfaces]
    TRY.
        oo_result = lo_rek->listfaces(
          iv_collectionid = iv_collection_id
          iv_maxresults = iv_max_results ).

        DATA(lt_faces) = oo_result->get_faces( ).
        MESSAGE 'Faces listed successfully.' TYPE 'I'.
      CATCH /aws1/cx_rekresourcenotfoundex.
        MESSAGE 'Collection not found.' TYPE 'E'.
      CATCH /aws1/cx_rekinvalidparameterex.
        MESSAGE 'Invalid parameter value.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[rek.abapv1.listfaces]
  ENDMETHOD.


  METHOD search_faces_by_image.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_rek) = /aws1/cl_rek_factory=>create( lo_session ).

    " snippet-start:[rek.abapv1.searchfacesbyimage]
    TRY.
        " Create S3 object reference for the image
        DATA(lo_s3object) = NEW /aws1/cl_reks3object(
          iv_bucket = iv_s3_bucket
          iv_name = iv_s3_key ).

        " Create image object
        DATA(lo_image) = NEW /aws1/cl_rekimage(
          io_s3object = lo_s3object ).

        " Search for matching faces
        oo_result = lo_rek->searchfacesbyimage(
          iv_collectionid = iv_collection_id
          io_image = lo_image
          iv_facematchthreshold = iv_threshold
          iv_maxfaces = iv_max_faces ).

        DATA(lt_face_matches) = oo_result->get_facematches( ).
        MESSAGE 'Face search completed successfully.' TYPE 'I'.
      CATCH /aws1/cx_rekresourcenotfoundex.
        MESSAGE 'Collection not found.' TYPE 'E'.
      CATCH /aws1/cx_rekinvalids3objectex.
        MESSAGE 'Invalid S3 object.' TYPE 'E'.
      CATCH /aws1/cx_rekinvalidparameterex.
        MESSAGE 'Invalid parameter value.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[rek.abapv1.searchfacesbyimage]
  ENDMETHOD.


  METHOD search_faces.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_rek) = /aws1/cl_rek_factory=>create( lo_session ).

    " snippet-start:[rek.abapv1.searchfaces]
    TRY.
        oo_result = lo_rek->searchfaces(
          iv_collectionid = iv_collection_id
          iv_faceid = iv_face_id
          iv_facematchthreshold = iv_threshold
          iv_maxfaces = iv_max_faces ).

        DATA(lt_face_matches) = oo_result->get_facematches( ).
        MESSAGE 'Face search completed successfully.' TYPE 'I'.
      CATCH /aws1/cx_rekresourcenotfoundex.
        MESSAGE 'Collection or face not found.' TYPE 'E'.
      CATCH /aws1/cx_rekinvalidparameterex.
        MESSAGE 'Invalid parameter value.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[rek.abapv1.searchfaces]
  ENDMETHOD.


  METHOD delete_faces.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_rek) = /aws1/cl_rek_factory=>create( lo_session ).

    " snippet-start:[rek.abapv1.deletefaces]
    TRY.
        oo_result = lo_rek->deletefaces(
          iv_collectionid = iv_collection_id
          it_faceids = it_face_ids ).

        DATA(lt_deleted_faces) = oo_result->get_deletedfaces( ).
        MESSAGE 'Faces deleted successfully.' TYPE 'I'.
      CATCH /aws1/cx_rekresourcenotfoundex.
        MESSAGE 'Collection not found.' TYPE 'E'.
      CATCH /aws1/cx_rekinvalidparameterex.
        MESSAGE 'Invalid parameter value.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[rek.abapv1.deletefaces]
  ENDMETHOD.


  METHOD list_collections.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_rek) = /aws1/cl_rek_factory=>create( lo_session ).

    " snippet-start:[rek.abapv1.listcollections]
    TRY.
        oo_result = lo_rek->listcollections(
          iv_maxresults = iv_max_results ).

        DATA(lt_collection_ids) = oo_result->get_collectionids( ).
        MESSAGE 'Collections listed successfully.' TYPE 'I'.
      CATCH /aws1/cx_rekinvalidparameterex.
        MESSAGE 'Invalid parameter value.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[rek.abapv1.listcollections]
  ENDMETHOD.


  METHOD detect_faces.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_rek) = /aws1/cl_rek_factory=>create( lo_session ).

    " snippet-start:[rek.abapv1.detectfaces]
    TRY.
        " Create S3 object reference for the image
        DATA(lo_s3object) = NEW /aws1/cl_reks3object(
          iv_bucket = iv_s3_bucket
          iv_name = iv_s3_key ).

        " Create image object
        DATA(lo_image) = NEW /aws1/cl_rekimage(
          io_s3object = lo_s3object ).

        " Detect faces in the image with all attributes
        DATA(lt_attributes) = VALUE /aws1/cl_rekattributes_w=>tt_attributes( ).
        APPEND 'ALL' TO lt_attributes.

        oo_result = lo_rek->detectfaces(
          io_image = lo_image
          it_attributes = lt_attributes ).

        DATA(lt_face_details) = oo_result->get_facedetails( ).
        MESSAGE 'Faces detected successfully.' TYPE 'I'.
      CATCH /aws1/cx_rekinvalids3objectex.
        MESSAGE 'Invalid S3 object.' TYPE 'E'.
      CATCH /aws1/cx_rekinvalidparameterex.
        MESSAGE 'Invalid parameter value.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[rek.abapv1.detectfaces]
  ENDMETHOD.


  METHOD compare_faces.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_rek) = /aws1/cl_rek_factory=>create( lo_session ).

    " snippet-start:[rek.abapv1.comparefaces]
    TRY.
        " Create S3 object reference for the source image
        DATA(lo_source_s3obj) = NEW /aws1/cl_reks3object(
          iv_bucket = iv_source_s3_bucket
          iv_name = iv_source_s3_key ).

        " Create source image object
        DATA(lo_source_image) = NEW /aws1/cl_rekimage(
          io_s3object = lo_source_s3obj ).

        " Create S3 object reference for the target image
        DATA(lo_target_s3obj) = NEW /aws1/cl_reks3object(
          iv_bucket = iv_target_s3_bucket
          iv_name = iv_target_s3_key ).

        " Create target image object
        DATA(lo_target_image) = NEW /aws1/cl_rekimage(
          io_s3object = lo_target_s3obj ).

        " Compare faces
        oo_result = lo_rek->comparefaces(
          io_sourceimage = lo_source_image
          io_targetimage = lo_target_image
          iv_similaritythreshold = iv_similarity ).

        DATA(lt_face_matches) = oo_result->get_facematches( ).
        MESSAGE 'Face comparison completed successfully.' TYPE 'I'.
      CATCH /aws1/cx_rekinvalids3objectex.
        MESSAGE 'Invalid S3 object.' TYPE 'E'.
      CATCH /aws1/cx_rekinvalidparameterex.
        MESSAGE 'Invalid parameter value.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[rek.abapv1.comparefaces]
  ENDMETHOD.


  METHOD detect_labels.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_rek) = /aws1/cl_rek_factory=>create( lo_session ).

    " snippet-start:[rek.abapv1.detectlabels]
    TRY.
        " Create S3 object reference for the image
        DATA(lo_s3object) = NEW /aws1/cl_reks3object(
          iv_bucket = iv_s3_bucket
          iv_name = iv_s3_key ).

        " Create image object
        DATA(lo_image) = NEW /aws1/cl_rekimage(
          io_s3object = lo_s3object ).

        " Detect labels in the image
        oo_result = lo_rek->detectlabels(
          io_image = lo_image
          iv_maxlabels = iv_max_labels ).

        DATA(lt_labels) = oo_result->get_labels( ).
        MESSAGE 'Labels detected successfully.' TYPE 'I'.
      CATCH /aws1/cx_rekinvalids3objectex.
        MESSAGE 'Invalid S3 object.' TYPE 'E'.
      CATCH /aws1/cx_rekinvalidparameterex.
        MESSAGE 'Invalid parameter value.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[rek.abapv1.detectlabels]
  ENDMETHOD.


  METHOD detect_moderation_labels.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_rek) = /aws1/cl_rek_factory=>create( lo_session ).

    " snippet-start:[rek.abapv1.detectmoderationlabels]
    TRY.
        " Create S3 object reference for the image
        DATA(lo_s3object) = NEW /aws1/cl_reks3object(
          iv_bucket = iv_s3_bucket
          iv_name = iv_s3_key ).

        " Create image object
        DATA(lo_image) = NEW /aws1/cl_rekimage(
          io_s3object = lo_s3object ).

        " Detect moderation labels
        oo_result = lo_rek->detectmoderationlabels(
          io_image = lo_image ).

        DATA(lt_moderation_labels) = oo_result->get_moderationlabels( ).
        MESSAGE 'Moderation labels detected successfully.' TYPE 'I'.
      CATCH /aws1/cx_rekinvalids3objectex.
        MESSAGE 'Invalid S3 object.' TYPE 'E'.
      CATCH /aws1/cx_rekinvalidparameterex.
        MESSAGE 'Invalid parameter value.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[rek.abapv1.detectmoderationlabels]
  ENDMETHOD.


  METHOD detect_text.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_rek) = /aws1/cl_rek_factory=>create( lo_session ).

    " snippet-start:[rek.abapv1.detecttext]
    TRY.
        " Create S3 object reference for the image
        DATA(lo_s3object) = NEW /aws1/cl_reks3object(
          iv_bucket = iv_s3_bucket
          iv_name = iv_s3_key ).

        " Create image object
        DATA(lo_image) = NEW /aws1/cl_rekimage(
          io_s3object = lo_s3object ).

        " Detect text in the image
        oo_result = lo_rek->detecttext(
          io_image = lo_image ).

        DATA(lt_text_detections) = oo_result->get_textdetections( ).
        MESSAGE 'Text detected successfully.' TYPE 'I'.
      CATCH /aws1/cx_rekinvalids3objectex.
        MESSAGE 'Invalid S3 object.' TYPE 'E'.
      CATCH /aws1/cx_rekinvalidparameterex.
        MESSAGE 'Invalid parameter value.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[rek.abapv1.detecttext]
  ENDMETHOD.


  METHOD recognize_celebrities.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_rek) = /aws1/cl_rek_factory=>create( lo_session ).

    " snippet-start:[rek.abapv1.recognizecelebrities]
    TRY.
        " Create S3 object reference for the image
        DATA(lo_s3object) = NEW /aws1/cl_reks3object(
          iv_bucket = iv_s3_bucket
          iv_name = iv_s3_key ).

        " Create image object
        DATA(lo_image) = NEW /aws1/cl_rekimage(
          io_s3object = lo_s3object ).

        " Recognize celebrities
        oo_result = lo_rek->recognizecelebrities(
          io_image = lo_image ).

        DATA(lt_celebrity_faces) = oo_result->get_celebrityfaces( ).
        MESSAGE 'Celebrities recognized successfully.' TYPE 'I'.
      CATCH /aws1/cx_rekinvalids3objectex.
        MESSAGE 'Invalid S3 object.' TYPE 'E'.
      CATCH /aws1/cx_rekinvalidparameterex.
        MESSAGE 'Invalid parameter value.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[rek.abapv1.recognizecelebrities]
  ENDMETHOD.

ENDCLASS.
