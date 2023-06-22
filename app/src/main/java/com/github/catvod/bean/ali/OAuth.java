package com.github.catvod.bean.ali;

import android.text.TextUtils;

import com.github.catvod.ali.API;
import com.github.catvod.utils.FileUtil;
import com.github.catvod.utils.Prefers;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

public class OAuth {

    @SerializedName("token_type")
    private String tokenType;
    @SerializedName("access_token")
    private String accessToken;
    @SerializedName("refresh_token")
    private String refreshToken;

    /*
    * 把json字符串转为OAuth对象
    **/
    public static OAuth objectFrom(String str) {
        OAuth item = new Gson().fromJson(str, OAuth.class);
        return item == null ? new OAuth() : item;
    }
    /*
     * 返回Token类型
     **/
    public String getTokenType() {
        return TextUtils.isEmpty(tokenType) ? "" : tokenType;
    }

    public String getAccessToken() {
        return TextUtils.isEmpty(accessToken) ? "" : accessToken;
    }

    public String getRefreshToken() {
        return TextUtils.isEmpty(refreshToken) ? "" : refreshToken;
    }
    /*
     * 返回授权
     **/
    public String getAuthorization() {
        return getTokenType() + " " + getAccessToken();
    }
    /*
     * 清除Token
     **/
    public OAuth clean() {
        this.refreshToken = "";
        this.accessToken = "";
        return this;
    }
    /*
     * 保存Token
     **/
    public OAuth save() {
        FileUtil.write(API.get().getOAuthCache(), toString());
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
