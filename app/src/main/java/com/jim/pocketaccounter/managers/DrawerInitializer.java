package com.jim.pocketaccounter.managers;

import android.app.ProgressDialog;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.jim.pocketaccounter.PocketAccounter;
import com.jim.pocketaccounter.R;
//import com.jim.pocketaccounter.debt.DebtBorrowFragment;
import com.jim.pocketaccounter.credit.notificat.NotificationManagerCredit;
import com.jim.pocketaccounter.database.DaoSession;
import com.jim.pocketaccounter.debt.DebtBorrowFragment;
import com.jim.pocketaccounter.fragments.AccountFragment;
import com.jim.pocketaccounter.fragments.AutoMarketFragment;
import com.jim.pocketaccounter.fragments.CategoryFragment;
import com.jim.pocketaccounter.fragments.CreditTabLay;
import com.jim.pocketaccounter.fragments.CurrencyFragment;
import com.jim.pocketaccounter.fragments.PurposeFragment;
import com.jim.pocketaccounter.syncbase.SignInGoogleMoneyHold;
import com.jim.pocketaccounter.syncbase.SyncBase;
import com.jim.pocketaccounter.utils.CircleImageView;
import com.jim.pocketaccounter.utils.FABIcon;
import com.jim.pocketaccounter.utils.navdrawer.LeftMenuAdapter;
import com.jim.pocketaccounter.utils.navdrawer.LeftMenuItem;
import com.jim.pocketaccounter.utils.navdrawer.LeftSideDrawer;
import com.jim.pocketaccounter.utils.password.PasswordWindow;
import com.jim.pocketaccounter.utils.record.RecordExpanseView;
import com.jim.pocketaccounter.utils.record.RecordIncomesView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;


import static com.jim.pocketaccounter.PocketAccounter.PRESSED;
import static com.jim.pocketaccounter.PocketAccounter.reg;

/**
 * Created by DEV on 28.08.2016.
 */

public class DrawerInitializer {
    private PocketAccounter pocketAccounter;
    private LeftSideDrawer drawer;
    private ListView lvLeftMenu;
    private PAFragmentManager fragmentManager;


    TextView userName, userEmail;
    com.jim.pocketaccounter.utils.CircleImageView userAvatar;
    SharedPreferences spref;
    public static SyncBase mySync;

    SharedPreferences.Editor ed;
    private RelativeLayout rlRecordsMain, rlRecordIncomes, rlRecordBalance;
    private TextView tvRecordIncome, tvRecordBalanse, tvRecordExpanse;
    private ImageView ivToolbarMostRight, ivToolbarExcel;
    private RecordExpanseView expanseView;
    private RecordIncomesView incomeView;
    private PasswordWindow pwPassword;
    private Spinner spToolbar;
    boolean downloadnycCanRest = true;
    public static boolean isCalcLayoutOpen = false;
    Uri imageUri;
    ImageView fabIconFrame;
    public static final int key_for_restat = 10101;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReferenceFromUrl("gs://pocket-accounter.appspot.com");
    DownloadImageTask imagetask;
    View mainRoot;
    private AnimationDrawable mAnimationDrawable;
    private NotificationManagerCredit notific;
    boolean keyFromCalc = false;

    public DrawerInitializer(PocketAccounter pocketAccounter, PAFragmentManager fragmentManager) {
        this.pocketAccounter = pocketAccounter;
        this.fragmentManager = fragmentManager;
        drawer = new LeftSideDrawer(pocketAccounter);
        drawer.setLeftBehindContentView(R.layout.activity_behind_left_simple);
        lvLeftMenu = (ListView) pocketAccounter.findViewById(R.id.lvLeftMenu);
        fillNavigationDrawer();
    }

