package org.example.model.up;

import lombok.Data;
import org.example.model.deep.PageInfo;
import org.example.service.CubeLookUpService;

@Data
public class RequestCubeLookUp {

    private PageInfo pageInfo = new PageInfo();
    private LOOK_UP_LEVEL level;
    private String code;

    public enum LOOK_UP_LEVEL {
        TB,
        GOSB,
        ORGANIZATION,
        CONTRACT,
        SHOP,
        TERMINAL,
        NONE
    }
}
