package com.auribisesmyplayschool.myplayschool.adminApp.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.auribisesmyplayschool.myplayschool.Helper.Constants;
import com.auribisesmyplayschool.myplayschool.R;
import com.auribisesmyplayschool.myplayschool.adminApp.bean.BranCourBean;
import com.auribisesmyplayschool.myplayschool.adminApp.bean.BranchBean;
import com.auribisesmyplayschool.myplayschool.adminApp.bean.CourseBean;
import com.auribisesmyplayschool.myplayschool.adminApp.classes.AdminUtil;
import com.auribisesmyplayschool.myplayschool.bean.AdminBean;
import com.auribisesmyplayschool.myplayschool.classes.AttUtil;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;

import dmax.dialog.SpotsDialog;

public class BranchCourseActivity extends AppCompatActivity {
    EditText edCourseFees,edCourseDuration,edCourseHour,edCourseDesc;
    Button btnAddBranchCourse;
    Spinner spinnerBranch,spinnerCourse;
    int posBranch=0,posCourse=0,updateBranchPos=0,updateCoursePos=0;
    boolean flag;
    TextView txtbCourseName;
    SharedPreferences preferences;
    int responseSignal=0,reqCode=0;
    ArrayList<String> arrayListBranchName,arrayListCourseName;
    ArrayList<BranchBean> branchBeanArrayList;
    ArrayList<CourseBean> courseBeanArrayList ;
    ArrayList<BranCourBean> branCourArrayList;
    BranCourBean branCourBean;
    boolean updateFlag=false;
    AdminBean adminBean;
    FirebaseFirestore db;
    CourseBean courseBean;
    int branch_course_id;
    ArrayList<Integer> listId;
    int check;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_branch_course);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        preferences = getSharedPreferences(AttUtil.shpREG, Context.MODE_PRIVATE);
        db = FirebaseFirestore.getInstance();
        Intent rcvi = getIntent();
        adminBean = (AdminBean) rcvi.getSerializableExtra(AdminUtil.TAG_ADMIN_BEAN);
        //branch_id = rcvi.getIntExtra(AdminUtil.branchId,0);
        if(rcvi.hasExtra(AdminUtil.TAG_BRANCOUR)){
            updateFlag = true;
        }
        initViews();
        if(updateFlag){
            branchBeanArrayList = (ArrayList<BranchBean>) rcvi.getSerializableExtra(AdminUtil.TAG_BRANCHARRAYLIST);
            branCourBean = (BranCourBean) rcvi.getSerializableExtra(AdminUtil.TAG_BRANCOUR);
            edCourseDuration.setText(branCourBean.getCourseDuration()+"");
            edCourseFees.setText(branCourBean.getCourseFees()+"");
            edCourseHour.setText(branCourBean.getCourseHours()+"");
            edCourseDesc.setText(branCourBean.getCourseDesc()+"");
            arrayListBranchName.add("--Select a Branch--");
            for(int i=0;i<branchBeanArrayList.size();i++){
                arrayListBranchName.add(branchBeanArrayList.get(i).getBranchName());
                if(branchBeanArrayList.get(i).getBranchId()==branCourBean.getBranchId()){
                    updateBranchPos = i+1;
                    posBranch = updateBranchPos-1;
                }
            }
            spinnerBranch.setAdapter(new ArrayAdapter(this,android.R.layout.simple_dropdown_item_1line,
                    arrayListBranchName));
            spinnerBranch.setSelection(updateBranchPos);
        }else{
            branchBeanArrayList = (ArrayList<BranchBean>) rcvi.getSerializableExtra(AdminUtil.TAG_BRANCHARRAYLIST);
            arrayListBranchName.add("--Select a Branch--");
            for(int i=0;i<branchBeanArrayList.size();i++){
                arrayListBranchName.add(branchBeanArrayList.get(i).getBranchName());
            }
            spinnerBranch.setAdapter(new ArrayAdapter(this,android.R.layout.simple_dropdown_item_1line,
                    arrayListBranchName));
        }
        AttUtil.progressDialog(this);
        AttUtil.pd(1);
        get_course();



    }

    public void get_course() {
        db.collection(Constants.adminCollection).document(String.valueOf(adminBean.getAdminId()))
                .collection(Constants.coursesCollection).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
                    if (!queryDocumentSnapshots.getDocuments().isEmpty()) {
                        courseBean = doc.getDocument().toObject(CourseBean.class);
                        courseBeanArrayList.add(courseBean);
                    }
                }
                if(courseBeanArrayList.size()>0&&courseBeanArrayList!=null){
                    arrayListCourseName.clear();
                    arrayListCourseName.add("--Select Class--");
                    for(int i = 0;i<courseBeanArrayList.size();i++){
                        arrayListCourseName.add(courseBeanArrayList.get(i).getCourseName());
                        if(updateFlag){
                            if(courseBeanArrayList.get(i).getCourseId()==branCourBean.getCourseId()){
                                updateCoursePos = i+1;
                                posCourse = updateCoursePos-1;
                            }
                        }
                    }
                    spinnerCourse.setAdapter(new ArrayAdapter(BranchCourseActivity.this,
                            android.R.layout.simple_dropdown_item_1line, arrayListCourseName));
                    if(updateFlag){
                        spinnerCourse.setSelection(updateCoursePos);
                    }
                }else{
                    arrayListCourseName.clear();
                    arrayListCourseName.add("--No Class--");
                    spinnerCourse.setAdapter(new ArrayAdapter(BranchCourseActivity.this,
                            android.R.layout.simple_dropdown_item_1line, arrayListCourseName));
                }
                AttUtil.pd(0);
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    public void initViews(){
        branCourBean = new BranCourBean();
        arrayListCourseName = new ArrayList<>();
        arrayListBranchName = new ArrayList<>();
        courseBean = new CourseBean();
        //branchBeanArrayList = new ArrayList<>();
        courseBeanArrayList = new ArrayList<>();
        branCourArrayList = new ArrayList<>();
        edCourseFees = (EditText) findViewById(R.id.edCourseFees);
        edCourseDuration = (EditText) findViewById(R.id.edCourseDuration);
        edCourseHour = (EditText) findViewById(R.id.edCourseHour);
        edCourseDesc = (EditText) findViewById(R.id.edCourseDesc);
        spinnerBranch = (Spinner) findViewById(R.id.spinnerBranches);
        spinnerCourse = (Spinner) findViewById(R.id.spinnerCourses);
        btnAddBranchCourse = (Button) findViewById(R.id.btnAddBranchCourse);
        txtbCourseName = (TextView) findViewById(R.id.textView14);

        //Set Listeners
        spinnerBranch.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                posBranch = position;
                //get_branch_course_id();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerCourse.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                posCourse = position;

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        btnAddBranchCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkEmptyValues()){
                        if(AdminUtil.isNetworkConnected(BranchCourseActivity.this)){
                            if(!updateFlag){
                                AttUtil.progressDialog(BranchCourseActivity.this);
                                AttUtil.pd(1);
                                checkIfAlreadyAssigned();
                                //getLastBranCourId();
                                //add_Branch_Course();
                            }else{
                                AttUtil.progressDialog(BranchCourseActivity.this);
                                AttUtil.pd(1);
                                checkIfAlreadyAssigned();
                            }

                        }else{
                            // addNetConnect();
                        }


                }
            }
        });
    }

     void checkUpdateDetails() {
        if(branchBeanArrayList.get(posBranch-1).getBranchId() == branchBeanArrayList.get(updateBranchPos - 1).getBranchId()){
            updateDetails();
        }else{
            updateDetailsToNewBranch();
        }
    }

     void updateDetailsToNewBranch() {
        db.collection(Constants.branchCollection).document(String.valueOf(branCourBean.getBranchId()))
                .collection(Constants.branch_course_collection).document(String.valueOf(branCourBean.getBranCourId()))
                .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                branCourBean.setBranchId(branchBeanArrayList.get(posBranch-1).getBranchId());
                branCourBean.setCourseName(courseBeanArrayList.get(posCourse-1).getCourseName());
                branCourBean.setCourseId(courseBeanArrayList.get(posCourse-1).getCourseId());
                //addToNewDB();
                getLastId();
            }
        });
    }

     void getLastId() {
        listId = new ArrayList<>();
         db.collection(Constants.branchCollection).document(String.valueOf(branCourBean.getBranchId()))
                 .collection(Constants.branch_course_collection).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
             @Override
             public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                 for(DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()){
                     branch_course_id = doc.getLong("branCourId").intValue();
                     listId.add(branch_course_id);
                 }

                 if(listId.size()>0){
                     branch_course_id = Collections.max(listId);
                     branch_course_id = branch_course_id + 1;
                 }else{
                     branch_course_id = 1;
                 }
                 addToNewBranch();
             }
         });
    }

    void addToNewBranch() {
        branCourBean.setBranCourId(branch_course_id);
        db.collection(Constants.branchCollection).document(String.valueOf(branCourBean.getBranchId()))
                .collection(Constants.branch_course_collection).document(String.valueOf(branCourBean.getBranCourId()))
                .set(branCourBean).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                AttUtil.pd(0);
                Toast.makeText(BranchCourseActivity.this,courseBeanArrayList.get(posCourse-1).getCourseName()+
                        " is update in "+branchBeanArrayList.get(posBranch-1).getBranchName()+
                        " branch.", Toast.LENGTH_SHORT).show();
                Intent intent=new Intent();
                intent.putExtra("signal",1);
                setResult(AdminUtil.RES_CODE,intent);
                finish();
            }
        });
    }

    void updateDetails() {
        branCourBean.setCourseName(courseBeanArrayList.get(posCourse-1).getCourseName());
        branCourBean.setCourseId(courseBeanArrayList.get(posCourse-1).getCourseId());

        db.collection(Constants.branchCollection).document(String.valueOf(branCourBean.getBranchId()))
                .collection(Constants.branch_course_collection).document(String.valueOf(branCourBean.getBranCourId()))
                .update("courseName",branCourBean.getCourseName(),"courseId",branCourBean.getCourseId())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        AttUtil.pd(0);
                        Toast.makeText(BranchCourseActivity.this,courseBeanArrayList.get(posCourse-1).getCourseName()+
                                " is update in "+branchBeanArrayList.get(posBranch-1).getBranchName()+
                                " branch.", Toast.LENGTH_SHORT).show();
                        Intent intent=new Intent();
                        intent.putExtra(AdminUtil.TAG_BRANCOUR,branCourBean);
                        setResult(AdminUtil.RES_CODE,intent);
                        finish();
                    }
                });
    }

    void checkIfAlreadyAssigned(){
        check = 1;
        db.collection(Constants.branchCollection).document(String.valueOf(branchBeanArrayList.get(posBranch-1).getBranchId()))
                .collection(Constants.branch_course_collection).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()){
                    if(doc.getLong("courseId").intValue() == courseBeanArrayList.get(posCourse-1).getCourseId()){
                        check = 0;
                        break;
                    }else{
                        check = 1;
                    }
                }


                if(check == 0){
                    AttUtil.pd(0);
                    if(!updateFlag){
                        spinnerBranch.setSelection(0);
                        spinnerCourse.setSelection(0);
                    }
                    Toast.makeText(BranchCourseActivity.this,courseBeanArrayList.get(posCourse-1).getCourseName()+
                                   " is already assigned to "+branchBeanArrayList.get(posBranch-1).getBranchName()+".", Toast.LENGTH_SHORT).show();
                }else{
                    if(!updateFlag){
                        getLastBranCourId();
                    }else{
                        checkUpdateDetails();
                    }

                }
            }
        });

    }

     void getLastBranCourId() {
        listId = new ArrayList<>();
        db.collection(Constants.branchCollection).document(String.valueOf(branchBeanArrayList.get(posBranch-1).getBranchId()))
                .collection(Constants.branch_course_collection).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()){
                    if (!queryDocumentSnapshots.isEmpty()){
                        branch_course_id = doc.getLong("branCourId").intValue();
                        listId.add(branch_course_id);
                    }else{
                        branch_course_id = 1;
                    }
                }

                if(listId.size()>0){
                    branch_course_id = Collections.max(listId);
                    branch_course_id = branch_course_id + 1;
                }else {
                    branch_course_id = 1;
                }
                setDataToBean();
            }
        });
    }

     void setDataToBean() {
         branCourBean.setBranchId(branchBeanArrayList.get(posBranch-1).getBranchId());
         branCourBean.setCourseId(courseBeanArrayList.get(posCourse-1).getCourseId());
         branCourBean.setCourseFees(edCourseFees.getText().toString().trim());
         branCourBean.setCourseDuration(edCourseDuration.getText().toString().trim());
         branCourBean.setCourseHours(edCourseHour.getText().toString().trim());
         branCourBean.setCourseDesc(edCourseDesc.getText().toString().trim());
         branCourBean.setCourseName(courseBeanArrayList.get(posCourse-1).getCourseName());
         branCourBean.setCourseStatus(1);
         branCourBean.setAdminId(adminBean.getAdminId());
         branCourBean.setBranCourId(branch_course_id);
         add_Branch_Course();
    }

    public void get_branch_course_id(){
        AttUtil.progressDialog(BranchCourseActivity.this);
        AttUtil.pd(1);
        db.collection(Constants.branchCollection).document(String.valueOf(posBranch))
                .collection(Constants.branch_course_collection).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
                    if (doc.getType() == DocumentChange.Type.ADDED) {
                        if(!queryDocumentSnapshots.isEmpty()){
                            //size = queryDocumentSnapshots.size();
                            branCourBean = doc.getDocument().toObject(BranCourBean.class);
                            branch_course_id = branCourBean.getBranCourId();
                            branCourArrayList.add(branCourBean);

                        }else {
                            branch_course_id=0;
                        }
                    }
                }
                AttUtil.pd(0);
            }
        });
    }

    /*public boolean checkIfAlreadyAssigned(){
        for(int i=0;i<branch_course_id;i++){
            if (branCourArrayList.get(i).getCourseName()==courseBeanArrayList.get(posCourse).getCourseName()){
                 return false;
            }else
                return true;
        }
        return false;
    }*/

    public void add_Branch_Course() {
      // if(checkIfAlreadyAssigned()){
           db.collection(Constants.branchCollection).document(String.valueOf(branchBeanArrayList.get(posBranch-1).getBranchId()))
                   .collection(Constants.branch_course_collection).document(String.valueOf(branCourBean.getBranCourId()))
                   .set(branCourBean).addOnSuccessListener(new OnSuccessListener<Void>() {
                       @Override
                       public void onSuccess(Void aVoid) {
                           AttUtil.pd(0);
                           Toast.makeText(BranchCourseActivity.this,courseBeanArrayList.get(posCourse-1).getCourseName()+
                                   " is assigned to "+branchBeanArrayList.get(posBranch-1).getBranchName()+".", Toast.LENGTH_SHORT).show();
                           spinnerBranch.setSelection(0);
                           spinnerCourse.setSelection(0);
                       }
                   })
                   .addOnFailureListener(new OnFailureListener() {
                       @Override
                       public void onFailure(@NonNull Exception e) {

                       }
                   });
       //}else{
         //  Toast.makeText(BranchCourseActivity.this,courseBeanArrayList.get(posCourse-1).getCourseName()+
           //        " is already assigned to "+branchBeanArrayList.get(posBranch-1).getBranchName()+".", Toast.LENGTH_SHORT).show();
       }

    public boolean checkEmptyValues(){
        boolean flag = true;
        String msg="Error:";
//        if(edCourseFees.getText().toString().length() == 0 ) {
//            msg = msg+"\nInvalid Course Fee";
//            flag = false;
//        }else {
//            edCourseFees.setError(null);
//        }
//        if(edCourseDuration.getText().toString().length() == 0 ) {
//            msg = msg+"\nEmpty Course Duration";
//            flag = false;
//        }else {
//            edCourseDuration.setError(null);
//        }
//        if(edCourseHour.getText().toString().length() == 0 ) {
//            msg = msg+"\nEmpty Course Daily Hours";
//            flag = false;
//        }else {
//            edCourseHour.setError(null);
//        }
//        if(edCourseDesc.getText().toString().length() == 0 ) {
//            msg = msg+"\nEmpty Course Daily Hours";
//            flag = false;
//        }else {
//            edCourseDesc.setError(null);
//        }
        /*if(edCourseDiscount.getText().toString().length() == 0 ) {
            edCourseDiscount.setError("Kindly Enter Course Discount");
            msg = msg+"\nEmpty Course Discount";
            flag = false;
        }else {
            edCourseDiscount.setError(null);
        }*/
        if(posBranch==0){
            msg = msg+"\nSelect Branch";
            flag = false;
        }
        if(posCourse==0){
            msg = msg+"\nSelect Class";
            flag = false;
        }
        if(!flag){
            Toast.makeText(BranchCourseActivity.this,msg, Toast.LENGTH_LONG).show();
        }
        return flag;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
