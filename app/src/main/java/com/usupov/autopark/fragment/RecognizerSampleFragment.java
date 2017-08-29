package com.usupov.autopark.fragment;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import ru.yandex.speechkit.Error;
import ru.yandex.speechkit.Recognition;
import ru.yandex.speechkit.RecognitionHypothesis;
import ru.yandex.speechkit.Recognizer;
import ru.yandex.speechkit.RecognizerListener;
import ru.yandex.speechkit.SpeechKit;

import com.pkmmte.view.CircularImageView;
import com.google.android.productcard.R;
import com.usupov.autopark.service.SpeachRecogn;

import java.util.ArrayList;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

/**
 * This file is a part of the samples for Yandex SpeechKit Mobile SDK.
 * <br/>
 * Version for Android © 2016 Yandex LLC.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <br/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <br/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

public class RecognizerSampleFragment extends DialogFragment implements RecognizerListener {
//    private static final String API_KEY_FOR_TESTS_ONLY = "069b6659-984b-4c5f-880e-aaedcfd84102";
    private static final String API_KEY_FOR_TESTS_ONLY = "8a2fdc7a-fc0d-476f-bebe-5f048ac278ac";

    private static final int REQUEST_PERMISSION_CODE = 1;

//    private ProgressBar progressBar;
//    private TextView currentStatus;
//    private TextView recognitionResult;
//    private TextView textAll;
    private TextView titleText, repeatText;
    private CircularImageView imageMicrophone;
    private ImageView imageMicro;
    private static ArrayList<String> all_results;
    private Context context;
    private Recognizer recognizer;
    private static String resultText;
//    private Button btnDone;
    private String curResult = "";
    public boolean alreadyClicked;
    public static int x;
    private boolean activeRecognation;


    public interface EditNameDialogListener {
        void onFinishEditDialog(String resultTextSpeech);
    }

    public RecognizerSampleFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        resultText = "";
        SpeechKit.getInstance().configure(getContext(), API_KEY_FOR_TESTS_ONLY);
    }

    public static RecognizerSampleFragment newInstance(int title) {
        RecognizerSampleFragment frag = new RecognizerSampleFragment();
        Bundle args = new Bundle();
        args.putInt("title", title);
        x = title;
        frag.setArguments(args);
        return frag;
    }

//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        System.out.println("HJHDHHDHHHHHHH");
//        return inflater.inflate(R.layout.fragment_yandex_speech, container, false);
//    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_yandex_speach, null);
        builder.setView(view);
        // Остальной код

//        progressBar = (ProgressBar) view.findViewById(R.id.voice_power_bar);
//        currentStatus = (TextView) view.findViewById(R.id.current_state);
//        recognitionResult = (TextView) view.findViewById(R.id.result);
//        textAll = (TextView) view.findViewById(R.id.texts_all);
        all_results = new ArrayList<>();
        alreadyClicked = false;
        context = getActivity();
        titleText = (TextView) view.findViewById(R.id.textYandexSpeech);
        titleText.setText(context.getString(x));
        repeatText = (TextView) view.findViewById(R.id.textYandexSpeechRepeat);

        imageMicrophone = (CircularImageView) view.findViewById(R.id.imageYandexSpeech);

        imageMicro = (ImageView) view.findViewById(R.id.imageMicrophone);
        imageMicro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (activeRecognation)
                    return;
                createAndStartRecognizer();
                Drawable drawable = ContextCompat.getDrawable(context,R.mipmap.ic_microphone_active);
                imageMicro.setImageDrawable(drawable);
                repeatText.setVisibility(View.GONE);
                titleText.setText(context.getString(x));
            }
        });

        activeRecognation = true;
        setCancelable(true);
        createAndStartRecognizer();

//        imageMicrophone.getLayoutParams().width = 100 + 75;
//        imageMicrophone.getLayoutParams().height = 100 + 75;

//        imageMicrophone.getLayoutParams().width = 180;
//        imageMicrophone.getLayoutParams().height = 180;
        return builder.create();
    }


    @Override
    public void onPause() {
        super.onPause();
        resetRecognizer();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != REQUEST_PERMISSION_CODE) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        if (grantResults.length == 1 && grantResults[0] == PERMISSION_GRANTED) {
            createAndStartRecognizer();
        } else {
            updateStatus("Record audio permission was not granted");
        }
    }

    private void resetRecognizer() {
        if (recognizer != null) {
            recognizer.cancel();
            recognizer = null;
        }
    }

    @Override
    public void onRecordingBegin(Recognizer recognizer) {
        updateStatus("Запись началось");
    }

    @Override
    public void onSpeechDetected(Recognizer recognizer) {
        updateStatus("Голос определено");
    }

    @Override
    public void onSpeechEnds(Recognizer recognizer) {
        updateStatus("Голос закончилось");
    }

    @Override
    public void onRecordingDone(Recognizer recognizer) {
        updateStatus("Запись закончилось");
    }

    @Override
    public void onSoundDataRecorded(Recognizer recognizer, byte[] bytes) {
    }

    @Override
    public void onPowerUpdated(Recognizer recognizer, float power) {
//        updateProgress((int) (power * progressBar.getMax()));
        System.out.println(power * 100+" 77777777777777778888888");
        updateProgress((int) (power * 100));
    }

    @Override
    public void onPartialResults(Recognizer recognizer, Recognition recognition, boolean b) {
        curResult = recognition.getBestResultText();

        all_results.clear();
        for (RecognitionHypothesis r : recognition.getHypotheses()) {
            all_results.add(r.getNormalized());
        }

//        System.out.println("PartialRRRRRRRRRRR"+" "+all_results.size());
//        String result = "";
//        for (int i = 0; i < h.length; i++) {
//            result += h[i].toString() + "\n";
//        }
//        textAll.setText(result);
//        updateStatus("Partial results " + recognition.getBestResultText());
    }

    @Override
    public void onRecognitionDone(Recognizer recognizer, Recognition recognition) {
        System.out.println("Done***********************");
        curResult = recognition.getBestResultText();
        System.out.println(all_results.size()+" +++++++++++++++++++++++++++++");
//        updateResult(curResult);
        all_results.clear();
        for (RecognitionHypothesis r : recognition.getHypotheses()) {
            all_results.add(r.getNormalized());
        }
        updateProgress(0);

        finish();
    }

    private void finish() {

        String recognatedText = SpeachRecogn.vinSpeach(all_results, context).toUpperCase();

        EditNameDialogListener activity = (EditNameDialogListener) context;
        if (recognatedText==null || recognatedText.isEmpty()) {
            titleText.setText(context.getString(R.string.yandex_speech_error));
            repeatText.setVisibility(View.VISIBLE);
            Drawable drawable = ContextCompat.getDrawable(context,R.mipmap.ic_microphone);
            imageMicro.setImageDrawable(drawable);
        }
        else {
            activity.onFinishEditDialog(recognatedText);

            dismiss();
        }
        activeRecognation = false;
    }

    @Override
    public void onError(Recognizer recognizer, ru.yandex.speechkit.Error error) {
        System.out.println("Errorrrrrrrrrrrr");
        if (error.getCode() == Error.ERROR_CANCELED) {
            updateStatus("Cancelled");
            updateProgress(0);
        } else {
            updateStatus("Error occurred " + error.getString());
            resetRecognizer();
        }
        finish();
    }

    private void createAndStartRecognizer() {
        final Context context = getContext();
        if (context == null) {
            return;
        }

        if (ContextCompat.checkSelfPermission(context, RECORD_AUDIO) != PERMISSION_GRANTED) {
            requestPermissions(new String[]{RECORD_AUDIO}, REQUEST_PERMISSION_CODE);
        } else {
            // Reset the current recognizer.
            resetRecognizer();
            // To create a new recognizer, specify the language, the model - a scope of recognition to get the most appropriate results,
            // set the listener to handle the recognition events.
            recognizer = Recognizer.create(Recognizer.Language.RUSSIAN, Recognizer.Model.NOTES, RecognizerSampleFragment.this);
            // Don't forget to call start on the created object.
            recognizer.start();
        }
    }

    private void updateResult(String text) {
        resultText += text;
//        recognitionResult.setText(resultText);
    }

    private void updateStatus(final String text) {
//        currentStatus.setText(text);
    }

    private void updateProgress(int progress) {
//        progressBar.setProgress(progress);
//        mVoiceView.animateRadius(progress);
        System.out.println(progress+" 777777777777777788888888884444");
//        imageMicrophone.getLayoutParams().width = 100 + 75;
//        imageMicrophone.getLayoutParams().height = 100 + 75;
        float scaleVal = (float) ((80 + progress) / 90.0);
        ScaleAnimation scale = new ScaleAnimation(scaleVal, scaleVal, scaleVal, scaleVal, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scale.setFillAfter(true);
        scale.setDuration(500);
        imageMicrophone.startAnimation(scale);
    }
}
