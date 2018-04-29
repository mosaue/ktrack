package com.ktrack.morti.ktrack.contactUtils;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ktrack.morti.ktrack.R;

import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.MyViewHolder> {

    private List<Contact> moviesList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView contactName, contactPhone, contactMain;

        public MyViewHolder(View view) {
            super(view);
            contactName = (TextView) view.findViewById(R.id.name_list);
            contactPhone = (TextView) view.findViewById(R.id.phone_list);
            contactMain = (TextView) view.findViewById(R.id.primary_list);
        }
    }

    public ContactAdapter(List<Contact> moviesList) {
        this.moviesList = moviesList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.contact_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Contact contact = moviesList.get(position);
        holder.contactName.setText(contact.getName());
        holder.contactPhone.setText(contact.getPhone());
        holder.contactMain.setText(contact.getPrimary());
    }

    @Override
    public int getItemCount() {
        return moviesList.size();
    }
}
