package org.onebillion.onecourse.utils;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.ArrayMap;

import org.onebillion.onecourse.mainui.MainActivity;
import org.onebillion.onecourse.mainui.OBSectionController;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

/**
 * Created by michal on 02/08/2017.
 */

public class OCM_MlUnitInstance extends DBObject
{
    public final static int INSTANCE_TYPE_STUDY = 1;
    public final static int INSTANCE_TYPE_REVIEW = 2;

    public long unitid;
    public int userid, sessionid, seqNo, elapsedTime, type, starColour;
    public float score;
    public long startTime, endTime;

    public OBSectionController sectionController;
    public OCM_MlUnit mlUnit;

    private static final String[] intFields = {"userid","sessionid","seqNo","elapsedTime","type","starColour"};
    private static final String[] floatFields = {"score"};
    private static final String[] longFields = {"startTime","endTime","unitid"};

    public OCM_MlUnitInstance()
    {
        score = 0;
        elapsedTime = 0;
        startTime = 0;
        endTime = 0;
        starColour = -1;
    }


    public static OCM_MlUnitInstance initWithMlUnit(OCM_MlUnit unit, int userid, int sessionid, long starttime, int week, int type)
    {
        return initInDBWithMlUnit(unit,userid,sessionid,starttime,week,type);
    }

    private static OCM_MlUnitInstance initInDBWithMlUnit(OCM_MlUnit unit, int userid, int sessionid, long starttime, int week, int type)
    {
        OCM_MlUnitInstance mlui = new OCM_MlUnitInstance();
        mlui.userid = userid;
        mlui.unitid = unit.unitid;
        mlui.sessionid = sessionid;
        mlui.mlUnit = unit;
        mlui.type = type;

        DBSQL db = null;
        try
        {
            db = new DBSQL(true);
            Cursor cursor = db.prepareRawQuery(String.format("SELECT MAX(seqNo)as seqNo FROM %s AS UI " +
                    "JOIN %s AS S ON S.userid = UI.userid AND S.sessionid = UI.sessionid " +
                            "WHERE UI.userid = ? AND UI.unitid = ? AND UI.type = ? AND S.day > ? AND S.day <= ?",
                    DBSQL.TABLE_UNIT_INSTANCES, DBSQL.TABLE_SESSIONS),
                    Arrays.asList(String.valueOf(userid), String.valueOf(unit.unitid),String.valueOf(type)
                            ,String.valueOf((week-1)*7),String.valueOf(week*7)));

            int columnIndex = cursor.getColumnIndex("seqNo");
            if(cursor.moveToFirst() && !cursor.isNull(columnIndex))
            {
                mlui.seqNo = cursor.getInt(columnIndex) + 1;
            }
            else
            {
                mlui.seqNo = 0;
            }
            mlui.startTime = starttime;
            if(!mlui.saveToDB(db))
                mlui = null;
            cursor.close();
        }
        catch(Exception e)
        {
            MainActivity.log("OCM_MlUnitInstance: database access error: " + e.getMessage());
        }
        finally
        {
            if(db != null)
                db.close();
        }
        return mlui;
    }


    public boolean updateDataInDB(DBSQL db)
    {
        Map<String,String> whereMap = new ArrayMap<>();
        whereMap.put("userid",String.valueOf(userid));
        whereMap.put("unitid",String.valueOf(unitid));
        whereMap.put("seqNo",String.valueOf(seqNo));
        whereMap.put("type",String.valueOf(type));

        ContentValues contentValues = new ContentValues();
        contentValues.put("endTime",endTime);
        contentValues.put("score",score);
        contentValues.put("elapsedTime",elapsedTime);
        contentValues.put("starColour",starColour);
        boolean result = db.doUpdateOnTable(DBSQL.TABLE_UNIT_INSTANCES,whereMap,contentValues) > 0;
        return result;
    }

    public Boolean saveToDB(DBSQL db)
    {
        ContentValues contentValues = getContentValues(null,intFields,longFields,floatFields);
        boolean result = db.doInsertOnTable(DBSQL.TABLE_UNIT_INSTANCES,contentValues) > 0;
        return result;
    }

}