    public LeftSideDrawer getDrawer() {
        return drawer;
    }
    private void fillNavigationDrawer() {
        String[] cats = pocketAccounter.getResources().getStringArray(R.array.drawer_cats);
        String[] financeSubItemTitles = pocketAccounter.getResources().getStringArray(R.array.finance_subitems);
        String[] financeSubItemIcons = pocketAccounter.getResources().getStringArray(R.array.finance_subitem_icons);
        String[] statisticsSubItemTitles = pocketAccounter.getResources().getStringArray(R.array.statistics_subitems);
        String[] statisticsSubItemIcons = pocketAccounter.getResources().getStringArray(R.array.statistics_subitems_icons);
        String[] debtSubItemTitles = pocketAccounter.getResources().getStringArray(R.array.debts_subitems);
        String[] debtSubItemIcons = pocketAccounter.getResources().getStringArray(R.array.debts_subitem_icons);
        List<LeftMenuItem> items = new ArrayList<>();
        spref = pocketAccounter.getSharedPreferences("infoFirst", pocketAccounter.MODE_PRIVATE);
        mySync = new SyncBase(storageRef, pocketAccounter, "PocketAccounterDatabase");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            userName.setText(user.getDisplayName());
            userEmail.setText(user.getEmail());
            try {
                if (user.getPhotoUrl() != null) {
                    imagetask = new DownloadImageTask(userAvatar);
                    imagetask.execute(user.getPhotoUrl().toString());
                    imageUri = user.getPhotoUrl();
                }
            } catch (Exception o) {

            }
        }

        userAvatar = (CircleImageView) pocketAccounter.findViewById(R.id.userphoto);
        userName = (TextView) pocketAccounter.findViewById(R.id.tvToolbarName);
        userEmail = (TextView) pocketAccounter.findViewById(R.id.tvGoogleMail);

