package joseguerra.ordereat.common;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import joseguerra.ordereat.Remote.ApiService;
import joseguerra.ordereat.Remote.IGoogleService;
import joseguerra.ordereat.Remote.RetrofitClient;
import joseguerra.ordereat.modelo.User;

public class Common {

    public static String topicName= "News";

    public static final String INTENT_FOOD_ID = "FoodId" ;
    public static User currentUser; // usuario actual

    private static final String BASE_URL= " https://fcm.googleapis.com/";

    public static  String currentKey;

    public  static String restaurantSelectId;


    private static final String Google_API_URL= "https://maps.googleapis.com/";

    public static final String USER_PHONE="userPhone";

    public static ApiService getFCMservice()
    {
        return RetrofitClient.getClient(BASE_URL).create(ApiService.class);
    }

    public static IGoogleService getGoogleservice()
    {
        return RetrofitClient.getClientGoogleApi(Google_API_URL).create(IGoogleService.class);
    }

    public static String converCodeToStatus(String status) {

        switch (status) {
            case "0":
                return "Placed";
            case "1":
                return "On my way";
            case "2":
                return "Shipping";
            default:
                return "Shipped";
        }
    }

    public static boolean isConectInter(Context context)
    {
        ConnectivityManager connectivityManager= (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager!=null)
        {
            NetworkInfo[] info= connectivityManager.getAllNetworkInfo();
            if (info!=null)
            {
                for (NetworkInfo anInfo : info) {
                    if (anInfo.getState() == NetworkInfo.State.CONNECTED)
                        return true;
                }
            }
        }
        return false;
    }

    public static  final String Delete= "DELETE";
    public static  final String User_key= "User";
    public static  final String Password_key= "Password";
}