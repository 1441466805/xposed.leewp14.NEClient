package xposed.leewp14.NEClient;

import java.util.LinkedHashMap;
import java.util.Map;

import de.robv.android.xposed.XposedBridge;

class Http extends CloudMusicPackage.HttpBase {

    private Http(String method, String urlString, Map<String, String> postData, Map<String, String> additionalHeaders) throws Throwable {
        super(method, urlString, postData);
        setAdditionHeader(additionalHeaders);

        int retryCount = 3;
        while (true) {
            try {
                startRequest();
                break;
            } catch (Throwable t) {
                retryCount--;
                if (retryCount <= 0) {
                    XposedBridge.log(urlString);
                    throw t;
                }
            }
        }
    }

    static Http get(String urlString) throws Throwable {
        return new Http("GET", urlString, null, null);
    }

    static Http post(String urlString, Map<String, String> postData) throws Throwable {
        return new Http("POST", urlString, postData, null);
    }

    static Http headByGet(String urlString) throws Throwable {
        Map<String, String> map = new LinkedHashMap<String, String>() {{
            put("Range", "bytes=0-0");
        }};
        return new Http("GET", urlString, null, map);
    }
}

