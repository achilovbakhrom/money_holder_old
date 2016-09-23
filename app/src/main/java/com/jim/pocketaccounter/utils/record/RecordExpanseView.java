package com.jim.pocketaccounter.utils.record;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.jim.pocketaccounter.PocketAccounter;
import com.jim.pocketaccounter.PocketAccounterApplication;
import com.jim.pocketaccounter.R;
import com.jim.pocketaccounter.database.BoardButton;
import com.jim.pocketaccounter.database.BoardButtonDao;
import com.jim.pocketaccounter.database.CreditDetials;
import com.jim.pocketaccounter.database.DaoSession;
import com.jim.pocketaccounter.database.DebtBorrow;
import com.jim.pocketaccounter.database.DebtBorrowDao;
import com.jim.pocketaccounter.database.FinanceRecordDao;
import com.jim.pocketaccounter.database.RootCategoryDao;
import com.jim.pocketaccounter.database.FinanceRecord;
import com.jim.pocketaccounter.database.RootCategory;
import com.jim.pocketaccounter.finance.CategoryAdapterForDialog;
import com.jim.pocketaccounter.fragments.AddCreditFragment;
import com.jim.pocketaccounter.fragments.RecordEditFragment;
import com.jim.pocketaccounter.fragments.RootCategoryEditFragment;
import com.jim.pocketaccounter.managers.CommonOperations;
import com.jim.pocketaccounter.managers.LogicManager;
import com.jim.pocketaccounter.managers.PAFragmentManager;
import com.jim.pocketaccounter.utils.OperationsListDialog;
import com.jim.pocketaccounter.utils.PocketAccounterGeneral;
import com.jim.pocketaccounter.utils.WarningDialog;
import com.jim.pocketaccounter.utils.cache.DataCache;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Vibrator;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GestureDetectorCompat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.greenrobot.greendao.query.QueryBuilder;

import javax.inject.Inject;

