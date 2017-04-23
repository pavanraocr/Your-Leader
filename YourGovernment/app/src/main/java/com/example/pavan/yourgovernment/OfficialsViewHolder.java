package com.example.pavan.yourgovernment;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/**
 * Created by pavan on 4/16/17.
 */

public class OfficialsViewHolder extends RecyclerView.ViewHolder {
    public TextView officialName;
    public TextView officeName;


    public OfficialsViewHolder(View itemView) {
        super(itemView);

        officeName = (TextView) itemView.findViewById(R.id.tv_office_name);
        officialName = (TextView) itemView.findViewById(R.id.tv_official_name);
    }
}
