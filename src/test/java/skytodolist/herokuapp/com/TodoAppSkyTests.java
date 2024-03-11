package skytodolist.herokuapp.com;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Евгений добрый день!
 * Понимаю, что можно было уменьшить количество кода.
 * Мои вопросы:
 * 1. Запутался в проверках, можно проверять: код ответа, название Header, содержимое Header, тело ответа полностью.
 * Проверки зависят от задания?
 * 2. Наверно нужно было, проверки вынести в отдельные методы.?
 * 3. Названия переменных и методов. При большом количестве переменных и методов, начинаю путаться. Можете дать совет по именованию переменных и методов?
 */

public class TodoAppSkyTests {
    private HttpClient client;

    @BeforeEach
    public void setUp() {
        client = HttpClientBuilder.create().build();
    }

    public static final String URL = "https://todo-app-sky.herokuapp.com/";

    @Test
    @DisplayName("Create a task, check the answer, delete the task and check the answer")
    public void testCreateAndDeleteTask() throws Exception {
        HttpResponse createResponse = createTask();
        assertEquals(201, createResponse.getStatusLine().getStatusCode());

        // Проверить тип контента в заголовке
        Header contentTypeHeader = createResponse.getFirstHeader("Content-Type");
        assertTrue(contentTypeHeader.getValue().contains("application/json"));

        // Проверить, что тело ответа не null
        HttpEntity createResponseEntity = createResponse.getEntity();
        assertNotNull(createResponseEntity);

        // Преобразовать тело ответа в строку JSON
        String createResponseBody = EntityUtils.toString(createResponseEntity);
        assertNotNull(createResponseBody);

        // Проверить, что JSON содержит "sheep"
        assertTrue(createResponseBody.contains("sheep"));

        String taskId = extractTaskId(createResponseBody);

        HttpResponse deleteResponse = deleteTask(taskId);
        assertEquals(204, deleteResponse.getStatusLine().getStatusCode());

        // Проверить тип контента в заголовке
        Header contentTypeHeaderDelete = deleteResponse.getFirstHeader("Content-Type");
        assertTrue(contentTypeHeaderDelete.getValue().contains("application/json"));

        // Проверить, что тело ответа не null
        HttpEntity responseEntityDelete = deleteResponse.getEntity();
        assertNull(responseEntityDelete);
    }

    @Test
    @DisplayName("Create a task, rename it and check the answer, delete the task")
    public void testRenameTask() throws Exception {
        HttpResponse createResponse = createTask();
        assertEquals(201, createResponse.getStatusLine().getStatusCode());

        HttpEntity createResponseEntity = createResponse.getEntity();
        String createResponseBody = EntityUtils.toString(createResponseEntity);

        String taskId = extractTaskId(createResponseBody);
        HttpResponse renameResponse = renameTask(taskId);

        assertEquals(200, renameResponse.getStatusLine().getStatusCode());

        // Проверить тип контента в заголовке
        Header contentTypeHeader = renameResponse.getFirstHeader("Content-Type");
        assertTrue(contentTypeHeader.getValue().contains("application/json"));

        // Проверить, что тело ответа не null
        HttpEntity createResponseEntityRename = renameResponse.getEntity();
        assertNotNull(createResponseEntity);

        // Преобразовать тело ответа в строку JSON
        String createResponseBodyRename = EntityUtils.toString(createResponseEntityRename);
        assertNotNull(createResponseBody);

        // Проверить, что JSON содержит "sheep"
        assertTrue(createResponseBodyRename.contains("wolf"));

        HttpResponse deleteResponse = deleteTask(taskId);
        assertEquals(204, deleteResponse.getStatusLine().getStatusCode());

    }

