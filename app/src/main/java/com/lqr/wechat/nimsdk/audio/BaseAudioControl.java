package com.lqr.wechat.nimsdk.audio;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.text.TextUtils;

import com.lqr.wechat.R;
import com.lqr.wechat.utils.LogUtils;
import com.netease.nimlib.sdk.media.player.AudioPlayer;
import com.netease.nimlib.sdk.media.player.OnPlayListener;

abstract public class BaseAudioControl<T> {

    interface AudioControllerState {
        int stop = 0;
        int ready = 1;
        int playing = 2;
    }

    private int state;
    protected boolean isEarPhoneModeEnable = true; // 是否是听筒模式

    public interface AudioControlListener {
        //AudioControl准备就绪，已经postDelayed playRunnable，不等同于AudioPlayer已经开始播放
        public void onAudioControllerReady(Playable playable);

        /**
         * 结束播放
         */
        public void onEndPlay(Playable playable);

        /**
         * 显示播放过程中的进度条
         *
         * @param curPosition 当前进度，如果传-1则自动获取进度
         */
        public void updatePlayingProgress(Playable playable, long curPosition);
    }

    protected AudioControlListener audioControlListener;

    protected Context mContext;
    protected AudioPlayer currentAudioPlayer;
    protected Playable currentPlayable;

    protected boolean needSeek = false;
    protected long seekPosition;

    private MediaPlayer mSuffixPlayer = null;
    private boolean mSuffix = false;
    protected Handler mHandler = new Handler();

    private BasePlayerListener basePlayerListener = null;

    protected void setOnPlayListener(Playable playingPlayable, AudioControlListener audioControlListener) {
        this.audioControlListener = audioControlListener;

        basePlayerListener = new BasePlayerListener(currentAudioPlayer, playingPlayable);
        currentAudioPlayer.setOnPlayListener(basePlayerListener);
        basePlayerListener.setAudioControlListener(audioControlListener);
    }

    public void setEarPhoneModeEnable(boolean isEarPhoneModeEnable) {
        this.isEarPhoneModeEnable = isEarPhoneModeEnable;
        if (isEarPhoneModeEnable) {
            updateAudioStreamType(AudioManager.STREAM_VOICE_CALL);
        } else {
            updateAudioStreamType(AudioManager.STREAM_MUSIC);
        }
    }

    @SuppressWarnings("unchecked")
    public void changeAudioControlListener(AudioControlListener audioControlListener) {
        this.audioControlListener = audioControlListener;

        if (isPlayingAudio()) {
            OnPlayListener onPlayListener = currentAudioPlayer.getOnPlayListener();
            if (onPlayListener != null) {
                ((BasePlayerListener) onPlayListener).setAudioControlListener(audioControlListener);
            }
        }
    }

    public AudioControlListener getAudioControlListener() {
        return audioControlListener;
    }

    public BaseAudioControl(Context context, boolean suffix) {
        this.mContext = context;
        this.mSuffix = suffix;
    }

