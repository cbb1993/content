package com.huanhong.content.model.api;

public class ApiProtocol
{
    public static final String TYPE_REQUEST = "request";
    public static final String TYPE_RESPONSE = "response";
    public static final String TYPE_HEARTBEAT = "heartBeat";

    private static int mID = 0;
    private static ApiProtocol mHeartBeat;
    private String id;
    private String type, method, message;
    private Object data;
    private boolean ok = true;

    public static ApiProtocol getHeartBeat()
    {
        if (mHeartBeat == null)
        {
            mHeartBeat = new ApiProtocol();
            mHeartBeat.setType(TYPE_HEARTBEAT);
        }

        return mHeartBeat;
    }

    public static synchronized String getAutoID()
    {
        int result = mID;
        if (++mID > 65535)
        {
            mID = 0;
        }
        return result + "";
    }

    public String getMethod()
    {
        return method;
    }

    public void setMethod(String method)
    {
        this.method = method;
    }

    public Object getData()
    {
        return data;
    }

    public void setData(Object data)
    {
        this.data = data;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }

    public boolean isOk()
    {
        return ok;
    }

    public void setOk(boolean ok)
    {
        this.ok = ok;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public ApiProtocol getResponse(String msg)
    {
        ApiProtocol apiProtocol = new ApiProtocol();
        apiProtocol.setId(getId());
        apiProtocol.setType(TYPE_RESPONSE);
        apiProtocol.setMethod(getMethod());
        apiProtocol.setOk(true);
        apiProtocol.setMessage(msg);
        return apiProtocol;
    }

    public ApiProtocol getErrorResponse(String msg)
    {
        ApiProtocol apiProtocol = getResponse(msg);
        apiProtocol.setOk(false);
        return apiProtocol;
    }
}
