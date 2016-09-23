package com.jim.pocketaccounter.utils.record;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.v4.content.ContextCompat;

import com.jim.pocketaccounter.PocketAccounterApplication;
import com.jim.pocketaccounter.R;
import com.jim.pocketaccounter.database.BoardButton;
import com.jim.pocketaccounter.database.DaoSession;
import com.jim.pocketaccounter.database.RootCategory;
import com.jim.pocketaccounter.utils.PocketAccounterGeneral;
import com.jim.pocketaccounter.utils.cache.DataCache;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

public class RecordButtonIncome {
	private boolean pressed = false;
	private RectF container;
	private int type;
	private BoardButton boardButton;
	private Path shape;
	private Bitmap shadow;
	private float radius, clearance;
	private float aLetterHeight;
	private Context context;
	private Calendar date;
	private BitmapFactory.Options options;
	@Inject
	DaoSession daoSession;
	@Inject
	DataCache dataCache;
	public RecordButtonIncome(Context context, int type, Calendar date) {
		this.context = context;
		((PocketAccounterApplication) context.getApplicationContext()).component().inject(this);
		options = new BitmapFactory.Options();
		options.inPreferredConfig = Bitmap.Config.RGB_565;
		clearance = context.getResources().getDimension(R.dimen.one_dp);
		shape = new Path();
		Paint paint = new Paint();
		paint.setTextSize(context.getResources().getDimension(R.dimen.ten_sp));
		Rect bounds = new Rect();
		paint.getTextBounds("A", 0, "A".length(), bounds);
		aLetterHeight = bounds.height();
		this.type = type;
		this.date = (Calendar) date.clone();
	}
	public void setBounds(float left, float top, float right, float bottom, float radius) {
		container = new RectF(left, top, right, bottom);
		this.radius = radius;
		switch(type) {
		case PocketAccounterGeneral.DOWN_MOST_LEFT:
			initMostLeft();
			break;
		case PocketAccounterGeneral.DOWN_SIMPLE:
			initSimple();
			break;
		case PocketAccounterGeneral.DOWN_MOST_RIGHT:
			initMostRight();
			break;
		}
	}
	private void initMostLeft() {
		shape.moveTo(container.left+2*radius, container.top);
		shape.lineTo(container.right, container.top);
		shape.lineTo(container.right, container.bottom);
		shape.lineTo(container.left+2*radius, container.bottom);
		shape.arcTo(new RectF(container.left, container.bottom-2*radius, container.left+2*radius, container.bottom), 90.0f, 90.0f);
		shape.lineTo(container.left, container.top+2*radius);
		shape.arcTo(new RectF(container.left, container.top, container.left+2*radius, container.top+2*radius), 180.0f, 90.0f);
		shape.close();
		float bitmapWidth, bitmapHeight;
		bitmapHeight = 6*container.height()/5;
		bitmapWidth = bitmapHeight;
		if (dataCache.getElements().get(PocketAccounterGeneral.DOWN_MOST_LEFT) == null) {
			shadow = BitmapFactory.decodeResource(context.getResources(), R.drawable.left_bottom, options);
			shadow = Bitmap.createScaledBitmap(shadow, (int)(bitmapWidth-2*clearance), (int)(bitmapHeight-2*clearance), false);
			dataCache.getElements().put(PocketAccounterGeneral.DOWN_MOST_LEFT, shadow);
		}
		else
			shadow = dataCache.getElements().get(PocketAccounterGeneral.DOWN_MOST_LEFT);

	}
	private void initMostRight() {
		shape.moveTo(container.left, container.top);
		shape.lineTo(container.right, container.top);
		shape.lineTo(container.right, container.bottom-2*radius);
		shape.arcTo(new RectF(container.right-2*radius, container.bottom-2*radius, container.right, container.bottom), 0.0f, 90.0f);
		shape.lineTo(container.left, container.bottom);
		shape.lineTo(container.left, container.top);
		shape.close();
		float bitmapWidth, bitmapHeight;
		bitmapHeight = container.height()/5;
		bitmapWidth = bitmapHeight*6;
		if (dataCache.getElements().get(PocketAccounterGeneral.DOWN_MOST_RIGHT) == null) {
			shadow = BitmapFactory.decodeResource(context.getResources(), R.drawable.bottom_shadow, options);
			shadow = Bitmap.createScaledBitmap(shadow, (int)(bitmapWidth-2*clearance), (int)bitmapHeight, false);
			dataCache.getElements().put(PocketAccounterGeneral.DOWN_MOST_RIGHT, shadow);
		}
		else
			shadow = dataCache.getElements().get(PocketAccounterGeneral.DOWN_MOST_RIGHT);
	}
	private void initSimple() {
		shape.moveTo(container.left, container.top);
		shape.lineTo(container.right, container.top);
		shape.lineTo(container.right, container.bottom);
		shape.lineTo(container.left, container.bottom);
		shape.lineTo(container.left, container.top);
		shape.close();
		float bitmapWidth, bitmapHeight;
		bitmapHeight = container.height()/5;
		bitmapWidth = bitmapHeight*6;
		if (dataCache.getElements().get(PocketAccounterGeneral.DOWN_SIMPLE) == null) {
			shadow = BitmapFactory.decodeResource(context.getResources(), R.drawable.bottom_shadow, options);
			shadow = Bitmap.createScaledBitmap(shadow, (int)(bitmapWidth-2*clearance), (int)bitmapHeight, false);
			dataCache.getElements().put(PocketAccounterGeneral.DOWN_SIMPLE, shadow);
		}
		else
			shadow = dataCache.getElements().get(PocketAccounterGeneral.DOWN_SIMPLE);
	}
	public void drawButton(Canvas canvas) {
		Bitmap scaled;
		Paint bitmapPaint = new Paint();
		bitmapPaint.setAntiAlias(true);
		bitmapPaint.setAlpha(0x77);
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(Color.WHITE);
		switch(type) {
		case PocketAccounterGeneral.DOWN_MOST_LEFT:
			if (!pressed) 
				canvas.drawBitmap(shadow, container.right-shadow.getWidth(), container.top, bitmapPaint);
			canvas.drawPath(shape, paint);
			if (pressed) {
				Paint innerShadowPaint = new Paint();
				innerShadowPaint.setColor(Color.BLACK);
				innerShadowPaint.setAlpha(0x22);
				innerShadowPaint.setAntiAlias(true);
				canvas.drawPath(shape, innerShadowPaint);
				Bitmap innerShadow;
				if (dataCache.getElements().get(PocketAccounterGeneral.DOWN_MOST_LEFT_PRESSED) == null) {
					innerShadow = BitmapFactory.decodeResource(context.getResources(), R.drawable.record_pressed_first, options);
					innerShadow = Bitmap.createScaledBitmap(innerShadow, (int)(container.width()/5), (int)container.height(), false);
					dataCache.getElements().put(PocketAccounterGeneral.DOWN_MOST_LEFT_PRESSED, innerShadow);
				}
				else
					innerShadow = dataCache.getElements().get(PocketAccounterGeneral.DOWN_MOST_LEFT_PRESSED);
				innerShadowPaint.setAlpha(0x66);
				canvas.drawBitmap(innerShadow, container.right-innerShadow.getWidth(), container.top, innerShadowPaint);
			}
			break;
		case PocketAccounterGeneral.DOWN_SIMPLE:
			if (!pressed) 
				canvas.drawBitmap(shadow, container.left-shadow.getHeight(), container.bottom, bitmapPaint);
			canvas.drawPath(shape, paint);
			if (pressed) {
				Paint innerShadowPaint = new Paint();
				innerShadowPaint.setColor(Color.BLACK);
				innerShadowPaint.setAlpha(0x22);
				canvas.drawPath(shape, innerShadowPaint);
				Bitmap innerShadow;
				if (dataCache.getElements().get(PocketAccounterGeneral.DOWN_SIMPLE_PRESSED) == null) {
					innerShadow = BitmapFactory.decodeResource(context.getResources(), R.drawable.record_pressed_first, options);
					innerShadow = Bitmap.createScaledBitmap(innerShadow, (int)(container.width()/5), (int)container.height(), false);
					dataCache.getElements().put(PocketAccounterGeneral.DOWN_SIMPLE_PRESSED, innerShadow);
				}
				else
					innerShadow = dataCache.getElements().get(PocketAccounterGeneral.DOWN_SIMPLE_PRESSED);
				innerShadowPaint.setAlpha(0x66);
				canvas.drawBitmap(innerShadow, container.right-innerShadow.getWidth(), container.top, innerShadowPaint);
			}
			break;
		case PocketAccounterGeneral.DOWN_MOST_RIGHT:
			if (!pressed) 
				canvas.drawBitmap(shadow, container.left-shadow.getHeight(), container.bottom, bitmapPaint);
			canvas.drawPath(shape, paint);
			if (pressed) {
				Paint innerShadowPaint = new Paint();
				innerShadowPaint.setColor(Color.BLACK);
				innerShadowPaint.setAlpha(0x22);
				canvas.drawPath(shape, innerShadowPaint);
			}
			break;
		}
		bitmapPaint.setAlpha(0xFF);
		if (boardButton.getCategoryId() != null) {
			RootCategory category = null;
			List<RootCategory> categories = daoSession.getRootCategoryDao().loadAll();
			for (RootCategory cat : categories) {
				if (cat.getId().matches(boardButton.getCategoryId())) {
					category = cat;
					break;
				}
			}
			int resId = context.getResources().getIdentifier(category.getIcon(), "drawable", context.getPackageName());
			if (dataCache.getBoardBitmapsCache().get(16+boardButton.getPos()) == null) {
				scaled = BitmapFactory.decodeResource(context.getResources(), resId);
				scaled = Bitmap.createScaledBitmap(scaled, (int)context.getResources().getDimension(R.dimen.thirty_dp), (int)context.getResources().getDimension(R.dimen.thirty_dp), true);
				List<Bitmap> list = new ArrayList<>();
				list.add(scaled);
				dataCache.getBoardBitmapsCache().put(16+boardButton.getPos(), list);
			}
			else
				scaled = dataCache.getBoardBitmapsCache().get(16+boardButton.getPos()).get(0);
			canvas.drawBitmap(scaled, container.centerX()-scaled.getWidth()/2, container.centerY()-scaled.getHeight(), bitmapPaint);
			Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
			textPaint.setColor(ContextCompat.getColor(context, R.color.toolbar_text_color));
			textPaint.setTextSize(context.getResources().getDimension(R.dimen.ten_sp));
			Rect bounds = new Rect();
			String catText = category.getName();
			for (int i=0; i < category.getName().length(); i++) {
				textPaint.getTextBounds(category.getName(), 0, i, bounds);
				if (bounds.width() >= container.width()) {
					catText = category.getName().substring(0, i-5);
					catText = catText + "...";
					break;
				}
			}
			textPaint.getTextBounds(catText, 0, catText.length(), bounds);
			canvas.drawText(catText, container.centerX()-bounds.width()/2, container.centerY()+2*aLetterHeight, textPaint);
		} else {
			if (dataCache.getBoardBitmapsCache().get(16+boardButton.getPos()) == null) {
				scaled = BitmapFactory.decodeResource(context.getResources(), R.drawable.no_category, options);
				scaled = Bitmap.createScaledBitmap(scaled, (int)context.getResources().getDimension(R.dimen.thirty_dp), (int)context.getResources().getDimension(R.dimen.thirty_dp), true);
				List<Bitmap> list = new ArrayList<>();
				list.add(scaled);
				dataCache.getBoardBitmapsCache().put(16+boardButton.getPos(), list);
			}
			else
				scaled = dataCache.getBoardBitmapsCache().get(16+boardButton.getPos()).get(0);
			canvas.drawBitmap(scaled, container.centerX()-scaled.getWidth()/2, container.centerY()-scaled.getHeight(), bitmapPaint);
			Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
			textPaint.setColor(ContextCompat.getColor(context, R.color.toolbar_text_color));
			textPaint.setTextSize(context.getResources().getDimension(R.dimen.ten_sp));
			Rect bounds = new Rect();
			String text = context.getResources().getString(R.string.add);
			textPaint.getTextBounds(text, 0, text.length(), bounds);
			canvas.drawText(text, container.centerX()-bounds.width()/2, container.centerY()+2*aLetterHeight, textPaint);
		}
	}
	public void setPressed(boolean pressed) {
		this.pressed = pressed;
	}
	public RectF getContainer() {
		return container;
	}
	public Path getShape() {
		return shape;
	}
	public void setCategory(BoardButton boardButton) {this.boardButton = boardButton;}
	public BoardButton getCategory() {return boardButton;}
}
