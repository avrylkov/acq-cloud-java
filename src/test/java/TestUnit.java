import com.jsoniter.JsonIterator;
import lombok.extern.slf4j.Slf4j;
import org.example.Handler;
import org.example.Main;
import org.example.Request;
import org.example.Response;
import org.example.model.deep.DataAllTb;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@Slf4j
public class TestUnit {

    @Test
    public void testHandler() {
        Handler handler  = new Handler();
        Request request = new Request();
        request.getQueryStringParameters().put("name", "User");
        Response response = handler.apply(request);
        log.info(response.toString());
        assertNotNull(response);
    }

    @Test
    public void testJsonData() throws IOException {
        InputStream resourceAsStream = Main.class.getResourceAsStream("/json/data.json");
        String str = new String(resourceAsStream.readAllBytes());

        Request request = JsonIterator.deserialize(str, Request.class);
        assertTrue(!request.getQueryStringParameters().isEmpty());
    }

    @Test
    public void testJsonDataClass() throws IOException {
        InputStream resourceAsStream = Main.class.getResourceAsStream("/json/data-cube.json");
        String str = new String(resourceAsStream.readAllBytes());

        DataAllTb dataAllTb = JsonIterator.deserialize(str, DataAllTb.class);
        assertNotNull(dataAllTb);
    }

}
