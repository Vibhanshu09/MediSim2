package com.example.vibhanshu.medisim;

import java.util.HashMap;

/**
 * Created by Vibhanshu Rai on 27-01-2018.
 */

public class MediGeneric {
    private String generic_name, icd_code, t_c, brand;
    private HashMap<String, Boolean> brandMap;

    public MediGeneric(){} //Default constructor for DatabaseSnapshot.getValue(MediGeneric.class);

    public MediGeneric(String generic_name,String icd_code, String t_c, String brand){
        this.generic_name = generic_name;
        this.icd_code = icd_code;
        this.t_c = t_c;
        this.brand = brand;
    }

    public String getGenericName(){return generic_name;}
    public String getIcd_code(){return icd_code;}
    public String getT_c(){return t_c;}


    public HashMap<String , Boolean> mapBrandNameForGeneric(){
        HashMap<String , Boolean> result = new HashMap<>();
        result.put(brand,true);
        return result;
    }
    public HashMap<String , Object> mapNewGenericDetail(){
        HashMap<String , Object> result = new HashMap<>();
        result.put("generic_name",generic_name);
        result.put("brand",mapBrandNameForGeneric());
        result.put("icd_code",icd_code);
        result.put("t_c",t_c);
        return result;
    }
}
