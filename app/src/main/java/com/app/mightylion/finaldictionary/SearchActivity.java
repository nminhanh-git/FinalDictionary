package com.app.mightylion.finaldictionary;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import net.gotev.speech.GoogleVoiceTypingDisabledException;
import net.gotev.speech.Speech;
import net.gotev.speech.SpeechDelegate;
import net.gotev.speech.SpeechRecognitionNotAvailable;
import net.gotev.speech.SpeechUtil;
import net.gotev.speech.ui.SpeechProgressView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class SearchActivity extends AppCompatActivity {
    private EditText searchEditText;
    private ProgressBar searchProgressBar;
    private RelativeLayout initialLayout;

    private DatabaseHandler databaseHandler;
    private SearchedWordDatabaseHandler searchedWordDatabaseHandler;

    public static final String WORD = "word";
    private ArrayList<String> wordList;
    private ArrayAdapter<String> adapter;
    private ListView wordListView;
    private String searchWord;
    private static final int REQ_CODE_SPEECH_INPUT = 100;
    private boolean INIT_FLAG = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        wordListView = (ListView) findViewById(R.id.lv_search);
        searchEditText = (EditText) findViewById(R.id.edt_search);
        initialLayout = findViewById(R.id.init_layout);
        if (INIT_FLAG) {
            initialLayout.setVisibility(View.VISIBLE);
            INIT_FLAG = false;
        }
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                initialLayout.requestFocus();
            }
        });

        searchProgressBar = findViewById(R.id.word_progress);
        searchProgressBar.setVisibility(View.GONE);

        databaseHandler = new DatabaseHandler(this);
        databaseHandler.createDatabase();

        searchedWordDatabaseHandler = new SearchedWordDatabaseHandler(this);

        wordList = new ArrayList<>();
        wordList.addAll(searchedWordDatabaseHandler.getAllSearchedWord());

        adapter = new WordListAdapter(SearchActivity.this, wordList);
        wordListView.setAdapter(adapter);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                initialLayout.setVisibility(View.GONE);
            }
        }, 3000);


        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                searchWord = editable.toString();
                new WordLoadTask().execute(searchWord);
                adapter.notifyDataSetChanged();
            }
        });

        wordListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView tv = view.findViewById(R.id.word_text);
                Intent intent = new Intent(SearchActivity.this, WordDetailActivity.class);
                String word = tv.getText().toString().trim();
                searchedWordDatabaseHandler.addWord(word);
                intent.putExtra(WORD, word);
                startActivity(intent);
            }
        });
    }

    public class WordLoadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            String searchWord = strings[0];
            if (searchWord.equals("")) {
                wordList.clear();
                wordList.addAll(searchedWordDatabaseHandler.getAllSearchedWord());
            } else {
                wordList.clear();
                wordList.addAll(databaseHandler.searchWord(searchWord));
            }
            return strings[0];
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            searchProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            adapter.notifyDataSetChanged();
            searchProgressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.word_speech_search:
                if (isNetworkAvailable(SearchActivity.this)) {
                    promptSpeechInput();
                } else{
                    Toast.makeText(this, "Chức năng này yêu cầu kết nốt mạng", Toast.LENGTH_SHORT).show();
                }
                    return true;
            case R.id.info:
                Intent InfoIntent = new Intent(SearchActivity.this, InfoActivity.class);
                startActivity(InfoIntent);
                return true;
            case R.id.clear_search_history:
                searchedWordDatabaseHandler.clearSearchHistory();
                wordList.clear();

                initialLayout.setVisibility(View.VISIBLE);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        initialLayout.setVisibility(View.GONE);
                        adapter.notifyDataSetChanged();
                    }
                }, 1500);
                return true;
        }
        return false;
    }

    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.US);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isNetworkAvailable(Context context) {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return (connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT:
                if (resultCode == RESULT_OK && data != null) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    searchWord = result.get(0).trim().toLowerCase();
                    Intent WordIntent = new Intent(SearchActivity.this, WordDetailActivity.class);
                    WordIntent.putExtra(WORD, searchWord);
                    Toast.makeText(this, searchWord, Toast.LENGTH_SHORT).show();
                    searchedWordDatabaseHandler.addWord(searchWord.trim());
                    startActivity(WordIntent);
                    break;
                }
        }
    }
}
