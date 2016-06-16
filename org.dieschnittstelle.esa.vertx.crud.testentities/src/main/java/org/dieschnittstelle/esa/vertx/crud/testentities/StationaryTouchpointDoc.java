package org.dieschnittstelle.esa.vertx.crud.testentities;

import org.dieschnittstelle.jee.esa.entities.crm.Address;
import org.dieschnittstelle.jee.esa.entities.crm.StationaryTouchpoint;

import javax.persistence.Entity;

/**
 * Created by master on 07.06.16.
 */
@Entity
public class StationaryTouchpointDoc extends StationaryTouchpoint {

    private String _id;

    public StationaryTouchpointDoc() {

    }

    public StationaryTouchpointDoc(int id, String name, Address location) {
        super(id,name,location);
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String toString() {
        return "{" + super.toString() + ", " + this._id + "}";
    }

}
