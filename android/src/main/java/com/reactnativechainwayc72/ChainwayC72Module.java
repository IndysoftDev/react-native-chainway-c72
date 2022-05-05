package com.indytrack;

import androidx.annotation.NonNull;
import java.util.ArrayList;
import java.util.List;
import androidx.annotation.Nullable;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Context;
import android.os.Build;

import java.util.Map;
import java.util.HashMap;
import java.util.UUID;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.module.annotations.ReactModule;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import com.rscja.deviceapi.RFIDWithUHFUART;
import com.rscja.deviceapi.entity.UHFTAGInfo;
import com.rscja.deviceapi.interfaces.IUHF;
import com.rscja.deviceapi.interfaces.ConnectionStatus;
import com.rscja.deviceapi.exception.ConfigurationException;
import com.barcode.BarcodeUtility;
import com.zebra.adc.decoder.Barcode2DWithSoft;
import com.rscja.deviceapi.RFIDWithUHF.BankEnum;

@ReactModule(name = ChainwayC72Module.NAME)
public class ChainwayC72Module extends ReactContextBaseJavaModule implements LifecycleEventListener {
    public static final String NAME = "ChainwayC72";
    private final ReactApplicationContext reactContext;

    private RFIDWithUHFUART mReader;
    private Boolean uhfInventoryStatus = false;
    private List<String> scannedTags = new ArrayList<>();
    private static BarcodeUtility barcodeUtility = null;
    private static BarcodeDataReceiver barcodeDataReceiver = null;

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
        uhfInventoryStatus = false;
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

            // RFID
            if (mReader == null) {
                mReader = RFIDWithUHFUART.getInstance();
            }

            mReader.init();
            mReader.setEPCAndTIDUserMode(0, 8);
            // Barcode
            if (barcodeUtility == null) {
                barcodeUtility = BarcodeUtility.getInstance();
            }

            barcodeUtility.setOutputMode(this.reactContext, 2);// Broadcast receive data
            barcodeUtility.setScanResultBroadcast(this.reactContext, "com.scanner.broadcast", "data"); // Set Broadcast
            barcodeUtility.open(this.reactContext, BarcodeUtility.ModuleType.BARCODE_2D);
            barcodeUtility.setReleaseScan(this.reactContext, true);
            barcodeUtility.setScanFailureBroadcast(this.reactContext, false);
            barcodeUtility.enableContinuousScan(this.reactContext, true);
            barcodeUtility.enablePlayFailureSound(this.reactContext, true);
            barcodeUtility.enablePlaySuccessSound(this.reactContext, true);
            barcodeUtility.enableEnter(this.reactContext, false);
            barcodeUtility.setBarcodeEncodingFormat(this.reactContext, 1);

