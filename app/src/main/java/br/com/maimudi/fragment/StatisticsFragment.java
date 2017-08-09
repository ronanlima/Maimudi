package br.com.maimudi.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import br.com.maimudi.R;
import br.com.maimudi.activity.MainTab;
import br.com.maimudi.adapter.TabAdapter;
import br.com.maimudi.model.Friend;
import br.com.maimudi.services.PostChartService;

/**
 * Created by brunolemgruber on 16/03/16.
 */
public class StatisticsFragment extends Fragment implements OnChartValueSelectedListener, ProfileFragment.OnDrawChartByUserData {
    protected static final String TAG = StatisticsFragment.class.getCanonicalName().toUpperCase();

    private LinearLayout llCharts, llChartsAlert;
    private ProgressBar progressGenre, progressArtist;
    private TextView tvErroChartGenre, tvErroChartArtist;
    private PieChart mChartEstiloMusical,mChartArtista;
    private Friend friend;
    private Map<String, Integer> artists = new HashMap<>();
    private Map<String, Integer> genres = new HashMap<>();
    private Integer sumArtists, sumGenres;

    @Override
    public void drawCharts(Friend f) {
        this.friend = f;
        onResume();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.statics, container, false);

        MainTab mainTab = (MainTab) getActivity();
        TabAdapter adapter = (TabAdapter) mainTab.getmViewPager().getAdapter();
        friend = adapter.getFriend();

        if (adapter.getListenerCharts() == null){
            adapter.setListenerCharts(this);
        }

        llCharts = (LinearLayout) view.findViewById(R.id.ll_charts);
        llChartsAlert = (LinearLayout) view.findViewById(R.id.ll_erro_charts);
        progressGenre = (ProgressBar) view.findViewById(R.id.loading_chartRock);
        progressArtist = (ProgressBar) view.findViewById(R.id.loading_chartArtist);
        tvErroChartGenre = (TextView) view.findViewById(R.id.tv_erro_chartRock);
        tvErroChartArtist = (TextView) view.findViewById(R.id.tv_erro_chartArtist);

        mChartEstiloMusical = (PieChart) view.findViewById(R.id.chartRock);
        mChartArtista = (PieChart) view.findViewById(R.id.chart_artist);