    protected void playSuffix() {
        if (mSuffix) {
            mSuffixPlayer = MediaPlayer.create(mContext, R.raw.audio_end_tip);
            mSuffixPlayer.setLooping(false);
            mSuffixPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mSuffixPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mSuffixPlayer.release();
                    mSuffixPlayer = null;
                }
            });
            mSuffixPlayer.start();
        }
    }

    protected boolean startAudio(
            Playable playable,
            AudioControlListener audioControlListener,
            int audioStreamType,
            boolean resetOrigAudioStreamType,
            long delayMillis) {
        String filePath = playable.getPath();
        if (TextUtils.isEmpty(filePath)) {
            return false;
        }

        //正在播放，停止播放
        if (isPlayingAudio()) {
            stopAudio();
            //如果相等，就是同一个对象了
            if (currentPlayable.isAudioEqual(playable)) {
                return false;
            }
        }

        state = AudioControllerState.stop;

        currentPlayable = playable;
        currentAudioPlayer = new AudioPlayer(mContext);
        currentAudioPlayer.setDataSource(filePath);

        setOnPlayListener(currentPlayable, audioControlListener);

        if (resetOrigAudioStreamType) {
            this.origAudioStreamType = audioStreamType;
        }
        this.currentAudioStreamType = audioStreamType;

        mHandler.postDelayed(playRunnable, delayMillis);

        state = AudioControllerState.ready;
        if (audioControlListener != null) {
            audioControlListener.onAudioControllerReady(currentPlayable);
        }

        return true;
    }

    Runnable playRunnable = new Runnable() {

        @Override
        public void run() {
            if (currentAudioPlayer == null) {
                LogUtils.sf("playRunnable run when currentAudioPlayer == null");
                return;
            }

            currentAudioPlayer.start(currentAudioStreamType);
        }
    };

    private int origAudioStreamType;
    private int currentAudioStreamType;

    public int getCurrentAudioStreamType() {
        return currentAudioStreamType;
    }

    protected int getUserSettingAudioStreamType() {
        // 听筒模式/扬声器模式
        if (isEarPhoneModeEnable) {
            return AudioManager.STREAM_VOICE_CALL;
        } else {
            return AudioManager.STREAM_MUSIC;
        }
    }

    protected void resetAudioController(Playable playable) {
        currentAudioPlayer.setOnPlayListener(null);
        currentAudioPlayer = null;

        state = AudioControllerState.stop;
    }

    //playing or ready
    public boolean isPlayingAudio() {
        if (currentAudioPlayer != null) {
            return state == AudioControllerState.playing
                    || state == AudioControllerState.ready;
        } else {
            return false;
        }
    }

    //stop or cancel
    public void stopAudio() {
        if (state == AudioControllerState.playing) {
            //playing->stop
            currentAudioPlayer.stop();
        } else if (state == AudioControllerState.ready) {
            //ready->cancel
            mHandler.removeCallbacks(playRunnable);
            resetAudioController(currentPlayable);

            if (audioControlListener != null) {
                audioControlListener.onEndPlay(currentPlayable);
            }
        }
    }

    public boolean updateAudioStreamType(int audioStreamType) {
        if (!isPlayingAudio()) {
            return false;
        }

        if (audioStreamType == getCurrentAudioStreamType()) {
            return false;
        }

        changeAudioStreamType(audioStreamType);
        return true;
    }

    public boolean restoreAudioStreamType() {
        if (!isPlayingAudio()) {
            return false;
        }

        if (origAudioStreamType == getCurrentAudioStreamType()) {
            return false;
        }

        changeAudioStreamType(origAudioStreamType);
        return true;
    }

    private void changeAudioStreamType(int audioStreamType) {
        if (currentAudioPlayer.isPlaying()) {
            seekPosition = currentAudioPlayer.getCurrentPosition();
            needSeek = true;
            currentAudioStreamType = audioStreamType;
            currentAudioPlayer.start(audioStreamType);
        } else {
            currentAudioStreamType = origAudioStreamType;
        }
    }

    public class BasePlayerListener implements OnPlayListener {
        protected AudioPlayer listenerPlayingAudioPlayer;
        protected Playable listenerPlayingPlayable;
        protected AudioControlListener audioControlListener;

        public BasePlayerListener(AudioPlayer playingAudioPlayer, Playable playingPlayable) {
            listenerPlayingAudioPlayer = playingAudioPlayer;
            listenerPlayingPlayable = playingPlayable;
        }

        public void setAudioControlListener(AudioControlListener audioControlListener) {
            this.audioControlListener = audioControlListener;
        }

        protected boolean checkAudioPlayerValid() {
            if (currentAudioPlayer != listenerPlayingAudioPlayer) {
                return false;
            }

            return true;
        }

        @Override
        public void onPrepared() {
            if (!checkAudioPlayerValid()) {
                return;
            }

            state = AudioControllerState.playing;
            if (needSeek) {
                needSeek = false;
                listenerPlayingAudioPlayer.seekTo((int) seekPosition);
            }
        }

        @Override
        public void onPlaying(long curPosition) {
            if (!checkAudioPlayerValid()) {
                return;
            }

            if (audioControlListener != null) {
                audioControlListener.updatePlayingProgress(listenerPlayingPlayable, curPosition);
            }
        }

        @Override
        public void onInterrupt() {
            if (!checkAudioPlayerValid()) {
                return;
            }

            resetAudioController(listenerPlayingPlayable);
            if (audioControlListener != null) {
                audioControlListener.onEndPlay(currentPlayable);
            }

        }

        @Override
        public void onError(String error) {
            if (!checkAudioPlayerValid()) {
                return;
            }

            resetAudioController(listenerPlayingPlayable);
            if (audioControlListener != null) {
                audioControlListener.onEndPlay(currentPlayable);
            }
        }

        @Override
        public void onCompletion() {
            if (!checkAudioPlayerValid()) {
                return;
            }

            resetAudioController(listenerPlayingPlayable);
            if (audioControlListener != null) {
                audioControlListener.onEndPlay(currentPlayable);
            }

            playSuffix();
        }
    }

    ;

    public void startPlayAudio(
            T t,
            AudioControlListener audioControlListener) {
        startPlayAudio(t, audioControlListener, getUserSettingAudioStreamType());
    }

    public void startPlayAudio(
            T t,
            AudioControlListener audioControlListener,
            int audioStreamType) {
        startPlayAudioDelay(0, t, audioControlListener, audioStreamType);
    }

    public void startPlayAudioDelay(long delayMillis, T t, AudioControlListener audioControlListener) {
        startPlayAudioDelay(delayMillis, t, audioControlListener, getUserSettingAudioStreamType());
    }

    public abstract void startPlayAudioDelay(long delayMillis, T t, AudioControlListener audioControlListener, int audioStreamType);

    public abstract T getPlayingAudio();
}
