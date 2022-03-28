package com.example.dynamodb;

import software.amazon.awssdk.enhanced.dynamodb.EnhancedType;
import software.amazon.awssdk.enhanced.dynamodb.internal.converter.attribute.ListAttributeConverter;
import software.amazon.awssdk.enhanced.dynamodb.internal.converter.attribute.StringAttributeConverter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.StaticTableSchema;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

import java.util.ArrayList;
import java.util.List;

import static software.amazon.awssdk.enhanced.dynamodb.mapper.StaticAttributeTags.primaryPartitionKey;

@DynamoDbBean
public class Contact {
    private String id;
    private List<String> pid;
    // getters and setters

    public static final StaticTableSchema.Builder<Contact> TABLE_SCHEMA =
            StaticTableSchema.builder(Contact.class)
                    .newItemSupplier(Contact::new)
                    .addAttribute(String.class, a -> a.name("id")
                            .getter(Contact::getId)
                            .setter(Contact::setId)
                            .tags(primaryPartitionKey()))
                    .addAttribute(EnhancedType.listOf(String.class),
                            a -> a.name("pid")
                                    .getter(Contact::getPid)
                                    .setter(Contact::setPid)
                                    .attributeConverter(ListAttributeConverter.builder(EnhancedType.listOf(String.class))
                                            .collectionConstructor(ArrayList::new)
                                            .elementConverter(StringAttributeConverter.create())
                                            .build()));

    @DynamoDbPartitionKey
    public String getId() {
        return this.id;
    };

    public void setId(String id) {

        this.id = id;
    }

    public List<String> getPid() {
        return this.pid;
    }

    public void setPid(List pid) {
        this.pid = pid;
    }
}