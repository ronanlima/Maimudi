package br.com.maimudi.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.tokenautocomplete.FilteredArrayAdapter;
import com.tokenautocomplete.TokenCompleteTextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.maimudi.MaiMudiApplication;
import br.com.maimudi.R;
import br.com.maimudi.adapter.MoodTopAdapter;
import br.com.maimudi.asyncTask.ImageLoaderExternals;
import br.com.maimudi.asyncTask.TaskParams;
import br.com.maimudi.autoCompleteToken.UserCompletionView;
import br.com.maimudi.factory.DiscogsFactory;
import br.com.maimudi.factory.VagalumeFactory;
import br.com.maimudi.model.Mood;
import br.com.maimudi.model.Music;
import br.com.maimudi.model.Post;
import br.com.maimudi.model.User;
import br.com.maimudi.services.MoodsService;
import fr.ganfra.materialspinner.MaterialSpinner;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by brunolemgruber on 17/03/16.
 */
public class ActionActivity extends BaseActivity implements TokenCompleteTextView.TokenListener, ImageLoaderExternals.OnBitmapLoaded {
    private static final String TAG = ActionActivity.class.getCanonicalName().toUpperCase();
    private static final String[] charsEspecials = {".","#","$","[","]"};

    protected RecyclerView recyclerView;
    private Spinner spinner3;
    private LinearLayout llAction, llActionGeral;
    private Button button;
    private Music selectMusic;
    private TextView music;
    private ImageView album,edit;
    private TextView lyric;
    private Palette.Swatch textSwatch;
    private LinearLayoutManager mLayoutManager;
    private List<Mood> moods;
    private TextView mood;
    private Context context = this;

    List<User> users;
    ArrayAdapter<User> adapter;
    UserCompletionView userCompletionView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.post_mudi_action);
//        recuperaListaUsersFirebase(savedInstanceState);
        initToolbar();

        selectMusic = getIntent().getParcelableExtra("music");

        lyric = (TextView) findViewById(R.id.lyric);
        lyric.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActionActivity.this,CutLyricActivity.class);
                intent.putExtra("selectMusic",lyric.getText().toString());
                intent.putExtra("titleMusic",selectMusic.getMusica());
                if(textSwatch != null){
                    intent.putExtra("background",textSwatch.getRgb());
                    intent.putExtra("text", textSwatch.getTitleTextColor());
                }
                startActivityForResult(intent,1);
            }
        });

        Call<JsonObject> call = VagalumeFactory.getInstance().getVagalumeApi().getLetra(selectMusic.getArtista(), selectMusic.getMusica(), getResources().getString(R.string.api_key_vagalume));
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful() && response.code() == 200){
                    if (response.body().get("type").getAsString().contains("notfound")){
                        Log.d(TAG, response.body().toString());
                        lyric.setHint(context.getResources().getString(R.string.msg_falha_recup_letra_vagalume));
                    } else {
                        Log.d(TAG, response.body().toString());
                        for (JsonElement elm:response.body().getAsJsonArray("mus")) {
                            lyric.setText(elm.getAsJsonObject().get("text").getAsString());
                        }
                    }
                } else {
                    Log.d(TAG, "Falha ao recuperar letra para a música '"+selectMusic.getMusica()+"', Artista: '"+selectMusic.getArtista()+"'. Mensagem = "+response.message());
                    lyric.setHint(context.getResources().getString(R.string.msg_falha_recup_letra_vagalume));
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e(TAG, t.getMessage());
                lyric.setHint(context.getResources().getString(R.string.msg_falha_recup_letra_vagalume));
            }
        });

        button = (Button) findViewById(R.id.btnPost);
        button.setLayoutParams(new TableLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0.00f));
        mood = (TextView) findViewById(R.id.mood);
        moods = MoodsService.getMoods(this);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(new MoodTopAdapter(this, moods,onClickListener()));

        String[] ACTION = {"Cantando","Mandando Indireta","Relembrando os velhos tempos","Pensando em alguém"};
        ArrayAdapter<String> adapterAction = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, ACTION);
        adapterAction.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner3 = (MaterialSpinner) findViewById(R.id.spinner3);
        spinner3.setAdapter(adapterAction);

        music = (TextView) findViewById(R.id.music);
        music.setText(selectMusic.getArtista() + " - " + selectMusic.getMusica());
        album = (ImageView) findViewById(R.id.album);

        new ImageLoaderExternals().execute(new TaskParams(this, ActionActivity.this, selectMusic.getImgSpotify()));

        llAction = (LinearLayout) findViewById(R.id.ll_action);
        //llAction.setLayoutParams(new TableLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0.00f));

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!lyric.getText().toString().trim().isEmpty()){
                    buscaInfosComplementaresPost(populaPost());
                } else {
                    Snackbar snackbar = Snackbar.make(button, getResources().getString(R.string.msg_alert_busca), Snackbar.LENGTH_LONG);
                    ViewGroup group = (ViewGroup) snackbar.getView();
                    group.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary));
                    snackbar.show();
                }
            }
        });

        /** FIXME: Comentado temporariamente para efetuar a troca na forma em que o usuário seleciona suas próprias imagens para ser capa do álbum da música que
            está compartilhando
        album.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActionActivity.this,MediaActivity.class);
                startActivityForResult(intent,2);
            }
        });*/