        return  view;
    }

    @Override
    public void onResume() {
        super.onResume();
        PostChartService.getArtistsFromPosts(getContext(), friend.getUser(), getListenerToPosts());
    }

    public static <K, V extends Comparable<? super V>> Map<String, Integer> sortByValue( Map<String, Integer> map ) {
        List<Map.Entry<String, Integer>> list = new LinkedList<>( map.entrySet() );
        Collections.sort( list, new Comparator<Map.Entry<String, Integer>>()  {
            public int compare( Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2 ) {
                return (o2.getValue()).compareTo( o1.getValue() );
            }
        });

        Map<String, Integer> result = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> entry : list) {
            result.put( entry.getKey(), entry.getValue() );
        }
        return result;
    }

    private PostChartService.onFinishReadPosts getListenerToPosts(){
        return new PostChartService.onFinishReadPosts() {
            @Override
            public void onFinishRead(Map<String, Integer> artists, Map<String, Integer> genres, int sumArtists, int sumGenres) {
                if (sumArtists == 0 && sumGenres == 0){
                    llChartsAlert.setVisibility(View.VISIBLE);
                    llCharts.setVisibility(View.GONE);
                } else {
                    llChartsAlert.setVisibility(View.GONE);
                    llCharts.setVisibility(View.VISIBLE);
                    progressArtist.setVisibility(View.GONE);
                    if (sumArtists == 0){
                        tvErroChartArtist.setVisibility(View.VISIBLE);
                    } else {
                        setSumArtists(sumArtists);
                        setArtists(sortByValue(artists));
                        if (getArtists().size() > 5) {
                            Map<String, Integer> aux = getMapAux(getArtists());
                            setChartArtista(mChartArtista, aux);
                        } else {
                            setChartArtista(mChartArtista, getArtists());
                        }
                        mChartArtista.setVisibility(View.VISIBLE);
                    }

                    progressGenre.setVisibility(View.GONE);
                    if (sumGenres == 0){
                        tvErroChartGenre.setVisibility(View.VISIBLE);
                    } else {
                        setSumGenres(sumGenres);
                        setGenres(sortByValue(genres));
                        if (getGenres().size() > 5) {
                            Map<String, Integer> aux = getMapAux(getGenres());
                            setChartEstiloMusical(mChartEstiloMusical, aux);
                        } else {
                            setChartEstiloMusical(mChartEstiloMusical, getGenres());
                        }
                        mChartEstiloMusical.setVisibility(View.VISIBLE);
                    }

                }
            }
        };
    }

    @NonNull
    private Map<String, Integer> getMapAux(Map<String, Integer> map) {
        Map<String, Integer> aux = new HashMap<>();
        int i = 0, countOutros = 0;
        for (Map.Entry entry:map.entrySet()) {
            if (i < 5) {
                aux.put(entry.getKey().toString(), new Integer(entry.getValue().toString()));
            } else {
                countOutros += new Integer(entry.getValue().toString()).intValue();
            }
            i++;
        }
        aux.put("Outros", new Integer(countOutros));
        return aux;
    }

    private void setData(float range, PieChart pieChart, Map<String, Integer> map, int sumData) {
        ArrayList<Entry> yVals1 = new ArrayList<>();
        int i = 0;
        for (Map.Entry item: map.entrySet()) {
            yVals1.add(new Entry(Float.parseFloat(item.getValue().toString()) * range / sumData, i++));
        }

        ArrayList<String> xVals = new ArrayList<>();
        for (Map.Entry item: map.entrySet()) {
            xVals.add(item.getKey().toString());
        }

        PieDataSet dataSet = new PieDataSet(yVals1, "");
        dataSet.setSliceSpace(4f);
        dataSet.setSelectionShift(8f);

        ArrayList<Integer> colors = new ArrayList<>();

        colors.add(Color.rgb(46, 204, 113));
        colors.add(Color.rgb(26, 188, 156));
        colors.add(Color.rgb(22, 160, 133));
        colors.add(Color.rgb(39, 174, 96));
        colors.add(Color.rgb(52, 152, 219));
        colors.add(Color.rgb(189, 195, 199));
        colors.add(Color.rgb(149, 165, 166));
        colors.add(Color.rgb(127, 140, 141));
        colors.add(Color.rgb(236, 240, 241));
        colors.add(Color.rgb(192, 57, 43));
        colors.add(Color.rgb(231, 76, 60));

        dataSet.setColors(colors);

        PieData data = new PieData(xVals, dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(7f);
        data.setValueTextColor(Color.BLACK);
        pieChart.setData(data);
        pieChart.highlightValues(null);
        pieChart.invalidate();
    }

    private void setChartEstiloMusical(PieChart mChart, Map<String, Integer> map){
        mChart.setUsePercentValues(false);
        mChart.setDragDecelerationFrictionCoef(0.95f);

        mChart.setDrawHoleEnabled(true);
        mChart.setHoleColor(Color.WHITE);

        mChart.setTransparentCircleColor(Color.WHITE);
        mChart.setTransparentCircleAlpha(80);

        mChart.setHoleRadius(48f);
        mChart.setTransparentCircleRadius(58f);

        mChart.setDrawCenterText(true);
        mChart.setCenterText(getResources().getString(R.string.label_estilo_musical));

        mChart.setRotationAngle(0);
        mChart.setRotationEnabled(true);
        mChart.setHighlightPerTapEnabled(true);
        mChart.getLegend().setEnabled(false);
        mChart.setDescription("");

        mChart.setOnChartValueSelectedListener(this);
        setData(100, mChart, map, getSumGenres());
        mChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);
    }

    private void setChartArtista(PieChart mChart, Map<String, Integer> map){
        mChart.setUsePercentValues(true);
        mChart.setDescription("");
        mChart.getLegend().setEnabled(false);
        //mChart.setExtraOffsets(5, 10, 5, 5);

        mChart.setDragDecelerationFrictionCoef(0.95f);

        mChart.setDrawHoleEnabled(true);
        mChart.setHoleColor(Color.WHITE);

        mChart.setTransparentCircleColor(Color.WHITE);
        mChart.setTransparentCircleAlpha(80);

        mChart.setHoleRadius(48f);
        mChart.setTransparentCircleRadius(58f);

        mChart.setDrawCenterText(true);
        mChart.setCenterText(getResources().getString(R.string.label_artista));

        mChart.setRotationAngle(0);
        mChart.setRotationEnabled(true);
        mChart.setHighlightPerTapEnabled(true);

        mChart.setOnChartValueSelectedListener(this);

        setData(100, mChart, map, getSumArtists());
        mChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);
    }

    @Override
    public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {

    }

    @Override
    public void onNothingSelected() {

    }

    public Map<String, Integer> getArtists() {
        return artists;
    }

    public void setArtists(Map<String, Integer> artists) {
        this.artists = artists;
    }

    public Map<String, Integer> getGenres() {
        return genres;
    }

    public void setGenres(Map<String, Integer> genres) {
        this.genres = genres;
    }

    public Integer getSumArtists() {
        return sumArtists;
    }

    public void setSumArtists(Integer sumArtists) {
        this.sumArtists = sumArtists;
    }

    public Integer getSumGenres() {
        return sumGenres;
    }

    public void setSumGenres(Integer sumGenres) {
        this.sumGenres = sumGenres;
    }

}
