package org.example.service;

import org.apache.commons.lang3.StringUtils;
import org.example.model.RequestCubeLookUp;
import org.example.model.deep.DataCube;
import org.example.model.Metric;
import org.example.model.RequestCubeDeep;
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
            Long organization = dataProvider.getSumTbOrganization(d.getCode());
            Long contract = dataProvider.getSumTbContract(d.getCode());
            Long shop = dataProvider.getSumTbShop(d.getCode());
            //
            d.getMetrics().add(new Metric("ГОСБы", gosb));
            d.getMetrics().add(new Metric("Организации", organization));
            d.getMetrics().add(new Metric("Договоры", contract));
            d.getMetrics().add(new Metric("ТСТ", shop));
        });
        return dataTbList;
    }

    public List<DataCube> fillTbGosb(String tb) {
        List<DataCube> dataGosbList = dataProvider.fillTbGosb(tb);
        dataGosbList.forEach(d -> {
            Long organization = dataProvider.getSumTbGosbOrganization(tb, d.getCode());
            Long contract = dataProvider.getSumTbGosbContract(tb, d.getCode());
            Long shop = dataProvider.getSumTbGosbShop(tb, d.getCode());
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
            case TB: return filterByCode(fillTbGosb(requestCubeDeep.getTb()), requestCubeDeep);
            case GOSB: return filterByCode(fillTbGosbOrg(requestCubeDeep.getTb(), requestCubeDeep.getGosb()), requestCubeDeep);
            case ORG: return filterByCode(fillTbGosbOrgContr(requestCubeDeep.getTb(), requestCubeDeep.getGosb(), requestCubeDeep.getOrg()), requestCubeDeep);
        }
        return Collections.EMPTY_LIST;
    }

    private List<DataCube> fillTbGosbOrgContr(String tb, String gosb, String org) {
        List<DataCube> dataContractList = dataProvider.fillTbGosbOrgContr(tb, gosb, org);
        dataContractList.forEach(contr -> {
            Long shop = dataProvider.getSumTbGosbOrgContrShop(tb, gosb, org, contr.getCode());
            //
            contr.getMetrics().add(new Metric("ТСТ",shop));
        });
        return filterNonZeroMetric(dataContractList);
    }

    private List<DataCube> fillTbGosbOrg(String tb, String gosb) {
        List<DataCube> dataOrganizationList = dataProvider.fillTbGosbOrg(tb, gosb);
        dataOrganizationList.forEach(org -> {
            Long contract = dataProvider.getSumTbGosbOrgContract(tb, gosb, org.getCode());
            Long shop = dataProvider.getSumTbGosbOrgShop(tb, gosb, org.getCode());
            //
            org.getMetrics().add(new Metric("Договоры", contract));
            org.getMetrics().add(new Metric("ТСТ",shop));
        });
        return filterNonZeroMetric(dataOrganizationList);
    }

    private List<DataCube>  filterNonZeroMetric(List<DataCube> data) {
        return data.stream()
                .filter(o -> o.getMetrics().stream().mapToLong(Metric::getValue).sum() > 0)
                .collect(Collectors.toList());
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
                .filter(c -> c.getCode().toLowerCase().contains(requestCubeDeep.getCodeFilter().toLowerCase()))
                .collect(Collectors.toList());
    }

}
