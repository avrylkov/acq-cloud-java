package org.example.model.deep;

import lombok.Data;
import org.example.model.RequestCubeCode;

@Data
public class RequestCubeDeep {

    private PageInfo pageInfo = new PageInfo();
    private RequestCubeCode code;
    private String codeFilter;
    private String tb;
    private String gosb;
    private String org;

}
