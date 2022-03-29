package org.niklab.utubeboooster;


import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class SubsFragment extends Fragment {

    private RecyclerView recyclerView;
    private DatabaseReference SubssRef;
    private String Uid,Id;
    private TextView War;
    private ImageView imageView;
    FirebaseRecyclerAdapter adapter;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View views = inflater.inflate(R.layout.fragment_subs, container, false);


        recyclerView = views.findViewById(R.id.subsRecy);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        SubssRef = FirebaseDatabase.getInstance().getReference().child("SubsList");

        FloatingActionButton fab = views.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(getActivity(), InsertSubActivity.class);
                startActivity(in);

            }
        });

        final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        assert firebaseUser != null;
        Uid = firebaseUser.getUid();
        imageView = views.findViewById(R.id.imageView);
        War = views.findViewById(R.id.Nwar);
        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<SubsAdapter>()
                .setQuery(SubssRef,SubsAdapter.class)
                .build();


        adapter = new FirebaseRecyclerAdapter<SubsAdapter, SubsAdapterHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final SubsFragment.SubsAdapterHolder holder, int position, @NonNull SubsAdapter model) {
                String userIds = getRef(position).getKey();
                assert userIds != null;
                SubssRef.child(userIds).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {



                        if (dataSnapshot.hasChild("image")){
                            String image = dataSnapshot.child("image").getValue(String.class);
                            final String name = dataSnapshot.child("name").getValue(String.class);
                            Long viewsDone = dataSnapshot.child("viewsDone").getValue(long.class);
                            final String link = dataSnapshot.child("link").getValue(String.class);
                            final Long cost = dataSnapshot.child("cost").getValue(Long.class);
                            Long views = dataSnapshot.child("views").getValue(Long.class);
                            String UIId = dataSnapshot.getRef().getKey();


                            holder.Name.setText(name);
                            holder.Value.setText(String.valueOf(cost));
                            Picasso.get().load(image).into(holder.imageView);


                            holder.Views.setText(String.valueOf(viewsDone));


                            holder.button.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    assert viewsDone != null;
                                    int vd= viewsDone.intValue();
                                    assert views != null;
                                    int vw = views.intValue();

                                    if (vd>=vw){

                                        dataSnapshot.getRef().setValue(null);
                                        adapter.notifyDataSetChanged();
                                        adapter.startListening();


                                    }else {
                                        if (link!=null){
                                            Id =  link.substring(link.indexOf("l/") + 2);
                                        }
                                        Intent Viewintent = new Intent(getContext(),subscriptionsActivity.class);
                                        Viewintent.putExtra("Id",Id);
                                        Viewintent.putExtra("Image",image);
                                        Viewintent.putExtra("cost",cost);
                                        Viewintent.putExtra("name",name);
                                        Viewintent.putExtra("UId",UIId);
                                        startActivity(Viewintent);

                                    }





                                }
                            });


                        }


                        if (dataSnapshot.hasChild(Uid)){
                            GradientDrawable backgroundGradient = (GradientDrawable)holder.button2.getBackground();
                            backgroundGradient.setColor(getResources().getColor(android.R.color.holo_red_dark));

                        }else {
                            GradientDrawable backgroundGradient = (GradientDrawable)holder.button2.getBackground();
                            backgroundGradient.setColor(getResources().getColor(android.R.color.holo_green_light));
                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }




            @NonNull
            @Override
            public SubsFragment.SubsAdapterHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.subscriberview,viewGroup,false);
                return new SubsFragment.SubsAdapterHolder(view);
            }


        };



        recyclerView.setAdapter(adapter);


        return views;

    }

    @Override
    public void onStart() {
        super.onStart();

        SubssRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                adapter.startListening();
                if (dataSnapshot.getChildrenCount()==0){
                    recyclerView.setVisibility(View.GONE);
                    imageView.setVisibility(View.VISIBLE);
                    War.setVisibility(View.VISIBLE);
                }else {
                    imageView.setVisibility(View.GONE);
                    War.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    static class SubsAdapterHolder extends RecyclerView.ViewHolder {
        TextView Name,Value,Views;
        ImageView imageView;
        Button button,button2;
        SubsAdapterHolder(@NonNull View itemView) {
            super(itemView);
            Name = itemView.findViewById(R.id.Name);
            Value = itemView.findViewById(R.id.Value);
            imageView = itemView.findViewById(R.id.imageView2);
            Views = itemView.findViewById(R.id.views);
            button = itemView.findViewById(R.id.button3);
            button2 = itemView.findViewById(R.id.ch);
        }


    }

}
