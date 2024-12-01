package org.example;

import lombok.extern.slf4j.Slf4j;
import org.example.model.deep.PageDataDeep;
import org.example.model.deep.RequestCubeDeep;
import org.example.model.up.PageDataLookUp;
import org.example.model.up.RequestCubeLookUp;
import org.example.service.CubeDeepService;
import org.example.service.CubeLookUpService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(maxAge = 3600)
@RestController
@Slf4j
public class Controller {

    private CubeDeepService cubeDeepService = new CubeDeepService();
    private CubeLookUpService cubeLookUpService = new CubeLookUpService();

    @PostMapping(value = "/deep")
    public PageDataDeep getCubeDeep(@RequestBody RequestCubeDeep requestCubeDeep) {
       return cubeDeepService.getDataCubeDeep(requestCubeDeep);
   }

    @PostMapping("/look-up")
    public PageDataLookUp getCubeLookUp(@RequestBody RequestCubeLookUp requestCubeLookUp) {
       return cubeLookUpService.getDataLookUp(requestCubeLookUp);
   }


}
