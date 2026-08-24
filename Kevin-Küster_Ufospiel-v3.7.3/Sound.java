import java.io.*;
import javax.sound.sampled.*;

public class Sound
{
    private String file;
    private String source;
    private File yourFile;
    private AudioInputStream stream;
    private AudioFormat format;
    private DataLine.Info info;
    private Clip clip;
    
    public Sound(String pSource)
    {
        source = pSource;
    }
    
    public void play()
    {
        try
        {
            if (this.songPlaying() == false)
            {
                try 
                {
                    file = source;
                    yourFile = new File(file);
                    stream = AudioSystem.getAudioInputStream(yourFile);
                    format = stream.getFormat();
                    info = new DataLine.Info(Clip.class, format);
                    clip = (Clip) AudioSystem.getLine(info);
                    clip.open(stream);
                    clip.start();
                }
                catch (Exception e)
                {
                }
            }
        }
        catch (java.lang.NullPointerException e)
        {
            try 
            {
                file = source;
                yourFile = new File(file);
                stream = AudioSystem.getAudioInputStream(yourFile);
                format = stream.getFormat();
                info = new DataLine.Info(Clip.class, format);
                clip = (Clip) AudioSystem.getLine(info);
                clip.open(stream);
                clip.start();
            }
            catch (Exception f)
            {
            }
        }
    }
    
    public void playUltimate()
    {
        try 
        {
            file = source;
            yourFile = new File(file);
            stream = AudioSystem.getAudioInputStream(yourFile);
            format = stream.getFormat();
            info = new DataLine.Info(Clip.class, format);
            clip = (Clip) AudioSystem.getLine(info);
            clip.open(stream);
            clip.start();
        }
        catch (Exception f)
        {
        }
    }
    
    public void stop()
    {
        try
        {
            clip.stop();
        }
        catch (java.lang.NullPointerException e)
        {
        }
    }
    
    private boolean songPlaying()
    {
        if (clip.isRunning())
        {
            return true;
        }else
        {
            return false;
        }
    }
}