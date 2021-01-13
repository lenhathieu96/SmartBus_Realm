package com.smartbus_realm.Model;

public class Company {
   private String name;
   private String fullname;
   private String address;
   private String phone;
   private String tax_code;
   private String print_at;
   private String email;


    public Company(String name, String full_name, String address, String phone, String taxCode, String print_at, String email) {
        this.name = name;
        this.fullname = full_name;
        this.address = address;
        this.phone = phone;
        this.tax_code = taxCode;
        this.print_at = print_at;
        this.email = email;
    }


    public String getName() {
        return name;
    }

    public String getFull_name() {
        return fullname;
    }

    public String getAddress() {
        return address;
    }

    public String getPhone() {
        return phone;
    }

    public String getTaxCode() {
        return tax_code;
    }

    public String getPrint_at() {
        return print_at;
    }

    public String getEmail() {
        return email;
    }
}
