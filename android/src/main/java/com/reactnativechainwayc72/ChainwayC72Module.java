package com.reactnativechainwayc72;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.module.annotations.ReactModule;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.WritableNativeMap;


import com.rscja.deviceapi.RFIDWithUHFUART;
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
    public void initializeReader(Promise promise) {
        try {
            connect();
            promise.resolve(true);
        } catch (Exception err) {
            promise.reject(err);
        }
    }

    @ReactMethod
    public void deinitializeReader(Promise promise) {
        try {
            disconnect();
            promise.resolve(true);
        } catch (Exception err) {
            promise.reject(err);
        }
    }

}
