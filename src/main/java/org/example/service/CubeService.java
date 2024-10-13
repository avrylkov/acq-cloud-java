package org.example.service;

import org.apache.commons.lang3.StringUtils;
import org.example.model.RequestCubeLookUp;
import org.example.model.deep.DataCube;
import org.example.model.Metric;
import org.example.model.RequestCubeDeep;
import org.example.model.up.DataCubeLookUp;
import org.example.model.up.DataCubeLookUpTb;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class CubeService {

    private CubeDataProvider dataProvider = new CubeDataProvider();


    public DataCube getAllTb() {
        DataCube dataCube = new DataCube("Все ТБ");
        Long tb = dataProvider.getAllTb();
        Long gosb = dataProvider.getAllGosb();
        Long organization = dataProvider.getAllOrganization();
        Long contract = dataProvider.getAllContract();
        Long shop = dataProvider.getAllShop();
        //
        dataCube.getMetrics().add(new Metric("ТБ", tb));
        dataCube.getMetrics().add(new Metric("ГОСБы", gosb));
        dataCube.getMetrics().add(new Metric("Организации", organization));
        dataCube.getMetrics().add(new Metric("Договоры", contract));
        dataCube.getMetrics().add(new Metric("ТСТ", shop));
        //
        return dataCube;
    }

    public List<DataCube> fillAllTb() {
        List<DataCube> dataTbList = dataProvider.fillAllTb();
        dataTbList.forEach(d -> {
            Long gosb = dataProvider.getTbGosb(d.getCode());
            Long organization = dataProvider.getTbOrganization(d.getCode());
            Long contract = dataProvider.getTbContract(d.getCode());
            Long shop = dataProvider.getTbShop(d.getCode());
            //
            d.getMetrics().add(new Metric("ГОСБы", gosb));
            d.getMetrics().add(new Metric("Организации", organization));
            d.getMetrics().add(new Metric("Договоры", contract));
            d.getMetrics().add(new Metric("ТСТ", shop));
        });
        return dataTbList;
    }

    public List<DataCube> fillAllTbGosb(String tb) {
        List<DataCube> dataGosbList = dataProvider.fillAllTbGosb(tb);
        dataGosbList.forEach(d -> {
            Long organization = dataProvider.getTbGosbOrganization(tb, d.getCode());
            Long contract = dataProvider.getTbGosbContract(tb, d.getCode());
            Long shop = dataProvider.getTbGosbShop(tb, d.getCode());
            //
            d.getMetrics().add(new Metric("Организации", organization));
            d.getMetrics().add(new Metric("Договоры", contract));
            d.getMetrics().add(new Metric("ТСТ",shop));
        });
        return dataGosbList;
    }

    public List<DataCube> getDataCube(RequestCubeDeep requestCubeDeep) {
        switch (requestCubeDeep.getCode()) {
            case ALL :  return filterByCode(Collections.singletonList(getAllTb()), requestCubeDeep);
            case ALL_TB: return filterByCode(fillAllTb(), requestCubeDeep);
            case TB: return filterByCode(fillAllTbGosb(requestCubeDeep.getTb()), requestCubeDeep);
            case GOSB: return filterByCode(fillAllTbGosbOrganization(requestCubeDeep.getTb(), requestCubeDeep.getGosb()), requestCubeDeep);
        }
        return Collections.EMPTY_LIST;
    }

    private List<DataCube> fillAllTbGosbOrganization(String tb, String gosb) {
        List<DataCube> dataOrganizationList = dataProvider.fillAllTbGosbOrganization(tb, gosb);
        dataOrganizationList.forEach(d -> {
            Long contract = dataProvider.getTbGosbContract(tb, d.getCode());
            Long shop = dataProvider.getTbGosbShop(tb, d.getCode());
            //
            d.getMetrics().add(new Metric("Договоры", contract));
            d.getMetrics().add(new Metric("ТСТ",shop));
        });
        return dataOrganizationList;

    }

    /*
     *    Look UP
     */
    public Set<DataCubeLookUpTb> getDataLookUpByContract(RequestCubeLookUp requestCubeLookUp) {
        return dataProvider.getDataLookUpByContract(requestCubeLookUp);

    }

    private List<DataCube> filterByCode(List<DataCube> cubeList, RequestCubeDeep requestCubeDeep) {
        if (StringUtils.isEmpty(requestCubeDeep.getCodeFilter())) {
            return cubeList;
        }
        return cubeList.stream()
                .filter(c -> c.getCode().contains(requestCubeDeep.getCodeFilter()))
                .collect(Collectors.toList());
    }

}
