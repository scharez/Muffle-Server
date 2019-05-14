package helper;

import java.io.IOException;

public class MP3Downloader{

    private JsonBuilder jb = new JsonBuilder();

    public MP3Downloader() {}

    public String download(String path, String url) {


        Runtime rt = Runtime.getRuntime();

        try {
            rt.exec("youtube-dl -f bestaudio --extract-audio --audio-format mp3 --audio-quality 0 " + url);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // youtube-dl -o "/Users/scharez/Desktop/%(title)s.%(ext)s" -f bestaudio --extract-audio --audio-format mp3 --audio-quality 0 https://www.youtube.com/watch?v=Xm8-bw3nLMA

        return "LOL";
    }

    /*
    Problem mit youtube-dl bekomme ich nur .webm files heraus, die man wieder umkovertieren müsste oder doch ned


    Downlaod mit Threadpool und Executer Service
     */

}
