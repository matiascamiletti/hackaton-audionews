package com.mobileia.audionews.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.mobileia.audionews.R;
import com.mobileia.audionews.activity.DetailActivity;
import com.mobileia.audionews.adapter.NewsAdapter;
import com.mobileia.audionews.library.MCSpeech;
import com.mobileia.audionews.library.MCSpeechListener;
import com.mobileia.audionews.model.LNNews;
import com.mobileia.audionews.service.LaNacion;
import com.mobileia.audionews.service.ServiceListener;

import java.util.List;

public class NewsFragment extends Fragment implements AbsListView.OnItemClickListener {

    /**
     * The fragment's ListView/GridView.
     */
    private AbsListView mListView;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private NewsAdapter mAdapter;

    protected MCSpeech mSpeech;

    private int mPositionSpeech = 0;

    private boolean isSpeeching = false;

    public static NewsFragment newInstance() {
        NewsFragment fragment = new NewsFragment();
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public NewsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAdapter = new NewsAdapter(getActivity());
        LaNacion.getLastNews(getActivity(), new ServiceListener() {
            @Override
            public void onComplete(List<LNNews> list) {
                mAdapter.setList(list);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news, container, false);

        // Set the adapter
        mListView = (AbsListView) view.findViewById(android.R.id.list);
        ((AdapterView<ListAdapter>) mListView).setAdapter(mAdapter);

        // Set OnItemClickListener so we can be notified on item clicks
        mListView.setOnItemClickListener(this);

        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        LNNews n = mAdapter.getList().get(position);
        DetailActivity.createInstance(getActivity(), n);
    }

    /**
     * The default content for this Fragment has a TextView that is shown when
     * the list is empty. If you would like to change the text, call this method
     * to supply the text it should use.
     */
    public void setEmptyText(CharSequence emptyText) {
        View emptyView = mListView.getEmptyView();

        if (emptyView instanceof TextView) {
            ((TextView) emptyView).setText(emptyText);
        }
    }

    public void startSpeech(MCSpeech speech){
        mSpeech = speech;

        if(mPositionSpeech > 0){
            inactiveNews(mPositionSpeech-1);
        }
        mPositionSpeech = 0;
        isSpeeching = true;
        nextSpeech();
    }

    public void pauseSpeech(){
        mSpeech.stop();
        isSpeeching = false;
        inactiveNews(mPositionSpeech);
    }

    public void resumeSpeech(){
        isSpeeching = true;
        nextSpeech();
    }

    public void nextSpeech(){
        if(mPositionSpeech > 0){
            inactiveNews(mPositionSpeech-1);
        }

        if(mAdapter.getList().size() <= mPositionSpeech){
            pauseSpeech();
            return;
        }

        final LNNews n = mAdapter.getList().get(mPositionSpeech);
        mSpeech.speak(n.category, String.valueOf(n.identifier) + "b", null);
        mSpeech.speak(n.title, String.valueOf(n.identifier), new MCSpeechListener() {
            @Override
            public void onComplete(String utteranceId) {

                if(utteranceId.compareTo(String.valueOf(n.identifier)) != 0 || !isSpeeching){
                    return;
                }

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mPositionSpeech++;
                        nextSpeech();
                    }
                });

            }
        });
        n.isSpeeching = true;

        updateItemAtPosition(mPositionSpeech);
    }

    public void nextNews(){
        pauseSpeech();
        mPositionSpeech++;
        isSpeeching = true;
        nextSpeech();
    }

    public void prevNews(){
        pauseSpeech();

        if(mPositionSpeech > 0){
            mPositionSpeech--;
        }

        isSpeeching = true;
        nextSpeech();
    }

    private void inactiveNews(int position){
        LNNews old = mAdapter.getList().get(position);
        old.isSpeeching = false;
        updateItemAtPosition(position);
    }

    private void updateItemAtPosition(int position) {
        int visiblePosition = mListView.getFirstVisiblePosition();
        View view = mListView.getChildAt(position - visiblePosition);
        mListView.getAdapter().getView(position, view, mListView);
    }

}
