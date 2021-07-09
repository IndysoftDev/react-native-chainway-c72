package com.reactnativechainwayc72;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.module.annotations.ReactModule;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.Arguments;

import com.rscja.deviceapi.RFIDWithUHFUART;
import com.rscja.deviceapi.entity.UHFTAGInfo;
import com.rscja.deviceapi.interfaces.ConnectionStatus;
import com.rscja.deviceapi.exception.ConfigurationException;

@ReactModule(name = ChainwayC72Module.NAME)
public class ChainwayC72Module extends ReactContextBaseJavaModule implements LifecycleEventListener {
    public static final String NAME = "ChainwayC72";
    private final ReactApplicationContext reactContext;

    private RFIDWithUHFUART mReader;

    public ChainwayC72Module(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
        this.reactContext.addLifecycleEventListener(this);
    }

    @Override
    @NonNull
    public String getName() {
        return NAME;
    }

    @Override
    public void onHostDestroy() {
        disconnect();
    }

    @Override
    public void onHostResume() {
    }

    @Override
    public void onHostPause() {
    }

    private void connect() {
        try {
            if (mReader != null) {
                disconnect();
            }

            //RFID
            if (mReader == null) {
                mReader = RFIDWithUHFUART.getInstance();
            }

            mReader.init();
            
        } catch (ConfigurationException e) {
            e.printStackTrace();
        }
    }

    private void disconnect() {
        if (mReader != null) {
            mReader.free();
            mReader = null;
        }
    }

    @ReactMethod
    public void initReader(Promise promise) {
        try {
            connect();
            promise.resolve(true);
        } catch (Exception err) {
            promise.reject(err);
        }
    }

    @ReactMethod
    public void deinitReader(Promise promise) {
        try {
            disconnect();
            promise.resolve(true);
        } catch (Exception err) {
            promise.reject(err);
        }
    }

    @ReactMethod
    public void isReaderInit(Promise promise) {
        try {
            if (mReader != null) {
                promise.resolve(mReader.getConnectStatus() == ConnectionStatus.CONNECTED);
            } else {
                promise.resolve(false);
            }
        } catch (Exception err) {
            promise.reject(err);
        }
    }

    @ReactMethod
    public void readSingleTag(final Promise promise) {
        try {
            UHFTAGInfo tag = mReader.inventorySingleTag();

            if(tag != null) {
                WritableMap map = Arguments.createMap();
                map.putString("epc", tag.getEPC());
                map.putString("rssi", tag.getRssi());
                promise.resolve(map);
            } else {
                promise.reject("READ_ERROR", "READ FAILED");
            }

        } catch (Exception ex) {
            promise.reject("READ_ERROR", ex);
        }
    }

    @ReactMethod
    public void readPower(final Promise promise) {
        try {
            int uhfPower = mReader.getPower();
            if(uhfPower>=0) {
                promise.resolve(uhfPower);
            } else {
                promise.reject("POWER_ERROR", "INVALID POWER VALUE");
            }
        } catch (Exception ex) {
            promise.reject("POWER_ERROR", ex.getLocalizedMessage());
        }
    }

    @ReactMethod
    public void setPower(int powerValue, final Promise promise) {
        try {
            Boolean uhfPowerState = mReader.setPower(powerValue);
            if(uhfPowerState)
                promise.resolve(uhfPowerState);
            else
                promise.reject("POWER_ERROR", "Can't Change Power");
        } catch (Exception ex) {
            promise.reject("POWER_ERROR", ex.getLocalizedMessage());
        }
    }

}
