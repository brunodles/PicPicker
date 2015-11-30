package br.com.brunolima.pic_picker.listener;

import android.content.Intent;

/**
 * This interface is need just to make who will care about the onActivityResponse listener.
 * If you start an activity from a fragment, the response will be sent to OnActivityResponse on that fragment.
 * But if you start and activity from another Activity the response won't reach the fragment.
 * <p/>
 * You don't even need to implement it, you just need to make you actvivity or fragment implement it.
 * It will just work, cuz the method already exists.
 * <p/>
 * Created by brunodles on 10/11/14.
 */
public interface ActivityStarter {

    void startActivityForResult(Intent intent, int requestCodeAttachImage);
}