@SuppressLint("DrawAllocation")
public class RecordExpanseView extends View implements 	GestureDetector.OnGestureListener {
	private final float workspaceCornerRadius, workspaceMargin;
	private Bitmap workspaceShader;
	private RectF workspace;
	private ArrayList<RecordButtonExpanse> buttons;
	private GestureDetectorCompat gestureDetector;
	private Calendar date;
	private Canvas canvas;
	private float twoDp;
	private int tableCount;
	private int currentPage;
	@Inject	DaoSession daoSession;
	@Inject	PAFragmentManager paFragmentManager;
	@Inject	LogicManager logicManager;
	@Inject	WarningDialog warningDialog;
	@Inject	DataCache dataCache;
	@Inject	CommonOperations commonOperations;
	@Inject SharedPreferences sharedPreferences;
	@Inject OperationsListDialog operationsListDialog;
	public RecordExpanseView(Context context, Calendar date) {
		super(context);
		((PocketAccounter) context).component((PocketAccounterApplication) context.getApplicationContext()).inject(this);
		this.tableCount = 4;
		this.currentPage = 0;
		this.date = (Calendar) date.clone();
		gestureDetector = new GestureDetectorCompat(getContext(),this);
		workspaceCornerRadius = getResources().getDimension(R.dimen.five_dp);
		workspaceMargin = getResources().getDimension(R.dimen.twenty_dp);
		initButtons();
		setClickable(true);
		twoDp = getResources().getDimension(R.dimen.two_dp);
	}
	private void initButtons() {
		buttons = new ArrayList<>();
		RecordButtonExpanse button;
		int type = 0;
		BoardButtonDao boardButtonDao = daoSession.getBoardButtonDao();
		List<BoardButton> boardButtonList = boardButtonDao.loadAll();
		for (int i = 0; i < PocketAccounterGeneral.EXPENSE_BUTTONS_COUNT; i++) {
			switch(i) {
				case 0:
					type = PocketAccounterGeneral.UP_TOP_LEFT;
					break;
				case 1:
				case 2:
					type = PocketAccounterGeneral.UP_TOP_SIMPLE;
					break;
				case 3:
					type = PocketAccounterGeneral.UP_TOP_RIGHT;
					break;
				case 4:
				case 8:
					type = PocketAccounterGeneral.UP_LEFT_SIMPLE;
					break;
				case 5:
				case 6:
				case 9:
				case 10:
					type = PocketAccounterGeneral.UP_SIMPLE;
					break;
				case 7:
				case 11:
					type = PocketAccounterGeneral.UP_RIGHT_SIMPLE;
					break;
				case 12:
					type = PocketAccounterGeneral.UP_LEFT_BOTTOM;
					break;
				case 13:
				case 14:
					type = PocketAccounterGeneral.UP_BOTTOM_SIMPLE;
					break;
				case 15:
					type = PocketAccounterGeneral.UP_BOTTOM_RIGHT;
					break;
			}
			button = new RecordButtonExpanse(getContext(), type, date);
			for (int j=0; j<boardButtonList.size(); j++) {
				if (boardButtonList.get(j).getTable() == PocketAccounterGeneral.EXPENSE &&
					(boardButtonList.get(j).getPos()-currentPage*PocketAccounterGeneral.EXPENSE_BUTTONS_COUNT) == i) {
					button.setCategory(boardButtonList.get(j));
				}
			}
			buttons.add(button);
		}
	}

	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(final Canvas canvas) {
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
		height = workspace.height()/4;
		final int buttonsSize = PocketAccounterGeneral.EXPENSE_BUTTONS_COUNT;
		float left, top, right, bottom;
		for (int i=0; i<buttonsSize; i++) {
			Log.d("sss", "poses: "+i);
			left = workspace.left+(i%4)*width;
			top = workspace.top+((int)Math.floor(i/4)*height);
			right = workspace.left+(i%4+1)*width;
			bottom = workspace.top+((int)(Math.floor(i/4)+1)*height);
			buttons.get(i).setBounds(left, top, right, bottom, workspaceCornerRadius);
			buttons.get(i).drawButton(canvas);
		}
		Paint borderPaint = new Paint();
		borderPaint.setColor(ContextCompat.getColor(getContext(), R.color.belt_balanse));
		borderPaint.setStrokeWidth(getResources().getDimension(R.dimen.one_dp));
		for (int i=0; i<3; i++) {
			canvas.drawLine(workspace.left,
					workspace.top+(i+1)*width,
					workspace.right,
					workspace.top+(i+1)*width, borderPaint);
			canvas.drawLine(workspace.left+(i+1)*width,
					workspace.top,
					workspace.left+(i+1)*width,
					workspace.bottom, borderPaint);
		}
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
		for (RecordButtonExpanse button : buttons) {
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
		if (dataCache.getElements().get(PocketAccounterGeneral.UP_WORKSPACE_SHADER) == null) {
			workspaceShader = commonOperations.getRoundedCornerBitmap(
					commonOperations.decodeSampledBitmapFromResource(
																	getResources(), R.drawable.workspace_shader,
																	(int) workspace.width(),
																	(int) workspace.height()),
																	(int) workspaceCornerRadius);
			workspaceShader = Bitmap.createScaledBitmap(workspaceShader, (int)workspace.width(), (int)workspace.height(), false);
			dataCache.getElements().put(PocketAccounterGeneral.UP_WORKSPACE_SHADER, workspaceShader);
		}
		else
			workspaceShader = dataCache.getElements().get(PocketAccounterGeneral.UP_WORKSPACE_SHADER);
		Paint paint = new Paint();
		paint.setAlpha(0x55);
		paint.setAntiAlias(true);
		canvas.drawBitmap(workspaceShader, workspace.left, workspace.top, paint);
		paint.setStyle(Paint.Style.STROKE);
		paint.setColor(ContextCompat.getColor(getContext(), R.color.record_outline));
		paint.setStrokeWidth(getResources().getDimension(R.dimen.one_dp));
		canvas.drawRoundRect(workspace, workspaceCornerRadius, workspaceCornerRadius, paint);
	}

	@SuppressLint("ClickableViewAccessibility")
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
	public void onShowPress(MotionEvent e) {}
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
						List<BoardButton> boardButtons = daoSession.getBoardButtonDao().queryBuilder()
								.where(BoardButtonDao.Properties.Table.eq(PocketAccounterGeneral.EXPENSE))
								.list();


						if (boardButtons.get(position).getCategoryId() == null)
							openTypeChooseDialog(position);
						else if (boardButtons.get(position).getType() == PocketAccounterGeneral.CATEGORY) {
							RootCategory category = null;
							if(boardButtons.get(position).getCategoryId() == null)
								category = null;
							else {
								List<RootCategory> categoryList = daoSession.getRootCategoryDao().queryBuilder()
										.where(RootCategoryDao.Properties.Id.eq(boardButtons.get(position).getCategoryId()))
										.list();
								if (!categoryList.isEmpty())
									category = categoryList.get(0);
							}
							paFragmentManager.displayFragment(new RecordEditFragment(category, date, null, PocketAccounterGeneral.MAIN));
						}
						else if (boardButtons.get(position).getType() == PocketAccounterGeneral.CREDIT) {}
						else if (boardButtons.get(position).getType() == PocketAccounterGeneral.DEBT_BORROW) {}
						else if (boardButtons.get(position).getType() == PocketAccounterGeneral.PAGE) {}
						else if (boardButtons.get(position).getType() == PocketAccounterGeneral.CREDIT) {
							String[] functionIds = getResources().getStringArray(R.array.operation_ids);
							if (boardButtons.get(position).getCategoryId().matches(functionIds[0])) {
								if (currentPage == tableCount-1)
									currentPage = 0;
								else
									currentPage++;
							}
							else if (boardButtons.get(position).getCategoryId().matches(functionIds[1])) {
								if (currentPage == 0)
									currentPage = tableCount-1;
								else
									currentPage--;
							}
							initButtons();
							invalidate();
						}
					}
				}, 150);
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
						.queryBuilder().where(BoardButtonDao.Properties.Table.eq(PocketAccounterGeneral.EXPENSE))
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
		Calendar beg = (Calendar) date.clone();
		beg.set(Calendar.HOUR_OF_DAY, 0);
		beg.set(Calendar.MINUTE, 0);
		beg.set(Calendar.SECOND, 0);
		beg.set(Calendar.MILLISECOND, 0);
		Calendar end = (Calendar) date.clone();
		end.set(Calendar.HOUR_OF_DAY, 23);
		end.set(Calendar.MINUTE, 59);
		end.set(Calendar.SECOND, 59);
		end.set(Calendar.MILLISECOND, 59);
		String edit = getContext().getString(R.string.to_edit);
		String change = getResources().getString(R.string.change);
		String clear = getResources().getString(R.string.clear);
		String clearRecords = getContext().getString(R.string.clear_records);
		BoardButton cur = daoSession.getBoardButtonDao().queryBuilder()
				.where(BoardButtonDao.Properties.Pos.eq(pos), BoardButtonDao.Properties.Table.eq(PocketAccounterGeneral.EXPENSE))
				.list().isEmpty() ?
				null:daoSession.getBoardButtonDao().queryBuilder()
				.where(BoardButtonDao.Properties.Pos.eq(pos))
				.list().get(0);
		String[] items = null;
		List<FinanceRecord> temp = daoSession.getFinanceRecordDao().queryBuilder()
				.where(FinanceRecordDao.Properties.CategoryId.eq(cur.getCategoryId()),
						FinanceRecordDao.Properties.Date.ge(beg),
						FinanceRecordDao.Properties.Date.le(end)).list();
		if (!temp.isEmpty()) {
			items = new String[4];
			items[0] = change;
			items[1] = clear;
			items[2] = edit;
			items[3] = clearRecords;
		} else {
			items = new String[2];
			items[0] = change;
			items[1] = clear;
		}
		operationsListDialog.setAdapter(items);
		operationsListDialog.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				switch (position) {
					case 0:
						openTypeChooseDialog(pos);
						break;
					case 1:
						logicManager.changeBoardButton(PocketAccounterGeneral.EXPENSE, pos, null);
						changeIconInCache(pos, "no_category");
						initButtons();
						for (int i=0; i<buttons.size(); i++)
							buttons.get(i).setPressed(false);
						invalidate();
						operationsListDialog.dismiss();
						break;
					case 2:
						openEditDialog(pos);
						break;
					case 3:
						clear(pos);
						break;
				}
				PocketAccounter.PRESSED = false;
			}
		});
		operationsListDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				for (int i=0; i<buttons.size(); i++)
					buttons.get(i).setPressed(false);
				PocketAccounter.PRESSED = false;
				invalidate();
			}
		});
		operationsListDialog.show();;
	}

	private void openTypeChooseDialog(final int pos) {
		String[] items = getResources().getStringArray(R.array.board_operation_names_long_press);
		operationsListDialog.setAdapter(items);
		operationsListDialog.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
				if (position <= 2) {
					String[] operationCategory = getResources().getStringArray(R.array.operation_category);
					operationsListDialog.setAdapter(operationCategory);
					operationsListDialog.setOnItemClickListener(new AdapterView.OnItemClickListener() {
						@Override
						public void onItemClick(AdapterView<?> parent, View view, int p, long id) {
							switch (p) {
								case 0:
									switch (position) {
										case 0:
											openCategoryChooseDialog(pos);
											break;
										case 1:
											openCreditsChooseDialog(pos);
											break;
										case 2:
											openDebtBorrowChooseDialog(pos);
											break;
									}
									break;
								case 1:
									switch (position) {
										case 0:
											paFragmentManager.displayFragment(new RootCategoryEditFragment(null, PocketAccounterGeneral.EXPANSE_MODE, pos, date));
											break;
										case 1:
											paFragmentManager.displayFragment((new AddCreditFragment()).setDateFormatModes(PocketAccounterGeneral.EXPANSE_MODE,pos));
											break;
										case 2:

											break;
									}
									break;
							}
							operationsListDialog.dismiss();
						}
					});
					operationsListDialog.show();
				}
				else {
					switch (position) {
						case 3:
							openOperationsList(pos);
							break;
						case 4:
							openPageChooseDialog(pos);
							break;
					}
					operationsListDialog.dismiss();
				}
			}
		});
		operationsListDialog.show();
	}

	private void openPageChooseDialog(final int pos) {
		final Dialog dialog=new Dialog(getContext());
		View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_with_listview, null);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(dialogView);
		final ArrayList<IconWithName> categories = new ArrayList<>();
		String[] pageNames = getContext().getResources().getStringArray(R.array.page_names);
		String[] pageIds = getContext().getResources().getStringArray(R.array.page_ids);
		String[] pageIcons = getContext().getResources().getStringArray(R.array.page_icons);
		for (int i=0; i<pageNames.length; i++) {
			IconWithName iconWithName = new IconWithName(pageIcons[i], pageNames[i], pageIds[i]);
			categories.add(iconWithName);
		}
		CategoryAdapterForDialog adapter = new CategoryAdapterForDialog(getContext(), categories);
		ListView lvDialog = (ListView) dialogView.findViewById(R.id.lvDialog);
		lvDialog.setAdapter(adapter);
		lvDialog.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				logicManager.changeBoardButton(PocketAccounterGeneral.EXPENSE, pos, categories.get(position).getId());
				changeIconInCache(pos, categories.get(position).getIcon());
				initButtons();
				for (int i=0; i<buttons.size(); i++)
					buttons.get(i).setPressed(false);
				invalidate();
				PocketAccounter.PRESSED = false;
				dialog.dismiss();
			}
		});
		dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				for (int i=0; i<buttons.size(); i++)
					buttons.get(i).setPressed(false);
				invalidate();
				PocketAccounter.PRESSED = false;
			}
		});
		dialog.show();
	}

	private void openOperationsList(final int pos) {
		final Dialog dialog=new Dialog(getContext());
		View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_with_listview, null);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(dialogView);
		final ArrayList<IconWithName> categories = new ArrayList<>();
		String[] operationNames = getResources().getStringArray(R.array.operation_names);
		String[] operationIds = getResources().getStringArray(R.array.operation_ids);
		String[] operationIcons = getResources().getStringArray(R.array.operation_icons);
		for (int i=0; i<operationNames.length; i++) {
			IconWithName iconWithName = new IconWithName(operationIcons[i], operationNames[i], operationIds[i]);
			categories.add(iconWithName);
		}
		CategoryAdapterForDialog adapter = new CategoryAdapterForDialog(getContext(), categories);
		ListView lvDialog = (ListView) dialogView.findViewById(R.id.lvDialog);
		lvDialog.setAdapter(adapter);
		lvDialog.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				logicManager.changeBoardButton(PocketAccounterGeneral.EXPENSE, pos, categories.get(position).getId());
				changeIconInCache(pos, categories.get(position).getIcon());
				initButtons();
				for (int i=0; i<buttons.size(); i++)
					buttons.get(i).setPressed(false);
				invalidate();
				PocketAccounter.PRESSED = false;
				dialog.dismiss();
			}
		});
		dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				for (int i=0; i<buttons.size(); i++)
					buttons.get(i).setPressed(false);
				invalidate();
				PocketAccounter.PRESSED = false;
			}
		});
		dialog.show();
	}

	private void clear(final int pos) {
		final Calendar beg = (Calendar) date.clone();
		beg.set(Calendar.HOUR_OF_DAY, 0);
		beg.set(Calendar.MINUTE, 0);
		beg.set(Calendar.SECOND, 0);
		beg.set(Calendar.MILLISECOND, 0);
		final Calendar end = (Calendar) date.clone();
		end.set(Calendar.HOUR_OF_DAY, 23);
		end.set(Calendar.MINUTE, 59);
		end.set(Calendar.SECOND, 59);
		end.set(Calendar.MILLISECOND, 59);
		final String id = daoSession.getBoardButtonDao().queryBuilder()
							.where(BoardButtonDao.Properties.Pos.eq(pos))
							.list().get(0).getCategoryId();
		warningDialog.setText(getContext().getString(R.string.clear_warning));
		warningDialog.setOnYesButtonListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				QueryBuilder<FinanceRecord> financeRecordQueryBuilder = daoSession.getFinanceRecordDao().queryBuilder();
				List<FinanceRecord> deletingRecords = financeRecordQueryBuilder.where(FinanceRecordDao.Properties.Date.ge(beg),
						FinanceRecordDao.Properties.Date.le(end), FinanceRecordDao.Properties.CategoryId.eq(id))
						.list();
				daoSession.getFinanceRecordDao().deleteInTx(deletingRecords);
				PocketAccounter.PRESSED = false;
				for (int i=0; i<buttons.size(); i++)
					buttons.get(i).setPressed(false);
				PocketAccounter.PRESSED = false;
				invalidate();
				warningDialog.dismiss();
			}
		});
		warningDialog.setOnNoButtonClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				for (int i=0; i<buttons.size(); i++)
					buttons.get(i).setPressed(false);
				PocketAccounter.PRESSED = false;
				invalidate();
				warningDialog.dismiss();
			}
		});
		warningDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				for (int i=0; i<buttons.size(); i++)
					buttons.get(i).setPressed(false);
				PocketAccounter.PRESSED = false;
				invalidate();
			}
		});
		warningDialog.show();
	}

	private void openEditDialog(int position) {
//		final Dialog dialog=new Dialog(getContext());
//		View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_with_listview, null);
//		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//		dialog.setContentView(dialogView);
//		ListView lvDialog = (ListView) dialogView.findViewById(R.id.lvDialog);
//		final ArrayList<FinanceRecord> temp = new ArrayList<>();
//		String id = PocketAccounter.financeManager.getExpanses().get(position).getId();
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
//				PocketAccounter.financeManager.saveExpenses();
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
		final Dialog dialog=new Dialog(getContext());
		View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_with_listview, null);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(dialogView);
		String[] operationNames = getResources().getStringArray(R.array.operation_names);
		ListView lvDialog = (ListView) dialogView.findViewById(R.id.lvDialog);
		ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, operationNames);
		lvDialog.setAdapter(adapter);
		lvDialog.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//				if (position == 0) {
