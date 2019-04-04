package joseguerra.ordereat.Remote;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class RetrofitClient {

    private static Retrofit retrofit= null;
    private static Retrofit retrofit2= null;
    private static Retrofit retrofit3= null;

    public static Retrofit getClient(String baseURL)
    {
        if (retrofit== null)
        {
            retrofit= new Retrofit.Builder()
                    .baseUrl(baseURL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static Retrofit getClientGoogleApi(String baseURL)
    {
        if (retrofit2== null)
        {
            retrofit2= new Retrofit.Builder()
                    .baseUrl(baseURL)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .build();
        }
        return retrofit2;
    }

    public static Retrofit getClientRuta(String baseURL)
    {
        if (retrofit3== null)
        {
            retrofit3= new Retrofit.Builder()
                    .baseUrl(baseURL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit3;
    }
}
