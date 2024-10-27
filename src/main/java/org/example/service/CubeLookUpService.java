package org.example.service;

import org.apache.commons.lang3.StringUtils;
import org.example.model.RequestCubeLookUp;
import org.example.model.deep.DataContract;
import org.example.model.deep.DataGosb;
import org.example.model.deep.DataOrganization;
import org.example.model.deep.DataTb;
import org.example.model.up.DataCubeLookUp;
import org.example.model.up.DataCubeLookUpContract;
import org.example.model.up.DataCubeLookUpGosb;
import org.example.model.up.DataCubeLookUpOrganization;
import org.example.model.up.DataCubeLookUpTb;

import java.util.Set;

public class CubeLookUpService {

    private CubeDataProvider cubeDataProvider = new CubeDataProvider();

    enum LOOK_UP_LEVEL {
        ORGANIZATION,
        CONTRACT,
        NONE
    }

    public Set<DataCubeLookUpTb> getDataLookUp(RequestCubeLookUp request) {
        DataCubeLookUp lookUp = new DataCubeLookUp();
        LOOK_UP_LEVEL lookUpLevel = LOOK_UP_LEVEL.NONE;
        if (StringUtils.isNotEmpty(request.getContract())) {
            lookUpLevel = LOOK_UP_LEVEL.CONTRACT;
        } else if (StringUtils.isNotEmpty(request.getOrganization())) {
            lookUpLevel = LOOK_UP_LEVEL.ORGANIZATION;
        }

        for(DataTb tb : cubeDataProvider.getTb())  {
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

    private DataCubeLookUpOrganization makeTbAndGosbAndOrganization(DataCubeLookUp lookUp, DataTb tb, DataGosb gosb, DataOrganization organization) {
        DataCubeLookUpGosb lookUpGosb = makeTbAndGosb(lookUp, tb, gosb);
        DataCubeLookUpOrganization lookUpOrganization = lookUpGosb.findOrganization(organization.getCode());
        if (lookUpOrganization == null) {
            lookUpOrganization = new DataCubeLookUpOrganization(organization.getCode());
            lookUpGosb.getOrganizations().add(lookUpOrganization);
        }
        return lookUpOrganization;
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


}
