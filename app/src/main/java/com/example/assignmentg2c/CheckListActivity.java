package com.example.assignmentg2c;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;

public class CheckListActivity extends AppCompatActivity {
    public  ArrayList<ModalClass> serviceList;
    private RecyclerView recyclerView;
    FloatingActionButton addService;
    Dialog addServiceDialog;
    EditText serviceInput;
    Button addButton;
    FirebaseAuth mAuth;
    ProgressDialog progressDialog;
    AdapterClass adapterClass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checking);
        serviceList = new ArrayList<>();
        recyclerView = findViewById(R.id.servicesRv);
        addService = findViewById(R.id.floatingActionButton);
        addServiceDialog = new Dialog(CheckListActivity.this);
        addServiceDialog.setContentView(R.layout.add_service_layout);
        serviceInput =addServiceDialog.findViewById(R.id.inputCheckListEt);
        addButton = addServiceDialog.findViewById(R.id.addServiceBtn);
        mAuth = FirebaseAuth.getInstance();
        setRv();
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);
        addService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addServiceDialog.show();
            }
        });
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addServiceDialog.dismiss();
                validateData();
            }
        });


    }

    private void setRv() {
        serviceList.add(new ModalClass("Vehicle washing"));
        serviceList.add(new ModalClass("Brake shoe rubbing"));
        serviceList.add(new ModalClass("Cardburetor cleaning (If required)"));
        serviceList.add(new ModalClass("Greasing and lubrication"));
        serviceList.add(new ModalClass("Oil leakage"));
        serviceList.add(new ModalClass("Lighting check"));

        adapterClass = new AdapterClass(serviceList, CheckListActivity.this);
        recyclerView.setLayoutManager(new LinearLayoutManager(CheckListActivity.this));
        recyclerView.setAdapter(adapterClass);


    }


    String service;
    private void validateData() {
        service = serviceInput.getText().toString().trim();
        Log.d(TAG, "validateData: "+service);
        if (TextUtils.isEmpty(service)){
            Toast.makeText(this, "Please enter service name", Toast.LENGTH_SHORT).show();
        }
        else{
            serviceList.add(new ModalClass(service));
           adapterClass = new AdapterClass(serviceList, CheckListActivity.this);
           recyclerView.setLayoutManager(new LinearLayoutManager(CheckListActivity.this));
            adapterClass.notifyItemInserted(serviceList.size()-1);
            recyclerView.scrollToPosition(serviceList.size()-1);
            recyclerView.setAdapter(adapterClass);
            addToFirebase();
       }
    }

    private void addToFirebase() {
        progressDialog.setMessage("Adding data....");
        progressDialog.show();
        long timestamp = System.currentTimeMillis();
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("id",timestamp);
        hashMap.put("service",service);
        hashMap.put("uid",mAuth.getUid());
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Services");
        reference.child(service).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                      progressDialog.dismiss();
                      Toast.makeText(CheckListActivity.this, "Service added successfully", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(CheckListActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

}