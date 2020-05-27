package kelly;

import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

public class SoundPlayer {
    private static HashMap<String, byte[]> soundCache = new HashMap<>();
    public enum SoundFX {
        LAZER("/sfx/laser.wav")
        , STARTUP ("/sfx/startup.wav")
        , UFO ("/sfx/UFO.wav")
        , EXPLOSION1 ("/sfx/explosion1.wav")
        , EXPLOSION2 ("/sfx/explosion2.wav")
        , EXPLOSION3 ("/sfx/explosion3.wav")
        , SWISHHH ("/sfx/swishhh.wav")
        , SWISHBANG ("/sfx/swishbang.wav")
        , QUIET ("/sfx/quiet.wav")
        , SPACENOISE ("/sfx/spacenoise.wav")
        ;

        private String path;
        SoundFX(String path) {
            this.path = path;
        }

        public void playSound() {
            SoundPlayer.playSoundFromClassPath(path);
        }
    }

    public static void main(String[] args) throws InterruptedException {
        for(SoundFX sfx : SoundFX.values()) {
            sfx.playSound();
            Thread.sleep(2000);
        }
    }

    // copied from stack overflow:
    // https://stackoverflow.com/questions/1264709/convert-inputstream-to-byte-array-in-java
    public static byte[] readIntoMemory(InputStream is) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        int nRead;
        byte[] data = new byte[16384];

        while ((nRead = is.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }

        return buffer.toByteArray();
    }

    /**
     * Loads the audio data using cache. If the audio data isn't in the cache,
     * then loads the data from the disc and saves it to the cache.
     * @param path
     * @return
     */
    public static byte[] loadFromClassPath(String path) {
        byte[] result = soundCache.get(path);

        if(result == null) {
            try (InputStream is = SoundPlayer.class.getResourceAsStream(path)) {
                result = readIntoMemory(is);
                soundCache.put(path, result);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return result;
    }

    public static void playSoundFromClassPath(String path) {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(loadFromClassPath(path))) {
            playSound(bais);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void playSound(InputStream is) {
        try (AudioInputStream ais = AudioSystem.getAudioInputStream(is)) {
            final Clip clip = AudioSystem.getClip();
            clip.addLineListener((event) -> {
                LineEvent.Type type = event.getType();
                if (type == LineEvent.Type.STOP) {
                    event.getLine().close();
//                } else if (type == LineEvent.Type.OPEN) {
//
//                } else if (type == LineEvent.Type.CLOSE) {
//
//                } else if (type == LineEvent.Type.START) {
//
                }
            });
            clip.open(ais);
            clip.start();
        } catch (UnsupportedAudioFileException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
    }
}
