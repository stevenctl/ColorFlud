package co.sugarware.colorflud;

public interface GoogleResolver {

    void signIn();
    void signOut();
    void rateGame();
    void submitScore(long score);
    void showScores();
    boolean isSignedIn();

}
