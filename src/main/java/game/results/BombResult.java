package game.results;

/**
 * Created by lars on 11.02.15.
 */
public class BombResult implements Result {

    private final boolean ok;
    private final BombHitType hit;

    public BombResult(boolean ok, BombHitType hit) {
        this.ok = ok;
        this.hit = hit;
    }

    @Override
    public boolean isOk() {
        return ok;
    }

    public BombHitType getHit() {
        return hit;
    }
}
