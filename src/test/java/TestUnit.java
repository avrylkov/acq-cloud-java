import com.jsoniter.JsonIterator;
import lombok.extern.slf4j.Slf4j;
import org.example.HandlerDeep;
import org.example.Main;
import org.example.model.deep.DataAllTb;
import org.example.model.deep.PageData;
import org.example.model.deep.RequestCubeDeep;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.assertNotNull;

@Slf4j
public class TestUnit {

    @Test
    public void testHandler() {
        HandlerDeep handlerDeep = new HandlerDeep();
        RequestCubeDeep request = new RequestCubeDeep();
        request.setTb("10");
        PageData response = handlerDeep.apply(request);
        log.info(response.toString());
        assertNotNull(response);
    }

    @Test
    public void testJsonDataClass() throws IOException {
        InputStream resourceAsStream = Main.class.getResourceAsStream("/json/data-cube.json");
        String str = new String(resourceAsStream.readAllBytes());

        DataAllTb dataAllTb = JsonIterator.deserialize(str, DataAllTb.class);
        assertNotNull(dataAllTb);
    }

}