    @Test
    @DisplayName("Create a task, mark completed and check the answer, delete the task")
    public void testDoneTask() throws Exception {
        HttpResponse createResponse = createTask();
        assertEquals(201, createResponse.getStatusLine().getStatusCode());

        HttpEntity createResponseEntity = createResponse.getEntity();
        String createResponseBody = EntityUtils.toString(createResponseEntity);

        String taskId = extractTaskId(createResponseBody);
        HttpResponse doneResponse = doneTask(taskId);

        assertEquals(200, doneResponse.getStatusLine().getStatusCode());
//
        // Проверить тип контента в заголовке
        Header contentTypeHeader = doneResponse.getFirstHeader("Content-Type");
        assertTrue(contentTypeHeader.getValue().contains("application/json"));
//
        // Проверить, что тело ответа не null
        HttpEntity createResponseEntityDone = doneResponse.getEntity();
        assertNotNull(createResponseEntityDone);

        // Преобразовать тело ответа в строку JSON
        String createResponseBodyDone = EntityUtils.toString(createResponseEntityDone);
        assertNotNull(createResponseBodyDone);
//
//        // Проверить, что JSON содержит "sheep"
        assertTrue(createResponseBodyDone.contains("\"completed\":true"));

        HttpResponse deleteResponse = deleteTask(taskId);
        assertEquals(204, deleteResponse.getStatusLine().getStatusCode());

    }

    @Test
    @DisplayName("Get task list")
    public void testGetAllTasks() throws Exception {
        HttpResponse deleteAllTasksResponse = deleteAllTask();
        assertEquals(204, deleteAllTasksResponse.getStatusLine().getStatusCode());

        HttpResponse createTaskResponse = createTask();
        assertEquals(201, createTaskResponse.getStatusLine().getStatusCode());

        HttpResponse getAllTasksResponse = getAllTask();
        assertEquals(200, getAllTasksResponse.getStatusLine().getStatusCode());
        assertEquals(1, getAllTasksResponse.getHeaders("Content-Type").length);

        HttpEntity createAllTasksResponse = getAllTasksResponse.getEntity();
        assertNotNull(createAllTasksResponse);

        // Преобразовать тело ответа в строку JSON
        String createAllTasksResponseBody = EntityUtils.toString(createAllTasksResponse);
        assertNotNull(createAllTasksResponseBody);

        assertTrue(createAllTasksResponseBody.contains("sheep"));

        HttpResponse deleteAllTasksResponse2 = deleteAllTask();
        assertEquals(204, deleteAllTasksResponse2.getStatusLine().getStatusCode());
    }

    private HttpResponse createTask() throws IOException {
        HttpPost postRequest = new HttpPost(URL);

        String myContent = "{\"title\" : \"sheep\"}";
        StringEntity entity = new StringEntity(myContent, ContentType.APPLICATION_JSON);
        postRequest.setEntity(entity);

        HttpResponse response = client.execute(postRequest);

        return response;
    }

    private HttpResponse deleteTask(String id) throws IOException {
        HttpDelete deleteRequest = new HttpDelete(URL + id);
        HttpResponse response = client.execute(deleteRequest);

        return response;
    }

    private HttpResponse deleteAllTask() throws IOException {
        HttpDelete deleteRequest = new HttpDelete(URL);
        HttpResponse response = client.execute(deleteRequest);

        return response;
    }

    private HttpResponse getAllTask() throws IOException {
        HttpGet getRequest = new HttpGet(URL);
        HttpResponse responseList = client.execute(getRequest);
        return responseList;
    }

    private String extractTaskId(String responseBody) {
        return responseBody.substring(6, 12);
    }

    private HttpResponse renameTask(String id) throws IOException {
        HttpPatch renameRequest = new HttpPatch(URL + id);
        String myContent = "{\"title\" : \"wolf\"}";
        StringEntity entity = new StringEntity(myContent, ContentType.APPLICATION_JSON);
        renameRequest.setEntity(entity);

        HttpResponse response = client.execute(renameRequest);

        return response;
    }

    private HttpResponse doneTask(String id) throws IOException {
        HttpPatch doneRequest = new HttpPatch(URL + id);
        String myContent = "{\"completed\" : \"true\"}";
        StringEntity entity = new StringEntity(myContent, ContentType.APPLICATION_JSON);
        doneRequest.setEntity(entity);

        HttpResponse response = client.execute(doneRequest);

        return response;
    }
}
