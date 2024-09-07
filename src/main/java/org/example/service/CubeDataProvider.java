package org.example.service;

import com.jsoniter.JsonIterator;
import lombok.extern.slf4j.Slf4j;
import org.example.Main;
import org.example.model.DataAllTb;
import org.example.model.DataGosb;
import org.example.model.DataTb;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

@Slf4j
public class CubeDataProvider {

    private final Lock lock = new ReentrantLock();
    private static DataAllTb dataAllTb;

    public CubeDataProvider() {
        init();
    }

    public DataAllTb getAllTb() {
        return new DataAllTb();
    }

    /*
          ТБ
     */
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

    public List<DataTb> fillAllTb() {
        return new ArrayList<>(dataAllTb.getTb());
    }

    public Long getTbGosb(String tb) {
        return dataAllTb.getTb().stream()
                .filter(t -> t.getCode().equals(tb))
                .mapToLong(t -> t.getGosb().size())
                .sum();
    }

    public Long getTbOrganization(String tb) {
        return dataAllTb.getTb().stream()
                .filter(t -> t.getCode().equals(tb))
                .flatMap(t -> t.getGosb().stream())
                .mapToLong(t -> t.getOrganization().size()).sum();
    }

    public Long getTbContract(String tb) {
        return dataAllTb.getTb().stream()
                .filter(t -> t.getCode().equals(tb))
                .flatMap(t -> t.getGosb().stream())
                .flatMap(t -> t.getOrganization().stream())
                .mapToLong(t -> t.getContract().size())
                .sum();
    }

    /*
     *  ТБ - ГОСБ
     */

    public List<DataGosb> fillAllTbGosb(String tb) {
        return dataAllTb.getTb().stream()
                .filter(t -> t.getCode().equals(tb))
                .flatMap(t -> t.getGosb().stream())
                .collect(Collectors.toList());
    }

    public Long getTbGosbOrganization(String tb, String gosb) {
        return dataAllTb.getTb().stream()
                .filter(t -> t.getCode().equals(tb))
                .flatMap(t -> t.getGosb().stream())
                .filter(t -> t.getCode().equals(gosb))
                .mapToLong(t -> t.getOrganization().size())
                .sum();
    }

    public Long getTbGosbContract(String tb, String gosb) {
        return dataAllTb.getTb().stream()
                .filter(t -> t.getCode().equals(tb))
                .flatMap(t -> t.getGosb().stream())
                .filter(t -> t.getCode().equals(gosb))
                .flatMap(t -> t.getOrganization().stream())
                .mapToLong(t -> t.getContract().size()).sum();
    }

    private void init() {
        if (lock.tryLock()) {
            try {
                if (dataAllTb == null) {
                    InputStream resourceAsStream = Main.class.getResourceAsStream("/json/data-cube.json");
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
