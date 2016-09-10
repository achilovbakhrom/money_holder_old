package com.jim.pocketaccounter.debt;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.jim.pocketaccounter.PocketAccounter;
import com.jim.pocketaccounter.PocketAccounterApplication;
import com.jim.pocketaccounter.R;
import com.jim.pocketaccounter.database.Account;
import com.jim.pocketaccounter.database.AccountDao;
import com.jim.pocketaccounter.database.DaoSession;
import com.jim.pocketaccounter.database.DebtBorrow;
import com.jim.pocketaccounter.database.DebtBorrowDao;
import com.jim.pocketaccounter.database.Recking;
import com.jim.pocketaccounter.managers.CommonOperations;
import com.jim.pocketaccounter.managers.LogicManager;
import com.jim.pocketaccounter.managers.PAFragmentManager;
import com.jim.pocketaccounter.utils.DatePicker;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import javax.inject.Inject;
import javax.inject.Named;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by user on 6/4/2016.
 */

public class BorrowFragment extends Fragment {
    @Inject
    DatePicker datePicker;
    @Inject
    @Named(value = "display_formmatter")
    SimpleDateFormat dateFormat;
    @Inject
    PAFragmentManager paFragmentManager;
    @Inject
    LogicManager logicManager;
    @Inject
    CommonOperations commonOperations;
    @Inject
    DaoSession daoSession;
    DebtBorrowDao debtBorrowDao;
    AccountDao accountDao;

    private RecyclerView recyclerView;
    private LinearLayoutManager mLayoutManager;
    private MyAdapter myAdapter;

    private DebtBorrowFragment debtBorrowFragment;
    private int TYPE = 0;

