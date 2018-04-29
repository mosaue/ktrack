package com.ktrack.morti.ktrack.contactUtils;

public class Contact {
    private String name, phone, primary;

    public Contact() {
    }

    public Contact(String name, String phone, String primary) {
        this.name = name;
        this.phone = phone;
        this.primary = primary;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrimary() {
        return primary;
    }

    public void setPrimary(String primary) {
        this.primary = primary;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
