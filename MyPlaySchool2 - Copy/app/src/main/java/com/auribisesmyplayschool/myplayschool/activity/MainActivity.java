package com.auribisesmyplayschool.myplayschool.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.auribisesmyplayschool.myplayschool.Helper.Constants;
import com.auribisesmyplayschool.myplayschool.R;
import com.auribisesmyplayschool.myplayschool.adapter.GridItemsMainAdapter;
import com.auribisesmyplayschool.myplayschool.adapter.MainPageEduEnquiryAdapter;
import com.auribisesmyplayschool.myplayschool.adapter.MainPageUpcomingJoiningsAdapter;
import com.auribisesmyplayschool.myplayschool.adminApp.activity.AddUserActivity;
import com.auribisesmyplayschool.myplayschool.adminApp.activity.ManageBranchManagerActivity;
import com.auribisesmyplayschool.myplayschool.adminApp.bean.BranCourBean;
import com.auribisesmyplayschool.myplayschool.adminApp.bean.BranchBean;
import com.auribisesmyplayschool.myplayschool.adminApp.classes.AdminUtil;
import com.auribisesmyplayschool.myplayschool.bean.AdmissionBean;
import com.auribisesmyplayschool.myplayschool.bean.BatchBean;
import com.auribisesmyplayschool.myplayschool.bean.GridItemUserHomeBean;
import com.auribisesmyplayschool.myplayschool.adminApp.bean.UserBean;
import com.auribisesmyplayschool.myplayschool.bean.QuoteBean;
import com.auribisesmyplayschool.myplayschool.bean.StudentBean;
import com.auribisesmyplayschool.myplayschool.bean.TeacherBean;
import com.auribisesmyplayschool.myplayschool.classes.MyGridView;
import com.auribisesmyplayschool.myplayschool.bean.SignInBean;
import com.auribisesmyplayschool.myplayschool.classes.AttUtil;
import com.auribisesmyplayschool.myplayschool.classes.RecyclerItemClickListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.annotation.Nullable;

import dmax.dialog.SpotsDialog;

