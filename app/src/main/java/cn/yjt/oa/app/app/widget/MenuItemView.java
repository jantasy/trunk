package cn.yjt.oa.app.app.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.widget.dragdrop.DragSource;
import cn.yjt.oa.app.widget.dragdrop.DropTarget;

public class MenuItemView extends RelativeLayout implements DragSource,
		DropTarget {

	public int size;// 1,2,3 1/4 2/1 1
	public int x;
	public int y;
	public String id;//
	private MenuItemView dragItem;

	public MenuItemView(Context context) {
		super(context);
	}

	public MenuItemView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public MenuItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public void drawShadow(Canvas canvas) {
		this.destroyDrawingCache();
		this.setDrawingCacheEnabled(true);
		this.setDrawingCacheBackgroundColor(0x000000);
		Bitmap bm = Bitmap.createBitmap(this.getDrawingCache(true));
		Bitmap bitmap = Bitmap.createBitmap(bm, 8, 8, bm.getWidth() - 8,
				bm.getHeight() - 8);
		Paint paint = new Paint();
		paint.setAlpha(0x70); // 设置透明程度
		canvas.drawBitmap(bitmap, 0, 0, paint);
		// draw(canvas);
	}

	@Override
	public Object startDrag() {
		setVisibility(View.INVISIBLE);
		return this;
	}

	@Override
	public void onDragComplete(boolean isCanceled) {
		setVisibility(View.VISIBLE);
	}

	@Override
	public boolean accept(Object dragItem, DragSource dragSource) {
		if (dragItem != null && dragItem instanceof MenuItemView) {
			this.dragItem = (MenuItemView) dragItem;
			return true;
		}
		return false;
	}

	// 如果该view被拖拽，当完成拖拽时如何处理
	@Override
	public void drop(Object dragItem,int x, int y) {
		//在这进行交换界面
		if(dragItem!=null&&dragItem instanceof MenuItemView)
			((MentroView)getParent()).swapChild(this, (MenuItemView)dragItem);
	}

	// 处理和被移动的交换
	@Override
	public void onDragIn(int x, int y) {
		System.out.println("onDragIn");
	}

	// 不需要处理
	@Override
	public void onDragOver(int x, int y) {
		System.out.println("onDragOver");
	}

	// 不需要处理
	@Override
	public void onDragOut(int x, int y) {
		System.out.println("onDragOut");
	}
	
	public void setTitle(int rid){
		((TextView)findViewById(R.id.tv_title)).setText(rid);
	}
	public void setIcon(int rid){
		((ImageView)findViewById(R.id.img_icon)).setImageResource(rid);
	}
}
