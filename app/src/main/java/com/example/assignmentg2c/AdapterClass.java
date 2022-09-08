package com.example.assignmentg2c;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class AdapterClass extends RecyclerView.Adapter<AdapterClass.ViewHolder> {
    public  ArrayList<ModalClass> serviceList;
    public Context context;
    public AdapterClass (ArrayList<ModalClass> serviceList,Context context){
        this.serviceList = serviceList;
        this.context = context;
    }
    Button deletButton;
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.service_list,parent,false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
     ModalClass modalClass = serviceList.get(position);
     String service = modalClass.getServiceName();
     holder.serviceTxt.setText(service);
//     holder.itemView.setOnClickListener(new View.OnClickListener() {
//         @Override
//         public void onClick(View view) {
//             Toast.makeText(context, "Item clicked", Toast.LENGTH_SHORT).show();
//         }
//     });
     holder.rl.setOnLongClickListener(new View.OnLongClickListener() {
         @Override
         public boolean onLongClick(View view) {
             int pos = holder.getAdapterPosition();
             Dialog dialog = new Dialog(context);
             dialog.setContentView(R.layout.delete_service_layout);
             deletButton = dialog.findViewById(R.id.deleteServiceBtn);
             dialog.show();
             deletButton.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View view) {
                     serviceList.remove(pos);
                     notifyItemRemoved(pos);
                     dialog.dismiss();
                     deleteService(modalClass,holder);
                 }
             });
             return false;
         }
     });

    }

    private void deleteService(ModalClass modalClass, ViewHolder holder) {
        String serviceName = modalClass.getServiceName();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Services");
        ref.child(serviceName).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(context, "Successfully deleted", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return serviceList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView serviceTxt;
        RelativeLayout rl;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            serviceTxt = itemView.findViewById(R.id.serviceNameTv);
            rl = itemView.findViewById(R.id.relativeLayout);
        }
    }
}
