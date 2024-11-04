package org.example;

import lombok.extern.slf4j.Slf4j;
import org.example.model.deep.DataCube;
import org.example.model.deep.PageData;
import org.example.model.deep.RequestCubeDeep;
import org.example.service.CubeDeepService;

import java.util.function.Function;

@Slf4j
public class HandlerDeep implements Function<RequestCubeDeep, PageData> {

    private CubeDeepService cubeDeepService = new CubeDeepService();

    /*
      private static LocalDateTime localDate = LocalDateTime.now();
      private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

      InputStream resourceAsStream = Main.class.getResourceAsStream("/json/data.json");
      String strResource = new String(resourceAsStream.readAllBytes());
     */

    @Override
    public PageData apply(RequestCubeDeep request) {
        try {
            return cubeDeepService.getDataCubeDeep(request);
        } catch (Exception e) {
            log.error("Exception", e);
            PageData pageData = new PageData(0);
            pageData.getDataCubes().add(new DataCube(e.getMessage()));
            return pageData;
        }
    }
}