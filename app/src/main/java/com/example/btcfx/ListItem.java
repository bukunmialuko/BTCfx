package com.example.btcfx;


public class ListItem {

    private String currency;
    private String value;

    //constructor to initialize the currency and value for the ListItem class
    public ListItem(String currency, String value) {
        this.currency = currency;
        this.value = value;
    }

    //the get method that returns a string of the currency
    public String getCurrency() {
        return currency;
    }

    //the get method that returns a string of 1 Bitcoin equivalent
    //of the currency.
    public String getValue() {
        return value;
    }
}
