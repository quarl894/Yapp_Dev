package yapp.dev_diary.Voice;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.naver.speech.clientapi.SpeechConfig;
import com.naver.speech.clientapi.SpeechRecognitionResult;

import java.lang.ref.WeakReference;
import java.util.List;

import yapp.dev_diary.MainActivity;
import yapp.dev_diary.R;

/**
 * Created by 이경원 on 2017-09-15.
 */

public class RecognitionThread extends Thread{

    private static RecognitionThread instance = null;

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String CLIENT_ID = "YMlwBBEWsZetqSTIivI5";
    // 1. "내 애플리케이션"에서 Client ID를 확인해서 이곳에 적어주세요.
    // 2. build.gradle (Module:app)에서 패키지명을 실제 개발자센터 애플리케이션 설정의 '안드로이드 앱 패키지 이름'으로 바꿔 주세요

    private RecognitionHandler handler;
    private NaverRecognizer naverRecognizer;
    private AudioWriterPCM writer;

//    private TextView txtResult; // 결과 텍스트 뷰
//    private Button btnStart;    // 시작/중단 버튼
//    private Button btnPause;   // 취소 버튼
//    private Button btnCancle;   // 취소 버튼
    private String mResult;     // 텍스트뷰에 setText 하기 전에 결과를 저장

    private boolean isEpdTypeSelected;
    private SpeechConfig.EndPointDetectType currentEpdType;

    private Activity mActivity;
    private EditText voice_txt;

    public static RecognitionThread getInstance(MainActivity activity, Context context){
        if(instance == null)
            instance = new RecognitionThread(activity, context);
        return instance;
    }

    private RecognitionThread(MainActivity activity, Context context){
        handler = new RecognitionHandler(activity);
        naverRecognizer = new NaverRecognizer(activity, handler, CLIENT_ID);
        mResult = null;
        mActivity = activity;
        voice_txt = activity.voice_text;
    }



    public void run() {
        Log.d("RecognitionThread", "run()");
        super.run();
    }

    @Override
    public synchronized void start() {
        Log.d("RecognitionThread", "start()");
        super.start();



        mResult = "";
        naverRecognizer.recognize();
        voice_txt.setText("Thread Started...");
    }

    @Override
    public void interrupt() {
        Log.d("RecognitionThread", "interrupt()");
        super.interrupt();

        naverRecognizer.getSpeechRecognizer().stop();
        voice_txt.setText("Thread Interrupted...");
    }


    // Handle speech recognition Messages. 메시지 핸들링을 위한 액티비티의 메서드.
    private void handleMessage(Message msg) {
        switch (msg.what) {
            case R.id.clientReady:
                // Now an user can speak.
                voice_txt.setText("Connected");
                writer = new AudioWriterPCM(
                        Environment.getExternalStorageDirectory().getAbsolutePath() + "/NaverSpeechTest");
                writer.open("Test");
                break;

            case R.id.audioRecording:
                writer.write((short[]) msg.obj);
                break;

            case R.id.partialResult:
                // Extract obj property typed with String.
                mResult += (String) (msg.obj) + "\n";
                voice_txt.setText(mResult);
                break;

            case R.id.finalResult:
                // Extract obj property typed with String array.
                // The first element is recognition result for speech.
                SpeechRecognitionResult speechRecognitionResult = (SpeechRecognitionResult) msg.obj;
                List<String> results = speechRecognitionResult.getResults();
                StringBuilder strBuf = new StringBuilder();
                strBuf.append(results.get(0));
//                for(String result : results) {
//                    strBuf.append(result);
//                    strBuf.append("\n");
//                }
                mResult = strBuf.toString();
                voice_txt.setText(mResult);

            case R.id.recognitionError:
                if (writer != null) {
                    writer.close();
                }

                mResult = "Error code : " + msg.obj.toString();
                voice_txt.setText(mResult);

                break;

            case R.id.clientInactive:
                if (writer != null) {
                    writer.close();
                }

                break;

            case R.id.endPointDetectTypeSelected:
                isEpdTypeSelected = true;
                currentEpdType = (SpeechConfig.EndPointDetectType) msg.obj;
                if(currentEpdType == SpeechConfig.EndPointDetectType.AUTO) {
                    Toast.makeText(mActivity, "AUTO epd type is selected.", Toast.LENGTH_SHORT).show();
                } else if(currentEpdType == SpeechConfig.EndPointDetectType.MANUAL) {
                    Toast.makeText(mActivity, "MANUAL epd type is selected.", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }


    // Declare handler for handling SpeechRecognizer thread's Messages. 쓰레드의 메시지 핸들링위한 핸들러. 별개의 클래스로 정의.
    static class RecognitionHandler extends Handler {
        private final WeakReference<MainActivity> mActivity;

        RecognitionHandler(MainActivity activity) {
            mActivity = new WeakReference<MainActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            MainActivity activity = mActivity.get();
            if (activity != null) {
                handleMessage(msg);
            }
        }
    }


}
