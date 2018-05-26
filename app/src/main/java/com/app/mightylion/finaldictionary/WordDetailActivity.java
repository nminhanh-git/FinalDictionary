package com.app.mightylion.finaldictionary;

import android.content.Intent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import net.gotev.speech.Speech;

import java.util.Locale;

public class WordDetailActivity extends AppCompatActivity {

    private WebView wordWebView;

    private DatabaseHandler databaseHandler;
    private String searchWord;
    private String speakWord = "";

    private TextToSpeech UKVoice;
    private TextToSpeech USVoice;

    private Animation leftToRightAnim;
    private Animation rightToLeftAnim;

    private boolean GO_BACK = false;

    @Override
    public void onBackPressed() {
        if (wordWebView.canGoBack()) {
            wordWebView.startAnimation(rightToLeftAnim);
            wordWebView.goBack();
            GO_BACK = true;
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_detail);

        getSupportActionBar().setDisplayShowTitleEnabled(false);

        leftToRightAnim = AnimationUtils.loadAnimation(WordDetailActivity.this, R.anim.left_to_right);
        rightToLeftAnim = AnimationUtils.loadAnimation(WordDetailActivity.this, R.anim.right_to_left);

        wordWebView = findViewById(R.id.word_web_view);
        wordWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith("entry://")) {
                    speakWord = showContent(url);
                    return true;
                }
                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                if (GO_BACK) {
                    speakWord = wordWebView.getUrl().substring(8);
                    view.scrollTo(0, 0);
                }
            }
        });

        UKVoice = new TextToSpeech(WordDetailActivity.this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = UKVoice.setLanguage(Locale.UK);
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Toast.makeText(WordDetailActivity.this, "Ngôn ngữ này không được hỗ trợ", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        USVoice = new TextToSpeech(WordDetailActivity.this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = USVoice.setLanguage(Locale.US);
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Toast.makeText(WordDetailActivity.this, "Ngôn ngữ này không được hỗ trợ", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        databaseHandler = new DatabaseHandler(this);
        databaseHandler.createDatabase();

        Intent wordIntent = getIntent();
        searchWord = wordIntent.getStringExtra(SearchActivity.WORD);
        speakWord = showContent(searchWord);

    }

    @Override
    protected void onDestroy() {
        USVoice.stop();
        UKVoice.stop();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.speech_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.UK_voice:
                UKVoice.speak(speakWord, TextToSpeech.QUEUE_FLUSH, null);
                return true;
            case R.id.US_voice:
                USVoice.speak(speakWord, TextToSpeech.QUEUE_FLUSH, null);
                return true;
        }
        return false;
    }

    protected String showContent(String word) {
        String searchWord = word;
        if (word.contains("entry://")) {
            searchWord = word;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(word.substring(8));
            word = stringBuilder.toString();
        }
        String wordContent = databaseHandler.getWord(word);
        if (wordContent == null) {
            Toast.makeText(this, "Không tìm thấy từ này", Toast.LENGTH_SHORT).show();
            this.finish();
            return "";
        } else {
            StringBuilder contentBuilder = new StringBuilder();
            contentBuilder.append("<!DOCTYPE html>\n" +
                    "<html>\n" +
                    "    <head>\n" +
                    "        <meta charset=\"utf-8\">\n" +
                    "            <meta content=\"IE=edge\" http-equiv=\"X-UA-Compatible\">\n" +
                    "                <title>\n" +
                    "                </title>\n" +
                    "                <link href=\"theme.css\" rel=\"stylesheet\">\n" +
                    "                </link>\n" +
                    "            </meta>\n" +
                    "        </meta>\n" +
                    "    </head>\n" +
                    "    <body>\n");

            contentBuilder.append(wordContent);
            contentBuilder.append("</body>\n" + "</html>");
            wordWebView.startAnimation(leftToRightAnim);
            if (!searchWord.contains("entry://")) {
                searchWord = "entry://" + searchWord;
            }
            wordWebView.loadDataWithBaseURL("file:////android_asset/", contentBuilder.toString(), "text/html", "utf-8", searchWord);
            return word;
        }
    }
}
