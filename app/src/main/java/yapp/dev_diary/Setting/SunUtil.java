package yapp.dev_diary.Setting;
import java.io.File;

import android.os.Environment;
/**
 * Created by seoheepark on 2017-08-20.
 */

public class SunUtil {
    public static String makeDir(String dirName)
    {
        String mRootPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + dirName;

        try
        {
            File fRoot = new File(mRootPath);
            if (fRoot.exists() == false)
            {
                if (fRoot.mkdirs() == false)
                {
                    throw new Exception("");
                }
            }
        }
        catch (Exception e)
        {
            mRootPath = "-1";
        }

        return mRootPath + "/";
    }


    public static String isFileExistsRename(String filePath, String fileName)
    {
        String returnValue = fileName;
        int i = 0;

        if (isFileExists(filePath + fileName))
            returnValue = isFileExistsRename(filePath, fileName, i);

        return returnValue;
    }

    private static String isFileExistsRename(String filePath, String fileName, int idx)
    {
        boolean b = true;
        String returnValue = fileName;

        while(b)
        {
            if (isFileExists(filePath + idx + "_" + fileName))
                idx++;
            else
                b = false;
        }

        returnValue = idx + "_" + fileName;
        return returnValue;
    }

    public static boolean isFileExists(String filePathName)
    {
        boolean returnValue = false;
        File f = new File(filePathName);

        try
        {
            if (f.exists())
                returnValue = true;
            else
                returnValue = false;

            f = null;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            f = null;
        }

        return returnValue;
    }
}
