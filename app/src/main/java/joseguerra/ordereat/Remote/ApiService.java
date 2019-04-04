package joseguerra.ordereat.Remote;


import joseguerra.ordereat.modelo.DataMessage;
import joseguerra.ordereat.modelo.MyResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ApiService {

@Headers(

        {
                "Content-Type:application/json",
                "Authorization:key=AAAAsZ_KMgA:APA91bFDX48gCGbCyhZAtMFx8MEDWDe5qIKkvnuAskNBOXSXIdkGz8x1HcE-xETTNsQAdEbE7_Bl-44kJCIqerLwqwnAokwF8nfN8GG2IZg67VL9MwCFQNRIrI23OJQTFLdIvDkk1Kq9"
        }

)
 @POST("fcm/send")
   Call<MyResponse> sendNotification(@Body DataMessage body);
}
