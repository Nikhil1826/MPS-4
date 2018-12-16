package com.auribisesmyplayschool.myplayschool.adminApp.activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.auribisesmyplayschool.myplayschool.Helper.Constants;
import com.auribisesmyplayschool.myplayschool.R;
import com.auribisesmyplayschool.myplayschool.adminApp.adapter.BranchCourseAdapter;
import com.auribisesmyplayschool.myplayschool.adminApp.bean.BranCourBean;
import com.auribisesmyplayschool.myplayschool.adminApp.bean.BranchBean;
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

import dmax.dialog.SpotsDialog;

public class ManageBranchCourseActivity extends AppCompatActivity {
    ListView listView;
    ArrayList<BranCourBean> branCourBeanArrayList;
    BranchCourseAdapter branchCourseAdapter;
    String courseDesc,cStatus;
    Intent i;
    BranCourBean branCourBean;
    SharedPreferences preferences;
    int pos,branchId;
    FirebaseFirestore db;
    AdminBean adminBean;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_branch_course);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        AttUtil.progressDialog(this);
        preferences = getSharedPreferences(AttUtil.shpREG, Context.MODE_PRIVATE);
        db = FirebaseFirestore.getInstance();
        initViews();
        i = getIntent();
        branchId = i.getIntExtra(AdminUtil.branchId,0);
        adminBean = (AdminBean) i.getSerializableExtra(AdminUtil.TAG_ADMIN_BEAN);
        AttUtil.pd(1);
        get_branchCourse();
    }

    public void initViews(){
        listView = (ListView) findViewById(R.id.lvBranCourse);
        branCourBean = new BranCourBean();
        branCourBeanArrayList = new ArrayList<>();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                pos = position;
                branCourBean = branCourBeanArrayList.get(position);
                courseDesc = branCourBeanArrayList.get(position).getCourseDesc();

                if(branCourBean.getCourseStatus() == 1){
                    cStatus = "Deactivate";
                } else {
                    cStatus = "Activate";
                }

                show_options();
            }
        });
    }

    public void show_options(){
        AlertDialog.Builder build = new AlertDialog.Builder(this);
        String[] options = {"Course Description","Update Course's Details",cStatus};

        build.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch(which){
                    case 0:
                        displayDesc();
                        break;
                    case 1:
                        Intent intent = new Intent(ManageBranchCourseActivity.this,BranchCourseActivity.class);
                        intent.putExtra(AdminUtil.TAG_BRANCOUR,branCourBean);
                        intent.putExtra(AdminUtil.TAG_BRANCHARRAYLIST,
                                (ArrayList<BranchBean>)i.getSerializableExtra(AdminUtil.TAG_BRANCHARRAYLIST));
                        intent.putExtra(AdminUtil.TAG_ADMIN_BEAN,adminBean);
                        startActivityForResult(intent,AdminUtil.REQ_CODE);
                        break;
                    case 2:
                        if (AdminUtil.isNetworkConnected(ManageBranchCourseActivity.this)){
                            AttUtil.progressDialog(ManageBranchCourseActivity.this);
                            AttUtil.pd(1);
                            start_deactivate();
                        }

                        //else
                            //activateNetConnect();
                        break;
                    default:
                        break;
                }
            }
        });

        build.create().show();
    }

     void start_deactivate() {
        if(branCourBean.getCourseStatus() == 1){
            branCourBean.setCourseStatus(0);
        }else{
            branCourBean.setCourseStatus(1);
        }

         db.collection(Constants.branchCollection).document(String.valueOf(branchId))
                 .collection(Constants.branch_course_collection).document(String.valueOf(branCourBean.getBranCourId()))
                 .update("courseStatus",branCourBean.getCourseStatus()).addOnSuccessListener(new OnSuccessListener<Void>() {
             @Override
             public void onSuccess(Void aVoid) {
                 AttUtil.pd(0);
                 Toast.makeText(ManageBranchCourseActivity.this,"Course status changed",Toast.LENGTH_SHORT).show();
                 branchCourseAdapter.notifyDataSetChanged();
             }
         });

    }


    public void displayDesc(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        if(!courseDesc.isEmpty()){
            alertDialogBuilder.setMessage(courseDesc);
        }else{
            alertDialogBuilder.setMessage("N/A");
        }


        alertDialogBuilder.setPositiveButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {

            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialogBuilder.setCancelable(false);
        alertDialog.show();
    }

    public void get_branchCourse(){
        db.collection(Constants.branchCollection).document(String.valueOf(branchId))
                .collection(Constants.branch_course_collection).get().
                addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                        if(!queryDocumentSnapshots.isEmpty()){
                            branCourBean = doc.toObject(BranCourBean.class);
                            branCourBeanArrayList.add(branCourBean);

                        }
                }

                AttUtil.pd(0);
                if(branCourBeanArrayList.size()>0){
                    branchCourseAdapter = new BranchCourseAdapter(ManageBranchCourseActivity.this,
                            R.layout.admin_adapter_branch_course_listitem, branCourBeanArrayList);
                    listView.setAdapter(branchCourseAdapter);
                    getSupportActionBar().setTitle("Course's" + " (" + branCourBeanArrayList.size()+ ")");
                    branchCourseAdapter.notifyDataSetChanged();
                }else{
                    Toast.makeText(ManageBranchCourseActivity.this,"No Classes found",Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if((requestCode == AdminUtil.REQ_CODE)&&(resultCode == AdminUtil.RES_CODE)){
            int signal = data.getIntExtra("signal",0);
            if(signal == 0){
                branCourBean = (BranCourBean) data.getSerializableExtra(AdminUtil.TAG_BRANCOUR);
                branCourBeanArrayList.set(pos,branCourBean);
            }else{
               branCourBeanArrayList.remove(pos);
                getSupportActionBar().setTitle("Course's" + " (" + branCourBeanArrayList.size()+ ")");
            }
            branchCourseAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_admin,menu);
        MenuItem searchitem=menu.findItem(R.id.menu_search);
        MenuItem mItem = menu.findItem(R.id.action_settings);
        mItem.setVisible(false);
        SearchManager searchManager = (SearchManager) ManageBranchCourseActivity.this.getSystemService(Context.SEARCH_SERVICE);

        SearchView searchView = null;
        if (searchitem != null) {
            searchView = (SearchView) searchitem.getActionView();
        }
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(ManageBranchCourseActivity.this.getComponentName()));
        }
        searchView.setOnQueryTextListener(onQueryTextListener);
        return true;
    }

    SearchView.OnQueryTextListener onQueryTextListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query) {
            return false;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            branchCourseAdapter.filter(newText.toString());
            branchCourseAdapter.notifyDataSetChanged();
            return false;
        }
    };
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
