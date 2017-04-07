package com.johnsontechinc.learnrealm;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;

import org.reactivestreams.Subscription;

import io.reactivex.Flowable;
import io.reactivex.FlowableSubscriber;
import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.PUT;

public class RetrofitActivity extends AppCompatActivity {

    final static String TOKEN_API_ME_GET = " Bearer pQBUMLDXkMKxLjE-thMnU2ENoMrta7Y7bsXujpQ72hf2b0QH_yPd1fU8a5SBaP7DxvUH7I0SjIWbpH64Sr9pFTiII367nQC6f5kvaqCTjTV4Gg4nYeqT-Uq6I3-nT06KiozMj_nh149D3hZHnjl77sh_hLwU4fntHU9qNtg1Y0L7MZQkcBhrERz-jqvt3qWIwVrrXDEs7RQaRdKZ8z9Wo_qaFMrIIFkO68axo7SRQG20N_UjPqF6GpjLTsm5v54C8RC8HrVIEkVLNtK4E_UmUKPjWbGritlF97IJdqx8E2j8PxE_5Dc607PeJUoP4eKkYvVxNDjcthnh7uSfBFu9d0R527Q";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retrofit);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.10.208.150/")
                .addConverterFactory(ScalarsConverterFactory.create())
                .client(new OkHttpClient())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

//        TestService testService = retrofit.create(TestService.class);
//        Call<TestBean> testBeanCall = testService.getTestRx();
//
//        testBeanCall.enqueue(new Callback<TestBean>() {
//            @Override
//            public void onResponse(Call<TestBean> call, Response<TestBean> response) {
//
//            }
//
//            @Override
//            public void onFailure(Call<TestBean> call, Throwable t) {
//
//            }
//        });

        TestServiceRx serviceRx = retrofit.create(TestServiceRx.class);
//        Observable observable = serviceRx.getTestRx(TOKEN_API_ME_GET);
//
//        observable
//                .subscribeOn(Schedulers.io())
//                .subscribe(x -> Log.d("debug", "onNext:" + x.toString()),
//                        throwable -> Log.d("debug", "onError:" + throwable.toString()));

        ChangePersonalInfoRequest request = new ChangePersonalInfoRequest();
        request.account = "HelloWorld";

        String json = new Gson().toJson(request);
        Observable observable = serviceRx.setUser(TOKEN_API_ME_GET, json);
        observable
                .subscribeOn(Schedulers.io())
                .subscribe(x -> {
                    Log.d("debug", "onNext:" + x.toString());
                }, e -> {
                    Log.d("debug", "onError:" + e.toString());

                });


//        Call<String> stringCall = serviceRx.getTestOriginal(TOKEN_API_ME_GET);
//        stringCall.enqueue(new Callback<String>() {
//            @Override
//            public void onResponse(Call<String> call, Response<String> response) {
//                Log.d("debug","onResponse");
//
//                if(response.isSuccessful()){
//                    Log.d("debug","succ");
//                    Log.d("debug",response.body());
//
//                }else {
//                    Log.d("debug","onUnSucc, Code"+ response.raw().code());
//                }
//
//            }
//
//            @Override
//            public void onFailure(Call<String> call, Throwable t) {
//
////                t.printStackTrace();
//                Log.d("debug","onFailure:");
//            }
//        });


    }

    interface TestService {

        @GET("/")
        Call<TestBean> getTest();
    }

    interface TestServiceRx {

        @GET("/api/Me")
        Call<String> getTestOriginal(@Header("Authorization") String token);

        @GET("/api/me/avatar")
        Observable<String> getTestRx(@Header("Authorization") String token);

        @Headers("Content-Type: application/json")
        @PUT("/api/Me")
        Observable<String> setUser(@Header("Authorization") String token, @Body String body);

    }

    class TestBean {

    }


    public class ChangePersonalInfoRequest {

        @Expose
        private String createTime;

        @Expose
        private String email;

        @Expose
        private String nickName;

        @Expose
        private String avatarIcon;

        @Expose
        private String tel;

        @Expose
        private String account;

        @Expose
        private String weChat;

        @Expose
        private String memberId;

        @Expose
        private String qq;

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getNickName() {
            return nickName;
        }

        public void setNickName(String nickName) {
            this.nickName = nickName;
        }

        public String getAvatarIcon() {
            return avatarIcon;
        }

        public void setAvatarIcon(String avatarIcon) {
            this.avatarIcon = avatarIcon;
        }

        public String getTel() {
            return tel;
        }

        public void setTel(String tel) {
            this.tel = tel;
        }

        public String getAccount() {
            return account;
        }

        public void setAccount(String account) {
            this.account = account;
        }

        public String getWeChat() {
            return weChat;
        }

        public void setWeChat(String weChat) {
            this.weChat = weChat;
        }

        public String getMemberId() {
            return memberId;
        }

        public void setMemberId(String memberId) {
            this.memberId = memberId;
        }

        public String getQq() {
            return qq;
        }

        public void setQq(String qq) {
            this.qq = qq;
        }

        @Override
        public String toString() {
            return "ClassPojo [createTime = " + createTime + ", email = " + email + ", nickName = " + nickName + ", avatarIcon = " + avatarIcon + ", tel = " + tel + ", account = " + account + ", weChat = " + weChat + ", memberId = " + memberId + ", qq = " + qq + "]";
        }
    }
}
