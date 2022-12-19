" """"""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""
" "  Copyright 2022 Amazon.com, Inc. or its affiliates. All Rights
" "  Reserved.
" "  SPDX-License-Identifier: MIT-0
" """"""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""

CLASS ltc_zcl_aws1_sgm_scenario DEFINITION DEFERRED.
CLASS zcl_aws1_sgm_scenario DEFINITION LOCAL FRIENDS ltc_zcl_aws1_sgm_scenario.

CLASS ltc_zcl_aws1_sgm_scenario DEFINITION FOR TESTING DURATION LONG RISK LEVEL HARMLESS.

  PRIVATE SECTION.

    CONSTANTS: cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA ao_sgm TYPE REF TO /aws1/if_sgm.
    DATA ao_s3 TYPE REF TO /aws1/if_s3.
    DATA ao_session TYPE REF TO /aws1/cl_rt_session_base.
    DATA ao_sgm_scenario TYPE REF TO zcl_aws1_sgm_scenario.
    DATA av_lrole TYPE /aws1/sgmrolearn.

    METHODS getting_started_scenario FOR TESTING RAISING /aws1/cx_rt_generic.
    METHODS setup RAISING /aws1/cx_rt_generic ycx_aws1_mit_generic.

ENDCLASS.       "ltc_Zcl_Aws1_Sgm_Scenario


CLASS ltc_zcl_aws1_sgm_scenario IMPLEMENTATION.

  METHOD setup.
    ao_session = /aws1/cl_rt_session_aws=>create( iv_profile_id = cv_pfl ).
    ao_sgm = /aws1/cl_sgm_factory=>create( ao_session ).
    ao_s3 = /aws1/cl_s3_factory=>create( ao_session ).
    ao_sgm_scenario = NEW zcl_aws1_sgm_scenario( ).
  ENDMETHOD.

  METHOD getting_started_scenario.

    "This test case runs a training job for sales prediction using the built-in algorithm XGBoost.

    DATA lv_found TYPE abap_bool VALUE abap_false.
    DATA lv_timestamp TYPE timestamp.
    DATA lo_ep_output TYPE REF TO /aws1/cl_sgmcreateendptoutput.
    DATA lv_endpoint_name TYPE /aws1/sgmendpointname.
    DATA lv_endpoint_config_name TYPE /aws1/sgmendpointconfigname.
    DATA lv_model_name TYPE /aws1/sgmmodelname.
    DATA lv_bucket_name TYPE /aws1/s3_bucketname.
    DATA lv_file_content TYPE /aws1/s3_streamingblob.
    DATA lv_trn_data_s3uri TYPE /aws1/sgms3uri.
    DATA lv_val_data_s3uri TYPE /aws1/sgms3uri.
    DATA lv_s3_output_path TYPE /aws1/sgms3uri.
    DATA lv_model_key TYPE /aws1/s3_objectkey.
    DATA lv_uuid_16 TYPE sysuuid_x16.

    "Define job name.
    CONSTANTS cv_training_job_name TYPE /aws1/sgmtrainingjobname VALUE 'code-example-trn-job-'.

    "Define hyperparameters.
    CONSTANTS cv_hp_max_depth TYPE   /aws1/sgmhyperparametervalue    VALUE '3'.
    CONSTANTS cv_hp_scale_pos_weight TYPE   /aws1/sgmhyperparametervalue    VALUE '2.0'.
    CONSTANTS cv_hp_num_round TYPE   /aws1/sgmhyperparametervalue    VALUE '100'.
    CONSTANTS cv_hp_objective	TYPE  /aws1/sgmhyperparametervalue     VALUE 'binary:logistic'.
    CONSTANTS cv_hp_subsample TYPE   /aws1/sgmhyperparametervalue    VALUE '0.5'.
    CONSTANTS cv_hp_eta TYPE  /aws1/sgmhyperparametervalue    VALUE	 '0.1'.
    CONSTANTS cv_hp_eval_metric TYPE   /aws1/sgmhyperparametervalue    VALUE 'auc'.

    "Define training data.
    CONSTANTS cv_trn_data_s3datatype TYPE  /aws1/sgms3datatype       VALUE 'S3Prefix'.
    CONSTANTS cv_trn_data_s3datadistribution TYPE  /aws1/sgms3datadistribution        VALUE 'FullyReplicated'.
    CONSTANTS cv_trn_data_compressiontype TYPE  /aws1/sgmcompressiontype     VALUE 'None'.
    CONSTANTS cv_trn_data_contenttype TYPE   /aws1/sgmcontenttype    VALUE 'libsvm'.

    "Define validation data.
    CONSTANTS cv_val_data_s3datatype TYPE  /aws1/sgms3datatype       VALUE 'S3Prefix'.
    CONSTANTS cv_val_data_s3datadistribution TYPE  /aws1/sgms3datadistribution      VALUE 'FullyReplicated'.
    CONSTANTS cv_val_data_compressiontype TYPE   /aws1/sgmcompressiontype    VALUE 'None'.
    CONSTANTS cv_val_data_contenttype TYPE   /aws1/sgmcontenttype    VALUE 'libsvm'.

    "Define training parameters.
    "SGM public training image ref to https://docs.aws.amazon.com/sagemaker/latest/dg/ecr-us-east-1.html#xgboost-us-east-1.title
    CONSTANTS cv_training_image TYPE /aws1/sgmalgorithmimage VALUE '123456789012.abc.ecr.us-east-1.amazonaws.com/sagemaker-xgboost:1.5-1'.
    CONSTANTS cv_training_input_mode TYPE /aws1/sgmtraininginputmode VALUE  'File'.
    CONSTANTS cv_instance_count TYPE /aws1/sgmtraininginstancecount VALUE '1'.
    CONSTANTS cv_instance_type TYPE /aws1/sgmtraininginstancetype VALUE 'ml.c4.2xlarge'.
    CONSTANTS cv_volume_sizeingb TYPE /aws1/sgmvolumesizeingb VALUE '10'.
    CONSTANTS cv_max_runtime_in_seconds TYPE /aws1/sgmmaxruntimeinseconds VALUE '1800'.

    "Define model parameters.
    CONSTANTS cv_model_name TYPE /aws1/sgmmodelname VALUE 'code-example-sgm-model-'.

    "Define endpoint parameters.
    CONSTANTS cv_endpoint_name TYPE /aws1/sgmendpointname VALUE 'code-example-endpoint-'.
    CONSTANTS cv_endpoint_config_name TYPE /aws1/sgmendpointconfigname VALUE 'code-example-endpoint-cfg-'.
    CONSTANTS cv_endpoint_variant_name TYPE /aws1/sgmvariantname VALUE  'code-example-endpoint-variant-'.
    CONSTANTS cv_ep_instance_type TYPE  /aws1/sgminstancetype VALUE 'ml.m4.xlarge'.
    CONSTANTS cv_ep_initial_instance_count  TYPE  /aws1/sgminitialtaskcount VALUE  '1'.

    "Create training data in Amazon Simple Storage Service (Amazon S3).
    CONSTANTS cv_bucket_name TYPE /aws1/s3_bucketname VALUE 'code-example-sgm-'.
    CONSTANTS cv_train_key TYPE /aws1/s3_objectkey VALUE 'sagemaker/train/train.libsvm'.
    CONSTANTS cv_val_key TYPE /aws1/s3_objectkey VALUE 'sagemaker/validation/validation.libsvm'.

    "Define role Amazon Resource Name (ARN).
    DATA(lt_roles) = ao_session->get_configuration( )->get_logical_iam_roles( ).
    READ TABLE lt_roles WITH KEY profile_id = cv_pfl INTO DATA(lo_role).
    av_lrole = lo_role-iam_role_arn.

    lv_uuid_16 = cl_system_uuid=>create_uuid_x16_static( ).
    lv_bucket_name = cv_bucket_name && lv_uuid_16.
    TRANSLATE lv_bucket_name TO LOWER CASE.

    ao_s3->createbucket( iv_bucket = lv_bucket_name ).

    lv_trn_data_s3uri = 's3://' && lv_bucket_name && '/' && cv_train_key.
    lv_val_data_s3uri = 's3://' && lv_bucket_name && '/' && cv_val_key.
    lv_s3_output_path = 's3://' && lv_bucket_name && '/' && 'sagemaker/'.

    lv_model_key = 'sagemaker/' && cv_training_job_name && lv_uuid_16 && '/output/model.tar.gz'.
    TRANSLATE lv_model_key TO LOWER CASE.
    lv_endpoint_name = cv_endpoint_name && lv_uuid_16.
    TRANSLATE lv_endpoint_name TO LOWER CASE.
    lv_endpoint_config_name = cv_endpoint_config_name && lv_uuid_16.
    TRANSLATE lv_endpoint_config_name TO LOWER CASE.
    lv_model_name = cv_model_name && lv_uuid_16.
    TRANSLATE lv_model_name TO LOWER CASE.
    DATA(lv_training_job_name) = cv_training_job_name && lv_uuid_16.
    TRANSLATE lv_training_job_name TO LOWER CASE.
    DATA(lv_endpoint_variant_name) = cv_endpoint_variant_name && lv_uuid_16.
    TRANSLATE lv_endpoint_variant_name TO LOWER CASE.

    lv_file_content = /aws1/cl_rt_util=>string_to_xstring(
        |0 0:75 1:6.4 17:1 25:1 95:1 325:1\n| &&
        |0 0:59 1:7.3 16:1 19:1 296:1 328:1\n| &&
        |0 0:66 1:6.6 9:1 25:1 193:1 330:1\n| &&
        |0 0:64 1:6.9 9:1 21:1 146:1 330:1\n| &&
        |0 0:65 1:8.699999999999999 9:1 29:1 252:1 325:1\n| &&
        |0 0:61 1:6.1 14:1 29:1 42:1 325:1\n| &&
        |0 0:81 1:8.300000000000001 12:1 29:1 166:1 325:1\n| &&
        |0 0:43 1:3.9 10:1 27:1 310:1 330:1\n| &&
        |0 0:69 1:6.9 8:1 25:1 95:1 325:1\n| &&
        |1 0:88 1:8.6 9:1 21:1 204:1 330:1\n| &&
        |0 0:55 1:7.8 12:1 22:1 140:1 325:1\n| &&
        |0 0:62 1:6 16:1 29:1 95:1 325:1\n| &&
        |0 0:66 1:7.8 15:1 19:1 204:1 330:1\n| &&
        |0 0:85 1:5.8 7:1 19:1 295:1 328:1\n| &&
        |0 0:80 1:6.8 12:1 29:1 166:1 325:1\n| &&
        |0 0:70 1:3.6 7:1 28:1 95:1 330:1\n| &&
        |1 0:81 1:6.2 10:1 27:1 61:1 328:1\n| &&
        |0 0:77 1:7.1 7:1 30:1 222:1 326:1\n| &&
        |0 0:39 1:3.1 7:1 27:1 102:1 330:1\n| &&
        |0 0:55 1:5.7 4:1 21:1 278:1 330:1\n| &&
        |1 0:87 1:7.6 11:1 19:1 310:1 328:1\n| &&
        |0 0:62 1:6.2 9:1 25:1 149:1 325:1\n| &&
        |0 0:80 1:8 4:1 24:1 230:1 325:1\n| &&
        |0 0:68 1:4.9 7:1 30:1 298:1 330:1\n| &&
        |0 0:73 1:9 4:1 26:1 204:1 330:1\n| &&
        |0 0:66 1:7.2 9:1 19:1 204:1 330:1\n| &&
        |0 0:66 1:8.199999999999999 8:1 26:1 38:1 325:1\n| &&
        |0 0:86 1:8.199999999999999 4:1 23:1 209:1 325:1\n| &&
        |0 0:81 1:6.4 8:1 29:1 95:1 325:1\n| &&
        |0 0:71 1:7.1 5:1 29:1 95:1 325:1\n| &&
        |0 0:79 1:7.9 5:1 22:1 42:1 325:1\n| &&
        |1 0:64 1:6.4 9:1 22:1 252:1 330:1\n| &&
        |0 0:78 1:8.300000000000001 9:1 29:1 42:1 325:1\n| &&
        |1 0:91 1:9 8:1 19:1 305:1 328:1\n| &&
        |0 0:84 1:6.8 10:1 29:1 95:1 326:1\n| &&
        |0 0:85 1:8.199999999999999 7:1 27:1 322:1 328:1\n| &&
        |0 0:33 1:7 4:1 27:1 166:1 325:1\n| &&
        |0 0:73 1:3 7:1 27:1 42:1 328:1\n| &&
        |0 0:81 1:8 6:1 29:1 95:1 325:1\n| &&
        |0 0:59 1:6.8 2:1 25:1 75:1 325:1\n| &&
        |0 0:85 1:7.5 9:1 29:1 249:1 325:1\n| &&
        |0 0:79 1:7.6 5:1 29:1 95:1 325:1\n| &&
        |0 0:63 1:6.9 10:1 29:1 95:1 325:1\n| &&
        |1 0:95 1:6.8 10:1 23:1 252:1 325:1\n| &&
        |0 0:47 1:6.1 6:1 19:1 95:1 330:1\n| &&
        |0 0:81 1:8.6 14:1 19:1 249:1 328:1\n| &&
        |0 0:50 1:7 9:1 27:1 193:1 328:1\n| &&
        |0 0:88 1:7.9 10:1 29:1 95:1 326:1\n| &&
        |1 0:74 1:6.1 10:1 29:1 166:1 325:1\n| &&
        |0 0:82 1:8.1 17:1 29:1 95:1 325:1\n| &&
        |0 0:67 1:7.2 10:1 19:1 95:1 328:1\n| &&
        |0 0:67 1:6.7 6:1 23:1 307:1 326:1\n| &&
        |0 0:77 1:7.9 10:1 29:1 95:1 325:1\n| &&
        |0 0:68 1:5.9 7:1 19:1 61:1 328:1\n| &&
        |1 0:81 1:7.5 10:1 19:1 310:1 326:1\n| &&
        |0 0:62 1:7.5 4:1 26:1 278:1 326:1\n| &&
        |1 0:53 1:6.9 5:1 26:1 149:1 325:1\n| &&
        |0 0:78 1:8.1 2:1 26:1 315:1 326:1\n| &&
        |0 0:54 1:5.4 17:1 29:1 79:1 325:1\n| &&
        |0 0:89 1:8.4 10:1 25:1 252:1 326:1\n| &&
        |1 0:75 1:5.6 2:1 26:1 209:1 325:1\n| &&
        |0 0:78 1:8.300000000000001 9:1 29:1 252:1 325:1\n| &&
        |0 0:75 1:8.199999999999999 7:1 28:1 198:1 325:1\n| &&
        |0 0:72 1:7 7:1 27:1 256:1 330:1\n| &&
        |0 0:57 1:4.8 9:1 21:1 71:1 330:1\n| &&
        |0 0:58 1:5.9 10:1 27:1 204:1 330:1\n| &&
        |0 0:74 1:8.300000000000001 17:1 28:1 224:1 330:1\n| &&
        |0 0:48 1:7.1 14:1 26:1 249:1 330:1\n| &&
        |0 0:82 1:7.3 10:1 22:1 276:1 330:1\n| &&
        |0 0:66 1:7.4 6:1 25:1 307:1 325:1\n| &&
        |1 0:70 1:8.699999999999999 9:1 19:1 95:1 325:1\n| &&
        |0 0:86 1:8.4 7:1 23:1 295:1 326:1\n| &&
        |0 0:42 1:4.2 17:1 25:1 204:1 330:1\n| &&
        |0 0:75 1:7.8 4:1 27:1 75:1 325:1\n| &&
        |0 0:93 1:9.300000000000001 9:1 19:1 71:1 330:1\n| &&
        |0 0:63 1:7.8 17:1 22:1 279:1 330:1\n| &&
        |1 0:71 1:5.7 4:1 24:1 193:1 326:1\n| &&
        |0 0:42 1:5 16:1 19:1 256:1 326:1\n| &&
        |0 0:53 1:5.2 16:1 22:1 249:1 330:1\n| &&
        |0 0:64 1:6.4 12:1 19:1 282:1 330:1\n| &&
        |0 0:60 1:7 14:1 19:1 209:1 325:1\n| &&
        |0 0:86 1:7.4 16:1 21:1 95:1 328:1\n| &&
        |0 0:91 1:8.9 17:1 29:1 249:1 325:1\n| &&
        |1 0:82 1:7.8 10:1 26:1 61:1 328:1\n| &&
        |1 0:92 1:8.300000000000001 16:1 22:1 95:1 330:1\n| &&
        |1 0:85 1:8.800000000000001 9:1 26:1 252:1 325:1\n| &&
        |0 0:79 1:7.5 9:1 29:1 252:1 325:1\n| &&
        |1 0:61 1:5.3 14:1 28:1 35:1 325:1\n| &&
        |0 0:75 1:7.7 7:1 28:1 95:1 330:1\n| &&
        |0 0:68 1:8.5 14:1 29:1 279:1 326:1\n| &&
        |1 0:67 1:5 16:1 27:1 71:1 328:1\n| &&
        |0 0:79 1:8.5 12:1 26:1 103:1 330:1\n| &&
        |0 0:61 1:5.1 16:1 29:1 105:1 330:1\n| &&
        |1 0:72 1:8.699999999999999 12:1 26:1 71:1 330:1\n| &&
        |0 0:60 1:6.3 9:1 20:1 307:1 328:1\n| &&
        |0 0:67 1:7.8 9:1 29:1 39:1 325:1\n| &&
        |0 0:53 1:5.6 9:1 26:1 189:1 330:1\n| &&
        |1 0:98 1:7.7 8:1 29:1 42:1 330:1\n| &&
        |0 0:76 1:6.7 7:1 28:1 75:1 330:1\n| &&
        |1 0:74 1:5.3 7:1 28:1 95:1 330:1\n|
    ).

    ao_s3->putobject(
            iv_bucket = lv_bucket_name
            iv_key = cv_train_key
            iv_body = lv_file_content
    ).

    ao_s3->putobject(
            iv_bucket = lv_bucket_name
            iv_key = cv_val_key
            iv_body = lv_file_content
    ).

    ao_sgm_scenario->getting_started_with_sgm(
      EXPORTING
        iv_training_job_name            = lv_training_job_name
        iv_role_arn                     = av_lrole
        iv_trn_data_s3datatype          = cv_trn_data_s3datatype
        iv_trn_data_s3datadistribution  = cv_trn_data_s3datadistribution
        iv_trn_data_s3uri                = lv_trn_data_s3uri
        iv_trn_data_compressiontype     = cv_trn_data_compressiontype
        iv_trn_data_contenttype         = cv_trn_data_contenttype
        iv_val_data_s3datatype          = cv_val_data_s3datatype
        iv_val_data_s3datadistribution  = cv_val_data_s3datadistribution
        iv_val_data_s3uri                = lv_val_data_s3uri
        iv_val_data_compressiontype      = cv_val_data_compressiontype
        iv_val_data_contenttype          = cv_val_data_contenttype
        iv_hp_max_depth                 = cv_hp_max_depth
        iv_hp_scale_pos_weight          = cv_hp_scale_pos_weight
        iv_hp_num_round                  = cv_hp_num_round
        iv_hp_objective                  = cv_hp_objective
        iv_hp_subsample                  = cv_hp_subsample
        iv_hp_eval_metric                = cv_hp_eval_metric
        iv_hp_eta                        = cv_hp_eta
        iv_training_image               = cv_training_image
        iv_training_input_mode          = cv_training_input_mode
        iv_instance_count               = cv_instance_count
        iv_instance_type                = cv_instance_type
        iv_volume_sizeingb              = cv_volume_sizeingb
        iv_s3_output_path               = lv_s3_output_path
        iv_max_runtime_in_seconds       = cv_max_runtime_in_seconds
        iv_ep_instance_type             = cv_ep_instance_type
        iv_ep_initial_instance_count    = cv_ep_initial_instance_count
        iv_model_name                   = lv_model_name
        iv_ep_name                      = lv_endpoint_name
        iv_ep_cfg_name                  = lv_endpoint_config_name
        iv_ep_variant_name              = lv_endpoint_variant_name
      IMPORTING
        oo_ep_output = lo_ep_output
    ).

    lv_found = abap_false.

    IF lo_ep_output->has_endpointarn( ) = 'X'.
      lv_found               = abap_true.
    ENDIF.

    cl_abap_unit_assert=>assert_true(
      act = lv_found
      msg = |Endpoint not found|
    ).

    DATA(lo_model_list_result) = ao_sgm->listmodels(
         iv_namecontains = lv_model_name
       ).
    lv_found = abap_false.

    "The model should be deleted.
    LOOP AT lo_model_list_result->get_models( ) INTO DATA(lo_models).
      IF lo_models->get_modelname( ) = lv_model_name.
        lv_found = abap_true.
      ENDIF.
    ENDLOOP.

    cl_abap_unit_assert=>assert_false(
      act = lv_found
      msg = |Model was not deleted|
    ).

    ao_s3->deleteobject(
        iv_bucket = lv_bucket_name
        iv_key = cv_train_key
    ).

    ao_s3->deleteobject(
        iv_bucket = lv_bucket_name
        iv_key = cv_val_key
    ).

    ao_s3->deleteobject(
        iv_bucket = lv_bucket_name
        iv_key = lv_model_key
    ).

    ao_s3->deletebucket(
        iv_bucket = lv_bucket_name
    ).

  ENDMETHOD.

ENDCLASS.
