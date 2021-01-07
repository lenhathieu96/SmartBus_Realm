package com.smartbus_realm.Model;

public class Vehicle {
   private  int company_id;
   private String  company_name;


   private String  imei;
   private int  vehicle_id;
   private String  license_plates;
   private String  direction_name;
   private int  direction;
   private int  route_number;
   private int is_running;
   private long timestamp;
   private String coordinates;
   private float speed;

   private String  user;
   private String  phone_user;
   private String  sub_user;
   private String  phone_sub_user;

    public Vehicle(int company_id, String company_name, String imei, int vehicle_id, String license_plates, String direction_name, int direction, int route_number, int is_running, String user, String phone_user, String sub_user, String phone_sub_user) {
        this.company_id = company_id;
        this.company_name = company_name;
        this.imei = imei;
        this.vehicle_id = vehicle_id;
        this.license_plates = license_plates;
        this.direction_name = direction_name;
        this.direction = direction;
        this.route_number = route_number;
        this.is_running = is_running;
        this.user = user;
        this.phone_user = phone_user;
        this.sub_user = sub_user;
        this.phone_sub_user = phone_sub_user;
    }

    public Vehicle() {
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void setCoordinates(String coordinates) {
        this.coordinates = coordinates;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }
}
