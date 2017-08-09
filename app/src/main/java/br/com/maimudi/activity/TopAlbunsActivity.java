package br.com.maimudi.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.List;

import br.com.maimudi.R;
import br.com.maimudi.adapter.MudiTopAdapter;
import br.com.maimudi.services.AlbunsService;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by brunolemgruber on 17/03/16.
 */
public class TopAlbunsActivity extends BaseActivity {

    protected RecyclerView recyclerView1,recyclerView2,recyclerView3,recyclerView4;
    private List<Integer> topMaiMudi,topMaiMudi2,topMaiMudi3,topMaiMudi4;
    private LinearLayoutManager mLayoutManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.post_mudi_top);

//        this.topMaiMudi = AlbunsService.getTopAlbuns(this);
//        this.topMaiMudi2 = AlbunsService.getTopAlbuns(this);
//        this.topMaiMudi3 = AlbunsService.getTopAlbuns(this);
//        this.topMaiMudi4 = AlbunsService.getTopAlbuns(this);

        recyclerView1 = (RecyclerView) findViewById(R.id.recyclerView1);
        mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView1.setLayoutManager(mLayoutManager);
        recyclerView1.setItemAnimator(new DefaultItemAnimator());
        recyclerView1.setHasFixedSize(true);
        recyclerView1.setAdapter(new MudiTopAdapter(this, topMaiMudi,onClickListener()));

        recyclerView2 = (RecyclerView) findViewById(R.id.recyclerView2);
        mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView2.setLayoutManager(mLayoutManager);
        recyclerView2.setItemAnimator(new DefaultItemAnimator());
        recyclerView2.setHasFixedSize(true);
        recyclerView2.setAdapter(new MudiTopAdapter(this, topMaiMudi2,onClickListener()));

        recyclerView3 = (RecyclerView) findViewById(R.id.recyclerView3);
        mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView3.setLayoutManager(mLayoutManager);
        recyclerView3.setItemAnimator(new DefaultItemAnimator());
        recyclerView3.setHasFixedSize(true);
        recyclerView3.setAdapter(new MudiTopAdapter(this, topMaiMudi3,onClickListener()));

        recyclerView4 = (RecyclerView) findViewById(R.id.recyclerView4);
        mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView4.setLayoutManager(mLayoutManager);
        recyclerView4.setItemAnimator(new DefaultItemAnimator());
        recyclerView4.setHasFixedSize(true);
        recyclerView4.setAdapter(new MudiTopAdapter(this, topMaiMudi4,onClickListener()));

    }

    private MudiTopAdapter.onClickListener onClickListener() {
        return new MudiTopAdapter.onClickListener() {
            @Override
            public void onClickListener(View view, int idx) {

                startActivity(new Intent(TopAlbunsActivity.this,SelectMusicActivity.class));
            }
        };
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
