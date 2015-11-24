package cn.yjt.oa.app.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;
import cn.yjt.oa.app.MainApplication;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.imageloader.ImageLoader.ImageContainer;
import cn.yjt.oa.app.imageloader.ImageLoader.ImageLoaderListener;

public class BitmapUtils {
	private BitmapUtils() {
	}

	public static Bitmap compressImage(Bitmap image) {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
		int options = 100;
		while (baos.toByteArray().length / 1024 > 100) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
			baos.reset();// 重置baos即清空baos
			image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
			options -= 10;// 每次都减少10
		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
		return bitmap;
	}
	
	// 图片转为文件
	public static boolean saveBitmap2file(Bitmap bmp, String filename) {
		CompressFormat format = Bitmap.CompressFormat.JPEG;
		int quality = 100;
		OutputStream stream = null;
		try {
			stream = new FileOutputStream(Environment.getExternalStorageDirectory() + filename);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		if(bmp!=null){
			return bmp.compress(format, quality, stream);
		}
		return false;
	}
	
	public static Bitmap getimage(String srcPath) {
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		// 开始读入图片，此时把options.inJustDecodeBounds 设回true了
		newOpts.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);// 此时返回bm为空
		newOpts.inJustDecodeBounds = false;
		int w = newOpts.outWidth;
		int h = newOpts.outHeight;
		// 现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
		float hh = 200f;// 这里设置高度为800f
		float ww = 200f;// 这里设置宽度为480f
		// 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
		int be = 1;// be=1表示不缩放
		if (w > h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
			be = (int) (newOpts.outWidth / ww);
		} else if (w < h && h > hh) {// 如果高度高的话根据宽度固定大小缩放
			be = (int) (newOpts.outHeight / hh);
		}
		if (be <= 0)
			be = 1;
		newOpts.inSampleSize = be;// 设置缩放比例
		// 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
		bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
		return compressImage(bitmap);// 压缩好比例大小后再进行质量压缩
	}

