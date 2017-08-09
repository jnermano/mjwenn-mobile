package com.kasik.mjwenn.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.kasik.mjwenn.R;
import com.kasik.mjwenn.models.Announcement;
import com.kasik.mjwenn.utils.Session;
import com.kasik.mjwenn.views.NewAnnoucement;

/**
 * Created by Ermano on 6/29/2017.
 * userr annoucements
 */

public class FragmentAnnouncements extends Fragment {

    private static final String TAG = "Fragment announcement";

    Context context;

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private FirebaseRecyclerAdapter<Announcement, AnnouncementHolder> mFirebaseAdapter;


    FloatingActionButton fab_add_announcement;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_announcements, container, false);
        context = v.getContext();

        mRecyclerView = (RecyclerView) v.findViewById(R.id.main_recyclerview);
        mLinearLayoutManager = new LinearLayoutManager(context);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);

        fab_add_announcement = (FloatingActionButton) v.findViewById(R.id.account_fab_add_announcement);
        fab_add_announcement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, NewAnnoucement.class);
                startActivity(i);
            }
        });

        if (FirebaseAuth.getInstance().getCurrentUser() != null)
            fillView("");

        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {
                final int position = viewHolder.getAdapterPosition(); //get position which is swipe

                if (direction == ItemTouchHelper.LEFT) {    //if swipe left

                    AlertDialog.Builder builder = new AlertDialog.Builder(context); //alert for confirm to delete
                    builder.setMessage(getString(R.string.confirm_delete));    //set message

                    builder.setPositiveButton(getString(R.string.remove), new DialogInterface.OnClickListener() { //when click on DELETE
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            FirebaseDatabase.getInstance().getReference().child("/user_announcements/" + FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .child((mFirebaseAdapter.getRef(position)).getKey())
                                    .removeValue();

                            return;
                        }
                    }).setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {  //not removing items if cancel is done
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mFirebaseAdapter.notifyItemRemoved(position + 1);    //notifies the RecyclerView Adapter that data in adapter has been removed at a particular position.
                            mFirebaseAdapter.notifyItemRangeChanged(position, mFirebaseAdapter.getItemCount());
                            return;
                        }
                    }).show();  //show alert dialog
                }
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView); //set swipe to recylcerview

        setHasOptionsMenu(true);
        return v;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_my_announcement, menu);  // Use filter.xml from step 1
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_menu_announcements_all:
                fillView("");
                break;
            case R.id.action_menu_announcements_alerts:
                fillView("10HT");
                break;
            case R.id.action_menu_announcements_ann:
                fillView("00HT");
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    void fillView(String s) {
        mFirebaseAdapter = new FirebaseRecyclerAdapter<Announcement, AnnouncementHolder>(
                Announcement.class,
                R.layout.adapter_announcement,
                AnnouncementHolder.class,
                FirebaseDatabase.getInstance().getReference("/user_announcements/" + FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .orderByChild("filter")
                        .startAt(s)
                        .endAt(s + "\uf8ff")
        ) {
            @Override
            protected void populateViewHolder(AnnouncementHolder viewHolder, final Announcement model, int position) {
                model.setId(getRef(position).getKey());
                viewHolder.tv_name.setText(model.toString());
                viewHolder.tv_card.setText(model.getCard());
                try {
                    viewHolder.tv_address.setText(model.getAddress().toString());
                } catch (Exception e) {
                    viewHolder.tv_address.setText(model.getAddr());
                    e.printStackTrace();
                }

                viewHolder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Session.setAnnouncement(model);
                        Intent i = new Intent(context, NewAnnoucement.class);
                        startActivity(i);
                    }
                });
            }


        };

        mFirebaseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
            }
        });


        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setAdapter(mFirebaseAdapter);
    }


    private static class AnnouncementHolder extends RecyclerView.ViewHolder {
        public TextView tv_name, tv_card, tv_address;
        public View view;

        public AnnouncementHolder(View itemView) {
            super(itemView);
            view = itemView;
            tv_name = (TextView) view.findViewById(R.id.adapter_ann_name);
            tv_card = (TextView) view.findViewById(R.id.adapter_ann_card_type);
            tv_address = (TextView) view.findViewById(R.id.adapter_ann_location);
        }
    }

}
