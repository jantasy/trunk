package cn.yjt.oa.app.task;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import cn.yjt.oa.app.MainActivity;
import cn.yjt.oa.app.MainApplication;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.graphics.RoundedDrawable;
import cn.yjt.oa.app.imageloader.ImageLoader.ImageContainer;
import cn.yjt.oa.app.imageloader.ImageLoader.ImageLoaderListener;
import cn.yjt.oa.app.picturepicker.DefaultPicturePicker;
import cn.yjt.oa.app.picturepicker.PicturePicker;

public class PictureBoradFragment extends Fragment implements OnClickListener {

	public static String YJT_DIR = Environment.getExternalStorageDirectory()
			+ "/yijitong/" + MainActivity.userName + "";

	private List<Uri> pictures = new ArrayList<Uri>();

	private ImageView imageView1;
	private ImageView imageView2;
	private ImageView imageView3;
	private ImageView imageView4;

	private View root;

	private View delete1;
	private View delete2;
	private View delete3;
	private View delete4;

	private PicturePicker picker;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		root = inflater.inflate(R.layout.picture_borad, container, false);
		imageView1 = (ImageView) root.findViewById(R.id.imageView1);
		imageView2 = (ImageView) root.findViewById(R.id.imageView2);
		imageView3 = (ImageView) root.findViewById(R.id.imageView3);
		imageView4 = (ImageView) root.findViewById(R.id.imageView4);
		delete1 = root.findViewById(R.id.delete1);
		delete2 = root.findViewById(R.id.delete2);
		delete3 = root.findViewById(R.id.delete3);
		delete4 = root.findViewById(R.id.delete4);
		imageView1.setOnClickListener(this);
		imageView2.setOnClickListener(this);
		imageView3.setOnClickListener(this);
		imageView4.setOnClickListener(this);
		delete1.setOnClickListener(this);
		delete2.setOnClickListener(this);
		delete3.setOnClickListener(this);
		delete4.setOnClickListener(this);

		updateImage();
		picker = new DefaultPicturePicker();

