package de.majaf.voci.entity;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class SingleIdEntity {

    @Id @GeneratedValue
    private long id;

    @Override
    public boolean equals(Object other) {
        if(other.getClass().equals(this.getClass())) {
            SingleIdEntity otherEntity = (SingleIdEntity) other;
            return this.id == otherEntity.getId();
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(this.id);
    }

    public long getId() {
        return id;
    }
}
