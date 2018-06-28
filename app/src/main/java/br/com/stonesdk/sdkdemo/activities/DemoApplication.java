package br.com.stonesdk.sdkdemo.activities;

import android.app.Application;

import java.util.List;

import stone.application.StoneStart;
import stone.user.UserModel;

/**
 * @author frodrigues
 * @since 28/06/2018
 */
public class DemoApplication extends Application {
    private static List<UserModel> users;

    @Override
    public void onCreate() {
        super.onCreate();
        users = StoneStart.init(this);
    }

    public static List<UserModel> getUsers() {
        return users;
    }

}
