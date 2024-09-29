package org.example;

import org.example.model.RequestCubeLookUp;
import org.example.model.deep.DataCube;
import org.example.model.RequestCubeDeep;
import org.example.model.up.DataCubeLookUp;
import org.example.model.up.DataCubeLookUpTb;
import org.example.service.CubeService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

@CrossOrigin(maxAge = 3600)
@RestController
public class Controller {

    private CubeService cubeService = new CubeService();

    @GetMapping("/all-tb")
    public DataCube allTb() {
        return cubeService.getAllTb();
    }

    @GetMapping("/fill-all-tb")
    public List<DataCube> fillAllTb() {
       return cubeService.fillAllTb();
    }

    @GetMapping("/tb/{code}")
    public List<DataCube> fillAllTbGosb(@PathVariable String code) {
        return cubeService.fillAllTbGosb(code);
    }

    @PostMapping("/cube")
    public List<DataCube> getCube(@RequestBody RequestCubeDeep requestCubeDeep) {
       return cubeService.getDataCube(requestCubeDeep);
   }

    @PostMapping("/look-up")
    public Set<DataCubeLookUpTb> getCube(@RequestBody RequestCubeLookUp requestCubeLookUp) {
       return cubeService.getDataLookUpByContract(requestCubeLookUp);
   }


}
