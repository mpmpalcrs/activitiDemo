package com.fitechsoft.activiti.domain;

import javax.persistence.*;

/**
 * This class represents all passive objects in the system.
 *
 * @author Chun Cao
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class FDObject extends FDEntity {

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    private String oid;

}
