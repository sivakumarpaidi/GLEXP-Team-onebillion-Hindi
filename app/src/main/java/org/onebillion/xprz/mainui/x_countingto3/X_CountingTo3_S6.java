package org.onebillion.xprz.mainui.x_countingto3;

import org.onebillion.xprz.controls.OBGroup;
import org.onebillion.xprz.mainui.generic.XPRZ_Generic;
import org.onebillion.xprz.mainui.generic.XPRZ_Generic_Tracing;

import java.util.EnumSet;

/**
 * Created by pedroloureiro on 13/07/16.
 */
public class X_CountingTo3_S6 extends XPRZ_Generic_Tracing
{

    public X_CountingTo3_S6()
    {
        super(true);
    }

    @Override
    public void action_prepareScene (String scene, Boolean redraw)
    {
        super.action_prepareScene(scene, redraw);
        //
        OBGroup orangeBowl = (OBGroup) objectDict.get("orange_bowl");
        for (int i = 1 ; i <= 3; i++)
        {
            OBGroup orange = (OBGroup) orangeBowl.objectDict.get(String.format("orange_%d", i));
            orange.setHidden(i > Integer.parseInt(eventAttributes.get("oranges")));
        }
    }

    public void demo6a() throws Exception
    {
        setStatus(STATUS_DOING_DEMO);
        loadPointer(POINTER_MIDDLE);
        //
        action_playNextDemoSentence(false); // Look
        XPRZ_Generic.pointer_moveToObjectByName("orange_bowl", -15, 0.6f, EnumSet.of(XPRZ_Generic.Anchor.ANCHOR_MIDDLE), true, this);
        waitAudio();
        waitForSecs(0.3);
        //
        action_playNextDemoSentence(true); // One orange in the bowl
        waitForSecs(0.3);
        //
        pointer_demoTrace(true); // One
        waitForSecs(0.3);
        //
        thePointer.hide();
        waitForSecs(0.7);
        //
        nextScene();
    }


    public void demo6c() throws Exception
    {
        setStatus(STATUS_DOING_DEMO);
        loadPointer(POINTER_MIDDLE);
        //
        action_playNextDemoSentence(false); // Two oranges in the bowl. Three oranges in the bowl. No oranges in the bowl
        XPRZ_Generic.pointer_moveToObjectByName("orange_bowl", -15, 0.6f, EnumSet.of(XPRZ_Generic.Anchor.ANCHOR_MIDDLE), true, this);
        waitAudio();
        waitForSecs(0.3);
        //
        pointer_demoTrace(true); // Two. Three. Zero
        waitForSecs(0.3);
        //
        thePointer.hide();
        waitForSecs(0.7);
        //
        nextScene();
    }

    public void demo6e() throws Exception
    {
        demo6c();
    }

    public void demo6g() throws Exception
    {
        demo6c();
    }

}
