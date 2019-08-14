package com.zyn.lib.api;

/**
 * Created by tuka2401 on 2017/2/9.
 */

public class ApiResponse
{
    private String stateCode;
    private String data;
    private String dataMap;
    private String message;

    public String getStateCode()
    {
        return stateCode;
    }

    public void setStateCode(String stateCode)
    {
        this.stateCode = stateCode;
    }

    public String getData()
    {
        return data;
    }

    public void setData(String data)
    {
        this.data = data;
    }

    public String getDataMap()
    {
        return dataMap;
    }

    public void setDataMap(String dataMap)
    {
        this.dataMap = dataMap;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }
}
