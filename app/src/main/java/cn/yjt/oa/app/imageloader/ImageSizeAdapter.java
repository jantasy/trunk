package cn.yjt.oa.app.imageloader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class ImageSizeAdapter {
	/**
	 * Scales one side of a rectangle to fit aspect ratio.
	 * 
	 * @param maxPrimary
	 *            Maximum size of the primary dimension (i.e. width for max
	 *            width), or zero to maintain aspect ratio with secondary
	 *            dimension
	 * @param maxSecondary
	 *            Maximum size of the secondary dimension, or zero to maintain
	 *            aspect ratio with primary dimension
	 * @param actualPrimary
	 *            Actual size of the primary dimension
	 * @param actualSecondary
	 *            Actual size of the secondary dimension
	 */
	public static int getResizedDimension(int maxPrimary, int maxSecondary,
			int actualPrimary, int actualSecondary) {
		// If no dominant value at all, just return the actual.
		if (maxPrimary == 0 && maxSecondary == 0) {
			return actualPrimary;
		}

		// If primary is unspecified, scale primary to match secondary's scaling
		// ratio.
		if (maxPrimary == 0) {
			double ratio = (double) maxSecondary / (double) actualSecondary;
			return (int) (actualPrimary * ratio);
		}

		if (maxSecondary == 0) {
			return maxPrimary;
		}

		double ratio = (double) actualSecondary / (double) actualPrimary;
		int resized = maxPrimary;
		if (resized * ratio > maxSecondary) {
			resized = (int) (maxSecondary / ratio);
		}
		return resized;
	}
	
	public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		if(reqWidth == 0 || reqHeight == 0){
			return 1;
		}
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			final int halfHeight = height / 2;
			final int halfWidth = width / 2;

			// Calculate the largest inSampleSize value that is a power of 2 and
			// keeps both
			// height and width larger than the requested height and width.
			while ((halfHeight / inSampleSize) > reqHeight
					&& (halfWidth / inSampleSize) > reqWidth) {
				inSampleSize *= 2;
			}
		}
		return inSampleSize;
	}

	
	public static Bitmap scaledBitmap(
			Bitmap bitmap,int actualWidth,int actualHeight) {
		int desiredWidth = ImageSizeAdapter.getResizedDimension(actualWidth,
				actualHeight, bitmap.getWidth(), bitmap.getHeight());
		int desiredHeight = ImageSizeAdapter.getResizedDimension(actualWidth,
				actualHeight, bitmap.getHeight(), bitmap.getWidth());
		Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, desiredWidth,
				desiredHeight, true);
		return scaledBitmap;
	}

}
