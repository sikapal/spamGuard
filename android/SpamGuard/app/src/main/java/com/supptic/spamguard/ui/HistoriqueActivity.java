package com.supptic.spamguard.ui;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.supptic.spamguard.R;
import com.supptic.spamguard.viewmodel.AnalyseViewModel;

public class HistoriqueActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historique);

        AnalyseViewModel viewModel = new ViewModelProvider(this).get(AnalyseViewModel.class);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_vider) {
                viewModel.viderHistorique();
                return true;
            }
            return false;
        });

        AnalyseAdapter adapter = new AnalyseAdapter();
        RecyclerView recycler = findViewById(R.id.recyclerHistorique);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(adapter);

        View layoutVide = findViewById(R.id.layoutVide);
        viewModel.getHistorique().observe(this, liste -> {
            adapter.submitList(liste);
            layoutVide.setVisibility(liste.isEmpty() ? View.VISIBLE : View.GONE);
        });
    }
}
