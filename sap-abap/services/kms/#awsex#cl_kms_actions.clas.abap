" Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
" SPDX-License-Identifier: Apache-2.0
CLASS /awsex/cl_kms_actions DEFINITION
  PUBLIC
  FINAL
  CREATE PUBLIC .

  PUBLIC SECTION.

    METHODS create_key
      IMPORTING
        !iv_description   TYPE /aws1/kmsdescriptiontype
      EXPORTING
        !oo_result        TYPE REF TO /aws1/cl_kmscreatekeyresponse
      RAISING
        /aws1/cx_rt_generic.

    METHODS create_asymmetric_key
      EXPORTING
        !oo_result TYPE REF TO /aws1/cl_kmscreatekeyresponse
      RAISING
        /aws1/cx_rt_generic.

    METHODS list_keys
      EXPORTING
        !oo_result TYPE REF TO /aws1/cl_kmslistkeysresponse
      RAISING
        /aws1/cx_rt_generic.

    METHODS describe_key
      IMPORTING
        !iv_key_id TYPE /aws1/kmskeyidtype
      EXPORTING
        !oo_result TYPE REF TO /aws1/cl_kmsdescrkeyresponse
      RAISING
        /aws1/cx_rt_generic.

    METHODS generate_data_key
      IMPORTING
        !iv_key_id TYPE /aws1/kmskeyidtype
      EXPORTING
        !oo_result TYPE REF TO /aws1/cl_kmsgeneratedatakeyrsp
      RAISING
        /aws1/cx_rt_generic.

    METHODS enable_key
      IMPORTING
        !iv_key_id TYPE /aws1/kmskeyidtype
      RAISING
        /aws1/cx_rt_generic.

    METHODS disable_key
      IMPORTING
        !iv_key_id TYPE /aws1/kmskeyidtype
      RAISING
        /aws1/cx_rt_generic.

    METHODS schedule_key_deletion
      IMPORTING
        !iv_key_id              TYPE /aws1/kmskeyidtype
        !iv_pending_window_days TYPE /aws1/kmspendingwindowindays00
      EXPORTING
        !oo_result              TYPE REF TO /aws1/cl_kmsschdkeydeletionrsp
      RAISING
        /aws1/cx_rt_generic.

    METHODS enable_key_rotation
      IMPORTING
        !iv_key_id TYPE /aws1/kmskeyidtype
      RAISING
        /aws1/cx_rt_generic.

    METHODS tag_resource
      IMPORTING
        !iv_key_id    TYPE /aws1/kmskeyidtype
        !iv_tag_key   TYPE /aws1/kmstagkeytype
        !iv_tag_value TYPE /aws1/kmstagvaluetype
      RAISING
        /aws1/cx_rt_generic.

    METHODS create_alias
      IMPORTING
        !iv_key_id     TYPE /aws1/kmskeyidtype
        !iv_alias_name TYPE /aws1/kmsaliasnametype
      RAISING
        /aws1/cx_rt_generic.

    METHODS list_aliases
      EXPORTING
        !oo_result TYPE REF TO /aws1/cl_kmslistaliasesrsp
      RAISING
        /aws1/cx_rt_generic.

    METHODS update_alias
      IMPORTING
        !iv_alias_name     TYPE /aws1/kmsaliasnametype
        !iv_target_key_id  TYPE /aws1/kmskeyidtype
      RAISING
        /aws1/cx_rt_generic.

    METHODS delete_alias
      IMPORTING
        !iv_alias_name TYPE /aws1/kmsaliasnametype
      RAISING
        /aws1/cx_rt_generic.

    METHODS create_grant
      IMPORTING
        !iv_key_id            TYPE /aws1/kmskeyidtype
        !iv_grantee_principal TYPE /aws1/kmsprincipalidtype
        !it_operations        TYPE /aws1/cl_kmsgrantoplist_w=>tt_grantoperationlist
      EXPORTING
        !oo_result            TYPE REF TO /aws1/cl_kmscreategrantrsp
      RAISING
        /aws1/cx_rt_generic.

    METHODS list_grants
      IMPORTING
        !iv_key_id TYPE /aws1/kmskeyidtype
      EXPORTING
        !oo_result TYPE REF TO /aws1/cl_kmslistgrantsresponse
      RAISING
        /aws1/cx_rt_generic.

    METHODS retire_grant
      IMPORTING
        !iv_grant_token TYPE /aws1/kmsgranttokentype
      RAISING
        /aws1/cx_rt_generic.

    METHODS revoke_grant
      IMPORTING
        !iv_key_id   TYPE /aws1/kmskeyidtype
        !iv_grant_id TYPE /aws1/kmsgrantidtype
      RAISING
        /aws1/cx_rt_generic.

    METHODS get_key_policy
      IMPORTING
        !iv_key_id TYPE /aws1/kmskeyidtype
      EXPORTING
        !oo_result TYPE REF TO /aws1/cl_kmsgetkeypolicyrsp
      RAISING
        /aws1/cx_rt_generic.

    METHODS put_key_policy
      IMPORTING
        !iv_key_id TYPE /aws1/kmskeyidtype
        !iv_policy TYPE /aws1/kmspolicytype
      RAISING
        /aws1/cx_rt_generic.

    METHODS list_key_policies
      IMPORTING
        !iv_key_id TYPE /aws1/kmskeyidtype
      EXPORTING
        !oo_result TYPE REF TO /aws1/cl_kmslistkeypolresponse
      RAISING
        /aws1/cx_rt_generic.

    METHODS encrypt
      IMPORTING
        !iv_key_id    TYPE /aws1/kmskeyidtype
        !iv_plaintext TYPE /aws1/kmsplaintexttype
      EXPORTING
        !oo_result    TYPE REF TO /aws1/cl_kmsencryptresponse
      RAISING
        /aws1/cx_rt_generic.

    METHODS decrypt
      IMPORTING
        !iv_key_id         TYPE /aws1/kmskeyidtype
        !iv_ciphertext_blob TYPE /aws1/kmsciphertexttype
      EXPORTING
        !oo_result         TYPE REF TO /aws1/cl_kmsdecryptresponse
      RAISING
        /aws1/cx_rt_generic.

    METHODS re_encrypt
      IMPORTING
        !iv_source_key_id      TYPE /aws1/kmskeyidtype
        !iv_destination_key_id TYPE /aws1/kmskeyidtype
        !iv_ciphertext_blob    TYPE /aws1/kmsciphertexttype
      EXPORTING
        !oo_result             TYPE REF TO /aws1/cl_kmsreencryptresponse
      RAISING
        /aws1/cx_rt_generic.

    METHODS sign
      IMPORTING
        !iv_key_id            TYPE /aws1/kmskeyidtype
        !iv_message           TYPE /aws1/kmsplaintexttype
        !iv_signing_algorithm TYPE /aws1/kmssigningalgorithmspec
      EXPORTING
        !oo_result            TYPE REF TO /aws1/cl_kmssignresponse
      RAISING
        /aws1/cx_rt_generic.

    METHODS verify
      IMPORTING
        !iv_key_id            TYPE /aws1/kmskeyidtype
        !iv_message           TYPE /aws1/kmsplaintexttype
        !iv_signature         TYPE /aws1/kmsciphertexttype
        !iv_signing_algorithm TYPE /aws1/kmssigningalgorithmspec
      EXPORTING
        !oo_result            TYPE REF TO /aws1/cl_kmsverifyresponse
      RAISING
        /aws1/cx_rt_generic.

  PROTECTED SECTION.
  PRIVATE SECTION.
