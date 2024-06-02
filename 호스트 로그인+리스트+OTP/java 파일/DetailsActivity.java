package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class DetailsActivity extends AppCompatActivity {

    private TextView textViewOutput;
    private EditText editTextAdditional;
    private Button buttonAdd;
    private Button buttonNext;
    private RecyclerView recyclerView;
    private ItemAdapter adapter;
    private List<String> itemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        textViewOutput = findViewById(R.id.textViewOutput);
        editTextAdditional = findViewById(R.id.editTextAdditional);
        buttonAdd = findViewById(R.id.buttonAdd);
        buttonNext = findViewById(R.id.buttonNext);
        recyclerView = findViewById(R.id.recyclerView);
        itemList = new ArrayList<>();
        adapter = new ItemAdapter(itemList);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        String name = getIntent().getStringExtra("name");
        String meeting = getIntent().getStringExtra("meeting");

        String outputText = "이름: " + name + "\n모임장: " + meeting;
        textViewOutput.setText(outputText);

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String additionalInput = editTextAdditional.getText().toString();
                if (!additionalInput.isEmpty()) {
                    itemList.add(additionalInput);
                    adapter.notifyItemInserted(itemList.size() - 1);
                    editTextAdditional.setText("");
                }
            }
        });

        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailsActivity.this, OTPActivity.class);
                startActivity(intent);
            }
        });
    }
}


