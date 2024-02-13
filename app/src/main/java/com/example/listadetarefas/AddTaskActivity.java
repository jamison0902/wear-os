package com.example.listadetarefas;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Objects;

public class AddTaskActivity extends AppCompatActivity {

    private ActivityResultLauncher<Intent> voiceInputLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        Button addTaskButton = findViewById(R.id.confirmButton);
        Button voiceInputButton = findViewById(R.id.voiceInputButton);

        addTaskButton.setOnClickListener(view -> addTask());

        voiceInputButton.setOnClickListener(view -> startVoiceInput());

        // Configuração do lançador para entrada de voz
        voiceInputLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK) {
                Intent data = result.getData();
                ArrayList<String> resultArray = Objects.requireNonNull(data).getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                if (resultArray != null && !resultArray.isEmpty()) {
                    String voiceInput = resultArray.get(0);
                    ((EditText) findViewById(R.id.taskEditText)).setText(voiceInput);
                } else {
                    Toast.makeText(this, "Não foi possível reconhecer o texto da voz", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void addTask() {
        // Obter o texto da tarefa do campo de texto
        String task = ((EditText) findViewById(R.id.taskEditText)).getText().toString();

        // Criar intent do resultado
        Intent resultIntent = new Intent();
        resultIntent.putExtra("task", task);

        // Definir o resultado e terminar a atividade
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    private void startVoiceInput() {
        // Cria um intent para reconhecimento de voz
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

        // Inicia a atividade de reconhecimento de voz usando o lançador
        voiceInputLauncher.launch(intent);
    }
}
