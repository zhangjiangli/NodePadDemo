package share.imooc.com.nodepaddemo.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by asus- on 2017/5/9.
 */

public class StrParseUtil {
    private static ArrayList<String> contentList=new ArrayList<String>();
    private static ArrayList<String>picPathtList=new ArrayList<String>();
    private static ArrayList<String>voicePathtList=new ArrayList<String>();
    private static final String mBitmapTag = "☆";
    public static final String mVoiceBitmapTag = "♀";
    public static ArrayList<String> parse(String string,String reqtag){
        contentList.clear();
        picPathtList.clear();
        String[] strs=string.split("\n");
        for (String str:strs) {
            if (str.indexOf(mBitmapTag)!=-1){
                String path = str.replaceAll(mBitmapTag, "");
                picPathtList.add(path);
            }else if (str.indexOf(mVoiceBitmapTag)!=-1){
                String path = str.replaceAll(mVoiceBitmapTag, "");
                voicePathtList.add(path);
            } else {
                contentList.add(str);
            }
        }
        if (reqtag.equals("content")){
            return contentList;
        }else if (reqtag.equals("picPath")){
            return picPathtList;
        }else if (reqtag.equals("voicePath")){
            return voicePathtList;
        }
        return null;
    }
}
