package sil.sil.lis;

public interface LoggerInterface {

    void playGame();
    void play();
    boolean isDeveloper();
    boolean isCharging();
    int battaryLevel();
    String gamePlayString(String dest, String camp, String uId, String g_ad_id);
    boolean internetConnection();
    void apps();
}
