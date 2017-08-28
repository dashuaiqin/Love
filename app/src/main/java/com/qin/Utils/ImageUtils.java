package com.qin.Utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.DrawableTypeRequest;
import com.bumptech.glide.Glide;
import com.qin.cons.IntegerCons;
import com.qin.cons.StringCons;
import com.qin.love.BaseActivity;
import com.qin.love.ChooseImageActivity;
import com.qin.love.MainActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;


/**
 * 获取照片的工具类
 * 
 * @author 123
 * 
 */
public class ImageUtils {
	private File PHOTO_FOLDER = new File(StringCons.PHOTO_DIR);// 拍照存储路径
	public File mCurrentPhotoFile;// 新照的照片的File对象
	private Context context;
	private  static ImageUtils instance;// 供其他地方调用

	public ImageUtils(Context context) {
		this.context = context;

	}

	public static ImageUtils getInstance(Context context){
		if(instance!=null){
			instance=null;
		}
	      instance=new ImageUtils(context);
	      return instance;
    }
	/**
	 * 通过调用系统相机拍照获取照片
	 */
	public String doTakePhoto() {
		String photoName = null;
		if (!PHOTO_FOLDER.exists()) {
			PHOTO_FOLDER.mkdirs();// 创建拍摄照片的存储目录
		}
		try {
			photoName = getPhotoFileName();
			mCurrentPhotoFile = new File(PHOTO_FOLDER,photoName);
			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); // 调用系统相机
			// 指定照片保存路径（SD卡），拍摄的照片会保存到此文件夹
			Uri imageUri = Uri.fromFile(mCurrentPhotoFile);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
			((Activity) context).startActivityForResult(intent,
					IntegerCons.PHOTO_WITH_CAMERA); // 调用相机拍摄照片后跳转
		} catch (Exception e) {
			Toast.makeText(context, "获取照片失败!" + e.getMessage(),
					Toast.LENGTH_SHORT).show();
		}
		return photoName;
	}

	/**
	 * 从相册中获取照片
	 */
	public void doGetPhoto() {
		Intent i = new Intent(context, ChooseImageActivity.class);
		((Activity) context).startActivityForResult(i,
				IntegerCons.PHOTO_WITH_ALBUM);// 从系统获取照片后的跳转
	}

	/**
	 * 用当前时间给取得的图片命名
	 * 
	 * @return
	 */
	@SuppressLint("SimpleDateFormat")
	public static String getPhotoFileName() {
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"'IMG'_yyyyMMdd_HHmmss");
		return dateFormat.format(date) + generateString(8) + ".jpg";
	}

	/**
	 * 将照片保存到SD卡中
	 * 
	 * @param photoBitmap
	 */

	public void savePhotoToSDCard(Bitmap photoBitmap) {
		if (!PHOTO_FOLDER.exists()) {
			PHOTO_FOLDER.mkdirs();// 创建拍摄照片的存储目录
		}
		File photoFile = new File(PHOTO_FOLDER, "icon.jpg");
		if (photoFile.exists()) {
			photoFile.delete();
		}
		try {
			FileOutputStream out = new FileOutputStream(photoFile);
			photoBitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
			out.flush();
			out.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 入口，显示获取照片的两种方式的菜单
	 */
	public void getphoto() {
		String[] items = { "拍照", "从相册获取" };
		AlertDialog dialog = new AlertDialog.Builder(context).setTitle("获取照片")
				.setItems(items, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int item) {
						if (item == 0)
							doTakePhoto();
						else
							doGetPhoto();
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();

					}
				}).create();

		dialog.show();// 显示对话框
	}

	/**
	 * 获取从本地图库返回来的时候的URI解析出来的文件路径
	 * 
	 * @return
	 */
	public static String getPhotoPathByLocalUri(Context context, Uri uri) {
		String[] filePathColumn = { MediaStore.Images.Media.DATA };
		Cursor cursor = context.getContentResolver().query(uri, filePathColumn,
				null, null, null);
		cursor.moveToFirst();
		int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
		String picturePath = cursor.getString(columnIndex);
		cursor.close();
		return picturePath;
	}

	/**
	 * 根据path通过内容提供器获取图片缩略图
	 * 
	 * @param context
	 * @param cr
	 * @param Imagepath
	 * @return
	 */
	public static Bitmap getImageThumbnail(Context context, ContentResolver cr,
			String Imagepath) {
		ContentResolver testcr = context.getContentResolver();
		String[] projection = { MediaStore.Images.Media.DATA,
				MediaStore.Images.Media._ID, };
		String whereClause = MediaStore.Images.Media.DATA + " = '" + Imagepath
				+ "'";
		Cursor cursor = testcr.query(
				MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection,
				whereClause, null, null);
		int _id = 0;
		if (cursor == null || cursor.getCount() == 0) {
			return null;
		}
		if (cursor.moveToFirst()) {
			int _idColumn = cursor.getColumnIndex(MediaStore.Images.Media._ID);
			do {
				_id = cursor.getInt(_idColumn);
			} while (cursor.moveToNext());
		}
		cursor.close();
		// 通过id获取到图片
		Bitmap bitmap = getBmByid(context, _id);
		return bitmap;
	}

	/**
	 * 根据指定的图像路径和大小来获取缩略图 此方法有两点好处： 1.
	 * 使用较小的内存空间，第一次获取的bitmap实际上为null，只是为了读取宽度和高度，
	 * 第二次读取的bitmap是根据比例压缩过的图像，第三次读取的bitmap是所要的缩略图。 2.
	 * 缩略图对于原图像来讲没有拉伸，这里使用了2.2版本的新工具ThumbnailUtils，使 用这个工具生成的图像不会被拉伸。
	 * 
	 * @param imagePath
	 *            图像的路径
	 * @param width
	 *            指定输出图像的宽度
	 * @param height
	 *            指定输出图像的高度
	 * @return 生成的缩略图
	 */
	public static Bitmap getImageThumbnailbyself(String imagePath, int width,
			int height) {
		Bitmap bitmap = null;
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		// 获取这个图片的宽和高，注意此处的bitmap为null
		bitmap = BitmapFactory.decodeFile(imagePath, options);
		options.inJustDecodeBounds = false; // 设为 false
		// 计算缩放比
		int h = options.outHeight;
		int w = options.outWidth;
		int beWidth = w / width;
		int beHeight = h / height;
		int be = 1;
		if (beWidth < beHeight) {
			be = beWidth;
		} else {
			be = beHeight;
		}
		if (be <= 0) {
			be = 1;
		}
		options.inSampleSize = be;
		// 重新读入图片，读取缩放后的bitmap，注意这次要把options.inJustDecodeBounds 设为 false
		bitmap = BitmapFactory.decodeFile(imagePath, options);
		// 利用ThumbnailUtils来创建缩略图，这里要指定要缩放哪个Bitmap对象
		bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
				ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
		return bitmap;
	}

	/**
	 * 获取图片文件夹列表
	 * 
	 * @param context
	 * @return
	 */

	public static JSONArray getPhotoFolderList(Context context) {
		JSONArray cacheJs = new JSONArray();
		// 查询的列
		String[] projection = new String[] { MediaStore.Images.Media._ID,
				MediaStore.Images.Media.BUCKET_ID, // 直接包含该图片文件的文件夹ID，防止在不同下的文件夹重名
				MediaStore.Images.Media.BUCKET_DISPLAY_NAME, // 直接包含该图片文件的文件夹名
				MediaStore.Images.Media.DISPLAY_NAME, // 图片文件名
				MediaStore.Images.Media.DATA, // 图片绝对路径
				"count(" + MediaStore.Images.Media._ID + ")"// 统计当前文件夹下共有多少张图片
		};
		// 这种写法是为了进行分组查询，详情可参考http://yelinsen.iteye.com/blog/836935
		String selection = " 0==0) group by bucket_display_name --(";
		try {// 开机还没加载完成时可能会出错，做一个容错
			Cursor cursor = context.getContentResolver().query(
					MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection,
					selection, null, null);
			while (cursor.moveToNext()) {
				String folderId = cursor.getString(cursor
						.getColumnIndex(MediaStore.Images.Media.BUCKET_ID));
				String folderName = cursor
						.getString(cursor
								.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME));
				int fileId = cursor.getInt(cursor
						.getColumnIndex(MediaStore.Images.Media._ID));
				String fileName = cursor.getString(cursor
						.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
				String filePath = cursor.getString(cursor
						.getColumnIndex(MediaStore.Images.Media.DATA));
				int fileCount = cursor.getInt(5);// 该文件夹下一共有多少张图片
				JSONObject object = new JSONObject();
				try {
					object.put("folderId", folderId);
					object.put("folderName", folderName);
					object.put("fileId", fileId);
					object.put("fileName", fileName);
					object.put("filePath", filePath);
					object.put("fileCount", fileCount);
				} catch (JSONException e) {
					e.printStackTrace();
					Log.i("HH", "错误原因：" + e.getMessage());
				}
				cacheJs.put(object);
			}
			if (null != cursor && !cursor.isClosed()) {
				cursor.close();
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return cacheJs;
	}

	/**
	 * 通过id获取缩略图bitmap
	 * 
	 * @return
	 */
	public static Bitmap getBmByid(Context context, int id) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inDither = false;
		options.inPreferredConfig = Bitmap.Config.RGB_565;
		return MediaStore.Images.Thumbnails.getThumbnail(
				context.getContentResolver(), id, Images.Thumbnails.MICRO_KIND,
				options);
	}

	/**
	 * 获取某个文件夹下的图片
	 *
	 * @return
	 */

	public static JSONArray getPhotoListByFolder(Context context,
			String folderName) {
		JSONArray cacheJs = new JSONArray();
		// 查询的列
		String[] projection = new String[] { MediaStore.Images.Media._ID,
				MediaStore.Images.Media.DATA, // 图片绝对路径
		};
		// 查询条件
		String selection = MediaStore.Images.Media.BUCKET_DISPLAY_NAME + "='"
				+ folderName + "'";

		Cursor cursor = context.getContentResolver().query(
				MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection,
				selection, null,
				MediaStore.Images.Media.DATE_MODIFIED + " DESC");
		while (cursor.moveToNext()) {
			int fileId = cursor.getInt(cursor
					.getColumnIndex(MediaStore.Images.Media._ID));
			String filePath = cursor.getString(cursor
					.getColumnIndex(MediaStore.Images.Media.DATA));
			JSONObject object = new JSONObject();
			try {
				object.put("fileId", fileId);
				object.put("filePath", filePath);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			cacheJs.put(object);
		}
		if (null != cursor && !cursor.isClosed()) {
			cursor.close();
		}
		return cacheJs;
	}

	/**
	 * 复制单个文件
	 * 
	 * @param oldPath
	 *            String 原文件路径 如：c:/fqf.txt
	 * @param newPath
	 *            String 复制后路径 如：f:/fqf.txt
	 * @return boolean
	 */
	public void copyFile(String oldPath, String newPath) {
		try {
			int byteread = 0;
			File oldfile = new File(oldPath);
			if (!oldfile.exists()) {
				return;
			}
			if (!oldfile.isFile()) {
				return;
			}
			if (!oldfile.canRead()) {
				return;
			}
			if (oldfile.exists()) { // 文件存在时
				InputStream inStream = new FileInputStream(oldPath); // 读入原文件
				FileOutputStream fs = new FileOutputStream(newPath);
				byte[] buffer = new byte[1444];
				while ((byteread = inStream.read(buffer)) != -1) {
					fs.write(buffer, 0, byteread);
				}
				inStream.close();
				fs.close();
			}
		} catch (Exception e) {
			System.out.println("复制单个文件操作出错");
			e.printStackTrace();

		}

	}

	/**
	 * 返回一个定长的随机字符串(只包含大小写字母、数字)，用于图片名字
	 * 
	 * @param length
	 *            随机字符串长度
	 * @return 随机字符串
	 */
	public static String generateString(int length) {
		String allChar = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
		StringBuffer sb = new StringBuffer();
		Random random = new Random();
		for (int i = 0; i < length; i++) {
			sb.append(allChar.charAt(random.nextInt(allChar.length())));
		}
		return sb.toString();
	}

	/**
	 * 获取到图片名字，并连接成字符串
	 * 
	 * @return
	 */
	public String getPhotoNames(@SuppressWarnings("rawtypes") List list) {
		String paths = "";
		for (int i = 0; i < list.size(); i++) {
			@SuppressWarnings("unchecked")
			Map<String, Object> map = (Map<String, Object>) list.get(i);
			String photoName = (String) map.get("photoName");
			paths += photoName + "\0";
		}
		return paths.substring(0, paths.length());
	}
	/**
	 * 根据路径获取bitmap
	 * @param path
	 * @return
	 */
	public static Bitmap convertToBitmap(String path) {
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inPreferredConfig = Bitmap.Config.RGB_565;
		opts.inJustDecodeBounds = false;
//		opts.inSampleSize = 2;
		Bitmap bitmap = BitmapFactory.decodeFile(path, opts);
		return bitmap;
	}

	/**
	 * 截取圆形的图片
	 * @param bitmap
	 * @return
	 */
	public static Bitmap toRoundBitmap(Bitmap bitmap) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		int r = 0;
		if(width > height) {
			r = height;
		} else {
			r = width;
		}
		Bitmap backgroundBmp = Bitmap.createBitmap(width,
				height, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(backgroundBmp);
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		RectF rect = new RectF(0, 0, r, r);
		canvas.drawRoundRect(rect, r / 2, r / 2, paint);
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
		canvas.drawBitmap(bitmap, null, rect, paint);
		return backgroundBmp;
	}

	/**
	 * 把bitmap转换成String
	 *
	 * @param filePath
	 * @return
	 */
	public static String bitmapToString(String filePath) {

		Bitmap bm = getSuitableBitmap(filePath);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.JPEG, 40, baos);
		byte[] b = baos.toByteArray();

		return Base64.encodeToString(b, Base64.DEFAULT);

	}

	/**
	 * 计算图片的缩放值
	 *
	 * @param options
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 */
	public static int calculateInSampleSize(BitmapFactory.Options options,
											int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			// Calculate ratios of height and width to requested height and
			// width
			final int heightRatio = Math.round((float) height
					/ (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);

			// Choose the smallest ratio as inSampleSize value, this will
			// guarantee
			// a final image with both dimensions larger than or equal to the
			// requested height and width.
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		}

		return inSampleSize;
	}




	/**
	 * 根据路径获得突破并压缩返回bitmap用于显示
	 *
	 * @param filePath
	 * @return
	 */
public static  Bitmap getSuitableBitmap(String filePath){
	Bitmap bitmap = null;
    int width= MainActivity.mScreenWidth;
    int height= MainActivity.mScreenHeight;
	final BitmapFactory.Options options = new BitmapFactory.Options();
	options.inJustDecodeBounds = true;
	BitmapFactory.decodeFile(filePath, options);//不分配内存，指获取长款
	while(bitmap == null) {
	   try {
		   width=width-90;
		   height=height-150;
	    // Calculate inSampleSize
	    options.inSampleSize = calculateInSampleSize(options, width, height);
		   // Decode bitmap with inSampleSize set
		   options.inJustDecodeBounds = false;
		// 实例化Bitmap
		bitmap = 	BitmapFactory.decodeFile(filePath, options);
	    } catch (OutOfMemoryError e) {

	    }
	}
		return bitmap;
	}

	/**
	 * 根据路径删除图片
	 *
	 * @param path
	 */
	public static void deleteTempFile(String path) {
		File file = new File(path);
		if (file.exists()) {
			file.delete();
		}
	}

	/**
	 * 添加到图库
	 */
	public static void galleryAddPic(Context context, String path) {
		Intent mediaScanIntent = new Intent(
				Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
		File f = new File(path);
		Uri contentUri = Uri.fromFile(f);
		mediaScanIntent.setData(contentUri);
		context.sendBroadcast(mediaScanIntent);
	}




	/**
	 * 通过imageloader显示图片
	 */
	public static void showImageByLoader(Context context,String imageUrl, final ImageView imageView,int loading) {
		DrawableTypeRequest<String> dt= Glide.with(context).load(imageUrl);
		if (loading!= IntegerCons.NONE){
			dt.placeholder(loading);
		}
		dt.into(imageView);
	}

	/**
	 * 删除图片
	 */
	public static void deletePhoto(final BaseActivity activity,String path) {
		if (StringUtils.isBlank(path)) {
			return;
		}
		BmobFile file = new BmobFile();
		file.setUrl(path);//此url是上传文件成功之后通过bmobFile.getUrl()方法获取的。
		file.delete(new UpdateListener() {

			@Override
			public void done(BmobException e) {
				if(e==null){
					activity.shortToast("删除文件成功");
				}else{
					activity.shortToast("删除文件失败：" +e.getMessage());
				}
			}
		});
	}

	/**
	 * 图片压缩
	 */
	public static String Jpeg(String path,int quality,Context context) {
		String pn = ImageUtils.getPhotoFileName();
		Bitmap bit = ImageUtils.getSuitableBitmap(path);
		File dirFile = context.getExternalCacheDir();
		if (!dirFile.exists()) {
			dirFile.mkdirs();
		}
		File jpegTrueFile = new File(dirFile, pn);
		NativeUtil.compressBitmap(bit, quality,
				jpegTrueFile.getAbsolutePath(), false);
		if (bit != null && !bit.isRecycled()) {
			bit.recycle();
			bit = null;
		}

		return context.getExternalCacheDir().toString() + "/" + pn;
	}
}
