package com.auribisesmyplayschool.myplayschool.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.auribisesmyplayschool.myplayschool.Helper.Constants;
import com.auribisesmyplayschool.myplayschool.R;
import com.auribisesmyplayschool.myplayschool.adapter.ApproveAdapter;
import com.auribisesmyplayschool.myplayschool.bean.AdmissionBean;
import com.auribisesmyplayschool.myplayschool.bean.StudentBean;
import com.auribisesmyplayschool.myplayschool.classes.AttUtil;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class StudentFeeListActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    ListView listViewStudents;
    ArrayList<StudentBean> studentList;
    ApproveAdapter studentListAdapter;
    Intent intent;
    SharedPreferences pref;
    int batchId,branchId,position;
    boolean approvedStudents;
    ArrayList<StudentBean> joiningDateList,joinedStudentsList;
    StudentBean studentBean;
    AdmissionBean admissionBean;
    ArrayList<AdmissionBean> admissionBeanArrayList;
    FirebaseFirestore db;
    int check;

    void initViews(){
        db = FirebaseFirestore.getInstance();
        studentBean = new StudentBean();
        studentList = new ArrayList<>();
        pref=getSharedPreferences(AttUtil.shpREG,MODE_PRIVATE);
        intent=getIntent();
        batchId=intent.getIntExtra(AttUtil.KEY_BATCH_ID, 0);
        branchId=pref.getInt(AttUtil.shpBranchId,0);
        //studentList = (ArrayList<StudentBean>) intent.getSerializableExtra("studentList");
        admissionBeanArrayList = (ArrayList<AdmissionBean>) intent.getSerializableExtra("admissionList");
        joiningDateList=new ArrayList<>();
        joinedStudentsList=new ArrayList<>();
        if(getIntent().getIntExtra("approved_students",0)==1)
            approvedStudents=true;
        listViewStudents=(ListView)findViewById(R.id.listViewStudents);
        listViewStudents.setOnItemClickListener(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_fee_list);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initViews();
        if(admissionBeanArrayList.size()>0){
            AttUtil.progressDialog(this);
            AttUtil.pd(1);
            fetchStudentList();
        }else{
            Toast.makeText(StudentFeeListActivity.this,"No record found",Toast.LENGTH_SHORT).show();
            finish();
        }

        //retrieveStudents();
        //studentListAdapter = new ApproveAdapter(this, R.layout.adapter_approve_students,studentList);
        //listViewStudents.setAdapter(studentListAdapter);
    }

     void fetchStudentList() {
        check = 0;
        for(int i=0;i<admissionBeanArrayList.size();i++){
            final int finalI = i;
            db.collection(Constants.student_collection).document(String.valueOf(admissionBeanArrayList.get(i).getStudentId()))
                    .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    studentBean = documentSnapshot.toObject(StudentBean.class);
                    studentBean.setApproved(admissionBeanArrayList.get(finalI).getApproved());
                    studentBean.setJoinDate(admissionBeanArrayList.get(finalI).getJoinDate());
                    studentBean.setAdmStatus(admissionBeanArrayList.get(finalI).getAdmStatus());
                    studentBean.setCourseName(admissionBeanArrayList.get(finalI).getCourseName());
                    studentBean.setDate(admissionBeanArrayList.get(finalI).getDate());
                    studentBean.setBatchId(admissionBeanArrayList.get(finalI).getBatchId());
                    studentBean.setBatch_title(admissionBeanArrayList.get(finalI).getBatch_title());
                    studentList.add(studentBean);
                    check = check + 1;

                    if(check == admissionBeanArrayList.size()){
                        AttUtil.pd(0);
                        studentListAdapter = new ApproveAdapter(StudentFeeListActivity.this, R.layout.adapter_approve_students,studentList);
                        listViewStudents.setAdapter(studentListAdapter);
                    }
                }
            });
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        this.position=position;
        studentBean = studentList.get(position);

        for(int i=0;i<admissionBeanArrayList.size();i++){
            if(admissionBeanArrayList.get(i).getStudentId() == studentBean.getStudentId()){
                admissionBean = admissionBeanArrayList.get(i);
                break;
            }
        }


        if(approvedStudents){
            /*Intent intent=new Intent(this,FeeRecordActivity.class);
            intent.putExtra("admissionId",studentList.get(position).getAdmissionId());
            intent.putExtra("studentId",studentList.get(position).getStudentId());
            intent.putExtra("feeCategory",studentList.get(position).getFeeCategorySelected());
            startActivity(intent);*/
        }else{
            Intent intent=new Intent(this,FeeSelectActivty.class);
            intent.putExtra("studentId",studentBean.getStudentId());
            intent.putExtra(AttUtil.TAG_ADMISSIONBEAN,admissionBean);
            intent.putExtra("studentbean",studentBean);
            intent.putExtra("activitySignal",1);
            startActivityForResult(intent,301);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home)
            finish();
        /*if(item.getItemId()==1){
            loadUpcomingEvents();
        }if(item.getItemId()==2){
            loadJoinedStudents();
        }if(item.getItemId()==3){
            askForSortOptions();
        }*/

        return super.onOptionsItemSelected(item);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==301 && resultCode==201){
            studentBean = (StudentBean) data.getSerializableExtra(AttUtil.TAG_STUDENTBEAN);
            admissionBean = (AdmissionBean) data.getSerializableExtra(AttUtil.TAG_ADMISSIONBEAN);
            setResult(920,new Intent().putExtra(AttUtil.TAG_STUDENTBEAN,studentBean)
            .putExtra(AttUtil.TAG_ADMISSIONBEAN,admissionBean));
            studentList.remove(position);
            studentListAdapter.notifyDataSetChanged();
            if(studentList.size()==0)
                finish();
        }
    }


}
