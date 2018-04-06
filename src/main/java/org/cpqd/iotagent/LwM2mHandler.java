package org.cpqd.iotagent;


/*
This helper class encapsulate all requests to devices.
Any device registration is expected to be active and working
Checking if registrations are active is not this class responsibility
In case of failure any request should fail silently
 */

import com.google.gson.Gson;
import org.eclipse.leshan.core.request.ObserveRequest;
import org.apache.log4j.Logger;
import org.eclipse.leshan.core.request.ReadRequest;
import org.eclipse.leshan.core.request.WriteRequest;
import org.eclipse.leshan.core.response.ObserveResponse;
import org.eclipse.leshan.core.response.ReadResponse;
import org.eclipse.leshan.core.response.WriteResponse;
import org.eclipse.leshan.server.LwM2mServer;
import org.eclipse.leshan.server.registration.Registration;

public class LwM2mHandler {
    private Logger mLogger = Logger.getLogger(LwM2mHandler.class);

    private Gson gson;
    private LwM2mServer server;
    private long readTimout;
    private long writeTimeout;

    public LwM2mHandler(LwM2mServer server, Gson gson) {
        this.readTimout = 5000;
        this.writeTimeout = 5000;
        this.server = server;
        this.gson = gson;
    }

    public String ReadResource(Registration registration, int objectId, int objectInstanceId, int resourceId) {
        String value = "";
        try {
            ReadResponse response = server.send(registration, new ReadRequest(objectId, objectInstanceId, resourceId), readTimout);
            value = gson.toJsonTree(response.getContent()).getAsJsonObject().get("value").toString().replaceAll("^\"|\"$", "");
        } catch (Exception e) {
            // Todo(jsiloto): Log errors here
            e.printStackTrace();
            mLogger.error(e);
        }
        return value;
    }

    public void ObserveResource(Registration registration, int objectId, int objectInstanceId, int resourceId) {
        try {
            ObserveResponse response = server.send(registration, new ObserveRequest(objectId, objectInstanceId, resourceId), readTimout);
        } catch (Exception e) {
            // Todo(jsiloto): Log errors here
            e.printStackTrace();
            mLogger.error(e);
        }
    }


    public void WriteResource(Registration registration, int objectId, int objectInstanceId, int resourceId, String value) {
        try {
            WriteResponse response = server.send(registration, new WriteRequest(objectId, objectInstanceId, resourceId, value), writeTimeout);
        } catch (Exception e) {
            // Todo(jsiloto): Log errors here
            e.printStackTrace();
            mLogger.error(e);
        }
    }


}
