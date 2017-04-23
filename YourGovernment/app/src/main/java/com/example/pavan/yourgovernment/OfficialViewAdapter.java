package com.example.pavan.yourgovernment;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by pavan on 4/16/17.
 */

public class OfficialViewAdapter extends RecyclerView.Adapter<OfficialsViewHolder> {
    //never assign a new memory for this variable
    private List<Officials> officialsList;
    private MainActivity ma;

    /**
     * Sets the adapter with the list of data that is use to set the recycler view
     * @param officialsList - list of objects of type Stocks
     */
    public OfficialViewAdapter(List<Officials> officialsList, MainActivity mainAct) {
        this.officialsList = officialsList;
        ma = mainAct;
    }

    @Override
    public OfficialsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.officials_list_element, parent, false);
        v.setOnClickListener(ma);

        return new OfficialsViewHolder(v);
    }

    @Override
    public void onBindViewHolder(OfficialsViewHolder holder, int position) {
        Officials official = officialsList.get(position);
        StringBuilder sb = new StringBuilder();

        //this will form the name of the official
        sb.append(official.getName());

        if(!official.getParty().isEmpty() && !official.getParty().equals("Unknown"))
        {
            sb.append(" (");
            sb.append(official.getParty());
            sb.append(")");
        }

        holder.officeName.setText(official.getOffice());
        holder.officialName.setText(sb.toString());
    }

    @Override
    public int getItemCount() {
        return officialsList.size();
    }
}
