package org.niklab.utubeboooster;


import android.content.Intent;
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


public class ViewsFragment extends Fragment {

    private RecyclerView recyclerView;
    private DatabaseReference ViewsRef;
    private String Uid;
    private TextView War;
    private ImageView imageView;
    private int Date;
    private FirebaseRecyclerAdapter adapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View views = inflater.inflate(R.layout.fragment_views, container, false);

       recyclerView = views.findViewById(R.id.viewsRecy);
       recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
       ViewsRef = FirebaseDatabase.getInstance().getReference().child("ViewsList");

        FloatingActionButton fab = views.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(getActivity(), InsertActivity.class);
                startActivity(in);

            }
        });



        final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        assert firebaseUser != null;
        Uid = firebaseUser.getUid();

        imageView = views.findViewById(R.id.imageView);
        War = views.findViewById(R.id.Nwar);
        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<ViewsAdapter>()
                .setQuery(ViewsRef,ViewsAdapter.class)
                .build();


        adapter = new FirebaseRecyclerAdapter<ViewsAdapter, ViewsAdapterHolder>(options) {

            @Override
            protected void onBindViewHolder(@NonNull final ViewsAdapterHolder holder, int position, @NonNull ViewsAdapter model) {
                String userIds = getRef(position).getKey();
                assert userIds != null;
                ViewsRef.child(userIds).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                        if (dataSnapshot.hasChild(Uid)){
                            Long date = dataSnapshot.child(Uid).getValue(Long.class);

                            assert date != null;
                            int dat = date.intValue();

                            if (dat!=Date){
                                recyclerView.removeAllViews();
                                dataSnapshot.child(Uid).getRef().setValue(null);
                                adapter.startListening();



                            }

                            ViewGroup.LayoutParams layoutParams =holder.itemView.getLayoutParams();
                            layoutParams.width= ViewGroup.LayoutParams.MATCH_PARENT;
                            layoutParams.height= 0;
                            holder.itemView.setLayoutParams(layoutParams);







                        }else if (dataSnapshot.hasChild("name")){
                            String image = dataSnapshot.child("image").getValue(String.class);
                            final String name = dataSnapshot.child("name").getValue(String.class);
                            Long viewsDone = dataSnapshot.child("viewsDone").getValue(long.class);
                            final String link = dataSnapshot.child("link").getValue(String.class);
                            final Long cost = dataSnapshot.child("cost").getValue(Long.class);
                            Long views = dataSnapshot.child("views").getValue(Long.class);




                            String UIId = dataSnapshot.getRef().getKey();
                            assert name != null;
                            String upToNCharacters = name.substring(0, Math.min(name.length(), 20));

                            holder.Name.setText(upToNCharacters);
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

                                    if (vd>=vw) {
                                        dataSnapshot.getRef().setValue(null);
                                        adapter.notifyDataSetChanged();
                                        adapter.startListening();
                                    }else {
                                        Intent Viewintent = new Intent(getContext(),ViewVideos.class);
                                        Viewintent.putExtra("Link",link);
                                        Viewintent.putExtra("cost",cost);
                                        Viewintent.putExtra("name",name);
                                        Viewintent.putExtra("UId",UIId);
                                        startActivity(Viewintent);
                                    }

                                }
                            });
                        }



                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

            @NonNull
            @Override
            public ViewsAdapterHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.viewsview,viewGroup,false);
                return new ViewsAdapterHolder(view);
            }



        };




        recyclerView.setAdapter(adapter);






        return views;

    }




    @Override
    public void onStart() {
        super.onStart();


        final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference UBoosterRef = rootRef.child(Uid);
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                adapter.startListening();

                if (dataSnapshot.child("Date").child("time2").exists()){

                    Long date = dataSnapshot.child("Date").child("time2").getValue(Long.class);
                    assert date != null;
                    Date = date.intValue();

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {


            }
        };

        UBoosterRef.addValueEventListener(eventListener);



        ViewsRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

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

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }








    public static class ViewsAdapterHolder extends RecyclerView.ViewHolder {
        TextView Name,Value,Views;
        ImageView imageView;
        Button button;

        ViewsAdapterHolder(@NonNull View itemView) {
            super(itemView);

            Name = itemView.findViewById(R.id.Name);
            Value = itemView.findViewById(R.id.Value);
            imageView = itemView.findViewById(R.id.imageView2);
            Views = itemView.findViewById(R.id.views);
            button = itemView.findViewById(R.id.button3);

        }
    }
}
