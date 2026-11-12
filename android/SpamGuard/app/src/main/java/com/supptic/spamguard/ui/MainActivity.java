package com.supptic.spamguard.ui;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.supptic.spamguard.R;
import com.supptic.spamguard.viewmodel.AnalyseViewModel;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        AnalyseViewModel vm = new ViewModelProvider(this).get(AnalyseViewModel.class);
        vm.getAnalyse().observe(this, r -> Log.d("SpamGuard", r.etat + " → "
                + (r.donnees != null ? r.donnees.getLabel() + " " + r.donnees.getConfiance() + "%" : r.message)));
        vm.analyser("WINNER!! Claim your free prize now, call 09061701461");

    }

}