//					boolean expanseCategoryFound = false;
//					for (int i = 0; i < PocketAccounter.financeManager.getCategories().size(); i++) {
//						if (PocketAccounter.financeManager.getCategories().get(i).getType() == PocketAccounterGeneral.EXPENSE) {
//							expanseCategoryFound = true;
//							break;
//						}
//					}
//					if (expanseCategoryFound)
//						openCategoryChooseDialog(pos);
//					else
//						((PocketAccounter)getContext()).replaceFragment(new RootCategoryEditFragment(null, PocketAccounterGeneral.EXPANSE_MODE, pos, date));
//				}
//				else
//					((PocketAccounter)getContext()).replaceFragment(new RootCategoryEditFragment(null, PocketAccounterGeneral.EXPANSE_MODE, pos, date));
//				PocketAccounter.PRESSED = false;
//				PocketAccounter.financeManager.saveExpenses();
				dialog.dismiss();
			}
		});
		dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				for (int i=0; i<buttons.size(); i++)
					buttons.get(i).setPressed(false);
				invalidate();
				PocketAccounter.PRESSED = false;
			}
		});
		dialog.show();
	}
	private void openCategoryChooseDialog(final int pos) {
		final Dialog dialog=new Dialog(getContext());
		View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_with_listview, null);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(dialogView);
		final ArrayList<IconWithName> categories = new ArrayList<>();
		List<RootCategory> categoryList = daoSession.getRootCategoryDao().queryBuilder()
					.where(RootCategoryDao.Properties.Type.eq(PocketAccounterGeneral.EXPENSE))
					.build()
					.list();
		for (RootCategory category : categoryList) {
			IconWithName iconWithName = new IconWithName(category.getIcon(), category.getName(), category.getId());
			categories.add(iconWithName);
		}
		CategoryAdapterForDialog adapter = new CategoryAdapterForDialog(getContext(), categories);
		ListView lvDialog = (ListView) dialogView.findViewById(R.id.lvDialog);
		lvDialog.setAdapter(adapter);
		lvDialog.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				logicManager.changeBoardButton(PocketAccounterGeneral.EXPENSE, pos, categories.get(position).getId());
				changeIconInCache(pos, categories.get(position).getIcon());
				initButtons();
				for (int i=0; i<buttons.size(); i++)
					buttons.get(i).setPressed(false);
				invalidate();
				PocketAccounter.PRESSED = false;
				dialog.dismiss();
			}
		});
		dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				for (int i=0; i<buttons.size(); i++)
					buttons.get(i).setPressed(false);
				invalidate();
				PocketAccounter.PRESSED = false;
			}
		});
		dialog.show();
	}

	private void openDebtBorrowChooseDialog(final int pos) {
		final ArrayList<IconWithName> categories = new ArrayList<>();
		List<DebtBorrow> debtBorrowList = daoSession.getDebtBorrowDao()
				.queryBuilder().where(DebtBorrowDao.Properties.To_archive.eq(false)).list();
		if (debtBorrowList.isEmpty()) {
			final Dialog dialog=new Dialog(getContext());
			View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_with_listview, null);
			dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			dialog.setContentView(dialogView);
			for (DebtBorrow debtBorrow : debtBorrowList) {
				IconWithName iconWithName = new IconWithName(debtBorrow.getPerson().getPhoto(),
						debtBorrow.getPerson().getName(), debtBorrow.getId());
				categories.add(iconWithName);
			}
			CategoryAdapterForDialog adapter = new CategoryAdapterForDialog(getContext(), categories);
			ListView lvDialog = (ListView) dialogView.findViewById(R.id.lvDialog);
			lvDialog.setAdapter(adapter);
			lvDialog.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					logicManager.changeBoardButton(PocketAccounterGeneral.EXPENSE, pos, categories.get(position).getId());
					changeIconInCache(pos, categories.get(position).getIcon());
					initButtons();
					for (int i=0; i<buttons.size(); i++)
						buttons.get(i).setPressed(false);
					invalidate();
					PocketAccounter.PRESSED = false;
					dialog.dismiss();
				}
			});
			dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
				@Override
				public void onCancel(DialogInterface dialog) {
					for (int i=0; i<buttons.size(); i++)
						buttons.get(i).setPressed(false);
					invalidate();
					PocketAccounter.PRESSED = false;
				}
			});
			dialog.show();
		}
		else {
			Toast.makeText(getContext(), R.string.debt_borrow_list_is_empty, Toast.LENGTH_SHORT).show();
		}

	}

	private void openCreditsChooseDialog(final int pos) {
		final Dialog dialog=new Dialog(getContext());
		View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_with_listview, null);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(dialogView);
		final ArrayList<IconWithName> categories = new ArrayList<>();
		List<CreditDetials> creditDetialsList = daoSession.getCreditDetialsDao().loadAll();
		for (CreditDetials creditDetials : creditDetialsList) {
			IconWithName iconWithName = new IconWithName(creditDetials.getIcon_ID(),
					creditDetials.getCredit_name(), Long.toString(creditDetials.getMyCredit_id()));
			categories.add(iconWithName);
		}
		CategoryAdapterForDialog adapter = new CategoryAdapterForDialog(getContext(), categories);
		ListView lvDialog = (ListView) dialogView.findViewById(R.id.lvDialog);
		lvDialog.setAdapter(adapter);
		lvDialog.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				logicManager.changeBoardButton(PocketAccounterGeneral.EXPENSE, pos, categories.get(position).getId());
				changeIconInCache(pos, categories.get(position).getIcon());
				initButtons();
				for (int i=0; i<buttons.size(); i++)
					buttons.get(i).setPressed(false);
				invalidate();
				PocketAccounter.PRESSED = false;
				dialog.dismiss();
			}
		});
		dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				for (int i=0; i<buttons.size(); i++)
					buttons.get(i).setPressed(false);
				invalidate();
				PocketAccounter.PRESSED = false;
			}
		});
		dialog.show();
	}
	private void changeIconInCache(int pos, String icon) {
		int resId = getResources().getIdentifier(icon, "drawable", getContext().getPackageName());
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inPreferredConfig = Bitmap.Config.RGB_565;
		Bitmap scaled = BitmapFactory.decodeResource(getResources(), resId, options);
		scaled = Bitmap.createScaledBitmap(scaled, (int)getResources().getDimension(R.dimen.thirty_dp), (int) getResources().getDimension(R.dimen.thirty_dp), true);
		List<Bitmap> list = new ArrayList<>();
		list.add(scaled);
		dataCache.getBoardBitmapsCache().put(pos, list);
	}
}