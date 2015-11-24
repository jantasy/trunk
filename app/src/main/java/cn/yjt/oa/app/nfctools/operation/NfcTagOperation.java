package cn.yjt.oa.app.nfctools.operation;

import java.util.Arrays;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import cn.yjt.oa.app.MainApplication;

public abstract class NfcTagOperation implements Parcelable, Cloneable {

    static final String TAG = NfcTagOperation.class.getSimpleName();

    public static final int NFC_TAG_OPERATION_WIFI_OPEN = 0x0000;
    public static final int NFC_TAG_OPERATION_WIFI_CLOSE = 0x0001;
    public static final int NFC_TAG_OPERATION_WIFI_TOGGLE = 0x0002;
    public static final int NFC_TAG_OPERATION_DATA_OPEN = 0x0004;
    public static final int NFC_TAG_OPERATION_DATA_CLOSE = 0x0005;
    public static final int NFC_TAG_OPERATION_DATA_TOGGLE = 0x0006;
    public static final int NFC_TAG_OPERATION_BLUETOOTH_OPEN = 0x0007;
    public static final int NFC_TAG_OPERATION_BLUETOOTH_CLOSE = 0x0008;
    public static final int NFC_TAG_OPERATION_BLUETOOTH_TOGGLE = 0x0009;
    public static final int NFC_TAG_OPERATION_AIRPLANE_MODE_OPEN = 0x000a;
    public static final int NFC_TAG_OPERATION_AIRPLANE_MODE_CLOSE = 0x000b;
    public static final int NFC_TAG_OPERATION_AIRPLANE_MODE_TOGGLE = 0x000c;
    public static final int NFC_TAG_OPERATION_SILENT_OFF = 0x000d;
    public static final int NFC_TAG_OPERATION_SILENT_MUTE = 0x000e;
    public static final int NFC_TAG_OPERATION_SILENT_VIBRATE = 0x000f;
    public static final int NFC_TAG_OPERATION_SIGNIN = 0x0016;
    public static final int NFC_TAG_OPERATION_OPEN_APP = 0x0017;
    public static final int NFC_TAG_OPERATION_PATROL = 0x0018;

    private static final int OPERATION_ID_LENGTH = 2;
    private static final String SCHEME_RESOURCE = "res://";

    private String operationName;
    private String operatorName;
    private int operationId;
    private Context context;
    private byte[] extraData;
    private String icon;
    private String tagName;
    private String sn;
    private String notificationFormat;
    private String broadcastAction;
    private boolean singleChoose;

    public String getOperationName() {
        return operationName;
    }

    public String getOperationText() {
        return operationName;
    }

    void setOperationName(String operationName) {
        this.operationName = operationName;
    }

    public String getOperatorName() {
        return operatorName;
    }