		return root;
	}

	private void updateImage() {

		FragmentActivity activity = getActivity();
		if (activity instanceof NewInterface) {
			NewInterface n = (NewInterface) activity;
			n.refreshNew();
		}

		imageView1.setVisibility(pictures.size() == 0 ? View.VISIBLE
				: View.INVISIBLE);
		imageView2.setVisibility(pictures.size() == 1 ? View.VISIBLE
				: View.INVISIBLE);
		imageView3.setVisibility(pictures.size() == 2 ? View.VISIBLE
				: View.INVISIBLE);
		imageView4.setVisibility(pictures.size() == 3 ? View.VISIBLE
				: View.INVISIBLE);

		delete1.setVisibility(pictures.size() > 0 ? View.VISIBLE
				: View.INVISIBLE);
		delete2.setVisibility(pictures.size() > 1 ? View.VISIBLE
				: View.INVISIBLE);
		delete3.setVisibility(pictures.size() > 2 ? View.VISIBLE
				: View.INVISIBLE);
		delete4.setVisibility(pictures.size() > 3 ? View.VISIBLE
				: View.INVISIBLE);

		if (pictures.size() == 0) {
			setAddState(imageView1);
		} else if (pictures.size() == 1) {
			loadBitmap(imageView1, 0);
			setAddState(imageView2);
		} else if (pictures.size() == 2) {
			loadBitmap(imageView1, 0);
			loadBitmap(imageView2, 1);
			setAddState(imageView3);
		} else if (pictures.size() == 3) {
			loadBitmap(imageView1, 0);
			loadBitmap(imageView2, 1);
			loadBitmap(imageView3, 2);
			setAddState(imageView4);
		} else if (pictures.size() == 4) {
			loadBitmap(imageView1, 0);
			loadBitmap(imageView2, 1);
			loadBitmap(imageView3, 2);
			loadBitmap(imageView4, 3);
		}
	}

	private void setAddState(ImageView imageView) {
		imageView.setScaleType(ScaleType.CENTER);
		imageView.setImageResource(R.drawable.task_picture_add);
		imageView.setBackgroundResource(R.drawable.task_picture_add_bg);
	}

	private void loadBitmap(final ImageView imageView, final int index) {

		MainApplication.getImageLoader().get(pictures.get(index).toString(),
				imageView.getWidth(), imageView.getHeight(),
				new ImageLoaderListener() {

					@Override
					public void onSuccess(ImageContainer container) {
						Bitmap result = container.getBitmap();
						setImage(imageView, result);
						if (index == 0) {
							delete1.setVisibility(View.VISIBLE);
						} else if (index == 1) {
							delete2.setVisibility(View.VISIBLE);
						} else if (index == 2) {
							delete3.setVisibility(View.VISIBLE);
						} else if (index == 3) {
							delete4.setVisibility(View.VISIBLE);
						}
					}

					@Override
					public void onError(ImageContainer container) {
						imageView.setScaleType(ScaleType.CENTER);
						imageView.setImageResource(R.drawable.task_picture_add);
					}

					private void setImage(final ImageView imageView,
							Bitmap result) {
						imageView
								.setBackgroundResource(R.drawable.task_picture_bg);
						imageView.setVisibility(View.VISIBLE);
						imageView.setScaleType(ScaleType.FIT_XY);
						RoundedDrawable drawable = new RoundedDrawable(result);
						float radius = TypedValue.applyDimension(
								TypedValue.COMPLEX_UNIT_DIP, 3, getResources()
										.getDisplayMetrics());
						drawable.setCornerRadius(radius);
						imageView.setImageDrawable(drawable);
					}
				});
//		new AsyncTask<Void, Void, Bitmap>() {
//
//			protected void onPreExecute() {
//				imageView.setImageBitmap(null);
//			}
//
//			@Override
//			protected Bitmap doInBackground(Void... params) {
//				// return getBitmap(index);
//				Uri uri = pictures.get(index);
//				// Bitmap bitmap = new
//				// DiskImageCache(getActivity()).getBitmap(uri.toString());
//
//				return bitmap;
//			}
//
//			@Override
//			protected void onPostExecute(Bitmap result) {
//				if (result != null) {
//					setImage(imageView, result);
//					if (index == 0) {
//						delete1.setVisibility(View.VISIBLE);
//					} else if (index == 1) {
//						delete2.setVisibility(View.VISIBLE);
//					} else if (index == 2) {
//						delete3.setVisibility(View.VISIBLE);
//					} else if (index == 3) {
//						delete4.setVisibility(View.VISIBLE);
//					}
//				} else {
//					imageView.setScaleType(ScaleType.CENTER);
//					imageView.setImageResource(R.drawable.task_picture_add);
//				}
//
//			}
//
//			private void setImage(final ImageView imageView, Bitmap result) {
//				imageView.setBackgroundResource(R.drawable.task_picture_bg);
//				imageView.setVisibility(View.VISIBLE);
//				imageView.setScaleType(ScaleType.FIT_XY);
//				RoundedDrawable drawable = new RoundedDrawable(result);
//				float radius = TypedValue.applyDimension(
//						TypedValue.COMPLEX_UNIT_DIP, 3, getResources()
//								.getDisplayMetrics());
//				drawable.setCornerRadius(radius);
//				imageView.setImageDrawable(drawable);
//			}
//		}.execute();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.imageView1:
			if (pictures.size() == 0) {
				addPicture();
			}
			break;
		case R.id.imageView2:
			if (pictures.size() == 1) {
				addPicture();
			}
			break;
		case R.id.imageView3:
			if (pictures.size() == 2) {
				addPicture();
			}
			break;
		case R.id.imageView4:
			if (pictures.size() == 3) {
				addPicture();
			}
			break;
		case R.id.delete1:
			delete(0);
			break;
		case R.id.delete2:
			delete(1);
			break;
		case R.id.delete3:
			delete(2);
			break;
		case R.id.delete4:
			delete(3);
			break;
		default:
			break;
		}
	}

	private void delete(int index) {
		pictures.remove(index);
		updateImage();
	}

	private void addPicture() {
		picker.pickPicture(this);
	}

//	private Bitmap getBitmap(int index) {
//
//		if (bitmaps.size() > index) {
//			return bitmaps.get(index);
//		}
//
//		Uri uri = pictures.get(index);
//		if ("file".equals(uri.getScheme())) {
//
//			Bitmap image = BitmapUtils.getImage(uri.getPath(),
//					imageView1.getWidth(), imageView1.getHeight());
//			bitmaps.add(image);
//			return image;
//		} else if ("content".equals(uri.getScheme())) {
//			try {
//				File tempFile = createTempFile();
//				boolean copy = FileUtils.copy(getActivity()
//						.getContentResolver().openInputStream(uri), tempFile);
//				if (copy) {
//					Bitmap image = BitmapUtils.getImage(
//							tempFile.getAbsolutePath(), imageView1.getWidth(),
//							imageView1.getHeight());
//					pictures.remove(index);
//					pictures.add(index, Uri.fromFile(tempFile));
//					bitmaps.add(image);
//					return image;
//				}
//			} catch (FileNotFoundException e) {
//				e.printStackTrace();
//			}
//		}
//		return null;
//	}

	private File createTempFile() {
		return new File(YJT_DIR, UUID.randomUUID() + ".jpg");
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// if (resultCode == Activity.RESULT_OK) {
		// if (requestCode == REQUEST_IMAGE_GALLERY) {
		// Uri uri = data.getData();
		// pictures.add(uri);
		// updateImage();
		// } else if (requestCode == REQUEST_CAMERA) {
		// System.out.println(data);
		// pictures.add(Uri.fromFile(tempFile));
		// updateImage();
		// }
		// }
		if (picker.isPickerResult(requestCode)) {
			Uri uri = picker.getPicture(requestCode, resultCode, data);
			if(uri != null){
				pictures.add(uri);
				updateImage();
			}
		}
	}

	public boolean hasPicture() {
		return !pictures.isEmpty();
	}

	public List<Uri> getPictures() {
		return pictures;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

	}
}
