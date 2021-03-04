package com.example.cs125project;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AddressComponent;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Calendar;

public class ReportFragment extends Fragment {
    String username;
    int numReports;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("ReportFragment", "Begin initialization");
        final View view = inflater.inflate(R.layout.fragment_report, container, false);
        final EditText reportIntensityET = view.findViewById(R.id.report_intensity);
        final EditText reportCategoryET = view.findViewById(R.id.report_category);
        final EditText reportHoursET = view.findViewById(R.id.report_duration_hours_edittext);
        final EditText reportMinET = view.findViewById(R.id.report_duration_min_edittext);
        Button saveDataBtn = view.findViewById(R.id.report_save_button);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        //get username through shared preferences, check if it already exists in firebase
        SharedPreferences sharedPref = view.getContext().getSharedPreferences(getString(R.string.username_shared_preference_key), view.getContext().MODE_PRIVATE);
        username = sharedPref.getString("username", "noUsername");

        DatabaseReference dbRef = database.getReference();


        dbRef.child("users").child(username).child("reports").child("count").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Integer database_num_reports = dataSnapshot.getValue(Integer.class);
                if (database_num_reports != null) {
                    numReports = database_num_reports;
                } else {
                    numReports = 0;
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.d("onCancelled", "triggered in the event that this listener either failed at the server, or is removed as a result of the security and Firebase Database rules.");
            }
        });




        //email number age
        saveDataBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Report report = new Report();
                report.setCategory(reportCategoryET.getText().toString());
                report.setIntensity(reportIntensityET.getText().toString());
                report.setMin(Integer.parseInt(reportMinET.getText().toString()));
                report.setHrs(Integer.parseInt(reportHoursET.getText().toString()));
                report.setSubmissionTime(Calendar.getInstance().getTime());
                numReports += 1;
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference dbRef = database.getReference();
                dbRef.child("users").child(username).child("reports").child("count").setValue(numReports);
                dbRef.child("users").child(username).child("reports").child(Integer.toString(numReports)).setValue(report);
            }
        });
        Log.d("ProfileFragment", "End initialization");
        return view;
    }
    //exercise time spent, exercise level, exercise activity category
}
