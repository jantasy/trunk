package cn.yjt.oa.app.nfctools;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.nfc.NfcAdapter;
import android.os.Build;
import android.util.Log;

import com.nxp.nfclib.classic.MFClassic;
import com.nxp.nfclib.exceptions.SmartCardException;
import com.nxp.nfclib.icode.ICodeSLI;
import com.nxp.nfclib.icode.ICodeSLIL;
import com.nxp.nfclib.icode.ICodeSLIS;
import com.nxp.nfclib.icode.ICodeSLIX;
import com.nxp.nfclib.icode.ICodeSLIX2;
import com.nxp.nfclib.icode.ICodeSLIXL;
import com.nxp.nfclib.icode.ICodeSLIXS;
import com.nxp.nfclib.ntag.INTag213215216;
import com.nxp.nfclib.ntag.NTag203x;
import com.nxp.nfclib.ntag.NTag210;
import com.nxp.nfclib.ntag.NTag213215216;
import com.nxp.nfclib.ntag.NTag213F216F;
import com.nxp.nfclib.ntag.NTagI2C;
import com.nxp.nfclib.plus.PlusSL1;
import com.nxp.nfclib.ultralight.Ultralight;
import com.nxp.nfclib.ultralight.UltralightC;
import com.nxp.nfclib.ultralight.UltralightEV1;
import com.nxp.nfcliblite.Interface.NxpNfcLibLite;
import com.nxp.nfcliblite.Interface.Nxpnfcliblitecallback;
import com.nxp.nfcliblite.cards.Plus;

@TargetApi(Build.VERSION_CODES.GINGERBREAD_MR1)
public abstract class NfcDetector {
	/**
	 * Broadcast Action: The state of the local NFC adapter has been changed.
	 * <p>
	 * For example, NFC has been turned on or off.
	 * <p>
	 * Always contains the extra field {@link #EXTRA_STATE}
	 */

	public static final String ACTION_ADAPTER_STATE_CHANGED = "android.nfc.action.ADAPTER_STATE_CHANGED";

	/**
	 * Used as an int extra field in {@link #ACTION_STATE_CHANGED} intents to
	 * request the current power state. Possible values are: {@link #STATE_OFF},
	 * {@link #STATE_TURNING_ON}, {@link #STATE_ON}, {@link #STATE_TURNING_OFF},
	 */

	public static final String EXTRA_ADAPTER_STATE = "android.nfc.extra.ADAPTER_STATE";

	public static final int STATE_OFF = 1;
	public static final int STATE_TURNING_ON = 2;
	public static final int STATE_ON = 3;
	public static final int STATE_TURNING_OFF = 4;

	private static final String ACTION_NFC_SETTINGS = "android.settings.NFC_SETTINGS";

	/** this action seems never to be emitted, but is here for future use */
	private static final String ACTION_TAG_LEFT_FIELD = "android.nfc.action.TAG_LOST";

	private static final String TAG = NfcDetector.class.getName();

	private static IntentFilter nfcStateChangeIntentFilter = new IntentFilter(
			ACTION_ADAPTER_STATE_CHANGED);

	public static byte[] PWD = new byte[] { 0x11, 0x01, 0x01, 0x01 };
	public static byte[] PACK = new byte[] { 0x22, 0x22 };
	public static byte[] PASSWORD_MARK = new byte[] { (byte) 0xFF, (byte) 0xFF,
			(byte) 0xFF, (byte) 0xFF };

	public static final int PROTECTED_POSITION = 100;
	public static final int PASSWORD_MARK_POSITION = 99;

	protected NfcAdapter nfcAdapter;
	protected IntentFilter[] writeTagFilters;
	protected PendingIntent nfcPendingIntent;

	protected boolean foreground = false;
	protected boolean intentProcessed = false;
	protected boolean nfcEnabled = false;

	protected BroadcastReceiver nfcStateChangeBroadcastReceiver;

	protected boolean detecting = false;

	/** NXP chips support Mifare Classic, others do not. */
	protected boolean nxpMifareClassic;

	private Activity activity;
	private NfcDetectorInterface detectorInterface;

	private NxpNfcLibLite libInstance;

	private boolean writePassword = false;

	private boolean guessUsePassword;

	private static ExecutorService threadExecutor = Executors
			.newSingleThreadExecutor();

	public NfcDetector(Activity activity, NfcDetectorInterface detectorInterface) {
		if (activity == null || detectorInterface == null) {
			throw new NullPointerException(
					"activity and detectorInterface must not be null");
		}
		this.activity = activity;
		this.detectorInterface = detectorInterface;
		libInstance = NxpNfcLibLite.getInstance();
		libInstance.registerActivity(activity);
	}

	public void setWritePassword(boolean writePassword) {
		this.writePassword = writePassword;
	}

