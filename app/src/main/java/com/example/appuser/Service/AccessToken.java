package com.example.appuser.Service;

import android.util.Log;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.common.collect.Lists;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class AccessToken {
    private final static String firebaseMessagingScope = "https://www.googleapis.com/auth/firebase.messaging";

    public String getAccessToken() {
        try {
            String jsonString = "{\n" +
                    "  \"type\": \"service_account\",\n" +
                    "  \"project_id\": \"clothing-57842\",\n" +
                    "  \"private_key_id\": \"279caaab09e3f73b65dfe4f845cbac2cdb598935\",\n" +
                    "  \"private_key\": \"-----BEGIN PRIVATE KEY-----\\nMIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCchEZ0AAUyinHq\\n4YoDQDnhJdNc0cYyCnsyXaqBkAekDDAUgOJvL59gWZiuLCh5jEVpul5rdJDE8/3b\\nSaMCf+r1oaO7dvo2GjfCvXGS43PmItNqGeeicRljOoF3/RdYmujaYdM90fL4eGeD\\nDUTPn6pd4hHtrpwFvYGAszny9Eugw4HAmcE0wIjro1Ol7+OdibJYIRfnPr6OfGti\\nzBdaTBdMr/6yxEPhROrZbyPrZUDCXwFCGoOdYu7o8+jdmT5+UtlOqSqLeKpsCJ1P\\nilC9s1/HxDjgP3oVBbFIEcdyejbOas7fgSgrfu4IfPjlotr4wcXJCRjCZWEVs1Gj\\nSI0yxaBhAgMBAAECggEACP4qpCCaK9i7ieDSkrQ+fxqyxbsB1b83a+Ho2Y/ieIEA\\nxDTRjczhLeKH/L3px24RUO1V3nuwk7hnJhHZjVu3LszOQQepgbFI8eX7oBJ0hs0K\\nc6awYet4p3RlmvTOQheIeH+1aPBmsvMME4IQKZPFcKZ8HDlzmnrUdYYWtshljgOG\\nrVX9x5NcPWqjPQK6z05zrlqoyBRQH7DEG+OH+gNqFQoPjac0+Ov2DGTdofth9+XV\\nD/p4Q4QKSzpPw0Q785Mc9k27yVrmWTRQj/MXYciCXYPsmfUq91tdheqcmOJcwkBH\\nh2KfG7hJsV8ZJiYBQemmW2TNrnxra7AmCEN8u+EViQKBgQDPPkiOlzJ4ezSLqeu9\\nQhJ3j6z+YsbIjCfpAIjZG1oxpbaLkjMEIX2CN8fM5DqHaUfEs4yTR3cgeXxciuKf\\n/6/IsDdOIif6SvPNHJoPiVX806L8D0YOMN1Wg6LCPbZSzb7HYQOznXQt5liINtn/\\nwDmRZ7z2iyLCwzphH5xpmmdTdQKBgQDBVtzmgD5dEv6pjP9v4Z4eRra3Zd5eGEwG\\nVV6vPCo3eePkV8EIztegFWe0HFfnY6RgotiSExIPrvVbMrnvgzi7m8evzoh9inKu\\n759LlLU2wuN287sMvl56s5Bmfm4vW2Vw7b0FtwteSoo/TvLGoHRVn0FjBT/5LXvH\\nqTL9H9eXvQKBgH7pv3lC8m+B083rwyflfHWnkONADkfmPNKdtzBtZbCdsMqMP525\\n/KhwDHVXFtiyCndVFTNkQnmqrBPoIZyl8jpOX7ADjsPifqcPavE0w2nPGTKPD/HE\\nzucaOeQF8s+Gm9xxdtjaBbnOJE1Wee37A8Yd50R8NE9dSzULLLgnDDu5AoGAZuVS\\n1X4FMsXMMPJwc9vwqg5aagrWKN/nfitrI/nyQeomJj2p54Ul7vXVZl77TZOTRTcF\\n6eAqaZxkym+Z02XUCMzknqVdsOrBFGdFMRVoyEc6hfE7aJHceJMeU94PyDZtK4fr\\nZKJBc6JaJ01eGeYFZBhI1WlS3cueJ+nd0CdlN0UCgYEAt0YAKhkftT7zyKWBZbsi\\nVi0qDqOPU3ue2YO0Ipyfn3tqVnwv5G3nKlyA7fJ5Ox03y1ojX9qd2tGMRGQmIELH\\n7hTVjJN7GERaaUq+TFupPoch8NT535I49Qk5dDyjcPM+8sIiQaTyv65rLzW7Zo0F\\n3aNKDFi/PRveRQPWEcRyGeg=\\n-----END PRIVATE KEY-----\\n\",\n" +
                    "  \"client_email\": \"firebase-adminsdk-wkc3c@clothing-57842.iam.gserviceaccount.com\",\n" +
                    "  \"client_id\": \"109178446408660498266\",\n" +
                    "  \"auth_uri\": \"https://accounts.google.com/o/oauth2/auth\",\n" +
                    "  \"token_uri\": \"https://oauth2.googleapis.com/token\",\n" +
                    "  \"auth_provider_x509_cert_url\": \"https://www.googleapis.com/oauth2/v1/certs\",\n" +
                    "  \"client_x509_cert_url\": \"https://www.googleapis.com/robot/v1/metadata/x509/firebase-adminsdk-wkc3c%40clothing-57842.iam.gserviceaccount.com\",\n" +
                    "  \"universe_domain\": \"googleapis.com\"\n" +
                    "}";

            InputStream inputStream = new ByteArrayInputStream(jsonString.getBytes(StandardCharsets.UTF_8));
            GoogleCredentials googleCredentials = GoogleCredentials.fromStream(inputStream).createScoped(Lists.newArrayList(firebaseMessagingScope));
            googleCredentials.refresh();
            String accessToken = googleCredentials.getAccessToken().getTokenValue();
            Log.d("AccessToken", "Access Token: " + accessToken);
            return accessToken;
        } catch (IOException e) {
            Log.e("AccessTokenError", "Error getting access token: " + e.getMessage(), e);
            return null;
        }
    }
}
