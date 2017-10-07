package xposed.leewp14.NEClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

import static de.robv.android.xposed.XposedBridge.log;
import static xposed.leewp14.NEClient.Utility.optString;

class Song {
    long id;
    int code;
    int br;
    int fee;
    float gain;
    String md5;
    int payed;
    long size;
    String type;
    JSONObject uf;
    String url;

    String matchedPlatform;
    String matchedSongName;
    String matchedArtistName;
    boolean matchedDuration;

    static Song parseFromOther(JSONObject songJson) {
        Song song = new Song();
        song.id = songJson.optLong("id");
        song.code = songJson.optInt("code");
        song.br = songJson.optInt("br");
        song.fee = songJson.optInt("fee");
        song.gain = (float) songJson.optDouble("gain");
        song.md5 = optString(songJson, "md5");
        song.payed = songJson.optInt("payed");
        song.size = songJson.optLong("size");
        song.type = optString(songJson, "type");
        song.uf = songJson.optJSONObject("uf");
        song.url = optString(songJson, "url");
        song.parseMatchInfo(songJson);
        return song;
    }

    static Song parseFromDetail(JSONObject songJson, long id, int br) {
        Song song = new Song();
        long fid = songJson.optLong("fid");
        if (fid > 0) {
            song.id = id;
            song.br = br;
            song.gain = (float) songJson.optDouble("vd");
            song.md5 = String.format(Locale.getDefault(), "%032d", fid);
            song.size = songJson.optLong("size");
            song.type = "mp3";
            song.url = CloudMusicPackage.NeteaseMusicUtils.generateUrl(fid);
            return song;
        }
        return null;
    }

    void parseMatchInfo(JSONObject songJson) {
        matchedPlatform = optString(songJson, "matchedPlatform");
        matchedSongName = optString(songJson, "matchedSongName");
        matchedArtistName = optString(songJson, "matchedArtistName");
        matchedDuration = songJson.optBoolean("matchedDuration");
    }

    boolean checkAccessible() {
        if (url != null) {
            try {
                Http h = Http.headByGet(url, false);
                if (h.getResponseCode() == 200 || h.getResponseCode() == 206) {
                    url = h.getFinalLocation();
                    size = h.getContentLength(); // re-calc music size for 3rd party url
                    return true;
                }
            } catch (Throwable t) {
                log(id + "\n" + br + "\n" + url);
                log(t);
            }
        }
        return false;
    }

    boolean isMatchedSong() {
        return matchedPlatform != null;
    }

    JSONObject getMatchedJson() throws JSONException {
        return new JSONObject()
                .put("matchedPlatform", matchedPlatform)
                .put("matchedSongName", matchedSongName)
                .put("matchedArtistName", matchedArtistName);
    }
}