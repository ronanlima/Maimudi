package br.com.maimudi.activity;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import br.com.maimudi.MaiMudiApplication;
import br.com.maimudi.R;
import br.com.maimudi.adapter.SelectMusicAdapter;
import br.com.maimudi.factory.SpotifyFactory;
import br.com.maimudi.model.Music;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by brunolemgruber on 17/03/16.
 */
public class SelectMusicActivity extends BaseActivity {
    private static final String TAG = SelectMusicAdapter.class.getCanonicalName().toUpperCase();

    protected RecyclerView recyclerView;
    private List<Music> musics;
    private LinearLayout linearLayout, llInfoUser;
    private LinearLayoutManager mLayoutManager;
    private TextView result;
    private EditText search;
    private ImageView searchTrack;
    private SelectMusicAdapter selectMusicAdapter;
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.post_mudi_select_music);
        linearLayout = (LinearLayout) findViewById(R.id.ll_select_music);
        llInfoUser = (LinearLayout) findViewById(R.id.ll_info_user);

        setupToolBar();
        updateImageProfile(MaiMudiApplication.getInstance().getLoggedUser().getImageProfile() != null);
        updateNomeUsuario(linearLayout);

        this.musics = new ArrayList<>();
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);
        selectMusicAdapter = new SelectMusicAdapter(this, musics, onClickListener(), onPlayMusic());
        result = (TextView) findViewById(R.id.result);
        search = (EditText) findViewById(R.id.search);
        searchTrack = (ImageView) findViewById(R.id.search_track);
        searchTrack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (search.getText() != null && !search.getText().toString().trim().isEmpty()){
                    callMethodSearchSpotify(search.getText().toString());
                } else {
                    showAlertParametersSearch(linearLayout, getResources().getString(R.string.msg_alerta_mus_art_alb));
                }
            }
        });
        search.setInputType(InputType.TYPE_CLASS_TEXT);
        search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == 6 && v.getText() != null && !v.getText().toString().trim().isEmpty()){
                    callMethodSearchSpotify(v.getText().toString());
                } else {
                    Log.d(TAG, "Usuário pressionou enter sem ter informado nada no campo de busca.");
                    showAlertParametersSearch(linearLayout, getResources().getString(R.string.msg_alerta_mus_art_alb));
                }
                return true;
            }
        });
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (mediaPlayer != null){
                    mediaPlayer.stop();
                    mediaPlayer.reset();
                }
            }
        });
    }

    private void showAlertParametersSearch(LinearLayout linearLayout, String text) {
        Snackbar snackbar = Snackbar.make(linearLayout, text, Snackbar.LENGTH_LONG);
        ViewGroup group = (ViewGroup) snackbar.getView();
        group.setBackgroundColor(ContextCompat.getColor(getBaseContext(), R.color.colorPrimary));
        snackbar.show();
    }

    private void callMethodSearchSpotify(String text) {
        setupDialog(this, getResources().getString(R.string.label_efetuando_busca));
        getSimpleArcDialog().setCancelable(false);
        getSimpleArcDialog().show();
        musics.clear();
        searchTrackSpotify(text);
        InputMethodManager inputMethodManager = (InputMethodManager) getBaseContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getWindow().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    private void searchTrackSpotify(final String texto) {
        Call<JsonObject> call = SpotifyFactory.getInstance().getSpotifyApi().getTracksSpotify(texto,"BR",20,"track","album","artist");
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Log.d(TAG, response.body().toString());
                if (response.isSuccessful() && response.code() == 200){
                    JsonObject obj = response.body().getAsJsonObject("tracks");
                    if (obj.getAsJsonArray("items").size() > 0){
                        llInfoUser.setVisibility(View.GONE);
                        for (JsonElement o: obj.getAsJsonArray("items")) {
                            if (!((JsonObject) o).get("preview_url").isJsonNull()) {
                                Music m = new Music();
                                m.setArtista(((JsonObject) o).getAsJsonArray("artists").get(0).getAsJsonObject().get("name").getAsString());
                                m.setMusica(((JsonObject) o).get("name").getAsString());
                                m.setAlbum(((JsonObject) o).get("album").getAsJsonObject().get("name").getAsString().toUpperCase());
                                if (((JsonObject) o).getAsJsonObject("album").getAsJsonArray("images") != null && ((JsonObject) o).getAsJsonObject("album").getAsJsonArray("images").size() != 0){
                                    m.setImgSpotify(((JsonObject) o).getAsJsonObject("album").getAsJsonArray("images").get(1).getAsJsonObject().get("url").getAsString());
                                }
                                m.setPreviewUrl(((JsonObject) o).get("preview_url").getAsString());
                                musics.add(m);
                            } else {
                                Log.w(TAG, "A música '"+((JsonObject) o).get("name")+"', álbum '"+((JsonObject) o).get("album").getAsJsonObject().get("name")+"' não possui referência (url) para tocar um trecho da faixa!");
                            }
                        }
                        if(selectMusicAdapter != null && musics.size() > 0) {
                            recyclerView.setAdapter(selectMusicAdapter);
                            result.setText(getResources().getString(R.string.result, musics.size() != 0 ? String.valueOf(musics.size()) : R.string.result_vazio));
                        }
                    } else {
                        llInfoUser.setVisibility(View.VISIBLE);
                        showAlertParametersSearch(linearLayout, getResources().getString(R.string.msg_nenhum_resultado, texto));
                    }
                    getSimpleArcDialog().dismiss();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                getSimpleArcDialog().dismiss();
                Log.e(TAG, "Falha na consulta ao spotify. Termo de busca = "+texto+". Erro = "+t.getMessage());
                if (t.getMessage().contains("timed out")){
                    showAlertParametersSearch(linearLayout, getResources().getString(R.string.msg_tempo_busca_excedido));
                }
            }
        });
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    private SelectMusicAdapter.onClickListener onClickListener() {
        return new SelectMusicAdapter.onClickListener() {
            @Override
            public void onClickListener(View view, int idx) {
                Music m = musics.get(idx);
                Intent intent = new Intent(SelectMusicActivity.this,ActionActivity.class);
                intent.putExtra("music",m);
                startActivity(intent);
            }
        };
    }

    private SelectMusicAdapter.onPlayMusic onPlayMusic(){
        return new SelectMusicAdapter.onPlayMusic() {
            @Override
            public void onPlayMusic(View view, int idx, String string) {
                Music m = musics.get(idx);
                if(string.equalsIgnoreCase("PLAY_MUSIC")){
                    if (mediaPlayer == null){
                        mediaPlayer = new MediaPlayer();
                    }
                    mediaPlayer.stop();
                    mediaPlayer.reset();
                    mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    try {
                        mediaPlayer.setDataSource(m.getPreviewUrl());
                        mediaPlayer.prepare();
                        mediaPlayer.start();
                        Log.v(TAG, "ACTION_DOWN start record for song ="+m.getPreviewUrl());
                    } catch (IOException e) {
                        Log.v(TAG, "ACTION_DOWN some problem happen");
                        e.printStackTrace();
                    }
                }else if(string.equalsIgnoreCase("STOP_MUSIC")){
                    Log.v(TAG, "ACTION_UP end record for song ="+m.getPreviewUrl());
                    mediaPlayer.stop();
                    mediaPlayer.reset();
                }
            }
        };
    }

}
