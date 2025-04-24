package com.example.IntegrationWithHubStaff.util;


public class HubstaffApiConstants {
    public static final String BASE_URL = "https://api.hubstaff.com/v2";
    public static final String AUTH_URL = "https://account.hubstaff.com/authorizations/new";
    public static final String TOKEN_URL = "https://account.hubstaff.com/access_tokens";

    public static final String ORGANIZATIONS_ENDPOINT = BASE_URL + "/organizations";
    public static final String ORGANIZATION_MEMBERS_TEMPLATE = BASE_URL + "/organizations/%s/members?include=users";

    public static final String APP_ACTIVITIES_DAILY_TEMPLATE =
            BASE_URL + "/organizations/%s/application_activities/daily?date[start]=%s&date[stop]=%s";

    public static final String OAUTH_RESPONSE_TYPE = "code";
    public static final String OAUTH_GRANT_TYPE = "authorization_code";
    public static final String REFRESH_GRANT_TYPE = "refresh_token";
}