        FABIcon fabIcon = (FABIcon) pocketAccounter.findViewById(R.id.fabDrawerNavIcon);
        fabIconFrame = (ImageView) pocketAccounter.findViewById(R.id.iconFrameForAnim);

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            fabIconFrame.setBackgroundResource(R.drawable.cloud_anim);
            mAnimationDrawable = (AnimationDrawable) fabIconFrame.getBackground();

        } else
            fabIconFrame.setBackgroundResource(R.drawable.cloud_sign_in);


        reg = new SignInGoogleMoneyHold(pocketAccounter, new SignInGoogleMoneyHold.UpdateSucsess() {
            @Override
            public void updateToSucsess() {
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    imagetask = new DownloadImageTask(userAvatar);
                    userName.setText(user.getDisplayName());
                    userEmail.setText(user.getEmail());
                    if (user.getPhotoUrl() != null) {
                        try {
                            imagetask.execute(user.getPhotoUrl().toString());

                        } catch (Exception o) {
                        }
                        imageUri = user.getPhotoUrl();
                    }

                    showProgressDialog(pocketAccounter.getString(R.string.cheking_user));
                    PocketAccounter.mySync.meta_Message(user.getUid(), new SyncBase.ChangeStateLisMETA() {
                        @Override
                        public void onSuccses(final long inFormat) {
                            hideProgressDialog();
                            Date datee = new Date();
                            fabIconFrame.setBackgroundResource(R.drawable.cloud_anim);
                            mAnimationDrawable = (AnimationDrawable) fabIconFrame.getBackground();
                            datee.setTime(inFormat);
                            final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(pocketAccounter);
                            builder.setMessage(pocketAccounter.getString(R.string.sync_last_data_sign_up) + (new SimpleDateFormat("dd.MM.yyyy kk:mm")).format(datee))
                                    .setPositiveButton(pocketAccounter.getString(R.string.yes), new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            showProgressDialog(pocketAccounter.getString(R.string.download));
                                            PocketAccounter.mySync.downloadLast(user.getUid(), new SyncBase.ChangeStateLis() {
                                                @Override
                                                public void onSuccses() {
                                                    pocketAccounter.runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            hideProgressDialog();
//                                                            fragmentManager.initialize(new GregorianCalendar());
                                                            if (!drawer.isClosed()) {
                                                                drawer.close();
                                                            }
                                                        }
                                                    });
                                                }

                                                @Override
                                                public void onFailed(String e) {
                                                    hideProgressDialog();
                                                }
                                            });
                                        }
                                    }).setNegativeButton(pocketAccounter.getString(R.string.no), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    hideProgressDialog();
                                    dialog.cancel();

                                }
                            });
                            builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                                @Override
                                public void onCancel(DialogInterface dialog) {
                                    hideProgressDialog();
                                }
                            });
                            builder.create().show();
                        }

                        @Override
                        public void onFailed(Exception e) {
                            hideProgressDialog();
                            fabIconFrame.setBackgroundResource(R.drawable.cloud_anim);
                            mAnimationDrawable = (AnimationDrawable) fabIconFrame.getBackground();

                        }
                    });
                }
            }

            @Override
            public void updateToFailed() {
                userName.setText(R.string.try_later);
                userEmail.setText(R.string.err_con);
            }
        });

        fabIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final FirebaseUser userim = FirebaseAuth.getInstance().getCurrentUser();
                if (userim != null) {

                    (new Handler()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(pocketAccounter);
                            builder.setMessage(R.string.sync_message)
                                    .setPositiveButton(R.string.sync_short, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            mAnimationDrawable.start();
//                                            financeManager.saveAllDatas();
                                            mySync.uploadBASE(userim.getUid(), new SyncBase.ChangeStateLis() {
                                                @Override
                                                public void onSuccses() {
                                                    mAnimationDrawable.stop();
                                                    fabIconFrame.setBackgroundResource(R.drawable.cloud_sucsess);
                                                    (new Handler()).postDelayed(new Runnable() {
                                                        @Override
                                                        public void run() {

                                                            fabIconFrame.setBackgroundResource(R.drawable.cloud_anim);
                                                            mAnimationDrawable = (AnimationDrawable) fabIconFrame.getBackground();

                                                        }
                                                    }, 2000);
                                                }

                                                @Override
                                                public void onFailed(String e) {
                                                    mAnimationDrawable.stop();
                                                    fabIconFrame.setBackgroundResource(R.drawable.cloud_error);
                                                    (new Handler()).postDelayed(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            fabIconFrame.setBackgroundResource(R.drawable.cloud_anim);
                                                            mAnimationDrawable = (AnimationDrawable) fabIconFrame.getBackground();

                                                        }
                                                    }, 2000);
                                                }
                                            });

                                        }
                                    }).setNegativeButton(pocketAccounter.getString(R.string.cancel1), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                            builder.create().show();
                        }
                    }, 150);
                } else {
                    drawer.close();
                    (new Handler()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (spref.getBoolean("FIRSTSYNC", true)) {
                                reg.openDialog();
                            } else
                                reg.regitUser();
                        }
                    }, 150);
                }
            }
        });

        LeftMenuItem main = new LeftMenuItem(cats[0], R.drawable.drawer_home);
        main.setGroup(true);
        items.add(main);
        LeftMenuItem finance = new LeftMenuItem(cats[1], R.drawable.drawer_finance);
        finance.setGroup(true);
        items.add(finance);
        for (int i = 0; i < financeSubItemTitles.length; i++) {
            int resId = pocketAccounter.getResources().getIdentifier(financeSubItemIcons[i], "drawable", pocketAccounter.getPackageName());
            LeftMenuItem subItem = new LeftMenuItem(financeSubItemTitles[i], resId);
            subItem.setGroup(false);
            items.add(subItem);
        }
        LeftMenuItem debts = new LeftMenuItem(cats[3], R.drawable.drawer_debts);
        debts.setGroup(true);
        items.add(debts);
        for (int i = 0; i < debtSubItemTitles.length; i++) {
            int resId = pocketAccounter.getResources().getIdentifier(debtSubItemIcons[i], "drawable", pocketAccounter.getPackageName());
            LeftMenuItem subItem = new LeftMenuItem(debtSubItemTitles[i], resId);
            subItem.setGroup(false);
            items.add(subItem);
        }
        LeftMenuItem statistics = new LeftMenuItem(cats[2], R.drawable.drawer_statistics);
        statistics.setGroup(true);
        items.add(statistics);
        for (int i = 0; i < statisticsSubItemTitles.length; i++) {
            int resId = pocketAccounter.getResources().getIdentifier(statisticsSubItemIcons[i], "drawable", pocketAccounter.getPackageName());
            LeftMenuItem subItem = new LeftMenuItem(statisticsSubItemTitles[i], resId);
            subItem.setGroup(false);
            items.add(subItem);
        }
        LeftMenuItem smsParse = new LeftMenuItem(cats[4], R.drawable.drawer_sms);
        smsParse.setGroup(true);
        items.add(smsParse);
        LeftMenuItem settings = new LeftMenuItem(cats[5], R.drawable.drawer_settings);
        settings.setGroup(true);
        items.add(settings);
        LeftMenuItem rateApp = new LeftMenuItem(cats[6], R.drawable.drawer_rate);
        rateApp.setGroup(true);
        items.add(rateApp);
        LeftMenuItem share = new LeftMenuItem(cats[7], R.drawable.drawer_share);
        share.setGroup(true);
        items.add(share);
        LeftMenuItem writeToUs = new LeftMenuItem(cats[8], R.drawable.drawer_letter_us);
        writeToUs.setGroup(true);
        items.add(writeToUs);
        LeftMenuAdapter adapter = new LeftMenuAdapter(pocketAccounter, items);
        lvLeftMenu.setAdapter(adapter);
        lvLeftMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                if (fragmentManager.getFragmentManager().getBackStackEntryCount() == 0 && position == 0) {
                    pocketAccounter.findViewById(R.id.change).setVisibility(View.VISIBLE);
                } else {
                    pocketAccounter.findViewById(R.id.change).setVisibility(View.GONE);
                }
                drawer.closeLeftSide();
                drawer.postDelayed(new Runnable() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void run() {
                        switch (position) {
                            case 0:
                                fragmentManager.displayMainWindow();
                                break;
                            case 1:
                            case 2:
                                fragmentManager.displayFragment(new CurrencyFragment());
                                break;
                            case 3:
                                fragmentManager.displayFragment(new CategoryFragment());
                                break;
                            case 4:
                                fragmentManager.displayFragment(new AccountFragment());
                                //Accounting management
                                break;
                            case 5:
                                fragmentManager.displayFragment(new PurposeFragment());
                                break;
                            case 6:
                                fragmentManager.displayFragment(new AutoMarketFragment());
                                break;
                            case 7:
                            case 8:
                                fragmentManager.displayFragment(new CreditTabLay());
                                break;
                            case 9:
                                fragmentManager.displayFragment(new DebtBorrowFragment());
                                break;
                            case 10:
//                                fragmentManager.displayFragment(new ReportByAccountFragment());
                                break;
                            case 11:
//                                fragmentManager.displayFragment(new TableBarFragment());
                                break;
                            case 12:
//                                fragmentManager.displayFragment(new ReportByCategory());
                                break;
                            case 13:
//                                fragmentManager.displayFragment(new SMSParseFragment());
                                break;
                            case 14:

//                                Intent zssettings = new Intent(pocketAccounter, SettingsActivity.class);
//                                PocketAccounter.openActivity=true;
//                                for (int i = 0; i < fragmentManager.getBackStackEntryCount(); i++) {
//                                    fragmentManager.popBackStack();
//                                }
//                                startActivityForResult(zssettings, key_for_restat);
                                break;
                            case 15:
//                                if (keyboardVisible) {
//                                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                                    imm.hideSoftInputFromWindow(mainRoot.getWindowToken(), 0);
//                                }
//
//                                findViewById(R.id.change).setVisibility(View.VISIBLE);
//                                Intent rate_app_web = new Intent(Intent.ACTION_VIEW);
//                                PocketAccounter.openActivity=true;
//
//                                rate_app_web.setData(Uri.parse(getString(R.string.rate_app_web)));
//                                startActivity(rate_app_web);
                                break;
                            case 16:
//                                if (keyboardVisible) {
//                                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                                    imm.hideSoftInputFromWindow(mainRoot.getWindowToken(), 0);
//                                }
                                pocketAccounter.findViewById(R.id.change).setVisibility(View.VISIBLE);
                                Intent rate_app_web = new Intent(Intent.ACTION_VIEW);
                                PocketAccounter.openActivity=true;
                                rate_app_web.setData(Uri.parse(pocketAccounter.getString(R.string.rate_app_web)));
                                pocketAccounter.startActivity(rate_app_web);
                                break;
                            case 17:
                                pocketAccounter.findViewById(R.id.change).setVisibility(View.VISIBLE);
                                Intent Email = new Intent(Intent.ACTION_SEND);
                                PocketAccounter.openActivity=true;
                                Email.setType("text/email");
                                Email.putExtra(Intent.EXTRA_SUBJECT, pocketAccounter.getString(R.string.share_app));
                                Email.putExtra(Intent.EXTRA_TEXT, pocketAccounter.getString(R.string.share_app_text));
                                pocketAccounter.startActivity(Intent.createChooser(Email, pocketAccounter.getString(R.string.share_app)));
                                break;
                            case 18:
                                pocketAccounter.findViewById(R.id.change).setVisibility(View.VISIBLE);
                                openGmail(pocketAccounter, new String[]{pocketAccounter.getString(R.string.to_email)},
                                            pocketAccounter.getString(R.string.feedback_subject),
                                            pocketAccounter.getString(R.string.feedback_content));
                                break;
                        }

                    }
                }, 170);
            }
        });
    }
    public static void openGmail(Activity activity, String[] email, String subject, String content) {
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        PocketAccounter.openActivity=true;

        emailIntent.putExtra(Intent.EXTRA_EMAIL, email);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.setType("text/plain");
        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, content);
        final PackageManager pm = activity.getPackageManager();
        final List<ResolveInfo> matches = pm.queryIntentActivities(emailIntent, 0);
        ResolveInfo best = null;
        for (final ResolveInfo info : matches)
            if (info.activityInfo.packageName.endsWith(".gm") || info.activityInfo.name.toLowerCase().contains("gmail"))
                best = info;
        if (best != null)
            emailIntent.setClassName(best.activityInfo.packageName, best.activityInfo.name);
        activity.startActivity(emailIntent);
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        CircleImageView bmImage;

        public DownloadImageTask(CircleImageView bmImage) {
            this.bmImage = bmImage;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            File file = new File(pocketAccounter.getFilesDir(), "userphoto.jpg");
            if (file.exists()) {
                bmImage.setImageURI(Uri.parse(file.getAbsolutePath()));
            }
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;

            for (; true; ) {
                try {
                    InputStream in = new java.net.URL(urldisplay).openStream();
                    mIcon11 = BitmapFactory.decodeStream(in);
                    break;
                } catch (Exception e) {
                    e.printStackTrace();
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                }
                if (isCancelled()) break;
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            if (result != null) {
                downloadnycCanRest = false;
                bmImage.setImageBitmap(result);
                File file = new File(pocketAccounter.getFilesDir(), "userphoto.jpg");
                FileOutputStream out = null;

                try {
                    out = new FileOutputStream(file);
                    result.compress(Bitmap.CompressFormat.JPEG, 100, out);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (out != null) {
                            out.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    private ProgressDialog mProgressDialog;
    public void showProgressDialog(String message) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(pocketAccounter);
            mProgressDialog.setMessage(message);
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }

    public void onStopSuniy(){


        try {

            if (imagetask != null)
                imagetask.cancel(true);
            if (imagetask != null) {
                imagetask.cancel(true);
                imagetask = null;
            }
        } catch (Exception o) {

        }
    }
}
