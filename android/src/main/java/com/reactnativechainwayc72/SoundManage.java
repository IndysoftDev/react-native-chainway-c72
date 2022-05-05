package com.reactnativechainwayc72;
import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;



public class SoundManage {

    private  static int successID=-1;
    private  static int failureID=-1;
    private  static SoundPool soundPool=null;
    public static void  PlaySound(Context c, SoundType type)
    {
        soundPool= getSoundPool();
        if(soundPool==null)
            return;

        int id=-1;

        if(type== SoundType.SUCCESS)
        {
            if(successID==-1) {
                successID = soundPool.load(c, R.raw.barcodebeep, 1);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            id=successID;
        }
        else  if(type== SoundType.FAILURE)
        {
            if(failureID==-1)
            {
                failureID =soundPool.load(c,R.raw.serror,1);
            }
            id=failureID;
        }
        if(id!=-1)
            soundPool.play(id,1, 1, 0, 0, 1);

    }

    private static SoundPool getSoundPool()
    {
        if(soundPool==null)
        {
            soundPool= new SoundPool(10, AudioManager.STREAM_SYSTEM,5);
        }
        return soundPool;
    }




    public  enum SoundType{
       FAILURE,SUCCESS
    }


}
