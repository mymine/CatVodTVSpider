package com.github.catvod.bean.ali;

import android.text.TextUtils;

import com.github.catvod.ali.API;
import com.github.catvod.utils.FileUtil;
import com.github.catvod.utils.Prefers;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

public class User {

    @SerializedName("default_drive_id")
    private String driveId;
    @SerializedName("user_id")
    private String userId;
    @SerializedName("token_type")
    private String tokenType;
    @SerializedName("access_token")
    private String accessToken;
    @SerializedName("refresh_token")
    private String refreshToken;

    /*
     * 把json字符串转为User对象
     **/
    public static User objectFrom(String str) {
        User item = new Gson().fromJson(str, User.class);
        return item == null ? new User() : item;
    }

    /*
     * 各成员变量的get and set方法
     **/
    public String getDriveId() {
        return TextUtils.isEmpty(driveId) ? "" : driveId;
    }

    public String getUserId() {
        return TextUtils.isEmpty(userId) ? "" : userId;
    }

    public String getTokenType() {
        return TextUtils.isEmpty(tokenType) ? "" : tokenType;
    }

    public String getAccessToken() {
        return TextUtils.isEmpty(accessToken) ? "" : accessToken;
    }

    public String getRefreshToken() {
        return TextUtils.isEmpty(refreshToken) ? "" : refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }


    /*
    * 返回TokenType+AccessToken
    * */
    public String getAuthorization() {
        return getTokenType() + " " + getAccessToken();
    }

    /*
     * 清除Token
     **/
    public User clean() {
        this.refreshToken = "";
        this.accessToken = "";
        return this;
    }
    /*
     * 保存Token
     **/
    public User save() {
        FileUtil.write(API.get().getUserCache(), toString());
        return this;
    }
    /*
     * 将本对象转换成json字符串
     **/
    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