public class MainActivity extends AppCompatActivity {
    String error = "Kindly Select Batch";
    ArrayList<SignInBean> signInBeanArrayList;
    ArrayList<String> batchNameArray;
    ArrayList<Integer> batchIdArray;
    SignInBean signInBean;
    int userAdminId,userId,branchId;
    Intent rcv;
    String[] options;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    Spinner spGroup;
    Button btnSelect;
   // ArrayList<QuoteBean> quoteList;
    ArrayList<String> spinList;
    ArrayList<BranchBean> branchBeanArrayList,rcvBranchBeanArrayList,rcvBranchBeanArrayList2;
    //ArrayList<BatchBean> batchList;
    Dialog dialog;
    String strGroup;
    int batchId, reqCode = 0, branchListPosition = 0, request;
    int k = 0, pos = 0;
    ArrayList<UserBean> userBeanArrayList,teacherBeanArrayList,counsellorBeanArrayList;
    ArrayList<BranCourBean> courseBeanArrayList, courseBeanArrayListActive = new ArrayList<>();
    private SwipeRefreshLayout waveSwipeRefreshLayout;
    private int posEnquiry = 0;
    //private ArrayList<StudentBean> stuEduEnquiryBeanArrayList, stuUpcomingJoiningBeanArrayList;
    MainPageEduEnquiryAdapter mainPageEduEnquiryAdapter;
    MainPageUpcomingJoiningsAdapter mainPageUpcomingJoiningsAdapter;
    RecyclerView listViewEduEnquiry, listViewUpcomingJoinings;
    //StudentBean upcomingStudentBean;
    TextView tvNumberOfParentsInstalled, tvMAHeaderDetails, tvMATitle;
    MyGridView gridViewMainActivityOne, gridViewMainActivityTwo, gridViewMainActivityThree;
    ArrayList<GridItemUserHomeBean> listGridItemsOne, listGridItemsTwo, listGridItemsThree;
    GridItemsMainAdapter gridItemsMainAdapterOne, gridItemsMainAdapterTwo, gridItemsMainAdapterThree;
    ImageView ivVisitInfo,ivMASettings;
    EditText editTextQuoteUpdate;
    FirebaseFirestore db;
    BranCourBean branCourBean;
    StudentBean upcomingStudentBean;
    UserBean userBean;
    ArrayList<BatchBean> batchList,batchListActive,batchListSection,batchListGroup;
    BatchBean batchBean,rcvBatchBean;
    BranchBean branchBean;
    String branchName;
    TeacherBean teacherBean;
    ArrayList<TeacherBean> teacherBeanArrayList1;
    int check=0;
    StudentBean studentBean,todayStuEnquiry;
    ArrayList<StudentBean> studentBeanArrayList,studentFeeNotAllocatedList;
    ArrayList<AdmissionBean> admissionBeanArrayList,admissionFeeNotAllocatedList;
    int feeCategorySelected,applicSize;
    Calendar calendar;
    int cyear,cmonth,cday;
    String cDayString,cMonthString,currentDate;
    ArrayList<StudentBean> stuEduEnquiryBeanArrayList,stuUpcomingJoiningBeanArrayList;
    AdmissionBean admissionBean;
    ArrayList<QuoteBean> quoteList;
    QuoteBean quoteBean;
    ArrayList<AdmissionBean> tempFeeNotList,admissionUpcomingLis;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        preferences = getSharedPreferences(AttUtil.shpREG, MODE_PRIVATE);
        AttUtil.progressDialog(this);
        calendar = Calendar.getInstance();
        cyear = calendar.get(Calendar.YEAR);
        cmonth = calendar.get(Calendar.MONTH);
        cday = calendar.get(Calendar.DAY_OF_MONTH);
        getDate(cyear,cmonth,cday);
        rcv = getIntent();
        TextView upComingText = findViewById(R.id.upText);
        upComingText.setVisibility(View.GONE);
        signInBean = (SignInBean) rcv.getSerializableExtra(AttUtil.signInBean);
        userAdminId = rcv.getIntExtra("adminId",0);
        userId = rcv.getIntExtra("userId",0);
        branchName = rcv.getStringExtra("branchName");
        branchId = rcv.getIntExtra("branchId",0);
        rcvBranchBeanArrayList= (ArrayList<BranchBean>) rcv.getSerializableExtra("branchBeanArrayList");
        rcvBranchBeanArrayList2 = new ArrayList<>();
        db = FirebaseFirestore.getInstance();
        init();
        AttUtil.pd(1);
        if(preferences.getInt(AttUtil.shpLoginType,0)==2){
            getCoursesActiveList();
        }else if(preferences.getInt(AttUtil.shpLoginType,0) == 4){
            getCoursesCounsellorList();
        }
    }

     void getCoursesCounsellorList() {
         courseBeanArrayList = new ArrayList<>();
         courseBeanArrayListActive = new ArrayList<>();
         db.collection(Constants.branchCollection).document(String.valueOf(signInBean.getBranchId()))
                 .collection(Constants.branch_course_collection).get()
                 .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                     @Override
                     public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                         for(DocumentSnapshot doc: queryDocumentSnapshots.getDocuments()){
                             if(doc.exists()){
                                 branCourBean = doc.toObject(BranCourBean.class);
                                 courseBeanArrayList.add(branCourBean);
                             }
                         }

                         if(courseBeanArrayList.size()>0){
                             for (int i = 0; i < courseBeanArrayList.size(); i++) {
                                 if (courseBeanArrayList.get(i).getCourseStatus() == 1)
                                     courseBeanArrayListActive.add(courseBeanArrayList.get(i));
                             }
                         }
                         //AttUtil.pd(0);
                         //getStuCounsLateJoinings();
                         //getCounsellorApplication();
                         getCounsellorTodayEnquiry();

                     }
                 });
    }

     void getCounsellorTodayEnquiry() {
         stuEduEnquiryBeanArrayList = new ArrayList<>();
         final LinearLayout llMainEnquiryListElseLayout = (LinearLayout) findViewById(R.id.tvMainEnquiryListElseLayout);
         db.collection(Constants.student_collection).whereEqualTo("branchId",signInBean.getBranchId())
                 .whereEqualTo("enquiryBean.userId",signInBean.getUserId())
                 .whereEqualTo("enquiryBean.nextVisit",currentDate).get()
                 .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                     @Override
                     public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                         stuEduEnquiryBeanArrayList.clear();
                         for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()){
                             todayStuEnquiry = doc.toObject(StudentBean.class);
                             stuEduEnquiryBeanArrayList.add(todayStuEnquiry);
                         }
                         if (stuEduEnquiryBeanArrayList.size() > 0 && stuEduEnquiryBeanArrayList != null) {
                             mainPageEduEnquiryAdapter = new MainPageEduEnquiryAdapter(stuEduEnquiryBeanArrayList, MainActivity.this);
                             try {
                                 listViewEduEnquiry.setAdapter(mainPageEduEnquiryAdapter);
                             } catch (Exception e) {
                                 e.printStackTrace();
                             }
                             listViewEduEnquiry.setVisibility(View.VISIBLE);
                             llMainEnquiryListElseLayout.setVisibility(View.GONE);
                         } else {
                             llMainEnquiryListElseLayout.setVisibility(View.VISIBLE);
                             listViewEduEnquiry.setVisibility(View.GONE);
                         }
                         getCounsellorApplication();
                     }
                 });
    }

    void getCounsellorApplication() {
        AttUtil.pd(0);
             db.collection(Constants.student_collection).whereEqualTo("branchId",signInBean.getBranchId())
                     .whereEqualTo("enquiryBean.userId",signInBean.getUserId())
                     .addSnapshotListener(new EventListener<QuerySnapshot>() {
                         @Override
                         public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                             applicSize = queryDocumentSnapshots.size();
                             listGridItemsOne.get(1).setTitle("Application(" + (applicSize) + ")");
                             gridItemsMainAdapterOne.notifyDataSetChanged();
                         }
                     });
    }

    void getStuCounsLateJoinings() {
        tempFeeNotList  = new ArrayList<>();
         db.collection(Constants.admission_collection).whereEqualTo("batchId",0)
                 .whereEqualTo("branchId",signInBean.getBranchId()).get()
                 .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                     @Override
                     public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                         admissionBeanArrayList.clear();
                         admissionFeeNotAllocatedList.clear();
                         for(DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()){
                             feeCategorySelected = doc.getLong("feeCategorySelected").intValue();
                             admissionBean = doc.toObject(AdmissionBean.class);
                             if(feeCategorySelected>0){
                                 admissionBeanArrayList.add(admissionBean);
                             }else {
                                 //admissionFeeNotAllocatedList.add(admissionBean);
                                 tempFeeNotList.add(admissionBean);
                             }
                         }

                         fetchFeeNotStu();

                         /*if(admissionFeeNotAllocatedList.size()>0){
                             listGridItemsOne.get(6).setTitle("Assign Fees Plan(" + admissionFeeNotAllocatedList.size() + ")");
                             gridItemsMainAdapterOne.notifyDataSetChanged();
                         }
                         if(admissionBeanArrayList.size()>0){
                             AttUtil.laterJoinings = admissionBeanArrayList.size();
                         }else{
                             AttUtil.laterJoinings = 0;
                         }*/
                     }
                 });
    }

     void fetchFeeNotStu() {
        check = 0;
        for(int i=0;i<tempFeeNotList.size();i++){
            final int finalI = i;
            db.collection(Constants.student_collection).document(String.valueOf(tempFeeNotList.get(i).getStudentId()))
                    .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    studentBean = documentSnapshot.toObject(StudentBean.class);
                    if(studentBean.getUserId() == signInBean.getUserId()){
                        admissionFeeNotAllocatedList.add(tempFeeNotList.get(finalI));
                    }
                    check = check + 1;
                    if(check == tempFeeNotList.size()){
                        AttUtil.pd(0);
                        if(admissionFeeNotAllocatedList.size()>0){
                            listGridItemsOne.get(2).setTitle("Assign Fees Plan(" + admissionFeeNotAllocatedList.size() + ")");
                            gridItemsMainAdapterOne.notifyDataSetChanged();
                        }
                    }
                }
            });
        }
    }

    void getSizeModules() {
         listGridItemsOne.get(5).setTitle("Class(" + courseBeanArrayList.size() + ")");
         listGridItemsOne.get(7).setTitle("Teachers(" + teacherBeanArrayList1.size() + ")");
         listGridItemsOne.get(6).setTitle("Assign Fees Plan(" + admissionFeeNotAllocatedList.size() + ")");
         listGridItemsOne.get(1).setTitle("Application(" + (applicSize+1) + ")");
         listGridItemsOne.get(4).setTitle("Section("+batchList.size()+")");
         gridItemsMainAdapterOne.notifyDataSetChanged();
         AttUtil.pd(0);
    }

    public void getUsersList() {
        teacherBeanArrayList = new ArrayList<>();
        userBeanArrayList = new ArrayList<>();
        counsellorBeanArrayList = new ArrayList<>();
        db.collection(Constants.usersCollection)
                .whereEqualTo("branchId",signInBean.getBranchId()).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
               // progressDialog.dismiss();
             for(DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()){
                 if(doc.exists()){
                     userBean = doc.toObject(UserBean.class);
                     userBeanArrayList.add(userBean);
                 }
             }

             if(userBeanArrayList.size()>0){
                 for(int i=0;i<userBeanArrayList.size();i++){
                     if(userBeanArrayList.get(i).getUserType()==3)
                         teacherBeanArrayList.add(userBeanArrayList.get(i));
                     if(userBeanArrayList.get(i).getUserType()==2)
                         counsellorBeanArrayList.add(userBeanArrayList.get(i));
                 }
             }
             getLateJoinings();
            }
        });
    }

    public void getCoursesActiveList() {
        courseBeanArrayList = new ArrayList<>();
        courseBeanArrayListActive = new ArrayList<>();
        db.collection(Constants.branchCollection).document(String.valueOf(signInBean.getBranchId()))
                .collection(Constants.branch_course_collection).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(DocumentSnapshot doc: queryDocumentSnapshots.getDocuments()){
                    if(doc.exists()){
                        branCourBean = doc.toObject(BranCourBean.class);
                        courseBeanArrayList.add(branCourBean);
                    }
                }

                if(courseBeanArrayList.size()>0){
                    for (int i = 0; i < courseBeanArrayList.size(); i++) {
                        if (courseBeanArrayList.get(i).getCourseStatus() == 1)
                            courseBeanArrayListActive.add(courseBeanArrayList.get(i));
                    }
                }
                //getLateJoinings();
                getUsersList();

            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    void init() {
        todayStuEnquiry = new StudentBean();
        upcomingStudentBean = new StudentBean();
        batchBean = new BatchBean();
        batchList = new ArrayList<>();
        batchListActive = new ArrayList<>();
        batchListSection = new ArrayList<>();
        batchListGroup = new ArrayList<>();
        branchBean = new BranchBean();
        branchBeanArrayList = new ArrayList<>();
        teacherBeanArrayList = new ArrayList<>();
        teacherBeanArrayList1 = new ArrayList<>();
        counsellorBeanArrayList = new ArrayList<>();
        userBeanArrayList = new ArrayList<>();
        batchIdArray = new ArrayList<>();
        batchNameArray = new ArrayList<>();
        signInBeanArrayList = new ArrayList<>();
        branCourBean = new BranCourBean();
        courseBeanArrayList = new ArrayList<>();
        studentBean = new StudentBean();
        admissionBeanArrayList = new ArrayList<>();
        admissionFeeNotAllocatedList = new ArrayList<>();
        studentBeanArrayList = new ArrayList<>();
        studentFeeNotAllocatedList = new ArrayList<>();
        preferences = getSharedPreferences(AttUtil.shpREG, Context.MODE_PRIVATE);
        editor = preferences.edit();
        //SignInBean signInBean = new Gson().fromJson(preferences.getString(AttUtil.shpSignInBean, ""), SignInBean.class);
        //batchList = new ArrayList<>();
        spinList = new ArrayList<>();
//        nav_view =  findViewById(R.id.nav_drawer);
//        mDrawerLayout =  findViewById(R.id.drawer_layout);
//        coordinatorLayout =  findViewById(R.id.snackBarPosition);
//        View view = nav_view.getHeaderView(0);

        TextView textViewEmail =  findViewById(R.id.textViewEmail);
        TextView textViewName =  findViewById(R.id.textViewName);
        tvMATitle=  findViewById(R.id.tvMATitle);
       // ivMASettings =  findViewById(R.id.ivMASettings);
        /*ivMASettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!waveSwipeRefreshLayout.isRefreshing()) {
                   // startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                }
            }
        });*/
           if (preferences.getInt(AttUtil.shpLoginType, 0) == 2) {
            textViewEmail.setText(signInBean.getUserEmail());
            textViewName.setText(signInBean.getUserName());
        }
        if (preferences.getInt(AttUtil.shpLoginType, 0) == 4) {
            textViewEmail.setText(signInBean.getUserEmail());
            textViewName.setText(signInBean.getUserName());
        }
        tvNumberOfParentsInstalled = findViewById(R.id.tvNumberOfParentsInstalled);
        tvMAHeaderDetails = findViewById(R.id.tvMAHeaderDetails);
        ivVisitInfo = findViewById(R.id.ivVisitInfo);
        stuEduEnquiryBeanArrayList = new ArrayList<>();
        stuUpcomingJoiningBeanArrayList = new ArrayList<>();
        gridViewMainActivityOne = findViewById(R.id.gridViewMainActivityOne);
        gridViewMainActivityTwo = findViewById(R.id.gridViewMainActivityTwo);
        gridViewMainActivityThree = findViewById(R.id.gridViewMainActivityThree);
        waveSwipeRefreshLayout = findViewById(R.id.mainPageSwipe);
        waveSwipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(this, R.color.colorPrimary));
        try {
            waveSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    waveSwipeRefreshLayout.setRefreshing(true);
                    if (preferences.getInt(AttUtil.shpLoginType, 0) == 2) {
                        if (AttUtil.isNetworkConnected(MainActivity.this)) {
                            //retrieveUsersCourses();
                            AttUtil.progressDialog(MainActivity.this);
                            AttUtil.pd(1);
                            getCoursesActiveList();

                        } else {
                            //retrieveCBENetConnect();
                        }
                    } else if (preferences.getInt(AttUtil.shpLoginType, 0) == 4) {
                        if (AttUtil.isNetworkConnected(MainActivity.this)) {
                            AttUtil.progressDialog(MainActivity.this);
                            AttUtil.pd(1);
                            getCoursesCounsellorList();
                        } else {
                            //retrieveCBENetConnect();
                        }
                    }
                    waveSwipeRefreshLayout.setRefreshing(false);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        ivVisitInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recordInfo();
            }
        });
        listViewEduEnquiry = findViewById(R.id.lvMainEnquiry);
        listViewUpcomingJoinings = findViewById(R.id.lvUpcomingJoining);
        try {
            listViewEduEnquiry.setLayoutManager(new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false));
            listViewEduEnquiry.addOnItemTouchListener(new RecyclerItemClickListener(MainActivity.this, new RecyclerItemClickListener.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    if (!waveSwipeRefreshLayout.isRefreshing()) {
                        posEnquiry = position;
                        startActivityForResult(new Intent(MainActivity.this, ViewEnquiryDetailActivity.class)
                                .putExtra(AttUtil.TAG_STUDENTBEAN, stuEduEnquiryBeanArrayList.get(posEnquiry))
                                .putExtra(AttUtil.KEY_COURSE_ARRAYLIST, courseBeanArrayListActive)
                               // .putExtra(AttUtil.KEY_USER_ARRAYLIST, teacherBeanArrayList)
                                .putExtra(AttUtil.KEY_COUNSELLOR_ARRAYLIST, counsellorBeanArrayList), AttUtil.REQ_CODE);
                    }
                }
            }));

        } catch (Exception e) {
            e.printStackTrace();
        }
        /*try {
            listViewUpcomingJoinings.setLayoutManager(new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false));
            listViewUpcomingJoinings.addOnItemTouchListener(new RecyclerItemClickListener(MainActivity.this, new RecyclerItemClickListener.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    if (!waveSwipeRefreshLayout.isRefreshing()) {
                       upcomingStudentBean = stuUpcomingJoiningBeanArrayList.get(position);
                        showOptionsUpcoming();
                    }
                }
            }));
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        listGridItemsOne = new ArrayList<>();
        listGridItemsTwo = new ArrayList<>();
        listGridItemsThree = new ArrayList<>();
        if (preferences.getInt(AttUtil.shpLoginType, 0) == 2) {
            listGridItemsOne.add(new GridItemUserHomeBean("Add Application", R.drawable.ic_forms));
            listGridItemsOne.add(new GridItemUserHomeBean("Application(0)", R.drawable.ic_open_folder_with_document));
            listGridItemsOne.add(new GridItemUserHomeBean("Students", R.drawable.ic_group));
            listGridItemsOne.add(new GridItemUserHomeBean("Add Section", R.drawable.ic_add_list));
            listGridItemsOne.add(new GridItemUserHomeBean("Section(0)", R.drawable.ic_list));
            listGridItemsOne.add(new GridItemUserHomeBean("Class"+"("+courseBeanArrayListActive.size()+")", R.drawable.ic_books));
            listGridItemsOne.add(new GridItemUserHomeBean("Assign Fee Plan(0)", R.drawable.ic_strategy));
            listGridItemsOne.add(new GridItemUserHomeBean("Teacher(0)", R.drawable.ic_presentation));
            listGridItemsOne.add(new GridItemUserHomeBean("Employees", R.drawable.ic_group_employee));
            //listGridItemsOne.add(new GridItemUserHomeBean("Counsellor(0)", R.drawable.ic_users));
            listGridItemsOne.add(new GridItemUserHomeBean("Fee Summary", R.drawable.ic_user_fee_summary));
            listGridItemsOne.add(new GridItemUserHomeBean("Attendance", R.drawable.ic_hold));
            listGridItemsOne.add(new GridItemUserHomeBean("Transport", R.drawable.ic_transport));

            listGridItemsTwo.add(new GridItemUserHomeBean("Digital Message", R.drawable.ic_mail));
            //listGridItemsTwo.add(new GridItemUserHomeBean("Media", R.drawable.ic_network));
            listGridItemsTwo.add(new GridItemUserHomeBean("Update Tip", R.drawable.ic_light_bulb));
            listGridItemsThree.add(new GridItemUserHomeBean("Switch branch", R.drawable.ic_change));
            listGridItemsThree.add(new GridItemUserHomeBean("Logout", R.drawable.ic_logout));
            gridItemsMainAdapterOne = new GridItemsMainAdapter(MainActivity.this, R.layout.grid_item_main_activity, listGridItemsOne);
            gridViewMainActivityOne.setAdapter(gridItemsMainAdapterOne);
            gridItemsMainAdapterTwo = new GridItemsMainAdapter(MainActivity.this, R.layout.grid_item_main_activity, listGridItemsTwo);
            gridViewMainActivityTwo.setAdapter(gridItemsMainAdapterTwo);
            gridItemsMainAdapterThree = new GridItemsMainAdapter(MainActivity.this, R.layout.grid_item_main_activity, listGridItemsThree);
            gridViewMainActivityThree.setAdapter(gridItemsMainAdapterThree);
            gridViewMainActivityOne.setOnItemClickListener(onItemClickListenerOne);
            gridViewMainActivityTwo.setOnItemClickListener(onItemClickListenerTwo);
            gridViewMainActivityThree.setOnItemClickListener(onItemClickListenerThree);
            tvMAHeaderDetails.setText(signInBean.getUserEmail());
            tvMATitle.setText("MPS - Manager");
        } else {
            listGridItemsOne.add(new GridItemUserHomeBean("Add Application", R.drawable.ic_forms));
            listGridItemsOne.add(new GridItemUserHomeBean("Application(0)", R.drawable.ic_open_folder_with_document));
           // listGridItemsOne.add(new GridItemUserHomeBean("Assign Fee Plan(0)", R.drawable.ic_strategy));

            listGridItemsThree.add(new GridItemUserHomeBean("Logout", R.drawable.ic_logout));
            gridItemsMainAdapterOne = new GridItemsMainAdapter(MainActivity.this, R.layout.grid_item_main_activity, listGridItemsOne);
            gridViewMainActivityOne.setAdapter(gridItemsMainAdapterOne);

            gridItemsMainAdapterThree = new GridItemsMainAdapter(MainActivity.this, R.layout.grid_item_main_activity, listGridItemsThree);
            gridViewMainActivityThree.setAdapter(gridItemsMainAdapterThree);
            gridViewMainActivityOne.setOnItemClickListener(onItemClickListenerOne);
            gridViewMainActivityThree.setOnItemClickListener(onItemClickListenerThree);

            findViewById(R.id.lUpcomingJoiningsLayout).setVisibility(View.GONE);
            findViewById(R.id.lAppInstalls).setVisibility(View.GONE);
           // tvMAHeaderDetails.setText(new Gson().fromJson(preferences.getString(AttUtil.shpCounsellorBean, ""), SignInBean.class).getUserEmail());
            tvMATitle.setText("MPS - Counsellor");
        }


//        gridViewMainActivity.setOnItemClickListener(this);

        /*if (preferences.getInt(AttUtil.shpLoginType, 0) == 2) {
            if (AttUtil.isNetworkConnected(MainActivity.this)) {
                retrieveUsersCourses();
            } else {
                retrieveCBENetConnect();
            }
        } else if (preferences.getInt(AttUtil.shpLoginType, 0) == 4) {
            if (AttUtil.isNetworkConnected(MainActivity.this)) {
                retrieveCounsellorData();
            } else {
                retrieveCBENetConnect();
            }
        }*/
    }

    void showOptionsUpcoming() {
        final AlertDialog.Builder build = new AlertDialog.Builder(this);

        String[] options = {"Send Message to Parent/Spouse", "Call Parent/Spouse"};

        build.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch (i) {
                    case 0:
                        showSendMsg(upcomingStudentBean.getFatherPhone());
                        break;
                    case 1:
                        call(upcomingStudentBean.getFatherPhone());
                        break;
                }
            }
        });

        build.create().show();
    }

    void showSendMsg(String phone) {

        Uri uri = Uri.parse("smsto:" + phone);
        Intent it = new Intent(Intent.ACTION_SENDTO, uri);
        startActivity(it);

    }

    void call(String phone) {
        Intent i = new Intent(Intent.ACTION_DIAL);
        i.setData(Uri.parse("tel:" + phone));
        startActivity(i);
    }

        void recordInfo() {
            android.support.v7.app.AlertDialog.Builder dialogBuilder =
                    new android.support.v7.app.AlertDialog.Builder(MainActivity.this);
            dialogBuilder.setTitle("Information");
            LinearLayout layout = new LinearLayout(MainActivity.this);
            layout.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(15, LinearLayout.LayoutParams.MATCH_PARENT);
            params.setMargins(0, 0, 0, 0);
            params1.setMargins(30, 0, 0, 0);
            LinearLayout enquiryLinearLayout = new LinearLayout(MainActivity.this);
            LinearLayout counsellingLinearLayout = new LinearLayout(MainActivity.this);
//        LinearLayout demoLinearLayout = new LinearLayout(MainActivity.this);
            LinearLayout registerLinearLayout = new LinearLayout(MainActivity.this);
            LinearLayout discardLinearLayout = new LinearLayout(MainActivity.this);
            enquiryLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
            counsellingLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
//        demoLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
            registerLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
            discardLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
            TextView enquiry = new TextView(MainActivity.this);
            TextView counselling = new TextView(MainActivity.this);
//        TextView demo = new TextView(MainActivity.this);
            TextView register = new TextView(MainActivity.this);
            TextView discard = new TextView(MainActivity.this);
            enquiry.setText(" - Enquiry");
            counselling.setText(" - Counsellinging");
//        demo.setText(" - Demo Student");
            register.setText(" - Registered Student");
            discard.setText(" - Discarded Enquiry");
            View enquiryview = new View(MainActivity.this);
            View counsellingview = new View(MainActivity.this);
//        View demoview = new View(MainActivity.this);
            View registerview = new View(MainActivity.this);
            View discardview = new View(MainActivity.this);
            enquiryview.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.colorSeaGreen));
            counsellingview.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.colorYellow));
