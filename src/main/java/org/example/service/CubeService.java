package org.example.service;

import org.example.model.DataCube;
import org.example.model.RequestCube;

import java.util.Collections;
import java.util.List;

public class CubeService {

    private CubeDataProvider dataProvider = new CubeDataProvider();


    public DataCube getAllTb() {
        DataCube dataCube = new DataCube("all-tb");
        Long tb = dataProvider.getAllTb();
        Long gosb = dataProvider.getAllGosb();
        Long organization = dataProvider.getAllOrganization();
        Long contract = dataProvider.getAllContract();
        //
        dataCube.getMetrics().put("ТБ", tb);
        dataCube.getMetrics().put("ГОСБы", gosb);
        dataCube.getMetrics().put("Организации", organization);
        dataCube.getMetrics().put("Договоры", contract);
        //
        return dataCube;
    }

    public List<DataCube> fillAllTb() {
        List<DataCube> dataTbList = dataProvider.fillAllTb();
        dataTbList.forEach(d -> {
            Long gosb = dataProvider.getTbGosb(d.getCode());
            Long organization = dataProvider.getTbOrganization(d.getCode());
            Long contract = dataProvider.getTbContract(d.getCode());
            //
            d.getMetrics().put("ГОСБы", gosb);
            d.getMetrics().put("Организации", organization);
            d.getMetrics().put("Договоры", contract);
        });
        return dataTbList;
    }

    public List<DataCube> fillAllTbGosb(String tb) {
        List<DataCube> dataGosbList = dataProvider.fillAllTbGosb(tb);
        dataGosbList.forEach(d -> {
            Long organization = dataProvider.getTbGosbOrganization(tb, d.getCode());
            Long contract = dataProvider.getTbGosbContract(tb, d.getCode());
            //
            d.getMetrics().put("Организации", organization);
            d.getMetrics().put("Договоры", contract);
        });
        return dataGosbList;
    }

    public List<DataCube> getDataCube(RequestCube requestCube) {
        switch (requestCube.getCode()) {
            case ALL :  return Collections.singletonList(getAllTb());
            case ALL_TB: return fillAllTb();
            case TB: return fillAllTbGosb(requestCube.getTb());
        }
        return Collections.EMPTY_LIST;
    }
}
