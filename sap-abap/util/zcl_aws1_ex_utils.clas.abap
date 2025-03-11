CLASS zcl_aws1_ex_utils DEFINITION
  PUBLIC
  FINAL
  CREATE PUBLIC .

  PUBLIC SECTION.

    CONSTANTS cv_asset_prefix TYPE string VALUE 'aws-example' ##NO_TEXT.

    CLASS-METHODS get_random_string
      RETURNING
      VALUE(ov_str) TYPE string .
    CLASS-METHODS cleanup_bucket
      IMPORTING
      !iv_bucket TYPE /aws1/s3_bucketname
      !io_s3 TYPE REF TO /aws1/if_s3
      RAISING
      /aws1/cx_rt_generic .
    CLASS-METHODS create_bucket
      IMPORTING
      !iv_bucket TYPE /aws1/s3_bucketname
      !io_s3 TYPE REF TO /aws1/if_s3
      !io_session TYPE REF TO /aws1/cl_rt_session_base
      RAISING
      /aws1/cx_rt_generic .
  PROTECTED SECTION.
  PRIVATE SECTION.
ENDCLASS.



CLASS ZCL_AWS1_EX_UTILS IMPLEMENTATION.


  METHOD cleanup_bucket.
    TRY.
        DATA lt_obj TYPE /aws1/cl_s3_objectidentifier=>tt_objectidentifierlist.
        LOOP AT io_s3->listobjectsv2( iv_bucket = iv_bucket )->get_contents( ) ASSIGNING FIELD-SYMBOL(<obj>).
          APPEND NEW /aws1/cl_s3_objectidentifier( iv_key = <obj>->get_key( ) ) TO lt_obj.
        ENDLOOP.
        IF lines( lt_obj ) > 0.
          io_s3->deleteobjects(
               iv_bucket                     = iv_bucket
               io_delete                     = NEW /aws1/cl_s3_delete( it_objects = lt_obj ) ).
        ENDIF.
        io_s3->deletebucket( iv_bucket = iv_bucket ).
      CATCH /aws1/cx_s3_nosuchbucket INTO DATA(lo_ex).
      CATCH /aws1/cx_s3_clientexc INTO DATA(lo_ex2).
        IF lo_ex2->av_err_code = 'InvalidBucketName'.
          " do nothing
        ELSE.
          RAISE EXCEPTION lo_ex2.
        ENDIF.
    ENDTRY.
  ENDMETHOD.


  METHOD create_bucket.
    " determine our region from our session
    DATA(lv_region) = CONV /aws1/s3_bucketlocationcnstrnt( io_session->get_region( ) ).
    DATA lo_constraint TYPE REF TO /aws1/cl_s3_createbucketconf.
    " When in the us-east-1 region, you must not specify a constraint
    " In all other regions, specify the region as the constraint
    IF lv_region = 'us-east-1'.
      CLEAR lo_constraint.
    ELSE.
      lo_constraint = NEW /aws1/cl_s3_createbucketconf( lv_region ).
    ENDIF.

    io_s3->createbucket(
        iv_bucket = iv_bucket
        io_createbucketconfiguration  = lo_constraint ).
  ENDMETHOD.


  METHOD get_random_string.
    CALL FUNCTION 'GENERAL_GET_RANDOM_STRING'
      EXPORTING
        number_chars  = 10
      IMPORTING
        random_string = ov_str.
  ENDMETHOD.
ENDCLASS.
