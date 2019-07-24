package com.notificationremove.notificationblocker;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.notificationremove.notificationblocker.MODEL.BlockList;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class App_List_Adapter extends RecyclerView.Adapter<App_List_Adapter.MyViewHolder> {
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView app_name;
        public ImageView app_icon;
        public Switch status;

        public MyViewHolder(View view) {
            super(view);
            app_name = view.findViewById(R.id.app_name);
            app_icon = view.findViewById(R.id.app_icon);
            status = view.findViewById(R.id.status);
        }
    }

    List<String> stringList;
    Context context1;
    private Realm realm;
    BlockList blockList;

    App_List_Adapter(Context context, List<String> list) {
        stringList = list;
        context1 = context;
    }

    public App_List_Adapter(List<String> moviesList) {
        this.stringList = moviesList;
        Log.d("check1", stringList.size() + "");
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        /*View view2 = LayoutInflater.from(context1).inflate(R.layout.app_list_row,parent,false);

        MyViewHolder viewHolder = new MyViewHolder(view2);

        return viewHolder;*/
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.app_list_row, parent, false);
        try {
            RealmConfiguration config = new RealmConfiguration.Builder()
                    .name("notification.realm")
                    .schemaVersion(1)
                    .deleteRealmIfMigrationNeeded()
                    .build();
            realm = Realm.getInstance(config);
        } catch (Exception e) {
            Log.d("Error Line Number", Log.getStackTraceString(e));
        }

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        try {
            ApkInfoExtractor apkInfoExtractor = new ApkInfoExtractor(context1);


            final String ApplicationPackageName = stringList.get(position);
            final String ApplicationLabelName = apkInfoExtractor.GetAppName(ApplicationPackageName);
            Drawable drawable = apkInfoExtractor.getAppIconByPackageName(ApplicationPackageName);

            holder.app_name.setText(ApplicationLabelName);

            //viewHolder.textView_App_Package_Name.setText(ApplicationPackageName);

            holder.app_icon.setImageDrawable(drawable);

            //Adding click listener on CardView to open clicked application directly from here .

            holder.status.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    blockList = realm.where(BlockList.class).equalTo("package_name", ApplicationPackageName).findFirst();
                    realm.beginTransaction();
                    if (isChecked) {
                        // If the switch button is on
                        if (blockList == null) {
                            BlockList blockList_new = realm.createObject(BlockList.class);
                            blockList_new.setPackage_name(ApplicationPackageName);
                            blockList_new.setStatus("ok");
                        } else {
                            blockList.setStatus("ok");
                        }
                    } else {
                        // If the switch button is off
                        if (blockList == null) {
                            BlockList blockList_new = realm.createObject(BlockList.class);
                            blockList_new.setPackage_name(ApplicationPackageName);
                            blockList_new.setStatus("okok");
                        } else {
                            blockList.setStatus("okok");
                        }
                    }
                    realm.commitTransaction();
                }
            });
            BlockList blockList2 = realm.where(BlockList.class).equalTo("package_name", ApplicationPackageName).findFirst();
            if (blockList2 != null && blockList2.getStatus().equals("ok") && holder.app_name.getText().equals(ApplicationLabelName)) {
                Log.d("switch", blockList2.getPackage_name());
                holder.status.setChecked(true);
            }
        } catch (Exception e) {
            Log.d("Error Line Number", Log.getStackTraceString(e));
        }
    }

    @Override
    public int getItemCount() {
        return stringList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}
