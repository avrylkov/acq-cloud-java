package org.example.service;

import org.apache.commons.lang3.StringUtils;
import org.example.model.deep.DataContract;
import org.example.model.deep.DataGosb;
import org.example.model.deep.DataOrganization;
import org.example.model.deep.DataShop;
import org.example.model.deep.DataTb;
import org.example.model.deep.DataTerminal;
import org.example.model.deep.PageInfo;
import org.example.model.up.DataCubeLookUp;
import org.example.model.up.DataCubeLookUpContract;
import org.example.model.up.DataCubeLookUpGosb;
import org.example.model.up.DataCubeLookUpOrganization;
import org.example.model.up.DataCubeLookUpShop;
import org.example.model.up.DataCubeLookUpTb;
import org.example.model.up.DataCubeLookUpTerminal;
import org.example.model.up.PageDataLookUp;
import org.example.model.up.RequestCubeLookUp;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class CubeLookUpService {

    private CubeDataProvider cubeDataProvider = new CubeDataProvider();

    enum LOOK_UP_LEVEL {
        ORGANIZATION,
        CONTRACT,
        SHOP,
        TERMINAL,
        NONE
    }

    public PageDataLookUp getDataLookUp(RequestCubeLookUp request) {
        DataCubeLookUp lookUp = new DataCubeLookUp();
        LOOK_UP_LEVEL lookUpLevel = getLookUpLevel(request);
        if (lookUpLevel == LOOK_UP_LEVEL.NONE) {
            return new PageDataLookUp(0);
        }

        for(DataTb tb : cubeDataProvider.getTb())  {
            for (DataGosb gosb : tb.getGosb()) {
                for(DataOrganization organization: gosb.getOrganization()) {
                    if (lookUpLevel == LOOK_UP_LEVEL.ORGANIZATION && organization.getCode().contains(request.getOrganization())) {
                        addLookUpOrganization(lookUp, tb, gosb, organization);
                        continue;
                    }
                    //
                    for (DataContract contract : organization.getContract()) {
                        if (lookUpLevel == LOOK_UP_LEVEL.CONTRACT && contract.getCode().contains(request.getContract())) {
                            addLookUpContract(lookUp, tb, gosb, organization, contract);
                            continue;
                        }
                        for (DataShop shop : contract.getShop()) {
                            if (lookUpLevel == LOOK_UP_LEVEL.SHOP && shop.getCode().contains(request.getShop())) {
                                addLookUpShop(lookUp, tb, gosb, organization, contract, shop);
                                continue;
                            }
                            for (DataTerminal terminal: shop.getTerminal()) {
                                if (lookUpLevel == LOOK_UP_LEVEL.TERMINAL && terminal.getCode().contains(request.getTerminal())) {
                                    addLookUpTerminal(lookUp, tb, gosb, organization, contract, shop, terminal);
                                    continue;
                                }
                            }
                        }
                    }
                }
            }
        }
        return pagination(lookUp.getTbs(), request.getPageInfo(), lookUpLevel);
    }

    private LOOK_UP_LEVEL getLookUpLevel(RequestCubeLookUp request) {
        if (StringUtils.isNotEmpty(request.getContract())) {
            return LOOK_UP_LEVEL.CONTRACT;
        } else if (StringUtils.isNotEmpty(request.getOrganization())) {
            return LOOK_UP_LEVEL.ORGANIZATION;
        } else if (StringUtils.isNotEmpty(request.getShop())) {
            return LOOK_UP_LEVEL.SHOP;
        } else if (StringUtils.isNotEmpty(request.getTerminal())) {
            return LOOK_UP_LEVEL.TERMINAL;
        }
        return LOOK_UP_LEVEL.NONE;
    }

    private DataCubeLookUpOrganization addLookUpOrganization(DataCubeLookUp lookUp, DataTb tb, DataGosb gosb, DataOrganization organization) {
        DataCubeLookUpGosb lookUpGosb = addLookUpGosb(lookUp, tb, gosb);
        DataCubeLookUpOrganization lookUpOrganization = lookUpGosb.findOrganization(organization.getCode());
        if (lookUpOrganization == null) {
            lookUpOrganization = new DataCubeLookUpOrganization(organization.getCode());
            lookUpGosb.getOrganizations().add(lookUpOrganization);
        }
        return lookUpOrganization;
    }

    private DataCubeLookUpContract addLookUpContract(DataCubeLookUp lookUp, DataTb tb, DataGosb gosb, DataOrganization organization, DataContract contract) {
        DataCubeLookUpOrganization lookUpOrganization = addLookUpOrganization(lookUp, tb, gosb, organization);
        DataCubeLookUpContract lookUpContract = lookUpOrganization.findContract(contract.getCode());
        if (lookUpContract == null) {
            lookUpContract = new DataCubeLookUpContract(contract.getCode());
            lookUpOrganization.getContracts().add(lookUpContract);
        }
        return lookUpContract;
    }

    private DataCubeLookUpShop addLookUpShop(DataCubeLookUp lookUp, DataTb tb, DataGosb gosb, DataOrganization organization, DataContract contract, DataShop shop) {
        DataCubeLookUpContract lookUpContract = addLookUpContract(lookUp, tb, gosb, organization, contract);
        DataCubeLookUpShop lookUpShop = lookUpContract.findShop(shop.getCode());
        if (lookUpShop == null) {
            lookUpShop = new DataCubeLookUpShop(shop.getCode());
            lookUpContract.getShops().add(lookUpShop);
        }
        return lookUpShop;
    }

    private void addLookUpTerminal(DataCubeLookUp lookUp, DataTb tb, DataGosb gosb, DataOrganization organization, DataContract contract, DataShop shop, DataTerminal terminal) {
        DataCubeLookUpShop lookUpShop = addLookUpShop(lookUp, tb, gosb, organization, contract, shop);
        DataCubeLookUpTerminal lookUpTerminal = lookUpShop.findTerminal(terminal.getCode());
        if (lookUpTerminal == null) {
            lookUpTerminal = new DataCubeLookUpTerminal(terminal.getCode());
            lookUpShop.getTerminals().add(lookUpTerminal);
        }
    }

    private DataCubeLookUpGosb addLookUpGosb(DataCubeLookUp lookUp, DataTb tb, DataGosb gosb) {
        DataCubeLookUpTb cubeLookUpTb = lookUp.findTb(tb.getCode());
        if (cubeLookUpTb == null) {
            cubeLookUpTb = new DataCubeLookUpTb(tb.getCode());
            lookUp.getTbs().add(cubeLookUpTb);
        }

        DataCubeLookUpGosb lookUpGosb = cubeLookUpTb.findGosb(gosb.getCode());
        if (lookUpGosb == null) {
            lookUpGosb = new DataCubeLookUpGosb(gosb.getCode());
            cubeLookUpTb.getGosbs().add(lookUpGosb);
        }
        return lookUpGosb;
    }

    private PageDataLookUp pagination(Set<DataCubeLookUpTb> dataCubes, PageInfo pageInfo, LOOK_UP_LEVEL lookUpLevel) {
        int startIndex = (pageInfo.getPageNumber() - 1) * pageInfo.getPageSize();
        long dataCubeSize = getLevelSize(dataCubes, lookUpLevel);
        int endIndex = Math.min(startIndex + pageInfo.getPageSize(), Long.valueOf(dataCubeSize).intValue());

        int size = 0;
        List<DataCubeLookUpTb> tbs = new ArrayList<>();
        for (DataCubeLookUpTb dataCubeLookUpTb : dataCubes) {
            for (DataCubeLookUpGosb dataCubeLookUpGosb : dataCubeLookUpTb.getGosbs()) {
                for (DataCubeLookUpOrganization dataCubeLookUpOrganization : dataCubeLookUpGosb.getOrganizations()) {
                    if (lookUpLevel == LOOK_UP_LEVEL.ORGANIZATION) {
                        if (size >= startIndex && size <= endIndex) {
                            addLookUpOrganization(tbs,
                                    dataCubeLookUpTb,
                                    dataCubeLookUpGosb,
                                    dataCubeLookUpOrganization);
                        }
                        size++;
                        continue;
                    }

                    for (DataCubeLookUpContract dataCubeLookUpContract : dataCubeLookUpOrganization.getContracts()) {
                        if (lookUpLevel == LOOK_UP_LEVEL.CONTRACT) {
                            if (size >= startIndex && size <= endIndex) {
                                addLookUpContract(tbs,
                                        dataCubeLookUpTb,
                                        dataCubeLookUpGosb,
                                        dataCubeLookUpOrganization,
                                        dataCubeLookUpContract);
                            }
                            size++;
                            continue;
                        }

                        for (DataCubeLookUpShop dataCubeLookUpShop : dataCubeLookUpContract.getShops()) {
                            if (lookUpLevel == LOOK_UP_LEVEL.SHOP) {
                                if (size >= startIndex && size <= endIndex) {
                                    addLookUpShop(tbs,
                                            dataCubeLookUpTb,
                                            dataCubeLookUpGosb,
                                            dataCubeLookUpOrganization,
                                            dataCubeLookUpContract,
                                            dataCubeLookUpShop);
                                }
                                size++;
                                continue;
                            }

                            for (DataCubeLookUpTerminal dataCubeLookUpTerminal : dataCubeLookUpShop.getTerminals()) {
                                if (lookUpLevel == LOOK_UP_LEVEL.TERMINAL) {
                                    if (size >= startIndex && size <= endIndex) {
                                        addLookUpTerminal(tbs,
                                                dataCubeLookUpTb,
                                                dataCubeLookUpGosb,
                                                dataCubeLookUpOrganization,
                                                dataCubeLookUpContract,
                                                dataCubeLookUpShop,
                                                dataCubeLookUpTerminal);
                                    }
                                    size++;
                                }
                            }
                        }
                    }
                }
            }
        }

        PageDataLookUp pageData = new PageDataLookUp(size);
        pageData.setDataCubes(tbs);
        return pageData;
    }

    private long getLevelSize(Set<DataCubeLookUpTb> dataCubes, LOOK_UP_LEVEL lookUpLevel) {
        if (lookUpLevel == LOOK_UP_LEVEL.ORGANIZATION) {
            return dataCubes.stream()
                    .flatMap(t-> t.getGosbs().stream())
                    .mapToLong(g -> g.getOrganizations().size())
                    .sum();
        }
        if (lookUpLevel == LOOK_UP_LEVEL.CONTRACT) {
            return dataCubes.stream()
                    .flatMap(t-> t.getGosbs().stream())
                    .flatMap(g -> g.getOrganizations().stream())
                    .mapToLong(o -> o.getContracts().size())
                    .sum();
        }
        if (lookUpLevel == LOOK_UP_LEVEL.SHOP) {
            return dataCubes.stream().flatMap(t-> t.getGosbs().stream())
                    .flatMap(g -> g.getOrganizations().stream())
                    .flatMap(o -> o.getContracts().stream())
                    .mapToLong(o -> o.getShops().size())
                    .sum();
        }
        if (lookUpLevel == LOOK_UP_LEVEL.TERMINAL) {
            return dataCubes.stream().flatMap(t-> t.getGosbs().stream())
                    .flatMap(g -> g.getOrganizations().stream())
                    .flatMap(o -> o.getContracts().stream())
                    .flatMap(c -> c.getShops().stream())
                    .mapToLong(s -> s.getTerminals().size())
                    .sum();
        }
        return 0;
    }

    private DataCubeLookUpOrganization addLookUpOrganization(List<DataCubeLookUpTb> dataCubes, DataCubeLookUpTb dataCubeLookUpTb, DataCubeLookUpGosb dataCubeLookUpGosb, DataCubeLookUpOrganization dataCubeLookUpOrganization) {
        DataCubeLookUpTb tb = dataCubes.stream()
                .filter(d -> d.getCode().equals(dataCubeLookUpTb.getCode()))
                .findFirst()
                .orElse(null);
        if (tb == null) {
            tb = new DataCubeLookUpTb(dataCubeLookUpTb.getCode());
            dataCubes.add(tb);
        }
        DataCubeLookUpGosb gosb = tb.findGosb(dataCubeLookUpGosb.getCode());
        if (gosb==null) {
            gosb = new DataCubeLookUpGosb(dataCubeLookUpGosb.getCode());
            tb.getGosbs().add(gosb);
        }
        DataCubeLookUpOrganization organization = gosb.findOrganization(dataCubeLookUpOrganization.getCode());
        if (organization == null) {
            organization = new DataCubeLookUpOrganization(dataCubeLookUpOrganization.getCode());
            gosb.getOrganizations().add(organization);
        }
        return organization;
    }

    private DataCubeLookUpContract addLookUpContract(List<DataCubeLookUpTb> dataCubes,
                                   DataCubeLookUpTb dataCubeLookUpTb,
                                   DataCubeLookUpGosb dataCubeLookUpGosb,
                                   DataCubeLookUpOrganization dataCubeLookUpOrganization,
                                   DataCubeLookUpContract dataCubeLookUpContract) {
        DataCubeLookUpOrganization organization = addLookUpOrganization(dataCubes, dataCubeLookUpTb, dataCubeLookUpGosb, dataCubeLookUpOrganization);
        DataCubeLookUpContract contract = organization.findContract(dataCubeLookUpContract.getCode());
        if (contract == null) {
            contract = new DataCubeLookUpContract(dataCubeLookUpContract.getCode());
            organization.getContracts().add(contract);
        }
        return contract;
    }

    private DataCubeLookUpShop addLookUpShop(List<DataCubeLookUpTb> tbs, DataCubeLookUpTb dataCubeLookUpTb,
                               DataCubeLookUpGosb dataCubeLookUpGosb,
                               DataCubeLookUpOrganization dataCubeLookUpOrganization,
                               DataCubeLookUpContract dataCubeLookUpContract,
                               DataCubeLookUpShop dataCubeLookUpShop) {
        DataCubeLookUpContract contract = addLookUpContract(tbs, dataCubeLookUpTb, dataCubeLookUpGosb, dataCubeLookUpOrganization, dataCubeLookUpContract);
        DataCubeLookUpShop shop = contract.findShop(dataCubeLookUpShop.getCode());
        if (shop == null) {
            shop = new DataCubeLookUpShop(dataCubeLookUpShop.getCode());
            contract.getShops().add(shop);
        }
        return shop;
    }

    private DataCubeLookUpTerminal addLookUpTerminal(List<DataCubeLookUpTb> dataCubes,
                                   DataCubeLookUpTb dataCubeLookUpTb,
                                   DataCubeLookUpGosb dataCubeLookUpGosb,
                                   DataCubeLookUpOrganization dataCubeLookUpOrganization,
                                   DataCubeLookUpContract dataCubeLookUpContract,
                                   DataCubeLookUpShop dataCubeLookUpShop, DataCubeLookUpTerminal dataCubeLookUpTerminal) {
        DataCubeLookUpShop shop = addLookUpShop(dataCubes, dataCubeLookUpTb, dataCubeLookUpGosb, dataCubeLookUpOrganization, dataCubeLookUpContract, dataCubeLookUpShop);
        DataCubeLookUpTerminal terminal = shop.findTerminal(dataCubeLookUpTerminal.getCode());
        if (terminal == null) {
            terminal = new DataCubeLookUpTerminal(dataCubeLookUpTerminal.getCode());
            shop.getTerminals().add(terminal);
        }
        return terminal;
    }


}