//        demoview.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.colorBlue));
            registerview.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.colorGreen));
            discardview.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.colorOrange));

            enquiryLinearLayout.addView(enquiryview, 0, params1);
            enquiryLinearLayout.addView(enquiry, 1, params);
            counsellingLinearLayout.addView(counsellingview, 0, params1);
            counsellingLinearLayout.addView(counselling, 1, params);
//        demoLinearLayout.addView(demoview, 0, params1);
//        demoLinearLayout.addView(demo, 1, params);
            registerLinearLayout.addView(registerview, 0, params1);
            registerLinearLayout.addView(register, 1, params);
            discardLinearLayout.addView(discardview, 0, params1);
            discardLinearLayout.addView(discard, 1, params);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                enquiry.setTextAppearance(android.R.style.TextAppearance_Medium);
                counselling.setTextAppearance(android.R.style.TextAppearance_Medium);
//            demo.setTextAppearance(android.R.style.TextAppearance_Medium);
                register.setTextAppearance(android.R.style.TextAppearance_Medium);
                discard.setTextAppearance(android.R.style.TextAppearance_Medium);
            }

            layout.addView(enquiryLinearLayout, params);
            layout.addView(counsellingLinearLayout, params);
//        layout.addView(demoLinearLayout, params);
            layout.addView(registerLinearLayout, params);
            layout.addView(discardLinearLayout, params);

            dialogBuilder.setView(layout);
            dialogBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            android.support.v7.app.AlertDialog alertDialog = dialogBuilder.create();
            alertDialog.setCancelable(false);
            alertDialog.show();
        }



    AdapterView.OnItemClickListener onItemClickListenerOne = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (preferences.getInt(AttUtil.shpLoginType, 0) == 2) {
                switch (position) {
                    case 0:
                        startActivity(new Intent(MainActivity.this, AddEnquiryActivity.class)
                                .putExtra(AttUtil.KEY_COURSE_ARRAYLIST, courseBeanArrayListActive)
                                .putExtra(AttUtil.KEY_COUNSELLOR_ARRAYLIST, counsellorBeanArrayList)
                                .putExtra(AttUtil.signInBean,signInBean)
                        );
                        overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);
                        break;
                    case 1:
                      /* startActivity(new Intent(MainActivity.this, ManageEnquiryActivity.class)
                                .putExtra(AttUtil.KEY_COURSE_ARRAYLIST, courseBeanArrayListActive)
                               // .putExtra(AttUtil.KEY_USER_ARRAYLIST, teacherBeanArrayList)
                                .putExtra(AttUtil.KEY_COUNSELLOR_ARRAYLIST, counsellorBeanArrayList)
                                .putExtra(AttUtil.signInBean,signInBean)
                       );*/
                      Intent intent = new Intent(MainActivity.this,ManageEnquiryActivity.class);
                      intent.putExtra(AttUtil.KEY_COURSE_ARRAYLIST, courseBeanArrayListActive)
                              .putExtra(AttUtil.KEY_COUNSELLOR_ARRAYLIST, counsellorBeanArrayList)
                              .putExtra(AttUtil.signInBean,signInBean);
                      startActivityForResult(intent,AttUtil.REQ_CODE);
                        overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);
                        break;
                    case 2:
                        k = 1;
                        viewBatch(1);
                        break;
                    case 3:
                        startActivityForResult(new Intent(MainActivity.this, AddBatchActivity.class)
                                .putExtra(AttUtil.KEY_COURSE_ARRAYLIST, courseBeanArrayListActive),250);
                        break;
                    case 4:
                        coursesList();
                        break;
                    case 5:
                        AttUtil.progressDialog(MainActivity.this);
                        AttUtil.pd(1);
                        startActivity(new Intent(MainActivity.this, CourseListActivity.class)
                                .putExtra(AttUtil.KEY_COURSE_ARRAYLIST, courseBeanArrayListActive));
                        AttUtil.pd(0);
                        break;
                    case 6:
                        /*startActivity(new Intent(MainActivity.this, StudentFeeListActivity.class)
                                .putExtra("approved_students", 0)
                                .putExtra("studentList",studentFeeNotAllocatedList));*/

                        startActivityForResult(new Intent(MainActivity.this, StudentFeeListActivity.class).putExtra("approved_students", 0)
                                .putExtra("admissionList",admissionFeeNotAllocatedList),820);
                        break;
                    case 7:
                        startActivity(new Intent(MainActivity.this, TeachersListActivity.class)
                                .putExtra("teacherBeanArrayList",teacherBeanArrayList1));
                        break;
                    case 8:
                        //Toast.makeText(MainActivity.this,""+branchBeanArrayList.size(),Toast.LENGTH_SHORT).show();
                        startActivityForResult(new Intent(MainActivity.this, ManageBranchManagerActivity.class)
                                .putExtra(AttUtil.signInBean,signInBean).putExtra("adminId",userAdminId)
                                .putExtra("userId",userId).putExtra("branchName",branchName)
                                .putExtra("branchId",branchId),AttUtil.REQ_CODE);
                        /*startActivity(new Intent(MainActivity.this, ManageBranchManagerActivity.class)
                                .putExtra(AttUtil.signInBean,signInBean).putExtra("adminId",userAdminId)
                                .putExtra("userId",userId).putExtra("branchName",branchName));*/
                        break;
                    case 9:
                        startActivity(new Intent(MainActivity.this, StudentListActivity.class)
                                .putExtra("lateJoining", 2).putExtra("choose", 1)
                                .putExtra(AttUtil.KEY_BATCH_ID, 0).putExtra("branchId",signInBean.getBranchId()));
                        break;
                    case 10:
                        startActivity(new Intent(MainActivity.this, StudentAttendanceActivity.class)
                        .putExtra(AttUtil.BATCH_LIST,batchList).putExtra("userBean",signInBean));
                        break;
                    case 11:
                        startActivity(new Intent(MainActivity.this, TransportActivity.class)
                        .putExtra("branchId",signInBean.getBranchId())
                        .putExtra("userList",userBeanArrayList));
                        break;
                }
            }else{
                switch (position) {
                    case 0:
                        startActivity(new Intent(MainActivity.this, AddEnquiryActivity.class)
                                .putExtra(AttUtil.KEY_COURSE_ARRAYLIST, courseBeanArrayListActive)
                                .putExtra(AttUtil.KEY_COUNSELLOR_ARRAYLIST, counsellorBeanArrayList)
                                .putExtra(AttUtil.signInBean,signInBean));
                        overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);
                        break;
                    case 1:
                        startActivity(new Intent(MainActivity.this, ManageEnquiryActivity.class)
                                .putExtra(AttUtil.KEY_COURSE_ARRAYLIST, courseBeanArrayListActive)
                                .putExtra(AttUtil.signInBean,signInBean)
                                //.putExtra(AttUtil.KEY_USER_ARRAYLIST, teacherBeanArrayList)
                                .putExtra(AttUtil.KEY_COUNSELLOR_ARRAYLIST, counsellorBeanArrayList));
                        overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);
                        break;
                    /*case 2:
                        startActivityForResult(new Intent(MainActivity.this, StudentFeeListActivity.class)
                                .putExtra("approved_students", 0)
                                .putExtra("admissionList",admissionFeeNotAllocatedList),820);
                        break;*/

                }
            }

        }
    };

    AdapterView.OnItemClickListener onItemClickListenerTwo = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            switch (position) {
                case 0:
                    Intent intent = new Intent(MainActivity.this, DashboardActivity.class);
                    intent.putExtra(AttUtil.BATCH_LIST,batchList);
                    startActivity(intent);
                    break;
                /*case 1:
                    Intent intent2 = new Intent(MainActivity.this, DashboardActivity.class);
                    intent2.putExtra("upload", 1);
                    startActivity(intent2);
                    break;*/
                case 1:
                    alertUpdateTip();
                    break;
            }
        }
    };

     void alertUpdateTip() {
         AlertDialog.Builder builder = new AlertDialog.Builder(this);
         builder.setTitle("Tip of the day");
         LinearLayout linearLayout = new LinearLayout(this);
         linearLayout.setOrientation(LinearLayout.VERTICAL);
         LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
         editTextQuoteUpdate = new EditText(this);
         editTextQuoteUpdate.setText(quoteList.get(0).getQuote());
         linearLayout.setPadding(50, 10, 10, 50);
         linearLayout.addView(editTextQuoteUpdate, params);
         builder.setView(linearLayout);
         builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
             @Override
             public void onClick(DialogInterface dialogInterface, int i) {
                 if (AttUtil.isNetworkConnected(MainActivity.this)) {
                     updateTipRequest();
                 } else {
                     //updatetipNetConnect();
                 }
             }
         });
         builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
             @Override
             public void onClick(DialogInterface dialogInterface, int i) {
                 dialogInterface.dismiss();
             }
         });
         builder.create().show();
    }

     void updateTipRequest() {
         AttUtil.progressDialog(this);
         AttUtil.pd(1);
         quoteBean.setQuote_id(1);
         quoteBean.setQuote(editTextQuoteUpdate.getText().toString().trim());
         quoteBean.setStatus(1);
         db.collection(Constants.quote_collection).document(String.valueOf(quoteBean.getQuote_id()))
                 .update("quote",quoteBean.getQuote()).addOnSuccessListener(new OnSuccessListener<Void>() {
             @Override
             public void onSuccess(Void aVoid) {
                 quoteList.set(0,quoteBean);
                 editTextQuoteUpdate.setText(quoteBean.getQuote());
                 Toast.makeText(MainActivity.this,"Tip of the day Updated",Toast.LENGTH_SHORT).show();
                 AttUtil.pd(0);
             }
         });
    }


    void viewBatch(final int request) {
        spinList.clear();
       // batchList.clear();
        batchNameArray.clear();
        batchIdArray.clear();
//                            spinList.add("All");
//                            batchNameArray.add("");
//                            batchIdArray.add(0);

        if (batchList.size() > 0 || batchList != null) {
          //  AttUtil.batchBeanArrayListActive.clear();
            batchListActive.clear();
            for (int i = 0; i < batchListSection.size(); i++) {
                if (batchList.get(i).getBatchStatus() == 1) {
                    batchListActive.add(batchList.get(i));
                }
            }
        }
        //batchList = batchListSection;
        int lateJoiningCount = 0;
        if (request == 1) {
            spinList.add("Late Joinings");
            batchIdArray.add(0, 0);
        }
        for (int i = 0; i < batchList.size(); i++) {
            batchIdArray.add(batchList.get(i).getBatchId());
//            lateJoiningCount = lateJoiningCount + batchList.get(i).getLaterJoinings();
            batchNameArray.add(batchList.get(i).getBatch_title());
            spinList.add(batchList.get(i).getBatch_title() + " | Active:" + batchList.get(i).getActiveStudents() + " - Inactive:" + batchList.get(i).getInActiveStudents());
        }
        spinList.remove(0);
        spinList.add(0, "Late Joinings | " + AttUtil.laterJoinings);
        chooseFromGroup(request);
    }

    void chooseFromGroup(final int request) {
         AttUtil.pd(0);
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.group_dialog);
        dialog.setTitle("Select a Section");

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();
        lp.copyFrom(window.getAttributes());

        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);

        spGroup = dialog.findViewById(R.id.spinnerGroup);
        btnSelect = dialog.findViewById(R.id.buttonRet);
        spGroup.setAdapter(new ArrayAdapter(this, android.R.layout.simple_list_item_1, spinList));
        spGroup.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView var1, View var2, int position, long var4) {
                strGroup = spinList.get(position).toString();
                try {
                    pos = position;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            public void onNothingSelected(AdapterView var1) {
            }
        });
        btnSelect.setOnClickListener(new View.OnClickListener() {
            public void onClick(View var1) {
                if (strGroup.contains("Select")) {
                    Toast.makeText(MainActivity.this, error, Toast.LENGTH_SHORT).show();
                } else {
                    Intent var2 = null;
                    if (request == 1) {
                        var2 = new Intent(MainActivity.this, StudentListActivity.class);
                        var2.putExtra(AttUtil.KEY_COURSE_ARRAYLIST, courseBeanArrayListActive);
                        var2.putExtra(AttUtil.BATCH_LIST,batchList);
                    } else if (request == 2) {
                        //var2 = new Intent(MainActivity.this, ApproveStudentsActivity.class);
                    } else if (request == 3) {
                       /* var2 = new Intent(MainActivity.this, StudentFeeListActivity.class);
                        var2.putExtra("approved_students", 1);*/
                    } else if (request == 4) {
                        /*var2 = new Intent(MainActivity.this, StudentFeeListActivity.class);
                        var2.putExtra("approved_students", 0);*/
                    }
                    if (request == 1) {
                        if (pos == 0) {
                            var2.putExtra(AttUtil.KEY_BATCH_ID, 0);
                            var2.putExtra("lateJoining", 1);
                            var2.putExtra(AttUtil.KEY_STUDENT_LATE_JOINING_ARRAYLIST,admissionBeanArrayList);
                        } else{
                            var2.putExtra("branchId",signInBean.getBranchId());
                            var2.putExtra(AttUtil.KEY_BATCH_ID, batchIdArray.get(pos));
                            var2.putExtra(AttUtil.KEY_BATCH_NAME,batchNameArray.get(pos-1));
                        }

                    } else
                        var2.putExtra(AttUtil.KEY_BATCH_ID, batchIdArray.get(pos));

                    var2.putExtra("choose", 1);
                   // startActivity(var2);
                    startActivityForResult(var2,501);
                    dialog.dismiss();
                }
            }
        });
        dialog.show();
    }

    private void getSwitchBranches() {
        if(rcvBranchBeanArrayList.size()>0){
            for(int i=0;i<rcvBranchBeanArrayList.size();i++){
                if(rcvBranchBeanArrayList.get(i).getBranchName().equals(branchName)){ ;
                    rcvBranchBeanArrayList.remove(i);
                }
            }
        }
        //getNoOfApplication();
        getTodayEnquiry();
    }

     void getTodayEnquiry() {
        stuEduEnquiryBeanArrayList = new ArrayList<>();
         final LinearLayout llMainEnquiryListElseLayout = (LinearLayout) findViewById(R.id.tvMainEnquiryListElseLayout);
        db.collection(Constants.student_collection).whereEqualTo("branchId",signInBean.getBranchId())
                .whereEqualTo("enquiryBean.nextVisit",currentDate).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                stuEduEnquiryBeanArrayList.clear();
                for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()){
                    todayStuEnquiry = doc.toObject(StudentBean.class);
                    stuEduEnquiryBeanArrayList.add(todayStuEnquiry);
                }
                if (stuEduEnquiryBeanArrayList.size() > 0 && stuEduEnquiryBeanArrayList != null) {
                    mainPageEduEnquiryAdapter = new MainPageEduEnquiryAdapter(stuEduEnquiryBeanArrayList, MainActivity.this);
                    try {
                        listViewEduEnquiry.setAdapter(mainPageEduEnquiryAdapter);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    listViewEduEnquiry.setVisibility(View.VISIBLE);
                    llMainEnquiryListElseLayout.setVisibility(View.GONE);
                } else {
                    llMainEnquiryListElseLayout.setVisibility(View.VISIBLE);
                    listViewEduEnquiry.setVisibility(View.GONE);
                }
                //getNoOfApplication();
                getQuoteList();
                //getUpcomingJoiningStu();
            }
        });
    }

     void getQuoteList() {
         quoteBean = new QuoteBean();
         quoteList = new ArrayList<>();
         db.collection(Constants.quote_collection).whereEqualTo("status",1)
                 .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
             @Override
             public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                 for(DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()){
                     quoteBean = doc.toObject(QuoteBean.class);
                     quoteList.add(quoteBean);
                 }

                 getNoOfApplication();
                 //getUpcomingJoiningStu();
             }
         });
    }

    void getUpcomingJoiningStu() {
         admissionUpcomingLis = new ArrayList<>();
         admissionBean = new AdmissionBean();
        db.collection(Constants.admission_collection).whereEqualTo("branchId",signInBean.getBranchId())
                .whereEqualTo("joinDate",currentDate).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                stuUpcomingJoiningBeanArrayList.clear();
                for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()){
                    /*upcomingStudentBean = doc.toObject(StudentBean.class);
                    stuUpcomingJoiningBeanArrayList.add(upcomingStudentBean);*/
                    admissionBean = doc.toObject(AdmissionBean.class);
                    admissionUpcomingLis.add(admissionBean);
                }
                Toast.makeText(MainActivity.this,"size "+admissionUpcomingLis.size(),Toast.LENGTH_SHORT).show();
                /*LinearLayout llUpcomingJoiningListElseLayout = (LinearLayout) findViewById(R.id.tvUpcomingJoiningListElseLayout);
                if (stuUpcomingJoiningBeanArrayList.size() > 0 && stuUpcomingJoiningBeanArrayList != null) {
                    mainPageUpcomingJoiningsAdapter = new MainPageUpcomingJoiningsAdapter(stuUpcomingJoiningBeanArrayList, MainActivity.this);
                    //mainPageEduEnquiryAdapter1 = new MainPageEduEnquiryAdapter(stuUpcomingJoiningBeanArrayList, MainActivity.this);

                    try {
                        listViewUpcomingJoinings.setAdapter(mainPageUpcomingJoiningsAdapter);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    listViewUpcomingJoinings.setVisibility(View.VISIBLE);
                    llUpcomingJoiningListElseLayout.setVisibility(View.GONE);
                } else {
                    llUpcomingJoiningListElseLayout.setVisibility(View.VISIBLE);
                    listViewUpcomingJoinings.setVisibility(View.GONE);
                }
                getNoOfApplication();*/
                fetchUpcomingStu();
            }
        });
    }

     void fetchUpcomingStu() {
         check = 0;
         for(int i=0;i<admissionUpcomingLis.size();i++){
             final int finalI = i;
             db.collection(Constants.student_collection).document(String.valueOf(admissionUpcomingLis.get(i).getStudentId()))
                     .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                 @Override
                 public void onSuccess(DocumentSnapshot documentSnapshot) {
                     upcomingStudentBean = documentSnapshot.toObject(StudentBean.class);
                     upcomingStudentBean.setJoinDate(admissionUpcomingLis.get(finalI).getJoinDate());
                     stuUpcomingJoiningBeanArrayList.add(upcomingStudentBean);
                     check = check + 1;
                     Toast.makeText(MainActivity.this,""+check,Toast.LENGTH_SHORT).show();
                     /*if(check == admissionUpcomingLis.size()){
                         LinearLayout llUpcomingJoiningListElseLayout = (LinearLayout) findViewById(R.id.tvUpcomingJoiningListElseLayout);
                         if (stuUpcomingJoiningBeanArrayList.size() > 0 && stuUpcomingJoiningBeanArrayList != null) {
                             mainPageUpcomingJoiningsAdapter = new MainPageUpcomingJoiningsAdapter(stuUpcomingJoiningBeanArrayList, MainActivity.this);
                             //mainPageEduEnquiryAdapter1 = new MainPageEduEnquiryAdapter(stuUpcomingJoiningBeanArrayList, MainActivity.this);

                             try {
                                 listViewUpcomingJoinings.setAdapter(mainPageUpcomingJoiningsAdapter);
                             } catch (Exception e) {
                                 e.printStackTrace();
                             }
                             listViewUpcomingJoinings.setVisibility(View.VISIBLE);
                             llUpcomingJoiningListElseLayout.setVisibility(View.GONE);
                         } else {
                             llUpcomingJoiningListElseLayout.setVisibility(View.VISIBLE);
                             listViewUpcomingJoinings.setVisibility(View.GONE);
                         }
                         getNoOfApplication();
                     }*/
                 }
             });
         }
    }

    void getNoOfApplication() {
        db.collection(Constants.student_collection).whereEqualTo("branchId",signInBean.getBranchId())
              .addSnapshotListener(new EventListener<QuerySnapshot>() {
                  @Override
                  public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                      applicSize = queryDocumentSnapshots.size();
                      listGridItemsOne.get(1).setTitle("Application(" + (applicSize+1) + ")");
                      gridItemsMainAdapterOne.notifyDataSetChanged();
                  }
              });
        getSizeModules();
    }

    AdapterView.OnItemClickListener onItemClickListenerThree = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (preferences.getInt(AttUtil.shpLoginType, 0) == 2) {
                switch (position) {
                    case 0:
                        retrieveBranch();
                        break;
                    case 1:
                        logout();
                        break;
                }
            }else{
                switch (position) {
                    case 0:
                        logout();
                        break;
                }
            }
        }
    };

    private void retrieveBranch() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose a Branch");
        String options[]= new String[rcvBranchBeanArrayList.size()];
        for(int i=0;i<rcvBranchBeanArrayList.size();i++){
            options[i]=rcvBranchBeanArrayList.get(i).getBranchName();
        }
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override

            public void onClick(DialogInterface dialog, int which) {
                AttUtil.progressDialog(MainActivity.this);
                AttUtil.pd(1);
                getBranches(which);

            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setCancelable(false);
        builder.create().show();
    }

     void getBranches(final int which) {
         db.collection(Constants.branchCollection)
                 .whereEqualTo("userId",userId).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
             @Override
             public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                 branchBeanArrayList.clear();
                 for(DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()){
                     branchBean = doc.toObject(BranchBean.class);
                     branchBeanArrayList.add(branchBean);
                 }
                 signInBean.setBranchId(rcvBranchBeanArrayList.get(which).getBranchId());
                 signInBean.setBranchName(rcvBranchBeanArrayList.get(which).getBranchName());
                 Intent intent = new Intent(MainActivity.this,MainActivity.class);
                 intent.putExtra(AttUtil.signInBean,signInBean);
                 intent.putExtra("adminId",userAdminId);
                 intent.putExtra("userId",userId);
                 intent.putExtra("branchName",rcvBranchBeanArrayList.get(which).getBranchName());
                 intent.putExtra("branchBeanArrayList",branchBeanArrayList);
                 startActivity(intent);
             }
         });
    }

    void logout() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure to logout?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                editor.clear().commit();
                teacherBeanArrayList1.clear();
                courseBeanArrayListActive.clear();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setCancelable(false);
        builder.create().show();
    }


    public void fetchTeacherList(){
        teacherBeanArrayList1 = new ArrayList<>();
        db.collection(Constants.usersCollection).whereEqualTo("branchName",branchName)
                .whereEqualTo("userType",3).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                teacherBeanArrayList1.clear();
                for(DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                    if (doc.exists()) {
                        teacherBean = doc.toObject(TeacherBean.class);
                        teacherBeanArrayList1.add(teacherBean);
                    }
                }
                getSwitchBranches();
            }
        });
    }

    void coursesList() {
         AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
         String options[]= new String[courseBeanArrayListActive.size()];
         for(int i=0;i<courseBeanArrayListActive.size();i++){
             options[i]=courseBeanArrayListActive.get(i).getCourseName();
         }
         builder.setTitle("Choose a course");
         builder.setItems(options, new DialogInterface.OnClickListener() {
             @Override
             public void onClick(DialogInterface dialog, int which) {
                 AttUtil.progressDialog(MainActivity.this);
                 AttUtil.pd(1);
                 getBatches(which);
             }
         });
         builder.create().show();
    }

     void getBatches(final int pos) {
         db.collection(Constants.branchCollection).document(String.valueOf(signInBean.getBranchId()))
                 .collection(Constants.branch_course_collection).
                 document(String.valueOf(courseBeanArrayListActive.get(pos).getBranCourId()))
                 .collection(Constants.batch_section_collection)
                 .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
             @Override
             public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                 batchListGroup.clear();
                 for(DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()){
                     batchBean = doc.getDocument().toObject(BatchBean.class);
                     batchListGroup.add(batchBean);
                 }

                 startActivityForResult(new Intent(MainActivity.this, BatchListActivity.class)
                         .putExtra(AttUtil.BATCH_LIST,batchListGroup)
                         .putExtra(AttUtil.KEY_COURSE_ARRAYLIST,courseBeanArrayListActive)
                         .putExtra("branchName",branchName),850);
                 AttUtil.pd(0);
             }
         });

    }


    void getBatchesSection(){
        batchList = new ArrayList<>();
        if(courseBeanArrayListActive.size()>0){
            for( int i=0;i<courseBeanArrayListActive.size();i++){
                final int j = i;
                db.collection(Constants.branchCollection).document(String.valueOf(signInBean.getBranchId()))
                        .collection(Constants.branch_course_collection)
                        .document(String.valueOf(courseBeanArrayListActive.get(i).getBranCourId()))
                        .collection(Constants.batch_section_collection).get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()){
                            if(doc.exists()){
                                batchBean = doc.toObject(BatchBean.class);
                                batchList.add(batchBean);
                            }
                        }
                        check = check+j;
                        if(check==courseBeanArrayListActive.size()){
                            fetchTeacherList();
                        }
                    }
                });
            }
            if(batchList.size() == 0){
                fetchTeacherList();
            }
        }
    }

     void getLateJoinings() {
         db.collection(Constants.admission_collection).whereEqualTo("batchId",0)
                 .whereEqualTo("branchId",signInBean.getBranchId()).get()
                 .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
             @Override
             public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                 admissionBeanArrayList.clear();
                 admissionFeeNotAllocatedList.clear();
                 for(DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()){
                     feeCategorySelected = doc.getLong("feeCategorySelected").intValue();
                      admissionBean = doc.toObject(AdmissionBean.class);
                     if(feeCategorySelected>0){
                         admissionBeanArrayList.add(admissionBean);
                     }else {
                         admissionFeeNotAllocatedList.add(admissionBean);
                     }
                 }

                 if(admissionFeeNotAllocatedList.size()>0){
                     listGridItemsOne.get(6).setTitle("Assign Fees Plan(" + admissionFeeNotAllocatedList.size() + ")");
                     gridItemsMainAdapterOne.notifyDataSetChanged();
                 }
                 if(admissionBeanArrayList.size()>0){
                     AttUtil.laterJoinings = admissionBeanArrayList.size();
                 }else{
                     AttUtil.laterJoinings = 0;
                 }
                 getBatchesSection();
             }
         });
    }


    @Override
    protected void onRestart() {
        super.onRestart();
       /* teacherBeanArrayList1.clear();
        progressDialog = new SpotsDialog(MainActivity.this, R.style.Custom);
        progressDialog.show();
        fetchTeacherList();
        progressDialog.dismiss();*/

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 501 && resultCode == 601){
            rcvBatchBean = (BatchBean) data.getSerializableExtra("batchBean");
            int position = data.getIntExtra("position",0);
            Toast.makeText(MainActivity.this,""+rcvBatchBean.getActiveStudents(),Toast.LENGTH_SHORT).show();
            for(int i=0;i<batchList.size();i++){
                if(batchList.get(i).getBatch_title().equals(rcvBatchBean.getBatch_title())){
                    batchList.get(i).setActiveStudents(rcvBatchBean.getActiveStudents());
                    break;
                }
            }

            admissionBeanArrayList.remove(position);
            if(admissionBeanArrayList.size()>0){
                AttUtil.laterJoinings = admissionBeanArrayList.size();
            }else{
                AttUtil.laterJoinings = 0;
            }
        }

        if(requestCode == 501 && resultCode == 5001){
            admissionBeanArrayList = (ArrayList<AdmissionBean>) data.getSerializableExtra(AttUtil.KEY_STUDENT_LATE_JOINING_ARRAYLIST);
        }

        if(requestCode == 250 && resultCode == 350){
            BatchBean addBatch = (BatchBean) data.getSerializableExtra(AttUtil.KEY_BATCH);
            batchList.add(addBatch);
            listGridItemsOne.get(4).setTitle("Section("+batchList.size()+")");
            gridItemsMainAdapterOne.notifyDataSetChanged();
        }

        if(requestCode == 850 && resultCode == 950){
            BatchBean updatedBatch = (BatchBean) data.getSerializableExtra(AttUtil.UPDATED_BATCH);
            int check = data.getIntExtra(AttUtil.CHECK,0);

            if(check != 1){
                for(int i=0;i<batchList.size();i++){
                    if(batchList.get(i).getBatchId() == updatedBatch.getBatchId()){
                        batchList.get(i).setBatch_title(updatedBatch.getBatch_title());
                        batchList.get(i).setBatch_seat(updatedBatch.getBatch_seat());
                        batchList.get(i).setCoursetitle(updatedBatch.getCoursetitle());
                        break;
                    }
                }
            }else {
                getBatchesSection();
            }

        }

        if(requestCode == AttUtil.REQ_CODE && resultCode == 125){
            StudentBean bean = (StudentBean) data.getSerializableExtra(AttUtil.TAG_STUDENTBEAN);
            AdmissionBean admissionBean = (AdmissionBean) data.getSerializableExtra(AttUtil.TAG_ADMISSIONBEAN);
            Toast.makeText(MainActivity.this,""+admissionBean.getFeeCategorySelected(),Toast.LENGTH_SHORT).show();
            if(admissionBean.getFeeCategorySelected() > 0){
               // studentBeanArrayList.add(bean);
                admissionBeanArrayList.add(admissionBean);
                if(admissionBeanArrayList.size()>0){
                    AttUtil.laterJoinings = admissionBeanArrayList.size();
                }else{
                    AttUtil.laterJoinings = 0;
                }
            }else{
               // studentFeeNotAllocatedList.add(bean);
                admissionFeeNotAllocatedList.add(admissionBean);
                if(admissionFeeNotAllocatedList.size()>0){
                    if(preferences.getInt(AttUtil.shpLoginType,0) == 2){
                        listGridItemsOne.get(6).setTitle("Assign Fees Plan(" + admissionFeeNotAllocatedList.size() + ")");
                    }
                    gridItemsMainAdapterOne.notifyDataSetChanged();
                }
            }

        }

        if(requestCode == AttUtil.REQ_CODE && resultCode == 9001){
            StudentBean bean = (StudentBean) data.getSerializableExtra(AttUtil.TAG_STUDENTBEAN);

        }

        if(requestCode == AttUtil.REQ_CODE && resultCode == 225){
            UserBean userBean = (UserBean) data.getSerializableExtra("userBean");
            if(userBean.getUserType() == 2){
                counsellorBeanArrayList.add(userBean);
            }

            if(userBean.getUserType() == 3){
                AttUtil.progressDialog(this);
                AttUtil.pd(1);
                getNewTeacherList();
            }
        }

        if(requestCode == 820 && resultCode == 920){
            if(preferences.getInt(AttUtil.shpLoginType,0) == 2){
                StudentBean studentBean = (StudentBean) data.getSerializableExtra(AttUtil.TAG_STUDENTBEAN);
                AdmissionBean admissionBean = (AdmissionBean) data.getSerializableExtra(AttUtil.TAG_ADMISSIONBEAN);
                int position=0;
                admissionBeanArrayList.add(admissionBean);
                for(int i=0;i<admissionFeeNotAllocatedList.size();i++){
                    if(admissionFeeNotAllocatedList.get(i).getAdmissionId() == admissionBean.getAdmissionId()){
                        position = i;
                        break;
                    }
                }
                admissionFeeNotAllocatedList.remove(position);
                AttUtil.laterJoinings = admissionBeanArrayList.size();
                if(admissionFeeNotAllocatedList.size()>0){
                    listGridItemsOne.get(6).setTitle("Assign Fees Plan(" + admissionFeeNotAllocatedList.size() + ")");
                    gridItemsMainAdapterOne.notifyDataSetChanged();
                }else{
                    listGridItemsOne.get(6).setTitle("Assign Fees Plan(" + 0 + ")");
                    gridItemsMainAdapterOne.notifyDataSetChanged();
                }
            }else{
                StudentBean studentBean = (StudentBean) data.getSerializableExtra(AttUtil.TAG_STUDENTBEAN);
                AdmissionBean admissionBean = (AdmissionBean) data.getSerializableExtra(AttUtil.TAG_ADMISSIONBEAN);
                int position=0;
                for(int i=0;i<admissionFeeNotAllocatedList.size();i++){
                    if(admissionFeeNotAllocatedList.get(i).getAdmissionId() == admissionBean.getAdmissionId()){
                        position = i;
                        break;
                    }
                }
                admissionFeeNotAllocatedList.remove(position);
                if(admissionFeeNotAllocatedList.size()>0){
                    listGridItemsOne.get(2).setTitle("Assign Fees Plan(" + admissionFeeNotAllocatedList.size() + ")");
                    gridItemsMainAdapterOne.notifyDataSetChanged();
                }else{
                    listGridItemsOne.get(2).setTitle("Assign Fees Plan(" + 0 + ")");
                    gridItemsMainAdapterOne.notifyDataSetChanged();
                }
            }


        }

        if(requestCode == 850 && resultCode == 965){
            int callTeacher = data.getIntExtra("callTeacher",0);

            if(callTeacher == 1){
                AttUtil.progressDialog(this);
                AttUtil.pd(1);
                getNewTeacherList();
            }
        }

        if(requestCode == 501 && resultCode == 901){
            batchList.clear();
            batchList = (ArrayList<BatchBean>) data.getSerializableExtra(AttUtil.BATCH_LIST);
        }

    }

    void getNewTeacherList(){
        db.collection(Constants.usersCollection).whereEqualTo("branchName",branchName)
                .whereEqualTo("userType",3).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                teacherBeanArrayList1.clear();
                for(DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                    if (doc.exists()) {
                        teacherBean = doc.toObject(TeacherBean.class);
                        teacherBeanArrayList1.add(teacherBean);
                    }
                }
                listGridItemsOne.get(7).setTitle("Teachers(" + teacherBeanArrayList1.size() + ")");
                gridItemsMainAdapterOne.notifyDataSetChanged();
                AttUtil.pd(0);

            }
        });
    }

    void getDate(int year, int monthOfYear, int dayOfMonth) {
        monthOfYear = monthOfYear + 1;
        if (dayOfMonth <= 9) {
            cDayString = "0" + dayOfMonth;
        } else {
            cDayString = String.valueOf(dayOfMonth);
        }
        if (monthOfYear <= 9) {
            cMonthString = "0" + monthOfYear;
        } else {
            cMonthString = String.valueOf(monthOfYear);
        }
        //datefor = year+"-"+cMonthString+"-"+cDayString;
        //enquiryBean.setDate(year+"-"+cMonthString+"-"+cDayString);
        currentDate = year + "-" + cMonthString + "-" + cDayString;
    }
}
