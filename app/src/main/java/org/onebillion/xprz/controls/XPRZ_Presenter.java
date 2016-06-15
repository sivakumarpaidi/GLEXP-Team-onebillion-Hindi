package org.onebillion.xprz.controls;

import android.graphics.Path;
import android.graphics.PointF;

import org.onebillion.xprz.mainui.MainActivity;
import org.onebillion.xprz.mainui.OBSectionController;
import org.onebillion.xprz.utils.OBAnim;
import org.onebillion.xprz.utils.OBAnimationGroup;
import org.onebillion.xprz.utils.OBAudioManager;
import org.onebillion.xprz.utils.OB_Maths;
import org.onebillion.xprz.utils.OB_utils;

import java.util.Collections;
import java.util.List;

/**
 * Created by alan on 07/06/16.
 */
public class XPRZ_Presenter extends OBCharacter
{

    public static XPRZ_Presenter characterWithGroup(OBGroup g)
    {
        XPRZ_Presenter c = new XPRZ_Presenter();
        c.control = g;
        return c;
    }


    public void selectArmIndex(int i)
    {
        showAnatomy("leftarms","arm",i);
    }

    public void faceFront()
    {
        control.lockScreen();
        showOnly("facefront");
        selectArmIndex(0);
        OBControl c = control.objectDict.get("front");
        if (c.scaleX < 0)
        {
            c.reflectInAncestor(control);
            c = control.objectDict.get("leftarms");
            c.reflectInAncestor(control);
            c = control.objectDict.get("skinneck");
            c.reflectInAncestor(control);
        }
        control.needsRetexture = true;
        control.invalidate();
        control.unlockScreen();
    }

    public void faceFrontReflected()
    {
        showOnly("facefront");
        OBControl c = control.objectDict.get("front");
        if (c.scaleX >= 0)
        {
            c.reflectInAncestor(control);
            c = control.objectDict.get("leftarms");
            c.reflectInAncestor(control);
            c = control.objectDict.get("skinneck");
            c.reflectInAncestor(control);
        }
        selectArmIndex(0);
    }
    public void faceRight()
    {
        control.lockScreen();
        showOnly("faceright");
        OBControl c = control.objectDict.get("faceright");
        if (c.scaleX < 0)
        {
            c.reflectInAncestor(control);
            control.objectDict.get("hairlock").setZPosition(1);
        }
        control.needsRetexture = true;
        control.invalidate();
        control.unlockScreen();
    }

    public void faceLeft()
    {
        control.lockScreen();
        showOnly("faceright");
        OBControl c = control.objectDict.get("faceright");
        if (c.scaleX >= 0)
        {
            c.reflectInAncestor(control);
            control.objectDict.get("hairlock").setZPosition(-1);
        }
        control.needsRetexture = true;
        control.invalidate();
        control.unlockScreen();
    }

    public void speak(List<Object> audioFiles, OBSectionController controller)
    {
        OBAudioManager audioMan = OBAudioManager.audioManager;
        OBGroup mouth = (OBGroup) control.objectDict.get("mouth");

        long token  = controller.takeSequenceLockInterrupt(true);
        try
        {
            for (Object af : OB_utils.insertAudioInterval(audioFiles,300))
            {
                if (af instanceof String)
                {
                    controller.playAudio((String)af);
                    int mframe = 1, nframe = 1 ;
                    while (audioMan.isPlaying() || audioMan.isPreparing())
                    {
                        showOnly(String.format("mouth_%d",mframe),mouth);
                        nframe = OB_Maths.randomInt(1, 6);
                        if(mframe == nframe)
                        {
                            mframe = (nframe+1)%6 +1;
                        }
                        else
                        {
                            mframe = nframe;
                        }
                        control.needsRetexture = true;
                        control.invalidate();

                        controller.waitForSecs(0.07 + OB_Maths.rndom()/10);
                    }
                }
                else
                {
                    showOnly("mouth_0",mouth);
                    float f = (Float) af;
                    controller.waitForSecs(f/1000);
                }
                control.needsRetexture = true;
                control.invalidate();
                controller.checkSequenceToken(token);
            }
        }
        catch (Exception exception)
        {
        }
        controller.unlockSequenceLock();
        showOnly("mouth_0",mouth);

    }

    public void moveHandfromIndex(int fromIdx,int toIdx,double dursecs) throws Exception
    {
        int difIdx = toIdx - fromIdx;
        double dur = Math.abs(dursecs / difIdx);
        int dif = (int)Math.signum(toIdx - fromIdx);
        for (int i = fromIdx;i != toIdx+dif;i+=dif)
        {
            selectArmIndex(i);
            if (control.controller == null)
                Thread.sleep(20);
            else
                ((OBSectionController)control.controller).waitForSecsNoThrow(dur);
        }
    }

    public static Path PathForWalk(PointF startpt, PointF endpt, int segs, float yoffset)
{
    Path path = new Path();
    path.moveTo(startpt.x,startpt.y);
    float startx = startpt.x;
    float endx = endpt.x;
    float incrx = (endx - startx) / segs;
    float currx = startx;
    float y = startpt.y;
    for (int i = 0;i < segs;i++)
    {
        float xc0 = currx + incrx / 3.0f;
        float xc1 = xc0 + incrx / 3.0f;
        currx += incrx;
        path.cubicTo(xc0,y+yoffset,xc1,y+yoffset,currx,y);
    }
    return path;
}

    public static int BouncesForWalk(float xdist)
    {
        return (int)Math.ceil(xdist / MainActivity.mainViewController.glView().getWidth() * 5.0);
    }

    float DurationForWalk(float xdist)
    {
        return (float)(xdist / MainActivity.mainViewController.glView().getWidth() * 3.0);
    }

    public void walk(PointF endPt)
    {
        PointF startPt = control.position;

        float dist = endPt.x - startPt.x;

        if (dist > 0)
            faceRight();
        else
            faceLeft();

        dist = Math.abs(dist);
        Path path = PathForWalk(startPt, endPt, BouncesForWalk(dist), MainActivity.mainActivity.applyGraphicScale(-20));
        OBAnim pAnim = OBAnim.pathMoveAnim(control,path,false,0);
        OBAnimationGroup.runAnims(Collections.singletonList(pAnim),DurationForWalk(dist),true,OBAnim.ANIM_EASE_OUT,(OBSectionController)control.controller);
    }


}