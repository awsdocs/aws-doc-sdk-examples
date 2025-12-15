" Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
" SPDX-License-Identifier: Apache-2.0
CLASS /awsex/cl_s3_actions DEFINITION
  PUBLIC
  FINAL
  CREATE PUBLIC .

  PUBLIC SECTION.

    METHODS create_bucket
      IMPORTING
                !iv_bucket_name TYPE /aws1/s3_bucketname
      RAISING   /aws1/cx_rt_generic.
    METHODS put_object
      IMPORTING
                !iv_bucket_name TYPE /aws1/s3_bucketname
                !iv_file_name   TYPE /aws1/s3_objectkey
      RAISING   /aws1/cx_rt_generic.
    METHODS get_object
      IMPORTING
                !iv_bucket_name TYPE /aws1/s3_bucketname
                !iv_object_key  TYPE /aws1/s3_objectkey
      EXPORTING
                !oo_result      TYPE REF TO /aws1/cl_s3_getobjectoutput
      RAISING   /aws1/cx_rt_generic.
    METHODS copy_object
      IMPORTING
                !iv_dest_bucket TYPE /aws1/s3_bucketname
                !iv_dest_object TYPE /aws1/s3_objectkey
                !iv_src_bucket  TYPE /aws1/s3_bucketname
                !iv_src_object  TYPE /aws1/s3_objectkey
      RAISING   /aws1/cx_rt_generic.
    METHODS list_objects
      IMPORTING
                !iv_bucket_name TYPE /aws1/s3_bucketname
      EXPORTING
                !oo_result      TYPE REF TO /aws1/cl_s3_listobjectsoutput
      RAISING   /aws1/cx_rt_generic.
    METHODS delete_object
      IMPORTING
                !iv_bucket_name TYPE /aws1/s3_bucketname
                !iv_object_key  TYPE /aws1/s3_objectkey
      RAISING   /aws1/cx_rt_generic.
    METHODS delete_bucket
      IMPORTING
                !iv_bucket_name TYPE /aws1/s3_bucketname
      RAISING   /aws1/cx_rt_generic.
    METHODS list_objects_v2
      IMPORTING
                !iv_bucket_name TYPE /aws1/s3_bucketname
      EXPORTING
                !oo_result      TYPE REF TO /aws1/cl_s3_listobjsv2output
      RAISING   /aws1/cx_rt_generic.
    METHODS delete_objects
      IMPORTING
                !iv_bucket_name TYPE /aws1/s3_bucketname
                !it_object_keys TYPE /aws1/cl_s3_objectidentifier=>tt_objectidentifierlist
      EXPORTING
                !oo_result      TYPE REF TO /aws1/cl_s3_deleteobjsoutput
      RAISING   /aws1/cx_rt_generic.
    METHODS get_bucket_acl
      IMPORTING
                !iv_bucket_name TYPE /aws1/s3_bucketname
      EXPORTING
                !oo_result      TYPE REF TO /aws1/cl_s3_getbucketacloutput
      RAISING   /aws1/cx_rt_generic.
    METHODS put_bucket_acl
      IMPORTING
                !iv_bucket_name TYPE /aws1/s3_bucketname
                !iv_grantwrite  TYPE /aws1/s3_grantwrite
      RAISING   /aws1/cx_rt_generic.
    METHODS get_bucket_cors
      IMPORTING
                !iv_bucket_name TYPE /aws1/s3_bucketname
      EXPORTING
                !oo_result      TYPE REF TO /aws1/cl_s3_getbktcorsoutput
      RAISING   /aws1/cx_rt_generic.
    METHODS put_bucket_cors
      IMPORTING
                !iv_bucket_name TYPE /aws1/s3_bucketname
                !it_cors_rules  TYPE /aws1/cl_s3_corsrule=>tt_corsrules
      RAISING   /aws1/cx_rt_generic.
    METHODS delete_bucket_cors
      IMPORTING
                !iv_bucket_name TYPE /aws1/s3_bucketname
      RAISING   /aws1/cx_rt_generic.
    METHODS get_bucket_policy
      IMPORTING
                !iv_bucket_name TYPE /aws1/s3_bucketname
      EXPORTING
                !oo_result      TYPE REF TO /aws1/cl_s3_getbktpolicyoutput
      RAISING   /aws1/cx_rt_generic.
    METHODS put_bucket_policy
      IMPORTING
                !iv_bucket_name TYPE /aws1/s3_bucketname
                !iv_policy      TYPE /aws1/s3_policy
      RAISING   /aws1/cx_rt_generic.
    METHODS delete_bucket_policy
      IMPORTING
                !iv_bucket_name TYPE /aws1/s3_bucketname
      RAISING   /aws1/cx_rt_generic.
    METHODS get_bucket_lifecycle_conf
      IMPORTING
                !iv_bucket_name TYPE /aws1/s3_bucketname
      EXPORTING
                !oo_result      TYPE REF TO /aws1/cl_s3_getbktlcconfoutput
      RAISING   /aws1/cx_rt_generic.
    METHODS put_bucket_lifecycle_conf
      IMPORTING
                !iv_bucket_name    TYPE /aws1/s3_bucketname
                !it_lifecycle_rule TYPE /aws1/cl_s3_lifecyclerule=>tt_lifecyclerules
      RAISING   /aws1/cx_rt_generic.
    METHODS delete_bucket_lifecycle
      IMPORTING
                !iv_bucket_name TYPE /aws1/s3_bucketname
      RAISING   /aws1/cx_rt_generic.
    METHODS get_object_acl
      IMPORTING
                !iv_bucket_name TYPE /aws1/s3_bucketname
                !iv_object_key  TYPE /aws1/s3_objectkey
      EXPORTING
                !oo_result      TYPE REF TO /aws1/cl_s3_getobjectacloutput
      RAISING   /aws1/cx_rt_generic.
    METHODS put_object_acl
      IMPORTING
                !iv_bucket_name TYPE /aws1/s3_bucketname
                !iv_object_key  TYPE /aws1/s3_objectkey
                !iv_grantre TYPE /aws1/s3_grantread
      RAISING   /aws1/cx_rt_generic.
    METHODS get_object_legal_hold
      IMPORTING
                !iv_bucket_name TYPE /aws1/s3_bucketname
                !iv_object_key  TYPE /aws1/s3_objectkey
      EXPORTING
                !oo_result      TYPE REF TO /aws1/cl_s3_getobjlegalholdout
      RAISING   /aws1/cx_rt_generic.
    METHODS put_object_legal_hold
      IMPORTING
                !iv_bucket_name TYPE /aws1/s3_bucketname
                !iv_object_key  TYPE /aws1/s3_objectkey
                !iv_status      TYPE /aws1/s3_objlocklegalholdstat
      RAISING   /aws1/cx_rt_generic.
    METHODS put_object_retention
      IMPORTING
                !iv_bucket_name TYPE /aws1/s3_bucketname
                !iv_object_key  TYPE /aws1/s3_objectkey
                !iv_mode        TYPE /aws1/s3_objectlockmode
                !iv_retain_date TYPE /aws1/s3_objlockrtnuntildate
      RAISING   /aws1/cx_rt_generic.
    METHODS get_object_lock_conf
      IMPORTING
                !iv_bucket_name TYPE /aws1/s3_bucketname
      EXPORTING
                !oo_result      TYPE REF TO /aws1/cl_s3_getobjlockconfout
      RAISING   /aws1/cx_rt_generic.
    METHODS put_object_lock_conf
      IMPORTING
                !iv_bucket_name TYPE /aws1/s3_bucketname
                !iv_enabled     TYPE /aws1/s3_objectlockenabled
      RAISING   /aws1/cx_rt_generic.
    METHODS put_bucket_versioning
      IMPORTING
                !iv_bucket_name TYPE /aws1/s3_bucketname
                !iv_status      TYPE /aws1/s3_bucketvrsingstatus
      RAISING   /aws1/cx_rt_generic.
    METHODS list_object_versions
      IMPORTING
                !iv_bucket_name TYPE /aws1/s3_bucketname
                !iv_prefix      TYPE /aws1/s3_prefix OPTIONAL
      EXPORTING
                !oo_result      TYPE REF TO /aws1/cl_s3_listobjvrssoutput
      RAISING   /aws1/cx_rt_generic.
    METHODS head_bucket
      IMPORTING
                !iv_bucket_name TYPE /aws1/s3_bucketname
      EXPORTING
                !oo_result      TYPE REF TO /aws1/cl_s3_headbucketoutput
      RAISING   /aws1/cx_rt_generic.
  PROTECTED SECTION.
  PRIVATE SECTION.
