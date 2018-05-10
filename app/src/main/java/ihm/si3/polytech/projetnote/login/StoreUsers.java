package ihm.si3.polytech.projetnote.login;

public class StoreUsers {
    private static String userName;
    private static String mailAdress;
    private static String urlPicture;

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
}