    public static BorrowFragment getInstance(int type) {
        BorrowFragment fragment = new BorrowFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("type", type);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TYPE = getArguments().getInt("type", 0);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.borrow_fragment_layout, container, false);
        ((PocketAccounter) getContext()).component((PocketAccounterApplication) getContext().getApplicationContext()).inject(this);
        debtBorrowDao = daoSession.getDebtBorrowDao();
        accountDao = daoSession.getAccountDao();
        recyclerView = (RecyclerView) view.findViewById(R.id.lvBorrowFragment);
        myAdapter = new MyAdapter();
        mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(myAdapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                try {
                    debtBorrowFragment.onScrolledList(dy > 0);
                } catch (NullPointerException e) {
                }
            }
        });
        return view;
    }


    public void setDebtBorrowFragment(DebtBorrowFragment debtBorrowFragment) {
        this.debtBorrowFragment = debtBorrowFragment;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private class MyAdapter extends RecyclerView.Adapter<ViewHolder> {
        private ArrayList<DebtBorrow> persons;
        private ArrayList<DebtBorrow> allPersons;

        public MyAdapter() {
            allPersons = (ArrayList<DebtBorrow>) debtBorrowDao.queryBuilder().list();
            persons = new ArrayList<>();
            for (DebtBorrow person : debtBorrowDao.queryBuilder().list()) {
                if (!person.getTo_archive() && person.getType() == TYPE) {
                    persons.add(person);
                } else {
                    if (person.getTo_archive() && TYPE == 2) {
                        persons.add(person);
                    }
                }
            }
        }

        public int getItemCount() {
            return persons.size();
        }

        public void onBindViewHolder(final ViewHolder view, final int position) {
            final int t = 0;
            final DebtBorrow person = persons.get(Math.abs(t - position));
            if (person.getType() == DebtBorrow.DEBT) {
                view.rl.setBackgroundResource(R.color.grey_light_red);
                view.fl.setBackgroundResource(R.color.grey_light_red);
            }
            view.BorrowPersonName.setText(person.getPerson().getName());
            view.BorrowPersonNumber.setText(person.getPerson().getPhoneNumber());
            view.BorrowPersonDateGet.setText(dateFormat.format(person.getTakenDate().getTime()));
            if (person.getReturnDate() == null) {
                view.BorrowPersonDateRepeat.setText(R.string.no_date_selected);
            } else {
                view.BorrowPersonDateRepeat.setText(dateFormat.format(person.getReturnDate().getTime()));
            }
            double qq = 0;
            if (person.getReckings() != null) {
                for (Recking rk : person.getReckings()) {
                    qq += rk.getAmount();
                }
            }
            String ss = (person.getAmount() - qq) == (int) (person.getAmount() - qq) ? "" + (int) (person.getAmount() - qq) : "" + (person.getAmount() - qq);
            if (person.getTo_archive() || qq >= person.getAmount()) {
                view.BorrowPersonSumm.setText(getResources().getString(R.string.repaid));
            } else
                view.BorrowPersonSumm.setText(ss + person.getCurrency().getAbbr());
            if (person.getPerson().getPhoto().matches("") || person.getPerson().getPhoto().matches("0")) {
                view.BorrowPersonPhotoPath.setImageResource(R.drawable.no_photo);
            } else {
                try {
                    view.BorrowPersonPhotoPath.setImageBitmap(queryContactImage(Integer.parseInt(person.getPerson().getPhoto())));
                } catch (Exception e) {
                    Bitmap bit = BitmapFactory.decodeFile(person.getPerson().getPhoto());
                    view.BorrowPersonPhotoPath.setImageBitmap(bit);
                }
            }

            view.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Fragment fragment = InfoDebtBorrowFragment.getInstance(persons.get(Math.abs(t - position)).getId(), TYPE);
                    paFragmentManager.getFragmentManager().popBackStack();
                    paFragmentManager.displayFragment(fragment);

                }
            });
            if (TYPE == 2) {
                view.pay.setVisibility(View.GONE);
                view.call.setVisibility(View.GONE);
            } else {
                double total = 0;
                for (Recking rec : person.getReckings()) {
                    total += rec.getAmount();
                }
                if (total >= person.getAmount()) {
                    view.pay.setText(getString(R.string.archive));
                } else view.pay.setText(getString(R.string.payy));
            }

            if (person.getPerson().getPhoneNumber().matches("")) {
                view.call.setVisibility(View.INVISIBLE);
            }

            view.call.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + person.getPerson().getPhoneNumber()));
                    if (intent.resolveActivity(getContext().getPackageManager()) != null) {
                        startActivity(intent);
                    }
                }
            });

            view.pay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!view.pay.getText().toString().matches(getString(R.string.archive))) {
                        final Dialog dialog = new Dialog(getActivity());
                        View dialogView = getActivity().getLayoutInflater().inflate(R.layout.add_pay_debt_borrow_info_mod, null);
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setContentView(dialogView);
                        final EditText enterDate = (EditText) dialogView.findViewById(R.id.etInfoDebtBorrowDate);
                        final EditText enterPay = (EditText) dialogView.findViewById(R.id.etInfoDebtBorrowPaySumm);
                        final EditText comment = (EditText) dialogView.findViewById(R.id.etInfoDebtBorrowPayComment);
                        final Spinner accountSp = (Spinner) dialogView.findViewById(R.id.spInfoDebtBorrowAccount);
                        RelativeLayout relativeLayout = (RelativeLayout) dialogView.findViewById(R.id.is_calc);
                        if (!person.getCalculate()) {
                            relativeLayout.setVisibility(View.GONE);
                        }
                        String[] accaounts = new String[accountDao.queryBuilder().list().size()];
                        for (int i = 0; i < accaounts.length; i++) {
                            accaounts[i] = accountDao.queryBuilder().list().get(i).getName();
                        }

                        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                                getContext(), R.layout.spiner_gravity_right, accaounts);

                        accountSp.setAdapter(arrayAdapter);

                        ImageView cancel = (ImageView) dialogView.findViewById(R.id.ivInfoDebtBorrowCancel);
                        ImageView save = (ImageView) dialogView.findViewById(R.id.ivInfoDebtBorrowSave);
                        final Calendar date = Calendar.getInstance();
                        enterDate.setText(dateFormat.format(date.getTime()));
                        cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                        final DatePickerDialog.OnDateSetListener getDatesetListener = new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(android.widget.DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                date.set(year, monthOfYear, dayOfMonth);
                                if (date.compareTo(person.getTakenDate()) < 0) {
                                    date.setTime(person.getTakenDate().getTime());
                                    enterDate.setError(getString(R.string.incorrect_date));
                                } else {
                                    enterDate.setError(null);
                                }
                                enterDate.setText(dateFormat.format(date.getTime()));
                            }
                        };
                        enterDate.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Calendar calendar = Calendar.getInstance();
                                Dialog mDialog = new DatePickerDialog(getContext(),
                                        getDatesetListener, calendar.get(Calendar.YEAR),
                                        calendar.get(Calendar.MONTH), calendar
                                        .get(Calendar.DAY_OF_MONTH));
                                mDialog.show();
                            }
                        });
                        save.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String ac = "";
                                if (person.getCalculate()) {
                                    for (Account account : accountDao.queryBuilder().list()) {
                                        if (account.getName().matches(accountSp.getSelectedItem().toString())) {
                                            ac = account.getId();
                                            break;
                                        }
                                    }
                                }
                                boolean tek = false;
                                if (!enterPay.getText().toString().isEmpty()) {
                                    int len = person.getCurrency().getAbbr().length();
                                    if (Double.parseDouble(view.BorrowPersonSumm.getText().toString().substring(0, view.BorrowPersonSumm.getText().toString().length() - len))
                                            - Double.parseDouble(enterPay.getText().toString()) < 0) {
                                        if (person.getCalculate() && isMumkin(person, ac, Double.parseDouble(enterPay.getText().toString())))
                                            tek = true;
                                        if (!person.getCalculate()) tek = true;

                                        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                        final String finalAc = ac;
                                        builder.setMessage(getResources().getString(R.string.incorrect_pay))
                                                .setPositiveButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface d, int id) {
                                                        d.dismiss();
                                                        dialog.dismiss();
                                                    }
                                                }).setNegativeButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface d, int id) {
                                                d.cancel();
                                                if (person.getCalculate()) {
                                                    Recking recking = new Recking(dateFormat.format(date.getTime()),
                                                            Double.parseDouble(enterPay.getText().toString()),
                                                            persons.get(position).getId(), finalAc,
                                                            comment.getText().toString());

                                                    persons.get(position).getReckings().add(0, recking);
                                                    double total = 0;
                                                    for (Recking recking1 : persons.get(position).getReckings()) {
                                                        total += recking1.getAmount();
                                                    }
                                                    if (persons.get(position).getAmount() <= total) {
                                                        view.pay.setText(getString(R.string.archive));
                                                    }
                                                    view.BorrowPersonSumm.setText(getResources().getString(R.string.repaid));
                                                    dialog.dismiss();
                                                } else {
                                                    Recking recking = new Recking(dateFormat.format(date.getTime()),
                                                            Double.parseDouble(enterPay.getText().toString()),
                                                            persons.get(position).getId(), comment.getText().toString());

                                                    persons.get(position).getReckings().add(0, recking);
                                                    double total = 0;
                                                    for (Recking recking1 : persons.get(position).getReckings()) {
                                                        total += recking1.getAmount();
                                                    }
                                                    if (persons.get(position).getAmount() <= total) {
                                                        view.pay.setText(getString(R.string.archive));
                                                    }
                                                    view.BorrowPersonSumm.setText(getResources().getString(R.string.repaid));
                                                    dialog.dismiss();
                                                }
                                            }
                                        });
                                        if (tek) {
                                            builder.create().show();
                                        }
                                    } else {
                                        Recking recking = null;
                                        if (person.getCalculate() && isMumkin(person, ac, Double.parseDouble(enterPay.getText().toString()))) {
                                            recking = new Recking(dateFormat.format(date.getTime()),
                                                    Double.parseDouble(enterPay.getText().toString()),
                                                    persons.get(position).getId(), ac,
                                                    comment.getText().toString());
                                            persons.get(position).getReckings().add(0, recking);
                                            double total = 0;
                                            for (Recking recking1 : persons.get(position).getReckings()) {
                                                total += recking1.getAmount();
                                            }
                                            if (persons.get(position).getAmount() <= total) {
                                                view.pay.setText(getString(R.string.archive));
                                            }
                                            view.BorrowPersonSumm.setText("" + ((persons.get(position).getAmount() - total) ==
                                                    ((int) (persons.get(position).getAmount() - total)) ?
                                                    ("" + (int) (persons.get(position).getAmount() - total)) :
                                                    ("" + (persons.get(position).getAmount() - total))) + person.getCurrency().getAbbr());
                                            dialog.dismiss();
                                        } else {
                                            if (!person.getCalculate()) {
                                                recking = new Recking(dateFormat.format(date.getTime()),
                                                        Double.parseDouble(enterPay.getText().toString()),
                                                        persons.get(position).getId(),
                                                        comment.getText().toString());
                                                persons.get(position).getReckings().add(0, recking);
                                                double total = 0;
                                                for (Recking recking1 : persons.get(position).getReckings()) {
                                                    total += recking1.getAmount();
                                                }
                                                if (persons.get(position).getAmount() <= total) {
                                                    view.pay.setText(getString(R.string.archive));
                                                }
                                                view.BorrowPersonSumm.setText("" + ((persons.get(position).getAmount() - total) ==
                                                        ((int) (persons.get(position).getAmount() - total)) ?
                                                        ("" + (int) (persons.get(position).getAmount() - total)) :
                                                        ("" + (persons.get(position).getAmount() - total))) + person.getCurrency().getAbbr());
                                                dialog.dismiss();
                                            }
                                        }
                                    }
                                } else {
                                    enterPay.setError(getString(R.string.enter_pay_value));
                                }
                            }
                        });
                        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
                        int width = displayMetrics.widthPixels;
                        dialog.getWindow().setLayout(7 * width / 8, RelativeLayout.LayoutParams.WRAP_CONTENT);
                        dialog.show();
                    } else {
                        for (int i = 0; i < debtBorrowDao.queryBuilder().list().size(); i++) {
                            if (debtBorrowDao.queryBuilder().list().get(i).getId().matches(person.getId())) {
                                debtBorrowDao.queryBuilder().list().get(i).setTo_archive(true);
                                allPersons.get(i).setTo_archive(true);
                                try {
                                    persons.remove(position);
                                } catch (IndexOutOfBoundsException e) {
                                    return;
                                }
                                break;
                            }
                        }
                        notifyItemRemoved(position);
                    }
                }
            });
        }

        private boolean isMumkin(DebtBorrow debt, String accountId, Double summ) {
            Account account = null;
            for (Account ac : accountDao.queryBuilder().list()) {
                if (ac.getId().matches(accountId)) {
                    account = ac;
                    break;
                }
            }
            if (account != null && account.getIsLimited()) {
                double limit = account.getLimite();
                double accounted = logicManager.isLimitAccess(account, debt.getTakenDate());
//            if (account.isLimited() && debt.getCalculate()) {
//                double limit = account.getLimitSum();
//                double accounted = PocketAccounterGeneral.getCost(debt.getTakenDate(),account.getStartMoneyCurrency(),account.getLimitCurrency(),account.getAmount());
//                for (int i = 0; i < PocketAccounter.financeManager.getRecords().size(); i++) {
//                    FinanceRecord tempac=PocketAccounter.financeManager.getRecords().get(i);
//                    if (tempac.getAccount().getId().matches(account.getId())) {
//                        if (tempac.getCategory().getType() == PocketAccounterGeneral.INCOME)
//                            accounted = accounted + PocketAccounterGeneral.getCost(tempac.getDate(),tempac.getCurrency(),account.getLimitCurrency(),tempac.getAmount());
//                        else
//                            accounted = accounted - PocketAccounterGeneral.getCost(tempac.getDate(),tempac.getCurrency(),account.getLimitCurrency(),tempac.getAmount());
//                    }
//                }
//                for (DebtBorrow debtBorrow : PocketAccounter.financeManager.getDebtBorrows()) {
//                    if (debtBorrow.getCalculate()) {
//                        if (debtBorrow.getAccount().getId().matches(account.getId())) {
//                            if (debtBorrow.getType() == DebtBorrow.BORROW) {
//                                accounted = accounted - PocketAccounterGeneral.getCost(debtBorrow.getTakenDate(), debtBorrow.getCurrency(), account.getLimitCurrency(),debtBorrow.getAmount());
//                            } else {
//                                accounted = accounted + PocketAccounterGeneral.getCost(debtBorrow.getTakenDate(), debtBorrow.getCurrency(), account.getLimitCurrency(),debtBorrow.getAmount());
//                            }
//                            for (Recking recking : debtBorrow.getReckings()) {
//                                SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
//                                Calendar cal = Calendar.getInstance();
//                                try {
//                                    cal.setTime(format.parse(recking.getPayDate()));
//                                } catch (ParseException e) {
//                                    e.printStackTrace();
//                                }
//                                if (debtBorrow.getType() == DebtBorrow.BORROW) {
//                                    accounted = accounted + PocketAccounterGeneral.getCost(cal, debtBorrow.getCurrency(), account.getLimitCurrency(),recking.getAmount());
//                                } else {
//                                    accounted = accounted - PocketAccounterGeneral.getCost(cal, debtBorrow.getCurrency(),account.getLimitCurrency(), recking.getAmount());
//                                }
//                            }
//                        } else {
//                            for (Recking recking : debtBorrow.getReckings()) {
//                                SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
//                                Calendar cal = Calendar.getInstance();
//                                if (recking.getAccountId().matches(account.getId())) {
//                                    try {
//                                        cal.setTime(format.parse(recking.getPayDate()));
//                                    } catch (ParseException e) {
//                                        e.printStackTrace();
//                                    }
//                                    if (debtBorrow.getType() == DebtBorrow.BORROW) {
//                                        accounted = accounted + PocketAccounterGeneral.getCost(cal, debtBorrow.getCurrency(), account.getLimitCurrency(),recking.getAmount());
//                                    } else {
//                                        accounted = accounted - PocketAccounterGeneral.getCost(cal, debtBorrow.getCurrency(),account.getLimitCurrency(), recking.getAmount());
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//
//                for (CreditDetials creditDetials : PocketAccounter.financeManager.getCredits()) {
//                    if (creditDetials.isKey_for_include()) {
//                        for (ReckingCredit reckingCredit : creditDetials.getReckings()) {
//                            if (reckingCredit.getAccountId().matches(account.getId())) {
//                                Calendar cal = Calendar.getInstance();
//                                cal.setTimeInMillis(reckingCredit.getPayDate());
//                                accounted = accounted - PocketAccounterGeneral.getCost(cal, creditDetials.getValyute_currency(),account.getLimitCurrency(), reckingCredit.getAmount());
//                            }
//                        }
//                    }
//                }
                if (debt.getType() == DebtBorrow.DEBT) {
                    accounted = accounted - commonOperations.getCost(Calendar.getInstance(), debt.getCurrency(), account.getCurrency(), summ);
                } else {
                    accounted = accounted + commonOperations.getCost(Calendar.getInstance(), debt.getCurrency(), account.getCurrency(), summ);
                }
                if (-limit > accounted) {
                    Toast.makeText(getContext(), R.string.limit_exceed, Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
            return true;
        }

        private Bitmap queryContactImage(int imageDataRow) {
            Cursor c = getContext().getContentResolver().query(ContactsContract.Data.CONTENT_URI, new String[]{
                    ContactsContract.CommonDataKinds.Photo.PHOTO
            }, ContactsContract.Data._ID + "=?", new String[]{
                    Integer.toString(imageDataRow)
            }, null);
            byte[] imageBytes = null;
            if (c != null) {
                if (c.moveToFirst()) {
                    imageBytes = c.getBlob(0);
                }
                c.close();
            }
            if (imageBytes != null) {
                return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            } else {
                return null;
            }
        }

        public ViewHolder onCreateViewHolder(ViewGroup parent, int var2) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_borrow_fragment_mod, parent, false);
            return new ViewHolder(view);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView BorrowPersonName;
        public TextView BorrowPersonNumber;
        public TextView BorrowPersonSumm;
        public TextView BorrowPersonDateGet;
        public TextView BorrowPersonDateRepeat;
        public CircleImageView BorrowPersonPhotoPath;
        public TextView pay;
        public TextView call;
        public RelativeLayout rl;
        public LinearLayout fl;

        public ViewHolder(View view) {
            super(view);
            BorrowPersonName = (TextView) view.findViewById(R.id.tvBorrowPersonName);
            BorrowPersonNumber = (TextView) view.findViewById(R.id.tvBorrowPersonNumber);
            BorrowPersonSumm = (TextView) view.findViewById(R.id.tvBorrowPersonSumm);
            BorrowPersonDateGet = (TextView) view.findViewById(R.id.tvBorrowPersonDateGet);
            BorrowPersonDateRepeat = (TextView) view.findViewById(R.id.tvBorrowPersonDateRepeat);
            BorrowPersonPhotoPath = (CircleImageView) view.findViewById(R.id.imBorrowPerson);
            pay = (TextView) view.findViewById(R.id.btBorrowPersonPay);
            call = (TextView) view.findViewById(R.id.call_person_debt_borrow);
            rl = (RelativeLayout) view.findViewById(R.id.rlDebtBorrowTop);
            fl = (LinearLayout) view.findViewById(R.id.frameLayout);
        }
    }

    public void changeList() {
        MyAdapter adapter = new MyAdapter();
        recyclerView.setAdapter(adapter);
    }
}