package org.example;

import lombok.extern.slf4j.Slf4j;
import org.example.model.Metric;
import org.example.model.RequestCubeLookUp;
import org.example.model.deep.DataCube;
import org.example.model.deep.PageData;
import org.example.model.deep.RequestCubeDeep;
import org.example.model.up.DataCubeLookUpTb;
import org.example.service.CubeDeepService;
import org.example.service.CubeLookUpService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@CrossOrigin(maxAge = 3600)
@RestController
@Slf4j
public class Controller {

    private CubeDeepService cubeDeepService = new CubeDeepService();
    private CubeLookUpService cubeLookUpService = new CubeLookUpService();

    @PostMapping("/test")
    public PageData test(@RequestBody RequestCubeDeep requestCubeDeep) {
        log.info("request, {}", requestCubeDeep);
        PageData pageData = new PageData(1);
        DataCube dataCube = new DataCube("10");
        dataCube.getMetrics().add(new Metric(requestCubeDeep.getTb(), 1L));
        pageData.getDataCubes().add(dataCube);
        return pageData;
    }

    @PostMapping("/deep")
    public PageData getCubeDeep(@RequestBody RequestCubeDeep requestCubeDeep) {
       return cubeDeepService.getDataCubeDeep(requestCubeDeep);
   }

    @PostMapping("/look-up")
    public Set<DataCubeLookUpTb> getCubeLookUp(@RequestBody RequestCubeLookUp requestCubeLookUp) {
       return cubeLookUpService.getDataLookUp(requestCubeLookUp);
   }


}
