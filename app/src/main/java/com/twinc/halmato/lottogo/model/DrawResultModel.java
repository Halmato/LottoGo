package com.twinc.halmato.lottogo.model;


/**
 * Created by Tiaan on 3/2/2017.
 */

public class DrawResultModel
{
    public String getResultAsString()
    {
        return result.toString();
    }

    public String getResultOfBallByIndex(int indexOfBall) {
        return resultArray[indexOfBall];
    }

    /*public void setResult(String[] result)
    {
        this.result = result;
    }*/

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
    private String[] resultArray;

    // Needed for Gson extraction
    public DrawResultModel() {}

    public DrawResultModel(String result) {
        this.date = getCurrentDate();
        this.result = result;
        this.resultArray = parseResultAsStringToArray(result);
    }

    private String[] parseResultAsStringToArray(String result) {

        String[] resultsAsArray = new String[6];

        for (int i = 0; i < 6; i++) {

            String ballResult = "";

            for (int j = 0; j < 2; j++) {
                int numberIndex = (i * 2) + j;
                ballResult += result.charAt(numberIndex);
            }

            resultsAsArray[i] = ballResult;
        }

        return resultsAsArray;
    }

    private String getCurrentDate() {
        // TODO: 3/31/2017  
        return "2017-05-11";
    }
}