//        edit = (ImageView) findViewById(R.id.edit);
//        edit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                Intent intent = new Intent(ActionActivity.this,MediaActivity.class);
//                startActivityForResult(intent,2);
//            }
//        });

    }

    private void redirectTimeline() {
        Intent intent = new Intent(getApplicationContext(), MainTab.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void initToolbar() {
        llActionGeral = (LinearLayout) findViewById(R.id.ll_mudi_action);
        setupToolBar();
        updateImageProfile(MaiMudiApplication.getInstance().getLoggedUser().getImageProfile() != null);
        updateNomeUsuario(llActionGeral);
    }

    private void initCompletionViewSeguidores(List<User> users) {
        adapter = new FilteredArrayAdapter<User>(context, R.layout.auto_complete_item, users) {

            @Override
            protected boolean keepObject(User user, String mask) {
                mask = mask.toLowerCase();
                return user.getNome().toLowerCase().startsWith(mask) || user.getNickname().toLowerCase().startsWith(mask);
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    LayoutInflater l = (LayoutInflater)getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
                    convertView = l.inflate(R.layout.auto_complete_item, parent, false);
                }

                User u = getItem(position);
                ((TextView)convertView.findViewById(R.id.user)).setText(u.getNome());
                ((TextView)convertView.findViewById(R.id.nickname)).setText(u.getNickname());
//                ((ImageView)convertView.findViewById(R.id.profile_image)).setImageResource(u.getImg());
                return convertView;
            }

        };
    }

    private void configCompletionView() {
        /*userCompletionView = (UserCompletionView)findViewById(R.id.searchView);
        userCompletionView.setAdapter(adapter);
        userCompletionView.setTokenListener(this);
        userCompletionView.allowCollapse(true);
        userCompletionView.setThreshold(2);
        userCompletionView.setSplitChar(' ');
        userCompletionView.setTokenClickStyle(TokenCompleteTextView.TokenClickStyle.Select);
        userCompletionView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                userCompletionView.setText(adapterView.getItemAtPosition(i).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        User user = new User();
        user.setNome("Todos meus ouvintes");
        userCompletionView.addObject(user);*/
    }

    private void recuperaListaUsersFirebase(final Bundle savedInstanceState){
        final List<User> lista = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(getResources().getString(R.string.ref_firebase_users));
        ref.orderByChild(getResources().getString(R.string.ref_firebase_nickname)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data: dataSnapshot.getChildren()) {
                    User user = new User();
                    user.setNome(data.child(getResources().getString(R.string.user_nome)).getValue().toString());
                    user.setNickname(data.child(getResources().getString(R.string.user_nickname)).getValue().toString());
                    lista.add(user);
                }
//                initCompletionViewSeguidores(lista);
//                configCompletionView();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
//                initCompletionViewSeguidores(users = new ArrayList<User>());
//                configCompletionView();
                setupDialog(context, getResources().getString(R.string.msg_falha_seguidores));
                getSimpleArcDialog().show();
            }
        });
    }

    private void gravaPostFirebase(Post post) {
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(getResources().getString(R.string.ref_firebase_posts));
        final String postCriado = ref.push().getKey();
        ref.child(postCriado).setValue(post).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                redirectTimeline();
            }
        });
    }

    private Post populaPost(){
        Post post = new Post();
        post.setUser(FirebaseAuth.getInstance().getCurrentUser().getUid());
        post.setPost_img(selectMusic.getImgSpotify());
        post.setPreview(selectMusic.getPreviewUrl());
        Date date = new Date();
        post.setTimestamp(date.getTime());
        if (!lyric.getText().equals("")){
            post.setLyric(lyric.getText().toString());
        }
        post.setNickname(MaiMudiApplication.getInstance().getLoggedUser().getNickname());
        if (!mood.getText().equals("")){
            post.setFeeling(mood.getText().toString());
        }
        if (!((MaterialSpinner) spinner3).getHint().equals(spinner3.getSelectedItem())){
            post.setAction(spinner3.getSelectedItem().toString());
        }
        post.setTo("TodosOuvintes");
        if (MaiMudiApplication.getInstance().getLoggedUser().getProfile_img() != null && !MaiMudiApplication.getInstance().getLoggedUser().getProfile_img().isEmpty()){
            post.setUser_img(MaiMudiApplication.getInstance().getLoggedUser().getProfile_img());
        }
        return post;
    }

    /**
     * Busca na api do Discosound, informações como ano, gênero e nacionalidade da música selecionada.
     */
    private void buscaInfosComplementaresPost(final Post post){
        DiscogsFactory.getInstance().getDiscogsApi().getInfoGrafico(post.getTrack(), post.getArtist(), 3, 1).enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if (response.isSuccessful() && response.code() == 200 && ((JsonObject) response.body()).getAsJsonArray("results").size() > 0){
                    JsonObject obj = ((JsonObject) response.body()).getAsJsonArray("results").get(0).getAsJsonObject();
                    GsonDiscoSound mGson = new Gson().fromJson(obj, GsonDiscoSound.class);
                    finalizaPost(post, mGson.getCountry(), mGson.getGenre().get(0), mGson.getYear());
                } else {
                    Log.d(TAG, "Não foi possível recuperar (year, country e genre) para a música: "+post.getTrack()+"-Artista:"+post.getArtist());
                    finalizaPost(post, "Unknown", "Unknown", "Unknown");
                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                Log.e(TAG, "Falha ao buscar informações no Discosound. User = '"
                        +FirebaseAuth.getInstance().getCurrentUser().getUid()+"' - Música: "+post.getTrack()+"" +
                        ", artista = "+post.getArtist()+"\n. Error = "+t.getMessage());
                finalizaPost(post, "Unknown", "Unknown", "Unknown");
            }
        });
    }

    private void finalizaPost(Post post, String country, String genre, String year) {
        setDateCharts("artist", replaceCaracter(removeCaracteresEspeciais(selectMusic.getArtista())));
        setDateCharts("country", replaceCaracter(removeCaracteresEspeciais(country)));
        setDateCharts("genre", replaceCaracter(removeCaracteresEspeciais(genre)));
        if (year != null && !year.equals("null")) {
            setDateCharts("year", year);
        }
        gravaPostFirebase(post);
    }

    private String removeCaracteresEspeciais(String originalString){
        String strReplace = originalString;
        for (String chars:charsEspecials) {
            if (originalString.contains(chars)){
                strReplace = strReplace.replace(chars,"");
            }
        }
        return strReplace;
    }

    private String replaceCaracter(String originalString) {
        String strReplace = originalString;
        if (originalString.contains("/")) {
            strReplace = strReplace.replace("/",",");
        }
        return strReplace;
    }

    private void setDateCharts(final String child, final String key) {
        final DatabaseReference refCharts = FirebaseDatabase.getInstance().getReference().child(getResources().getString(R.string.ref_firebase_charts));
        refCharts.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(child+"/"+key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(getResources().getString(R.string.ref_firebase_charts));
                if (dataSnapshot.exists()){
                    Long val = Long.parseLong(dataSnapshot.getValue().toString());
                    Map<String, Object> map = new HashMap<>();
                    map.put(key, ++val);
                    reference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(child).updateChildren(map);
                } else {
                    reference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(child+"/"+key).setValue(1);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private MoodTopAdapter.onClickListener onClickListener() {
        return new MoodTopAdapter.onClickListener() {
            @Override
            public void onClickListener(View view, int idx) {
                mood.setText(moods.get(idx).getNome());
            }
        };
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    private void updateTokenConfirmation() {
        StringBuilder sb = new StringBuilder("Current tokens:\n");
        for (Object token: userCompletionView.getObjects()) {
            sb.append(token.toString());
            sb.append("\n");
        }
    }


    @Override
    public void onTokenAdded(Object token) {
        updateTokenConfirmation();
    }

    @Override
    public void onTokenRemoved(Object token) {
        updateTokenConfirmation();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 1){
            if(resultCode == Activity.RESULT_OK){
                Bundle MBuddle = data.getExtras();
                String result= MBuddle.getString("result");
                lyric.setText(result);
            }
        }else if(requestCode == 2){
            if(resultCode == Activity.RESULT_OK){
                Bundle MBuddle = data.getExtras();
                Bitmap result= MBuddle.getParcelable("result");
                album.setImageBitmap(result);
            }
        }
    }

    @Override
    public void returnBitmap(Bitmap bitmap) {
        Log.d(TAG, "Bitmap recuperado da imagem do álbum = "+bitmap);
        album.setImageBitmap(bitmap);
        Bitmap bmp = ((BitmapDrawable)album.getDrawable()).getBitmap();
        Palette.from(bmp).generate(new Palette.PaletteAsyncListener() {
            @Override
            public void onGenerated(Palette palette) {
                textSwatch = palette.getVibrantSwatch();
                if(textSwatch != null){
                    llAction.setBackgroundColor(textSwatch.getRgb());
                    music.setTextColor(textSwatch.getTitleTextColor());
                }
            }
        });
    }

    private class GsonDiscoSound {
        private String year;
        private String country;
        private List<String> genre;

        public String getYear() {
            return year;
        }

        public String getCountry() {
            return country;
        }

        public List<String> getGenre() {
            return genre;
        }
    }
}
