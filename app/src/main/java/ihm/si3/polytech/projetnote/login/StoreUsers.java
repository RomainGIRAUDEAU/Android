package ihm.si3.polytech.projetnote.login;

import com.google.android.gms.maps.model.LatLng;

public class StoreUsers {
    private static String userName;
    private static String mailAdress;
    private static String urlPicture;
    private static LatLng position;

    public static String getMailAdress() {
        return mailAdress;
    }

    public static void setMailAdress(String mailAdress) {
        StoreUsers.mailAdress = mailAdress;
    }

    public static String getUrlPicture() {
        return urlPicture;
    }

    public static void setUrlPicture(String urlPicture) {
        StoreUsers.urlPicture = urlPicture;
    }

    public static String getUserName() {
        return userName;
    }

    public static void setUserName(String userName) {
        StoreUsers.userName = userName;
    }

    public static LatLng getPosition() {
        return position;
    }


}
