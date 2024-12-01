package org.example;

import lombok.extern.slf4j.Slf4j;
import org.example.model.up.PageDataLookUp;
import org.example.model.up.RequestCubeLookUp;
import org.example.model.up.DataCubeLookUpTb;
import org.example.service.CubeLookUpService;

import java.util.Collections;
import java.util.Set;
import java.util.function.Function;

@Slf4j
public class HandlerLookUp implements Function<RequestCubeLookUp, PageDataLookUp> {

    private final CubeLookUpService cubeLookUpService = new CubeLookUpService();

    @Override
    public PageDataLookUp apply(RequestCubeLookUp requestCubeLookUp) {
        try {
            return cubeLookUpService.getDataLookUp(requestCubeLookUp);
        } catch (Exception e) {
            log.error("Exception", e);
        }
        return new PageDataLookUp(0);
    }
}