ENDCLASS.



CLASS /AWSEX/CL_S3_ACTIONS IMPLEMENTATION.


  METHOD copy_object.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_s3) = /aws1/cl_s3_factory=>create( lo_session ).

    "snippet-start:[s3.abapv1.copy_object]
    TRY.
        lo_s3->copyobject(
          iv_bucket = iv_dest_bucket
          iv_key = iv_dest_object
          iv_copysource = |{ iv_src_bucket }/{ iv_src_object }| ).
        MESSAGE 'Object copied to another bucket.' TYPE 'I'.
      CATCH /aws1/cx_s3_nosuchbucket.
        MESSAGE 'Bucket does not exist.' TYPE 'E'.
      CATCH /aws1/cx_s3_nosuchkey.
        MESSAGE 'Object key does not exist.' TYPE 'E'.
    ENDTRY.
    "snippet-end:[s3.abapv1.copy_object]
  ENDMETHOD.


  METHOD create_bucket.

    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_s3) = /aws1/cl_s3_factory=>create( lo_session ).

    " snippet-start:[s3.abapv1.create_bucket]
    TRY.
        " determine our region from our session
        DATA(lv_region) = CONV /aws1/s3_bucketlocationcnstrnt( lo_session->get_region( ) ).
        DATA lo_constraint TYPE REF TO /aws1/cl_s3_createbucketconf.
        " When in the us-east-1 region, you must not specify a constraint
        " In all other regions, specify the region as the constraint
        IF lv_region = 'us-east-1'.
          CLEAR lo_constraint.
        ELSE.
          lo_constraint = NEW /aws1/cl_s3_createbucketconf( lv_region ).
        ENDIF.

        lo_s3->createbucket(
            iv_bucket = iv_bucket_name
            io_createbucketconfiguration  = lo_constraint ).
        MESSAGE 'S3 bucket created.' TYPE 'I'.
      CATCH /aws1/cx_s3_bucketalrdyexists.
        MESSAGE 'Bucket name already exists.' TYPE 'E'.
      CATCH /aws1/cx_s3_bktalrdyownedbyyou.
        MESSAGE 'Bucket already exists and is owned by you.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[s3.abapv1.create_bucket]
  ENDMETHOD.


  METHOD delete_bucket.

    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_s3) = /aws1/cl_s3_factory=>create( lo_session ).

    "snippet-start:[s3.abapv1.delete_bucket]
    TRY.

        lo_s3->deletebucket(
            iv_bucket = iv_bucket_name ).
        MESSAGE 'Deleted S3 bucket.' TYPE 'I'.
      CATCH /aws1/cx_s3_nosuchbucket.
        MESSAGE 'Bucket does not exist.' TYPE 'E'.
    ENDTRY.
    "snippet-end:[s3.abapv1.delete_bucket]

  ENDMETHOD.


  METHOD delete_object.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_s3) = /aws1/cl_s3_factory=>create( lo_session ).

    "snippet-start:[s3.abapv1.delete_object]
    TRY.
        lo_s3->deleteobject(
            iv_bucket = iv_bucket_name
            iv_key = iv_object_key ).
        MESSAGE 'Object deleted from S3 bucket.' TYPE 'I'.
      CATCH /aws1/cx_s3_nosuchbucket.
        MESSAGE 'Bucket does not exist.' TYPE 'E'.
    ENDTRY.
    "snippet-end:[s3.abapv1.delete_object]
  ENDMETHOD.


  METHOD get_object.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_s3) = /aws1/cl_s3_factory=>create( lo_session ).

    "snippet-start:[s3.abapv1.get_object]
    TRY.
        oo_result = lo_s3->getobject(           " oo_result is returned for testing purposes. "
                  iv_bucket = iv_bucket_name
                  iv_key = iv_object_key ).
        DATA(lv_object_data) = oo_result->get_body( ).
        MESSAGE 'Object retrieved from S3 bucket.' TYPE 'I'.
      CATCH /aws1/cx_s3_nosuchbucket.
        MESSAGE 'Bucket does not exist.' TYPE 'E'.
      CATCH /aws1/cx_s3_nosuchkey.
        MESSAGE 'Object key does not exist.' TYPE 'E'.
    ENDTRY.
    "snippet-end:[s3.abapv1.get_object]

  ENDMETHOD.


  METHOD list_objects.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_s3) = /aws1/cl_s3_factory=>create( lo_session ).

    "snippet-start:[s3.abapv1.list_objects]
    TRY.
        oo_result = lo_s3->listobjects(         " oo_result is returned for testing purposes. "
          iv_bucket = iv_bucket_name ).
        MESSAGE 'Retrieved list of objects in S3 bucket.' TYPE 'I'.
      CATCH /aws1/cx_s3_nosuchbucket.
        MESSAGE 'Bucket does not exist.' TYPE 'E'.
    ENDTRY.
    "snippet-end:[s3.abapv1.list_objects]
  ENDMETHOD.


  METHOD list_objects_v2.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_s3) = /aws1/cl_s3_factory=>create( lo_session ).

    "snippet-start:[s3.abapv1.list_objects_v2]
    TRY.
        oo_result = lo_s3->listobjectsv2(         " oo_result is returned for testing purposes. "
          iv_bucket = iv_bucket_name ).
        MESSAGE 'Retrieved list of objects in S3 bucket.' TYPE 'I'.
      CATCH /aws1/cx_s3_nosuchbucket.
        MESSAGE 'Bucket does not exist.' TYPE 'E'.
    ENDTRY.
    "snippet-end:[s3.abapv1.list_objects_v2]
  ENDMETHOD.


  METHOD put_object.

    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_s3) = /aws1/cl_s3_factory=>create( lo_session ).

    "snippet-start:[s3.abapv1.put_object]

    "Get contents of file from application server."
    DATA lv_body TYPE xstring.
    OPEN DATASET iv_file_name FOR INPUT IN BINARY MODE.
    READ DATASET iv_file_name INTO lv_body.
    CLOSE DATASET iv_file_name.

    "Upload/put an object to an S3 bucket."
    TRY.
        lo_s3->putobject(
            iv_bucket = iv_bucket_name
            iv_key = iv_file_name
            iv_body = lv_body ).
        MESSAGE 'Object uploaded to S3 bucket.' TYPE 'I'.
      CATCH /aws1/cx_s3_nosuchbucket.
        MESSAGE 'Bucket does not exist.' TYPE 'E'.
    ENDTRY.

    "snippet-end:[s3.abapv1.put_object]
  ENDMETHOD.


  METHOD delete_objects.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_s3) = /aws1/cl_s3_factory=>create( lo_session ).

    " snippet-start:[s3.abapv1.delete_objects]
    TRY.
        oo_result = lo_s3->deleteobjects(         " oo_result is returned for testing purposes. "
          iv_bucket = iv_bucket_name
          io_delete = NEW /aws1/cl_s3_delete( it_objects = it_object_keys ) ).
        MESSAGE 'Objects deleted from S3 bucket.' TYPE 'I'.
      CATCH /aws1/cx_s3_nosuchbucket.
        MESSAGE 'Bucket does not exist.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[s3.abapv1.delete_objects]
  ENDMETHOD.


  METHOD get_bucket_acl.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_s3) = /aws1/cl_s3_factory=>create( lo_session ).

    " snippet-start:[s3.abapv1.get_bucket_acl]
    TRY.
        oo_result = lo_s3->getbucketacl(         " oo_result is returned for testing purposes. "
          iv_bucket = iv_bucket_name ).
        MESSAGE 'Retrieved bucket ACL.' TYPE 'I'.
      CATCH /aws1/cx_s3_nosuchbucket.
        MESSAGE 'Bucket does not exist.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[s3.abapv1.get_bucket_acl]
  ENDMETHOD.


  METHOD put_bucket_acl.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_s3) = /aws1/cl_s3_factory=>create( lo_session ).

    " snippet-start:[s3.abapv1.put_bucket_acl]
    TRY.
        " Example: Grant log delivery access to a bucket
        " iv_grantwrite = 'uri=http://acs.amazonaws.com/groups/s3/LogDelivery'
        lo_s3->putbucketacl(
          iv_bucket = iv_bucket_name
          iv_grantwrite = iv_grantwrite ).
        MESSAGE 'Bucket ACL updated.' TYPE 'I'.
      CATCH /aws1/cx_s3_nosuchbucket.
        MESSAGE 'Bucket does not exist.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[s3.abapv1.put_bucket_acl]
  ENDMETHOD.


  METHOD get_bucket_cors.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_s3) = /aws1/cl_s3_factory=>create( lo_session ).

    " snippet-start:[s3.abapv1.get_bucket_cors]
    TRY.
        oo_result = lo_s3->getbucketcors(         " oo_result is returned for testing purposes. "
          iv_bucket = iv_bucket_name ).
        MESSAGE 'Retrieved bucket CORS configuration.' TYPE 'I'.
      CATCH /aws1/cx_s3_nosuchbucket.
        MESSAGE 'Bucket does not exist.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[s3.abapv1.get_bucket_cors]
  ENDMETHOD.


  METHOD put_bucket_cors.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_s3) = /aws1/cl_s3_factory=>create( lo_session ).

    " snippet-start:[s3.abapv1.put_bucket_cors]
    TRY.
        " Example: Allow PUT, POST, DELETE methods from http://www.example.com
        lo_s3->putbucketcors(
          iv_bucket = iv_bucket_name
          io_corsconfiguration = NEW /aws1/cl_s3_corsconfiguration(
            it_corsrules = it_cors_rules ) ).
        MESSAGE 'Bucket CORS configuration set.' TYPE 'I'.
      CATCH /aws1/cx_s3_nosuchbucket.
        MESSAGE 'Bucket does not exist.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[s3.abapv1.put_bucket_cors]
  ENDMETHOD.


  METHOD delete_bucket_cors.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_s3) = /aws1/cl_s3_factory=>create( lo_session ).

    " snippet-start:[s3.abapv1.delete_bucket_cors]
    TRY.
        lo_s3->deletebucketcors(
          iv_bucket = iv_bucket_name ).
        MESSAGE 'Bucket CORS configuration deleted.' TYPE 'I'.
      CATCH /aws1/cx_s3_nosuchbucket.
        MESSAGE 'Bucket does not exist.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[s3.abapv1.delete_bucket_cors]
  ENDMETHOD.


  METHOD get_bucket_policy.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_s3) = /aws1/cl_s3_factory=>create( lo_session ).

    " snippet-start:[s3.abapv1.get_bucket_policy]
    TRY.
        oo_result = lo_s3->getbucketpolicy(         " oo_result is returned for testing purposes. "
          iv_bucket = iv_bucket_name ).
        DATA(lv_policy) = oo_result->get_policy( ).
        MESSAGE 'Retrieved bucket policy.' TYPE 'I'.
      CATCH /aws1/cx_s3_nosuchbucket.
        MESSAGE 'Bucket does not exist.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[s3.abapv1.get_bucket_policy]
  ENDMETHOD.


  METHOD put_bucket_policy.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_s3) = /aws1/cl_s3_factory=>create( lo_session ).

    " snippet-start:[s3.abapv1.put_bucket_policy]
    TRY.
        " Example policy JSON string
        " iv_policy = '{"Version":"2012-10-17","Statement":[{"Effect":"Allow","Principal":{"AWS":"arn:aws:iam::123456789012:user/user"},"Action":["s3:GetObject"],"Resource":["arn:aws:s3:::bucketname/*"]}]}'
        lo_s3->putbucketpolicy(
          iv_bucket = iv_bucket_name
          iv_policy = iv_policy ).
        MESSAGE 'Bucket policy set.' TYPE 'I'.
      CATCH /aws1/cx_s3_nosuchbucket.
        MESSAGE 'Bucket does not exist.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[s3.abapv1.put_bucket_policy]
  ENDMETHOD.


  METHOD delete_bucket_policy.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_s3) = /aws1/cl_s3_factory=>create( lo_session ).

    " snippet-start:[s3.abapv1.delete_bucket_policy]
    TRY.
        lo_s3->deletebucketpolicy(
          iv_bucket = iv_bucket_name ).
        MESSAGE 'Bucket policy deleted.' TYPE 'I'.
      CATCH /aws1/cx_s3_nosuchbucket.
        MESSAGE 'Bucket does not exist.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[s3.abapv1.delete_bucket_policy]
  ENDMETHOD.


  METHOD get_bucket_lifecycle_conf.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_s3) = /aws1/cl_s3_factory=>create( lo_session ).

    " snippet-start:[s3.abapv1.get_bucket_lifecycle_configuration]
    TRY.
        oo_result = lo_s3->getbucketlifecycleconf(         " oo_result is returned for testing purposes. "
          iv_bucket = iv_bucket_name ).
        MESSAGE 'Retrieved bucket lifecycle configuration.' TYPE 'I'.
      CATCH /aws1/cx_s3_nosuchbucket.
        MESSAGE 'Bucket does not exist.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[s3.abapv1.get_bucket_lifecycle_configuration]
  ENDMETHOD.


  METHOD put_bucket_lifecycle_conf.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_s3) = /aws1/cl_s3_factory=>create( lo_session ).

    " snippet-start:[s3.abapv1.put_bucket_lifecycle_configuration]
    TRY.
        " Example: Expire objects with prefix 'logs/' after 30 days
        lo_s3->putbucketlifecycleconf(
          iv_bucket = iv_bucket_name
          io_lifecycleconfiguration = NEW /aws1/cl_s3_bucketlcconf(
            it_rules = it_lifecycle_rule ) ).
        MESSAGE 'Bucket lifecycle configuration set.' TYPE 'I'.
      CATCH /aws1/cx_s3_nosuchbucket.
        MESSAGE 'Bucket does not exist.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[s3.abapv1.put_bucket_lifecycle_configuration]
  ENDMETHOD.


  METHOD delete_bucket_lifecycle.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_s3) = /aws1/cl_s3_factory=>create( lo_session ).

    " snippet-start:[s3.abapv1.delete_bucket_lifecycle]
    TRY.
        lo_s3->deletebucketlifecycle(
          iv_bucket = iv_bucket_name ).
        MESSAGE 'Bucket lifecycle configuration deleted.' TYPE 'I'.
      CATCH /aws1/cx_s3_nosuchbucket.
        MESSAGE 'Bucket does not exist.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[s3.abapv1.delete_bucket_lifecycle]
  ENDMETHOD.


  METHOD get_object_acl.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_s3) = /aws1/cl_s3_factory=>create( lo_session ).

    " snippet-start:[s3.abapv1.get_object_acl]
    TRY.
        oo_result = lo_s3->getobjectacl(         " oo_result is returned for testing purposes. "
          iv_bucket = iv_bucket_name
          iv_key = iv_object_key ).
        MESSAGE 'Retrieved object ACL.' TYPE 'I'.
      CATCH /aws1/cx_s3_nosuchbucket.
        MESSAGE 'Bucket does not exist.' TYPE 'E'.
      CATCH /aws1/cx_s3_nosuchkey.
        MESSAGE 'Object key does not exist.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[s3.abapv1.get_object_acl]
  ENDMETHOD.


  METHOD put_object_acl.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_s3) = /aws1/cl_s3_factory=>create( lo_session ).

    " snippet-start:[s3.abapv1.put_object_acl]
    TRY.
        " Example: Grant read access to an AWS user
        " iv_grantread = 'emailAddress=user@example.com'
        lo_s3->putobjectacl(
          iv_bucket = iv_bucket_name
          iv_key = iv_object_key
          iv_grantread = iv_grantread ).
        MESSAGE 'Object ACL updated.' TYPE 'I'.
      CATCH /aws1/cx_s3_nosuchbucket.
        MESSAGE 'Bucket does not exist.' TYPE 'E'.
      CATCH /aws1/cx_s3_nosuchkey.
        MESSAGE 'Object key does not exist.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[s3.abapv1.put_object_acl]
  ENDMETHOD.


  METHOD get_object_legal_hold.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_s3) = /aws1/cl_s3_factory=>create( lo_session ).

    " snippet-start:[s3.abapv1.get_object_legal_hold]
    TRY.
        oo_result = lo_s3->getobjectlegalhold(         " oo_result is returned for testing purposes. "
          iv_bucket = iv_bucket_name
          iv_key = iv_object_key ).
        MESSAGE 'Retrieved object legal hold status.' TYPE 'I'.
      CATCH /aws1/cx_s3_nosuchbucket.
        MESSAGE 'Bucket does not exist.' TYPE 'E'.
      CATCH /aws1/cx_s3_nosuchkey.
        MESSAGE 'Object key does not exist.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[s3.abapv1.get_object_legal_hold]
  ENDMETHOD.


  METHOD put_object_legal_hold.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_s3) = /aws1/cl_s3_factory=>create( lo_session ).

    " snippet-start:[s3.abapv1.put_object_legal_hold]
    TRY.
        " Example: Set legal hold status to ON
        " iv_status = 'ON'
        lo_s3->putobjectlegalhold(
          iv_bucket = iv_bucket_name
          iv_key = iv_object_key
          io_legalhold = NEW /aws1/cl_s3_objlocklegalhold(
            iv_status = iv_status ) ).
        MESSAGE 'Object legal hold status set.' TYPE 'I'.
      CATCH /aws1/cx_s3_nosuchbucket.
        MESSAGE 'Bucket does not exist.' TYPE 'E'.
      CATCH /aws1/cx_s3_nosuchkey.
        MESSAGE 'Object key does not exist.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[s3.abapv1.put_object_legal_hold]
  ENDMETHOD.


  METHOD put_object_retention.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_s3) = /aws1/cl_s3_factory=>create( lo_session ).

    " snippet-start:[s3.abapv1.put_object_retention]
    TRY.
        " Example: Set retention mode to GOVERNANCE for 30 days
        " iv_mode = 'GOVERNANCE'
        " iv_retain_date should be a timestamp in the future
        lo_s3->putobjectretention(
          iv_bucket = iv_bucket_name
          iv_key = iv_object_key
          io_retention = NEW /aws1/cl_s3_objectlockret(
            iv_mode = iv_mode
            iv_retainuntildate = iv_retain_date )
          iv_bypassgovernanceretention = abap_true ).
        MESSAGE 'Object retention set.' TYPE 'I'.
      CATCH /aws1/cx_s3_nosuchbucket.
        MESSAGE 'Bucket does not exist.' TYPE 'E'.
      CATCH /aws1/cx_s3_nosuchkey.
        MESSAGE 'Object key does not exist.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[s3.abapv1.put_object_retention]
  ENDMETHOD.


  METHOD get_object_lock_conf.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_s3) = /aws1/cl_s3_factory=>create( lo_session ).

    " snippet-start:[s3.abapv1.get_object_lock_configuration]
    TRY.
        oo_result = lo_s3->getobjectlockconfiguration(         " oo_result is returned for testing purposes. "
          iv_bucket = iv_bucket_name ).
        MESSAGE 'Retrieved object lock configuration.' TYPE 'I'.
      CATCH /aws1/cx_s3_nosuchbucket.
        MESSAGE 'Bucket does not exist.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[s3.abapv1.get_object_lock_configuration]
  ENDMETHOD.


  METHOD put_object_lock_conf.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_s3) = /aws1/cl_s3_factory=>create( lo_session ).

    " snippet-start:[s3.abapv1.put_object_lock_configuration]
    TRY.
        " Example: Enable object lock with default retention
        " iv_enabled = 'Enabled'
        lo_s3->putobjectlockconfiguration(
          iv_bucket = iv_bucket_name
          io_objectlockconfiguration = NEW /aws1/cl_s3_objectlockconf(
            iv_objectlockenabled = iv_enabled ) ).
        MESSAGE 'Object lock configuration set.' TYPE 'I'.
      CATCH /aws1/cx_s3_nosuchbucket.
        MESSAGE 'Bucket does not exist.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[s3.abapv1.put_object_lock_configuration]
  ENDMETHOD.


  METHOD put_bucket_versioning.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_s3) = /aws1/cl_s3_factory=>create( lo_session ).

    " snippet-start:[s3.abapv1.put_bucket_versioning]
    TRY.
        " Example: Enable versioning on a bucket
        " iv_status = 'Enabled'
        lo_s3->putbucketversioning(
          iv_bucket = iv_bucket_name
          io_versioningconfiguration = NEW /aws1/cl_s3_versioningconf(
            iv_status = iv_status ) ).
        MESSAGE 'Bucket versioning enabled.' TYPE 'I'.
      CATCH /aws1/cx_s3_nosuchbucket.
        MESSAGE 'Bucket does not exist.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[s3.abapv1.put_bucket_versioning]
  ENDMETHOD.


  METHOD list_object_versions.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_s3) = /aws1/cl_s3_factory=>create( lo_session ).

    " snippet-start:[s3.abapv1.list_object_versions]
    TRY.
        oo_result = lo_s3->listobjectversions(         " oo_result is returned for testing purposes. "
          iv_bucket = iv_bucket_name
          iv_prefix = iv_prefix ).
        MESSAGE 'Retrieved object versions.' TYPE 'I'.
      CATCH /aws1/cx_s3_nosuchbucket.
        MESSAGE 'Bucket does not exist.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[s3.abapv1.list_object_versions]
  ENDMETHOD.


  METHOD head_bucket.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_s3) = /aws1/cl_s3_factory=>create( lo_session ).

    " snippet-start:[s3.abapv1.head_bucket]
    TRY.
        oo_result = lo_s3->headbucket(         " oo_result is returned for testing purposes. "
          iv_bucket = iv_bucket_name ).
        MESSAGE 'Bucket exists and you have access to it.' TYPE 'I'.
      CATCH /aws1/cx_s3_nosuchbucket.
        MESSAGE 'Bucket does not exist.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[s3.abapv1.head_bucket]
  ENDMETHOD.
ENDCLASS.