            if (barcodeDataReceiver == null) {
                barcodeDataReceiver = new BarcodeDataReceiver();
                IntentFilter intentFilter = new IntentFilter();
                intentFilter.addAction("com.scanner.broadcast");
                this.reactContext.registerReceiver(barcodeDataReceiver, intentFilter);
            }

        } catch (ConfigurationException e) {
            e.printStackTrace();
        }
    }

    private void disconnect() {
        if (mReader != null) {
            mReader.free();
            mReader = null;
        }

        if (barcodeUtility != null) {
            barcodeUtility.close(this.reactContext, BarcodeUtility.ModuleType.BARCODE_2D);
            barcodeUtility = null;
        }

        if (barcodeDataReceiver != null) {
            this.reactContext.unregisterReceiver(barcodeDataReceiver);
            barcodeDataReceiver = null;
        }
    }

    @ReactMethod
    public void initReader(Promise promise) {
        try {
            connect();
            promise.resolve(true);
        } catch (Exception err) {
            promise.reject(err.getLocalizedMessage());
        }
    }

    @ReactMethod
    public void deinitReader(Promise promise) {
        try {
            disconnect();
            promise.resolve(true);
        } catch (Exception err) {
            promise.reject(err.getLocalizedMessage());
        }
    }

    @ReactMethod
    public void getDeviceModel(Promise promise) {
        String model = Build.MODEL;
        try {
            promise.resolve(model);
        } catch (Exception err) {
            promise.reject(err.getLocalizedMessage());
        }
    }

    @ReactMethod
    public void createUUID(Promise promise) {
        try {
            UUID uuid = UUID.randomUUID();
            String uuidString = uuid.toString();
            promise.resolve(uuidString);
        } catch (Exception err) {
            promise.reject(err.getLocalizedMessage());
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
            promise.reject(err.getLocalizedMessage());
        }
    }

    @ReactMethod
    public void barcodeRead(Promise promise) {

        try {
            if (barcodeUtility != null) {
                barcodeUtility.startScan(this.reactContext, BarcodeUtility.ModuleType.BARCODE_2D);
                promise.resolve(true);
            }
            promise.resolve(false);
        } catch (Exception err) {
            promise.reject(err.getLocalizedMessage());
        }
    }

    @ReactMethod
    public void barcodeCancel(Promise promise) {
        try {
            if (barcodeUtility != null) {
                barcodeUtility.stopScan(this.reactContext, BarcodeUtility.ModuleType.BARCODE_2D);
                promise.resolve(true);
            }
            promise.resolve(false);
        } catch (Exception err) {
            promise.reject(err.getLocalizedMessage());
        }
    }

    private WritableMap createTagMap(UHFTAGInfo tag) {
        WritableMap map = Arguments.createMap();
        map.putString("epc", tag.getEPC());
        map.putString("tid", tag.getTid());
        map.putString("rssi", tag.getRssi());
        map.putString("user", tag.getUser());
        return map;
    }

    @ReactMethod
    public void readSingleTag(final Promise promise) {
        try {
            UHFTAGInfo tag = mReader.inventorySingleTag();

            if (tag != null) {
                WritableMap map = createTagMap(tag);
                sendEvent("UHF_TAG", map);
                promise.resolve(true);
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
            if (uhfPower >= 0) {
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
            if (uhfPowerState)
                promise.resolve(uhfPowerState);
            else
                promise.reject("POWER_ERROR", "Can't Change Power");
        } catch (Exception ex) {
            promise.reject("POWER_ERROR", ex.getLocalizedMessage());
        }
    }

    private int getTargetBank(int target) {
        int Bank;
        switch (target) {
            case 0:
                Bank = IUHF.Bank_EPC;
                break;
            case 1:
                Bank = IUHF.Bank_USER;
                break;
            default:
                Bank = IUHF.Bank_EPC;
                break;
        }
        return Bank;
    }

    @ReactMethod
    public void writeToEPC(
            String targetTID,
            int targetBank,
            int startAddress,
            String newData,
            final Promise promise) {

        try {
            String accessPwd = "00000000";
            int targetTIDLength = targetTID.length();
            Boolean uhfWriteState = mReader.writeData(accessPwd, IUHF.Bank_TID, 0, targetTIDLength, targetTID,
                    IUHF.Bank_EPC, 2, newData.length(), newData);

            if (uhfWriteState)
                promise.resolve(uhfWriteState);
            else
                promise.reject("READER_ERROR", "Can't Write Data");

        } catch (Exception ex) {

            promise.reject("READER_ERROR", ex.getLocalizedMessage());

        }
    }

    @ReactMethod
    public void startScan(final Promise promise) {
        uhfInventoryStatus = mReader.startInventoryTag();
        new TagThread().start();
        promise.resolve(uhfInventoryStatus);
    }

    @ReactMethod
    public void stopScan(final Promise promise) {
        uhfInventoryStatus = false;
        mReader.stopInventory();
        promise.resolve(scannedTags.size());
    }

    @ReactMethod
    public void findTag(final String findEpc, final Promise promise) {
        uhfInventoryStatus = mReader.startInventoryTag();
        new TagThread(findEpc).start();
        promise.resolve(uhfInventoryStatus);
    }

    @ReactMethod
    public void clearTags(final Promise promise) {
        scannedTags.clear();
        promise.resolve(true);
    }

    private void sendEvent(String eventName, @Nullable WritableMap map) {
        getReactApplicationContext()
                .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit(eventName, map);
    }

    private void sendEvent(String eventName, String msg) {
        getReactApplicationContext()
                .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit(eventName, msg);
    }

    private void sendEvent(String eventName, Boolean msg) {
        getReactApplicationContext()
                .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit(eventName, msg);
    }

    class TagThread extends Thread {

        String findEpc;

        public TagThread() {
            findEpc = "";
        }

        public TagThread(String findEpc) {
            this.findEpc = findEpc;
        }

        public void run() {
            String strTid;
            String strResult;
            UHFTAGInfo res = null;
            while (uhfInventoryStatus) {
                res = mReader.readTagFromBuffer();
                if (res != null) {
                    if ("".equals(findEpc))
                        addIfNotExists(res);
                    else
                        lostTagOnly(res);
                }
            }
        }

        public void lostTagOnly(UHFTAGInfo tag) {
            String epc = tag.getEPC(); // mReader.convertUiiToEPC(tag[1]);
            if (epc.equals(findEpc)) {
                WritableMap map = createTagMap(tag);
                sendEvent("UHF_TAG", map);
            }
        }

        public void addIfNotExists(UHFTAGInfo tag) {
            if (!scannedTags.contains(tag.getEPC())) {
                WritableMap map = createTagMap(tag);
                sendEvent("UHF_TAG", map);
            }
        }
    }

    class BarcodeDataReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String barCode = intent.getStringExtra("data");
            String status = intent.getStringExtra("SCAN_STATE");

            if (status != null && (status.equals("cancel") || status.equals("failure"))) {
                sendEvent("BARCODE", false);
            } else {
                if (barCode.length() == 0) {
                    sendEvent("BARCODE", false);
                } else {
                    sendEvent("BARCODE", barCode);
                }
            }
        }
    }
}
