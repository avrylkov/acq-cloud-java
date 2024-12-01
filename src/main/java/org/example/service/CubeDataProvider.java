package org.example.service;

import com.jsoniter.JsonIterator;
import lombok.extern.slf4j.Slf4j;
import org.example.Main;
import org.example.model.deep.DataAllTb;
import org.example.model.deep.DataContract;
import org.example.model.deep.DataCube;
import org.example.model.deep.DataGosb;
import org.example.model.deep.DataOrganization;
import org.example.model.deep.DataShop;
import org.example.model.deep.DataTb;
import org.example.model.deep.DataTerminal;
import org.example.model.deep.PageInfo;

import java.io.IOException;
import java.io.InputStream;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashSet;
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

    public List<DataTb> getTb() {
       return dataAllTb.getTb();
    }

    public Long getAllTb() {
        return Long.valueOf(dataAllTb.getTb().size());
    }

    public Long getAllGosb() {
        return dataAllTb.getTb().stream()
                .mapToLong(t -> t.getGosb().size())
                .sum();
    }


    public Long getAllOrganization() {
        return flatMapTbGosb()
                .mapToLong(t -> t.getOrganization().size()).sum();
    }

    public Long getAllContract() {
        return flatMapTbGosbOrg()
                .mapToLong(t -> t.getContract().size())
                .sum();
    }

    public Long getAllShop() {
        return flatMapTbGosbOrgContract()
                .mapToLong(t -> t.getShop().size())
                .sum();
    }

    public Long getAllTerminal() {
        return flatMapTbGosbOrgContract()
                .flatMap(t -> t.getShop().stream())
                .mapToLong(t -> t.getTerminal().size())
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
        return flatMapGosbOrg(filterTb(tb))
                .mapToLong(t -> t.getContract().size())
                .sum();
    }

    public Long getSumTbShop(String tb) {
        return flatMapGosbOrgContract(filterTb(tb))
                .mapToLong(t -> t.getShop().size())
                .sum();
    }

    public Long getSumTbTerminal(String tb) {
        return flatMapGosbOrgContract(filterTb(tb))
                .flatMap(t -> t.getShop().stream())
                .mapToLong(t -> t.getTerminal().size())
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

    public  <T> List<T> pagination(List<T> dataCubes, PageInfo pageInfo) {
        int startIndex = (pageInfo.getPageNumber() - 1) * pageInfo.getPageSize();
        int endIndex = Math.min(startIndex + pageInfo.getPageSize(), dataCubes.size());

        return dataCubes.stream()
                .skip(startIndex)
                .limit(endIndex - startIndex)
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

    public List<DataCube> fillTbGosbOrgContrShop(String tb, String gosb, String org, String contract) {
        return filterContract(filterOrg(filterGosb(filterTb(tb), gosb), org), contract)
                .flatMap(o -> o.getShop().stream())
                .map(DataShop::getCode)
                .map(DataCube::new)
                .collect(Collectors.toList());
    }

    public List<DataCube> fillTbGosbOrgContrShopTerminal(String tb, String gosb, String org, String contract, String shop) {
        return filterContract(filterOrg(filterGosb(filterTb(tb), gosb), org), contract)
                .flatMap(o -> o.getShop().stream())
                .filter(s -> s.getCode().equals(shop))
                .flatMap(s -> s.getTerminal().stream())
                .map(DataTerminal::getCode)
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

    public Long getSumTbGosbTerminal(String tb, String gosb) {
        return filterGosb(filterTb(tb), gosb)
                .flatMap(t -> t.getOrganization().stream())
                .flatMap(t -> t.getContract().stream())
                .flatMap(t -> t.getShop().stream())
                .mapToLong(t -> t.getTerminal().size()).sum();
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

    public Long getSumTbGosbOrgTerminal(String tb, String gosb, String org) {
        return filterOrganization(filterGosb(filterTb(tb), gosb), org)
                .flatMap(o -> o.getContract().stream())
                .flatMap(o -> o.getShop().stream())
                .mapToLong(c -> c.getTerminal().size()).sum();
    }


    private Stream<DataContract> filterContract(Stream<DataOrganization> organizationStream, String contract) {
         return organizationStream.flatMap(o -> o.getContract().stream())
                .filter(c -> c.getCode().equals(contract));
    }

    private Stream<DataShop> filterShop(Stream<DataContract> contractStream, String shop) {
         return contractStream.flatMap(o -> o.getShop().stream())
                .filter(c -> c.getCode().equals(shop));
    }

    public Long getSumTbGosbOrgContrShop(String tb, String gosb, String org, String contr) {
        return filterContract(filterOrganization(filterGosb(filterTb(tb), gosb), org), contr)
                .mapToLong(c -> c.getShop().size()).sum();
    }

    public Long getSumTbGosbOrgContrTerminal(String tb, String gosb, String org, String contr) {
        return filterContract(filterOrganization(filterGosb(filterTb(tb), gosb), org), contr)
                .flatMap(c -> c.getShop().stream())
                .mapToLong(c -> c.getTerminal().size()).sum();

    }

    public Long getSumTbGosbOrgContrShopTerminal(String tb, String gosb, String org, String contract, String shop) {
        return filterShop(filterContract(filterOrganization(filterGosb(filterTb(tb), gosb), org), contract), shop)
                .mapToLong(c -> c.getTerminal().size()).sum();
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

    private Stream<DataGosb> flatMapTbGosb() {
        return dataAllTb.getTb().stream()
                .flatMap(t -> t.getGosb().stream());
    }

    private Stream<DataOrganization> flatMapTbGosbOrg() {
        return flatMapTbGosb()
                .flatMap(t -> t.getOrganization().stream());
    }

    private Stream<DataContract> flatMapTbGosbOrgContract() {
        return flatMapTbGosbOrg()
                .flatMap(t -> t.getContract().stream());
    }

    private Stream<DataOrganization> flatMapGosbOrg(Stream<DataTb> dataTbStream) {
        return dataTbStream.flatMap(t -> t.getGosb().stream())
                .flatMap(t -> t.getOrganization().stream());
    }

    private Stream<DataContract> flatMapGosbOrgContract(Stream<DataTb> dataTbStream) {
        return flatMapGosbOrg(dataTbStream)
                .flatMap(t -> t.getContract().stream());
    }

    private void init() {
        if (lock.tryLock()) {
            try {
                if (dataAllTb == null) {
                    LocalDateTime start = LocalDateTime.now();
                    log.info("start init");
                    SecureRandom secureRandom = new SecureRandom();
                    Set<String> keys = new HashSet<>(30_000);

                    InputStream resourceAsStream = Main.class.getResourceAsStream("/json/cube.json");
                    String str = new String(resourceAsStream.readAllBytes());
                    dataAllTb = JsonIterator.deserialize(str, DataAllTb.class);
                    dataAllTb.getTb().stream()
                            .flatMap(t -> t.getGosb().stream())
                            .flatMap(g -> g.getOrganization().stream())
                            .peek(o -> {
                                if (keys.contains(o.getCode())) {
                                    o.setCode(o.getCode() + "#" + secureRandom.nextInt());
                                } else {
                                    keys.add(o.getCode());
                                }
                            })
                            .flatMap(o -> o.getContract().stream())
                            .peek(c -> {
                                if (keys.contains(c.getCode())) {
                                    c.setCode(c.getCode() + "#" + secureRandom.nextInt());
                                } else {
                                    keys.add(c.getCode());
                                }
                            })
                            .flatMap(c -> c.getShop().stream())
                            .forEach(s -> {
                                if (keys.contains(s.getCode())) {
                                    s.setCode(s.getCode() + "#" + secureRandom.nextInt());
                                } else {
                                    keys.add(s.getCode());
                                }
                            });
                    Duration duration = Duration.between(start, LocalDateTime.now());
                    log.info("end init duration: {}", duration.toMillis());
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
