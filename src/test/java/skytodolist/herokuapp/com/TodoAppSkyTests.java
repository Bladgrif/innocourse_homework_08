package skytodolist.herokuapp.com;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TodoAppSkyTests {
    private final int count = 0;
    private HttpClient client = HttpClientBuilder.create().build();
    public final String URL = "https://todo-app-sky.herokuapp.com/";

    @Test
    public void testCreateTask() throws Exception {
        HttpResponse response = createTask("Sheep");
        assertEquals(201, response.getStatusLine().getStatusCode());

        String body = EntityUtils.toString(response.getEntity());

        // Проверить поля ответа
        assertEquals(201, response.getStatusLine().getStatusCode());
        Header contentTypeHeader = response.getFirstHeader("Content-Type");
        assertTrue(contentTypeHeader.getValue().contains("application/json"));

    }

    private HttpResponse createTask(String title) throws IOException {
        HttpPost postRequest = new HttpPost(URL);

        String myContent = "{\"title\" : \"" + title + " " + count + "\"}";
        StringEntity entity = new StringEntity(myContent, ContentType.APPLICATION_JSON);
        postRequest.setEntity(entity);

        HttpResponse response = client.execute(postRequest);

        return response;
    }
}
