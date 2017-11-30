package com.app.mightylion.finaldictionary;

import android.content.Intent;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebBackForwardList;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import java.util.Locale;

public class WordDetailActivity extends AppCompatActivity implements View.OnClickListener {

    private WebView wordWebView;
    private Button BritishSpeaker;
    private Button AmericanSpeaker;

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
                    view.scrollTo(0,0);
                }
            }
        });

        BritishSpeaker = findViewById(R.id.word_pronounce_british);
        BritishSpeaker.setOnClickListener(this);
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

        AmericanSpeaker = findViewById(R.id.word_pronounce_american);
        AmericanSpeaker.setOnClickListener(this);
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
        searchWord = wordIntent.getStringExtra("word");
        speakWord = showContent(searchWord);

    }

    protected String showContent(String word) {
        String searchWord = "about:blank";
        if (word.contains("entry://")) {
            searchWord = word;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(word.substring(8));
            word = stringBuilder.toString();
        }
        String wordContent = getWordHTML(word);
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
            wordWebView.loadDataWithBaseURL("file:////android_asset/", contentBuilder.toString(), "text/html", "utf-8", searchWord);
            return word;
        }
    }

    protected String getWordHTML(String word) {
        Word aWord = databaseHandler.getWord(word);
        if (aWord == null) return null;
        else return aWord.getContent();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.word_pronounce_british:
                UKVoice.speak(speakWord, TextToSpeech.QUEUE_FLUSH, null);
                break;
            case R.id.word_pronounce_american:
                USVoice.speak(speakWord, TextToSpeech.QUEUE_FLUSH, null);
        }
    }
}
