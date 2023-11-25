package ka.chapter4.item20.sing;

public interface SingerSongWriter extends Singer, SongWriter {
    AudioClip strum();

    void actSensitive();
}
