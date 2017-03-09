package com.twinc.halmato.lottogo.model;

import java.util.Date;

/**
 * Created by Tiaan on 3/2/2017.
 */

public class Draw
{
    public String getResult()
    {
        return result;
    }

    public void setResult(String result)
    {
        this.result = result;
    }

    public String getDate()
    {
        return date;
    }

    public void setDate(String date)
    {
        this.date = date;
    }

    private String result;
    private String date;

    public Draw() {

    }
    public Draw(String result) {
        this.date = getCurrentDate();
        this.result = result;
    }

    private String getCurrentDate() {
        return "2017-05-11";
    }
}
