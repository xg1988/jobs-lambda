package helloworld;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class AppTest {
  @Test
  public void successfulResponse() {
    App app = new App();
    APIGatewayProxyRequestEvent input = new APIGatewayProxyRequestEvent();
    Map<String, String> map = new HashMap<>();
    map.put("test", "1234");
    input.setQueryStringParameters(map);
    String body = String.format("{ \"userId\": \"xg1988\", \"comment\": \"hello\", \"type\": \"01\" }");
    input.setBody(body);

    APIGatewayProxyResponseEvent result = app.handleRequest(input, null);
    assertEquals(200, result.getStatusCode().intValue());
    assertEquals("application/json", result.getHeaders().get("Content-Type"));
    String content = result.getBody();

    System.out.println("시스템로그 [content]  : "+ content);

    assertNotNull(content);
    //assertTrue(content.contains("\"hello world\""));
    //assertTrue(content.contains("\"location\""));
  }
}
