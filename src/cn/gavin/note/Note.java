package cn.gavin.note;

import java.util.Date;

/**
 * Created by gluo on 9/29/2016.
 */
public interface Note {
    boolean save(boolean force);
    boolean sync(boolean force);
    void setText(String text);
    Long getTime();
    String getText();
}
