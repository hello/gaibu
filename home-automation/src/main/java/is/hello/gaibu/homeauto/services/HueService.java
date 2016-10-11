package is.hello.gaibu.homeauto.services;

import java.util.List;
import java.util.Map;

import is.hello.gaibu.homeauto.models.HueGroup;
import is.hello.gaibu.homeauto.models.HueLightState;
import is.hello.gaibu.homeauto.models.HueScene;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * Created by jnorgan on 10/5/16.
 */
public interface HueService {
  @GET("/v2/bridges")
  Call<List<Map<String, String>>> getBridges();

  @PUT("v2/bridges/{bridge_id}/0/config")
  Call<List<Map<String, Map<String,String>>>> setConfigValue(@Body final Map<String, Boolean> configValue);

  @POST("v2/bridges/{bridge_id}/")
  Call<List<Map<String, Map<String,String>>>> requestWhiteList(@Body final Map<String, String> deviceInfo);

  @GET("/v2/bridges/{bridge_id}/{whitelist_id}/groups")
  Call<Map<String, HueGroup>> getGroups();

  //TODO: Replace these return types with proper Objects
  @PUT("/v2/bridges/{bridge_id}/{whitelist_id}/groups/{group_id}/action")
  Call<List<Map<String, Map<String, String>>>> setGroupState(@Body final Map<String, Object> stateValues);

  @GET("/v2/bridges/{bridge_id}/{whitelist_id}/scenes")
  Call<Map<String, HueScene>> getScenes();

  @POST("/v2/bridges/{bridge_id}/{whitelist_id}/scenes")
  Call<List<Map<String, Map<String, String>>>> createScene(@Body final HueScene hueScene);

  @PUT("/v2/bridges/{bridge_id}/{whitelist_id}/scenes/{scene_id}/lightstates/{light_id}")
  Call<List<Map<String, Map<String, String>>>> setSceneLightState(@Path("scene_id") final String sceneId,
                                                                  @Path("light_id") final String lightId,
                                                                  @Body final HueLightState lightState);

}
