package org.onebillion.onecourse.controls;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.style.ForegroundColorSpan;

/**
 * Created by alan on 21/04/16.
 */
public class OBTextLayer extends OBLayer
{
    public static int JUST_CENTRE = 0,
    JUST_LEFT = 1,
    JUST_RIGHT = 2;
    public StaticLayout stLayout;
    Typeface typeFace;
    float textSize;
    String text;
    int colour;
    TextPaint textPaint;
    float lineOffset;
    int hiStartIdx=-1,hiEndIdx=-1;
    int hiRangeColour;
    float letterSpacing,lineSpaceMultiplier=1.0f;
    public int justification = JUST_CENTRE;
    Rect tempRect;
    SpannableString spanner;
    boolean displayObjectsValid = false;
    public float maxWidth = -1;

    public OBTextLayer()
    {
        super();
        textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setStyle(Paint.Style.FILL);
        tempRect = new Rect();
    }

    public OBTextLayer(Typeface tf,float size,int col,String s)
    {
        this();
        typeFace = tf;
        textSize = size;
        text = s;
        colour = col;
    }

    @Override
    public OBLayer copy()
    {
        OBTextLayer obj = (OBTextLayer)super.copy();
        obj.tempRect = new Rect();
        obj.typeFace = typeFace;
        obj.textSize = textSize;
        obj.text = text;
        obj.colour = colour;
        obj.textPaint = new TextPaint(textPaint);
        obj.lineOffset = lineOffset;
        obj.letterSpacing = letterSpacing;
        obj.lineSpaceMultiplier = lineSpaceMultiplier;
        obj.hiStartIdx = hiStartIdx;
        obj.hiEndIdx = hiEndIdx;
        obj.hiRangeColour = hiRangeColour;
        obj.justification = justification;
        obj.maxWidth = maxWidth;
        return obj;
    }

    public void makeDisplayObjects(float maxw,int just)
    {
        textPaint.setTextSize(textSize);
        textPaint.setTypeface(typeFace);
        textPaint.setColor(colour);
        spanner = new SpannableString(text);
        if (hiStartIdx >= 0)
            spanner.setSpan(new ForegroundColorSpan(hiRangeColour),hiStartIdx,hiEndIdx, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        float mw = maxw > 0?maxw:(just==JUST_CENTRE)?bounds().width():4000;
        stLayout = new StaticLayout(spanner,textPaint,(int)Math.ceil(mw),
                (just==JUST_CENTRE)?Layout.Alignment.ALIGN_CENTER:Layout.Alignment.ALIGN_NORMAL,
                lineSpaceMultiplier,0,false);
        displayObjectsValid = true;
    }
    @Override

    public void draw(Canvas canvas)
    {
        if (!displayObjectsValid)
        {
            makeDisplayObjects(maxWidth,justification);
        }
        float l = 0;
        //if (justification == JUST_CENTER)
          //  l = (bounds().right - stLayout.getLineWidth(0)) / 2f;
        canvas.save();
        canvas.translate(l,0);
        stLayout.draw(canvas);
        canvas.restore();
    }
    public void drawo(Canvas canvas)
    {
        textPaint.setTextSize(textSize);
        textPaint.setTypeface(typeFace);
        textPaint.setColor(colour);
        if (letterSpacing != 0)
            textPaint.setLetterSpacing(letterSpacing / textSize);
        textPaint.getTextBounds(text, 0, text.length(), tempRect);
        if (letterSpacing != 0)
            tempRect.right += (text.length() * letterSpacing);
        float textStart = (bounds().right - tempRect.right) / 2;
        canvas.drawText(text,textStart,lineOffset,textPaint);
        hiStartIdx = 0;hiEndIdx = text.length();
    }

    public float baselineOffset()
    {
        if (!displayObjectsValid)
            makeDisplayObjects(maxWidth,justification);
        return stLayout.getLineBaseline(0);
    }

    public float textWidth(String tx)
    {
        TextPaint tp = new TextPaint();
        tp.setTextSize(textSize);
        tp.setTypeface(typeFace);
        tp.setColor(colour);
        SpannableString ss = new SpannableString(tx);
        StaticLayout sl = new StaticLayout(ss,tp,4000, Layout.Alignment.ALIGN_NORMAL,1,0,false);
        return sl.getLineWidth(0);
    }
    public float textOffset(int idx)
    {
        if (idx == 0)
            return 0;
        return textWidth(text.substring(0,idx + 1)) - textWidth(text.substring(idx,idx + 1));
    }
    public void calcBounds(RectF bb)
    {
        //if (!displayObjectsValid)
        makeDisplayObjects(maxWidth,JUST_LEFT);
        int linect = stLayout.getLineCount();
        bb.left = 0;
        bb.top = 0;
        float maxw = 0;
      /*  if (justification == JUST_CENTRE)
            maxw = maxWidth;
        else*/
        {
            for (int i = 0;i < linect;i++)
            {
                float w = stLayout.getLineWidth(i);
                float x = stLayout.getLineRight(i);
                Rect r = new Rect();
                if (w > maxw)
                    maxw = w;
            }
        }
        bb.right = maxw;
        bb.bottom = stLayout.getLineBottom(linect - 1);
    }
    public void sizeToBoundingBox()
    {
        RectF b = new RectF();
        calcBounds(b);
        setBounds(b);
        displayObjectsValid = false;
    }

    public void setBounds(RectF bounds)
    {
        //maxWidth = bounds.width();
        super.setBounds(bounds);
    }
    public float letterSpacing()
    {
        return letterSpacing;
    }

    public void setLetterSpacing(float letterSpacing)
    {
        this.letterSpacing = letterSpacing;
        displayObjectsValid = false;
    }

    public void setHighRange(int st,int en,int colour)
    {
        hiStartIdx = st;
        hiEndIdx = en;
        hiRangeColour = colour;
        displayObjectsValid = false;
    }


    public Typeface typeFace()
    {
        return typeFace;
    }

    public void setTypeFace(Typeface typeFace)
    {
        this.typeFace = typeFace;
        displayObjectsValid = false;
    }

    public float textSize()
    {
        return textSize;
    }

    public void setTextSize(float textSize)
    {
        this.textSize = textSize;
        displayObjectsValid = false;
    }

    public String text()
    {
        return text;
    }

    public void setText(String text)
    {
        this.text = text;
        displayObjectsValid = false;
    }

    public int colour()
    {
        return colour;
    }

    public void setColour(int colour)
    {
        this.colour = colour;
        displayObjectsValid = false;
    }

    public void getSelectionPath(int start, int end, Path dest)
    {
        if (!displayObjectsValid)
            makeDisplayObjects(maxWidth,justification);
        stLayout.getSelectionPath(start,end,dest);
    }

}
