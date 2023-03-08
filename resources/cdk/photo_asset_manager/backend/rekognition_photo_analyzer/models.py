from aws_cdk.aws_apigateway import Model, JsonSchema, JsonSchemaType, JsonSchemaVersion


def ArchiveRequestModel(scope):
    return Model(
        scope,
        rest_api=scope.api,
        id="ArchiveRequestModel",
        schema=JsonSchema(
            schema=JsonSchemaVersion.DRAFT4,
            type=JsonSchemaType.OBJECT,
            properties={
                "images": JsonSchema(
                    type=JsonSchemaType.ARRAY,
                    items=JsonSchema(type=JsonSchemaType.STRING),
                )
            },
        ),
    )


def LabelsResponseModel(scope):
    return Model(
        scope,
        rest_api=scope.api,
        id="LabelsResponseModel",
        schema=JsonSchema(
            schema=JsonSchemaVersion.DRAFT4,
            type=JsonSchemaType.OBJECT,
            properties={
                "labels": JsonSchema(
                    type=JsonSchemaType.OBJECT,
                    additional_properties=JsonSchema(
                        type=JsonSchemaType.OBJECT,
                        properties={
                            "count": JsonSchema(
                                type=JsonSchemaType.INTEGER,
                            )
                        },
                    ),
                )
            },
        ),
    )


def UploadRequestModel(scope):
    return Model(
        scope,
        rest_api=scope.api,
        id="UploadRequestModel",
        schema=JsonSchema(
            schema=JsonSchemaVersion.DRAFT4,
            type=JsonSchemaType.OBJECT,
            properties={
                "file_name": JsonSchemaType.STRING,
            },
        ),
    )


def UploadResponseModel(scope):
    return Model(
        scope,
        rest_api=scope.api,
        id="UploadResponseModel",
        schema=JsonSchema(
            schema=JsonSchemaVersion.DRAFT4,
            type=JsonSchemaType.OBJECT,
            properties={
                "url": JsonSchemaType.STRING,
            },
        ),
    )


def CopyRequestModel(scope):
    return Model(
        scope,
        rest_api=scope.api,
        id="CopyRequestModel",
        schema=JsonSchema(
            schema=JsonSchemaVersion.DRAFT4,
            type=JsonSchemaType.OBJECT,
            properties={
                "source": JsonSchemaType.STRING,
            },
        ),
    )


def CopyResponseModel(scope):
    return Model(
        scope,
        rest_api=scope.api,
        id="CopyResponseModel",
        schema=JsonSchema(
            schema=JsonSchemaVersion.DRAFT4,
            type=JsonSchemaType.OBJECT,
            properties={
                "count": JsonSchemaType.INTEGER,
            },
        ),
    )


def DownloadRequestModel(scope):
    return Model(
        scope,
        rest_api=scope.api,
        id="DownloadRequestModel",
        schema=JsonSchema(
            schema=JsonSchemaVersion.DRAFT4,
            type=JsonSchemaType.OBJECT,
            properties={
                "labels": JsonSchema(
                    type=JsonSchemaType.ARRAY,
                    items=JsonSchema(type=JsonSchemaType.STRING),
                )
            },
        ),
    )


def Empty(scope):
    return Model(
        scope,
        rest_api=scope.api,
        id="Empty",
        schema=JsonSchema(schema=JsonSchemaVersion.DRAFT4, type=JsonSchemaType.OBJECT),
    )
