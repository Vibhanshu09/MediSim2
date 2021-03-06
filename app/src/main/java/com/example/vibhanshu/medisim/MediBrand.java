package com.example.vibhanshu.medisim;

import com.google.firebase.database.Exclude;

import java.util.HashMap;

/**
 * Created by Vibhanshu Rai on 26-01-2018.
 */

public class MediBrand {

    private String name;
    private String company;
    private String generic;
    private String type;
    private double price;
    private String quantity;
    private String unit;
    private String state;

    public MediBrand(){} //Default constructor for DatabaseSnapshot.getValue(MediBrand.class);

    public MediBrand(String name,String company,String genericName, String type, double price, String quantity, String unit,String state){
        this.name = name;
        this.company = company;
        this.generic = genericName;
        this.type = type;
        this.price = price;
        this.quantity = quantity;
        this.unit = unit;
        this.state = state;
    }

    public void setCompany(String company){this.company = company;}
    public void setType(String type){this.type = type;}
    public void setPrice(double price){this.price = price;}
    public void setQuantity(String quantity){this.quantity = quantity;}
    public void setUnit(String unit){this.unit = unit;}
    public void setState(String state){this.state = state;}

    public String getName(){return name;}
    public String getCompany(){return  company;}
    public String getGeneric(){return generic;}
    public String getType(){return type;}
    public double getPrice(){return price;}
    public String getQuantity(){return quantity;}
    public String getUnit(){return unit;}
    public String getState(){return state;}

    @Exclude
    public HashMap<String, Object> mapMediBrand(){
        HashMap<String, Object> result = new HashMap<>();
        result.put("name",name);
        result.put("company",company);
        result.put("generic",generic);
        result.put("type",type);
        result.put("price",price);
        result.put("quantity",quantity);
        result.put("unit",unit);
        result.put("state",state);
        return result;
    }

}
