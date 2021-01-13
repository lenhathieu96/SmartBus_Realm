package com.smartbus_realm.Model;

public class Ticket {
    String start_station;
    String arrive_station;
    String order_code;
    String sign;
    String deduction;
    String discount;
    String balance;
    String time;
    String price;
    String allocation;
    Boolean the_tra_truoc;


    public Ticket(String start_station, String arrive_station, String order_code, String sign, String deduction, String discount, String balance, String time, Boolean the_tra_truoc) {
        this.start_station = start_station;
        this.arrive_station = arrive_station;
        this.order_code = order_code;
        this.sign = sign;
        this.deduction = deduction;
        this.discount = discount;
        this.balance = balance;
        this.time = time;
        this.the_tra_truoc = the_tra_truoc;
    }

    public String getStart_station() {
        return start_station;
    }

    public String getArrive_station() {
        return arrive_station;
    }

    public String getOrder_code() {
        return order_code;
    }

    public String getSign() {
        return sign;
    }

    public String getDeduction() {
        return deduction;
    }

    public String getDiscount() {
        return discount;
    }

    public String getBalance() {
        return balance;
    }

    public String getTime() {
        return time;
    }

    public Boolean getThe_tra_truoc() {
        return the_tra_truoc;
    }

    public String getPrice() {
        return price;
    }

    public String getAllocation() {
        return allocation;
    }
}
