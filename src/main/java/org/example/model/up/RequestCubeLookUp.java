package org.example.model.up;

import lombok.Data;
import org.example.model.deep.PageInfo;

@Data
public class RequestCubeLookUp {

    private PageInfo pageInfo = new PageInfo();
    private String tb;
    private String gosb;
    private String organization;
    private String contract;
    private String shop;
    private String terminal;

}
