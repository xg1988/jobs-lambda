package helloworld;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ConditionalCheckFailedException;
import com.amazonaws.services.dynamodbv2.model.ResourceNotFoundException;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import helloworld.service.Comment;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.Test;


/**
 * Handler for requests to Lambda function.
 */
public class App implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    public APIGatewayProxyResponseEvent handleRequest(final APIGatewayProxyRequestEvent input, final Context context) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type",         "application/json");
        headers.put("X-Custom-Header",      "application/json");


        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent()
                                                    .withHeaders(headers);

        String body             = input.getBody();
        String version          = input.getVersion();

        System.out.println("시스템로그 [body]: " + body);
        System.out.println("시스템로그 [version]: " + version);
        System.out.println("input -> "+ input);
        try {
            //final String pageContents = this.getPageContents("https://checkip.amazonaws.com");
            AmazonDynamoDB ddb = _connectDynamoDB();

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            Comment comment = objectMapper.readValue(body, Comment.class);

            try {
                Map<String, AttributeValue> item_values = new HashMap<>();
                item_values.put("guid",             new AttributeValue(UUID.randomUUID().toString()));
                item_values.put("registDate",       new AttributeValue(String.valueOf(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddhhmm")))));
                item_values.put("comment",          new AttributeValue(comment.getComment()));
                item_values.put("userId",           new AttributeValue(comment.getUserId()));
                item_values.put("type",           new AttributeValue(comment.getType()));

                System.out.println("시스템로그 [item_values]: " + item_values.toString());

                ddb.putItem("Comment", item_values);

            } catch (ResourceNotFoundException e) {
                System.out.println("시스템로그 []: " + e);
            } catch (AmazonServiceException e) {
                System.out.println("시스템로그 []: " + e);
            }


            return response
                    .withStatusCode(200)
                    .withBody(comment.toString());
        } catch (Exception e) {
            System.out.println("시스템로그 []: e : "+ e);
            return response
                    .withBody("{}")
                    .withStatusCode(500);
        }
    }

    private AmazonDynamoDB _connectDynamoDB() {
        return AmazonDynamoDBClientBuilder.standard()
                .withRegion(Regions.AP_NORTHEAST_2)
                .build();
    }

    private String getPageContents(String address) throws IOException{
        URL url = new URL(address);
        try(BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()))) {
            return br.lines().collect(Collectors.joining(System.lineSeparator()));
        }
    }
}
