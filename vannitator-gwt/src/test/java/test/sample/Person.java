package test.sample;

import java.util.Date;

import org.soqqo.vannitator.annotation.gwt.GenRequestProxy;

@GenRequestProxy
public class Person {

    private String name;
    
    private Date dateOfBirth;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
}
