package br.com.maimudi.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import br.com.maimudi.MaiMudiApplication;
import br.com.maimudi.R;
import br.com.maimudi.autoCompleteToken.UserCompletionView;
import br.com.maimudi.model.User;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by brunolemgruber on 17/03/16.
 */
public class CutLyricActivity extends BaseActivity  {

    protected RecyclerView recyclerView;
    private Button button;
    private String selectMusic, titleMusic;
    private TextView music;
    private EditText lyric;
    private LinearLayout llAction, llActionGeral;
    private CheckBox checkBox;
    private CharSequence selectedText;


    User[] users;
    ArrayAdapter<User> adapter;
    UserCompletionView userCompletionView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.post_mudi_cut);
        initToolbar();

        selectMusic = getIntent().getExtras().getString("selectMusic");
        titleMusic = getIntent().getExtras().getString("titleMusic");

        llAction = (LinearLayout) findViewById(R.id.ll_action);
        checkBox = (CheckBox) findViewById(R.id.check);
        music = (TextView) findViewById(R.id.music);

        lyric = (EditText) findViewById(R.id.lyric);
        lyric.setText(selectMusic);
        lyric.setTextIsSelectable(true);
        lyric.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
               // menu.add(0, 0, 0, "Cortar").setIcon(R.drawable.cut);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                menu.removeItem(android.R.id.selectAll);
                menu.removeItem(android.R.id.cut);
                menu.removeItem(android.R.id.copy);
                menu.removeItem(android.R.id.paste);
                menu.removeItem(android.R.id.shareText);
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {

            }
        });

        music.setText(titleMusic);

        if(getIntent().getExtras().getInt("background") != 0 && getIntent().getExtras().getInt("text") != 0){
            llAction.setBackgroundColor((Integer) getIntent().getExtras().getInt("background"));
            music.setTextColor(getIntent().getExtras().getInt("text"));
            checkBox.setTextColor(getIntent().getExtras().getInt("text"));
        }

        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(((CheckBox)v).isChecked()){
                    lyric.setText("");
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(lyric, InputMethodManager.SHOW_IMPLICIT);
                    button.setText(getResources().getString(R.string.label_escrever_cutlyric));
                    llAction.setLayoutParams(new TableLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0.00f));
                    button.setLayoutParams(new TableLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0.00f));
                }else{
                    lyric.setText(selectMusic);
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(lyric.getWindowToken(), 0);
                    button.setText(getResources().getString(R.string.label_cortar_cutlyric));
                }
            }
        });

        button = (Button) findViewById(R.id.btnCut);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!checkBox.isChecked()){
                    int min = 0;
                    int max = lyric.getText().length();
                    if (lyric.isFocused()) {
                        final int selStart = lyric.getSelectionStart();
                        final int selEnd = lyric.getSelectionEnd();

                        min = Math.max(0, Math.min(selStart, selEnd));
                        max = Math.max(0, Math.max(selStart, selEnd));
                    }
                    selectedText = lyric.getText().subSequence(min, max);
                }else{
                    selectedText = lyric.getText().toString();
                }

                Intent returnIntent = new Intent();
                returnIntent.putExtra("result",selectedText.toString());
                setResult(RESULT_OK,returnIntent);
                finish();
            }
        });

    }

    private void initToolbar() {
        llActionGeral = (LinearLayout) findViewById(R.id.ll_lyric_cut);
        setupToolBar();
        updateImageProfile(MaiMudiApplication.getInstance().getLoggedUser().getImageProfile() != null);
        updateNomeUsuario(llActionGeral);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

}
