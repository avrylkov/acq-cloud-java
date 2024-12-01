package org.example.service;

import org.apache.commons.lang3.StringUtils;
import org.example.model.deep.DataCube;
import org.example.model.Metric;
import org.example.model.deep.PageInfo;
import org.example.model.deep.RequestCubeDeep;
import org.example.model.deep.PageDataDeep;
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
        Long terminal = dataProvider.getAllTerminal();
        //
        dataCube.getMetrics().add(new Metric("ТБ", tb));
        dataCube.getMetrics().add(new Metric("ГОСБы", gosb));
        dataCube.getMetrics().add(new Metric("Организации", organization));
        dataCube.getMetrics().add(new Metric("Договоры", contract));
        dataCube.getMetrics().add(new Metric("ТСТ", shop));
        dataCube.getMetrics().add(new Metric("Терминалы", terminal));
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
            Long terminal = dataProvider.getSumTbTerminal(d.getCode());
            //
            d.getMetrics().add(new Metric("ГОСБы", gosb));
            d.getMetrics().add(new Metric("Организации", organization));
            d.getMetrics().add(new Metric("Договоры", contract));
            d.getMetrics().add(new Metric("ТСТ", shop));
            d.getMetrics().add(new Metric("Терминалы", terminal));
        });
        return dataTbList;
    }

    public List<DataCube> fillTbGosb(String tb) {
        List<DataCube> dataGosbList = dataProvider.fillTbGosb(tb);
        dataGosbList.forEach(d -> {
            Long organization = dataProvider.getSumTbGosbOrganization(tb, d.getCode());
            Long contract = dataProvider.getSumTbGosbContract(tb, d.getCode());
            Long shop = dataProvider.getSumTbGosbShop(tb, d.getCode());
            Long terminal = dataProvider.getSumTbGosbTerminal(tb, d.getCode());
            //
            d.getMetrics().add(new Metric("Организации", organization));
            d.getMetrics().add(new Metric("Договоры", contract));
            d.getMetrics().add(new Metric("ТСТ",shop));
            d.getMetrics().add(new Metric("Терминалы",terminal));
        });
        return dataGosbList;
    }

    public PageDataDeep getDataCubeDeep(@NotNull RequestCubeDeep rq) {
        switch (rq.getCode()) {
            case ALL :  return pagination(Collections.singletonList(getAllTb()), rq.getPageInfo());
            case ALL_TB: return pagination(filterByCode(fillAllTb(), rq), rq.getPageInfo());
            case TB: return pagination(filterByCode(fillTbGosb(rq.getTb()), rq), rq.getPageInfo());
            case GOSB: return pagination(filterByCode(fillTbGosbOrg(rq.getTb(), rq.getGosb()), rq), rq.getPageInfo());
            case ORG: return pagination(filterByCode(fillTbGosbOrgContr(rq.getTb(), rq.getGosb(), rq.getOrg()), rq), rq.getPageInfo());
            case CONTRACT: return pagination(filterByCode(fillTbGosbOrgContrShop(rq.getTb(), rq.getGosb(), rq.getOrg(), rq.getContract()), rq), rq.getPageInfo());
            case SHOP: return pagination(filterByCode(fillTbGosbOrgContrShopTerminal(rq.getTb(), rq.getGosb(), rq.getOrg(), rq.getContract(), rq.getShop()), rq), rq.getPageInfo());
        }
        return new PageDataDeep(0);
    }

    private List<DataCube> fillTbGosbOrgContrShopTerminal(String tb, String gosb, String org, String contract, String shop) {
        return dataProvider.fillTbGosbOrgContrShopTerminal(tb, gosb, org, contract, shop);
    }

    private List<DataCube> fillTbGosbOrgContrShop(String tb, String gosb, String org, String contract) {
        List<DataCube> dataShopList = dataProvider.fillTbGosbOrgContrShop(tb, gosb, org, contract);
        dataShopList.forEach(shop -> {
            Long terminal = dataProvider.getSumTbGosbOrgContrShopTerminal(tb, gosb, org, contract, shop.getCode());
            //
            shop.getMetrics().add(new Metric("Терминалы", terminal));
        });
        return filterNonZeroMetric(dataShopList);
    }

    private List<DataCube> fillTbGosbOrgContr(String tb, String gosb, String org) {
        List<DataCube> dataContractList = dataProvider.fillTbGosbOrgContr(tb, gosb, org);
        dataContractList.forEach(contr -> {
            Long shop = dataProvider.getSumTbGosbOrgContrShop(tb, gosb, org, contr.getCode());
            Long terminal = dataProvider.getSumTbGosbOrgContrTerminal(tb, gosb, org, contr.getCode());
            //
            contr.getMetrics().add(new Metric("ТСТ",shop));
            contr.getMetrics().add(new Metric("Терминалы",terminal));
        });
        return filterNonZeroMetric(dataContractList);
    }

    private List<DataCube> fillTbGosbOrg(String tb, String gosb) {
        List<DataCube> dataOrganizationList = dataProvider.fillTbGosbOrg(tb, gosb);
        dataOrganizationList.forEach(org -> {
            Long contract = dataProvider.getSumTbGosbOrgContract(tb, gosb, org.getCode());
            Long shop = dataProvider.getSumTbGosbOrgShop(tb, gosb, org.getCode());
            Long terminal = dataProvider.getSumTbGosbOrgTerminal(tb, gosb, org.getCode());
            //
            org.getMetrics().add(new Metric("Договоры", contract));
            org.getMetrics().add(new Metric("ТСТ",shop));
            org.getMetrics().add(new Metric("Терминалы",terminal));
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


    private PageDataDeep pagination(List<DataCube> dataCubes, PageInfo pageInfo) {
        List<DataCube> pagination = dataProvider.pagination(dataCubes, pageInfo);
        PageDataDeep pageDataDeep = new PageDataDeep(dataCubes.size());
        pageDataDeep.setDataCubes(pagination);
        return pageDataDeep;
    }

}
