package org.example;

import org.example.model.DataCube;
import org.example.model.RequestCube;
import org.example.service.CubeService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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

    //@PutMapping("/cube")
    @PostMapping("/cube")
    public List<DataCube> getCube(@RequestBody RequestCube requestCube) {
       return cubeService.getDataCube(requestCube);
   }


}
