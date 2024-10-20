package org.example.service;

import com.jsoniter.JsonIterator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.example.Main;
import org.example.model.RequestCubeLookUp;
import org.example.model.deep.DataAllTb;
import org.example.model.deep.DataContract;
import org.example.model.deep.DataCube;
import org.example.model.deep.DataGosb;
import org.example.model.deep.DataOrganization;
import org.example.model.deep.DataTb;
import org.example.model.up.DataCubeLookUp;
import org.example.model.up.DataCubeLookUpContract;
import org.example.model.up.DataCubeLookUpGosb;
import org.example.model.up.DataCubeLookUpOrganization;
import org.example.model.up.DataCubeLookUpTb;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class CubeDataProvider implements DataProvider {

    private final Lock lock = new ReentrantLock();
    private static DataAllTb dataAllTb;

    public CubeDataProvider() {
        init();
    }

    /*
          ТБ
     */

    public Long getAllTb() {
        return Long.valueOf(dataAllTb.getTb().size());
    }

    public Long getAllGosb() {
        return dataAllTb.getTb().stream()
                .mapToLong(t -> t.getGosb().size())
                .sum();
    }


    public Long getAllOrganization() {
        return dataAllTb.getTb().stream()
                .flatMap(t -> t.getGosb().stream())
                .mapToLong(t -> t.getOrganization().size()).sum();
    }

    public Long getAllContract() {
        return dataAllTb.getTb().stream()
                .flatMap(t -> t.getGosb().stream())
                .flatMap(t -> t.getOrganization().stream())
                .mapToLong(t -> t.getContract().size())
                .sum();
    }

    public Long getAllShop() {
        return dataAllTb.getTb().stream()
                .flatMap(t -> t.getGosb().stream())
                .flatMap(t -> t.getOrganization().stream())
                .flatMap(t -> t.getContract().stream())
                .mapToLong(t -> t.getShop().size())
                .sum();
    }

    public List<DataCube> fillAllTb() {
        return dataAllTb.getTb().stream()
                .map(DataTb::getCode)
                .map(DataCube::new)
                .collect(Collectors.toList());
    }

    public Long getTbGosb(String tb) {
        return filterTb(tb)
                .mapToLong(t -> t.getGosb().size())
                .sum();
    }

    public Long getSumTbOrganization(String tb) {
        return filterTb(tb)
                .flatMap(t -> t.getGosb().stream())
                .mapToLong(t -> t.getOrganization().size()).sum();
    }

    public Long getSumTbContract(String tb) {
        return filterTb(tb)
                .flatMap(t -> t.getGosb().stream())
                .flatMap(t -> t.getOrganization().stream())
                .mapToLong(t -> t.getContract().size())
                .sum();
    }

    public Long getSumTbShop(String tb) {
        return filterTb(tb)
                .flatMap(t -> t.getGosb().stream())
                .flatMap(t -> t.getOrganization().stream())
                .flatMap(t -> t.getContract().stream())
                .mapToLong(t -> t.getShop().size())
                .sum();
    }

    /*
     *  ТБ - ГОСБ
     */

    public List<DataCube> fillTbGosb(String tb) {
        return filterTb(tb)
                .flatMap(t -> t.getGosb().stream())
                .map(DataGosb::getCode)
                .map(DataCube::new)
                .collect(Collectors.toList());
    }

    public List<DataCube> fillTbGosbOrg(String tb, String gosb) {
        return filterGosb(filterTb(tb), gosb)
                .flatMap(g -> g.getOrganization().stream())
                .map(DataOrganization::getCode)
                .map(DataCube::new)
                .collect(Collectors.toList());
    }

    private Stream<DataOrganization> filterOrg(Stream<DataGosb> dataGosbStream, String org) {
        return dataGosbStream.flatMap(g -> g.getOrganization().stream())
                .filter(o -> o.getCode().equals(org));
    }

    public List<DataCube> fillTbGosbOrgContr(String tb, String gosb, String org) {
        return filterOrg(filterGosb(filterTb(tb), gosb), org)
                .flatMap(o -> o.getContract().stream())
                .map(DataContract::getCode)
                .map(DataCube::new)
                .collect(Collectors.toList());
    }


    public Long getSumTbGosbOrganization(String tb, String gosb) {
        return  filterGosb(filterTb(tb), gosb)
                .mapToLong(g -> g.getOrganization().size())
                .sum();
    }

    public Long getSumTbGosbContract(String tb, String gosb) {
        return filterGosb(filterTb(tb), gosb)
                .flatMap(g -> g.getOrganization().stream())
                .mapToLong(o -> o.getContract().size()).sum();
    }

    public Long getSumTbGosbShop(String tb, String gosb) {
        return filterGosb(filterTb(tb), gosb)
                .flatMap(t -> t.getOrganization().stream())
                .flatMap(t -> t.getContract().stream())
                .mapToLong(t -> t.getShop().size()).sum();
    }

    public Long getSumTbGosbOrgContract(String tb, String gosb, String org) {
        return filterOrganization(filterGosb(filterTb(tb), gosb), org)
                .mapToLong(o -> o.getContract().size()).sum();
    }

    public Long getSumTbGosbOrgShop(String tb, String gosb, String org) {
        return filterOrganization(filterGosb(filterTb(tb), gosb), org)
                .flatMap(o -> o.getContract().stream())
                .mapToLong(c -> c.getShop().size()).sum();
    }

    private Stream<DataContract> filterContract(Stream<DataOrganization> organizationStream, String contract) {
         return organizationStream.flatMap(o -> o.getContract().stream())
                .filter(c -> c.getCode().equals(contract));
    }

    public Long getSumTbGosbOrgContrShop(String tb, String gosb, String org, String contr) {
        return filterContract(filterOrganization(filterGosb(filterTb(tb), gosb), org), contr)
                .mapToLong(c -> c.getShop().size()).sum();
    }

    private Stream<DataTb> filterTb(String tb) {
        return dataAllTb.getTb().stream()
                .filter(t -> t.getCode().equals(tb));
    }

    private Stream<DataGosb> filterGosb(Stream<DataTb> dataTbStream, String gosb) {
        return dataTbStream.flatMap(t -> t.getGosb().stream())
                .filter(g -> g.getCode().equals(gosb));
    }

    private Stream<DataOrganization> filterOrganization(Stream<DataGosb> dataGosbStream, String organization) {
        return dataGosbStream.flatMap(g -> g.getOrganization().stream())
                .filter(o -> o.getCode().equals(organization));
    }


    enum LOOK_UP_LEVEL {
        ORGANIZATION,
        CONTRACT,
        NONE
    }

    public Set<DataCubeLookUpTb> getDataLookUpByContract(RequestCubeLookUp request) {
        DataCubeLookUp lookUp = new DataCubeLookUp();
        LOOK_UP_LEVEL lookUpLevel = LOOK_UP_LEVEL.NONE;
        if (StringUtils.isNotEmpty(request.getContract())) {
            lookUpLevel = LOOK_UP_LEVEL.CONTRACT;
        } else if (StringUtils.isNotEmpty(request.getOrganization())) {
            lookUpLevel = LOOK_UP_LEVEL.ORGANIZATION;
        }

        for(DataTb tb : dataAllTb.getTb())  {
            for (DataGosb gosb : tb.getGosb()) {
                for(DataOrganization organization: gosb.getOrganization()) {
                    if (lookUpLevel == LOOK_UP_LEVEL.ORGANIZATION && organization.getCode().contains(request.getOrganization())) {
                        DataCubeLookUpGosb lookUpGosb = makeTbAndGosb(lookUp, tb, gosb);
                        DataCubeLookUpOrganization lookUpOrganization = lookUpGosb.findOrganization(organization.getCode());
                        if (lookUpOrganization == null) {
                            lookUpOrganization = new DataCubeLookUpOrganization(organization.getCode());
                            lookUpGosb.getOrganizations().add(lookUpOrganization);
                        }
                        continue;
                    }
                    //
                    for (DataContract contract : organization.getContract()) {
                        if (lookUpLevel == LOOK_UP_LEVEL.CONTRACT && contract.getCode().contains(request.getContract())) {
                            DataCubeLookUpOrganization lookUpOrganization = makeTbAndGosbAndOrganization(lookUp, tb, gosb, organization);
                            DataCubeLookUpContract lookUpContract = lookUpOrganization.findContract(contract.getCode());
                            if (lookUpContract == null) {
                                lookUpContract = new DataCubeLookUpContract(contract.getCode());
                                lookUpOrganization.getContracts().add(lookUpContract);

                            }
                            continue;
                        }
                    }
                }
            }
        }
        return lookUp.getTbs();
    }

    private DataCubeLookUpGosb makeTbAndGosb(DataCubeLookUp lookUp, DataTb tb, DataGosb gosb) {
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

    private DataCubeLookUpOrganization makeTbAndGosbAndOrganization(DataCubeLookUp lookUp, DataTb tb, DataGosb gosb, DataOrganization organization) {
        DataCubeLookUpGosb lookUpGosb = makeTbAndGosb(lookUp, tb, gosb);
        DataCubeLookUpOrganization lookUpOrganization = lookUpGosb.findOrganization(organization.getCode());
        if (lookUpOrganization == null) {
            lookUpOrganization = new DataCubeLookUpOrganization(organization.getCode());
            lookUpGosb.getOrganizations().add(lookUpOrganization);
        }
        return lookUpOrganization;
    }

    private void init() {
        if (lock.tryLock()) {
            try {
                if (dataAllTb == null) {
                    InputStream resourceAsStream = Main.class.getResourceAsStream("/json/cube.json");
                    String str = new String(resourceAsStream.readAllBytes());
                    dataAllTb = JsonIterator.deserialize(str, DataAllTb.class);
                }
            } catch (IOException e) {
                log.error("init", e);
            } finally {
                lock.unlock();
            }
        } else {
            dataAllTb = new DataAllTb();
        }
    }

}
