package rakco.com.travelconnectmindtree.helpers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;

import com.parse.ParseImageView;

/**
 * Created by Rakshak.R.Hegde on 18-03-2015.
 */
public class CircularParseImageView extends ParseImageView {

	private static final String TAG = "CircularParseImageView";

	public CircularParseImageView(Context context) {
		this(context, null);
	}

	public CircularParseImageView(Context context, AttributeSet attributeSet) {
		this(context, attributeSet, 0);
	}

	public CircularParseImageView(Context context, AttributeSet attributeSet, int defStyle) {
		super(context, attributeSet, defStyle);
	}

	@Override
	public void setImageBitmap(Bitmap bitmap) {
		super.setImageBitmap(transform(bitmap));
	}

	public Bitmap transform(Bitmap source) {
		int size = Math.min(source.getWidth(), source.getHeight());

		int x = (source.getWidth() - size) / 2;
		int y = (source.getHeight() - size) / 2;

		Bitmap squaredBitmap = Bitmap.createBitmap(source, x, y, size, size);
		if (squaredBitmap != source) {
			source.recycle();
		}

		Bitmap bitmap = Bitmap.createBitmap(size, size, source.getConfig());

		Canvas canvas = new Canvas(bitmap);
		Paint paint = new Paint();
		BitmapShader shader = new BitmapShader(squaredBitmap,
				BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
		paint.setShader(shader);
		paint.setAntiAlias(true);

		float r = size / 2f;
		canvas.drawCircle(r, r, r, paint);

		squaredBitmap.recycle();
		return bitmap;
	}
}
