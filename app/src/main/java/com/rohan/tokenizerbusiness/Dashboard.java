package com.rohan.tokenizerbusiness;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

public class Dashboard extends AppCompatActivity {

    private FirebaseFirestore firebaseFirestore;
    private FirestoreRecyclerAdapter adapter;
    private RecyclerView bookinglist;
    private String place_name;
    private TextView nobooking, nameofplace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        nobooking = findViewById(R.id.nobooking);
        nameofplace = findViewById(R.id.name_of_place);

        getSupportActionBar().setTitle(Html.fromHtml("<font color='#000'>Tokenizer Business </font>"));
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#EFDA11")));


        firebaseFirestore = FirebaseFirestore.getInstance();
        bookinglist = findViewById(R.id.bookinglist);


        firebaseFirestore.collection("Places").
                document(""+FirebaseAuth.getInstance().getCurrentUser().
                        getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful())
                {
                    nameofplace.setText(task.getResult().getString("Name"));
                }
            }
        });

        Query query = firebaseFirestore.collection("BookingDetails").whereEqualTo("Business_email",
                FirebaseAuth.getInstance().getCurrentUser().getEmail());

        FirestoreRecyclerOptions<BookingsModel> option = new FirestoreRecyclerOptions.Builder<BookingsModel>()
                .setQuery(query, BookingsModel.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<BookingsModel, BookingViewHolder>(option) {
            @NonNull
            @Override
            public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.show_bookings,
                        parent, false);
                return new BookingViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull BookingViewHolder holder, int position, @NonNull BookingsModel model) {

                holder.booking_timing.setText(model.getTiming().toString());
                FirebaseFirestore documentReference = FirebaseFirestore.getInstance();
                documentReference.collection("Users").document(""+model.getUserId())
                        .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()) {
                            holder.email.setText("Email: "+task.getResult().getString("Email"));
                            holder.name.setText(task.getResult().getString("Name"));
                        }
                    }
                });

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Dashboard.this, ScannerActivity.class);
                        intent.putExtra("Name", holder.name.getText());
                        intent.putExtra("Email", holder.email.getText());
                        intent.putExtra("Time", holder.booking_timing.getText());
                        intent.putExtra("id", getSnapshots().getSnapshot(position).getId());
                        startActivity(intent);
                    }
                });

            }

            @Override
            public void onDataChanged() {
                super.onDataChanged();
                if(getItemCount() == 0)
                {
                    nobooking.setVisibility(View.VISIBLE);
                }
                else{
                    nobooking.setVisibility(View.INVISIBLE);
                }
            }
        };

        bookinglist.setHasFixedSize(true);
        bookinglist.setLayoutManager(new LinearLayoutManager(this));
        bookinglist.setAdapter(adapter);

    }
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu, menu);
        Drawable drawable = menu.findItem(R.id.item1).getIcon();
        if (drawable != null) {
            drawable.mutate();
            drawable.setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_ATOP);
        }
        return true;
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logOut:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(Dashboard.this, MainActivity.class));
                finish();
                return true;
            default:
                return false;
        }
    }


    public class BookingViewHolder extends RecyclerView.ViewHolder {

        private TextView name;
        private TextView email;
        private TextView booking_timing;

        public BookingViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            email = itemView.findViewById(R.id.email);
            booking_timing = itemView.findViewById(R.id.booking_time);
        }
    }


    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }
}