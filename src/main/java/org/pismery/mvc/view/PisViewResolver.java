package org.pismery.mvc.view;

import org.pismery.mvc.MyModelAndView;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PisViewResolver implements ViewResolver {

    private String viewName;
    private File file;

    public PisViewResolver(String viewName, File file) {
        this.viewName = viewName;
        this.file = file;
    }

    @Override
    public String parse(MyModelAndView mv) {
        String result = null;
        try (RandomAccessFile file = new RandomAccessFile(this.file,"r");
             FileChannel channel = file.getChannel()) {

            MappedByteBuffer buffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
            buffer.load();
            char[] content = new char[buffer.limit()];
            for (int i = 0; i < buffer.limit(); i++) {
                content[i] = (char) buffer.get();
            }
            String line = new String(content);
            Matcher matcher = matcher(line);
            while (matcher.find()) {
                for (int i=1;i<=matcher.groupCount();i++){
                    String paramName = matcher.group(i);
                    Object paramValue = mv.getModel().get(paramName);
                    if(null == paramValue) continue;
                    line = line.replaceAll("@\\{"+paramName+"\\}",paramValue.toString());
                }
            }
            buffer.clear();
            result = line;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    private Matcher matcher(String str){
        Pattern pattern = Pattern.compile("@\\{(.+?)\\}",Pattern.CASE_INSENSITIVE);
        return pattern.matcher(str);
    }

    @Override
    public String matchView() {
        return viewName;
    }


    public String getViewName() {
        return viewName;
    }

    public void setViewName(String viewName) {
        this.viewName = viewName;
    }
}
