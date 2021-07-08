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

    private static final String UHF_READER_POWER_ON_ERROR = "UHF_READER_POWER_ON_ERROR";
    private static final String UHF_READER_INIT_ERROR = "UHF_READER_INIT_ERROR";
    private static final String UHF_READER_READ_ERROR = "UHF_READER_READ_ERROR";
    private static final String UHF_READER_RELEASE_ERROR = "UHF_READER_RELEASE_ERROR";
    private static final String UHF_READER_WRITE_ERROR = "UHF_READER_WRITE_ERROR";
    private static final String UHF_READER_OTHER_ERROR = "UHF_READER_OTHER_ERROR";

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

}
