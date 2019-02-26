package hu.ait.android.geonote;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import hu.ait.android.geonote.adapter.ViewRecyclerAdapter;
import hu.ait.android.geonote.data.Post;

import static java.security.AccessController.getContext;

public class ViewActivity extends AppCompatActivity {

    private ViewRecyclerAdapter viewRecyclerAdapter;
    Double Lat;
    Double Lon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);

        viewRecyclerAdapter = new ViewRecyclerAdapter(getApplicationContext(),
                FirebaseAuth.getInstance().getCurrentUser().getUid());

        RecyclerView recyclerViewPlaces = (RecyclerView) findViewById(
                R.id.recyclerViewPosts);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerViewPlaces.setLayoutManager(layoutManager);
        recyclerViewPlaces.setAdapter(viewRecyclerAdapter);

        Intent prevIntent = getIntent();
        Lat = prevIntent.getDoubleExtra("Lat", 0);
        Lon = prevIntent.getDoubleExtra("Lon", 0);

        initPosts();
    }

    private void initPosts() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("posts");
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Post newPost = dataSnapshot.getValue(Post.class);


                if (Math.abs(newPost.getLat() - Lat) <= .01 && Math.abs(newPost.getLon() - Lon) <= .01) {
                    viewRecyclerAdapter.addPost(newPost, dataSnapshot.getKey());
                }
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                viewRecyclerAdapter.removePostByKey(dataSnapshot.getKey());
                Intent returnIntent = new Intent();
                returnIntent.putExtra("result", 1);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
