package br.com.maimudi.adapter;

import android.media.MediaPlayer;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import br.com.maimudi.fragment.FindUserFragments;
import br.com.maimudi.fragment.FriendsFragment;
import br.com.maimudi.fragment.ProfileFragment;
import br.com.maimudi.fragment.StatisticsFragment;
import br.com.maimudi.fragment.TimelineFragment;
import br.com.maimudi.model.Friend;
import br.com.maimudi.model.User;


/**
 * Created by brunolemgruber on 04/12/15.
 */
public class TabAdapter extends FragmentPagerAdapter {

    private Friend friend;
    private User user;
    private MediaPlayer mediaPlayer;
    private ProfileFragment.OnDrawChartByUserData listenerCharts;

    public TabAdapter(FragmentManager fm) {
        super(fm);
        setMediaPlayer(new MediaPlayer());
    }

    @Override
    public int getCount() {
        return 5;
    }


    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return TimelineFragment.newInstance(getMediaPlayer());
            case 1:
                return new FriendsFragment();
            case 2:
                return new FindUserFragments();
            case 3:
                StatisticsFragment sf = new StatisticsFragment();
                listenerCharts = sf;
                return sf;
            case 4:
                ProfileFragment f = ProfileFragment.newInstance(getMediaPlayer());
                return f;
            default:
                return null;
        }
    }

    public Friend getFriend() {
        return friend;
    }

    public void setFriend(Friend friend) {
        this.friend = friend;
    }

    public MediaPlayer getMediaPlayer() { return mediaPlayer; }

    public void setMediaPlayer(MediaPlayer mediaPlayer) { this.mediaPlayer = mediaPlayer; }

    public ProfileFragment.OnDrawChartByUserData getListenerCharts() {
        return listenerCharts;
    }

    public void setListenerCharts(ProfileFragment.OnDrawChartByUserData listenerCharts) {
        this.listenerCharts = listenerCharts;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
