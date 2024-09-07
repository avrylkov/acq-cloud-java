package org.example.service;

import org.example.model.DataAllTb;
import org.example.model.DataGosb;
import org.example.model.DataTb;

import java.util.List;

public class CubeData {

    private CubeDataProvider dataProvider = new CubeDataProvider();


    public DataAllTb getAllTb() {
        DataAllTb dataAllTb = dataProvider.getAllTb();
        Long gosb = dataProvider.getAllGosb();
        Long organization = dataProvider.getAllOrganization();
        Long contract = dataProvider.getAllContract();
        //
        dataAllTb.getMetrics().put("ГОСБы", gosb);
        dataAllTb.getMetrics().put("Организации", organization);
        dataAllTb.getMetrics().put("Договоры", contract);
        //
        return dataAllTb;
    }

    public List<DataTb> fillAllTb() {
        List<DataTb> dataTbList = dataProvider.fillAllTb();
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

    public List<DataGosb> fillAllTbGosb(String tb) {
        List<DataGosb> dataGosbList = dataProvider.fillAllTbGosb(tb);
        dataGosbList.forEach(d -> {
            Long organization = dataProvider.getTbGosbOrganization(tb, d.getCode());
            Long contract = dataProvider.getTbGosbContract(tb, d.getCode());
            //
            d.getMetrics().put("Организации", organization);
            d.getMetrics().put("Договоры", contract);
        });
        return dataGosbList;
    }

}
