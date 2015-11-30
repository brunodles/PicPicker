package br.com.brunolima.pic_picker.listener;

import android.content.Intent;

/**
 * Created by brunodles on 10/11/14.
 */
public interface ActivityStarter {

    void startActivityForResult(Intent intent, int requestCodeAttachImage);
}
