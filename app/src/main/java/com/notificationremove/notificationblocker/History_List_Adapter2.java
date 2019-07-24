package com.notificationremove.notificationblocker;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.ads.Ad;
import com.notificationremove.notificationblocker.MODEL.Notification_History;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

public class History_List_Adapter2 extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int RECIPE = 0;
    private static final int NATIVE_AD = 1;

    // A menu item view type.
    private static final int MENU_ITEM_VIEW_TYPE = 0;

    private static final int UNIFIED_NATIVE_AD_VIEW_TYPE = 1;

    // An Activity's Context.
    private final Context mContext;
    List<Object> stringList;
    RealmResults<Notification_History> notification_histories;
    Context context1;
    private Realm realm;

    public History_List_Adapter2(Context context, List<Object> list) {
        this.mContext = context;
        stringList = list;
        context1 = context;
        try {
            RealmConfiguration config = new RealmConfiguration.Builder()
                    .name("notification.realm")
                    .schemaVersion(1)
                    .deleteRealmIfMigrationNeeded()
                    .build();
            realm = Realm.getInstance(config);
        } catch (Exception e) {
            Log.d("Error Line Number", Log.getStackTraceString(e));
            if (realm != null) {
                realm.close();
            }
        } finally {

        }
    }


    public class History_List_Adapter3 extends RecyclerView.ViewHolder {
        public TextView history_name, history_count;
        public ImageView history_app_icon;

        public History_List_Adapter3(View view) {
            super(view);
            history_name = view.findViewById(R.id.history_name);
            history_count = view.findViewById(R.id.history_number);
            history_app_icon = view.findViewById(R.id.history_app_icon);
        }
    }

    @Override
    public int getItemCount() {
        return stringList.size();
    }

    @Override
    public int getItemViewType(int position) {
        Object item = stringList.get(position);
        if (item instanceof String) {
            return RECIPE;
        } else if (item instanceof Ad) {
            return NATIVE_AD;
        } else {
            return -1;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        if (viewType == RECIPE) {
            View itemView = inflater.inflate(R.layout.history_list, parent, false);
            return new History_List_Adapter3(itemView);
        } else {
            return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int itemType = getItemViewType(position);
        if (itemType == RECIPE) {
            History_List_Adapter3 menuItemHolder = (History_List_Adapter3) holder;
            ApkInfoExtractor apkInfoExtractor = new ApkInfoExtractor(context1);


            final String ApplicationPackageName = (String) stringList.get(position);
            //final String ApplicationPackageName = (String) final_app_list.get(position);
            final String ApplicationLabelName = apkInfoExtractor.GetAppName(ApplicationPackageName);
            Drawable drawable = apkInfoExtractor.getAppIconByPackageName(ApplicationPackageName);

            menuItemHolder.history_name.setText(ApplicationLabelName);

            //viewHolder.textView_App_Package_Name.setText(ApplicationPackageName);

            Notification_History history = realm.where(Notification_History.class).equalTo("apkname", ApplicationPackageName).findFirst();
            if (history != null) {
                menuItemHolder.history_count.setText(history.getNotification_count());
            } else {
                menuItemHolder.history_count.setText("0");
            }
            menuItemHolder.history_app_icon.setImageDrawable(drawable);
        }
    }
}
