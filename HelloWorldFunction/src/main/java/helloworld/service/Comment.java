package helloworld.service;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

import java.time.LocalDateTime;

@DynamoDBTable(tableName = "Comment")
public class Comment {

    private String guid;
    private LocalDateTime registDt;
    private String userId;
    private String comment;

    @DynamoDBHashKey(attributeName = "guid")
    public String getGuid() {
        return guid;
    }

    @DynamoDBRangeKey(attributeName = "registDt")
    public LocalDateTime getRegistDt() {
        return registDt;
    }

    @DynamoDBAttribute(attributeName = "userId")
    public String getUserId() {
        return userId;
    }

    @DynamoDBAttribute(attributeName = "comment")
    public String getComment() {
        return comment;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public void setRegistDt(LocalDateTime registDt) {
        this.registDt = registDt;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
