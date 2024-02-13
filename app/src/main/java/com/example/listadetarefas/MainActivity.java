package com.example.listadetarefas;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private ListView taskListView;
    private Button addTaskButton, playButton;
    private ArrayList<String> taskList;
    private ArrayAdapter<String> tasksAdapter;
    private TextToSpeech textToSpeech;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        taskList = new ArrayList<>();
        tasksAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, taskList);

        taskListView = findViewById(R.id.taskListView);
        taskListView.setAdapter(tasksAdapter);

        addTaskButton = findViewById(R.id.addTaskButton);
        addTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddTaskActivity.class);
                startActivityForResult(intent, 1);
            }
        });

        playButton = findViewById(R.id.playButton);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playTaskList();
            }
        });

        taskListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                showDeleteDialog(position);
                return true;
            }
        });

        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = textToSpeech.setLanguage(Locale.getDefault());

                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        // Handle TTS language not supported
                    }
                } else {
                    // Handle TTS initialization failed
                }
            }
        });
    }

    private void playTaskList() {
        StringBuilder textToSpeak = new StringBuilder();
        for (String task : taskList) {
            textToSpeak.append(task).append(". ");
        }
        textToSpeech.speak(textToSpeak.toString(), TextToSpeech.QUEUE_FLUSH, null, null);
    }

    private void showDeleteDialog(final int position) {
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("Deletar tarefa")
                .setMessage("Você tem certeza que quer deletar esta tarefa?")
                .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        taskList.remove(position);
                        tasksAdapter.notifyDataSetChanged();
                    }
                })
                .setNegativeButton("Não", null)
                .create().show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            String task = data.getStringExtra("task");
            taskList.add(task);
            tasksAdapter.notifyDataSetChanged();
        }
    }
}