    void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    void setOperationId(int operationId) {
        this.operationId = operationId;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public byte[] getExtraData() {
        return extraData;
    }

    public void setExtraData(byte[] extraData) {
        this.extraData = extraData;
    }

    public int getOperationId() {
        return operationId;
    }

    public String getIcon() {
        return icon;
    }

    void setIcon(String icon) {
        this.icon = icon;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public String getNotificationFormat() {
        return notificationFormat;
    }

    public void setNotificationFormat(String notificationFormat) {
        this.notificationFormat = notificationFormat;
    }

    public String getBroadcastAction() {
        return broadcastAction;
    }

    public void setBroadcastAction(String broadcastAction) {
        this.broadcastAction = broadcastAction;
    }

    public boolean isSingleChoose() {
        return singleChoose;
    }

    public void setSingleChoose(boolean singleChoose) {
        this.singleChoose = singleChoose;
    }

    public Drawable getIconDrawable(Context context) {
        if (icon != null) {
            if (icon.startsWith(SCHEME_RESOURCE)) {
                String resourcePath = icon.substring(SCHEME_RESOURCE.length());
                int identifier = context.getResources().getIdentifier(
                        resourcePath, null, context.getPackageName());
                return context.getResources().getDrawable(identifier);
            }
        }
        return null;
    }

    public abstract void excuteOperation();

    @Override
    public boolean equals(Object o) {
        if (o != null && o instanceof NfcTagOperation) {
            return true;
        }
        return super.equals(o);
    }

    public static NfcTagOperation create(int operationId) {
        NfcTagOperation nfcTagOperation;
        switch (operationId) {
            case NFC_TAG_OPERATION_WIFI_OPEN:
                nfcTagOperation = new NfcTagWifiOpenOperation();
                break;
            case NFC_TAG_OPERATION_WIFI_CLOSE:
                nfcTagOperation = new NfcTagWifiCloseOperation();
                break;
            case NFC_TAG_OPERATION_WIFI_TOGGLE:
                nfcTagOperation = new NfcTagWifiToggleOperation();
                break;
            case NFC_TAG_OPERATION_DATA_OPEN:
                nfcTagOperation = new NfcTagDataOpenOperation();
                break;
            case NFC_TAG_OPERATION_DATA_CLOSE:
                nfcTagOperation = new NfcTagDataCloseOperation();
                break;
            case NFC_TAG_OPERATION_DATA_TOGGLE:
                nfcTagOperation = new NfcTagDataToggleOperation();
                break;
            case NFC_TAG_OPERATION_BLUETOOTH_OPEN:
                nfcTagOperation = new NfcTagBluetoothOpenOperation();
                break;
            case NFC_TAG_OPERATION_BLUETOOTH_CLOSE:
                nfcTagOperation = new NfcTagBluetoothCloseOperation();
                break;
            case NFC_TAG_OPERATION_BLUETOOTH_TOGGLE:
                nfcTagOperation = new NfcTagBluetoothToggleOperation();
                break;
            case NFC_TAG_OPERATION_AIRPLANE_MODE_OPEN:
                nfcTagOperation = new NfcTagAirplaneModeOpenOperation();
                break;
            case NFC_TAG_OPERATION_AIRPLANE_MODE_CLOSE:
                nfcTagOperation = new NfcTagAirplaneModeCloseOperation();
                break;
            case NFC_TAG_OPERATION_AIRPLANE_MODE_TOGGLE:
                nfcTagOperation = new NfcTagAirplaneModeToggleOperation();
                break;
            case NFC_TAG_OPERATION_SILENT_OFF:
                nfcTagOperation = new NfcTagSilentOffOperation();
                break;
            case NFC_TAG_OPERATION_SILENT_MUTE:
                nfcTagOperation = new NfcTagSilentMuteOperation();
                break;
            case NFC_TAG_OPERATION_SILENT_VIBRATE:
                nfcTagOperation = new NfcTagSilentVibrateOperation();
                break;
            case NFC_TAG_OPERATION_SIGNIN:
                nfcTagOperation = new NfcTagSigninOperation();
                break;
            case NFC_TAG_OPERATION_OPEN_APP:
                nfcTagOperation = new NfcTagOpenAppOperation();
                break;
            case NFC_TAG_OPERATION_PATROL:
                nfcTagOperation = new NfcTagPatrolOperation();
                break;

            default:
                nfcTagOperation = null;
                break;
        }
        return nfcTagOperation;
    }

    public static NfcTagOperation parseOperation(byte[] data) {
        // The length of operation's id is 2.
        if (data.length >= OPERATION_ID_LENGTH) {
            int operationId = (data[0] << 8) + data[1];
            NfcTagOperation operation = NfcTagOperation.create(operationId);
            NfcTagOperation originalOperation = NfcTagOperatorConfig.getInstance(MainApplication.getAppContext()).findNfcTagOperationWithOperationId(operationId);
            operation.setContext(originalOperation.getContext());
            operation.setIcon(originalOperation.getIcon());
            operation.setOperatorName(originalOperation.getOperatorName());
            operation.setOperationName(originalOperation.getOperationName());
            operation.setOperationId(operationId);
            operation.setNotificationFormat(originalOperation.getNotificationFormat());
            operation.setBroadcastAction(originalOperation.getBroadcastAction());
            operation.setSingleChoose(originalOperation.isSingleChoose());

            // If length of data bigger than 2,there is a extraData for this
            // operation.
            if (data.length > OPERATION_ID_LENGTH) {
                byte[] extraData = new byte[data.length - OPERATION_ID_LENGTH];
                System.arraycopy(data, OPERATION_ID_LENGTH, extraData, 0,
                        data.length - OPERATION_ID_LENGTH);
                operation.setExtraData(extraData);
            }
            return operation;
        }
        return null;
    }

    public byte[] generateOperationData() {
        byte[] data = new byte[2];

        int operationId = getOperationId();
        data[0] = (byte) (operationId >> 8);
        data[1] = (byte) (operationId & 0xFF);

        byte[] extraData = getExtraData();
        if (extraData != null && extraData.length > 0) {
            byte[] b = new byte[data.length + extraData.length];
            System.arraycopy(data, 0, b, 0, data.length);
            System.arraycopy(extraData, 0, b, data.length, extraData.length);
            data = b;
        }

        return data;
    }


    @Override
    public String toString() {
        return "NfcTagOperation [operationName=" + operationName
                + ", operatorName=" + operatorName + ", operationId="
                + (int) operationId + ", extraData=" + Arrays.toString(extraData)
                + ", icon=" + icon + "]";
    }


    public static final Creator<NfcTagOperation> CREATOR = new Creator<NfcTagOperation>() {

        @Override
        public NfcTagOperation[] newArray(int size) {
            return new NfcTagOperation[size];
        }

        @Override
        public NfcTagOperation createFromParcel(Parcel source) {
            int len = source.readInt();
            if (len != -1) {
                byte[] data = new byte[len];
                source.readByteArray(data);
                Log.d(TAG, "createFromParcel:" + Arrays.toString(data));
                return parseOperation(data);
            }
            return null;
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {


        byte[] generateOperationData = generateOperationData();
        Log.d(TAG, "writeToParcel:" + Arrays.toString(generateOperationData));
        dest.writeInt(generateOperationData.length);
        dest.writeByteArray(generateOperationData);

    }

    public NfcTagOperation cloneSelf() {
        try {
            return (NfcTagOperation) clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
