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
        headers.put("Content-Type", "application/json");
        headers.put("X-Custom-Header", "application/json");

        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent()
                .withHeaders(headers);
        Map<String, String> queryStringMap = input.getQueryStringParameters();
        String body = input.getBody();
        String version = input.getVersion();
        try {
            final String pageContents = this.getPageContents("https://checkip.amazonaws.com");

            JSONObject obj1 = new JSONObject();
            obj1.put("pageContents", pageContents);

            _connectDynamoDB(queryStringMap);

            return response
                    .withStatusCode(200)
                    .withBody(obj1.toJSONString());
        } catch (Exception e) {
            return response
                    .withBody("{}")
                    .withStatusCode(500);
        }
    }

    private void _connectDynamoDB(Map<String, String> queryStringMap) throws ConditionalCheckFailedException {
        final AmazonDynamoDB ddb = AmazonDynamoDBClientBuilder.standard()
                .withRegion(Regions.AP_NORTHEAST_2)
                .build();
        System.out.println("시스템로그 []: " + queryStringMap.toString());
        try {
            Map<String, AttributeValue> item_values = new HashMap<>();
            item_values.put("guid", new AttributeValue(UUID.randomUUID().toString()));
            item_values.put("registDate", new AttributeValue(String.valueOf(LocalDateTime.now().format(DateTimeFormatter.ofPattern("YYYYMMDDHHMMSS")))));
            item_values.put("comment", new AttributeValue(queryStringMap.get("comment")));
            item_values.put("userId", new AttributeValue(queryStringMap.get("userId")));

            ddb.putItem("Comment", item_values);
        } catch (ResourceNotFoundException e) {
            System.out.println("시스템로그 []: " + e);
        } catch (AmazonServiceException e) {
            System.out.println("시스템로그 []: " + e);
        }


    }

    private String getPageContents(String address) throws IOException{
        URL url = new URL(address);
        try(BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()))) {
            return br.lines().collect(Collectors.joining(System.lineSeparator()));
        }
    }
}