	public void create() {
		Log.d(TAG, "create");

		nxpMifareClassic = hasMifareClassic();

		// Check for available NFC Adapter
		PackageManager pm = activity.getPackageManager();
		if (pm.hasSystemFeature(PackageManager.FEATURE_NFC)
				&& NfcAdapter.getDefaultAdapter(activity) != null) {
			Log.d(TAG, "NFC feature found");

			onNfcFeatureFound();
		} else {
			Log.d(TAG, "NFC feature not found");

			detectorInterface.onNfcFeatureNotFound();
		}

	}

	private boolean hasMifareClassic() {
		return activity.getPackageManager().hasSystemFeature("com.nxp.mifare");
	}

	/**
	 * Notify that NFC is available
	 */

	protected void onNfcFeatureFound() {
		initializeNfc();
		detectInitialNfcState();
	}

	/**
	 * 
	 * Initialize Nfc fields
	 * 
	 */

	protected void initializeNfc() {
		nfcAdapter = NfcAdapter.getDefaultAdapter(activity);
		nfcPendingIntent = PendingIntent.getActivity(activity, 0, new Intent(
				activity, activity.getClass())
				.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

		IntentFilter tagDetected = new IntentFilter(
				NfcAdapter.ACTION_TAG_DISCOVERED);
		IntentFilter ndefDetected = new IntentFilter(
				NfcAdapter.ACTION_NDEF_DISCOVERED);
		IntentFilter techDetected = new IntentFilter(
				NfcAdapter.ACTION_TECH_DISCOVERED);
		IntentFilter tagLost = new IntentFilter(ACTION_TAG_LEFT_FIELD);

		writeTagFilters = new IntentFilter[] { ndefDetected, tagDetected,
				techDetected, tagLost };

		nfcStateChangeBroadcastReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				final int state = intent.getIntExtra(EXTRA_ADAPTER_STATE, -1);
				if (state == STATE_OFF || state == STATE_ON) {

					activity.runOnUiThread(new Runnable() {
						public void run() {
							if (state == STATE_ON) {
								if (detecting) {
									enableForeground();
								}
							}

							detectNfcStateChanges();
						}
					});
				}
			}
		};
	}

	/**
	 * 
	 * Detect initial NFC state.
	 * 
	 */

	protected void detectInitialNfcState() {
		nfcEnabled = nfcAdapter.isEnabled();
		if (nfcEnabled) {
			Log.d(TAG, "NFC is enabled");

			detectorInterface.onNfcStateEnabled();
		} else {
			Log.d(TAG, "NFC is disabled"); // change state in wireless settings

			detectorInterface.onNfcStateDisabled();
		}
	}

	public void resume() {

		if (nfcAdapter != null) {
			// enable foreground mode if nfc is on and we have started detecting
			boolean enabled = nfcAdapter.isEnabled();
			if (enabled && detecting) {
				enableForeground();
			}

			detectNfcStateChanges();

			// for quicksettings
			startDetectingNfcStateChanges();
		}

		if (!intentProcessed) {
			intentProcessed = true;

			processIntent();
		}

	}

	/**
	 * 
	 * Detect changes in NFC settings - enabled/disabled
	 * 
	 */

	protected void detectNfcStateChanges() {
		Log.d(TAG, "Detect NFC state changes while previously "
				+ (nfcEnabled ? "enabled" : "disabled"));

		boolean enabled = nfcAdapter.isEnabled();
		if (nfcEnabled != enabled) {
			Log.d(TAG, "NFC state change detected; NFC is now "
					+ (enabled ? "enabled" : "disabled"));
			detectorInterface.onNfcStateChange(enabled);

			nfcEnabled = enabled;
		} else {
			Log.d(TAG, "NFC state remains "
					+ (enabled ? "enabled" : "disabled"));
		}
	}

	protected void startDetectingNfcStateChanges() {
		activity.registerReceiver(nfcStateChangeBroadcastReceiver,
				nfcStateChangeIntentFilter);
	}

	protected void stopDetectingNfcStateChanges() {
		activity.unregisterReceiver(nfcStateChangeBroadcastReceiver);
	}

	public void pause() {

		if (nfcAdapter != null) {
			disableForeground();

			// for quicksettings
			stopDetectingNfcStateChanges();
		}
	}

	public void newIntent(Intent intent) {

		Log.d(TAG, "onNewIntent");

		// onResume gets called after this to handle the intent
		intentProcessed = false;

		activity.setIntent(intent);
	}

	protected void enableForeground() {
		if (!foreground && nfcAdapter != null) {
			Log.d(TAG, "Enable nfc forground mode");

			nfcAdapter.enableForegroundDispatch(activity, nfcPendingIntent,
					null, null);

			foreground = true;
		}
	}

	/**
	 * 
	 * Start detecting NDEF messages
	 * 
	 */

	public void startDetecting() {
		if (!detecting) {
			enableForeground();

			detecting = true;
		}
	}

	/**
	 * 
	 * Stop detecting NDEF messages
	 * 
	 */

	public void stopDetecting() {
		if (detecting) {
			disableForeground();

			detecting = false;
		}
	}

	protected void disableForeground() {
		if (foreground) {
			Log.d(TAG, "Disable nfc forground mode");

			nfcAdapter.disableForegroundDispatch(activity);

			foreground = false;
		}
	}

	/**
	 * 
	 * Process the current intent, looking for NFC-related actions
	 * 
	 */
	boolean nxp = true;

	public boolean authenticatePwd(NTag213215216 tag) {
		try {
			tag.authenticatePwd(PWD, PACK);
			return true;
		} catch (SmartCardException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	public void processIntent() {
		final Intent intent = activity.getIntent();
		// TODO：
		// 设为true，采用nxp的加密
		if (nxp) {

			libInstance.filterIntent(intent, new Nxpnfcliblitecallback() {
				
				@Override
				public void onNTag213215216CardDetected(INTag213215216 ntag) {
					if(ntag instanceof NTag213215216){
						NTag213215216 tag = (NTag213215216) ntag;
						try {
							tag.getReader().connect();
							try {
								nTag213215216CardDetected(tag,
										checkIfUsePassword(tag), intent);
							} catch (SmartCardException e) {
								e.printStackTrace();
							}
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							try {
								tag.getReader().close();
							} catch (Throwable e) {
								e.printStackTrace();
							}
						}
					}
					
				}
				
			});
		} else {

			// Check to see that the Activity started due to an Android Beam
			if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
				Log.d(TAG, "Process NDEF discovered action");

				nfcIntentDetected(intent, NfcAdapter.ACTION_NDEF_DISCOVERED);
			} else if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent
					.getAction())) {
				Log.d(TAG, "Process TAG discovered action");

				nfcIntentDetected(intent, NfcAdapter.ACTION_TAG_DISCOVERED);
			} else if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent
					.getAction())) {
				Log.d(TAG, "Process TECH discovered action");

				nfcIntentDetected(intent, NfcAdapter.ACTION_TECH_DISCOVERED);
			} else if (ACTION_TAG_LEFT_FIELD.equals(intent.getAction())) {
				Log.d(TAG, "Process tag lost action");

				detectorInterface.onTagLost(); // NOTE: This seems not to work
												// as expected
			} else {
				Log.d(TAG, "Ignore action " + intent.getAction());
			}

		}
	}

	private boolean checkIfUsePassword(NTag213215216 tag)
			throws SmartCardException, IOException {
		byte[] mark = tag.read(PASSWORD_MARK_POSITION);
		// If mark was 0xffffffff,tag must be protected by password.
		return mark[0] == (byte) 0xff && mark[1] == (byte) 0xff
				&& mark[2] == (byte) 0xff && mark[3] == (byte) 0xff;
	}

	public static String toHexString(byte[] data) {
		StringBuilder serial = new StringBuilder();
		for (int i = 0; i < data.length; i++) {
			String hexString = Integer.toHexString(data[i] & 0xFF);
			if (hexString.length() == 1) {
				hexString = "0" + hexString;
			}
			serial.append(hexString).append(":");
		}
		serial.replace(serial.length() - 1, serial.length(), "");
		return serial.toString();
	}

	/**
	 * 
	 * Launch an activity for NFC (or wireless) settings, so that the user might
	 * enable or disable nfc
	 * 
	 */

	protected void startNfcSettingsActivity() {
		if (android.os.Build.VERSION.SDK_INT >= 16) {
			activity.startActivity(new Intent(ACTION_NFC_SETTINGS)); // android.provider.Settings.ACTION_NFC_SETTINGS
		} else {
			activity.startActivity(new Intent(
					android.provider.Settings.ACTION_WIRELESS_SETTINGS));
		}
	}

	public boolean isDetecting() {
		return detecting;
	}

	/**
	 * 
	 * Incoming NFC communication (in form of tag or beam) detected
	 * 
	 */
	public abstract void nfcIntentDetected(Intent intent, String action);

	/**
	 * NXP SDK detected result.
	 * 
	 * @param tag
	 */
	public abstract void nTag213215216CardDetected(NTag213215216 tag,
			boolean isUsePassword, Intent intent) throws SmartCardException;

	// TODO：写一个开始结束
	public static interface NfcDetectorInterface {

		/**
		 * 
		 * NFC feature was found and is currently enabled
		 * 
		 */

		public void onNfcStateEnabled();

		/**
		 * 
		 * NFC feature was found but is currently disabled
		 * 
		 */

		public void onNfcStateDisabled();

		/**
		 * 
		 * NFC setting changed since last check. For example, the user enabled
		 * NFC in the wireless settings.
		 * 
		 */

		public void onNfcStateChange(boolean enabled);

		/**
		 * 
		 * This device does not have NFC hardware
		 * 
		 */

		public void onNfcFeatureNotFound();

		public void onTagLost();

	}
}
