package com.example.dbapp;

import java.io.Serializable;

// Serialise objects
// send data as an object.
// pickle module provides this function in python

public class Item implements Serializable {

    private int itemid;
    private String itemname;
    private int price;
    private String description;
    private String imgurl;

    // Popular languages and frameworks today
    // automates the process once the variables are declared.

    // constructor without parameters - for general use
    public Item(){
    }
    // constructor with parameters
    // for testing and reading data from external sources
    // for quick object creation
    public Item(int itemid, String itemname, int price, String description, String imgurl){
        this.itemid = itemid;
        this.itemname = itemname;
        this.price = price;
        this.description = description;
        this.imgurl = imgurl;

    }

    public int getItemid() {
        return itemid;
    }

    public String getItemname() {
        return itemname;
    }

    public int getPrice() {
        return price;
    }

    public String getDescription() {
        return description;
    }

    public String getImgurl() {
        return imgurl;
    }

    public void setItemid(int itemid) {
        this.itemid = itemid;
    }

    public void setItemname(String itemname) {
        this.itemname = itemname;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImgurl(String imgurl) {
        this.imgurl = imgurl;
    }

    // method for debugging
    // 인스턴스 이름을 출력하는 메소드에 대입하면 자동으로 호출됨...?
    // __str__() does the job in python
    // li = [1, 2, 3, 4] print(li) => print(li.__str__())
    @Override
    public String toString() {
        return "Item{" +
                "itemid=" + itemid +
                ", itemname='" + itemname + '\'' +
                ", price=" + price +
                ", description='" + description + '\'' +
                ", imgurl='" + imgurl + '\'' +
                '}';
    }
}