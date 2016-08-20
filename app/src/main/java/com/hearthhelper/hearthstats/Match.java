package com.hearthhelper.hearthstats;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Value;

/**
 * Created by gunterj on 8/18/16.
 */
@Getter
public class Match {
    private int deck_id;
    private String oppclass;
    private String result;
    private String coin;
    private String mode;

    public Match(int deck_id, String oppclass, boolean won, boolean coin, boolean ranked) {
        this.deck_id = deck_id;
        this.oppclass = oppclass;
        this.result = won ? "Win" : "Loss";
        this.coin = coin ? "true" : "false";
        this.mode = ranked ? "Ranked" : "Casual";
    }
}
