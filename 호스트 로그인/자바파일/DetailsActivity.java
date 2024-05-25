package com.example.myapplication;

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

    private RecyclerView recyclerView;
    private EditText editTextAdditional;
    private Button buttonAdd;
    private List<String> itemList;
    private ItemAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        recyclerView = findViewById(R.id.recyclerView);
        editTextAdditional = findViewById(R.id.editTextAdditional);
        buttonAdd = findViewById(R.id.buttonAdd);
        itemList = new ArrayList<>();
        adapter = new ItemAdapter(itemList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        String name = getIntent().getStringExtra("name");
        String meeting = getIntent().getStringExtra("meeting");

        TextView textViewOutput = findViewById(R.id.textViewOutput);
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
    }
}

