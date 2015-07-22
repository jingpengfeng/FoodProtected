package com.gzmob.utils;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.lang.ref.WeakReference;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.widget.ImageView;

public class SDImageLoader {

	public void load(Context c, String filePath, ImageView v,
			int placeHolderImageId) {
		if (cancelPotentialSDLoad(filePath, v)) {
			SDLoadImageTask task = new SDLoadImageTask(v);
			Bitmap b = BitmapFactory.decodeResource(c.getResources(),
					placeHolderImageId);
			SDLoadDrawable sdDrawable = new SDLoadDrawable(b, task);
			v.setImageDrawable(sdDrawable);
			task.execute(filePath);
		}
	}

	private Bitmap loadImageFromSDCard(String filePath) {

		try {

			/** 谷歌关于inSampleSize的动态算法 */
			FileInputStream f = new FileInputStream(new File(filePath));
			FileDescriptor fd = f.getFD();
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeFileDescriptor(fd, null, options);
			options.inSampleSize = computeSampleSize(options, 400, 800 * 480);
			// options.inSampleSize = computeSampleSize(options, 80, 128 * 128);
			options.inJustDecodeBounds = false;
			Bitmap photo = BitmapFactory.decodeStream(f, null, options);

			// BitmapFactory.Options bfo = new BitmapFactory.Options();
			// bfo.inSampleSize = 4;
			// bfo.outWidth = 150;
			// bfo.outHeight = 150;
			// Bitmap photo = BitmapFactory.decodeFile(filePath, bfo);
			return photo;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private static boolean cancelPotentialSDLoad(String filePath, ImageView v) {

		SDLoadImageTask sdLoadTask = getAsyncSDLoadImageTask(v);

		if (sdLoadTask != null) {
			String path = sdLoadTask.getFilePath();
			if ((path == null) || (!path.equals(filePath))) {
				sdLoadTask.cancel(true);
			} else {
				return false;
			}
		}
		return true;
	}

	private static SDLoadImageTask getAsyncSDLoadImageTask(ImageView v) {

		if (v != null) {
			Drawable drawable = v.getDrawable();
			if (drawable instanceof SDLoadDrawable) {
				SDLoadDrawable asyncLoadedDrawable = (SDLoadDrawable) drawable;
				return asyncLoadedDrawable.getAsyncSDLoadTask();
			}
		}
		return null;
	}

	private class SDLoadImageTask extends AsyncTask<String, Void, Bitmap> {

		private String mFilePath;
		private final WeakReference<ImageView> mImageViewReference;

		public String getFilePath() {
			return mFilePath;
		}

		public SDLoadImageTask(ImageView v) {
			mImageViewReference = new WeakReference<ImageView>(v);
		}

		@Override
		protected void onPostExecute(Bitmap bmp) {
			if (mImageViewReference != null) {
				ImageView v = mImageViewReference.get();
				SDLoadImageTask sdLoadTask = getAsyncSDLoadImageTask(v);
				// Change bitmap only if this process is still associated with
				// it
				if (this == sdLoadTask) {
					if (bmp != null) {
						v.setImageBitmap(bmp);
					}
				}
			}
		}

		@Override
		protected Bitmap doInBackground(String... params) {
			mFilePath = params[0];
			return loadImageFromSDCard(mFilePath);
		}
	}

	private class SDLoadDrawable extends BitmapDrawable {
		private final WeakReference<SDLoadImageTask> asyncSDLoadTaskReference;

		public SDLoadDrawable(Bitmap b, SDLoadImageTask asyncSDLoadTask) {
			super(b);
			asyncSDLoadTaskReference = new WeakReference<SDLoadImageTask>(
					asyncSDLoadTask);
		}

		public SDLoadImageTask getAsyncSDLoadTask() {
			return asyncSDLoadTaskReference.get();
		}
	}

	// 计算图片的缩放值（1）
	public static int computeSampleSize(BitmapFactory.Options options,
			int minSideLength, int maxNumOfPixels) {
		int initialSize = computeInitialSampleSize(options, minSideLength,
				maxNumOfPixels);

		int roundedSize;
		if (initialSize <= 8) {
			roundedSize = 1;
			while (roundedSize < initialSize) {
				roundedSize <<= 1;
			}
		} else {
			roundedSize = (initialSize + 7) / 8 * 8;
		}

		return roundedSize;
	}

	// 计算图片的缩放值（2）
	private static int computeInitialSampleSize(BitmapFactory.Options options,
			int minSideLength, int maxNumOfPixels) {
		double w = options.outWidth;
		double h = options.outHeight;

		int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math
				.sqrt(w * h / maxNumOfPixels));
		int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(
				Math.floor(w / minSideLength), Math.floor(h / minSideLength));

		if (upperBound < lowerBound) {
			// return the larger one when there is no overlapping zone.
			return lowerBound;
		}

		if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
			return 1;
		} else if (minSideLength == -1) {
			return lowerBound;
		} else {
			return upperBound;
		}
	}
}