ENDCLASS.



CLASS /AWSEX/CL_KMS_ACTIONS IMPLEMENTATION.


  METHOD create_key.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_kms) = /aws1/cl_kms_factory=>create( lo_session ).

    " snippet-start:[kms.abapv1.create_key]
    TRY.
        " iv_description = 'Created by the AWS SDK for SAP ABAP'
        oo_result = lo_kms->createkey( iv_description = iv_description ).
        MESSAGE 'KMS key created successfully.' TYPE 'I'.
      CATCH /aws1/cx_kmskmsinternalex.
        MESSAGE 'An internal error occurred.' TYPE 'E'.
      CATCH /aws1/cx_kmslimitexceededex.
        MESSAGE 'Limit exceeded for KMS resources.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[kms.abapv1.create_key]
  ENDMETHOD.


  METHOD create_asymmetric_key.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_kms) = /aws1/cl_kms_factory=>create( lo_session ).

    " snippet-start:[kms.abapv1.create_asymmetric_key]
    TRY.
        " iv_keyspec = 'RSA_2048'
        " iv_keyusage = 'SIGN_VERIFY'
        " iv_origin = 'AWS_KMS'
        oo_result = lo_kms->createkey(
          iv_keyspec = 'RSA_2048'
          iv_keyusage = 'SIGN_VERIFY'
          iv_origin = 'AWS_KMS'
        ).
        MESSAGE 'Asymmetric KMS key created successfully.' TYPE 'I'.
      CATCH /aws1/cx_kmskmsinternalex.
        MESSAGE 'An internal error occurred.' TYPE 'E'.
      CATCH /aws1/cx_kmslimitexceededex.
        MESSAGE 'Limit exceeded for KMS resources.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[kms.abapv1.create_asymmetric_key]
  ENDMETHOD.


  METHOD list_keys.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_kms) = /aws1/cl_kms_factory=>create( lo_session ).

    " snippet-start:[kms.abapv1.list_keys]
    TRY.
        oo_result = lo_kms->listkeys( ).
        MESSAGE 'Retrieved KMS keys list.' TYPE 'I'.
      CATCH /aws1/cx_kmskmsinternalex.
        MESSAGE 'An internal error occurred.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[kms.abapv1.list_keys]
  ENDMETHOD.


  METHOD describe_key.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_kms) = /aws1/cl_kms_factory=>create( lo_session ).

    " snippet-start:[kms.abapv1.describe_key]
    TRY.
        " iv_key_id = 'arn:aws:kms:us-east-1:123456789012:key/1234abcd-12ab-34cd-56ef-1234567890ab'
        oo_result = lo_kms->describekey( iv_keyid = iv_key_id ).
        DATA(lo_key) = oo_result->get_keymetadata( ).
        MESSAGE 'Retrieved key information successfully.' TYPE 'I'.
      CATCH /aws1/cx_kmsnotfoundexception.
        MESSAGE 'Key not found.' TYPE 'E'.
      CATCH /aws1/cx_kmskmsinternalex.
        MESSAGE 'An internal error occurred.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[kms.abapv1.describe_key]
  ENDMETHOD.


  METHOD generate_data_key.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_kms) = /aws1/cl_kms_factory=>create( lo_session ).

    " snippet-start:[kms.abapv1.generate_data_key]
    TRY.
        " iv_key_id = 'arn:aws:kms:us-east-1:123456789012:key/1234abcd-12ab-34cd-56ef-1234567890ab'
        " iv_keyspec = 'AES_256'
        oo_result = lo_kms->generatedatakey(
          iv_keyid = iv_key_id
          iv_keyspec = 'AES_256'
        ).
        MESSAGE 'Data key generated successfully.' TYPE 'I'.
      CATCH /aws1/cx_kmsdisabledexception.
        MESSAGE 'The key is disabled.' TYPE 'E'.
      CATCH /aws1/cx_kmsnotfoundexception.
        MESSAGE 'Key not found.' TYPE 'E'.
      CATCH /aws1/cx_kmskmsinternalex.
        MESSAGE 'An internal error occurred.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[kms.abapv1.generate_data_key]
  ENDMETHOD.


  METHOD enable_key.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_kms) = /aws1/cl_kms_factory=>create( lo_session ).

    " snippet-start:[kms.abapv1.enable_key]
    TRY.
        " iv_key_id = 'arn:aws:kms:us-east-1:123456789012:key/1234abcd-12ab-34cd-56ef-1234567890ab'
        lo_kms->enablekey( iv_keyid = iv_key_id ).
        MESSAGE 'KMS key enabled successfully.' TYPE 'I'.
      CATCH /aws1/cx_kmsnotfoundexception.
        MESSAGE 'Key not found.' TYPE 'E'.
      CATCH /aws1/cx_kmskmsinternalex.
        MESSAGE 'An internal error occurred.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[kms.abapv1.enable_key]
  ENDMETHOD.


  METHOD disable_key.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_kms) = /aws1/cl_kms_factory=>create( lo_session ).

    " snippet-start:[kms.abapv1.disable_key]
    TRY.
        " iv_key_id = 'arn:aws:kms:us-east-1:123456789012:key/1234abcd-12ab-34cd-56ef-1234567890ab'
        lo_kms->disablekey( iv_keyid = iv_key_id ).
        MESSAGE 'KMS key disabled successfully.' TYPE 'I'.
      CATCH /aws1/cx_kmsnotfoundexception.
        MESSAGE 'Key not found.' TYPE 'E'.
      CATCH /aws1/cx_kmskmsinternalex.
        MESSAGE 'An internal error occurred.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[kms.abapv1.disable_key]
  ENDMETHOD.


  METHOD schedule_key_deletion.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_kms) = /aws1/cl_kms_factory=>create( lo_session ).

    " snippet-start:[kms.abapv1.schedule_key_deletion]
    TRY.
        " iv_key_id = 'arn:aws:kms:us-east-1:123456789012:key/1234abcd-12ab-34cd-56ef-1234567890ab'
        " iv_pending_window_days = 7
        oo_result = lo_kms->schedulekeydeletion(
          iv_keyid = iv_key_id
          iv_pendingwindowindays = iv_pending_window_days
        ).
        MESSAGE 'Key scheduled for deletion.' TYPE 'I'.
      CATCH /aws1/cx_kmsnotfoundexception.
        MESSAGE 'Key not found.' TYPE 'E'.
      CATCH /aws1/cx_kmskmsinternalex.
        MESSAGE 'An internal error occurred.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[kms.abapv1.schedule_key_deletion]
  ENDMETHOD.


  METHOD enable_key_rotation.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_kms) = /aws1/cl_kms_factory=>create( lo_session ).

    " snippet-start:[kms.abapv1.enable_key_rotation]
    TRY.
        " iv_key_id = 'arn:aws:kms:us-east-1:123456789012:key/1234abcd-12ab-34cd-56ef-1234567890ab'
        lo_kms->enablekeyrotation( iv_keyid = iv_key_id ).
        MESSAGE 'Key rotation enabled successfully.' TYPE 'I'.
      CATCH /aws1/cx_kmsdisabledexception.
        MESSAGE 'The key is disabled.' TYPE 'E'.
      CATCH /aws1/cx_kmsnotfoundexception.
        MESSAGE 'Key not found.' TYPE 'E'.
      CATCH /aws1/cx_kmsunsupportedopex.
        MESSAGE 'Operation not supported for this key.' TYPE 'E'.
      CATCH /aws1/cx_kmskmsinternalex.
        MESSAGE 'An internal error occurred.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[kms.abapv1.enable_key_rotation]
  ENDMETHOD.


  METHOD tag_resource.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_kms) = /aws1/cl_kms_factory=>create( lo_session ).

    " snippet-start:[kms.abapv1.tag_resource]
    DATA lt_tags TYPE /aws1/cl_kmstag=>tt_taglist.

    TRY.
        " iv_key_id = 'arn:aws:kms:us-east-1:123456789012:key/1234abcd-12ab-34cd-56ef-1234567890ab'
        " iv_tag_key = 'Environment'
        " iv_tag_value = 'Production'
        APPEND NEW /aws1/cl_kmstag(
          iv_tagkey = iv_tag_key
          iv_tagvalue = iv_tag_value
        ) TO lt_tags.

        lo_kms->tagresource(
          iv_keyid = iv_key_id
          it_tags = lt_tags
        ).
        MESSAGE 'Tag added to KMS key successfully.' TYPE 'I'.
      CATCH /aws1/cx_kmsnotfoundexception.
        MESSAGE 'Key not found.' TYPE 'E'.
      CATCH /aws1/cx_kmstagexception.
        MESSAGE 'Invalid tag format.' TYPE 'E'.
      CATCH /aws1/cx_kmskmsinternalex.
        MESSAGE 'An internal error occurred.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[kms.abapv1.tag_resource]
  ENDMETHOD.


  METHOD create_alias.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_kms) = /aws1/cl_kms_factory=>create( lo_session ).

    " snippet-start:[kms.abapv1.create_alias]
    TRY.
        " iv_alias_name = 'alias/my-key-alias'
        " iv_key_id = 'arn:aws:kms:us-east-1:123456789012:key/1234abcd-12ab-34cd-56ef-1234567890ab'
        lo_kms->createalias(
          iv_aliasname = iv_alias_name
          iv_targetkeyid = iv_key_id
        ).
        MESSAGE 'Alias created successfully.' TYPE 'I'.
      CATCH /aws1/cx_kmsalreadyexistsex.
        MESSAGE 'Alias already exists.' TYPE 'E'.
      CATCH /aws1/cx_kmsnotfoundexception.
        MESSAGE 'Key not found.' TYPE 'E'.
      CATCH /aws1/cx_kmsinvalidaliasnameex.
        MESSAGE 'Invalid alias name.' TYPE 'E'.
      CATCH /aws1/cx_kmskmsinternalex.
        MESSAGE 'An internal error occurred.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[kms.abapv1.create_alias]
  ENDMETHOD.


  METHOD list_aliases.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_kms) = /aws1/cl_kms_factory=>create( lo_session ).

    " snippet-start:[kms.abapv1.list_aliases]
    TRY.
        oo_result = lo_kms->listaliases( ).
        MESSAGE 'Retrieved KMS aliases list.' TYPE 'I'.
      CATCH /aws1/cx_kmskmsinternalex.
        MESSAGE 'An internal error occurred.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[kms.abapv1.list_aliases]
  ENDMETHOD.


  METHOD update_alias.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_kms) = /aws1/cl_kms_factory=>create( lo_session ).

    " snippet-start:[kms.abapv1.update_alias]
    TRY.
        " iv_alias_name = 'alias/my-key-alias'
        " iv_target_key_id = 'arn:aws:kms:us-east-1:123456789012:key/5678dcba-56cd-78ef-90ab-5678901234cd'
        lo_kms->updatealias(
          iv_aliasname = iv_alias_name
          iv_targetkeyid = iv_target_key_id
        ).
        MESSAGE 'Alias updated successfully.' TYPE 'I'.
      CATCH /aws1/cx_kmsnotfoundexception.
        MESSAGE 'Alias or key not found.' TYPE 'E'.
      CATCH /aws1/cx_kmskmsinternalex.
        MESSAGE 'An internal error occurred.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[kms.abapv1.update_alias]
  ENDMETHOD.


  METHOD delete_alias.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_kms) = /aws1/cl_kms_factory=>create( lo_session ).

    " snippet-start:[kms.abapv1.delete_alias]
    TRY.
        " iv_alias_name = 'alias/my-key-alias'
        lo_kms->deletealias( iv_aliasname = iv_alias_name ).
        MESSAGE 'Alias deleted successfully.' TYPE 'I'.
      CATCH /aws1/cx_kmsnotfoundexception.
        MESSAGE 'Alias not found.' TYPE 'E'.
      CATCH /aws1/cx_kmskmsinternalex.
        MESSAGE 'An internal error occurred.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[kms.abapv1.delete_alias]
  ENDMETHOD.


  METHOD create_grant.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_kms) = /aws1/cl_kms_factory=>create( lo_session ).

    " snippet-start:[kms.abapv1.create_grant]
    TRY.
        " iv_key_id = 'arn:aws:kms:us-east-1:123456789012:key/1234abcd-12ab-34cd-56ef-1234567890ab'
        " iv_grantee_principal = 'arn:aws:iam::123456789012:role/my-role'
        " it_operations contains 'Encrypt', 'Decrypt', 'GenerateDataKey'
        oo_result = lo_kms->creategrant(
          iv_keyid = iv_key_id
          iv_granteeprincipal = iv_grantee_principal
          it_operations = it_operations
        ).
        MESSAGE 'Grant created successfully.' TYPE 'I'.
      CATCH /aws1/cx_kmsdisabledexception.
        MESSAGE 'The key is disabled.' TYPE 'E'.
      CATCH /aws1/cx_kmsnotfoundexception.
        MESSAGE 'Key not found.' TYPE 'E'.
      CATCH /aws1/cx_kmskmsinternalex.
        MESSAGE 'An internal error occurred.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[kms.abapv1.create_grant]
  ENDMETHOD.


  METHOD list_grants.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_kms) = /aws1/cl_kms_factory=>create( lo_session ).

    " snippet-start:[kms.abapv1.list_grants]
    TRY.
        " iv_key_id = 'arn:aws:kms:us-east-1:123456789012:key/1234abcd-12ab-34cd-56ef-1234567890ab'
        oo_result = lo_kms->listgrants( iv_keyid = iv_key_id ).
        MESSAGE 'Retrieved grants list.' TYPE 'I'.
      CATCH /aws1/cx_kmsnotfoundexception.
        MESSAGE 'Key not found.' TYPE 'E'.
      CATCH /aws1/cx_kmskmsinternalex.
        MESSAGE 'An internal error occurred.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[kms.abapv1.list_grants]
  ENDMETHOD.


  METHOD retire_grant.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_kms) = /aws1/cl_kms_factory=>create( lo_session ).

    " snippet-start:[kms.abapv1.retire_grant]
    TRY.
        " iv_grant_token = 'AQpAM2RhZ...'
        lo_kms->retiregrant( iv_granttoken = iv_grant_token ).
        MESSAGE 'Grant retired successfully.' TYPE 'I'.
      CATCH /aws1/cx_kmsnotfoundexception.
        MESSAGE 'Grant not found.' TYPE 'E'.
      CATCH /aws1/cx_kmsinvgranttokenex.
        MESSAGE 'Invalid grant token.' TYPE 'E'.
      CATCH /aws1/cx_kmskmsinternalex.
        MESSAGE 'An internal error occurred.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[kms.abapv1.retire_grant]
  ENDMETHOD.


  METHOD revoke_grant.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_kms) = /aws1/cl_kms_factory=>create( lo_session ).

    " snippet-start:[kms.abapv1.revoke_grant]
    TRY.
        " iv_key_id = 'arn:aws:kms:us-east-1:123456789012:key/1234abcd-12ab-34cd-56ef-1234567890ab'
        " iv_grant_id = '1a2b3c4d5e6f7g8h9i0j1k2l3m4n5o6p'
        lo_kms->revokegrant(
          iv_keyid = iv_key_id
          iv_grantid = iv_grant_id
        ).
        MESSAGE 'Grant revoked successfully.' TYPE 'I'.
      CATCH /aws1/cx_kmsnotfoundexception.
        MESSAGE 'Grant or key not found.' TYPE 'E'.
      CATCH /aws1/cx_kmsinvalidgrantidex.
        MESSAGE 'Invalid grant ID.' TYPE 'E'.
      CATCH /aws1/cx_kmskmsinternalex.
        MESSAGE 'An internal error occurred.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[kms.abapv1.revoke_grant]
  ENDMETHOD.


  METHOD get_key_policy.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_kms) = /aws1/cl_kms_factory=>create( lo_session ).

    " snippet-start:[kms.abapv1.get_key_policy]
    TRY.
        " iv_key_id = 'arn:aws:kms:us-east-1:123456789012:key/1234abcd-12ab-34cd-56ef-1234567890ab'
        oo_result = lo_kms->getkeypolicy(
          iv_keyid = iv_key_id
          iv_policyname = 'default'
        ).
        MESSAGE 'Retrieved key policy successfully.' TYPE 'I'.
      CATCH /aws1/cx_kmsnotfoundexception.
        MESSAGE 'Key not found.' TYPE 'E'.
      CATCH /aws1/cx_kmskmsinternalex.
        MESSAGE 'An internal error occurred.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[kms.abapv1.get_key_policy]
  ENDMETHOD.


  METHOD put_key_policy.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_kms) = /aws1/cl_kms_factory=>create( lo_session ).

    " snippet-start:[kms.abapv1.put_key_policy]
    TRY.
        " iv_key_id = 'arn:aws:kms:us-east-1:123456789012:key/1234abcd-12ab-34cd-56ef-1234567890ab'
        " iv_policy = '{"Version": "2012-10-17", "Statement": [...]}'
        lo_kms->putkeypolicy(
          iv_keyid = iv_key_id
          iv_policyname = 'default'
          iv_policy = iv_policy
        ).
        MESSAGE 'Key policy updated successfully.' TYPE 'I'.
      CATCH /aws1/cx_kmsnotfoundexception.
        MESSAGE 'Key not found.' TYPE 'E'.
      CATCH /aws1/cx_kmsmalformedplydocex.
        MESSAGE 'Malformed policy document.' TYPE 'E'.
      CATCH /aws1/cx_kmskmsinternalex.
        MESSAGE 'An internal error occurred.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[kms.abapv1.put_key_policy]
  ENDMETHOD.


  METHOD list_key_policies.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_kms) = /aws1/cl_kms_factory=>create( lo_session ).

    " snippet-start:[kms.abapv1.list_key_policies]
    TRY.
        " iv_key_id = 'arn:aws:kms:us-east-1:123456789012:key/1234abcd-12ab-34cd-56ef-1234567890ab'
        oo_result = lo_kms->listkeypolicies( iv_keyid = iv_key_id ).
        MESSAGE 'Retrieved key policies list.' TYPE 'I'.
      CATCH /aws1/cx_kmsnotfoundexception.
        MESSAGE 'Key not found.' TYPE 'E'.
      CATCH /aws1/cx_kmskmsinternalex.
        MESSAGE 'An internal error occurred.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[kms.abapv1.list_key_policies]
  ENDMETHOD.


  METHOD encrypt.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_kms) = /aws1/cl_kms_factory=>create( lo_session ).

    " snippet-start:[kms.abapv1.encrypt]
    TRY.
        " iv_key_id = 'arn:aws:kms:us-east-1:123456789012:key/1234abcd-12ab-34cd-56ef-1234567890ab'
        " iv_plaintext contains the data to encrypt
        oo_result = lo_kms->encrypt(
          iv_keyid = iv_key_id
          iv_plaintext = iv_plaintext
        ).
        MESSAGE 'Text encrypted successfully.' TYPE 'I'.
      CATCH /aws1/cx_kmsdisabledexception.
        MESSAGE 'The key is disabled.' TYPE 'E'.
      CATCH /aws1/cx_kmsnotfoundexception.
        MESSAGE 'Key not found.' TYPE 'E'.
      CATCH /aws1/cx_kmskmsinternalex.
        MESSAGE 'An internal error occurred.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[kms.abapv1.encrypt]
  ENDMETHOD.


  METHOD decrypt.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_kms) = /aws1/cl_kms_factory=>create( lo_session ).

    " snippet-start:[kms.abapv1.decrypt]
    TRY.
        " iv_key_id = 'arn:aws:kms:us-east-1:123456789012:key/1234abcd-12ab-34cd-56ef-1234567890ab'
        " iv_ciphertext_blob contains the encrypted data
        oo_result = lo_kms->decrypt(
          iv_keyid = iv_key_id
          iv_ciphertextblob = iv_ciphertext_blob
        ).
        MESSAGE 'Text decrypted successfully.' TYPE 'I'.
      CATCH /aws1/cx_kmsdisabledexception.
        MESSAGE 'The key is disabled.' TYPE 'E'.
      CATCH /aws1/cx_kmsincorrectkeyex.
        MESSAGE 'Incorrect key for decryption.' TYPE 'E'.
      CATCH /aws1/cx_kmsnotfoundexception.
        MESSAGE 'Key not found.' TYPE 'E'.
      CATCH /aws1/cx_kmskmsinternalex.
        MESSAGE 'An internal error occurred.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[kms.abapv1.decrypt]
  ENDMETHOD.


  METHOD re_encrypt.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_kms) = /aws1/cl_kms_factory=>create( lo_session ).

    " snippet-start:[kms.abapv1.re_encrypt]
    TRY.
        " iv_source_key_id = 'arn:aws:kms:us-east-1:123456789012:key/1234abcd-12ab-34cd-56ef-1234567890ab'
        " iv_destination_key_id = 'arn:aws:kms:us-east-1:123456789012:key/5678dcba-56cd-78ef-90ab-5678901234cd'
        " iv_ciphertext_blob contains the encrypted data
        oo_result = lo_kms->reencrypt(
          iv_sourcekeyid = iv_source_key_id
          iv_destinationkeyid = iv_destination_key_id
          iv_ciphertextblob = iv_ciphertext_blob
        ).
        MESSAGE 'Ciphertext reencrypted successfully.' TYPE 'I'.
      CATCH /aws1/cx_kmsdisabledexception.
        MESSAGE 'The key is disabled.' TYPE 'E'.
      CATCH /aws1/cx_kmsincorrectkeyex.
        MESSAGE 'Incorrect source key for decryption.' TYPE 'E'.
      CATCH /aws1/cx_kmsnotfoundexception.
        MESSAGE 'Key not found.' TYPE 'E'.
      CATCH /aws1/cx_kmskmsinternalex.
        MESSAGE 'An internal error occurred.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[kms.abapv1.re_encrypt]
  ENDMETHOD.


  METHOD sign.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_kms) = /aws1/cl_kms_factory=>create( lo_session ).

    " snippet-start:[kms.abapv1.sign]
    TRY.
        " iv_key_id = 'arn:aws:kms:us-east-1:123456789012:key/1234abcd-12ab-34cd-56ef-1234567890ab' (asymmetric key)
        " iv_message contains the message to sign
        " iv_signing_algorithm = 'RSASSA_PSS_SHA_256'
        oo_result = lo_kms->sign(
          iv_keyid = iv_key_id
          iv_message = iv_message
          iv_signingalgorithm = iv_signing_algorithm
        ).
        MESSAGE 'Message signed successfully.' TYPE 'I'.
      CATCH /aws1/cx_kmsdisabledexception.
        MESSAGE 'The key is disabled.' TYPE 'E'.
      CATCH /aws1/cx_kmsnotfoundexception.
        MESSAGE 'Key not found.' TYPE 'E'.
      CATCH /aws1/cx_kmsinvalidkeyusageex.
        MESSAGE 'Key cannot be used for signing.' TYPE 'E'.
      CATCH /aws1/cx_kmskmsinternalex.
        MESSAGE 'An internal error occurred.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[kms.abapv1.sign]
  ENDMETHOD.


  METHOD verify.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_kms) = /aws1/cl_kms_factory=>create( lo_session ).

    " snippet-start:[kms.abapv1.verify]
    TRY.
        " iv_key_id = 'arn:aws:kms:us-east-1:123456789012:key/1234abcd-12ab-34cd-56ef-1234567890ab' (asymmetric key)
        " iv_message contains the original message
        " iv_signature contains the signature to verify
        " iv_signing_algorithm = 'RSASSA_PSS_SHA_256'
        oo_result = lo_kms->verify(
          iv_keyid = iv_key_id
          iv_message = iv_message
          iv_signature = iv_signature
          iv_signingalgorithm = iv_signing_algorithm
        ).
        DATA(lv_valid) = oo_result->get_signaturevalid( ).
        IF lv_valid = abap_true.
          MESSAGE 'Signature is valid.' TYPE 'I'.
        ELSE.
          MESSAGE 'Signature is invalid.' TYPE 'I'.
        ENDIF.
      CATCH /aws1/cx_kmsdisabledexception.
        MESSAGE 'The key is disabled.' TYPE 'E'.
      CATCH /aws1/cx_kmsnotfoundexception.
        MESSAGE 'Key not found.' TYPE 'E'.
      CATCH /aws1/cx_kmskmsinvalidsigex.
        MESSAGE 'Invalid signature.' TYPE 'E'.
      CATCH /aws1/cx_kmskmsinternalex.
        MESSAGE 'An internal error occurred.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[kms.abapv1.verify]
  ENDMETHOD.

ENDCLASS.
