package share.imooc.com.nodepaddemo.model;

import org.litepal.crud.DataSupport;

/**
 * Created by asus- on 2017/5/2.
 */

public class Message extends DataSupport {
    private String content;
    private String time;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