	public static Bitmap getImage(String srcPath, int ww, int hh) {
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		// 开始读入图片，此时把options.inJustDecodeBounds 设回true了
		newOpts.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);// 此时返回bm为空
		newOpts.inJustDecodeBounds = false;
		int w = newOpts.outWidth;
		int h = newOpts.outHeight;
		// 现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
		// 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
		int be = 1;// be=1表示不缩放
		if (w > h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
			be = (int) (newOpts.outWidth / ww);
		} else if (w < h && h > hh) {// 如果高度高的话根据宽度固定大小缩放
			be = (int) (newOpts.outHeight / hh);
		}
		if (be <= 0)
			be = 1;
		newOpts.inSampleSize = be;// 设置缩放比例
		// 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
		bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
		return compressImage(bitmap);// 压缩好比例大小后再进行质量压缩
	}

	public static Bitmap getImage(InputStream in, int ww, int hh) {
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		// 开始读入图片，此时把options.inJustDecodeBounds 设回true了
		Bitmap bitmap;
		if (in.markSupported()) {
			newOpts.inJustDecodeBounds = true;
			Log.d("mark", "mark  supported");
			in.mark(1);
			bitmap = BitmapFactory.decodeStream(in, null, newOpts);// 此时返回bm为空
			newOpts.inJustDecodeBounds = false;
			int w = newOpts.outWidth;
			int h = newOpts.outHeight;
			// 现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
			// 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
			int be = 1;// be=1表示不缩放
			if (w > h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
				be = (int) (newOpts.outWidth / ww);
			} else if (w < h && h > hh) {// 如果高度高的话根据宽度固定大小缩放
				be = (int) (newOpts.outHeight / hh);
			}
			if (be <= 0)
				be = 1;
			newOpts.inSampleSize = be;// 设置缩放比例
			// 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
			try {
				in.reset();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			Log.d("mark", "mark not supported");
		}

		bitmap = BitmapFactory.decodeStream(in, null, newOpts);
		return compressImage(bitmap);// 压缩好比例大小后再进行质量压缩
	}
	
	public static Bitmap compressImage(String filepath,int inSampleSize){
		BitmapFactory.Options opts=new BitmapFactory.Options();//获取缩略图显示到屏幕上
		opts.inSampleSize = inSampleSize;
        return BitmapFactory.decodeFile(filepath,opts);
	}
	
	public static Bitmap setBitmapToImageView(String filepath,ImageView imageView){
        Bitmap cbitmap=compressImage(filepath,2);
		//Bitmap bitmap = BitmapFactory.decodeFile(filepath);
		if(cbitmap != null){
			//Bitmap image = BitmapUtils.compressImage(bitmap);
            //获取图片的旋转角度，有些系统把拍照的图片旋转了，有的没有旋转 
            int degree = BitmapUtils.readPictureDegree(filepath);  
            //把图片旋转为正的方向 
            Bitmap newbitmap = BitmapUtils.rotaingImageView(degree, cbitmap);  
            imageView.setImageBitmap(newbitmap);
		}
		return cbitmap;
	}
	
	/*
     * 读取图片属性：旋转的角度
     * @param path 图片绝对路径
     * @return degree旋转的角度
     */
    public static int readPictureDegree(String path) {
        int degree  = 0;
        try {
                ExifInterface exifInterface = new ExifInterface(path);
                int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                		ExifInterface.ORIENTATION_NORMAL);
                switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                        degree = 90;
                        break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                        degree = 180;
                        break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                        degree = 270;
                        break;
                }
        } catch (IOException e) {
                e.printStackTrace();
        }
        return degree;
    }
   /*
    * 旋转图片 
    */ 
   public static Bitmap rotaingImageView(int angle , Bitmap bitmap) {  
       //旋转图片动作   
       Matrix matrix = new Matrix();
       matrix.postRotate(angle);  
       // 创建新的图片
       Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,  
               bitmap.getWidth(), bitmap.getHeight(), matrix, true);  
       return resizedBitmap;  
   }

	public static Bitmap getImage(ContentResolver resolver, Uri uri, int ww,
			int hh) {
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		// 开始读入图片，此时把options.inJustDecodeBounds 设回true了
		newOpts.inJustDecodeBounds = true;
		Bitmap bitmap;
		InputStream is = null;
		try {
			is = resolver.openInputStream(uri);

			bitmap = BitmapFactory.decodeStream(is, null, newOpts);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
				}
			}
		}
		// 此时返回bm为空
		newOpts.inJustDecodeBounds = false;
		int w = newOpts.outWidth;
		int h = newOpts.outHeight;
		// 现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
		// 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
		int be = 1;// be=1表示不缩放
		if (w > h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
			be = (int) (newOpts.outWidth / ww);
		} else if (w < h && h > hh) {// 如果高度高的话根据宽度固定大小缩放
			be = (int) (newOpts.outHeight / hh);
		}
		if (be <= 0)
			be = 1;
		newOpts.inSampleSize = be;// 设置缩放比例
		// 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
		try {
			is = resolver.openInputStream(uri);
			bitmap = BitmapFactory.decodeStream(is, null, newOpts);
			return compressImage(bitmap);// 压缩好比例大小后再进行质量压缩
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
				}
			}
		}
		return null;
	}

	public static Bitmap compressBitmap(Bitmap image) {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		if (baos.toByteArray().length / 1024 > 1024) {// 判断如果图片大于1M,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出
			baos.reset();// 重置baos即清空baos
			image.compress(Bitmap.CompressFormat.JPEG, 50, baos);// 这里压缩50%，把压缩后的数据存放到baos中
		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		// 开始读入图片，此时把options.inJustDecodeBounds 设回true了
		newOpts.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
		newOpts.inJustDecodeBounds = false;
		int w = newOpts.outWidth;
		int h = newOpts.outHeight;
		// 现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
		float hh = 800f;// 这里设置高度为800f
		float ww = 480f;// 这里设置宽度为480f
		// 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
		int be = 1;// be=1表示不缩放
		if (w > h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
			be = (int) (newOpts.outWidth / ww);
		} else if (w < h && h > hh) {// 如果高度高的话根据宽度固定大小缩放
			be = (int) (newOpts.outHeight / hh);
		}
		if (be <= 0)
			be = 1;
		newOpts.inSampleSize = be;// 设置缩放比例
		// 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
		isBm = new ByteArrayInputStream(baos.toByteArray());
		bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
		return compressImage(bitmap);// 压缩好比例大小后再进行质量压缩
	}
	
	public static Bitmap getCircleBitmap(Context context,Bitmap bitmap,int height,int width){
		return getPersonalHeaderIcon(context, bitmap, width, height);
	}
	
	public static Bitmap getCircleBitmap(Context context,Bitmap bitmap){
		return getCircleBitmap(context,bitmap,-1,-1);
	}

	public static Bitmap getPersonalHeaderIcon(Context context,
			Bitmap headerIcon) {

		return getPersonalHeaderIcon(context, headerIcon, -1, -1);
	}


	public static Bitmap getPersonalHeaderIcon(Context context,
			Bitmap headerIcon, int width, int height) {

		Drawable mask = context.getResources().getDrawable(
				R.drawable.personal_account_center_headpic_mask);
		if (width <= 0)
			width = mask.getIntrinsicWidth();
		if (height <= 0)
			height = mask.getIntrinsicHeight();
		Bitmap bmp = Bitmap.createBitmap(width, height, Config.ARGB_8888);

		Canvas canvas = ViewUtil.getCanvas();
		canvas.setBitmap(bmp);

		Paint p = ViewUtil.getPaint();

		Rect src = ViewUtil.getRect1();
		src.set(0, 0, headerIcon.getWidth(), headerIcon.getHeight());
		Rect dst = ViewUtil.getRect2();
		dst.set(0, 0, width, height);
		canvas.drawBitmap(headerIcon, src, dst, p);

		p = null;
		if (mask instanceof BitmapDrawable) {
			p = ((BitmapDrawable) mask).getPaint();
		} else if (mask instanceof NinePatchDrawable) {
			p = ((NinePatchDrawable) mask).getPaint();
		} else if (mask instanceof ShapeDrawable) {
			p = ((ShapeDrawable) mask).getPaint();
		}
		if (p != null) {
			p.setAntiAlias(true);
			p.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
		}
		mask.setBounds(0, 0, width, height);
		mask.draw(canvas);

		ApiAdapter.setCanvasNullBitmap(canvas);
		return bmp;
	}
	
	public static Bitmap getMachineContactHeaderIcon(Context context,
			Bitmap headerIcon) {
		
		return getMachineContactHeaderIcon(context, headerIcon, -1, -1);
	}
	public static Bitmap getMachineContactHeaderIcon(Context context,
			Bitmap headerIcon, int width, int height) {
		
		Drawable mask = context.getResources().getDrawable(
				R.drawable.personal_account_center_headpic_mask);
		if (width <= 0)
			width = mask.getIntrinsicWidth();
		if (height <= 0)
			height = mask.getIntrinsicHeight();
		Bitmap bmp = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		
		Canvas canvas = ViewUtil.getCanvas();
		canvas.setBitmap(bmp);
		
		Paint p = ViewUtil.getPaint();
		
		Rect src = ViewUtil.getRect1();
		if(headerIcon.getWidth()>headerIcon.getHeight()){
			src.set(0, 0, headerIcon.getHeight(), headerIcon.getHeight());
		}else{
			src.set(0, 0, headerIcon.getWidth(), headerIcon.getWidth());
		}
		Rect dst = ViewUtil.getRect2();
		dst.set(0, 0, width, height);
		canvas.drawBitmap(headerIcon, src, dst, p);
		
		p = null;
		if (mask instanceof BitmapDrawable) {
			p = ((BitmapDrawable) mask).getPaint();
		} else if (mask instanceof NinePatchDrawable) {
			p = ((NinePatchDrawable) mask).getPaint();
		} else if (mask instanceof ShapeDrawable) {
			p = ((ShapeDrawable) mask).getPaint();
		}
		if (p != null) {
			p.setAntiAlias(true);
			p.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
		}
		mask.setBounds(0, 0, width, height);
		mask.draw(canvas);
		
		ApiAdapter.setCanvasNullBitmap(canvas);
		return bmp;
	}

	/**设置用户登录头像*/
	public static void setLoginHeadIcon(final Context context, String avatar,
			final ImageView icon, final int size, final int default_icon_id) {
		if(icon == null){
			return;
		}
		icon.setImageResource(default_icon_id);
		MainApplication.getHeadImageLoader().get(avatar, size,size,new ImageLoaderListener() {
			
			@Override
			public void onSuccess(ImageContainer container) {
				icon.setImageBitmap(container.getBitmap());
			}
			
			@Override
			public void onError(ImageContainer container) {
				icon.setImageResource(default_icon_id);
			}
		});
	}

	public static Bitmap getDefaultHeadIcon(Context context, final int size,
			int defalut_icon_id) {
		BitmapDrawable defaultIcon = (BitmapDrawable) context.getResources()
				.getDrawable(defalut_icon_id);
		Bitmap defaultBm = defaultIcon.getBitmap();
		return getPersonalHeaderIcon(context, defaultBm, size, size);
	}

	/**
	 * Returns the largest power-of-two divisor for use in downscaling a bitmap
	 * that will not result in the scaling past the desired dimensions.
	 * 
	 * @param actualWidth
	 *            Actual width of the bitmap
	 * @param actualHeight
	 *            Actual height of the bitmap
	 * @param desiredWidth
	 *            Desired width of the bitmap
	 * @param desiredHeight
	 *            Desired height of the bitmap
	 */
	// Visible for testing.
	public static int findBestSampleSize(int actualWidth, int actualHeight,
			int desiredWidth, int desiredHeight) {
		double wr = (double) actualWidth / desiredWidth;
		double hr = (double) actualHeight / desiredHeight;
		double ratio = Math.min(wr, hr);
		float n = 1.0f;
		while ((n * 2) <= ratio) {
			n *= 2;
		}

		return (int) n;
	}

	public static Bitmap decodeFile(String path,int maxWidth,int maxHeight,Config config) {
		Options opts = new Options();
		opts.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, opts);
		opts.inSampleSize = findBestSampleSize(opts.outWidth, opts.outHeight, maxWidth, maxHeight);
		opts.inJustDecodeBounds = false;
		opts.inPreferredConfig = config;
		return BitmapFactory.decodeFile(path, opts);
	}
}
