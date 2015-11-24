package cn.yjt.oa.app.attendance;

import java.util.List;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Vibrator;
import android.text.format.Time;
import android.util.Log;
import android.widget.Toast;

public class ShakeManager {
	private Context context;
	private SensorManager sManager;
	private OnSensorChangedListener l;
	private Vibrator vibrator;

	private boolean registerSuccess = false;

	private static Handler handler = new Handler();
	private static final long HANDLER_DELAYTIME = 500;

	public static final float SHAKE_SENSE_VALUE_X_CHANGE_DEFAULT = 6f;
	public static final float SHAKE_SENSE_VALUE_Y_CHANGE_DEFAULT = 6f;
	public static final float SHAKE_SENSE_VALUE_Z_CHANGE_DEFAULT = 6f;

	private float shakeSenseValueChangeX = SHAKE_SENSE_VALUE_X_CHANGE_DEFAULT;
	private float shakeSenseValueChangeY = SHAKE_SENSE_VALUE_Y_CHANGE_DEFAULT;
	private float shakeSenseValueChangeZ = SHAKE_SENSE_VALUE_Z_CHANGE_DEFAULT;

	private float[] lastValue;

	private float max_X;
	private float max_Y;
	private float max_Z;

	private boolean useValueZ = false;

	private boolean isToastShow = false;

	private boolean isUserShake = false;

	private static final long VIBRATE_TIME = 300;

	private long reachTime;

	private long oldTime = 0;
	private long oldTime2 = 0;

	private long changeTime;

	public ShakeManager(Context context) {
		this.context = context;

		sManager = (SensorManager) context
				.getSystemService(Context.SENSOR_SERVICE);
		vibrator = (Vibrator) context
				.getSystemService(Context.VIBRATOR_SERVICE);

	}

	public void registerSensorListener(OnSensorChangedListener l) {

		if (hasSensor() && !registerSuccess) {
			Sensor sensor = sManager
					.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
			registerSuccess = sManager.registerListener(listener, sensor,
					SensorManager.SENSOR_DELAY_FASTEST);
		}
		if (!registerSuccess && !isToastShow) {
			// showToast(R.string.sensor_not_found);
		} else {
			this.l = l;
		}
	}

	public boolean hasSensor() {
		List<Sensor> sensors = sManager
				.getSensorList(Sensor.TYPE_ACCELEROMETER);
		return sensors != null && sensors.size() > 0;
	}

	private void showToast(int res) {
		isToastShow = true;
		Toast.makeText(context, res, Toast.LENGTH_SHORT).show();
		handler.postDelayed(new Runnable() {

			@Override
			public void run() {
				isToastShow = false;
			}
		}, 2000);
	}

	public void unregisterSensorListener() {
		if (registerSuccess) {
			sManager.unregisterListener(listener,
					sManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER));
			registerSuccess = false;
		}
	}

	public void setShakeSenseValueX(float value) {
		shakeSenseValueChangeX = value;
	}

	public void setShakeSenseValueY(float value) {
		shakeSenseValueChangeY = value;
	}

	public void setShakeSenseValueZ(float value) {
		shakeSenseValueChangeZ = value;
	}

	public void setUseValueZ(boolean useValueZ) {
		this.useValueZ = useValueZ;
	}

	public void setShakeSenseValues(float valueX, float valueY, float valueZ,
			boolean useValueZ) {
		shakeSenseValueChangeX = valueX;
		shakeSenseValueChangeY = valueY;
		shakeSenseValueChangeZ = valueZ;
		this.useValueZ = useValueZ;

	}

	private SensorEventListener listener = new SensorEventListener() {

		@Override
		public void onSensorChanged(SensorEvent event) {
			ShakeManager.this.onSensorChanged(event.values);
		}

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {

		}
	};

	private void onSensorChanged(float[] values) {
		if (l == null || !l.onChangeFinish()) {
			return;
		}
		final float alpha = 0.8f;
		float[] gravity = new float[3];
		gravity[0] = alpha * gravity[0] + (1 - alpha) * values[0];
		gravity[1] = alpha * gravity[1] + (1 - alpha) * values[1];
		gravity[2] = alpha * gravity[2] + (1 - alpha) * values[2];

		float[] linear_acceleration = new float[3];

		linear_acceleration[0] = values[0] - gravity[0];
		linear_acceleration[1] = values[1] - gravity[1];
		linear_acceleration[2] = values[2] - gravity[2];

		if (lastValue == null) {
			lastValue = new float[3];
			lastValue[0] = linear_acceleration[0];
			lastValue[1] = linear_acceleration[1];
			lastValue[2] = linear_acceleration[2];
			return;
		}
		float x = linear_acceleration[0] - lastValue[0];
		float y = linear_acceleration[1] - lastValue[1];
		float z = linear_acceleration[2] - lastValue[2];
		lastValue[0] = linear_acceleration[0];
		lastValue[1] = linear_acceleration[1];
		lastValue[2] = linear_acceleration[2];
		if (useValueZ) {
			onSensorChanged(x, y, z);
		} else {
			onSensorChanged(x, y);
		}
	}

	private void onSensorChanged(float x, float y) {
		if (l != null) {
			if (x > shakeSenseValueChangeX || y > shakeSenseValueChangeY) {
				if (!isUserShake) {
					isUserShake = isUserShake();
					return;
				}
				changeTime = System.currentTimeMillis();
				// showToast(R.string.search_shake);
				l.onSensorChanged();
				oldTime = 0;
				isUserShake = false;

				// handler.postDelayed(new Runnable() {
				//
				// @Override
				// public void run() {
				// if (System.currentTimeMillis() - changeTime >=
				// HANDLER_DELAYTIME) {
				// showToast(R.string.search_shake);
				// l.onSensorChanged();
				// oldTime = 0;
				// isUserShake = false;
				// }
				// }
				// }, HANDLER_DELAYTIME);
			}
		}
	}

	private void onSensorChanged(float x, float y, float z) {
		if (l != null) {
			if (x > shakeSenseValueChangeX || y > shakeSenseValueChangeY
					|| z > shakeSenseValueChangeZ) {
				if (!isUserShake) {
					isUserShake = isUserShake();
					return;
				}
				changeTime = System.currentTimeMillis();
				// showToast(R.string.search_shake);
				l.onSensorChanged();
				oldTime = 0;
				isUserShake = false;
				// handler.postDelayed(new Runnable() {
				//
				// @Override
				// public void run() {
				// if (System.currentTimeMillis() - changeTime >=
				// HANDLER_DELAYTIME) {
				// showToast(R.string.search_shake);
				// l.onSensorChanged();
				// }
				// }
				// }, HANDLER_DELAYTIME);

			}
		}
	}

	private boolean isUserShake() {
		final long currentTime = System.currentTimeMillis();
		handler.postDelayed(new Runnable() {

			@Override
			public void run() {
				if (System.currentTimeMillis() - currentTime >= HANDLER_DELAYTIME) {
					if (!isUserShake) {
						oldTime = 0;
					}
				}
			}
		}, HANDLER_DELAYTIME);

		if (oldTime == 0) {
			oldTime = currentTime;
			return false;
		}
		if (currentTime - oldTime >= HANDLER_DELAYTIME / 20) {
			return true;
		}

		return false;
	}

	public interface OnSensorChangedListener {
		public void onSensorChanged();

		public boolean onChangeFinish();
	}
}
