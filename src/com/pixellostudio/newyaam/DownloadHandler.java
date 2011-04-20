package com.pixellostudio.newyaam;

import android.os.Handler;

public class DownloadHandler extends Handler
{
        public static final int MSG_SET_LENGTH = 0;
        public static final int MSG_ON_RECV = 1;
        public static final int MSG_FINISHED = 2;
        public static final int MSG_ERROR = 3;
        public static final int MSG_ABORTED = 4;
        
        public void sendSetLength(long length)
        {
                sendMessage(obtainMessage(MSG_SET_LENGTH, (Long)length));       
        }

        public void sendOnRecv(long recvd)
        {
                sendMessage(obtainMessage(MSG_ON_RECV, (Long)recvd));
        }

        public void sendFinished()
        {
                sendMessage(obtainMessage(MSG_FINISHED));
        }

        public void sendError(String errmsg)
        {
                sendMessage(obtainMessage(MSG_ERROR, errmsg));
        }

        public void sendAborted()
        {
                sendMessage(obtainMessage(MSG_ABORTED));
        }

        public void removeMyMessages()
        {
                removeMessages(MSG_SET_LENGTH);
                removeMessages(MSG_ON_RECV);
                removeMessages(MSG_FINISHED);
                removeMessages(MSG_ERROR);
                removeMessages(MSG_ABORTED);
        }
}
