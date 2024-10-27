package org.example.service;

import org.apache.commons.lang3.StringUtils;
import org.example.model.deep.DataCube;
import org.example.model.Metric;
import org.example.model.deep.PageInfo;
import org.example.model.deep.RequestCubeDeep;
import org.example.model.deep.PageData;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CubeDeepService {

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

    public PageData getDataCubeDeep(@NotNull RequestCubeDeep requestCubeDeep) {
        switch (requestCubeDeep.getCode()) {
            case ALL :  return pagination(filterByCode(Collections.singletonList(getAllTb()), requestCubeDeep), requestCubeDeep.getPageInfo());
            case ALL_TB: return pagination(filterByCode(fillAllTb(), requestCubeDeep), requestCubeDeep.getPageInfo());
            case TB: return pagination(filterByCode(fillTbGosb(requestCubeDeep.getTb()), requestCubeDeep), requestCubeDeep.getPageInfo());
            case GOSB: return pagination(filterByCode(fillTbGosbOrg(requestCubeDeep.getTb(), requestCubeDeep.getGosb()), requestCubeDeep), requestCubeDeep.getPageInfo());
            case ORG: return pagination(filterByCode(fillTbGosbOrgContr(requestCubeDeep.getTb(), requestCubeDeep.getGosb(), requestCubeDeep.getOrg()), requestCubeDeep), requestCubeDeep.getPageInfo());
        }
        return new PageData(0);
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

    private List<DataCube> filterByCode(List<DataCube> cubeList, RequestCubeDeep requestCubeDeep) {
        if (StringUtils.isEmpty(requestCubeDeep.getCodeFilter())) {
            return cubeList;
        }
        return cubeList.stream()
                .filter(c -> c.getCode().toLowerCase().contains(requestCubeDeep.getCodeFilter().toLowerCase()))
                .collect(Collectors.toList());
    }


    private PageData pagination(List<DataCube> dataCubes, PageInfo pageInfo) {
        int startIndex = (pageInfo.getPageNumber() - 1) * pageInfo.getPageSize();
        int endIndex = Math.min(startIndex + pageInfo.getPageSize(), dataCubes.size());

        PageData pageData = new PageData(dataCubes.size());
        pageData.setDataCubes(dataCubes.stream()
                .skip(startIndex)
                .limit(endIndex - startIndex)
                .collect(Collectors.toList()));
        return pageData;
    }

}
