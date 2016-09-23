package com.jim.pocketaccounter.utils.record;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.jim.pocketaccounter.PocketAccounter;
import com.jim.pocketaccounter.PocketAccounterApplication;
import com.jim.pocketaccounter.R;
import com.jim.pocketaccounter.database.BoardButton;
import com.jim.pocketaccounter.database.BoardButtonDao;
import com.jim.pocketaccounter.database.DaoSession;
import com.jim.pocketaccounter.database.FinanceRecordDao;
import com.jim.pocketaccounter.database.RootCategoryDao;
import com.jim.pocketaccounter.finance.CategoryAdapterForDialog;
import com.jim.pocketaccounter.database.FinanceRecord;
import com.jim.pocketaccounter.database.RootCategory;
import com.jim.pocketaccounter.fragments.RecordEditFragment;
import com.jim.pocketaccounter.managers.CommonOperations;
import com.jim.pocketaccounter.managers.PAFragmentManager;
import com.jim.pocketaccounter.utils.PocketAccounterGeneral;
import com.jim.pocketaccounter.utils.cache.DataCache;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Vibrator;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GestureDetectorCompat;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import javax.inject.Inject;

@SuppressLint("DrawAllocation")
public class RecordIncomesView extends View implements 	GestureDetector.OnGestureListener{
	private final float workspaceCornerRadius, workspaceMargin;
	private Bitmap workspaceShader;
	private RectF workspace;
	private ArrayList<RecordButtonIncome> buttons;
	private GestureDetectorCompat gestureDetector;
	private Calendar date;
	private Canvas canvas;
	@Inject
	DaoSession daoSession;
	@Inject
	PAFragmentManager paFragmentManager;
	@Inject
	DataCache dataCache;
	@Inject
	CommonOperations commonOperations;
	public RecordIncomesView(Context context, Calendar date) {
		super(context);
		((PocketAccounter) context).component((PocketAccounterApplication) context.getApplicationContext()).inject(this);
		gestureDetector = new GestureDetectorCompat(getContext(),this);
		workspaceCornerRadius = getResources().getDimension(R.dimen.five_dp);
		workspaceMargin = getResources().getDimension(R.dimen.twenty_dp);
		this.date = (Calendar) date.clone();
		initButtons();
		setClickable(true);
	}
	private void initButtons() {
		buttons = new ArrayList<>();
		RecordButtonIncome button;
		int type = 0;
		BoardButtonDao boardButtonDao = daoSession.getBoardButtonDao();
		List<BoardButton> boardButtonList = boardButtonDao.loadAll();
		for (int i=0; i<4; i++) {
			switch(i) {
				case 0:
					type = PocketAccounterGeneral.DOWN_MOST_LEFT;
					break;
				case 1:
				case 2:
					type = PocketAccounterGeneral.DOWN_SIMPLE;
					break;
				case 3:
					type = PocketAccounterGeneral.DOWN_MOST_RIGHT;
					break;
			}
			button = new RecordButtonIncome(getContext(), type, date);
			for (int j=0; j<boardButtonList.size(); j++) {
				if (boardButtonList.get(j).getTable() == PocketAccounterGeneral.INCOME &&
						boardButtonList.get(j).getPos() == i) {
					if (boardButtonList.get(j).getCategoryId() == null ||
							boardButtonList.get(j).getCategoryId().matches("")) {
						button.setCategory(null);
						break;
					}
					else
						button.setCategory(boardButtonList.get(j));
				}
			}
			buttons.add(button);
		}
	}
	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		this.canvas = canvas;
		workspace = new RectF(workspaceMargin,
								workspaceMargin,
								getWidth()-workspaceMargin,
								getHeight()-workspaceMargin);
		drawButtons();
		drawPercents();
		drawWorkspaceShader();
	}
	private void drawButtons() {
		float width, height;
		width = workspace.width()/4;
		height = workspace.height();
		float left, top, right, bottom;
		for (int i=0; i<4; i++) {
			left = workspace.left+(i%4)*width;
			top = workspace.top+((int)Math.floor(i/4)*height);
			right = workspace.left+(i%4+1)*width;
			bottom = workspace.top+((int)(Math.floor(i/4)+1)*height);
			buttons.get(i).setBounds(left, top, right, bottom, workspaceCornerRadius);
			buttons.get(i).drawButton(canvas);
		}
		Paint borderPaint = new Paint();
		borderPaint.setColor(ContextCompat.getColor(getContext(), R.color.record_borders));
		borderPaint.setStrokeWidth(getResources().getDimension(R.dimen.one_dp));
		for (int i=0; i<3; i++) 
			canvas.drawLine(workspace.left+(i+1)*width, workspace.top, workspace.left+(i+1)*width, workspace.bottom, borderPaint);
	}
	public void drawPercents() {
		Calendar begin = (Calendar)dataCache.getEndDate().clone();
		begin.set(Calendar.HOUR_OF_DAY, 0);
		begin.set(Calendar.MINUTE, 0);
		begin.set(Calendar.SECOND, 0);
		begin.set(Calendar.MILLISECOND, 0);
		Calendar end = (Calendar) begin.clone();
		end.set(Calendar.HOUR_OF_DAY, 23);
		end.set(Calendar.MINUTE, 59);
		end.set(Calendar.SECOND, 59);
		end.set(Calendar.MILLISECOND, 59);
		List<FinanceRecord> allDay = daoSession.getFinanceRecordDao()
				.queryBuilder()
				.where(	FinanceRecordDao.Properties.Date.ge(begin.getTimeInMillis()),
						FinanceRecordDao.Properties.Date.le(end.getTimeInMillis()))
				.list();
		if (sum(allDay) == 0) return;
		Rect bounds = new Rect();
		Paint textPaint = new Paint();
		textPaint.setColor(ContextCompat.getColor(getContext(), R.color.red));
		textPaint.setTextSize(getResources().getDimension(R.dimen.ten_sp));
		textPaint.setAntiAlias(true);
		Rect letBound = new Rect();
		textPaint.getTextBounds("A", 0, "A".length(), letBound);
		DecimalFormat format = new DecimalFormat("0.00");
		float aLetterHeight = letBound.height();
		List<FinanceRecord> byCategory = new ArrayList<>();
		for (RecordButtonIncome button : buttons) {
			if (button.getCategory() == null) continue;
			byCategory.clear();
			for (FinanceRecord financeRecord : allDay) {
				if (financeRecord.getCategory().getId().matches(button.getCategory().getCategoryId())) {
					byCategory.add(financeRecord);
				}
			}
			if (sum(byCategory) == 0) return;
			double amount = 0.0;
			if (sum(allDay) != 0)
				amount = 100 * sum(byCategory) / sum(allDay);
			String text = format.format(amount)+"%";
			textPaint.getTextBounds(text, 0, text.length(), bounds);
			canvas.drawText(text, button.getContainer().centerX()-bounds.width()/2,
					button.getContainer().centerY()+4*aLetterHeight, textPaint);
		}
	}
	private double sum(List<FinanceRecord> records) {
		double result = 0.0;
		for (FinanceRecord record : records) {
			if (record.getCategory().getType() == PocketAccounterGeneral.INCOME)
				result += commonOperations.getCost(record);
			else
				result -= commonOperations.getCost(record);
		}
		return result;
	}
	private void drawWorkspaceShader() {
		if (dataCache.getElements().get(PocketAccounterGeneral.DOWN_WORKSPACE_SHADER) == null) {
			workspaceShader = commonOperations.getRoundedCornerBitmap(
					commonOperations.decodeSampledBitmapFromResource(
							getResources(), R.drawable.workspace_shader, (int)workspace.width(), (int)workspace.height()),
					(int) workspaceCornerRadius);
			workspaceShader = Bitmap.createScaledBitmap(workspaceShader, (int)workspace.width(), (int)workspace.height(), false);
			dataCache.getElements().put(PocketAccounterGeneral.DOWN_WORKSPACE_SHADER, workspaceShader);
		}
		else
			workspaceShader = dataCache.getElements().get(PocketAccounterGeneral.DOWN_WORKSPACE_SHADER);
		Paint paint = new Paint();
		paint.setAlpha(0x55);
		paint.setAntiAlias(true);
		canvas.drawBitmap(workspaceShader, workspace.left, workspace.top, paint);
		paint.setStyle(Paint.Style.STROKE);
		paint.setColor(ContextCompat.getColor(getContext(), R.color.record_outline));
		paint.setStrokeWidth(getResources().getDimension(R.dimen.one_dp));
		canvas.drawRoundRect(workspace, workspaceCornerRadius, workspaceCornerRadius, paint);
	}
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		this.gestureDetector.onTouchEvent(event);
		return super.onTouchEvent(event);
	}


	@Override
	public boolean onDown(MotionEvent e) {
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {

	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		if (PocketAccounter.PRESSED) return false;
		int size = buttons.size();
		float x = e.getX();
		float y = e.getY();
		for (int i=0; i<size; i++) {
			if (buttons.get(i).getContainer().contains(x, y)) {
				buttons.get(i).setPressed(true);
				final int position = i;
				postDelayed(new Runnable() {
					@Override
					public void run() {
						RootCategory category = null;
						List<BoardButton> boardButtons = daoSession.getBoardButtonDao().queryBuilder()
								.where(BoardButtonDao.Properties.Type.eq(PocketAccounterGeneral.INCOME))
								.list();
						if(boardButtons.get(position).getCategoryId() == null)
							category = null;
						else {
							List<RootCategory> categoryList = daoSession.getRootCategoryDao().queryBuilder()
									.where(RootCategoryDao.Properties.Id.eq(boardButtons.get(position).getCategoryId()))
									.list();
							if (!categoryList.isEmpty())
								category = categoryList.get(0);
						}
						if (category != null)
							paFragmentManager.displayFragment(new RecordEditFragment(category, date, null, PocketAccounterGeneral.MAIN));
						else
							openChooseDialog(position);
					}
				}, 250);
				invalidate();
				PocketAccounter.PRESSED = true;
				break;
			}
		}
		return false;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		Vibrator vibr = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
		vibr.vibrate(60);
		float x = e.getX(), y = e.getY();
		int size = buttons.size();
		for (int i=0; i<size; i++) {
			if (buttons.get(i).getContainer().contains(x, y)) {
				buttons.get(i).setPressed(true);
				final int position = i;
				List<BoardButton> boardButtonList = daoSession.getBoardButtonDao()
						.queryBuilder().where(BoardButtonDao.Properties.Type.eq(PocketAccounterGeneral.INCOME))
						.list();
				if (boardButtonList.get(position).getCategoryId() == null) {
					for (int j=0; j<buttons.size(); j++)
						buttons.get(j).setPressed(false);
					invalidate();
					return;
				}
				postDelayed(new Runnable() {
					@Override
					public void run() {
						openChooseDialogLongPress(position);
					}
				}, 250);
				invalidate();
				break;
			}
		}
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		return false;
	}
	private void openChooseDialogLongPress(final int pos) {
//		final Dialog dialog=new Dialog(getContext());
//		View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_with_listview, null);
//		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//		dialog.setContentView(dialogView);
//		String id = PocketAccounter.financeManager.getIncomes().get(pos).getId();
//		boolean hasAnyRecord = false;
//		ArrayList<FinanceRecord> temp = new ArrayList<>();
//		Calendar begin = (Calendar) date.clone();
//		begin.set(Calendar.HOUR_OF_DAY, 0);
//		begin.set(Calendar.MINUTE, 0);
//		begin.set(Calendar.SECOND, 0);
//		begin.set(Calendar.MILLISECOND, 0);
//		Calendar end = (Calendar)date.clone();
//		end.set(Calendar.HOUR_OF_DAY, 23);
//		end.set(Calendar.MINUTE, 59);
//		end.set(Calendar.SECOND, 59);
//		end.set(Calendar.MILLISECOND, 59);
//		for (int i=0; i<PocketAccounter.financeManager.getRecords().size(); i++) {
//			Calendar day = PocketAccounter.financeManager.getRecords().get(i).getDate();
//			if(day.compareTo(begin) >= 0 && day.compareTo(end) <= 0) {
//				temp.add(PocketAccounter.financeManager.getRecords().get(i));
//			}
//		}
//		for (int i=0; i<temp.size(); i++) {
//			if(temp.get(i).getCategory().getId().matches(id)) {
//				hasAnyRecord = true;
//				break;
//			}
//		}
//		String edit = getContext().getString(R.string.to_edit);
//		String change = getResources().getString(R.string.change);
//		final String clear = getResources().getString(R.string.clear);
//		String clearReocrds = getResources().getString(R.string.clear_records);
//		ArrayAdapter<String> adapter;
//		if (hasAnyRecord) {
//			String[] items = new String[4];
//			items[0] = change;
//			items[1] = clear;
//			items[2] = edit;
//			items[3] = clearReocrds;
//			adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, items);
//		}
//		else {
//			String[] items = new String[2];
//			items[0] = change;
//			items[1] = clear;
//			adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, items);
//		}
//		ListView lvDialog = (ListView) dialogView.findViewById(R.id.lvDialog);
//		lvDialog.setAdapter(adapter);
//		lvDialog.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//			@Override
//			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//				switch (position) {
//					case 0:
//						openCategoryChooseDialog(pos);
//						break;
//					case 1:
//						PocketAccounter.financeManager.getIncomes().set(pos, null);
//						initButtons();
//						for (int i=0; i<buttons.size(); i++)
//							buttons.get(i).setPressed(false);
//						invalidate();
//						PocketAccounter.financeManager.saveIncomes();
//						break;
//					case 2:
//						openEditDialog(pos);
//						break;
//					case 3:
//						clear(pos);
//						break;
//				}
//				PocketAccounter.PRESSED = false;
//				dialog.dismiss();
//			}
//		});
//		dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
//			@Override
//			public void onCancel(DialogInterface dialog) {
//				for (int i=0; i<buttons.size(); i++)
//					buttons.get(i).setPressed(false);
//				PocketAccounter.PRESSED = false;
//				invalidate();
//			}
//		});
//		dialog.show();
	}
	private void clear(final int pos) {
//		final Dialog dialog=new Dialog(getContext());
//		View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.warning_dialog, null);
//		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//		dialog.setContentView(dialogView);
//		TextView tv = (TextView) dialogView.findViewById(R.id.tvWarningText);
//		String catName = PocketAccounter.financeManager.getIncomes().get(pos).getName();
//		SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
//		tv.setText("Записи, связанные с категорией "+catName +" будут удалены ("+format.format(date.getTime())+").");
//		final Calendar beg = (Calendar) date.clone();
//		beg.set(Calendar.HOUR_OF_DAY, 0);
//		beg.set(Calendar.MINUTE, 0);
//		beg.set(Calendar.SECOND, 0);
//		beg.set(Calendar.MILLISECOND, 0);
//		final Calendar end = (Calendar) date.clone();
//		end.set(Calendar.HOUR_OF_DAY, 23);
//		end.set(Calendar.MINUTE, 59);
//		end.set(Calendar.SECOND, 59);
//		end.set(Calendar.MILLISECOND, 59);
//		Button btnYes = (Button) dialogView.findViewById(R.id.btnWarningYes);
//		final String id = PocketAccounter.financeManager.getIncomes().get(pos).getId();
//		btnYes.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				for (int i=0; i<PocketAccounter.financeManager.getRecords().size(); i++) {
//					Calendar day = PocketAccounter.financeManager.getRecords().get(i).getDate();
//					if (beg.compareTo(day) <= 0 && end.compareTo(day) >= 0 &&
//							PocketAccounter.financeManager.getRecords().get(i).getCategory().getId().matches(id)) {
//						PocketAccounter.financeManager.getRecords().remove(i);
//						i--;
//					}
//				}
//				PocketAccounter.PRESSED = false;
//				for (int i=0; i<buttons.size(); i++)
//					buttons.get(i).setPressed(false);
//				PocketAccounter.PRESSED = false;
//				invalidate();
////				((PocketAccounter)getContext()).calculateBalance(date);
//				PocketAccounter.financeManager.saveIncomes();
//				dialog.dismiss();
//			}
//		});
//		Button btnNo = (Button) dialogView.findViewById(R.id.btnWarningNo);
//		btnNo.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				for (int i=0; i<buttons.size(); i++)
//					buttons.get(i).setPressed(false);
//				PocketAccounter.PRESSED = false;
//				invalidate();
//				dialog.dismiss();
//			}
//		});
//		dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
//			@Override
//			public void onCancel(DialogInterface dialog) {
//				for (int i=0; i<buttons.size(); i++)
//					buttons.get(i).setPressed(false);
//				PocketAccounter.PRESSED = false;
//				invalidate();
//			}
//		});
//		dialog.show();
	}
	private void openEditDialog(int position) {
//		final Dialog dialog=new Dialog(getContext());
//		View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_with_listview, null);
//		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//		dialog.setContentView(dialogView);
//		ListView lvDialog = (ListView) dialogView.findViewById(R.id.lvDialog);
//		final ArrayList<FinanceRecord> temp = new ArrayList<>();
//		String id = PocketAccounter.financeManager.getIncomes().get(position).getId();
//		for (int i = 0; i < PocketAccounter.financeManager.getRecords().size(); i++) {
//			if (PocketAccounter.financeManager.getRecords().get(i).getCategory().getId().matches(id))
//				temp.add(PocketAccounter.financeManager.getRecords().get(i));
//		}
//		LongPressAdapter adapter = new LongPressAdapter(getContext(), temp);
//		lvDialog.setAdapter(adapter);
//		lvDialog.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//			@Override
//			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
////				((PocketAccounter)getContext()).replaceFragment(new RecordEditFragment(temp.get(position).getCategory(), date, temp.get(position), PocketAccounterGeneral.MAIN));
//				PocketAccounter.PRESSED = false;
//				PocketAccounter.financeManager.saveIncomes();
//				dialog.dismiss();
//			}
//		});
//		dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
//			@Override
//			public void onCancel(DialogInterface dialog) {
//				for (int i=0; i<buttons.size(); i++)
//					buttons.get(i).setPressed(false);
//				invalidate();
//				PocketAccounter.PRESSED = false;
//			}
//		});
//		dialog.show();
	}
	private void openChooseDialog(final int pos) {
//		final Dialog dialog=new Dialog(getContext());
//		View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_with_listview, null);
//		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//		dialog.setContentView(dialogView);
//		String add, create;
//		add = getResources().getString(R.string.add);
//		create = getResources().getString(R.string.create);
//		String[] items = new String[2];
//		items[0] = add;
//		items[1] = create;
//		ListView lvDialog = (ListView) dialogView.findViewById(R.id.lvDialog);
//		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, items);
//		lvDialog.setAdapter(adapter);
//		lvDialog.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//			@Override
//			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//				if (position == 0) {
//					boolean incomeCategoryFound = false;
//					for (int i=0; i<PocketAccounter.financeManager.getCategories().size(); i++) {
//						if (PocketAccounter.financeManager.getCategories().get(i).getType() == PocketAccounterGeneral.INCOME) {
//							incomeCategoryFound = true;
//							break;
//						}
//					}
//					if (incomeCategoryFound)
//						openCategoryChooseDialog(pos);
////					else
////						((PocketAccounter)getContext()).replaceFragment(new RootCategoryEditFragment(null, PocketAccounterGeneral.INCOME_MODE, pos, date));
//				}
////				else
////					((PocketAccounter)getContext()).replaceFragment(new RootCategoryEditFragment(null, PocketAccounterGeneral.INCOME_MODE, pos, date));
//				PocketAccounter.PRESSED = false;
//				PocketAccounter.financeManager.saveIncomes();
//				dialog.dismiss();
//			}
//		});
//		dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
//			@Override
//			public void onCancel(DialogInterface dialog) {
//				for (int i=0; i<buttons.size(); i++)
//					buttons.get(i).setPressed(false);
//				invalidate();
//				PocketAccounter.PRESSED = false;
//			}
//		});
//		dialog.show();
	}
	private void openCategoryChooseDialog(final int pos) {
//		final Dialog dialog=new Dialog(getContext());
//		View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_with_listview, null);
//		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//		dialog.setContentView(dialogView);
//		final ArrayList<RootCategory> categories = new ArrayList<RootCategory>();
//		for (int i=0; i<PocketAccounter.financeManager.getCategories().size(); i++) {
//			if (PocketAccounter.financeManager.getCategories().get(i).getType() == PocketAccounterGeneral.INCOME)
//				categories.add(PocketAccounter.financeManager.getCategories().get(i));
//		}
//		CategoryAdapterForDialog adapter = new CategoryAdapterForDialog(getContext(), categories);
//		ListView lvDialog = (ListView) dialogView.findViewById(R.id.lvDialog);
//		lvDialog.setAdapter(adapter);
//		lvDialog.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//			@Override
//			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//				PocketAccounter.financeManager.getIncomes().set(pos, categories.get(position));
//				initButtons();
//				for (int i=0; i<buttons.size(); i++)
//					buttons.get(i).setPressed(false);
//				invalidate();
//				PocketAccounter.financeManager.saveIncomes();
//				PocketAccounter.PRESSED = false;
//				dialog.dismiss();
//			}
//		});
//		dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
//			@Override
//			public void onCancel(DialogInterface dialog) {
//				for (int i=0; i<buttons.size(); i++)
//					buttons.get(i).setPressed(false);
//				invalidate();
//				PocketAccounter.PRESSED = false;
//			}
//		});
//		dialog.show();
	}
}