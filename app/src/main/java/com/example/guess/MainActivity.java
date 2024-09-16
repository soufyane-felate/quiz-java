package com.example.guess;

import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private int currentQuestionIndex = 0;
    private int correctAnswers = 0;
    private int incorrectAnswers = 0;
    private TextView questionTextView;
    private ListView choicesListView;
    private Button nextButton;
    private Button againButton;
    private TextView countDownTextView;
    private CountDownTimer countDownTimer;
    private boolean isTimerRunning = false;

    private String[][] questionsAndChoices;
    private String[] questions;
    private String[] correctAnswersList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        questionTextView = findViewById(R.id.questionTextView);
        choicesListView = findViewById(R.id.choicesListView);
        nextButton = findViewById(R.id.nextButton);
        againButton = findViewById(R.id.again);
        countDownTextView = findViewById(R.id.conteur);

        questionsAndChoices = Choix.getQuestions();
        questions = Question.getQuestions();
        correctAnswersList = Reponse.getReponses();

        // Start the quiz
        startQuiz();

        nextButton.setOnClickListener(view -> goToNextQuestion());
        againButton.setOnClickListener(view -> restartQuiz());
    }

    private void startQuiz() {
        againButton.setVisibility(View.GONE);
        displayQuestion(currentQuestionIndex);
        resetUI();
    }

    private void restartQuiz() {
        // Reset values
        currentQuestionIndex = 0;
        correctAnswers = 0;
        incorrectAnswers = 0;
        resetUI();
        startQuiz();
    }

    private void resetUI() {
        if (isTimerRunning) {
            countDownTimer.cancel();
            isTimerRunning = false;
        }
        countDownTextView.setText("Time Left: 30");
        nextButton.setEnabled(true);
    }

    private void displayQuestion(int index) {
        questionTextView.setText(questions[index]);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, questionsAndChoices[index]);
        choicesListView.setAdapter(adapter);

        for (int i = 0; i < choicesListView.getChildCount(); i++) {
            choicesListView.getChildAt(i).setBackgroundColor(Color.TRANSPARENT);
        }

        choicesListView.setEnabled(true);

        choicesListView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedChoice = (String) parent.getItemAtPosition(position);
            checkAnswer(selectedChoice, index, view);
        });

        startCountDown();
    }

    private void startCountDown() {
        if (isTimerRunning) {
            countDownTimer.cancel();
        }

        countDownTimer = new CountDownTimer(30000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                countDownTextView.setText("Time Left: " + millisUntilFinished / 1000);
            }

            @Override
            public void onFinish() {
                if (isTimerRunning) {
                    countDownTextView.setText("Time's up!");
                    markAnswerAsIncorrect();
                    goToNextQuestion();
                }
            }
        }.start();
        isTimerRunning = true;
    }

    private void markAnswerAsIncorrect() {
        incorrectAnswers++;
    }

    private void goToNextQuestion() {
        if (isTimerRunning) {
            countDownTimer.cancel();
            isTimerRunning = false;
        }

        // Apply flip out animation
        Animation flipOut = AnimationUtils.loadAnimation(this, R.anim.flip_out);
        questionTextView.startAnimation(flipOut);
        choicesListView.startAnimation(flipOut);

        flipOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                currentQuestionIndex++;
                if (currentQuestionIndex < questions.length) {
                    // Display the next question with flip in animation
                    displayQuestion(currentQuestionIndex);
                    Animation flipIn = AnimationUtils.loadAnimation(MainActivity.this, R.anim.flip_in);
                    questionTextView.startAnimation(flipIn);
                    choicesListView.startAnimation(flipIn);
                } else {
                    // Show final score and show the "Again" button
                    showFinalScore();
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }

    private void showFinalScore() {
        questionTextView.setText("Quiz terminé ! \nVotre score est : " + correctAnswers + " corrects, " + incorrectAnswers + " incorrects");
        choicesListView.setAdapter(null);
        nextButton.setEnabled(false);
        againButton.setVisibility(View.VISIBLE);
        if (correctAnswers > incorrectAnswers) {
            questionTextView.setText("Félicitations, vous avez réussi le test votre marque : " + correctAnswers);
            questionTextView.setBackgroundColor(Color.GREEN);
        } else {
            questionTextView.setText("Désolé, vous n'avez pas réussi le test : " + correctAnswersList);
            questionTextView.setBackgroundColor(Color.RED);
        }
    }

    private void checkAnswer(String selectedChoice, int questionIndex, View view) {
        String correctAnswer = correctAnswersList[questionIndex];
        View selectedView = choicesListView.getChildAt(choicesListView.getPositionForView(view));

        // Load fade-in effect
        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);

        if (selectedChoice.equals(correctAnswer)) {
            selectedView.setBackgroundColor(Color.GREEN);
            selectedView.startAnimation(fadeIn);
            correctAnswers++;
        } else {
            selectedView.setBackgroundColor(Color.RED);
            selectedView.startAnimation(fadeIn);
            incorrectAnswers++;
            for (int i = 0; i < choicesListView.getChildCount(); i++) {
                if (choicesListView.getItemAtPosition(i).equals(correctAnswer)) {
                    View correctView = choicesListView.getChildAt(i);
                    correctView.setBackgroundColor(Color.GREEN);
                    correctView.startAnimation(fadeIn);
                }
            }
        }
        choicesListView.setEnabled(false);
        if (isTimerRunning) {
            countDownTimer.cancel();
            isTimerRunning = false;
        }
    }
}
