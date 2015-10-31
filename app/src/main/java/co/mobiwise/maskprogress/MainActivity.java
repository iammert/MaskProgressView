package co.mobiwise.maskprogress;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

import co.mobiwise.library.AnimationCompleteListener;
import co.mobiwise.library.MaskProgressView;
import co.mobiwise.library.OnProgressDraggedListener;

public class MainActivity extends AppCompatActivity
        implements
        OnProgressDraggedListener,
        AnimationCompleteListener,
        View.OnClickListener {

    ArrayList<Music> musicArrayList = new ArrayList<>();
    int index = 0;

    Button buttonPlayPause;
    Button buttonNext;
    Button buttonPrevious;

    MaskProgressView maskProgressView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);

        loadMusic();

        buttonPlayPause = (Button) findViewById(R.id.buttonControl);
        buttonNext = (Button) findViewById(R.id.buttonNext);
        buttonPrevious = (Button) findViewById(R.id.buttonPrevious);
        buttonPlayPause.setOnClickListener(this);
        buttonNext.setOnClickListener(this);
        buttonPrevious.setOnClickListener(this);

        maskProgressView = (MaskProgressView) findViewById(R.id.maskProgressView);
        maskProgressView.setOnProgressDraggedListener(new OnProgressDraggedListener() {
            @Override
            public void onProgressDragged(int position) {

            }

            @Override
            public void onProgressDragging(int position) {

            }
        });
        maskProgressView.setAnimationCompleteListener(new AnimationCompleteListener() {
            @Override
            public void onAnimationCompleted() {

            }
        });
        maskProgressView.setmMaxSeconds(musicArrayList.get(index).durationInSeconds);
        maskProgressView.setCoverImage(musicArrayList.get(index).coverImage);
    }

    @Override
    public void onProgressDragged(int position) {
        Log.v("TEST","Current Second : " + position );
    }

    @Override
    public void onProgressDragging(int position) {
        Log.v("TEST","Current Second : " + position );
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonControl:
                if(maskProgressView.isPlaying()){
                    buttonPlayPause.setBackgroundResource(R.drawable.icon_play);
                    maskProgressView.pause();
                }
                else{
                    buttonPlayPause.setBackgroundResource(R.drawable.icon_pause);
                    maskProgressView.start();
                }

                break;
            case R.id.buttonNext:
                if(index < musicArrayList.size() - 1)
                    index = index + 1;

                maskProgressView.stop();

                maskProgressView.setmMaxSeconds(musicArrayList.get(index).durationInSeconds);
                maskProgressView.setCoverImage(musicArrayList.get(index).coverImage);
                maskProgressView.start();

                buttonPlayPause.setBackgroundResource(R.drawable.icon_pause);

                break;
            case R.id.buttonPrevious:

                if(index > 0)
                    index = index - 1;

                maskProgressView.setmMaxSeconds(musicArrayList.get(index).durationInSeconds);
                maskProgressView.setCoverImage(musicArrayList.get(index).coverImage);
                maskProgressView.start();

                buttonPlayPause.setBackgroundResource(R.drawable.icon_pause);

                break;
            default:
                break;
        }
    }

    private void loadMusic() {
        musicArrayList.add(new Music(R.drawable.cover, 120));
        musicArrayList.add(new Music(R.drawable.cover2, 30));
        musicArrayList.add(new Music(R.drawable.cover, 120));
        musicArrayList.add(new Music(R.drawable.cover2, 30));
        musicArrayList.add(new Music(R.drawable.cover, 120));
        musicArrayList.add(new Music(R.drawable.cover2, 30));
        musicArrayList.add(new Music(R.drawable.cover, 120));
        musicArrayList.add(new Music(R.drawable.cover2, 30));
    }

    @Override
    public void onAnimationCompleted() {
        buttonPlayPause.setBackgroundResource(R.drawable.icon_play);
    }
}
