package com.hearthhelper;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.hearthhelper.hearthstats.Deck;
import com.hearthhelper.hearthstats.HearthstatsApi;
import com.hearthhelper.hearthstats.HearthstatsApiProvider;
import com.hearthhelper.hearthstats.Match;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by gunterj on 8/20/16.
 */
public class MatchRecordViewController implements SharedPreferences.OnSharedPreferenceChangeListener {

    private final View view;
    private final SharedPreferences preferences;

    @BindView(R.id.button_coin)
    ToggleButton buttonCoin;
    @BindView(R.id.button_ranked)
    ToggleButton buttonRanked;
    @BindView(R.id.button_win)
    ToggleButton buttonWin;
    @BindView(R.id.button_record)
    Button record;
    @BindView(R.id.spinner_player)
    Spinner spinnerPlayer;
    @BindView(R.id.spinner_opponent)
    Spinner spinnerOpponent;

    @BindView(R.id.text_record)
    TextView textRecord;
    @BindView(R.id.error)
    TextView textError;


    private HearthstatsApi hearthstatsApi;

    public MatchRecordViewController(View view){
        this.view = view;
        ButterKnife.bind(this, view);

        preferences = PreferenceManager.getDefaultSharedPreferences(view.getContext());
        preferences.registerOnSharedPreferenceChangeListener(this);

        connectToHearthstats();
        bindRecord();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        connectToHearthstats();
    }

    private void connectToHearthstats() {
        String email = preferences.getString("hearthstats_email", null);
        String password = preferences.getString("hearthstats_password", null);
        if (email == null || password == null) {
            textError.setText("Please set your login details in settings.");
            return;
        }
        Observable.create(new HearthstatsApiProvider(email, password))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<HearthstatsApi>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        textError.setText("Authentication failed." + e.getMessage());
                    }

                    @Override
                    public void onNext(HearthstatsApi hearthstatsApi) {
                        MatchRecordViewController.this.hearthstatsApi = hearthstatsApi;
                        populateDeckList();
                    }
                });
    }

    private void populateDeckList() {
        Observable.create(hearthstatsApi.getDecks())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Deck>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        textError.setText("Deck list retreival failed." + e.getMessage());
                    }

                    @Override
                    public void onNext(List<Deck> decks) {
                        spinnerPlayer.setAdapter(new ArrayAdapter<Deck>(view.getContext(), android.R.layout.simple_spinner_item, decks));
                        spinnerOpponent.setAdapter(new ArrayAdapter<Deck>(view.getContext(), android.R.layout.simple_spinner_item, Deck.opponentDecks()));
                    }
                });
    }

    private void bindRecord() {
        record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Deck selectedDeck = (Deck) spinnerPlayer.getSelectedItem();
                Deck opponent = (Deck) spinnerOpponent.getSelectedItem();
                Match match = new Match(selectedDeck.getId(), opponent.getName(), buttonWin.isChecked(), buttonCoin.isChecked(), buttonRanked.isChecked());
                Observable.create(hearthstatsApi.recordMatch(match))
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Subscriber<Boolean>() {
                            @Override
                            public void onCompleted() {
                            }

                            @Override
                            public void onError(Throwable e) {
                                textError.setText("Error saving match result" + e.getMessage());
                            }

                            @Override
                            public void onNext(Boolean aBoolean) {
                            }
                        });
            }
        });
    }
}
